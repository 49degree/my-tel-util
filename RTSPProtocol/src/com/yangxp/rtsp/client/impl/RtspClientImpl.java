package com.yangxp.rtsp.client.impl;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.rtsp.RtspSession;
import com.yangxp.rtsp.client.RtspClient;
import com.yangxp.rtsp.impl.RtspSessionImpl;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.MessageBase;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;
import com.yangxp.rtsp.utils.MyStreamHandlerFactory;
import com.yangxp.rtsp.utils.UUIDGenerator;

public abstract class RtspClientImpl implements RtspClient{
	static Logger logger = Logger.getLogger(RtspClientImpl.class);
	static{
		try{
			URL.setURLStreamHandlerFactory(new MyStreamHandlerFactory());
		}catch(Error e){
			
		}
	}
	
	SocketClient mSocketClient = null;
	RtspSession mRtspSession = null;
	int cseq = 1;
	
	HashMap<Integer,RequestMessage> requestList = new HashMap<Integer,RequestMessage>();
	String mRstpURL = null;
	String mRtspVersion = null;
	URL mURL = null;
	

	void sendMessage(Message message) {
		// TODO Auto-generated method stub
		String messageStr = message.packageMessage();
		ByteBuffer buffer = ByteBuffer.wrap((messageStr).getBytes());
		
		mSocketClient.sendBuffer(buffer);
	}



	
	public void request(RequestMessage request){
		
	}
	
	public void onResponse(ResponseMessage response){
		
	}
	
	public void response(ResponseMessage response){
		
	}
	
	public void onRequest(RequestMessage request){
		
	}

}
