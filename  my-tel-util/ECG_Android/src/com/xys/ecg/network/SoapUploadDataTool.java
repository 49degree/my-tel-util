package com.xys.ecg.network;

import android.os.Handler;
import android.os.Message;

import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.bean.UploadEcgDataHeader;
import com.xys.ecg.bean.UploadEventEcgDataHeader;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.log.Logger;
import com.xys.ecg.network.soap.SoapHttpService;
import com.xys.ecg.utils.Base64Coder;
import com.xys.ecg.utils.TypeConversion;

public class SoapUploadDataTool implements Runnable{
	public static Logger logger = Logger.getLogger(SoapUploadDataTool.class);
	private byte[] ecgPacketBytes = null;//需要上传的数据
	UploadEcgDataHeader uploadEcgDataHeader = null;//数据类型定义对象
	private Handler mainEventHandler = null;//调用线程的handler
	
	public SoapUploadDataTool(byte[] ecgPacketBytes,Handler handler,UploadEcgDataHeader uploadEcgDataHeader){
		this.ecgPacketBytes = ecgPacketBytes;
		this.mainEventHandler = handler;
		this.uploadEcgDataHeader = uploadEcgDataHeader;
	}
	
	public void run(){
		logger.debug("SoapTool upload data");
		//构造数据格式
//		Version  版本号(2bytes)
//		Lcid    语言标识(2bytes)
//		Reserved (保留位)(2bytes)
//		Request Data Unit 1 请求数据单元一
//		Request Data Unit 2 请求数据单元二
//		请求数据单元的组成格式
//		Request Business Code 请求业务代码(2bytes)
//		Request Function Code 请求功能代码(2bytes)
//		Request Data Length   请求数据内容长度(4bytes)
//		Request Data Detail   请求具体数据内容
		
		byte[] topPacketBytes = SoapTool.getTopPacketBytes();//获取平台定义包
		byte[] checkPacketBytes = SoapTool.getCheckPacketBytes(SoapTool.deviceCode);//验证数据单元定义包 ,传入设备授权码
		//byte[] ecgPacketBytes = this.getEcgPacketBytes();//心电数据单元定义包

		int allDataLength = topPacketBytes.length+checkPacketBytes.length;
		if(ecgPacketBytes!=null){//判断数据包是否为空
			allDataLength +=ecgPacketBytes.length;
		}
		byte[] value = new byte[allDataLength];
		//以下为合并数据包
		System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
		System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
		if(ecgPacketBytes!=null){
			System.arraycopy(ecgPacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, ecgPacketBytes.length);
		}
		SoapHttpService soapHttpService = new SoapHttpService(
				SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);//构造连接WEBSERVICE对象
		
		soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//增加入参
		String resultStr = soapHttpService.sendRequest();//上传
		byte[] reByte = Base64Coder.decode(resultStr);
		resultStr = new String(reByte);
		logger.info(resultStr);
		if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){//判断网络返回是否成功
			resultStr = SoapTool.pareResult(reByte)[0];//解析返回数据
		}
		
		if(ecgPacketBytes!=null&&TypeConversion.bytesToShort(
				uploadEcgDataHeader.getRqSendFunctionCode(), 0)==(short)0x0004){//0x0004为事件上传功能码
			//如果是事件上传，则判断前半段数据上传是否成功，成功则上传后半段，失败不再上传后半段数据
			UploadEventEcgDataHeader uploadEventEcgDataHeader = (UploadEventEcgDataHeader)uploadEcgDataHeader;
			if(uploadEventEcgDataHeader.getDataType()==(byte)0&&resultStr.indexOf("成功")<0){//前半段数据上传失败，置状态为正常
				EcgBusiness.getInstance().setSoonUploadState(EcgBusiness.UploadState.NORMAL);
				//resultStr = "前半段数据:"+resultStr;
			}
			
			if(uploadEventEcgDataHeader.getDataType()==(byte)1){
				//resultStr = "后半段数据:"+resultStr;
			}
		}
		
		
		
		logger.info(resultStr);
		if(mainEventHandler!=null){
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,resultStr);//提示信息返回主线程
			mainEventHandler.sendMessage(msg);
		}
	}

	/**
	 * 心电数据单元定义包
	 * @return
	 */
//	public byte[] getEcgPacketBytes(){
//		//组装需要上传的心电数据对象
//		List<byte[]> ecgPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
//		List<byte[]> accXPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
//		List<byte[]> accYPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
//		List<byte[]> accZPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//需要上传的心电数据包集合
//		int ecgPacketByteLength = 0;
//		int accXPacketByteLength = 0;
//		int accYPacketByteLength = 0;
//		int accZPacketByteLength = 0;
//		
//		for(EcgDataEntity ecgDataEntity:ecgDataList){
//			//传心电数据
//			byte[] ecgPacket = ecgDataEntity.getEcgPacket().getEcgData();//ECG_PACKET心电数据
//			byte[] accXPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_X_PACKET心电数据
//			byte[] accYPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Y_PACKET心电数据
//			byte[] accZPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Z_PACKET心电数据
//			
//			ecgPacketByteLength+=ecgPacket.length;
//			accXPacketByteLength+=accXPacket.length;
//			accYPacketByteLength+=accYPacket.length;
//			accZPacketByteLength+=accZPacket.length;
//			
//			ecgPacketByteList.add(ecgPacket);
//			accXPacketByteList.add(accXPacket);
//			accYPacketByteList.add(accYPacket);
//			accZPacketByteList.add(accZPacket);
//		}
//		
//		byte[] ecgPacketBytes = new byte[ecgPacketByteLength+8];
//		byte[] accXPacketBytes = new byte[accXPacketByteLength+8];
//		byte[] accYPacketBytes = new byte[accYPacketByteLength+8];
//		byte[] accZPacketBytes = new byte[accZPacketByteLength+8];
//		ecgPacketByteLength = 8;
//		accXPacketByteLength = 8;
//		accYPacketByteLength = 8;
//		accZPacketByteLength = 8;
//		
//		int step = 0;
//		int ecgDataListLength = ecgDataList.size();
//		while(step<ecgDataListLength){
//			System.arraycopy(ecgPacketByteList.get(step),0 , ecgPacketBytes,ecgPacketByteLength , ecgPacketByteList.get(step).length);
//			System.arraycopy(accXPacketByteList.get(step),0 , accXPacketBytes,accXPacketByteLength , accXPacketByteList.get(step).length);
//			System.arraycopy(accYPacketByteList.get(step),0 , accYPacketBytes,accYPacketByteLength , accYPacketByteList.get(step).length);
//			System.arraycopy(accZPacketByteList.get(step),0 , accZPacketBytes,accZPacketByteLength , accZPacketByteList.get(step).length);
//			
//			ecgPacketByteLength+=ecgPacketByteList.get(step).length;
//			accXPacketByteLength+=accXPacketByteList.get(step).length;
//			accYPacketByteLength+=accYPacketByteList.get(step).length;
//			accZPacketByteLength+=accZPacketByteList.get(step).length;
//			step++;
//		}
//
//		/*
//		数据类型4byte
//		数据类别DataCategory(1byte)	
//		MDF(1Bit)	 DF(MutilChannel Data Format)，多通道(或多导联)数据排列方式标志位，当该位为0时表示多通道的数据是连续的，当该位为1时表时多通道的数据是交错的
//		Reserved(5Bit)	Reserved，4位保留，第一个bit用于标识数据类型（心电数据/触摸式数据），其置0表示心电数据类型，置1表示触摸式数据类型
//		Channels(18Bit) 数据类型
//		ecgPacketHeader包含ECG,ACC_X,ACC_Y,ACC_Z的包头信息
//		*/
//		byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataList.get(0));
//		//以下为构造ECG包头  数据类别1byte 数据类型格式3bytes
//		System.arraycopy(ecgPacketHeader[0],0 , ecgPacketBytes,0 , 4);//4bytes	数据类型
//		System.arraycopy(TypeConversion.intToBytes(ecgPacketByteLength-8),0 , ecgPacketBytes,4 , 4);//4bytes	数据长度(n)
//		//以下为构造ACC_X包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
//		System.arraycopy(ecgPacketHeader[1],0 , accXPacketBytes,0 , 4);//4bytes	数据类型
//		System.arraycopy(TypeConversion.intToBytes(accXPacketByteLength-8),0 , accXPacketBytes,4 , 4);//4bytes	数据长度(n)
//
//		//以下为构造ACC_Y包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
//		System.arraycopy(ecgPacketHeader[2],0 , accYPacketBytes,0 , 4);//4bytes	数据类型
//		System.arraycopy(TypeConversion.intToBytes(accYPacketByteLength-8),0 , accYPacketBytes,4 , 4);//4bytes	数据长度(n)
//		
//		//以下为构造ACC_Z包头  数据类别1byte 数据类型格式3bytes 数据类型格式只有后3位不同
//		System.arraycopy(ecgPacketHeader[3],0 , accZPacketBytes,0 , 4);//4bytes	数据类型
//		System.arraycopy(TypeConversion.intToBytes(accZPacketByteLength-8),0 , accZPacketBytes,4 , 4);//4bytes	数据长度(n)
//		
//		//单个需要上传的心电数据包 12为   8bytes	心电数据开始采集的时间和4byte数据长度
//		int allSendDataLength = uploadEcgDataHeader.getHeaderLength();//上传的心电数据包集合的长度
//		byte[] ecgByteData = new byte[ecgPacketByteLength+accXPacketByteLength+accYPacketByteLength+accZPacketByteLength+allSendDataLength];
//		
//		System.arraycopy(ecgPacketBytes,0 , ecgByteData,allSendDataLength , ecgPacketByteLength);//COPY ecg data Packet
//		allSendDataLength+=ecgPacketByteLength;
//		System.arraycopy(accXPacketBytes,0 , ecgByteData,allSendDataLength , accXPacketByteLength);//COPY accX data Packet
//		allSendDataLength+=accXPacketByteLength;
//		System.arraycopy(accYPacketBytes,0 , ecgByteData,allSendDataLength , accYPacketByteLength);//COPY accy data Packet
//		allSendDataLength+=accYPacketByteLength;
//		System.arraycopy(accZPacketBytes,0 , ecgByteData,allSendDataLength , accZPacketByteLength);//COPY accz data Packet
//		allSendDataLength+=accZPacketByteLength;
//		
//		/*
//		 * 构造心电数据包头信息 根据uploadEcgDataHeader对象的不同可以构造 《 心电数据上传和心电事件及其数据上传》
//		 * ecgDataHeaderBytes位整个心电数据的包头，标识《 心电数据上传 或者 心电事件及其数据上传》
//		 */
//		uploadEcgDataHeader.setDataBeginTime(ecgDataList.get(0).dataPacketTime);//8bytes	心电数据开始采集的时间
//		uploadEcgDataHeader.setDataLenght(TypeConversion.intToBytes(allSendDataLength));//4byte数据长度
//		byte[] ecgDataHeaderBytes = uploadEcgDataHeader.getEcgDataHeaderBytes();
//		
//		System.arraycopy(ecgDataHeaderBytes,0 , ecgByteData,0 , ecgDataHeaderBytes.length);//构造心电数据包头信息
//
//		logger.info("ecgByteData length1:"+ecgByteData.length);
//		ecgByteData = ZLibUtils.compress(ecgByteData);
//		allSendDataLength = ecgByteData.length;
//		logger.info("ecgByteData length2:"+ecgByteData.length);
//		
//		//上传心电数据单元 平台定义
//		byte[] rqSendBusinessCode =uploadEcgDataHeader.getRqSendBusinessCode();//请求业务代码(2bytes)
//		byte[] rqSendFunctionCode =uploadEcgDataHeader.getRqSendFunctionCode();//请求功能代码(2bytes)
//		byte[] rqSendDataLength = TypeConversion.intToBytes(allSendDataLength);//请求数据内容长度(4bytes)
//		byte[] packetBytes = new byte[allSendDataLength+8];//请求数据内容长度
//		System.arraycopy(rqSendBusinessCode, 0, packetBytes, 0, 2);
//		System.arraycopy(rqSendFunctionCode, 0, packetBytes, 2, 2);
//		System.arraycopy(rqSendDataLength, 0, packetBytes, 4, 4);
//		System.arraycopy(ecgByteData, 0, packetBytes, 8, allSendDataLength);
//		return packetBytes;
//	}


}
