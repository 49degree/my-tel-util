package com.custom.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.custom.utils.Constant;

public class LeftConnectView extends JPanel{
	
	public LeftConnectView(String imgName){
		super();
		
		//已经连接左边视图
		setOpaque(false);//背景色设为透明的了
		setLayout(null); 
		setPreferredSize(new   Dimension(420,100)); 
		setBounds(10, 10, 110, 220);
		//setBorder(BorderFactory.createLineBorder(Color.black));
		
		final JLabel  label = new JLabel ();
		label.setText("PAD已经连接电脑");
		label.setOpaque(false);//背景色设为透明的了       
		label.setLayout(null); 
		label.setPreferredSize(new   Dimension(420,100)); 
		label.setBounds(0, 10, 110, 50);
		//label.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		
		
    	final Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
		}catch(Exception e){
			
		}
		
		final JLabel  label2 = new JLabel ();
		label2.setText("昵称："+properties.getProperty(Constant.userName,""));
		label2.setOpaque(false);//背景色设为透明的了       
		label2.setLayout(null); 
		label2.setPreferredSize(new   Dimension(420,100)); 
		label2.setBounds(0, 50, 110, 40);
		//label2.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		//未连接左边视图
		final ImageIcon img = new ImageIcon(Main.imgPath+imgName);

		JPanel panelRight = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(img.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		
		panelRight.setOpaque(false);//背景色设为透明的了
		panelRight.setLayout(null); 
		panelRight.setPreferredSize(new   Dimension(420,100)); 
		panelRight.setBounds(5, 95, img.getIconWidth(), img.getIconHeight());
		//panelRight.setBorder(BorderFactory.createLineBorder(Color.black));
		
		
		add(label);
		add(label2);
		add(panelRight);

	}
}
