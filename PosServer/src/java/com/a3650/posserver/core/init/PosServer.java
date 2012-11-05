/***********************************************************************
 * Module:  PosServer.java
 * Author:  Administrator
 * Purpose: Defines the Interface PosServer
 ***********************************************************************/

package com.a3650.posserver.core.init;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public abstract class PosServer {
	protected ExecutorService clientThreadPool = null;
	protected ClientCount clientCount = new ClientCount();
	
	public class ClientCount{
		public int count = 0 ;
	}
	//private ServerThreadPool clientThreadPool = null;//自定义线程池
	public PosServer(){
		clientThreadPool = Executors.newFixedThreadPool(InitContext.getClientThreadPoolSize());
		
		//clientThreadPool = ServerThreadPool.getServerThreadPool(InitContext.getClientThreadPoolSize());
	}
	
	public abstract void setOperatorCenter(String operator);

	protected int startBusiness(OperatorCenter operatorCenter){
		clientThreadPool.execute(operatorCenter);
		return 0;
	}

}