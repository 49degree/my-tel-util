package com.yangxp.rtsp.server.impl;

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.yangxp.net.tcp.ClientConnectedListener;
import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.SocketServer;
import com.yangxp.rtsp.client.RtspClient;
import com.yangxp.rtsp.client.impl.RtspClientImpl;
import com.yangxp.rtsp.server.RtspServer;
import com.yangxp.rtsp.utils.MyStreamHandlerFactory;

public class RtspServerImpl implements RtspServer{
	static Logger logger = Logger.getLogger(RtspServerImpl.class);
	
	static{
		try{
			URL.setURLStreamHandlerFactory(new MyStreamHandlerFactory());
		}catch(Error e){
			
		}
	}
	
	int mPort;
	SocketServer mSocketServerImpl = null;
	HashMap<Integer,RtspClient> clientMap = new HashMap<Integer,RtspClient>();
	public RtspServerImpl(int port){
		mPort = port;
		mSocketServerImpl  = SocketServer.Factory.getDefault(mPort); 
		
		mSocketServerImpl.setClientConnectedListener(new ClientConnectedListener(){
			@Override
			public void onClientConnected(SocketClient socketClient) {
				// TODO Auto-generated method stub
				RtspClientImpl rtspClientImpl = new RtspClientImpl(socketClient);
				onRtspClientConnected(rtspClientImpl);
			}
		});
		
		mSocketServerImpl.doOpen();
	}
	
	@Override
	public void onRtspClientConnected(RtspClient client) {
		// TODO Auto-generated method stub
		clientMap.put(client.hashCode(), client);
	}
	
	
}
