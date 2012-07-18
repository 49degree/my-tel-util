package com.custom.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class LeftPanel extends JPanel {
	public LeftPanel(){
		super();
		
		setLayout(null); 
		setPreferredSize(new   Dimension(420,100)); 
		setBounds(95, 65, 125, 235);
		//setBorder(BorderFactory.createLineBorder(Color.black)); 
		setOpaque(false);
		


	}
	
	public void setPannel(Component comp){
		this.removeAll();
		this.add(comp);
		this.doLayout();
		this.repaint();
	}
}
