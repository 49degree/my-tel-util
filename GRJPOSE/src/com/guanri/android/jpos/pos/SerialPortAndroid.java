package com.guanri.android.jpos.pos;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.guanri.android.lib.log.Logger;


public class SerialPortAndroid extends SerialPortImp{
	private static final String TAG = "AndroidSerialPort";
	static Logger logger = Logger.getLogger(SerialPortAndroid.class);
	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;

	
	public SerialPortAndroid(String device, int baudRate) throws SecurityException, IOException {
		this.device = device;
		this.bountRate = baudRate;
		
		if ( (device.length() == 0) || (baudRate == -1)) {
			throw new InvalidParameterException();
		}

		/* Open the serial port */
		File file = new File(device);
		/* Check access permission */
		if (!file.canRead() || !file.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + file.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !file.canRead()
						|| !file.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(file.getAbsolutePath(), baudRate);
		if (mFd == null) {
			throw new IOException();
		}
		mInputStream = new FileInputStream(mFd);
		mOutputStream = new FileOutputStream(mFd);
	}



	@Override
	public void portClose(){
		try{
			logger.debug("开始关闭流..........");
			mInputStream.close();
			mOutputStream.close();
			close();
			logger.debug("关闭流结束..........");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * JNI本地方法 打开设备
	 * @param path
	 * @param baudrate
	 * @return
	 */
	private native static FileDescriptor open(String path, int baudrate);
	
	/**
	 * 关闭设备
	 */
	public native void close();
	static {
		System.loadLibrary("serial_port");
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
