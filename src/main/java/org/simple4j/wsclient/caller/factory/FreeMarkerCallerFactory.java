package org.simple4j.wsclient.caller.factory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.simple4j.wsclient.caller.Caller;
import org.simple4j.wsclient.caller.ICaller;
import org.simple4j.wsclient.caller.PreTransactionCallback;
import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.formatter.IFormatter;
import org.simple4j.wsclient.formatter.impl.FreemarkerFormatter;
import org.simple4j.wsclient.http.HTTPWSClient;
import org.simple4j.wsclient.parser.IParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * This is a convenience class to load the caller configuration using FreeMarker
 * template for request url, header and body. The configuration will be in JSON/XML
 * format. This will suffice for most of the usecases. Sample code is available
 * in org.simple4j.wsclient.test.freemarkercallerfactory.CallerFactoryTest
 * and its associated configurations
 * 
 * @author jsrinivas108
 *
 */
public class FreeMarkerCallerFactory implements CallerFactory
{
	private static final String REQUEST_HEADERS = "request.headers";

	private static final String REQUEST_BODY = "request.body";

	private static final String REQUEST_URL_PATTERN = "request.urlPattern";

	private static Logger logger = LoggerFactory.getLogger(FreeMarkerCallerFactory.class);

	/**
	 * File location which contains the configuration for the creation of
	 * org.simple4j.wsclient.caller.Caller The structure of JSON/XML follows
	 * FreeMarkerCallerFactoryConfiguration If both configFile and
	 * config are configured, config will take precedence
	 */
	private File configFile = null;

	/**
	 * Configuration for the creation of org.simple4j.wsclient.caller.Caller
	 * The structure of JSON/XML follows FreeMarkerCallerFactoryConfiguration If
	 * both configFile and config are configured, config will take
	 * precedence
	 */
	private String config = null;

	/**
	 * This is an optional configuration and will be set as preTransactionCallback
	 * in the created Caller instance
	 */
	private PreTransactionCallback preTransactionCallback = null;

	/**
	 * This will be set as responseBodyParsers in the created Caller instance
	 */
	private Map<String, IParser> responseBodyParsers;

	/**
	 * This will be set as responseBodyParsers in the created Caller instance
	 */
	private HTTPWSClient httpWSClient = null;

	/**
	 * This is an optional configuration to set any finer settings to FreeMarker. If
	 * not set, will get the configuration from configFile or config using
	 * fields freemarkerVersion and freemarkerEncoding. If FreeMarker related
	 * configurations are present in jSONConfigFile or config and this
	 * configuration is also set, the settings in the configFile or config
	 * will take precedence.
	 */
	private Configuration freemarkerConfiguration = null;
	
	private ConfigParser configParser = null;
	
	private Caller caller = null;

	public File getConfigFile()
	{
		if (configFile == null || !configFile.exists() | !configFile.isFile())
		{
			if (this.config == null || this.config.trim().length() == 0)
			{
				throw new SystemException("FreeMarkerCallerFactory.configFile-invalid",
						"FreeMarkerCallerFactory.configFile is not configured properly:" + this.configFile);
			}
		}
		return configFile;
	}

	public void setConfigFile(File configFile)
	{
		this.configFile = configFile;
	}

	public String getConfig()
	{
		return config;
	}

	public void setConfig(String config)
	{
		this.config = config;
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

	public ConfigParser getConfigParser()
	{
		if(this.configParser == null)
			throw new SystemException("FreeMarkerCallerFactory.configParser-null",
					"FreeMarkerCallerFactory.configParser is not configured.");
		return configParser;
	}

	public void setConfigParser(ConfigParser configParser)
	{
		this.configParser = configParser;
	}

	public Configuration getFreemarkerConfiguration()
	{
		return freemarkerConfiguration;
	}

	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration)
	{
		this.freemarkerConfiguration = freemarkerConfiguration;
	}

	public ICaller getCaller()
	{
		if (this.caller != null)
			return this.caller;

		FreeMarkerCallerFactoryConfiguration readValue = null;
		try
		{
			if (this.getConfig() != null && this.getConfig().trim().length() > 0)
			{
				readValue = this.getConfigParser().parse(this.getConfig());
			} else
			{
				readValue = this.getConfigParser().parse(this.getFileContent(this.getConfigFile()));
			}

		} catch (IOException e)
		{
			throw new SystemException("Config-invalid", e);
		}
		Configuration configuration = this.getFreemarkerConfiguration(readValue);

		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate(REQUEST_URL_PATTERN, readValue.getRequest().getUrlPattern());
		if(readValue.getRequest().getClasspathBodyFile() != null && readValue.getRequest().getClasspathBodyFile().trim().length() > 0)
		{
			Path bodyFile = Path.of(this.getClass().getResource(readValue.getRequest().getClasspathBodyFile()).getPath());
			try
			{
				stringTemplateLoader.putTemplate(REQUEST_BODY, Files.readString(bodyFile));
			} catch (IOException e)
			{
				logger.warn("Failed to load body from file {}", bodyFile.toString(), e);
				logger.info("using body configuration");
				stringTemplateLoader.putTemplate(REQUEST_BODY, readValue.getRequest().getBody());
			}
		}
		else
		{
			stringTemplateLoader.putTemplate(REQUEST_BODY, readValue.getRequest().getBody());
		}

		Map<String, String> headerTemplates = readValue.getRequest().getHeaders();
		Set<Entry<String, String>> entrySet = headerTemplates.entrySet();
		Map<String, List<IFormatter>> requestHeaderFormatters = new HashMap<String, List<IFormatter>>();

		for (Entry<String, String> entry : entrySet)
		{
			String key = REQUEST_HEADERS + entry.getKey();
			stringTemplateLoader.putTemplate(key, entry.getValue());

			if (!requestHeaderFormatters.containsKey(entry.getKey()))
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
		requestURLFormatter.setConfiguration(configuration);
		requestURLFormatter.setOutputEncoding("UTF-8");
		requestURLFormatter.setTemplateName(REQUEST_URL_PATTERN);
		caller.setRequestURLFormatter(requestURLFormatter);

		Map<String, String> staticHeaders = readValue.getRequest().getStaticHeaders();
		Map<String, List<String>> staticHeaderValues = new HashMap<String, List<String>>();

		Set<Entry<String, String>> entrySet2 = staticHeaders.entrySet();
		for (Entry<String, String> entry : entrySet2)
		{
			String key = entry.getKey();
			if (!staticHeaderValues.containsKey(key))
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
		requestBodyFormatter.setTemplateName(REQUEST_BODY);
		caller.setRequestBodyFormatter(requestBodyFormatter);

		Map<String, String> responseBodyToCustomFieldMapping = readValue.getResponse()
				.getResponseBodyToCustomFieldMapping();

		caller.setResponseBodyToCustomFieldMapping(responseBodyToCustomFieldMapping);

		caller.setResponseBodyParsers(this.responseBodyParsers);

		caller.setPreTransactionCallback(preTransactionCallback);
		this.caller = caller;
		return this.caller;
	}

	private Configuration getFreemarkerConfiguration(FreeMarkerCallerFactoryConfiguration readValue)
	{
		if (this.getFreemarkerConfiguration() != null)
		{
			if (readValue.getFreemarkerVersion() != null)
			{
				logger.warn(
						"Freemarker configuration is set in freemarkerConfiguration and also freemarkerVersion defined in configFile:{}. Other calls using the CallerFactory may be impacted.",
						this.getConfigFile());
			}
			if (readValue.getFreemarkerEncoding() != null)
			{
				logger.warn(
						"Freemarker configuration is set in freemarkerConfiguration and also freemarkerEncoding defined in configFile:{}. Other calls using the CallerFactory may be impacted.",
						this.getConfigFile());
			}
		}
		Version incompatibleImprovements = new Version(readValue.getFreemarkerVersion());
		Configuration configuration = new Configuration(incompatibleImprovements);
		configuration.setDefaultEncoding(readValue.getFreemarkerEncoding());
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.setFreemarkerConfiguration(configuration);
		return this.getFreemarkerConfiguration();
	}

	private String getFileContent(File file) throws IOException
	{
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8))
		{
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		return contentBuilder.toString();
	}
}
