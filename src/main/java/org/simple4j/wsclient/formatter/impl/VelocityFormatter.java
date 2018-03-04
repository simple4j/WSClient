package org.simple4j.wsclient.formatter.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.formatter.IFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jsrinivas108
 */
public class VelocityFormatter implements IFormatter
{

	private static Logger logger = LoggerFactory.getLogger(VelocityFormatter.class);
	private VelocityEngine velocityEngine;
	private String template;
	private String encoding = "UTF-8";
	private String argumentKey = "argument";

	public String getTemplate()
	{
		if (this.template == null || this.template.length() < 1)
		{
			throw new SystemException("", "VelocityFormater.template is not configured");
		} else
		{
			return template;
		}
	}

	public void setTemplate(String template)
	{
		this.template = template;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public String getArgumentKey()
	{
		return argumentKey;
	}

	public void setArgumentKey(String argumentKey)
	{
		this.argumentKey = argumentKey;
	}

	public VelocityEngine getVelocityEngine()
	{
		if (velocityEngine == null)
		{
			velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			velocityEngine.init();
		}
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine)
	{
		this.velocityEngine = velocityEngine;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String formatData(Object arg)
	{
		logger.info("template=" + this.getTemplate());
		logger.info("requestData=" + arg);

		StringWriter writer = new StringWriter();

		// TODO:change name to parameter map or something closer to
		Map velocityObject = new HashMap();
		velocityObject.put(this.getArgumentKey(), arg);
		logger.info("Invoking the velocity engine using template: " + this.getTemplate() + ", encoding format: "
				+ this.getEncoding() + ", input data: " + velocityObject);

		Template template2 = this.getVelocityEngine().getTemplate(this.getTemplate());
		VelocityContext context = new VelocityContext(velocityObject);
		template2.merge(context, writer);

		// VelocityEngineUtils.mergeTemplate(velocityEngine, this.getTemplate(),
		// this.getEncoding(), velocityObject, writer);
		String velocityResponse = writer.toString();
		logger.info("Velocity engine formatted value: " + velocityResponse);
		return velocityResponse;
	}

}
