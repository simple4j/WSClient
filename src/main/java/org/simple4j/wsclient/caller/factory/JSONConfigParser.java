package org.simple4j.wsclient.caller.factory;

import org.simple4j.wsclient.exception.SystemException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONConfigParser implements ConfigParser
{

	@Override
	public FreeMarkerCallerFactoryConfiguration parse(String configString)
	{
		ObjectMapper jsonMapper = new ObjectMapper();
		FreeMarkerCallerFactoryConfiguration ret;
		try
		{
			ret = jsonMapper.readValue(configString, FreeMarkerCallerFactoryConfiguration.class);
			return ret;
		} catch (JsonProcessingException e)
		{
			throw new SystemException("Config-invalid", e);
		}
	}

}
