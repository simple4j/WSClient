package org.simple4j.wsclient.parser;

import java.util.Map;

/**
 * @author jsrinivas108
 */
public interface IParser
{

	public Map<String, ? extends Object> parseData(String arg);

}
