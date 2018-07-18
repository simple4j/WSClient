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
 * This IFormatter implementation uses org.apache.velocity templateName to
 * format String.
 * 
 * @author jsrinivas108
 */
public class VelocityFormatter implements IFormatter
{

	private static Logger logger = LoggerFactory.getLogger(VelocityFormatter.class);

	/**
	 * Instance of org.apache.velocity.app.VelocityEngine to load templateName. This
	 * field is optional and default instance will load templateName from classpath
	 */
	private VelocityEngine velocityEngine;

	/**
	 * Name of the template to load
	 */
	private String templateName;

	/**
	 * Character encoding used for the formatting
	 */
	private String encoding = "UTF-8";

	/**
	 * Name of the input object field name in the template
	 */
	private String argumentKey = "argument";

	public String getTemplateName()
	{
		if (this.templateName == null || this.templateName.length() < 1)
		{
			throw new SystemException("", "VelocityFormater.template is not configured");
		} else
		{
			return templateName;
		}
	}

	public void setTemplateName(String template)
	{
		this.templateName = template;
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
			velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, this.getEncoding());
			velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, this.getEncoding());
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
		logger.info("templateName=" + this.getTemplateName());
		logger.info("requestData=" + arg);

		StringWriter writer = new StringWriter();

		Map velocityObject = new HashMap();
		velocityObject.put(this.getArgumentKey(), arg);
		logger.info("Invoking the velocity engine using templateName: " + this.getTemplateName() + ", encoding format: "
				+ this.getEncoding() + ", input data: " + velocityObject);

		Template template2 = this.getVelocityEngine().getTemplate(this.getTemplateName());
		VelocityContext context = new VelocityContext(velocityObject);
		template2.merge(context, writer);

		String velocityResponse = writer.toString();
		logger.info("Velocity engine formatted value: " + velocityResponse);
		return velocityResponse;
	}

}
