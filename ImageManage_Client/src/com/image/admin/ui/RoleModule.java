package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.image.admin.service.RolesService;
import com.image.common.BasicModule;
import com.image.common.pojo.DictRoles;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;
/**
 * 角色管理模块
 * @author L
 */
public class RoleModule extends BasicModule {

	private static final long serialVersionUID = -6789096743618961884L;
	
	private JButton btnAdd;
	private JTable tableView;
	private JPanel pnlMainN = new JPanel();
	private JPanel pnlMainC = new JPanel();
	JScrollPane scpTable;
	DefaultTableModel dtm;
	private static final String[] columnNames={"角色ID", "角色名称", "角色拼音" ,"角色等级" ,"角色说明" ,"操作"};
	private MainSet mainSet;
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
	@SuppressWarnings("serial")
	public RoleModule(MainSet mainSet) {
		super(mainSet);
		this.setMainSet(mainSet);
		
		btnAdd = new JButton(ResourceUtils.getResourceByKey("JButton.add_label"));
		CommonComponentUtils.setButtonStyle(btnAdd,70,24,"add.gif");
		scpTable = new JScrollPane();
		tableView = new JTable(){
  		  public boolean isCellEditable(int row,int column){
  			if(column==columnNames.length-1){
				return true;
			}
  			return false;
  		  }
  	    };
		init();
		
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		tableInit();
		panelInit();
		buttonInit();
		
	}
	
	/**
	 * 表格控件初始化
	 */
	private void tableInit() {
		
		dtm = new DefaultTableModel(new Object[][]{},columnNames);
		List<DictRoles> list=rolesService.listDictRoles();
		
		Vector<Object> v = null;
		if(list!= null){
			for(DictRoles listRole:rolesService.listDictRoles()){
				v = new Vector<Object>();
				v.add(listRole.getRoleId());
				v.add(listRole.getRoleName());
				v.add(listRole.getRolePy());
			    v.add(listRole.getRoleLevel().equals(0) ? "车间级":"段级");
				v.add(listRole.getRoleNote());
				v.add(null);
				dtm.addRow(v);
				
			}
		}
		
		
		tableView.setModel(dtm);
		//设置表格列宽度
		tableView.getColumn("角色ID").setMinWidth(50);
		tableView.getColumn("角色名称").setMinWidth(100);
		tableView.getColumn("角色拼音").setMinWidth(100);
		tableView.getColumn("角色等级").setMinWidth(100);
		tableView.getColumn("角色说明").setMinWidth(150);
		tableView.getColumn("操作").setMinWidth(270);
		
		tableView.getColumnModel().getColumn(columnNames.length-1).setPreferredWidth(280);
		tableView.getColumnModel().getColumn(columnNames.length-1).setCellRenderer(new MyButtonRenderer());
		tableView.getColumnModel().getColumn(columnNames.length-1).setCellEditor(new MyButtonEditor());
		
		//设置表格列名居中
		JTableHeader head=tableView.getTableHeader();
		DefaultTableCellRenderer hr=(DefaultTableCellRenderer)head.getDefaultRenderer();
		hr.setHorizontalAlignment(SwingConstants.CENTER);
		//设置表格内容居中
		DefaultTableCellRenderer tcr=new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		tableView.setDefaultRenderer(Object.class, tcr);
		//设置列标题不能移动   
		tableView.getTableHeader().setReorderingAllowed(false);   
		tableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   
		//排序
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(dtm);  
		tableView.setRowSorter(sorter); 
		
	}
	/**
	 * 面板控件初始化
	 */
	private void panelInit() {
		
		tableView.setPreferredScrollableViewportSize(new Dimension(550, 30));
		pnlMainN.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlMainN.add(btnAdd);
		
		pnlMainC.setLayout(new BorderLayout());
		pnlMainC.add(tableView.getTableHeader(),BorderLayout.NORTH);
		
		pnlMainC.add(scpTable,BorderLayout.CENTER);
		scpTable.getViewport().add(tableView);
		
		
		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(pnlMainN,BorderLayout.NORTH);
		getModulePanel().add(pnlMainC,BorderLayout.CENTER);
	}

	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new AddRoleDialog() {
					private static final long serialVersionUID = -5150192344789248586L;

					@Override
					protected void processWindowEvent(WindowEvent e) {
						if (e.getID() == WindowEvent.WINDOW_CLOSED) {
							tableInit();
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}
		});

	}

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
		
		JButton editBtn=new JButton("编辑");
		editBtn.setName("edit");
		CommonComponentUtils.setButtonStyle(editBtn,70,20,null);
		editBtn.addActionListener(new ButtonOnclickListener());
		
		JButton deleteBtn=new JButton("删除");
		deleteBtn.setName("delete");
		CommonComponentUtils.setButtonStyle(deleteBtn,70,20,null);
		deleteBtn.addActionListener(new ButtonOnclickListener());
		
		JPanel jpanel=new JPanel();
		JButton empowerBtn=new JButton("授权");
		empowerBtn.setName("empower");
		CommonComponentUtils.setButtonStyle(empowerBtn,70,20,null);
		empowerBtn.addActionListener(new ButtonOnclickListener());
		
		jpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpanel.add(editBtn);
		jpanel.add(deleteBtn);
		jpanel.add(empowerBtn);
		return jpanel;
	}
	
	/**
	 * 监听按钮操作事件
	 *
	 */
	final class ButtonOnclickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button=(JButton)e.getSource();
			int row=tableView.getSelectedRow();
			//获得ID
			final Long roleId=Long.parseLong(tableView.getValueAt(row, 0)+"");
			if("edit".equals(button.getName())){
				DictRoles role = rolesService.getDictRolesById(roleId);
				JDialog dialog = new EditRoleDialog(role) {
					private static final long serialVersionUID = -8410250000456481144L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if (e.getID() == WindowEvent.WINDOW_CLOSED) {
							tableInit();
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);

			}else if("delete".equals(button.getName())){
				Long count=rolesService.countUsers(roleId);
				if (count != 0) {
					JOptionPane.showMessageDialog(null, "该角色存在用户，不能删除","错误",JOptionPane.ERROR_MESSAGE);
				}else{
					int len = JOptionPane.showConfirmDialog(null,
							"确定要删除此角色吗？", "提示",
							JOptionPane.YES_NO_OPTION);
					if (len == 0) {
						rolesService.deleteRole(roleId);
						JOptionPane.showMessageDialog(null, "删除成功",
								"信息", JOptionPane.INFORMATION_MESSAGE);
						tableInit();
					}
				}
			}else if("empower".equals(button.getName())){
				DictRoles role = rolesService.getDictRolesById(roleId);
				JDialog dialog=new EmpowerDialog(role);
				dialog.pack();
				dialog.setVisible(true);
			}
		}
	}
	
	@Override
	public void refresh() {
		System.out.println("刷新了角色管理");
	}


	public MainSet getMainSet() {
		return mainSet;
	}


	public void setMainSet(MainSet mainSet) {
		this.mainSet = mainSet;
	}
	
}