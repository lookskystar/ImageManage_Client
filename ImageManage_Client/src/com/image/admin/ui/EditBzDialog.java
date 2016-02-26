package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.image.admin.service.UsersService;
import com.image.common.Params;
import com.image.common.pojo.DictTeams;
import com.image.common.pojo.DictUsers;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;

/**
 * 编辑班组
 * @author L
 *
 */
public class EditBzDialog extends JDialog {
	
	private static final long serialVersionUID = 135114794971821382L;
	
	JPanel pnlMain;
	JLabel lblBzName;
	JLabel lblDepart;
	JLabel warn;
	JTextField txtBzName;
	JComboBox Depart;
	JButton btnSave;
	JButton btnReset;
	DictUsers user=Params.sessionUser;
	DictTeams list;
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
	
	public EditBzDialog(DictTeams list){
		this.list=list;
		init();
		panelInit();
		buttonInit();
	}
	
	//初始化
	public void init(){
		
		pnlMain = new JPanel(null);
		lblBzName = new JLabel("班组名称:");
		lblDepart = new JLabel("班组所属:");
		String[] test = {"段级部门","所级部门"};
		txtBzName=new JTextField();
		Depart = new JComboBox(test);
		warn=new JLabel("*");
		warn.setForeground(Color.RED);
		btnSave = new JButton(ResourceUtils.getResourceByKey("JButton.commit_label"));
		CommonComponentUtils.setButtonStyle(btnSave,70,24,null);
		btnReset = new JButton(ResourceUtils.getResourceByKey("JButton.cancel_label"));
		CommonComponentUtils.setButtonStyle(btnReset,70,24,null);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		txtBzName.setText(list.getTeamName());
		if(list.getTeamLevel()==1){
			Depart.setSelectedItem("段级部门");
		}else{
			Depart.setSelectedItem("所级部门");
		}
	}
	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtBzName.setText("");
				txtBzName.grabFocus();
			}
		});
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				this.btnSave_actionPerformed(e);
			}

			private void btnSave_actionPerformed(ActionEvent e) {
				String teamName = txtBzName.getText().trim();
				if ("".equals(teamName)) {
					JOptionPane.showMessageDialog(null, "班组名称不能为空", "错误",
							JOptionPane.ERROR_MESSAGE);
				} else {
					DictTeams team = usersService.getDictTeamsByName(teamName,user.getAreaId());
					if (team != null&&!teamName.equals(list.getTeamName())) {
						JOptionPane.showMessageDialog(null, "修改失败,班组名称已经存在",
								"错误", JOptionPane.ERROR_MESSAGE);
					} else {
						list.setTeamName(teamName);
						if ("段级部门".equals(Depart.getSelectedItem())) {
							list.setTeamLevel(1);
						} else {
							list.setTeamLevel(2);
						}
						list.setAreaId(user.getAreaId());
						usersService.updateDictTeams(list);
						JOptionPane.showMessageDialog(null, "修改成功", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						EditBzDialog.this.dispose();
					}
				}
			}
		});
	}
	
	/**
	 * 面板、位置初始化
	 */
	public void panelInit() {
		setTitle("修改班组");
		setMinimumSize(new Dimension(400, 320));
		pnlMain.add(lblBzName);
		pnlMain.add(lblDepart);
		pnlMain.add(txtBzName);
		pnlMain.add(Depart);
		pnlMain.add(warn);
		pnlMain.add(btnSave);
		pnlMain.add(btnReset);

		lblBzName.setBounds(90, 35, 100, 25);
		txtBzName.setBounds(150, 35, 100, 25);
		warn.setBounds(260, 35, 100, 25);
		lblDepart.setBounds(90, 75, 100, 25);
		Depart.setBounds(150, 75, 100, 25);
		btnSave.setBounds(90, 120, 70, 22);
		btnReset.setBounds(200, 120, 70, 22);

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
