package com.guanri.android.jpos.pad.bill99;

import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
import com.guanri.android.jpos.network.CryptionControl;
import com.guanri.android.jpos.pad.ServerDataHandlerFactory;
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
	 * 收到POS数据 ，上送服务器
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	@Override
	public JposPackageFather receivePosData(PosMessageBean posMessageBean){
		
		
		//return createQueryBalance(posMessageBean);
		
		//return createLogin99(posMessageBean);
		
		// 消费
		String Trank2 = "5264102500120211=1301123";
		String Trank3 = "";
		String CardNo = "5264102500120211";
		String pwdstr = "";
		String cardPeriod = "1301";
		int money = 10000;
		String datestr = "0910";
		String timestr = "175422";
		String orderNo = "000001";
		String userNo = "001";
		String billNo = "010001";
		return  createSale(Trank2, Trank3, CardNo, pwdstr, cardPeriod, money, 
				datestr, timestr, orderNo, userNo, billNo);
	}
	
	
	

	
	/**
	 * 构造查询余额数据
	 * @param posMessageBean 从POS获取的数据
	 */
	public JposPackageFather createQueryBalance(PosMessageBean posMessageBean){
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
		
		//得到模拟数据的mac值
//		byte[] mab = ServerDataHandlerFactory.geServerDataHandler().getPosMacBlock(null);
//		String makSource = (String)sendMap.get(11)+(String)sendMap.get(13)+(String)sendMap.get(12)+(String)sendMap.get(41);
//		byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
//		sendMap.put(64, mac);
		
		//设置消息类型
		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_MONEY);
		
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
		return jposPackage99Bill;
	}
	
	/**
	 * 构造签到方法
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createLogin99(PosMessageBean posMessageBean){
		
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
		
		JposPackage99Bill jposPackage = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage;
		
		
	}
	
	/**
	 * 消费
	 * @param Trank2 第二磁道
	 * @param Trank3 第三磁道
	 * @param CardNo 卡号, 主账号
	 * @param pwdstr 密码
	 * @param CardPeriod 卡有效期
 	 * @param money 金额
	 * @return
	 */
	public JposPackageFather createSale(String Trank2,String Trank3,String CardNo,String pwdstr,
			String cardPeriod,int money,String datestr,String timestr,String orderNo,
			String userNo,String billNo){
		//String CardNo = Trank.substring(0, 19);
		//判断POS输入类型
		String inputtype;
		if(Trank2.equals("")){
			if(pwdstr.equals(""))
				inputtype = "012";
			else
				inputtype = "011";
		}else{
			if(pwdstr.equals(""))
				inputtype = "021";
			else
				inputtype = "022";
		}
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(2, CardNo);
		sendMap.put(3, "000000");
		sendMap.put(4, "000000095439");
		sendMap.put(11, "011005");
		sendMap.put(12, timestr);
		sendMap.put(13, datestr);
		// 域14 卡有效期
		if(inputtype.equals("011")||inputtype.equals("012"))
			sendMap.put(14, cardPeriod);
		sendMap.put(22, "022");
		sendMap.put(24, "009");
		sendMap.put(25, "14");
		//if(!Trank2.equals(""))
			sendMap.put(35, "5264102500120211=1508201");
		//if(!Trank3.equals(""))
		//	sendMap.put(36,Trank3);
		// 域41 终端代码
		sendMap.put(41, MessageTypeDefine99Bill.POSID);
		// 域42 商户代码
		sendMap.put(42, MessageTypeDefine99Bill.CONTACT);
		// 域49  货币代码
		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
		// 自定义域 60 将来用于存放保单号
		
		
		//sendMap.put(60, "");
		// 处理61 域
		
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue(orderNo);
		data1.put(1,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue(userNo);
		data1.put(2,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("3");
		leaf.setValue(billNo);
		data1.put(3,leaf);
		
		sendMap.put(61, "000000001123460");	
		
		sendMap.put(64, "");
		
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		//设置消息头类型

		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)
		messageType.setPageLength((short)59);
		messageType.setId((byte)0x60);  
		messageType.setServerAddress("0000");
		messageType.setServerAddress("0000");
		messageType.setAddress("0090");
		messageType.setPagever("0100");
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_PAY_MONEY);
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill;
		
	}
	
		
}
