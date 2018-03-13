package org.simple4j.wsclient.test.requestobjecttype.vo;

import java.util.Date;
import java.util.Map;

public class Order
{

	private String orderId;
	
	//TODO: need to check data time formatters in freemarker and velocity
	private Date orderDate;
	
	private Map<Product, OrderItem> orderItems;

	public String getOrderId()
	{
		return orderId;
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	public Date getOrderDate()
	{
		return orderDate;
	}

	public void setOrderDate(Date orderDate)
	{
		this.orderDate = orderDate;
	}

	public Map<Product, OrderItem> getOrderItems()
	{
		return orderItems;
	}

	public void setOrderItems(Map<Product, OrderItem> orderItems)
	{
		this.orderItems = orderItems;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append(" [getOrderId()=").append(getOrderId()).append(", getOrderDate()=")
				.append(getOrderDate()).append(", getOrderItems()=").append(getOrderItems()).append("]");
		return builder.toString();
	}
	
}
