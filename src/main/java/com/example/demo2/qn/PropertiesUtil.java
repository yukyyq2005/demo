package com.example.demo2.qn;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static Properties prop = null;

	static {
		try {
			String classPath = System.getProperty("user.dir")
//					+ "/conf.properties";
					+ "/conf/rabbit.properties";

			System.out.print("classPath:"+classPath);
			prop = new Properties();
			prop.load(new FileInputStream(new File(classPath)));
		} catch (Exception e) {
			try {
				String classPath = PropertiesUtil.class.getClassLoader()
						.getResource("").getPath().replaceAll("%20", " ")
//						+ "/conf.properties";
						+ "conf/rabbit.properties";
				prop = new Properties();
				prop.load(new FileInputStream(new File(classPath)));
			} catch (Exception e1) {
				try {
					InputStream in = PropertiesUtil.class.getClassLoader()
							.getResourceAsStream("conf.properties");
					prop = new Properties();
					prop.load(in);
				} catch (Exception e2) {
					logger.error("load properties exception.",e2);
				}
			}
		}
	}

	public static String getValue(String key) {
		String value = "";
		if (key != null) {
			value = prop.getProperty(key);
		}
		return value;
	}

	public static int getIntValue(String key) {
		String value = "0";
		if (key != null) {
			value = prop.getProperty(key);
		}
		return Integer.parseInt(value);
	}

}
