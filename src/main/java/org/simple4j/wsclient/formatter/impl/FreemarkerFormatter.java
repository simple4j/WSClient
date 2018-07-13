package org.simple4j.wsclient.formatter.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.simple4j.wsclient.formatter.IFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * This formatter implementation takes Freemarker template configuration and formats the inputObject by applying the template.
 * 
 * @author jsrinivas108
 */
public class FreemarkerFormatter implements IFormatter
{
	private static Logger logger = LoggerFactory.getLogger(FreemarkerFormatter.class);

	/**
	 * Instance of freemarker.template.Configuration to load freemarker template.
	 */
	private Configuration configuration = null;

	/**
	 * Name of the template to load
	 */
	private String templateName = null;

	/**
	 * Character encoding of the formatted string
	 */
	private String outputEncoding = "UTF-8";

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public String getOutputEncoding()
	{
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding)
	{
		this.outputEncoding = outputEncoding;
	}

	@Override
	public String formatData(Object inputObject) throws Exception
	{

		Template temp = this.getConfiguration().getTemplate(this.getTemplateName());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(baos);
		temp.process(inputObject, out);
		return new String(baos.toByteArray(), this.getOutputEncoding());
	}

}
