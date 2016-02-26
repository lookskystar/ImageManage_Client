package com.image.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;


/**
 * 文件上传窗口
 * @author HarderXin
 *
 */
public class UploadDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 135114794971821382L;

	public UploadDialog(){
		createAndShowGUI();
	}
	
	public void createAndShowGUI(){
		UIManager manager = new UIManager();
		setTitle("FTP文件上传");
		setJMenuBar(manager.createMenuBar());
		setContentPane(manager.createContentPane());
		setMinimumSize(new Dimension(500, 400));
		//初始化位置
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int width=screenSize.width;
		int height=screenSize.height;
		int x=(width-500)/2;
		int y=(height-400)/2;
		setLocation(x, y);//初始化位置
		setModal(true);
	}
}
