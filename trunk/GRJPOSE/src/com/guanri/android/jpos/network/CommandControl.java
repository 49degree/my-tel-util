package com.guanri.android.jpos.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.jpos.pad.ServerDataHandlerFactory;
import com.guanri.android.jpos.pad.ServerParseData;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

public class CommandControl {
	public static Logger logger = Logger.getLogger(CommandControl.class);//日志对象
	
	private static CommandControl instance = new CommandControl();
	private String serverIp = "211.148.7.252";//ip地址
	private int serverPort = 7001;//端口号
	
	private Socket socket = null;//连接对象
	private InputStream in = null;//输入流
	private OutputStream out = null;//输出流
	
	public static CommandControl getInstance(){
		return instance;
	}
	
	/**
	 * 构造方法
	 */
	private CommandControl(){
		
	}
	
	/**
	 * 在当前线程发送上传命令，在命令发送立即关闭连接
	 * 但是要自己处理IO异常和连接异常CommandParseException
	 * @param upCommandParse
	 * @return DownCommandParse 
	 */
	public synchronized byte[] sendUpCommand(ServerParseData serverParseData) throws IOException,CommandParseException{
		byte[] sendData = serverParseData.getBeSendData();
		byte[] returnData = this.submit(sendData);
		return returnData;
	}
	

	private boolean stopReceive = false;
	private byte[] recvAllBuffer = new byte[2048];//接收到的数据缓冲区
	private byte[] recvbuf = new byte[1024];
	private int recvAllBufferIndex = 0;
	private boolean isConnect = false;//是否连接

	/**
	 *  连接服务器
	 *  connectTimeOut;//连接超时时间
	 *  readTimeOut;//数据发送超时时间
	 */
	public synchronized boolean connect(int connectTimeOut,int readTimeOut) throws IOException{
		
		try {
			if(!isConnect){
				logger.debug("开始连接");
				socket = new Socket();
				socket.setSoTimeout(readTimeOut);//读取数据超时设置
				
				socket.setSendBufferSize(1024);
				socket.setSoLinger( true, 50 );
		        //关闭Nagle算法.立即发包   
		        socket.setTcpNoDelay(true);
				socket.connect(new InetSocketAddress(serverIp,serverPort), connectTimeOut);//建立连接超时设置
				out = socket.getOutputStream();
				in = socket.getInputStream();
				isConnect = true;
				stopReceive = false;
				logger.debug("连接完成");
			}
		} catch (IOException e) {
			isConnect = false;
			throw e;
		}
		
		return isConnect;
	}
	
	
	/**
	 * 发送命令
	 * @param sendData
	 * @return
	 * @throws IOException
	 * @throws CommandParseException
	 */

	public synchronized byte[] submit(byte[] sendData) throws IOException,CommandParseException{
		recvAllBufferIndex = 0;
		try {
			logger.debug("开始发送数据:"+TypeConversion.byte2hex(sendData));
			out.write(sendData);//发送数据
			out.flush(); 
			
			while (in!=null&&!stopReceive) {
				int nReadbyteLength = in.read(recvbuf);// 读取数据
				logger.debug("接收到的数据："+TypeConversion.byte2hex(recvbuf,0,nReadbyteLength));
				if (nReadbyteLength > 0) {
					// 将读到的数据插入全局数据缓冲区 
					//填充数据到缓存
					recvAllBuffer = Utils.insertEnoughLengthBuffer(recvAllBuffer, recvAllBufferIndex, recvbuf, 0, nReadbyteLength, 512);
					recvAllBufferIndex +=nReadbyteLength;
					//包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
					if(TypeConversion.bytesToShortEx(recvAllBuffer, 0)<=recvAllBufferIndex-2){
						stopReceive = true;
					}
				} else {
					stopReceive = true;
				}
				logger.debug("发送数据结束:"+recvAllBufferIndex);
			}
			
			// 关闭连接
			try {
				in.close();
				in = null;
				out.close();
				out = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//解析接收到的数据
			if(recvAllBuffer != null){ 
				byte[] returnData = new byte[recvAllBufferIndex];
				System.arraycopy(recvAllBuffer, 0, returnData, 0, recvAllBufferIndex);
				return returnData;
			}else{
				throw new CommandParseException("接收数据为空");
			}
		} catch (IOException e) {
			throw e;
		}catch(CommandParseException ex){
			throw ex;
		}catch(NullPointerException ne){
			throw new CommandParseException("下载数据解析错误");
		}finally {
			isConnect = false;
			try {
				socket.close();
			} catch (Exception ex) {
			}
			socket = null;
		}		
	}

	
	

}
