package com.image.query.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.image.common.BasicModule;
import com.image.common.pojo.DictWorktype;
import com.image.common.pojo.JcRec;
import com.image.common.pojo.ProcedureInfo;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.query.service.QueryService;

public class QueryTreeModule extends BasicModule {

	private static final long serialVersionUID = 6812348754254476770L;

	private static HessianServiceFactory hessianServiceFactory;

	private static QueryService queryService;

	static {
		hessianServiceFactory = HessianServiceFactory.getInstance();
		// 加载服务接口
		try {
			queryService = (QueryService) hessianServiceFactory.createService(QueryService.class, "queryService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 树形菜单 */
	private JTree dataTree;
	/** 数据表头 */
	private JScrollPane dataHeadPanel;
	/** 数据表格 */
	private JTable dataTablel;
	/** 左部面板 */
	private JPanel leftPanel;
	/** 左上面板 */
	private JPanel leftNorthPanel;
	/** 左中面板 */
	private JPanel leftCenterPanel;
	/** 左下面板 */
	private JPanel leftSouthPanel;
	/** 中部面板 */
	private JPanel centerPanel;
	/** 中中面板 */
	private JPanel centerCenterPanel;
	/** 中下面板 */
	private JPanel centerSouthPanel;
	/** 表头数据 */
	private String[] colunmNames = { "编号", "车型", "车号", "工序大类", "工序次数", "工作者", "工作标准", "实拍数量", "实拍时长", "异常情况", "操作" };
	/** 传递参数 */
	private Map<String, String> params;
	/** 主界面 */
	@SuppressWarnings("unused")
	private MainSet mainSet;

	public QueryTreeModule(MainSet mainSet) {
		super(mainSet);
		this.mainSet = mainSet;
		componentInit();
	}

	public QueryTreeModule(MainSet mainSet, Map<String, String> params) {
		super(mainSet);
		this.mainSet = mainSet;
		this.params = params;
		componentInit();
	}

	public void resetModule() {

	}

	/**
	 * 刷新表格数据
	 */
	public void refreshTable() {
		Object[][] rowData = queryDataResult();
		CommonComponentUtils.refreshTable(dataTablel, colunmNames, rowData);
		dataTablel.getColumnModel().getColumn(colunmNames.length - 5).setPreferredWidth(200);
		CommonComponentUtils.setTableStyle(dataTablel, colunmNames, 200, new DefaultTableRender(),
				new DefaultTableEditer());

	} 

	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {

	}

	/**
	 * 获取表格数据
	 * 
	 * @param conditionMap
	 * @return
	 */
	private Object[][] queryDataResult() {
		List<Map<String, String>> uploadRecListOnPlan = queryService.findDetailUploadRecOnJc(params.get("jcRecId"),
				params.get("proId"), params.get("workId"));
		Integer dataSize = uploadRecListOnPlan.size();
		Object[][] rowData = new Object[dataSize][];
		for (int i = 0; i < dataSize; i++) {
			Map<String, String> dataMap = uploadRecListOnPlan.get(i);
			List<Object> innerList = new LinkedList<Object>();
			innerList.add(i + 1);
			innerList.add(dataMap.get("jcType"));
			innerList.add(dataMap.get("jcNum"));
			innerList.add(dataMap.get("workType"));
			innerList.add(dataMap.get("proRank"));
			innerList.add(dataMap.get("takeName"));
			innerList.add("照片数量：" + dataMap.get("imageNumStd") + "视频时长：" + dataMap.get("videoTimeStd"));
			innerList.add(dataMap.get("imageNumAct"));
			innerList.add(dataMap.get("videoTimeAct"));
			innerList.add(dataMap.get("status"));
			innerList.add(null);
			rowData[i] = innerList.toArray();
		}
		return rowData;
	}

	/**
	 * 表格控件初始化
	 */
	private void tableInit() {
		Object[][] rowData = queryDataResult();
		dataTablel = CommonComponentUtils.createTable(colunmNames, rowData);
		dataTablel.getColumnModel().getColumn(colunmNames.length - 5).setPreferredWidth(200);
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setPreferredWidth(200);
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setCellRenderer(new DefaultTableRender());
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setCellEditor(new DefaultTableEditer());
	}

	/**
	 * 树形控件初始化
	 */
	private void treeInit() {
		Map<JcRec, Map<DictWorktype, List<ProcedureInfo>>> outerDataMap = queryService.constructProTree(Long
				.parseLong(params.get("jcRecId")));
		DefaultMutableTreeNode root = null;
		DefaultMutableTreeNode parent = null;
		DefaultMutableTreeNode child = null;
		for (Iterator<JcRec> iterator = outerDataMap.keySet().iterator(); iterator.hasNext();) {
			JcRec outerKey = iterator.next();
			// 根节点
			root = new DefaultMutableTreeNode(params.get("jcTypeNum"));
			Map<DictWorktype, List<ProcedureInfo>> innerDataMap = outerDataMap.get(outerKey);
			for (Iterator<DictWorktype> innerIterator = innerDataMap.keySet().iterator(); innerIterator.hasNext();) {
				// 父节点
				DictWorktype dictWorktype = (DictWorktype) innerIterator.next();
				parent = new DefaultMutableTreeNode(dictWorktype.getWorkType());
				parent.setUserObject(dictWorktype);
				// 添加子节点
				List<ProcedureInfo> procedureInfos = innerDataMap.get(dictWorktype);
				for (ProcedureInfo procedureInfo : procedureInfos) {
					child = new DefaultMutableTreeNode(procedureInfo.getProName());
					child.setUserObject(procedureInfo);
					parent.add(child);
				}
				// 添加父节点
				root.add(parent);
			}
		}
		TreeModel treeModel = new DefaultTreeModel(root);
		dataTree = new JTree(treeModel);
		// 添加点击事件
		dataTree.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				// 清空条件数据
				params.remove("workId");
				params.remove("proId");
				JTree tempTree = (JTree) e.getSource();
				int row = tempTree.getRowForLocation(e.getX(), e.getY());
				if (row != -1) {
					TreePath path = tempTree.getPathForRow(row);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
					int childCount = node.getChildCount();
					if (null != parentNode) {
						if (childCount > 0) {
							params.put("workId", (((DictWorktype) node.getUserObject()).getWorkId()).toString());
						} else {
							params.put("proId", (((ProcedureInfo) node.getUserObject()).getProId()).toString());
							params.put("workId", (((DictWorktype) parentNode.getUserObject()).getWorkId()).toString());
						}
					}
					refreshTable();
				}
			}
		});
	}

	/**
	 * 面板控件初始化
	 */
	private void panelInit() {
		// 左部面板
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		// 左上面板
		leftNorthPanel = new JPanel();
		leftNorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// 左中面板
		leftCenterPanel = new JPanel();
		leftCenterPanel.setLayout(new BorderLayout());
		leftCenterPanel.add(dataTree, BorderLayout.CENTER);
		// 左下面板
		leftSouthPanel = new JPanel();
		leftSouthPanel.setLayout(new BorderLayout());
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
		leftPanel.add(leftNorthPanel, BorderLayout.NORTH);
		leftPanel.add(leftSouthPanel, BorderLayout.SOUTH);
		// 表头面板
		dataHeadPanel = new JScrollPane();
		dataHeadPanel.getViewport().add(dataTablel);
		// 中中面板
		centerCenterPanel = new JPanel();
		centerCenterPanel.setLayout(new BorderLayout());
		centerCenterPanel.add(dataHeadPanel);
		// 中下面板
		centerSouthPanel = new JPanel();
		centerSouthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// 中部面板
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		centerPanel.add(centerSouthPanel, BorderLayout.SOUTH);

	}

	/**
	 * 布局控件初始化
	 */
	private void layoutInit() {
		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(centerPanel, BorderLayout.CENTER);
		getModulePanel().add(leftPanel, BorderLayout.WEST);
	}

	/**
	 * 初始化
	 */
	private void componentInit() {
		buttonInit();
		tableInit();
		treeInit();
		panelInit();
		layoutInit();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	private class DefaultTableRender implements TableCellRenderer {

		private JPanel innerPanel;

		{
			innerPanel = new JPanel();
			JButton detailBtn = new JButton("查看详情");
			detailBtn.setPreferredSize(new Dimension(70, 20));
			detailBtn.setName("detail");
			detailBtn.setForeground(Color.white);
			detailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			innerPanel.add(detailBtn);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			// TODO Auto-generated method stub
			return innerPanel;
		}
	}

	private class DefaultTableEditer implements TableCellEditor {

		private JPanel innerPanel;

		{
			innerPanel = new JPanel();
			JButton detailBtn = new JButton("查看详情");
			detailBtn.setPreferredSize(new Dimension(70, 20));
			detailBtn.setName("detail");
			detailBtn.setForeground(Color.white);
			detailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			innerPanel.add(detailBtn);
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub

		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			// TODO Auto-generated method stub
			return innerPanel;
		}
	}

}
