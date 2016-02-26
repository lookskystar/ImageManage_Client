package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
 * 新增人员
 * @author L
 *
 */
public class AddUserDialog extends JDialog {
	
	private static final long serialVersionUID = 135114794971821382L;

	JPanel pnlMain;
	JLabel lblRole;
	JComboBox RoleComboBox;
	JLabel roleWarn;
	JLabel lblBz;
	JComboBox BzComboBox;
	JLabel bzWarn;
	JLabel lblUserName;
	JTextField txtUserName;
	JLabel usernameWarn;
	JLabel lblGh;
	JTextField txtGh;
	JLabel ghWarn;
	JLabel lblName;
	JTextField txtName;
	JLabel nameWarn;
	JLabel lblPassWord;
	JTextField txtPassWord;
	JLabel passwordWarn;
	JLabel lblPy;
	JTextField txtPy;
	JLabel pyWarn;
	JLabel lblIDK;
	JTextField txtIDK;
	JButton btnSave;
	JButton btnReset;
	DictUsers user=Params.sessionUser;
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
	
	public AddUserDialog(){
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
		List<DictTeams> teamList = usersService.listDictTeams(user.getAreaId());
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
		roleWarn=new JLabel("*");
		roleWarn.setForeground(Color.RED);
		lblBz=new JLabel("班    组：");
		bzWarn=new JLabel("*");
		bzWarn.setForeground(Color.RED);
		lblName = new JLabel("用户姓名：");
		txtName=new JTextField();
		nameWarn=new JLabel("*");
		nameWarn.setForeground(Color.RED);
		lblGh = new JLabel("工    号：");
		txtGh=new JTextField();
		ghWarn=new JLabel("*");
		ghWarn.setForeground(Color.RED);
		lblUserName = new JLabel("登陆名称：");
		txtUserName=new JTextField();
		usernameWarn=new JLabel("*");
		usernameWarn.setForeground(Color.RED);
		lblPassWord = new JLabel("登陆密码：");
		txtPassWord=new JTextField();
		passwordWarn=new JLabel("*");
		passwordWarn.setForeground(Color.RED);
		lblPy = new JLabel("拼音缩写：");
		txtPy=new JTextField();
		pyWarn=new JLabel("*");
		pyWarn.setForeground(Color.RED);
		lblIDK = new JLabel("IDK 卡号：");
		txtIDK=new JTextField();
		btnSave = new JButton(ResourceUtils.getResourceByKey("JButton.commit_label"));
		CommonComponentUtils.setButtonStyle(btnSave,70,24,null);
		btnReset = new JButton(ResourceUtils.getResourceByKey("JButton.cancel_label"));
		CommonComponentUtils.setButtonStyle(btnReset,70,24,null);

		this.setLayout(new BorderLayout());
		this.setResizable(false);
	}
	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtName.setText("");
				txtGh.setText("");
				txtUserName.setText("");
				txtPassWord.setText("");
				txtPy.setText("");
				txtIDK.setText("");
				//获得焦点
				txtName.grabFocus();
			}
		});
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				this.btnSave_actionPerformed(e);
			}

			private void btnSave_actionPerformed(ActionEvent e) {
				String name=txtName.getText().trim();
				String gonghao=txtGh.getText().trim();
				String username=txtUserName.getText().trim();
				String password=txtPassWord.getText().trim();
				String py=txtPy.getText().trim();
				DictUsers dictUser=new DictUsers();
				if (RoleComboBox.getSelectedIndex()==0) {
					JOptionPane.showMessageDialog(null, "角色不能为空", "错误",JOptionPane.ERROR_MESSAGE);
				}else
				if (BzComboBox.getSelectedIndex()==0) {
					JOptionPane.showMessageDialog(null, "班组不能为空", "错误",JOptionPane.ERROR_MESSAGE);
				}else
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(null, "用户姓名不能为空","错误", JOptionPane.ERROR_MESSAGE);
				}else
				if ("".equals(gonghao)) {
					JOptionPane.showMessageDialog(null, "工号不能为空","错误", JOptionPane.ERROR_MESSAGE);
				}else
				if ("".equals(username)) {
					JOptionPane.showMessageDialog(null, "登陆名称不能为空","错误", JOptionPane.ERROR_MESSAGE);
				}else
				if ("".equals(password)) {
					JOptionPane.showMessageDialog(null, "登陆密码不能为空","错误", JOptionPane.ERROR_MESSAGE);
				}else
				if ("".equals(py)) {
					JOptionPane.showMessageDialog(null, "拼音缩写不能为空","错误", JOptionPane.ERROR_MESSAGE);
				}else{
					for (DictRoles listRole : usersService.listDictRoles()) {
						if (RoleComboBox.getSelectedItem().equals(
								listRole.getRoleName())) {
							dictUser.setRoleId(listRole.getRoleId());
							break;
						}
					}
					for (DictTeams listTeam : usersService.listDictTeams(user.getAreaId())) {
						if (BzComboBox.getSelectedItem().equals(
								listTeam.getTeamName())) {
							dictUser.setTeamId(listTeam.getTeamId());
							break;
						}
					}
					dictUser.setName(name);
					dictUser.setGonghao(gonghao);
					dictUser.setUsername(username);
					dictUser.setPassword(password);
					dictUser.setPy(py);
					dictUser.setIdnum(txtIDK.getText().trim());
					dictUser.setAreaId(user.getAreaId());
					dictUser.setIsuser(1);
					usersService.saveDictUsers(dictUser);
					JOptionPane.showMessageDialog(null, "新增成功", "信息",JOptionPane.INFORMATION_MESSAGE);
					AddUserDialog.this.dispose();
				}
			}
		});
	}

	/**
	 * 面板、位置初始化
	 */
	public void panelInit() {
		setTitle("添加人员");
		setMinimumSize(new Dimension(600, 480));
		pnlMain.add(lblRole);
		pnlMain.add(roleWarn);
		pnlMain.add(RoleComboBox);
		pnlMain.add(BzComboBox);
		pnlMain.add(lblBz);
		pnlMain.add(bzWarn);
		pnlMain.add(lblUserName);
		pnlMain.add(txtUserName);
		pnlMain.add(usernameWarn);
		pnlMain.add(lblGh);
		pnlMain.add(txtGh);
		pnlMain.add(ghWarn);
		pnlMain.add(lblName);
		pnlMain.add(txtName);
		pnlMain.add(nameWarn);
		pnlMain.add(lblPassWord);
		pnlMain.add(txtPassWord);
		pnlMain.add(passwordWarn);
		pnlMain.add(lblPy);
		pnlMain.add(txtPy);
		pnlMain.add(pyWarn);
		pnlMain.add(lblIDK);
		pnlMain.add(txtIDK);
		pnlMain.add(btnSave);
		pnlMain.add(btnReset);

		lblRole.setBounds(40, 35, 100, 25);
		RoleComboBox.setBounds(120, 35, 100, 25);
		roleWarn.setBounds(230, 35, 100, 25);
		lblBz.setBounds(300, 35, 100, 25);
		BzComboBox.setBounds(380, 35, 100, 25);
		bzWarn.setBounds(490, 35, 100, 25);
		lblName.setBounds(40, 75, 100, 25);
		txtName.setBounds(120, 75, 100, 25);
		nameWarn.setBounds(230, 75, 100, 25);
		lblGh.setBounds(300, 75, 100, 25);
		txtGh.setBounds(380, 75, 100, 25);
		ghWarn.setBounds(490, 75, 100, 25);
		lblUserName.setBounds(40, 115, 100, 25);
		txtUserName.setBounds(120, 115, 100, 25);
		usernameWarn.setBounds(230, 115, 100, 25);
		lblPassWord.setBounds(300, 115, 100, 25);
		txtPassWord.setBounds(380, 115, 100, 25);
		passwordWarn.setBounds(490, 115, 100, 25);
		lblPy.setBounds(40, 155, 100, 25);
		txtPy.setBounds(120, 155, 100, 25);
		pyWarn.setBounds(230, 155, 100, 25);
		lblIDK.setBounds(300, 155, 100, 25);
		txtIDK.setBounds(380, 155, 100, 25);

		btnSave.setBounds(180, 200, 70, 22);
		btnReset.setBounds(300, 200, 70, 22);

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
