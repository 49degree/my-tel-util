package com.custom.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Main extends JFrame{
	static int WIDTH = 400;
	static int HEIGHT = 300;
	static int screenX = 200;
	static int screenY = 200;
	ImageIcon img;
	public static String imgPath;
	public Main() {
		super("资源管理");
		imgPath = System.getProperty("user.dir")+"\\img\\";
		img = new ImageIcon(imgPath+"Backgroud.png");
		
		WIDTH = img.getIconWidth();
		HEIGHT = img.getIconHeight();
		
		setSize(WIDTH, HEIGHT);
		setLocation(screenX, screenY);// 定位
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.setUndecorated(true);

		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(img.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
        panel.setOpaque(false);//背景色设为透明的了

        try {
            //jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
		Container contentPane = this.getContentPane();
		panel.setLayout(null); 
		contentPane.add(panel, BorderLayout.CENTER);
		

		//左边布局
		JPanel leftPanel = new JPanel(); 
		leftPanel.setLayout(null); 
		leftPanel.setPreferredSize(new   Dimension(420,100)); 
		leftPanel.setBounds(95, 65, 125, 235);
		leftPanel.setBorder(BorderFactory.createLineBorder(Color.black)); 
		leftPanel.setOpaque(false);
		panel.add(leftPanel);
		
		//右边布局
		JPanel rightPanel = new JPanel(); 
		rightPanel.setLayout(null); 
		rightPanel.setPreferredSize(new   Dimension(420,100)); 
		rightPanel.setBounds(228, 65, 250, 235);
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.black)); 
		rightPanel.setOpaque(false);
		panel.add(rightPanel);		
		
		//按钮
		BottomPannel buttonPanel = new BottomPannel(leftPanel,rightPanel);
		buttonPanel.setLayout(null); 
		buttonPanel.setPreferredSize(new   Dimension(420,100)); 
		
		buttonPanel.setBounds(80, 315, 420, 100);
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel); 
		
		setVisible(true);
	}


	public static void main(String[] args){
		new Main();
	}
}
