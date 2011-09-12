package com.guanri.android.jpos.pos;

import java.io.File;
import java.io.IOException;

/**
 * 抽象串口对象
 * @author Administrator
 *
 */
public class SerialPortFactory {

	/**
	 * 构造串口对象
	 * @return
	 */
	public static SerialPortImp getSerialPort() throws SecurityException{
		SerialPortAndroid serialPort = null;
		//serialPort = new PCSerialPort("COM1",9600);
		try{
			serialPort =  new SerialPortAndroid("/dev/ttyUSB0",9600);
		}catch(SecurityException se){
			se.printStackTrace();
			throw se;
		}catch(IOException io){
			io.printStackTrace();
		}
		return serialPort;
	}
	
	/**
	 * 查询android中是否存在某个端口
	 * @param devicePath
	 * @return
	 */
    public static boolean findAndroidDevice(String devicePath){
    	boolean result = false;
		File dev = new File("/dev");
		File[] files = dev.listFiles();
		int i;
		for (i=0; i<files.length; i++) {
			if (files[i].getAbsolutePath().equals(devicePath)) {
				result = true;
				break;
			}
		}
		return result;
    }
    
    
}
