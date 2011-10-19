package com.xys.ecg.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;


import org.xml.sax.SAXException;

import android.os.Looper;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.log.Logger;
import com.xys.ecg.utils.TypeConversion;
import com.xys.ecg.file.*;

public class BluetoothControl extends Thread {
	// constructor
	private BluetoothControl(){
		btFactory = BluetoothFactory.getInstance();//传输层
	}	
	
	// singled instance
	private static BluetoothControl instance = new BluetoothControl();	
	
	public static BluetoothControl getInstance(){
		return instance;
	}
	
	// start control thread
	public static void startControlThread(){
		instance.start();
	}
	
	public void setConnectState(boolean b){
		if(!b){
			state = STATE.DISCONNECT;
		}
	}
	
	private Logger log = Logger.getLogger(BluetoothFactory.class);
	private BluetoothFactory btFactory = null;//传输层
	private EcgBusiness business =  EcgBusiness.getInstance();//数据中心实例	
	
	private boolean isStarted = false;	// 是否已经开始过了
	
	// 业务逻辑状态
	private static enum STATE {
		GET_CONFIG,
		GET_CUR_GDIP,
		START,
		GET_OLD_GDIP,
		WAIT,
		DISCONNECT
	}
	
	private byte dataType;//当前数据类型，0x01为当前导联，0x02为触摸历史数据，0x03为导联历史，0x04为当前触摸

	private int dataID = 0;//数据ID
	private long dataTime = 0;//第一个数据包的时间
	private byte channel; //通道号
	boolean isFirstData = false;//是否是第一个包	
	
	private STATE state;		// 当前状态
	private boolean bIsWait = false;//是否待待，为true时等待，为false时唤醒
	private ArrayList<Byte> packet = new ArrayList<Byte>();		// 分析缓冲区
	private byte[] dataBuf = new byte[1024];	// 业务数据缓冲区
	
	
	// 收到数据就进行同步解析，异步的话要用缓冲区缓存
	public void  savePacket(byte[] packet, int len, byte channel){			//传输层调用		
		try{
			System.arraycopy(packet, 0, dataBuf, 0, len);
		}catch(Exception e){
			log.debug("copy failed" + e.getMessage());
		}
		
		this.channel = channel;
		
		parse();
	}
	
	/**
	 * 开始通信
	 * @return
	 */
	public  boolean startcomm(){
		if(!btFactory.isConnected()){
			log.debug("no connection");
			return false;
		}

		// 记录点击开始的状态，并开始获取配置传输数据
		isStarted = true;
		state = STATE.GET_CONFIG;
		
		return true;
	}
	
	//解析包,判断是什么类型的包，解析的过程 中不允许对数据进行存取
	public synchronized void  parse (){
		try{
			// 配置应答
			if(dataBuf[0] == 0x02){ //设备信息
				// 改变业务控制状态
				state = STATE.GET_CUR_GDIP;//请求当前数据
				getVersion();
				
				log.debug("recv config response");
				return;
			}
			
			// GDIP应答
			if(dataBuf[0] == 0x04){	
				//  获取到ID及时间						
				state = STATE.START;
				getGDIP();
				
				packet.clear();
				log.debug("recv gdip response");
				return;
			}
			
			// 数据
			if(dataBuf[0] == 0x06){
				//  解析业务数据包，并发往数据中心
				boolean isHisData =  false;
							
				
				//通道1为当前数据，2为历史数据
				if(channel == 0x01){
					isHisData = false;  
				}else if(channel == 0x02){
					isHisData =  true;
				}else{
					//错误
				}
				
				if(isFirstData)
				{
					// 发送start命令后的第一个数据包
					EcgDataEntity ecgDataEntity = new EcgDataEntity(dataBuf, true, TypeConversion.longToBytes(dataTime), isHisData);
					business.insertEcgData(ecgDataEntity);//发往数据中心
					 
					isFirstData = false;//第一个数据发送完之后，就将基置为false
				}
				else
				{
					// 普通数据包，没有时间
					EcgDataEntity ecgDataEntity = new EcgDataEntity(dataBuf, false, null, isHisData);
					business.insertEcgData(ecgDataEntity);//发往数据中心
				}
			   
			}
		}catch(Exception e){
			log.debug("exception" + e.getMessage());
		}
	}
	
	// 获取版本号
	public void getVersion(){
		byte[] temp = new byte[4];	
		
		temp[0] = dataBuf[2];
		temp[1] = dataBuf[3];
		temp[2] = dataBuf[4];
		temp[3] = dataBuf[5];
	    //写配置文件
		String strVersion = null;
		strVersion = "V"+temp[0]+"."+temp[1]+"."+temp[2]+"."+temp[3]+"";
		EcgXmlFileOperate xmlOperate =  new EcgXmlFileOperate("ECGConfig",ECGApplication.getInstance());
		try {
			xmlOperate.updateEcgXmlCurrentNode("Version", strVersion);
			xmlOperate.close();
		} catch (Exception e) {
			log.debug("Write configFile failed ");
			e.printStackTrace();
		}
		try{
//			EcgXmlFileOperate xml();
		}catch(Exception e){
			
		}
		
		temp[0] = dataBuf[6];
		temp[1] = dataBuf[7];
		temp[2] = dataBuf[8];
		temp[3] = dataBuf[9];
		int hardVersion = TypeConversion.bytesToInt(temp, 0);
		
		
		// TODO: write to config file
	}
	
	// 获取GIDP应答信息
	public void getGDIP(){
		byte[] temp = new byte[4];
		
		dataType = dataBuf[1];
		
		temp[0] = dataBuf[2];
		temp[1] = dataBuf[3];
		temp[2] = dataBuf[4];
		temp[3] = dataBuf[5];
		dataID = TypeConversion.bytesToInt(temp, 0);
		log.debug("start id:"+ dataID);
		
		byte[] time = new byte[8]; 
		time[0] = dataBuf[6];
		time[1] = dataBuf[7];
		time[2] = dataBuf[8];
		time[3] = dataBuf[9];
		time[4] = 0;
		time[5] = 0;
		time[6] = 0;
		time[7] = 0;
		dataTime = TypeConversion.bytesToLong(time, 0);
		log.debug("start time:"+ dataTime);
	}
	
    // 发送”配置命令“,获取配置
    public boolean sendcfg(){	
    	long ltime = (new Date().getTime())/1000;
    	int timeSecond = (int)ltime;
   	
    	byte[] time = new byte[4];
    	time = TypeConversion.intToBytes(timeSecond);//返回当前时间，到1970年1月1日的秒数，为4个字节
    	
    	byte[] config = new byte[8]; 
    	config[0] = 0x01;
    	config[1] = time[0];
    	config[2] = time[1];
    	config[3] = time[2];
    	config[4] = time[3];
    	config[5] = 0; //间隔时间为1分钟
    	config[6] = 0; //清空数据
    	config[7] = 0;	// 保留
    	
    	boolean bRet = false;
    	try{
    		if(btFactory == null){
    			btFactory = BluetoothFactory.getInstance();
    		}

    		bRet = btFactory.send(config, 8); // 发给采集器
    	}catch(Exception e){
    		// 
    		log.debug("send data error" + e.getMessage());
    		bRet = false;
    	}
    	
    	return bRet;
    }
    
    // 发送当前GDIP命令
    public boolean sendCurGDIP(){ 	
    	try{
    		Thread.sleep(500);
    	}catch(Exception e){
    		
    	}
    	
    	byte[] current =  new byte[2];
    	current[0] = 0x03;
    	current[1] = 0x01;
    	
    	// 调用传输层发送功能
    	return btFactory.send(current, 2);
    }
    
    // 发送START命令
    public boolean sendStart()
    {
    	byte[] start = new byte[5];
    	start[0] = 0x05;
    	start[1] = TypeConversion.intToBytes(dataID)[0];
    	start[2] = TypeConversion.intToBytes(dataID)[1];
    	start[3] = TypeConversion.intToBytes(dataID)[2];
    	start[4] = TypeConversion.intToBytes(dataID)[3];  	
    	
    	// 调用传输层发送功能
    	isFirstData = true; //发开始命令前标记下一个包为第一个数据包
    	
    	return btFactory.send(start, 5);
    }
    
    // 业务流程控制
	public void run() {
		while (true) {
			// 获取蓝牙控制对象，无对象则sleep 2s再检测
			if (btFactory == null) {
				btFactory = BluetoothFactory.getInstance();

				if (btFactory == null) {
					try {
						Thread.sleep(2000);
						continue;
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}

			// 检测是否连接，无连接则sleep 2s再检测
			if (!btFactory.isConnected()) {
				// 连接失败则 state 回复到初始状态
				state = STATE.WAIT;

				try {
					Thread.sleep(2000);				

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				continue;

			}// end if

			// 业务流程控制
//			if(isStarted){
				// 如果用户已经点击过开始按钮，那么只要一连接上采集器就开始传输数据
//				state = STATE.GET_CONFIG;
//			}
			
			switch (state) {
			case GET_CONFIG:				
				if (sendcfg()) {
					// 进入wait状态，等待采集器的响应
					log.debug("send config command, waiting response");
					state = STATE.WAIT;
				}

				break;

			case GET_CUR_GDIP:
				if (sendCurGDIP()) {
					// 进入wait状态，等待采集器的响应
					log.debug("send get current gdip command, waiting response");
					state = STATE.WAIT;
				}
				break;

			case START:
				// TODO: wait user click start button
				if (sendStart()) {
					log.debug("send start command, waiting ecg data");
					state = STATE.WAIT;
				}
				break;

			case GET_OLD_GDIP: // 历史数据
				break;

			case WAIT:
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					log.debug(e.getMessage());
				}
				break;
				
			case DISCONNECT:
				break;

			default:
				break;

			}// end switch
		}// end while	
	}//end run 
}
