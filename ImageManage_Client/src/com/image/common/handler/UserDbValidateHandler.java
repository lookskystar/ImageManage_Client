package com.image.common.handler;

import javax.swing.JLabel;

import com.image.common.Params;
import com.image.common.pojo.DictUsers;
import com.image.common.util.HessianServiceFactory;
import com.image.work.service.DictUsersService;

public class UserDbValidateHandler extends AbstractHandler {

	@Override
	public boolean dealRequest(CheckData checkData) throws Exception {
		HessianServiceFactory factory = HessianServiceFactory.getInstance();
		String userName = checkData.getUserName();
		String passWord = checkData.getPassWord();
		JLabel loginErrMsg = checkData.getLoginErrMsg();
		DictUsersService service = (DictUsersService) factory.createService(DictUsersService.class, "dictUsersService");
		DictUsers user = service.login(userName, passWord);
		if (null == user) {
			loginErrMsg.setText("*用户名或密码错误!");
			return false;
		}
		checkData.setUser(user);
		Params.sessionUser = user;
		if (null != handler) {
			return this.handler.dealRequest(checkData);
		}
		return true;
	}

}
