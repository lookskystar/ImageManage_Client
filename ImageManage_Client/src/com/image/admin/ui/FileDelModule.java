package com.image.admin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.eltima.components.ui.DatePicker;
import com.image.admin.service.DelService;
import com.image.admin.service.UsersService;
import com.image.common.BasicModule;
import com.image.common.pojo.DelRec;
import com.image.common.pojo.DictAreas;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.Pager;
import com.image.common.util.PagerHelper;
import com.image.common.util.ResourceUtils;

/**
 * 文件删除模块
 * 
 * @author L
 * 
 */
public class FileDelModule extends BasicModule {
	private static final long serialVersionUID = -7451390005180893409L;

	private JLabel fileBegin;
	private JLabel fileEnd;
	private JComboBox areaComboBox;
	private JLabel fileArea;
	private JButton btnDel;
	private JTable tableView;
	private JButton btnUp;
	private JButton btnDown;
	private JButton firstBtn;// 首页
	private JButton lastBtn;// 末页
	private int totalRows;// 总记录数
	private int totalPages;// 总页数
	private int currentPage = 1;// 当前页
	private JLabel totalLabel;// 总页数显示
	private JLabel currentPageLabel;// 当前页显示Label
	private JLabel totalPageLabel;// 共页显示Label
	JScrollPane scpTable;
	private DatePicker dpBegin = new DatePicker(null, "yyyy-MM-dd", null,
			new Dimension(120, 24));
	private DatePicker dpEnd = new DatePicker(null, "yyyy-MM-dd", null,
			new Dimension(120, 24));
	String[] columnNames = { "序号", "文件记录开始时间", "文件记录结束时间", "删除操作时间" };
	private JPanel pnlMainN = new JPanel();
	private JPanel pnlMainC = new JPanel();
	private JPanel pnlMainS = new JPanel();
	private MainSet mainSet;
	public static final SimpleDateFormat YMD_SDFORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static HessianServiceFactory hessianServiceFactory;
	private static DelService delService;
	private static UsersService usersService;

	// 调用服务接口
	static {
		hessianServiceFactory = HessianServiceFactory.getInstance();
		try {
			delService = (DelService) hessianServiceFactory.createService(
					DelService.class, "delService");
			usersService = (UsersService) hessianServiceFactory.createService(
					UsersService.class, "usersService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("serial")
	public FileDelModule(MainSet mainSet) {
		super(mainSet);
		this.setMainSet(mainSet);
		// 分页标签初始化
		currentPageLabel = new JLabel();
		currentPageLabel.setForeground(Color.RED);
		totalLabel = new JLabel();
		totalLabel.setForeground(Color.RED);
		totalPageLabel = new JLabel();
		totalPageLabel.setForeground(Color.RED);

		fileBegin = new JLabel("文件记录开始时间：");
		fileEnd = new JLabel("文件记录结束时间：");
		fileArea = new JLabel("地区：");

		btnUp = new JButton(ResourceUtils
				.getResourceByKey("JButton.pageUp_label"));
		CommonComponentUtils.setButtonStyle(btnUp, 80, 24,
				"pagination_prev.gif");
		btnDown = new JButton(ResourceUtils
				.getResourceByKey("JButton.pageDown_label"));
		CommonComponentUtils.setButtonStyle(btnDown, 80, 24,
				"pagination_next.gif");
		firstBtn = new JButton(ResourceUtils
				.getResourceByKey("JButton.pageFirst_label"));
		CommonComponentUtils.setButtonStyle(firstBtn, 80, 24,
				"pagination_first.gif");
		lastBtn = new JButton(ResourceUtils
				.getResourceByKey("JButton.pageLast_label"));
		CommonComponentUtils.setButtonStyle(lastBtn, 80, 24,
				"pagination_last.gif");
		btnDel = new JButton(ResourceUtils
				.getResourceByKey("JButton.del_label"));
		CommonComponentUtils.setButtonStyle(btnDel, 70, 24, "delete.gif");
		scpTable = new JScrollPane();
		tableView = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		init();
	}

	/**
	 * 初始化
	 * */
	public void init() {
		comboboxInit();
		tableInit();
		panelInit();
		buttonInit();
	}

	/**
	 * 选择控件初始化
	 */
	private void comboboxInit() {
		List<DictAreas> areaList = usersService.listDictArea();
		Integer comboboxSize = areaList.size();
		Object[] areaData = new Object[comboboxSize + 1];
		areaData[0] = "-选择地区-";
		for (int i = 0; i < comboboxSize; i++) {
			areaData[i + 1] = areaList.get(i).getAreaName();
		}
		areaComboBox = new JComboBox(areaData);
		areaComboBox.setPreferredSize(new Dimension(130, 26));
	}

	/**
	 * 刷新表格数据
	 */
	private void refreshTable(String pageMethod) {
		Object[][] rows = cteateTableData(pageMethod);
		CommonComponentUtils.refreshTable(tableView, columnNames, rows);
	}

	/**
	 * 获得表格数据
	 */
	private Object[][] cteateTableData(String pageMethod) {
		// 分页
		totalRows = delService.getDelRecCount();// 获得总数据数
		Pager pager = PagerHelper.getPager(currentPage + "", pageMethod,
				totalRows);
		currentPage = pager.getCurrentPage();
		totalPages = pager.getTotalPages();
		totalLabel.setText("" + totalRows + "");
		currentPageLabel.setText("" + currentPage + "");
		totalPageLabel.setText("" + totalPages + "");
		List<DelRec> list = delService.findDelRecPage(pager.getPageSize(),
				pager.getStartRow());

		Integer size = list.size();
		Object[][] rowData = new Object[size][];
		List<Object> listDel = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				listDel = new ArrayList<Object>();
				listDel.add(list.get(i).getId());
				listDel.add(list.get(i).getBtime());
				listDel.add(list.get(i).getEtime());
				listDel.add(list.get(i).getDelTime());
				rowData[i] = listDel.toArray();
			}
		}

		return rowData;
	}

	/**
	 * 表格控件初始化
	 */
	private void tableInit() {

		Object[][] rowData = cteateTableData("first");
		tableView = CommonComponentUtils.createTable(columnNames, rowData);
	}

	/**
	 * 面板控件初始化
	 */
	private void panelInit() {
		pnlMainN.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlMainN.add(fileBegin);
		pnlMainN.add(dpBegin);
		pnlMainN.add(fileEnd);
		pnlMainN.add(dpEnd);
		pnlMainN.add(fileArea);
		pnlMainN.add(areaComboBox);
		pnlMainN.add(btnDel);

		pnlMainC.setLayout(new BorderLayout());
		pnlMainC.add(tableView.getTableHeader(), BorderLayout.NORTH);
		scpTable.getViewport().add(tableView);
		pnlMainC.add(scpTable, BorderLayout.CENTER);

		pnlMainS.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlMainS.add(new JLabel("总共"));
		pnlMainS.add(totalLabel);
		pnlMainS.add(new JLabel("条记录，每页显示10条记录，当前页:第"));
		pnlMainS.add(currentPageLabel);
		pnlMainS.add(new JLabel("页,共"));
		pnlMainS.add(totalPageLabel);
		pnlMainS.add(new JLabel("页\b\b\b\b\b\b\b\b"));
		pnlMainS.add(firstBtn);
		pnlMainS.add(btnUp);
		pnlMainS.add(btnDown);
		pnlMainS.add(lastBtn);

		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(pnlMainN, BorderLayout.NORTH);
		getModulePanel().add(pnlMainC, BorderLayout.CENTER);
		getModulePanel().add(pnlMainS, BorderLayout.SOUTH);
	}

	/**
	 * 按钮控件初始化
	 */
	private void buttonInit() {
		btnDel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String beginTime = dpBegin.getText().trim();
				String endTime = dpEnd.getText().trim();
				Long areaId = null;
				for (DictAreas listArea : usersService.listDictArea()) {
					if (areaComboBox.getSelectedItem().equals(
							listArea.getAreaName())) {
						areaId = listArea.getAreaId();
					}
				}
				if ("".equals(beginTime) || "".equals(endTime)
						|| areaId == null) {
					JOptionPane.showMessageDialog(null, "请选择起始、结束时间与操作地区",
							"提示", JOptionPane.ERROR_MESSAGE);
				} else {
					int len = JOptionPane.showConfirmDialog(null,
							"确定要删除此段时间的记录信息及影像文件吗？", "提示",
							JOptionPane.YES_NO_OPTION);
					if (len == 0) {
						delService.deleteRec(beginTime, endTime, areaId);
						JOptionPane.showMessageDialog(null, "删除成功", "信息",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
				refreshTable("first");
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

	@Override
	public void refresh() {
		System.out.println("刷新了文件删除");
	}

	public MainSet getMainSet() {
		return mainSet;
	}

	public void setMainSet(MainSet mainSet) {
		this.mainSet = mainSet;
	}

}