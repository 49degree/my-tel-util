package com.skyeyes.base.network.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.SkyeyeNetworkClient;
import com.skyeyes.base.util.TypeConversion;

public class SkyeyeSocketClient implements SkyeyeNetworkClient,Runnable{
	private String mHost;
	private int mPort;
	private boolean mClose = false;
	
	private SocketHandler mSocketHandler;
	//通信通道
	private SocketChannel mSocketChannel;
	// 信道选择器
	private Selector selector;
	
	//private SendCmdBean mSendCmdBean;
	
	private Queue<SendCmdBean> mSendCmdBeanQueue = new LinkedList<SendCmdBean>();
	
	private ByteBuffer cmdHeaderBuffer = ByteBuffer.allocate(8);
	private ByteBuffer mReadBuffer = null;
	
	private boolean mIsShort = false;
	
	public SkyeyeSocketClient(SocketHandler socketHandler,Boolean isShort) throws NetworkException{
		mHost = "113.106.89.91";
		mPort = 4015;
		if(socketHandler == null){
			throw new NetworkException("SocketReturnData is null");
		}
		mSocketHandler = socketHandler;
		mSocketHandler.setSkyeyeSocketClient(this);
		mIsShort = isShort;
	}
	
	public void setServerAddr(String host,int port){
		mHost = host;
		mPort = port;
	}


	public synchronized void sendCmd(SendCmdBean sendCmdBean) throws NetworkException{
		if(isClosed()){
			throw new NetworkException("socket is closed");
		}
		
		
		if(mIsShort && mSendCmdBeanQueue.size()>0){
			throw new NetworkException("client has send cmd");
		}
		
		mSendCmdBeanQueue.offer(sendCmdBean);

		if(mSocketChannel == null){
			Thread t = new Thread(this);
			//t.setDaemon(true);
			t.start();//开启线程
		}else{
			try {
				mSocketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mSocketHandler.onSocketClosed();
			}
		}
	}
	
	private synchronized boolean isClosed(){
		return mClose;
	}
	
	public synchronized void doClose(){
		try {
			if(selector!=null)
				selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selector = null;
		
		try {
			if(mSocketChannel != null)
				mSocketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!mClose)
			mSocketHandler.onSocketClosed();
		
		mSocketChannel = null;
		mClose = true;
	}
	
	public void run(){
		if(mSocketChannel == null){
			InetSocketAddress address = new InetSocketAddress(mHost, mPort) ;
			
			try {
				selector = Selector.open();
				mSocketChannel = SocketChannel.open(address);
				mSocketChannel.configureBlocking(false);
				mSocketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
				return ;
			}
		}
		Set<SelectionKey> keys = null;
		Iterator<SelectionKey> it = null;
		while(!isClosed()){
			try {
				if(selector==null || 
						selector.select(50)<1){//更新监听信息，一定要调用
					continue;
				}
				keys = selector.selectedKeys();
				it = keys.iterator();
				
				while(it.hasNext()){
					SelectionKey key = it.next();
					it.remove();
					try {
						if(key.isReadable()){
							//System.out.println("遍历keys+++++++++++++++ isReadable:"+mSocketChannel.socket().getInputStream().available());
							int len = 0;
							if(mReadBuffer == null){
								cmdHeaderBuffer.rewind();
								//收报文头
								
								do{
									len = mSocketChannel.read(cmdHeaderBuffer);
								}while(len > 0 && cmdHeaderBuffer.position()<cmdHeaderBuffer.capacity());
								//System.out.println("接收到报文头："+cmdHeaderBuffer.position()+":"+TypeConversion.byte2hex(cmdHeaderBuffer.array(),0,cmdHeaderBuffer.capacity()));
								
								//比较报文头是否正确
								if(TypeConversion.bytesToInt(CommandControl.CMD_HEADER,0) != TypeConversion.bytesToInt(cmdHeaderBuffer.array(),0)){
									mSocketHandler.onCmdException(new CommandParseException("CMD_HEADER is error:"+TypeConversion.byte2hex(cmdHeaderBuffer.array(),0,4)));
									return;
								}
								//计算报文长度
								len = TypeConversion.bytesToInt(cmdHeaderBuffer.array(), 4);
								//System.out.println("报文长度："+len);
								mReadBuffer = ByteBuffer.allocate(12+len);
								cmdHeaderBuffer.flip();
								mReadBuffer.put(cmdHeaderBuffer);
							}
							
							len = mSocketChannel.read(mReadBuffer);
							//System.out.println("接收到报文长度："+len);
							
							if(mReadBuffer.position()==mReadBuffer.capacity()){
								System.out.println("接收到全部报文长度："+mReadBuffer.capacity()+
										(mReadBuffer.capacity()>100?"":":数据："+TypeConversion.byte2hex(mReadBuffer.array(),0,mReadBuffer.capacity())));
								ReceiveCmdBean receiveCmdBean = null;
								try {
									receiveCmdBean = CommandControl.parseReceiveCmd(mReadBuffer.array());
								} catch (CommandParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									mSocketHandler.onCmdException(e);
									break;
								}
								//如果是短连接，且不是应答数据包，则不进行处理(receiveCmdBean.getCommandHeader().cmdCode!=0 为请求数据)
								if(mIsShort && receiveCmdBean!=null && receiveCmdBean.getCommandHeader().cmdCode!=0)
									break;
								//返回命令对象
								mSocketHandler.onReceiveCmd(receiveCmdBean);
								
								mReadBuffer = null;
								
								//短连接
								if(mIsShort){
									doClose();
									return;
								}
							}
						}else if(key.isWritable()){
							System.out.println("遍历keys+++++++++++++++ isWritable:");
							synchronized (this) {
								if(mSendCmdBeanQueue.size()>0){
									try {
										ByteBuffer sendBuffer = ByteBuffer.wrap(CommandControl.parseDownComand(mSendCmdBeanQueue.peek()));
										System.out.println("上传报文："+sendBuffer.capacity()+":数据："+TypeConversion.byte2hex(sendBuffer.array(),0,sendBuffer.capacity()));
										mSocketChannel.write(sendBuffer);
									} catch (CommandParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										mSocketHandler.onCmdException(e);
									}
									
									if(mIsShort){
										//短连接，不可再写入
										mSocketChannel.register(selector, SelectionKey.OP_READ);
									}else{
										//非短连接，置空发送对象，可立即接收信息
										mSendCmdBeanQueue.poll();
									}
									
									if(mSendCmdBeanQueue.size()==0){
										//如果已经没有待发数据，则置成只能读取
										mSocketChannel.register(selector, SelectionKey.OP_READ);
									}
								}	
							}

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(!isClosed()){
							mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
						}
						break;
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				if(!isClosed()){
					mSocketHandler.onSocketException(new NetworkException(e1.getMessage()));
				}
				break;
			}
		}
		doClose();
	}
	
	
	public SocketHandler getmSocketHandler() {
		return mSocketHandler;
	}


	public boolean isShort() {
		return mIsShort;
	}
	public interface SocketHandler{ 
		public void setSkyeyeSocketClient(SkyeyeSocketClient skyeyeSocketClient);
		public void onReceiveCmd(ReceiveCmdBean receiveCmdBean);
		public void onCmdException(CommandParseException ex);
		public void onSocketException(NetworkException ex);
		public void onSocketClosed();
	}
}
