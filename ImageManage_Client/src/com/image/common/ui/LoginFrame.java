package com.image.common.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.image.autoupdate.client.AutoUpdateClient;
import com.image.autoupdate.client.ProgressBarDialog;
import com.image.common.handler.CheckData;
import com.image.common.handler.EmptyCheckHandler;
import com.image.common.handler.FtpSetHandler;
import com.image.common.handler.PropertyHandler;
import com.image.common.handler.UserDbValidateHandler;
import com.image.common.util.ImageUtils;
import com.image.common.util.ResourceUtils;

public class LoginFrame extends JFrame{
	
	private static final long serialVersionUID = 1150199457458237181L;
	private static Logger log = Logger.getLogger(LoginFrame.class);
	private static final String REMOTE_URL = "config/remote.properties";
	
	/**
	 * 登陆Frame主界面
	 */
	public LoginFrame(){
		setTitle(ResourceUtils.getResourceByKey("Frame.title"));
		JPanel jpanel=this.createLoginPanel();
	    GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jpanel,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jpanel,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        setFocusable(true);
        addKeyListener(new LoginKeyListener());
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(450, 480));
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int width=screenSize.width;
		int height=screenSize.height;
		int x=(width-this.getWidth())/2;
		int y=(height-this.getHeight())/2;
		setLocation(x, y);//初始化位置
		setResizable(false);
		pack();
		setVisible(true);
		
		ProgressBarDialog dialog = new ProgressBarDialog(this,false);
		AutoUpdateClient autoUpdateClient = new AutoUpdateClient(dialog);
		autoUpdateClient.update();
	}
	
	/**
	 * 创建登陆面板
	 * @return
	 */
	
	private JLabel username;
	private JLabel password;
	private JTextField unameText;
	private JPasswordField pwdText;
	private JButton loginBtn;
	private JButton resetBtn;
	private JButton setUpBtn;
	private JLabel title;
	private JLabel unameMsg;
	private JLabel pwdMsg;
	private JLabel about;
	private JPanel contentPanel;
	private JPanel setUpPanel;
	private JPanel setUpNorthPanel;
	private JPanel setUpCenterPanel;
	private JPanel detailPanel;
	private JLabel ipAddressLabel;
	private JLabel portLabel;
	private JTextField ipAddressText;
	private JTextField portText;
	
	private JPanel createLoginPanel(){
		//使用JPanel设置背景图片
		JPanel jpanel=new JPanel(){
			private static final long serialVersionUID = 6242600666011676793L;
			
			@Override
			protected void paintComponent(Graphics g) {
			    ImageIcon icon=ImageUtils.createImageIcon("login.jpg");
			    Image img=icon.getImage();
			    g.drawImage(img, 0, 0, icon.getIconWidth(), 
			    		icon.getIconHeight(), icon.getImageObserver());
			}
		};
		
		username=new JLabel();
		password=new JLabel();
		unameText=new JTextField();
		pwdText=new JPasswordField();
		loginBtn=new JButton();
		resetBtn=new JButton();
		title=new JLabel();
		unameMsg=new JLabel();
		pwdMsg=new JLabel();
		about=new JLabel();
		ipAddressLabel = new JLabel();
		portLabel = new JLabel();
		ipAddressText = new JTextField(22);
		portText = new JTextField(22);
		setUpBtn = new JButton();
		
		ipAddressLabel.setFont(new java.awt.Font("宋体", 1, 14));
		ipAddressLabel.setText(ResourceUtils.getResourceByKey("Login.label.ip"));
		portLabel.setFont(new java.awt.Font("宋体", 1, 14));
		portLabel.setText(ResourceUtils.getResourceByKey("Login.label.port"));
		
		username.setFont(new java.awt.Font("宋体", 1, 14));
        username.setText(ResourceUtils.getResourceByKey("Login.label.name"));

        password.setFont(new java.awt.Font("宋体", 1, 14));
        password.setText(ResourceUtils.getResourceByKey("Login.label.pwd"));
        
        unameText.addKeyListener(new LoginKeyListener());
        pwdText.addKeyListener(new LoginKeyListener());

        title.setFont(new java.awt.Font("宋体", Font.PLAIN,  22));
        title.setForeground(Color.BLACK);
        title.setText(ResourceUtils.getResourceByKey("Login.label.title"));

        loginBtn.setText(ResourceUtils.getResourceByKey("Login.btn.login"));
        loginBtn.setFont(new java.awt.Font("宋体", 1, 12));
        loginBtn.addActionListener(new LoginListener());
        loginBtn.addKeyListener(new LoginKeyListener());
        loginBtn.setIcon(ImageUtils.createImageIcon("user_group.gif"));
        loginBtn.setUI(new
        		BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

        resetBtn.setText(ResourceUtils.getResourceByKey("Login.btn.reset"));
        resetBtn.setFont(new java.awt.Font("宋体", 1, 12));
        resetBtn.setIcon(ImageUtils.createImageIcon("refresh.gif"));
        resetBtn.setUI(new 
        		BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unameText.setText("");
				pwdText.setText("");
				//获得焦点
				//unameText.requestFocus();
				unameText.grabFocus();
			}
		});
        setUpBtn.setFont(new java.awt.Font("宋体", 1, 12));
        setUpBtn.setText("高级设置");
        setUpBtn.addActionListener(new setUpListener());
        
        unameMsg.setForeground(new java.awt.Color(255, 0, 51));
        pwdMsg.setForeground(new java.awt.Color(255, 0, 51));
        
        about.setText("@骁睿铁路科技 2014");
        about.setFont(new java.awt.Font("宋体", 1, 12));
        about.setForeground(new java.awt.Color(0, 0, 255));
        
        contentPanel = new JPanel();
        GroupLayout jPanel1Layout = new GroupLayout(contentPanel);
        contentPanel.setLayout(jPanel1Layout);
        contentPanel.setBackground(null);
        contentPanel.setOpaque(false);
        
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(124, 124, 124)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 122, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(username)
                            .addComponent(password))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(unameText, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                            .addComponent(pwdText))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(unameMsg)
                            .addComponent(pwdMsg))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(title)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(title)
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username)
                    .addComponent(unameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unameMsg))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(password)
                    .addComponent(pwdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwdMsg))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        
        detailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detailPanel.setBackground(null);
        detailPanel.setOpaque(false);
        detailPanel.add(about);
        
        setUpPanel = new JPanel(new BorderLayout());
        setUpNorthPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setUpNorthPanel.add(setUpBtn);
        setUpNorthPanel.setBackground(null);
        setUpNorthPanel.setOpaque(false);
        
        setUpCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        setUpCenterPanel.add(ipAddressLabel);
        setUpCenterPanel.add(ipAddressText);
        setUpCenterPanel.add(portLabel);
        setUpCenterPanel.add(portText);
        setUpCenterPanel.setVisible(false);
        setUpCenterPanel.setBackground(null);
        setUpCenterPanel.setOpaque(false);
        
        setUpPanel.add(setUpNorthPanel, BorderLayout.NORTH);
        setUpPanel.add(setUpCenterPanel, BorderLayout.CENTER);
        setUpPanel.setBackground(null);
        setUpPanel.setOpaque(false);
        
        
        jpanel.setLayout(new BorderLayout());
        jpanel.add(contentPanel, BorderLayout.NORTH);
        jpanel.add(setUpPanel, BorderLayout.CENTER);
        jpanel.add(detailPanel, BorderLayout.SOUTH);
        return jpanel;
	}
	
	final class LoginListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			validateUser();
		}
	}
	
	final class setUpListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setUpCenterPanel.setVisible(!setUpCenterPanel.isVisible());
		}
		
	}
	
	final class LoginKeyListener implements KeyListener{
		@SuppressWarnings("static-access")
		@Override
		public void keyPressed(KeyEvent e) {
			int k=e.getKeyCode();
			if(k==e.VK_ENTER){
				validateUser();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
	}
	
	private void validateUser(){
		PropertyHandler propertyHandler = new PropertyHandler();
		EmptyCheckHandler emptyCheckHandler = new EmptyCheckHandler();
		UserDbValidateHandler validateHandler = new UserDbValidateHandler();
		FtpSetHandler ftpSetHandler = new FtpSetHandler();
		propertyHandler.setHandler(emptyCheckHandler);
		emptyCheckHandler.setHandler(validateHandler);
		validateHandler.setHandler(ftpSetHandler);
		CheckData checkData = new CheckData();
		checkData.setPropertyUrl(REMOTE_URL);
		checkData.setUserName(unameText.getText());
		checkData.setPassWord(new String(pwdText.getPassword()));
		checkData.setUserNameMsg(unameMsg);
		checkData.setPassWordMsg(pwdMsg);
		checkData.setLoginErrMsg(unameMsg);
		checkData.setIp(ipAddressText.getText());
		checkData.setPort(portText.getText());
		try {
			if(propertyHandler.dealRequest(checkData)){
				new MainSet(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
						.getDefaultConfiguration());
				LoginFrame.this.dispose();
			}
		} catch (Exception e) {
			unameMsg.setText("*服务器连接异常");
			log.error("服务器连接异常");
			e.printStackTrace();
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
			log.error("BeautyEyeLNF运行失败，原因是："+e.getMessage());
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LoginFrame();
			}
		});
	}
}
