package com.skyeyes.base.network;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;

public interface SocketHandler{ 
	public void setSkyeyeSocketClient(SkyeyeNetworkClient skyeyeSocketClient);
	public void onReceiveCmd(ReceiveCmdBean receiveCmdBean);
	public void onCmdException(CommandParseException ex);
	public void onSocketException(NetworkException ex);
	public void onSocketClosed();
}
