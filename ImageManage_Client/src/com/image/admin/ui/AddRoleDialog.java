package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.image.admin.service.RolesService;
import com.image.common.pojo.DictRoles;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;

/**
 * 新增角色
 * @author L
 *
 */
public class AddRoleDialog extends JDialog {
	
	private static final long serialVersionUID = 135114794971821382L;
	
	JPanel pnlMain;
	JLabel lblRoleName;
	JTextField txtRoleName;
	JLabel warn;
	JLabel lblRolePy;
	JTextField txtRolePy;
	JLabel lblRoleLevel;
	JRadioButton btnCj;
	JRadioButton btnD;
	ButtonGroup roleLevel=new ButtonGroup() ;
	JLabel lblRoleNote;
	JTextField txtRoleNote;
	JButton btnSave;
	JButton btnReset;
	private static HessianServiceFactory hessianServiceFactory;
	private static RolesService rolesService;

	// 调用服务接口
	static {
		hessianServiceFactory = HessianServiceFactory.getInstance();
		try {
			rolesService = (RolesService) hessianServiceFactory.createService(
					RolesService.class, "rolesService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AddRoleDialog(){
		init();
		panelInit();
		buttonInit();
	}
	
	/**
	 * 初始化
	 * */
	public void init(){
		
		pnlMain = new JPanel(null);
		lblRoleName = new JLabel("角色名称:");
		txtRoleName=new JTextField();
		warn=new JLabel("*");
		warn.setForeground(Color.RED);
		lblRolePy = new JLabel("角色拼音:");
		txtRolePy=new JTextField();
		lblRoleLevel=new JLabel("角色等级:");
		lblRoleNote = new JLabel("角色说明:");
		txtRoleNote=new JTextField();
		btnCj=new JRadioButton("车间级",true);
		btnD=new JRadioButton("段级");
		roleLevel.add(btnCj);
		roleLevel.add(btnD);
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
				txtRoleName.setText("");
				txtRolePy.setText("");
				txtRoleNote.setText("");
				//获得焦点
				txtRoleName.grabFocus();
			}
		});
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				this.btnSave_actionPerformed(e);
			}

			private void btnSave_actionPerformed(ActionEvent e) {
				DictRoles dictRole = new DictRoles();
				String roleName=txtRoleName.getText().trim();
				if("".equals(roleName)){
					JOptionPane.showMessageDialog(null, "角色名称不能为空","错误",JOptionPane.ERROR_MESSAGE);
				}else{
					DictRoles role=rolesService.getDictRolesByName(roleName);
					if(role!=null){
						JOptionPane.showMessageDialog(null, "新增失败,角色名称已经存在","错误",JOptionPane.ERROR_MESSAGE);
					}else{
						dictRole.setRoleName(roleName);
						dictRole.setRolePy(txtRolePy.getText().trim());
						if (btnCj.isSelected()) {
							dictRole.setRoleLevel(0);
						} else {
							dictRole.setRoleLevel(1);
						}
						dictRole.setRoleNote(txtRoleNote.getText().trim());
						rolesService.saveRole(dictRole);
						JOptionPane.showMessageDialog(null, "新增成功", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						AddRoleDialog.this.dispose();
					}
				}
			}
		});
	}

	/**
	 * 面板、位置初始化
	 */
	public void panelInit() {
		setTitle("添加角色");
		setMinimumSize(new Dimension(420, 380));
		pnlMain.add(lblRoleName);
		pnlMain.add(txtRoleName);
		pnlMain.add(warn);
		pnlMain.add(lblRolePy);
		pnlMain.add(txtRolePy);
		pnlMain.add(lblRoleLevel);
		pnlMain.add(btnCj);
		pnlMain.add(btnD);
		pnlMain.add(lblRoleNote);
		pnlMain.add(txtRoleNote);
		pnlMain.add(btnSave);
		pnlMain.add(btnReset);

		lblRoleName.setBounds(60, 35, 100, 25);
		txtRoleName.setBounds(120, 35, 100, 25);
		warn.setBounds(230, 35, 100, 25);
		lblRolePy.setBounds(60, 75, 100, 25);
		txtRolePy.setBounds(120, 75, 100, 25);
		lblRoleLevel.setBounds(60, 115, 100, 25);
		btnCj.setBounds(120, 115, 70, 25);
		btnD.setBounds(195, 115, 70, 25);
		lblRoleNote.setBounds(60, 155, 100, 25);
		txtRoleNote.setBounds(120, 155, 140, 60);
		btnSave.setBounds(60, 230, 70, 22);
		btnReset.setBounds(170, 230, 70, 22);

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
