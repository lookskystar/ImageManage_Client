package com.image.set.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.image.common.util.CommonComponentUtils;
import com.image.common.util.HessianServiceFactory;
import com.image.work.service.DeviceService;

/**
 * 设备履历信息
 * @author dell
 *
 */
public class RecordDeviceDialog extends JDialog{

	private static final long serialVersionUID = -5388413186499612597L;
	private Long devicesId;//设备ID
	private JTable jtable;
	private static final String[] columnNames={"设备型号","设备编码", "操作人", "班组" ,"操作类型" ,"操作时间","备注"};
	
	private static DeviceService deviceService;
	static{
		HessianServiceFactory factory=HessianServiceFactory.getInstance();
		try {
			deviceService=(DeviceService)factory.createService(DeviceService.class, "deviceService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 */
	public RecordDeviceDialog(Long devicesId){
		this.devicesId=devicesId;
		JScrollPane panel=createTablePanel();
		add(panel);
		setTitle("设备履历信息");
		setResizable(false);//设置不能自己拖动窗口大小
		setMinimumSize(new Dimension(1000, 450));
		//初始化位置
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int width=screenSize.width;
		int height=screenSize.height;
		int x=(width-1000)/2;
		int y=(height-450)/2;
		setLocation(x, y);//初始化位置
		setModal(true);
	}
	
	/**
	 * 创建表格面板
	 * @return
	 */
	private JScrollPane createTablePanel(){
		Object[][] rows=cteateTableData();
		jtable=CommonComponentUtils.createTable(columnNames, rows);
		JScrollPane jscrollPane=new JScrollPane(jtable);
		return jscrollPane;
	}
	
	/**
	 * 创建表格数据
	 * @return
	 */
	private Object[][] cteateTableData(){
		List<Object[]> list= deviceService.findResume(devicesId);
		Object[][] objs=null;
		if(list!=null&&list.size()>0){
			objs=new Object[list.size()][];
			for(int i=0;i<list.size();i++){
				objs[i]=list.get(i);
			}
		}
		return objs;
	}
}
