package com.yangxp.rtsp.client;

import java.net.InetSocketAddress;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.impl.UnBlockClient;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;

public interface RtspClient{
	public void request(RequestMessage request);
	
	public void onResponse(ResponseMessage response);
	
	public void response(ResponseMessage response);
	
	public void onRequest(RequestMessage request);
}
