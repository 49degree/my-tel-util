/***********************************************************************
 * Module:  SocketInit.java
 * Author:  Administrator
 * Purpose: Defines the Class SocketInit
 ***********************************************************************/

package com.a3650.posserver.core.init.socket;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.init.InitContext;
import com.a3650.posserver.core.init.PosServer;

public class SocketInitContext extends InitContext {
	static Logger logger = Logger.getLogger(SocketInitContext.class);
	private PosServer socketServer;
	private int serverPort = 0;
	private int serverTimeOut = 0;

	public static SocketInitContext getContext() {
		return Context.context;
	}

	public static void start() {
		Context.start();// 只是加载内部类，在加载内部类的过程中会出事化SocketInit对象
	}

	private SocketInitContext() {
		init();
	}

	protected void init() {
		try {
			logger.info("正在初始化。。。。。。。。。。");
			super.init();
			serverPort = Integer.parseInt(properties.getProperty("serverPort").trim());
			serverTimeOut = Integer.parseInt(properties.getProperty("serverTimeOut").trim());
			openSocketServer();
			logger.info("初始化完成。。。。。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("初始化失败");
		}

	}

	private void openSocketServer() {
		// TODO: implement
		//socketServer = SocketServer.Instance.init();
		socketServer = ChannelSocketServer.Instance.init();
		socketServer.setOperatorCenter(operatorCenter);
		new Thread((Runnable) socketServer).start();
	}

	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例没有绑定关系， 而且只有被调用到才会装载，从而实现了延迟加载
	 **/
	public static class Context {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		public static SocketInitContext context = new SocketInitContext();

		public static void start() {
		}
	}


	public PosServer getSocketServer() {
		return socketServer;
	}

	public int getServerPort() {
		return serverPort;
	}
	
	public static void main(String[] args){
		//服务器配置文件
		if(args.length>0&&args[0]!=null&&!"".equals(args[0]))
				InitContext.SERVER_CONFIG=args[0];
		SocketInitContext.start();
	}

	public int getServerTimeOut() {
		return serverTimeOut;
	}

}