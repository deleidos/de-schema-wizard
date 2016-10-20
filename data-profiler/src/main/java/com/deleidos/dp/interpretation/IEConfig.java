package com.deleidos.dp.interpretation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.deleidos.config.AbstractConfig;
import com.deleidos.dp.deserializors.SerializationUtility;

/**
 * Configuration class for Interpretation Engine
 * @author leegc
 *
 */
public class IEConfig extends AbstractConfig {
	private static final Logger logger = Logger.getLogger(IEConfig.class);
	public static String CONFIG_RESOURCE_NAME = "/build.properties";
	public static final String SW_CONFIG_PROPERTIES = "SW_CONFIG_PROPERTIES";
	public static final String BUILT_IN_OVERRIDE = "default";
	private static final String PROPERTIES_IE_URL = "ie.url";
	private static final String ENV_IE_PORT = "SW_IE_PORT";
	private static final String PROPERTIES_IE_TIMEOUT = "ie.timeout";
	private static final String ENV_IE_TIMEOUT = "IE_TIMEOUT";
	private static final String PROPERTIES_IE_TIMEOUT_MULTIPLIER = "ie.timeout.multiplier";
	private static final String ENV_IE_TIMEOUT_MULTIPLIER = "IE_TIMEOUT_MULTIPLIER";
	private static final String PROPERTIES_IE_MATCH_WEIGHT = "ie.match.weight";
	private static final String ENV_IE_MATCH_WEIGHT = "IE_MATCH_WEIGHT";
	private static final String PROPERTIES_IE_MAX_GEO_CALLS = "ie.max.geo.calls";
	private static final String ENV_IE_MAX_GEO_CALLS = "IE_MAX_GEO_CALLS";
	private static final Integer DEFAULT_CONNECTION_TIMEOUT = 15000;
	private static final Double DEFAULT_ESTIMATE_MULTIPLIER = 5.0;
	private static final Double DEFAULT_INTERPRETATION_MATCH_WEIGHT = .8;
	private static final String DEFAULT_URL = "http://localhost:5000";
	private static final Integer DEFAULT_MAX_GEO_CALLS = 5000;
	private boolean fakeGeocode = false;
	private String filePath = null;

	public static IEConfig dynamicConfig(String url) {
		IEConfig dynamic = new IEConfig();
		dynamic.setUrl(url);
		return dynamic;
	}

	public IEConfig() {
		File file = new File("~" + File.separator + CONFIG_RESOURCE_NAME);
		if(file.exists()) {
			filePath = file.getAbsolutePath();
		} else {
			filePath = (System.getenv(SW_CONFIG_PROPERTIES) != null) ? System.getenv(SW_CONFIG_PROPERTIES) : null;
		}
	}

	public IEConfig load() throws IOException {
		Properties properties = new Properties();
		if(filePath == null) {
			properties.load(getClass().getResourceAsStream(CONFIG_RESOURCE_NAME));
		} else {
			logger.info("Grabbing IE config from " + this.filePath + ".");
			FileInputStream fis = new FileInputStream(this.filePath);
			properties.load(fis);
			fis.close();
		}
			
		Map<String, String> propMap = new HashMap<String, String>();
		properties.forEach((k,v)->propMap.put(k.toString(), v.toString()));
		setIETimeout((Integer)valueOrDefault(
				propMap, PROPERTIES_IE_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT));
		setMultiplier((Double)valueOrDefault(
				propMap, PROPERTIES_IE_TIMEOUT_MULTIPLIER, DEFAULT_ESTIMATE_MULTIPLIER));
		setUrl(valueOrDefault(
				propMap, PROPERTIES_IE_URL, DEFAULT_URL).toString());
		setInterpretationMatchWeight((Double)valueOrDefault(
				propMap, PROPERTIES_IE_MATCH_WEIGHT, DEFAULT_INTERPRETATION_MATCH_WEIGHT));
		setMaxGeoCalls((Integer)valueOrDefault(
				propMap, PROPERTIES_IE_MAX_GEO_CALLS, DEFAULT_MAX_GEO_CALLS));
		loadFromEnv();
		
		logger.info(getConfigurationReport());
		return this;
	}

	public AbstractConfig loadFromEnv() {
		Map<String, String> envMap = System.getenv();
		setIETimeout((Integer)valueOrDefault(envMap, ENV_IE_TIMEOUT, getIETimeout()));
		setMultiplier((Double)valueOrDefault(envMap, ENV_IE_TIMEOUT_MULTIPLIER, getMultiplier()));
		setUrl(valueOrDefault(envMap, ENV_IE_PORT, getUrl()).toString());
		setInterpretationMatchWeight((Double)valueOrDefault(envMap, ENV_IE_MATCH_WEIGHT, getInterpretationMatchWeight()));
		setMaxGeoCalls((Integer)valueOrDefault(envMap, ENV_IE_MAX_GEO_CALLS, getMaxGeoCalls()));
		return this;
	}

	public boolean useBuiltin() {
		return getUrl() == null || getUrl().equals(BUILT_IN_OVERRIDE);
	}

	public String getUrl() {
		return configMapping.get(PROPERTIES_IE_URL).toString();
	}

	public void setUrl(String url) {
		if(url != null && url.contains("tcp")) {
			url = url.replaceFirst("tcp", "http");
		}
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		this.configMapping.put(PROPERTIES_IE_URL, url);
	}

	public boolean isFakeGeocode() {
		return fakeGeocode;
	}

	public void setFakeGeocode(boolean fakeGeocode) {
		this.fakeGeocode = fakeGeocode;
	}

	public int getIETimeout() {
		return (Integer)configMapping.get(PROPERTIES_IE_TIMEOUT);
	}

	public void setIETimeout(int readTimeout) {
		this.configMapping.put(PROPERTIES_IE_TIMEOUT, readTimeout);
	}

	public double getMultiplier() {
		return (Double)configMapping.get(PROPERTIES_IE_TIMEOUT_MULTIPLIER);
	}

	public void setMultiplier(double multiplier) {
		this.configMapping.put(PROPERTIES_IE_TIMEOUT_MULTIPLIER, multiplier);
	}

	public double getInterpretationMatchWeight() {
		return (Double)configMapping.get(PROPERTIES_IE_MATCH_WEIGHT);
	}

	public void setInterpretationMatchWeight(double interpretationMatchWeight) {
		this.configMapping.put(PROPERTIES_IE_MATCH_WEIGHT, interpretationMatchWeight);
	}
	
	public Integer getMaxGeoCalls() {
		return (Integer)configMapping.get(PROPERTIES_IE_MAX_GEO_CALLS);
	}
	
	public void setMaxGeoCalls(Integer maxGeoCalls) {
		configMapping.put(PROPERTIES_IE_MAX_GEO_CALLS, maxGeoCalls);
	}

	public static IEConfig BUILTIN_CONFIG = new IEConfig() {
		{
			setUrl("default");
			setIETimeout(DEFAULT_CONNECTION_TIMEOUT);
			setInterpretationMatchWeight(DEFAULT_INTERPRETATION_MATCH_WEIGHT);
			setMultiplier(DEFAULT_ESTIMATE_MULTIPLIER);
			setMaxGeoCalls(DEFAULT_MAX_GEO_CALLS);
			setFakeGeocode(true);
		}
	};

	@Override
	public String getConfigurationName() {
		return "Interpretation Engine Configuration";
	}

}
