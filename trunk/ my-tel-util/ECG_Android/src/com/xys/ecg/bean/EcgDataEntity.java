package com.xys.ecg.bean;

import java.io.Serializable;

import com.xys.ecg.utils.TypeConversion;

public class EcgDataEntity implements Serializable{
//	public static Logger logger = Logger.getLogger(EcgDataEntity.class);
	
	public DataPacketHeadEntity packetHead = null;//��ͷ
	public EcgPacketEntity ecgPacket = null;//ecg���ݰ�
	public AccPacketEntity accPacket = null;//acc���ݰ�
	public boolean isFirstData = true;//�Ƿ�Ϊ��һ�����ݰ�
	public byte[] dataPacketTime = new byte[8];//�ɼ����ݵ�ʱ��,��λΪ��
	public boolean isHisDate = false  ;//�Ƿ���ʷ����
	
	

	public EcgDataEntity(){
		super();
		this.packetHead = new DataPacketHeadEntity();
		this.ecgPacket = new EcgPacketEntity();
		this.accPacket = new AccPacketEntity();
	}

	/**
	 * ���ݴ����BYTE�����ʼ������
	 * @param source
	 * @param isFirstData
	 * @param dataPacketTime
	 */
	public EcgDataEntity(byte[] source,boolean isFirstData,byte[] dataPacketTime,boolean isHisDate){
		this();
		try{
			//��ʼ����ͷ����
			packetHead.packetType = source[0];
			System.arraycopy(source, 1, packetHead.packetLength, 0, 2);
			System.arraycopy(source, 3, packetHead.packetId, 0, 4);
			System.arraycopy(source, 7, packetHead.packetStatus, 0, 2);
			packetHead.packetReserved = source[9];
			//��ʼ��ECG���ݶ���
			System.arraycopy(source, 10, ecgPacket.ecgDataType, 0, 2);
			System.arraycopy(source, 12, ecgPacket.ecgDataLead, 0, 3);
			System.arraycopy(source, 15, ecgPacket.ecgDataLength, 0, 2);
			ecgPacket.ecgDataReserved = source[17];
			//����ecgPacket.ecgDataLength���ȼ���ʣ�����ݵ�Ԫ����ΪecgDateLength-8
			short ecgDateLength = TypeConversion.bytesToShort(ecgPacket.ecgDataLength, 0);
			
			int ecgDataLth = ecgDateLength-8;
			ecgPacket.ecgData = new byte[ecgDataLth];
			System.arraycopy(source, 18, ecgPacket.ecgData, 0, ecgDateLength-8);
			
			//��ʼ��ACC���ݶ���
			System.arraycopy(source, 10+ecgDateLength, accPacket.accType, 0, 2);
			accPacket.accStatus = source[12+ecgDateLength];
			System.arraycopy(source, 13+ecgDateLength, accPacket.accLength, 0, 2);
			accPacket.accReserved = source[15+ecgDateLength];
			
			short accDateLength = (short)((TypeConversion.bytesToShort(accPacket.accLength, 0)-6)/3);
			
			accPacket.accAxisX = new byte[accDateLength];
			accPacket.accAxisY = new byte[accDateLength];
			accPacket.accAxisZ = new byte[accDateLength];
			
			System.arraycopy(source, 16+ecgDateLength, accPacket.accAxisX, 0, accDateLength);
			System.arraycopy(source, 16+accDateLength+ecgDateLength, accPacket.accAxisY, 0, accDateLength);
			System.arraycopy(source, 16+accDateLength*2+ecgDateLength, accPacket.accAxisZ, 0, accDateLength);
			
			this.isFirstData = isFirstData;
			this.dataPacketTime = dataPacketTime;
			this.isHisDate = isHisDate;
		}catch(Exception e){
			System.out.print("exception" + e.getMessage());
		}
	}
	
	public EcgDataEntity(DataPacketHeadEntity packetHead,
			EcgPacketEntity ecgPacket, AccPacketEntity accPacket,
			boolean isFirstData, byte[] dataPacketTime, boolean isHisDate) {
		super();
		this.packetHead = packetHead;
		this.ecgPacket = ecgPacket;
		this.accPacket = accPacket;
		this.isFirstData = isFirstData;
		this.dataPacketTime = dataPacketTime;
		this.isHisDate = isHisDate;
	}
	

	
	public DataPacketHeadEntity getPacketHead() {
		return packetHead;
	}

	public void setPacketHead(DataPacketHeadEntity packetHead) {
		
		this.packetHead = packetHead;
	}

	public EcgPacketEntity getEcgPacket() {
		return ecgPacket;
	}

	public void setEcgPacket(EcgPacketEntity ecgPacket) {
		this.ecgPacket = ecgPacket;
	}

	public AccPacketEntity getAccPacket() {
		return accPacket;
	}

	public void setAccPacket(AccPacketEntity accPacket) {
		this.accPacket = accPacket;
	} 
	
	public boolean getIsFirstData() {
		return isFirstData;
	}
	public void setIsFirstData(boolean isFirstData) {
		this.isFirstData = isFirstData;
	}
	
	public byte[] getDataPacketTime() {
		return dataPacketTime;
	}
	public void setDataPacketTime(byte[] dataPacketTime) {
		this.dataPacketTime = dataPacketTime;
	}

	public boolean getIsHisDate() {
		return isHisDate;
	}

	public void setIsHisDate(boolean isHisDate) {
		this.isHisDate = isHisDate;
	}
	
	/**
	 * ��ȡ��ǰ���Ƿ��С�������ť���¼���־
	 * @return
	 */
	public byte getIsEventData(){
		try {
			byte temp = packetHead.packetStatus[0];
			return (byte)(temp & 0x01);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * ��ͷ
	 * 
	 * @author Administrator
	 * 
	 */
	public class DataPacketHeadEntity {
		public byte packetType;// ������
		public byte[] packetLength = new byte[2];// ������
		public byte[] packetId = new byte[4];// �����
		public byte[] packetStatus = new byte[2];// ��״̬
		public byte packetReserved;// �����ֶ�

		public DataPacketHeadEntity() {

		}

		public DataPacketHeadEntity(byte packetType, byte[] packetLength,
				byte[] packetId, byte[] packetStatus, byte packetReserved) {
			super();
			this.packetType = packetType;
			this.packetLength = packetLength;
			this.packetId = packetId;
			this.packetStatus = packetStatus;
			this.packetReserved = packetReserved;
		}

		public byte getPacketType() {
			return packetType;
		}

		public void setPacketType(byte packetType) {
			this.packetType = packetType;
		}

		public byte[] getPacketLength() {
			return packetLength;
		}  

		public void setPacketLength(byte[] packetLength) {
			this.packetLength = packetLength;
		}

		public byte[] getPacketId() {
			return packetId;
		}

		public void setPacketId(byte[] packetId) {
			this.packetId = packetId;
		}

		public byte[] getPacketStatus() {
			return packetStatus;
		}

		public void setPacketStatus(byte[] packetStatus) {
			this.packetStatus = packetStatus;
		}

		public byte getPacketReserved() {
			return packetReserved;
		}

		public void setPacketReserved(byte packetReserved) {
			this.packetReserved = packetReserved;
		}
		
		public byte[] getByteArray(){
			byte[] allByte = new byte[10];
			allByte[0] = packetType;
			System.arraycopy(packetLength, 0, allByte, 1, 2);
			System.arraycopy(packetId, 0, allByte, 3, 4);
			System.arraycopy(packetStatus, 0, allByte, 7, 2);
			allByte[9] = packetReserved;
			return allByte;
		}
	}

	/**
	 * ECG���ݰ�
	 * 
	 * @author Administrator
	 * 
	 */
	public class EcgPacketEntity {

		public byte[] ecgDataType = new byte[2];// ECG����Ƶ��
		public byte[] ecgDataLead = new byte[3];// ��ʶ�����ļ���ͨ��������
		public byte[] ecgDataLength = new byte[2];// ECG���ݳ���
		public byte ecgDataReserved;// �����ֶ�
		public byte[] ecgData = null;// ECG����

		public EcgPacketEntity(){
			
		}

		public EcgPacketEntity(byte[] ecgDataType, byte[] ecgDataLead,
				byte[] ecgDataLength, byte ecgDataReserved, byte[] ecgData) {
			super();
			this.ecgDataType = ecgDataType;
			this.ecgDataLead = ecgDataLead;
			this.ecgDataLength = ecgDataLength;
			this.ecgDataReserved = ecgDataReserved;
			this.ecgData = ecgData;
		}

		public byte[] getEcgDataType() {
			return ecgDataType;
		}

		public void setEcgDataType(byte[] ecgDataType) {
			this.ecgDataType = ecgDataType;
		}

		public byte[] getEcgDataLead() {
			return ecgDataLead;
		}

		public void setEcgDataLead(byte[] ecgDataLead) {
			this.ecgDataLead = ecgDataLead;
		}

		public byte[] getEcgDataLength() {
			return ecgDataLength;
		}

		public void setEcgDataLength(byte[] ecgDataLength) {
			this.ecgDataLength = ecgDataLength;
		}

		public byte getEcgDataReserved() {
			return ecgDataReserved;
		}

		public void setEcgDataReserved(byte ecgDataReserved) {
			this.ecgDataReserved = ecgDataReserved;
		}

		public byte[] getEcgData() {
			return ecgData;
		}

		public void setEcgData(byte[] ecgData) {
			this.ecgData = ecgData;
		}
		
		
		public byte[] getByteArray(){
			byte[] allByte = new byte[8+ecgData.length];
			System.arraycopy(ecgDataType, 0, allByte, 0, 2);
			System.arraycopy(ecgDataLead, 0, allByte, 2, 3);
			System.arraycopy(ecgDataLength, 0, allByte, 5, 2);
			System.arraycopy(ecgData, 0, allByte, 8, ecgData.length);
			
			return allByte;
		}

	}

	/**
	 * ACC���ݰ�
	 * 
	 * @author Administrator
	 * 
	 */
	public class AccPacketEntity {
		public byte[] accType = new byte[2];// ACC����Ƶ��
		public byte accStatus;// ACC�����ļ���������˶���
		public byte[] accLength = new byte[2];// ACC������ĳ���
		public byte accReserved;// �����ֶ�
		public byte[] accAxisX = null;//new byte[72];// ACC X����
		public byte[] accAxisY = null;//new byte[72];// ACC Y����
		public byte[] accAxisZ = null;//new byte[72];// ACC Z����
		public AccPacketEntity() {

		}
		public AccPacketEntity(byte[] accType, byte accStatus,
				byte[] accLength, byte accReserved, byte[] accAxisX,
				byte[] accAxisY, byte[] accAxisZ) {
			super();
			this.accType = accType;
			this.accStatus = accStatus;
			this.accLength = accLength;
			this.accReserved = accReserved;
			this.accAxisX = accAxisX;
			this.accAxisY = accAxisY;
			this.accAxisZ = accAxisZ;
		}



		public byte[] getAccType() {
			return accType;
		}

		public void setAccType(byte[] accType) {
			this.accType = accType;
		}

		public byte getAccStatus() {
			return accStatus;
		}

		public void setAccStatus(byte accStatus) {
			this.accStatus = accStatus;
		}

		public byte[] getAccLength() {
			return accLength;
		}

		public void setAccLength(byte[] accLength) {
			this.accLength = accLength;
		}

		public byte getAccReserved() {
			return accReserved;
		}

		public void setAccReserved(byte accReserved) {
			this.accReserved = accReserved;
		}
		
		public byte[] getAccAxisX() {
			return accAxisX;
		}



		public void setAccAxisX(byte[] accAxisX) {
			this.accAxisX = accAxisX;
		}



		public byte[] getAccAxisY() {
			return accAxisY;
		}



		public void setAccAxisY(byte[] accAxisY) {
			this.accAxisY = accAxisY;
		}



		public byte[] getAccAxisZ() {
			return accAxisZ;
		}



		public void setAccAxisZ(byte[] accAxisZ) {
			this.accAxisZ = accAxisZ;
		}



		public byte[] getByteArray(){
			byte[] allByte = new byte[8+accAxisX.length+accAxisY.length+accAxisZ.length];
			allByte[0] = accType[0];
			allByte[1] = accType[1];
			System.arraycopy(accType, 0, allByte, 0,2);
			allByte[2] = accStatus;
			allByte[3] = accLength[0];
			allByte[4] = accLength[1];
			System.arraycopy(accLength, 0, allByte, 3,2);
			allByte[5] = accReserved;
			System.arraycopy(accAxisX, 0, allByte, 6,accAxisX.length);
			System.arraycopy(accAxisY, 0, allByte, 6+accAxisX.length,accAxisY.length);
			System.arraycopy(accAxisZ, 0, allByte, 6+accAxisX.length+accAxisY.length,accAxisZ.length);
			return allByte;
		}
	}
	
	/**
	 * ��ԭ�ɲɼ����ɼ��������ݰ�
	 * @return
	 */
	public byte[] getByteArray(){

		byte[] packetHeadByte = packetHead.getByteArray();
		byte[] ecgPacketByte = ecgPacket.getByteArray();
		byte[] accPacketByte = accPacket.getByteArray();
		
		byte[] allByte = new byte[packetHeadByte.length+ecgPacketByte.length+accPacketByte.length];
		
		System.arraycopy(packetHeadByte,0,allByte,0,packetHeadByte.length); 
		System.arraycopy(ecgPacketByte ,0,allByte,packetHeadByte.length,ecgPacketByte.length); 
		System.arraycopy(accPacketByte, 0,allByte,packetHeadByte.length+packetHeadByte.length,accPacketByte.length); 
		
		return allByte;
	}


}


