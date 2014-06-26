package com.skyeyes.base.network;

import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.impl.SkyeyeBlockClient;

public class NetWorkFactory {
	public static SkyeyeNetworkClient getSkyeyeNetworkClient(SocketHandler socketHandler,Boolean isShort) throws NetworkException{
		return new SkyeyeBlockClient(socketHandler, isShort);
	}
}
