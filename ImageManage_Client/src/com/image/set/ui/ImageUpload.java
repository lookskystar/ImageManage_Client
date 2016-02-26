package com.image.set.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.enterprisedt.net.ftp.FileTransferClient;
import com.image.common.Params;
import com.image.common.pojo.ProcedureStep;
import com.image.common.ui.ImagePanel;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.Contains;
import com.image.common.util.FTPUnit;
import com.image.common.util.HessianServiceFactory;
import com.image.set.service.PresetImageService;

public class ImageUpload extends JDialog{

	private static final long serialVersionUID = 7491303207843849472L;

	// 新增标准图片按钮
	private JButton addImageBtn;
	// 标准工序详细信息标签
	private JLabel procedureDetailLab;
	Object[] procedure;
	List<ProcedureStep> procedureStep;
	private static PresetImageService presetImageService;
	
	static {
		HessianServiceFactory factory = HessianServiceFactory.getInstance();
		try {
			presetImageService = (PresetImageService) factory.createService(PresetImageService.class, "presetImageService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 构造函数
	public ImageUpload(Object[] procedure, List<ProcedureStep> procedureStep) {
		this.procedure = procedure;
		this.procedureStep = procedureStep;
		init();
	}

	// 窗口初始化
	public void init() {
		JPanel containJPanel = new JPanel();// 内容面板
		containJPanel.setLayout(null);

		setTitle("工序标准图片");
		setMinimumSize(new Dimension(800, 600));
		getContentPane().add(containJPanel);

		addImageBtn = new JButton("新增标准图片");
		String procedureInfo = "工序名称：" + procedure[1] + " 工序编号："
				+ procedure[10] + " 工序大类：" + procedure[15] + " 标准作业时间(分钟)： "
				+ procedure[8] + " 标准摄像时间(分钟)：" + procedure[4] + " 拍照数量："
				+ procedure[3] + " 备注：" + procedure[6] + "";
		procedureDetailLab = new JLabel(procedureInfo);

		final JPanel imageJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		String ftp = Params.ftpUrl; // ftp地址
		int port = Params.ftpPort; // ftp服务端口

		String title = null;
		long stepId = 0;
		int order = 0;
		String url = null;
		String imageUrl = null;
		ImagePanel iconPanel = null;

		for (ProcedureStep procedureStep2 : procedureStep) {
			stepId = procedureStep2.getStepId();
			url = procedureStep2.getPrestepImage(); // 压缩图片路径
			title = procedureStep2.getStepName();
			order = procedureStep2.getProSn();
			imageUrl = "ftp://" + ftp + ":" + port + "/" + url + "";
			// final String imageUrl =
			// "C:\\Documents and Settings\\Administrator\\My Documents\\images\\1.jpg";
			try {
				iconPanel = new ImagePanel(new URL(imageUrl), 200, 200, title, order, stepId);
				new Thread(iconPanel).start();
				imageJPanel.add(iconPanel);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		containJPanel.add(imageJPanel);
		imageJPanel.setBounds(20, 80, 780, 600);
		containJPanel.add(addImageBtn);
		addImageBtn.setBounds(20, 20, 120, 24);
		CommonComponentUtils.setButtonStyle(addImageBtn, 120, 24, "add.gif");
		//上传图片按钮事件
		addImageBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long nodeId = Long.parseLong(procedure[0].toString());
				ImageUploadFrame imageUploadFrame = new ImageUploadFrame(nodeId);
				imageUploadFrame.setVisible(true);
			}
		});
		
		containJPanel.add(procedureDetailLab);
		procedureDetailLab.setBounds(20, 50, 780, 24);

		// 初始化位置
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int width = screenSize.width;                                      
		int height = screenSize.height;
		int x = (width - 800) / 2;
		int y = (height - 600) / 2;
		setLocation(x, y);
		setModal(true);
	}
	
	//图片上传窗口
	class ImageUploadFrame extends JDialog{

		private static final long serialVersionUID = 6271083834316670155L;
		//标题
		private JLabel titleJLabel;
		private JTextField titleJtxt;
		//顺序
		private JLabel orderJLabel;
		private JTextField orderJtxt;
		//预览
		private JLabel imageViewJLabel;
		//上传图片
		private JLabel upImageJLabel;
		private JTextField fileJtxt;
		private JButton fileBtn;
		
		//备注
		private JLabel note;
		private JTextField noteJtxt;
		
		private JButton commitBtn;
		private JButton cancleBtn;
		private long nodeId;
		private File file;//选中的上传的图片
		
 		public ImageUploadFrame(long nodeId){
 			this.nodeId = nodeId;
 			init();
		}
 		
 		public void init(){
			titleJLabel = new JLabel("标题：");
			titleJtxt = new JTextField();
			orderJLabel = new JLabel("顺序：");
			orderJtxt = new JTextField();
			imageViewJLabel = new JLabel("图片预览：");
			upImageJLabel = new JLabel("上传图片：");
			fileJtxt = new JTextField();
			fileBtn = new JButton("浏览...");
			note = new JLabel("备注：");
			noteJtxt = new JTextField();
			commitBtn = new JButton("提交");
			cancleBtn = new JButton("重置");

			setTitle("图片上传");
			setSize(360, 480);
			JPanel containJPanel = new JPanel();// 内容面板
			containJPanel.setLayout(null);
			getContentPane().add(containJPanel);

			containJPanel.add(titleJLabel);
			titleJLabel.setBounds(90, 50, 80, 25);
			containJPanel.add(titleJtxt);
			titleJtxt.setBounds(130, 50, 120, 25);

			containJPanel.add(orderJLabel);
			orderJLabel.setBounds(90, 90, 80, 25);
			containJPanel.add(orderJtxt);
			orderJtxt.setBounds(130, 90, 120, 25);

			containJPanel.add(imageViewJLabel);
			imageViewJLabel.setBounds(67, 130, 120, 120);
			
			containJPanel.add(upImageJLabel);
			upImageJLabel.setBounds(67, 250, 80, 25);
			containJPanel.add(fileJtxt);
			fileJtxt.setBounds(130, 250, 120, 25);
			containJPanel.add(fileBtn);
			fileBtn.setBounds(260, 250, 80, 25);
			fileBtn.setForeground(Color.white);
			fileBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
			
			// 选择上传图片
			fileBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					file = showFileChooserDialog();
					fileJtxt.setText(file.getName());
				}
			});

			containJPanel.add(note);
			note.setBounds(90, 280, 80, 25);
			containJPanel.add(noteJtxt);
			noteJtxt.setBounds(130, 280, 120, 25);

			containJPanel.add(commitBtn);
			commitBtn.setBounds(100, 320, 80, 30);
			commitBtn.setForeground(Color.white);
			commitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
			containJPanel.add(cancleBtn);
			cancleBtn.setBounds(200, 320, 80, 30);
			cancleBtn.setForeground(Color.white);
			cancleBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
			
			// 图片上传
			commitBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String title = titleJtxt.getText().trim();// 标题
					String order = orderJtxt.getText().trim();// 顺序
					String fileName = fileJtxt.getText().trim();// 路径
					String ftp = Params.ftpUrl; // ftp地址
					int port = Params.ftpPort; // ftp服务端口
					String username = Params.ftpUserName;// ftp用户名
					String password = Params.ftpPwd;// 密码
					
					try {
						FileTransferClient ftpClient = FTPUnit.getFileTransferClient(ftp, port, username,password);
						Object[] object = (Object[]) presetImageService.findProcedure(nodeId);

						String folderPath = "/" + Contains.STANDARD_IMAGE_FOLDER + "/" + object[5] + "/" + object[10] + "/";
						Map<String, String[]> map = FTPUnit.getImagePath(new String[] { fileName }, folderPath);
						String remoteFilePath = map.get("imgurl")[0];// 原文件
						String minRemoteFilePath = map.get("preimgurl")[0];// 压缩文件
						
						FTPUnit.upload(file, remoteFilePath, folderPath, ftpClient);
						// 图像压缩
						FTPUnit.zipWidthHeightImageFile(remoteFilePath,
								minRemoteFilePath, Contains.IMAGE_ZIP_WIDTH,
								Contains.IMAGE_ZIP_HEIGHT,
								Contains.IMAGE_ZIP_QUALITY, ftpClient);

						FTPUnit.closeFileTransferClient(ftpClient);

						ProcedureStep procedureStep = new ProcedureStep();
						procedureStep.setProId(nodeId);
						procedureStep.setStepName(title);
						System.out.println(order);
						procedureStep.setProSn(Integer.parseInt(order));
						procedureStep.setPrestepImage(minRemoteFilePath);
						procedureStep.setStepImage(remoteFilePath);

						presetImageService.saveProcedureStep(procedureStep);
						System.out.println("上传成功、、、");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// 初始化位置
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int width = screenSize.width;
			int height = screenSize.height;
			int x = (width - 360) / 2;
			int y = (height - 480) / 2;
			setLocation(x, y);
			setModal(true);
		}
	}

	// 获得选中图片
	public File showFileChooserDialog() {
		JFileChooser chooser = new JFileChooser("c:\\");
		// chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);只能选择目录
		// chooser.setFileHidingEnabled(true);显示隐藏文件
		chooser.setMultiSelectionEnabled(false);// 不可以选择多个文件
		chooser.showOpenDialog(this);
		File file = chooser.getSelectedFile();
		return file;
	}
}
