/***********************************************************************
 * Module:  SocketServer.java
 * Author:  Administrator
 * Purpose: Defines the Class SocketServer
 ***********************************************************************/

package com.a3650.posserver.core.init.socket;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.hibernate.stat.Statistics;

import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.init.Client.CloseListener;
import com.a3650.posserver.core.init.OperatorCenter;
import com.a3650.posserver.core.init.PosServer;

public class SocketServer extends PosServer implements Runnable {
	static Logger logger = Logger.getLogger(SocketServer.class); 
	
	public static SocketServer instance = null;
	private String operatorClass = null;
	private ServerSocket serverSocket = null;
	private Boolean serverStop = new Boolean(false);

	/**
	 * 单例内部类
	 * @author xueping.yang
	 *
	 */
	public static class Instance{
		static{
			instance = new SocketServer();
		}
		public static SocketServer init(){
			return instance;
		}
	}
	
	private SocketServer(){
		super();
		logger.info("实例化服务器对象.....");
	}
	
	/**
	 * 启动服务
	 */
	public void run() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(
					SocketInitContext.getContext().getServerPort())); // 绑定端口
			//Statistics st = HibernateUtil.getSessionFactory().getStatistics(); 
			while(!serverStop){
				Socket client = serverSocket.accept();
				accept(client);
				//printSecondLevelCache("",st);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}
	
	/**
	 *停止服务 
	 */
	public void stopServer(){
		try{
			Thread.currentThread().interrupt();
			serverStop = true;
		}catch(Exception e){}
	}
	/**
	 * 接收连接
	 * @param key
	 * @throws IOException
	 */

	private void accept(final Socket client) {
		try {
			synchronized (clientCount) {
				clientCount.count = clientCount.count+1;
				logger.info("客户端接入,当前客户总数："+clientCount.count);
			}
			
			
			SocketClient socketClient = new SocketClient(client);
			socketClient.setCloseListener(new CloseListener(){
				public void close(){
					synchronized (clientCount) {
						clientCount.count = clientCount.count-1;
						logger.info("客户端关闭,当前客户总数："+clientCount.count);
					}
				}
			});
			// 创建处理业务对象
			Class<?> op = Class.forName(operatorClass);
			Constructor<?> constructor = op.getConstructor(Client.class);
			OperatorCenter operatorCenter = (OperatorCenter) constructor.newInstance(socketClient);
			startBusiness(operatorCenter);
		} catch (Exception re) {
			logger.error("处理连接失败");
			re.printStackTrace();
		}
	}
    
    
	@Override
	public void setOperatorCenter(String operator) {
		// TODO Auto-generated method stub
		this.operatorClass = operator;
	}

	@Override
	public int startBusiness(OperatorCenter operatorCenter) {
		// TODO Auto-generated method stub
		return super.startBusiness(operatorCenter);
	}
	
	public static void printSecondLevelCache(String tag, Statistics st) {
		System.out.println("tag:" + tag);
		System.out.println("put:" + st.getSecondLevelCachePutCount());
		System.out.println("hit:" + st.getSecondLevelCacheHitCount());
		System.out.println("miss:" + st.getSecondLevelCacheMissCount());
		System.out.println("load:" + st.getEntityLoadCount());
	}

}