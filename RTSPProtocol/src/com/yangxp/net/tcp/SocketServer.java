package com.yangxp.net.tcp;

import com.yangxp.net.tcp.impl.SocketServerImpl;

public interface SocketServer {
	public static class Factory{
		public static SocketServer getDefault(int port){
			return new SocketServerImpl(port);
		}
	}
	
	public void doOpen();
	public void doClose();
	public void setClientConnectedListener(ClientConnectedListener clientConnectedListener);

}

