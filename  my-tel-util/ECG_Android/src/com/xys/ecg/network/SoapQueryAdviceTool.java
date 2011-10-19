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
 * ��ȡҽ������
 * @author Administrator
 *
 */
public class SoapQueryAdviceTool implements Runnable{
	private Handler mainEventHandler = null;
	private Context context = null;

	public static void queryAdviceTool(Context context,Handler mainEventHandler){
		if(SoapTool.checkInternet(mainEventHandler, "û�п��õ����磬ȡ����ȡ����ҽ������!")){//��������Ƿ����
			new Thread(new SoapQueryAdviceTool(context,mainEventHandler)).start();//�����ϴ������߳�
		}
	}
	
	private SoapQueryAdviceTool(Context context,Handler mainEventHandler){
		this.mainEventHandler = mainEventHandler;
		this.context = context;
	}
	public void run(){
		/**
		 * ����
		 
		ArrayList<DoctorAdviceEntity> doctorAdviceList = new ArrayList<DoctorAdviceEntity>(10);
		DoctorAdviceEntity doctorAdviceEntity;
		for(short i=0;i<10;i++){
			doctorAdviceEntity = new DoctorAdviceEntity();
			doctorAdviceEntity.setDoctorName("����"+i);
			doctorAdviceEntity.setContent("ҽ������"+i);
			doctorAdviceEntity.setArriveTime("2011/04/01 09:30");
			doctorAdviceList.add(doctorAdviceEntity);
		}
		*/
		
		
		byte[] topPacketBytes = SoapTool.getTopPacketBytes();//��ȡƽ̨�����
		byte[] checkPacketBytes = SoapTool.getCheckPacketBytes(SoapTool.deviceCode);//��֤���ݵ�Ԫ����� ,�����豸��Ȩ��
		byte[] queryPacketBytes = this.getQueryPacketBytes(0L,0L);//�ĵ����ݵ�Ԫ�����

		byte[] value = new byte[topPacketBytes.length+checkPacketBytes.length+queryPacketBytes.length];
		
		//����Ϊ�ϲ����ݰ�
		System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
		System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
		System.arraycopy(queryPacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, queryPacketBytes.length);

		//��ѯҽ������
		SoapHttpService soapHttpService = new SoapHttpService(
				SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);//��������WEBSERVICE����
		soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//�������
		String resultStr = soapHttpService.sendRequest();//�ϴ�
		
		byte[] reByte = Base64Coder.decode(resultStr);
		resultStr = new String(reByte);
		
		//�����������ݿ�
		if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){
			String[] returnArgs = SoapTool.pareResult(reByte);
			resultStr = returnArgs[0];
			if(resultStr.indexOf("�ɹ�")>-1){
				ArrayList<DoctorAdviceEntity> doctorAdviceList = paresReturnBytes(Base64Coder.decode(returnArgs[1]));//������������
				Message msg = mainEventHandler.obtainMessage(HandlerWhat.SoapQueryAdviceTool2Parents,doctorAdviceList);//���ؽ�������߳�
				mainEventHandler.sendMessage(msg);
				//����ҽ�����鵽�������ݿ�
				DoctorAdviceDB doctorAdviceDB = new DoctorAdviceDB(context);
				doctorAdviceDB.insertDoctorAdvice(doctorAdviceList);
				doctorAdviceDB.close();
				//�ϴ��Ѿ�������
				byte[] updatePacketBytes = this.getUpdatePacketBytes(doctorAdviceList);//֪ͨ������ҽ������ɹ��ܻ�ȡ�����
				value = new byte[topPacketBytes.length+checkPacketBytes.length+updatePacketBytes.length];
				//����Ϊ�ϲ����ݰ�
				System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
				System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
				System.arraycopy(updatePacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, updatePacketBytes.length);
				
				//��������WEBSERVICE����
				soapHttpService = new SoapHttpService(
						SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);
				soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//�������
				resultStr = soapHttpService.sendRequest();//�ϴ�
				reByte = Base64Coder.decode(resultStr);
				resultStr = new String(reByte);
				if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){
					resultStr = SoapTool.pareResult(reByte)[0];
				}
			}
		}
		Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,resultStr);//��ʾ��Ϣ�������߳�
		mainEventHandler.sendMessage(msg);
	}
	
	/**
	 * ��ѯҽ���������ݶ����
	 * 8�ֽڱ�ʾ�Ŀ�ʼʱ��
	 * 8�ֽڱ�ʾ�Ľ���ʱ��
	 * (���ʱ�䶼Ϊ0��ʾ�������µ�ҽ����Ϣ)
	 * @return
	 */
	public byte[] getQueryPacketBytes(long startTime,long endTime){
		byte[] queryPacketBytes = new byte[24];//������������
		
		byte[] rqSendBusinessCode =TypeConversion.shortToBytes((short)0x0001);//����ҵ�����(2bytes)
		byte[] rqSendFunctionCode =TypeConversion.shortToBytes((short)0x0008);//�����ܴ���(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(16);;//�����������ݳ���(4bytes)
		
		System.arraycopy(rqSendBusinessCode, 0, queryPacketBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, queryPacketBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, queryPacketBytes, 4, 4);
		
		System.arraycopy(TypeConversion.longToBytes(startTime), 0, queryPacketBytes, 8, 8);
		System.arraycopy(TypeConversion.longToBytes(endTime), 0, queryPacketBytes, 16, 8);
		return queryPacketBytes;
	}
	
	/**
	 * �������ز���
	 * 
	 * ҽ����Ϣ��ʽ��
	 * 4bytes	֪ͨ��ϢID��
	 * ҽ�����ݣ�
	 * 2���ֱ�ʾ��ҽ������
	 * ҽ����Ϣ1
	 * ҽ����Ϣ2
	 * ��.
	 * ҽ����Ϣ��ʽ��
	 * 4bytes	֪ͨ��ϢID��
	 * 8bytes	ҽ����Ϣ�´��ʱ��
	 * 1Byte	ҽ����������
	 * ҽ������
	 * 2Byte	���ݳ���
	 * ҽ������
	 * @param resultBytes
	 * @return
	 */
	public ArrayList<DoctorAdviceEntity> paresReturnBytes(byte[] resultBytes){
		short doctorAdviceNum = TypeConversion.bytesToShort(resultBytes, 0);//2���ֱ�ʾ��ҽ������
		ArrayList<DoctorAdviceEntity> doctorAdviceList = new ArrayList<DoctorAdviceEntity>(doctorAdviceNum);
		DoctorAdviceEntity doctorAdviceEntity = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		int beginAdviceIndex = 2;
		byte[] doctorNameBytes = null;
		byte[] doctorTextBytes = null;
		//ѭ������ȡҽ��
		for(short i=0;i<doctorAdviceNum;i++){
			doctorAdviceEntity = new DoctorAdviceEntity();
			doctorAdviceEntity.setAdviceID(TypeConversion.bytesToInt(resultBytes,beginAdviceIndex));//֪ͨ��ϢID��
			beginAdviceIndex +=4;
			long time = TypeConversion.bytesToLong(resultBytes, beginAdviceIndex);//��ǰʱ�������
			doctorAdviceEntity.setArriveTime(sf.format(new Date(time*1000)));//ҽ����Ϣ�´��ʱ��
			beginAdviceIndex +=8;
			doctorNameBytes = new byte[resultBytes[beginAdviceIndex]];//ҽ����������
			beginAdviceIndex +=1;			
			System.arraycopy(resultBytes, beginAdviceIndex, doctorNameBytes, 0, doctorNameBytes.length);//ҽ������
			doctorAdviceEntity.setDoctorName(new String(doctorNameBytes));
			beginAdviceIndex+=doctorNameBytes.length;
			doctorTextBytes = new byte[TypeConversion.bytesToShort(resultBytes,beginAdviceIndex)];//���ݳ���
			beginAdviceIndex +=2;
			System.arraycopy(resultBytes, beginAdviceIndex, doctorTextBytes, 0, doctorTextBytes.length);//ҽ������
			doctorAdviceEntity.setContent(new String(doctorTextBytes));
			beginAdviceIndex +=doctorTextBytes.length;
			doctorAdviceList.add(doctorAdviceEntity);
		}
		return doctorAdviceList;
	}

	
	/**
	 * ֪ͨ������ҽ������ɹ��ܻ�ȡ
	 * 2���ֱ�ʾ��ҽ������
	 * 4�ֽڣ�֪ͨ��ϢId 1
	 * 4�ֽڣ�֪ͨ��ϢId 2
	 * @return
	 */
	public byte[] getUpdatePacketBytes(ArrayList<DoctorAdviceEntity> doctorAdviceList){
		int packetLength = 4*doctorAdviceList.size()+2;
		
		byte[] updatePacketBytes = new byte[packetLength+8];//������������
		
		byte[] rqSendBusinessCode =TypeConversion.shortToBytes((short)0x0001);//����ҵ�����(2bytes)
		byte[] rqSendFunctionCode =TypeConversion.shortToBytes((short)0x0009);//�����ܴ���(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(packetLength);;//�����������ݳ���(4bytes)
		
		System.arraycopy(rqSendBusinessCode, 0, updatePacketBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, updatePacketBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, updatePacketBytes, 4, 4);
		int beginAdviceIndex = 8;
		
		System.arraycopy(TypeConversion.shortToBytes((short)doctorAdviceList.size()), 0, updatePacketBytes, beginAdviceIndex, 2);//2���ֱ�ʾ��ҽ������
		beginAdviceIndex += 2;
		
		for(DoctorAdviceEntity doctorAdviceEntity:doctorAdviceList){
			System.arraycopy(TypeConversion.intToBytes(doctorAdviceEntity.getAdviceID()), 0, updatePacketBytes, beginAdviceIndex, 4);//4�ֽڣ�֪ͨ��ϢId 1
			beginAdviceIndex += 4;
		}
		return updatePacketBytes;
	}
	
	
}
