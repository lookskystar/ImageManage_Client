package com.image.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigUtil {

	private static Logger log = Logger.getLogger(ConfigUtil.class);

	public static String getProperty(String name, String fileURL) {
		Properties prop = new Properties();
		FileInputStream fis = null;
		try {
			try {
				fis = new FileInputStream(fileURL);
				prop.load(fis);
			} finally {
				fis.close();
			}
			return prop.getProperty(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setProperty(HashMap<String, String> keyValue, String fileURL) {
		Properties prop = new Properties();
		FileOutputStream fos = null;
		try {
			try {
				fos = new FileOutputStream(fileURL);
				for (Iterator<String> iterator = keyValue.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = keyValue.get(key);
					prop.setProperty(key, value);
				}
				prop.store(fos, "Config setUp!");
			} finally {
				fos.close();
			}
		} catch (IOException e) {
			log.error("修改配置文件失败：" + e.getMessage());
		} 
	}
}
