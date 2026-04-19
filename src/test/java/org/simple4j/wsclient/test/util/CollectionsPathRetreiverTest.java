package org.simple4j.wsclient.test.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.simple4j.wsclient.exception.SystemException;
import org.simple4j.wsclient.util.CollectionsPathRetreiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionsPathRetreiverTest
{
	private static Logger logger = LoggerFactory.getLogger(CollectionsPathRetreiverTest.class);

	@Test
	public void testGetJSON()
	{
		Map randomSalesPersonMap = getRandomSalesPersonMap();
		logger.info("{}",randomSalesPersonMap);
		CollectionsPathRetreiver cpr = new CollectionsPathRetreiver();
		// TODO: verified manually but will have to switch to assertions.
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "salesPersonId"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "salesPersonId1"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0]"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderDate"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderId"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[*]"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderItems"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[*].orderItems"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderItems.KEYS"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderItems.KEYS[LENGTH]"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderItems.KEYS[0]"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[0].orderItems.KEYS[1].productId"));
		logger.info("{}",cpr.getNestedProperty(randomSalesPersonMap, "orders[LENGTH]"));

		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		randomSalesPersonMap = getRandomSalesPersonMap();
		
		logger.info("Starting value {}",randomSalesPersonMap);
		
		String removalPath = "orders[0].orderItems.KEYS[1].productDescription";
		
		this.delete(randomSalesPersonMap, removalPath);
		logger.info("After removing productDescription {}",randomSalesPersonMap);
		
		removalPath = "orders[1]";
		this.delete(randomSalesPersonMap, removalPath);
		logger.info("After removing second order {}",randomSalesPersonMap);
		
		removalPath = "orders[*]";
		this.delete(randomSalesPersonMap, removalPath);
		logger.info("After removing all orders {}",randomSalesPersonMap);
		
	}

	private void delete(Map randomSalesPersonMap, String removalPath)
	{
		CollectionsPathRetreiver cpr = new CollectionsPathRetreiver();
		if(removalPath.endsWith("]"))
		{
			int lastIndexOfBeginSquarBracket = removalPath.lastIndexOf("[");
			String parentPath = removalPath.substring(0, lastIndexOfBeginSquarBracket);
			String removalIndexStr = removalPath.substring(lastIndexOfBeginSquarBracket+1, removalPath.length()-1);
			logger.info("{},{}", parentPath, removalIndexStr);
			Integer removalIndex = -1;
			if(!removalIndexStr.equals("*"))
			{
				try
				{
					removalIndex = Integer.parseInt(removalIndexStr);
				}
				catch(NumberFormatException e)
				{
					throw new SystemException("REMOVALINDEX-NOTANUMBER", e);
				}
			}
			List parentLevelValues = cpr.getNestedProperty(randomSalesPersonMap, parentPath);
			logger.info("parentLevelValues: {}", parentLevelValues);

			if(removalIndex >=0 )
			{
				parentLevelValues.remove(removalIndex);
			}
			else
			{
				parentLevelValues.clear();
				for(int i=0 ; i < parentLevelValues.size() ; i++)
				{
					logger.info("parentLevelValues.get(i): {}", parentLevelValues.get(i));
				}
			}
		}
		else
		{
			int lastIndexOfDot = removalPath.lastIndexOf(".");
			String parentPath = removalPath.substring(0, lastIndexOfDot);
			String removalPropertyName = removalPath.substring(lastIndexOfDot+1);
			List parentLevelValues = cpr.getNestedProperty(randomSalesPersonMap, parentPath);
			for(int i=0 ; i < parentLevelValues.size() ; i++)
			{
				if(parentLevelValues.get(i) instanceof Map)
				{
					((Map)parentLevelValues.get(i)).remove(removalPropertyName);
				}
				else
					throw new SystemException("PARENTVALUE-NOTAMAP", "Parent level value is not a Map object");
			}
		}
		
	}

	private static Map getRandomSalesPersonMap()
	{
		Map ret = new HashMap();
		ret.put("salesPersonId", "sp-" + UUID.randomUUID());

		List orders = new ArrayList();
		orders.add(getRandomOrderMap());
		orders.add(getRandomOrderMap());

		ret.put("orders", orders);
		return ret;
	}

	private static Map getRandomOrderMap()
	{
		Map ret = new HashMap();
		ret.put("orderDate", new Date());
		ret.put("orderId", "oi" + UUID.randomUUID().toString());
		Map orderItems = new HashMap();
		orderItems.put(getRandomProductMap(), getRandomOrderItemMap());
		orderItems.put(getRandomProductMap(), getRandomOrderItemMap());
		ret.put("orderItems", orderItems);
		return ret;
	}

	private static Map getRandomOrderItemMap()
	{
		Map ret = new HashMap();
		ret.put("orderItemId", "oii" + UUID.randomUUID());
		ret.put("quantity", new Random().nextInt(10));
		ret.put("status", "ois" + UUID.randomUUID());
		return ret;
	}

	private static Map getRandomProductMap()
	{
		Map ret = new HashMap();
		ret.put("productId", new Random().nextInt(99999));
		ret.put("productDescription", "This is a sample test product:" + ret.get("productId"));
		return ret;
	}

}
