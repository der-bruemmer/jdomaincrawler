package jdomaincrawler.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to load and store properties
 * 
 * @author didier
 * 
 */
public class PropertiesFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(PropertiesFactory.class);

	private static PropertiesFactory instance = new PropertiesFactory();
	private static Map<String, Properties> propertiesMap = null;
	private static Properties defaultProperties = null;
	private static String defaultPath;

	private PropertiesFactory() {
	}

	public static void loadProperties(final String path, final boolean def) {
		Properties properties = null;
		FileInputStream input = null;
		try {
			properties = new Properties();
			input = new FileInputStream(new File(path));
			properties.load(input);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (def) {
			defaultProperties = properties;
			defaultPath = path;
		} else {
			if (propertiesMap == null) {
				propertiesMap = new HashMap<String, Properties>();
			}
			propertiesMap.put(path, properties);
		}
		logger.debug("Properties {} loaded", path);
	}

	public static Properties getProperties() {
		return defaultProperties;
	}

	public static Properties getProperties(final String path) {
		if (path.equals(defaultPath)) {
			return defaultProperties;
		}
		if (propertiesMap == null || propertiesMap.get(path) == null) {
			loadProperties(path, false);
		}
		return propertiesMap.get(path);
	}
}
