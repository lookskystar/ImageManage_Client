package com.image.common.handler;

import javax.swing.JLabel;

import com.image.common.util.StringUtils;

public class EmptyCheckHandler extends AbstractHandler {

	@Override
	public boolean dealRequest(CheckData checkData) throws Exception {
		String userName = checkData.getUserName();
		String passWord = checkData.getPassWord();
		JLabel userNameMsg = checkData.getUserNameMsg();
		JLabel passWordMsg = checkData.getPassWordMsg();
		if (!StringUtils.isNotEmpty(userName)) {
			userNameMsg.setText("*用户名不能为空!");
			passWordMsg.setText("");
			return false;
		}
		if (!StringUtils.isNotEmpty(passWord)) {
			passWordMsg.setText("*密码不能为空!");
			userNameMsg.setText("");
			return false;
		}
		if (null != handler) {
			return this.handler.dealRequest(checkData);
		}
		return true;
	}

}
