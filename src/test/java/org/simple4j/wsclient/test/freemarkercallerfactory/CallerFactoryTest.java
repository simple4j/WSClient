package org.simple4j.wsclient.test.freemarkercallerfactory;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simple4j.wsclient.caller.ICaller;
import org.simple4j.wsclient.caller.factory.FreeMarkerCallerFactory;
import org.simple4j.wsclient.caller.factory.FreeMarkerJSONCallerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class CallerFactoryTest
{

	private static Logger logger = LoggerFactory.getLogger(CallerFactoryTest.class);

	private static WireMockServer wireMockServer = null;

	private static ICaller postCaller = null;
	private static ICaller postCallerXML = null;
	private static ICaller postCallerClasspathBodyFileXML = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.usingFilesUnderClasspath("server/wiremock"));
		wireMockServer.start();

		logger.info("wiremock started");

		ApplicationContext context = new ClassPathXmlApplicationContext("client/freemarkercallerfactory/main-AppCntxt.xml");
		
		FreeMarkerJSONCallerFactory factory = context.getBean("postCallerFactory", FreeMarkerJSONCallerFactory.class);
		postCaller = factory.getCaller();
		
		FreeMarkerCallerFactory xmlFactory = context.getBean("postCallerXMLFactory", FreeMarkerCallerFactory.class);
		postCallerXML = xmlFactory.getCaller();
		
		FreeMarkerCallerFactory classpathBodyFileXMLFactory = context.getBean("postCallerclasspathBodyFileXMLFactory", FreeMarkerCallerFactory.class);
		postCallerClasspathBodyFileXML = xmlFactory.getCaller();
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
	public void testCallerFactory()
	{
		logger.info("inside testCallerFactory");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2.0");
		Map<String, Object> response = postCaller.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("reqheadr2");
		logger.info("reqheadr2:{}",responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", reqObj.get("testProperty2"), responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info("responsevalue{}"+responsevalue);
		assertEquals("Post call response does not match", "bodyparam1:bp1param2.0signatureVal1", responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("callbackkey");
		logger.info("callbackkey{}"+responsevalue);
		assertEquals("Post call response does not match", "callbackvalue", responsevalue);
		
	}

	@Test
	public void testCallerXMLFactory()
	{
		logger.info("inside testCallerXMLFactory");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2.0");
		Map<String, Object> response = postCallerXML.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("reqheadr2");
		logger.info("reqheadr2:{}",responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", reqObj.get("testProperty2"), responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info("responsevalue{}"+responsevalue);
		assertEquals("Post call response does not match", "bodyparam1:bp1param2.0signatureVal1", responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("callbackkey");
		logger.info("callbackkey{}"+responsevalue);
		assertEquals("Post call response does not match", "callbackvalue", responsevalue);
		
	}

	@Test
	public void testCallerClasspathBodyFileXMLFactory()
	{
		logger.info("inside testCallerClasspathBodyFileXMLFactory");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2.0");
		Map<String, Object> response = postCallerClasspathBodyFileXML.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("reqheadr2");
		logger.info("reqheadr2:{}",responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", reqObj.get("testProperty2"), responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info("responsevalue{}"+responsevalue);
		assertEquals("Post call response does not match", "bodyparam1:bp1param2.0signatureVal1", responsevalue);
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("callbackkey");
		logger.info("callbackkey{}"+responsevalue);
		assertEquals("Post call response does not match", "callbackvalue", responsevalue);
		
	}

}
