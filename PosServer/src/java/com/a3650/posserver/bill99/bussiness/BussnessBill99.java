/***********************************************************************
 * Module:  JposBussness.java
 * Author:  Administrator
 * Purpose: Defines the Interface JposBussness
 ***********************************************************************/

package com.a3650.posserver.bill99.bussiness;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.bill99.datapackage.DataInPackageBill99;
import com.a3650.posserver.bill99.datapackage.DataMessageTypeBill99;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.bussiness.Transactions;
import com.a3650.posserver.core.datapackage.DataInPackage;

/**
 *业务处理类 
 **/
public class BussnessBill99 extends Bussness{
	static Logger logger =  Logger.getLogger(BussnessBill99.class);
	public final static HashMap<Integer,Transactions> bussnessMap = new HashMap<Integer,Transactions>();
	public final static HashMap<Integer,Transactions> needMacMap = new HashMap<Integer,Transactions>();
	public final static HashMap<Integer,Transactions> needEncodeMap = new HashMap<Integer,Transactions>();
	private String bussType = null;
	private Operator operator = null;
	
	public static Transactions loginReceive = new Transactions("990000",null,"0800",null);//签到
	public static Transactions loginBack = new Transactions("990000",null,"0810",null);//签到返回
	
	public static Transactions queryOrderReceive = new Transactions("340000",null,"0200",null);//查询保单信息
	public static Transactions queryOrderBack = new Transactions("340000",null,"0210",null);//查询保单信息返回
	
	public static Transactions payOrderReceive = new Transactions("000000",null,"0200",null);//支付保单信息
	public static Transactions payOrderBack = new Transactions("000000",null,"0210",null);//支付保单信息返回
	
	public static Transactions payInsureReceive = new Transactions("880000",null,"0800",null);//交易回执
	public static Transactions payInsureBack = new Transactions("880000",null,"0810",null);//交易回执返回	
	
	public static Transactions checkBatchReceive = new Transactions(null,null,"0500",null);//批结算
	public static Transactions checkBatchBack = new Transactions(null,null,"0510",null);//批结算返回	
	
	public static Transactions payBackReceive = new Transactions("000000",null,"0400",null);//冲正
	public static Transactions payBackBack = new Transactions("000000",null,"0410",null);//冲正返回	
	
	public final static int loginReceiveHashCode = loginReceive.hashCode();
	public final static int queryOrderReceiveHashCode = queryOrderReceive.hashCode();
	public final static int payOrderReceiveHashCode = payOrderReceive.hashCode();
	public final static int payInsureReceiveHashCode = payInsureReceive.hashCode();
	public final static int checkBatchReceiveHashCode = checkBatchReceive.hashCode();
	public final static int payBackReceiveHashCode = payBackReceive.hashCode();
	static{
		//String transCode, String serverCode,
		//String msgTypeCode, String transTypeCode
		bussnessMap.put(loginReceiveHashCode, loginReceive);
		bussnessMap.put(queryOrderReceiveHashCode, queryOrderReceive);
		needMacMap.put(queryOrderReceiveHashCode, queryOrderReceive);
		needMacMap.put(payOrderReceiveHashCode, payOrderReceive);
		needMacMap.put(payInsureReceiveHashCode, payInsureReceive);
		needMacMap.put(payBackReceiveHashCode, payBackReceive);
	}

	public BussnessBill99(DataInPackage mDataInPackage){
		super(mDataInPackage);
	}
	
	/**
	 * 判断是否需要校验MAC
	 * @return
	 */
	@Override
	public boolean needCheckMac(){
		return needMacMap.containsKey(transactionsCode);
	}
	
	/**
	 * 输出数据
	 */
	public void printReceiveDatas(){
		logger.info(bussType+"业务开始====================================");
		printMsgHeaderDatas("收到",(DataMessageTypeBill99)sendMessageType);
		super.printDatas("收到数据",sendMap);
	}
	/**
	 * 输出数据
	 */
	public void printReturnDatas(){
		printMsgHeaderDatas("返回",(DataMessageTypeBill99)returnMessageType);
		super.printDatas("返回数据",returnMap);
		logger.info(bussType+"业务完成-----------------------------------");
	}
	
	/**
	 * 输出包头数据
	 */
	public void printMsgHeaderDatas(String type,DataMessageTypeBill99 msgType){
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)+0000(消息类型码)
		logger.info(type+"TPDU："+(msgType.getId()>>4)+(msgType.getId()&0x0F)+msgType.getToAddress()+msgType.getSourceAddress());
		logger.info(type+"报文版本号："+msgType.getVersionNo());
		logger.info(type+"消息类型码："+msgType.getMessageType());
	}
	
   /**
    * 处理业务请求
    */
	@Override
	protected void handlerBussness() {
		
		
		returnMessageType = DataMessageTypeBill99.getInstance();
		returnMap = new TreeMap<Integer, Object>();// sendMap.clone();

		//进行业务处理
		if (transactionsCode == loginReceiveHashCode) {
			returnMessageType.setMessageType(loginBack.getMsgTypeCode());//设置消息头类型
			bussType="签到";
			operator = new PosLogin(this);
		} else if (transactionsCode == queryOrderReceiveHashCode){//查询保单信息
			returnMessageType.setMessageType(queryOrderBack.getMsgTypeCode());//设置消息头类型
			bussType="查询保单信息";
			operator = new QueryApsaiOrder(this);	
		} else if (transactionsCode == payOrderReceiveHashCode){//支付保单
			returnMessageType.setMessageType(queryOrderBack.getMsgTypeCode());//设置消息头类型
			bussType="消费";
			operator = new PayOrder(this);
		} else if (transactionsCode == payInsureReceiveHashCode){//交易回执
			returnMessageType.setMessageType(payInsureBack.getMsgTypeCode());//设置消息头类型
			bussType="交易回执";
			operator = new InsurePayInfo(this);
		} else if (transactionsCode == checkBatchReceiveHashCode){//批结算
			returnMessageType.setMessageType(checkBatchBack.getMsgTypeCode());//设置消息头类型
			bussType="批结算";
			operator = new BatchPayInfo(this);
		} else if (transactionsCode == payBackReceiveHashCode){//冲正
			returnMessageType.setMessageType(payBackBack.getMsgTypeCode());//设置消息头类型
			bussType="冲正";
			operator = new BackPayInfo(this);			
		}else {
			bussType="未知";
			printReceiveDatas();
			return;
		}
		
		printReceiveDatas();
		operator.excute();
		
		// 遍历返回数据
		if(returnMap.containsKey(39)){
			Iterator<Integer> iterator = sendMap.keySet().iterator();
			while (iterator.hasNext()) {
				Integer key = iterator.next();
				if(key!=52&&key!=53&&!returnMap.containsKey(key)){//52,53域不用返回
					returnMap.put(key, sendMap.get(key));
				}
			}
		}

		printReturnDatas();
		returnPackage = new DataInPackageBill99(returnMap,returnMessageType);
	}
}