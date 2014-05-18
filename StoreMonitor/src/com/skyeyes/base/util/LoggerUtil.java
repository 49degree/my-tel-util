package com.skyeyes.base.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;
import android.util.Log;

import com.skyeyes.base.BaseApplication;

/**
 * 记录日志工具类
 * 
 * @author
 *  
 */
public class LoggerUtil {
	private Class operateClass = null;
	private static String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "nurse"+File.separator +"logs";
	private static String logFile = "nurse.log";
	private static SimpleDateFormat fileTimeFormat  = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dataTimeFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int overallLevel = Level.DEBUG;//定义全局日志级别
	private int localLevel = Level.DEBUG;//定义当前对象的日志级别
	//定义一个静态类,定义日志级别   
    public static final class Level{
    	public static final int NULL=0;
    	public static final int DEBUG=1;
    	public static final int INFO=2;
    	public static final int WARN=3;
    	public static final int ERROR=4;
    }
    
 
    
	/**
	 * 获取日志对象
	 * @param operateClass
	 * @return
	 */
	public static LoggerUtil getLogger(Class operateClass){
		return new LoggerUtil(operateClass);
	}
	/**
	 * 构造函数
	 * @param operateClass
	 */
	public LoggerUtil(Class operateClass){
		localLevel = LoggerUtil.overallLevel;
		this.operateClass = operateClass;	
	}
	
	/**
	 * 构造函数
	 * @param operateClass
	 */
	public LoggerUtil(Class operateClass,int localLevel){
		this.localLevel = localLevel;
		this.operateClass = operateClass;	
	}
	
	/**
	 * DEBUG级别日志方法
	 * @param logInfo
	 */
	public void debug(String logInfo){
		if(!BaseApplication.getInstance().isOutputLog())return;
		if(localLevel<Level.INFO){//根据日志级别判断是否需要打印
			//获取调用类信息
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();	   
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.d(traceInfo.toString(),logInfo);
	        this.logInfo(logInfo,traceInfo);//记录日志文件
		}

	}
	/**
	 * info级别日志方法
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
	 * warn级别日志方法
	 * @param logInfo
	 */
	public void warn(String logInfo){
		if(localLevel<Level.ERROR){
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.w(traceInfo.toString(),logInfo);
	        if(BaseApplication.getInstance().isWriteLog()){
		        this.logInfo(logInfo,traceInfo);
			}
		}
	}
	/**
	 * error级别日志方法
	 * @param logInfo
	 */
	public void error(String logInfo){
		
		if(localLevel<=Level.ERROR){
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.e(traceInfo.toString(),logInfo);
	        if(BaseApplication.getInstance().isWriteLog()){
		        this.logInfo(logInfo,traceInfo);
			}
		}
		
	}
	
	/**
	 * 记录异常堆栈信息
	 * @param ex
	 */
	public void logThrowable(Throwable ex){
		if(!BaseApplication.getInstance().isOutputLog())return;
		String info = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos);
			ex.printStackTrace(printStream);
			byte[] data = baos.toByteArray();
			info = new String(data);
			data = null;
			
	        StringBuffer traceInfo = new StringBuffer();    
	        StackTraceElement[] stacks = new Throwable().getStackTrace();
	        traceInfo.append(stacks[1].getClassName()).append("(").append(stacks[1].getLineNumber()).append(")");
	        Log.e(traceInfo.toString(),info);
			if(BaseApplication.getInstance().isWriteLog()){
		        this.logInfo(info,traceInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 记录日志信息logInfo到日志文件中， 
	 * 如果logFile文件存在，则直接写入， 如不存在则新建文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param logInfo
	 */
	public void logInfo(String logInfo,StringBuffer traceInfo) {
		String fileName = new StringBuffer().append(logFile).append(".").append(this.getDataTime(fileTimeFormat)).toString();

		StringBuffer info = new StringBuffer(this.getDataTime(dataTimeFormat)).append("-").append(traceInfo).append("  ").append(logInfo).append("\n");
		//创建目录
		if(createDir(logPath)==null)
			return;
		try{
			//判断日志文件是否存在
			File f=new File(logPath,fileName); 
			if(!f.exists()){//检查fileName是否存在
				f.createNewFile();//在当前目录下建立一个名为fileName的文件 
			}
			//定义一个类RandomAccessFile的对象，并实例化 
			java.io.RandomAccessFile rf=new java.io.RandomAccessFile(logPath + "/"+fileName,"rw"); 
			rf.seek(rf.length());//将指针移动到文件末尾 
			
			rf.write(EncodingUtils.getBytes(info.toString(), "GBK"));
			rf.close();//关闭文件流 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	/**
	 * 判断目录是否存在，不存在则创建文件夹，成功返回文件夹的路径，失败返回空
	 * @param filePath
	 */
	private String createDir(String filePath) {
		File fileDir = null; // 文件流变量
		boolean hasDir = false; // 标示文件流对象是否存在
		fileDir = new File(filePath); // 生成文件流对象
		hasDir = fileDir.exists(); // 判断文件流对象是否存在
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
		//判断是否成功
		if(!hasDir){
			filePath = null;
		}
		return filePath;
	}
	
	
	//获取当前时间字符串
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
