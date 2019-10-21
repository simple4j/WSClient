package org.simple4j.wsclient.caller;

import java.util.Map;

public class Response
{
/*
{
`        "responseBodyToCustomFieldMapping": {
            "asdf": "...."
        }
 */
	private Map<String,String> responseBodyToCustomFieldMapping = null;

	public Map<String, String> getResponseBodyToCustomFieldMapping()
	{
		return responseBodyToCustomFieldMapping;
	}

	public void setResponseBodyToCustomFieldMapping(Map<String, String> responseBodyToCustomFieldMapping)
	{
		this.responseBodyToCustomFieldMapping = responseBodyToCustomFieldMapping;
	}
	
}
