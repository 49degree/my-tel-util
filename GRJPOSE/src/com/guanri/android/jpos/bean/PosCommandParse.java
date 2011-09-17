package com.guanri.android.jpos.bean;


import com.guanri.android.exception.CommandParseException;
import com.guanri.android.lib.log.Logger;

/**
 * 下载命令解析
 * 
 * @author Administrator
 * 
 */
public class PosCommandParse {
	public static Logger logger = Logger.getLogger(PosCommandParse.class);//日志对象
	private byte[] transferByte;//交互的数据
	private PosMessageBean posMessageBean;//消息内容对象
	
	private String packageHeader = null;//包头
	private int packageLength = 0;//长度  长度为高在前，低在后
	private byte[] message = null;//消息内容
	private byte[] checkData = null;//校验和：从包头到MAC（包含包头和MAC字段）所有数据的异或值
	

	
	/**
	 * 构造函数
	 * 
	 */
	public PosCommandParse(byte[] transferByte) throws CommandParseException{
		this.transferByte = transferByte;
		parseCommandBuffer();
	}
	
	/**
	 * 由byte数组解析数据对象，用于解析POS下行数据
	 */
	private void parseCommandBuffer() throws CommandParseException{
		if(transferByte==null||transferByte.length==0)
			throw new CommandParseException("input data is null");
		
		posMessageBean = new PosMessageBean();
	}
	
	public byte[] getTransferByte() {
		return transferByte;
	}

	public PosMessageBean getPosMessageBean() {
		return posMessageBean;
	}

	public String getPackageHeader() {
		return packageHeader;
	}

	public void setPackageHeader(String packageHeader) {
		this.packageHeader = packageHeader;
	}

	public int getPackageLength() {
		return packageLength;
	}

	public void setPackageLength(int packageLength) {
		this.packageLength = packageLength;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public byte[] getCheckData() {
		return checkData;
	}

	public void setCheckData(byte[] checkData) {
		this.checkData = checkData;
	}

	public void setTransferByte(byte[] transferByte) {
		this.transferByte = transferByte;
	}

	public void setPosMessageBean(PosMessageBean posMessageBean) {
		this.posMessageBean = posMessageBean;
	}
	
	
}

