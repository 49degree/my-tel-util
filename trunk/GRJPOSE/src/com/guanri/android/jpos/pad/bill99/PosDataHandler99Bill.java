package com.guanri.android.jpos.pad.bill99;

import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant;
import com.guanri.android.jpos.pad.PosDataHandlerImp;

public class PosDataHandler99Bill implements PosDataHandlerImp{
	public static PosDataHandlerImp instance = null;
	public static PosDataHandlerImp getInstance(){
		if(instance==null){
			instance = new PosDataHandler99Bill(); 
		}
		return instance;
	}
	
	/**
	 * 收到POS数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	public synchronized void receiveData(PosMessageBean posMessageBean){
		//根据收到的信息进行编码，发送到服务器
		switch(posMessageBean.getMessageType()){
		case JposConstant.PosMessageType.CMD_CODE_MEMO:// 交易报文
			parseCodeMemo();
			break;
		case JposConstant.PosMessageType.TRANSFER_MESSAGE:// 交易报文
			parseTransferMessage();
			break;
		case JposConstant.PosMessageType.DATA_TRANSFER:// 数据传输
			parseDataTransfer();
			break;
		}
		
		
	}
	
	public byte[] parseCodeMemo(){
		return null;
	}
	
	public byte[] parseTransferMessage(){
		return null;
	}
	
	public byte[] parseDataTransfer(){
		return null;
	}
	

}
