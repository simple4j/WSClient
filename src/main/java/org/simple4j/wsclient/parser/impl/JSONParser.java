package org.simple4j.wsclient.parser.impl;

import java.io.IOException;
import java.util.Map;

import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.parser.IParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jsrinivas108
 */
public class JSONParser implements IParser
{

	private static Logger logger = LoggerFactory.getLogger(JSONParser.class);

	public Map<String, ? extends Object> parseData(String arg)
	{
		ObjectMapper jsonMapper = new ObjectMapper();
		try
		{
			return jsonMapper.readValue(arg, Map.class);
		} catch (IOException e)
		{
			throw new SystemException("JSON_PARSE_FAILED", e);
		}
	}

}
