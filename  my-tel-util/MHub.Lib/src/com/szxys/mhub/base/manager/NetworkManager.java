/*
 * @(#)NetWorkTools.java	1.00 11/04/21
 *
 * Copyright (c) 2011-2013  New Element Inc. 
 * 9/10f, Building 2, Financial Base, No.6 Keyuan Road, 
 * Nanshan District, Shenzhen 518057
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * New Element Medical Equipment TechnoLogcaty Development CO., Ltd 
 * ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with New Element.
 */
package com.szxys.mhub.base.manager;

import java.util.Timer;
import java.util.TimerTask;

import com.szxys.mhub.common.Logcat;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;



/**
 * 网络管理工具类
 * @author xak
 * @version 1.0
 */
public class NetworkManager {
	/*网络连接类型*/
	/**无网络连接*/
	public final static int TYPE_NETWORK_NONE = 0;		
	/**WIFI连接*/
	public final static int TYPE_WIFI = 1;			
	/**GPRS连接*/
	public final static int TYPE_GPRS = 2;	
	/**2G连接*/
	public final static int TYPE_GPRS_2G = 3;	
	/**所有网络连接*/
	public final static int TYPE_ALL = 4;
	/**未知网络连接*/
	public final static int TYPE_NETWORK_UNKNOW = 5;	
	/**根据配置文件自动选择网络类型，默认先检测当前是否存在可用网络（包括USB网络连接），
	 * 如果不存在，则优先建立WIFI连接，然后是3G,最后建立2G连接*/
	public final static int TYPE_AUTO = 6;	
	
	/*监听事件信息*/
	/**创建连接成功消息*/
	public static final int NETWORK_CONNECTED = 0;
	/**创建连接失败消息*/
	public static final int NETWORK_CONNECT_NO = 1;
	/**网络中断消息*/
	public static final int NETWORK_DIS_CONNECTED = 2;
	/**网络正在连接*/
	public static final int NETWORK_CONNECTING = 3;
	
	/*错误码*/
	/**参数错误*/
	public static final int PARAM_ERROR = -1;
	/**APN错误*/
	public static final int APN_ERROR = -2;	
	/**删除连接失败*/
	public static final int DEL_ERROR = -3;	
	
	/**操作成功*/
	public static final int RESULT_OK = 0;	
		
	/*APN NUMERIC*/
	private final String NUMERIC_TYPE_MOBILE_TD = "46000";
	private final String NUMERIC_TYPE_UNICOM = "46001";
	private final String NUMERIC_TYPE_MOBILE_GSM = "46002";
	private final String NUMERIC_TYPE_TELECOM = "46003";	
	
	/*GPRS网络类型*/	
	private final static int GPRS_TYPE_UNKOWN = 0;			//未知网络
	private final static int GPRS_TYPE_UNICOM_2G = 1;		//中国联通(2G)
	private final static int GPRS_TYPE_UNICOM_3G = 2;		//中国联通(3G)
	private final static int GPRS_TYPE_MOBIL_2G = 3;		//中国移动GSM(2G)
	private final static int GPRS_TYPE_MOBIL_3G = 4;		//中国移动TD(3G)
	private final static int GPRS_TYPE_TELECOM_2G = 5;		//中国电信(2G)
	private final static int GPRS_TYPE_TELECOM_3G = 6;		//中国电信(3G)
		
	private final static String TAG = "NetWorkTools";	
	private final static Uri URL_APN_LIST = Uri.parse("content://telephony/carriers");	
	private final static int APN_ID = 10001;
	private final static String APN_PROXY_TELECOM = "10.0.0.200";
	private final static String APN_PORT_TELECOM = "80";
	private final static int TIME_OUT = 10000;
	private static final String FILENAME = "apn.txt";
	private static final String PATH = "apnset";
	
	
	private Context mContext;
	private int mGprsType;
	private boolean mIsAuto;
	private boolean mIsConnected;
	protected Timer mTimer;
	private ConnectionChangeReciver mReceiver;
	private OnNetworkStatusChangeListener mListener;
	private String mImsi;

	/**
	 * 默认构造器
	 * @param context
	 */
	public NetworkManager(Context context){
		mContext = context;
		mGprsType = -1;			
		mReceiver = new ConnectionChangeReciver();
	    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	    mContext.registerReceiver(mReceiver, filter);			
	}

	/**
	 * 获取当前网络连接类型
	 * @return  当前网络连接类型；可能为
	 * {@link #TYPE_NETWORK_NONE}, {@link #YPE_WIFI}, 或者 {@link #TYPE_GPRS}。
	 */
	public int getNetWorkInfo() {
		ConnectivityManager connec = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo activeNetInfo = connec.getActiveNetworkInfo(); 
		if (activeNetInfo!=null && activeNetInfo.isAvailable()) {
			if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return TYPE_WIFI;
			} else {
				return TYPE_GPRS;
			}
		} else {
			return TYPE_NETWORK_NONE;
		}		
	}	

	/**
	 * 创建网络连接，通过参数指定需要创建的网络类型。如果创建出错，则返回错误码、如果当前网络可用则返回 {@link #NETWORK_CONNECTED}；
	 * 如果正在建立连接则返回 {@link #NETWORK_CONNECTING}。当返回值为NETWORK_CONNECTIONG时，需要注册 {@link #OnNetworkStatusChangeListener}
	 * 监听器，如果创建成功则触发{@link #NETWORK_CONNECTED}事件，否则触发 {@link #NETWORK_CONNECT_NO}事件。
	 * 
	 * @param networkType 创建的网络连接类型，可以为 {@link #TYPE_AUTO}、 {@link #TYPE_WIFI}、 {@link #TYPE_GPRS}、
	 *  	  {@link #TYPE_GPRS_3G} 或者 {@link #TYPE_GPRS_2G}
	 *  @return {@link #NETWORK_CONNECTED}、{@link #NETWORK_CONNECTING}或者错误代码
	 */
	public int createNetworkConnection(int networkType){
		if (getNetWorkInfo()!= TYPE_NETWORK_NONE) 
		{			
			return NETWORK_CONNECTED;			
		}
		switch (networkType) {
			case TYPE_AUTO:
				return createAutoConnection();			
			case TYPE_WIFI:
				return createWifiConnection();			
			case TYPE_GPRS:
				return createGprsConnection(false);	
			case TYPE_GPRS_2G:
				return createGprsConnection(true);				
			default:
				return PARAM_ERROR;
		}
	}
	
	/**
	 * 删除网络连接；可以指定需要删除的网络连接的类型。
	 * 
	 * @param networkType
	 */
	public int deleteNetworkConnection(int networkType) {
		switch (networkType) {
		case TYPE_ALL:
			if (DelWifiConnection() == RESULT_OK
					&& DeleteGprsConnection(true) == RESULT_OK) {
				return RESULT_OK;
			} else {
				return DEL_ERROR;
			}
		case TYPE_WIFI:
			return DelWifiConnection();
		case TYPE_GPRS:
			return DeleteGprsConnection(true);
		default:
			return PARAM_ERROR;
		}
	}

	/**
	 * 设置网络状态变化监听器
	 * @param ls 网络状态变化监听器
	 */
	public void setOnNetworkStatusChangeListener(OnNetworkStatusChangeListener ls){
		mListener = ls;
	}	
	
	/**
	 *  
	 */
	public void stopMonitor (){
		if (mReceiver!=null) {
			mContext.unregisterReceiver(mReceiver);
		}	
	}
	
	/**创建wifi连接*/
	private int createWifiConnection() {
		// TODO Auto-generated method stub
		Logcat.v(TAG, "开始创建WIFI连接...");
		mTimer = new Timer();
		mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);       
		IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, filter);
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		WifiLock mWifiLock = mWifiManager.createWifiLock("NetWorkTools");		
		if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(true);   
            Logcat.v(TAG, "打开WIFI");
        }  
		return NETWORK_CONNECTING;
	}

	/**自动创建连接*/
	private int createAutoConnection() {
		// TODO Auto-generated method stub
		mIsAuto = true;
		return createWifiConnection();			
	}

	
	
	/**
	 * 创建GPRS连接，优先使用3G网络
	 * @param isUseOnly2G 如果为True则只使用2G网络，否则优先使用3G网络
	 * @return 创建成功返回NETWORK_CONNECTING；否则返回错误码
	 */
	private int createGprsConnection(boolean isUseOnly2G) {
		Logcat.v(TAG, "开始创建GPRS连接...");
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT); 
		}		
		mGprsType = getGprsType();
		Logcat.v(TAG, "GPRS网络类型为:" + mGprsType);
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
				Logcat.w(TAG, "网络未知！");
				apnNode = new ApnNode(APN_ID, "unknow", "uknow", mImsi.substring(0, 5));
		
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
		return NETWORK_CONNECTING;
	}
	
	/**
	 * 删除GPRS连接
	 * @param isDeleteAll 如果为true，则删除所有可用连接；否则只删除程序创建的连接
	 * @return 删除成功返回RESULT_OK，否则返回错误码
	 */
	private int DeleteGprsConnection(boolean isDeleteAll) {		
		if (isDeleteAll) {
			mContext.getContentResolver().delete(URL_APN_LIST, "current=?", new String[]{"1"});
		} else {
			mContext.getContentResolver().delete(URL_APN_LIST, "_id=?", new String[]{String.valueOf(APN_ID)});
		}
		return RESULT_OK;
	}
	
	/**删除WIFI连接*/
	private int DelWifiConnection() {
		// TODO Auto-generated method stub
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);		
		if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(false);   
            Logcat.v(TAG, "关闭开WIFI");
        }  
		return RESULT_OK;
	}
	
	/**获取GPRS类型*/
	private int getGprsType() {
		TelephonyManager telManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);			
		mImsi = telManager.getSubscriberId();  
		if(mImsi!=null){  			
		    if (mImsi.startsWith(NUMERIC_TYPE_MOBILE_TD)) {
		    	return GPRS_TYPE_MOBIL_3G;
		    } else if (mImsi.startsWith(NUMERIC_TYPE_UNICOM)) {
		    	int networkType = telManager.getNetworkType();
		    	if (networkType == TelephonyManager.NETWORK_TYPE_GPRS ||networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
		    		return GPRS_TYPE_UNICOM_2G;
		    	} else {
		    		return GPRS_TYPE_UNICOM_3G;
		    	}		    	
		    } else if (mImsi.startsWith(NUMERIC_TYPE_MOBILE_GSM)) {
		    	return GPRS_TYPE_MOBIL_2G;
		    } else if (mImsi.startsWith(NUMERIC_TYPE_TELECOM)) {
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
	
	/**广播监听类*/
	private class ConnectionChangeReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub		
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {	
				Logcat.v(TAG, "网络状态变化");
				ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connec.getActiveNetworkInfo();
				if (activeNetInfo!= null && activeNetInfo.isConnected() && !mIsConnected) {
					Logcat.v(TAG, "连接创建成功");
					mIsConnected = true;
					if (mTimer != null) {
						mTimer.purge();
					}						
					mListener.onNetworkStatusChange(NETWORK_CONNECTED);
				} else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) == true && mIsConnected){
					Logcat.e(TAG, "网络中断！");
					mIsConnected = false;
					mListener.onNetworkStatusChange(NETWORK_DIS_CONNECTED);
				}
			}			
		}//onReceive end
	}
	
	/**网络延时计时器*/
	private class TimeOutCheckTimer extends TimerTask {			
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connec.getActiveNetworkInfo();
			if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
				Logcat.e(TAG, "本次连接尝试失败");
				WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
				if(mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
				}
				if (mIsAuto) {	
					mIsAuto = false;
					mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);
					createGprsConnection(false);
				} else {
					Logcat.e(TAG, "连接超时，无法建立网络连接");
					mListener.onNetworkStatusChange(NETWORK_CONNECT_NO);	               
				}				
			}
		}
	}

}