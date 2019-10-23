package org.simple4j.wsclient.test.pretransactioncallback;

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

public class CallbackTest
{

	private static Logger logger = LoggerFactory.getLogger(CallbackTest.class);

	private static WireMockServer wireMockServer = null;

	private static Caller postCaller = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.usingFilesUnderClasspath("server/wiremock").extensions(new ResponseTemplateTransformer(true)));
		wireMockServer.start();

		logger.info("wiremock started");

		ApplicationContext context = new ClassPathXmlApplicationContext("client/pretransactioncallback/main-AppCntxt.xml");
		postCaller = context.getBean("postCaller", Caller.class);
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
	public void testCallback()
	{
		logger.info("inside testCallback");
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
		
		responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("callbackheaderkey");
		logger.info("callbackheaderkey{}"+responsevalue);
		assertEquals("Post call response does not match", "callbackheadervalue1", responsevalue);
	}

}
