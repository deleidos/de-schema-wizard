package com.deleidos.dp.environ;

import java.io.IOException;

import org.junit.Test;

import com.deleidos.dp.interpretation.IEConfig;
import com.deleidos.hd.h2.H2Config;

public class PropertiesTest {

	@Test
	public void testGetIEProperties() throws IOException {
		IEConfig ieConfig = new IEConfig();
		ieConfig.load();
		ieConfig.getIETimeout();
		ieConfig.getInterpretationMatchWeight();
		ieConfig.getMultiplier();
		ieConfig.getUrl();
	}
	
	@Test
	public void testGetH2Properties() throws IOException {
		H2Config h2Config = new H2Config();
		h2Config.load();
	}
}
