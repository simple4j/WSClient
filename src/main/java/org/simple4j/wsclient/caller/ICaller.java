package org.simple4j.wsclient.caller;

import java.util.Map;

public interface ICaller
{
	public Map<String, Object> call(Object requestObject);
}
