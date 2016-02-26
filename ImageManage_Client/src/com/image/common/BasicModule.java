package com.image.common;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jb2011.lnf.beautyeye.widget.N9ComponentFactory;

import com.image.common.ui.MainSet;
import com.image.common.util.ImageUtils;
import com.image.common.util.ResourceUtils;

public abstract class BasicModule extends JApplet {
	
	private static final long serialVersionUID = 2645452963925543001L;

	//预定义宽度
    private int PREFERRED_WIDTH = 680;
    
    //预定义高度
    private int PREFERRED_HEIGHT = 600;

    Border loweredBorder =  new EmptyBorder(15,10,5,10);

    public static Dimension HGAP2 = new Dimension(2,1);
    public static Dimension VGAP2 = new Dimension(1,2);
    public static Dimension HGAP5 = new Dimension(5,1);
    public static Dimension VGAP5 = new Dimension(1,5);
    public static Dimension HGAP10 = new Dimension(10,1);
    public static Dimension VGAP10 = new Dimension(1,10);
    public static Dimension HGAP15 = new Dimension(15,1);
    public static Dimension VGAP15 = new Dimension(1,15);
    public static Dimension HGAP20 = new Dimension(20,1);
    public static Dimension VGAP20 = new Dimension(1,20);
    public static Dimension HGAP25 = new Dimension(25,1);
    public static Dimension VGAP25 = new Dimension(1,25);
    public static Dimension HGAP30 = new Dimension(30,1);
    public static Dimension VGAP30 = new Dimension(1,30);
	
    private MainSet mainSet = null;
    private JPanel panel = null;
    private String name = null;
    private String iconPath = null;
    
    public BasicModule(MainSet mainSet){
    	this(mainSet,null,null);
    }
    
    public BasicModule(MainSet mainSet,String name, String iconPath) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        this.mainSet = mainSet;
        this.name = name;
    	this.iconPath = iconPath;
    }

    /**
     * 显示
     */
    public void mainImpl() {
    	JFrame frame = new JFrame(getName());
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(getModulePanel(), BorderLayout.CENTER);
        getModulePanel().setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		frame.pack();
		frame.setVisible(true);
    }

    /**
     * 创建水平面板
     */
    public JPanel createHorizontalPanel(boolean threeD) {
        JPanel p = N9ComponentFactory.createPanel_style1(null).setDrawBg(threeD);//modified by jb2011
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setAlignmentY(TOP_ALIGNMENT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        if(threeD) {
            p.setBorder(loweredBorder);
        }
        //因背景是白色N9图，这里设置它不填充默认背景好看一点，要不然灰色背景出来就不好看了
        p.setOpaque(false);
        return p;
    }
    
    /**
     * 创建垂直面板
     */
    public JPanel createVerticalPanel(boolean threeD) {
    	JPanel p = N9ComponentFactory.createPanel_style1(null).setDrawBg(threeD);//modified by jb2011
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentY(TOP_ALIGNMENT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        if(threeD) {
            p.setBorder(loweredBorder);
        }
        return p;
    }

    /* 
     * 初始化
     */
    public void init() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getModulePanel(), BorderLayout.CENTER);
    }
    
    /**
     * 刷新
     * @return
     */
    public abstract void refresh();
    
    public JPanel getModulePanel() {
    	return panel;
    }
    
    public MainSet getMainSet() {
    	return this.mainSet;
    }

	public String getName() {
		return ResourceUtils.getResourceByKey(name);
	}

	public Icon getIcon() {
		if(iconPath==null){
			return null;
		}else{
			return ImageUtils.createImageIcon(iconPath);
		}
	}
}

