package com.guanri.android.jpos.pos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}
