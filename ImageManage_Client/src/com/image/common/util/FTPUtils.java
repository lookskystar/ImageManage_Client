package com.image.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * FTP信息资源获取类
 * @author Administrator
 *
 */
public class FTPUtils {
	
	private static ResourceBundle resource = null;
	static {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream("config/ftp.properties"));
			resource = new PropertyResourceBundle(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 获取字符串参数
     * @param key
     * @return
     */
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
	
	/**
	 * 获取boolean型参数
	 * @param key
	 * @return
	 */
	public static Boolean getResourceByKeyToBoolean(String key) {
		String value = getResourceByKey(key);
		if (value != null && value.length() > 0) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}
	
	/**
	 * 获取integer型参数
	 * @param key
	 * @return
	 */
	public static Integer getResourceByKeyToInteger(String key) {
		String value = getResourceByKey(key);
		if (value != null && value.length() > 0) {
			return Integer.parseInt(value);
		}
		return null;
	}
	
	/**
	 * 获取long型参数
	 * @param key
	 * @return
	 */
	public static Long getResourceByKeyToLong(String key) {
		Integer maxFileSize=getResourceByKeyToInteger(key);
		if (maxFileSize != null) {
			return maxFileSize * 1024 * 1024 * 1024L; //GB;
		}
		return null;
	}
	
	/**
	 * 获取字符参数
	 * @param key
	 * @return
	 */
	public static char getResourceByKeyToChar(String key){
		return (getResourceByKey(key)).charAt(0);
	}
	
	/**
	 * 是否使用文件扩展名
	 * 
	 * @return
	 */
	public static boolean useFileExtension() {
		String fileNameExtension=getResourceByKey("fileNameExtension");
		return fileNameExtension != null
				&& fileNameExtension.trim().length() > 0;
	}
	
	/**
	 * 获取文件扩展名
	 * 
	 * @return
	 */
	public static List<String> getFileNameExtension() {
		String fileNameExtension=getResourceByKey("fileNameExtension");
		String extension = fileNameExtension.trim();
		List<String> suffixList = new ArrayList<String>();

		String[] suffixs = extension.split(",");

		String suf = null;
		for (String suffix : suffixs) {
			suf = suffix.trim();
			if (suf.length() > 0) {
				suffixList.add(suf.toLowerCase());
			}
		}
		return suffixList;
	}
	
	public static String[] getFileNameExtensionArray() {
		List<String> list = getFileNameExtension();
		return list.toArray(new String[list.size()]);
	}
	
}
