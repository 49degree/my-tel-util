package com.custom.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.custom.client.LeftPanel;
import com.custom.client.RightPanel;
import com.custom.utils.Constant;
import com.custom.utils.HandlerWhat;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;

public class Update{
	private static final Logger logger = Logger.getLogger(Update.class);
    /** Called when the activity is first created. */

    CustomUtils customUtils = null;
    LeftPanel leftPanel;
    RightPanel rightPanel;
    public Update(LeftPanel leftPanel,RightPanel rightPanel) {

    	
    	this.leftPanel = leftPanel;
    	this.rightPanel = rightPanel; 
    	customUtils = new CustomUtils();
    	customUtils.queryInfo();//联网查询
    	
    	LoadResources.loadUpdateInstalledInfo();
    	createInstalledfolds();
    	
    }
	
    
    Thread downThread = null;
    boolean downThreadStop = false;

	public void onClick() {
		downThread = new Thread() {
			public void run() {
				customUtils = new CustomUtils();
				customUtils.queryInfo();
				logger.error("JSONObject installed = customUtils.queryInfo();");
				try {
					Iterator it = LoadResources.updateInstalledInfo.keySet()
							.iterator();
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
    			break;//没有存储空间
    		case 2:
    			times++;
    			if(times%5==0){
    			}
    			break;//报告下载进度
    		case 3:
    			try{
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
    				
    			}
    			
    			break;//解压文件
    		case 4://连接异常
    			break;//连接异常
    		case 5://连接下载文件
    			break;//没有存储空间    		
    		case 6:
    			break;//连接异常
    		case HandlerWhat.NETWORK_CONNECT_RESULE://网络连接状态结果通知
    			updateUi((Boolean)msg.obj);
    			
    			break;
    		case 7://更新已经安装情况
    			break; 
    		case 8://更新未安装情况
    			break;  
    		case 9://更新未安装情况
    			break; 
    		case 10://更新未安装情况
    			break;     			

    		default:
    			break;
    		}
    	}
    };
    
    public void updateUi(boolean networkState){
		createInstalledfolds();
    }
    
    public void createInstalledfolds(){
		try{
	    	StringBuffer temp = new StringBuffer();
	    	Iterator it = LoadResources.installedfolds.keySet().iterator();
	    	int count = 0;
	    	temp.append("          ");
	    	while(it.hasNext()){
	    		String name = (String)it.next();
	    		int value = LoadResources.installedfolds.get(name);
	    		logger.error("name:"+name+":value:"+value);
	    		if(count>0&&++count%3==0){
	    			temp.append("\n");
	    			temp.append("          ");
	    		}
	    		temp.append(name).append(":").append(LoadResources.installedfolds.get(name)).append("          ");
	    	}
	    	handler.sendMessage(handler.obtainMessage(7, temp.toString())); 
	    	handler.sendMessage(handler.obtainMessage(10));
		}catch(Exception e){
			
		}
    }
    public int createNoInstalledfolds(){
    	int count = 0;
    	StringBuffer temp = new StringBuffer();
    	Iterator it = LoadResources.noInstalledfolds.keySet().iterator();
    	int allNum = 0;
    	logger.error("createNoInstalledfolds");
    	while(it.hasNext()){
    		String name = (String)it.next();
    		temp.append("          ");
    		if(count>0&&count++%3==0){
    			temp.append("\n");
    			temp.append("          ");
    		}
    		temp.append(name).append(":").append(LoadResources.noInstalledfolds.get(name)).append("          ");
    		allNum +=LoadResources.noInstalledfolds.get(name);
    	}

    	handler.sendMessage(handler.obtainMessage(8, temp.toString()));
    	String msg1 = "发现了"+allNum+"个新内容，保持及时更新，享受最佳服务！";
    	handler.sendMessage(handler.obtainMessage(9, msg1));
    	return allNum;
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