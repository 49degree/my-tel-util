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
	//SOAP�������˳���
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
	 * ��ʼ���豸��Ϣ
	 */
	public static void initSoapInfo(){
		try{
			EcgXmlFileOperate XMLConfig =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
			wsdlAddr = XMLConfig.selectEcgXmlNode("WebServiceAddrOfLan").getParentNodeAttributeValue();
			deviceCode = Long.parseLong(XMLConfig.selectEcgXmlNode("ValidateCode").getParentNodeAttributeValue());//��Ȩ��
		}catch(Exception e){
			
		}
	}
	
	
	/**
	 * �������״̬����ʾ��ǰ̨
	 * @return
	 */
	public static boolean checkInternet(Handler mainEventHandler,String returnMsg){
		boolean canConnect = true;
		if(!NetworkTool.checkInternet(ECGApplication.getInstance())){//�ж�����״̬
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,returnMsg);//��ʾ��Ϣ�������߳�
			mainEventHandler.sendMessage(msg);
			canConnect = false;
		}
		return canConnect;
	}
	
	/**
	 * ��ȡƽ̨�����
	 * @return
	 */
	public static byte[] getTopPacketBytes(){
		byte[] version =new byte[]{3,3};//�汾��(2bytes)
		byte[] lcid =TypeConversion.shortToBytes((short)2052);//���Ա�ʶ(2bytes) zh-cn	0x0804	2052
		byte[] reserved =new byte[2];//(2bytes)
		
		byte[] topPacketBytes = new byte[6];
		System.arraycopy(version, 0, topPacketBytes, 0, 2);
		System.arraycopy(lcid, 0, topPacketBytes, 2, 2);
		System.arraycopy(reserved, 0, topPacketBytes, 4, 2);
		
		return topPacketBytes;
	}
	
	/**
	 * ��֤���ݵ�Ԫ�����
	 * 
	 * deviceNo �豸��Ȩ��
	 * @return
	 */
	public static byte[] getCheckPacketBytes(long deviceNo){
		//��֤���ݵ�Ԫ
		byte[] rqBusinessCode =TypeConversion.shortToBytes((short)0x0001);//����ҵ�����(2bytes)
		byte[] rqCheckFunctionCode =TypeConversion.shortToBytes((short)0x0001);//�����ܴ���(2bytes)
		byte[] rqCheckDataLength =TypeConversion.intToBytes(8);//�����������ݳ���(4bytes)
		byte[] deviceCode = TypeConversion.longToBytes(deviceNo);//64λ���α�ʾ���豸��Ȩ��
		
		byte[] checkPacketBytes = new byte[16];
		System.arraycopy(rqBusinessCode, 0, checkPacketBytes, 0, 2);
		System.arraycopy(rqCheckFunctionCode, 0, checkPacketBytes, 2, 2);
		System.arraycopy(rqCheckDataLength, 0, checkPacketBytes, 4, 4);
		System.arraycopy(deviceCode, 0, checkPacketBytes, 8, 8);
		
		return checkPacketBytes;
	}


	/**
	 * ���ݷ�������ʾ�û���Ϣ
	 * 
	 * reByte ���ص��ֽ���
	 * @return
	 */
	public static String[] pareResult(byte[] reByte) {
		String[] returnArgs = new String[2];
		StringBuffer returnStr = new StringBuffer();
		// Reserved (����)(4bytes)
		// Response Data Unit 1 ��Ӧ���ݵ�Ԫһ
		// Response Data Unit 2 ��Ӧ���ݵ�Ԫ��
		
		//��Ӧ���ݵ�Ԫһ λ��֤��Ԫ��������
		short responseBusinessCode = TypeConversion.bytesToShort(reByte, 4);// Response Business Code ��Ӧҵ�����(2bytes)
		short responseFunctionCode = TypeConversion.bytesToShort(reByte, 6);// Response Function Code ��Ӧ���ܴ���(2bytes)
		short responseCategoryCode = TypeConversion.bytesToShort(reByte, 8);// Response Category Code ��Ӧ������(2bytes)
		int responseDataLength = TypeConversion.bytesToInt(reByte, 10);// Response Data Length ��Ӧ�������ݳ���(4bytes)
		String businessResponseData = new String(reByte, 14, responseDataLength);// Business Response Data ��Ӧ������������
		if(responseFunctionCode==1&&responseCategoryCode!=0){
			returnStr.append("��Ȩ����֤ʧ�ܣ�").append(businessResponseData);
			returnArgs[0] = returnStr.toString();
			return returnArgs;
		}
		
		//��Ӧ���ݵ�Ԫ����Ӧҵ���ܵ���Ӧ
		int startIndex = 14+responseDataLength;
		short responseBusinessCode2 = TypeConversion.bytesToShort(reByte, startIndex);// Response Business Code ��Ӧҵ�����(2bytes)
		short responseFunctionCode2 = TypeConversion.bytesToShort(reByte, startIndex+2);// Response Function Code ��Ӧ���ܴ���(2bytes)
		short responseCategoryCode2 = TypeConversion.bytesToShort(reByte, startIndex+4);// Response Category Code ��Ӧ������(2bytes)
		int responseDataLength2 = TypeConversion.bytesToInt(reByte, startIndex+6);// Response Data Length ��Ӧ�������ݳ���(4bytes)
		String businessResponseData2 = new String(Base64Coder.encode(reByte, startIndex+10, responseDataLength2));// Business Response Data ��Ӧ������������

		//������Ӧ���ܴ����ȡ��������
		switch(responseFunctionCode2){
		case 0x0001:
			returnStr.append("�û���֤");
			break;
		case 0x0002:
			returnStr.append("�ĵ������ϴ�");
			break;
		case 0x0003:
			returnStr.append("����ʵʱ�ϴ�");
			break;			
		case 0x0004:
			returnStr.append("�ĵ��¼����������ϴ�");
			break;		
		case 0x0008:
			returnStr.append("��ȡҽ������");
			break;		
		case 0x0009:
			returnStr.append("ҽ������ɹ��ܻ�ȡ֪ͨ");
			break;
		default :
			break;
		}
		
		if(returnStr.length()>0){
			returnStr.append(":");
		}
		//������Ӧ���ܴ��� �� ��Ӧ�����뷵����ʾ��Ϣ
		/*��Ӧ�����������ֽڵ���ֵ��ʾ��Ӧ�����һ��Ϊ0��ʾ������Ӧ������ֵ��ʾ�����״̬��Ϣ����Ӧ����ʹ�÷�Χ�涨��
		 * 0x0000 - 0x1000 ��Χ��ƽ̨������͡�
		 * 0x1001 �C 0xFFFF��Χ��ҵ������͡�
		 * 0x0103	�Ҳ���ǰ����¼����ݣ��ϴ���������ʱ�����ݿ���鲻����Ӧ��ǰ������ݣ�
		 * 0x0104	�Ҳ����������¼���Ϣ���ϴ��¼�����ʱ�鲻����Ӧ���ĵ��¼���
		 * 0x0105	�޷��������������
		 * 0x0106	�ظ��ϴ�������
		 * 0x0107	��ǰ�������ݱ�־������ֵ����Ԥ�ڷ�Χ
		 * 0x0108	��Ƭ�ϴ���Ų���
		 * 0x0100	�����쳣
		 * 0x0601	������Ϣ������
		 */
//		if(responseCategoryCode2<=Integer.parseInt("1000", 16)&&responseCategoryCode2>0){
//			
//			returnStr.append("0X").append(Integer.toHexString(responseCategoryCode2).toUpperCase());
		if(responseCategoryCode2>0){			
			switch(responseCategoryCode2){
			case 0x0000:returnStr.append("������Ӧ ");break;
			case 0x0001:returnStr.append("Ӧ�ó����쳣��ԭ����ȷ") ;break;
			case 0x0002:returnStr.append("�����ʼ��ʧ�� ");break;
			case 0x0003:returnStr.append("Ӧ�ó������ô�");break;
			case 0x0100:returnStr.append("�������ݰ��쳣");break;
			case 0x0101:returnStr.append("�����ҵ�����δ��֧��"); break;
			case 0x0102:returnStr.append("����Ĺ��ܴ���δ��֧��");break;
			case 0x0300:returnStr.append("�C 0x03FF	�����쳣") ;break;
			case 0x0200:returnStr.append("�����쳣");break;
			case 0x0400:returnStr.append("IO�쳣 ") ;break;
			case 0x0401:returnStr.append("�ļ���Ŀ¼������");break;
			case 0x0402:returnStr.append("���ļ�ʧ��");break;
			case 0x0403:returnStr.append("�����ļ�ʧ��");break;
			case 0x0500:returnStr.append("���ݲ���ʧ��");break;
			
			case 0x0501:	returnStr.append("���ݿ�����ʧ��");break;
			case 0x0502:	returnStr.append("���ݿ����ݳ�ͻ");break;
			case 0x0503:	returnStr.append("���ݿ����ݳ�ͻ");break;
			case 0x0504:	returnStr.append("�����ͻ");break;
			case 0x0600:	returnStr.append("δͨ����֤");break;
			case 0x0602:	returnStr.append("�������");break;
			case 0x0603:	returnStr.append("��֧�ִ���֤��ʽ");break;	
			case 0x0604:	returnStr.append("Ȩ�޽�ֹ");break;

			case 0x0103:
				returnStr.append("�Ҳ���ǰ����¼�����");
				break;
			case 0x0104:
				returnStr.append("�Ҳ����������¼���Ϣ");
				break;
			case 0x0105:
				returnStr.append("�޷��������������");
				break;			
			case 0x0106:
				returnStr.append("�ظ��ϴ�������");
				break;		
			case 0x0107:
				returnStr.append("��ǰ�������ݱ�־������ֵ����Ԥ�ڷ�Χ");
				break;		
			case 0x0108:
				returnStr.append("��Ƭ�ϴ���Ų���");
				break;
			case 0x0601:
				returnStr.append("������Ϣ������");
				break;				
			default :
				returnStr.append(":").append("ƽ̨����ʧ����Ϣ!");
				break;
			}
//		}else if(responseCategoryCode2>Integer.parseInt("1000", 16)){
//			returnStr.append("0X").append(Integer.toHexString(responseCategoryCode2).toUpperCase()).append(":").append("ҵ�񷵻�ʧ����Ϣ!");
		}else{
			returnStr.append("�����ɹ�");
		}
		returnArgs[0] = returnStr.toString();
		returnArgs[1] = businessResponseData2;
		return returnArgs;
	}
}
