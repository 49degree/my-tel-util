package com.custom.userinfo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.RightPanel;
import com.custom.utils.Logger;

public class UserInfoView {
	private static final Logger logger = Logger.getLogger(UserInfoView.class);
    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    
    JPanel namePanel = null;
    JPanel passwordPanel = null;
	public UserInfoView(LeftPanel leftPanel,RightPanel rightPanel) {
    	this.leftPanel = leftPanel;
    	this.rightPanel = rightPanel; 
    	this.leftPanel.removeAll();
    	this.rightPanel.removeAll();
    	
		//已经连接左边视图
		final JPanel connectPanel = new LeftConnectView();
		leftPanel.setPannel(connectPanel);
    	
    	
        //连接右边视图
    	namePanel = new JPanel();
    	namePanel.setOpaque(false);//背景色设为透明的了       
    	namePanel.setLayout(null); 
    	namePanel.setBounds(10, 10, 230, 215);
    	
    	passwordPanel = new JPanel();
    	passwordPanel.setOpaque(false);//背景色设为透明的了       
    	passwordPanel.setLayout(null); 
    	passwordPanel.setBounds(10, 10, 230, 215);
    	
    	JLabel  nameLeble = new JLabel("昵称：");
    	nameLeble.setOpaque(false);//背景色设为透明的了       
    	nameLeble.setLayout(null); 
    	nameLeble.setBounds(30, 30, 40, 20);
    	
    	JTextField nameEdit = new JTextField();
    	nameEdit.setOpaque(false);//背景色设为透明的了       
    	nameEdit.setLayout(null); 
    	nameEdit.setBounds(70, 30, 40, 60);
    	
    	
		JButton passwdBtn = new MyButton(null);
		passwdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		passwdBtn.setBounds(70, 30, 50, 60);
		
    	namePanel.add(nameLeble);
    	namePanel.add(nameEdit);
    	
    	this.rightPanel.setPannel(namePanel);
    	
    	
    	
	}
	
	
	public class MyButton extends JButton{
		public MyButton(ImageIcon icon){
			super();
			setIcon(icon);
		    setBackground(new Color(0,0,0,0));
		    setBorder(null);
		    setHorizontalTextPosition(SwingConstants.CENTER);   
		    setVerticalTextPosition(SwingConstants.CENTER);  
		    setForeground(Color.WHITE);
		}
	}
}
