package com.xys.ecg.upload;
import java.io.File;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.bean.UploadEcgDataHeader;
import com.xys.ecg.log.Logger;
import com.xys.ecg.network.SoapTool;
import com.xys.ecg.network.SoapUploadDataTool;
import com.xys.ecg.utils.TimesUtils;
import com.xys.ecg.utils.TypeConversion;
import com.xys.ecg.utils.ZLibUtils;

/**
 * 上传工具类
 * @author Administrator
 *
 */
public class UploadEcgInfoTool {
	public static Logger logger = Logger.getLogger(UploadEcgInfoTool.class);
	/**
	 * 上传指定的心电数据文件
	 * 
	 * 在这里读取文件为可通过SoapTool上传的数据
	 * @param filePath 文件路径
	 * fileName 文件名称
	 * @return
	 */
	public static boolean sendEcgFile(String filePath,String fileName,Handler mainEventHandler){
		//读取文件
		try{
			if(SoapTool.checkInternet(mainEventHandler, "没有可用的网络，取消上传心电数据!")){//检测网络是否可用
				File file = new File(filePath+"/"+fileName);
				FileInputStream fin = new FileInputStream(file);
				byte[] fileBytes = new byte[(int)file.length()];
				if(fin!=null){
					fin.read(fileBytes);
				}
				
				//读出4块文件的长度
//				if(fileBytes!=null){ 
//					int ecgLength = TypeConversion.bytesToInt(fileBytes, 4);
//					int accX = TypeConversion.bytesToInt(fileBytes, 12+ecgLength);
//					int accY = TypeConversion.bytesToInt(fileBytes, 20+ecgLength+accX);
//					int accZ = TypeConversion.bytesToInt(fileBytes, 28+ecgLength+accX+accY);
//					logger.info("fileBytes:"+ecgLength+":"+accX+":"+accY+":"+accZ+":");
//				}
				
				
				
				
				
				
				UploadEcgDataHeader uploadEcgDataHeader = new UploadEcgDataHeader();
				uploadEcgDataHeader.setRqSendBusinessCode(TypeConversion.shortToBytes((short)0x0001));
				uploadEcgDataHeader.setRqSendFunctionCode(TypeConversion.shortToBytes((short)0x0002));
				String dataBeginTime = file.getName().substring(0, 14);//文件名称即采集开始时间
				byte[] dataBeginTimeBytes = TypeConversion.longToBytes(TimesUtils.string2TimeMill(dataBeginTime, "yyyyMMddHHmmss")/1000);
				uploadEcgDataHeader.setDataBeginTime(dataBeginTimeBytes);//8bytes	心电数据开始采集的时间
				
				byte[] ecgDataPacketBytes = UploadEcgInfoTool.getEcgPacketBytes(fileBytes, uploadEcgDataHeader);//构造心电数据包 
				//private UploadEcgDataHeader uploadEcgDataHeader = null;//心电数据头对象 ,通过该变量区分是  心电事件及其数据上传or心电数据上传
				new Thread(new SoapUploadDataTool(ecgDataPacketBytes,mainEventHandler,uploadEcgDataHeader)).start();//启动上传数据线程
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
		}
		
		
		
		return true;
	}
	
	/**
	 * 上传指定的心电数据包
	 * 
	 * 在这里读取文件为可通过SoapTool上传的数据
	 * @param ecgDataEntity 待上传心电数据包
	 * @return
	 */
	public static boolean sendEcgData(List<EcgDataEntity> ecgDataList,UploadEcgDataHeader uploadEcgDataHeader,Handler mainEventHandler){
		if(SoapTool.checkInternet(mainEventHandler, "没有可用的网络，取消上传心电数据!")){//检测网络是否可用
			byte[] ecgDataPacketBytes = null;
			if(ecgDataList!=null){//如果数据列表不为空，则获取心电数据包
				ecgDataPacketBytes = UploadEcgInfoTool.getEcgDataPacketBytes(ecgDataList);//获取ECG,ACC_X,ACC_Y,ACC_Z数据包
				logger.debug("ecgDataPacketBytes:"+ecgDataPacketBytes.length);
			}
			//uploadEcgDataHeader.setDataBeginTime(ecgDataList.get(0).dataPacketTime);//8bytes	心电数据开始采集的时间
			ecgDataPacketBytes = UploadEcgInfoTool.getEcgPacketBytes(ecgDataPacketBytes, uploadEcgDataHeader);//构造心电数据包 
			//private UploadEcgDataHeader uploadEcgDataHeader = null;//心电数据头对象 ,通过该变量区分是  心电事件及其数据上传or心电数据上传
			new Thread(new SoapUploadDataTool(ecgDataPacketBytes,mainEventHandler,uploadEcgDataHeader)).start();//启动上传数据线程
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 *  构造心电数据包 packetBytes
	 *  ecgDataHeaderBytes为整个心电数据的包头，标识《 心电数据上传 或者 心电事件及其数据上传》
	 * @return
	 */
	public static byte[] getEcgPacketBytes(byte[] ecgDataPacketBytes,UploadEcgDataHeader uploadEcgDataHeader){
		

		/*
		 * 构造心电数据包头信息 根据uploadEcgDataHeader对象的不同可以构造 《 心电数据上传和心电事件及其数据上传》
		 * ecgDataHeaderBytes位整个心电数据的包头，标识《 心电数据上传 或者 心电事件及其数据上传》
		 */
		//int allSendDataLength = ecgDataPacketBytes.length+uploadEcgDataHeader.getHeaderLength();
		
		int allSendDataLength = uploadEcgDataHeader.getHeaderLength();
		if(ecgDataPacketBytes!=null){//判断数据包是否为空 
			allSendDataLength += ecgDataPacketBytes.length;
		}
		
		logger.debug("getEcgPacketBytes allSendDataLength:"+(ecgDataPacketBytes==null?0:ecgDataPacketBytes.length)+"getHeaderLength:"+uploadEcgDataHeader.getHeaderLength());
		
		byte[] ecgByteData = new byte[allSendDataLength];
		
		uploadEcgDataHeader.setDataLenght(TypeConversion.intToBytes(
				allSendDataLength-uploadEcgDataHeader.getHeaderLength()));//4byte数据长度,不包含数据头
		byte[] ecgDataHeaderBytes = uploadEcgDataHeader.getEcgDataHeaderBytes();//获取心电数据包头信息
		System.arraycopy(ecgDataHeaderBytes,0 , ecgByteData,0 , ecgDataHeaderBytes.length);//COPY 心电数据包头信息
		
		if(ecgDataPacketBytes!=null){//判断数据包是否为空 ，如果为空则只包含数据包头
			System.arraycopy(ecgDataPacketBytes,0 , ecgByteData,ecgDataHeaderBytes.length , ecgDataPacketBytes.length);//COPY ECG,ACC_X,ACC_Y,ACC_Z数据包
			ecgDataPacketBytes = null;
		}
		
		logger.info("ecgByteData length1:"+ecgByteData.length);
		ecgByteData = ZLibUtils.compress(ecgByteData);
		allSendDataLength = ecgByteData.length;
		logger.info("ecgByteData length2:"+ecgByteData.length);
		
		//上传心电数据单元 平台定义
		byte[] rqSendBusinessCode =uploadEcgDataHeader.getRqSendBusinessCode();//请求业务代码(2bytes)
		byte[] rqSendFunctionCode =uploadEcgDataHeader.getRqSendFunctionCode();//请求功能代码(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(allSendDataLength);//请求数据内容长度(4bytes)
		
		byte[] packetBytes = new byte[allSendDataLength+8];//请求数据内容长度
		System.arraycopy(rqSendBusinessCode, 0, packetBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, packetBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, packetBytes, 4, 4);
		System.arraycopy(ecgByteData, 0, packetBytes, 8, allSendDataLength);
		return packetBytes;
	}
	/**
	 * 解析心电数据并返回ECG,ACC_X,ACC_Y,ACC_Z数据包
	 * 数据段1
	 * 数据段2
	 * 。。。。。。
	 * 3.5 数据段格式
	 * 4bytes	数据类型
	 * 4bytes	数据长度(n)
	 * (n)bytes	数据
	 * 3.6数据类型格式
	 * 数据类型4byte
	 * 数据类别DataCategory
	 * (1byte)	MDF
	 * (1Bit)	Reserved
	 * (5Bit)	Channels
	 * (18Bit)
	 * 数据格式详细说明请看《WebService手机端通信接口说明书》3.4数据包格式和3.5 数据段格式
	 * @return ECG,ACC_X,ACC_Y,ACC_Z数据包
	 */
	public static byte[] getEcgDataPacketBytes(List<EcgDataEntity> ecgDataList){
		//组装需要上传的心电数据对象
		List<byte[]> ecgPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
		List<byte[]> accXPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
		List<byte[]> accYPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
		List<byte[]> accZPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
		int ecgPacketByteLength = 0;
		int accXPacketByteLength = 0;
		int accYPacketByteLength = 0;
		int accZPacketByteLength = 0;
		
		for(EcgDataEntity ecgDataEntity:ecgDataList){
			//传心电数据
			byte[] ecgPacket = ecgDataEntity.getEcgPacket().getEcgData();//ECG_PACKET心电数据
			byte[] accXPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_X_PACKET心电数据
			byte[] accYPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Y_PACKET心电数据
			byte[] accZPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Z_PACKET心电数据
			
			ecgPacketByteLength+=ecgPacket.length;
			accXPacketByteLength+=accXPacket.length;
			accYPacketByteLength+=accYPacket.length;
			accZPacketByteLength+=accZPacket.length;
			
			ecgPacketByteList.add(ecgPacket);
			accXPacketByteList.add(accXPacket);
			accYPacketByteList.add(accYPacket);
			accZPacketByteList.add(accZPacket);
		}
		
		byte[] ecgPacketBytes = new byte[ecgPacketByteLength+8];
		byte[] accXPacketBytes = new byte[accXPacketByteLength+8];
		byte[] accYPacketBytes = new byte[accYPacketByteLength+8];
		byte[] accZPacketBytes = new byte[accZPacketByteLength+8];
		ecgPacketByteLength = 8;
		accXPacketByteLength = 8;
		accYPacketByteLength = 8;
		accZPacketByteLength = 8;
		
		int step = 0;
		int ecgDataListLength = ecgDataList.size();
		while(step<ecgDataListLength){
			System.arraycopy(ecgPacketByteList.get(step),0 , ecgPacketBytes,ecgPacketByteLength , ecgPacketByteList.get(step).length);
			System.arraycopy(accXPacketByteList.get(step),0 , accXPacketBytes,accXPacketByteLength , accXPacketByteList.get(step).length);
			System.arraycopy(accYPacketByteList.get(step),0 , accYPacketBytes,accYPacketByteLength , accYPacketByteList.get(step).length);
			System.arraycopy(accZPacketByteList.get(step),0 , accZPacketBytes,accZPacketByteLength , accZPacketByteList.get(step).length);
			
			ecgPacketByteLength+=ecgPacketByteList.get(step).length;
			accXPacketByteLength+=accXPacketByteList.get(step).length;
			accYPacketByteLength+=accYPacketByteList.get(step).length;
			accZPacketByteLength+=accZPacketByteList.get(step).length;
			step++;
		}

		/*
		数据类型4byte
		数据类别DataCategory(1byte)	
		MDF(1Bit)	 DF(MutilChannel Data Format)，多通道(或多导联)数据排列方式标志位，当该位为0时表示多通道的数据是连续的，当该位为1时表时多通道的数据是交错的
		Reserved(5Bit)	Reserved，4位保留，第一个bit用于标识数据类型（心电数据/触摸式数据），其置0表示心电数据类型，置1表示触摸式数据类型
		Channels(18Bit) 数据类型
		ecgPacketHeader包含ECG,ACC_X,ACC_Y,ACC_Z的包头信息
		*/
		byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataList.get(0));
		//以下为构造ECG包头  数据类别1byte 数据类型格式3bytes
		System.arraycopy(ecgPacketHeader[0],0 , ecgPacketBytes,0 , 4);//4bytes	数据类型
		System.arraycopy(TypeConversion.intToBytes(ecgPacketByteLength-8),0 , ecgPacketBytes,4 , 4);//4bytes	数据长度(n)
		//以下为构造ACC_X包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		System.arraycopy(ecgPacketHeader[1],0 , accXPacketBytes,0 , 4);//4bytes	数据类型
		System.arraycopy(TypeConversion.intToBytes(accXPacketByteLength-8),0 , accXPacketBytes,4 , 4);//4bytes	数据长度(n)

		//以下为构造ACC_Y包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		System.arraycopy(ecgPacketHeader[2],0 , accYPacketBytes,0 , 4);//4bytes	数据类型
		System.arraycopy(TypeConversion.intToBytes(accYPacketByteLength-8),0 , accYPacketBytes,4 , 4);//4bytes	数据长度(n)
		
		//以下为构造ACC_Z包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		System.arraycopy(ecgPacketHeader[3],0 , accZPacketBytes,0 , 4);//4bytes	数据类型
		System.arraycopy(TypeConversion.intToBytes(accZPacketByteLength-8),0 , accZPacketBytes,4 , 4);//4bytes	数据长度(n)
		
		//单个需要上传的心电数据包 12为   8bytes	心电数据开始采集的时间和4byte数据长度
		int allSendDataLength = 0;//上传的心电数据包集合的长度
		byte[] ecgByteData = new byte[ecgPacketByteLength+accXPacketByteLength+accYPacketByteLength+accZPacketByteLength];
		System.arraycopy(ecgPacketBytes,0 , ecgByteData,allSendDataLength , ecgPacketByteLength);//COPY ecg data Packet
		allSendDataLength+=ecgPacketByteLength;
		System.arraycopy(accXPacketBytes,0 , ecgByteData,allSendDataLength , accXPacketByteLength);//COPY accX data Packet
		allSendDataLength+=accXPacketByteLength;
		System.arraycopy(accYPacketBytes,0 , ecgByteData,allSendDataLength , accYPacketByteLength);//COPY accy data Packet
		allSendDataLength+=accYPacketByteLength;
		System.arraycopy(accZPacketBytes,0 , ecgByteData,allSendDataLength , accZPacketByteLength);//COPY accz data Packet
		allSendDataLength+=accZPacketByteLength;
		
		return ecgByteData;
	}
	
	
	
	/**
	 * 数据类型4byte
	 * 数据类别DataCategory(1byte)	
	 * MDF(1Bit)	 DF(MutilChannel Data Format)，多通道(或多导联)数据排列方式标志位，当该位为0时表示多通道的数据是连续的，当该位为1时表时多通道的数据是交错的
	 * Reserved(5Bit)	Reserved，4位保留，第一个bit用于标识数据类型（心电数据/触摸式数据），其置0表示心电数据类型，置1表示触摸式数据类型
	 * Channels(18Bit) 数据类型
	 * ecgPacketHeader包含ECG,ACC_X,ACC_Y,ACC_Z的包头信息
	 * @param ecgDataEntity
	 * @return
	 */
	public static byte[][] getEcgPacketHeader(EcgDataEntity ecgDataEntity){
		//byte[] ecgPacketBytes = new byte[4];
		//byte[] accXPacketBytes = new byte[4];
		//byte[] accYPacketBytes = new byte[4];
		//byte[] accZPacketBytes = new byte[4];
		
		byte[][] ecgPacketHeader = new byte[4][4];
		
		//以下为构造ECG包头  数据类别1byte 数据类型格式3bytes
		ecgPacketHeader[0][0]=(byte)0x1;//数据类别DataCategory用一个字节表示,为不同的数据种类和采样频率分别指定一个值,最多可表示255种类别
		/*
		数据类型4byte
		数据类别DataCategory(1byte)	
		MDF(1Bit)	 DF(MutilChannel Data Format)，多通道(或多导联)数据排列方式标志位，当该位为0时表示多通道的数据是连续的，当该位为1时表时多通道的数据是交错的
		Reserved(5Bit)	Reserved，4位保留，第一个bit用于标识数据类型（心电数据/触摸式数据），其置0表示心电数据类型，置1表示触摸式数据类型
		Channels(18Bit) 数据类型
		*/
		byte[] packetTypeBytes = {(byte)0,(byte)0,(byte)0};
		byte status2 = ecgDataEntity.getPacketHead().getPacketStatus()[1];//标识数据类型（第一位标识  心电数据/触摸式数据）
		packetTypeBytes[0]=(byte)(packetTypeBytes[0]|(status2<<7>>>1));//第一位置0，第二位以status2的第一植入 标识  心电数据/触摸式数据
		byte[] ecgPacketLead = ecgDataEntity.getEcgPacket().getEcgDataLead();
		packetTypeBytes[2] = ecgPacketLead[0];
		packetTypeBytes[1] = ecgPacketLead[1];
		packetTypeBytes[0] = (byte)(packetTypeBytes[0]|(ecgPacketLead[2]<<6>>>6));//packetTypeBytes的第0字节的后2位以ecgPacketLead的第2字节的前2位填充
		
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[0],1 , packetTypeBytes.length);//3bytes	数据类型
		//以下为构造ACC_X包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		ecgPacketHeader[1][0]=(byte)0x2;
		packetTypeBytes[0] = (byte)(packetTypeBytes[0]>>>2<<2);//给ecgPacketLead后2bit置0
		packetTypeBytes[1] = 0;
		packetTypeBytes[2] = (byte)1;//数据类型格式 前6位保持不变，第24位标识ACC_X数据包
		
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[1],1 , packetTypeBytes.length);//3bytes	数据类型

		//以下为构造ACC_Y包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		ecgPacketHeader[2][0] = (byte)0x2;
		packetTypeBytes[2] = (byte)2;//数据类型格式 前6位保持不变，第23位标识ACC_Y数据包
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[2],1 , packetTypeBytes.length);//3bytes	数据类型
		
		//以下为构造ACC_Z包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
		ecgPacketHeader[3][0]=(byte)0x2;
		packetTypeBytes[2] = (byte)4;//数据类型格式 前6位保持不变，第22位标识ACC_Z数据包
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[3],1 , packetTypeBytes.length);//3bytes	数据类型
		
		//EcgPacketHeader
		
		return ecgPacketHeader;
	}
}
