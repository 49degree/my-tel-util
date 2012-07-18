package com.custom.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class LeftNoConnectView extends JPanel{
	ImageIcon img = null;
	public LeftNoConnectView(){
		super();
		
		//未连接左边视图
		img = new ImageIcon(Main.imgPath+"unlink.png");

		setOpaque(false);//背景色设为透明的了
		setLayout(null); 
		setPreferredSize(new   Dimension(420,100)); 
		setBounds(10, 10, img.getIconWidth(), img.getIconHeight());
		//setBorder(BorderFactory.createLineBorder(Color.black));

	}

	public void paintComponent(Graphics g) {
		g.drawImage(img.getImage(), 0, 0, null);
		super.paintComponent(g);
	}
}
