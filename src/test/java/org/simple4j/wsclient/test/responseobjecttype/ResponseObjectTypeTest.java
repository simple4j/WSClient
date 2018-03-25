package org.simple4j.wsclient.test.responseobjecttype;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

public class ResponseObjectTypeTest
{

	private static Logger logger = LoggerFactory.getLogger(ResponseObjectTypeTest.class);

	private static WireMockServer wireMockServer = null;

	private static Caller getJSONCaller = null;

	private static Caller getXMLCaller = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.usingFilesUnderClasspath("server/wiremock").extensions(new ResponseTemplateTransformer(true)));
		wireMockServer.start();

		logger.info("wiremock started");

		ApplicationContext context = new ClassPathXmlApplicationContext("client/responseobjecttype/main-AppCntxt.xml");
		getJSONCaller = context.getBean("getJSONCaller", Caller.class);
		getXMLCaller = context.getBean("getXMLCaller", Caller.class);
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
	public void testGetJSON()
	{
		logger.info("inside testGetJSON");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "S-" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		Map<String, Object> response = getJSONCaller.serviceCall(reqObj);
		logger.info("response from call {}", response);
		assertResponse(reqObj, response);
	}

	@Test
	public void testGetXML()
	{
		logger.info("inside testGetXML");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "S-" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		Map<String, Object> response = getXMLCaller.serviceCall(reqObj);
		logger.info("response from call {}", response);
		assertResponse(reqObj, response);
	}

	private void assertResponse(HashMap<String, String> reqObj, Map<String, Object> response)
	{
		assertEquals("call HTTP status code does not match", "200", response.get("HTTP_STATUS_CODE"));
		Map responseObject = (Map) response.get("HTTP_RESPONSE_OBJECT");
		Map beansMap = (Map)responseObject.get("beans");
		List beanList = (List)beansMap.get("bean");
		Map beanMap = (Map)beanList.get(0);
		List<Map> propertyList = (List<Map>)beanMap.get("property");
		for(int i = 0 ; i < propertyList.size() ; i++)
		{
			if("socketTimeOutMillis".equals(propertyList.get(i).get("name")))
			{
				assertEquals("Get call response does not match", reqObj.get("testProperty1"), propertyList.get(i).get("value"));
				break;
			}
		}
	}

	@Test
	public void testGetErrorXML()
	{
		logger.info("inside testGetXML");
		HashMap<String, String> reqObj = new HashMap<String, String>();
		reqObj.put("testProperty1", "E-" + System.currentTimeMillis());
		reqObj.put("testProperty2", "param2");
		Map<String, Object> response = getXMLCaller.serviceCall(reqObj);
		logger.info("response from call {}", response);
		assertEquals("call HTTP status code does not match", "412", response.get("HTTP_STATUS_CODE"));
		
		Map responseObject = (Map) response.get("HTTP_RESPONSE_OBJECT");
		Map errorResponseMap = (Map)responseObject.get("errorResponse");
		List errorReasonCodeList = (List) errorResponseMap.get("errorReasonCode");
		assertTrue("errorReasonCode does not contain param1-empty", errorReasonCodeList.contains("param1-empty"));
		assertTrue("errorReasonCode does not contain param2-maxlength", errorReasonCodeList.contains("param2-maxlength"));
		assertEquals("errorType does not match", "VALIDATION_FAILURE", errorResponseMap.get("errorType"));
		assertEquals("description does not match", "Some fields failed validation check", errorResponseMap.get("description"));
		assertEquals("errorId does not match", reqObj.get("testProperty1"), errorResponseMap.get("errorId"));
	}

}
