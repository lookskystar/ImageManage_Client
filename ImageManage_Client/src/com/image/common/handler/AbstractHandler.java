package com.image.common.handler;

public abstract class AbstractHandler {

	protected AbstractHandler handler;
	
	public AbstractHandler getHandler() {
		return handler;
	}

	public void setHandler(AbstractHandler handler) {
		this.handler = handler;
	}

	public abstract boolean dealRequest(CheckData checkData) throws Exception;

}
