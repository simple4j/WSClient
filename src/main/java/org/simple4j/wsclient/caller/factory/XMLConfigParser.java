package org.simple4j.wsclient.caller.factory;

import org.simple4j.wsclient.exception.SystemException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLConfigParser implements ConfigParser
{

	@Override
	public FreeMarkerCallerFactoryConfiguration parse(String configString)
	{
		XmlMapper xmlMapper = new XmlMapper();
		FreeMarkerCallerFactoryConfiguration ret;
		try
		{
			ret = xmlMapper.readValue(configString, FreeMarkerCallerFactoryConfiguration.class);
			return ret;
		} catch (JsonProcessingException e)
		{
			throw new SystemException("Config-invalid", e);
		}
	}

}
