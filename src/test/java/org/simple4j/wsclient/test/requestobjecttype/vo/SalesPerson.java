package org.simple4j.wsclient.test.requestobjecttype.vo;

import java.util.List;

public class SalesPerson
{

	private String salesPersonId;

	private List<Order> orders;

	public String getSalesPersonId()
	{
		return salesPersonId;
	}

	public void setSalesPersonId(String salesPersonId)
	{
		this.salesPersonId = salesPersonId;
	}

	public List<Order> getOrders()
	{
		return orders;
	}

	public void setOrders(List<Order> orders)
	{
		this.orders = orders;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append("[getSalesPersonId()=").append(getSalesPersonId())
				.append(", getOrders()=").append(getOrders()).append("]");
		return builder.toString();
	}

}
