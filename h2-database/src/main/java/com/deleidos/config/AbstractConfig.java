package com.deleidos.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class AbstractConfig {
	private static final Logger logger = Logger.getLogger(AbstractConfig.class);
	protected final Map<String, Object> configMapping;
	
	public AbstractConfig() {
		configMapping = new HashMap<String, Object>();
	}
	
	public abstract String getConfigurationName();
	
	public abstract AbstractConfig load() throws IOException;
	
	public abstract AbstractConfig loadFromEnv();
	
	public String getConfigurationReport() {
		StringBuilder sb = new StringBuilder();
		sb.append(getConfigurationName() + ":");
		for (String key : configMapping.keySet()) {
			sb.append("\n\t" + key + "=" + configMapping.get(key));
		}
		return sb.toString();
	}
	
	/*protected void addToConfigMapping(Map<String, String> properties, 
			String propertyName, Object defaultValue) {
		configMapping.put(propertyName, 
				valueOrDefault(properties, propertyName, defaultValue));
	}*/
	
	protected Object valueOrDefault(Map<String, String> properties, 
			String propertyName, Object defaultValue) {
		try {
			String property = properties.getOrDefault(propertyName, defaultValue.toString());
			Object typedValue = property;
			if (defaultValue.getClass().equals(Double.class)) {
				typedValue = Double.valueOf(property);
			} else if (defaultValue.getClass().equals(Integer.class)) {
				typedValue = Integer.valueOf(property);
			} else {
				typedValue = property.toString();
			}
			return typedValue;
		} catch (Exception e) {
			logger.error("Incorrectly defined " + propertyName 
					+ ".  Using default " + defaultValue.toString(), e);
		} 
		return defaultValue;
	}
	
}
