package com.yangxp.net.tcp;

import java.nio.ByteBuffer;

import com.yangxp.net.exception.CommandParseException;
import com.yangxp.net.exception.NetworkException;

public interface SocketHandler{ 
	public void onReceiveCmd(ByteBuffer buffer);
	public void onCmdException(CommandParseException ex);
	public void onSocketException(NetworkException ex);
	public void onSocketClosed();
}
