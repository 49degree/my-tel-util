package com.guanri.android.lib.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;

/**
 * 蓝牙设备连接基类
 * @author 杨雪平
 *
 */
public abstract class BluetoothDeviceImp {
	private static Logger logger = Logger.getLogger(BluetoothDeviceImp.class);
	
	public boolean isConnected = false;// 是否连接上蓝牙
	public ArrayList<Byte> recvBuffer = new ArrayList<Byte>(); // 接收到的数据缓冲区
	ConnectBluetoothRunalbe connectBluetoothRunalbe = null;
	public BluetoothSocket bluetoothSocket = null;// 套接字
	public Thread connectBthTask = null; //连接蓝牙线程
	public InputStream bthInputStream = null;//输入流
	public OutputStream bthOutputStream = null;//输入流
	public PrintStream printWriter = null;
	
	private int connectMaxTimes = 10;//连接最大次数
	private int connectedTimes = 0;//已经连接多少次
	private boolean hasCloseBthDevice = false;//记录是否已经重启蓝牙适配器
	private Timer mTimer = null;
	private final static int TIME_OUT = 15000;//如果15秒钟后还是连接不上，则重启蓝牙适配器
	
	/**
	 * 检测是否连接
	 * @return 
	 */
	public synchronized boolean isConnected() {
		return isConnected;
	} 
	
	/**
	 * 检测是否连接
	 * @return
	 */
	public synchronized void setConnected(boolean connected) {
		this.isConnected = connected; 
	}

	/**
	 * 断开蓝牙连接
	 * @return
	 */
	public synchronized void disConnected(Handler mainEventHandler) {
		if(bluetoothSocket!=null){
			connectedTimes = connectMaxTimes;
			try{
				if(bthInputStream!=null){
					bthInputStream.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(printWriter!=null){
					printWriter.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(bthOutputStream!=null){
					bthOutputStream.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(bluetoothSocket!=null){
					bluetoothSocket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			bthInputStream = null;
			printWriter = null;
			bthOutputStream = null;
			bluetoothSocket = null;
			setConnected(false);
		}
		if(mainEventHandler!=null){
			Message msg = mainEventHandler.obtainMessage(
					HandlerWhat.BLUE_THOOTH_CONNECT_RESULE,new Boolean(true));
			mainEventHandler.sendMessage(msg);
		}

	}

	/**
	 * 开始蓝牙线程
	 * @param mainEventHandler
	 */
	public  void  beginConnectBth(Handler mainEventHandler){
		if(!isConnected()){
			connectBluetoothRunalbe = new ConnectBluetoothRunalbe(mainEventHandler);
			connectBthTask = new Thread(connectBluetoothRunalbe);
			connectBthTask.setDaemon(true);
			connectBthTask.start();
		}else{
			Message msg = mainEventHandler.obtainMessage(
					HandlerWhat.BLUE_THOOTH_CONNECT_RESULE,new Boolean(isConnected()));
			mainEventHandler.sendMessage(msg);
		}

	}
	
	
	/**
	 * 取消蓝牙绑定进程
	 * @param mainEventHandler
	 */
	public  void  cancelBondProcess(){
		logger.debug("cancelBondProcess++++++++++++++++++++++");
		new Thread(){
			public void run(){
				try {
					if(mTimer!=null){
						mTimer.purge();
					}
					//获取已经配对设备
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					//获取已经配对设备 断开蓝牙设备
					if(!isConnected()){
						// 判断蓝牙是否打开,强行关闭
						bluetoothAdapter.disable();
						connectedTimes = connectMaxTimes+2;
//						Thread.sleep(5000);
//						bluetoothAdapter.enable();
//						logger.error("cancelBondProcess____________________________");
					} 
				} catch (Exception e) {
					e.printStackTrace();  
				}
			}
		}.start();
	}	
	
	
	/**
	 * 开始蓝牙线程
	 * @param mainEventHandler
	 */
	public  void  beginConnectBth(Handler mainEventHandler,int connectMaxTimes){
		this.connectMaxTimes = connectMaxTimes;
		this.beginConnectBth(mainEventHandler);
	}
	
	/**
	 * 接收信息
	 */
	public abstract void receiveData(Handler handler);
	
	/**
	 * 发送指令
	 */
	public abstract void sendData(Handler handler,byte[] value);
	/**
	 * 获取蓝牙设备地址
	 * @return
	 */
	public abstract String getBTAddr();
	/**
	 * 获取蓝牙设备配对密码
	 * @return
	 */
	public abstract String getBTPsd();
	/**
	 * 获取蓝牙设备配对UUID
	 * @return
	 */
	public abstract String getUuidHeader();
	
	/**
	 * 连接蓝牙线程
	 * @author Administrator
	 *
	 */
	public class ConnectBluetoothRunalbe implements Runnable{
		Handler mainEventHandler = null;
		BluetoothDevice device = null;
		public ConnectBluetoothRunalbe(Handler mainEventHandler){
			this.mainEventHandler = mainEventHandler; 
			connectedTimes = 0; 
			hasCloseBthDevice = false; 
		}
		
		public void run(){
			Looper.prepare();
			//mTimer = new Timer();
			while(!isConnected()&&connectedTimes++<connectMaxTimes){
				//mTimer.schedule(new TimeOutCheckTimer(), TIME_OUT);
				// 连接目标蓝牙设备
				if(connectBth()){//连接成功
					//mTimer.purge();
					try{
						//获取输入流
						bthInputStream =  bluetoothSocket.getInputStream();
						receiveData(mainEventHandler);//启动接收消息线程
						//获取输出流
						bthOutputStream =  bluetoothSocket.getOutputStream();
						printWriter = new PrintStream(bthOutputStream, true);
					}catch(IOException e){
						e.printStackTrace();
						return ;
					}
				}else{
					// 如果没有连接，则连接一次，并休息1s
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
			
			Message msg = mainEventHandler.obtainMessage(
					HandlerWhat.BLUE_THOOTH_CONNECT_RESULE,new Boolean(isConnected()));
			mainEventHandler.sendMessage(msg);
			
		}
		
		/**
		 * 连接蓝牙设备
		 * @param strAddr
		 * @param strPsw
		 * @return
		 */
		private boolean connectBth(){
			boolean connected = false;
			// 密码配对
			if (pairBluetooth(getBTAddr(), getBTPsd(),getUuidHeader())) {
				// 调用android的socket连接目标设备
				try {
					logger.debug("Bluetooth Connect begin!");
					bluetoothSocket.connect();
					connected = true;
				} catch (Exception e) {
					logger.error("++++++++++++++++++++++++++++Bluetooth Connect failed!" + e.getMessage());
				}
			}else{
				logger.debug("pair failed");
			}
			setConnected(connected);
			return connected;
		}
		
		/**
		 * 蓝牙配对
		 * @param strAddr
		 * @param strPsw
		 * @param uuidHeader
		 * @return
		 */
		private boolean pairBluetooth(String strAddr, String strPsw,String uuidHeader) {
			// 查询有没有配对，没配对的话进行配对
			try {
				UUID uuid = UUID.fromString(uuidHeader);
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				// 取消可能存在的系统搜索
				bluetoothAdapter.cancelDiscovery();
				//如果是第一次连接，则先关闭蓝牙
				// 判断蓝牙是否打开,如果没打开，不做提示，强行打开
				if (!bluetoothAdapter.isEnabled()) {
					bluetoothAdapter.enable();
					Thread.sleep(5000);
				}
				if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) {
					logger.debug("address invalid");
					return false;
				}
				device = bluetoothAdapter.getRemoteDevice(strAddr);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					BluetoothUtils.setPin(device.getClass(), device, strPsw); //
					BluetoothUtils.createBond(device.getClass(), device);
				}
				bluetoothSocket = BluetoothUtils.createRfcommSocket(device.getClass(), device);
			} catch (Exception e) {
				logger.error("Bluetooth Paire is Failed!" + e.getMessage());
				return false;// 配对失败就返回false
			}
			return true;
		}		
	}
	
	/**
	 *蓝牙连接延时计数器 
	 */
	private class TimeOutCheckTimer extends TimerTask {
		@Override
		public void run() {
			try {
				//获取已经配对设
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				//获取已经配对设备，如果2次连接失败，则断开蓝牙设备
				if(!isConnected()&&bluetoothAdapter.isEnabled()&&!hasCloseBthDevice){
					// 判断蓝牙是否打开,强行关闭
					bluetoothAdapter.disable();
					hasCloseBthDevice = true;
					connectMaxTimes++;
					if(connectBthTask!=null){
						synchronized(connectBthTask){
							connectBthTask.wait();
							Thread.sleep(5000);
							connectBthTask.notifyAll();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Bluetooth Paire is Failed!" + e.getMessage());
			}

		}
	}
}
