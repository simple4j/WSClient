package org.simple4j.wsclient.formatter.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.formatter.IFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jsrinivas108
 */
public class SimpleFormatter implements IFormatter
{

	private static Logger logger = LoggerFactory.getLogger(SimpleFormatter.class);
	private String beginTokenString = "~!";
	private String endTokenString = "!~";
	private String pattern = "";
	private Map<String, String> mapping;

	public String getBeginTokenString()
	{
		return beginTokenString;
	}

	public void setBeginTokenString(String beginTokenString)
	{
		this.beginTokenString = beginTokenString;
	}

	public String getEndTokenString()
	{
		return endTokenString;
	}

	public void setEndTokenString(String endTokenString)
	{
		this.endTokenString = endTokenString;
	}

	public String getPattern()
	{
		if (pattern == null || pattern.trim().length() == 0)
			throw new SystemException("SimpleFormatter.pattern-empty", "Please set template pattern");
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public Map<String, String> getMapping()
	{
		if (mapping == null || mapping.size() == 0)
			throw new SystemException("SimpleFormatter.mapping-empty",
					"Please add a mapping from input object to template variables");
		return mapping;
	}

	public void setMapping(Map<String, String> mapping)
	{
		this.mapping = mapping;
	}

	private Map<String, String> implementMappingOnObject(Object arg)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> mappedObject = new HashMap<String, String>();
		for (Entry<String, String> entry : this.getMapping().entrySet())
		{
			mappedObject.put(entry.getKey(), "" + PropertyUtils.getNestedProperty(arg, entry.getValue()));
		}
		return mappedObject;
	}

	public String formatData(Object arg) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		logger.info("beginTokenString=" + getBeginTokenString());
		logger.info("endTokenString=" + getEndTokenString());
		logger.info("pattern=" + pattern);
		logger.info("requestData=" + arg);
		String formattedPattern = new String(pattern);

		Map<String, String> newArg = null;
		if (this.getMapping() != null && this.getMapping().size() > 0)
		{
			newArg = implementMappingOnObject(arg);

			for (Entry<String, String> ent : newArg.entrySet())
			{
				if (ent.getValue() != null && ent.getKey() != null && ent.getKey() instanceof String)
				{
					formattedPattern = formattedPattern.replace(
							this.getBeginTokenString() + ent.getKey() + this.getEndTokenString(), ent.getValue());
				}
			}
		}
		logger.info("formatted value=" + formattedPattern);
		return formattedPattern;
	}

}
