package com.image.set.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.image.common.Params;
import com.image.common.pojo.DevicesInfo;
import com.image.common.pojo.DictAreas;
import com.image.common.pojo.DictUsers;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.work.service.DeviceService;

public class AddDeviceDialog extends JDialog{

	private static final long serialVersionUID = -5388413186499612597L;
	private JLabel areaLabel;
	private JComboBox areaCombox;
	private JLabel deviceTypeLabel;
	private JTextField deviceTypeTxt;
	private JLabel deviceCodeLabel;
	private JTextField deviceCodeTxt;
	private JLabel noteLabel;
	private JTextField noteTxt;
	private JButton saveBtn;
	private JButton resetBtn;
	private JLabel msgLabel;
	private static DeviceService deviceService;
	static{
		HessianServiceFactory factory=HessianServiceFactory.getInstance();
		try {
			deviceService=(DeviceService)factory.createService(DeviceService.class, "deviceService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 */
	public AddDeviceDialog(){
		JPanel panel=createPanel();
		add(panel);
		setTitle("设备入库");
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
		areaLabel=new JLabel("地区:");
		Object[] datas=createAreaData();
		areaCombox=new JComboBox(datas);
		
		deviceTypeLabel=new JLabel("设备型号:");
		deviceTypeTxt=new JTextField(20);
		deviceTypeTxt.setText("XR-11plf");
		
		deviceCodeLabel=new JLabel("设备编码:");
		deviceCodeTxt=new JTextField(20);
		
		noteLabel=new JLabel("备注:");
		noteTxt=new JTextField(20);
		
		msgLabel=new JLabel();
		msgLabel.setForeground(Color.RED);
		
		saveBtn=new JButton("提交");
		CommonComponentUtils.setButtonStyle(saveBtn, 100, 24, "ok.gif");
		saveBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDevice();
			}
		});
		resetBtn=new JButton("重置");
		CommonComponentUtils.setButtonStyle(resetBtn, 100, 24, "refresh.gif");
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deviceCodeTxt.setText("");
				noteTxt.setText("");
			}
		});

		areaLabel.setBounds(90, 35, 100, 25);
		areaCombox.setBounds(150, 35, 120, 25);
		deviceTypeLabel.setBounds(90, 75, 100, 25);
		deviceTypeTxt.setBounds(150, 75, 120, 25);
		deviceCodeLabel.setBounds(90, 115, 100, 25);
		deviceCodeTxt.setBounds(150, 115, 120, 25);
		noteLabel.setBounds(90, 155, 100, 25);
		noteTxt.setBounds(150, 155, 120, 25);
		msgLabel.setBounds(150, 185, 150, 25);
		saveBtn.setBounds(75, 210, 100, 24);
		resetBtn.setBounds(205, 210, 100, 24);
		
		panel.add(areaLabel);
		panel.add(areaCombox);
		panel.add(deviceTypeLabel);
		panel.add(deviceTypeTxt);
		panel.add(deviceCodeLabel);
		panel.add(deviceCodeTxt);
		panel.add(noteLabel);
		panel.add(noteTxt);
		panel.add(msgLabel);
		panel.add(saveBtn);
		panel.add(resetBtn);
		return panel;
	}
	
	/**
	 * 创建地区数据
	 * @return
	 */
	private Object[] createAreaData(){
		DictUsers user=Params.sessionUser;
		long areaId=(user==null?0:user.getAreaId());
		List<DictAreas> areas=deviceService.listDictAreas();
		List<Object> list=new ArrayList<Object>();
		for(DictAreas area:areas){
			if(areaId==area.getAreaId()){
				list.set(0, area.getAreaName());
			}else{
				list.add(area.getAreaName());
			}
		}
		return list.toArray();
	}
	
	/**
	 * 保存设备信息
	 */
	public void saveDevice(){
		String areaMsg=areaCombox.getSelectedItem().toString();
		String deviceType=deviceTypeTxt.getText();
		String deviceCode=deviceCodeTxt.getText();
		String deviceNote=noteTxt.getText();
		Long areaId=null;
		
		if("".equals(deviceType)){
			msgLabel.setText("*设备型号不能为空");
		}else if("".equals(deviceCode)){
			msgLabel.setText("*设备编码不能为空");
		}else{
			DevicesInfo devicesInfo=deviceService.findDevicesInfoByTypeCode(deviceType, deviceCode);
			if(devicesInfo!=null){
				msgLabel.setText("*该设备编码已存在");
			}else{
				msgLabel.setText("");
				List<DictAreas> areas=deviceService.listDictAreas();
				for(DictAreas area:areas){
					if(areaMsg.equals(area.getAreaName())){
						areaId=area.getAreaId();
						break;
					}
				}
				devicesInfo=new DevicesInfo();
				devicesInfo.setAreaId(areaId);
				devicesInfo.setDevicesType(deviceType);
				devicesInfo.setDeviceCode(deviceCode);
				devicesInfo.setDevicesNote(deviceNote);
				devicesInfo.setDeviceStatus(0);
				devicesInfo.setIsDelete(0);
				deviceService.saveDevicesInfo(devicesInfo);
				JOptionPane.showMessageDialog(this, "设备信息添加成功", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
			}
		}
	}

}
