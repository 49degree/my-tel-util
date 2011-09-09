package com.guanri.android.jpos.pad.bill99;

import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.PosCommandParse;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;

public class ServerDataHandler99Bill{
	public static ServerDataHandler99Bill instance = null;
	public static ServerDataHandler99Bill getInstance(){
		if(instance==null){
			instance = new ServerDataHandler99Bill(); 
		}
		return instance;
	}
	
	public byte[] parseSubmitData(TreeMap<Integer,Object> sendMap,JposMessageType99Bill messageType){
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
		return jposPackage99Bill.packaged();
	}
	
	
	/**
	 * 构造查询余额数据
	 * @param posMessageBean 从POS获取的数据
	 */
	public byte[] createQueryBalance(PosMessageBean posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		//根据POS得到的数据构造sendMap对象
		//.....................
		
		
		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
		//设置消息类型
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_MONEY);
		return parseSubmitData(sendMap,messageType);
	}
	
	/**
	 * 解析查询余额数据，构造发送到POS的数据
	 * @param posMessageBean 原始POS数据
	 * @param serverData 服务器返回数据
	 * @return
	 */
	public byte[] parseQueryBalance(PosMessageBean posMessageBean,byte[] serverData){
		try{
			JposUnPackage99Bill bill = new JposUnPackage99Bill(serverData);
			bill.unPacketed();
			TreeMap<Integer, Object>  treeMap = bill.getMReturnMap();
			JposMessageType mMessageType = bill.getMMessageType();

			//根据服务器得到的数据构造返回POS的数据
			//.....................
			
			
		}catch(PacketException e){
			
		}
		return null;
	}
	
	/**
	 * 构造签到数据
	 * @param posCommandParse 从POS获取的数据
	 */
	public byte[] createCheckIn(PosMessageBean posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		//根据POS得到的数据构造sendMap对象
		//.....................
		
		
		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
		//设置消息类型
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_MONEY);
		return parseSubmitData(sendMap,messageType);
	}
	
	/**
	 * 解析签到数据，构造发送到POS的数据
	 * @param posMessageBean 原始POS数据
	 * @param serverData 服务器返回数据
	 * @return
	 */
	public byte[] parseCheckIn(PosMessageBean posMessageBean,byte[] serverData){
		try{
			JposUnPackage99Bill bill = new JposUnPackage99Bill(serverData);
			bill.unPacketed();
			TreeMap<Integer, Object>  treeMap = bill.getMReturnMap();
			JposMessageType mMessageType = bill.getMMessageType();

			//根据服务器得到的数据构造返回POS的数据
			//.....................
			
			
		}catch(PacketException e){
			
		}
		return null;
	}	
	
	/**
	 * 构造消费数据
	 * @param posCommandParse 从POS获取的数据
	 */
	public byte[] createPay(PosMessageBean posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		//根据POS得到的数据构造sendMap对象
		//.....................
		
		
		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
		//设置消息类型
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_MONEY);
		return parseSubmitData(sendMap,messageType);
	}
	
	/**
	 * 解析消费数据，构造发送到POS的数据
	 * @param posMessageBean 原始POS数据
	 * @param serverData 服务器返回数据
	 * @return
	 */
	public byte[] parsePay(PosMessageBean posMessageBean,byte[] serverData){
		try{
			JposUnPackage99Bill bill = new JposUnPackage99Bill(serverData);
			bill.unPacketed();
			TreeMap<Integer, Object>  treeMap = bill.getMReturnMap();
			JposMessageType mMessageType = bill.getMMessageType();

			//根据服务器得到的数据构造返回POS的数据
			//.....................
			
			
		}catch(PacketException e){
			
		}
		return null;
	}		
}
