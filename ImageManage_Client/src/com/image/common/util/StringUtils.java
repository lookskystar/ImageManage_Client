package com.image.common.util;

/**
 * 字符串工具类
 * @author hp-pc
 *
 */
public class StringUtils {
	
	/**
	 * 私有构造方法
	 */
	private StringUtils() {
		
	}
	
	/**
	 * 判断字符串是否为空
	 * @param str 被判断的字符串
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		if(null != str && !"".equals(str)){
			return true;
		}
		return false;
	}

}
