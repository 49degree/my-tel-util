package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;

public class SkyeyeSocketClient implements Runnable {
	public static String SERVER_CONFIG = "server.properties";
	protected String mHost;
	protected int mPort;
	
	protected SocketChannel mSocketChannel;
	// 信道选择器
	private Selector selector;
	
	private Set<SelectionKey>  keys ; 
	private Iterator<SelectionKey> it;
	private SelectionKey key;
	private ByteBuffer readByteBuffer = ByteBuffer.allocate(8);
	
	/**
	 * 构造方法
	 */
	private SkyeyeSocketClient(){
		try{
			Properties properties = new Properties();
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ SERVER_CONFIG));
			mHost = "localhost";
			mPort = Integer.parseInt(properties.getProperty("serverPort").trim());
			
			mHost = "113.106.89.91";
			mPort = 4015;
		}catch(Exception e){
			
		}
	}

	byte[] buffer;
	int connectType = 0;
	
	public void submit(byte[] buffer,SocketReturnData socketReturnData){
		this.buffer = buffer;
		InetSocketAddress address = new InetSocketAddress(mHost, mPort) ;
		try {
			selector = Selector.open();
			//测试不同连接方式
			switch(connectType){
			case 0:{
				//方式一
				mSocketChannel = SocketChannel.open(address);
				mSocketChannel.configureBlocking(false);
				mSocketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE).attach(socketReturnData);
				break;
			}
			case 1:{
				//方式二
				mSocketChannel = SocketChannel.open();
				mSocketChannel.connect(address);
				//如果使用mSocketChannel.connect(address)方式进行连接，
				//那么configureBlocking(false)应该在connect(address)后调用才会立即进行TCP连接
				mSocketChannel.configureBlocking(false);
				mSocketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE).attach(socketReturnData);
				break;
			}
			case 2:{
				//方式三
				mSocketChannel = SocketChannel.open();
				mSocketChannel.configureBlocking(false);
				mSocketChannel.connect(address);
				//如果使用mSocketChannel.connect(address)方式进行连接，
				//configureBlocking(false)在connect(address)前面调用，则需要监听连接KEY，
				//在获得连接KEY时调((SocketChannel)key.channel()).finishConnect();
				mSocketChannel.register(selector, SelectionKey.OP_CONNECT,socketReturnData);
				break;
			}
			}
			System.out.println("connect+++++++++++++++"+mSocketChannel.socket().isConnected());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("开始连接+++++++++++++++");
		boolean toSelect = true;
		while(toSelect){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				if(selector.select()<1){//更新监听信息，一定要调用
					continue;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			keys = selector.selectedKeys();
			it = keys.iterator();
			System.out.println("遍历keys+++++++++++++++:"+keys.size());
			while(it.hasNext()){
				key = it.next();
				it.remove();
				if(key.isConnectable()){
					System.out.println("遍历keys+++++++++++++++ isConnectable:");
					try {
						((SocketChannel)key.channel()).finishConnect();
						SelectionKey tempKey = ((SocketChannel)key.channel()).register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,key.attachment());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(key.isReadable()){
					System.out.println("遍历keys+++++++++++++++ isReadable:");
					try {
						readByteBuffer.rewind();
						if(((SocketChannel)key.channel()).read(readByteBuffer)<0 || 
								((SocketReturnData)key.attachment()).returnData(readByteBuffer)){
							toSelect = false;
							break;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						((SocketReturnData)key.attachment()).returnDataFailure();
						break;
					}
				}else if(key.isWritable()){
					System.out.println("遍历keys+++++++++++++++ isWritable:");
					try {
						System.out.println("发送到服务器数据:"+((SocketChannel)key.channel()).write(ByteBuffer.wrap(buffer))+":"+TypeConversion.byte2hex(buffer,0,buffer.length));
						((SocketChannel)key.channel()).register(selector, SelectionKey.OP_READ,key.attachment());
					} catch (ClosedChannelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						toSelect = false;
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						toSelect = false;
						break;
					}
				}
			}
			
		}
		try {
			mSocketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public interface SocketReturnData{
		public abstract boolean returnData(ByteBuffer returnBuffer);
		public abstract void returnDataFailure();
	}
	
	private static class SocketReturnDataImpl implements SocketReturnData{
		byte[] buffer = null;
		int readLen = 0;

		@Override
		public void returnDataFailure() {
			// TODO Auto-generated method stub
			System.out.println("测试连接失败");
		}

		@Override
		public boolean returnData(ByteBuffer returnBuffer) {
			// TODO Auto-generated method stub
			if(buffer == null){
				readLen = 0;
				buffer = new byte[returnBuffer.position()];
				// 将读到的数据插入全局数据缓冲区 
				//填充数据到缓存
				
			}
			buffer = Utils.insertEnoughLengthBuffer(buffer, readLen, returnBuffer.array(), 0, returnBuffer.position(), 512);
			readLen += returnBuffer.position();
			System.out.println("接收到服务器下发数据："+TypeConversion.byte2hex(buffer,0,readLen));
			if(readLen<2)
				return false;
			//包前两个字节 是包长度, 高位在前，低位在后 ，判断收到数据是否已经收完
			if(TypeConversion.bytesToShortEx(buffer, 0)<=readLen-2){
				System.out.println("测试连接成功");
				buffer = null;
				return true;
			}else{
				return false;
			}
		}
		
	};
	
	
	public static void testBussness(final int type){
		//签到报文
		final byte[] bufferLongin = TypeConversion.hexStringToByte("00406000000090010008002038010000C000089900000006461521190518000932303130303630313130343131303034353131303031320009303030303031303031");
		//订单查询报文
		final byte[] bufferQuery = TypeConversion.hexStringToByte("00406000000090010002002020058000C080113400000006470012000914323031303036303131303431313030343531313030313201560001314B351EF66F8F3B22");
		//交易报文
		final byte[] bufferPay = TypeConversion.hexStringToByte("00B56000000090010002007038058030C0909916439225831321973700000000000010002300064815220105180021000914324392258313219737D100310114951903000032303130303630313130343131303034353131303031320156C235A8258585B85E0051317CB2E2CAD4B1A3B5A57CB2E2CAD4B1A3B5A5B5D8D6B77CB2E2CAD4B1A3B5A5C1AACFB5B7BDCABD3132337C313030302E32330001310015303030303031303031303030303434F1E2667D0288CFEC");
		//交易回执报文
		final byte[] bufferPayCheck = TypeConversion.hexStringToByte("006F6000000090010008002038018008C400098800000006531524370518000914303030303030303030303932323031303036303131303431313030343531313030313200240000000232300030000430323030003100063030303030300009303030303031303031A63505D07550242D");
		final SkyeyeSocketClient commandControl = new SkyeyeSocketClient();
		commandControl.submit(bufferLongin, new SocketReturnDataImpl());
		new Thread(commandControl).start();
	}
	
	public static void main(String[] args){
		if(args.length>0&&args[0]!=null)
			SERVER_CONFIG = args[0];
		testBussness(1);
//     	testBussness(2);
//		testBussness(3);
//		testBussness(4);
		
	}
}
