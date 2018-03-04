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
 * @author jsrinivas108
 */
public class FreemarkerFormatter implements IFormatter
{
	private static Logger logger = LoggerFactory.getLogger(FreemarkerFormatter.class);

	private Configuration configuration = null;

	private String templateName = null;

	private String encoding = "UTF-8";

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

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	@Override
	public String formatData(Object arg) throws Exception
	{

		Template temp = this.getConfiguration().getTemplate(this.getTemplateName());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(baos);
		temp.process(arg, out);
		return new String(baos.toByteArray(), this.getEncoding());
	}

}
