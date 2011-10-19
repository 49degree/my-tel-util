/*
 * @(#)NetWorkTools.java	1.00 11/03/11
 *
 * Copyright (c) 2011-2013  New Element Inc. 
 * 9/10f, Building 2, Financial Base, No.6 Keyuan Road, 
 * Nanshan District, Shenzhen 518057
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * New Element Medical Equipment Technology Development CO., Ltd 
 * ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with New Element.
 */
package com.xys.ecg.network;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * �������ӹ�����
 * @version 1.0
 */
public class NetWorkTools {
	/*��ǰ������������*/
	/**����������*/
	public final static int NET_TYPE_NONE = 0;		
	/**WIFI����*/
	public final static int NET_TYPE_WIFI = 1;			
	/**GPRS����*/
	public final static int NET_TYPE_GPRS = 2;		
	/**�������ӳɹ���Ϣ*/
	public static final int STATUS_CONNECTED = 0;
	/**��������ʧ����Ϣ*/
	public static final int STATUS_CONNECT_NO = 1;
	/**�����ж���Ϣ*/
	public static final int STATUS_DIS_CONNECTED = 2;
	
	/**
	 * �㲥��Ϊ��ָ������״̬�ı䣬ͨ�������ö����ܵ�֪�������ӵĽ�����Լ���ó������й����������һЩ�仯�����
	 * ����������״̬��Ϣ
	 * @see EXTRA_STATUS_INFO
	 */
	public final static String ACTION_CONNECTION_STATUS_CHANGE ="com.guanri.ACTION_CONNECTION_STATUS_CHANGE";
	
	/**��ǰ������Ϣ��ͨ��{@link android.content.Intent#getStringExtra(String,String)}����ȡ��*/
	public final static String EXTRA_STATUS_INFO = "statusInfo";	
	
	/*APN NUMERIC*/
	private final String NUMERIC_TYPE_MOBILE_TD  = "46000";
	private final String NUMERIC_TYPE_UNICOM     = "46001";
	private final String NUMERIC_TYPE_MOBILE_GSM = "46002";
	private final String NUMERIC_TYPE_TELECOM    = "46003";
	
	/*GPRS��������*/
	private final static int GPRS_TYPE_UNKOWN     = 0;		//δ֪����
	private final static int GPRS_TYPE_UNICOM_2G  = 1;		//�й���ͨ(2G)
	private final static int GPRS_TYPE_UNICOM_3G  = 2;		//�й���ͨ(3G)
	private final static int GPRS_TYPE_MOBIL_2G   = 3;		//�й��ƶ�GSM(2G)
	private final static int GPRS_TYPE_MOBIL_3G   = 4;		//�й��ƶ�TD(3G)
	private final static int GPRS_TYPE_TELECOM_2G = 5;		//�й�����(2G)
	private final static int GPRS_TYPE_TELECOM_3G = 6;		//�й�����(3G)
		
	private final static String TAG = "NetWorkTools";	
	private final static Uri URL_APN_LIST = Uri.parse("content://telephony/carriers");
	private final static Uri URL_APN_CURRENT = Uri.parse("content://telephony/carriers/preferapn");
	private final static int APN_ID = 10001;
	private final static String APN_PROXY_TELECOM = "10.0.0.200";
	private final static String APN_PORT_TELECOM = "80";
	private final static int TIME_OUT = 10000;
	
	
	private Context mContext;
	private int mGprsType;
	private boolean mIsAuto;
	private boolean mIsConnected;
	private ConnectionChangeReciver mReceiver;
	private Timer mTimer;
	
	public NetWorkTools(Context context, Handler handler) {
		super();
		mContext = context;
		mGprsType = -1;		
		mReceiver = new ConnectionChangeReciver();
	    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	    mContext.registerReceiver(mReceiver, filter);
		// TODO Auto-generated constructor stub
	}	
	
	/**
	 * �Զ�������粢��������;
	 * @return �ɹ�����true�����򷵻�false
	 */
	public void autoConnect() {
		if (getNetWorkInfo() == NET_TYPE_NONE) 
		{
			createConnection();	
		}
				
	}

	/**
	 * �����������ӣ�����ʹ��WIFI�����WIFI�޷�ʹ�ã���ʹ��GPRS��GPRS����������ʹ��3G����
	 * @return	�����ɹ�����true��ʧ�ܷ���false
	 */
	public void createConnection() {
		createWifiConnection();
		mIsAuto = true;
	}
	
	/**
	 *  ȡ��������Ĭ�ϻ�ע��һ���㲥����������ϵͳ����״̬���������˳������ʱ��Ӧ��ע����
	 */
	public void cancelMmonitor(){
		if (mReceiver!=null) {
			mContext.unregisterReceiver(mReceiver);
		}	
	}
	
	/**
	 * ����WIFI����	
	 * @return �����ɹ�����true�����򷵻�false
	 */
	private boolean createWifiConnection() {
		// TODO Auto-generated method stub	
		LogCat.v(TAG, "��ʼ����WIFI����...");
		mTimer = new Timer();
		mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);       
		IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, filter);
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		WifiLock mWifiLock = mWifiManager.createWifiLock("NetWorkTools");		
		if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(true);   
            LogCat.v(TAG, "��WIFI");
        }  
		return false;
	}

	/**
	 * ��ȡ��ǰ������������
	 * @return  ��ǰ�����������ͣ�����Ϊ
	 * {@link #NET_TYPE_NONE}, {@link #NET_TYPE_WIFI}, ���� {@link #NET_TYPE_GPRS}��
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
	 * ����GPRS���ӣ�����ʹ��3G����
	 * @param isUseOnly2G ���ΪTrue��ֻʹ��2G���磬��������ʹ��3G����
	 * @return �����ɹ�����true�����򷵻�false
	 */
	public boolean createGprsConnection(boolean isUseOnly2G) {
		LogCat.v(TAG, "��ʼ����GPRS����...");
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT); 
		}		
		mGprsType = getGprsType();
		LogCat.v(TAG, "GPRS��������Ϊ:" + mGprsType);
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
				Log.e(TAG, "�����޷�ʶ��");
				return false;				
		}
		//����APN
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
		//���ô�����APNΪ��ǰAPN
		ContentResolver resolver = mContext.getContentResolver();  
		ContentValues values = new ContentValues();  
		values.put("apn_id", APN_ID);  
		if (resolver.update(URL_APN_CURRENT, values, null, null)>0) {
			return true;
		} else {
			Log.e(TAG, "APN��Ч!");
			return false;
		}
		
			
	}
	
	/**
	 * ɾ��GPRS����
	 * @param isDeleteAll ���Ϊtrue����ɾ�����п������ӣ�����ֻɾ�����򴴽�������
	 * @return ɾ���ɹ�����True�����򷵻�false
	 */
	public boolean DeleteGprsConnection(boolean isDeleteAll) {		
		if (isDeleteAll) {
			mContext.getContentResolver().delete(URL_APN_LIST, "current=?", new String[]{"1"});
		} else {
			mContext.getContentResolver().delete(URL_APN_LIST, "_id=?", new String[]{String.valueOf(APN_ID)});
		}
		return true;
	}
	
		
	/**��ȡGPRS����*/
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
		
	/**��ȡ������(APN)��*/
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
	
	/** ��־��ӡ�� */
	private static class LogCat {
		public final static boolean DEBUG = true;
		public static void v(String tag, String mes) {
			if (DEBUG) {
				Log.v(tag, mes);
			}
		}
	}
	
	/**�㲥������*/
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
					LogCat.v(TAG, "���Ӵ����ɹ�");
					mIsConnected = true;
					if (mTimer != null) {
						mTimer.purge();
					}				
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_CONNECTED);
	                mContext.sendBroadcast(tmpIntent); 
				} else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) == true && mIsConnected){
					Log.e(TAG, "�����жϣ�");
					mIsConnected = false;
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_DIS_CONNECTED);
	                mContext.sendBroadcast(tmpIntent);
				}
			}			
		}//onReceive end
	}
	
	/**������ʱ��ʱ��*/
	private class TimeOutCheckTimer extends TimerTask {			
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connec.getActiveNetworkInfo();
			if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
				Log.e(TAG, "�������ӳ���ʧ��");
				WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
				if(mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
				}
				if (mIsAuto) {	
					mIsAuto = false;
					mTimer.schedule(new TimeOutCheckTimer() , TIME_OUT);
					createGprsConnection(false);
				} else {
					Log.e(TAG, "���ӳ�ʱ���޷�������������");
					Intent tmpIntent = new Intent(ACTION_CONNECTION_STATUS_CHANGE);   
					tmpIntent.putExtra(EXTRA_STATUS_INFO, STATUS_CONNECT_NO);
	                mContext.sendBroadcast(tmpIntent); 
				}				
			}
		}
	}
}
