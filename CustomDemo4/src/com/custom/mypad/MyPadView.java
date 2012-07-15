package com.custom.mypad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftNoConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.Main;
import com.custom.client.RightPanel;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;

public class MyPadView {
	public boolean isStop = false;
	public Boolean hasConnectPad = null;
	public MyPadView(final LeftPanel leftPanel,final RightPanel rightPanel) {
		super();
		
		//已经连接左边视图
		final JPanel connectPanel = new LeftConnectView();
		//未连接左边视图
		final JPanel noConnectpanel = new LeftNoConnectView();
        
        //未连接右边视图
		
        //连接右边视图

        

		
		
        final JTextArea noConnectText=new JTextArea();
        noConnectText.setLineWrap(true);
        noConnectText.setOpaque(false);//背景色设为透明的了       
        noConnectText.setLayout(null); 
        noConnectText.setPreferredSize(new   Dimension(420,100)); 
       
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
    				System.out.println(line);
    				noConnectText.append(line);
    				noConnectText.append("\n");
    			}
    			line = fin.readLine();
    		}
        }catch(Exception e){
        	
        }

        //已经连接右边视图
		final ImageIcon imgRight = new ImageIcon(Main.imgPath+"notHeaderPic.png");
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
		panelRight.setBorder(BorderFactory.createLineBorder(Color.black));
        
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
