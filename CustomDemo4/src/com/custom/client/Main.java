package com.custom.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.custom.utils.Constant;
import com.custom.utils.LoadResources;


public class Main extends JFrame{
	public static int WIDTH = 400;
	public static int HEIGHT = 300;
	static int screenX = 200;
	static int screenY = 200;
	ImageIcon img;
	public static String imgPath;
	public static String textPath;
	
	public static Main mainInstance = null;
	private JPanel panel = null;
	public Main() {
		super("资源管理");
		this.setResizable(false);
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

		panel = new JPanel() {
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
		//帮助按钮
		createHelpButton();
		createMinButton();
		createCloseButton();
		
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
		
		
		
    	final Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
		}catch(Exception e){
			
		}
		if(properties.getProperty(Constant.userPsd)!=null&&
				!"".equals(properties.getProperty(Constant.userPsd).trim())){
			createCheckPsdDialog();
		}
		
		this.addWindowListener(new WindowAdapter(){
            //捕获窗口关闭事件
            public void windowClosing(WindowEvent e){            
              //窗口关闭时的相应处理操作
            }
            //捕获窗口最小化事件
            public void windowIconified(WindowEvent e){
                //窗口最小化时的相应处理操作

            }
        });

		
	}
	HelpBottomPannel helpBottomPannel = null;
	public void createHelpButton(){
		ImageIcon backguandPic = new ImageIcon(Main.imgPath+"about_n.png");
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setBounds(407, 25, backguandPic.getIconWidth(), backguandPic.getIconHeight());
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(helpBottomPannel!=null){
					helpBottomPannel.hide();
					panel.remove(helpBottomPannel);
					helpBottomPannel = null;
					panel.repaint();
				}else{
					helpBottomPannel = new HelpBottomPannel();
					panel.add(helpBottomPannel);
					panel.repaint();
				}


			}
		});
		panel.add(psdBtn);
	}
	
	
	public void createMinButton(){
		ImageIcon backguandPic = new ImageIcon(Main.imgPath+"min_n.png");
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setBounds(432, 25, backguandPic.getIconWidth(), backguandPic.getIconHeight());
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.this.setExtendedState(Frame.ICONIFIED);
			}
		});
		panel.add(psdBtn);
	}
	
	
	public void createCloseButton(){
		ImageIcon backguandPic = new ImageIcon(Main.imgPath+"close_n.png");
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setBounds(457, 25, backguandPic.getIconWidth(), backguandPic.getIconHeight());
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  System.exit(0);	
			}
		});
		panel.add(psdBtn);
	}
	
	public void createCheckPsdDialog(){
		final ImageIcon pic = new ImageIcon(Main.imgPath+"psd_message.png");
		final JDialog diolog = new JDialog(Main.mainInstance,"提示");
		((JPanel) diolog.getContentPane()).setOpaque(false); 
		diolog.setUndecorated(true); 
		
    	
		JPanel passwordPanel = new JPanel(){
			public void paintComponent(Graphics g) {
				g.drawImage(pic.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		
    	passwordPanel.setOpaque(false);//背景色设为透明的了       
    	passwordPanel.setLayout(null); 
    	passwordPanel.setBounds(10, 10, 230, 180);
		
    	JLabel  psdLeble = new JLabel("请输入密码：");
    	psdLeble.setOpaque(false);//背景色设为透明的了       
    	psdLeble.setLayout(null); 
    	psdLeble.setBounds(20, 40, 80, 20);
		
    	final JPasswordField psdEdit = new JPasswordField();
    	psdEdit.setOpaque(false);//背景色设为透明的了       
    	psdEdit.setLayout(null); 
    	psdEdit.setBounds(100, 40, 80, 20);
    	
    	ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setText("确定");
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			   	final Properties properties = new Properties();
				try{
					properties.load(new FileInputStream(System.getProperty("user.dir")
							+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
				}catch(Exception e1){
					
				}
				if(properties.getProperty(Constant.userPsd).equals(psdEdit.getText())){
					diolog.hide();
				}else{
					psdEdit.setText("");
					createDialog("密码错误");
				}
			}
		});
		psdBtn.setBounds(40, 100, 60, 20);
		
		
		JButton cancelBtn = new MyButton(backguandPic);
		cancelBtn.setText("取消");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
				Main.this.hide();
			}
		});
		cancelBtn.setBounds(110, 100, 60, 20);
		
    	passwordPanel.add(psdLeble);
    	passwordPanel.add(psdEdit);
    	passwordPanel.add(psdBtn);
    	passwordPanel.add(cancelBtn);
    	
    	
    	diolog.getContentPane().add(passwordPanel, BorderLayout.CENTER);
    	
		diolog.setLocation(Main.mainInstance.location().x+(Main.mainInstance.WIDTH-pic.getIconWidth())/2, Main.mainInstance.location().y+100);// 定位

		diolog.setSize(pic.getIconWidth(), pic.getIconHeight());
		diolog.setModal(true);
		diolog.show();
	}
	
	public static void createDialog(String msg){
		createDialog("prompt_message.png",msg,null);
	}
	public static void createDialog(String picName,String msg){
		createDialog(picName,msg,null);
	}
	
	public static void createDialog(String picName,String msg,final DialogClose dialogClose){
		final ImageIcon pic = new ImageIcon(Main.imgPath+picName);
		final JDialog diolog = new JDialog(Main.mainInstance,"提示");
		((JPanel) diolog.getContentPane()).setOpaque(false); 
		diolog.setUndecorated(true); 
		
    	
		JPanel passwordPanel = new JPanel(){
			public void paintComponent(Graphics g) {
				g.drawImage(pic.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		
    	passwordPanel.setOpaque(false);//背景色设为透明的了       
    	passwordPanel.setLayout(null); 
    	passwordPanel.setBounds(10, 10, 200, 180);
		
        final JTextArea msgtext=new JTextArea();
        msgtext.setLineWrap(true);
        msgtext.setOpaque(false);//背景色设为透明的了       
        msgtext.setLayout(null); 
        msgtext.setBounds(10, 40, 100, 80);
        msgtext.setEditable(false);
        msgtext.setText(msg);
        
    	
    	ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
		JButton psdBtn = new MyButton(backguandPic);
		psdBtn.setText("确定");
		psdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diolog.hide();
				if(dialogClose!=null)
					dialogClose.close();
			}
		});
		psdBtn.setBounds(70, 130, 60, 20);
		
    	passwordPanel.add(msgtext);
    	passwordPanel.add(psdBtn);
    	
    	diolog.getContentPane().add(passwordPanel, BorderLayout.CENTER);
    	
		diolog.setLocation(Main.mainInstance.location().x+(Main.mainInstance.WIDTH-pic.getIconWidth())/2, Main.mainInstance.location().y+110);// 定位

		diolog.setSize(pic.getIconWidth(), pic.getIconHeight());
		diolog.setModal(true);
		diolog.show();
	}
	
	
	public static class MyButton extends JButton{
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
	
	public class HelpBottomPannel extends JPanel {
		public HelpBottomPannel() {
			super();
	    	setOpaque(false);//背景色设为透明的了       
	    	setLayout(null); 
	    	setBounds(385, 45, 100, 120);
	    	setPreferredSize(new   Dimension(420,100));
	    	
			ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
			JButton mediaBtn = new MyButton(backguandPic);
			mediaBtn.setText("帮助");
			mediaBtn.setBounds(10, 10, 40, 30);
			
			mediaBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try{
						Runtime.getRuntime().exec( "hh   "+Main.textPath+Constant.helpFileName); 
					}catch(Exception e1){
						
					}
					if(helpBottomPannel!=null){
						helpBottomPannel.hide();
						panel.remove(helpBottomPannel);
						helpBottomPannel = null;
						panel.repaint();
					}
				}
			});
			
			JButton picBtn = new MyButton(backguandPic);
			picBtn.setText("关于");
			picBtn.setBounds(10, 50, 40, 30);
			picBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					HelpBottomPannel.this.hide();
					
			        byte[] buffer = LoadResources.loadFile(Main.textPath+"about.txt");
			        StringBuffer text = new StringBuffer();
			        try{
			    		BufferedReader fin = null;
			    		if(Constant.fileEncode!=null)
			    			fin = new BufferedReader(
			    				new InputStreamReader(
			    						new ByteArrayInputStream(buffer),Constant.fileEncode));
			    		else{
			    			fin = new BufferedReader(
			        				new InputStreamReader(new ByteArrayInputStream(buffer)));
			    		}
			    		String line = fin.readLine();
			    		while(line!=null){
			    			if(line!=null){
			    				text.append(line);
			    				text.append("\n");
			    			}
			    			line = fin.readLine();
			    		}
			        }catch(Exception e1){
			        	
			        }
			        createDialog("aboutinfo.png",text.toString());
					if(helpBottomPannel!=null){
						helpBottomPannel.hide();
						panel.remove(helpBottomPannel);
						helpBottomPannel = null;
						panel.repaint();
					}
				}
			});
			
			
			JButton sendBtn = new MyButton(backguandPic);
			sendBtn.setText("版本信息");
			sendBtn.setBounds(10, 90, 40, 30);
			sendBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					HelpBottomPannel.this.hide();
			        byte[] buffer = LoadResources.loadFile(Main.textPath+"version.txt");
			        StringBuffer text = new StringBuffer();
			        try{
			    		BufferedReader fin = null;
			    		if(Constant.fileEncode!=null)
			    			fin = new BufferedReader(
			    				new InputStreamReader(
			    						new ByteArrayInputStream(buffer),Constant.fileEncode));
			    		else{
			    			fin = new BufferedReader(
			        				new InputStreamReader(new ByteArrayInputStream(buffer)));
			    		}
			    		String line = fin.readLine();
			    		while(line!=null){
			    			if(line!=null){
			    				text.append(line);
			    				text.append("\n");
			    			}
			    			line = fin.readLine();
			    		}
			        }catch(Exception e1){
			        	
			        }
			        createDialog("versionInfo.png",text.toString());
					if(helpBottomPannel!=null){
						helpBottomPannel.hide();
						panel.remove(helpBottomPannel);
						helpBottomPannel = null;
						panel.repaint();
					}
				}
			});
			
			add(mediaBtn);
			add(picBtn);
			add(sendBtn);

		}
		
		public void createDialog(String picName,String msg){
			final ImageIcon pic = new ImageIcon(Main.imgPath+picName);
			final JDialog diolog = new JDialog(Main.mainInstance,"提示");
			((JPanel) diolog.getContentPane()).setOpaque(false); 
			diolog.setUndecorated(true); 
			
	    	
			JPanel passwordPanel = new JPanel(){
				public void paintComponent(Graphics g) {
					g.drawImage(pic.getImage(), 0, 0, null);
					super.paintComponent(g);
				}
			};
			
	    	passwordPanel.setOpaque(false);//背景色设为透明的了       
	    	passwordPanel.setLayout(null); 
	    	passwordPanel.setBounds(10, 10, 200, 180);
			
	        final JTextArea msgtext=new JTextArea();
	        msgtext.setLineWrap(true);
	        msgtext.setOpaque(false);//背景色设为透明的了       
	        msgtext.setLayout(null); 
	        msgtext.setBounds(10, 40, 350, 120);
	        msgtext.setEditable(false);
	        msgtext.setText(msg);
	        //msgtext.setBorder(BorderFactory.createLineBorder(Color.black));
	    	
	    	ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
			JButton psdBtn = new MyButton(backguandPic);
			psdBtn.setText("确定");
			psdBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					diolog.hide();
				}
			});
			psdBtn.setBounds(pic.getIconWidth()/2-30, 170, 60, 20);
			
	    	passwordPanel.add(msgtext);
	    	passwordPanel.add(psdBtn);
	    	
	    	diolog.getContentPane().add(passwordPanel, BorderLayout.CENTER);
	    	
			diolog.setLocation(Main.mainInstance.location().x+(Main.mainInstance.WIDTH-pic.getIconWidth())/2, Main.mainInstance.location().y+110);// 定位

			diolog.setSize(pic.getIconWidth(), pic.getIconHeight());
			diolog.setModal(true);
			diolog.show();
		}

	}

	public interface DialogClose{
		public void close();
	}
	
	public static void main(String[] args){
		new Main();
	}
}
