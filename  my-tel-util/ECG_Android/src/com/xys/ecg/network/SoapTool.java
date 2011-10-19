package com.xys.ecg.network;

import android.os.Handler;
import android.os.Message;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.utils.Base64Coder;
import com.xys.ecg.utils.TypeConversion;


public class SoapTool {
	//public static Logger logger = Logger.getLogger(SoapTool.class);
	//SOAP服务器端常量
	public static String wsdlAddr = null;//"http://172.18.17.46/WrmWebService/Ecg/WrmRemoteService.asmx";
	//public static String wsdlAddr = "http://172.18.17.46/WrmWebService/Ecg/WrmRemoteService.asmx";
	
	public static String nameSpace = "http://szxys.cn/com/rhmpservice/";
	public static String methodName = "Process";
	public static String parameterName = "request";
	public static String outParameterName = "response";
	
	public static long deviceCode = 0;//30067335242L;
	static{
		SoapTool.initSoapInfo();
	}
	/**
	 * 初始化设备信息
	 */
	public static void initSoapInfo(){
		try{
			EcgXmlFileOperate XMLConfig =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
			wsdlAddr = XMLConfig.selectEcgXmlNode("WebServiceAddrOfLan").getParentNodeAttributeValue();
			deviceCode = Long.parseLong(XMLConfig.selectEcgXmlNode("ValidateCode").getParentNodeAttributeValue());//授权码
		}catch(Exception e){
			
		}
	}
	
	
	/**
	 * 检测网络状态并提示给前台
	 * @return
	 */
	public static boolean checkInternet(Handler mainEventHandler,String returnMsg){
		boolean canConnect = true;
		if(!NetworkTool.checkInternet(ECGApplication.getInstance())){//判断网络状态
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,returnMsg);//提示信息返回主线程
			mainEventHandler.sendMessage(msg);
			canConnect = false;
		}
		return canConnect;
	}
	
	/**
	 * 获取平台定义包
	 * @return
	 */
	public static byte[] getTopPacketBytes(){
		byte[] version =new byte[]{3,3};//版本号(2bytes)
		byte[] lcid =TypeConversion.shortToBytes((short)2052);//语言标识(2bytes) zh-cn	0x0804	2052
		byte[] reserved =new byte[2];//(2bytes)
		
		byte[] topPacketBytes = new byte[6];
		System.arraycopy(version, 0, topPacketBytes, 0, 2);
		System.arraycopy(lcid, 0, topPacketBytes, 2, 2);
		System.arraycopy(reserved, 0, topPacketBytes, 4, 2);
		
		return topPacketBytes;
	}
	
	/**
	 * 验证数据单元定义包
	 * 
	 * deviceNo 设备授权码
	 * @return
	 */
	public static byte[] getCheckPacketBytes(long deviceNo){
		//验证数据单元
		byte[] rqBusinessCode =TypeConversion.shortToBytes((short)0x0001);//请求业务代码(2bytes)
		byte[] rqCheckFunctionCode =TypeConversion.shortToBytes((short)0x0001);//请求功能代码(2bytes)
		byte[] rqCheckDataLength =TypeConversion.intToBytes(8);//请求数据内容长度(4bytes)
		byte[] deviceCode = TypeConversion.longToBytes(deviceNo);//64位整形表示的设备授权码
		
		byte[] checkPacketBytes = new byte[16];
		System.arraycopy(rqBusinessCode, 0, checkPacketBytes, 0, 2);
		System.arraycopy(rqCheckFunctionCode, 0, checkPacketBytes, 2, 2);
		System.arraycopy(rqCheckDataLength, 0, checkPacketBytes, 4, 4);
		System.arraycopy(deviceCode, 0, checkPacketBytes, 8, 8);
		
		return checkPacketBytes;
	}


	/**
	 * 根据返回码提示用户信息
	 * 
	 * reByte 返回的字节码
	 * @return
	 */
	public static String[] pareResult(byte[] reByte) {
		String[] returnArgs = new String[2];
		StringBuffer returnStr = new StringBuffer();
		// Reserved (保留)(4bytes)
		// Response Data Unit 1 响应数据单元一
		// Response Data Unit 2 响应数据单元二
		
		//响应数据单元一 位验证单元返回数据
		short responseBusinessCode = TypeConversion.bytesToShort(reByte, 4);// Response Business Code 响应业务代码(2bytes)
		short responseFunctionCode = TypeConversion.bytesToShort(reByte, 6);// Response Function Code 响应功能代码(2bytes)
		short responseCategoryCode = TypeConversion.bytesToShort(reByte, 8);// Response Category Code 响应类别代码(2bytes)
		int responseDataLength = TypeConversion.bytesToInt(reByte, 10);// Response Data Length 响应数据内容长度(4bytes)
		String businessResponseData = new String(reByte, 14, responseDataLength);// Business Response Data 响应具体数据内容
		if(responseFunctionCode==1&&responseCategoryCode!=0){
			returnStr.append("授权码验证失败：").append(businessResponseData);
			returnArgs[0] = returnStr.toString();
			return returnArgs;
		}
		
		//响应数据单元二对应业务功能的响应
		int startIndex = 14+responseDataLength;
		short responseBusinessCode2 = TypeConversion.bytesToShort(reByte, startIndex);// Response Business Code 响应业务代码(2bytes)
		short responseFunctionCode2 = TypeConversion.bytesToShort(reByte, startIndex+2);// Response Function Code 响应功能代码(2bytes)
		short responseCategoryCode2 = TypeConversion.bytesToShort(reByte, startIndex+4);// Response Category Code 响应类别代码(2bytes)
		int responseDataLength2 = TypeConversion.bytesToInt(reByte, startIndex+6);// Response Data Length 响应数据内容长度(4bytes)
		String businessResponseData2 = new String(Base64Coder.encode(reByte, startIndex+10, responseDataLength2));// Business Response Data 响应具体数据内容

		//根据响应功能代码获取功能名称
		switch(responseFunctionCode2){
		case 0x0001:
			returnStr.append("用户验证");
			break;
		case 0x0002:
			returnStr.append("心电数据上传");
			break;
		case 0x0003:
			returnStr.append("数据实时上传");
			break;			
		case 0x0004:
			returnStr.append("心电事件及其数据上传");
			break;		
		case 0x0008:
			returnStr.append("获取医生建议");
			break;		
		case 0x0009:
			returnStr.append("医生建议成功能获取通知");
			break;
		default :
			break;
		}
		
		if(returnStr.length()>0){
			returnStr.append(":");
		}
		//根据响应功能代码 和 响应类别代码返回提示信息
		/*响应类别代码用两字节的数值表示响应的类别，一般为0表示正常响应，其它值表示错误的状态信息。响应代码使用范围规定：
		 * 0x0000 - 0x1000 范围由平台定义解释。
		 * 0x1001 – 0xFFFF范围由业务定义解释。
		 * 0x0103	找不到前半段事件数据（上传后半段数据时在数据库里查不到对应的前半段数据）
		 * 0x0104	找不到关联的事件信息（上传事件数据时查不到对应的心电事件）
		 * 0x0105	无法处理的数据类型
		 * 0x0106	重复上传的数据
		 * 0x0107	读前后半段数据标志的数据值超出预期范围
		 * 0x0108	分片上传序号不对
		 * 0x0100	数据异常
		 * 0x0601	病人信息不存在
		 */
//		if(responseCategoryCode2<=Integer.parseInt("1000", 16)&&responseCategoryCode2>0){
//			
//			returnStr.append("0X").append(Integer.toHexString(responseCategoryCode2).toUpperCase());
		if(responseCategoryCode2>0){			
			switch(responseCategoryCode2){
			case 0x0000:returnStr.append("正常响应 ");break;
			case 0x0001:returnStr.append("应用程序异常，原因不明确") ;break;
			case 0x0002:returnStr.append("程序初始化失败 ");break;
			case 0x0003:returnStr.append("应用程序配置错");break;
			case 0x0100:returnStr.append("请求数据包异常");break;
			case 0x0101:returnStr.append("请求的业务代码未被支持"); break;
			case 0x0102:returnStr.append("请求的功能代码未被支持");break;
			case 0x0300:returnStr.append("– 0x03FF	网络异常") ;break;
			case 0x0200:returnStr.append("网络异常");break;
			case 0x0400:returnStr.append("IO异常 ") ;break;
			case 0x0401:returnStr.append("文件或目录不存在");break;
			case 0x0402:returnStr.append("打开文件失败");break;
			case 0x0403:returnStr.append("创建文件失败");break;
			case 0x0500:returnStr.append("数据操作失败");break;
			
			case 0x0501:	returnStr.append("数据库连接失败");break;
			case 0x0502:	returnStr.append("数据库数据冲突");break;
			case 0x0503:	returnStr.append("数据库数据冲突");break;
			case 0x0504:	returnStr.append("事务冲突");break;
			case 0x0600:	returnStr.append("未通过验证");break;
			case 0x0602:	returnStr.append("密码错误");break;
			case 0x0603:	returnStr.append("不支持此验证方式");break;	
			case 0x0604:	returnStr.append("权限禁止");break;

			case 0x0103:
				returnStr.append("找不到前半段事件数据");
				break;
			case 0x0104:
				returnStr.append("找不到关联的事件信息");
				break;
			case 0x0105:
				returnStr.append("无法处理的数据类型");
				break;			
			case 0x0106:
				returnStr.append("重复上传的数据");
				break;		
			case 0x0107:
				returnStr.append("读前后半段数据标志的数据值超出预期范围");
				break;		
			case 0x0108:
				returnStr.append("分片上传序号不对");
				break;
			case 0x0601:
				returnStr.append("病人信息不存在");
				break;				
			default :
				returnStr.append(":").append("平台返回失败信息!");
				break;
			}
//		}else if(responseCategoryCode2>Integer.parseInt("1000", 16)){
//			returnStr.append("0X").append(Integer.toHexString(responseCategoryCode2).toUpperCase()).append(":").append("业务返回失败信息!");
		}else{
			returnStr.append("操作成功");
		}
		returnArgs[0] = returnStr.toString();
		returnArgs[1] = businessResponseData2;
		return returnArgs;
	}
}
