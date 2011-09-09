package com.guanri.android.jpos.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.TreeMap;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefineUnionpay;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.unionpay.JposMessageTypeUnionPay;
import com.guanri.android.jpos.iso.unionpay.JposPackageUnionPay;
import com.guanri.android.jpos.pos.data.PosCommandParse;
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
	 * 在新线程中发送命令，在命令发送立即关闭连接
	 * 通过UpCommandParse对象中的handler返回信息
	 * @param upCommandParse
	 * @return
	 */
	public synchronized boolean sendUpCommandInThread(PosCommandParse upCommandParse){
		CommandControlTask commandControlTask = new CommandControlTask(upCommandParse);
		commandControlTask.start();
		return true;
	}
	
	/**
	 * 在当前线程发送上传命令，在命令发送立即关闭连接
	 * 但是要自己处理IO异常和连接异常CommandParseException
	 * @param upCommandParse
	 * @return DownCommandParse 
	 */
	public synchronized PosMessageBean sendUpCommand(PosCommandParse upCommandParse) throws IOException,CommandParseException{
		PosMessageBean posMessageBean = this.submit(upCommandParse.getTransferByte());
		return posMessageBean;
	}
	
	/**
	 * 与服务器交互线程
	 * @author Administrator
	 *
	 */
	public class CommandControlTask extends Thread{
		private PosCommandParse upCommandParse = null;
		/**
		 * @roseuid 4DF8330D00DA
		 */
		public CommandControlTask(PosCommandParse upCommandParse) {
			this.upCommandParse = upCommandParse;
		}

		public void run() {
			try {
				//通过handler消息机制返回结果
				PosMessageBean osMessageBean = submit(upCommandParse.getTransferByte());
			}catch(Exception e){
				
			}
		}
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

	private PosMessageBean submit(byte[] sendData) throws IOException,CommandParseException{
		recvAllBufferIndex = 0;
		try {
			out.write(sendData);//发送数据
			out.flush(); 
			
			while (in!=null&&!stopReceive) {
				int nReadbyteLength = in.read(recvbuf);// 读取数据
				if (nReadbyteLength > -1) {
					// 将读到的数据插入全局数据缓冲区 
					//填充数据到缓存
					recvAllBuffer = Utils.insertEnoughLengthBuffer(recvAllBuffer, recvAllBufferIndex, recvbuf, 0, nReadbyteLength, 512);
					recvAllBufferIndex +=nReadbyteLength;
					//包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
					if(TypeConversion.bytesToShortEx(recvAllBuffer, 0)<=recvAllBufferIndex-2){
						stopReceive = false;
					}
				} else {
					stopReceive = false;
				}
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
				PosCommandParse downCommandParse = new PosCommandParse(recvAllBuffer);
				return downCommandParse.getPosMessageBean();
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
			try {
				socket.close();
			} catch (Exception ex) {
			}
			socket = null;
		}		
	}

	
	
	public static void main(String[] args){
		logger.debug(TypeConversion.byte2hex(bill99longin()));
	}
	
	
	/**
	 * 银联签到方法
	 * @return 返回签到报文
	 */
	public static byte[] bill99longin(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "000000010001");
		// 域63 自定义域  63.1 操作员代码
		sendMap.put(63, "001");
		
		
		
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		messageType.setPageLength((short)59);
		messageType.setId((byte)0x60);  
		messageType.setServerAddress("0000");
		messageType.setServerAddress("0000");
		messageType.setAddress("0090");
		messageType.setPagever("0100");
		
//		messageType.setServerAddress("0000");
//		messageType.setAddress("0000");
//		messageType.setAppType("60");
//		messageType.setSoftVer("22");
//		messageType.setPosstate("0");
//		messageType.setDisposal("3");
//		messageType.setPreserving("000000");
		//设置消息头类型
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_POS_CHECK_IN);
		
		JposPackage99Bill jposPackageUnionPay = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackageUnionPay.packaged();
	}
	
	/**
	 * 银联签到方法
	 * @return 返回签到报文
	 */
	public static byte[] unionpaylongin(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "000000010001");
		// 域63 自定义域  63.1 操作员代码
		sendMap.put(63, "001");
		
		JposMessageTypeUnionPay messageType = new JposMessageTypeUnionPay();
		messageType.setServerAddress("0000");
		messageType.setAddress("0000");
		messageType.setAppType("60");
		messageType.setSoftVer("22");
		messageType.setPosstate("0");
		messageType.setDisposal("3");
		messageType.setPreserving("000000");
		//设置消息头类型
		messageType.setMessageType(MessageTypeDefineUnionpay.REQUEST_POS_CHECK_IN);
		
		JposPackageUnionPay jposPackageUnionPay = new JposPackageUnionPay(sendMap,messageType);
	 
		byte[] ruslt = jposPackageUnionPay.packaged();
		return ruslt;
	}
}
