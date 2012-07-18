package com.custom.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RightPanel extends JPanel {
	public RightPanel(){
		super();
		setOpaque(false);//背景色设为透明的了
		setLayout(null); 
		setPreferredSize(new   Dimension(420,100)); 
		setBounds(228, 65, 250, 235);
		//setBorder(BorderFactory.createLineBorder(Color.black)); 
        
	}
	
	public void setPannel(Component comp){
		//this.removeAll();
		this.add(comp);
		this.doLayout();
		this.repaint();
	}

}
