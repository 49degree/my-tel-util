package com.guanri.android.jpos.pos;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 抽象串口对象
 * @author Administrator
 *
 */
public abstract class SerialPortImp {
	protected String device;//对于PC为com1,2，3，对于android 为如"/dev/ttyUSB0"的路径
	protected int bountRate=9600;//波特率默认为9600
	protected InputStream mInputStream;//输入流
	protected OutputStream mOutputStream;//输出流
	
	// Getters and setters
	public InputStream getInputStream() {
		return mInputStream;
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}
	
	public abstract void portClose();
}
