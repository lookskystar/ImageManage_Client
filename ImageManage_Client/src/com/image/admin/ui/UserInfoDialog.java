package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.image.admin.service.UsersService;
import com.image.common.Params;
import com.image.common.pojo.DictRoles;
import com.image.common.pojo.DictTeams;
import com.image.common.pojo.DictUsers;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;

/**
 * 查看人员详情
 * @author L
 *
 */
public class UserInfoDialog extends JDialog {
	
	private static final long serialVersionUID = 135114794971821382L;

	JPanel pnlMain;
	JLabel lblRole;
	JComboBox RoleComboBox;
	JLabel lblBz;
	JComboBox BzComboBox;
	JLabel lblUserName;
	JTextField txtUserName;
	JLabel lblGh;
	JTextField txtGh;
	JLabel lblName;
	JTextField txtName;
	JLabel lblPassWord;
	JTextField txtPassWord;
	JLabel lblPy;
	JTextField txtPy;
	JLabel lblIDK;
	JTextField txtIDK;
	JButton btnEnsure;
	DictUsers sessionUser=Params.sessionUser;
	DictUsers user;
	private static HessianServiceFactory hessianServiceFactory;
	private static UsersService usersService;
	// 调用服务接口
	static {
		hessianServiceFactory = HessianServiceFactory.getInstance();
		try {
			usersService = (UsersService) hessianServiceFactory.createService(
					UsersService.class, "usersService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserInfoDialog(DictUsers user){
		this.user=user;
		comboboxInit();
		init();
		panelInit();
		buttonInit();
	}
	
	/**
	 * 选择控件初始化
	 */
	private void comboboxInit() {
		// 角色选择下拉框
		List<DictRoles> roleList = usersService.listDictRoles();
		Integer roleSize = roleList.size();
		Object[] roleData = new Object[roleSize + 1];
		roleData[0] = "-选择-";
		for (int i = 0; i < roleSize; i++) {
			roleData[i + 1] = roleList.get(i).getRoleName();
		}
		RoleComboBox = new JComboBox(roleData);
		RoleComboBox.setPreferredSize(new Dimension(130, 26));
		
		// 班组选择下拉框
		List<DictTeams> teamList = usersService.listDictTeams(sessionUser.getAreaId());
		Integer teamSize = teamList.size();
		Object[] teamData = new Object[teamSize + 1];
		teamData[0] = "-选择-";
		for (int i = 0; i < teamSize; i++) {
			teamData[i + 1] = teamList.get(i).getTeamName();
		}
		BzComboBox = new JComboBox(teamData);
		BzComboBox.setPreferredSize(new Dimension(150, 26));
	}
	
	//初始化
	public void init(){
		
		pnlMain = new JPanel(null);
		lblRole = new JLabel("角    色：");
		lblBz=new JLabel("班    组：");
		lblName = new JLabel("用户姓名：");
		txtName=new JTextField();
		lblGh = new JLabel("工    号：");
		txtGh=new JTextField();
		lblUserName = new JLabel("登陆名称：");
		txtUserName=new JTextField();
		lblPassWord = new JLabel("登陆密码：");
		txtPassWord=new JTextField();
		lblPy = new JLabel("拼音缩写：");
		txtPy=new JTextField();
		lblIDK = new JLabel("IDK 卡号：");
		txtIDK=new JTextField();
		btnEnsure = new JButton(ResourceUtils.getResourceByKey("JButton.ensure_label"));
		CommonComponentUtils.setButtonStyle(btnEnsure,70,24,null);
		
		for (DictRoles listRole : usersService.listDictRoles()) {
			if (user.getRoleId()==listRole.getRoleId()) {
				RoleComboBox.setSelectedItem(listRole.getRoleName());
				break;
			}
		}
		for (DictTeams listTeam : usersService.listDictTeams(sessionUser.getAreaId())) {
			if (user.getTeamId()==listTeam.getTeamId()) {
				BzComboBox.setSelectedItem(listTeam.getTeamName());
				break;
			}
		}
		txtName.setText(user.getName());
		txtGh.setText(user.getGonghao());
		txtUserName.setText(user.getUsername());
		txtPassWord.setText(user.getPassword());
		txtPy.setText(user.getPy());
		txtIDK.setText(user.getIdnum());

		this.setLayout(new BorderLayout());
		this.setResizable(false);
	}
	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		
		btnEnsure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UserInfoDialog.this.dispose();
			}
		});
	}

	/**
	 * 面板、位置初始化
	 */
	public void panelInit() {
		setTitle("查看用户详情");
		setMinimumSize(new Dimension(600, 420));
		pnlMain.add(lblRole);
		pnlMain.add(RoleComboBox);
		pnlMain.add(BzComboBox);
		pnlMain.add(lblBz);
		pnlMain.add(lblUserName);
		pnlMain.add(txtUserName);
		pnlMain.add(lblGh);
		pnlMain.add(txtGh);
		pnlMain.add(lblName);
		pnlMain.add(txtName);
		pnlMain.add(lblPassWord);
		pnlMain.add(txtPassWord);
		pnlMain.add(lblPy);
		pnlMain.add(txtPy);
		pnlMain.add(lblIDK);
		pnlMain.add(txtIDK);
		pnlMain.add(btnEnsure);

		lblRole.setBounds(40, 35, 100, 25);
		RoleComboBox.setBounds(120, 35, 100, 25);
		lblBz.setBounds(300, 35, 100, 25);
		BzComboBox.setBounds(380, 35, 100, 25);
		lblName.setBounds(40, 75, 100, 25);
		txtName.setBounds(120, 75, 100, 25);
		lblGh.setBounds(300, 75, 100, 25);
		txtGh.setBounds(380, 75, 100, 25);
		lblUserName.setBounds(40, 115, 100, 25);
		txtUserName.setBounds(120, 115, 100, 25);
		lblPassWord.setBounds(300, 115, 100, 25);
		txtPassWord.setBounds(380, 115, 100, 25);
		lblPy.setBounds(40, 155, 100, 25);
		txtPy.setBounds(120, 155, 100, 25);
		lblIDK.setBounds(300, 155, 100, 25);
		txtIDK.setBounds(380, 155, 100, 25);

		btnEnsure.setBounds(240, 240, 70, 22);

		this.add(pnlMain);
		this.setVisible(true);

		// 初始化位置
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		int x = (width - this.getWidth()) / 2;
		int y = (height - this.getHeight()) / 2;
		setLocation(x, y);
		setModal(true);
	}
}
