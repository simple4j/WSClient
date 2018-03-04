package org.simple4j.wsclient.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * @author jsrinivas108
 */
public class HTTPWSResponse
{

	private Map<String, List<String>> responseHeaders = null;
	private String responseBodyAsString = null;
	private int statusCode = -1;
	private String statusLine = null;
	private String statusText = null;
	private String contentType = null;
	private String contentEncoding = null;

	public HTTPWSResponse(HttpResponse response) throws ParseException, IOException
	{
		StatusLine statusLineObj = response.getStatusLine();
		this.statusLine = statusLineObj.toString();
		this.statusCode = statusLineObj.getStatusCode();
		this.statusText = statusLineObj.getReasonPhrase();

		this.responseHeaders = getHeaders(response.getAllHeaders());

		HttpEntity entity = response.getEntity();
		this.contentType = entity.getContentType().getValue();
		if (entity.getContentEncoding() != null)
		{
			this.contentEncoding = entity.getContentEncoding().getValue();
		}
		this.responseBodyAsString = entity != null ? EntityUtils.toString(entity) : null;
	}

	private Map<String, List<String>> getHeaders(Header[] responseHeaders)
	{
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		for (int i = 0; i < responseHeaders.length; i++)
		{
			List<String> responseFooter = ret.get(responseHeaders[i].getName());
			if (responseFooter == null)
			{
				responseFooter = new ArrayList<String>();
			}
			responseFooter.add(responseHeaders[i].getValue());
		}
		return ret;
	}

	public Map<String, List<String>> getResponseHeaders()
	{
		return responseHeaders;
	}

	public String getResponseBodyAsString()
	{
		return responseBodyAsString;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public String getStatusLine()
	{
		return statusLine;
	}

	public String getStatusText()
	{
		return statusText;
	}

	public String getContentType()
	{
		return contentType;
	}

	public String getContentEncoding()
	{
		return contentEncoding;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("[responseHeaders=");
		builder.append(responseHeaders);
		builder.append(", responseBodyAsString=");
		builder.append(responseBodyAsString);
		builder.append(", statusCode=");
		builder.append(statusCode);
		builder.append(", statusLine=");
		builder.append(statusLine);
		builder.append(", statusText=");
		builder.append(statusText);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", contentEncoding=");
		builder.append(contentEncoding);
		builder.append("]");
		return builder.toString();
	}

}