package org.simple4j.wsclient.test.requestobjecttype.vo;

public class Product
{

	private Integer productId;
	
	private String productDescription;

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public String getProductDescription()
	{
		return productDescription;
	}

	public void setProductDescription(String productDescription)
	{
		this.productDescription = productDescription;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append("[getProductId()=").append(getProductId()).append(", getProductDescription()=")
				.append(getProductDescription()).append("]");
		return builder.toString();
	}
	
}
