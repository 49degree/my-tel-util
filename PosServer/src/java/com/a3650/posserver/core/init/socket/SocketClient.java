/***********************************************************************
 * Module:  SocketClient.java
 * Author:  Administrator
 * Purpose: Defines the Class SocketClient
 ***********************************************************************/

package com.a3650.posserver.core.init.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;


public class SocketClient implements Client {
	static Logger logger = Logger.getLogger(SocketClient.class);
	
	private final int READ_TIMEOUT = 60*1000;
	
	private InputStream in = null;
	private OutputStream out = null;
	private byte[] recvAllBuffer = new byte[1024];//接收到的数据缓冲区
	private byte[] recvbuf = new byte[512];// 1KB的缓冲区
	private int recvAllBufferIndex = 0;
	
	private boolean stopReceive = false;
	private Socket mClient = null;

	public SocketClient(Socket client) throws IOException{
		mClient = client;
		try{
			mClient.setSoTimeout(SocketInitContext.getContext().getServerTimeOut());
			mClient.setTcpNoDelay(true);
			in = mClient.getInputStream();
			out = mClient.getOutputStream();
		}catch(IOException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 接收数据
	 */
	@Override
	public byte[] receiveData() throws IOException{
		try {
			logger.debug("开始接收数据！！！！！！！！！！！");
			// TODO Auto-generated method stub
			while (in != null && !stopReceive) {
				int nReadbyteLength = in.read(recvbuf);// 读取数据
				if (nReadbyteLength > 0) {
					// 将读到的数据插入全局数据缓冲区
					// 填充数据到缓存
					recvAllBuffer = Utils.insertEnoughLengthBuffer(
							recvAllBuffer, recvAllBufferIndex, recvbuf, 0,
							nReadbyteLength, 512);
					recvAllBufferIndex += nReadbyteLength;
					logger.debug("收到数据："+TypeConversion.byte2hex(recvAllBuffer,0,recvAllBufferIndex));
					// 包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
					if (TypeConversion.bytesToShortEx(recvAllBuffer, 0) <= recvAllBufferIndex - 2) {
						stopReceive = true;
					}
				} else {
					stopReceive = true;
				}
			}
			// 解析接收到的数据
			if (recvAllBufferIndex > 0) {
				byte[] returnData = new byte[recvAllBufferIndex];
				System.arraycopy(recvAllBuffer, 0, returnData, 0,
						recvAllBufferIndex);
				
				return returnData;
			}
		} catch (IOException e) {
			throw e;
		}finally{
			recvAllBufferIndex = 0;
		}
		return null;
	}

	
	/**
	 * 返回数据
	 */
	@Override
	public boolean returnData(byte[] buffer)  throws IOException{
		// TODO Auto-generated method stub
		try {
			out.write(buffer);
			out.flush();
			return true;
		} catch (IOException e) {
			throw e;
		}finally{
		}
	}
	
	
	/**
	 *  关闭服务器连接
	 *  connectTimeOut;//连接超时时间
	 *  readTimeOut;//数据发送超时时间
	 */
	@Override
	public synchronized boolean close(){
		// 关闭连接
		try {
			in.close();
			in = null;
			out.close();
			out = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			mClient.close();
			mClient = null;
		} catch (Exception ex) {
		}
		if(closeListener!=null){
			closeListener.close();
		}
		return true;
	}	
	
	
	public CloseListener closeListener = null;
	
	public CloseListener getCloseListener() {
		return closeListener;
	}

	public void setCloseListener(CloseListener closeListener) {
		this.closeListener = closeListener;
	}


	

}