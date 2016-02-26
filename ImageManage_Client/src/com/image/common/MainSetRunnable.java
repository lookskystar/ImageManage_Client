package com.image.common;

import com.image.common.ui.MainSet;

public class MainSetRunnable implements Runnable{

	protected MainSet mainSet;
	protected Object obj;

	public MainSetRunnable(MainSet mainSet, Object obj) {
		this.mainSet = mainSet;
		this.obj = obj;
	}

	public void run() {
	}
}
