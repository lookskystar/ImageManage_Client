package com.image.autoupdate.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;
/**
 * 更新升级进度提示框
 * @author Administrator
 *
 */
public class ProgressBarDialog extends JDialog{

	private static final long serialVersionUID = -2397863161431623867L;
	
	private JProgressBar progressBar;
	private JLabel statusLabel;
	private boolean flag ;
	
	public ProgressBarDialog(JFrame frame,boolean flag){
		this.progressBar = new JProgressBar();
		this.progressBar.setIndeterminate(true);
		this.statusLabel = new JLabel();
		this.flag = flag;
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar,BorderLayout.NORTH);
		panel.add(statusLabel,BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		setTitle("更新升级");
        setResizable(true);
        setSize(380, 72);
        Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int width=screenSize.width;
		int height=screenSize.height;
		int x=(width-this.getWidth())/2;
		int y=(height-this.getHeight())/2;
        setLocation(x, y);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 不允许关闭
        setVisible(flag);
	}
	
	/**
	 * 更新提示语
	 * @param text
	 */
	public void setStatus(String text){
		statusLabel.setText(text);
	}
	
	/**
	 * 更新完成
	 * @param text
	 * @param optionType 例如 JOptionPane.OK_OPTION
	 */
	public void closeAndTip(String tipText,int optionType){
		progressBar.setValue(100);
		progressBar.setIndeterminate(false);
		
		this.dispose();
		if((!flag && optionType==JOptionPane.YES_OPTION)||flag){
			JOptionPane.showMessageDialog(this,tipText,"更新完成",optionType);
		}
	}
	
	public static void main(String[] args) {
		//加入样式
		try{
			BeautyEyeLNFHelper.frameBorderStyle =
				FrameBorderStyle.translucencySmallShadow;
			//去掉隐藏设置按钮
			UIManager.put("RootPane.setupButtonVisible", false);
    		BeautyEyeLNFHelper.launchBeautyEyeLNF();
		}catch (Exception e){
			e.printStackTrace();
		}
		new ProgressBarDialog(null,true);
	}
}
