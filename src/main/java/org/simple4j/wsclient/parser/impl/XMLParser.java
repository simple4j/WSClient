package org.simple4j.wsclient.parser.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.parser.IParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author jsrinivas108
 */
public class XMLParser implements IParser
{

	private static Logger logger = LoggerFactory.getLogger(XMLParser.class);

	private ThreadLocal<Map<String, List<Node>>> xpathEvalCacheTL = new ThreadLocal<Map<String, List<Node>>>();

	private boolean removePrefix = true;

	private List<String> listElementXpaths = new ArrayList<String>();
	
	private List<String> attributedElementXpaths = new ArrayList<String>();
	
	private String textNodeKey = "TEXT";

	public boolean isRemovePrefix()
	{
		return removePrefix;
	}

	public void setRemovePrefix(boolean removePrefix)
	{
		this.removePrefix = removePrefix;
	}

	public List<String> getListElementXpaths()
	{
		return listElementXpaths;
	}

	public void setListElementXpaths(List<String> listElementXpaths)
	{
		this.listElementXpaths = listElementXpaths;
	}

	public List<String> getAttributedElementXpaths()
	{
		return attributedElementXpaths;
	}

	public void setAttributedElementXpaths(List<String> attributedElementXpaths)
	{
		this.attributedElementXpaths = attributedElementXpaths;
	}

	public String getTextNodeKey()
	{
		return textNodeKey;
	}

	public void setTextNodeKey(String textNodeKey)
	{
		this.textNodeKey = textNodeKey;
	}

	public Map<String, ? extends Object> parseData(String inputXMLStr)
	{
		try
		{
			byte[] inputXMLBA = inputXMLStr.getBytes();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream bais = new ByteArrayInputStream(inputXMLBA);
			Document document = builder.parse(bais);

			Node node = document;

			this.xpathEvalCacheTL.set(new HashMap<String, List<Node>>());
			Map<String, ? extends Object> convertedMap = convert2Collections(node, null);
			return convertedMap;
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e)
		{
			throw new SystemException("XML_PARSE_FAILED", e);
		}
	}

	private Map<String, ? extends Object> convert2Collections(Node node, Node parent)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{
		logger.trace("processing node:" + node.getNodeName());
		logger.trace("processing node no prefix:" + handlePrefix(node.getNodeName()));

		logger.trace("processing nodelocalname:" + node.getLocalName());
		logger.trace(node.getPrefix());
		logger.trace(node.getNamespaceURI());
		logger.trace(node.getBaseURI());

		Map<String, Object> ret = new HashMap<String, Object>();
		if (node == null)
			return ret;
		if (node.getNodeType() == Node.COMMENT_NODE)
			return ret;
		if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE)
		{
			ret.put(getTextNodeKey(), (Object) node.getNodeValue());
			return ret;
		}

		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null)
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				Node attribute = attributes.item(i);
				String prefixHandledAttributeName = handlePrefix(attribute.getNodeName());
				if (prefixHandledAttributeName.equalsIgnoreCase("nill")
						&& "true".equalsIgnoreCase(attribute.getNodeValue()))
					return null;
				ret.put(prefixHandledAttributeName, attribute.getNodeValue());
			}
		}

		NodeList childNodes = node.getChildNodes();
		if (childNodes != null)
		{
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				Node child = childNodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE
						|| child.getNodeType() == Node.TEXT_NODE)
				{
					Map<String, ? extends Object> tempMap = convert2Collections(child, node);
					String childNodeName = handlePrefix(child.getNodeName());
					logger.trace(childNodeName + " processed values:" + tempMap);
					if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE)
					{
						// below if condition is to remove unwanted text nodes between real element
						// nodes
						if (childNodes.getLength() == 1)
							ret.putAll(tempMap);
					} else
					{
						// 1-N check
						boolean isListElement = doesNodeMatchAnyXpath(child, this.getListElementXpaths());

						if (isListElement)
						{
							Object value = processAttributedElements(child, tempMap);

							List<Object> values = null;
							if (!ret.containsKey(childNodeName))
							{
								values = new ArrayList<Object>();
								ret.put(childNodeName, values);
							} else
							{
								values = (List<Object>) ret.get(childNodeName);
							}
							values.add(value);
						}
						else
						{
							Object value = processAttributedElements(child, tempMap);

							if (ret.containsKey(childNodeName))
							{
								Object obj = ret.get(childNodeName);
								List<Object> values = null;
								if (obj instanceof List)
								{
									values = (List<Object>) obj;
								} else
								{
									values = new ArrayList<Object>();
									values.add(obj);
									ret.put(childNodeName, values);
								}
								values.add(value);
							} else
							{
								ret.put(childNodeName, value);
							}
						}
					}
				} else
				{
					continue;
				}
			}
		}
		return ret;
	}

	private Object processAttributedElements(Node child, Map<String, ? extends Object> tempMap)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{
		Object value = tempMap;
		if(doesNodeMatchAnyXpath(child, this.getAttributedElementXpaths()))
		{
			// continue adding the whole Map as some may be without attribute and some may be with attribute
		}
		else
		{
			if (tempMap != null && tempMap.containsKey(getTextNodeKey()) && tempMap.size() == 1)
			{
				// if node does not contain any attributes, avoid having another Map
				value = tempMap.get(getTextNodeKey());
			}
			else
			{
				// continue adding the whole Map as this node is not configured as attributed but has attributes
			}
		}
		return value;
	}

	private boolean doesNodeMatchAnyXpath(Node child, List<String> xpaths)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{
		boolean isListElement = false;
		for (int j = 0; j < xpaths.size() && !isListElement; j++)
		{
			String listElementXpath = xpaths.get(j);
			isListElement = doesNodeMatchXpath(child, listElementXpath);
		}
		return isListElement;
	}

	private String handlePrefix(String nodeName)
	{
		if (removePrefix)
		{
			if (nodeName.contains(":"))
			{
				return nodeName.replaceFirst("^.*:", "");
			} else
				return nodeName;
		} else
			return nodeName;
	}

	private boolean doesNodeMatchXpath(Node node, String xpathExpression)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{
		List<Node> xpathMatchedNodes = xpathEvalCacheTL.get().get(xpathExpression);
		if (xpathMatchedNodes == null)
		{
			xpathMatchedNodes = evaluate(node, xpathExpression);
			xpathEvalCacheTL.get().put(xpathExpression, xpathMatchedNodes);
		}
		for (int index = 0; index < xpathMatchedNodes.size(); index++)
		{
			if (node.isSameNode(xpathMatchedNodes.get(index)))
			{
				xpathMatchedNodes.remove(index);
				return true;
			}
		}

		return false;
	}

	private List<Node> evaluate(Node node, String xpathExpression)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
	{
		List<Node> ret = new ArrayList<Node>();
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(xpathExpression);
		NodeList xpathMatchedNodeList = (NodeList) expr.evaluate(node.getOwnerDocument(), XPathConstants.NODESET);
		for (int index = 0; index < xpathMatchedNodeList.getLength(); index++)
		{
			ret.add(xpathMatchedNodeList.item(index));
		}

		return ret;
	}
}
