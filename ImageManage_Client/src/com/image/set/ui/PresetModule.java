package com.image.set.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.image.common.BasicModule;
import com.image.common.pojo.DictWorktype;
import com.image.common.pojo.ProcedureInfo;
import com.image.common.pojo.ProcedureStep;
import com.image.common.ui.MainSet;
import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.common.util.Pager;
import com.image.common.util.PagerHelper;
import com.image.common.util.ResourceUtils;
import com.image.set.service.PresetImageService;

/**
 * 标准预设管理模块
 * 
 * @author Administrator
 * 
 */
public class PresetModule extends BasicModule {

	private static final long serialVersionUID = -7451390005180893409L;
	private static final String[] columnNames = { "序号", "工序名称", "车型", "工序编号", "工序大类", "工序顺序", "标准作业时间(min)",
			"标准摄像时间(min)", "拍照数量", "工序间隔", "备注", "操作" };

	private JButton addPresetBtn;
	private JLabel jcTypeLab;
	private JComboBox jcTypeJcbox;
	private JLabel presetLab;
	private JComboBox presetJcbox;
	private JButton findBtn;
	private JTable presetListJtb;
	private JButton pageUpBtn;// 上一页
	private JButton pageDownBtn; // 下一页
	private JButton firstBtn;// 首页
	private JButton lastBtn;// 末页
	private int totalRows;// 总记录数
	private int totalPages;// 总页数
	private int currentPage = 1;// 当前页
	private JLabel totalLabel;// 总页数显示
	private JLabel currentPageLabel;// 当前页显示Label
	private JLabel totalPageLabel;// 共页显示Label
	JScrollPane scpTable;
	private static PresetImageService presetImageService;
	private JPanel pNorth = new JPanel();
	private JPanel pCenter = new JPanel();

	static {
		HessianServiceFactory factory = HessianServiceFactory.getInstance();
		try {
			presetImageService = (PresetImageService) factory.createService(PresetImageService.class,"presetImageService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PresetModule(MainSet mainSet) {
		super(mainSet);
		presetInit(mainSet);
	}

	// 标准预设初始化
	@SuppressWarnings("serial")
	public void presetInit(final MainSet mainSet) {
		Object[] obj = presetImageService.findJcstypeAll().toArray();
		Object[] jcType = Arrays.copyOf(obj, obj.length + 1);
		jcType[0] = "-请选择-";
		for (int i = 1; i < jcType.length; i++) {
			jcType[i] = obj[i - 1];
		}

		Object[] obj2 = presetImageService.findProcedureAll().toArray();
		Object[] preset = Arrays.copyOf(obj2, obj2.length + 1);
		preset[0] = "-请选择-";
		for (int i = 1; i < preset.length; i++) {
			preset[i] = obj2[i - 1];
		}

		addPresetBtn = new JButton(ResourceUtils.getResourceByKey("Set.addPreset_button"));
		CommonComponentUtils.setButtonStyle(addPresetBtn, 150, 24, "add.gif");

		jcTypeLab = new JLabel(ResourceUtils.getResourceByKey("Set.jcType.label"));
		jcTypeJcbox = new JComboBox(jcType);
		jcTypeJcbox.setPreferredSize(new Dimension(100, 20));

		presetLab = new JLabel(ResourceUtils.getResourceByKey("Set.proName.label"));
		presetJcbox = new JComboBox(preset);
		presetJcbox.setPreferredSize(new Dimension(180, 20));

		findBtn = new JButton(ResourceUtils.getResourceByKey("JButton.sreach_label"));
		CommonComponentUtils.setButtonStyle(findBtn, 100, 24, "find2.gif");

		pageUpBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageUp_label"));
		pageDownBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageDown_label"));
		scpTable = new JScrollPane();
		presetListJtb = new JTable() {
			public boolean isCellEditable(int row, int column) {
				if (column == columnNames.length - 1) {
					return true;
				}
				return false;
			}
		};
		presetListJtb.setPreferredScrollableViewportSize(new Dimension(550, 30));

		// 分页标签初始化
		currentPageLabel = new JLabel();
		currentPageLabel.setForeground(Color.RED);
		totalLabel = new JLabel();
		totalLabel.setForeground(Color.RED);
		totalPageLabel = new JLabel();
		totalPageLabel.setForeground(Color.RED);

		pNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		pNorth.add(addPresetBtn);
		pNorth.add(jcTypeLab);
		pNorth.add(jcTypeJcbox);
		pNorth.add(presetLab);
		pNorth.add(presetJcbox);
		pNorth.add(findBtn);

		tableInit();
		pCenter.setLayout(new BorderLayout());
		pCenter.add(presetListJtb.getTableHeader(), BorderLayout.NORTH);
		pCenter.add(scpTable, BorderLayout.CENTER);
		scpTable.getViewport().add(presetListJtb);

		getModulePanel().setLayout(new BorderLayout());
		getModulePanel().add(pNorth, BorderLayout.NORTH);
		getModulePanel().add(pCenter, BorderLayout.CENTER);
		getModulePanel().add(createDownPane(), BorderLayout.SOUTH);

		// 查询按钮事件
		findBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable("first");
			}
		});

		// 添加工序标准按钮事件
		addPresetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new AddPresetFrame();
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

	@Override
	public void refresh() {
		System.out.println("刷新了标准预设");
	}

	// 为表格最后一行添加操作按钮
	final class MyButtonRenderer implements TableCellRenderer {

		private JPanel jpanel;

		public MyButtonRenderer() {
			jpanel = initOperate();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			return jpanel;
		}
	}

	// 设置表格最后一行按钮可操作
	final class MyButtonEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = -1418709238432901903L;
		private JPanel jpanel;

		public MyButtonEditor() {
			jpanel = initOperate();
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable jtable, Object value, boolean isselected, int row,
				int column) {
			return jpanel;
		}
	}

	/**
	 * 初始化功能操作按钮
	 * */
	private JPanel initOperate() {
		JPanel jpanel = new JPanel();
		JButton detailBtn = new JButton("查看详情/上传标准图片");
		detailBtn.setPreferredSize(new Dimension(150, 20));
		detailBtn.setName("detail");
		detailBtn.setForeground(Color.white);
		detailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		// 查看上传图片事件
		detailBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = presetListJtb.getSelectedRow();
				Long id = Long.parseLong(presetListJtb.getValueAt(row, 0) + "");
				Object[] procedureInfo = (Object[]) presetImageService.findProcedure(id);
				List<ProcedureStep> procedureStep = presetImageService.findProcedureImageById(id);

				JDialog dialog = new ImageUpload(procedureInfo, procedureStep);
				dialog.pack();
				dialog.setVisible(true);
			}
		});

		JButton editBtn = new JButton("编辑");
		editBtn.setPreferredSize(new Dimension(60, 20));
		editBtn.setName("edit");
		editBtn.setForeground(Color.white);
		editBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

		// 编辑事件
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = presetListJtb.getSelectedRow();
				Long id = Long.parseLong(presetListJtb.getValueAt(row, 0) + "");

				Object[] procedureInfo = (Object[]) presetImageService.findProcedure(id);
				JDialog dialog = new EditPreset(procedureInfo);
				dialog.pack();
				dialog.setVisible(true);
			}
		});

		JButton deleteBtn = new JButton("删除");
		deleteBtn.setPreferredSize(new Dimension(60, 20));
		deleteBtn.setName("delete");
		deleteBtn.setForeground(Color.white);
		deleteBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

		// 删除事件
		deleteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = presetListJtb.getSelectedRow();
				Long id = Long.parseLong(presetListJtb.getValueAt(row, 0) + "");
				int len = JOptionPane.showConfirmDialog(null, "确定删除吗？", "提示", JOptionPane.YES_NO_OPTION);
				if (len == 0) {
					presetImageService.deletePreset(id);
					JOptionPane.showMessageDialog(null, "删除成功", "信息", JOptionPane.INFORMATION_MESSAGE);
					refreshTable("first");
				}
			}
		});

		jpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpanel.add(detailBtn);
		jpanel.add(editBtn);
		jpanel.add(deleteBtn);
		return jpanel;
	}

	/**
	 * 表格初始化
	 * */
	private void tableInit() {
		Object[][] rowData = cteateTableData("first");
		presetListJtb = CommonComponentUtils.createTable(columnNames, rowData);
		// 设置表格列宽度
		presetListJtb.getColumn("操作").setMinWidth(270);
		presetListJtb.getColumnModel().getColumn(columnNames.length - 1).setPreferredWidth(270);
		presetListJtb.getColumnModel().getColumn(columnNames.length - 1).setCellRenderer(new MyButtonRenderer());
		presetListJtb.getColumnModel().getColumn(columnNames.length - 1).setCellEditor(new MyButtonEditor());
	}

	/**
	 * 刷新表格数据
	 */
	private void refreshTable(String pageMethod) {
		Object[][] rows = cteateTableData(pageMethod);
		CommonComponentUtils.refreshTable(presetListJtb, columnNames, rows);
		CommonComponentUtils.setTableStyle(presetListJtb, columnNames, 280, new MyButtonRenderer(),
				new MyButtonEditor());
	}

	/**
	 * 获得表格数据
	 */
	private Object[][] cteateTableData(String pageMethod) {
		String jcType = jcTypeJcbox.getSelectedItem().toString();
		String preset = presetJcbox.getSelectedItem().toString();
		List<Object> results = null;

		if ("-请选择-".equals(jcType) && "-请选择-".equals(preset)) {
			totalRows = presetImageService.findPresetsCount(null, null);
			Pager pager = PagerHelper.getPager(currentPage + "", pageMethod, totalRows);
			currentPage = pager.getCurrentPage();
			totalPages = pager.getTotalPages();
			totalLabel.setText("" + totalRows + "");
			currentPageLabel.setText("" + currentPage + "");
			totalPageLabel.setText("" + totalPages + "");
			results = presetImageService.findPresets(pager.getPageSize(), pager.getStartRow(), null, null);
		} else if ("-请选择-".equals(jcType) && !"-请选择-".equals(preset)) {
			totalRows = presetImageService.findPresetsCount(null, preset);
			Pager pager = PagerHelper.getPager(currentPage + "", pageMethod, totalRows);
			currentPage = pager.getCurrentPage();
			totalPages = pager.getTotalPages();
			totalLabel.setText("" + totalRows + "");
			currentPageLabel.setText("" + currentPage + "");
			totalPageLabel.setText("" + totalPages + "");
			results = presetImageService.findPresets(pager.getPageSize(), pager.getStartRow(), null, preset);
		} else if (!"-请选择-".equals(jcType) && "-请选择-".equals(preset)) {
			totalRows = presetImageService.findPresetsCount(jcType, null);
			Pager pager = PagerHelper.getPager(currentPage + "", pageMethod, totalRows);
			currentPage = pager.getCurrentPage();
			totalPages = pager.getTotalPages();
			totalLabel.setText("" + totalRows + "");
			currentPageLabel.setText("" + currentPage + "");
			totalPageLabel.setText("" + totalPages + "");
			results = presetImageService.findPresets(pager.getPageSize(), pager.getStartRow(), jcType, null);
		} else {
			totalRows = presetImageService.findPresetsCount(jcType, preset);
			Pager pager = PagerHelper.getPager(currentPage + "", pageMethod, totalRows);
			currentPage = pager.getCurrentPage();
			totalPages = pager.getTotalPages();
			totalLabel.setText("" + totalRows + "");
			currentPageLabel.setText("" + currentPage + "");
			totalPageLabel.setText("" + totalPages + "");
			results = presetImageService.findPresets(pager.getPageSize(), pager.getStartRow(), jcType, preset);
		}
		Integer size = results.size();
		Object[][] rowData = new Object[size][];
		List<Object> listPreset = null;
		if (null != results) {
			for (int i = 0; i < results.size(); i++) {
				listPreset = new ArrayList<Object>();
				Object[] obj = (Object[]) results.get(i);
				listPreset.add(obj[0]);// 序号
				listPreset.add(obj[1]);// 工序名称
				listPreset.add(obj[5]);// 车型
				listPreset.add(obj[10]);// 工序编号
				listPreset.add(obj[15]);// 工序大类
				listPreset.add(obj[7]);// 工序顺序
				listPreset.add(obj[8]);// 标准作业时间
				listPreset.add(obj[4]);// 标准摄像时间
				listPreset.add(obj[3]);// 拍照数量
				listPreset.add(obj[9]);// 工序间隔
				listPreset.add(obj[6]);// 备注
				listPreset.add(null);
				rowData[i] = listPreset.toArray();
			}
		}
		return rowData;
	}

	/**
	 * 创建翻页面板
	 * 
	 * @return
	 */
	private JPanel createDownPane() {
		JPanel jpan = new JPanel();
		jpan.setLayout(new FlowLayout(FlowLayout.RIGHT));
		firstBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageFirst_label"));
		CommonComponentUtils.setButtonStyle(firstBtn, 80, 24, "pagination_first.gif");
		firstBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("first");
			}
		});

		pageUpBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageUp_label"));
		CommonComponentUtils.setButtonStyle(pageUpBtn, 80, 24, "pagination_prev.gif");
		pageUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("previous");
			}
		});

		pageDownBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageDown_label"));
		CommonComponentUtils.setButtonStyle(pageDownBtn, 80, 24, "pagination_next.gif");
		pageDownBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable("next");
			}
		});

		lastBtn = new JButton(ResourceUtils.getResourceByKey("JButton.pageLast_label"));
		CommonComponentUtils.setButtonStyle(lastBtn, 80, 24, "pagination_last.gif");
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
	 * 添加标准工序窗口
	 * */
	class AddPresetFrame extends JDialog {

		private static final long serialVersionUID = -2065720034072077439L;

		private JLabel proNameLab;
		private JLabel jcTypeLab;
		private JLabel proNumLab;
		private JLabel proOrderLab;
		private JLabel workTimeLab;
		private JLabel cameraTimeLab;
		private JLabel photoNumLab;
		private JLabel workSpaceTimeLab;
		private JLabel proTypeLab;
		private JLabel noteLab;
		private JTextField proNameTxt;
		private JComboBox jcTypeBox;
		private JTextField proNumTxt;
		private JTextField proOrderTxt;
		private JTextField workTimeTxt;
		private JTextField cameraTimeTxt;
		private JTextField photoNumTxt;
		private JTextField workSpaceTimeTxt;
		private JComboBox proTypeBox;
		private JTextArea noteTxt;
		private JButton commitBtn;
		private JButton cancleBtn;

		public AddPresetFrame() {
			init();
		}

		// 初始化
		public void init() {
			Object[] jcType = presetImageService.findJcstypeAll().toArray();
			List<DictWorktype> list = presetImageService.findDictWorktype();
			List<String> datas = new ArrayList<String>();
			for (DictWorktype workType : list) {
				datas.add(workType.getWorkType());
			}

			proNameLab = new JLabel(ResourceUtils.getResourceByKey("Set.proSetName.label"));
			proNameTxt = new JTextField();
			jcTypeLab = new JLabel(ResourceUtils.getResourceByKey("Set.jcType.label"));
			jcTypeBox = new JComboBox(jcType);
			proNumLab = new JLabel(ResourceUtils.getResourceByKey("Set.proNum.label"));
			proNumTxt = new JTextField();
			proOrderLab = new JLabel(ResourceUtils.getResourceByKey("Set.proOrder.label"));
			proOrderTxt = new JTextField();
			workTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proWorkTime.label"));
			workTimeTxt = new JTextField();
			cameraTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.cameraTime.label"));
			cameraTimeTxt = new JTextField();
			photoNumLab = new JLabel(ResourceUtils.getResourceByKey("Set.photoNum.label"));
			photoNumTxt = new JTextField();
			workSpaceTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proWorkSpace.label"));
			workSpaceTimeTxt = new JTextField();
			proTypeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proType.label"));
			proTypeBox = new JComboBox(datas.toArray());
			noteLab = new JLabel(ResourceUtils.getResourceByKey("Set.note.label"));
			noteTxt = new JTextArea();
			commitBtn = new JButton(ResourceUtils.getResourceByKey("JButton.commit_label"));
			cancleBtn = new JButton(ResourceUtils.getResourceByKey("JButton.cancel_label"));

			JPanel bottomJPanel = new JPanel();
			bottomJPanel.setLayout(new BorderLayout());

			JPanel addProsetJPanel = new JPanel();
			addProsetJPanel.setLayout(null);
			bottomJPanel.add(addProsetJPanel);

			setTitle("添加标准工序");
			setMinimumSize(new Dimension(480, 600));
			setContentPane(bottomJPanel);

			addProsetJPanel.add(proNameLab);
			proNameLab.setBounds(120, 60, 120, 25);
			addProsetJPanel.add(proNameTxt);
			proNameTxt.setBounds(210, 60, 150, 25);

			addProsetJPanel.add(jcTypeLab);
			jcTypeLab.setBounds(168, 90, 120, 25);
			addProsetJPanel.add(jcTypeBox);
			jcTypeBox.setBounds(210, 90, 150, 25);

			addProsetJPanel.add(proNumLab);
			proNumLab.setBounds(144, 120, 120, 25);
			addProsetJPanel.add(proNumTxt);
			proNumTxt.setBounds(210, 120, 150, 25);

			addProsetJPanel.add(proOrderLab);
			proOrderLab.setBounds(144, 150, 120, 25);
			addProsetJPanel.add(proOrderTxt);
			proOrderTxt.setBounds(210, 150, 150, 25);

			addProsetJPanel.add(workTimeLab);
			workTimeLab.setBounds(84, 180, 200, 25);
			addProsetJPanel.add(workTimeTxt);
			workTimeTxt.setBounds(210, 180, 150, 25);

			addProsetJPanel.add(cameraTimeLab);
			cameraTimeLab.setBounds(84, 210, 200, 25);
			addProsetJPanel.add(cameraTimeTxt);
			cameraTimeTxt.setBounds(210, 210, 150, 25);

			addProsetJPanel.add(photoNumLab);
			photoNumLab.setBounds(144, 240, 120, 25);
			addProsetJPanel.add(photoNumTxt);
			photoNumTxt.setBounds(210, 240, 150, 25);

			addProsetJPanel.add(workSpaceTimeLab);
			workSpaceTimeLab.setBounds(96, 270, 200, 25);
			addProsetJPanel.add(workSpaceTimeTxt);
			workSpaceTimeTxt.setBounds(210, 270, 150, 25);

			addProsetJPanel.add(proTypeLab);
			proTypeLab.setBounds(144, 300, 120, 25);
			addProsetJPanel.add(proTypeBox);
			proTypeBox.setBounds(210, 300, 150, 25);

			addProsetJPanel.add(noteLab);
			noteLab.setBounds(168, 330, 120, 25);

			// 设置备注文本域面板
			JScrollPane scrollPane = new JScrollPane();
			addProsetJPanel.add(scrollPane);
			scrollPane.setBounds(210, 330, 200, 100);
			noteTxt.setLineWrap(true);
			scrollPane.setViewportView(noteTxt);

			addProsetJPanel.add(commitBtn);
			commitBtn.setBounds(120, 450, 100, 24);
			CommonComponentUtils.setButtonStyle(commitBtn, 100, 24, "ok.gif");
			addProsetJPanel.add(cancleBtn);
			cancleBtn.setBounds(240, 450, 100, 24);
			CommonComponentUtils.setButtonStyle(cancleBtn, 100, 24, "no.gif");

			// 取消按钮事件
			cancleBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					proNameTxt.setText("");
					proNumTxt.setText("");
					proOrderTxt.setText("");
					workTimeTxt.setText("");
					cameraTimeTxt.setText("");
					photoNumTxt.setText("");
					workSpaceTimeTxt.setText("");
					noteTxt.setText("");
					// 获得焦点
					proNameTxt.grabFocus();
				}
			});

			// 提交按钮事件
			commitBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String proName = proNameTxt.getText().trim();
					String jcType = jcTypeBox.getSelectedItem().toString();
					String proNum = proNumTxt.getText().trim();
					String proOrder = proOrderTxt.getText().trim();
					String workTime = workTimeTxt.getText().trim();
					String cameraTime = cameraTimeTxt.getText().trim();
					String photoNum = photoNumTxt.getText().trim();
					String workSpaceTime = workSpaceTimeTxt.getText().trim();
					String dictWorkType = proTypeBox.getSelectedItem().toString();
					String note = noteTxt.getText().trim();

					ProcedureInfo procedureInfo = new ProcedureInfo();
					DictWorktype dictWorktype = new DictWorktype();

					Long workId = null;
					for (DictWorktype dictWork : presetImageService.findDictWorktype()) {
						if (dictWorkType.equals(dictWork.getWorkType())) {
							workId = dictWork.getWorkId();
							break;
						}
					}
					dictWorktype.setWorkId(workId);
					if ("".equals(proName)) {
						JOptionPane.showMessageDialog(null, "工序标准名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(proNum)) {
						JOptionPane.showMessageDialog(null, "工序编号不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(proOrder)) {
						JOptionPane.showMessageDialog(null, "工序顺序不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(workTime)) {
						JOptionPane.showMessageDialog(null, "标准作业时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(cameraTime)) {
						JOptionPane.showMessageDialog(null, "标准摄像时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(photoNum)) {
						JOptionPane.showMessageDialog(null, "照片数量不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(workSpaceTime)) {
						JOptionPane.showMessageDialog(null, "工序作业时间间隔不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else {
						procedureInfo.setProName(proName);
						procedureInfo.setJcType(jcType);
						procedureInfo.setProNum(proNum);
						procedureInfo.setProSn(Integer.parseInt(proOrder));
						procedureInfo.setVideoTime(Double.valueOf(cameraTime));
						procedureInfo.setImageTimeDifference(Integer.parseInt(workTime));
						procedureInfo.setImageNum(Integer.parseInt(photoNum));
						procedureInfo.setProTimeDifference(Integer.parseInt(workSpaceTime));
						procedureInfo.setDictWorktype(dictWorktype);
						procedureInfo.setProNote(note);
						procedureInfo.setProType(2);
						procedureInfo.setShowType(1);
						presetImageService.savePreset(procedureInfo);
						JOptionPane.showMessageDialog(null, "新增成功", "信息", JOptionPane.INFORMATION_MESSAGE);
						AddPresetFrame.this.dispose();
						refreshTable("first");
					}
				}
			});

			// 初始化位置
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int width = screenSize.width;
			int height = screenSize.height;
			int x = (width - 480) / 2;
			int y = (height - 600) / 2;
			setLocation(x, y);
			setModal(true);
		}
	}

	/**
	 * 编辑面板
	 * */
	class EditPreset extends JDialog {

		private static final long serialVersionUID = -4297054079983775431L;

		private JLabel proNameLab;
		private JLabel jcTypeLab;
		private JLabel proNumLab;
		private JLabel proOrderLab;
		private JLabel workTimeLab;
		private JLabel cameraTimeLab;
		private JLabel photoNumLab;
		private JLabel workSpaceTimeLab;
		private JLabel proTypeLab;
		private JLabel noteLab;
		private JTextField proNameTxt;
		private JComboBox jcTypeBox;
		private JTextField proNumTxt;
		private JTextField proOrderTxt;
		private JTextField workTimeTxt;
		private JTextField cameraTimeTxt;
		private JTextField photoNumTxt;
		private JTextField workSpaceTimeTxt;
		private JComboBox proTypeBox;
		private JTextArea noteTxt;
		private JButton commitBtn;
		private JButton cancleBtn;
		Object[] procedure;

		public EditPreset(Object[] procedure) {
			this.procedure = procedure;
			init();
		}

		// 初始化
		public void init() {
			Object[] jcType = presetImageService.findJcstypeAll().toArray();
			List<DictWorktype> list = presetImageService.findDictWorktype();
			List<String> datas = new ArrayList<String>();
			for (DictWorktype workType : list) {
				datas.add(workType.getWorkType());
			}

			proNameLab = new JLabel(ResourceUtils.getResourceByKey("Set.proSetName.label"));
			proNameTxt = new JTextField();
			jcTypeLab = new JLabel(ResourceUtils.getResourceByKey("Set.jcType.label"));
			jcTypeBox = new JComboBox(jcType);
			proNumLab = new JLabel(ResourceUtils.getResourceByKey("Set.proNum.label"));
			proNumTxt = new JTextField();
			proOrderLab = new JLabel(ResourceUtils.getResourceByKey("Set.proOrder.label"));
			proOrderTxt = new JTextField();
			workTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proWorkTime.label"));
			workTimeTxt = new JTextField();
			cameraTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.cameraTime.label"));
			cameraTimeTxt = new JTextField();
			photoNumLab = new JLabel(ResourceUtils.getResourceByKey("Set.photoNum.label"));
			photoNumTxt = new JTextField();
			workSpaceTimeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proWorkSpace.label"));
			workSpaceTimeTxt = new JTextField();
			proTypeLab = new JLabel(ResourceUtils.getResourceByKey("Set.proType.label"));
			proTypeBox = new JComboBox(datas.toArray());
			noteLab = new JLabel(ResourceUtils.getResourceByKey("Set.note.label"));
			noteTxt = new JTextArea();
			commitBtn = new JButton(ResourceUtils.getResourceByKey("JButton.commit_label"));
			cancleBtn = new JButton(ResourceUtils.getResourceByKey("JButton.cancel_label"));

			JPanel bottomJPanel = new JPanel();
			bottomJPanel.setLayout(new BorderLayout());

			JPanel addProsetJPanel = new JPanel();
			addProsetJPanel.setLayout(null);
			bottomJPanel.add(addProsetJPanel);

			setTitle("添加标准工序");
			setMinimumSize(new Dimension(480, 600));
			setContentPane(bottomJPanel);

			addProsetJPanel.add(proNameLab);
			proNameLab.setBounds(120, 60, 120, 25);
			addProsetJPanel.add(proNameTxt);
			proNameTxt.setBounds(210, 60, 150, 25);

			addProsetJPanel.add(jcTypeLab);
			jcTypeLab.setBounds(168, 90, 120, 25);
			addProsetJPanel.add(jcTypeBox);
			jcTypeBox.setBounds(210, 90, 150, 25);

			addProsetJPanel.add(proNumLab);
			proNumLab.setBounds(144, 120, 120, 25);
			addProsetJPanel.add(proNumTxt);
			proNumTxt.setBounds(210, 120, 150, 25);

			addProsetJPanel.add(proOrderLab);
			proOrderLab.setBounds(144, 150, 120, 25);
			addProsetJPanel.add(proOrderTxt);
			proOrderTxt.setBounds(210, 150, 150, 25);

			addProsetJPanel.add(workTimeLab);
			workTimeLab.setBounds(84, 180, 200, 25);
			addProsetJPanel.add(workTimeTxt);
			workTimeTxt.setBounds(210, 180, 150, 25);

			addProsetJPanel.add(cameraTimeLab);
			cameraTimeLab.setBounds(84, 210, 200, 25);
			addProsetJPanel.add(cameraTimeTxt);
			cameraTimeTxt.setBounds(210, 210, 150, 25);

			addProsetJPanel.add(photoNumLab);
			photoNumLab.setBounds(144, 240, 120, 25);
			addProsetJPanel.add(photoNumTxt);
			photoNumTxt.setBounds(210, 240, 150, 25);

			addProsetJPanel.add(workSpaceTimeLab);
			workSpaceTimeLab.setBounds(96, 270, 200, 25);
			addProsetJPanel.add(workSpaceTimeTxt);
			workSpaceTimeTxt.setBounds(210, 270, 150, 25);

			addProsetJPanel.add(proTypeLab);
			proTypeLab.setBounds(144, 300, 120, 25);
			addProsetJPanel.add(proTypeBox);
			proTypeBox.setBounds(210, 300, 150, 25);

			addProsetJPanel.add(noteLab);
			noteLab.setBounds(168, 330, 120, 25);

			proNameTxt.setText(procedure[1].toString());// 工序名称
			jcTypeBox.setSelectedItem(procedure[5]);
			proNumTxt.setText(procedure[10].toString());// 工序编号
			proOrderTxt.setText(procedure[7].toString());// 工序顺序
			workTimeTxt.setText(procedure[8].toString());// 标准作业时间
			cameraTimeTxt.setText(procedure[4].toString());// 标准摄像时间
			photoNumTxt.setText(procedure[3].toString());// 拍照数量
			workSpaceTimeTxt.setText(procedure[9].toString());// 工序作业时间间隔
			// 工序大类
			proTypeBox.setSelectedItem(procedure[15]);

			if ("".equals(procedure[6]) || procedure[6] == null) {
				noteTxt.setText("");// 备注
			} else {
				noteTxt.setText(procedure[6].toString());// 备注
			}
			// 设置备注文本域面板
			JScrollPane scrollPane = new JScrollPane();
			addProsetJPanel.add(scrollPane);
			scrollPane.setBounds(210, 330, 200, 100);
			noteTxt.setLineWrap(true);
			scrollPane.setViewportView(noteTxt);

			addProsetJPanel.add(commitBtn);
			commitBtn.setBounds(120, 450, 100, 24);
			CommonComponentUtils.setButtonStyle(commitBtn, 100, 24, "ok.gif");
			addProsetJPanel.add(cancleBtn);
			cancleBtn.setBounds(240, 450, 100, 24);
			CommonComponentUtils.setButtonStyle(cancleBtn, 100, 24, "no.gif");

			// 取消按钮事件
			cancleBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					proNameTxt.setText("");
					proNumTxt.setText("");
					proOrderTxt.setText("");
					workTimeTxt.setText("");
					cameraTimeTxt.setText("");
					photoNumTxt.setText("");
					workSpaceTimeTxt.setText("");
					noteTxt.setText("");
					// 获得焦点
					proNameTxt.grabFocus();
				}
			});

			// 提交按钮事件
			commitBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String proName = proNameTxt.getText().trim();
					String jcType = jcTypeBox.getSelectedItem().toString();
					String proNum = proNumTxt.getText().trim();
					String proOrder = proOrderTxt.getText().trim();
					String workTime = workTimeTxt.getText().trim();
					String cameraTime = cameraTimeTxt.getText().trim();
					String photoNum = photoNumTxt.getText().trim();
					String workSpaceTime = workSpaceTimeTxt.getText().trim();
					String proType = proTypeBox.getSelectedItem().toString();
					String note = noteTxt.getText().trim();

					ProcedureInfo procedureInfo = new ProcedureInfo();
					DictWorktype dictWorktype = new DictWorktype();

					Long workId = null;
					for (DictWorktype dictWork : presetImageService.findDictWorktype()) {
						if (proType.equals(dictWork.getWorkType())) {
							workId = dictWork.getWorkId();
							break;
						}
					}
					dictWorktype.setWorkId(workId);

					if ("".equals(proName)) {
						JOptionPane.showMessageDialog(null, "工序标准名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(proNum)) {
						JOptionPane.showMessageDialog(null, "工序编号不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(proOrder)) {
						JOptionPane.showMessageDialog(null, "工序顺序不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(workTime)) {
						JOptionPane.showMessageDialog(null, "标准作业时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(cameraTime)) {
						JOptionPane.showMessageDialog(null, "标准摄像时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(photoNum)) {
						JOptionPane.showMessageDialog(null, "照片数量不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else if ("".equals(workSpaceTime)) {
						JOptionPane.showMessageDialog(null, "工序作业时间间隔不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					} else {
						procedureInfo.setProId(Long.valueOf(procedure[0].toString()));
						procedureInfo.setProName(proName);
						procedureInfo.setJcType(jcType);
						procedureInfo.setProNum(proNum);
						procedureInfo.setProSn(Integer.parseInt(proOrder));
						procedureInfo.setVideoTime(Double.valueOf(workTime));
						procedureInfo.setImageTimeDifference(Integer.parseInt(cameraTime));
						procedureInfo.setImageNum(Integer.parseInt(photoNum));
						procedureInfo.setProTimeDifference(Integer.parseInt(workSpaceTime));
						procedureInfo.setDictWorktype(dictWorktype);
						procedureInfo.setProNote(note);
						procedureInfo.setProType(2);
						procedureInfo.setShowType(1);
						presetImageService.savePreset(procedureInfo);
						JOptionPane.showMessageDialog(null, "编辑成功", "信息", JOptionPane.INFORMATION_MESSAGE);
						EditPreset.this.dispose();
						refreshTable("first");
					}
				}
			});

			// 初始化位置
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int width = screenSize.width;
			int height = screenSize.height;
			int x = (width - 480) / 2;
			int y = (height - 600) / 2;
			setLocation(x, y);
			setModal(true);
		}
	}
}