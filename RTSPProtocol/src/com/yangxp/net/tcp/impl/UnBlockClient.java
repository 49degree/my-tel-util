package com.yangxp.net.tcp.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.yangxp.net.exception.NetworkException;

public class UnBlockClient extends ClientBase implements Runnable{
	static Logger logger = Logger.getLogger(UnBlockClient.class);
	SocketChannel mSocketChannel;
	
	Selector selector;
	
	Iterator<SelectionKey> keys;
	
	Thread thread = null;
	
	Queue<ByteBuffer> mSendCmdBeanQueue = new LinkedList<ByteBuffer>();
	
	public UnBlockClient(InetSocketAddress socketAddr){
		super(socketAddr);
	}
	
	public UnBlockClient(SocketChannel socketChannel){
		super(new InetSocketAddress(socketChannel.socket().getInetAddress(),socketChannel.socket().getPort()));
		mSocketChannel = socketChannel;
		status = SocketStatus.CONNECTED;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		thread = Thread.currentThread();
		try {
			selector = Selector.open();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			mSocketHandler.onSocketException(new NetworkException(e1.getMessage()));
			e1.printStackTrace();
			return ;
		}
		
		
		if(mSocketChannel == null){//开启socket连接
			status = SocketStatus.CONNECTING;
			try {
				mSocketChannel = SocketChannel.open(mSocketAddr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
				doClose();
				return ;
			}
		}
		
		try {
			mSocketChannel.configureBlocking(false);
			mSocketChannel.register(selector, SelectionKey.OP_READ);
			status = SocketStatus.CONNECTED;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
			doClose();
			return ;
		}
		
		
		if(status == SocketStatus.CONNECTED){
			try {
				mSocketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int readLen;
		while(status == SocketStatus.CONNECTED){
			try {
				logger.warn("selector.select============");
				if(selector.select(0)>0){
					keys = selector.selectedKeys().iterator();
					while(keys.hasNext()){
						SelectionKey key = keys.next();
						keys.remove();
						
						if(key.isReadable()){
							//read msg
							try {
								logger.debug("start cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
								cmdReadBuffer.limit(cmdReadBuffer.capacity());
								int startPosition = cmdReadBuffer.position();
								if((readLen=mSocketChannel.read(cmdReadBuffer))>0){
									//logger.debug("end cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
									cmdReadBuffer.limit(startPosition+readLen);
									cmdReadBuffer.position(0);
									mSocketHandler.onReceiveCmd(cmdReadBuffer);
									//logger.debug("onReceiveCmd end cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
								}
								logger.debug("end cmdReadBuffer position:"+cmdReadBuffer.position()+":limit:"+cmdReadBuffer.limit());
							}catch(SocketTimeoutException e){
								
								//e.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								mSocketHandler.onSocketException(new NetworkException(e1.getMessage()));
								doClose();
							}
						}else if(key.isWritable()){
							synchronized (this) {
								logger.debug("mSendCmdBeanQueue.size()="+mSendCmdBeanQueue.size());
								if(mSendCmdBeanQueue.size()>0){
									ByteBuffer tmep = mSendCmdBeanQueue.poll();
									logger.debug("send msg="+new String(tmep.array()));
									mSocketChannel.write(tmep);
									if(mSendCmdBeanQueue.size()==0){
										//如果已经没有待发数据，则置成只能读取
										mSocketChannel.register(selector, SelectionKey.OP_READ);
									}
								}else{
									mSocketChannel.register(selector, SelectionKey.OP_READ);
								}
							}
						}
						
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void doOpen() {
		// TODO Auto-generated method stub
		super.doOpen();
		if(mSocketChannel == null){//开启socket连接
			new Thread(this).start();
		}
	}

	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		logger.warn("doClose============");
		try {
			mSocketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(status != SocketStatus.CLOSED)
			mSocketHandler.onSocketClosed();
		status = SocketStatus.CLOSED;
	}

	@Override
	public synchronized void sendBuffer(ByteBuffer buffer) {
		logger.warn("sendBuffer============");
		// TODO Auto-generated method stub
		if(status == SocketStatus.CLOSED){
			throw new IllegalArgumentException("socket is closed");
		}

		try {
			mSocketChannel.register(selector, SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
			doClose();
		}
		mSendCmdBeanQueue.offer(buffer);
		selector.wakeup();
		logger.warn("sendBuffer============end");
	}
}
