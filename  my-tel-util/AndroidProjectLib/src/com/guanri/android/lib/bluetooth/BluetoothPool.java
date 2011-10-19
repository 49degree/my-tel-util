package com.guanri.android.lib.bluetooth;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Handler;
import android.os.Message;

import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;

/**
 * 
 * @author 杨雪平
 *
 */
public class BluetoothPool {
	private static Logger logger = Logger.getLogger(BluetoothPool.class);
	private static BluetoothPool instance = new BluetoothPool();// singleton
	private static HashMap<String,BluetoothDeviceImp> bluetoothFactoryPool= new HashMap<String,BluetoothDeviceImp>();

	private BluetoothPool(){
	}
	/**
	 * 获取单列对象
	 * @return
	 */
	public static BluetoothPool getInstance() {
		return instance;
	}
	
	/**
	 * 获取蓝牙设备连接对象
	 * @param strBTAddress 蓝牙设备地址 (如00:1F:B7:02:4A:F4)
	 * @param devclass  蓝牙设备实现发送和接收数据的类（如com.guanri.android.lib.bluetooth.printer.BluetoothPrinter）
	 * @return
	 */
	public BluetoothDeviceImp getBluetoothFactory(String strBTAddress,Class devclass) {
		if(bluetoothFactoryPool.containsKey(strBTAddress)){
			return bluetoothFactoryPool.get(strBTAddress);
		}else{
			BluetoothDeviceImp temp = null;
			try{
				//创建蓝牙设备对象
				Constructor devConstructor = devclass.getConstructor(new Class[]{String.class});
				temp = (BluetoothDeviceImp)devConstructor.newInstance(new Object[]{strBTAddress});
				bluetoothFactoryPool.put(strBTAddress, temp);
			}catch(Exception e){
				e.printStackTrace();
			}
			return temp;
		}
	}
	
	public void releasBluetooth(Handler mainEventHandler){
		try{
			Iterator<String> key = bluetoothFactoryPool.keySet().iterator();
			while(key.hasNext()){
				bluetoothFactoryPool.get(key.next()).disConnected(mainEventHandler);
			}
			if(mainEventHandler!=null){
				Message msg = mainEventHandler.obtainMessage(
						HandlerWhat.BLUE_THOOTH_CONNECT_RESULE,new Boolean(false));
				mainEventHandler.sendMessage(msg);
			}
		}catch(Exception e){
			
		}

	}
	
	public void releasBluetooth(){
		this.releasBluetooth(null);

	}	
}
