package com.custom.client;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LeftConnectView extends JPanel{
	
	public LeftConnectView(){
		super();
		
		//已经连接左边视图
		setOpaque(false);//背景色设为透明的了
		setLayout(null); 
		setPreferredSize(new   Dimension(420,100)); 
		setBounds(10, 10, 110, 220);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		final JLabel  label = new JLabel ();
		label.setText("PAD已经连接电脑");
		label.setOpaque(false);//背景色设为透明的了       
		label.setLayout(null); 
		label.setPreferredSize(new   Dimension(420,100)); 
		label.setBounds(0, 10, 110, 50);
		label.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		final JLabel  label2 = new JLabel ();
		label2.setText("昵称：");
		label2.setOpaque(false);//背景色设为透明的了       
		label2.setLayout(null); 
		label2.setPreferredSize(new   Dimension(420,100)); 
		label2.setBounds(0, 50, 110, 50);
		label2.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		final JLabel  label3 = new JLabel ();
		label3.setText("PAD已经连接电脑");
		label3.setOpaque(false);//背景色设为透明的了       
		label3.setLayout(null); 
		label3.setPreferredSize(new   Dimension(420,100)); 
		label3.setBounds(0, 100, 110, 50);
		label3.setBorder(BorderFactory.createLineBorder(Color.black)); 
		
		add(label);
		add(label2);
		add(label3);

	}
}
