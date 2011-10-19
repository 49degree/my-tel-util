package com.xys.ecg.bean;

public class UploadEcgDataHeader {
	
	public byte[] rqSendBusinessCode;//2byte ����ҵ�����(2bytes)
	public byte[] rqSendFunctionCode;//2byte ��������
	public byte[] dataBeginTime;//8bytes	�ĵ����ݿ�ʼ�ɼ���ʱ��
	public byte[] dataLenght;//4bytes	���ݳ���
	public byte headerLength=12;//4bytes	���ݳ���
	
	
	
	public byte getHeaderLength() {
		return this.headerLength;
	}

	public byte[] getEcgDataHeaderBytes(){
		byte[] ecgDataHeaderBytes = new byte[12];
		System.arraycopy(dataBeginTime, 0, ecgDataHeaderBytes, 0, dataBeginTime.length);
		System.arraycopy(dataLenght, 0, ecgDataHeaderBytes, dataBeginTime.length, dataLenght.length);
		return ecgDataHeaderBytes;
	}
	
	public UploadEcgDataHeader(){
		
	}
	public UploadEcgDataHeader(byte[] rqSendBusinessCode,
			byte[] rqSendFunctionCode, byte[] dataBeginTime, byte[] dataLenght) {
		super();
		this.rqSendBusinessCode = rqSendBusinessCode;
		this.rqSendFunctionCode = rqSendFunctionCode;
		this.dataBeginTime = dataBeginTime;
		this.dataLenght = dataLenght;
	}

	public byte[] getRqSendBusinessCode() {
		return rqSendBusinessCode;
	}

	public void setRqSendBusinessCode(byte[] rqSendBusinessCode) {
		this.rqSendBusinessCode = rqSendBusinessCode;
	}

	public byte[] getRqSendFunctionCode() {
		return rqSendFunctionCode;
	}

	public void setRqSendFunctionCode(byte[] rqSendFunctionCode) {
		this.rqSendFunctionCode = rqSendFunctionCode;
	}
	
	public byte[] getDataBeginTime() {
		return dataBeginTime;
	}
	public void setDataBeginTime(byte[] dataBeginTime) {
		this.dataBeginTime = dataBeginTime;
	}
	public byte[] getDataLenght() {
		return dataLenght;
	}
	public void setDataLenght(byte[] dataLenght) {
		this.dataLenght = dataLenght;
	}


	
	
}
