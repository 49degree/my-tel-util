package com.yangxp.net.tcp.impl;

import org.apache.log4j.Logger;

import com.yangxp.net.exception.CommandParseException;
import com.yangxp.net.exception.NetworkException;
import com.yangxp.net.tcp.SocketHandler;

public abstract class SocketHandlerClient implements SocketHandler{
	static Logger logger = Logger.getLogger(SocketHandlerClient.class);

	@Override
	public void onCmdException(CommandParseException ex) {
		// TODO Auto-generated method stub
		logger.error(ex.getMessage());
	}

	@Override
	public void onSocketException(NetworkException ex) {
		// TODO Auto-generated method stub
		logger.error(ex.getMessage());
	}

	@Override
	public void onSocketClosed() {
		// TODO Auto-generated method stub
		logger.info("onSocketClosed");
	} 
}
