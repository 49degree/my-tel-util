package com.xys.ecg.network;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.xys.ecg.bean.DoctorAdviceEntity;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.network.soap.SoapHttpService;
import com.xys.ecg.sqlite.DoctorAdviceDB;
import com.xys.ecg.utils.Base64Coder;
import com.xys.ecg.utils.TypeConversion;

/**
 * 获取医生建议
 * @author Administrator
 *
 */
public class SoapQueryAdviceTool implements Runnable{
	private Handler mainEventHandler = null;
	private Context context = null;

	public static void queryAdviceTool(Context context,Handler mainEventHandler){
		if(SoapTool.checkInternet(mainEventHandler, "没有可用的网络，取消获取最新医生建议!")){//检测网络是否可用
			new Thread(new SoapQueryAdviceTool(context,mainEventHandler)).start();//启动上传数据线程
		}
	}
	
	private SoapQueryAdviceTool(Context context,Handler mainEventHandler){
		this.mainEventHandler = mainEventHandler;
		this.context = context;
	}
	public void run(){
		/**
		 * 测试
		 
		ArrayList<DoctorAdviceEntity> doctorAdviceList = new ArrayList<DoctorAdviceEntity>(10);
		DoctorAdviceEntity doctorAdviceEntity;
		for(short i=0;i<10;i++){
			doctorAdviceEntity = new DoctorAdviceEntity();
			doctorAdviceEntity.setDoctorName("测试"+i);
			doctorAdviceEntity.setContent("医生建议"+i);
			doctorAdviceEntity.setArriveTime("2011/04/01 09:30");
			doctorAdviceList.add(doctorAdviceEntity);
		}
		*/
		
		
		byte[] topPacketBytes = SoapTool.getTopPacketBytes();//获取平台定义包
		byte[] checkPacketBytes = SoapTool.getCheckPacketBytes(SoapTool.deviceCode);//验证数据单元定义包 ,传入设备授权码
		byte[] queryPacketBytes = this.getQueryPacketBytes(0L,0L);//心电数据单元定义包

		byte[] value = new byte[topPacketBytes.length+checkPacketBytes.length+queryPacketBytes.length];
		
		//以下为合并数据包
		System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
		System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
		System.arraycopy(queryPacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, queryPacketBytes.length);

		//查询医生建议
		SoapHttpService soapHttpService = new SoapHttpService(
				SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);//构造连接WEBSERVICE对象
		soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//增加入参
		String resultStr = soapHttpService.sendRequest();//上传
		
		byte[] reByte = Base64Coder.decode(resultStr);
		resultStr = new String(reByte);
		
		//保存结果到数据库
		if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){
			String[] returnArgs = SoapTool.pareResult(reByte);
			resultStr = returnArgs[0];
			if(resultStr.indexOf("成功")>-1){
				ArrayList<DoctorAdviceEntity> doctorAdviceList = paresReturnBytes(Base64Coder.decode(returnArgs[1]));//解析返回数据
				Message msg = mainEventHandler.obtainMessage(HandlerWhat.SoapQueryAdviceTool2Parents,doctorAdviceList);//返回结果给主线程
				mainEventHandler.sendMessage(msg);
				//保存医生建议到本地数据库
				DoctorAdviceDB doctorAdviceDB = new DoctorAdviceDB(context);
				doctorAdviceDB.insertDoctorAdvice(doctorAdviceList);
				doctorAdviceDB.close();
				//上传已经保存结果
				byte[] updatePacketBytes = this.getUpdatePacketBytes(doctorAdviceList);//通知服务器医生建议成功能获取定义包
				value = new byte[topPacketBytes.length+checkPacketBytes.length+updatePacketBytes.length];
				//以下为合并数据包
				System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
				System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
				System.arraycopy(updatePacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, updatePacketBytes.length);
				
				//构造连接WEBSERVICE对象
				soapHttpService = new SoapHttpService(
						SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);
				soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//增加入参
				resultStr = soapHttpService.sendRequest();//上传
				reByte = Base64Coder.decode(resultStr);
				resultStr = new String(reByte);
				if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){
					resultStr = SoapTool.pareResult(reByte)[0];
				}
			}
		}
		Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,resultStr);//提示信息返回主线程
		mainEventHandler.sendMessage(msg);
	}
	
	/**
	 * 查询医生建议数据定义包
	 * 8字节表示的开始时间
	 * 8字节表示的结束时间
	 * (如果时间都为0表示返回最新的医嘱信息)
	 * @return
	 */
	public byte[] getQueryPacketBytes(long startTime,long endTime){
		byte[] queryPacketBytes = new byte[24];//请求数据内容
		
		byte[] rqSendBusinessCode =TypeConversion.shortToBytes((short)0x0001);//请求业务代码(2bytes)
		byte[] rqSendFunctionCode =TypeConversion.shortToBytes((short)0x0008);//请求功能代码(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(16);;//请求数据内容长度(4bytes)
		
		System.arraycopy(rqSendBusinessCode, 0, queryPacketBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, queryPacketBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, queryPacketBytes, 4, 4);
		
		System.arraycopy(TypeConversion.longToBytes(startTime), 0, queryPacketBytes, 8, 8);
		System.arraycopy(TypeConversion.longToBytes(endTime), 0, queryPacketBytes, 16, 8);
		return queryPacketBytes;
	}
	
	/**
	 * 解析返回参数
	 * 
	 * 医嘱信息格式：
	 * 4bytes	通知信息ID号
	 * 医嘱内容：
	 * 2节字表示的医嘱条数
	 * 医嘱信息1
	 * 医嘱信息2
	 * ….
	 * 医嘱信息格式：
	 * 4bytes	通知信息ID号
	 * 8bytes	医嘱信息下达的时间
	 * 1Byte	医生姓名长度
	 * 医生姓名
	 * 2Byte	内容长度
	 * 医嘱内容
	 * @param resultBytes
	 * @return
	 */
	public ArrayList<DoctorAdviceEntity> paresReturnBytes(byte[] resultBytes){
		short doctorAdviceNum = TypeConversion.bytesToShort(resultBytes, 0);//2节字表示的医嘱条数
		ArrayList<DoctorAdviceEntity> doctorAdviceList = new ArrayList<DoctorAdviceEntity>(doctorAdviceNum);
		DoctorAdviceEntity doctorAdviceEntity = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		int beginAdviceIndex = 2;
		byte[] doctorNameBytes = null;
		byte[] doctorTextBytes = null;
		//循环遍历取医嘱
		for(short i=0;i<doctorAdviceNum;i++){
			doctorAdviceEntity = new DoctorAdviceEntity();
			doctorAdviceEntity.setAdviceID(TypeConversion.bytesToInt(resultBytes,beginAdviceIndex));//通知信息ID号
			beginAdviceIndex +=4;
			long time = TypeConversion.bytesToLong(resultBytes, beginAdviceIndex);//当前时间的秒数
			doctorAdviceEntity.setArriveTime(sf.format(new Date(time*1000)));//医嘱信息下达的时间
			beginAdviceIndex +=8;
			doctorNameBytes = new byte[resultBytes[beginAdviceIndex]];//医生姓名长度
			beginAdviceIndex +=1;			
			System.arraycopy(resultBytes, beginAdviceIndex, doctorNameBytes, 0, doctorNameBytes.length);//医生姓名
			doctorAdviceEntity.setDoctorName(new String(doctorNameBytes));
			beginAdviceIndex+=doctorNameBytes.length;
			doctorTextBytes = new byte[TypeConversion.bytesToShort(resultBytes,beginAdviceIndex)];//内容长度
			beginAdviceIndex +=2;
			System.arraycopy(resultBytes, beginAdviceIndex, doctorTextBytes, 0, doctorTextBytes.length);//医嘱内容
			doctorAdviceEntity.setContent(new String(doctorTextBytes));
			beginAdviceIndex +=doctorTextBytes.length;
			doctorAdviceList.add(doctorAdviceEntity);
		}
		return doctorAdviceList;
	}

	
	/**
	 * 通知服务器医生建议成功能获取
	 * 2节字表示的医嘱条数
	 * 4字节，通知信息Id 1
	 * 4字节，通知信息Id 2
	 * @return
	 */
	public byte[] getUpdatePacketBytes(ArrayList<DoctorAdviceEntity> doctorAdviceList){
		int packetLength = 4*doctorAdviceList.size()+2;
		
		byte[] updatePacketBytes = new byte[packetLength+8];//请求数据内容
		
		byte[] rqSendBusinessCode =TypeConversion.shortToBytes((short)0x0001);//请求业务代码(2bytes)
		byte[] rqSendFunctionCode =TypeConversion.shortToBytes((short)0x0009);//请求功能代码(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(packetLength);;//请求数据内容长度(4bytes)
		
		System.arraycopy(rqSendBusinessCode, 0, updatePacketBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, updatePacketBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, updatePacketBytes, 4, 4);
		int beginAdviceIndex = 8;
		
		System.arraycopy(TypeConversion.shortToBytes((short)doctorAdviceList.size()), 0, updatePacketBytes, beginAdviceIndex, 2);//2节字表示的医嘱条数
		beginAdviceIndex += 2;
		
		for(DoctorAdviceEntity doctorAdviceEntity:doctorAdviceList){
			System.arraycopy(TypeConversion.intToBytes(doctorAdviceEntity.getAdviceID()), 0, updatePacketBytes, beginAdviceIndex, 4);//4字节，通知信息Id 1
			beginAdviceIndex += 4;
		}
		return updatePacketBytes;
	}
	
	
}
