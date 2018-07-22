/**
 * Defines the exception and related classes.
 */
package org.simple4j.wsclient.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author jsrinivas108
 */
public class SystemException extends RuntimeException
{

	private ThrowableBean delegate = null;

	public SystemException(String reasonCode)
	{
		super();
		delegate = ThrowableBeanFactory.getThrowableBean(reasonCode, null);
	}

	public SystemException(String reasonCode, String message)
	{
		super(message);
		delegate = ThrowableBeanFactory.getThrowableBean(reasonCode, null);
	}

	public SystemException(String reasonCode, String message, Throwable rootCause)
	{
		super(message);
		delegate = ThrowableBeanFactory.getThrowableBean(reasonCode, rootCause);
	}

	public SystemException(String reasonCode, Throwable rootCause)
	{
		super();
		delegate = ThrowableBeanFactory.getThrowableBean(reasonCode, rootCause);
	}

	public String getId()
	{
		return delegate.getId();
	}

	public String getMessage()
	{
		return super.getMessage() + delegate.getMessage();
	}

	public String getReasonCode()
	{
		return delegate.getReasonCode();
	}

	public Map getRootProperties()
	{
		return delegate.getRootProperties();
	}

	public void printStackTrace(PrintStream s)
	{
		super.printStackTrace(s);
		delegate.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s)
	{
		super.printStackTrace(s);
		delegate.printStackTrace(s);
	}

}
