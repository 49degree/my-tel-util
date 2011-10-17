package com.guanri.android.jpos.pos.data.TerminalLinks;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class TCommTerminalLink extends TTerminalLink {

	public String CommName; // 串口名称, COM1, COM2, COM3
	public int BandRate = 9600;
	private boolean FConnected = false;

	private OutputStream outputStream;
	private InputStream inputStream;
	private SerialPort serialPort;
	private CommPortIdentifier portId;
	
	@SuppressWarnings("rawtypes")
	private Enumeration portList;

	public TCommTerminalLink() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Connect() {
		// TODO Auto-generated method stub
		FConnected = false;
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if ((portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
					& portId.getName().equals(CommName)) {
				try {
					serialPort = (SerialPort) portId.open("SimpleWriteApp",
							2000);
				} catch (PortInUseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				try {
					outputStream = serialPort.getOutputStream();
					inputStream = serialPort.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					serialPort.close();
					return;
				}
				try {
					serialPort.setSerialPortParams(BandRate,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					serialPort.close();
					return;
				}
				FConnected = true;
				return;
			}
			
		}

	}

	@Override
	public void Disconnect() {
		// TODO Auto-generated method stub
		if (FConnected)
			serialPort.close();
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
						//System.out.println("Read:"+ m);
						inputStream.read(ABytes, readCount, m);
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
