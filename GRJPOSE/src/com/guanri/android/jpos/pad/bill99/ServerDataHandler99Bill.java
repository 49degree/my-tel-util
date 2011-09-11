package com.guanri.android.jpos.pad.bill99;

import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
import com.guanri.android.jpos.pad.ServerDataHandlerImp;

public class ServerDataHandler99Bill implements ServerDataHandlerImp{
	public static ServerDataHandler99Bill instance = null;
	public static ServerDataHandler99Bill getInstance(){
		if(instance==null){
			instance = new ServerDataHandler99Bill(); 
		}
		return instance;
	}
	
	/**
	 * 收到POS数据 
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	@Override
	public byte[] receivePosData(PosMessageBean posMessageBean){
		
		
		return createQueryBalance(posMessageBean);
		
		//return createLogin99(posMessageBean);
	}
	
	/**
	 * 解析数据包
	 * @param sendMap
	 * @param messageType
	 * @return
	 */
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
		sendMap.put(2, "5264102500120211");
		// 域3 处理码
		sendMap.put(3, "310000");
		// 域11 流水号
		sendMap.put(11, "011089");
		// 域 12 本地交易时间
		sendMap.put(12, "102945");
		// 域13 本地交易日期
		sendMap.put(13, "0909");
		sendMap.put(22, "022");
				
		// 域24 NII
		sendMap.put(24,"009");
		// 域25 服务店条件码
		sendMap.put(25, "00");
		// 域35 2磁道数据
		sendMap.put(35, "5264102500120211=1508201");
		// 域41 终端代码
		sendMap.put(41, "20100601");
		// 域42 商户代码
		sendMap.put(42, "104110045110012");
		// 域49  货币代码
		sendMap.put(49, "156");
		//域41 原交易信息域
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue("000000");
		data1.put(1,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue("001");
		data1.put(2,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("3");
		leaf.setValue("123542");
		data1.put(3,leaf);
		sendMap.put(61, data1);//原交易信息域
		sendMap.put(64, null);
		//设置消息类型
		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_MONEY);
		return parseSubmitData(sendMap,messageType);
	}
	
	/**
	 * 构造签到方法
	 * @param posMessageBean
	 * @return
	 */
	public byte[] createLogin99(PosMessageBean posMessageBean){
		
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(3, "990000");
		// 域11 流水号
		// 域11 流水号
		sendMap.put(11, "011089");
		// 域 12 本地交易时间
		sendMap.put(12, "102945");
		// 域13 本地交易日期
		sendMap.put(13, "0909");
		sendMap.put(24, "009");
		// 域41 终端代码
		sendMap.put(41, MessageTypeDefine99Bill.POSID);
		// 域42 商户代码
		sendMap.put(42, MessageTypeDefine99Bill.CONTACT);
		
		// 域61 自定义域      61.1 批次号  000001 网络管理信息码 001
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue("000001");
		data1.put(1,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue(MessageTypeDefine99Bill.NETMSGCODE);
		data1.put(2, leaf);
		sendMap.put(61, data1);
		
		// 消息头
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		messageType.setPageLength((short)59);
		messageType.setId((byte)0x60);  
		messageType.setServerAddress("0000");
		messageType.setServerAddress("0000");
		messageType.setAddress("0090");
		messageType.setPagever("0100");
		
		//设置消息头类型
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_POS_CHECK_IN);
		
		JposPackage99Bill jposPackageUnionPay = new JposPackage99Bill(sendMap,messageType);
	 
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
