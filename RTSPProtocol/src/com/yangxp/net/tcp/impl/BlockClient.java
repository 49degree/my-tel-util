package com.yangxp.net.tcp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.yangxp.net.exception.NetworkException;

public class BlockClient extends ClientBase{
	static Logger logger = Logger.getLogger(BlockClient.class);
	private Socket mSocket = null;
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	
	//WriteThread mWriteThread = null;
	ReadThread mReadThread = null;
	
	
	private ExecutorService sendService = Executors.newSingleThreadExecutor();
	
	public BlockClient(InetSocketAddress socketAddr){
		super(socketAddr);
	}
	@Override
	public void doOpen(){
		super.doOpen();
		status = SocketStatus.CONNECTING;
		mSocket = new Socket();
		try {
			mSocket.setSoTimeout(10*1000);//10s
			mSocket.setTcpNoDelay(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
		}
		
		mReadThread = new ReadThread();
		mReadThread.setDaemon(true);
		mReadThread.start();
	}
	
	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		try{
			if(mSocket!=null)
				mSocket.close();
			if(mOutputStream!=null)
				mOutputStream.close();
			if(mInputStream!=null){
				mInputStream.close();
			}
			if(status != SocketStatus.CLOSED)
				mSocketHandler.onSocketClosed();
			status = SocketStatus.CLOSED;
			sendService.shutdownNow();
		}catch(Exception ex){
			
		}
		mSocket = null;
		mInputStream = null;
		mOutputStream = null;
	}

	public void sendBuffer(final ByteBuffer buffer){
		if(buffer==null)
			throw new IllegalArgumentException("be send buffer is null");
		if(mOutputStream==null)
			throw new IllegalArgumentException("socket has't initlizations");
		sendService.execute(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(status == SocketStatus.CONNECTED){
					try {
						mOutputStream.write(buffer.array(),buffer.position(),buffer.limit());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
						doClose();
					}
				}else{
					doClose();
				}
			}
			
		});
	}
	
	private class ReadThread extends Thread{
		public void run(){
			try {//开启socket连接
				mSocket.connect(mSocketAddr, 10*1000);//10s
				status = SocketStatus.CONNECTED;
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
				return;
			}
			int readLen = 0;
			while(status == SocketStatus.CONNECTED){
				try {
					cmdReadBuffer.limit(cmdReadBuffer.capacity());
					logger.debug("start cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
					if((readLen=mInputStream.read(cmdReadBuffer.array(),
							cmdReadBuffer.position(),cmdReadBuffer.capacity()-cmdReadBuffer.position()))>0){
						logger.debug("end cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
						cmdReadBuffer.limit(cmdReadBuffer.position()+readLen);
						mSocketHandler.onReceiveCmd(cmdReadBuffer);
						logger.debug("onReceiveCmd end cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
					}

						
				}catch(SocketTimeoutException e){
					
					//e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					mSocketHandler.onSocketException(new NetworkException(e1.getMessage()));
					doClose();
				}
			}
		}
	}

}
