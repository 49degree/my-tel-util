package com.guanri.android.jpos.pos.data;

import java.util.Iterator;
import java.util.TreeMap;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
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
	
	/**
	 * 构造函数
	 * 
	 */
	public PosCommandParse(byte[] transferByte) throws CommandParseException{
		this.transferByte = transferByte;
		parseCommandBuffer();
	}
	
	/**
	 * 构造函数
	 * 
	 */
	public PosCommandParse(PosMessageBean posMessageBean) throws CommandParseException{
		this.posMessageBean = posMessageBean;
		parseCommandBean();
	}

	/**
	 * 由PosMessageBean对象解析为byte数据，用于构造POS上行数据
	 */
	private void parseCommandBean() throws CommandParseException{
		if(posMessageBean==null)
			throw new CommandParseException("input data is null");
	}

	/**
	 * 由byte数组解析数据对象，用于解析POS下行数据
	 */
	private void parseCommandBuffer() throws CommandParseException{
		if(transferByte==null||transferByte.length==0)
			throw new CommandParseException("input data is null");
		try{
			JposUnPackage99Bill bill = new JposUnPackage99Bill(transferByte);
			bill.unPacketed();
			TreeMap<Integer, Object>  tree = bill.getmReturnMap();
			Iterator<Integer> it = bill.getmReturnMap().keySet().iterator();
			while(it.hasNext()){
				int bitValue = it.next();
				logger.debug(bitValue+"::::::::::::::::::::::"+tree.get(bitValue));

			}
			posMessageBean = new PosMessageBean();
		}catch(PacketException e){
			throw new CommandParseException("parseCommandBuffer error");
		}
	}
	
	public byte[] getTransferByte() {
		return transferByte;
	}

	public PosMessageBean getPosMessageBean() {
		return posMessageBean;
	}
}
