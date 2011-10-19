package com.szxys.mhub.base.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;

/**
 * 设备性能检测工具类，用来检测CPU、内存、FLASH、SD卡、电量的使用情况
 * @author sujinyi
 * */
public class PerformanceUtil {
	
	/**
	 * 工具类的单例对象
	 * */
	private static PerformanceUtil performanceUtil;
	
	/**
	 * 用于执行相关任务的上下文对象
	 */
	private Context context;
	
	/**
	 * 五秒前的cpu运行信息
	 * */
	private ArrayList<Long> o_cpuInfo=new ArrayList<Long>();
	
	/**
	 * cpu检测线程运行标识
	 * */
	private boolean isCheckRunning=false;
	
	/**
	 * 电池剩余电量
	 */
	private int batter=-1;
	
	/**
	 * cpu检测线程对象
	 * */
	private Runnable checkThread=new Runnable(){

		@Override
		public void run() {
		
			while(isCheckRunning){
				o_cpuInfo.clear();
				o_cpuInfo=getCPU();
				try {
					Thread.sleep(5000);
					System.out.println("run!");
				} catch (InterruptedException e) {
				
					e.printStackTrace();
				}
			}
		}
		
	};
	
	/**
	 * 用于监听电池信息的广播接收对象
	 */
	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {

                        int level = intent.getIntExtra("level", 0);
                        int scale = intent.getIntExtra("scale", 100);

                        batter=level*100/scale;
                        
                }
        }
	};
	
	
	/**
	 * 私有构造方法
	 * */
	private PerformanceUtil(Context context){
		this.context=context;
		this.registerBatterBroadCast();
	}
	
	/**
	 * 返回PerformanceUtil对象的静态方法
	 * @return PerformanceUtil :工具类对象
	 * */
	public static synchronized PerformanceUtil getPerformanceUtilInstance(Context context){
		
		if(performanceUtil==null){
			performanceUtil=new PerformanceUtil(context);
		}
		
		return performanceUtil;
	}
	
	
	/**
	 * 启动CPU检测线程
	 * @return boolean:  检测线程的运行标识，运行中为true
	 * */
	public synchronized boolean startCheck(){
		
		isCheckRunning=true;
		
		new Thread(checkThread).start();
		
		return isCheckRunning;
	}
	
	/**
	 * 停止CPU检测线程
	 * @return boolean: 检测线程的运行标识，运行为true
	 * */
	public boolean stopCheck(){
		isCheckRunning=false;
		
		return isCheckRunning;
	}
	
	/**
	 * 检测CPU使用情况
	 * @return int:  CPU占用率百分比,返回-1时，检测失败
	 * */
	public int getCPUInfo(){
		int info=-1;
		ArrayList<Long> oldInfo=new ArrayList<Long>(o_cpuInfo);
		
		if(oldInfo.size()!=0){
			ArrayList<Long> newInfo=getCPU();
			Long cpu_total1=(long) 0;
			Long cpu_total2=(long) 0;
			for(int i=0;i<o_cpuInfo.size();i++){
				cpu_total1+=(Long)oldInfo.get(i);
				cpu_total2+=(Long)newInfo.get(i);
			}
			
			Long idle1=oldInfo.get(3);
			Long idle2=newInfo.get(3);
			Long idle=idle2-idle1;
			info=(int) (100*(cpu_total2-cpu_total1-idle)/(cpu_total2-cpu_total1));
			
		}
		return info;
	}
	
	/**
	 * 检测内存占用情况
	 * @return long:   内存剩余字节
	 * */
	public long getMEMInfo(){
		long info=-1;
		File file=new File("\\proc\\meminfo");
		try {
			FileInputStream fis=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));
			br.readLine();
			info=Long.parseLong(getNumber(br.readLine())); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}
	
	/**
	 * 工具方法，从内存信息中提取数字
	 * @param str : 内存信息字符串
	 * @return String:   内存信息中的数字
	 * */
	private String getNumber(String str){
    	String number=null;
    	StringBuffer temp=new StringBuffer();
    	char c;
    	for(int i=0;i<str.length();i++){
    		c=str.charAt(i);
    		if(c>='0'&&c<='9')
    			temp.append(c);
    	}
    	number=new String(temp);
    	
    	return number;
    }
	
	/**
	 * 检测FLASH使用情况
	 * @return long:   FLASH剩余字节
	 * */
	public long getFLASHinfo(){
		long info=-1;
		
		File filePath = Environment.getDataDirectory();
		StatFs stat = new StatFs(filePath.getPath()); 
		int blocSize=stat.getBlockSize(); 
		int availableBlocks = stat.getAvailableBlocks();
		info=availableBlocks*blocSize;
		
		return info;
	}
	
	/**
	 * 检测SDCARD使用情况
	 * @return long:   SDCARD剩余字节
	 * */
	public long getSDCARDinfo(){
		long info=-1;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			 File filePath = Environment.getExternalStorageDirectory();     
			 StatFs stat = new StatFs(filePath.getPath()); 
			 int blocSize=stat.getBlockSize(); 
			 int availableBlocks = stat.getAvailableBlocks(); 
			 info=availableBlocks*blocSize;
		 }
		return info;
	}
	
	/**
	 * 检测电量剩余情况
	 * @return float:   剩余电量百分比
	 * */
	public int getBatteryInfo(){
		int info=-1;
		
		return info;
	}
	
	/**
	 * 获取CPU当前时刻使用信息
	 * @return ArrayList:保存当前CPU各类时间片的使用时间
	 * */
	private ArrayList<Long> getCPU(){
		ArrayList<Long> info=new ArrayList<Long>();
		File file=new File("\\proc\\stat");
		try {
			FileInputStream fis=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));
			String temp=br.readLine();
			
			String str_temp[]=temp.trim().split(" ");
			
			for(int i=2;i<str_temp.length;i++){
				
				info.add(Long.parseLong(str_temp[i]));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
		
	}
	
	/**
	 * 获取电池剩余电量信息
	 * @return :返回batter值，代表当前电量信息
	 */
	public int getBatter(){
		return batter;
	}
	
	/**
	 * 注册接收电量信息的广播
	 * @return :成功则返回true，失败则返回false
	 */
	private boolean registerBatterBroadCast(){
		boolean flag=false;
		try{
			context.registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			flag=true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 解除注册的接收电量信息的广播
	 * @return :成功解除返回true，失败返回false
	 */
	@SuppressWarnings("unused")
	private boolean unregisterBatterBroadCast(){
		boolean flag=false;
		try{
		context.unregisterReceiver(mBatteryInfoReceiver);
		flag=true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
}
