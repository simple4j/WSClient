package org.simple4j.wsclient.caller;

public class FreeMarkerJSONCallerFactoryConfiguration
{
	/*
{
`	"freemarkerVersion":"2.3.23",
    "request": {
`        "method": "PUT",
`        "urlPattern": "/simple4j/wsmock/httpmethods/put/.*",
`        "body": "{\"responsevalue\":\"{{request.body}}\", \"reqheadr2\":\"{{request.headers.x-header2}}\"}",
`        "staticHeaders": {
            "Content-Type": "application/JSON"
        },
`        "headers": {
            "aaa": "<freemarker template>"
        }
    },
    "response": {
`        "responseBodyToCustomFieldMapping": {
            "asdf": "...."
        }
    }
}

	 */

	private String freemarkerVersion = null;
	private Request request = null;
	private Response response = null;
	public String getFreemarkerVersion()
	{
		return freemarkerVersion;
	}
	public void setFreemarkerVersion(String freemarkerVersion)
	{
		this.freemarkerVersion = freemarkerVersion;
	}
	public Request getRequest()
	{
		return request;
	}
	public void setRequest(Request request)
	{
		this.request = request;
	}
	public Response getResponse()
	{
		return response;
	}
	public void setResponse(Response response)
	{
		this.response = response;
	}
	
	
}
