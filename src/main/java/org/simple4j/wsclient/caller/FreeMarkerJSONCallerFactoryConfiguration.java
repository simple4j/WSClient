package org.simple4j.wsclient.caller;

/**
 * This is POJO representation of JSON structure in FreeMarkerJSONCallerFactory.jSONConfigFile
 * The structure of JSON is
 * 
 * {
 * 	"freemarketEncoding":"UTF-8",
 * 	"freemarkerVersion":"2.3.23",
 *     "request": {
 *         "method": "PUT",
 *         "urlPattern": "/simple4j/wsmock/httpmethods/put/.*",
 *         "body": "{\"responsevalue\":\"{{request.body}}\", \"reqheadr2\":\"{{request.headers.x-header2}}\"}",
 *         "staticHeaders": {
 *             "Content-Type": "application/JSON"
 *         },
 *         "headers": {
 *             "aaa": "&lt;freemarker template&gt;"
 *         }
 *     },
 *     "response": {
 *         "responseBodyToCustomFieldMapping": {
 *             "asdf": "...."
 *         }
 *     }
 * }
 * 
 * @author jsrinivas108
 *
 */
public class FreeMarkerJSONCallerFactoryConfiguration
{

	private String freemarkerEncoding = null;
	private String freemarkerVersion = null;
	private Request request = null;
	private Response response = null;
	public String getFreemarkerEncoding()
	{
		return freemarkerEncoding;
	}
	public void setFreemarkerEncoding(String freemarkerEncoding)
	{
		this.freemarkerEncoding = freemarkerEncoding;
	}
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
