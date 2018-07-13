package org.simple4j.wsclient.test.requestobjecttype;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simple4j.wsclient.caller.Caller;
import org.simple4j.wsclient.test.requestobjecttype.vo.Order;
import org.simple4j.wsclient.test.requestobjecttype.vo.OrderItem;
import org.simple4j.wsclient.test.requestobjecttype.vo.Product;
import org.simple4j.wsclient.test.requestobjecttype.vo.SalesPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

public class RequestObjectTypeTest
{

	private static Logger logger = LoggerFactory.getLogger(RequestObjectTypeTest.class);

	private static WireMockServer wireMockServer = null;

	private static Caller simpleCaller = null;

	private static Caller complexCaller = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.info("", System.getenv());
		logger.info("", System.getProperties());

		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080)
				.usingFilesUnderClasspath("server/wiremock").extensions(new ResponseTemplateTransformer(true)));
		wireMockServer.start();

		logger.info("wiremock started");

		ApplicationContext context = new ClassPathXmlApplicationContext("client/requestobjecttype/main-AppCntxt.xml");
		simpleCaller = context.getBean("simpleCaller", Caller.class);
		complexCaller = context.getBean("complexCaller", Caller.class);
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
	public void testSimpleObject()
	{
		// TODO : generating body as "bodyparam1":"bp112,345" need to check ftl
		// documentation for integer formatting
		logger.info("inside testSimpleObject");
		Product product = new Product();
		product.setProductDescription("This is a sample test product");
		product.setProductId(12345);
		Map<String, Object> response = simpleCaller.call(product);
		logger.info("response from call", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", "rvh1" + product.getProductId(), responsevalue);
	}

	@Test
	public void testComplexObject()
	{
		// TODO : generating body as "bodyparam1":"bp112,345" need to check ftl
		// documentation for integer formatting
		logger.info("inside testComplexObject");

		SalesPerson sp = getRandomSalesPerson();

		logger.info("calling with param {}", sp);
		Map<String, Object> response = complexCaller.call(sp);
		logger.info("response from call {}", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", "rvh1" + sp.getSalesPersonId(), responsevalue);
	}

	private SalesPerson getRandomSalesPerson()
	{
		SalesPerson sp = new SalesPerson();
		sp.setSalesPersonId("sp-" + UUID.randomUUID());

		List<Order> orders = new ArrayList<Order>();
		orders.add(getRandomOrder());
		orders.add(getRandomOrder());

		sp.setOrders(orders);
		return sp;
	}

	/*
	 * { "salesPersonId":"abcd", "orders": [ { "orderDate":"", "orderId":"",
	 * "orderItems": [ { "productId":"", "productDescription":"", "orderItemId":"",
	 * "quantity":"", "status":"" } ] }, { "orderDate":"", "orderId":"",
	 * "orderItems": [ { "productId":"", "productDescription":"", "orderItemId":"",
	 * "quantity":"", "status":"" } ] } ] }
	 */
	private Order getRandomOrder()
	{
		Order ret = new Order();
		ret.setOrderDate(new Date());
		ret.setOrderId("oi" + UUID.randomUUID().toString());
		Map<Product, OrderItem> orderItems = new HashMap<Product, OrderItem>();
		orderItems.put(this.getRandomProduct(), this.getRandomOrderItem());
		orderItems.put(this.getRandomProduct(), this.getRandomOrderItem());
		ret.setOrderItems(orderItems);
		return ret;
	}

	private Product getRandomProduct()
	{
		Product ret = new Product();
		ret.setProductId(new Random().nextInt(99999));
		ret.setProductDescription("This is a sample test product:" + ret.getProductId());
		return ret;
	}

	private OrderItem getRandomOrderItem()
	{
		OrderItem ret = new OrderItem();
		ret.setOrderItemId("oii" + UUID.randomUUID());
		ret.setQuantity(new Random().nextInt(10));
		ret.setStatus("ois" + UUID.randomUUID());
		return ret;
	}

	@Test
	public void testComplexCollectionObject()
	{
		// TODO : generating body as "bodyparam1":"bp112,345" need to check ftl
		// documentation for integer formatting
		logger.info("inside testComplexCollectionObject");

		Map sp = getRandomSalesPersonMap();

		logger.info("calling with param {}", sp);
		Map<String, Object> response = complexCaller.call(sp);
		logger.info("response from call {}", response);
		String responsevalue = ((Map<String, String>) response.get("HTTP_RESPONSE_OBJECT")).get("responsevalue");
		logger.info(responsevalue);
		// HTTP_RESPONSE_OBJECT
		assertEquals("Post call response does not match", "rvh1" + sp.get("salesPersonId"), responsevalue);
	}

	private Map getRandomSalesPersonMap()
	{
		Map ret = new HashMap();
		ret.put("salesPersonId", "sp-" + UUID.randomUUID());

		List orders = new ArrayList();
		orders.add(getRandomOrderMap());
		orders.add(getRandomOrderMap());

		ret.put("orders", orders);
		return ret;
	}

	private Map getRandomOrderMap()
	{
		Map ret = new HashMap();
		ret.put("orderDate", new Date());
		ret.put("orderId", "oi" + UUID.randomUUID().toString());
		Map orderItems = new HashMap();
		orderItems.put(this.getRandomProductMap(), this.getRandomOrderItemMap());
		orderItems.put(this.getRandomProductMap(), this.getRandomOrderItemMap());
		ret.put("orderItems", orderItems);
		return ret;
	}

	private Map getRandomOrderItemMap()
	{
		Map ret = new HashMap();
		ret.put("orderItemId", "oii" + UUID.randomUUID());
		ret.put("quantity", new Random().nextInt(10));
		ret.put("status", "ois" + UUID.randomUUID());
		return ret;
	}

	private Map getRandomProductMap()
	{
		Map ret = new HashMap();
		ret.put("productId", new Random().nextInt(99999));
		ret.put("productDescription", "This is a sample test product:" + ret.get("productId"));
		return ret;
	}

}
