package com.guanri.android.jpos.pos.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

public class SeriolPortTest {
	static Logger logger = Logger.getLogger(SeriolPortTest.class);
    OutputStream outputStream;
    InputStream inputStream;
    Enumeration portList;
    CommPortIdentifier portId;
    SerialPort serialPort;
    boolean isStop =false;
	
	public static void main(String[] args) {
		 byte[] msg =
		 {0X55,0X55,0X55,0X55,0X55,0X08,0X13,0X00,0X02,0X01,0X01,0X00,0X00,0X30,0X30,0X31,
		 0X30,0X00,0X30,0X02,(byte)0X90,(byte)0XBE,0X00,0X00,(byte)0XB4,0X08};
		
		SeriolPortTest test = new SeriolPortTest();
		
		test.openComm();//打开端口
		
		test.sendData(msg);//发送数据
	}
	
	
	public void parseData(byte[] data){
		
	}
	
	public void openComm(){
        portList = CommPortIdentifier.getPortIdentifiers();
        logger.debug(portList.hasMoreElements()+"");
        while (portList.hasMoreElements()) {
        	
            portId = (CommPortIdentifier) portList.nextElement();
            logger.debug(portId.getName());
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM3")) {
                    try {
                        serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
                    } catch (PortInUseException e) {
                    	e.printStackTrace();
                    }
                    try {
                        outputStream = serialPort.getOutputStream();
                	    inputStream = serialPort.getInputStream();
                    } catch (IOException e) {
                    	e.printStackTrace();
                    	serialPort.close();
                    }
                    
                    try {
                        serialPort.setSerialPortParams(9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    } catch (UnsupportedCommOperationException e) {
                    	e.printStackTrace();
                    }
                    isConnect = true;
                    ReadData readData = new ReadData();
        			Thread readThread = new Thread(readData);
        			
        			readThread.start();
        			

        			break;
                }
            }
        }
	}
	
	public void sendData(byte[] data){
		try {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			outputStream.write(data);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
          	e.printStackTrace();
          	
          }
	}
	
	private byte[] recvAllBuffer = new byte[2048];//接收到的数据缓冲区
	private byte[] recvbuf = new byte[1024];
	private int recvAllBufferIndex = 0;
	private boolean isConnect = false;//是否连接
	public class ReadData implements Runnable{
		int numBytes = 0;
		public void run() {
			try {
				
				try {
					//System.out.println("test:"+inputStream.available());
					if(inputStream!=null){
						while (isConnect&&!isStop) {
							numBytes = inputStream.read(recvbuf);
							if(numBytes>0){
								logger.debug("read data:"+TypeConversion.byte2hex(recvbuf,0,numBytes));
								//填充数据到缓存
								recvAllBuffer = Utils.insertEnoughLengthBuffer(recvAllBuffer, recvAllBufferIndex, recvbuf, 0, numBytes, 512);
								recvAllBufferIndex +=numBytes;
								//包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
								if(TypeConversion.bytesToShortEx(recvAllBuffer, 0)<=recvAllBufferIndex-2){
									byte[] data = new byte[recvAllBufferIndex];
									System.arraycopy(recvAllBuffer, 0, data, 0, recvAllBufferIndex);
									parseData(data);//处理数据
								}
							}else{
								Thread.sleep(500);
							}
							
						}
					}
					logger.debug("end");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
	        	serialPort.close();
	        }
		}
	}
}
