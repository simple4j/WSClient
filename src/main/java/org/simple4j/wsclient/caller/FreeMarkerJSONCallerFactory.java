package org.simple4j.wsclient.caller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.formatter.IFormatter;
import org.simple4j.wsclient.formatter.impl.FreemarkerFormatter;
import org.simple4j.wsclient.http.HTTPWSClient;
import org.simple4j.wsclient.parser.IParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class FreeMarkerJSONCallerFactory
{
	private static Logger logger = LoggerFactory.getLogger(FreeMarkerJSONCallerFactory.class);
	
	private File jSONConfigFile = null;
	private Caller caller = null;
	private PreTransactionCallback preTransactionCallback = null;
	private Map<String, IParser> responseBodyParsers;
	private HTTPWSClient httpWSClient = null;
	private Configuration freemarkerConfiguration = null;

	public File getJSONConfigFile()
	{
		if(jSONConfigFile == null || !jSONConfigFile.exists() | !jSONConfigFile.isFile())
		{
			throw new SystemException("FreeMarkerJSONCallerFactory.jSONConfigFile-invalid", "FreeMarkerJSONCallerFactory.jSONConfigFile is not configured properly:"+this.jSONConfigFile);
		}
		return jSONConfigFile;
	}

	public void setJSONConfigFile(File jSONConfigFile)
	{
		this.jSONConfigFile = jSONConfigFile;
	}

	public PreTransactionCallback getPreTransactionCallback()
	{
		return preTransactionCallback;
	}

	public void setPreTransactionCallback(PreTransactionCallback preTransactionCallback)
	{
		this.preTransactionCallback = preTransactionCallback;
	}

	public Map<String, IParser> getResponseBodyParsers()
	{
		return responseBodyParsers;
	}

	public void setResponseBodyParsers(Map<String, IParser> responseBodyParsers)
	{
		this.responseBodyParsers = responseBodyParsers;
	}

	public HTTPWSClient getHttpWSClient()
	{
		return httpWSClient;
	}

	public void setHttpWSClient(HTTPWSClient httpWSClient)
	{
		this.httpWSClient = httpWSClient;
	}

	public Configuration getFreemarkerConfiguration()
	{
		return freemarkerConfiguration;
	}

	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration)
	{
		this.freemarkerConfiguration = freemarkerConfiguration;
	}

	public Caller getCaller() throws IOException
	{
		if(this.caller != null)
			return this.caller;
				
		ObjectMapper jsonMapper = new ObjectMapper();
		FreeMarkerJSONCallerFactoryConfiguration readValue = jsonMapper.readValue(this.getFileContent(this.getJSONConfigFile()), FreeMarkerJSONCallerFactoryConfiguration.class);
		
		Configuration configuration = this.getFreemarkerConfiguration(readValue);
		
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate("request.urlPattern", readValue.getRequest().getUrlPattern());
		stringTemplateLoader.putTemplate("request.body", readValue.getRequest().getBody());

		Map<String,String> headerTemplates = readValue.getRequest().getHeaders();
		Set<Entry<String, String>> entrySet = headerTemplates.entrySet();
		Map<String, List<IFormatter>> requestHeaderFormatters = new HashMap<String, List<IFormatter>>();

		for (Entry<String, String> entry : entrySet)
		{
			String key = "request.headers" + entry.getKey();
			stringTemplateLoader.putTemplate(key, entry.getValue());
			
			if(!requestHeaderFormatters.containsKey(entry.getKey()))
			{
				requestHeaderFormatters.put(entry.getKey(), new ArrayList<IFormatter>());
			}
			FreemarkerFormatter freemarkerFormatter = new FreemarkerFormatter();
			freemarkerFormatter.setConfiguration(configuration);
			freemarkerFormatter.setOutputEncoding("UTF-8");
			freemarkerFormatter.setTemplateName(key);
			requestHeaderFormatters.get(entry.getKey()).add(freemarkerFormatter);
		}
		
		configuration.setTemplateLoader(stringTemplateLoader);
		
		Caller caller = new Caller();
		caller.setHttpWSClient(this.getHttpWSClient());
		caller.setServiceMethod(readValue.getRequest().getMethod());
		
		FreemarkerFormatter requestURLFormatter = new FreemarkerFormatter();
		requestURLFormatter.setConfiguration(configuration );
		requestURLFormatter.setOutputEncoding("UTF-8");
		requestURLFormatter.setTemplateName("request.urlPattern");
		caller.setRequestURLFormatter(requestURLFormatter);

		Map<String,String> staticHeaders = readValue.getRequest().getStaticHeaders();
		Map<String, List<String>> staticHeaderValues = new HashMap<String, List<String>>();
		
		Set<Entry<String, String>> entrySet2 = staticHeaders.entrySet();
		for (Entry<String, String> entry : entrySet2)
		{
			String key = entry.getKey();
			if(!staticHeaderValues.containsKey(key))
			{
				staticHeaderValues.put(key, new ArrayList<String>());
			}
			staticHeaderValues.get(key).add(entry.getValue());
		}
		caller.setStaticHeaderValues(staticHeaderValues);
		caller.setRequestHeaderFormatters(requestHeaderFormatters);

		FreemarkerFormatter requestBodyFormatter = new FreemarkerFormatter();
		requestBodyFormatter.setConfiguration(configuration);
		requestBodyFormatter.setOutputEncoding("UTF-8");
		requestBodyFormatter.setTemplateName("request.body");
		caller.setRequestBodyFormatter(requestBodyFormatter);
		
		Map<String, String> responseBodyToCustomFieldMapping = readValue.getResponse().getResponseBodyToCustomFieldMapping();
		
		caller.setResponseBodyToCustomFieldMapping(responseBodyToCustomFieldMapping);
		
		caller.setResponseBodyParsers(this.responseBodyParsers);

		caller.setPreTransactionCallback(preTransactionCallback);
		this.caller = caller;
		return this.caller;
		
	}

	private Configuration getFreemarkerConfiguration(FreeMarkerJSONCallerFactoryConfiguration readValue)
	{
		if(this.getFreemarkerConfiguration() != null)
		{
			if(readValue.getFreemarkerVersion() != null)
			{
				logger.warn("Freemarker configuration is set in freemarkerConfiguration and also freemarkerVersion defined in jSONConfigFile:{}. Other calls using the CallerFactory may be impacted.", this.getJSONConfigFile());
			}
			if(readValue.getFreemarkerEncoding() != null)
			{
				logger.warn("Freemarker configuration is set in freemarkerConfiguration and also freemarkerEncoding defined in jSONConfigFile:{}. Other calls using the CallerFactory may be impacted.", this.getJSONConfigFile());
			}
		}
		Version incompatibleImprovements = new Version(readValue.getFreemarkerVersion());
		Configuration configuration = new Configuration(incompatibleImprovements );
		configuration.setDefaultEncoding(readValue.getFreemarkerEncoding());
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.setFreemarkerConfiguration(configuration);
		return this.getFreemarkerConfiguration();
	}

	private String getFileContent(File file) throws IOException
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8))
	    {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    return contentBuilder.toString();
	}
}
