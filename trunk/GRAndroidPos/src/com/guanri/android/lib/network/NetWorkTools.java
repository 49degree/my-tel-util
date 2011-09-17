package com.guanri.android.lib.network;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.guanri.android.lib.context.HandlerWhat;

/**
 * 网络连接工具类
 * @version 1.0
 */
public class NetWorkTools {
	/*当前网络连接类型*/
	/**无网络连接*/
	public final static int NET_TYPE_NONE = 0;		
	/**WIFI连接*/
	public final static int NET_TYPE_WIFI = 1;			
	/**GPRS连接*/
	public final static int NET_TYPE_GPRS = 2;		
	/**创建连接成功消息*/
	public static final int STATUS_CONNECTED = 0;
	/**创建连接失败消息*/
	public static final int STATUS_CONNECT_NO = 1;
	/**网络中断消息*/
	public static final int STATUS_DIS_CONNECTED = 2;
	
	/**
	 * 广播行为：指明连接状态改变，通过监听该动作能得知建立连接的结果，以及获得程序运行过程中网络的一些变化情况。
	 * 附带有网络状态信息
	 * @see EXTRA_STATUS_INFO
	 */
	public final static String ACTION_CONNECTION_STATUS_CHANGE ="com.guanri.ACTION_CONNECTION_STATUS_CHANGE";
	
	/**当前网络信息；通过{@link android.content.Intent#getStringExtra(String,String)}来获取。*/
	public final static String EXTRA_STATUS_INFO = "statusInfo";	
	
	/*APN NUMERIC*/
	private final String NUMERIC_TYPE_MOBILE_TD  = "46000";
	private final String NUMERIC_TYPE_UNICOM     = "46001";
	private final String NUMERIC_TYPE_MOBILE_GSM = "46002";
	private final String NUMERIC_TYPE_TELECOM    = "46003";
	
	/*GPRS网络类型*/
	private final static int GPRS_TYPE_UNKOWN     = 0;		//未知网络
	private final static int GPRS_TYPE_UNICOM_2G  = 1;		//中国联通(2G)
	private final static int GPRS_TYPE_UNICOM_3G  = 2;		//中国联通(3G)
	private final static int GPRS_TYPE_MOBIL_2G   = 3;		//中国移动GSM(2G)
	private final static int GPRS_TYPE_MOBIL_3G   = 4;		//中国移动TD(3G)
	private final static int GPRS_TYPE_TELECOM_2G = 5;		//中国电信(2G)
	private final static int GPRS_TYPE_TELECOM_3G = 6;		//中国电信(3G)
		
	private final static String TAG = "NetWorkTools";	
	private final static Uri URL_APN_LIST = Uri.parse("content://telephony/carriers");
	private final static Uri URL_APN_CURRENT = Uri.parse("content://telephony/carriers/preferapn");
	private final static int APN_ID = 10001;
	private final static String APN_PROXY_TELECOM = "10.0.0.200";
	private final static String APN_PORT_TELECOM = "80";
	private final static int TIME_OUT = 10000;
	
	
	private Context mContext;
	private Handler mHandler ;
	private int mGprsType;
	private boolean mIsAuto;
	private boolean mIsConnected;
	private ConnectionChangeReciver mReceiver;
	private Timer mTimer;
	
	public NetWorkTools(Context context, Handler handler) {
		super();
		mContext = context;
		mHandler = handler;
		mGprsType = -1;		
		mReceiver = new ConnectionChangeReciver();
	    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	    mContext.registerReceiver(mReceiver, filter);
		// TODO Auto-generated constructor stub
	}	
	
	/**
	 * 自动检测网络并建立连接;
	 * @return 成功返回true；否则返回false
	 */
	public void autoConnect() {
		if (getNetWorkInfo() == NET_TYPE_NONE) 
		{
			createConnection();	
		}
				
	}

	/**
	 * 创建网络连接；优先使用WIFI，如果WIFI无法使用，则使用GPRS，GPRS连接中优先使用3G网络
	 * @return	创建成功返回true，失败返回false
	 */
	public void createConnection() {
		createWifiConnection();
		mIsAuto = true;
	}
	
	/**
	 *  取消监听；默认会注册一个广播接收器监听系统网络状态，所以在退出程序的时候应该注销掉
	 */
	public void cancelMmonitor(){
		if (mReceiver!=null) {
			try{
				mContext.unregisterReceiver(mReceiver);
				if (mTimer != null) {
					mTimer.purge();
				}
			}catch(Exception e){
				//e.printStackTrace();
			}
		}	
	}
	
	/**
	 * 创建WIFI连接	
	 * @return 创建成功返回true；否则返回false
	 */
	private boolean createWifiConnection() {
		// TODO Auto-generated method stub	
		LogCat.v(TAG, "开始创建WIFI连接...");
		mTimer = new Timer();
		mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		WifiLock mWifiLock = mWifiManager.createWifiLock("NetWorkTools");		
		if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(true);   
            LogCat.v(TAG, "打开WIFI");
        }  
		return false;
	}

	/**
	 * 获取当前网络连接类型
	 * @return  当前网络连接类型；可能为
	 * {@link #NET_TYPE_NONE}, {@link #NET_TYPE_WIFI}, 或者 {@link #NET_TYPE_GPRS}。
	 */
	public int getNetWorkInfo() {
		ConnectivityManager connec = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo activeNetInfo = connec.getActiveNetworkInfo(); 
		if (activeNetInfo!=null && activeNetInfo.isAvailable()) {
			if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return NET_TYPE_WIFI;
			} else {
				return NET_TYPE_GPRS;
			}
		} else {
			return NET_TYPE_NONE;
		}		
	}	
	
	/**
	 * 创建GPRS连接，优先使用3G网络
	 * @param isUseOnly2G 如果为True则只使用2G网络，否则优先使用3G网络
	 * @return 创建成功返回true；否则返回false
	 */
	public boolean createGprsConnection(boolean isUseOnly2G) {
		LogCat.v(TAG, "开始创建GPRS连接...");
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT); 
		}		
		mGprsType = getGprsType();
		LogCat.v(TAG, "GPRS网络类型为:" + mGprsType);
		ApnNode apnNode = null;
		mContext.getContentResolver().delete(URL_APN_LIST, "_id=?", new String[]{String.valueOf(APN_ID)});
		switch (mGprsType) {
			case GPRS_TYPE_MOBIL_3G:
				if (!isUseOnly2G) {
					apnNode = new ApnNode(APN_ID, "cmnet", "cmnet", NUMERIC_TYPE_MOBILE_TD);				
					break;
				}				
			case GPRS_TYPE_MOBIL_2G:
				apnNode = new ApnNode(APN_ID, "cmnet", "cmnet", NUMERIC_TYPE_MOBILE_GSM);
				break;
			case GPRS_TYPE_UNICOM_3G:
				if (!isUseOnly2G) {
					apnNode = new ApnNode(APN_ID, "3gnet", "3gnet", NUMERIC_TYPE_UNICOM);
					break;
				}
			case GPRS_TYPE_UNICOM_2G:
				apnNode = new ApnNode(APN_ID, "uninet", "uninet", NUMERIC_TYPE_UNICOM);				
				break;
			case GPRS_TYPE_TELECOM_2G:				
			case GPRS_TYPE_TELECOM_3G:
				apnNode = new ApnNode(APN_ID, "ctnet", "ctnet", NUMERIC_TYPE_TELECOM);
				apnNode.setProxy(APN_PROXY_TELECOM, APN_PORT_TELECOM);
				break;
			default:
				Log.e(TAG, "网络无法识别！");
				return false;				
		}
		//创建APN
		ContentValues   row = new  ContentValues();
		row.put("_id", apnNode.id);
        row.put( "apn", apnNode.apn);
        row.put("name", apnNode.name);
        row.put("numeric", apnNode.numeric);
        row.put("mcc", apnNode.mcc);
        row.put("mnc", apnNode.mnc); 
        if (mGprsType ==GPRS_TYPE_TELECOM_2G || mGprsType == GPRS_TYPE_TELECOM_3G) {
        	row.put("proxy", apnNode.proxy);
        	row.put("port", apnNode.port);
        }
        mContext.getContentResolver().insert(URL_APN_LIST, row);		
		//设置创建的APN为当前APN
		ContentResolver resolver = mContext.getContentResolver();  
		ContentValues values = new ContentValues();  
		values.put("apn_id", APN_ID);  
		if (resolver.update(URL_APN_CURRENT, values, null, null)>0) {
			return true;
		} else {
			Log.e(TAG, "APN无效!");
			return false;
		}
		
			
	}
	
	/**
	 * 删除GPRS连接
	 * @param isDeleteAll 如果为true，则删除所有可用连接；否则只删除程序创建的连接
	 * @return 删除成功返回True，否则返回false
	 */
	public boolean DeleteGprsConnection(boolean isDeleteAll) {		
		if (isDeleteAll) {
			mContext.getContentResolver().delete(URL_APN_LIST, "current=?", new String[]{"1"});
		} else {
			mContext.getContentResolver().delete(URL_APN_LIST, "_id=?", new String[]{String.valueOf(APN_ID)});
		}
		return true;
	}
	
		
	/**获取GPRS类型*/
	private int getGprsType() {
		TelephonyManager telManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);			
		String imsi = telManager.getSubscriberId();  
		if(imsi!=null){  			
		    if (imsi.startsWith(NUMERIC_TYPE_MOBILE_TD)) {
		    	return GPRS_TYPE_MOBIL_3G;
		    } else if (imsi.startsWith(NUMERIC_TYPE_UNICOM)) {
		    	int networkType = telManager.getNetworkType();
		    	if (networkType != TelephonyManager.NETWORK_TYPE_GPRS ||networkType != TelephonyManager.NETWORK_TYPE_EDGE) {
		    		return GPRS_TYPE_UNICOM_3G;
		    	} else {
		    		return GPRS_TYPE_UNICOM_2G;
		    	}		    	
		    } else if (imsi.startsWith(NUMERIC_TYPE_MOBILE_GSM)) {
		    	return GPRS_TYPE_MOBIL_2G;
		    } else if (imsi.startsWith(NUMERIC_TYPE_TELECOM)) {
		    	int networkType = telManager.getNetworkType();
		    	if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
		    		return GPRS_TYPE_TELECOM_2G;
		    	} else {
		    		return GPRS_TYPE_TELECOM_3G;
		    	}			    	
		    } else {
		    	return GPRS_TYPE_UNKOWN;
		    }		    
		} 
		return GPRS_TYPE_UNKOWN;
	}
		
	/**存取点名称(APN)类*/
	private class ApnNode {
		private int id;
		private String apn;
		private String name;
		private String numeric;
		private String mcc;
		private String mnc;
		private String proxy;
		private String port;
		public ApnNode(int id, String apn, String name, String numeric) {
			super();
			this.id = id;
			this.apn = apn;
			this.name = name;
			this.numeric = numeric;
			this.mcc = numeric.substring(0, 3);
			this.mnc = numeric.substring(3, 5);
		}
		
		public void setProxy(String proxy,String port){
			this.proxy = proxy;
			this.port = port;
		}		
	
	}
	
	/** 日志打印类 */
	private static class LogCat {
		public final static boolean DEBUG = true;
		public static void v(String tag, String mes) {
			if (DEBUG) {
				Log.v(tag, mes);
			}
		}
	}
	
	/**广播监听类*/
	private class ConnectionChangeReciver extends BroadcastReceiver {
		private boolean mCheckFirst = true;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub		
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {	
				ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connec.getActiveNetworkInfo();
				if (activeNetInfo != null && activeNetInfo.isAvailable()) {
					LogCat.v(TAG, "连接创建成功");
					mIsConnected = true;
					if (mTimer != null) {
						mTimer.purge();
					}
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_CONNECTED);
	                mContext.sendBroadcast(tmpIntent); 
				} else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) == true && mIsConnected){
					Log.e(TAG, "网络中断！");
					mIsConnected = false;
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_DIS_CONNECTED);
	                mContext.sendBroadcast(tmpIntent);
				}
				if(mHandler!=null){
					Message msg = mHandler.obtainMessage(
							HandlerWhat.NETWORK_CONNECT_RESULE,new Boolean(mIsConnected));
					mHandler.sendMessage(msg);
				}
			}
			
		}
	}
	
	/**网络延时计时器*/
	private class TimeOutCheckTimer extends TimerTask {			
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connec.getActiveNetworkInfo();
			if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
				Log.e(TAG, "本次连接尝试失败");
				WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
				if(mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
				}
				if (mIsAuto) {	
					mIsAuto = false;
					mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);
					createGprsConnection(false);
				} else {
					Log.e(TAG, "连接超时，无法建立网络连接");
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_CONNECT_NO);
	                mContext.sendBroadcast(tmpIntent); 

					if(mHandler!=null){
						Message msg = mHandler.obtainMessage(
								HandlerWhat.NETWORK_CONNECT_RESULE,new Boolean(false));
						mHandler.sendMessage(msg);	
					}
				}				
			}
		}
	}
	
	/**
	 * 验证网络是否可用
	 * @return
	 */
	public static boolean checkInternet(Context context) {
		//activity = null;//模拟器测试不判断网络状况
		if (context != null) {
			boolean flag = false;
			ConnectivityManager cwjManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = cwjManager.getActiveNetworkInfo();
			if (network != null) {
				flag = network.isAvailable();
			}else{
				NetworkInfo[] info = cwjManager.getAllNetworkInfo(); 
				if (info != null) {
					for (int i = 0; i < info.length; i++) { 
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true; 
						}
					}
				}
			}
			return flag;
		}
		return true;
	}

}
