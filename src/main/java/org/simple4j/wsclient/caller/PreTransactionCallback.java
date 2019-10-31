package org.simple4j.wsclient.caller;

import java.util.List;
import java.util.Map;

/**
 * An instance of PreTransactionCallback will be called after the URL, headers
 * and body are derived. It can be used for custom logic like signing the
 * request just before making the transaction.
 */
public interface PreTransactionCallback
{
	
	/**
	 * This method will be invoked to update the request URL before sending the request
	 * @param method - HTTP method
	 * @param serviceURL - service URL
	 * @param requestBody - request body
	 * @param requestHeaders - request headers
	 * @return updated URL including query parameters
	 */
	public String updateURL(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders);

	/**
	 * This method will be invoked to update the request body before sending the request
	 * @param method - HTTP method
	 * @param serviceURL - service URL
	 * @param requestBody - request body
	 * @param requestHeaders - request headers
	 * @return updated body text
	 */
	public String updateBody(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders);

	/**
	 * This method will be invoked to update the request headers before sending the request
	 * @param method - HTTP method
	 * @param serviceURL - service URL
	 * @param requestBody - request body
	 * @param requestHeaders - request headers
	 * @return updated request headers
	 */
	public Map<String, List<String>> updateRequestHeader(String method, String serviceURL, String requestBody,
			Map<String, List<String>> requestHeaders);
}
