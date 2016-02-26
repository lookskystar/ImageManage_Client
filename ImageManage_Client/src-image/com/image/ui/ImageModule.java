package com.image.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.eltima.components.ui.DatePicker;
import com.image.common.BasicModule;
import com.image.common.Params;
import com.image.common.pojo.DictUsers;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;
import com.image.query.service.QueryService;
import com.image.query.ui.QueryTreeModule;
import com.image.set.service.PresetImageService;

public class ImageModule extends BasicModule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5281602543096733194L;
	private JTable jtable;
	private HessianServiceFactory factory=HessianServiceFactory.getInstance();
	public static final SimpleDateFormat YMD_SDFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String[] columnNames={"编号","车型车号","随车/图片","随车/视频","地勤/图片","地勤/视频","质检/图片","质检/视频","值班技术/图片","值班技术/视频","异常","操作"};
	
	private MainSet mainSet;

	public MainSet getMainSet() {
		return mainSet;
	}

	public void setMainSet(MainSet mainSet) {
		this.mainSet = mainSet;
	}

	public ImageModule(MainSet mainSet) {
		super(mainSet);
		this.setMainSet(mainSet);
		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(createOperatePanel(),BorderLayout.NORTH);
		getModulePanel().add(createTablePanel(),BorderLayout.CENTER);
	}
	
	private JLabel jcTypeLabel;
	private JComboBox jcTypeJcbox;
	private JLabel jcNumLabel;
	private JTextField jcNumTxt;
	private JLabel date;
	private DatePicker datePicker;
	private JButton queryBtn;
	private JButton loadBtn;
	/**
	 * 创建操作按钮面板
	 * @return
	 */
	private JPanel createOperatePanel(){
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		jcTypeLabel=new JLabel(ResourceUtils.getResourceByKey("JLabel.jc_type"));
		jcTypeJcbox = new JComboBox(createJcTypeData());
		jcTypeJcbox.setPreferredSize(new Dimension(100,24));
		
		jcNumLabel=new JLabel(ResourceUtils.getResourceByKey("JLabel.jc_num"));
		jcNumTxt=new JTextField(15);
		
		date=new JLabel(ResourceUtils.getResourceByKey("JLabel.date"));
		datePicker = new DatePicker(null,"yyyy-MM-dd",null,new Dimension(100,24));

		queryBtn=new JButton(ResourceUtils.getResourceByKey("JButton.sreach_label"));
		CommonComponentUtils.setButtonStyle(queryBtn,100,24,"find2.gif");
		queryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable();
			}
		});
		
		loadBtn=new JButton(ResourceUtils.getResourceByKey("JButton.load_label"));
		CommonComponentUtils.setButtonStyle(loadBtn,100,24,"add.gif");
		
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog=new UploadDialog(){
					
					private static final long serialVersionUID = -6798214071766190327L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if(e.getID() == WindowEvent.WINDOW_CLOSING){
							refreshTable();
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		
		jpanel.add(jcTypeLabel);
		jpanel.add(jcTypeJcbox);
		jpanel.add(jcNumLabel);
		jpanel.add(jcNumTxt);
		jpanel.add(date);
		jpanel.add(datePicker);
		jpanel.add(queryBtn);
		jpanel.add(loadBtn);
		return jpanel;
	}
	
	/**
	 * 创建表格面板
	 * @return
	 */
	private JScrollPane createTablePanel(){
		Object[][] rows=cteateTableData();
		jtable=CommonComponentUtils.createTable(columnNames, rows);
		CommonComponentUtils.setTableStyle(jtable, columnNames, 280, new MyButtonRenderer(), new MyButtonEditor());
		JScrollPane jscrollPane=new JScrollPane(jtable);
		return jscrollPane;
	}
	
	/**
	 * 刷新表格数据
	 */
	private void refreshTable(){
		Object[][] rows=cteateTableData();
		CommonComponentUtils.refreshTable(jtable, columnNames, rows);
		CommonComponentUtils.setTableStyle(jtable, columnNames, 280, new MyButtonRenderer(), new MyButtonEditor());
	}
	
	/**
	 * 获得机车类型数据
	 * @return
	 */
	private Object[] createJcTypeData(){
		try {
			PresetImageService presetImageService=(PresetImageService)factory.createService(PresetImageService.class, "presetImageService");
			List<String> jcTypeList = presetImageService.findJcstypeAll();
			jcTypeList.add(0, "-请选择-");
			return jcTypeList.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获得表格数据
	 * @return
	 */
	private Object[][] cteateTableData(){
		DictUsers user=Params.sessionUser;
		String jcType=(String)jcTypeJcbox.getSelectedItem();
		if(jcType.contains("请选择")){
			jcType=null;
		}
		String jcNum="".equals(jcNumTxt.getText())?null:jcNumTxt.getText();
		String taskDate="".equals(datePicker.getText())?null:datePicker.getText();
		if(taskDate==null || taskDate==""){
			taskDate = YMD_SDFORMAT.format(new Date());
		}
		String areaId=user.getAreaId()+"";
		try {
			QueryService queryService=(QueryService)factory.createService(QueryService.class, "queryService");
			//List<DictWorktype> workTypeList = queryService.findAllDictWorktype();
			List<Map<String, String>> uploadRecList = queryService.findAllUploadRecOnJc(jcType, jcNum, taskDate, areaId, null);
			if(uploadRecList!=null&&uploadRecList.size()>0){
				Object[][] datas=new Object[uploadRecList.size()][];
				String[] data=null;
				for(int i=0;i<uploadRecList.size();i++){
					Map<String,String> map=(Map<String,String>)uploadRecList.get(i);
					data=new String[map.size()];
					data[0]=map.get("jcRecId");
					data[1]=map.get("jcType")+"-"+map.get("jcNum");
					data[2]=map.get("onePic");
					data[3]=map.get("oneVid");
					data[4]=map.get("twoPic");
					data[5]=map.get("twoVid");
					data[6]=map.get("thrPic");
					data[7]=map.get("thrVid");
					data[8]=map.get("fourPic");
					data[9]=map.get("fourVid");
					if("0".equals(map.get("status"))){
						data[10]="正常";
					}else{
						data[10]="异常";
					}
					data[11]=null;
					datas[i]=data;
				}
				return datas;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 为表格最后一行添加操作按钮
	 * @author dell
	 *
	 */
	final class MyButtonRenderer implements TableCellRenderer{

		private JPanel jpanel;

		public MyButtonRenderer(){
			jpanel=initOperate();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return jpanel;
		}
	}
	
	/**
	 * 设置表格最后一行按钮可操作
	 * @author dell
	 *
	 */
	final class MyButtonEditor extends AbstractCellEditor implements TableCellEditor{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3257877651073991549L;
		private JPanel jpanel;
		
		public MyButtonEditor(){
			jpanel=initOperate();
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return jpanel;
		}
		
		//获取button的text，没有该方法，将会显示false
		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}
	
	/**
	 * 初始化功能按钮操作
	 * @return
	 */
	private JPanel initOperate(){
		JPanel jpanel=new JPanel();
		JButton detailBtn=new JButton("查看详情");
		detailBtn.setName("detail");
		CommonComponentUtils.setButtonStyle(detailBtn,70,20,null);
		detailBtn.addActionListener(new ButtonOnclickListener());
		
		JButton playerBtn=new JButton("视频播放");
		playerBtn.setName("player");
		CommonComponentUtils.setButtonStyle(playerBtn,70,20,null);
		playerBtn.addActionListener(new ButtonOnclickListener());
		
		JButton reportBtn=new JButton("系统报告");
		reportBtn.setName("report");
		CommonComponentUtils.setButtonStyle(reportBtn,70,20,null);
		reportBtn.addActionListener(new ButtonOnclickListener());
		
		JButton addressBtn=new JButton("影像地址");
		addressBtn.setName("address");
		CommonComponentUtils.setButtonStyle(addressBtn,70,20,null);
		addressBtn.addActionListener(new ButtonOnclickListener());
		
		jpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpanel.add(detailBtn);
		jpanel.add(playerBtn);
		jpanel.add(reportBtn);
		jpanel.add(addressBtn);
		return jpanel;
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
			int id=Integer.parseInt(jtable.getValueAt(row, 0)+"");
			Map<String,String> params=new HashMap<String,String>();
			String jcRecId = (String) jtable.getValueAt(row, 0);
			String jcTypeNum = (String) jtable.getValueAt(row, 1);
			params.put("jcRecId", jcRecId);
			params.put("jcTypeNum", jcTypeNum);
			if("detail".equals(button.getName())){
				mainSet.setModule(new QueryTreeModule(mainSet, params));
			}else if("player".equals(button.getName())){
				System.out.println("--视频播放操作---"+id);
			}else if("report".equals(button.getName())){
				System.out.println("--系统报告操作---"+id);
			}else if("address".equals(button.getName())){
				System.out.println("--影像地址操作---"+id);
			}
		}
	}
	
	
	
	@Override
	public void refresh() {
		
	}

}
