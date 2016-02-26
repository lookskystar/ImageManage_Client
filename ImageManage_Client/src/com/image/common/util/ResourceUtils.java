package com.image.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 资源文件获取类
 * @author Administrator
 *
 */
public class ResourceUtils {
	
	private static ResourceBundle resource = null;
	static {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream("config/image.properties"));
			resource = new PropertyResourceBundle(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String getResourceByKey(String key) {
		if(key==null || "".equals(key)){
			return "";
		}
		try {
			return resource.getString(key);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static char getResourceByKeyToChar(String key){
		return (getResourceByKey(key)).charAt(0);
	}
}
