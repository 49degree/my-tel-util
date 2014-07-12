package com.skyeyes.base.network.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.SkyeyeNetworkClient;
import com.skyeyes.base.network.SocketHandler;
import com.skyeyes.base.util.TypeConversion;

public class SkyeyeBlockClient implements SkyeyeNetworkClient{
	static String TAG = "SkyeyeBlockClient";
	private String mHost;
	private int mPort;
	private boolean mClose = false;
	private SocketHandler mSocketHandler;
	
	private Queue<SendCmdBean> mSendCmdBeanQueue = new LinkedList<SendCmdBean>();
	private ByteBuffer cmdHeaderBuffer = ByteBuffer.allocate(8);
	private ByteBuffer mReadBuffer = null;
	private boolean mIsShort = false;
	
	private Socket mSocket = null;
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	
	private WriteThread mWriteThread = null;
	private ReadThread mReadThread = null;
	
	public SkyeyeBlockClient(SocketHandler socketHandler,Boolean isShort) throws NetworkException{

		if(socketHandler == null){
			throw new NetworkException("SocketReturnData is null");
		}
		mSocketHandler = socketHandler;
		mSocketHandler.setSkyeyeSocketClient(this);
		mIsShort = isShort;
	}
	
	
	@Override
	public void setServerAddr(String host, int port) {
		// TODO Auto-generated method stub
		mHost = host;
		mPort = port;
	}

	@Override
	public void sendCmd(SendCmdBean sendCmdBean) throws NetworkException {
		// TODO Auto-generated method stub
		if(isClosed()){
			throw new NetworkException("socket is closed");
		}
		if(mIsShort && mSendCmdBeanQueue.size()>0){
			throw new NetworkException("client has send cmd");
		}

		if(mWriteThread==null){
			mWriteThread = new WriteThread();
			mWriteThread.start();
		}
		
		synchronized (mSendCmdBeanQueue) {
			mSendCmdBeanQueue.offer(sendCmdBean);
			mSendCmdBeanQueue.notify();
		}


		
	}
	
	private synchronized boolean isClosed(){
		return mClose;
	}
	
	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		System.out.println("doClose++++++++++");
		mClose = true;
		synchronized (mSendCmdBeanQueue) {
			mSendCmdBeanQueue.notify();
		}
		try{
			if(mSocket!=null)
				mSocket.close();
			if(mOutputStream!=null)
				mOutputStream.close();
			if(mInputStream!=null){
				mInputStream.close();
			}
			if(!mClose)
				mSocketHandler.onSocketClosed();

		}catch(Exception ex){
			
		}
		mSocket = null;
		mInputStream = null;
		mOutputStream = null;

	}
	
	
	private class ReadThread extends Thread{
		public void run(){
			while(!isClosed()){
				if(mInputStream==null){
					break;
				}
				try {
					int len = 0;
					if(mReadBuffer == null){
						cmdHeaderBuffer.rewind();
						//收报文头
						do{
							len = mInputStream.read(cmdHeaderBuffer.array(),cmdHeaderBuffer.position(),cmdHeaderBuffer.capacity()-cmdHeaderBuffer.position());
							cmdHeaderBuffer.position(cmdHeaderBuffer.position()+len);
						}while(len > 0 && cmdHeaderBuffer.position()<cmdHeaderBuffer.capacity());
						//System.out.println("接收到报文头："+cmdHeaderBuffer.position()+":"+TypeConversion.byte2hex(cmdHeaderBuffer.array(),0,cmdHeaderBuffer.capacity()));
						
						//比较报文头是否正确
						if(TypeConversion.bytesToInt(CommandControl.CMD_HEADER,0) != TypeConversion.bytesToInt(cmdHeaderBuffer.array(),0)){
							mSocketHandler.onCmdException(new CommandParseException("CMD_HEADER is error:"+TypeConversion.byte2hex(cmdHeaderBuffer.array(),0,4)));
							continue;
						}
						//计算报文长度
						len = TypeConversion.bytesToInt(cmdHeaderBuffer.array(), 4);
						//System.out.println("报文长度："+len);
						mReadBuffer = ByteBuffer.allocate(12+len);
						cmdHeaderBuffer.flip();
						mReadBuffer.put(cmdHeaderBuffer);
					}
					len = mInputStream.read(mReadBuffer.array(),mReadBuffer.position(),mReadBuffer.capacity()-mReadBuffer.position());
					mReadBuffer.position(mReadBuffer.position()+len);
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
							continue;
						//返回命令对象
						mSocketHandler.onReceiveCmd(receiveCmdBean);
						
						mReadBuffer = null;
						
						//短连接
						if(mIsShort){
							doClose();
							continue;
						}
					}
				}catch(SocketTimeoutException e){
					//e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					if(!isClosed()){
						mSocketHandler.onSocketException(new NetworkException(e1.getMessage()));
						doClose();
					}
					
				}
			}
			
		}
	}
	
	private class WriteThread extends Thread{
		public void run(){
			if(mSocket == null){
				InetSocketAddress address = new InetSocketAddress(mHost, mPort) ;
				try {
					mSocket = new Socket();
					mSocket.connect(address,30*1000);
					mSocket.setSoTimeout(10*1000);
					mSocket.setKeepAlive(true);
					mSocket.setTcpNoDelay(true);
					
					mOutputStream = mSocket.getOutputStream();
					mInputStream = mSocket.getInputStream();
					
					mReadThread = new ReadThread();
					mReadThread.start();
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					new Thread(){
						public void run(){
							try{
								mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
							}catch(Exception ex){
								
							}
						}
					}.start();
					doClose();
					return ;
				}
			}
			
			while(!isClosed()){
				if(mOutputStream==null){
					break;
				}
				synchronized (mSendCmdBeanQueue) {
					if(mSendCmdBeanQueue.size()>0){
						try {
							byte[] sendByte = CommandControl.parseDownComand(mSendCmdBeanQueue.peek());
							System.out.println("上传报文："+sendByte.length+":数据："+TypeConversion.byte2hex(sendByte,0,sendByte.length));
							mOutputStream.write(sendByte);
						} catch (CommandParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							mSocketHandler.onCmdException(e);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if(!isClosed()){
								mSocketHandler.onSocketException(new NetworkException(e.getMessage()));
								doClose();
							}
						}
						if(!mIsShort){
							//非短连接，置空发送对象
							mSendCmdBeanQueue.poll();
						}else
							break;
						
					}else{
						try {
							mSendCmdBeanQueue.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
