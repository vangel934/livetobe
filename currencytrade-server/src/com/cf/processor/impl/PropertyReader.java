package com.cf.processor.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum PropertyReader {
	INSTANCE;

	private static final String PROP_FXFILEPATH = "fx.file.path";
	private static final String PATH = "config.properties";

	private Properties props = new Properties();

	PropertyReader() {
		try {
			loadProps();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadProps() throws IOException {
		try (InputStream io = getClass().getClassLoader().getResourceAsStream(
				PATH)) {
			if (io != null) {
				props.load(io);
			} else {
				throw new FileNotFoundException(PATH);
			}
		}
	}

	public String readFxLocation() {
		return props.getProperty(PROP_FXFILEPATH);
	}

	public String readProperty(String prop) {
		return props.getProperty(prop);
	}

	public String readProperty(String prop, String defaultValue) {
		return props.getProperty(prop, defaultValue);
	}
}
