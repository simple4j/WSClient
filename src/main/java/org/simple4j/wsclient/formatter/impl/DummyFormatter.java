package org.simple4j.wsclient.formatter.impl;

import org.simple4j.wsclient.formatter.IFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jsrinivas108
 */
public class DummyFormatter implements IFormatter
{

	private static Logger logger = LoggerFactory.getLogger(DummyFormatter.class);
	private String value = "";

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String formatData(Object arg)
	{
		return this.getValue();
	}

}
