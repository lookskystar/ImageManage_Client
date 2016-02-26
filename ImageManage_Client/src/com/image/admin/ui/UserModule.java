package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.image.admin.service.UsersService;
import com.image.common.BasicModule;
import com.image.common.Params;
import com.image.common.pojo.DictAreas;
import com.image.common.pojo.DictRoles;
import com.image.common.pojo.DictTeams;
import com.image.common.pojo.DictUsers;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ImageUtils;
import com.image.common.util.Pager;
import com.image.common.util.PagerHelper;
import com.image.common.util.ResourceUtils;

/**
 * 人员管理模块
 * @author L
 */
public class UserModule extends BasicModule {

	private static final long serialVersionUID = -2089819139124983324L;

	private JButton bzAdd;
	private JButton bzUpdate;
	private JButton bzDel;
	private JLabel CollTree;
	private JLabel ExpTree;
	private JLabel jbName;
	private JTextField txtName;
	private JLabel jbGonghao;
	private JTextField txtGonghao;
	private JButton btnSreach;
	private JButton userAdd;
	private JTable tableView;
	JScrollPane scpTable;
	private MainSet mainSet;
	private JButton btnUp;
	private JButton btnDown;
	private JButton firstBtn;//首页
	private JButton lastBtn;//末页 
	private int totalRows;//总记录数
	private int totalPages;//总页数
	private int currentPage=1;//当前页
	private JLabel totalLabel;//总页数显示
	private JLabel currentPageLabel;//当前页显示Label
	private JLabel totalPageLabel;//共页显示Label
	DictUsers sessionUser=Params.sessionUser;
	private JPanel pnlWest = new JPanel();
	private JPanel pnlEast = new JPanel();
	private JPanel pWN = new JPanel();
	private JPanel pWC = new JPanel();
	private JPanel pWCN = new JPanel();
	private JPanel pWCC = new JPanel();
	private JPanel pEN = new JPanel();
	private JPanel pEC = new JPanel();
	private JPanel pES = new JPanel();
	private Long teamId=null;
	private static final String[] columnNames = { "用户ID", "地区", "用户名称", "工号", "角色", "登陆帐号",
			"登陆密码", "操作" };
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

	private JTree bzTree;
	

	@SuppressWarnings("serial")
	public UserModule(MainSet mainSet) {
		super(mainSet);
		this.setMainSet(mainSet);
		
		//分页标签初始化
		currentPageLabel=new JLabel();
		currentPageLabel.setForeground(Color.RED);
		totalLabel=new JLabel();
		totalLabel.setForeground(Color.RED);
		totalPageLabel=new JLabel();
		totalPageLabel.setForeground(Color.RED);
		
		bzAdd = new JButton(ResourceUtils.getResourceByKey("JButton.add_label"));
		CommonComponentUtils.setButtonStyle(bzAdd,50,24,null);
		bzUpdate = new JButton(ResourceUtils
				.getResourceByKey("JButton.update_label"));
		CommonComponentUtils.setButtonStyle(bzUpdate,50,24,null);
		bzDel = new JButton(ResourceUtils.getResourceByKey("JButton.del_label"));
		CommonComponentUtils.setButtonStyle(bzDel,50,24,null);
		CollTree=new JLabel("全部收缩");
		CollTree.setForeground(Color.BLUE);
		ExpTree=new JLabel("展开所有");
		ExpTree.setForeground(Color.BLUE);
		//设置JTree展开结点为'+''-'形式
		Icon icon1 = ImageUtils.createImageIcon("butCollapse.gif");
		Icon icon2 = ImageUtils.createImageIcon("butExpand.gif");
		UIManager.put("Tree.collapsedIcon", icon1);
		UIManager.put("Tree.expandedIcon", icon2);
		jbName = new JLabel("姓名：");
		txtName = new JTextField();
		txtName.setColumns(22);
		jbGonghao = new JLabel("工号：");
		txtGonghao = new JTextField();
		txtGonghao.setColumns(22);
		btnSreach = new JButton(ResourceUtils
				.getResourceByKey("JButton.sreach_label"));
		CommonComponentUtils.setButtonStyle(btnSreach,78,24,"find2.gif");
		userAdd = new JButton(ResourceUtils
				.getResourceByKey("JButton.add_label"));
		CommonComponentUtils.setButtonStyle(userAdd,70,24,"add.gif");
		btnUp = new JButton(ResourceUtils.getResourceByKey("JButton.pageUp_label"));
		CommonComponentUtils.setButtonStyle(btnUp,80,24,"pagination_prev.gif");
		btnDown = new JButton(ResourceUtils.getResourceByKey("JButton.pageDown_label"));
		CommonComponentUtils.setButtonStyle(btnDown,80,24,"pagination_next.gif");
		firstBtn=new JButton(ResourceUtils.getResourceByKey("JButton.pageFirst_label"));
		CommonComponentUtils.setButtonStyle(firstBtn,80,24,"pagination_first.gif");
		lastBtn=new JButton(ResourceUtils.getResourceByKey("JButton.pageLast_label"));
		CommonComponentUtils.setButtonStyle(lastBtn,80,24,"pagination_last.gif");
		scpTable = new JScrollPane();
		tableView = new JTable() {
			public boolean isCellEditable(int row, int column) {
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
	public void init() {
		treeInit();
		tableInit();
		panelInit();
		buttonInit();

	}
	

	/**
	 * 获得树的数据
	 * @return 
	 */
	private DefaultTreeModel cteateTreeData(){
		DefaultTreeModel model;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("全部");
		DefaultMutableTreeNode nodeD = new DefaultMutableTreeNode("段级部门");
		DefaultMutableTreeNode nodeS = new DefaultMutableTreeNode("所级部门");
		DefaultMutableTreeNode[] node;

		List<DictTeams> teamList = usersService.listDictTeams(sessionUser.getAreaId());
		node = new DefaultMutableTreeNode[teamList.size()];
		for (int i = 0; i < teamList.size(); i++) {
			node[i] = new DefaultMutableTreeNode(teamList.get(i));
			if (teamList.get(i).getTeamLevel() == 1) {
				nodeD.add(node[i]);
			} else {
				nodeS.add(node[i]);
			}
		}
		root.add(nodeD);
		root.add(nodeS);
		model = new DefaultTreeModel(root);
		return model;
		
	}

	/**
	 * 树初始化
	 */
	private void treeInit() {
		DefaultTreeModel model=cteateTreeData();
		bzTree = new JTree(model);
		// 添加点击事件
		bzTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JTree tempTree = (JTree) e.getSource();
				int row = tempTree.getRowForLocation(e.getX(), e.getY());
				if (row != -1) {
					TreePath path = tempTree.getPathForRow(row);
					// 取得被单击的节点
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
							.getParent();
					int childCount = node.getChildCount();
					if (null != parentNode) {
						if (!(childCount >0)) {
							teamId =  ((DictTeams) node.getUserObject()).getTeamId();
						}
					}
				}
				refreshTable("first");
			}
		});
	}
	
	/**
	 * 刷新树数据
	 */
	private void refreshTree(){
		DefaultTreeModel model=cteateTreeData();
		bzTree.setModel(model);
		bzTree.updateUI();
	}
	
	/**
	 * 刷新表格数据
	 */
	private void refreshTable(String pageMethod){
		Object[][] rows=cteateTableData(pageMethod);
		CommonComponentUtils.refreshTable(tableView, columnNames, rows);
		CommonComponentUtils.setTableStyle(tableView, columnNames, 280, new MyButtonRenderer(), new MyButtonEditor());
	}
	
	/**
	 * 获得表格数据
	 */
	private Object[][] cteateTableData(String pageMethod){
		String userName = txtName.getText().trim();
		String gonghao=txtGonghao.getText().trim();
		//分页
		totalRows=usersService.getDictUsersCount(sessionUser.getAreaId(), userName, gonghao, teamId);//获得总数据数
		Pager pager=PagerHelper.getPager(currentPage+"", pageMethod, totalRows);
		currentPage=pager.getCurrentPage();
		totalPages=pager.getTotalPages();
		totalLabel.setText(""+totalRows+"");
		currentPageLabel.setText(""+currentPage+"");
		totalPageLabel.setText(""+totalPages+"");
		List<DictUsers> list=usersService.findDictUsersPage(pager.getPageSize(), pager.getStartRow(), sessionUser.getAreaId(),userName,gonghao, teamId);
		
//		PageModel<DictUsers> list = usersService.findDictUsers(sessionUser.getAreaId(),userName,gonghao, teamId);
		Integer size = list.size();
		Object[][] rowData = new Object[size][];
		List<Object> listUser = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				listUser = new ArrayList<Object>();
				listUser.add(list.get(i).getUserId());
				for (DictAreas listArea : usersService.listDictArea()) {
					if (list.get(i).getAreaId().equals(
							listArea.getAreaId())) {
						listUser.add(listArea.getAreaName());
						break;
					}
				}
				listUser.add(list.get(i).getName());
				listUser.add(list.get(i).getGonghao());
				for (DictRoles listRole : usersService.listDictRoles()) {
					if (list.get(i).getRoleId().equals(
							listRole.getRoleId())) {
						listUser.add(listRole.getRoleName());
						break;
					}
				}
				listUser.add(list.get(i).getUsername());
				listUser.add(list.get(i).getPassword());
				listUser.add(null);
				rowData[i] = listUser.toArray();
			}
		}
		
		return rowData;
	}
	/**
	 * 表格控件初始化
	 */
	private void tableInit() {
		Object[][] rowData=cteateTableData("first");
		tableView = CommonComponentUtils.createTable(columnNames, rowData);
		tableView = CommonComponentUtils.setTableStyle(tableView, columnNames, 270, new MyButtonRenderer(), new MyButtonEditor());

	}

	/**
	 * 面板控件初始化
	 */
	private void panelInit() {
		pWN.setLayout(new FlowLayout(FlowLayout.LEFT));
		pWN.add(bzAdd);
		pWN.add(bzUpdate);
		pWN.add(bzDel);

		pWCN.setLayout(new FlowLayout(FlowLayout.LEFT));
		pWCN.add(ExpTree);
		pWCN.add(CollTree);
		
		pWCC.setLayout(new FlowLayout(FlowLayout.LEFT));
		pWCC.add(bzTree);

		pEN.setLayout(new FlowLayout(FlowLayout.LEFT));
		pEN.add(jbName);
		pEN.add(txtName);
		pEN.add(jbGonghao);
		pEN.add(txtGonghao);
		pEN.add(btnSreach);
		pEN.add(userAdd);

		pEC.setLayout(new BorderLayout());
		pEC.add(tableView.getTableHeader(), BorderLayout.NORTH);
		pEC.add(scpTable, BorderLayout.CENTER);
		scpTable.getViewport().add(tableView);

		pES.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pES.add(new JLabel("总共"));
		pES.add(totalLabel);
		pES.add(new JLabel("条记录，每页显示10条记录，当前页:第"));
		pES.add(currentPageLabel);
		pES.add(new JLabel("页,共"));
		pES.add(totalPageLabel);
		pES.add(new JLabel("页\b\b\b\b\b\b"));
		pES.add(firstBtn);
		pES.add(btnUp);
		pES.add(btnDown);
		pES.add(lastBtn);
		
		pWC.setLayout(new BorderLayout());
		pWC.add(pWCN, BorderLayout.NORTH);
		pWC.add(pWCC, BorderLayout.CENTER);

		pnlWest.setLayout(new BorderLayout());
		pnlWest.add(pWN, BorderLayout.NORTH);
		pnlWest.add(pWC, BorderLayout.CENTER);

		pnlEast.setLayout(new BorderLayout());
		pnlEast.add(pEN, BorderLayout.NORTH);
		pnlEast.add(pEC, BorderLayout.CENTER);
		pnlEast.add(pES, BorderLayout.SOUTH);

		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(pnlWest, BorderLayout.WEST);
		getModulePanel().add(pnlEast, BorderLayout.CENTER);

	}
	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		btnSreach.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("first");
			}
		});
		userAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new AddUserDialog(){
					private static final long serialVersionUID = -8117704990444540216L;
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

		bzAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new AddBzDialog(){
					private static final long serialVersionUID = 8291694293058906446L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if(e.getID()==WindowEvent.WINDOW_CLOSED){
							refreshTree();
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		
		bzUpdate.addActionListener(new ActionListener() {
			@SuppressWarnings("serial")
			@Override
			public void actionPerformed(ActionEvent e) {
				if(teamId==null){
					JOptionPane.showMessageDialog(null, "请选择班组","提示",JOptionPane.ERROR_MESSAGE);
				}else{
					DictTeams list=usersService.getDictTeamsById(teamId);
					JDialog dialog = new EditBzDialog(list){
						@Override
						protected void processWindowEvent(WindowEvent e) {
							if(e.getID()==WindowEvent.WINDOW_CLOSED){
								refreshTree();
							}
							super.processWindowEvent(e);
						}
					};
					dialog.pack();
					dialog.setVisible(true);
				}
				
			}
		});
		
		bzDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(teamId==null){
					JOptionPane.showMessageDialog(null, "请选择班组","提示",JOptionPane.ERROR_MESSAGE);
				}else{
					Long count=usersService.countUser(teamId);
					if(count!=0){
						JOptionPane.showMessageDialog(null, "该班组存在用户，不能删除","错误",JOptionPane.ERROR_MESSAGE);
					}else{
						int len = JOptionPane.showConfirmDialog(null,
								"确定要删除此班组吗？", "提示",
								JOptionPane.YES_NO_OPTION);
						if (len == 0) {
							usersService.delDictTeams(teamId);
							JOptionPane.showMessageDialog(null, "删除成功",
									"信息", JOptionPane.INFORMATION_MESSAGE);
							refreshTree();
						}
					}
				}
				
			}
		});
		CollTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = bzTree.getRowForLocation(e.getX(), e.getY());
				TreePath path = bzTree.getPathForRow(row);
				bzTree.collapsePath(path);
			}
		});
		
		ExpTree.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = bzTree.getRowForLocation(e.getX(), e.getY());
				TreePath path = bzTree.getPathForRow(row);
				TreeNode node = (TreeNode) path.getLastPathComponent();
				for (Enumeration<TreeNode> i = node.children(); i.hasMoreElements();) {
					bzTree.expandPath(path.pathByAddingChild(i.nextElement()));
				}

			}
		});
		firstBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("first");
			}
		});
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("previous");
			}
		});
		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("next");
			}
		});
		lastBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("last");
			}
		});

	}

	/**
	 * 为表格最后一行添加操作按钮
	 *
	 */
	final class MyButtonRenderer implements TableCellRenderer {

		private JPanel jpanel;

		public MyButtonRenderer() {
			jpanel = initOperate();
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
	 *
	 */
	final class MyButtonEditor extends AbstractCellEditor implements
			TableCellEditor {
		private static final long serialVersionUID = -3257877651073991549L;
		private JPanel jpanel;

		public MyButtonEditor() {
			jpanel = initOperate();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return jpanel;
		}

		// 获取button的text，没有该方法，将会显示false
		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}

	/**
	 * 初始化功能按钮操作
	 * @return
	 */
	private JPanel initOperate() {
		JPanel jpanel = new JPanel();
		JButton detailBtn = new JButton("查看");
		detailBtn.setName("detail");
		CommonComponentUtils.setButtonStyle(detailBtn,70,20,null);
		detailBtn.addActionListener(new ButtonOnclickListener());

		JButton editBtn = new JButton("编辑");
		editBtn.setName("edit");
		CommonComponentUtils.setButtonStyle(editBtn,70,20,null);
		editBtn.addActionListener(new ButtonOnclickListener());

		JButton deleteBtn = new JButton("删除");
		deleteBtn.setName("delete");
		CommonComponentUtils.setButtonStyle(deleteBtn,70,20,null);
		deleteBtn.addActionListener(new ButtonOnclickListener());

		jpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpanel.add(detailBtn);
		jpanel.add(editBtn);
		jpanel.add(deleteBtn);
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
			final Long userId=Long.parseLong(tableView.getValueAt(row, 0)+"");
			if("detail".equals(button.getName())){
				DictUsers user=usersService.getDictUsersById(userId);
				JDialog dialog = new UserInfoDialog(user);
				dialog.pack();
				dialog.setVisible(true);
				
			}else if("edit".equals(button.getName())){
				DictUsers user=usersService.getDictUsersById(userId);
				JDialog dialog = new EditUserDialog(user){
					private static final long serialVersionUID = 3040112433128770030L;
					@Override
					protected void processWindowEvent(WindowEvent e) {
						if (e.getID() == WindowEvent.WINDOW_CLOSED) {
							refreshTable("first");
						}
						super.processWindowEvent(e);
					}
				};
				dialog.pack();
				dialog.setVisible(true);
			}else if("delete".equals(button.getName())){
				String[] userIdArray = new String[1];
				userIdArray[0] = String.valueOf(userId);
				int len = JOptionPane.showConfirmDialog(null, "确定删除吗？", "提示",JOptionPane.YES_NO_OPTION);
				if (len == 0) {
					usersService.delDictUsers(userIdArray);
					JOptionPane.showMessageDialog(null, "删除成功", "信息",JOptionPane.INFORMATION_MESSAGE);
					refreshTable("first");
				}
			}
		}
	}

	@Override
	public void refresh() {
		System.out.println("刷新了人员管理");
	}

	public MainSet getMainSet() {
		return mainSet;
	}

	public void setMainSet(MainSet mainSet) {
		this.mainSet = mainSet;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

}