package com.guanri.android.jpos.pos;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

/**
 * 串口通信控制
 * 为单例模式
 * @author Administrator
 *
 */
public class PosCommandControlSerialPort extends PosCommandControlImp{
	static Logger logger = Logger.getLogger(PosCommandControlSerialPort.class);
	
	private static PosCommandControlImp instance = null;
	private SerialPortImp serialPortImp;// 串口
	private SerialPortThread readTask = null;
	private Timer timeOutTimer = null;
	private ReadTimerOut readTimerOut = null;
	private final static int TIME_OUT = 500;//读取数据超时
	private static byte[] HEADER_PACKET = {0X55,0X55,0X55,0X55,0X55};
	
	//保存读取的数据
	private byte[] returnData = new byte[1024];//读取数据缓存
	private int index = 0;//缓冲数据量（字节）
	boolean hasPacketHeader = false;//是否有数据头标识
	
	/**
	 * 获得控制对象
	 * @return
	 */
	public static PosCommandControlImp getInstance(){
		if(instance==null){
			instance = new PosCommandControlSerialPort();
		}
		return instance;
	}
	/**
	 * 构造函数
	 */
	private PosCommandControlSerialPort(){
		logger.debug("串口初始化.........");
		serialPortImp = SerialPortFactory.getSerialPort();//获取串口对象
		logger.debug("串口初始化结束.........");
		readTask = new SerialPortThread();//打开读取线程
		readTask.start();

	}

	
	private void clearTimeOutTimer(){
		if(timeOutTimer!=null){
			timeOutTimer.cancel();
			timeOutTimer.purge();
		}
	}
	
	/**
	 * 发送数据到串口设备，同一时间只能有一个线程调用
	 * 
	 */
	public synchronized void sendData(byte[] data,SendDataResultListener sendDataResultListener,long waitTime) throws IOException{
		try {
			clearTimeOutTimer();//清楚定时器中的操作
			readTimerOut = new ReadTimerOut(sendDataResultListener);
			timeOutTimer = new Timer();
			timeOutTimer.schedule(readTimerOut, waitTime);//设置超时时间及操作对象
			
			serialPortImp.mOutputStream.write(data);
			serialPortImp.mOutputStream.flush();
          } catch (IOException e) {
        	  logger.debug("发送数据出现异常"+e.getMessage());
        	  e.printStackTrace();
        	  throw e;
          }catch(Exception ex){
        	  ex.printStackTrace();
        	  logger.debug("发送数据出现异常2"+ex.getMessage());
          }
	}
	
	/**
	 * 关闭端口
	 */
	public void portClose(){
		logger.error("开始关闭串口..........");
		readTask.stopTask();
		readTask.interrupt();
		try {
	        byte[] msg = {0X55,0X55,0X55,0X55,0X55,0X08,0X13,0X00,0X02,0X01,0X01,0X00,0X00,0X30,0X30,0X31,
	    			0X30,0X00,0X30,0X02,(byte)0X90,(byte)0XBE,0X00,0X00,(byte)0XB4,0X08};
			serialPortImp.mOutputStream.write(msg);
			serialPortImp.mOutputStream.flush();
		} catch (IOException e) {
			 logger.error("发送数据出现异常"+e.getMessage());
			 e.printStackTrace();
        }
		serialPortImp.portClose();
		serialPortImp = null;
		instance = null;
		logger.error("关闭串口结束..........");
	}
	/**
	 * 读取数据线程
	 * @author Administrator
	 *
	 */
	private class SerialPortThread extends Thread {
		private boolean stopFlag = false;//停止标志
		private synchronized void stopTask(){
			stopFlag = true;
		}
		int size;
		byte[] buffer = new byte[1024];
		@Override
		public void run() {
			while(!stopFlag) {
				if (serialPortImp==null||serialPortImp.mInputStream == null) 
					return;
				ExecutorService executor = Executors.newSingleThreadExecutor();
				FutureTask<byte[]> futureTask = (FutureTask<byte[]>)executor.submit(  
				   new Callable<byte[]>() {//使用Callable接口作为构造参数  
				       public byte[] call() { 
				    	   //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型  
				    	   try{
				    		   logger.error("正在读取数据.........."+":"+Thread.currentThread().getName());
				    		   size = serialPortImp.mInputStream.read(buffer);
				    		   synchronized(returnData){
					    		   logger.error("读取数据为："+size+":"+TypeConversion.byte2hex(buffer,0,size)+":"+Thread.currentThread().getName());
									if (size > 0) {
										logger.error("记录数据为："+TypeConversion.byte2hex(buffer,0,size)+":"+Thread.currentThread().getName());
										returnData = Utils.insertEnoughLengthBuffer(returnData, index, buffer, 0, size, 512);//填充到数组returnData
										index +=size;
										logger.error("记录数据长度为："+index+":"+Thread.currentThread().getName());
									}
									logger.error("缓存数据为："+TypeConversion.byte2hex(returnData,0,index)+":"+Thread.currentThread().getName());
//									if(index>=5){
//										//判断是否包头
//										if(TypeConversion.byte2hex(returnData, 0, 5).equals(TypeConversion.byte2hex(HEADER_PACKET))){
//											hasPacketHeader = true;
//										}else{
//											hasPacketHeader = false;
//											index = 0;
//										}
//									}
				    		   }
				    		   SerialPortThread.this.interrupt();//唤醒父线程
				    	   }catch(IOException e){
				    		   e.printStackTrace();
				    	   }
				    	   return null;
				       }
				});
				
				//读取数据
				try {  
					if(hasPacketHeader){//如果已经收到包头，则设置超时时间，否则不设置
						futureTask.get(TIME_OUT, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间单位为毫秒秒。
					}else{
						futureTask.get();
					}
					if (size <= 0) {
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {  
					e.printStackTrace();
				    futureTask.cancel(true);  
				} catch (ExecutionException e) {  
					e.printStackTrace();
				    futureTask.cancel(true);  
				} catch (TimeoutException e) {
					e.printStackTrace();
					new Thread(readTimerOut).start();//返回参数
					synchronized(this){//如果读取数据超时，则该线程等待直到下次读取到数据
						try { 
							this.wait();
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
					}
				    futureTask.cancel(true);
				} finally {  
				    executor.shutdown();  
				    executor.shutdownNow();
				}
			}
		}
	}

	
	
	
	/**
	 *读取数据连接延时计数器 
	 */
	private class ReadTimerOut extends TimerTask {
		SendDataResultListener mSendDataResultListener = null;
		public ReadTimerOut(SendDataResultListener sendDataResultListener){
			mSendDataResultListener = sendDataResultListener;
		}
		
		@Override
		public void run() {
			clearTimeOutTimer();
			logger.error("开始返回数据:"+Thread.currentThread().getName());
			if(mSendDataResultListener!=null&&mSendDataResultListener.isDone==false){
				synchronized(returnData){
					logger.error("真正返回数据:"+Thread.currentThread().getName());
					//mSendDataResultListener.isDone = true;
					//hasPacketHeader = false;
					byte[] times = String.valueOf(new Date().getTime()).getBytes();
					
					byte[] buffer = new byte[times.length+index];
					
					System.arraycopy(times, 0, buffer, 0, times.length);
					
					System.arraycopy(returnData, 0, buffer, times.length, index);
					mSendDataResultListener.onSendDataResult(buffer);
					index = 0;
				}
			}
	
		}
	}
	
}
