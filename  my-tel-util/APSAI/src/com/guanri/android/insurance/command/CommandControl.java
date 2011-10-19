package com.guanri.android.insurance.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 命令上传下载控制
 * @author Administrator
 *
 */
public class CommandControl {
	public static Logger logger = Logger.getLogger(CommandControl.class);//日志对象
	
	private static final int message_timeout=120*1000;
	private CommandControlTask commandControlTask = null;
	//private LinkedList<UpCommandParse> upCommandQueue = null;
	private static CommandControl instance = new CommandControl();
	private static int comSeq;//命令序列号
	private String ip = null;//ip地址
	private int port;//端口号
	private int connTimeOut;//连接超时时间
	private InputStream in = null;
	private Socket socket = null;
	//private PrintStream out = null;
	private OutputStream out = null;
	private Timer mTimer= null;
	
	private CommandControl(){
		comSeq = getConfigInt(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.COM_SEQ);//命令序列号
	}
	
	/**
	 * 从配置文件中读取整型数据
	 * @param fileName
	 * @param elements
	 * @return
	 */
	private int getConfigInt(String fileName,String elements){
		try{
			return Integer.parseInt(SharedPreferencesUtils.getConfigString(
					fileName,elements));
		}catch(Exception e){
			return 0;
		}
	}
	
	public static CommandControl getInstance(){
		return instance;
	}
	
	/**
	 * 在新线程中发送命令，在命令发送立即关闭连接
	 * 通过UpCommandParse对象中的handler返回信息
	 * @param upCommandParse
	 * @return
	 */
	public synchronized boolean sendUpCommandInThread(UpCommandParse upCommandParse,Handler handler){
		commandControlTask = new CommandControlTask(upCommandParse,handler);
		commandControlTask.start();
		mTimer = new Timer();
		mTimer.schedule(new TimeOutCheckTimer(upCommandParse) , CommandControl.message_timeout);
		return true;
	}
	
	/**
	 * 在当前线程发送上传命令，在命令发送立即关闭连接
	 * 但是要自己处理IO异常和连接异常CommandParseException
	 * @param upCommandParse
	 * @return DownCommandParse 
	 */
	public synchronized DownCommandParse sendUpCommand(UpCommandParse upCommandParse) throws IOException,CommandParseException{
		DownCommandParse downCommandParse = this.submit(upCommandParse);
		mTimer = new Timer();
		mTimer.schedule(new TimeOutCheckTimer(upCommandParse) , CommandControl.message_timeout);
		return downCommandParse;
	}
	
	
	/**
	 * 获取命令系列
	 * @return
	 */
	public synchronized int getComSeq(){
		return comSeq;
	}
	
	/**
	 * 正常接收到返回命令，当前命令系列号+1
	 * @return
	 */
	private synchronized int getNextComSeq(byte[] recvAllBuffer){
		try{
			switch(recvAllBuffer[28]){
			case CommandConstant.ANSWER_CODE_SEQ_ERROR:
				comSeq = 0;//判断命令系列是否失序
				break;
			case CommandConstant.ANSWER_CODE_SEQ_STOP:
			case CommandConstant.ANSWER_CODE_RIGHT_SEQ_STOP:
				break;
			default:
				++comSeq;//命令序号+1
			}
		}catch(Exception e){
			
		}
		SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO,"ComSeq",String.valueOf(comSeq));//保存
		return comSeq;
	}
		
	/**
	 * 与服务器交互线程
	 * @author Administrator
	 *
	 */
	public class CommandControlTask extends Thread{
		private UpCommandParse upCommandParse = null;
		private Handler handler = null;
		/**
		 * @roseuid 4DF8330D00DA
		 */
		public CommandControlTask(UpCommandParse upCommandParse,Handler handler) {
			this.upCommandParse = upCommandParse;
			this.handler = handler;
		}

		public void run() {
			try {
				//通过handler消息机制返回结果
				DownCommandParse downCommandParse = submit(upCommandParse);
				Message msg = handler.obtainMessage(Integer.parseInt(
						upCommandParse.getUpCommandBean().getCommandCode()), downCommandParse);
				handler.sendMessage(msg); 
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				handler.sendMessage(createErrorMsg(handler,StringUtils.getStringFromValue(R.string.apsai_common_server_link_error)));
			}catch(CommandParseException ex){
				logger.error(ex.getMessage());
				handler.sendMessage(createErrorMsg(handler,ex.getMessage()));
			}
		}
	}
	
	
	
	/**
	 * 发送命令
	 * @param upCommandParse
	 * @return
	 * @throws IOException
	 * @throws CommandParseException
	 */
	private DownCommandParse submit(UpCommandParse upCommandParse) throws IOException,CommandParseException{
		byte[] recvAllBuffer = null; // 接收到的数据缓冲区
		try {
			ip = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.SERVER_INFO,SharedPreferencesUtils.SERVERIP);//ip地址
			port = getConfigInt(SharedPreferencesUtils.SERVER_INFO,SharedPreferencesUtils.SERVERPORT);//端口号
			connTimeOut = getConfigInt(SharedPreferencesUtils.SERVER_INFO,SharedPreferencesUtils.CONNTIMEOUT)*1000;//连接超时时间
			socket = new Socket();
			socket.setSoTimeout(connTimeOut);//读取数据超时设置3m
			
			socket.setSendBufferSize(1024);
			socket.setSoLinger( true, 50 );
	        //关闭Nagle算法.立即发包   
	        socket.setTcpNoDelay(true);    
	        
			socket.connect(new InetSocketAddress(ip,port), connTimeOut);//建立连接超时设置
			
			if(upCommandParse.getUpCommandBean().getCommandCode()==CommandConstant.CMD_SALE2){
				byte[] body = new byte[upCommandParse.getUpCommandBean().getBody().length+100];
				System.arraycopy(upCommandParse.getUpCommandBean().getBody(), 0, body, 0, body.length-100);
				upCommandParse.getUpCommandBean().setBody(body);
			}
			

 
			//out = new PrintStream(socket.getOutputStream(), true);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			logger.error("上传数据:"+upCommandParse.getCommandBuffer().length+":"+TypeConversion.byte2hex(upCommandParse.getCommandBuffer()));
			out.write(upCommandParse.getCommandBuffer());
			out.flush(); 
			
			while (in != null) {//实际上服务器严格执行一次上传一次下载服务，此处的循环实际没有意义
				byte[] recvbuf = new byte[2048];
				logger.error("等待读取数据:"+socket.getSendBufferSize());
				int nReadbyteLength = in.read(recvbuf);//读取数据
				if (nReadbyteLength > -1) {
					// 将读到的数据插入全局数据缓冲区
					if(recvAllBuffer!=null){
						byte[] temp = new byte[recvAllBuffer.length+nReadbyteLength];
						System.arraycopy(recvAllBuffer, 0, temp, 0, recvAllBuffer.length);
						System.arraycopy(recvbuf, 0, temp, recvAllBuffer.length, nReadbyteLength);
						recvAllBuffer = temp;
					}else{
						recvAllBuffer = new byte[nReadbyteLength];
						System.arraycopy(recvbuf, 0, recvAllBuffer,0, nReadbyteLength);
					}
				}else{
					logger.error("下载数据数据为空:"+this.getComSeq());
				}
				//关闭连接
				try {
					in.close();
					in = null;
					out.close();
					out = null;
				} catch (Exception ex) {
				}
			}
			
			if(recvAllBuffer != null){ 
				logger.error("下载数据:"+TypeConversion.byte2hex(recvAllBuffer));
				logger.debug("命令长度:"+TypeConversion.bytesToShort(recvAllBuffer, 0));
				logger.debug("命令码:"+TypeConversion.asciiToString(recvAllBuffer, 2, 6));
				logger.debug("命令序列:"+TypeConversion.bytesToInt(recvAllBuffer, 8));
				logger.debug("数字签名:"+TypeConversion.bytesToLong(recvAllBuffer, 12));
				logger.debug("应答码:"+recvAllBuffer[28]);
				logger.debug("应答信息:"+TypeConversion.asciiToString(recvAllBuffer, 29, 40));
				getNextComSeq(recvAllBuffer);//命令序号+1
				if(recvAllBuffer[28]==CommandConstant.ANSWER_CODE_SEQ_ERROR){//命令系列失序,则从新上传数据
					return submit(upCommandParse);
				}else{
					//通过handler消息机制返回结果
					DownCommandParse DownCommandParse = new DownCommandParse(recvAllBuffer);
					return DownCommandParse;
				}
			}else{
				throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_downcommand_parse_error));
			}
		} catch (IOException e) {
			throw e;
		}catch(CommandParseException ex){
			throw ex;
		}catch(NullPointerException ne){
			throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_downcommand_parse_error));
		}finally {
			try {
				if(mTimer!=null){
					mTimer.cancel();
					mTimer.purge();
				}
			} catch (Exception ex) {
			}finally {
				mTimer = null;
			}
			try {
				socket.close();
			} catch (Exception ex) {
			}
			socket = null;
		}		
	}
	
	
	/**
	 * 延时后执行的操作
	 * @author Administrator
	 *
	 */
	private class TimeOutCheckTimer extends TimerTask {		
		private UpCommandParse upCommandParse = null;
		private Handler handler = null;
		public TimeOutCheckTimer(UpCommandParse upCommandParse) {
			this.upCommandParse = upCommandParse;
		}
		
		public TimeOutCheckTimer(UpCommandParse upCommandParse,Handler handler) {
			this.upCommandParse = upCommandParse;
			this.handler = handler;
		}
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			if(handler!=null){
				handler.sendMessageDelayed(
						createTimeoutMsg(handler,StringUtils.getStringFromValue(R.string.apsai_common_server_no_req)), 
						CommandControl.message_timeout);
			}
			if(commandControlTask!=null&&commandControlTask.upCommandParse==this.upCommandParse){
				try{
					//commandControlTask.interrupt();//这中方式对应JDK 1.4以前的无效
					socket.close();//关闭连接
				}catch(Exception e){
					
				}
			}
		}
	}
	
	/**
	 * 构造超时相应消息
	 * @param handler
	 * @param msgStr
	 * @return
	 */
	private Message createTimeoutMsg(Handler handler,String msgStr){
		return handler.obtainMessage(HandlerWhat.NETWORK_REQUEST_TIMEOUT_WHAT, msgStr);
	}
	
	/**
	 * 构造错误相应消息
	 * @param handler
	 * @param msgStr
	 * @return
	 */
	private Message createErrorMsg(Handler handler,String msgStr){
		return handler.obtainMessage(HandlerWhat.NETWORK_REQUEST_ERROR_WHAT, msgStr);
	}
}
