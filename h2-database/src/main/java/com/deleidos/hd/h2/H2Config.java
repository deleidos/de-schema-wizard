package com.deleidos.hd.h2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.deleidos.config.AbstractConfig;

public class H2Config extends AbstractConfig {
	public static String CONFIG_RESOURCE_NAME = "/build.properties";
	public static final String SW_CONFIG_ENV_VAR = "SW_CONFIG_PROPERTIES";
	private static final Logger logger = Logger.getLogger(H2Config.class);
	private static final String PROPERTIES_H2_DRIVER = "h2.driver";
	private static final String PROPERTIES_H2_HOST = "h2.host";
	private static final String PROPERTIES_H2_DIR = "h2.dir";
	private static final String PROPERTIES_H2_NAME = "h2.name";
	private static final String PROPERTIES_H2_USER = "h2.user";
	private static final String PROPERTIES_H2_PORTNUM = "h2.portnum";
	private static final String PROPERTIES_H2_PASSWORD = "h2.passwd";
	private static final String ENV_H2_DRIVER = "H2_DB_DRIVER";
	private static final String ENV_H2_HOST = "H2_DB_HOST";
	private static final String ENV_H2_DIR = "H2_DB_DIR";
	private static final String ENV_H2_NAME = "H2_DB_NAME";
	private static final String ENV_H2_USER = "H2_DB_USER";
	private static final String ENV_H2_PORTNUM = "H2_DB_PORTNUM";
	private static final String ENV_H2_PASSWORD = "H2_DB_PASSWD";
	private static final String ENV_H2_PORT = "H2_DB_PORT";
	private static final String DEFAULT_DRIVER = H2Database.DB_DRIVER;
	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_DIR = "~/h2";
	private static final String DEFAULT_NAME = "data";
	private static final Integer DEFAULT_PORT = 9123;
	private static final String DEFAULT_USER = "sa";
	private static final String DEFAULT_PW = "";
	private String filePath = null;
	
	public H2Config() {
		File file = new File("~" + File.separator + CONFIG_RESOURCE_NAME);
		if(file.exists()) {
			filePath = file.getAbsolutePath();
		} else {
			filePath = (System.getenv(SW_CONFIG_ENV_VAR) != null) ? System.getenv(SW_CONFIG_ENV_VAR) : null;
		}
	}
	
	public H2Config load() throws IOException {
		Properties properties = new Properties();
		if(filePath == null) {
			properties.load(getClass().getResourceAsStream(CONFIG_RESOURCE_NAME));
			logger.info("Using default resource configuration.");
		} else {
			logger.info("Grabbing H2 config from " + this.filePath + ".");
			FileInputStream fis = new FileInputStream(this.filePath);
			properties.load(fis);
			fis.close();
		}
		
		Map<String, String> propMap = new HashMap<String, String>();
		properties.forEach((k,v)->propMap.put(k.toString(), v.toString()));
		setDriver(valueOrDefault(propMap, PROPERTIES_H2_DRIVER, DEFAULT_DRIVER).toString());
		setHost(valueOrDefault(propMap, PROPERTIES_H2_HOST, DEFAULT_HOST).toString());
		setDir(valueOrDefault(propMap, PROPERTIES_H2_DIR, DEFAULT_DIR).toString());
		setName(valueOrDefault(propMap, PROPERTIES_H2_NAME, DEFAULT_NAME).toString());
		setUser(valueOrDefault(propMap, PROPERTIES_H2_USER, DEFAULT_USER).toString());
		setPortNum((Integer)valueOrDefault(propMap, PROPERTIES_H2_PORTNUM, DEFAULT_PORT));
		setPasswd(valueOrDefault(propMap, PROPERTIES_H2_PASSWORD, DEFAULT_PW).toString());
		
		loadFromEnv();
		logger.info(getConfigurationReport());
		return this;
	}
	
	/**
	 * The environment overloads any configuration file settings.
	 * @return
	 */
	public AbstractConfig loadFromEnv() {
		Map<String, String> envMap = System.getenv();
		setDriver(valueOrDefault(envMap, ENV_H2_DRIVER, getDriver()).toString());
		setHost(valueOrDefault(envMap, ENV_H2_HOST, getHost()).toString());
		setDir(valueOrDefault(envMap, ENV_H2_DIR, getDir()).toString());
		setName(valueOrDefault(envMap, ENV_H2_NAME, getName()).toString());
		setUser(valueOrDefault(envMap, ENV_H2_USER, getUser()).toString());
		setPortNum((Integer)valueOrDefault(envMap, ENV_H2_PORTNUM, getPortNum()));
		setPasswd(valueOrDefault(envMap, ENV_H2_PASSWORD, getPasswd()).toString());
		if (envMap.containsKey(ENV_H2_PORT)) {
			setTcpConnectionString(envMap.get(ENV_H2_PORT));
		}
		return this;
	}
	
	public String getConnectionString() {
		return "jdbc:h2:" + getTcpConnectionString() + getDir() + "/" + getName();
	}

	public String getDriver() {
		return configMapping.get(PROPERTIES_H2_DRIVER).toString();
	}

	public void setDriver(String driver) {
		this.configMapping.put(PROPERTIES_H2_DRIVER, driver);
	}

	public String getHost() {
		return configMapping.get(PROPERTIES_H2_HOST).toString();
	}

	public void setHost(String host) {
		this.configMapping.put(PROPERTIES_H2_HOST, host);
	}

	public String getDir() {
		return configMapping.get(PROPERTIES_H2_DIR).toString();
	}

	public void setDir(String dir) {
		this.configMapping.put(PROPERTIES_H2_DIR, dir);
	}

	public String getName() {
		return configMapping.get(PROPERTIES_H2_NAME).toString();
	}

	public void setName(String name) {
		this.configMapping.put(PROPERTIES_H2_NAME, name);
	}
	
	public String connectionString(String host, String port) {
		return "tcp://" + host + ":" + port + "/";
	}

	public String getTcpConnectionString() {
		return connectionString(getHost(), getPortNum().toString());
	}

	public void setTcpConnectionString(String tcpConnectionString) {
		String noPrefix = tcpConnectionString.substring(6);
		String noHost = noPrefix.substring(noPrefix.indexOf(":") + 1);
		if (noHost.endsWith("/")) {
			noHost = noHost.substring(0, noHost.length() - 1);
		}
		setHost(noPrefix.substring(0, noPrefix.indexOf(":")));
		setPortNum(Integer.valueOf(noHost));
	}

	public Integer getPortNum() {
		return (Integer) configMapping.get(PROPERTIES_H2_PORTNUM);
	}

	public void setPortNum(Integer port) {
		configMapping.put(PROPERTIES_H2_PORTNUM, port);
	}

	public String getUser() {
		return configMapping.get(PROPERTIES_H2_USER).toString();
	}

	public void setUser(String user) {
		this.configMapping.put(PROPERTIES_H2_USER, user);
	}

	public String getPasswd() {
		return configMapping.get(PROPERTIES_H2_PASSWORD).toString();
	}

	public void setPasswd(String passwd) {
		this.configMapping.put(PROPERTIES_H2_PASSWORD, passwd);
	}
	
	/**
	 * Unit testing configuration.  Runs in the same JVM as the test.
	 */
	public static final H2Config TEST_CONFIG = new H2Config() {
		{
			setDir("~/test-files/");
			setHost("localhost");
			setName("data");
			setPasswd("");
			setPortNum(9124);
			setTcpConnectionString("tcp://localhost:9124/");
			setUser("sa");
			setDriver(H2Database.DB_DRIVER);
		}
	};

	@Override
	public String getConfigurationName() {
		return "H2 Database Configuration";
	}
	
	
}
