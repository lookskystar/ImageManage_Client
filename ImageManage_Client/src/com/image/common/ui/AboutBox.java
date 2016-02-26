package com.image.common.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.image.common.util.ImageUtils;
import com.image.common.util.ResourceUtils;

public class AboutBox extends JDialog {
	private static final long serialVersionUID = -9152075985505905857L;

	ImageIcon aboutimage = null;
	MainSet mainSet = null;

	public AboutBox(MainSet mainSet) {
		
		super(mainSet.getFrame(),ResourceUtils.getResourceByKey("AboutBox.title"), false);
		setResizable(false);
		
		JPanel panel = new AboutPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
		
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 3, 0));
//		buttonPanel.setOpaque(false);
//		JButton button = (JButton) buttonPanel.add(new JButton(ResourceUtils.getResourceByKey("AboutBox.ok_button_text")));
//		panel.add(buttonPanel, BorderLayout.SOUTH);
//		button.addActionListener(new OkAction(this));
	}
	
	/**
	 * 显示图片面板
	 */
	class AboutPanel extends JPanel {
		private static final long serialVersionUID = 3948766915149120691L;
		ImageIcon aboutimage = null;

		public AboutPanel() {
			aboutimage = ImageUtils.createImageIcon("About.jpg");
			setOpaque(false);
		}

		public void paint(Graphics g) {
			aboutimage.paintIcon(this, g, 0, 0);
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			return new Dimension(aboutimage.getIconWidth(),
					aboutimage.getIconHeight());
		}
	}
	
	/**
	 * 确定按钮事件
	 */
	class OkAction extends AbstractAction {
		private static final long serialVersionUID = -5505213315879036858L;
		JDialog aboutBox;
		
		protected OkAction(JDialog aboutBox) {
			super("OkAction");
			this.aboutBox = aboutBox;
		}
		
		public void actionPerformed(ActionEvent e) {
			aboutBox.setVisible(false);
		}
	}
}
