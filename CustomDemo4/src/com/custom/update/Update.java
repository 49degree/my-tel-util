package com.custom.update;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.json.JSONObject;

import com.custom.client.LeftConnectView;
import com.custom.client.LeftPanel;
import com.custom.client.Main;
import com.custom.client.RightPanel;
import com.custom.update.CustomUtils.FileInfo;
import com.custom.utils.Constant;
import com.custom.utils.HandlerWhat;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.PrcessTaskThread;
import com.custom.utils.PrcessTaskThread.CloseLintener;

public class Update{
	private static final Logger logger = Logger.getLogger(Update.class);
    /** Called when the activity is first created. */

    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    
    private JPanel mainPanel=null;
    private JTextArea updateText=null;
    private JTextArea noUpdateText=null;
    private JButton updateBtn = null; 
    private PrcessTaskThread prcessTaskThread = null;
    private CustomUtils customUtils = null;
    public Update(LeftPanel leftPanel,RightPanel rightPanel) {
    	this.leftPanel = leftPanel;
    	this.rightPanel = rightPanel; 
    	this.leftPanel.removeAll();
    	this.rightPanel.removeAll();
		//已经连接左边视图
		final JPanel connectPanel = new LeftConnectView();
		leftPanel.setPannel(connectPanel);
		

    	
        //连接右边视图
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);//背景色设为透明的了       
		mainPanel.setLayout(null); 
		mainPanel.setBounds(10, 10, 230, 215);
        
		updateText=new JTextArea();
		updateText.setLineWrap(true);
		updateText.setOpaque(false);//背景色设为透明的了       
		updateText.setLayout(null); 
		updateText.setBounds(10, 10, 190, 100);
		updateText.setBorder(BorderFactory.createLineBorder(Color.black)); 
		updateText.setText("updateText");
		updateText.setEditable(false);
		
		
		ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
		JButton updateBtn = new MyButton(backguandPic);
		updateBtn.setText("更新");
		updateBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				logger.error("JSONObject installed = customUtils.queryInfo();");
				if(noInstalledNum==0){
					Main.createDialog("没有未更新内容");
				}else{
					prcessTaskThread = new PrcessTaskThread(
							"更新资源","正在下载资源"
							,0,0,new CloseLintener(){
								public void close(){
								}
							});
					downFile();
				}
			}
		});
		updateBtn.setBounds(50, 192, 60, 20);
		
		JButton refreshBtn = new MyButton(backguandPic);
		refreshBtn.setText("刷新");
		refreshBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
		    	LoadResources.loadUpdateInstalledInfo();
		    	createInstalledfolds();
			}
		});
		refreshBtn.setBounds(120, 192, 60, 20);
		
		//mainPanel.add(updateText);
		mainPanel.add(updateBtn);
		mainPanel.add(refreshBtn);
		
        
		JScrollPane scroll = new JScrollPane(updateText); 
		scroll.setOpaque(false);//背景色设为透明的了 
		scroll.getViewport().setOpaque(false); 
		//把定义的JTextArea放到JScrollPane里面去 
		scroll.setHorizontalScrollBarPolicy( 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scroll.setPreferredSize(new   Dimension(420,100)); 
        scroll.setBounds(0, 10, 230, 180);
        mainPanel.add(scroll);
        
        leftPanel.setPannel(connectPanel);
        
        rightPanel.setPannel(mainPanel);
    	

    	customUtils = new CustomUtils();
    	customUtils.queryInfo();//联网查询
    	
    	LoadResources.loadUpdateInstalledInfo();
    	createInstalledfolds();
    	
    	
    	
    	
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
	
    
    Thread downThread = null;
    boolean downThreadStop = false;

    public void downFile()  {
    	
		downThread = new Thread() {
			public void run() {
				customUtils = new CustomUtils();
				customUtils.queryInfo();
				logger.error("JSONObject installed = customUtils.queryInfo();");
				try {
					Iterator it = LoadResources.updateInstalledInfo.keySet().iterator();
					logger.error("createNoInstalledfolds");
					while (!downThreadStop && it.hasNext()) {
						JSONObject install = LoadResources.updateInstalledInfo
								.get(it.next());
						String unZipflag = null;
						try {
							unZipflag = install.getString(Constant.fileUnziped);
						} catch (Exception e) {
						}

						if (install != null && unZipflag == null) {
							// logger.error(install.getString(Constant.updateId));
							handler.sendMessage(handler.obtainMessage(5));
							customUtils.downFile(install, handler);
							try {
								synchronized (this) {
									wait();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(handler.obtainMessage(
						HandlerWhat.NETWORK_CONNECT_RESULE, new Boolean(true)));
				logger.error("下载线程结束");
			}
		};
		downThread.setDaemon(true);
		downThread.start();

	}
    
    boolean hasNotify = false;
    private Handler handler = new Handler(){
    	int times = 0;
    	JSONObject msgObject = null;
    	@Override
    	public void handleMessage(Message msg){
    		int what = msg.what;
    		switch (what){
    		case 1:
    			if(prcessTaskThread!=null){
    				prcessTaskThread.stopBar();
    			}
    			Main.createDialog("存储空间不足");
    			
    			break;//没有存储空间
    		case 2:
    			times++;
    			if(times%5==0){
    			}
    			if(prcessTaskThread!=null){
    				FileInfo fileInfo = (FileInfo)msg.obj;
    				prcessTaskThread.setValue("已经下载"+fileInfo.downLength+"(bytes),共"+fileInfo.allLength+"(bytes)",(int)fileInfo.downLength/100,(int)fileInfo.allLength/100);
    			}
    			break;//报告下载进度
    		case 3:
    			try{
        			if(prcessTaskThread!=null){
        				prcessTaskThread.setValue("正在解压...",100,100);
        			}
        			msgObject = (JSONObject)msg.obj;
        			final String filePath = msgObject.getString(Constant.filePath);
        			new Thread(){
        				public void run(){
        					try{
        						ToGetFile toGetFile = new ToGetFile();
        						toGetFile.downFileFromzip(filePath);
        						toGetFile.delteDownFile(filePath);
	        					msgObject.put(Constant.fileUnziped, "true");
	        					LoadResources.updateInstalledInfo(msgObject,true);
	        					if(downThread!=null&&downThread.isAlive()){
	        						synchronized (downThread) {
	        							downThread.notify();
									}
	        					}
	        						
        					}catch(Exception e){//解压文件异常
        						e.printStackTrace();
        						downThreadStop = true;
	        					if(downThread!=null&&downThread.isAlive()){
	        						synchronized (downThread) {
	        							downThread.notify();
									}
	        					}
        					}
        					logger.error("解压线程结束");
        				}
        			}.start();
    			}catch(Exception e){//解压文件异常
    				e.printStackTrace();
    				
					downThreadStop = true;
					if(downThread!=null&&downThread.isAlive())
						downThread.interrupt();
					Main.createDialog("解压文件异常");
    			}
    			
    			break;//解压文件
    		case 4://连接异常
    			if(prcessTaskThread!=null){
    				prcessTaskThread.stopBar();
    			}
    			Main.createDialog("连接异常");
    			break;//连接异常
    		case 5://连接下载文件
    			if(prcessTaskThread!=null){
    				prcessTaskThread.setValue("连接下载文件...",100,100);
    			}
    			break;//没有存储空间    		
    		case 6:
    			if(prcessTaskThread!=null){
    				prcessTaskThread.stopBar();
    			}
    			Main.createDialog((String)msg.obj);
    			break;//连接异常
    		case 7://更新已经安装情况
    			updateText.setText((String)msg.obj);
    			break;      			

    		default:
    			break;
    		}
    	}
    };
   
    int installedNum = 0;
    int noInstalledNum = 0;
    public void createInstalledfolds(){
        installedNum = 0;
        noInstalledNum = 0;
		try{
	    	StringBuffer temp = new StringBuffer();
	    	Iterator it = LoadResources.installedfolds.keySet().iterator();
	    	int count = 0;
	    	while(it.hasNext()){
	    		String name = (String)it.next();
	    		int value = LoadResources.installedfolds.get(name);
	    		logger.error("name:"+name+":value:"+value);
	    		if(count>0&&++count%3==0){
	    			temp.append("\n");
	    			temp.append("          ");
	    		}
	    		temp.append(name).append(":").append(LoadResources.installedfolds.get(name)).append("          ");
	    		installedNum +=LoadResources.installedfolds.get(name);
	    	}
	    	
	    	temp.insert(0,"发现了"+installedNum+"个已更新内容\n");
	    	temp.append("\n\n");
	    	
	    	count = 0;
	    	it = LoadResources.noInstalledfolds.keySet().iterator();

	    	StringBuffer temp1 = new StringBuffer();
	    	while(it.hasNext()){
	    		String name = (String)it.next();
	    		temp1.append("          ");
	    		if(count>0&&count++%3==0){
	    			temp1.append("\n");
	    			temp1.append("          ");
	    		}
	    		temp1.append(name).append(":").append(LoadResources.noInstalledfolds.get(name)).append("          ");
	    		noInstalledNum +=LoadResources.noInstalledfolds.get(name);
	    	}
	    	temp1.insert(0,"发现了"+noInstalledNum+"个未更新内容，保持及时更新，享受最佳服务！\n");
	    	temp.append(temp1);
	    	logger.debug(temp.toString());
	    	handler.sendMessage(handler.obtainMessage(7, temp.toString()));
		}catch(Exception e){
			
		}
    }

    

    
	/**
	 * 获取文件名称
	 * @param files
	 * @return
	 */
	private String[] getFileNames(File[] files){
		String[] lists = null;
		if(files!=null&&files.length>0){
			
			lists = new String[files.length];
			for(int i=0;i<lists.length;i++){
				lists[i] = files[i].getName();
			}
			
		}
		return lists;
	}
	
	public void modifyInitedFile(HashMap<String,String> btnInfo,String filePath){
		try{
			//清空文件
			RandomAccessFile   raf   =   new   RandomAccessFile(filePath,   "rw"); 
			raf.setLength(0); 
			raf.close(); 
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			Iterator it = btnInfo.keySet().iterator();

			while(it.hasNext()){
				String key = (String)it.next();
				key = key+"\n"; 
				fos.write(key.getBytes());
			}
			fos.getChannel().force(true);
			fos.flush();
			fos.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

}