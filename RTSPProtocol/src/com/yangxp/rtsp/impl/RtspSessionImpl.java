package com.yangxp.rtsp.impl;

import com.yangxp.rtsp.RtspSession;

public class RtspSessionImpl implements RtspSession{
	String sessionId = null;
	public RtspSessionImpl(String sessionId){
		this.sessionId = sessionId;
	}
	public String getSessionId() {
		return sessionId;
	}
	
	public boolean equals(Object o){
		if(o!=null&&
				o instanceof RtspSessionImpl
				&&sessionId!=null&&sessionId.equals(((RtspSessionImpl)o).getSessionId()))
			return true;
		return false;
	}
}
