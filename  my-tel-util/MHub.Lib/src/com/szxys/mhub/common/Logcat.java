/*
 * @(#)Logcat.java	1.00 11/04/20
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
package com.szxys.mhub.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 日志管理类
 * @author xak
 * @version 1.0
 */
public final class Logcat {
	/**参考类别*/
	public final static int GRADE_VERBOSE = 0;
	
	/**调试类别*/
	public final static int GRADE_DEBUG = 1;
	
	/**提示类别*/
	public final static int GRADE_INFO = 2;
	
	/**警告类别*/
	public final static int GRADE_WARN = 3;
	
	/**错误类别*/
	public final static int GRADE_ERROR = 4;

	private final static int SAVE_TIME = 7;
	private final static int FILE_MAX_SIZE = 2000000;        //2M
	private final static int FILE_MAX_COUNT = 200;        //所有日志文件不超过400M
	private static final int BUF_SIZE = 128;			  //根据速度和内存占用均衡
	private final static String FILE_NAME_POSTFIX = ".log";
	private final static String PATH = "log";
	private final static String START_TIME_STRING = "starttime";
	private final static String LAST_FILE_NAME = "filename";
	private final static String LAST_FILE_SIZE = "filesize";
	private final static String FILE_COUNT = "filecount";
	private static final String TAG = null;
	
	private static int mGrade = GRADE_INFO;	
	private static int mSaveTime = SAVE_TIME;
	private static SimpleDateFormat mDateformat;
	private static boolean mRunFlag = true;
	private static Thread writeFileThread;	
	private static ByteQueue mQueue = new ByteQueue(BUF_SIZE);
	private static saving  save = new saving();
	private static int mFileCount;
	private static int mFileSize;

	

	private static FileRW mLogFile;
	private static String mLastFile;

	private static Context mContext;

	private static SharedPreferences mConfig;
	

	/**
	 * 打印参考类别（等级GRADE _VERBOSE）的日志信息。
	 * @param TAG   日志标识
	 * @param Msg	日志内容
	 */
	public  static void v(String TAG, String msg){
		Log.v(TAG, msg);
		if (mGrade <= GRADE_VERBOSE) {
			saveLogInfo("V",TAG,msg);
		}
	}

	/**
	 * 打印调试类别（等级GRADE_DEBUG）的日志信息
	 * @param TAG   日志标识
	 * @param Msg	日志内容
	 */
	public static void d(String TAG, String msg){
		Log.d(TAG, msg);
		if (mGrade <= GRADE_DEBUG) {
			saveLogInfo("D",TAG,msg);
		}
	}	

	/**
	 * 打印提示类别（等级GRADE_INFO）的日志信息
	 * @param TAG   日志标识
	 * @param Msg	日志内容
	 */
	public static void i(String TAG, String msg){
		Log.i(TAG, msg);
		if (mGrade <= GRADE_INFO) {
			saveLogInfo("I",TAG,msg);
		}
	}

	/**
	 * 打印警告类别（等级GRADE_WARN）的日志信息
	 * @param TAG   日志标识
	 * @param Msg	日志内容
	 */
	public static void w(String TAG, String msg){
		Log.w(TAG, msg);
		if (mGrade <= GRADE_WARN) {
			saveLogInfo("W",TAG,msg);
		}
	}

	/**
	 * 打印错误类别（等级GRADE_ERROR）的日信息
	 * @param TAG   日志标识
	 * @param Msg	日志内容
	 */
	public static void e(String TAG, String msg){
		Log.e(TAG, msg);
		if (mGrade <= GRADE_ERROR) {
			saveLogInfo("E",TAG,msg);
			
		}
	}
	
	/**
	 * 设置日志等级
	 * @param grade 日志等级，有日志等级大于或等于该等级的日子才被存储。
	 * SaveGrade按等级从低到高可以为GRADE_VERBOSE、GRADE_DEGUG、
	 * GRADE_INFO、GRADE_WARN或GRADE_ERROE。
	 */
	public static void setLogGrade(int grade) {
		mGrade = grade;
	}
	
	/**
	 * 设置日志存储天数
	 * @param time 日志存储天数
	 */
	public static void setSaveDate(int time) {
		mSaveTime = time;
	}
	
	public static void clearConfig() {
		SharedPreferences.Editor configEdit = mConfig.edit();
		configEdit.remove(START_TIME_STRING);
		configEdit.remove(LAST_FILE_NAME);
		configEdit.remove(LAST_FILE_SIZE);
		configEdit.remove(FILE_COUNT);
		configEdit.commit();
	}
	
	/**
	 * 启动日志模块
	 * @param  
	 */
	public static void start(Context content) {
		mContext = content;
		mDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long saveTime = mSaveTime*24*60*60*1000;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		mConfig = mContext.getSharedPreferences("log_config", Context.MODE_PRIVATE);		
		mLogFile = new FileRW(PATH, null, null); 
		
		String startTimeS =  mConfig.getString(START_TIME_STRING,null);
		if (startTimeS!= null) {		
			try {
				if (today.getTime() - df.parse(startTimeS).getTime() > saveTime ) {
					deleteFiles(saveTime, df, today);
				} 
				mLastFile = mConfig.getString(LAST_FILE_NAME,null);			
				if(mLastFile!=null && mLastFile.contains(df.format(today))) {
					mLogFile.Close();	              	
		        	mLogFile = new FileRW(PATH, mLastFile, null); 
		        	mFileSize = mConfig.getInt(LAST_FILE_SIZE,0);	        	       
				} else {
					mLogFile.Close();	
					mLastFile = df.format(today)+ "_" +mFileCount+FILE_NAME_POSTFIX;
		        	mLogFile = new FileRW(PATH, mLastFile , null);	
				}
				mFileCount =mConfig.getInt(FILE_COUNT,0);				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
				
		} else {
			mLogFile.Close();	
			mLastFile = df.format(today)+ "_" +mFileCount+FILE_NAME_POSTFIX;
        	mLogFile = new FileRW(PATH, mLastFile , null);   
	
			SharedPreferences.Editor configEdit = mConfig.edit();
			configEdit.putString(START_TIME_STRING, df.format(new Date(today.getTime())));
			configEdit.commit();			
		}
		writeFileThread = new Thread(save);
		writeFileThread.setDaemon(true);
		writeFileThread.start();
	}	
	
	/**
	 * 释放相关资源，可以再程序退出时调用
	 */
	public static void release() {
		saveInfo();
		mRunFlag = false;	
		mLogFile.Close();
		if (writeFileThread!=null) {
			writeFileThread = null;
		}
	}
	
	/**保存一些参数信息*/
	private static void saveInfo() {	
		Log.w(TAG, "save Log config info");
		SharedPreferences.Editor configEdit = mConfig.edit();
		configEdit.putInt(LAST_FILE_SIZE, mFileSize);
		configEdit.putString(LAST_FILE_NAME,mLastFile);
		configEdit.putInt(FILE_COUNT, mFileCount);
		configEdit.commit();
	}
	
	/**存储日志信息*/
	private  synchronized static void saveLogInfo(String type,String TAG, String msg) {	
		if(mLogFile == null) {
			return;
		}
		Date date = new Date();
		String log = type + "" +mDateformat.format(date) + " " + TAG + " " +msg + "\n";
		try {
			mQueue.add(log.getBytes());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	
	/**删除日志文件*/
	private static void deleteFiles(final long saveTime, final SimpleDateFormat df,
			final Date today)  {
		new Thread() {
			public void run() {
				
				String[] fileName = mLogFile.fileList();
				if (fileName == null) {
					return;
				}
				Date criticalTime = new Date(today.getTime() - saveTime);		
				for (int i=0; i<fileName.length; i++) {
					try {
						if(fileName[i].length()>10 && df.parse(fileName[i].substring(0,10)).before(criticalTime)) {
							mLogFile.deleteFile(PATH, fileName[i]);	
							mFileCount--;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				SharedPreferences.Editor configEdit = mConfig.edit();		
				configEdit.putString(START_TIME_STRING, df.format(new Date(today.getTime())));
				configEdit.putInt(FILE_COUNT, mFileCount);
				configEdit.commit();
			}
		}.start();		
	}

	/**存储日志类*/
	private static class saving implements Runnable {		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (mRunFlag) {	
				try {
					byte[] buf;
					buf = mQueue.removeAll();
					if (buf.length + mFileSize > FILE_MAX_SIZE) {
						mLogFile.Close();
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Date today = new Date();
						mLastFile = df.format(today)+ "_" +mFileCount+FILE_NAME_POSTFIX;
			        	mLogFile = new FileRW(PATH, mLastFile , null);	   
						mFileSize = 0;
						mFileCount++;
						SharedPreferences.Editor configEdit = mConfig.edit();			
						configEdit.putInt(FILE_COUNT, mFileCount);
						configEdit.putString(LAST_FILE_NAME, mLastFile);
						configEdit.commit();
						if (mFileCount > FILE_MAX_COUNT) {
							//暂未处理
						}
					} 
					mFileSize+= buf.length;
					mLogFile.WriteFile(buf, true);		
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}		
	}
	
	

}