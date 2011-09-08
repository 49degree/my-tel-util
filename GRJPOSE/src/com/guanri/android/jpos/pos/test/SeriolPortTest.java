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
		
		test.sendData(msg);
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
                    
                    ReadData readData = new ReadData();
        			Thread readThread = new Thread(readData);
        			readThread.setDaemon(true);
        			readThread.start();
        			

        			break;
                }
            }
        }
	}
	
	public void sendData(byte[] data){
		try {
//            for(int i=0;i<20;i++){
//         	   System.out.println("input :"+i);
//         	   outputStream.write(data); 
//         	   try{
//         		   Thread.sleep(1000);
//         	   } catch (InterruptedException e) {
//           			e.printStackTrace();
//           		}
//            
//            }
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
          	
          }finally{
          	try{
              	outputStream.flush();
              	outputStream.close();
          	}catch(Exception e){
          		e.printStackTrace();
          	}
          	serialPort.close();
          }
	}
	
	int i = 0;
	public class ReadData implements Runnable{
		public void run() {
			try {
				
				try {
					//System.out.println("test:"+inputStream.available());
					byte[] readBuffer = new byte[1024];
					if(inputStream!=null){
						while (!isStop) {
							logger.debug("test:"+i++);
							int numBytes = inputStream.read(readBuffer);
							System.out.println("read data:"+TypeConversion.byte2hex(readBuffer,0,numBytes));
							Thread.sleep(200);
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
