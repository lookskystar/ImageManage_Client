package com.image.common.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jb2011.lnf.beautyeye.widget.N9ComponentFactory;

import com.image.admin.service.RolesService;
import com.image.admin.ui.FileDelModule;
import com.image.admin.ui.RoleModule;
import com.image.admin.ui.UserModule;
import com.image.autoupdate.client.AutoUpdateClient;
import com.image.autoupdate.client.ProgressBarDialog;
import com.image.common.BasicModule;
import com.image.common.MainSetRunnable;
import com.image.common.Params;
import com.image.common.SwitchModuleAction;
import com.image.common.pojo.DictUsers;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ImageUtils;
import com.image.common.util.ResourceUtils;
import com.image.query.ui.QueryMainModule;
import com.image.set.ui.DeviceModule;
import com.image.set.ui.PresetModule;
import com.image.ui.ImageModule;

/**
 * 主界面
 * 
 * @author Administrator
 * 
 */
public class MainSet extends JPanel {

	private static final long serialVersionUID = 4368771832453283164L;
	private static Logger logger = Logger.getLogger(MainSet.class);

	private static final int PREFERRED_WIDTH = 720;
	private static final int PREFERRED_HEIGHT = 640;

	private static HessianServiceFactory hessianServiceFactory;

	private static RolesService rolesService;

	static {
		hessianServiceFactory = HessianServiceFactory.getInstance();
		// 加载服务接口
		try {
			rolesService = (RolesService) hessianServiceFactory.createService(RolesService.class, "rolesService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JFrame frame; // 主窗口JFrame
	private JDialog aboutBox = null;// 关于窗口
	private JPopupMenu popupMenu = null;// 弹出菜单

	private JPanel modulePanel = null;// 模块内容面板
	private JLabel statusField = null;// 状态提示标签
	private BasicModule currentModule = null;// 当前模块

	/**
	 * 主函数入库
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new MainSet(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration());
	}

	/**
	 * 构造方法
	 * 
	 * @param main
	 * @param gc
	 */
	public MainSet(GraphicsConfiguration gc) {
		// 隐藏设置按钮
		UIManager.put("RootPane.setupButtonVisible", false);

		frame = new JFrame(gc) {
			private static final long serialVersionUID = 9003635571032440349L;

			@Override
			protected void processWindowEvent(WindowEvent e) {
				if (e.getID() == WindowEvent.WINDOW_CLOSING) {
					Object[] choose = { "退出系统", "最小化到托盘" };
					int n = JOptionPane.showOptionDialog(this, "请选择你要进行的操作", "提示框", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null, choose, choose[0]);
					if (n == 0 || n == -1) {
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					} else {
						// 系统托盘
						systemTray();
					}
				}
				super.processWindowEvent(e);
			}
		};
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(200, 200));

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		initialize();// 初始化
		preload();// 预加载界面

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showMainFrame();
			}
		});
	}

	/**
	 * 显示窗口
	 */
	public void showMainFrame() {
		if (!frame.isVisible()) {
			frame.setTitle(ResourceUtils.getResourceByKey("Frame.title"));
			frame.getContentPane().add(this, BorderLayout.CENTER);
			frame.pack();

			Rectangle screenRect = frame.getGraphicsConfiguration().getBounds();
			Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());

			int centerWidth = screenRect.width < frame.getSize().width ? screenRect.x : screenRect.x + screenRect.width
					/ 2 - frame.getSize().width / 2;
			int centerHeight = screenRect.height < frame.getSize().height ? screenRect.y : screenRect.y
					+ screenRect.height / 2 - frame.getSize().height / 2;

			centerHeight = centerHeight < screenInsets.top ? screenInsets.top : centerHeight;

			frame.setLocation(centerWidth, centerHeight);
			frame.setVisible(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}

	/**
	 * 初始化界面
	 * 
	 * @param args
	 */
	private void initialize() {
		JMenuBar menuBar = createMenuBar();// 窗口菜单
		frame.setJMenuBar(menuBar);

		popupMenu = createPopupMenu(); // 弹出菜单
		frame.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				triggerEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				triggerEvent(e);
			}

			private void triggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		// 模块内容
		modulePanel = new JPanel(new BorderLayout()){
			private static final long serialVersionUID = 6242600666011676793L;
			
			@Override
			protected void paintComponent(Graphics g) {
			    ImageIcon icon=ImageUtils.createImageIcon("main.jpg");
			    g.drawImage(icon.getImage(), 0, 0, frame.getSize().width,frame.getSize().height,frame);
			}
		};
		modulePanel.setBorder(new ModulePanelBorder());
		add(modulePanel, BorderLayout.CENTER);

		// 提示面板
		statusField = new JLabel("");
		JPanel hintPanel = new JPanel(new BorderLayout());
		JLabel hintLabel = N9ComponentFactory.createLabel_style1(ResourceUtils
				.getResourceByKey("HintLabel.title_label"));
		hintPanel.add(hintLabel, BorderLayout.WEST);
		statusField.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		hintPanel.add(statusField, BorderLayout.CENTER);
		hintPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
		add(hintPanel, BorderLayout.SOUTH);
		// 系统托盘
		// systemTray();
	}
	
	private TrayIcon trayIcon;
	
	/**
	 * 系统托盘 中文乱码问题解决方案：右键--->run as-->run configurations -->Arguments-->VM
	 * arguments中添加-Dfile.encoding=GB18030 apply后运行即可
	 */
	@SuppressWarnings("serial")
	public void systemTray() {
		try {
			if (SystemTray.isSupported()) { // 判断系统是否支持系统托盘
				if(trayIcon==null){
					ImageIcon icon = ImageUtils.createImageIcon("bz.gif");// 载入图片,这里要写你的图标路径
					AbstractAction action = new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							showMainFrame();
						}
					};
					
					PopupMenu mainPopMenu = new PopupMenu();
					CommonComponentUtils.createPopupMenuItem2(mainPopMenu,
							ResourceUtils.getResourceByKey("Menu.main_label"), null, action, true);
					mainPopMenu.addSeparator();
					CommonComponentUtils.createPopupMenuItem2(mainPopMenu,
							ResourceUtils.getResourceByKey("Menu.about_label"), null, new AboutAction(this), true);
					mainPopMenu.addSeparator();
					CommonComponentUtils.createPopupMenuItem2(mainPopMenu,
							ResourceUtils.getResourceByKey("Menu.exit_label"), null, new ExitAction(this), true);
					
					trayIcon = new TrayIcon(icon.getImage(), ResourceUtils.getResourceByKey("Frame.title"),
							mainPopMenu);
					// 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口
					trayIcon.addMouseListener(new MouseAdapter() {// 双击事件
						@Override
						public void mouseClicked(MouseEvent e) {
							if (e.getClickCount() == 2) {
								showMainFrame();
							}
						}
					});
				}else{
					SystemTray.getSystemTray().remove(trayIcon); 
				}
				SystemTray.getSystemTray().add(trayIcon); // 创建系统托盘
			} else {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		} catch (Exception e) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 如果设置托盘失败,则直接可以关闭窗口
			logger.warn("设置系统托盘失败:" + e.getMessage());
		}
	}

	/**
	 * 初始化界面
	 */
	private void preload() {
		//setModule(new DeviceModule(this));
	}

	/**
	 * 设置当前模块
	 */
	public void setModule(BasicModule module) {
		currentModule = module;
		modulePanel.removeAll();
		modulePanel.add(currentModule.getModulePanel(), BorderLayout.CENTER);

		modulePanel.validate();
		modulePanel.repaint();

		// SwingUtilities.updateComponentTreeUI(modulePanel);
		setStatus(ResourceUtils.getResourceByKey("Status.tip"));
	}

	/**
	 * 改变状态栏
	 */
	public void setStatus(String s) {
		SwingUtilities.invokeLater(new MainSetRunnable(this, s) {
			public void run() {
				mainSet.statusField.setText((String) obj);
			}
		});
	}

	/**
	 * 创建窗口菜单
	 * 
	 * @param args
	 */
	private JMenuBar createMenuBar() {
		DictUsers user =  Params.sessionUser;
		Long roleId =  user.getRoleId();
		List<Object> funcIds =  rolesService.getFuncIdByIdOfSQL(roleId);
		List<String> rolesFunctionNames = new ArrayList<String>();
		for (Object obj : funcIds) {
			String funcName = (rolesService.getFuncNameById(Long.parseLong(obj.toString()))).toString();
			rolesFunctionNames.add(funcName);
		}
		
		JMenuBar menuBar = new JMenuBar();

		JLabel userLabel = null;
		if (Params.sessionUser == null) {
			userLabel = N9ComponentFactory.createLabel_style1(ResourceUtils.getResourceByKey("Login.nologin_label"));
		} else {
			userLabel = N9ComponentFactory.createLabel_style1(Params.sessionUser.getUsername());
		}
		menuBar.add(userLabel);
		
		// 影像预设
		JMenu setMenu = (JMenu) menuBar.add(new JMenu("影像预设(S)"));
		setMenu.setMnemonic('S');
		// 标准预设
		if(!rolesFunctionNames.contains("标准预设")){
			CommonComponentUtils.createMenuItem(setMenu, "标准预设(S)", 'S', null, new SwitchModuleAction<PresetModule>(this, PresetModule.class), false);
			setMenu.addSeparator();
		} else {
			CommonComponentUtils.createMenuItem(setMenu, "标准预设(S)", 'S', null, new SwitchModuleAction<PresetModule>(this, PresetModule.class), true);
			setMenu.addSeparator();
		}
		// 设备管理
		if(!rolesFunctionNames.contains("设备管理")){
			CommonComponentUtils.createMenuItem(setMenu, "设备管理(M)", 'M', null, new SwitchModuleAction<DeviceModule>(this, DeviceModule.class), false);
		} else {
			CommonComponentUtils.createMenuItem(setMenu, "设备管理(M)", 'M', null, new SwitchModuleAction<DeviceModule>(this, DeviceModule.class), true);
		}
		
		// 影像操作
		JMenu operationMenu = (JMenu) menuBar.add(new JMenu("影像操作(O)"));
		operationMenu.setMnemonic('O');
		// 影像操作
		if(!rolesFunctionNames.contains("图像操作")){
			CommonComponentUtils.createMenuItem(operationMenu, "影像操作(O)", 'O', null, new SwitchModuleAction<ImageModule>(this, ImageModule.class), false);
		} else {
			CommonComponentUtils.createMenuItem(operationMenu, "影像操作(O)", 'O', null, new SwitchModuleAction<ImageModule>(this, ImageModule.class), true);
		}
		
		// 统计归档
		JMenu countMenu = (JMenu) menuBar.add(new JMenu("统计归档(T)"));
		countMenu.setMnemonic('T');
		// 段级
		if(!rolesFunctionNames.contains("查询统计(段)")){
			CommonComponentUtils.createMenuItem(countMenu, "查询统计(段)", 'T', null, null, false);
		} else {
			CommonComponentUtils.createMenuItem(countMenu, "查询统计(段)", 'T', null, null, true);
		}
		// 所级统计
		if(!rolesFunctionNames.contains("查询统计(所)")){
			CommonComponentUtils.createMenuItem(countMenu, "查询统计(所)", 'Q', null, new SwitchModuleAction<QueryMainModule>(this, QueryMainModule.class), false);
		} else {
			CommonComponentUtils.createMenuItem(countMenu, "查询统计(所)", 'Q', null, new SwitchModuleAction<QueryMainModule>(this, QueryMainModule.class), true);
		}
		
		
		// 后台管理
		JMenu sysMenu = (JMenu) menuBar.add(new JMenu("后台管理(T)"));
		sysMenu.setMnemonic('T');
		if(!rolesFunctionNames.contains("人员管理")){
			CommonComponentUtils.createMenuItem(sysMenu, "人员管理(P)", 'P', null, new SwitchModuleAction<UserModule>(this, UserModule.class), false);
		} else {
			CommonComponentUtils.createMenuItem(sysMenu, "人员管理(P)", 'P', null, new SwitchModuleAction<UserModule>(this, UserModule.class), true);
		}
		if(!rolesFunctionNames.contains("角色管理")){
			CommonComponentUtils.createMenuItem(sysMenu, "角色管理(R)", 'R', null, new SwitchModuleAction<RoleModule>(this, RoleModule.class), false);
			sysMenu.addSeparator();
		} else {
			CommonComponentUtils.createMenuItem(sysMenu, "角色管理(R)", 'R', null, new SwitchModuleAction<RoleModule>(this, RoleModule.class), true);
			sysMenu.addSeparator();
		}
		if(!rolesFunctionNames.contains("文件删除")){
			CommonComponentUtils.createMenuItem(sysMenu, "文件删除(D)", 'D', null, new SwitchModuleAction<FileDelModule>(this, FileDelModule.class), false);
		} else {
			CommonComponentUtils.createMenuItem(sysMenu, "文件删除(D)", 'D', null, new SwitchModuleAction<FileDelModule>(this, FileDelModule.class), true);
		}
		
		// 通用操作
		JMenu commonMenu = (JMenu) menuBar.add(new JMenu(ResourceUtils.getResourceByKey("Menu.file_label")));
		commonMenu.setMnemonic(ResourceUtils.getResourceByKeyToChar("Menu.file_label"));
		CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.update_label"), ResourceUtils.getResourceByKeyToChar("Menu.update_mnemonic"), null, new VersionUpdateAction(this.getFrame(),true), true);
		CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.help_label"), ResourceUtils.getResourceByKeyToChar("Menu.help_mnemonic"), null, new HelpAction(), true);
		//CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.refresh_label"), ResourceUtils.getResourceByKeyToChar("Menu.refresh_mnemonic"), null, new RefreshAction(this), true);
		commonMenu.addSeparator();
		//CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.printset_label"), ResourceUtils.getResourceByKeyToChar("Menu.printset_mnemonic"), null, null, true);
		//CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.print_label"), ResourceUtils.getResourceByKeyToChar("Menu.print_mnemonic"), null, null, true);
		//commonMenu.addSeparator();
		//CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.login_label"), ResourceUtils.getResourceByKeyToChar("Menu.login_mnemonic"), null, new LoginAction(this), true);
		CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.exit_label"), ResourceUtils.getResourceByKeyToChar("Menu.exit_mnemonic"), null, new ExitAction(this), true);
		CommonComponentUtils.createMenuItem(commonMenu, ResourceUtils.getResourceByKey("Menu.about_label"), ResourceUtils.getResourceByKeyToChar("Menu.about_mnemonic"), null, new AboutAction(this), true);
		return menuBar;
	}

	/**
	 * 创建弹出菜单
	 * 
	 * @return
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.update_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.update_mnemonic"), null,
				new VersionUpdateAction(this.getFrame(), true), true);
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.help_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.help_mnemonic"), null, null, true);
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.refresh_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.refresh_label"), null, new RefreshAction(this), true);
		popupMenu.addSeparator();
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.printset_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.printset_mnemonic"), null, null, true);
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.print_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.print_mnemonic"), null, null, true);
		popupMenu.addSeparator();
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.login_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.login_mnemonic"), null, new LoginAction(this), true);
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.exit_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.exit_mnemonic"), null, new ExitAction(this), true);
		popupMenu.addSeparator();
		CommonComponentUtils.createPopupMenuItem(popupMenu, ResourceUtils.getResourceByKey("Menu.about_label"),
				ResourceUtils.getResourceByKeyToChar("Menu.about_mnemonic"), null, new AboutAction(this), true);
		return popupMenu;
	}

	/**
	 * 退出
	 */
	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 8754174785955362710L;
		MainSet mainSet;

		protected ExitAction(MainSet mainSet) {
			super("ExitAction");
			this.mainSet = mainSet;
		}

		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(null, "确定退出系统") == 0) {
				System.exit(0);
			}
		}
	}

	/**
	 * 关于
	 */
	class AboutAction extends AbstractAction {
		private static final long serialVersionUID = 6280359133908261181L;
		MainSet mainSet;

		protected AboutAction(MainSet mainSet) {
			super("AboutAction");
			this.mainSet = mainSet;
		}

		public void actionPerformed(ActionEvent e) {
			if (aboutBox == null) {
				aboutBox = new AboutBox(mainSet);
			}
			aboutBox.pack();
			aboutBox.setLocationRelativeTo(frame);
			aboutBox.setVisible(true);
		}
	}

	/**
	 * 刷新
	 * 
	 * @return
	 */
	class RefreshAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		MainSet mainSet;

		protected RefreshAction(MainSet mainSet) {
			super("RefreshAction");
			this.mainSet = mainSet;
		}

		public void actionPerformed(ActionEvent e) {
			currentModule.refresh();
		}
	}

	/**
	 * 重新登录
	 * 
	 * @author dell
	 * 
	 */
	class LoginAction extends AbstractAction {
		private static final long serialVersionUID = 8552453363931079606L;

		MainSet mainSet;

		protected LoginAction(MainSet mainSet) {
			super("LoginAction");
			this.mainSet = mainSet;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
			new LoginFrame();
		}

	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	class VersionUpdateAction extends AbstractAction {
		private static final long serialVersionUID = 2758116757212720263L;
		private boolean flag = false;
		private JFrame frame = null;

		public VersionUpdateAction(JFrame frame, boolean flag) {
			this.frame = frame;
			this.flag = flag;
		}

		public void actionPerformed(ActionEvent e) {
			ProgressBarDialog dialog = new ProgressBarDialog(frame, flag);
			AutoUpdateClient autoUpdateClient = new AutoUpdateClient(dialog);
			autoUpdateClient.update();
		}

	}
	
	/**
	 * 帮助文档
	 * @author Administrator
	 *
	 */
	class HelpAction extends AbstractAction{
		private static final long serialVersionUID = -1134671716233987785L;

		public void actionPerformed(ActionEvent e) {
			try {
				Runtime.getRuntime().exec("cmd /c .\\帮助文档.chm");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	public JFrame getFrame() {
		return frame;
	}
}