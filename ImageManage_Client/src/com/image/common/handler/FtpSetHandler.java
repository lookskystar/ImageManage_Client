package com.image.common.handler;

import com.image.common.Params;
import com.image.common.pojo.DictAreas;
import com.image.common.pojo.DictUsers;
import com.image.common.util.HessianServiceFactory;
import com.image.work.service.DictUsersService;

public class FtpSetHandler extends AbstractHandler {

	@Override
	public boolean dealRequest(CheckData checkData) throws Exception {
		HessianServiceFactory factory = HessianServiceFactory.getInstance();
		DictUsers user = checkData.getUser();
		DictUsersService service = (DictUsersService) factory.createService(DictUsersService.class, "dictUsersService");
		if (null != user.getAreaId()) {
			DictAreas dictarea = service.getDictAreasById(user.getAreaId());
			if (null != dictarea) {
				Params.ftpUrl = dictarea.getFtpIp();
				Params.ftpPort = dictarea.getFtpPort();
				Params.ftpUserName = dictarea.getFtpUsername();
				Params.ftpPwd = dictarea.getFtpPassword();
			}
		}
		if (null != handler) {
			return this.handler.dealRequest(checkData);
		}
		return true;
	}

}
