package org.simple4j.wsclient.caller;

import java.util.Map;

/**
 * @see FreeMarkerJSONCallerFactoryConfiguration
 * 
 * @author jsrinivas108
 *
 */
public class Request
{
/*
{
`        "method": "PUT",
`        "urlPattern": "/simple4j/wsmock/httpmethods/put/.*",
`        "body": "{\"responsevalue\":\"{{request.body}}\", \"reqheadr2\":\"{{request.headers.x-header2}}\"}",
`        "staticHeaders": {
            "Content-Type": "application/JSON"
        },
`        "headers": {
            "aaa": "<freemarker template>"
        }
    } */
	
	private String method = null;
	private String urlPattern = null;
	private String body = null;
	private Map<String, String> staticHeaders = null;
	private Map<String, String> headers = null;
	public String getMethod()
	{
		return method;
	}
	public void setMethod(String method)
	{
		this.method = method;
	}
	public String getUrlPattern()
	{
		return urlPattern;
	}
	public void setUrlPattern(String urlPattern)
	{
		this.urlPattern = urlPattern;
	}
	public String getBody()
	{
		return body;
	}
	public void setBody(String body)
	{
		this.body = body;
	}
	public Map<String, String> getStaticHeaders()
	{
		return staticHeaders;
	}
	public void setStaticHeaders(Map<String, String> staticHeaders)
	{
		this.staticHeaders = staticHeaders;
	}
	public Map<String, String> getHeaders()
	{
		return headers;
	}
	public void setHeaders(Map<String, String> headers)
	{
		this.headers = headers;
	}
	
	
}
