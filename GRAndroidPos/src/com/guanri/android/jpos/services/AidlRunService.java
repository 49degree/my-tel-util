package com.guanri.android.jpos.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.guanri.android.jpos.MainActivity;
import com.guanri.android.jpos.R;
import com.guanri.android.jpos.common.NetWorkBlthStateHandler;
import com.guanri.android.jpos.common.SharedPreferencesUtils;
import com.guanri.android.jpos.pos.SerialPortAndroid;
import com.guanri.android.jpos.pos.data.TerminalLinks.TAndroidCommTerminalLink;
import com.guanri.android.jpos.pos.data.TerminalParsers.TTerminalParser;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.LogInfo;
import com.guanri.android.lib.log.Logger;

public class AidlRunService extends Service{
    //定义个一个Tag标签   
	static Logger logger = Logger.getLogger(PosCenterThread.class);
    private static final String TAG = "AidlRunService"; 
    private boolean isAutoRun = true;//是否自动运行
    private PosCenterThread posCenterTask = null;//数据处理线程
    private FindCommTask findCommTask = null;//查询设备线程
    public static String LOG_INFO = "";
    
    

    
    
    public final static int NOTIFY_ID = 20110913;//通知ID

    @Override
    public void onCreate(){
    	logger.error("start onCreate~~~");  
    	super.onCreate();
        //初始化数据处理线程对象
    	AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","POS终端服务已经打开");
    	initPos();//初始化服务器ID,PORT,密码
    	//启动查询设备线程
    	IS_SERVER_STOP = false;
    	findCommTask = new FindCommTask();
    	findCommTask.start();
    	acquireWakeLock();//加入CPU锁，保持CUP在该service运行期间一直运行
    	MainApplication.getInstance().startNetWorkListen(new NetWorkBlthStateHandler());//开始监听网络状态
    }
    
    
    @Override
    public void onDestroy(){
    	logger.error("start onDestroy~~~");  
    	//关闭查询设备线程
    	IS_SERVER_STOP = true;
    	if(findCommTask!=null)
    		findCommTask.interrupt();
    	//停止真正运行线程
    	try{
    		mBinder.stopPos();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	AidlRunService.clearNotify(NOTIFY_ID);//清除消息提示
    	releaseWakeLock();//释放CPU锁，
    	MainApplication.getInstance().stopNetWorkListen();//停止监听网络情况
    	logger.error("end onDestroy~~~"); 
    	super.onDestroy();
    }
    
    @Override  
    public IBinder onBind(Intent intent) {  
        logger.error("start IBinder~~~");  
        return mBinder;  
    }
    
    @Override  
    public void onStart(Intent intent, int startId) {  
        logger.error("start onStart~~~");  
        super.onStart(intent, startId);  
    }  
    
    private final GrPosService.Stub mBinder = new GrPosService.Stub(){ 
        /**
         * 打开POS
         * @return
         */
        public boolean startPos(){
        	if(HAS_COMM_PORT){
        		//logger.error("GrPosService.Stub startPos");
        		posCenterTask = PosCenterThread.getInstance();
            	if(!posCenterTask.getTaskStop()&&!posCenterTask.isAlive()){//Thread在run之前和run完成之后isAlive()返回false,run运行过程中返回true;
            		logger.error("GrPosService.Stub startPos");
            		try{
            			posCenterTask.start();//Thread 不可重复调用start()方法，
            		}catch(IllegalThreadStateException ie){//重复调用则抛出异常
            			ie.printStackTrace();
            		}
            	}
        	}else{
        		AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","POS终端未找到");
        	}
        	return true;
        }
        /**
         * 关闭POS
         * @return
         */
        public boolean stopPos(){
        	if(posCenterTask!=null&&!posCenterTask.getTaskStop()){
        		logger.error("GrPosService.Stub stopPos");
        		posCenterTask.setTaskStop(true);
        		posCenterTask.interrupt();
        		posCenterTask = null;
        		AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","POS终端已经关闭");
        	}
        	return true;
        }
        
        public boolean hasCommPort(){
        	//logger.error("GrPosService.Stub hasCommPort");
        	return HAS_COMM_PORT;
        }
        /**
         * 其他操作
         * @param params
         * @return
         */
		public String operate(String params) {
			//logger.error("GrPosService.Stub operate");
			if ("LOG_INFO".equals(params)) {
				return LOG_INFO;
			} else{
				return LogInfo.instance.operate(params);	
			}
		}
    };
    
    
    /**
     *   各种锁的类型对CPU 、屏幕、键盘的影响： 
	    PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯有可能是关闭的。
	
	    SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
	
	    SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
	
	    FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
	
	    ACQUIRE_CAUSES_WAKEUP ：Normal wake locks don't actually turn on the illumination. Instead, they cause the illumination to remain on once it turns on (e.g. from user activity). This flag will force the screen and/or keyboard to turn on immediately, when the WakeLock is acquired. A typical use would be for notifications which are important for the user to see immediately.
	
	    ON_AFTER_RELEASE ：f this flag is set, the user activity timer will be reset when the WakeLock is released, causing the illumination to remain on a bit longer. This can be used to reduce flicker if you are cycling between wake lock conditions. 

   */
    private WakeLock wakeLock = null;
    /**
     * 打开锁4
     */
	private void acquireWakeLock() {
		if (wakeLock == null) {
			logger.debug("Acquiring wake lock");
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());//来生成WakeLock实例。int Flags指示要获取哪种WakeLock，不同的Lock对cpu 、屏幕、键盘灯有不同影响。 
			wakeLock.acquire();
		}
	}

	/**
	 * 关闭锁
	 */
	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	} 
    
    
    /**
     * 查询串口
     * @author Administrator
     *
     */
    public static boolean IS_SERVER_STOP = false;
    public static boolean HAS_COMM_PORT = false;
    public class FindCommTask extends Thread{
    	public void run(){
    		// 循环直到打开串口
    		while (!IS_SERVER_STOP) {
    			//logger.error(":FindCommTask is runing.........................:");
    			
    			try {
    				if (SerialPortAndroid.findAndroidDevice("/dev/ttyUSB0")) {
    					HAS_COMM_PORT = true;

    					if(isAutoRun){
    						mBinder.startPos();
    					}else{
        					AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","找到POS终端");
        					LOG_INFO="找到POS终端";
    					}
					}else{
						HAS_COMM_PORT = false;
						mBinder.stopPos();
						AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","未找到POS终端");
						LOG_INFO="未找到POS终端";

					}
    				Thread.sleep(1500);
    			}catch(SecurityException se){
    				se.printStackTrace();
    			} catch (Exception e) {
    				e.printStackTrace();
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    /**
     * POS数据读取线程
     * @author Administrator
     *
     */
    
    public static class PosCenterThread extends Thread{
    	
    	private TAndroidCommTerminalLink TerminalLink = null;
    	private TTerminalParser TerminalParser = null;
    	public boolean IS_TASK_STOP = false;//判断当前线程是否已经停止
    	private static PosCenterThread instance = null;
    	static int index = 0;
    	public static StringBuffer LOG_INFO = new StringBuffer();

    	/**
    	 * 如果instance为空或者instance.isAlive()且已经运行完则创建新线程对象
    	 * @return
    	 */
    	public synchronized static PosCenterThread getInstance(){
    		if (instance == null || (instance.IS_TASK_STOP == true)) {
    			instance = new PosCenterThread();
    		}
    		return instance;
    	}
    	
    	public synchronized boolean getTaskStop(){
    		return IS_TASK_STOP;
    	}
    	
    	public synchronized void setTaskStop(boolean isStop){
    		IS_TASK_STOP = isStop;
    	}
    	
    	
    	private PosCenterThread(){
    		IS_TASK_STOP = false;
    		TerminalLink = new TAndroidCommTerminalLink();
    		TerminalLink.CommName = "/dev/ttyUSB0";
    		TerminalLink.ReadTimeout = 5000;
    		TerminalParser = new TTerminalParser();
    	}
    	
    	@Override
    	public void run() {
    		//logger.error("PosCenterThread is start.........................:"+index++);
    		//未连接，判断是否存在串口设备
    		if (!TerminalLink.GetConnected()) {
    			if(SerialPortAndroid.findAndroidDevice(TerminalLink.CommName)){
    				try{
    					TerminalLink.Connect();
    					AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","设备连接成功,正在读取数据.....");
    				}catch(SecurityException se){
    					AidlRunService.LOG_INFO = "打开POS连接失败";
    				}
        			TerminalParser.SetTerminalLink(TerminalLink);
    			}else{
    				setTaskStop(true);
    				return ;
    			}
    		}
    		// 循环读取数据
    		while (!IS_TASK_STOP&&!IS_SERVER_STOP) {
    			//logger.error("PosCenterThread is reading..........................");
    			try {
    				//读取数据
    				if (TerminalLink.GetConnected()) {
    					AidlRunService.LOG_INFO = "设备连接成功,正在读取数据......";
    					TerminalParser.ParseRequest();
    				}
    				
    			}catch(SecurityException se){
    				se.printStackTrace();
    			} catch (Exception e) {
    				AidlRunService.LOG_INFO = "POS连接出现问题："+e.getMessage();
    				e.printStackTrace();
    			}
    		}
    		//关闭连接
    		try {
    			TerminalLink.Disconnect();
    			AidlRunService.LOG_INFO = "POS连接已经关闭";
    			AidlRunService.notify(AidlRunService.NOTIFY_ID,"POS服务通知","POS连接已经关闭.....");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		if(IS_SERVER_STOP){//清除消息提示
    	    	AidlRunService.clearNotify(NOTIFY_ID);//清除消息提示
    		}
    		setTaskStop(true);
    	}	
    }
    
	/**
	 * 发通知
	 * @param noifyId
	 * @param msg
	 */
	public static void notify(int noifyId,String title,String msg){
        //以下是对Notification的各种参数设定
        int icon=R.drawable.icon;
        String tickerText=title;
        long when=System.currentTimeMillis();
        Notification nfc=new Notification(icon,tickerText,when);
        Context cxt=MainApplication.getInstance();
        String expandedTitle=msg;
        String expandedText="";
        //intent是非常重要的参数,用来启动你实际想做的事情,设为null后点击状态栏上的Notification就没有任何反应了.
        Intent intent=new Intent(cxt,MainActivity.class);
        PendingIntent nfcIntent=PendingIntent.getActivity(cxt,0,intent,0);
        nfc.setLatestEventInfo(cxt,expandedTitle,expandedText,nfcIntent);
        //发送Notification
        NotificationManager nfcManager=(NotificationManager)cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        nfcManager.notify(noifyId,nfc);
	}
	
	/**
	 * 发通知
	 * @param noifyId
	 * @param msg
	 */
	public static void clearNotify(int noifyId){
        NotificationManager nfcManager=(NotificationManager)MainApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        nfcManager.cancel(noifyId);
	}
	
	/**
	 * POS终端初始化
	 */
	private void initPos(){
		String isPosInit = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.IS_POS_INIT);
		if(isPosInit==null||"".equals(isPosInit)){//未初始化
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
					SharedPreferencesUtils.IS_POS_INIT, "YES");//表示已经初始化
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
					SharedPreferencesUtils.POS_PWD, SharedPreferencesUtils.POS_PWD_INIT);//密码初始值
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.SERVER_INFO, 
					SharedPreferencesUtils.SERVERIP, SharedPreferencesUtils.SERVERIP_INIT);//IP初始值
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.SERVER_INFO, 
					SharedPreferencesUtils.SERVERPORT, SharedPreferencesUtils.SERVERPORT_INIT);//PORT初始值
			
		}
	}
}
