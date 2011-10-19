package com.xys.ecg.device;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
  
/** 
 * 功能：用于socket的交互 
 *  
 * @author wufenglong 
 *  
 */  
public class ThreadReadWriterIOSocket implements Runnable {  
	public static String TAG = "ThreadReadWriterIOSocket";
	
	public boolean ioThreadFlag = true;
	
    private Socket client;  
    private Activity context;  
    Handler hd = null;
  
    public ThreadReadWriterIOSocket(Activity context, Socket client,Handler hd) {  
  
        this.client = client;  
        this.context = context;  
        this.hd = hd;
    }  
  
    public void run() {  
        Log.d(TAG, Thread.currentThread().getName() + "---->"  
                + "a client has connected to server!");  
        BufferedOutputStream out;  
        BufferedInputStream in;  
        try {  
            /* PC端发来的数据msg */  
            String currCMD = "";  
            out = new BufferedOutputStream(client.getOutputStream());  
            in = new BufferedInputStream(client.getInputStream());  
            // testSocket();// 测试socket方法   
            while (ioThreadFlag) {  
                try {  
                    if (!client.isConnected()) {  
                        Log.v(TAG, Thread.currentThread().getName()  
                                + "---->" + "client is not Connected()");  
                        break;  
                    }  
                    /* 接收PC发来的数据 */  
                    Log.v(TAG, Thread.currentThread().getName()  
                            + "---->" + "will read......");  
                    /* 读操作命令 */ 
                    byte[] returnByte = readByteFromSocket(in);
                    
                    currCMD = readCMDFromSocket(returnByte);  
                    Log.v(TAG, Thread.currentThread().getName()  
                            + "---->" + "**currCMD ==== " + currCMD);
                    out.write("OK".getBytes());  
                    out.flush();
                    if (currCMD.equals("exit")) {
                        out.write("exit".getBytes());  
                        out.flush();
                    	ioThreadFlag = false;
                    	 Log.e(TAG, Thread.currentThread().getName()+ "---->" + "exit"); 
                    }else{
                    	updateText(returnByte);
                    }
                } catch (Exception e) {  
                	e.printStackTrace(); 
                	ioThreadFlag = false;
                    Log.e(TAG, Thread.currentThread().getName()+ "---->" + "read write error111111");  
                }  
            }  
            out.close();  
            in.close();  
        } catch (Exception e) {  
            Log.e(TAG, Thread.currentThread().getName()  + "---->" + "read write error222222");  
            e.printStackTrace();  
        } finally {  
            try {  
                if (client != null) {  
                    Log.v(TAG, Thread.currentThread().getName()+ "---->" + "client.close()");  
                    client.close();  
                }  
            } catch (IOException e) {  
                Log.e(TAG, Thread.currentThread().getName()  + "---->" + "read write error333333");  
                e.printStackTrace();  
            }  
        }  
    }  
 
    
    public void updateText(byte[] returnByte){
    	Message msg = hd.obtainMessage(0, returnByte);
    	hd.sendMessage(msg);
    }
  
    /* 读取命令 */  
    public static String readCMDFromSocket(byte[] returnByte) {  
        String msg = ""; 
        try {  
            msg = new String(returnByte, 0, returnByte.length, "utf-8");   
        } catch (Exception e) {  
            Log.v(TAG, Thread.currentThread().getName()  
                    + "---->" + "readFromSocket error");  
            e.printStackTrace();  
        }  
        // Log.v(Service139.TAG, "msg=" + msg);  
        return msg;  
    }  
    
    /* 读取字节流 */  
    public static byte[] readByteFromSocket(InputStream in) {  
        int MAX_BUFFER_BYTES = 2048;  
        String msg = "";  
        Log.v(TAG, Thread.currentThread().getName()+ "---->" + "readByteFromSocket start......");  
        int numReadedBytes = 0;
        byte[] returnByte = null;
        try {  
        	byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        	
    		numReadedBytes = in.read(tempbuffer, 0, MAX_BUFFER_BYTES);
    		Log.v(TAG, Thread.currentThread().getName()+ "---->" + "readByteFromSocket reading ......"+numReadedBytes); 
    		returnByte = new byte[numReadedBytes];
			System.arraycopy(tempbuffer, 0, returnByte, 0, numReadedBytes);
        		
        } catch (Exception e) {  
            Log.v(TAG, Thread.currentThread().getName()  
                    + "---->" + "readFromSocket error");  
            e.printStackTrace();  
        }  
        Log.v(TAG, Thread.currentThread().getName()+ "---->" + "readByteFromSocket end......");  
        return returnByte;  
    }    
}  
