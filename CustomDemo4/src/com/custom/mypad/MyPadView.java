package com.custom.mypad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftNoConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.Main;
import com.custom.client.RightPanel;
import com.custom.client.Main.MyButton;
import com.custom.media.MediaBottomPannel.MYFileFilter;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;

public class MyPadView {
	public boolean isStop = false;
	public Boolean hasConnectPad = null;
	public MyPadView(final LeftPanel leftPanel,final RightPanel rightPanel) {
		super();
		
		//已经连接左边视图
		final JPanel connectPanel = new LeftConnectView(Constant.leftPic1);
		//未连接左边视图
		final JPanel noConnectpanel = new LeftNoConnectView();
        

		
        final JTextArea noConnectText=new JTextArea();
        noConnectText.setLineWrap(true);
        noConnectText.setOpaque(false);//背景色设为透明的了       
        noConnectText.setLayout(null); 
        noConnectText.setPreferredSize(new   Dimension(420,100)); 
        noConnectText.setEditable(false);
        noConnectText.setBounds(10, 10, 230, 215);
        
        
		final JScrollPane noConnectscroll = new JScrollPane(noConnectText); 
		noConnectscroll.setOpaque(false);//背景色设为透明的了 
		noConnectscroll.getViewport().setOpaque(false); 
		//把定义的JTextArea放到JScrollPane里面去 
		noConnectscroll.setHorizontalScrollBarPolicy( 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		noConnectscroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		noConnectscroll.setPreferredSize(new   Dimension(420,100)); 
		noConnectscroll.setBounds(10, 10, 230, 215);
        
        byte[] buffer = LoadResources.loadFile(Main.textPath+"noConnectView.txt");
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
    				noConnectText.append(line);
    				noConnectText.append("\n");
    			}
    			line = fin.readLine();
    		}
        }catch(Exception e){
        	
        }

        //已经连接右边视图
        
        
        
		final ImageIcon imgRight = new ImageIcon();
		
    	final Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG));
		}catch(Exception e){
			
		}
		if(properties.getProperty(Constant.headerPic)!=null&&
				!"".equals(properties.getProperty(Constant.headerPic).trim())){
			imgRight.setImage(new ImageIcon(Main.imgPath+properties.getProperty(Constant.headerPic)).getImage());
		}else{
			imgRight.setImage(new ImageIcon(Main.imgPath+"notHeaderPic.png").getImage());
		}

		final JPanel panelRight = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(imgRight.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		panelRight.setOpaque(false);//背景色设为透明的了
		panelRight.setLayout(null); 
		panelRight.setPreferredSize(new   Dimension(420,100)); 
		panelRight.setBounds((250-imgRight.getIconWidth())/2, 10, imgRight.getIconWidth(), imgRight.getIconHeight());
		//panelRight.setBorder(BorderFactory.createLineBorder(Color.black));
		
		
    	ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
		final JButton setHeader = new MyButton(backguandPic);
		setHeader.setText("设置头像");
		setHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("选择图片");
				
				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setMultiSelectionEnabled(false);

				chooser.setFileFilter(new MYFileFilter(Constant.picTypes));
				
				if (chooser.showOpenDialog(rightPanel) == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					byte[] temp = LoadResources.loadFile(file.getAbsolutePath().toString());
					try{
						final String fileName = System.currentTimeMillis()+"_"+file.getName();
						FileOutputStream out = new FileOutputStream(Main.imgPath+fileName);
						out.write(temp);
						out.flush();
						out.close();
						properties.setProperty(Constant.headerPic,fileName);

						final ImageIcon imgPic = new ImageIcon(Main.imgPath+fileName);
						JPanel img = new JPanel() {
							public void paintComponent(Graphics g) {
								g.drawImage(imgPic.getImage(), 0, 0, null);
								super.paintComponent(g);
							}
						};
						img.setOpaque(false);//背景色设为透明的了
						img.setLayout(null); 
						img.setPreferredSize(new   Dimension(420,100)); 
						img.setBounds((250-imgRight.getIconWidth())/2, 10, imgRight.getIconWidth(), imgRight.getIconHeight());
						img.setBorder(BorderFactory.createLineBorder(Color.black));
						
						
			    	    rightPanel.removeAll();
						rightPanel.setPannel(img);
						rightPanel.setPannel(setHeader);
						
						try{
							
							properties.setProperty(Constant.headerPic, fileName);
							FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")
									+ File.separator +"bin"+File.separator+ Constant.SERVER_CONFIG); 
							
							properties.store(fos, "");
							fos.close();// 关闭流 
							Main.createDialog("设置成功");
						}catch(Exception e1){
							
						}	
						
					}catch(Exception e1){
						
					}
					
					

				}
			}
		});
        
		setHeader.setBounds(90, imgRight.getIconHeight()+30, 60, 30);
		
		
		new Thread(){
			public void run(){
				while(!isStop){
	
					if(!Constant.isConnectPad&&(hasConnectPad==null||hasConnectPad)){
				    	leftPanel.removeAll();
				    	rightPanel.removeAll();
				    	
						leftPanel.setPannel(noConnectpanel);
						rightPanel.setPannel(noConnectscroll);

					}else if(Constant.isConnectPad&&(hasConnectPad==null||!hasConnectPad)){
				    	leftPanel.removeAll();
				    	rightPanel.removeAll();
				    	
						leftPanel.setPannel(connectPanel);
						rightPanel.setPannel(panelRight);
						rightPanel.setPannel(setHeader);
					}
					hasConnectPad = Constant.isConnectPad;
					try{
						Thread.sleep(500);
					}catch(Exception e){
						
					}
					
				}
			}
		}.start();
		
		
		
	}
}
