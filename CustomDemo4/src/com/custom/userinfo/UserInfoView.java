package com.custom.userinfo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.Main;
import com.custom.client.RightPanel;
import com.custom.utils.Constant;
import com.custom.utils.Logger;

public class UserInfoView {
	private static final Logger logger = Logger.getLogger(UserInfoView.class);
    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    
    JPanel namePanel = null;
    JPanel passwordPanel = null;
    
    JPanel mainPanel = null;
    
    ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
    
    
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
    	namePanel.setBounds(10, 10, 230, 180);
    	
    	passwordPanel = new JPanel();
    	passwordPanel.setOpaque(false);//背景色设为透明的了       
    	passwordPanel.setLayout(null); 
    	passwordPanel.setBounds(10, 10, 230, 180);
    	
    	
    	
    	mainPanel = new JPanel();
    	mainPanel.setOpaque(false);//背景色设为透明的了       
    	mainPanel.setLayout(null); 
    	mainPanel.setBounds(10, 10, 230, 215);
    	
    	
    	
		JButton nameBtn = new MyButton(backguandPic);
		nameBtn.setText("昵称");
		nameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				creatNameView();
				mainPanel.removeAll();
				mainPanel.add(namePanel);
				mainPanel.repaint();
			}
		});
		nameBtn.setBounds(50, 200, 60, 20);
		
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setText("密码");
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				creatPasswordView();
				mainPanel.removeAll();
				mainPanel.add(passwordPanel);
				mainPanel.repaint();
			}
		});
		psdBtn.setBounds(130, 200, 60, 20);
		
		creatNameView();
		mainPanel.removeAll();
		mainPanel.add(namePanel);
		
		this.rightPanel.setPannel(mainPanel);
		this.rightPanel.setPannel(nameBtn);
		this.rightPanel.setPannel(psdBtn);
	}
	
	public void creatNameView(){
    	JLabel  nameLeble = new JLabel("昵称：");
    	nameLeble.setOpaque(false);//背景色设为透明的了       
    	nameLeble.setLayout(null); 
    	nameLeble.setBounds(30, 50, 40, 20);
    	
    	final JTextField nameEdit = new JTextField();
    	nameEdit.setOpaque(false);//背景色设为透明的了       
    	nameEdit.setLayout(null); 
    	nameEdit.setBounds(70, 50, 80, 20);
    	
    	final Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
		}catch(Exception e){
			
		}
		if(properties.getProperty(Constant.userName)!=null&&
				!"".equals(properties.getProperty(Constant.userName).trim())){
			nameEdit.setText(properties.getProperty(Constant.userName));
		}
    	
		JButton nameBtn = new MyButton(backguandPic);
		nameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(nameEdit.getText()==null||"".equals(nameEdit.getText().trim())){
					Main.createDialog("昵称不能为空");
				}else{
					try{
						
						properties.setProperty(Constant.userName, nameEdit.getText());
						FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")
								+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG); 
						
						properties.store(fos, "");
						fos.close();// 关闭流 
						Main.createDialog("修改成功");
					}catch(Exception e1){
						
					}
				}

			}
		});
		nameBtn.setBounds(60, 90, 50, 20);
		nameBtn.setText("修改");
		
		namePanel.removeAll();
    	namePanel.add(nameLeble);
    	namePanel.add(nameEdit);
    	namePanel.add(nameBtn);
    	
	}
	
	public void creatPasswordView(){
		passwordPanel.removeAll();
    	final Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
		}catch(Exception e){
			
		}
		
		
		
    	JLabel  psdLeble = new JLabel("原密码：");
    	psdLeble.setOpaque(false);//背景色设为透明的了       
    	psdLeble.setLayout(null); 
    	psdLeble.setBounds(30, 10, 60, 20);
    	
    	final JPasswordField psdEdit = new JPasswordField();
    	psdEdit.setOpaque(false);//背景色设为透明的了       
    	psdEdit.setLayout(null); 
    	psdEdit.setBounds(80, 10, 70, 20);

		
		if(properties.getProperty("userPsd")!=null&&
				!"".equals(properties.getProperty("userPsd").trim())){
			passwordPanel.add(psdLeble);
			passwordPanel.add(psdEdit);
		}
    	
    	JLabel  npsdLeble = new JLabel("新密码：");
    	npsdLeble.setOpaque(false);//背景色设为透明的了       
    	npsdLeble.setLayout(null); 
    	npsdLeble.setBounds(30, 40, 60, 20);
    	
    	final JPasswordField npsdEdit = new JPasswordField();
    	npsdEdit.setOpaque(false);//背景色设为透明的了       
    	npsdEdit.setLayout(null); 
    	npsdEdit.setBounds(80, 40, 70, 20);

    	
    	JLabel  npsdLeble1 = new JLabel("重复新密码：");
    	npsdLeble1.setOpaque(false);//背景色设为透明的了       
    	npsdLeble1.setLayout(null); 
    	npsdLeble1.setBounds(5, 70, 85, 20);
    	
    	final JPasswordField npsdEdit1 = new JPasswordField();
    	npsdEdit1.setOpaque(false);//背景色设为透明的了       
    	npsdEdit1.setLayout(null); 
    	npsdEdit1.setBounds(80, 70, 70, 20);
    	


		
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug(properties.getProperty(Constant.userPsd));
				if(properties.getProperty(Constant.userPsd)!=null&&
						!"".equals(properties.getProperty(Constant.userPsd).trim())){
					if(!properties.getProperty(Constant.userPsd).equals(psdEdit.getText())){
						Main.createDialog("旧密码错误");
						return;
					}
						
				}
				if(npsdEdit.getText()==null||npsdEdit1.getText()==null){
					Main.createDialog("密码不能为空");
					return;
				}
				if("".equals(npsdEdit.getText().trim())||"".equals(npsdEdit1.getText().trim())){
					Main.createDialog("密码不能为空");
					return;
				}
				if(!npsdEdit.getText().equals(npsdEdit1.getText())){
					Main.createDialog("两次输入密码不同");
					return;
				}
				
				try{
					
					properties.setProperty(Constant.userPsd, npsdEdit.getText());
					FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")
							+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG); 
					
					properties.store(fos, "");
					fos.close();// 关闭流 
					Main.createDialog("修改成功");
				}catch(Exception e1){
					
				}				
				psdEdit.setText("");
				npsdEdit.setText("");
				npsdEdit1.setText("");
			}
		});
		psdBtn.setBounds(60, 110, 50, 20);
		psdBtn.setText("修改");
		
		passwordPanel.add(npsdLeble);
		passwordPanel.add(npsdEdit);
		passwordPanel.add(npsdLeble1);
		passwordPanel.add(npsdEdit1);
		passwordPanel.add(psdBtn);
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
