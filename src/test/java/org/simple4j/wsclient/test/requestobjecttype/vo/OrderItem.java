package org.simple4j.wsclient.test.requestobjecttype.vo;

public class OrderItem
{

	private String orderItemId;

	private String status;

	private int quantity;

	public String getOrderItemId()
	{
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId)
	{
		this.orderItemId = orderItemId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append("[getOrderItemId()=").append(getOrderItemId()).append(", getStatus()=")
				.append(getStatus()).append(", getQuantity()=").append(getQuantity()).append("]");
		return builder.toString();
	}

}
