package com.custom.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LeftPanel extends JPanel {
	public LeftPanel(){
		super();
		setOpaque(false);//背景色设为透明的了
		JLabel  label = new JLabel ();
		label.setText("PAD已经连接电脑");
		
		label.setOpaque(false);//背景色设为透明的了
        
		label.setLayout(null); 
		label.setPreferredSize(new   Dimension(420,100)); 
		label.setBounds(10, 50, 110, 50);
		label.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		add(label);
		final ImageIcon img = new ImageIcon(Main.imgPath+"update_left.png");
		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(img.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
        panel.setOpaque(false);//背景色设为透明的了
        
        panel.setLayout(null); 
        panel.setPreferredSize(new   Dimension(420,100)); 
        panel.setBounds(10, 110, 110, 120);
        panel.setBorder(BorderFactory.createLineBorder(Color.black)); 
        
        add(panel);
	}

}
