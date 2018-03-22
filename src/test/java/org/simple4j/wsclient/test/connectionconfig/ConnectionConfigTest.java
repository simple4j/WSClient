package org.simple4j.wsclient.test.connectionconfig;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simple4j.wsclient.caller.Caller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public class ConnectionConfigTest
{

	private static Logger logger = LoggerFactory.getLogger(ConnectionConfigTest.class);

	private static WireMockServer wireMockServer = null;

	private static Caller getCaller = null;
	
	private static Caller timeoutCaller = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		ApplicationContext context = new ClassPathXmlApplicationContext("client/connectionconfig/main-AppCntxt.xml");
		getCaller = context.getBean("getCaller", Caller.class);
		timeoutCaller = context.getBean("timeoutCaller", Caller.class);

		String serverKeystorePath = context.getResource("classpath:/certs/server.jks").getFile().getAbsolutePath();
		logger.info("serverKeystorePath:{}", serverKeystorePath);
		String trustStorePath = context.getResource("classpath:/certs/truststore.jks").getFile().getAbsolutePath();
		logger.info("trustStorePath:{}", trustStorePath);
		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.httpsPort(8443)
				.keystorePassword("passwords")
				.keystorePath(serverKeystorePath)
				.trustStorePassword("passwordt")
				.trustStorePath(trustStorePath)
				.usingFilesUnderClasspath("server/wiremock").extensions(new ResponseTemplateTransformer(true)));
		wireMockServer.start();

		logger.info("wiremock started");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{

		wireMockServer.stop();
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGet()
	{
		logger.info("inside testGet");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		Map<String, Object> response = getCaller.serviceCall(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Get call response does not match", reqObj.get("testProperty1"), responsevalue);
	}

	@Test
	public void testTimeout()
	{
		logger.info("inside testTimeout");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		try
		{
			Map<String, Object> response = timeoutCaller.serviceCall(reqObj);
			logger.info("response from call", response);
			String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
			logger.info(responsevalue);
		}
		catch (RuntimeException e)
		{
			logger.warn("Expected exception for timeout", e);
		}
		WireMock.verify(2, WireMock.getRequestedFor(WireMock.urlEqualTo("/simple4j/wsmock/connectionconfig/timeout/"+reqObj.get("testProperty1"))));
	}

}
