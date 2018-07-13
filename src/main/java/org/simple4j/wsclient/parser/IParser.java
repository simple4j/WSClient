package org.simple4j.wsclient.parser;

import java.util.Map;

/**
 * The IParser interface is used on the response side to parse data from body.
 * The output of the parser is Java Collections based object tree.
 * 
 * @author jsrinivas108
 */
public interface IParser
{

	public Map<String, ? extends Object> parseData(String arg);

}
