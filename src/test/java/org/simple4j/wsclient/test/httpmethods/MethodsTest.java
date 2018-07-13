package org.simple4j.wsclient.test.httpmethods;

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
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

public class MethodsTest
{

	private static Logger logger = LoggerFactory.getLogger(MethodsTest.class);

	private static WireMockServer wireMockServer = null;

	private static Caller getCaller = null;

	private static Caller postCaller = null;

	private static Caller putCaller = null;

	private static Caller deleteCaller = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.usingFilesUnderClasspath("server/wiremock").extensions(new ResponseTemplateTransformer(true)));
		wireMockServer.start();

		logger.info("wiremock started");

		ApplicationContext context = new ClassPathXmlApplicationContext("client/httpmethods/main-AppCntxt.xml");
		getCaller = context.getBean("getCaller", Caller.class);
		postCaller = context.getBean("postCaller", Caller.class);
		putCaller = context.getBean("putCaller", Caller.class);
		deleteCaller = context.getBean("deleteCaller", Caller.class);
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
		Map<String, Object> response = getCaller.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Get call response does not match", reqObj.get("testProperty1"), responsevalue);
	}

	@Test
	public void testPost()
	{
		logger.info("inside testPost");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2.0");
		Map<String, Object> response = postCaller.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("reqheadr2");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", reqObj.get("testProperty2"), responsevalue);
	}

	@Test
	public void testPut()
	{
		logger.info("inside testPut");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2.1");
		Map<String, Object> response = putCaller.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("reqheadr2");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Put call response does not match", reqObj.get("testProperty2"), responsevalue);
	}

	@Test
	public void testdelete()
	{
		logger.info("inside testdelete");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		Map<String, Object> response = deleteCaller.call(reqObj);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Delete call response does not match", reqObj.get("testProperty1"), responsevalue);
	}

}
