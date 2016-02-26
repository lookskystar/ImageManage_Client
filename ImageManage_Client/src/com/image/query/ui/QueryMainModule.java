package com.image.query.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.eltima.components.ui.DatePicker;
import com.image.common.BasicModule;
import com.image.common.Params;
import com.image.common.pojo.DictUsers;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.ResourceUtils;
import com.image.query.service.QueryService;
import com.image.set.service.PresetImageService;

public class QueryMainModule extends BasicModule {

	private static final long serialVersionUID = 8459782491322871324L;

	private static String queryButtonName;

	private static HessianServiceFactory hessianServiceFactory;

	private static QueryService queryService;

	private static PresetImageService presetImageService;

	public static final SimpleDateFormat YMD_SDFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/** 分隔符 */
	private static JToolBar.Separator separator = new JToolBar.Separator(null);

	static {
		queryButtonName = ResourceUtils.getResourceByKey("JButton.sreach_label");
		hessianServiceFactory = HessianServiceFactory.getInstance();
		// 加载服务接口
		try {
			queryService = (QueryService) hessianServiceFactory.createService(QueryService.class, "queryService");
			presetImageService = (PresetImageService) hessianServiceFactory.createService(PresetImageService.class,
					"presetImageService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 主界面 */
	private MainSet mainSet;
	/** 车型标签 */
	private JLabel jcTypeLabel;
	/** 车型下拉框 */
	private JComboBox jcTypeComboBox;
	/** 车号标签 */
	private JLabel jcNumLabel;
	/** 车号输入框 */
	private JTextField jcNumTextField;
	/** 日期标签 */
	private JLabel dateLabel;
	/** 查询日期框 */
	private DatePicker choiceDatePicker;
	/** 查询按钮 */
	private JButton queryButton;
	/** 数据表头 */
	private JScrollPane dataHeadPanel;
	/** 数据表格 */
	private JTable dataTablel;
	/** 上部面板 */
	private JPanel northPanel;
	/** 中部面板 */
	private JPanel centerPanel;
	/** 下部面板 */
	private JPanel southPanel;
	/** 表头数据 */
	private String[] colunmNames = { "编号", "车型车号", "随车/图片", "随车/视频", "地勤/图片", "地勤/视频", "质检/图片", "质检/视频", "技术/图片",
			"技术/视频", "异常", "操作" };
	/** 切换面板参数容器 */
	private Map<String, String> params = new HashMap<String, String>();

	public QueryMainModule(MainSet mainSet) {
		super(mainSet);
		this.setMainSet(mainSet);
		componentInit();
	}

	public void resetModule() {
		mainSet.setModule(new QueryTreeModule(mainSet, params));
	}

	/**
	 * 刷新表格数据
	 */
	public void refreshTable() {
		Map<String, String> conditionMap = queryCondition();
		Object[][] rowData = queryDataResult(conditionMap);
		CommonComponentUtils.refreshTable(dataTablel, colunmNames, rowData);
		CommonComponentUtils.setTableStyle(dataTablel, colunmNames, 200, new DefaultTableRender(),
				new DefaultTableEditer());
	}

	/**
	 * 标签控件初始化
	 */
	private void labelInit() {
		jcTypeLabel = new JLabel("车型：");
		jcNumLabel = new JLabel("车号：");
		dateLabel = new JLabel("日期：");
	}

	/**
	 * 文本控件初始化
	 */
	private void textFieldInit() {
		jcNumTextField = new JTextField();
		jcNumTextField.setColumns(22);
	}

	/**
	 * 选择控件初始化
	 */
	private void comboboxInit() {
		List<String> jcTypeList = presetImageService.findJcstypeAll();
		Integer comboboxSize = jcTypeList.size();
		Object[] jcNumData = new Object[comboboxSize + 1];
		jcNumData[0] = "-请选择-";
		for (int i = 0; i < comboboxSize; i++) {
			jcNumData[i + 1] = jcTypeList.get(i);
		}
		jcTypeComboBox = new JComboBox(jcNumData);
	}

	/**
	 * 日期控件初始化
	 */
	private void datePickerInit() {
		choiceDatePicker = new DatePicker(new Date(), "yyyy-MM-dd", null, new Dimension(100, 24));
	}

	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		queryButton = new JButton(queryButtonName);
		CommonComponentUtils.setButtonStyle(queryButton, 100, 24, "find2.gif");
		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable();
			}
		});
	}

	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private Map<String, String> queryCondition() {
		Map<String, String> conditionMap = new HashMap<String, String>();
		String jcNum = jcNumTextField.getText();
		String jcType = jcTypeComboBox.getSelectedItem().toString().equals("-请选择-") ? "" : jcTypeComboBox
				.getSelectedItem().toString();
		String taskDate = choiceDatePicker.getText().equals("") ? YMD_SDFORMAT.format(new Date()) : choiceDatePicker
				.getText();
		conditionMap.put("jcNum", jcNum);
		conditionMap.put("jcType", jcType);
		conditionMap.put("taskDate", taskDate);
		return conditionMap;
	}

	/**
	 * 获取表格数据
	 * 
	 * @param conditionMap
	 * @return
	 */
	private Object[][] queryDataResult(Map<String, String> conditionMap) {
		DictUsers user = Params.sessionUser;
		String areaId = user.getAreaId().toString();
		List<Map<String, String>> uploadRecList = queryService.findAllUploadRecOnJc(conditionMap.get("jcType"),
				conditionMap.get("jcNum"), conditionMap.get("taskDate"), areaId, null);
		Integer dataSize = uploadRecList.size();
		Object[][] rowData = new Object[dataSize][];
		for (int i = 0; i < dataSize; i++) {
			Map<String, String> dataMap = uploadRecList.get(i);
			List<Object> innerList = new LinkedList<Object>();
			innerList.add(dataMap.get("jcRecId"));
			innerList.add(dataMap.get("jcType") + "-" + dataMap.get("jcNum"));
			innerList.add(dataMap.get("onePic"));
			innerList.add(dataMap.get("oneVid"));
			innerList.add(dataMap.get("twoPic"));
			innerList.add(dataMap.get("twoVid"));
			innerList.add(dataMap.get("thrPic"));
			innerList.add(dataMap.get("thrVid"));
			innerList.add(dataMap.get("fourPic"));
			innerList.add(dataMap.get("fourVid"));
			innerList.add(dataMap.get("status").equals("1") ? "正常" : "异常");
			innerList.add(null);
			rowData[i] = innerList.toArray();
		}
		return rowData;
	}

	/**
	 * 表格控件初始化
	 */
	private void tableInit() {
		Map<String, String> conditionMap = queryCondition();
		Object[][] rowData = queryDataResult(conditionMap);
		dataTablel = CommonComponentUtils.createTable(colunmNames, rowData);
		// 设置表格列宽度
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setPreferredWidth(200);
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setCellRenderer(new DefaultTableRender());
		dataTablel.getColumnModel().getColumn(colunmNames.length - 1).setCellEditor(new DefaultTableEditer());
	}

	/**
	 * 面板控件初始化
	 */
	private void panelInit() {
		// 上部面板
		northPanel = new JPanel();
		// 添加车型查询
		northPanel.add(jcTypeLabel);
		northPanel.add(jcTypeComboBox);
		northPanel.add(separator);
		// 添加车号查询
		northPanel.add(jcNumLabel);
		northPanel.add(jcNumTextField);
		northPanel.add(separator);
		// 添加时间选择
		northPanel.add(dateLabel);
		northPanel.add(choiceDatePicker);
		northPanel.add(separator);
		// 添加查询按钮
		northPanel.add(queryButton);
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// 表头面板
		dataHeadPanel = new JScrollPane();
		dataHeadPanel.getViewport().add(dataTablel);
		// 中部面板
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(dataHeadPanel);
		// 下部面板
		southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	}

	/**
	 * 布局控件初始化
	 */
	private void layoutInit() {
		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(northPanel, BorderLayout.NORTH);
		getModulePanel().add(centerPanel, BorderLayout.CENTER);
		getModulePanel().add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * 初始化
	 */
	private void componentInit() {
		labelInit();
		textFieldInit();
		comboboxInit();
		datePickerInit();
		buttonInit();
		tableInit();
		panelInit();
		layoutInit();
	}

	private class DefaultTableRender implements TableCellRenderer {

		private JPanel innerPanel;

		{
			innerPanel = new JPanel();
			JButton detailBtn = new JButton("详情");
			detailBtn.setPreferredSize(new Dimension(70, 20));
			detailBtn.setName("detail");
			detailBtn.setForeground(Color.white);
			detailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			JButton videoBtn = new JButton("视频地址");
			videoBtn.setPreferredSize(new Dimension(70, 20));
			videoBtn.setName("detail");
			videoBtn.setForeground(Color.white);
			videoBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			JButton reportBtn = new JButton("报告");
			reportBtn.setPreferredSize(new Dimension(70, 20));
			reportBtn.setName("detail");
			reportBtn.setForeground(Color.white);
			reportBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			JButton addressBtn = new JButton("影像地址");
			addressBtn.setPreferredSize(new Dimension(70, 20));
			addressBtn.setName("detail");
			addressBtn.setForeground(Color.white);
			addressBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			innerPanel.add(detailBtn);
			innerPanel.add(videoBtn);
			innerPanel.add(reportBtn);
			innerPanel.add(addressBtn);
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
			JButton detailBtn = new JButton("详情");
			detailBtn.setPreferredSize(new Dimension(70, 20));
			detailBtn.setName("detail");
			detailBtn.setForeground(Color.white);
			detailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
			detailBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// 获得选中行
					Integer rowIndex = dataTablel.getSelectedRow();
					String jcRecId = (String) dataTablel.getValueAt(rowIndex, 0);
					String jcTypeNum = (String) dataTablel.getValueAt(rowIndex, 1);
					params.put("jcRecId", jcRecId);
					params.put("jcTypeNum", jcTypeNum);
					resetModule();
				}
			});

			JButton videoBtn = new JButton("视频地址");
			videoBtn.setPreferredSize(new Dimension(70, 20));
			videoBtn.setName("detail");
			videoBtn.setForeground(Color.white);
			videoBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			JButton reportBtn = new JButton("报告");
			reportBtn.setPreferredSize(new Dimension(70, 20));
			reportBtn.setName("detail");
			reportBtn.setForeground(Color.white);
			reportBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			JButton addressBtn = new JButton("影像地址");
			addressBtn.setPreferredSize(new Dimension(70, 20));
			addressBtn.setName("detail");
			addressBtn.setForeground(Color.white);
			addressBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

			innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			innerPanel.add(detailBtn);
			innerPanel.add(videoBtn);
			innerPanel.add(reportBtn);
			innerPanel.add(addressBtn);
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	public MainSet getMainSet() {
		return mainSet;
	}

	public void setMainSet(MainSet mainSet) {
		this.mainSet = mainSet;
	}
}
