package com.guanri.android.jpos.pos;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 串口通信控制
 * 为单例模式
 * @author Administrator
 *
 */
public class PosCommandControlSerialPortBak extends PosCommandControlImp{
	static Logger logger = Logger.getLogger(PosCommandControlSerialPortBak.class);
	
	private static PosCommandControlImp instance = null;
	private SerialPortImp serialPortImp;// 串口
	private SerialPortThread readTask = null;
	private Timer timeOutTimer = null;
	private ReturnDataTask returnDataTask = null;
	private ExecutorService returnDataTaskPool = Executors.newSingleThreadExecutor(); 
	private final static int TIME_OUT = 1000;//读取数据间隔超时
	private final static int WAIT_READ_TIME = 1000;//读取数据间隔等待时间
	private static byte[] HEADER_PACKET = {0X55,0X55,0X55,0X55,0X55};
	
	//保存读取的数据
	private byte[] returnData = new byte[2048];//读取数据缓存
	private int index = 0;//缓冲数据量（字节）
	boolean hasPacketHeader = false;//是否有数据头标识
	
	/**
	 * 获得控制对象
	 * @return
	 */
	public static PosCommandControlImp getInstance(){
		if(instance==null){
			instance = new PosCommandControlSerialPortBak();
		}
		return instance;
	}
	/**
	 * 构造函数
	 */
	private PosCommandControlSerialPortBak(){
		logger.debug("串口初始化.........");
		serialPortImp = SerialPortFactory.getSerialPort();//获取串口对象
		logger.debug("串口初始化结束.........");
		readTask = new SerialPortThread();//打开读取线程
		timeOutTimer = new Timer();
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
			synchronized(returnData){
				clearTimeOutTimer();//清除定时器中的操作
				returnDataTask = new ReturnDataTask(sendDataResultListener);
				timeOutTimer.schedule(returnDataTask, waitTime);//设置超时时间及操作对象
			}
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
		//byte[] buffer = new byte[1024];
		int waitTime = 0;
		@Override
		public void run() {
			while(!stopFlag) {
				if (serialPortImp==null||serialPortImp.mInputStream == null) 
					return;
				try{
					int availLen = serialPortImp.mInputStream.available();
					synchronized (returnData) {
						if(availLen>0){
							waitTime = 0;
							size = serialPortImp.mInputStream.read(returnData, index, availLen);
							index += size;
							logger.error("读取数据为：" + size + ":"+ TypeConversion.byte2hex(returnData, index, size) + ":"+ Thread.currentThread().getName());
							logger.error("缓存数据为："+ TypeConversion.byte2hex(returnData, 0, index)+ ":" + Thread.currentThread().getName());
							if (index >= 1&& TypeConversion.asciiToString(returnData,0,1).equals("M")) {
								// 判断是否包头0
								hasPacketHeader = true;
								if(index >=3&&TypeConversion.bytesToShortEx(returnData, 1)==index){//如果数据包已经收完 
									returnData();//返回数据
								}
							}else{
								hasPacketHeader = false;
								index = 0;
							}
						}else{//无数据
							if(waitTime>TIME_OUT){
								if(index >= 3&& TypeConversion.bytesToShortEx(returnData, 1)==index){
									returnData();//返回数据
								}else{//丢弃数据
									index = 0;
									clearTimeOutTimer();//清除定时器中的操作
								}
							}else{//等待一段时间
								sleep(WAIT_READ_TIME);
								waitTime +=WAIT_READ_TIME;
							}
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	}

	/**
	 * 返回数据
	 */
	public void returnData(){
		synchronized(returnData){
			if(returnDataTask!=null){
				returnDataTaskPool.execute(returnDataTask);//返回参数
				returnDataTask = null;
			}else{//调用固定方法，返回POS数据
				
			}
		}
	}
	
	
	
	/**
	 *读取数据连接延时计数器 
	 */
	private class ReturnDataTask extends TimerTask {
		SendDataResultListener mSendDataResultListener = null;
		public ReturnDataTask(SendDataResultListener sendDataResultListener){
			mSendDataResultListener = sendDataResultListener;
		}
		
		@Override
		public void run() {
			clearTimeOutTimer();
			logger.error("开始返回数据:"+Thread.currentThread().getName());
			if(mSendDataResultListener!=null&&mSendDataResultListener.isDone==false){
				synchronized(returnData){
					logger.error("真正返回数据:"+Thread.currentThread().getName());
					mSendDataResultListener.isDone = true;
					hasPacketHeader = false;
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
