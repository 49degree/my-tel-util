package com.yangxp.net.tcp.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.yangxp.net.exception.CommandParseException;
import com.yangxp.net.exception.NetworkException;
import com.yangxp.net.tcp.ClientConnectedListener;
import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.SocketHandler;
import com.yangxp.net.tcp.SocketServer;

public class SocketServerImpl extends Thread implements SocketServer{
	static Logger logger = Logger.getLogger(SocketServerImpl.class);
	int mBindPort;
	
	ServerStatus status = ServerStatus.CLOSED;
	
	ExecutorService clientPools = Executors.newCachedThreadPool();
	
	
	SocketHandler mSocketHandler = null;
	
	ServerSocketChannel mServer = null;
	// 信道选择器
	private Selector selector;
	private Iterator<SelectionKey> keys;
	
	HashMap<Integer,SocketClient> clientMap = new HashMap<Integer,SocketClient>();
	
	ClientConnectedListener mClientConnectedListener;
	
	public enum ServerStatus{
		OPENED,OPENING,CLOSED
	}
	
	public SocketServerImpl(int bindPort){
		mBindPort = bindPort;
	}
	@Override
	public void doOpen() {
		// TODO Auto-generated method stub
		if(status != ServerStatus.CLOSED)
			throw new IllegalArgumentException("server is "+status.toString());
		logger.info("server is opening.....");
		try {
			status = ServerStatus.OPENING;
			mServer = ServerSocketChannel.open();
			mServer.socket().bind(new InetSocketAddress(mBindPort)); // 绑定端口
			mServer.configureBlocking(false); // 设置为非阻塞模式
			selector = Selector.open();
			mServer.register(selector, SelectionKey.OP_ACCEPT); // 注册关心的事件，对于Server来说主要是accpet了
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		try {
			logger.info("server is opened on host:port:"+InetAddress.getLocalHost().getHostAddress()+":"+mBindPort+".....");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start();
	}

	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		try{
			clientPools.shutdownNow();
			
			Iterator<Entry<Integer,SocketClient>> its = clientMap.entrySet().iterator();
			while(its.hasNext()){
				SocketClient client = its.next().getValue();
				its.remove();
				client.doClose();
			}
			
			if(mServer!=null)
				mServer.close();
			if(status != ServerStatus.CLOSED)
				mSocketHandler.onSocketClosed();
		}catch(Exception ex){
			
		}
		status = ServerStatus.CLOSED;
		mServer = null;
	}
	
	@Override
	public void run(){
		status = ServerStatus.OPENED;
		while(status==ServerStatus.OPENED){
			try {
				if(selector.select()>0){
					keys = selector.selectedKeys().iterator();
					while(keys.hasNext()){
						SelectionKey key = keys.next();
						keys.remove();
						if(key.isAcceptable()){
							ServerSocketChannel ssc2 = (ServerSocketChannel) key.channel();
							SocketChannel channel = ssc2.accept();
							
							UnBlockClient unBlockClient = new UnBlockClient(channel);
							
							if(mClientConnectedListener!=null)
								mClientConnectedListener.onClientConnected(unBlockClient);
							
							Integer hashKey = unBlockClient.hashCode();
							if(unBlockClient.getSocketHandler()==null){
								unBlockClient.doClose();
								continue;
							}
							
							unBlockClient.setSocketHandler(new SocketHandlerProxy(hashKey,unBlockClient.getSocketHandler()));
							
							clientMap.put(hashKey, unBlockClient);
							clientPools.execute(unBlockClient);
						}

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	private class SocketHandlerProxy implements SocketHandler{
		SocketHandler mSocketHandler = null;
		Integer mHashKey = null;
		private SocketHandlerProxy(Integer hashKey,SocketHandler socketHandler){
			mHashKey = hashKey;
			mSocketHandler = socketHandler;
		}
		@Override
		public void onReceiveCmd(ByteBuffer buffer) {
			// TODO Auto-generated method stub
			mSocketHandler.onReceiveCmd(buffer);
		}
		@Override
		public void onCmdException(CommandParseException ex) {
			// TODO Auto-generated method stub
			mSocketHandler.onCmdException(ex);
		}
		@Override
		public void onSocketException(NetworkException ex) {
			// TODO Auto-generated method stub
			mSocketHandler.onSocketException(ex);
		}
		@Override
		public void onSocketClosed() {
			// TODO Auto-generated method stub
			clientMap.remove(mHashKey);
			mSocketHandler.onSocketClosed();
			
		}

	}


	@Override
	public void setClientConnectedListener(
			ClientConnectedListener clientConnectedListener) {
		// TODO Auto-generated method stub
		mClientConnectedListener = clientConnectedListener;
	}
}
