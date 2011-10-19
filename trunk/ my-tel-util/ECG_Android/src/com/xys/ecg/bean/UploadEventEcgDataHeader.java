package com.xys.ecg.bean;

public class UploadEventEcgDataHeader extends UploadEcgDataHeader{
	public byte eventType ;//	1byte	�¼����ͱ�ţ����ֻ�����ݣ���Ϊ0��
	public byte[] event2getDataMoveTime; ////	4bytes	�¼�����������ڲɼ����ĵ����ݵ�ʱ��ƫ����(����)
	public byte[] event2eventDataMoveTime; //	4bytes	�¼���������¼����ݵ�ʱ��ƫ����(����)
	public byte dataType; //	1byte	ǰ�������ݱ�ʶ��0Ϊǰ������ݣ�1Ϊ��������
	
	public byte headerLength=22;//4bytes	���ݳ���
	
	


	public byte getHeaderLength() {
		return this.headerLength;
	}


	/**
	 * //	1byte	�¼����ͱ�ţ����ֻ�����ݣ���Ϊ0��
	 * //	8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ��
	 * //	4bytes	�¼�����������ڲɼ����ĵ����ݵ�ʱ��ƫ����(����)
	 * //	4bytes	�¼���������¼����ݵ�ʱ��ƫ����(����)
	 * //	1byte	ǰ�������ݱ�ʶ��0Ϊǰ������ݣ�1Ϊ��������
	 * //	4bytes	���ݳ���
	 */
	
	public byte[] getEcgDataHeaderBytes(){
		byte[] ecgDataHeaderBytes = new byte[22];
		int lenIndex = 0;
		ecgDataHeaderBytes[0] = eventType;
		lenIndex+=1;
		System.arraycopy(dataBeginTime, 0, ecgDataHeaderBytes, lenIndex, dataBeginTime.length);
		lenIndex+=dataBeginTime.length;
		System.arraycopy(event2getDataMoveTime, 0, ecgDataHeaderBytes,lenIndex, event2getDataMoveTime.length);
		lenIndex+=event2getDataMoveTime.length;
		
		System.arraycopy(event2eventDataMoveTime, 0, ecgDataHeaderBytes, lenIndex, event2eventDataMoveTime.length);
		lenIndex+=event2eventDataMoveTime.length;
		
		ecgDataHeaderBytes[lenIndex] = dataType;
		lenIndex+=1;
		System.arraycopy(dataLenght, 0, ecgDataHeaderBytes, lenIndex, dataLenght.length);
		
		return ecgDataHeaderBytes;
	}
	
	public UploadEventEcgDataHeader(){
		super();
	}
	public UploadEventEcgDataHeader(byte[] rqSendBusinessCode,
			byte[] rqSendFunctionCode, byte[] dataBeginTime, byte[] dataLenght,
			byte eventType, byte[] event2getDataMoveTime,
			byte[] event2eventDataMoveTime, byte dataType) {
		super(rqSendBusinessCode, rqSendFunctionCode, dataBeginTime, dataLenght);
		this.eventType = eventType;
		this.event2getDataMoveTime = event2getDataMoveTime;
		this.event2eventDataMoveTime = event2eventDataMoveTime;
		this.dataType = dataType;
	}
	
	
	public byte getEventType() {
		return eventType;
	}
	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}
	public byte[] getEvent2getDataMoveTime() {
		return event2getDataMoveTime;
	}
	public void setEvent2getDataMoveTime(byte[] event2getDataMoveTime) {
		this.event2getDataMoveTime = event2getDataMoveTime;
	}
	public byte[] getEvent2eventDataMoveTime() {
		return event2eventDataMoveTime;
	}
	public void setEvent2eventDataMoveTime(byte[] event2eventDataMoveTime) {
		this.event2eventDataMoveTime = event2eventDataMoveTime;
	}
	public byte getDataType() {
		return dataType;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}
	
	
}
