package com.image.set.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.image.admin.service.UsersService;
import com.image.common.BasicModule;
import com.image.common.Params;
import com.image.common.pojo.DevicesInfo;
import com.image.common.pojo.DictTeams;
import com.image.common.pojo.DictUsers;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.Pager;
import com.image.common.util.PagerHelper;
import com.image.common.util.ResourceUtils;
import com.image.work.service.DeviceService;
/**
 * 设备管理
 * @author Administrator
 *
 */
public class DeviceModule extends BasicModule {
	private static final long serialVersionUID = -7451390005180893409L;
	
	private JLabel equipState;
	private JComboBox stateText;
	private JLabel bzName;
	private JComboBox bzText;
	private JLabel getMan;
	private JTextField choiceGetMan;
	private JLabel equipCode;
	private JTextField code;
	private JButton searchEquip;
	private JButton equipEnter;
	private JLabel functionLabel;
	private JButton receiveBtn;
	private JButton returnBtn;
	private JButton backBtn;
	private JButton sendBtn;
	private JButton badBtn;
	private JButton recordBtn;
	private JTable jtable;
	private JButton pageUpBtn;//上一页
	private JButton pageDownBtn; //下一页
	private JButton firstBtn;//首页
	private JButton lastBtn;//末页 
	private int totalRows;//总记录数
	private int totalPages;//总页数
	private int currentPage=1;//当前页
	private JLabel totalLabel;//总页数显示
	private JLabel currentPageLabel;//当前页显示Label
	private JLabel totalPageLabel;//共页显示Label
	
	private static DeviceService deviceService;
	private static UsersService usersService;
	private static final String[] columnNames={"序号","型号", "编码", "设备状态" ,"班组" ,"领取人","领取时间","归还人","归还时间","备注"};
	
	static{
		HessianServiceFactory factory=HessianServiceFactory.getInstance();
		try {
			deviceService=(DeviceService)factory.createService(DeviceService.class, "deviceService");
			usersService = (UsersService) factory.createService(UsersService.class, "usersService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化控件
	 * @param mainSet
	 */
	public DeviceModule(MainSet mainSet) {
		super(mainSet);
		//分页标签初始化
		currentPageLabel=new JLabel();
		currentPageLabel.setForeground(Color.RED);
		totalLabel=new JLabel();
		totalLabel.setForeground(Color.RED);
		totalPageLabel=new JLabel();
		totalPageLabel.setForeground(Color.RED);
		
		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(createUpPanel(),BorderLayout.NORTH);
		getModulePanel().add(createTablePanel(),BorderLayout.CENTER);
		getModulePanel().add(createDownPane(),BorderLayout.SOUTH);
	}
	
	/**
	 * 创建操作面板
	 * @return
	 */
	private JPanel createUpPanel(){
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		String[] state = {"-请选择-","库存","使用","维修","报废"};
		equipState = new JLabel("状态：");
		stateText = new JComboBox(state);
		Object[] teams=createTeamData();
		bzName = new JLabel("班组：");
		bzText = new JComboBox(teams);
		getMan = new JLabel("领取人：");
		choiceGetMan = new JTextField(15);
		equipCode = new JLabel("设备编码：");
		code = new JTextField(15);
		searchEquip = new JButton(ResourceUtils.getResourceByKey("JButton.sreach_label"));
		CommonComponentUtils.setButtonStyle(searchEquip,100,24,"find2.gif");
		searchEquip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("first");
			}
		});
		
		equipEnter = new JButton(ResourceUtils.getResourceByKey("Device.equipEnter_label"));
		CommonComponentUtils.setButtonStyle(equipEnter,100,24,"add.gif");
		equipEnter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog=new AddDeviceDialog(){
					private static final long serialVersionUID = -6423327621189290644L;

					@Override
					protected void processWindowEvent(WindowEvent e) {
						if(e.getID()==WindowEvent.WINDOW_CLOSED){
							refreshTable("first");
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		
		functionLabel=new JLabel("\b\b\b功能操作:");
		receiveBtn=new JButton("领取");
		receiveBtn.setName("receive");
		receiveBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(receiveBtn,80,24,"home.gif");
		
		sendBtn=new JButton("送修");
		sendBtn.setName("send");
		sendBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(sendBtn,80,24,"reply.gif");
		
		returnBtn=new JButton("归还");
		returnBtn.setName("return");
		returnBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(returnBtn,80,24,"arr1.gif");
		
		backBtn=new JButton("修后返回");
		backBtn.setName("back");
		backBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(backBtn,100,24,"back.gif");
		
		badBtn=new JButton("报废");
		badBtn.setName("bad");
		badBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(badBtn,80,24,"recycle.gif");
		
		recordBtn=new JButton("履历");
		recordBtn.setName("record");
		recordBtn.addActionListener(new ButtonOnclickListener());
		CommonComponentUtils.setButtonStyle(recordBtn,80,24,"page.gif");
		
		setButtonNoEnable();
		
		jpanel.add(equipState);
		jpanel.add(stateText);
		jpanel.add(bzName);
		jpanel.add(bzText);
		jpanel.add(getMan);
		jpanel.add(choiceGetMan);
		jpanel.add(equipCode);
		jpanel.add(code);
		jpanel.add(searchEquip);
		jpanel.add(equipEnter);
		jpanel.add(functionLabel);
		jpanel.add(receiveBtn);
		jpanel.add(sendBtn);
		jpanel.add(returnBtn);
		jpanel.add(backBtn);
		jpanel.add(badBtn);
		jpanel.add(recordBtn);
		return jpanel;
		
		
	}
	
	/**
	 * 创建翻页面板
	 * @return
	 */
	private JPanel createDownPane(){
		JPanel jpan = new JPanel();
		jpan.setLayout(new FlowLayout(FlowLayout.RIGHT));
		firstBtn=new JButton(ResourceUtils.getResourceByKey("JButton.pageFirst_label"));
		CommonComponentUtils.setButtonStyle(firstBtn,80,24,"pagination_first.gif");
		firstBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("first");
			}
		});
		
		pageUpBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageUp_label"));
		CommonComponentUtils.setButtonStyle(pageUpBtn,80,24,"pagination_prev.gif");
		pageUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("previous");
			}
		});
		
		pageDownBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageDown_label"));
		CommonComponentUtils.setButtonStyle(pageDownBtn,80,24,"pagination_next.gif");
		pageDownBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("next");
			}
		});
		
		lastBtn=new JButton(ResourceUtils.getResourceByKey("JButton.pageLast_label"));
		CommonComponentUtils.setButtonStyle(lastBtn,80,24,"pagination_last.gif");
		lastBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("last");
			}
		});
		jpan.add(new JLabel("总共"));
		jpan.add(totalLabel);
		jpan.add(new JLabel("条记录，每页显示10条记录，当前页:第"));
		jpan.add(currentPageLabel);
		jpan.add(new JLabel("页,共"));
		jpan.add(totalPageLabel);
		jpan.add(new JLabel("页\b\b\b\b\b\b"));
		jpan.add(firstBtn);
		jpan.add(pageUpBtn);
		jpan.add(pageDownBtn);
		jpan.add(lastBtn);
		return jpan;
	}
	
	/**
	 * 创建表格面板
	 * @return
	 */
	private JScrollPane createTablePanel(){
		Object[][] rows=cteateTableData("first");
		
		jtable=CommonComponentUtils.createTable(columnNames, rows);
		JScrollPane jscrollPane=new JScrollPane(jtable);
		jtable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setButtonEable();
			}
		});
		return jscrollPane;
	}
	
	/**
	 * 设置按钮状态
	 */
	private void setButtonEable(){
		int row=jtable.getSelectedRow();
		String status=(String)jtable.getValueAt(row, 3);
		if("库存".equals(status)){
			receiveBtn.setEnabled(true);
			sendBtn.setEnabled(true);
			returnBtn.setEnabled(false);
			backBtn.setEnabled(false);
			badBtn.setEnabled(true);
			recordBtn.setEnabled(true);
		}else if("使用".equals(status)){
			receiveBtn.setEnabled(false);
			sendBtn.setEnabled(false);
			returnBtn.setEnabled(true);
			backBtn.setEnabled(false);
			badBtn.setEnabled(true);
			recordBtn.setEnabled(true);
		}else if("维修".equals(status)){
			receiveBtn.setEnabled(false);
			sendBtn.setEnabled(false);
			returnBtn.setEnabled(false);
			backBtn.setEnabled(true);
			badBtn.setEnabled(true);
			recordBtn.setEnabled(true);
		}else if("报废".equals(status)){
			receiveBtn.setEnabled(false);
			sendBtn.setEnabled(false);
			returnBtn.setEnabled(false);
			backBtn.setEnabled(false);
			badBtn.setEnabled(false);
			recordBtn.setEnabled(true);
		}
	}
	
	/**
	 * 设置按钮不能使用
	 */
	private void setButtonNoEnable(){
		receiveBtn.setEnabled(false);
		sendBtn.setEnabled(false);
		returnBtn.setEnabled(false);
		backBtn.setEnabled(false);
		badBtn.setEnabled(false);
		recordBtn.setEnabled(false);
	}
	
	/**
	 * 创建班组数据
	 * @return
	 */
	private Object[] createTeamData(){
		DictUsers user=Params.sessionUser;
		long areaId=(user==null?0:user.getAreaId());
		List<DictTeams> teams=usersService.listDictTeams(areaId);
		List<String> datas=new ArrayList<String>();
		datas.add("-请选择-");
		if(teams!=null&&teams.size()>0){
			for(DictTeams team:teams){
				datas.add(team.getTeamName());
			}
			return datas.toArray();
		}
		return null;
	}
	
	/**
	 * 创建表格数据
	 * @return
	 */
	private Object[][] cteateTableData(String pageMethod){
		DictUsers user=Params.sessionUser;
		long areaId=(user==null?0:user.getAreaId());
		Integer deviceStatus=null;
		Long teamId=null;
		if(stateText.getSelectedIndex()!=0){
			deviceStatus=stateText.getSelectedIndex()-1;
		}
		List<DictTeams> teams=usersService.listDictTeams(areaId);
		if(teams!=null&&teams.size()>0){
			for(DictTeams team:teams){
				if(team.getTeamName().equals(bzText.getSelectedItem().toString())){
					teamId=team.getTeamId();
					break;
				}
			}
		}
		String receiverName="".equals(choiceGetMan.getText())?null:choiceGetMan.getText();
		String deviceCode="".equals(code.getText())?null:code.getText();
		
		//分页
		totalRows=deviceService.getDevicesInfoCount(deviceStatus, teamId, receiverName, deviceCode,user.getAreaId());//获得总数据数
		Pager pager=PagerHelper.getPager(currentPage+"", pageMethod, totalRows);
		currentPage=pager.getCurrentPage();
		totalPages=pager.getTotalPages();
		totalLabel.setText(""+totalRows+"");
		currentPageLabel.setText(""+currentPage+"");
		totalPageLabel.setText(""+totalPages+"");
		List<DevicesInfo> deviceInfos=deviceService.findDevicesInfoPage(pager.getPageSize(), pager.getStartRow(), deviceStatus, teamId, receiverName, deviceCode,user.getAreaId());
		
		Object[][] datas=null;
		if(deviceInfos!=null&&deviceInfos.size()>0){
			datas=new Object[deviceInfos.size()][];
			List<Object> list=null;
			for(int i=0;i<deviceInfos.size();i++){
				DevicesInfo deviceInfo=deviceInfos.get(i);
				list=new ArrayList<Object>();
				list.add(deviceInfo.getDevicesId());
				list.add(deviceInfo.getDevicesType());
				list.add(deviceInfo.getDeviceCode());
				if(deviceInfo.getDeviceStatus()==0){
					list.add("库存");
				}else if(deviceInfo.getDeviceStatus()==1){
					list.add("使用");
				}else if(deviceInfo.getDeviceStatus()==2){
					list.add("维修");
				}else if(deviceInfo.getDeviceStatus()==3){
					list.add("报废");
				}
				list.add(deviceInfo.getTeamName());
				list.add(deviceInfo.getReceiverName());
				list.add(deviceInfo.getReceiverTime());
				list.add(deviceInfo.getReturnName());
				list.add(deviceInfo.getReturnTime());
				list.add(deviceInfo.getDevicesNote());
				list.add(null);
				datas[i]=list.toArray();
			}
		}
		return datas;
	}
	
	/**
	 * 刷新表格数据
	 */
	private void refreshTable(String pageMethod){
		setButtonNoEnable();
		Object[][] rows=cteateTableData(pageMethod);
		CommonComponentUtils.refreshTable(jtable, columnNames, rows);
	}
	
	/**
	 * 监听按钮操作事件
	 * @author dell
	 *
	 */
	final class ButtonOnclickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button=(JButton)e.getSource();
			int row=jtable.getSelectedRow();
			//获得ID
			Long devicesId=Long.parseLong(jtable.getValueAt(row, 0)+"");
			if("receive".equals(button.getName())){
				JDialog dialog=new ReceiveDeviceDialog(devicesId){
					private static final long serialVersionUID = -6423327621189290644L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if(e.getID()==WindowEvent.WINDOW_CLOSED){
							refreshTable("first");
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}else if("send".equals(button.getName())){
				int msg = JOptionPane.showConfirmDialog(null,
						"确认送去维修？", "提示",
						JOptionPane.YES_NO_OPTION);
				if(msg==0){
					String result=deviceService.repairDevicesInfo(devicesId+"");
					if("success".equals(result)){
						JOptionPane.showMessageDialog(null, "设备送修成功!", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						refreshTable("first");
					}
				}
			}else if("return".equals(button.getName())){
				int msg = JOptionPane.showConfirmDialog(null,
						"归还前,请确认手电影像文件已经上传!", "提示",
						JOptionPane.YES_NO_OPTION);
				if(msg==0){
					JDialog dialog=new ReturnDeviceDialog(devicesId){
						private static final long serialVersionUID = -6423327621189290644L;
						@Override
						protected void processWindowEvent(WindowEvent e) {
							if(e.getID()==WindowEvent.WINDOW_CLOSED){
								refreshTable("first");
							}
							super.processWindowEvent(e);
						}
					};
					dialog.pack();
					dialog.setVisible(true);
				}
			}else if("back".equals(button.getName())){
				int msg = JOptionPane.showConfirmDialog(null,
						"确认返修回来入库吗？", "提示",
						JOptionPane.YES_NO_OPTION);
				if(msg==0){
					String result=deviceService.backDevicesInfo(devicesId+"");
					if("success".equals(result)){
						JOptionPane.showMessageDialog(null, "设备入库成功!", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						refreshTable("first");
					}
				}
			}else if("bad".equals(button.getName())){
				int msg = JOptionPane.showConfirmDialog(null,
						"确认设备报废吗？", "提示",
						JOptionPane.YES_NO_OPTION);
				if(msg==0){
					String result=deviceService.discardDevicesInfo(devicesId+"");
					if("success".equals(result)){
						JOptionPane.showMessageDialog(null, "设备报废成功!", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						refreshTable("first");
					}
				}
			}else if("record".equals(button.getName())){
				JDialog dialog=new RecordDeviceDialog(devicesId){
					private static final long serialVersionUID = -6423327621189290644L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if(e.getID()==WindowEvent.WINDOW_CLOSING){
							refreshTable("first");
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}
		}
	}
		
	@Override
	public void refresh() {
		System.out.println("刷新了设备管理");
	}
}
