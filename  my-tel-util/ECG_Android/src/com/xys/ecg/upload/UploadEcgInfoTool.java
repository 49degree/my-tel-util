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
 * �ϴ�������
 * @author Administrator
 *
 */
public class UploadEcgInfoTool {
	public static Logger logger = Logger.getLogger(UploadEcgInfoTool.class);
	/**
	 * �ϴ�ָ�����ĵ������ļ�
	 * 
	 * �������ȡ�ļ�Ϊ��ͨ��SoapTool�ϴ�������
	 * @param filePath �ļ�·��
	 * fileName �ļ�����
	 * @return
	 */
	public static boolean sendEcgFile(String filePath,String fileName,Handler mainEventHandler){
		//��ȡ�ļ�
		try{
			if(SoapTool.checkInternet(mainEventHandler, "û�п��õ����磬ȡ���ϴ��ĵ�����!")){//��������Ƿ����
				File file = new File(filePath+"/"+fileName);
				FileInputStream fin = new FileInputStream(file);
				byte[] fileBytes = new byte[(int)file.length()];
				if(fin!=null){
					fin.read(fileBytes);
				}
				
				//����4���ļ��ĳ���
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
				String dataBeginTime = file.getName().substring(0, 14);//�ļ����Ƽ��ɼ���ʼʱ��
				byte[] dataBeginTimeBytes = TypeConversion.longToBytes(TimesUtils.string2TimeMill(dataBeginTime, "yyyyMMddHHmmss")/1000);
				uploadEcgDataHeader.setDataBeginTime(dataBeginTimeBytes);//8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ��
				
				byte[] ecgDataPacketBytes = UploadEcgInfoTool.getEcgPacketBytes(fileBytes, uploadEcgDataHeader);//�����ĵ����ݰ� 
				//private UploadEcgDataHeader uploadEcgDataHeader = null;//�ĵ�����ͷ���� ,ͨ���ñ���������  �ĵ��¼����������ϴ�or�ĵ������ϴ�
				new Thread(new SoapUploadDataTool(ecgDataPacketBytes,mainEventHandler,uploadEcgDataHeader)).start();//�����ϴ������߳�
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
		}
		
		
		
		return true;
	}
	
	/**
	 * �ϴ�ָ�����ĵ����ݰ�
	 * 
	 * �������ȡ�ļ�Ϊ��ͨ��SoapTool�ϴ�������
	 * @param ecgDataEntity ���ϴ��ĵ����ݰ�
	 * @return
	 */
	public static boolean sendEcgData(List<EcgDataEntity> ecgDataList,UploadEcgDataHeader uploadEcgDataHeader,Handler mainEventHandler){
		if(SoapTool.checkInternet(mainEventHandler, "û�п��õ����磬ȡ���ϴ��ĵ�����!")){//��������Ƿ����
			byte[] ecgDataPacketBytes = null;
			if(ecgDataList!=null){//��������б�Ϊ�գ����ȡ�ĵ����ݰ�
				ecgDataPacketBytes = UploadEcgInfoTool.getEcgDataPacketBytes(ecgDataList);//��ȡECG,ACC_X,ACC_Y,ACC_Z���ݰ�
				logger.debug("ecgDataPacketBytes:"+ecgDataPacketBytes.length);
			}
			//uploadEcgDataHeader.setDataBeginTime(ecgDataList.get(0).dataPacketTime);//8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ��
			ecgDataPacketBytes = UploadEcgInfoTool.getEcgPacketBytes(ecgDataPacketBytes, uploadEcgDataHeader);//�����ĵ����ݰ� 
			//private UploadEcgDataHeader uploadEcgDataHeader = null;//�ĵ�����ͷ���� ,ͨ���ñ���������  �ĵ��¼����������ϴ�or�ĵ������ϴ�
			new Thread(new SoapUploadDataTool(ecgDataPacketBytes,mainEventHandler,uploadEcgDataHeader)).start();//�����ϴ������߳�
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 *  �����ĵ����ݰ� packetBytes
	 *  ecgDataHeaderBytesΪ�����ĵ����ݵİ�ͷ����ʶ�� �ĵ������ϴ� ���� �ĵ��¼����������ϴ���
	 * @return
	 */
	public static byte[] getEcgPacketBytes(byte[] ecgDataPacketBytes,UploadEcgDataHeader uploadEcgDataHeader){
		

		/*
		 * �����ĵ����ݰ�ͷ��Ϣ ����uploadEcgDataHeader����Ĳ�ͬ���Թ��� �� �ĵ������ϴ����ĵ��¼����������ϴ���
		 * ecgDataHeaderBytesλ�����ĵ����ݵİ�ͷ����ʶ�� �ĵ������ϴ� ���� �ĵ��¼����������ϴ���
		 */
		//int allSendDataLength = ecgDataPacketBytes.length+uploadEcgDataHeader.getHeaderLength();
		
		int allSendDataLength = uploadEcgDataHeader.getHeaderLength();
		if(ecgDataPacketBytes!=null){//�ж����ݰ��Ƿ�Ϊ�� 
			allSendDataLength += ecgDataPacketBytes.length;
		}
		
		logger.debug("getEcgPacketBytes allSendDataLength:"+(ecgDataPacketBytes==null?0:ecgDataPacketBytes.length)+"getHeaderLength:"+uploadEcgDataHeader.getHeaderLength());
		
		byte[] ecgByteData = new byte[allSendDataLength];
		
		uploadEcgDataHeader.setDataLenght(TypeConversion.intToBytes(
				allSendDataLength-uploadEcgDataHeader.getHeaderLength()));//4byte���ݳ���,����������ͷ
		byte[] ecgDataHeaderBytes = uploadEcgDataHeader.getEcgDataHeaderBytes();//��ȡ�ĵ����ݰ�ͷ��Ϣ
		System.arraycopy(ecgDataHeaderBytes,0 , ecgByteData,0 , ecgDataHeaderBytes.length);//COPY �ĵ����ݰ�ͷ��Ϣ
		
		if(ecgDataPacketBytes!=null){//�ж����ݰ��Ƿ�Ϊ�� �����Ϊ����ֻ�������ݰ�ͷ
			System.arraycopy(ecgDataPacketBytes,0 , ecgByteData,ecgDataHeaderBytes.length , ecgDataPacketBytes.length);//COPY ECG,ACC_X,ACC_Y,ACC_Z���ݰ�
			ecgDataPacketBytes = null;
		}
		
		logger.info("ecgByteData length1:"+ecgByteData.length);
		ecgByteData = ZLibUtils.compress(ecgByteData);
		allSendDataLength = ecgByteData.length;
		logger.info("ecgByteData length2:"+ecgByteData.length);
		
		//�ϴ��ĵ����ݵ�Ԫ ƽ̨����
		byte[] rqSendBusinessCode =uploadEcgDataHeader.getRqSendBusinessCode();//����ҵ�����(2bytes)
		byte[] rqSendFunctionCode =uploadEcgDataHeader.getRqSendFunctionCode();//�����ܴ���(2bytes)
		byte[] rqSendDataLength = TypeConversion.intToBytes(allSendDataLength);//�����������ݳ���(4bytes)
		
		byte[] packetBytes = new byte[allSendDataLength+8];//�����������ݳ���
		System.arraycopy(rqSendBusinessCode, 0, packetBytes, 0, 2);
		System.arraycopy(rqSendFunctionCode, 0, packetBytes, 2, 2);
		System.arraycopy(rqSendDataLength, 0, packetBytes, 4, 4);
		System.arraycopy(ecgByteData, 0, packetBytes, 8, allSendDataLength);
		return packetBytes;
	}
	/**
	 * �����ĵ����ݲ�����ECG,ACC_X,ACC_Y,ACC_Z���ݰ�
	 * ���ݶ�1
	 * ���ݶ�2
	 * ������������
	 * 3.5 ���ݶθ�ʽ
	 * 4bytes	��������
	 * 4bytes	���ݳ���(n)
	 * (n)bytes	����
	 * 3.6�������͸�ʽ
	 * ��������4byte
	 * �������DataCategory
	 * (1byte)	MDF
	 * (1Bit)	Reserved
	 * (5Bit)	Channels
	 * (18Bit)
	 * ���ݸ�ʽ��ϸ˵���뿴��WebService�ֻ���ͨ�Žӿ�˵���顷3.4���ݰ���ʽ��3.5 ���ݶθ�ʽ
	 * @return ECG,ACC_X,ACC_Y,ACC_Z���ݰ�
	 */
	public static byte[] getEcgDataPacketBytes(List<EcgDataEntity> ecgDataList){
		//��װ��Ҫ�ϴ����ĵ����ݶ���
		List<byte[]> ecgPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
		List<byte[]> accXPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
		List<byte[]> accYPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
		List<byte[]> accZPacketByteList = new ArrayList<byte[]>(ecgDataList.size());//��Ҫ�ϴ����ĵ����ݰ�����
		int ecgPacketByteLength = 0;
		int accXPacketByteLength = 0;
		int accYPacketByteLength = 0;
		int accZPacketByteLength = 0;
		
		for(EcgDataEntity ecgDataEntity:ecgDataList){
			//���ĵ�����
			byte[] ecgPacket = ecgDataEntity.getEcgPacket().getEcgData();//ECG_PACKET�ĵ�����
			byte[] accXPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_X_PACKET�ĵ�����
			byte[] accYPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Y_PACKET�ĵ�����
			byte[] accZPacket = ecgDataEntity.getAccPacket().getAccAxisX();//ACC_Z_PACKET�ĵ�����
			
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
		��������4byte
		�������DataCategory(1byte)	
		MDF(1Bit)	 DF(MutilChannel Data Format)����ͨ��(��ർ��)�������з�ʽ��־λ������λΪ0ʱ��ʾ��ͨ���������������ģ�����λΪ1ʱ��ʱ��ͨ���������ǽ����
		Reserved(5Bit)	Reserved��4λ��������һ��bit���ڱ�ʶ�������ͣ��ĵ�����/����ʽ���ݣ�������0��ʾ�ĵ��������ͣ���1��ʾ����ʽ��������
		Channels(18Bit) ��������
		ecgPacketHeader����ECG,ACC_X,ACC_Y,ACC_Z�İ�ͷ��Ϣ
		*/
		byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataList.get(0));
		//����Ϊ����ECG��ͷ  �������1byte �������͸�ʽ3bytes
		System.arraycopy(ecgPacketHeader[0],0 , ecgPacketBytes,0 , 4);//4bytes	��������
		System.arraycopy(TypeConversion.intToBytes(ecgPacketByteLength-8),0 , ecgPacketBytes,4 , 4);//4bytes	���ݳ���(n)
		//����Ϊ����ACC_X��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		System.arraycopy(ecgPacketHeader[1],0 , accXPacketBytes,0 , 4);//4bytes	��������
		System.arraycopy(TypeConversion.intToBytes(accXPacketByteLength-8),0 , accXPacketBytes,4 , 4);//4bytes	���ݳ���(n)

		//����Ϊ����ACC_Y��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		System.arraycopy(ecgPacketHeader[2],0 , accYPacketBytes,0 , 4);//4bytes	��������
		System.arraycopy(TypeConversion.intToBytes(accYPacketByteLength-8),0 , accYPacketBytes,4 , 4);//4bytes	���ݳ���(n)
		
		//����Ϊ����ACC_Z��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		System.arraycopy(ecgPacketHeader[3],0 , accZPacketBytes,0 , 4);//4bytes	��������
		System.arraycopy(TypeConversion.intToBytes(accZPacketByteLength-8),0 , accZPacketBytes,4 , 4);//4bytes	���ݳ���(n)
		
		//������Ҫ�ϴ����ĵ����ݰ� 12Ϊ   8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ���4byte���ݳ���
		int allSendDataLength = 0;//�ϴ����ĵ����ݰ����ϵĳ���
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
	 * ��������4byte
	 * �������DataCategory(1byte)	
	 * MDF(1Bit)	 DF(MutilChannel Data Format)����ͨ��(��ർ��)�������з�ʽ��־λ������λΪ0ʱ��ʾ��ͨ���������������ģ�����λΪ1ʱ��ʱ��ͨ���������ǽ����
	 * Reserved(5Bit)	Reserved��4λ��������һ��bit���ڱ�ʶ�������ͣ��ĵ�����/����ʽ���ݣ�������0��ʾ�ĵ��������ͣ���1��ʾ����ʽ��������
	 * Channels(18Bit) ��������
	 * ecgPacketHeader����ECG,ACC_X,ACC_Y,ACC_Z�İ�ͷ��Ϣ
	 * @param ecgDataEntity
	 * @return
	 */
	public static byte[][] getEcgPacketHeader(EcgDataEntity ecgDataEntity){
		//byte[] ecgPacketBytes = new byte[4];
		//byte[] accXPacketBytes = new byte[4];
		//byte[] accYPacketBytes = new byte[4];
		//byte[] accZPacketBytes = new byte[4];
		
		byte[][] ecgPacketHeader = new byte[4][4];
		
		//����Ϊ����ECG��ͷ  �������1byte �������͸�ʽ3bytes
		ecgPacketHeader[0][0]=(byte)0x1;//�������DataCategory��һ���ֽڱ�ʾ,Ϊ��ͬ����������Ͳ���Ƶ�ʷֱ�ָ��һ��ֵ,���ɱ�ʾ255�����
		/*
		��������4byte
		�������DataCategory(1byte)	
		MDF(1Bit)	 DF(MutilChannel Data Format)����ͨ��(��ർ��)�������з�ʽ��־λ������λΪ0ʱ��ʾ��ͨ���������������ģ�����λΪ1ʱ��ʱ��ͨ���������ǽ����
		Reserved(5Bit)	Reserved��4λ��������һ��bit���ڱ�ʶ�������ͣ��ĵ�����/����ʽ���ݣ�������0��ʾ�ĵ��������ͣ���1��ʾ����ʽ��������
		Channels(18Bit) ��������
		*/
		byte[] packetTypeBytes = {(byte)0,(byte)0,(byte)0};
		byte status2 = ecgDataEntity.getPacketHead().getPacketStatus()[1];//��ʶ�������ͣ���һλ��ʶ  �ĵ�����/����ʽ���ݣ�
		packetTypeBytes[0]=(byte)(packetTypeBytes[0]|(status2<<7>>>1));//��һλ��0���ڶ�λ��status2�ĵ�һֲ�� ��ʶ  �ĵ�����/����ʽ����
		byte[] ecgPacketLead = ecgDataEntity.getEcgPacket().getEcgDataLead();
		packetTypeBytes[2] = ecgPacketLead[0];
		packetTypeBytes[1] = ecgPacketLead[1];
		packetTypeBytes[0] = (byte)(packetTypeBytes[0]|(ecgPacketLead[2]<<6>>>6));//packetTypeBytes�ĵ�0�ֽڵĺ�2λ��ecgPacketLead�ĵ�2�ֽڵ�ǰ2λ���
		
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[0],1 , packetTypeBytes.length);//3bytes	��������
		//����Ϊ����ACC_X��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		ecgPacketHeader[1][0]=(byte)0x2;
		packetTypeBytes[0] = (byte)(packetTypeBytes[0]>>>2<<2);//��ecgPacketLead��2bit��0
		packetTypeBytes[1] = 0;
		packetTypeBytes[2] = (byte)1;//�������͸�ʽ ǰ6λ���ֲ��䣬��24λ��ʶACC_X���ݰ�
		
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[1],1 , packetTypeBytes.length);//3bytes	��������

		//����Ϊ����ACC_Y��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		ecgPacketHeader[2][0] = (byte)0x2;
		packetTypeBytes[2] = (byte)2;//�������͸�ʽ ǰ6λ���ֲ��䣬��23λ��ʶACC_Y���ݰ�
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[2],1 , packetTypeBytes.length);//3bytes	��������
		
		//����Ϊ����ACC_Z��ͷ  �������1byte �������͸�ʽ3bytes �������͸�ʽֻ�к�3λ��ͬ
		ecgPacketHeader[3][0]=(byte)0x2;
		packetTypeBytes[2] = (byte)4;//�������͸�ʽ ǰ6λ���ֲ��䣬��22λ��ʶACC_Z���ݰ�
		System.arraycopy(packetTypeBytes,0 , ecgPacketHeader[3],1 , packetTypeBytes.length);//3bytes	��������
		
		//EcgPacketHeader
		
		return ecgPacketHeader;
	}
}
