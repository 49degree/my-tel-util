package com.guanri.android.jpos.pos.data.TerminalLinks;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import com.guanri.android.jpos.pos.SerialPortAndroid;
import com.guanri.android.lib.log.Logger;

public class TAndroidCommTerminalLink extends TTerminalLink {
	static Logger logger = Logger.getLogger(TAndroidCommTerminalLink.class);
	public String CommName; // 串口名称, COM1, COM2, COM3
	public int BandRate = 9600;
	private boolean FConnected = false;

	private OutputStream outputStream; 
	private InputStream inputStream;
	private SerialPortAndroid serialPort;
	
	@SuppressWarnings("rawtypes")
	private Enumeration portList;

	public TAndroidCommTerminalLink() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Connect() throws SecurityException{
		// TODO Auto-generated method stub
		FConnected = false;
		try{
			serialPort =  new SerialPortAndroid(CommName, BandRate);
			outputStream = serialPort.getOutputStream();
			inputStream = serialPort.getInputStream();
			FConnected = true;
			logger.error("TCommTerminalLink comm is starting.............");
			return;
		}catch(SecurityException se){
			se.printStackTrace();
			throw se;
		}catch(IOException io){
			io.printStackTrace(); 
		}
	}

	@Override
	public void Disconnect() {
		// TODO Auto-generated method stub
		if (FConnected)
			serialPort.portClose();
		FConnected = false;
	}

	@Override
	public boolean GetConnected() {
		// TODO Auto-generated method stub
		return FConnected;
	}

	@Override
	public boolean WriteBytes(byte[] ABytes) {
		// TODO Auto-generated method stub
		boolean Result = false;
		if (FConnected) {
			try {
				outputStream.write(ABytes);
				Result = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Result;
	}

	public byte[] readbyte() {
		byte[] bytes = new byte[1];
		try {
			inputStream.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes;
	}
	@Override
	public byte[] ReadBytes(int Count) {
		// TODO Auto-generated method stub
		byte[] ABytes;
		if (Count <= 0)
			Count = 1024;
		ABytes = new byte[Count];

		int timeout = ReadTimeout;
		int tick = 100;
		int readCount = 0, m, n;

		while (timeout > 0) {
			try {
				timeout -= tick;
				try {
					m = inputStream.available();
					if (m > 0) {
						n = Count - readCount;
						if (m > n)
							m = n;
						m = inputStream.read(ABytes, readCount, m);
						if (m < 0) continue;
						readCount += m;
						if (readCount >= Count)
							break;
						timeout = ReadTimeout;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.sleep(tick);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (readCount > 0) {
			if (readCount > Count)
				readCount = Count;
			byte[] Bytes = new byte[readCount];
			System.arraycopy(ABytes, 0, Bytes, 0, readCount);
			return Bytes;
		} else
			return null;
	}

}
