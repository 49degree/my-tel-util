/***********************************************************************
 * Module:  SocketServer.java
 * Author:  Administrator
 * Purpose: Defines the Class SocketServer
 ***********************************************************************/

package com.a3650.posserver.core.init.socket;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.init.Client.CloseListener;
import com.a3650.posserver.core.init.OperatorCenter;
import com.a3650.posserver.core.init.PosServer;

public class ChannelSocketServer extends PosServer implements Runnable  {
	static Logger logger = Logger.getLogger(ChannelSocketServer.class); 
	private String operatorClass = null;
	private Selector selector = null;
	private ServerSocketChannel ssc = null;
	private Boolean serverStop = new Boolean(false);
	private HashMap<String,Object> client = new HashMap<String,Object>();//维持业务处理连接对象
	public static ChannelSocketServer instance = null;
	/**
	 * 单例内部类
	 * @author xueping.yang
	 *
	 */
	public static class Instance{
		static{
			instance = new ChannelSocketServer();
		}
		public static ChannelSocketServer init(){
			return instance;
		}
	}
	
	private ChannelSocketServer(){
		super();
		logger.info("实例化服务器对象.....");
	}
	
	/**
	 * 启动服务
	 */
	public void run() {
		try {
			selector = Selector.open(); // 实例化selector
			ssc = ServerSocketChannel.open(); // 实例化ServerSocketChannel 对象  
			ssc.socket().bind(new InetSocketAddress(SocketInitContext.getContext().getServerPort())); // 绑定端口
			ssc.configureBlocking(false); // 设置为非阻塞模式
			ssc.register(selector, SelectionKey.OP_ACCEPT); // 注册关心的事件，对于Server来说主要是accpet了
			while (!serverStop) {
				int n = selector.select(); // 获取感兴趣的selector数量
				//logger.info("selector.select():"+n);
				if (n < 1)
					continue; // 如果没有则一直轮询检查
				Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator(); // 有新的链接，我们返回一个SelectionKey集合
				while (keyIter.hasNext()) {
					SelectionKey key = keyIter.next();
					try{
						// 移除处理过的键
						keyIter.remove();
				        // skip if not valid
				        if (!key.isValid()) {
				        	key.cancel();
				            continue;
				        }
						if (key.isAcceptable()) {
							// 有客户端连接请求时
							accept(key);
						}else if (key.isReadable()) {
							// 从客户端读取数据
							createClient(key);
						}else if(key.isWritable()){
							//logger.info("key.isWritable():"+key.isWritable());
						}

					}catch(Exception e){
						//e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				selector.close();
				ssc.close();
				clientThreadPool.shutdownNow();
			} catch (IOException e) {
			}
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
    private void accept(SelectionKey key)throws IOException {
		try {
			ServerSocketChannel ssc2 = (ServerSocketChannel) key.channel();
			SocketChannel channel = ssc2.accept();
			channel.configureBlocking(false); //同样是非阻塞
			channel.register(selector, SelectionKey.OP_READ); // 本次注册的是read事件，即receive接受
			synchronized (clientCount) {
				clientCount.count = clientCount.count+1;
				logger.info("客户端接入,当前客户总数："+clientCount.count);
			}
		} catch (IOException ioe) {
			// failed, drop the connection
			//throw ioe;
		} catch (RuntimeException re) {
			// failed, drop the connection
			//throw re;
		}
	}
	
    /**
     * 获取输入流对象，并开始处理流程
     * @param key
     */
    
    private void createClient(final SelectionKey key){
    	Socket socket = ((SocketChannel)key.channel()).socket();
    	String keyStr = socket.getInetAddress().getHostName()+socket.getPort();

		try {
			ChannelSocketClient socketClient = null;
			synchronized(client){
		    	if(client.containsKey(keyStr)||!key.isValid()){
		    		return;
		    	}
		    	
				socketClient = new ChannelSocketClient();
				socketClient.setSelectionKey(key);
				client.put(keyStr,socketClient);
			}
			
			socketClient.setCloseListener(new CloseListener(){
				public void close(){
					synchronized (clientCount) {
						clientCount.count = clientCount.count-1;
						logger.info("客户端关闭,当前客户总数："+clientCount.count);
					}
				}
			});
			key.interestOps(SelectionKey.OP_READ);
			//创建处理业务对象
			Class<?> op = Class.forName(operatorClass);
			Constructor<?> constructor = op.getConstructor(Client.class);
			OperatorCenter operatorCenter = (OperatorCenter)constructor.newInstance(socketClient);
			startBusiness(operatorCenter);
		} catch (Exception re) {
			logger.error("处理连接失败");
			re.printStackTrace();
		}
    }
    
	public void removeKey(SelectionKey key){
		synchronized(client){
			// 关闭连接
			try {
				if(key!=null){
				   	Socket socket = ((SocketChannel)key.channel()).socket();
					client.remove(socket.getInetAddress().getHostName()+socket.getPort());
				}
			} catch (Exception ex) {
			}
			//logger.info("removeKey+++++++++++++++");
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
	


}