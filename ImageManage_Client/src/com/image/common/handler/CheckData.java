package com.image.common.handler;

import javax.swing.JLabel;

import com.image.common.pojo.DictUsers;

public class CheckData {

	private String propertyUrl;

	private String userName;

	private String passWord;

	private JLabel userNameMsg;

	private JLabel passWordMsg;

	private JLabel loginErrMsg;

	private String ip;

	private String port;

	private DictUsers user;

	public CheckData() {
	}

	public String getPropertyUrl() {
		return propertyUrl;
	}

	public void setPropertyUrl(String propertyUrl) {
		this.propertyUrl = propertyUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public JLabel getUserNameMsg() {
		return userNameMsg;
	}

	public void setUserNameMsg(JLabel userNameMsg) {
		this.userNameMsg = userNameMsg;
	}

	public JLabel getPassWordMsg() {
		return passWordMsg;
	}

	public void setPassWordMsg(JLabel passWordMsg) {
		this.passWordMsg = passWordMsg;
	}

	public JLabel getLoginErrMsg() {
		return loginErrMsg;
	}

	public void setLoginErrMsg(JLabel loginErrMsg) {
		this.loginErrMsg = loginErrMsg;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public DictUsers getUser() {
		return user;
	}

	public void setUser(DictUsers user) {
		this.user = user;
	}

}
