package com.yangxp.net.tcp.impl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.SocketHandler;

public abstract class ClientBase implements SocketClient{
	InetSocketAddress mSocketAddr = null;
	SocketHandler mSocketHandler = null;
	
	ByteBuffer cmdReadBuffer = ByteBuffer.allocate(71);
	
	SocketStatus status = SocketStatus.IDLE;
	public enum SocketStatus{
		IDLE,CONNECTING,CONNECTED,CLOSED
	}
	
	public ClientBase(InetSocketAddress socketAddr){
		if(socketAddr==null)
			throw new IllegalArgumentException("socketAddr is null");
		mSocketAddr = socketAddr;
	}
	
	@Override
	public void setSocketHandler(SocketHandler socketHandler) {
		// TODO Auto-generated method stub
		mSocketHandler = socketHandler;
	}
	
	@Override
	public SocketHandler getSocketHandler() {
		// TODO Auto-generated method stub
		return mSocketHandler;
	}
	
	@Override
	public void doOpen(){
		if(mSocketHandler==null)
			throw new IllegalArgumentException("SocketHandler is null");
		if(status==SocketStatus.CONNECTING||status==SocketStatus.CONNECTED)
			throw new IllegalArgumentException("socket has "+status.toString());
	}
}
