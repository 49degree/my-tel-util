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
	private byte[] ecgPacketBytes = null;//��Ҫ�ϴ�������
	UploadEcgDataHeader uploadEcgDataHeader = null;//�������Ͷ������
	private Handler mainEventHandler = null;//�����̵߳�handler
	
	public SoapUploadDataTool(byte[] ecgPacketBytes,Handler handler,UploadEcgDataHeader uploadEcgDataHeader){
		this.ecgPacketBytes = ecgPacketBytes;
		this.mainEventHandler = handler;
		this.uploadEcgDataHeader = uploadEcgDataHeader;
	}
	
	public void run(){
		logger.debug("SoapTool upload data");
		//�������ݸ�ʽ
//		Version  �汾��(2bytes)
//		Lcid    ���Ա�ʶ(2bytes)
//		Reserved (����λ)(2bytes)
//		Request Data Unit 1 �������ݵ�Ԫһ
//		Request Data Unit 2 �������ݵ�Ԫ��
//		�������ݵ�Ԫ����ɸ�ʽ
//		Request Business Code ����ҵ�����(2bytes)
//		Request Function Code �����ܴ���(2bytes)
//		Request Data Length   �����������ݳ���(4bytes)
//		Request Data Detail   ���������������
		
		byte[] topPacketBytes = SoapTool.getTopPacketBytes();//��ȡƽ̨�����
		byte[] checkPacketBytes = SoapTool.getCheckPacketBytes(SoapTool.deviceCode);//��֤���ݵ�Ԫ����� ,�����豸��Ȩ��
		//byte[] ecgPacketBytes = this.getEcgPacketBytes();//�ĵ����ݵ�Ԫ�����

		int allDataLength = topPacketBytes.length+checkPacketBytes.length;
		if(ecgPacketBytes!=null){//�ж����ݰ��Ƿ�Ϊ��
			allDataLength +=ecgPacketBytes.length;
		}
		byte[] value = new byte[allDataLength];
		//����Ϊ�ϲ����ݰ�
		System.arraycopy(topPacketBytes, 0, value, 0, topPacketBytes.length);
		System.arraycopy(checkPacketBytes, 0, value, topPacketBytes.length, checkPacketBytes.length);
		if(ecgPacketBytes!=null){
			System.arraycopy(ecgPacketBytes, 0, value, topPacketBytes.length+checkPacketBytes.length, ecgPacketBytes.length);
		}
		SoapHttpService soapHttpService = new SoapHttpService(
				SoapTool.nameSpace,SoapTool.methodName,SoapTool.wsdlAddr,SoapTool.outParameterName);//��������WEBSERVICE����
		
		soapHttpService.addProperty(SoapTool.parameterName, Base64Coder.encodeByte(value));//�������
		String resultStr = soapHttpService.sendRequest();//�ϴ�
		byte[] reByte = Base64Coder.decode(resultStr);
		resultStr = new String(reByte);
		logger.info(resultStr);
		if(resultStr.length()>0&&!resultStr.substring(0, 1).equals("F")){//�ж����緵���Ƿ�ɹ�
			resultStr = SoapTool.pareResult(reByte)[0];//������������
		}
		
		if(ecgPacketBytes!=null&&TypeConversion.bytesToShort(
				uploadEcgDataHeader.getRqSendFunctionCode(), 0)==(short)0x0004){//0x0004Ϊ�¼��ϴ�������
			//������¼��ϴ������ж�ǰ��������ϴ��Ƿ�ɹ����ɹ����ϴ����Σ�ʧ�ܲ����ϴ���������
			UploadEventEcgDataHeader uploadEventEcgDataHeader = (UploadEventEcgDataHeader)uploadEcgDataHeader;
			if(uploadEventEcgDataHeader.getDataType()==(byte)0&&resultStr.indexOf("�ɹ�")<0){//ǰ��������ϴ�ʧ�ܣ���״̬Ϊ����
				EcgBusiness.getInstance().setSoonUploadState(EcgBusiness.UploadState.NORMAL);
				//resultStr = "ǰ�������:"+resultStr;
			}
			
			if(uploadEventEcgDataHeader.getDataType()==(byte)1){
				//resultStr = "��������:"+resultStr;
			}
		}
		
		
		
		logger.info(resultStr);
		if(mainEventHandler!=null){
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,resultStr);//��ʾ��Ϣ�������߳�
			mainEventHandler.sendMessage(msg);
		}
	}

	/**
	 * �ĵ����ݵ�Ԫ�����
	 * @return
	 */
//	public byte[] getEcgPacketBytes(){
//		//��װ��Ҫ�ϴ����ĵ����ݶ���
//		List<byte[]> ecgPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
//		List<byte[]> accXPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
//		List<byte[]> accYPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
//		List<byte[]> accZPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
//		int ecgPacketByteLength = 0;
//		int accXPacketByteLength = 0;
//		int accYPacketByteLength = 0;
//		int accZPacketByteLength = 0;
//		
//		for(EcgDataEntity ecgDataEntity:ecgDataList){
//			//���ĵ�����
//			byte[] ecgPacket = ecgDataEntity.getEcgPacket().getEcgData();//ECG_PACKET�ĵ�����
//			byte[] accXPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_X_PACKET�ĵ�����
//			byte[] accYPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Y_PACKET�ĵ�����
//			byte[] accZPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Z_PACKET�ĵ�����
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
//		��������4byte
//		�������DataCategory(1byte)	
//		MDF(1Bit)	 DF(MutilChannel Data Format)����ͨ��(��ർ��)�������з�ʽ��־λ������λΪ0ʱ��ʾ��ͨ���������������ģ�����λΪ1ʱ��ʱ��ͨ���������ǽ����
//		Reserved(5Bit)	Reserved��4λ��������һ��bit���ڱ�ʶ�������ͣ��ĵ�����/����ʽ���ݣ�������0��ʾ�ĵ��������ͣ���1��ʾ����ʽ��������
//		Channels(18Bit) ��������
//		ecgPacketHeader����ECG,ACC_X,ACC_Y,ACC_Z�İ�ͷ��Ϣ
//		*/
//		byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataList.get(0));
//		//����Ϊ����ECG��ͷ  �������1byte �������͸�ʽ3bytes
//		System.arraycopy(ecgPacketHeader[0],0 , ecgPacketBytes,0 , 4);//4bytes	��������
//		System.arraycopy(TypeConversion.intToBytes(ecgPacketByteLength-8),0 , ecgPacketBytes,4 , 4);//4bytes	���ݳ���(n)
//		//����Ϊ����ACC_X��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
//		System.arraycopy(ecgPacketHeader[1],0 , accXPacketBytes,0 , 4);//4bytes	��������
//		System.arraycopy(TypeConversion.intToBytes(accXPacketByteLength-8),0 , accXPacketBytes,4 , 4);//4bytes	���ݳ���(n)
//
//		//����Ϊ����ACC_Y��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
//		System.arraycopy(ecgPacketHeader[2],0 , accYPacketBytes,0 , 4);//4bytes	��������
//		System.arraycopy(TypeConversion.intToBytes(accYPacketByteLength-8),0 , accYPacketBytes,4 , 4);//4bytes	���ݳ���(n)
//		
//		//����Ϊ����ACC_Z��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
//		System.arraycopy(ecgPacketHeader[3],0 , accZPacketBytes,0 , 4);//4bytes	��������
//		System.arraycopy(TypeConversion.intToBytes(accZPacketByteLength-8),0 , accZPacketBytes,4 , 4);//4bytes	���ݳ���(n)
//		
//		//������Ҫ�ϴ����ĵ����ݰ� 12Ϊ   8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ���4byte���ݳ���
//		int allSendDataLength = uploadEcgDataHeader.getHeaderLength();//�ϴ����ĵ����ݰ����ϵĳ���
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
//		 * �����ĵ����ݰ�ͷ��Ϣ ����uploadEcgDataHeader����Ĳ�ͬ���Թ��� �� �ĵ������ϴ����ĵ��¼����������ϴ���
//		 * ecgDataHeaderBytesλ�����ĵ����ݵİ�ͷ����ʶ�� �ĵ������ϴ� ���� �ĵ��¼����������ϴ���
//		 */
//		uploadEcgDataHeader.setDataBeginTime(ecgDataList.get(0).dataPacketTime);//8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ��
//		uploadEcgDataHeader.setDataLenght(TypeConversion.intToBytes(allSendDataLength));//4byte���ݳ���
//		byte[] ecgDataHeaderBytes = uploadEcgDataHeader.getEcgDataHeaderBytes();
//		
//		System.arraycopy(ecgDataHeaderBytes,0 , ecgByteData,0 , ecgDataHeaderBytes.length);//�����ĵ����ݰ�ͷ��Ϣ
//
//		logger.info("ecgByteData length1:"+ecgByteData.length);
//		ecgByteData = ZLibUtils.compress(ecgByteData);
//		allSendDataLength = ecgByteData.length;
//		logger.info("ecgByteData length2:"+ecgByteData.length);
//		
//		//�ϴ��ĵ����ݵ�Ԫ ƽ̨����
//		byte[] rqSendBusinessCode =uploadEcgDataHeader.getRqSendBusinessCode();//����ҵ�����(2bytes)
//		byte[] rqSendFunctionCode =uploadEcgDataHeader.getRqSendFunctionCode();//�����ܴ���(2bytes)
//		byte[] rqSendDataLength = TypeConversion.intToBytes(allSendDataLength);//�����������ݳ���(4bytes)
//		byte[] packetBytes = new byte[allSendDataLength+8];//�����������ݳ���
//		System.arraycopy(rqSendBusinessCode, 0, packetBytes, 0, 2);
//		System.arraycopy(rqSendFunctionCode, 0, packetBytes, 2, 2);
//		System.arraycopy(rqSendDataLength, 0, packetBytes, 4, 4);
//		System.arraycopy(ecgByteData, 0, packetBytes, 8, allSendDataLength);
//		return packetBytes;
//	}


}
