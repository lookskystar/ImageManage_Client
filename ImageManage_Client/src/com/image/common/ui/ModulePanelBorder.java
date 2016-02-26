package com.image.common.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import javax.swing.border.AbstractBorder;

/**
 * 面板边框
 * @author Administrator
 */
public class ModulePanelBorder extends AbstractBorder{
	
	private static final long serialVersionUID = -6580368069152526742L;

	public ModulePanelBorder() {
		
	}

	public void paintBorder(Component c, Graphics g, int x, int y, 
			int width, int height) 
	{
		//** 绘制border的底线
		//虚线样式
		Stroke oldStroke = ((Graphics2D)g).getStroke();
		Stroke sroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0);//实线，空白
		((Graphics2D)g).setStroke(sroke);
		//底边上（浅灰色）
		g.setColor(new Color(200,200,200));
		g.drawLine(x,height-2, width-1,height-2); // draw bottom1
		//底边下（白色）：绘制一条白色虚线的目的是与上面的灰线产生较强对比度从而形成立体效果
		//，本L&F实现中因与Panel的底色对比度不够强烈而立体感不明显（颜色越深的底色最终效果越明显）
		g.setColor(Color.white);
		g.drawLine(x,height-1, width-1,height-1);//draw bottom2
		
		((Graphics2D)g).setStroke(oldStroke);
	}

	//border只有底边，且高度为2像素
	/* (non-Javadoc)
	 * @see javax.swing.border.AbstractBorder#getBorderInsets(java.awt.Component)
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(0,0,2,0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.border.AbstractBorder#getBorderInsets(java.awt.Component, java.awt.Insets)
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		return getBorderInsets(c);//insets;
	}
}
