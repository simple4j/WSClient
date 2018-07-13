package org.simple4j.wsclient.formatter;

/**
 * The IFormatter interface is used on the request side to format data to 
 * request components (URL, headers, body) based on the template configuration.
 * 
 * @author jsrinivas108
 */
public interface IFormatter
{

	/**
	 * This method uses some templating mechanism to format the inputObject to a String
	 *  
	 * @param inputObject
	 * @return
	 * @throws Exception
	 */
	public String formatData(Object inputObject) throws Exception;

}
