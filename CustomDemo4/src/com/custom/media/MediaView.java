package com.custom.media;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.Main;
import com.custom.client.RightPanel;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.PrcessTaskThread;
import com.custom.utils.PrcessTaskThread.CloseLintener;

public class MediaView {
	private static final Logger logger = Logger.getLogger(MediaView.class);
    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    private JTextArea text=null;
    
    private int buttonType = -1 ;//面板类型 0=多媒体；1=电子书
    
    private int movies = 0 ;
    private int pics = 0 ;
    private int mp3s = 0 ;
    
    private int books = 0 ;
    private int docs = 0 ;
    
    private Map<String,Boolean> filePaths = new HashMap<String,Boolean>();
    
    public MediaView(LeftPanel leftPanel,RightPanel rightPanel,int buttonType) {
    	this.leftPanel = leftPanel;
    	this.rightPanel = rightPanel; 
    	this.buttonType = buttonType;
    	this.leftPanel.removeAll();
    	this.rightPanel.removeAll();
    	
		//已经连接左边视图
		final JPanel connectPanel = new LeftConnectView();
		leftPanel.setPannel(connectPanel);
		


		
		
        //连接右边视图
        text=new JTextArea();
        text.setLineWrap(true);
        text.setOpaque(false);//背景色设为透明的了       
        text.setLayout(null); 
        text.setBounds(10, 10, 230, 215);
        text.setBorder(BorderFactory.createLineBorder(Color.black)); 

        
        
		JScrollPane scroll = new JScrollPane(text); 
		scroll.setOpaque(false);//背景色设为透明的了 
		scroll.getViewport().setOpaque(false); 
		//把定义的JTextArea放到JScrollPane里面去 
		scroll.setHorizontalScrollBarPolicy( 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scroll.setPreferredSize(new   Dimension(420,100)); 
        scroll.setBounds(10, 10, 230, 190);
        if(this.buttonType==0){
            MediaBottomPannel mediaBottomPannel = new MediaBottomPannel(this);
            mediaBottomPannel.setOpaque(false);//背景色设为透明的了       
            mediaBottomPannel.setLayout(null); 
            mediaBottomPannel.setPreferredSize(new   Dimension(420,100)); 
            mediaBottomPannel.setBounds(10, 200, 230, 30);
            leftPanel.setPannel(connectPanel);
            rightPanel.setPannel(scroll);
            setText();
            rightPanel.setPannel(mediaBottomPannel);
        }else if(this.buttonType == 1 ){
            BookBottomPannel bookBottomPannel = new BookBottomPannel(this);
            bookBottomPannel.setOpaque(false);//背景色设为透明的了       
            bookBottomPannel.setLayout(null); 
            bookBottomPannel.setPreferredSize(new   Dimension(420,100)); 
            bookBottomPannel.setBounds(10, 200, 230, 30);
            leftPanel.setPannel(connectPanel);
            rightPanel.setPannel(scroll);
            setText();
            rightPanel.setPannel(bookBottomPannel);
        }

    }

    public class BooleanObject{
    	boolean value = false;
    }
    /**
     * 0=视频；1=图片；2=MP3
     */
    public void uploadFile(){
    	if(buttonType==0){//多媒体
    		if(movies+pics+mp3s==0){
    			Main.createDialog("没有选择文件");
    			return;
    		}
    		
    	}else if(buttonType==1){//电子书
			if(books+docs==0){
				Main.createDialog("没有选择文件");
				return ;
			}
			
    	}
    	
    	
    	Thread t = new Thread(){
    		public void run(){
    			String dirName = "";
            	if(buttonType==0){//多媒体
            		dirName = "media";
            	}else if(buttonType==1){//电子书
            		dirName = "book";
            	}
    			
    	    	final BooleanObject stop = new BooleanObject();
    	    	int lessSpace = 10*1024*1024;//10M
    	    	
    	    	String fileSpaceStr = "";
    	    	int fileSpace = 0;
    	    	long readedSpace = 0;
    	    	
				PrcessTaskThread prcessTaskThread = new PrcessTaskThread(
						"复制文件","正在复制文件"
						,0,0,new CloseLintener(){
							public void close(){
								stop.value = true;
							}
						});
    	    	for(Map.Entry<String,Boolean> filePath:filePaths.entrySet()){
    	    		if(filePath.getValue()){
    	    			continue;
    	    		}
    	    		
    	    		File file = new File(filePath.getKey());
    	    		
    	    		
    	    		byte[] bufer = new byte[1024];
    	    		int readLength = 0;
    	    		if(!file.exists()||file.isDirectory())
    	    			continue;
    				long[] extsdSpace = LoadResources.readExtSDCard();
    				long[] sdSpace = LoadResources.readSDCard();
    				
    				fileSpaceStr = doubleFormat(file.length()/(1024*1024.0));
    				fileSpace = (int)file.length()/(1024*1024);
    				readedSpace = 0;
    				
    				if((extsdSpace[0]==0||extsdSpace[2]<file.length())
    						&&sdSpace[2]<=file.length()+lessSpace){
    					Main.createDialog("磁盘空间不足");
    					break;
    				}
    				String rootPath = null;
    				if(extsdSpace[2]>file.length()){
    					rootPath = Constant.getExtSdPath()+File.separator+Constant.mediaDirName+File.separator+dirName+File.separator;
    				}else if(sdSpace[2]>file.length()+lessSpace){
    					rootPath = Constant.getSdPath()+File.separator+Constant.mediaDirName+File.separator+dirName+File.separator;
    				}
    				
    				if(!new File(rootPath).exists()||new File(rootPath).isFile()){
    					new File(rootPath).mkdirs();
    				}
    				File temp = new File(rootPath+file.getName());
    				int bakTimes=0;
    				//如果已经有这个文件，使用别名
    				while(temp.exists()&&temp.isFile()){
    					int pointIndex = file.getName().indexOf(".");
    					temp = new File(rootPath
    							+file.getName().substring(0,pointIndex)+"("+(bakTimes++)+")"
    							+file.getName().substring(pointIndex));
    				}
    				


    				RandomAccessFile oSavedFile = null;
    				FileInputStream in = null;
    				try{
    					in = new FileInputStream(file); 
    					oSavedFile = new RandomAccessFile(temp.getAbsolutePath(),"rw");
    					oSavedFile.setLength(file.length());
    					bakTimes=0;
    					while(!stop.value&&(readLength = in.read(bufer))>0){
    						readedSpace += readLength;
    						oSavedFile.write(bufer,0,readLength);
    						
    						if(bakTimes%50==0||readedSpace>=file.length()){
    							//logger.debug(doubleFormat(readedSpace/(1024*1024.0))+"/"+fileSpaceStr);
    							if(readedSpace>=file.length()){
        							prcessTaskThread.setValue(
        									"正在复制"+file.getName()+"("+fileSpaceStr+"/"+fileSpaceStr+"M)",
        									(int)readedSpace/(1024*1024),
        									fileSpace);
    								break;
    							}else{
        							prcessTaskThread.setValue(
        									"正在复制"+file.getName()+"("+doubleFormat(readedSpace/(1024*1024.0))+"/"+fileSpaceStr+"M)",
        									(int)readedSpace/(1024*1024),
        									fileSpace);
    							}
    								
    						}
    						bakTimes++;
    					}
    					//logger.debug(System.currentTimeMillis()+":"+ file.getName()+"copyed 11");
    				}catch(Exception e){
    					e.printStackTrace();
    				}finally{
    					prcessTaskThread.setValue("正在保存文件"+file.getName(),100,100);
    					//logger.debug(System.currentTimeMillis()+":"+ file.getName()+"copyed 12");
    					try{
    						if(in!=null)
    							in.close();
    					}catch(Exception e){}
    					try{
    						if(oSavedFile!=null)
    							oSavedFile.close();
    					}catch(Exception e){}
    					//logger.debug(System.currentTimeMillis()+":"+ file.getName()+"copyed 13");
    				}
    				
    				if(stop.value)
    					break;
    				filePath.setValue(true);
    				setText();
    			
    	    	}
    	    	
				prcessTaskThread.setValue("完成",100,100);
    		}
    	};
    	t.setDaemon(true);
    	t.start();
    }
    
    /**
     * 0=视频；1=图片；2=MP3
     */
    public void addFilePath(String filePath,int type){
    	if(!filePaths.containsKey(filePath)){
        	this.filePaths.put(filePath,false);
        	if(buttonType==0){//多媒体
        		if(type==0){
        			movies++;
        		}else if(type==1){
        			pics++;
        		}else if(type==2)
        			mp3s++;
        	}else if(buttonType==1){//电子书
        		if(type==0){
        			books++;
        		}else if(type==1){
        			docs++;
        		}
        	}

        	setText();
    	}

    }
    
    public void clearFilePath(){
    	filePaths.clear();

    	
    	if(buttonType==0){//多媒体
        	movies = 0;
        	pics = 0;
        	mp3s = 0;
    	}else if(buttonType==1){//电子书
    		books = 0;
        	docs = 0;
        
    	}
    	setText();
    }   
    
    public void setText(){
		try {
			text.setText("");
			if ((movies == 0 && pics == 0 && mp3s == 0)&&(books==0&&docs==0)) {
				text.append("您还没有选择资源");
			} else {
				text.append("您已经选择了");

				StringBuffer bu = new StringBuffer();
				
		    	if(buttonType==0){//多媒体
					if (movies > 0) {
						bu.append((bu.length() == 0 ? "" : ";") + movies + "个视频");
					}
					if (pics > 0) {
						bu.append((bu.length() == 0 ? "" : ";") + pics + "张图片");
					}
					if (mp3s > 0) {
						bu.append((bu.length() == 0 ? "" : ";") + mp3s + "个MP3");
					}
		    	}else if(buttonType==1){//电子书
					if (books > 0) {
						bu.append((bu.length() == 0 ? "" : ";") + books + "个电子书");
					}
					if (docs > 0) {
						bu.append((bu.length() == 0 ? "" : ";") + docs + "个文档");
					}
		    	}
				

				text.append(bu.toString());
			}
			text.append("\n");
			for(String filePath:filePaths.keySet()){
				text.append(filePath);
				text.append(filePaths.get(filePath)==true?"(OK)":"");
				text.append("\n");
			}
			text.setCaretPosition(0);
		}catch(Exception e){
      	
      }
    }
    
    public static String doubleFormat(double d){   
        DecimalFormat df = new DecimalFormat("0.##");   
        return df.format(d);                   
    }
}
