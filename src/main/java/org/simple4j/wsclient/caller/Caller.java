package org.simple4j.wsclient.caller;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.formatter.IFormatter;
import org.simple4j.wsclient.http.HTTPWSClient;
import org.simple4j.wsclient.http.HTTPWSResponse;
import org.simple4j.wsclient.parser.IParser;
import org.simple4j.wsclient.util.CollectionsPathRetreiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entry point class for client program to make Web Service calls.
 * 
 * @see org.simple4j.wsclient.formatter.IFormatter and its subclasses - this is
 *      used to generate URL, RequestHeader and RequestBody using templating.
 * @see org.simple4j.wsclient.http.HTTPWSClient - this is the HTTP connection
 *      manager. Server host, port, http/https, timeouts are configured using
 *      this.
 * @see org.simple4j.wsclient.parser.IParser and its subclasses - this is used
 *      to parse the ResponseBody to java collections object tree
 * @author jsrinivas108
 */
public class Caller
{

	private static Logger logger = LoggerFactory.getLogger(Caller.class);
	private static InetAddress localHost = null;

	/**
	 * HTTP/HTTPS connector to send and receive HTTP requests.
	 */
	private HTTPWSClient httpWSClient;

	/**
	 * This will contain the HTTP method GET, POST, PUT and DELETE
	 */
	private String serviceMethod;

	/**
	 * Mapping to gather response values using property path notation. Key is the
	 * response field name and value is the property path expression. For example,
	 * in prop1.prop2[0].prop3.abc: prop1 - is a simple property prop2 - is an
	 * indexed property (List, Array etc.) with [0] representing first element prop3
	 * - is a java.util.Map property with abc representing the value with key "abc"
	 * 
	 * This also supports * wildcard for the index and the key. For example,
	 * prop1.prop2[*].prop3.*.prop4
	 */
	private Map<String, String> responseBodyToCustomFieldMapping;

	/**
	 * The field name where the HTTP status code will be returned. The default value
	 * is HTTP_STATUS_CODE.
	 */
	private String httpStatusCodeFieldName = "HTTP_STATUS_CODE";

	/**
	 * The field name where the HTTP response code will be returned. The default
	 * value is HTTP_RESPONSE_OBJECT.
	 */
	private String httpResponseObjectFieldName = "HTTP_RESPONSE_OBJECT";

	/**
	 * The field that specifies the HTTP header and a list of possible formatters
	 * for each value
	 */
	private Map<String, List<IFormatter>> requestHeaderFormatters;

	/**
	 * The Formatter to be used for preparing the HTTP request body
	 */
	private IFormatter requestBodyFormatter;

	/**
	 * The Formatter to be used for preparing the HTTP request URL
	 */
	private IFormatter requestURLFormatter;

	/**
	 * Map of responseBodyParsers keyed by regular expressions to match http status
	 * code. Value of the map in the parser
	 */
	private Map<String, IParser> responseBodyParsers;

	/**
	 * The response headers will be returned with this key in the response Map.
	 */
	private String responseHeaderFieldName = "HTTP_RESPONSE_HEADERS";

	/**
	 * This header will be injected with all request to trace requests from client
	 * to server. The value of the header is a random generated string. This header
	 * will not be injected if requestIdHeaderName is empty or null.
	 */
	private String requestIdHeaderName = "swsc-reqid";

	/**
	 * This header will be injected with all request to trace requests back to a
	 * client machine. The value of the header is the hostname of the client machine
	 * where this Caller is executed. This header will not be injected if its value
	 * is empty or null.
	 */
	private String clientHostHeaderName = "swsc-host";

	/**
	 * Any non templated static request headers and its values.
	 */
	private Map<String, List<String>> staticHeaderValues = null;

	private CollectionsPathRetreiver collectionsPathRetreiver = null;

	public String getResponseHeaderFieldName()
	{
		return responseHeaderFieldName;
	}

	public void setResponseHeaderFieldName(String responseHeaderFieldName)
	{
		this.responseHeaderFieldName = responseHeaderFieldName;
	}

	public Map<String, IParser> getResponseBodyParsers()
	{
		return responseBodyParsers;
	}

	public void setResponseBodyParsers(Map<String, IParser> responseBodyParsers)
	{
		this.responseBodyParsers = responseBodyParsers;
	}

	public String getRequestIdHeaderName()
	{
		return requestIdHeaderName;
	}

	public void setRequestIdHeaderName(String requestIdHeaderName)
	{
		this.requestIdHeaderName = requestIdHeaderName;
	}

	public String getClientHostHeaderName()
	{
		return clientHostHeaderName;
	}

	public void setClientHostHeaderName(String clientHostHeaderName)
	{
		this.clientHostHeaderName = clientHostHeaderName;
	}

	public Map<String, List<String>> getStaticHeaderValues()
	{
		return staticHeaderValues;
	}

	public void setStaticHeaderValues(Map<String, List<String>> staticHeaderValues)
	{
		this.staticHeaderValues = staticHeaderValues;
	}

	public Map<String, List<IFormatter>> getRequestHeaderFormatters()
	{
		return requestHeaderFormatters;
	}

	public void setRequestHeaderFormatters(Map<String, List<IFormatter>> requestHeaderFormatters)
	{
		this.requestHeaderFormatters = requestHeaderFormatters;
	}

	public IFormatter getRequestBodyFormatter()
	{
		return requestBodyFormatter;
	}

	public void setRequestBodyFormatter(IFormatter requestBodyFormatter)
	{
		this.requestBodyFormatter = requestBodyFormatter;
	}

	public IFormatter getRequestURLFormatter()
	{
		return requestURLFormatter;
	}

	public void setRequestURLFormatter(IFormatter requestURLFormatter)
	{
		this.requestURLFormatter = requestURLFormatter;
	}

	public HTTPWSClient getHttpWSClient()
	{
		return httpWSClient;
	}

	public void setHttpWSClient(HTTPWSClient httpWSClient)
	{
		this.httpWSClient = httpWSClient;
	}

	public String getServiceMethod()
	{
		return serviceMethod;
	}

	public void setServiceMethod(String serviceMethod)
	{
		this.serviceMethod = serviceMethod;
	}

	public Map<String, String> getResponseBodyToCustomFieldMapping()
	{
		return responseBodyToCustomFieldMapping;
	}

	public void setResponseBodyToCustomFieldMapping(Map<String, String> responseBodyToCustomFieldMapping)
	{
		this.responseBodyToCustomFieldMapping = responseBodyToCustomFieldMapping;
	}

	public String getHttpStatusCodeFieldName()
	{
		return httpStatusCodeFieldName;
	}

	public void setHttpStatusCodeFieldName(String httpStatusCodeFieldName)
	{
		this.httpStatusCodeFieldName = httpStatusCodeFieldName;
	}

	public String getHttpResponseObjectFieldName()
	{
		return httpResponseObjectFieldName;
	}

	public void setHttpResponseObjectFieldName(String httpResponseObjectFieldName)
	{
		this.httpResponseObjectFieldName = httpResponseObjectFieldName;
	}

	public CollectionsPathRetreiver getCollectionsPathRetreiver()
	{
		if (collectionsPathRetreiver == null)
		{
			collectionsPathRetreiver = new CollectionsPathRetreiver();
		}
		return collectionsPathRetreiver;
	}

	public void setCollectionsPathRetreiver(CollectionsPathRetreiver collectionsPathRetreiver)
	{
		this.collectionsPathRetreiver = collectionsPathRetreiver;
	}

	/**
	 * This is the entry method to invoke the configured Web Service call. Refer
	 * other configuration properties for more details of how each of them are used
	 * in the processing.
	 * 
	 * @param requestObject
	 *            - Will be used in the formatters for URL, RequestHeaders and
	 *            RequestBody. The specific usage is defined in their corresponding
	 *            template.
	 * @return - Map of return values. The keys of each of the object can be
	 *         configured through Caller properties.
	 */
	public Map<String, Object> call(Object requestObject)
	{
		try
		{
			Map<String, Object> ret = null;

			// formatting the service URL using the specified instance of IFormatter
			String serviceURL = this.getRequestURLFormatter().formatData(requestObject);
			logger.info("Finished formatting request URL: " + serviceURL);

			String requestBody = null;
			// formatting the request body using the specified instance of IFormatter
			if (this.getRequestBodyFormatter() != null)
			{
				requestBody = this.getRequestBodyFormatter().formatData(requestObject);
			}
			logger.info("Finished formatting request body: " + requestBody);

			// preparing each request header using the specified instances of IFormatter for
			// each header key
			Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
			if (this.getStaticHeaderValues() != null)
				requestHeaders.putAll(this.getStaticHeaderValues());
			for (Entry<String, List<IFormatter>> entry : this.getRequestHeaderFormatters().entrySet())
			{
				List<IFormatter> formatters = entry.getValue();
				Iterator<IFormatter> formatterIterator = formatters.iterator();
				while (formatterIterator.hasNext())
				{
					IFormatter formatter = formatterIterator.next();
					List<String> replacedValues = requestHeaders.get(entry.getKey());
					if (replacedValues == null)
					{
						replacedValues = new ArrayList<String>();
						requestHeaders.put(entry.getKey(), replacedValues);
					}
					replacedValues.add(formatter.formatData(requestObject));
				}
			}

			addRequestIdHeader(requestHeaders);
			addClientHostnameHeader(requestHeaders);
			// invoking the specified HTTP request for request data
			HTTPWSResponse response = invokeHTTP(serviceURL, requestBody, requestHeaders);

			String responseBodyAsString = response.getResponseBodyAsString();
			ret = new HashMap<String, Object>();

			// storing the response code
			ret.put(this.getHttpStatusCodeFieldName(), "" + response.getStatusCode());
			// storing the response parsed object
			ret.put(this.getHttpResponseObjectFieldName(), responseBodyAsString);
			if (this.getResponseBodyParsers() != null && this.getResponseBodyParsers().size() > 0)
			{
				for (Iterator<Entry<String, IParser>> iterator = this.getResponseBodyParsers().entrySet()
						.iterator(); iterator.hasNext();)
				{
					Entry<String, IParser> entry = iterator.next();
					if (("" + response.getStatusCode()).matches(entry.getKey()))
					{
						Map<String, ? extends Object> parseDataMap = entry.getValue().parseData(responseBodyAsString);
						ret.put(this.getHttpResponseObjectFieldName(), parseDataMap);
						Map<String, Object> objectsFromResponseBody = getObjectsFromResponseBody(parseDataMap);
						if (objectsFromResponseBody != null)
						{
							ret.putAll(objectsFromResponseBody);
						}

						break;
					}
				}
			}

			// storing the response headers and footers
			Map<String, String> headersMap = extractHeader(response.getResponseHeaders());
			ret.put(this.getResponseHeaderFieldName(), headersMap);

			// returning data to the requestor
			return ret;

		} catch (Exception t)
		{
			throw new SystemException("", "", t);
		}
	}

	private HTTPWSResponse invokeHTTP(String serviceURL, String requestBody, Map<String, List<String>> requestHeaders)
	{
		HTTPWSResponse response = null;
		if (this.getServiceMethod().equalsIgnoreCase("POST"))
		{
			response = this.getHttpWSClient().post(serviceURL, requestBody, requestHeaders);
		} else if (this.getServiceMethod().equalsIgnoreCase("GET"))
		{
			response = this.getHttpWSClient().get(serviceURL, requestHeaders);
		} else if (this.getServiceMethod().equalsIgnoreCase("DELETE"))
		{
			response = this.getHttpWSClient().delete(serviceURL, requestHeaders);
		} else
		{
			response = this.getHttpWSClient().put(serviceURL, requestBody, requestHeaders);
		}
		return response;
	}

	private Map<String, Object> getObjectsFromResponseBody(Map responseBodyObj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		if (this.getResponseBodyToCustomFieldMapping() == null
				|| this.getResponseBodyToCustomFieldMapping().size() == 0)
			return null;
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Entry<String, String> entry : this.getResponseBodyToCustomFieldMapping().entrySet())
		{
			/*
			 * if (entry.getValue().contains("[*]") || entry.getValue().contains("(*)")) {
			 * ret.put(entry.getKey(), this.getMultiObjectsFromBean(responseBodyObj,
			 * entry.getValue())); } else { Object valueObj =
			 * PropertyUtils.getNestedProperty(responseBodyObj, entry.getValue()); String
			 * valueStr; if (valueObj != null && !(valueObj instanceof String)) valueStr =
			 * "" + valueObj; else valueStr = (String) valueObj; ret.put(entry.getKey(),
			 * valueStr); }
			 */
			logger.debug("fetching response object:" + responseBodyObj + ", propertyPath:" + entry.getValue());
			List nestedProperty = this.getCollectionsPathRetreiver().getNestedProperty(responseBodyObj,
					entry.getValue());
			ret.put(entry.getKey(), nestedProperty);
		}
		return ret;
	}

	private List<Object> getMultiObjectsFromBean(Object bean, String propertyPathPattern)
	{
		List<Object> ret = new ArrayList<Object>();
		if (propertyPathPattern == null || propertyPathPattern.trim().length() <= 0)
		{
			ret.add(bean);
			return ret;
		}
		int arrayWildcardIndex = propertyPathPattern.indexOf("[*]");
		int mapWildcardIndex = propertyPathPattern.indexOf("(*)");

		if (arrayWildcardIndex > -1 && mapWildcardIndex > -1 && arrayWildcardIndex < mapWildcardIndex)
		{
			ret.addAll(handleIndexedProperty(bean, propertyPathPattern, arrayWildcardIndex));
		} else if (arrayWildcardIndex > -1 && mapWildcardIndex > -1 && arrayWildcardIndex > mapWildcardIndex)
		{
			ret.addAll(this.handleMapProperty(bean, propertyPathPattern, mapWildcardIndex));
		} else if (arrayWildcardIndex > -1)
		{
			ret.addAll(handleIndexedProperty(bean, propertyPathPattern, arrayWildcardIndex));
		} else
		{
			ret.addAll(this.handleMapProperty(bean, propertyPathPattern, mapWildcardIndex));
		}
		return ret;
	}

	private List<Object> handleIndexedProperty(Object bean, String propertyPathPattern, int arrayWildcardIndex)
	{
		List<Object> ret = new ArrayList<Object>();
		int propertyPathPatternLength = propertyPathPattern.length();
		String propertyPath = propertyPathPattern.substring(0, arrayWildcardIndex);
		String remainingPropertyPath = null;
		boolean moreWildcard = false;
		if (propertyPathPatternLength > arrayWildcardIndex + 4) /* 4 chars including . "[*]." */
		{
			remainingPropertyPath = propertyPathPattern.substring(arrayWildcardIndex + 4);
			moreWildcard = remainingPropertyPath.contains("[*]") || remainingPropertyPath.contains("(*)");
		}
		Object propertyValue;
		try
		{
			propertyValue = PropertyUtils.getNestedProperty(bean, propertyPath);
			if (propertyValue == null)
			{
				return ret;
			}
			int length = 0;
			if (propertyValue.getClass().isArray())
			{
				length = Array.getLength(propertyValue);
			} else
			{
				length = ((Collection<?>) propertyValue).size();
			}

			for (int i = 0; i < length; i++)
			{
				if (moreWildcard)
				{
					Object propertyValueElement = PropertyUtils.getNestedProperty(bean, propertyPath + "[" + i + "]");
					if (propertyValueElement != null)
					{
						ret.addAll(this.getMultiObjectsFromBean(propertyValueElement, remainingPropertyPath));
					}
				} else
				{
					Object propertyValueElement = null;
					if (remainingPropertyPath == null)
					{
						propertyValueElement = PropertyUtils.getNestedProperty(bean, propertyPath + "[" + i + "]");
					} else
					{
						propertyValueElement = PropertyUtils.getNestedProperty(bean,
								propertyPath + "[" + i + "]." + remainingPropertyPath);
					}
					if (propertyValueElement != null)
					{
						ret.addAll(this.getMultiObjectsFromBean(propertyValueElement, remainingPropertyPath));
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			logger.warn("Error while getting wildcard property path segment:" + propertyPath, e);
		}
		return ret;
	}

	private List<Object> handleMapProperty(Object bean, String propertyPathPattern, int mapWildcardIndex)
	{
		List<Object> ret = new ArrayList<Object>();
		int propertyPathPatternLength = propertyPathPattern.length();
		String propertyPath = propertyPathPattern.substring(0, mapWildcardIndex);
		String remainingPropertyPath = null;
		boolean moreWildcard = false;
		if (propertyPathPatternLength > mapWildcardIndex + 4) /* 4 chars including . "(*)." */
		{
			remainingPropertyPath = propertyPathPattern.substring(mapWildcardIndex + 4);
			moreWildcard = remainingPropertyPath.contains("[*]") || remainingPropertyPath.contains("(*)");
		}
		Map<String, Object> propertyValue;
		try
		{
			propertyValue = (Map<String, Object>) PropertyUtils.getNestedProperty(bean, propertyPath);
			if (propertyValue == null)
			{
				return ret;
			}

			for (Entry<String, Object> entry : propertyValue.entrySet())
			{
				if (moreWildcard)
				{
					Object propertyValueElement = PropertyUtils.getNestedProperty(bean,
							propertyPath + "(" + entry.getKey() + ")");
					if (propertyValueElement != null)
					{
						ret.addAll(this.getMultiObjectsFromBean(propertyValueElement, remainingPropertyPath));
					}
				} else
				{
					Object propertyValueElement = null;
					if (remainingPropertyPath == null)
					{
						propertyValueElement = PropertyUtils.getNestedProperty(bean,
								propertyPath + "(" + entry.getKey() + ")");
					} else
					{
						propertyValueElement = PropertyUtils.getNestedProperty(bean,
								propertyPath + "(" + entry.getKey() + ")." + remainingPropertyPath);
					}
					if (propertyValueElement != null)
					{
						ret.addAll(this.getMultiObjectsFromBean(propertyValueElement, remainingPropertyPath));
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			logger.warn("Error while getting wildcard property path segment:" + propertyPath, e);
		}
		return ret;
	}

	private void addRequestIdHeader(Map<String, List<String>> requestHeaders)
	{
		if (this.getRequestIdHeaderName() != null && this.getRequestIdHeaderName().trim().length() > 0)
		{
			ArrayList<String> headerValues = new ArrayList<String>();
			headerValues.add(UUID.randomUUID().toString());
			requestHeaders.put(this.getRequestIdHeaderName(), headerValues);
		}
	}

	private void addClientHostnameHeader(Map<String, List<String>> requestHeaders)
	{
		if (this.getClientHostHeaderName() != null && this.getClientHostHeaderName().trim().length() > 0)
		{
			if (localHost == null)
			{
				try
				{
					localHost = Inet4Address.getLocalHost();
				} catch (UnknownHostException e)
				{
					logger.warn("Error while getting local host name and IP", e);
				}
			}
			ArrayList<String> headerValues = new ArrayList<String>();
			headerValues.add("" + localHost);
			requestHeaders.put(this.getClientHostHeaderName(), headerValues);
		}
	}

	private Map<String, String> extractHeader(Map<String, List<String>> responseHeaders)
	{
		Map<String, String> ret = new HashMap<String, String>();
		if (responseHeaders != null && responseHeaders.size() > 0)
		{
			for (Entry<String, List<String>> entry : responseHeaders.entrySet())
			{

				if (entry.getValue().size() > 1)
				{
					for (int i = 0; i < entry.getValue().size(); i++)
					{
						ret.put(entry.getKey() + "[" + i + "]", entry.getValue().get(i));
					}
				} else if (entry.getValue().size() == 1)
				{
					ret.put(entry.getKey(), entry.getValue().get(0));
				}
			}
		}
		return ret;
	}

}