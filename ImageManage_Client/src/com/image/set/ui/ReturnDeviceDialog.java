package com.image.set.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eltima.components.ui.DatePicker;
import com.image.admin.service.UsersService;
import com.image.common.Params;
import com.image.common.pojo.DevicesInfo;
import com.image.common.pojo.DictTeams;
import com.image.common.pojo.DictUsers;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.work.service.DeviceService;

/**
 * 设备归还
 * @author dell
 *
 */
public class ReturnDeviceDialog extends JDialog{

	private static final long serialVersionUID = -5388413186499612597L;
	private JLabel proteamLabel;
	private JComboBox proteamCombox;
	private JLabel receiverLabel;
	private JComboBox receiverCombox;
	private JLabel receiverTimeLabel;
	private DatePicker receiveTimePicker;
	private JLabel noteLabel;
	private JTextField noteTxt;
	private JButton saveBtn;
	private JButton resetBtn;
	private JLabel msgLabel;
	private static DeviceService deviceService;
	private static UsersService usersService;
	private static DictUsers user;
	private Long devicesId;//设备ID
	private Long teamId;//班组ID
	static{
		HessianServiceFactory factory=HessianServiceFactory.getInstance();
		try {
			deviceService=(DeviceService)factory.createService(DeviceService.class, "deviceService");
			usersService = (UsersService) factory.createService(UsersService.class, "usersService");
			user=Params.sessionUser;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 */
	public ReturnDeviceDialog(Long devicesId){
		this.devicesId=devicesId;
		JPanel panel=createPanel();
		add(panel);
		setTitle("设备归还");
		setResizable(false);//设置不能自己拖动窗口大小
		setMinimumSize(new Dimension(380, 330));
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
	
	/**
	 * 创建内容面板
	 * @return
	 */
	private JPanel createPanel(){
		JPanel panel=new JPanel(null);
		proteamLabel=new JLabel("班组:");
		proteamCombox=new JComboBox();
		receiverLabel=new JLabel("领取人:");
		receiverCombox=new JComboBox();
		
		createTeamData();
		proteamCombox.addItemListener(new ItemListener() {
			@SuppressWarnings("static-access")
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==e.SELECTED){
					if(proteamCombox.getSelectedIndex()!=0){
						String itemName=e.getItem()+"";
						createTeamPerson(itemName);
					}else{
						createTeamPerson(null);
					}
				}
			}
		});
		
//		receiverLabel=new JLabel("领取人:");
//		receiverCombox=new JComboBox();
//		createTeamPerson(null);
		
		receiverTimeLabel=new JLabel("领取时间:");
		receiveTimePicker = new DatePicker(new Date(),"yyyy-MM-dd HH:mm",null,new Dimension(130,24));

		noteLabel=new JLabel("备注:");
		noteTxt=new JTextField(20);
		
		msgLabel=new JLabel();
		msgLabel.setForeground(Color.RED);
		
		saveBtn=new JButton("提交");
		CommonComponentUtils.setButtonStyle(saveBtn, 100, 24, "ok.gif");
		saveBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				returnDevice();
			}
		});
		resetBtn=new JButton("重置");
		CommonComponentUtils.setButtonStyle(resetBtn, 100, 24, "refresh.gif");
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createTeamData();
				noteTxt.setText("");
			}
		});

		proteamLabel.setBounds(90, 35, 100, 25);
		proteamCombox.setBounds(150, 35, 120, 25);
		receiverLabel.setBounds(90, 75, 100, 25);
		receiverCombox.setBounds(150, 75, 120, 25);
		receiverTimeLabel.setBounds(90, 115, 100, 25);
		receiveTimePicker.setBounds(150, 115, 130, 25);
		noteLabel.setBounds(90, 155, 100, 25);
		noteTxt.setBounds(150, 155, 120, 25);
		msgLabel.setBounds(150, 185, 150, 25);
		saveBtn.setBounds(75, 210, 100, 24);
		resetBtn.setBounds(205, 210, 100, 24);
		
		panel.add(proteamLabel);
		panel.add(proteamCombox);
		panel.add(receiverLabel);
		panel.add(receiverCombox);
		panel.add(receiverTimeLabel);
		panel.add(receiveTimePicker);
		panel.add(noteLabel);
		panel.add(noteTxt);
		panel.add(msgLabel);
		panel.add(saveBtn);
		panel.add(resetBtn);
		return panel;
	}
	
	/**
	 * 创建班组数据
	 * @return
	 */
	private void createTeamData(){
		proteamCombox.removeAllItems();
		long areaId=(user==null?0:user.getAreaId());
		List<DictTeams> teams=usersService.listDictTeams(areaId);
		proteamCombox.addItem("请选择班组");
		if(teams!=null&&teams.size()>0){
			for(DictTeams team:teams){
				proteamCombox.addItem(team.getTeamName());
			}
		}
		DevicesInfo device = deviceService.findDevicesInfoById(devicesId);
		proteamCombox.setSelectedItem(device.getTeamName());
		createTeamPerson(device.getTeamName());
	}
	
	/**
	 * 创建班组下领取人信息
	 * @param itemName
	 * @return
	 */
	private void createTeamPerson(String itemName){
		receiverCombox.removeAllItems();
		receiverCombox.addItem("请选择领取人");
		if(itemName!=null&&!"".equals(itemName)){
			long areaId=(user==null?0:user.getAreaId());
			List<DictTeams> teams=usersService.listDictTeams(areaId);
			if(teams!=null&&teams.size()>0){
				for(DictTeams team:teams){
					if(team.getTeamName().equals(itemName)){
						teamId=team.getTeamId();
						break;
					}
				}
			}
			List<DictUsers> users=deviceService.findUsersByTeamId(teamId);
			for(DictUsers user:users){
				receiverCombox.addItem(user.getName());
			}
		}
	}
	
	/**
	 * 保存设备归还信息
	 */
	public void returnDevice(){
		if(proteamCombox.getSelectedIndex()==0){
			msgLabel.setText("*请选择班组");
		}else if(receiverCombox.getSelectedIndex()==0){
			msgLabel.setText("*请选择领取人");
		}else{
			msgLabel.setText("");
			DevicesInfo device = deviceService.findDevicesInfoById(devicesId);
			String teamName=proteamCombox.getSelectedItem().toString();
			String returnName=receiverCombox.getSelectedItem().toString();
			String returnTime=receiveTimePicker.getText();
			String deviceNote=noteTxt.getText();
			
			device.setDeviceStatus(0);// 状态设为0(库存)
			device.setReturnName(returnName);
			device.setReturnTime(returnTime);
			device.setDevicesNote(deviceNote);
			device.setTeamId(teamId);
			device.setTeamName(teamName);
			device.setReceiverId(null);
			device.setReceiverName(null);
			device.setReceiverTime(null);//设置领取Id,领取人,领取时间为空
			deviceService.updateReturnDevicesInfo(device);
			
			
			
			JOptionPane.showMessageDialog(this, "设备归还成功", "信息",
					JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
	}
}
