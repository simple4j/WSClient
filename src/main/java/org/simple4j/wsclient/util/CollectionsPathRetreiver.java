/**
 * Contains any additional utility classes that the framework uses.
 */
package org.simple4j.wsclient.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CollectionsPathRetriever can take a Java Collections based object tree, nested property path and 
 * returns java.util.List of objects that match the nested property path.
 * The return value is a List because the nested property path supports wild cards.
 * 
 * For example, let us consider below Java Colleections base object tree of a sales person (may be parsed from XML or JSON response)
 * 
 * {	//Sales person object Map with salesPersonId and a List of orders
 * 	salesPersonId=sp-dd872619-7cf2-4bc1-b595-7434d09a4799,
 * 	orders=
 * 	[	//List of orders
 * 		{	//First order object Map with orderId, orderDate, orderItems
 * 			orderId=oifd5b0b84-cece-4d65-a7fb-1bf079ae7abd,
 * 			orderDate=Wed Jul 18 18:25:42 EDT 2018,
 * 			orderItems=
 * 			{	//orderItems is a Map instead of List because 
 * 				//		the key for the orderItems is product to make sure duplicate orderItems for the same product does not exist and 
 * 				//		the value is quantity, orderItemId representing DB key and status
 * 				{
 * 					productId=15612,
 * 					productDescription=This is a sample test product:15612
 * 				}
 * 				=
 * 				{
 * 					quantity=0,
 * 					orderItemId=oii30a00dfc-89d0-4747-a9c8-ab7dee630480,
 * 					status=ois69b76f1d-92c6-403f-8f44-60c466047417
 * 				}
 * 				,
 * 				{
 * 					productId=3489,
 * 					productDescription=This is a sample test product:3489
 * 				}
 * 				=
 * 				{
 * 					quantity=9,
 * 					orderItemId=oiidebe95c3-6b38-4988-94a9-a4481d56ea5b,
 * 					status=oisd027f2df-8a59-4616-9237-02de812694ac
 * 				}
 * 			}
 * 		}
 * 		,
 * 		{	//Second order same structure as first order
 * 			orderId=oi26a18bd9-9557-4497-bb26-77a7fe124f60,
 * 			orderDate=Wed Jul 18 18:25:42 EDT 2018,
 * 			orderItems=
 * 			{
 * 				{
 * 					productId=44136,
 * 					productDescription=This is a sample test product:44136
 * 				}
 * 				=
 * 				{
 * 					quantity=6,
 * 					orderItemId=oiiac12bb0f-d640-491d-a4d6-d90e025ccc42,
 * 					status=oisf9a69127-fa29-459e-9913-0f994e1a6d90
 * 				}
 * 				,
 * 				{
 * 					productId=3894,
 * 					productDescription=This is a sample test product:3894
 * 				}
 * 				=
 * 				{
 * 					quantity=2,
 * 					orderItemId=oii25be8b51-c213-4866-b6e9-cc79bf103de0,
 * 					status=oisfcf0e776-87dd-4e20-8563-cbc2170f00fd
 * 				}
 * 			}
 * 		}
 * 	]
 * }
 * 
 * 
 * nested property path of "salesPersonId" will return below results
 * [sp-dd872619-7cf2-4bc1-b595-7434d09a4799]
 * 
 * 
 * nested property path of "salesPersonId1" will return below results
 * []
 * 
 * 
 * nested property path of "orders" (same result for order[*]) will return below results
 * [
 * 	{
 * 		orderId=oifd5b0b84-cece-4d65-a7fb-1bf079ae7abd, 
 * 		orderDate=Wed Jul 18 18:25:42 EDT 2018, 
 * 		orderItems=
 * 		{
 * 			{
 * 				productId=15612, 
 * 				productDescription=This is a sample test product:15612
 * 			}
 * 			=
 * 			{
 * 				quantity=0, 
 * 				orderItemId=oii30a00dfc-89d0-4747-a9c8-ab7dee630480, 
 * 				status=ois69b76f1d-92c6-403f-8f44-60c466047417
 * 			}, 
 * 			{
 * 				productId=3489, 
 * 				productDescription=This is a sample test product:3489
 * 			}
 * 			=
 * 			{
 * 				quantity=9, 
 * 				orderItemId=oiidebe95c3-6b38-4988-94a9-a4481d56ea5b, 
 * 				status=oisd027f2df-8a59-4616-9237-02de812694ac
 * 			}
 * 		}
 * 	}, 
 * 	{
 * 		orderId=oi26a18bd9-9557-4497-bb26-77a7fe124f60, 
 * 		orderDate=Wed Jul 18 18:25:42 EDT 2018, 
 * 		orderItems=
 * 		{
 * 			{
 * 				productId=44136, 
 * 				productDescription=This is a sample test product:44136
 * 			}
 * 			=
 * 			{
 * 				quantity=6, 
 * 				orderItemId=oiiac12bb0f-d640-491d-a4d6-d90e025ccc42, 
 * 				status=oisf9a69127-fa29-459e-9913-0f994e1a6d90
 * 			}, 
 * 			{
 * 				productId=3894, 
 * 				productDescription=This is a sample test product:3894
 * 			}
 * 			=
 * 			{
 * 				quantity=2, 
 * 				orderItemId=oii25be8b51-c213-4866-b6e9-cc79bf103de0, 
 * 				status=oisfcf0e776-87dd-4e20-8563-cbc2170f00fd
 * 			}
 * 		}
 * 	}
 * ]
 * 
 * 
 * nested property path of "orders[0]" will return below results
 * [
 * 	{
 * 		orderId=oifd5b0b84-cece-4d65-a7fb-1bf079ae7abd, 
 * 		orderDate=Wed Jul 18 18:25:42 EDT 2018, 
 * 		orderItems=
 * 		{
 * 			{
 * 				productId=15612, 
 * 				productDescription=This is a sample test product:15612
 * 			}
 * 			=
 * 			{
 * 				quantity=0, 
 * 				orderItemId=oii30a00dfc-89d0-4747-a9c8-ab7dee630480, 
 * 				status=ois69b76f1d-92c6-403f-8f44-60c466047417
 * 			}, 
 * 			{
 * 				productId=3489, 
 * 				productDescription=This is a sample test product:3489
 * 			}
 * 			=
 * 			{
 * 				quantity=9, 
 * 				orderItemId=oiidebe95c3-6b38-4988-94a9-a4481d56ea5b, 
 * 				status=oisd027f2df-8a59-4616-9237-02de812694ac
 * 			}
 * 		}
 * 	}
 * ]
 * 
 * 
 * nested property path of "orders[0].orderItems" will return below results
 * [
 * 	{
 * 		{
 * 			productId=15612,
 * 			productDescription=This is a sample test product:15612
 * 		}
 * 		=
 * 		{
 * 			quantity=0,
 * 			orderItemId=oii30a00dfc-89d0-4747-a9c8-ab7dee630480,
 * 			status=ois69b76f1d-92c6-403f-8f44-60c466047417
 * 		}
 * 		,
 * 		{
 * 			productId=3489,
 * 			productDescription=This is a sample test product:3489
 * 		}
 * 		=
 * 		{
 * 			quantity=9,
 * 			orderItemId=oiidebe95c3-6b38-4988-94a9-a4481d56ea5b,
 * 			status=oisd027f2df-8a59-4616-9237-02de812694ac
 * 		}
 * 	}
 * ]
 * 
 * 
 * nested property path of "orders[0].orderItems.KEYS" will return below results
 * [
 * 	{
 * 		productId=15612,
 * 		productDescription=This is a sample test product:15612
 * 	}
 * 	,
 * 	{
 * 		productId=3489,
 * 		productDescription=This is a sample test product:3489
 * 	}
 * ]
 * 
 * 
 * nested property path of "orders[0].orderItems.KEYS[LENGTH]" will return below results
 * [2]
 * 
 * 
 * nested property path of "orders[0].orderItems.KEYS[0]" will return below results
 * [{productId=15612, productDescription=This is a sample test product:15612}]
 * 
 * 
 * nested property path of "orders[0].orderItems.KEYS[1].productId" will return below results
 * [3489]
 * 
 * 
 * nested property path of "orders[LENGTH]" will return below results
 * [2]
 * 
 * 
 * @author jsrinivas108
 *
 */
public class CollectionsPathRetreiver
{

	private static Logger logger = LoggerFactory.getLogger(CollectionsPathRetreiver.class);
	private String keysPropertyName = "KEYS";
	private String listLengthPropertyName = "LENGTH";

	public String getKeysPropertyName()
	{
		return keysPropertyName;
	}

	public void setKeysPropertyName(String keysPropertyName)
	{
		this.keysPropertyName = keysPropertyName;
	}

	public String getListLengthPropertyName()
	{
		return listLengthPropertyName;
	}

	public void setListLengthPropertyName(String listLengthPropertyName)
	{
		this.listLengthPropertyName = listLengthPropertyName;
	}

	public List getNestedProperty(Map target, String propertyPath)
	{
		if (target == null)
		{
			return null;
		}
		List ret = new ArrayList();
		String escapedPropertyPath = escapePropertyPath(propertyPath);

		// Below lines of code gets the first path segment and identifies if more path
		// segments exists
		String escapedPropertyPathSegment = null;
		String escapedRemainingPropertyPath = null;
		int dotIndex = escapedPropertyPath.indexOf(".");
		if (dotIndex + 1 >= escapedPropertyPath.length())
		{
			throw new RuntimeException("Property path ending in a dot:" + propertyPath);
		}
		if (dotIndex > -1)
		{
			escapedPropertyPathSegment = escapedPropertyPath.substring(0, dotIndex);
			escapedRemainingPropertyPath = escapedPropertyPath.substring(dotIndex + 1);
		} else
		{
			escapedPropertyPathSegment = escapedPropertyPath;
		}

		String escapedPropertyPathSegmentBase = escapedPropertyPathSegment;
		int indexOfSqrBracketOpen = escapedPropertyPathSegment.indexOf("[");
		if (indexOfSqrBracketOpen > -1)
		{
			// current property path segement has [ and hence an indexed property
			// below line gets the base property name of the current segment
			escapedPropertyPathSegmentBase = escapedPropertyPathSegment.substring(0, indexOfSqrBracketOpen);

			// Since its an indexted property, it should be a List
			List property = (List) getProperty(target, escapedPropertyPathSegmentBase);
			if (property == null)
			{
				if (escapedPropertyPathSegmentBase.equals(this.getKeysPropertyName()))
				{
					property = new ArrayList(target.keySet());
				} else
				{
					return null;
				}
			}

			// current property could be multi level indexed property similar to multi
			// dimentional array like [1][3][2]
			// below line recurses and gets the values. Also handles wild card indexes
			// [*][1][3]
			List vals = getMultilevelIndexedProperty(property,
					escapedPropertyPathSegment.substring(escapedPropertyPathSegmentBase.length()));
			if (vals != null)
			{
				if (escapedRemainingPropertyPath != null)
				{
					// loop through each of the values and proceed to next property path segment
					for (int i = 0; i < vals.size(); i++)
					{
						if (vals.get(i) instanceof Map)
						{
							Map mapProperty = (Map) vals.get(i);
							if (mapProperty != null)
							{
								List val = getNestedProperty(mapProperty, escapedRemainingPropertyPath);
								if (val != null)
									ret.addAll(val);
								else
									return null;
							} else
							{
								return null;
							}
						} else
						{
							System.out.println("List element is not a Map but has remaining path to process:"
									+ unescapePropertyPath(escapedRemainingPropertyPath));
						}
					}
				} else
				{
					// end of the property path segments. so add all to the return list
					ret.addAll(vals);
				}
			}
		} else
		{
			// non indexed property path segment
			if (escapedRemainingPropertyPath != null)
			{
				if (escapedPropertyPathSegmentBase.equalsIgnoreCase("*"))
				{
					Set<Entry> entrySet = target.entrySet();
					for (Entry entry : entrySet)
					{

						Object val = entry.getValue();
						if (val instanceof Map)
						{
							List nestedVal = getNestedMappedProperty((Map) val, escapedRemainingPropertyPath,
									escapedPropertyPathSegmentBase);
							if (nestedVal != null)
								ret.addAll(nestedVal);
						} else
						{
							System.out.println(val + " not instance of Map. But continuing processing.");
						}
					}
				} else
				{
					List val = getNestedMappedProperty(target, escapedRemainingPropertyPath,
							escapedPropertyPathSegmentBase);
					if (val != null)
						ret.addAll(val);
					else
						return null;
				}
			} else
			{
				if (this.getKeysPropertyName().equals(escapedPropertyPathSegmentBase))
				{
					ret.addAll(new ArrayList(target.keySet()));
				} else
				{
					// more property path segments are there to process, add it to the return List
					Object property = getProperty(target, escapedPropertyPathSegmentBase);
					if (property != null)
					{
						if (property instanceof List)
						{
							ret.addAll((List) property);
						} else
						{
							ret.add(property);
						}
					}
				}
			}
		}
		return ret;
	}

	private List getNestedMappedProperty(Map target, String escapedRemainingPropertyPath,
			String escapedPropertyPathSegmentBase)
	{
		if (this.getKeysPropertyName().equals(escapedPropertyPathSegmentBase))
		{
			if (escapedRemainingPropertyPath != null)
			{
				List ret = null;
				Set keySet = target.keySet();
				for (Iterator iterator = keySet.iterator(); iterator.hasNext();)
				{
					Object key = (Object) iterator.next();
					if (key != null)
					{
						if (key instanceof Map)
						{
							List val = getNestedProperty((Map) key, escapedRemainingPropertyPath);
							if (ret == null)
							{
								ret = val;
							} else
							{
								ret.addAll(val);
							}
						} else
						{
							System.out.println("key is not Map but has remaining path without index to process:"
									+ unescapePropertyPath(escapedRemainingPropertyPath));
						}
					}
				}
			} else
			{
				return new ArrayList(target.keySet());
			}
		}
		// more property path segments are there to process, so it must be a Map.
		// Proceed to next property path segment
		Map property = (Map) getProperty(target, escapedPropertyPathSegmentBase);
		if (property != null)
		{
			List val = getNestedProperty(property, escapedRemainingPropertyPath);
			return val;
		} else
		{
			return null;
		}
	}

	private String escapePropertyPath(String propertyPath)
	{
		String escapedPropertyPath = propertyPath.replaceAll("\\\\[.]", "~!@DOT@!~");
		escapedPropertyPath = escapedPropertyPath.replaceAll("\\\\[*]", "~!@STAR@!~");
		escapedPropertyPath = escapedPropertyPath.replaceAll("\\\\[\\(]", "~!@OPEN_BRACE@!~");
		escapedPropertyPath = escapedPropertyPath.replaceAll("\\\\[\\)]", "~!@CLOSE_BRACE@!~");

		escapedPropertyPath = escapedPropertyPath.replaceAll("\\\\[\\[]", "~!@OPEN_SQ_BRACE@!~");
		escapedPropertyPath = escapedPropertyPath.replaceAll("\\\\[\\]]", "~!@CLOSE_SQ_BRACE@!~");
		return escapedPropertyPath;
	}

	private Object getProperty(Map target, String escapedPropertyPathSegmentBase)
	{
		String propertyName = unescapePropertyPath(escapedPropertyPathSegmentBase);
		return target.get(propertyName);
	}

	private String unescapePropertyPath(String escapedPropertyPathSegmentBase)
	{
		String propertyPath = escapedPropertyPathSegmentBase.replaceAll("(~!@DOT@!~)", ".");
		propertyPath = propertyPath.replaceAll("(~!@STAR@!~)", "*");

		propertyPath = propertyPath.replaceAll("(~!@OPEN_BRACE@!~)", "(");
		propertyPath = propertyPath.replaceAll("(~!@CLOSE_BRACE@!~)", ")");

		propertyPath = propertyPath.replaceAll("(~!@OPEN_SQ_BRACE@!~)", "[");
		propertyPath = propertyPath.replaceAll("(~!@CLOSE_SQ_BRACE@!~)", "]");
		return propertyPath;
	}

	private List getMultilevelIndexedProperty(List target, String indexPaths)
	{
		// if(!directIndexing && indexOfSqrBracketOpen == 0)
		// throw new RuntimeException("Map cannot have index as key");
		int indexOfSqrBracketClose = indexPaths.indexOf("]");
		if (indexOfSqrBracketClose <= 1)
			throw new RuntimeException("Square brackets out of sequence or not matching");
		String indexStr = indexPaths.substring(1, indexOfSqrBracketClose);
		if (indexStr.matches("[0-9]+"))
		{
			int index = Integer.parseInt(indexStr);
			if (index < 0 || index > target.size() - 1)
			{
				logger.info("Property index out of range returning nothing. Target size:{} index:{}", target.size(),
						index);
				return new ArrayList();
			}
			Object val = target.get(index);
			if (indexPaths.length() - 1 > indexOfSqrBracketClose)
			{
				indexPaths = indexPaths.substring(indexOfSqrBracketClose + 1);
				if (val instanceof List)
					return getMultilevelIndexedProperty((List) val, indexPaths);
				else
					throw new RuntimeException("Nested indexed property does not contain List");
			} else
			{
				List ret = new ArrayList();
				ret.add(val);
				return ret;
			}
		} else if (indexStr.matches("[*]"))
		{
			if (indexPaths.length() - 1 > indexOfSqrBracketClose)
			{
				indexPaths = indexPaths.substring(indexOfSqrBracketClose + 1);
				List ret = new ArrayList();
				for (int i = 0; i < target.size(); i++)
				{
					if (target.get(i) instanceof List)
					{
						List val = getMultilevelIndexedProperty((List) target.get(i), indexPaths);
						if (val != null)
							ret.addAll(val);
					} else
						throw new RuntimeException("Nested indexed property does not contain List");
				}
				return ret;
			} else
			{
				// There is no sub path and the current index is wildcard, return the incoming
				// list
				return target;
			}
		} else if (indexStr.matches("(" + this.getListLengthPropertyName() + ")"))
		{
			List ret = new ArrayList();
			ret.add(target.size());
			return ret;
		} else
		{
			throw new RuntimeException("Index not a positive integer and not a wildcard");
		}
	}

}
