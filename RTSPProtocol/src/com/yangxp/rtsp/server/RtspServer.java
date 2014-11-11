package com.yangxp.rtsp.server;

import com.yangxp.rtsp.client.RtspClient;

public interface RtspServer {
	
	public void onRtspClientConnected(RtspClient client);
}
