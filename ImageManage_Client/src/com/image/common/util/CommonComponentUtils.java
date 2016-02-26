package com.image.common.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

/**
 * 常用控件工具类
 * @author Administrator
 *
 */
public class CommonComponentUtils {
	
	/**
	 * 创建菜单栏选项
	 * @param menu 父级菜单
	 * @param label 选项名字
	 * @param mnemonic 选项快捷键
	 * @param desc 选项描述
	 * @param action 选项触发事件
	 * @param enabled 是否可用
	 * @return
	 */
	public static JMenuItem createMenuItem(JMenu menu, String label, char mnemonic,
			String desc, Action action,boolean enabled) {
		JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(label));
		mi.setMnemonic(mnemonic);
		mi.getAccessibleContext().setAccessibleDescription(desc);
		mi.addActionListener(action);
		mi.setEnabled(enabled);
		return mi;
	}
	
	/**
	 * 创建弹出菜单项
	 * @param menu 父级菜单
	 * @param label 选项名字
	 * @param mnemonic 选项快捷键
	 * @param desc 选项描述
	 * @param action 选项触发事件
	 * @param enabled 是否可用
	 */
	public static JMenuItem createPopupMenuItem(JPopupMenu menu, String label, char mnemonic,
			String desc,Action action,boolean enabled) {
		JMenuItem mi = menu.add(new JMenuItem(label));
		mi.setMnemonic(mnemonic);
		mi.getAccessibleContext().setAccessibleDescription(desc);
		mi.addActionListener(action);
		mi.setEnabled(enabled);
		return mi;
	}
	
	/**
	 * 创建弹出菜单项
	 * @param menu 父级菜单
	 * @param label 选项名字
	 * @param desc 选项描述
	 * @param action 选项触发事件
	 * @param enabled 是否可用
	 */
	public static MenuItem createPopupMenuItem2(PopupMenu menu, String label,String desc,Action action,boolean enabled) {
		MenuItem mi = menu.add(new MenuItem(label));
		mi.getAccessibleContext().setAccessibleDescription(desc);
		mi.addActionListener(action);
		mi.setEnabled(enabled);
		return mi;
	}
	
	/**
	 * 创建表格
	 * @param columnNames:列名称
	 * @param rows 数据
	 * @return
	 */
	public static JTable createTable(final String[] columnNames,Object[][] rows){
		TableModel model = new DefaultTableModel(rows, columnNames){
			private static final long serialVersionUID = -5766126580008309636L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if(column==columnNames.length-1){
					return true;
				}
				return false;
			}
			
		};  
		JTable table=new JTable(model);
		//table.setEnabled(false);
		//设置表格列名居中
		JTableHeader head=table.getTableHeader();
		DefaultTableCellRenderer hr=(DefaultTableCellRenderer)head.getDefaultRenderer();
		hr.setHorizontalAlignment(SwingConstants.CENTER);
		//设置表格内容居中
		DefaultTableCellRenderer tcr=new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, tcr);
		// 设置列标题不能移动   
		table.getTableHeader().setReorderingAllowed(false);   
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   

		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);  
		table.setRowSorter(sorter); 
		return table;
	}
	
	/**
	 * 设置JTable样式
	 * @param jtable:需要设置样式的JTable
	 * @param columnNames:JTable列名称数组
	 * @param preferredWidth:设置最后一列宽度
	 * @param cellRenderer:设置最后一列cellRenderer,用户自定义
	 * @param cellEditor：设置最后一列cellEditor,用户自定义
	 * @return
	 */
	public static JTable setTableStyle(JTable jtable,String[] columnNames,
			int preferredWidth,TableCellRenderer cellRenderer,TableCellEditor cellEditor){
		jtable.getColumnModel().getColumn(columnNames.length-1).setPreferredWidth(preferredWidth);
		jtable.getColumnModel().getColumn(columnNames.length-1).setCellRenderer(cellRenderer);
		jtable.getColumnModel().getColumn(columnNames.length-1).setCellEditor(cellEditor);
		return jtable;
	}
	
	/**
	 * 刷新表格数据,如果最后一行有操作表格，调用下面方面后需要调用上面setTableStyle方法
	 * @param jtable:要刷新的JTable
	 * @param columnNames:JTable列名称
	 * @param datas：新数据
	 */
	public static void refreshTable(JTable jtable,String[] columnNames,Object[][] datas){
		DefaultTableModel tableModel=(DefaultTableModel)jtable.getModel();
		tableModel.setDataVector(datas, columnNames);
	}
	
	/**
	 * 设置按钮样式
	 * @param jbutton:要设置的按钮
	 * @param width:按钮的宽度
	 * @param height:按钮的高度
	 * @param imageName:图片的名称,没有设置为为null
	 */
	public static void setButtonStyle(JButton jbutton,int width,int height,String imageName){
		jbutton.setPreferredSize(new Dimension(width,height));
		if(imageName!=null){
			jbutton.setIcon(ImageUtils.createImageIcon(imageName));
		}
		jbutton.setForeground(Color.white);
		jbutton.setUI(new
        		BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
	}
}
