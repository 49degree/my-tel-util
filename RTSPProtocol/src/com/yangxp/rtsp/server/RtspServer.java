package com.yangxp.rtsp.server;

import com.yangxp.rtsp.client.RtspClient;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;

public interface RtspServer {
	
	public void onRtspClientConnected(RtspClient client);
	

}
