package org.simple4j.wsclient.exception;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author jsrinivas108
 */
public class ThrowableBeanFactory
{
	private static String host = null;

	public static ThrowableBean getThrowableBean(String reasonCode, Throwable rootCause)
	{
		init();
		ThrowableBean ret = new ThrowableBean(System.currentTimeMillis() + "@@" + host, reasonCode);
		if (rootCause != null)
		{
			ret.setRootMessage(rootCause.getMessage());
			ret.setRootTrace(getTrace(rootCause));
			ret.setRootProperties(getProperties(rootCause));
		}
		return ret;
	}

	private static Map getProperties(Throwable t)
	{
		return getProperties(t, new LinkedHashMap());
	}

	private static Map getProperties(Throwable t, Map ret)
	{
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(t);
		for (int i = 0; i < descriptors.length; i++)
		{
			if (!descriptors[i].getName().equalsIgnoreCase("cause")
					&& !descriptors[i].getName().equalsIgnoreCase("message")
					&& !descriptors[i].getName().equalsIgnoreCase("stackTrace"))
			{
				try
				{
					if (descriptors[i].getName().equalsIgnoreCase("localizedMessage"))
					{
						ret.put(descriptors[i].getName(), "[" + descriptors[i].getReadMethod().invoke(t, null) + "]");
					} else
					{
						if (descriptors[i].getName().equalsIgnoreCase("class"))
						{
							ret.put(descriptors[i].getName(),
									"" + ((Class) descriptors[i].getReadMethod().invoke(t, null)).getName());
						} else
						{
							ret.put(descriptors[i].getName(), "" + descriptors[i].getReadMethod().invoke(t, null));
						}
					}
				} catch (Throwable e)
				{
					// ignore if getter fails
				}
			}
		}
		Throwable cause = t.getCause();
		if (cause != null)
		{
			ret.put("cause", getProperties(cause, new LinkedHashMap()));
		}
		return Collections.unmodifiableMap(ret);
	}

	private static String getTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	private static void init()
	{
		if (host == null)
		{
			try
			{
				InetAddress hostAddress = InetAddress.getLocalHost();
				host = hostAddress.toString();
			} catch (Throwable e)
			{
				host = "unknown";
			}
		}
	}
}
