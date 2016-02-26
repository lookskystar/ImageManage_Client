package com.image.common.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.enterprisedt.net.ftp.FileTransferClient;
import com.image.common.Params;
import com.image.common.pojo.ProcedureStep;
import com.image.common.util.FTPUnit;
import com.image.common.util.HessianServiceFactory;
import com.image.set.service.PresetImageService;

public class ImagePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 6674326795251182877L;

	private JLabel iconLabel;
	private JButton delJBtn;
	private JButton editJBtn;
	private JLabel titleJLabel;
	private JLabel orderJLabel;

	private URL url;
	private int width;
	private int height;
	private String title;
	private int order;
	private long stepId;
	private static PresetImageService presetImageService;
	
	static {
		HessianServiceFactory factory = HessianServiceFactory.getInstance();
		try {
			presetImageService = (PresetImageService) factory.createService(PresetImageService.class, "presetImageService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ImagePanel(URL url, int width, int height, String title, int order,long stepId) {
		this.stepId = stepId;
		this.url = url;
		this.width = width;
		this.height = height;
		this.title = title;
		this.order = order;
	}

	@Override
	public void run() {
		ImageIcon image = new ImageIcon(url);
		image.setImage(image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
		iconLabel = new JLabel();
		iconLabel.setIcon(image);
		this.setLayout(new BorderLayout());
		this.setSize(250, 200);

		delJBtn = new JButton("删除");
		editJBtn = new JButton("编辑");
		titleJLabel = new JLabel("标题：" + title);
		orderJLabel = new JLabel("顺序：" + order);
		
		// 删除标准图片
		delJBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileTransferClient ftpClient = null;
				try {
					ftpClient = FTPUnit.getFileTransferClient(Params.ftpUrl,Params.ftpPort, Params.ftpUserName, Params.ftpPwd);
					ProcedureStep procedureStep = presetImageService.findProcedureStepById(stepId);
					FTPUnit.delete(new String[] { procedureStep.getStepImage(),procedureStep.getPrestepImage() }, 0, ftpClient);
					FTPUnit.closeFileTransferClient(ftpClient);
					presetImageService.deleteProcedureStepById(stepId);
					System.out.println("删除成功、、、");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		centerPanel.add(delJBtn);
		centerPanel.add(editJBtn);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(titleJLabel, BorderLayout.NORTH);
		southPanel.add(orderJLabel, BorderLayout.SOUTH);

		this.add(iconLabel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
}
