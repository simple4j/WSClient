package org.simple4j.wsclient.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * @author jsrinivas108
 */
public class ThrowableBean implements Serializable
{
	private String id = null;
	private String reasonCode = null;
	private String rootMessage = null;
	private String rootTrace = null;
	private Map rootProperties = null;

	protected ThrowableBean(String id, String reasonCode)
	{
		super();
		this.id = id;
		this.reasonCode = reasonCode;
	}

	protected String getId()
	{
		return id;
	}

	protected void setId(String id)
	{
		this.id = id;
	}

	protected String getReasonCode()
	{
		return reasonCode;
	}

	protected void setReasonCode(String reasonCode)
	{
		this.reasonCode = reasonCode;
	}

	protected void setRootMessage(String rootMessage)
	{
		this.rootMessage = rootMessage;
	}

	protected Map getRootProperties()
	{
		if (this.rootProperties == null)
			return new Properties();
		return rootProperties;
	}

	protected void setRootProperties(Map rootProperties)
	{
		this.rootProperties = rootProperties;
	}

	protected String getMessage()
	{
		String ret = "\n id=" + this.id + "\n reasonCode=" + this.reasonCode;
		if (this.rootMessage != null && this.rootMessage.trim().length() > 0)
		{
			ret = ret + "\n rootMessage=" + this.rootMessage;
		}
		if (this.getRootProperties() != null && this.getRootProperties().size() > 0)
		{
			ret = ret + "\n rootProperties=" + this.getRootProperties();
		}
		return ret;
	}

	protected void setRootTrace(String rootTrace)
	{
		this.rootTrace = rootTrace;
	}

	protected void printStackTrace(PrintStream s)
	{
		if (this.rootTrace != null)
		{
			s.println("caused by : " + this.rootTrace);
		}
	}

	protected void printStackTrace(PrintWriter s)
	{
		if (this.rootTrace != null)
		{
			s.println("caused by : " + this.rootTrace);
		}
	}

}
