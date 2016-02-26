package com.image.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.caucho.hessian.client.HessianProxyFactory;

public class HessianServiceFactory {

	private static HessianProxyFactory factory = new HessianProxyFactory();;

	/** 单例 */
	private HessianServiceFactory() {

	}

	private static class SingletonHolder {
		public static final HessianServiceFactory HessianServiceFactory = new HessianServiceFactory();
	}

	public static HessianServiceFactory getInstance() {
		return SingletonHolder.HessianServiceFactory;
	}

	/**
	 * 创建服务接口
	 * 
	 * @param <T>
	 * 
	 * @param serviceType
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public <T> Object createService(Class<T> serviceType, String serviceName) throws Exception {
		InputStream in = null;
		ResourceBundle resource = null;
		String url = "";
		try {
			try {
				in = new BufferedInputStream(new FileInputStream("config/remote.properties"));
				resource = new PropertyResourceBundle(in);
				String ip = resource.getString("ip").equals("") ? "" : resource.getString("ip");
				String port = resource.getString("port").equals("") ? "" : resource.getString("port");
				String server = resource.getString("server").equals("") ? "" : resource.getString("server");
				String preUrl = resource.getString("pre_url").equals("") ? "" : resource.getString("pre_url");
				url = "http://" + ip + ":" + port + "/" + server + "/" + preUrl + "/" + serviceName;
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return factory.create(serviceType, url);
	}
}
