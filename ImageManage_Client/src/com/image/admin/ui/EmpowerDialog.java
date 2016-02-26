package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.image.admin.service.RolesService;
import com.image.common.pojo.DictFunctions;
import com.image.common.pojo.DictRoles;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ImageUtils;
import com.image.common.util.ResourceUtils;

/**
 * 角色授权
 * 
 * @author L
 * 
 */
public class EmpowerDialog extends JDialog {

	private static final long serialVersionUID = 135114794971821382L;

	JPanel pnlMain = new JPanel();
	JPanel pnlCN = new JPanel();
	JPanel pnlCC = new JPanel();
	JPanel pnlC = new JPanel();
	JPanel pnlS = new JPanel();
	JScrollPane scpTree = new JScrollPane();
	JCheckBoxTree powerTree;
	JButton btnSave;
	JButton btnClose;
	private JLabel CollTree;
	private JLabel ExpTree;
	DictRoles role;
	
	List<String> str; 
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

	public EmpowerDialog(DictRoles role) {
		this.role = role;
		init();
		treeInit();
		panelInit();
		buttonInit();
	}


	/**
	 * 树初始化
	 */
	private void treeInit() {
		CheckNode root = new CheckNode("权限列表");
		CheckNode parent = null;
		CheckNode child = null; 
		List<DictFunctions> mainFun = rolesService.listMainFunctionPrivs();
		List<DictFunctions> secFun = rolesService.listSecFunctionPrivs();
		List<Object> oldlist=rolesService.getFuncIdByIdOfSQL(role.getRoleId());
		Long parentId;
		String funname = null;
		str=new ArrayList<String>();
		for (DictFunctions mfun : mainFun) {
			// 父节点
			parent = new CheckNode(mfun);
			for(DictFunctions sFun:secFun){
				parentId = sFun.getParentId();
				funname = rolesService.findFunnameById(parentId);
				if(funname.equals(mfun.getFuncName())){
					// 子节点
					child = new CheckNode(sFun);
					if(oldlist.contains(sFun.getFuncId().toString())){
						child.setSelected(true);
						str.add(sFun.getFuncId().toString());
					}
					parent.add(child);
				}
			}
			root.add(parent);
		}
		powerTree = new JCheckBoxTree(root);
		//点击事件
		powerTree.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			public void mousePressed(MouseEvent e) {
				JTree tempTree = (JTree) e.getSource();
				int row = tempTree.getRowForLocation(e.getX(), e.getY());
				if (row != -1) {
					TreePath path = tempTree.getPathForRow(row);
					// 取得被单击的节点
					CheckNode node = (CheckNode) path.getLastPathComponent();
					CheckNode parentNode = (CheckNode) node.getParent();
					int childCount = node.getChildCount();
					List<Object> seclist=rolesService.listSecFuncIdOfFunctionPrivs();
					String strFun=null;
					Long funcId;
					if (null == parentNode) {
						str.clear();
						for(int i=0;i<seclist.size();i++){
							str.add(seclist.get(i)+"");
						}
					}
					else if (null != parentNode && childCount > 0) {
						for (Enumeration<CheckNode> en = node.children(); en.hasMoreElements();) {
							funcId = ((DictFunctions) ((CheckNode) en.nextElement()).getUserObject()).getFuncId();
							strFun = funcId + "";
							if(str.contains(strFun)){
								str.remove(strFun);
							}else{
								str.add(strFun);
							}
						}
					}
					else {
						funcId = ((DictFunctions) node.getUserObject()).getFuncId();
						strFun = funcId + "";
						if (str.contains(strFun)) {
							str.remove(strFun);
						} else {
							str.add(strFun);
						}
					}
				}
			}
		});
	}

	/**
	 * 初始化
	 * */
	public void init() {

		// 设置JTree展开结点为'+''-'形式
		Icon icon1 = ImageUtils.createImageIcon("butCollapse.gif");
		Icon icon2 = ImageUtils.createImageIcon("butExpand.gif");
		UIManager.put("Tree.collapsedIcon", icon1);
		UIManager.put("Tree.expandedIcon", icon2);

		CollTree=new JLabel("全部收缩");
		CollTree.setForeground(Color.BLUE);
		ExpTree=new JLabel("展开所有");
		ExpTree.setForeground(Color.BLUE);
		btnSave = new JButton(ResourceUtils
				.getResourceByKey("JButton.commit_label"));
		CommonComponentUtils.setButtonStyle(btnSave,70,24,null);
		btnClose = new JButton(ResourceUtils
				.getResourceByKey("JButton.close_label"));
		CommonComponentUtils.setButtonStyle(btnClose,70,24,null);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
	}

	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {

		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EmpowerDialog.this.dispose();
			}
		});

		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				this.btnSave_actionPerformed(e);
			}

			private void btnSave_actionPerformed(ActionEvent e) {
				
				Object[] array=str.toArray();
				String[] strArray=new String[array.length];
				for(int i=0;i<array.length;i++){
					strArray[i]=array[i]+"";
				}
				if(strArray.length==0){
					JOptionPane.showMessageDialog(null, "请选择权限", "提示",JOptionPane.ERROR_MESSAGE);
				}else{
					rolesService.saveRolesFunction(strArray, role.getRoleId());
					JOptionPane.showMessageDialog(null, "保存成功", "信息",JOptionPane.INFORMATION_MESSAGE);
					EmpowerDialog.this.dispose();
				}
				
			}
		});
		//收缩
		CollTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = powerTree.getRowForLocation(e.getX(), e.getY());
				TreePath path = powerTree.getPathForRow(row);
				powerTree.collapsePath(path);
			}
		});
		//展开
		ExpTree.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = powerTree.getRowForLocation(e.getX(), e.getY());
				TreePath path = powerTree.getPathForRow(row);
				TreeNode node = (TreeNode) path.getLastPathComponent();
				for (Enumeration<TreeNode> i = node.children(); i.hasMoreElements();) {
					powerTree.expandPath(path.pathByAddingChild(i.nextElement()));
				}
			}
		});
	}

	/**
	 * 面板、位置初始化
	 */
	public void panelInit() {
		setTitle("角色授权");
		setMinimumSize(new Dimension(420, 380));

//		pnlCN.setLayout(new FlowLayout(FlowLayout.LEFT));
//		pnlCN.add(ExpTree);
//		pnlCN.add(CollTree);
		
//	    scpTree.getViewport().add(powerTree);		
		pnlCC.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlCC.add(powerTree);
		
		pnlC.setLayout(new BorderLayout());
		pnlC.add(pnlCN, BorderLayout.NORTH);
		pnlC.add(pnlCC, BorderLayout.CENTER);

		pnlS.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlS.add(btnSave);
		pnlS.add(btnClose);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlC, BorderLayout.CENTER);
		pnlMain.add(pnlS, BorderLayout.SOUTH);

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
	

	/**
	 * 带checkbox树
	 * @author L
	 *
	 */
	private class JCheckBoxTree extends JTree {
		
		private static final long serialVersionUID = 1L;

		public JCheckBoxTree(CheckNode checkNode) {
			super(checkNode);
			this.setCellRenderer(new CheckRenderer());
			this.setShowsRootHandles(true);
			this.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			this.putClientProperty("JTree.lineStyle", "Angled");
			this.addLister(this);
		}

		/***
		 * 添加点击事件 
         * 使其选中父节点时子节点也选中 
		 * @param tree
		 */
		private void addLister(final JTree tree) {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					int row = tree.getRowForLocation(e.getX(), e.getY());
					TreePath path = tree.getPathForRow(row);
					if (path != null) {
						CheckNode node = (CheckNode) path
								.getLastPathComponent();
						node.setSelected(!node.isSelected);
						if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
							if (node.isSelected) {
								tree.expandPath(path);
								
							} else {
								if (!node.isRoot()) {
									tree.collapsePath(path);
								}
							}
						}
						//响应事件更新树
						((DefaultTreeModel) tree.getModel()).nodeChanged(node);
//						System.out.println("被勾选的节点:"+node);
						tree.revalidate();
						tree.repaint();
					}
				}

			});
		}
	}
	

	public class CheckNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 1L;

		public final static int SINGLE_SELECTION = 0;

		public final static int DIG_IN_SELECTION = 4;

		protected int selectionMode;

		protected boolean isSelected;

		public CheckNode() {
			this(null);
		}

		public CheckNode(Object userObject) {
			this(userObject, true, false);
		}

		public CheckNode(Object userObject, boolean allowsChildren,
				boolean isSelected) {
			super(userObject, allowsChildren);
			this.isSelected = isSelected;
			setSelectionMode(DIG_IN_SELECTION);
		}

		public void setSelectionMode(int mode) {
			selectionMode = mode;
		}

		public int getSelectionMode() {
			return selectionMode;
		}

		/**
		 * 选中父节点时也级联选中子节点
		 * @param isSelected
		 */
		@SuppressWarnings("unchecked")
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
			if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
				Enumeration<CheckNode> e = children.elements();
				while (e.hasMoreElements()) {
					CheckNode node = (CheckNode) e.nextElement();
					node.setSelected(isSelected);
				}
			}
		}

		public boolean isSelected() {
			return isSelected;
		}
	}
	

	private class CheckRenderer extends JPanel implements TreeCellRenderer {
		private static final long serialVersionUID = 1L;

		protected JCheckBox check;

		protected TreeLabel label;

		public CheckRenderer() {
			setLayout(null);
			add(check = new JCheckBox());
			add(label = new TreeLabel());
			check.setBackground(UIManager.getColor("Tree.textBackground"));
			label.setForeground(UIManager.getColor("Tree.textForeground"));
		}

		/**
		 * 改变的节点为JLabel和JChekBox的组合 
		 */
		public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			String stringValue = tree.convertValueToText(value, isSelected,
					expanded, leaf, row, hasFocus);
			setEnabled(tree.isEnabled());
			check.setSelected(((CheckNode) value).isSelected());
			label.setFont(tree.getFont());
			label.setText(stringValue);
			label.setSelected(isSelected);
			label.setFocus(hasFocus);
			if (leaf) {
				// label.setIcon(UIManager.getIcon("Tree.leafIcon"));
				label.setIcon(null);//把leaf前的图片去掉   
			} else if (expanded) {
				label.setIcon(UIManager.getIcon("Tree.openIcon"));
			} else {
				label.setIcon(UIManager.getIcon("Tree.closedIcon"));
			}
			return this;
		}

		//设置高度宽度
		public Dimension getPreferredSize() {
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();
			return new Dimension(d_check.width + d_label.width,
					(d_check.height < d_label.height ? d_label.height
							: d_check.height));
		}

		public void doLayout() {
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();
			int y_check = 0;
			int y_label = 0;
			if (d_check.height < d_label.height) {
				y_check = (d_label.height - d_check.height) / 2;
			} else {
				y_label = (d_check.height - d_label.height) / 2;
			}
			check.setLocation(0, y_check);
			check.setBounds(0, y_check, d_check.width, d_check.height-7);
			label.setLocation(d_check.width, y_label);
			label.setBounds(d_check.width, y_label, d_label.width,
					d_label.height-5);
		}

		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

	
	}
	
	private class TreeLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		private boolean isSelected;

		private boolean hasFocus;

		public TreeLabel() {
		}

		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public void paint(Graphics g) {
			String str;
			if ((str = getText()) != null) {
				if (0 < str.length()) {
					if (isSelected) {
						g.setColor(UIManager
								.getColor("Tree.selectionBackground"));
					} else {
						g.setColor(UIManager
								.getColor("Tree.textBackground"));
					}
					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null) {
						imageOffset = currentI.getIconWidth()
								+ Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(imageOffset, 0, d.width - 1
							- imageOffset, d.height);
					if (hasFocus) {
						g.setColor(UIManager
								.getColor("Tree.selectionBorderColor"));
						g.drawRect(imageOffset, 0, d.width - 1
								- imageOffset, d.height - 1);
					}
				}
			}
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null) {
				retDimension = new Dimension(retDimension.width + 3,
						retDimension.height);
			}
			return retDimension;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
	}
}


