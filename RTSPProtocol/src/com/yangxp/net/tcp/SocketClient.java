package com.yangxp.net.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.yangxp.net.tcp.impl.UnBlockClient;

public interface SocketClient {
	public static class Factory{
		public static SocketClient getDefault(InetSocketAddress socketAddr){
			return new UnBlockClient(socketAddr);
		}
	}
	
	public void doOpen();
	public void doClose();
	public void setSocketHandler(SocketHandler socketHandler);
	public SocketHandler getSocketHandler();
	public void sendBuffer(ByteBuffer buffer);
}
