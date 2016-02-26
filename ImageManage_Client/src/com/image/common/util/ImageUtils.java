package com.image.common.util;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * 图片处理工具类
 *
 */
public class ImageUtils {
	
	private static Logger logger = Logger.getLogger(ImageUtils.class);
	
	/**
	 * 获取图片文件
	 */
	public static ImageIcon createImageIcon(String filename) {
		try{
			String path = "images/" + filename;
			return new ImageIcon(path); 
		}catch(Exception e){
			logger.error("加载图片"+filename+"异常："+e.toString());
			return null;
		}
	}
}
