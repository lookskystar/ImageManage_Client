package com.image.common;

import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;

import com.image.common.ui.MainSet;
import com.image.common.util.ResourceUtils;

public class SwitchModuleAction<T> extends AbstractAction{
	
	private static final long serialVersionUID = -7639626348349457694L;
	private MainSet mainSet;
	private Class<T> moduleCls;

	public SwitchModuleAction(MainSet mainSet, Class<T> moduleCls) {
//		super(module.getName(), module.getIcon());
		this.mainSet = mainSet;
		this.moduleCls = moduleCls;
	}

	/* 
	 * 切换面板
	 */
	public void actionPerformed(ActionEvent e) {
		mainSet.setStatus(ResourceUtils.getResourceByKey("Status.load"));
		try {
			Constructor<T> constructor = moduleCls.getDeclaredConstructor(MainSet.class);    
			constructor.setAccessible(true);    
			BasicModule module=(BasicModule)constructor.newInstance(mainSet);   
			mainSet.setModule(module);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
