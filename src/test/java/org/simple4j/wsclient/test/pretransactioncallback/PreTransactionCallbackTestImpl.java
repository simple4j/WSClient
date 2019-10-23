package org.simple4j.wsclient.test.pretransactioncallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simple4j.wsclient.caller.PreTransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreTransactionCallbackTestImpl implements PreTransactionCallback
{

	private static Logger logger = LoggerFactory.getLogger(PreTransactionCallbackTestImpl.class);
	
	@Override
	public String updateURL(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders)
	{
		logger.debug("Inside updateURL{}", serviceURL);
		if(serviceURL.indexOf("?") > -1)
		{
			return serviceURL + "&callbackkey=callbackvalue";
		}
		return serviceURL + "?callbackkey=callbackvalue";
	}

	@Override
	public String updateBody(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders)
	{
		logger.debug("Inside updateBody{}", requestBody);
		String signatureValue = "signatureVal1";
		return requestBody + signatureValue;
	}

	@Override
	public Map<String, List<String>> updateRequestHeader(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders)
	{
		logger.debug("Inside updateRequestHeader{}", requestHeaders);
		List<String> value = new ArrayList<String>();
		value.add("callbackheadervalue1");
		requestHeaders.put("callbackheaderkey", value );
		return requestHeaders;
	}

}
