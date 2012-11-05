package init;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.exception.CommandParseException;
import com.a3650.posserver.core.exception.PacketException;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;

public class TestSocket {
	public static Logger logger = Logger.getLogger(TestSocket.class);//日志对象
	public static String SERVER_CONFIG = "server.properties";
	private static TestSocket instance = new TestSocket();
	private String serverIp = "localhost";//ip地址
	private int serverPort = 0;//端口号
	
	private Socket socket = null;//连接对象
	private InputStream in = null;//输入流
	private OutputStream out = null;//输出流
	
	public static TestSocket getInstance(){
		return instance;
	}
	
	/**
	 * 构造方法
	 */
	private TestSocket(){
		try{
			Properties properties = new Properties();
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ SERVER_CONFIG));
			serverPort = Integer.parseInt(properties.getProperty("serverPort").trim());
		}catch(Exception e){
			
		}
	}

	
	/**
	 * 在当前线程发送上传命令，在命令发送立即关闭连接
	 * 但是要自己处理IO异常和连接异常CommandParseException
	 * @param serverUpDataParse
	 * @param sendData 发送的数据报文
	 * @return
	 * @throws IOException
	 * @throws PacketException
	 * @throws CommandParseException
	 */
	public synchronized byte[] sendbyteUpCommand( byte[] sendData) throws IOException,PacketException,CommandParseException{
		byte[] returnData = this.submit(sendData);
		
		return returnData;
	}

	private boolean stopReceive = false;
	private byte[] recvAllBuffer = new byte[2048];//接收到的数据缓冲区
	private byte[] recvbuf = new byte[1024];
	private int recvAllBufferIndex = 0;
	private boolean isConnect = false;//是否连接

	/**
	 * 判断连接状态
	 * @return
	 */
	public boolean isConnect(){
		return isConnect;
	}
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
			logger.debug("打开服务器连接异常："+e.getMessage());
			throw e;
		}
		
		return isConnect;
	}
	
	/**
	 *  关闭服务器连接
	 *  connectTimeOut;//连接超时时间
	 *  readTimeOut;//数据发送超时时间
	 */
	public synchronized void closeConnect() throws IOException{
		
		try {
			// 关闭连接
			try {
				in.close();
				in = null;
				out.close();
				out = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				socket.close();
			} catch (Exception ex) {
			}
			socket = null;
			isConnect = false;
		} catch (Exception e) {
			isConnect = false;
		}
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
			if(isConnect){
				logger.debug("发送到服务器数据:"+TypeConversion.byte2hex(sendData));
				out.write(sendData);//发送数据
				out.flush();

			}else{
				return null;
			}
			
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
				logger.debug("接收数据长度:"+recvAllBufferIndex);
			}

			//解析接收到的数据
			if(recvAllBufferIndex>0){ 
				byte[] returnData = new byte[recvAllBufferIndex];
				System.arraycopy(recvAllBuffer, 0, returnData, 0, recvAllBufferIndex);
				logger.debug("接收到服务器下发数据："+TypeConversion.byte2hex(recvbuf,0,recvAllBufferIndex));
				return returnData;
			}else{
				logger.debug("接收到服务器下发数据为空");
				throw new CommandParseException("接收数据为空");
			}
		} catch (IOException e) {
			closeConnect();
			logger.debug("发送数据到服务器异常："+e.getMessage());
			throw e;
		}catch(CommandParseException ex){
			throw ex;
		}	
	}
	static int i=0;
	public static void main(String[] args){
		if(args.length>0&&args[0]!=null)
			SERVER_CONFIG = args[0];
		testBussness(1);
     	//testBussness(2);
		//testBussness(3);
		//testBussness(4);
	}
	
	public static void testBussness(final int type){
		//签到报文
		final byte[] bufferLongin = TypeConversion.hexStringToByte("00406000000090010008002038010000C000089900000006461521190518000932303130303630313130343131303034353131303031320009303030303031303031");
		//订单查询报文
		final byte[] bufferQuery = TypeConversion.hexStringToByte("00406000000090010002002020058000C080113400000006470012000914323031303036303131303431313030343531313030313201560001314B351EF66F8F3B22");
		//交易报文
		final byte[] bufferPay = TypeConversion.hexStringToByte("00B56000000090010002007038058030C0909916439225831321973700000000000010002300064815220105180021000914324392258313219737D100310114951903000032303130303630313130343131303034353131303031320156C235A8258585B85E0051317CB2E2CAD4B1A3B5A57CB2E2CAD4B1A3B5A5B5D8D6B77CB2E2CAD4B1A3B5A5C1AACFB5B7BDCABD3132337C313030302E32330001310015303030303031303031303030303434F1E2667D0288CFEC");
		//交易回执报文
		final byte[] bufferPayCheck = TypeConversion.hexStringToByte("006F6000000090010008002038018008C400098800000006531524370518000914303030303030303030303932323031303036303131303431313030343531313030313200240000000232300030000430323030003100063030303030300009303030303031303031A63505D07550242D");
		
		for(i=0;i<1;i++){
				final int times = i;
				try{
					Thread.sleep(100);
				}catch(Exception e){
					e.printStackTrace();
				}
				new Thread(){
					public void run(){
						try{
							TestSocket commandControl = new TestSocket();
							commandControl.connect(60000, 60000);
							byte[] buffer2 = commandControl.submit(bufferLongin);
							if(buffer2!=null)
								logger.info("测试连接成功");
							else
								logger.info("测试连接失败");
							commandControl.closeConnect();
						}catch(Exception e){
							logger.info("测试连接失败："+e.getMessage());
						}
					}
				}.start();
		}
	}


}
