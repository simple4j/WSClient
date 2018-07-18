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
 * This implementation of IFormatter is to do simple find and replace
 * formatting. It first extracts key value pairs based on path navigation using
 * org.apache.commons.beanutils.PropertyUtils and applies the keys to the
 * template.
 * 
 * It has limitations on 1-n relationships and deep object hierarchy navigation.
 * 
 * @author jsrinivas108
 */
public class SimpleFormatter implements IFormatter
{

	private static Logger logger = LoggerFactory.getLogger(SimpleFormatter.class);

	/**
	 * Replacement token begin identifier string
	 */
	private String beginTokenString = "~!";

	/**
	 * Replacement token end identifier string
	 */
	private String endTokenString = "!~";

	/**
	 * Variablized template.
	 */
	private String templateString = "";

	/**
	 * Map with template variable as key and
	 * org.apache.commons.beanutils.PropertyUtils property path as value. This map
	 * will be processed before processing the template.
	 */
	private Map<String, String> inputObjectPropertyToTemplateVariableMapping;

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

	public String getTemplateString()
	{
		if (templateString == null || templateString.trim().length() == 0)
			throw new SystemException("SimpleFormatter.templateString-empty", "Please set template templateString");
		return templateString;
	}

	public void setTemplateString(String templateString)
	{
		this.templateString = templateString;
	}

	public Map<String, String> getInputObjectPropertyToTemplateVariableMapping()
	{
		if (inputObjectPropertyToTemplateVariableMapping == null
				|| inputObjectPropertyToTemplateVariableMapping.size() == 0)
			throw new SystemException("SimpleFormatter.inputObjectPropertyToTemplateVariableMapping-empty",
					"Please add a inputObjectPropertyToTemplateVariableMapping from input object to template variables");
		return inputObjectPropertyToTemplateVariableMapping;
	}

	public void setInputObjectPropertyToTemplateVariableMapping(Map<String, String> mapping)
	{
		this.inputObjectPropertyToTemplateVariableMapping = mapping;
	}

	private Map<String, String> mapInputObjectToTemplateVariables(Object arg)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> mappedObject = new HashMap<String, String>();
		for (Entry<String, String> entry : this.getInputObjectPropertyToTemplateVariableMapping().entrySet())
		{
			mappedObject.put(entry.getKey(), "" + PropertyUtils.getNestedProperty(arg, entry.getValue()));
		}
		return mappedObject;
	}

	public String formatData(Object arg) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		logger.info("beginTokenString=" + getBeginTokenString());
		logger.info("endTokenString=" + getEndTokenString());
		logger.info("templateString=" + templateString);
		logger.info("requestData=" + arg);
		String formattedString = new String(templateString);

		Map<String, String> newArg = null;
		if (this.getInputObjectPropertyToTemplateVariableMapping() != null
				&& this.getInputObjectPropertyToTemplateVariableMapping().size() > 0)
		{
			newArg = mapInputObjectToTemplateVariables(arg);

			for (Entry<String, String> ent : newArg.entrySet())
			{
				if (ent.getValue() != null && ent.getKey() != null && ent.getKey() instanceof String)
				{
					formattedString = formattedString.replace(
							this.getBeginTokenString() + ent.getKey() + this.getEndTokenString(), ent.getValue());
				}
			}
		}
		logger.info("formatted value=" + formattedString);
		return formattedString;
	}

}
