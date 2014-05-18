package com.skyeyes.base.network;

import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.exception.NetworkException;

public interface SkyeyeNetworkClient {
	public void setServerAddr(String host,int port);
	public void sendCmd(SendCmdBean sendCmdBean) throws NetworkException;
	public void doClose();
}
