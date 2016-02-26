package com.image.common.handler;

import java.util.HashMap;
import java.util.Map;

import com.image.common.util.ConfigUtil;
import com.image.common.util.StringUtils;

public class PropertyHandler extends AbstractHandler {

	@Override
	public boolean dealRequest(CheckData checkData) throws Exception {
		// 设置服务器IP和端口号
		Map<String, String> keyValue = new HashMap<String, String>();
		String propertyUrl = checkData.getPropertyUrl();
		String server = ConfigUtil.getProperty("server", propertyUrl);
		String pre_url = ConfigUtil.getProperty("pre_url", propertyUrl);
		String pre_ip = ConfigUtil.getProperty("ip", propertyUrl);
		String pre_port = ConfigUtil.getProperty("port", propertyUrl);
		String ip = checkData.getIp();
		String port = checkData.getPort();
		keyValue.put("server", server);
		keyValue.put("pre_url", pre_url);
		if (StringUtils.isNotEmpty(ip) && StringUtils.isNotEmpty(port)) {
			keyValue.put("ip", ip);
			keyValue.put("port", port);
		} else if (StringUtils.isNotEmpty(ip) && !StringUtils.isNotEmpty(port)) {
			keyValue.put("ip", ip);
			keyValue.put("port", pre_port);
		} else if (!StringUtils.isNotEmpty(ip) && StringUtils.isNotEmpty(port)) {
			keyValue.put("ip", pre_ip);
			keyValue.put("port", port);
		} else {
			keyValue.put("ip", pre_ip);
			keyValue.put("port", pre_port);
		}
		ConfigUtil.setProperty((HashMap<String, String>) keyValue, propertyUrl);
		if (null != handler) {
			return this.handler.dealRequest(checkData);
		}
		return true;
	}

}
