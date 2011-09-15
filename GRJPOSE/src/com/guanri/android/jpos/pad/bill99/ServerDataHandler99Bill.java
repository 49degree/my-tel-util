package com.guanri.android.jpos.pad.bill99;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.constant.JposConstant;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
import com.guanri.android.jpos.pad.ServerDataHandlerImp;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

public class ServerDataHandler99Bill implements ServerDataHandlerImp{
	final Logger logger = new Logger(ServerDataHandler99Bill.class);
	public static ServerDataHandler99Bill instance = null;
	public static ServerDataHandler99Bill getInstance(){
		if(instance==null){
			instance = new ServerDataHandler99Bill(); 
		}
		return instance;
	}
	
	
	/**
	 * 构造封包对象
	 */
	public JposPackageFather createJposPackage(TreeMap<Integer, Object> sendMap, JposMessageType messageType){
		return new JposPackage99Bill(sendMap,messageType);
	}
	
	/**
	 * 收到服务器下发数据 
	 * 进行相应的协议服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	public JposUnPackageFather receiveServerData(byte[] receiveData) throws PacketException{
		//解析数据
		try{
			JposUnPackage99Bill jposUnPackage = new JposUnPackage99Bill(receiveData);
			return jposUnPackage;
		}catch(PacketException e){
			//返回失败信息
			throw e;
		}
	}
	
	/**
	 * 构造返回POS的数据对象
	 * transaction 原报文
	 * getMap 返回报文
	 */
	public TTransaction createBackPosObject(TTransaction rtTransaction,TreeMap<Integer, Object> getMap, JposMessageType messageType){
		TTransaction tTransaction = new TTransaction();
		// 检查发送到服务器的数据与服务器返回的 商户号与终端好是否一直
		//if((rtTransaction.ProcessList.TerminalID().GetAsString().equals(getMap.get(41)))
		//		&&(rtTransaction.ProcessList.MerchantID().GetAsString().equals(getMap.get(42))))
		// 检查数据合法性
		if (!chackData(rtTransaction,getMap)){
			return tTransaction;
		}
		
		// 签到
		if ((messageType.getMessageType().equals(
				MessageTypeDefine99Bill.RESPONSE_POS_CHECK_IN))&&
				messageType.getTransactionCode().equals("99")) {
			// 解析签到放回报文
			tTransaction = ServerDataUnPackage99Bill.UnPackageLogin(rtTransaction, getMap, messageType);

		}
		// 余额查询
		if((messageType.getMessageType().equals(MessageTypeDefine99Bill.RESPONSE_OP_QUERY_MONEY))
				&&(messageType.getTransactionCode().equals("31"))){
			tTransaction = ServerDataUnPackage99Bill.UnPackageQuery(rtTransaction, getMap, messageType);
		}
		// 消费
		if((messageType.getMessageType().equals(MessageTypeDefine99Bill.RESPONSE_OP_PAY_MONEY))
			&&(messageType.getTransactionCode().equals("00"))){
			
			tTransaction = ServerDataUnPackage99Bill.UnPackageSale(rtTransaction, getMap, messageType);
		}
		
		// 保单查询
		if((messageType.getMessageType().equals(MessageTypeDefine99Bill.RESPONSE_OP_QUERY_INSURANCE))
			&&(messageType.getTransactionCode().equals("32"))){
			
			tTransaction = ServerDataUnPackage99Bill.UnPagckageQueryOQS(rtTransaction, getMap, messageType);
		}
		// 查询后消费返回报文解析
		if((messageType.getMessageType().equals(MessageTypeDefine99Bill.RESPONSE_OP_PAY_MONEY))
				&&(messageType.getTransactionCode().equals("32"))
				&&(rtTransaction.TransCode().GetAsString().equals("601"))){
			tTransaction = ServerDataUnPackage99Bill.UnPackageQueryOQSSale(rtTransaction, getMap, messageType);
		}
		return tTransaction;
	}
	
	private boolean chackData(TTransaction rtTransaction, TreeMap<Integer, Object> getMap) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * 收到POS数据 ，上送服务器
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	@Override
	public JposPackageFather receivePosData(TTransaction ttransaction){
		
		JposPackageFather jposPackageFather = null;
		//TTransaction ttransaction = new TTransaction();
		switch (ttransaction.TransCode().GetAsInteger()) {
		case JposConstant.POS_TRANCE_CODE_QUERY_BALANCE:
			// 余额查询
			jposPackageFather = createQueryBalance(ttransaction);
			break;
		case JposConstant.POS_TRANCE_CODE_PAY:
			// 消费
			jposPackageFather = createSale(ttransaction);
			break;
			// 退货
		case JposConstant.POS_TRANCE_CODE_BACK_ORDER:
			
			break;
		case 400:
			// 撤销
			//jposPackageFather = createCanelSale(ttransaction);
			break;
		case 1:
			// 签到
			jposPackageFather = createLogin99(ttransaction);
			break;
		case 4:
			// 冲正
			
			break;
		case 6:
			// 批结算
			
			break;
		case 600:
			// 订单查询
			jposPackageFather = createOQSDate(ttransaction);
		  break;
		case 601:
			// 查询后消费
			jposPackageFather = createOQSSale(ttransaction);
			break;
			
		case 7:  // 交易回执
		case 8:
			jposPackageFather = createSaleReceipt(ttransaction);
			break;
		default:
		
			break;
		}
		return jposPackageFather;
	}
	
	

	/**
	 * 构造查询余额测试方式
	 * @param posMessageBean 从POS获取的数据
	 */
	public JposPackageFather createQueryBalanceTest(TTransaction posMessageBean){
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
	 * 构造查询余额数据
	 * @param posMessageBean 从POS获取的数据
	 */
	public JposPackageFather createQueryBalance(TTransaction posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		//根据POS得到的数据构造sendMap对象
		sendMap.put(2, posMessageBean.ProcessList.GetPAN());
		// 域3 处理码
		sendMap.put(3, "310000");
		// 域11 流水号
		sendMap.put(11, posMessageBean.SerialNumber().GetAsString());
		// 域 12 本地交易时间
		sendMap.put(12, posMessageBean.Time().GetAsString());
		// 域13 本地交易日期
		sendMap.put(13, posMessageBean.Date().GetAsString());
		
		sendMap.put(22, "022");
				
		// 域24 NII
		sendMap.put(24,"009");
		// 域25 服务店条件码
		sendMap.put(25, "00");
		// 域35 2磁道数据
		sendMap.put(35, posMessageBean.ProcessList.GetTrack2Data());
		if(!posMessageBean.ProcessList.GetTrack3Data().equals(""))
			sendMap.put(36, posMessageBean.ProcessList.GetTrack3Data());
		// 域41 终端代码
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		// 域49  货币代码
		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
		//域41 原交易信息域
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue(posMessageBean.ProcessList.OrderNumber().GetAsString());
		data1.put(1,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue(posMessageBean.ProcessList.UserID().GetAsString());
		data1.put(2,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("3");
		leaf.setValue("000000");
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
	public JposPackageFather createLogin99(TTransaction posMessageBean){
		
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(3, "990000");
		// 域11 流水号
		if(posMessageBean.SerialNumber().GetAsString().equals(""))
			sendMap.put(11, "011089");
		else
			sendMap.put(11, posMessageBean.SerialNumber().GetAsString());	
		// 域 12 本地交易时间
		sendMap.put(12, posMessageBean.Time().GetAsString());
		// 域13 本地交易日期
		sendMap.put(13, posMessageBean.Date().GetAsString());
		sendMap.put(24, "009");
		// 域41 终端代码
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		
		
		
		// 域61 自定义域      61.1 批次号  000001 网络管理信息码 001
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue(posMessageBean.ProcessList.OrderNumber().GetAsString());
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
	 * 构造签到测试方法
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createLogin99Test(TTransaction posMessageBean){
		
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
	 * 消费测试
	 * @param Trank2 第二磁道
	 * @param Trank3 第三磁道
	 * @param CardNo 卡号, 主账号
	 * @param pwdstr 密码
	 * @param CardPeriod 卡有效期
 	 * @param money 金额
	 * @return
	 */
	public JposPackageFather createSaleTest(String Trank2,String Trank3,String CardNo,String pwdstr,
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
		sendMap.put(2, "5264102500120211");
		sendMap.put(3, "000000");
		sendMap.put(4, "000000008837");
		sendMap.put(11, "011007");
		sendMap.put(12, "174217");
		sendMap.put(13, "0912");
		// 域14 卡有效期
		//if(inputtype.equals("011")||inputtype.equals("012"))
		//	sendMap.put(14, cardPeriod);
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
		
		sendMap.put(61, data1);	
		
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
	
	/**
	 * 消费
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createSale(TTransaction posMessageBean){
		//String CardNo = Trank.substring(0, 19);
		//判断POS输入类型
		String inputtype;
		/*
		if(posMessageBean.ProcessList.GetTrack2Data().equals("")){
			if(posMessageBean.ProcessList.PINData().GetAsString().equals(""))
				inputtype = "012";
			else
				inputtype = "011";
		}else{
			if(posMessageBean.ProcessList.PINData().GetAsString().equals(""))
				inputtype = "021";
			else
				inputtype = "022";
		}
		*/
		inputtype = "022";
		
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		//System.out.println("主帐号："+ posMessageBean.ProcessList.GetPAN());
		sendMap.put(2, posMessageBean.ProcessList.GetPAN());
		sendMap.put(3, "000000");
		sendMap.put(4, posMessageBean.ProcessList.SaleAmount().GetAsString());
		sendMap.put(11, posMessageBean.SerialNumber().GetAsString());
		sendMap.put(12, posMessageBean.Time().GetAsString());
		sendMap.put(13, posMessageBean.Date().GetAsString());
		// 域14 卡有效期
		//if(posMessageBean.)
		//	sendMap.put(14, cardPeriod);
		sendMap.put(22, inputtype);
		sendMap.put(24, "009");
		sendMap.put(25, "14");
		//if(!posMessageBean.ProcessList.GetTrack2Data().equals(""))
		//	sendMap.put(35, "5264102500120211=1508201");
		//else
			sendMap.put(35, posMessageBean.ProcessList.GetTrack2Data());
		//if(!Trank3.equals(""))
		//	sendMap.put(36,Trank3);
		// 域41 终端代码
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		// 域49  货币代码
		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
		// 自定义域 60 将来用于存放保单号
		/*
		if(!posMessageBean.ProcessList.PINData().GetAsString().equals(""))
			sendMap.put(52, posMessageBean.ProcessList.PINData().GetData());
		*/
		//sendMap.put(60, "");
		// 处理61 域
		
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue("000001");
		data1.put(1,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue(posMessageBean.ProcessList.UserID().GetAsString());
		data1.put(2,leaf);
		
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("3");
		leaf.setValue("000000");
		data1.put(3,leaf);
		
		sendMap.put(61, data1);	
		
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
	
	/**
	 * 查询后消费
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createOQSSale(TTransaction posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 域2 主账号
		logger.debug("POS发送过来的主账号"+posMessageBean.ProcessList.GetPAN());
		sendMap.put(2, posMessageBean.ProcessList.GetPAN());
		// 域3 处理码
		sendMap.put(3, "000000");
		// 域4  交易金额
		sendMap.put(4, posMessageBean.ProcessList.ReturnAmount().GetAsString());
		// 域11 POS流水号 
		sendMap.put(11, posMessageBean.SerialNumber().GetAsString());
		// 域12   时间
		sendMap.put(12, posMessageBean.Time().GetAsString());
		// 域13 日期
		sendMap.put(13, posMessageBean.Date().GetAsString());
		// 域14 卡有效期
		//if(posMessageBean.)
		//	sendMap.put(14, cardPeriod);
		// 域22 POS输入方式  
		sendMap.put(22, "022");
		// 域24 NII
		sendMap.put(24, "009");
		// 域25 服务点条件码
		sendMap.put(25, "14");
		// 域35 第二磁道数  
		sendMap.put(35, posMessageBean.ProcessList.GetTrack2Data());
		// 域36 第三磁道数
		if(!posMessageBean.ProcessList.GetTrack2Data().equals(""))
			sendMap.put(36,posMessageBean.ProcessList.GetTrack2Data());
		// 域41 终端代码
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		// 域49  货币代码
		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
		//---------------------------------------------------------------------
		// 域52 个人识别码  暂时不用
		if((posMessageBean.ProcessList.PINData().GetData() != null)&&(false))
			sendMap.put(52, posMessageBean.ProcessList.PINData().GetData());
		//---------------------------------------------------------------------
		// 域57 显示信息 充保险公司后台查询得到的保单信息
		if(false)
			sendMap.put(57, "");
		//---------------------------------------------------------------------
		// 自定义域 60 将来用于存放保单号  待完善
		sendMap.put(60, posMessageBean.ProcessList.ReturnOrderNumber().GetAsString());
		// 处理61 域
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		logger.debug("Pos发送的批次号"+posMessageBean.ProcessList.OrderNumber().GetAsString());
		leaf.setValue(posMessageBean.ProcessList.OrderNumber().GetAsString());
		data1.put(1,leaf);
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		logger.debug("Pos发送的批次号"+posMessageBean.ProcessList.UserID().GetAsString());
		leaf.setValue(posMessageBean.ProcessList.UserID().GetAsString());
		data1.put(2,leaf);
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("3");
		// 域61.3 票据号 暂时只送000000
		leaf.setValue("000000");
		data1.put(3,leaf);
		sendMap.put(61, data1);	
		// MAC
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
	
	/**
	 * 交易回执
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createSaleReceipt(TTransaction posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 域3  处理码
		sendMap.put(3, "880000");
		// 第11域  POS流水号
		logger.debug("POS发送过来的流水号:"+posMessageBean.SerialNumber().GetAsString());
		sendMap.put(11, posMessageBean.SerialNumber().GetAsString());
		// 第12域  时间
		logger.debug("POS发送过来的时间:"+posMessageBean.Time().GetAsString());
		sendMap.put(12, posMessageBean.Time().GetAsString());
		// 第13域  
		logger.debug("POS发送过来的时间:"+posMessageBean.Date().GetAsString());
		sendMap.put(13, posMessageBean.Date().GetAsString());
		// 第24域  NII
		sendMap.put(24, "009");
		// 第25域  服务点条件码
		sendMap.put(25, "14");
		// 第37域 系统参考号 由后台系统定位原交易
		//----------------------------------
		if(false)
			sendMap.put(37, "");
		// 第41域  终端号
		logger.debug("POS发送过来的终端号:"+posMessageBean.ProcessList.TerminalID().GetAsString());
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		logger.debug("POS发送过来的终端号:"+posMessageBean.ProcessList.MerchantID().GetAsString());
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		// 域46 TVL
		ArrayList<JposSelfFieldLeaf> datalist = new ArrayList<JposSelfFieldLeaf>();
		JposSelfFieldLeaf jposf = new JposSelfFieldLeaf();
		jposf.setTag("1");
		jposf.setValue("20"); //表示交易回执		
		datalist.add(jposf);
		sendMap.put(46, datalist);
		// 处理61 域
		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("1");
		logger.debug("POS发送过来的交易批次号:"+posMessageBean.ProcessList.OrderNumber().GetAsString());
		leaf.setValue(posMessageBean.ProcessList.OrderNumber().GetAsString());
		data1.put(1,leaf);
		leaf = new JposSelfFieldLeaf();
		leaf.setTag("2");
		// 网管交易类型
		leaf.setValue("001");
		data1.put(2,leaf);
		sendMap.put(61, data1);	
		// 域64 MAC
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
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_POS_RECEIPTSALE);
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill;
		
	}
	/**
	 * OQS查询订单  根据订单号去保险后台查询相应的保单信息返回
	 * @param posMessageBean
	 * @return
	 */
	public JposPackageFather createOQSDate(TTransaction posMessageBean){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 第3域    处理码
		sendMap.put(3, "340000");
		// 第11域  POS流水号
		logger.debug("POS发送过来的流水号:"+posMessageBean.SerialNumber().GetAsString());
		sendMap.put(11, posMessageBean.SerialNumber().GetAsString());
		// 第22域  POS输入方式
		sendMap.put(22, "012");
		// 第24域  NII
		sendMap.put(24, "009");
		// 第25域  服务点条件码
		sendMap.put(25, "14");
		// 第41域  终端号
		logger.debug("POS发送过来的终端号:"+posMessageBean.ProcessList.TerminalID().GetAsString());
		sendMap.put(41, posMessageBean.ProcessList.TerminalID().GetAsString());
		// 域42 商户代码
		logger.debug("POS发送过来的商户号:"+posMessageBean.ProcessList.MerchantID().GetAsString());
		sendMap.put(42, posMessageBean.ProcessList.MerchantID().GetAsString());
		// 域49  货币代码
		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
		// 域60 订单号
		logger.debug("POS发送过来的订单号:"+posMessageBean.ProcessList.OrderNumber().GetAsString());
		sendMap.put(60, posMessageBean.ProcessList.OrderNumber().GetAsString());
		// 域64 MAC
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
		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_QUERY_INSURANCE);
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill;
	}
	/**
	 * 构造消费撤销数据
	 * @param posMessageBean 从POS获取的数据
	 */
//	public JposPackageFather createCanelSale(TTransaction posMessageBean){
//		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
//		//根据POS得到的数据构造sendMap对象
//		sendMap.put(2, "5264102500120211");//posMessageBean.ProcessList.GetPAN());
//		// 域3 处理码
//		sendMap.put(3, "200000");
//		// 域4  交易金额
//		sendMap.put(4, "000000008837");
//		// 域11 流水号
//		sendMap.put(11, "011006");//posMessageBean.SerialNumber().GetAsString());
//		// 域 12 本地交易时间
//		sendMap.put(12, "093622");//posMessageBean.Time().GetAsString());
//		// 域13 本地交易日期
//		sendMap.put(13, "0913");//posMessageBean.Date().GetAsString());
//		// 域22 POS输入方式
//		sendMap.put(22, "022");			
//		// 域24 NII
//		sendMap.put(24,"009");
//		// 域25 服务店条件码
//		sendMap.put(25, "14");
//		// 域35 2磁道数据
//		//if(posMessageBean.ProcessList.GetTrack2Data()!=null)
//			sendMap.put(35,  "5264102500120211=1508201");//posMessageBean.ProcessList.GetTrack2Data());
//		// 域36 3磁道数据
//		//if(posMessageBean.ProcessList.GetTrack3Data()!=null)
//		//	sendMap.put(36, posMessageBean.ProcessList.GetTrack3Data());
//		// 域38  授权码 原来消费的授权码
//		//if(posMessageBean.ProcessList.GetTrack2Data()!=null)
//			sendMap.put(38, "575432");//posMessageBean.ProcessList.GetTrack2Data());
//		// 域41 终端代码
//		sendMap.put(41, MessageTypeDefine99Bill.POSID);
//		// 域42 商户代码
//		sendMap.put(42, MessageTypeDefine99Bill.CONTACT);
//		// 域49  货币代码
//		sendMap.put(49, MessageTypeDefine99Bill.RMBCODE);
//		// 域52 个人识别码  密码
//		//if(!posMessageBean.ProcessList.PINData().GetIsEmpty())
//		//	sendMap.put(53, "");//posMessageBean.ProcessList.PINData().GetAsString());
//		//if(!posMessageBean.ProcessList.OrderNumber().GetIsEmpty())
//		//	sendMap.put(60, "");posMessageBean.ProcessList.OrderNumber().GetAsString());
//		
//		//域61 原交易信息域
//		TreeMap<Integer,JposSelfFieldLeaf> data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
//		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
//		// 60.1 批次号
//		leaf.setTag("1");
//		leaf.setValue("000001");
//		data1.put(1,leaf);
//		// 60.2 操作员号
//		leaf = new JposSelfFieldLeaf();
//		leaf.setTag("2");
//		leaf.setValue("001");//posMessageBean.ProcessList.UserID().GetAsString());
//		data1.put(2,leaf);
//		// 60.3 票据号
//		leaf = new JposSelfFieldLeaf();
//		leaf.setTag("3");
//		leaf.setValue("000000");
//		data1.put(3,leaf);
//		sendMap.put(61, data1);//原交易信息域
//		// 62 原始交易数据
//		data1 = new TreeMap<Integer,JposSelfFieldLeaf>();
//		leaf = new JposSelfFieldLeaf();
//		// 62.1 信息类型码
//		leaf.setTag("1");
//		leaf.setValue("0200");//posMessageBean.ProcessList.UserID().GetAsString());
//		data1.put(1,leaf);
//		leaf.setTag("2");
//		leaf.setValue("011005");//posMessageBean.ProcessList.UserID().GetAsString());
//		data1.put(2, leaf);
//		leaf.setTag("3");
//		leaf.setValue("0912174217");//posMessageBean.ProcessList.UserID().GetAsString());
//		sendMap.put(62, data1);
//		sendMap.put(64, "");
//		//sendMap.put(62, posMessageBean.ProcessList.UserID().GetAsString());
//		//得到模拟数据的mac值
////		byte[] mab = ServerDataHandlerFactory.geServerDataHandler().getPosMacBlock(null);
////		String makSource = (String)sendMap.get(11)+(String)sendMap.get(13)+(String)sendMap.get(12)+(String)sendMap.get(41);
////		byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
////		sendMap.put(64, mac);
//		
//		//设置消息类型
//		JposMessageType99Bill messageType = JposMessageType99Bill.getInstance();
//		messageType.setMessageType(MessageTypeDefine99Bill.REQUEST_OP_PAY_CANCEL);
//		
//		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
//		return jposPackage99Bill;
//	}
		
	
}
