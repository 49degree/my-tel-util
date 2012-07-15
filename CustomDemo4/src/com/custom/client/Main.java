package com.custom.client;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.custom.utils.Constant;


public class Main extends JFrame{
	public static int WIDTH = 400;
	public static int HEIGHT = 300;
	static int screenX = 200;
	static int screenY = 200;
	ImageIcon img;
	public static String imgPath;
	public static String textPath;
	
	public static Main mainInstance = null;
	public Main() {
		super("资源管理");
		mainInstance = this;
		imgPath = System.getProperty("user.dir")+"\\img\\";
		textPath = System.getProperty("user.dir")+"\\text\\";
		
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

		
		Container contentPane = this.getContentPane();
		panel.setLayout(null); 
		contentPane.add(panel, BorderLayout.CENTER);
		

		//左边布局
		LeftPanel leftPanel = new LeftPanel(); 
		panel.add(leftPanel);
		
		//右边布局
		RightPanel rightPanel = new RightPanel(); 
		panel.add(rightPanel);		
		
		//按钮
		BottomPannel buttonPanel = new BottomPannel(leftPanel,rightPanel);
		buttonPanel.setLayout(null); 
		buttonPanel.setPreferredSize(new   Dimension(420,100)); 
		
		buttonPanel.setBounds(80, 315, 420, 100);
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel); 
		
		setVisible(true);
		
		//监听是否连接PAD
		new Thread(){
			public void run(){
				while(true){
					Constant.getWinSdPath(0);
					try{
						Thread.sleep(1000);
					}catch(Exception e){}
				}
			}
		}.start();
		
	}
	
	public static void createDialog(String msg){
		JDialog diolog = new JDialog(Main.mainInstance,"提示");
		JLabel lable = new JLabel(msg);
		diolog.getContentPane().add(lable, BorderLayout.CENTER);

		diolog.setLocation(Main.mainInstance.location().x+(Main.mainInstance.WIDTH-200)/2, Main.mainInstance.location().y+100);// 定位

		diolog.setSize(200, 100);
		diolog.setModal(true);
		diolog.show();
	}

	public static void main(String[] args){
		new Main();
	}
}
