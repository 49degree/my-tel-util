package com.guanri.android.insurance.printer;



import java.io.PrintWriter;
import java.security.AccessController;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.guanri.android.lib.bluetooth.BluetoothDeviceImp;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;


/**
 * 蓝牙打印机管理类     
 * @author 杨雪平   
 * 
 */
public class BluetoothPrinter extends BluetoothDeviceImp{
	
	private Logger logger = Logger.getLogger(BluetoothPrinter.class);
	//private static BluetoothPrinter instance = new BluetoothPrinter();// singleton
	
	private ArrayList<Byte> recvBuffer = new ArrayList<Byte>(); // 接收到的数据缓冲区
	
	String strBTAddress = "00:1F:B7:02:4A:F4";//蓝牙设备地址
	String strBTPsd = "1234";
	String uuidHeader = "00001101-0000-1000-8000-00805F9B34FB";
	
	/**
	 * 获取单列对象
	 * @return
	 */
//	public static BluetoothPrinter getInstance() {
//		return instance;
//	}
	
	/**
	 * 私有构造函数
	 */
	public BluetoothPrinter(String strBTAddress) {
		this.strBTAddress = strBTAddress;
		
	}

	/**
	 * 接收信息
	 */
	@Override
	public void receiveData(Handler handler){
		new Thread(new ReceiveDataRunalbe(handler)).start();//读取数据
	}
	
	/**
	 * 打印方法
	 * @param mainEventHandler
	 * @param value
	 */
	@Override
	public synchronized void sendData(Handler mainEventHandler,byte[] value){
		logger.debug("begin to printInfo+++++++++++++++++++++++++++");
		Thread sendDataRunalbeTask = new Thread(new SendDataRunalbe(mainEventHandler,value));
		sendDataRunalbeTask.start();
	}
	
	/**
	 * 获取蓝牙设备地址
	 * @return
	 */
	@Override
	public String getBTAddr(){
		return strBTAddress;
	}
	/**
	 * 获取蓝牙设备配对密码
	 * @return
	 */
	@Override
	public String getBTPsd(){
		return strBTPsd;
	}
	/**
	 * 获取蓝牙设备配对UUID
	 * @return
	 */
	@Override
	public String getUuidHeader(){
		return uuidHeader;
	}

	/**
	 * 接收数据线程
	 * @author Administrator
	 *
	 */
	public class ReceiveDataRunalbe implements Runnable{
		Handler mainEventHandler = null;
		public ReceiveDataRunalbe(Handler mainEventHandler){
			this.mainEventHandler = mainEventHandler; 
		}
		public void run(){
			logger.error("begin to receive message+++++++++++++++++++++++++++");
			Looper.prepare();
			if (!isConnected()) {
				return ;
			}
			// 读取蓝牙数据，直到连接异常
			logger.error("ready to read");
			while (bthInputStream != null) {
				try {
					byte[] recvbuf = new byte[1024];
					int nReadbyte = bthInputStream.read(recvbuf);
					if (nReadbyte > -1) {
						// 将读到的数据插入全局数据缓冲区
						logger.debug("recv bytes: " + nReadbyte);
						logger.debug("recv string: " + new String(recvbuf, 0, nReadbyte));
						for (int i = 0; i < nReadbyte; i++) {
							recvBuffer.add(recvbuf[i]);
						}
					}else{
						logger.debug("read data end");
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Read data is failed!" + e.getMessage());
					setConnected(false);
					//在这里通知已经断开连接
					break;
				}
			}
		}
	}





	/**
	 * 传输数据线程
	 * @author Administrator
	 *
	 */
	public class SendDataRunalbe implements Runnable{
		Handler mainEventHandler = null;
		byte[] value = null;
		public SendDataRunalbe(Handler mainEventHandler,byte[] value){
			this.mainEventHandler = mainEventHandler;
			this.value = value;
		}
		public void run(){
			Looper.prepare();
			
			if(printWriter!=null){
				try{
					logger.debug("run printInfo start+++++++++++++++++++++++++++");
					printWriter.write(value);
					printWriter.write(System.getProperty("line.separator", "\n").getBytes());
					logger.debug("run printInfo end+++++++++++++++++++++++++++");
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			
		}
	}

}

