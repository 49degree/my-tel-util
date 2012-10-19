package com.blue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.MainApplication;
import com.bluetooth.TypeConversion;
import com.log.Logger;

/**
 * 蓝牙设备连接基类
 * @author 杨雪平
 *
 */
public class BluetoothDeviceImp {
	private static Logger logger = Logger.getLogger(BluetoothDeviceImp.class);
	public final static int BLUE_THOOTH_CONNECT_RESULE = 10000;
	public final static int BLUE_THOOTH_READ_RESULE = 10001;
	public boolean isConnected = false;// 是否连接上蓝牙
	public ArrayList<Byte> recvBuffer = new ArrayList<Byte>(); // 接收到的数据缓冲区
	ConnectBluetoothRunalbe connectBluetoothRunalbe = null;
	public BluetoothSocket bluetoothSocket = null;// 套接字
	public Thread connectBthTask = null; //连接蓝牙线程
	public InputStream bthInputStream = null;//输入流
	public OutputStream bthOutputStream = null;//输入流
	public PrintStream printWriter = null;
	
	private int connectMaxTimes = 30;//连接最大次数
	private int connectedTimes = 0;//已经连接多少次
	
	private Handler mainEventHandler = null;

	
	
	private String strBTAddress = "00:D0:17:A9:29:15";//蓝牙设备地址
	private String strBTPsd = "0000";
	private String uuidHeader = "00001101-0000-1000-8000-00805F9B34FB";
	
	
	
	public BluetoothDeviceImp(){
		
	}
	public BluetoothDeviceImp(String strBTAddress,String strBTPsd){
		this.strBTAddress = strBTAddress;
		this.strBTPsd = strBTPsd;
	}
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
	public synchronized void disConnected() {
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


	}

	/**
	 * 开始蓝牙线程
	 * @param mainEventHandler
	 */
	public  void  beginConnectBth(Handler mainEventHandler){
		if(!isConnected()){
			this.mainEventHandler = mainEventHandler;
			connectBluetoothRunalbe = new ConnectBluetoothRunalbe();
			connectBthTask = new Thread(connectBluetoothRunalbe);
			connectBthTask.setDaemon(true); 
			connectBthTask.start();
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
					//获取已经配对设备
					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					//获取已经配对设备 断开蓝牙设备
					if(!isConnected()){
						// 判断蓝牙是否打开,强行关闭
						bluetoothAdapter.disable();
						connectedTimes = connectMaxTimes+2;
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
	public byte[] receiveData(){
		int readLength = 0;
		byte[] recvbuf = new byte[1024];
		logger.error("Read data begin");
		while (bthInputStream != null) {
			try {
				logger.error("Read data by bthInputStream begin");
				int nReadbyte = bthInputStream.read();
				logger.error("Read data by bthInputStream data:"+nReadbyte);
				if (nReadbyte == 255) {
					break;
				}
				recvbuf[readLength] = (byte)nReadbyte;
				readLength ++;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Read data is failed!" + e.getMessage());

				//在这里通知已经断开连接
				break;
			}
		}
		setConnected(false);
		disConnected();
		if(readLength>0){
			byte[] temp = new byte[readLength];
			System.arraycopy(recvbuf, 0, temp, 0,readLength);
			return temp;
		}else
			return null;
	}
	
	/**
	 * 获取蓝牙设备地址
	 * @return
	 */
	public String getBTAddr(){
		return strBTAddress;
	}
	/**
	 * 获取蓝牙设备配对密码
	 * @return
	 */
	public String getBTPsd(){
		return strBTPsd;
	}
	/**
	 * 获取蓝牙设备配对UUID
	 * @return
	 */
	public String getUuidHeader(){
		return uuidHeader;
	}
	
	/**
	 * 连接蓝牙线程
	 * @author Administrator
	 *
	 */
	public class ConnectBluetoothRunalbe implements Runnable{
		
		BluetoothDevice device = null;
		public ConnectBluetoothRunalbe(){
			connectedTimes = 0; 
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
						logger.error("Bluetooth Connect bthInputStream!");
						//获取输入流
						bthInputStream =  bluetoothSocket.getInputStream();
						logger.error("Bluetooth Connect bthOutputStream!"+connectedTimes);
						Thread t = new ReadDataClass(bluetoothSocket,bthInputStream,connectedTimes);
						t.setDaemon(true);
						t.start();
						//获取输出流
//						bthOutputStream =  bluetoothSocket.getOutputStream();
//						logger.error("Bluetooth Connect bthOutputStream end !");
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
					BLUE_THOOTH_CONNECT_RESULE,new Boolean(isConnected()));
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
					logger.error("Bluetooth Connect begin!");
					bluetoothSocket.connect();
					logger.error("Bluetooth Connect end!"+bluetoothSocket.isConnected());
					connected = true;
				} catch (Exception e) {
					e.printStackTrace();
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
				if(connectedTimes>1&&device.getBondState() != BluetoothDevice.BOND_BONDED){
					BluetoothUtils.removeBond(device.getClass(), device);
				}
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					// 注册Receiver来获取蓝牙设备相关的结果   
			        IntentFilter intent = new IntentFilter();    
			        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			        intent.addAction(ACTION_PAIRING_REQUEST);
			        MainApplication.getInstance().registerReceiver(bondDevices, intent);
					
					BluetoothUtils.createBond(device.getClass(), device);
					logger.error("开始配对++++++++++++++++++++++++");
			        try{
			        	synchronized (this) {
			        		wait();
						}
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
			        MainApplication.getInstance().unregisterReceiver(bondDevices);
				}
				
//				UUID tempUuid = null;
//				tempUuid = UUID.fromString(uuidHeader);
//				logger.error("IDS:"+tempUuid);
//				bluetoothSocket = device.createRfcommSocketToServiceRecord(tempUuid);
				//bluetoothSocket = BluetoothUtils.createRfcommSocket(device.getClass(), device);
				bluetoothSocket = BluetoothUtils.createRfcommSocket1(device.getClass(), device,5);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Bluetooth Paire is Failed!" + e.getMessage());
				return false;// 配对失败就返回false
			}
			return true;
		}	
		
	    public static final String ACTION_PAIRING_REQUEST =
	            "android.bluetooth.device.action.PAIRING_REQUEST";
		//蓝牙设备查询结果广播接收器
		private BroadcastReceiver bondDevices = new BroadcastReceiver() {   
	        public void onReceive(Context context, Intent intent) {   
//	        	Broadcast Action: Indicates a change in the bond state of a remote device. For example, if a device is bonded (paired). 
//	        	Always contains the extra fields EXTRA_DEVICE, EXTRA_BOND_STATE and EXTRA_PREVIOUS_BOND_STATE. 
//	        	Requires android.Manifest.permission.BLUETOOTH to receive
	    	    if (intent.getAction().equals(ACTION_PAIRING_REQUEST)) {
	    	    	if(this.isOrderedBroadcast()){
	    	    		logger.error("有序广播++++++++++++++++++++++++");
	    	    		this.abortBroadcast();
	    	    	}
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                try {
	                	logger.error("输入PIN码++++++++++++++++++++++++");
						BluetoothUtils.setPin(device.getClass(), device, getBTPsd());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //
	            }else if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
	            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            	int bondResult = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
		        	logger.error("配对结果通知++++++++++++++++++++++++");
		        	logger.error(device.getAddress());
		        	logger.error("EXTRA_BOND_STATE:"+bondResult);
		    		
		        	if(bondResult==BluetoothDevice.BOND_BONDED||connectedTimes == connectMaxTimes){
			        	synchronized (ConnectBluetoothRunalbe.this) {
			    			ConnectBluetoothRunalbe.this.notify();
			    		}
		    		}else if(bondResult == BluetoothDevice.BOND_BONDING){
	                	//logger.error("输入PIN码++++++++++++++++++++++++");
						try {
							//BluetoothUtils.setPin(device.getClass(), device, getBTPsd());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}else{
		    			try {
							BluetoothUtils.createBond(device.getClass(), device);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}
		    		
	            }
	        }   
	    };   
		
		
	}
	
	
	private class ReadDataClass extends Thread{
		BluetoothSocket bluetoothSocket  ;
		InputStream  inputStream;
		int port;
		public ReadDataClass( BluetoothSocket bluetoothSocket,InputStream  inputStream,int port){
			this.bluetoothSocket = bluetoothSocket;
			this.inputStream = inputStream;
			this.port = port;
		}
		public void run(){
			int readLength = 0;
			byte[] recvbuf = new byte[1024];
			logger.error("Read data begin");
			while (inputStream != null) {
				try {
					logger.error("Read data port:"+port);
					int nReadbyte = inputStream.read();
					logger.error("Read data is:"+TypeConversion.byte2hex(new byte[]{(byte)nReadbyte}));
					if (nReadbyte == -1) {
						break;
					}
					
					if(mainEventHandler!=null){
						Message msg = mainEventHandler.obtainMessage(BLUE_THOOTH_READ_RESULE,nReadbyte);
						mainEventHandler.sendMessage(msg);
					}
						
					//recvbuf[readLength] = (byte)nReadbyte;
					readLength ++;
					//logger.error("read string is:"+new String(recvbuf,0,readLength));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Read data is failed!" + e.getMessage());
					//在这里通知已经断开连接
					break;
				}
			}
			try{
				inputStream.close();
				bluetoothSocket.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				disConnected();
			}catch(Exception e){
				e.printStackTrace();
			}
			

		}
	}
}
