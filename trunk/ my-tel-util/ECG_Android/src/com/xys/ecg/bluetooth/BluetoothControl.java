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
		btFactory = BluetoothFactory.getInstance();//�����
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
	private BluetoothFactory btFactory = null;//�����
	private EcgBusiness business =  EcgBusiness.getInstance();//��������ʵ��	
	
	private boolean isStarted = false;	// �Ƿ��Ѿ���ʼ����
	
	// ҵ���߼�״̬
	private static enum STATE {
		GET_CONFIG,
		GET_CUR_GDIP,
		START,
		GET_OLD_GDIP,
		WAIT,
		DISCONNECT
	}
	
	private byte dataType;//��ǰ�������ͣ�0x01Ϊ��ǰ������0x02Ϊ������ʷ���ݣ�0x03Ϊ������ʷ��0x04Ϊ��ǰ����

	private int dataID = 0;//����ID
	private long dataTime = 0;//��һ�����ݰ���ʱ��
	private byte channel; //ͨ����
	boolean isFirstData = false;//�Ƿ��ǵ�һ����	
	
	private STATE state;		// ��ǰ״̬
	private boolean bIsWait = false;//�Ƿ������Ϊtrueʱ�ȴ���Ϊfalseʱ����
	private ArrayList<Byte> packet = new ArrayList<Byte>();		// ����������
	private byte[] dataBuf = new byte[1024];	// ҵ�����ݻ�����
	
	
	// �յ����ݾͽ���ͬ���������첽�Ļ�Ҫ�û���������
	public void  savePacket(byte[] packet, int len, byte channel){			//��������		
		try{
			System.arraycopy(packet, 0, dataBuf, 0, len);
		}catch(Exception e){
			log.debug("copy failed" + e.getMessage());
		}
		
		this.channel = channel;
		
		parse();
	}
	
	/**
	 * ��ʼͨ��
	 * @return
	 */
	public  boolean startcomm(){
		if(!btFactory.isConnected()){
			log.debug("no connection");
			return false;
		}

		// ��¼�����ʼ��״̬������ʼ��ȡ���ô�������
		isStarted = true;
		state = STATE.GET_CONFIG;
		
		return true;
	}
	
	//������,�ж���ʲô���͵İ��������Ĺ��� �в���������ݽ��д�ȡ
	public synchronized void  parse (){
		try{
			// ����Ӧ��
			if(dataBuf[0] == 0x02){ //�豸��Ϣ
				// �ı�ҵ�����״̬
				state = STATE.GET_CUR_GDIP;//����ǰ����
				getVersion();
				
				log.debug("recv config response");
				return;
			}
			
			// GDIPӦ��
			if(dataBuf[0] == 0x04){	
				//  ��ȡ��ID��ʱ��						
				state = STATE.START;
				getGDIP();
				
				packet.clear();
				log.debug("recv gdip response");
				return;
			}
			
			// ����
			if(dataBuf[0] == 0x06){
				//  ����ҵ�����ݰ�����������������
				boolean isHisData =  false;
							
				
				//ͨ��1Ϊ��ǰ���ݣ�2Ϊ��ʷ����
				if(channel == 0x01){
					isHisData = false;  
				}else if(channel == 0x02){
					isHisData =  true;
				}else{
					//����
				}
				
				if(isFirstData)
				{
					// ����start�����ĵ�һ�����ݰ�
					EcgDataEntity ecgDataEntity = new EcgDataEntity(dataBuf, true, TypeConversion.longToBytes(dataTime), isHisData);
					business.insertEcgData(ecgDataEntity);//������������
					 
					isFirstData = false;//��һ�����ݷ�����֮�󣬾ͽ�����Ϊfalse
				}
				else
				{
					// ��ͨ���ݰ���û��ʱ��
					EcgDataEntity ecgDataEntity = new EcgDataEntity(dataBuf, false, null, isHisData);
					business.insertEcgData(ecgDataEntity);//������������
				}
			   
			}
		}catch(Exception e){
			log.debug("exception" + e.getMessage());
		}
	}
	
	// ��ȡ�汾��
	public void getVersion(){
		byte[] temp = new byte[4];	
		
		temp[0] = dataBuf[2];
		temp[1] = dataBuf[3];
		temp[2] = dataBuf[4];
		temp[3] = dataBuf[5];
	    //д�����ļ�
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
	
	// ��ȡGIDPӦ����Ϣ
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
	
    // ���͡��������,��ȡ����
    public boolean sendcfg(){	
    	long ltime = (new Date().getTime())/1000;
    	int timeSecond = (int)ltime;
   	
    	byte[] time = new byte[4];
    	time = TypeConversion.intToBytes(timeSecond);//���ص�ǰʱ�䣬��1970��1��1�յ�������Ϊ4���ֽ�
    	
    	byte[] config = new byte[8]; 
    	config[0] = 0x01;
    	config[1] = time[0];
    	config[2] = time[1];
    	config[3] = time[2];
    	config[4] = time[3];
    	config[5] = 0; //���ʱ��Ϊ1����
    	config[6] = 0; //�������
    	config[7] = 0;	// ����
    	
    	boolean bRet = false;
    	try{
    		if(btFactory == null){
    			btFactory = BluetoothFactory.getInstance();
    		}

    		bRet = btFactory.send(config, 8); // �����ɼ���
    	}catch(Exception e){
    		// 
    		log.debug("send data error" + e.getMessage());
    		bRet = false;
    	}
    	
    	return bRet;
    }
    
    // ���͵�ǰGDIP����
    public boolean sendCurGDIP(){ 	
    	try{
    		Thread.sleep(500);
    	}catch(Exception e){
    		
    	}
    	
    	byte[] current =  new byte[2];
    	current[0] = 0x03;
    	current[1] = 0x01;
    	
    	// ���ô���㷢�͹���
    	return btFactory.send(current, 2);
    }
    
    // ����START����
    public boolean sendStart()
    {
    	byte[] start = new byte[5];
    	start[0] = 0x05;
    	start[1] = TypeConversion.intToBytes(dataID)[0];
    	start[2] = TypeConversion.intToBytes(dataID)[1];
    	start[3] = TypeConversion.intToBytes(dataID)[2];
    	start[4] = TypeConversion.intToBytes(dataID)[3];  	
    	
    	// ���ô���㷢�͹���
    	isFirstData = true; //����ʼ����ǰ�����һ����Ϊ��һ�����ݰ�
    	
    	return btFactory.send(start, 5);
    }
    
    // ҵ�����̿���
	public void run() {
		while (true) {
			// ��ȡ�������ƶ����޶�����sleep 2s�ټ��
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

			// ����Ƿ����ӣ���������sleep 2s�ټ��
			if (!btFactory.isConnected()) {
				// ����ʧ���� state �ظ�����ʼ״̬
				state = STATE.WAIT;

				try {
					Thread.sleep(2000);				

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				continue;

			}// end if

			// ҵ�����̿���
//			if(isStarted){
				// ����û��Ѿ��������ʼ��ť����ôֻҪһ�����ϲɼ����Ϳ�ʼ��������
//				state = STATE.GET_CONFIG;
//			}
			
			switch (state) {
			case GET_CONFIG:				
				if (sendcfg()) {
					// ����wait״̬���ȴ��ɼ�������Ӧ
					log.debug("send config command, waiting response");
					state = STATE.WAIT;
				}

				break;

			case GET_CUR_GDIP:
				if (sendCurGDIP()) {
					// ����wait״̬���ȴ��ɼ�������Ӧ
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

			case GET_OLD_GDIP: // ��ʷ����
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
