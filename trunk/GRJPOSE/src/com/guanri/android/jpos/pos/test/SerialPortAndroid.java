package com.guanri.android.jpos.pos.test;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.guanri.android.lib.log.Logger;


public class SerialPortAndroid{
	private static final String TAG = "AndroidSerialPort";
	static Logger logger = Logger.getLogger(SerialPortAndroid.class);
	
	protected String device;//对于PC为com1,2，3，对于android 为如"/dev/ttyUSB0"的路径
	protected int bountRate=9600;//波特率默认为9600
	protected InputStream mInputStream;//输入流
	protected OutputStream mOutputStream;//输出流
	

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	
	
	
	/**
	 * 发送数据到串口设备，同一时间只能有一个线程调用
	 * 
	 */
	public synchronized byte[] sendData(byte[] data) throws IOException{
		try {
			mOutputStream.write(data);
			mOutputStream.flush();
			byte[] buffer = new byte[1024];
			int length = mInputStream.read(buffer);
			return buffer;
          } catch (IOException e) {
        	  logger.debug("发送数据出现异常"+e.getMessage());
        	  e.printStackTrace();
        	  throw e;
          }catch(Exception ex){
        	  ex.printStackTrace();
        	  logger.debug("发送数据出现异常2"+ex.getMessage());
          }
          return null;
	}
	


	
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

	// Getters and setters
	public InputStream getInputStream() {
		return mInputStream;
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}
	

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
}
