package org.simple4j.wsclient.caller.factory;

import java.util.Map;

/**
 * @see FreeMarkerCallerFactoryConfiguration
 * 
 * @author jsrinivas108
 *
 */
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
