package org.simple4j.wsclient.caller.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience class to load the caller configuration using FreeMarker
 * template for request url, header and body. The configuration will be in JSON
 * format. This will suffice for most of the usecases. Sample code is available
 * in org.simple4j.wsclient.test.freemarkercallerfactory.CallerFactoryTest
 * and its associated configurations
 * 
 * @author jsrinivas108
 * @deprecated - replace this class with FreeMarkerCallerFactory along with JSONConfigParser
 *
 */
public class FreeMarkerJSONCallerFactory extends FreeMarkerCallerFactory
{
	private static Logger logger = LoggerFactory.getLogger(FreeMarkerJSONCallerFactory.class);

	public FreeMarkerJSONCallerFactory()
	{
		this.setConfigParser(new JSONConfigParser());
	}
}
