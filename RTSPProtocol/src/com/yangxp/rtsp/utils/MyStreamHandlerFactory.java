package com.yangxp.rtsp.utils;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class MyStreamHandlerFactory implements URLStreamHandlerFactory {

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		// TODO Auto-generated method stub
		if("rtsp".equals(protocol.toLowerCase()))
			return new RSTPURLStreamHandler();
		return null;
	}

}
