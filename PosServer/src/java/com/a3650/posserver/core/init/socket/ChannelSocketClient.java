/***********************************************************************
 * Module:  SocketClient.java
 * Author:  Administrator
 * Purpose: Defines the Class SocketClient
 ***********************************************************************/

package com.a3650.posserver.core.init.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;


public class ChannelSocketClient implements Client {
	static Logger logger = Logger.getLogger(ChannelSocketClient.class);
	
	private final int READ_TIMEOUT = 60*1000;
	private int waitTime = 0;

	private byte[] recvAllBuffer = new byte[2048];//接收到的数据缓冲区
	private int recvAllBufferIndex = 0;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	
	private boolean stopReceive = false;
	private SocketChannel channel = null;
	private SelectionKey key = null;
	
	public synchronized void setSelectionKey(SelectionKey key){
		this.key = key;
		channel = (SocketChannel)key.channel();
		
		
	}
	
	/**
	 * 接收数据
	 */
	@Override
	public synchronized byte[] receiveData() throws IOException{
		try {
			//logger.info("开始接收数据！！！！！！！！！！！");
			// TODO Auto-generated method stub
			while (channel != null && !stopReceive) {
				buffer.clear();
				int nReadbyteLength = channel.read(buffer);// 读取数据
				//logger.info("收到数据长度！！！！！！！！！！！"+nReadbyteLength);
				if(nReadbyteLength<0){//客户端连接已经关闭
					throw new IOException("Client is close");
				}else if(nReadbyteLength == 0){//等待
					if(waitTime>READ_TIMEOUT)
						stopReceive = true;
					else{
						waitTime +=100;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {e.printStackTrace();}
					}	
				}else if (nReadbyteLength > 0) {
					// 将读到的数据插入全局数据缓冲区
					// 填充数据到缓存
					recvAllBuffer = Utils.insertEnoughLengthBuffer(
							recvAllBuffer, recvAllBufferIndex, buffer.array(), 0,
							nReadbyteLength, 512);
					recvAllBufferIndex += nReadbyteLength;
					logger.info("收到数据："+TypeConversion.byte2hex(recvAllBuffer,0,recvAllBufferIndex));
					// 包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
					if (TypeConversion.bytesToShortEx(recvAllBuffer, 0) <= recvAllBufferIndex - 2) {
						stopReceive = true;
					}
				}
				
			}
			// 解析接收到的数据
			if (TypeConversion.bytesToShortEx(recvAllBuffer, 0) <= recvAllBufferIndex - 2) {
				byte[] returnData = new byte[recvAllBufferIndex];
				System.arraycopy(recvAllBuffer, 0, returnData, 0,recvAllBufferIndex);
				return returnData;
			}
		} catch (IOException e) {
			closeChannel();
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
	public boolean returnData(byte[] mBuffer) {
		// TODO Auto-generated method stub
		try {
			if(channel!=null){
				buffer.clear();
				if(buffer.capacity()<mBuffer.length)
					buffer = ByteBuffer.allocate(mBuffer.length);
				buffer.put(mBuffer,0,mBuffer.length);
				buffer.position(0);
				buffer.limit(mBuffer.length);
				channel.write(buffer);
			}
		} catch (IOException e) {
			closeChannel();
		}
		return false;
	}
	
	/**
	 *  关闭服务器连接
	 *  connectTimeOut;//连接超时时间
	 *  readTimeOut;//数据发送超时时间
	 */
	public  boolean close(){
		return closeChannel();
	}	
	
	/**
	 *  关闭服务器连接
	 *  connectTimeOut;//连接超时时间
	 *  readTimeOut;//数据发送超时时间
	 */
	private  boolean closeChannel(){
		try {
			channel.close();
			channel = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			key.cancel();
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