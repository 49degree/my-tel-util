package com.yangxp.rtsp.impl;

import java.io.IOException;

import com.yangxp.rtsp.RtspSession;

public class RtspSessionImpl implements RtspSession{
	private static final long serialVersionUID = 1L;
	String sessionId = null;
	public RtspSessionImpl(){
		
	}
	
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
	@Override
	public String getSessionDescription() throws IllegalStateException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
