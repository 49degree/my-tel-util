package com.xys.ecg.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.util.Log;

/**
 * ��¼��־������
 * 
 * @author yangxp
 * 
 */
public class Logger {
	private Class operateClass = null;
	private static String logPath = "/sdcard/ecg/logs";
	private static String logFile = "Ecg.log";
	private static SimpleDateFormat fileTimeFormat  = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dataTimeFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int overallLevel = Level.DEBUG;//����ȫ����־����
	private int localLevel = Level.NULL;//���嵱ǰ�������־����
	//����һ����̬��,������־����   
    public static final class Level{
    	public static final int NULL=0;
    	public static final int DEBUG=1;
    	public static final int INFO=2;
    	public static final int WARN=3;
    	public static final int ERROR=4;
    }
    
 
    
	/**
	 * ��ȡ��־����
	 * @param operateClass
	 * @return
	 */
	public static Logger getLogger(Class operateClass){
		return new Logger(operateClass);
	}
	/**
	 * ���캯��
	 * @param operateClass
	 */
	public Logger(Class operateClass){
		localLevel = Logger.overallLevel;
		this.operateClass = operateClass;	
	}
	
	/**
	 * ���캯��
	 * @param operateClass
	 */
	public Logger(Class operateClass,int localLevel){
		this.localLevel = localLevel;
		this.operateClass = operateClass;	
	}
	
	/**
	 * DEBUG������־����
	 * @param logInfo
	 */
	public void debug(String logInfo){
		if(localLevel<Level.INFO){//������־�����ж��Ƿ���Ҫ��ӡ
			//��ȡ��������Ϣ
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();	   
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        logInfo += "[ECG]";
	        Log.d(traceInfo.toString(),logInfo);
	        this.logInfo(logInfo,traceInfo);//��¼��־�ļ�
		}

	}
	/**
	 * info������־����
	 * @param logInfo
	 */
	public void info(String logInfo){

		if(localLevel<Level.WARN){
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.i(traceInfo.toString(),logInfo);
	        this.logInfo(logInfo,traceInfo);
		}
	}
	/**
	 * warn������־����
	 * @param logInfo
	 */
	public void warn(String logInfo){
		if(localLevel<Level.ERROR){
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.w(traceInfo.toString(),logInfo);
	        this.logInfo(logInfo,traceInfo);
		}
	}
	/**
	 * error������־����
	 * @param logInfo
	 */
	public void error(String logInfo){
		
		if(localLevel<=Level.ERROR){
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.e(traceInfo.toString(),logInfo);
	        this.logInfo(logInfo,traceInfo);
		}
		
	}

	/**
	 * ��¼��־��ϢlogInfo����־�ļ��У� 
	 * ���logFile�ļ����ڣ���ֱ��д�룬 �粻�������½��ļ�
	 * 
	 * @param filePath
	 * @param fileName
	 * @param logInfo
	 */
	public void logInfo(String logInfo,StringBuffer traceInfo) {
		String fileName = new StringBuffer().append(logFile).append(".").append(this.getDataTime(fileTimeFormat)).toString();

		StringBuffer info = new StringBuffer(this.getDataTime(dataTimeFormat)).append("-").append(traceInfo).append("  ").append(logInfo).append("\n");
		//����Ŀ¼
		if(createDir(logPath)==null)
			return;
		try{
			//�ж���־�ļ��Ƿ����
			File f=new File(logPath,fileName); 
			if(!f.exists()){//���fileName�Ƿ����
				f.createNewFile();//�ڵ�ǰĿ¼�½���һ����ΪfileName���ļ� 
			}
			//����һ����RandomAccessFile�Ķ��󣬲�ʵ���� 
			java.io.RandomAccessFile rf=new java.io.RandomAccessFile(logPath + "\\"+fileName,"rw"); 
			rf.seek(rf.length());//��ָ���ƶ����ļ�ĩβ 
			
			rf.write(EncodingUtils.getBytes(info.toString(), "GBK"));
			rf.close();//�ر��ļ��� 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	/**
	 * �ж�Ŀ¼�Ƿ���ڣ��������򴴽��ļ��У��ɹ������ļ��е�·����ʧ�ܷ��ؿ�
	 * @param filePath
	 */
	public String createDir(String filePath) {
		File fileDir = null; // �ļ�������
		boolean hasDir = false; // ��ʾ�ļ��������Ƿ����
		fileDir = new File(filePath); // �����ļ�������
		hasDir = fileDir.exists(); // �ж��ļ��������Ƿ����
		if (!hasDir) {
			String[] fileDirs = filePath.split("/");
			StringBuffer fileDirStr = new StringBuffer();
			for(int i=0;i<fileDirs.length;i++){
				fileDir = new File(fileDirStr.append("/").append(fileDirs[i]).toString());
				if(!fileDir.exists()){
					hasDir = fileDir.mkdir();
				}
			}
			//hasDir = fileDir.mkdir();
		}
		//�ж��Ƿ�ɹ�
		if(!hasDir){
			filePath = null;
		}
		return filePath;
	}
	
	
	//��ȡ��ǰʱ���ַ���
	public String getDataTime(SimpleDateFormat dataFormat){
		return dataFormat.format(new Date());
	}
	
	public int getLocalLevel() {
		return localLevel;
	}
	public void setLocalLevel(int localLevel) {
		this.localLevel = localLevel;
	}
	
	
}
