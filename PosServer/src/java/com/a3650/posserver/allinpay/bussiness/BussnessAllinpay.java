/***********************************************************************
 * Module:  JposBussness.java
 * Author:  Administrator
 * Purpose: Defines the Interface JposBussness
 ***********************************************************************/

package com.a3650.posserver.allinpay.bussiness;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.allinpay.datapackage.DataInPackageAllinpay;
import com.a3650.posserver.allinpay.datapackage.DataMessageTypeAllinpay;
import com.a3650.posserver.allinpay.security.SecurityControlAllinpay;
import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.bean.CheckBatchInfo;
import com.a3650.posserver.core.bean.PayInfo;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Transactions;
import com.a3650.posserver.core.dao.impl.ApsaiOrderDao;
import com.a3650.posserver.core.dao.impl.CheckBatchInfoDao;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.init.InitContext;
import com.a3650.posserver.core.security.SecurityControl.SecurityControlException;
import com.a3650.posserver.core.security.SecurityUtils;
import com.a3650.posserver.core.service.impl.ApsaiOrderBuss;
import com.a3650.posserver.core.service.impl.ApsaiOrderPayInfoBuss;
import com.a3650.posserver.core.service.impl.CheckBatchInfoBuss;
import com.a3650.posserver.core.service.impl.PosTerminalBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;

/**
 *业务处理类 
 **/
public class BussnessAllinpay extends Bussness{
	static Logger logger =  Logger.getLogger(BussnessAllinpay.class);
	//  十组明文终端主密钥：
	//public final static byte[] keys = TypeConversion.hexStringToByte("D364FB15B07032B592FE0B8608B6C76EBF6885EFCB0426230DA2733846AB68492C76AEE6DAB3735404BF801AE5E583C78A0D922A769B4301B00D01C25E20DF134F4A0B3710B5DAE9DF2F32A7AB8334ECDFC19BE9BC34BA43D69B57AB1FEA25C2A19DFBDCE675F1DA38806ECEE6DC7F7A621FE5D08A9289312AC71040B0A2A4C8237073CDE60EDFAD6BBC6DEA376832E08607CB01BCDF3EC16491E901B9C81A19");
	
	public final static HashMap<Integer,Transactions> bussnessMap = new HashMap<Integer,Transactions>();
	public final static HashMap<Integer,Transactions> needMacMap = new HashMap<Integer,Transactions>();
	

	
	public static Transactions loginReceive = new Transactions(null,null,"0800",null);//签到
	public static Transactions loginBack = new Transactions(null,null,"0810",null);//签到返回
	
	public static Transactions queryOrderReceive = new Transactions("999999",null,"0220",null);//查询保单信息
	public static Transactions queryOrderBack = new Transactions("999999",null,"0230",null);//查询保单信息返回
	
	public static Transactions payOrderReceive = new Transactions("000000",null,"0200",null);//支付保单信息
	public static Transactions payOrderBack = new Transactions("000000",null,"0210",null);//支付保单信息返回
	
	public static Transactions checkBatchReceive = new Transactions(null,null,"0500",null);//批结算
	public static Transactions checkBatchBack = new Transactions(null,null,"0510",null);//批结算返回	
	
	public final static int loginReceiveHashCode = loginReceive.hashCode();
	public final static int queryOrderReceiveHashCode = queryOrderReceive.hashCode();
	public final static int payOrderReceiveHashCode = payOrderReceive.hashCode();
	public final static int checkBatchReceiveHashCode = checkBatchReceive.hashCode();

	
	static{
		//String transCode, String serverCode,
		//String msgTypeCode, String transTypeCode
		bussnessMap.put(loginReceiveHashCode, loginReceive);
		bussnessMap.put(queryOrderReceiveHashCode, queryOrderReceive);
		needMacMap.put(queryOrderReceiveHashCode, queryOrderReceive);
		needMacMap.put(payOrderReceiveHashCode, payOrderReceive);
		
	}

	public BussnessAllinpay(DataInPackage mDataInPackage){
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
		super.printDatas("收到数据",sendMap);
	}
	/**
	 * 输出数据
	 */
	public void printReturnDatas(){
		super.printDatas("返回数据",returnMap);
	}
   /**
    * 处理业务请求
    */
	@Override
	protected void handlerBussness() {
		printReceiveDatas();
		
		returnMessageType = DataMessageTypeAllinpay.getInstance();
		((DataMessageTypeAllinpay)returnMessageType).setCompanyId(((DataMessageTypeAllinpay)sendMessageType).getCompanyId());
		((DataMessageTypeAllinpay)returnMessageType).setPosId(((DataMessageTypeAllinpay)sendMessageType).getPosId());
		((DataMessageTypeAllinpay)returnMessageType).setAppType(((DataMessageTypeAllinpay)sendMessageType).getAppType());

		returnMap = new TreeMap<Integer, Object>();// sendMap.clone();

		//进行业务处理
		@SuppressWarnings("unchecked")
		TreeMap<Integer, DataSelfFieldLeaf> tlvData = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(60);
		if (transactionsCode == loginReceiveHashCode&&tlvData!=null&&tlvData.containsKey(3)) {
			if("001".equals(tlvData.get(3).getValue())||"003".equals(tlvData.get(3).getValue())){//签到
				returnMessageType.setMessageType(loginBack.getMsgTypeCode());//设置消息头类型
				login();
			}
		} else if (transactionsCode == queryOrderReceiveHashCode){//查询保单信息
			returnMessageType.setMessageType(queryOrderBack.getMsgTypeCode());//设置消息头类型
			queryOrder();
				
		} else if (transactionsCode == payOrderReceiveHashCode){//支付保单
			returnMessageType.setMessageType(queryOrderBack.getMsgTypeCode());//设置消息头类型
			payOrder();
				
		} else if (transactionsCode == checkBatchReceiveHashCode){//批结算
			returnMessageType.setMessageType(checkBatchBack.getMsgTypeCode());//设置消息头类型
			checkBatch();
		}else 
			return;
		
		
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
		returnPackage = new DataInPackageAllinpay(returnMap,returnMessageType);
	}
   

   /**
    * check in
    */
   private void login(){
		logger.info("login");
		PosTerminalCheckInDao checkInDao  = new PosTerminalCheckInBuss().getBaseDao();
		//验证终端
		PosTerminal posTerminal = new PosTerminalBuss().getBaseDao().get(((DataMessageTypeAllinpay)sendMessageType).getPosId());
		//登陆日志
		PosTerminalCheckIn checkIn = getReferenceNumber(((DataMessageTypeAllinpay)sendMessageType).getPosId());

		checkIn.setCheckInTime(Utils.getTimeString(new Date(), Utils.timeFormat));
		returnMap.put(12, Utils.getTimeString(new Date(),"HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(),"MMdd"));
		returnMap.put(32,InitContext.getAIIC());//受理方标识码(Acquiring Institution Identification Code)
		returnMap.put(37,checkIn.getReferenceNumber());//检索参考号(Retrieval Reference Number)		
		returnMap.put(39, "00");
		//构造返回密钥
		int rootKeyLength = posTerminal.getRootKeyLength();
		byte[] rootKeys = TypeConversion.hexStringToByte(posTerminal.getRootKey());
		byte keyIndex = (byte)(Math.random()*(rootKeys.length/rootKeyLength));//获取随机密钥
		
		checkIn.setRootKeyId(keyIndex);//保存ROOTKEY INDEX
		
		byte[] rootKey = new byte[rootKeyLength];
		System.arraycopy(rootKeys, keyIndex*rootKeyLength, rootKey, 0, rootKeyLength);
		
		byte[] keySecrit = new byte[rootKeyLength*3+12+1];
		keySecrit[0] = keyIndex;
		byte[] checkValue = new byte[8];
		//随机生成PIK
		byte[] pik = new byte[rootKeyLength];
		for(int i=0;i<pik.length;i++){
			pik[i] = (byte)((int)(Math.random()*256)&0xFF);
		}
		checkIn.setPik(TypeConversion.byte2hex(pik));//保存pik
		
		SecurityUtils securityUtils = new SecurityUtils();
		//加密PIK，并生成校验码
		if(rootKeyLength==8){
			checkValue = securityUtils.encryptoECB(checkValue, pik);
			pik = securityUtils.encryptoECB(pik, rootKey);
		}else if(rootKeyLength==16){
			checkValue = securityUtils.encryptoECBKey2(checkValue, pik);
			pik = securityUtils.encryptoECBKey2(pik, rootKey);
		}else if(rootKeyLength==32){
			checkValue = securityUtils.encryptoECBKey3(checkValue, pik);
			pik = securityUtils.encryptoECBKey3(pik, rootKey);
		}
		System.arraycopy(pik, 0, keySecrit, 1, rootKeyLength);
		System.arraycopy(checkValue, 0, keySecrit, rootKeyLength+1, 4);
		
		
		//随机生成mak
		byte[] mak = new byte[rootKeyLength];
		for(int i=0;i<8;i++){
			mak[i] = (byte)((int)(Math.random()*256)&0xFF);
		}
		checkIn.setMak(TypeConversion.byte2hex(mak));//保存mak
		//加密mak，并生成校验码
		checkValue = new byte[8];
		//“MAC工作密钥”前8个字节是密文，再16个字节是二进制零，后4个字节是checkvalue；前8个字节解出明文后，对8个数值0做单倍长密钥算法，取结果的前四位与checkvalue 的值比较应该是一致的
		byte[] maktemp = new byte[8];
		System.arraycopy(mak, 0, maktemp, 0, 8);
		checkValue = securityUtils.encryptoECB(checkValue, maktemp);
		if(rootKeyLength==8){
			mak = securityUtils.encryptoECB(mak, rootKey);
		}else if(rootKeyLength==16){
			mak = securityUtils.encryptoECBKey2(mak, rootKey);
		}else if(rootKeyLength==32){
			mak = securityUtils.encryptoECBKey3(mak, rootKey);
		}

		System.arraycopy(mak, 0, keySecrit, rootKeyLength+4+1, rootKeyLength);
		System.arraycopy(checkValue, 0, keySecrit, rootKeyLength*2+4+1, 4);
		
		
		//随机生成trk
		byte[] trk = new byte[rootKeyLength];
		for(int i=0;i<trk.length;i++){
			trk[i] = (byte)((int)(Math.random()*256)&0xFF);
		}
		checkIn.setTrk(TypeConversion.byte2hex(trk));//保存trk
		//加密trk，并生成校验码
		checkValue = new byte[8];
		if(rootKeyLength==8){
			checkValue = securityUtils.encryptoECB(checkValue, trk);
			trk = securityUtils.encryptoECB(trk, rootKey);
		}else if(rootKeyLength==16){
			checkValue = securityUtils.encryptoECBKey2(checkValue, trk);
			trk = securityUtils.encryptoECBKey2(trk, rootKey);
		}else if(rootKeyLength==32){
			checkValue = securityUtils.encryptoECBKey3(checkValue, trk);
			trk = securityUtils.encryptoECBKey3(trk, rootKey);
		}
		System.arraycopy(trk, 0, keySecrit, rootKeyLength*2+8+1, rootKeyLength);
		System.arraycopy(checkValue, 0, keySecrit, rootKeyLength*3+8+1, 4);
		checkIn.setSendEncodeKey(TypeConversion.byte2hex(keySecrit));
		
		//更新批次号
		if(sendMap.containsKey(60)){
			TreeMap<Integer,DataSelfFieldLeaf> data60 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(60);
			data60.get(2).setValue(checkIn.getBatchNumber());
			returnMap.put(60, data60);
		}
		
		
		returnMap.put(62, keySecrit);//返回KEY
		
		checkIn.setState('0');
		checkInDao.saveOrUpdate(checkIn);
   }

   /**
    * 保单查询
    */
	public void queryOrder() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = getReferenceNumber(((DataMessageTypeAllinpay)sendMessageType).getPosId());
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(32, InitContext.getAIIC());// 受理方标识码(Acquiring Institution Identification Code)
		returnMap.put(37, checkIn.getReferenceNumber());// 检索参考号(Retrieval  Reference Number)
		

		
		// 获取保单ID
		byte[] data46 = (byte[])sendMap.get(46);
		String orderInfo = TypeConversion.asciiToString(data46,37,data46.length-37);
		String[] orders = orderInfo.split("\\|");
		ApsaiOrderDao apsaiOrderDao = new ApsaiOrderBuss().getBaseDao();
		ApsaiOrder apsaiOrder = apsaiOrderDao.get(orders[0].trim());
		//判断保单是否已经支付
		ApsaiOrderPayInfoBuss apsaiOrderPayInfoBuss = new ApsaiOrderPayInfoBuss();
		if(apsaiOrder==null){
			returnMap.put(39, "95");
			returnMap.put(44, "无("+orders[0].trim()+")保单信息");
		}else if(apsaiOrderPayInfoBuss.checkIsPay(orders[0].trim())){////判断保单是否已经支付
			returnMap.put(39, "95");
			returnMap.put(44, "保单("+orders[0].trim()+")已经支付");
		}else{
			// 域46 自定义域
			TreeMap<Integer, DataSelfFieldLeaf> data = new TreeMap<Integer, DataSelfFieldLeaf>();
			DataSelfFieldLeaf jposf = new DataSelfFieldLeaf();
			// 交易类别：0A
			jposf.setLengthType(0);
			jposf.setMaxLength(2);
			jposf.setTag("1");
			jposf.setValue("0A");
			data.put(1, jposf);
			// 账单标首：016
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(20);
			jposf.setTag("2");
			String para2 = "016";
			for (int i = 20 - para2.length(); i > 0; i--) {
				para2 += " ";
			}
			jposf.setValue(para2);
			data.put(2, jposf);
			// 交易类型:01
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(2);
			jposf.setTag("3");
			jposf.setValue("01");
			data.put(3, jposf);
			// 交易代码
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("4");
			jposf.setLengthType(0);
			jposf.setMaxLength(10);
			jposf.setValue("0230810180");
			data.put(4, jposf);

			int leafLength = 0;
			// 保单号
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("6");
			jposf.setLengthType(0);
			jposf.setMaxLength(33);
			String para37 = orders[0]+"|";
			logger.debug("POS发送过来的保单号:" + para37);
			jposf.setValue(para37);
			data.put(6, jposf);
			leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;
			// 投保人姓名
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("7");
			jposf.setLengthType(0);
			jposf.setMaxLength(4);
			jposf.setValue(apsaiOrder.getCustomName() + "|");
			data.put(7, jposf);
			leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;
			// 投保人地址
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("8");
			jposf.setLengthType(0);
			jposf.setMaxLength(8);
			jposf.setValue(apsaiOrder.getCustomAddr()+ "|");
			data.put(8, jposf);
			leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;
			// 投保人联系方式
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("9");
			jposf.setLengthType(0);
			jposf.setMaxLength(8);
			jposf.setValue(apsaiOrder.getCustomLink()+ "|");
			data.put(9, jposf);
			leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;
			// 保单金额
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("10");
			jposf.setLengthType(0);
			jposf.setMaxLength(8);
			jposf.setValue(String.valueOf(apsaiOrder.getApsaiAmount()));
			data.put(10, jposf);
			leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;

			// 结束符
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("11");
			jposf.setLengthType(0);
			jposf.setMaxLength(1);
			jposf.setValue("#");
			data.put(11, jposf);
			//leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;
			

			// 子域长度
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(3);
			jposf.setTag("5");
			if(leafLength<10){
				jposf.setValue("00"+leafLength);
			}else if(leafLength<100){
				jposf.setValue("0"+leafLength);
			}else{
				jposf.setValue(String.valueOf(leafLength));
			}
			data.put(5, jposf);
			
			// 第46域 自定义域
			returnMap.put(46, data);
			
			returnMap.put(39, "00");
		}
		

		
		
		checkInDao.saveOrUpdate(checkIn);
	}

	/**
	 * 支付保单
	 */
	public void payOrder() {

		
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = getReferenceNumber(((DataMessageTypeAllinpay)sendMessageType).getPosId());

		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(32, InitContext.getAIIC());// 受理方标识码(Acquiring Institution
											// Identification Code)
		returnMap.put(37, checkIn.getReferenceNumber());// 检索参考号(Retrieval
														// Reference Number)

		// 获取保单ID
		byte[] data46 = (byte[]) sendMap.get(46);
		String orderInfo = TypeConversion.asciiToString(data46, 37,
				data46.length - 37);
		String orderId = orderInfo.substring(0, 32);
		ApsaiOrderDao apsaiOrderDao = new ApsaiOrderBuss().getBaseDao();
		ApsaiOrder apsaiOrder = apsaiOrderDao.get(orderId.trim());
		//判断保单是否已经支付
		ApsaiOrderPayInfoBuss apsaiOrderPayInfoBuss = new ApsaiOrderPayInfoBuss();
		if(apsaiOrder==null){
			returnMap.put(39, "95");
			returnMap.put(44, "无("+orderId.trim()+")保单信息");
		}else if(apsaiOrderPayInfoBuss.checkIsPay(orderId.trim())){////判断保单是否已经支付
			returnMap.put(39, "95");
			returnMap.put(44, "保单("+orderId.trim()+")已经支付");
		} else {

			//判断PIN码
			if(sendMap.containsKey(52)){
				//验证PIN码
				SecurityControlAllinpay securityControl = null;
				try {
					securityControl = new SecurityControlAllinpay(((DataMessageTypeAllinpay)sendMessageType).getPosId());//构造安全控制对象
					if(!securityControl.checkPin(dataInPackage)){
						returnMap.put(39, "99");
						returnMap.put(44, "");
						return;
					}
				} catch (SecurityControlException e) {
					e.printStackTrace();
					returnMap.put(39, "95");
					returnMap.put(44, "");
					return;
				}
			}
			
			
			
			//附加响应数据(Additional Response Data)
			String createBankInfo = "03080000";//发卡行编号
			String getBankInfo = "03080000";//收单行
			returnMap.put(44, createBankInfo+getBankInfo);
			
			// 域46 自定义域
			TreeMap<Integer, DataSelfFieldLeaf> data = new TreeMap<Integer, DataSelfFieldLeaf>();
			DataSelfFieldLeaf jposf = new DataSelfFieldLeaf();
			// 交易类别：0A
			jposf.setLengthType(0);
			jposf.setMaxLength(2);
			jposf.setTag("1");
			jposf.setValue("0A");
			data.put(1, jposf);
			// 账单标首：016
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(20);
			jposf.setTag("2");
			String para2 = "016";
			for (int i = 20 - para2.length(); i > 0; i--) {
				para2 += " ";
			}
			jposf.setValue(para2);
			data.put(2, jposf);
			// 交易类型:01
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(2);
			jposf.setTag("3");
			jposf.setValue("01");
			data.put(3, jposf);
			// 交易代码
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("4");
			jposf.setLengthType(0);
			jposf.setMaxLength(10);
			jposf.setValue("0210000000");
			data.put(4, jposf);

			int leafLength = 0;
			// 保单号
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("6");
			jposf.setLengthType(0);
			jposf.setMaxLength(33);
			String para37 = orderId;
			logger.debug("POS发送过来的保单号:" + para37);
			jposf.setValue(para37);
			data.put(6, jposf);
			leafLength += TypeConversion.stringToAscii(jposf.getValue()).length;

			// 结束符
			jposf = new DataSelfFieldLeaf();
			jposf.setTag("7");
			jposf.setLengthType(0);
			jposf.setMaxLength(1);
			jposf.setValue("#");
			data.put(7, jposf);
			// leafLength+=TypeConversion.stringToAscii(jposf.getValue()).length;

			// 子域长度
			jposf = new DataSelfFieldLeaf();
			jposf.setLengthType(0);
			jposf.setMaxLength(3);
			jposf.setTag("5");
			if (leafLength < 10) {
				jposf.setValue("00" + leafLength);
			} else if (leafLength < 100) {
				jposf.setValue("0" + leafLength);
			} else {
				jposf.setValue(String.valueOf(leafLength));
			}
			data.put(5, jposf);

			// 第46域 自定义域
			returnMap.put(46, data);

			returnMap.put(39, "00");
			
			PayInfo payInfo = new PayInfo();
			payInfo.setPosId(((DataMessageTypeAllinpay)sendMessageType).getPosId());
			payInfo.setPosOrderId((String)sendMap.get(11));
			payInfo.setReferenceNum(checkIn.getReferenceNumber());
			payInfo.setAddTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			payInfo.setPayState(PayInfo.PayInfoState.insure.value());
			payInfo.setBatchNumber(checkIn.getBatchNumber());
			payInfo.setInsureTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			payInfo.setPayAmount(apsaiOrder.getApsaiAmount());
			
			apsaiOrderPayInfoBuss.savePayInfo(apsaiOrder,payInfo);
		}

		checkInDao.saveOrUpdate(checkIn);
	}
	
	/**
	 * 批结算
	 */
	public void checkBatch() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = getReferenceNumber((String)sendMap.get(41));
		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(32, InitContext.getAIIC());// 受理方标识码(Acquiring Institution
											// Identification Code)
		returnMap.put(37, checkIn.getReferenceNumber());// 检索参考号(Retrieval
														// Reference Number)
			
		@SuppressWarnings("unchecked")
		TreeMap<Integer,DataSelfFieldLeaf> data60 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(60);
		if(data60==null||!data60.containsKey(2)){
			returnMap.put(39, "95");
			returnMap.put(44, "批次号错误");
		}else{
			String batchNumber = data60.get(2).getValue();
			
			CheckBatchInfoBuss checkBatchInfoBuss = new CheckBatchInfoBuss();
			long[] batchInfo = checkBatchInfoBuss.getBatchInfo(batchNumber);
			logger.info(Arrays.toString(batchInfo));
			//CheckBatchInfo.CheckBatchResult checkResult = CheckBatchInfo.CheckBatchResult.normal;
			@SuppressWarnings("unchecked")
			String data48 = (String)sendMap.get(48);
			
			//通联批结算数据长度和文档不一致（48域）变成N12,N4,N12,N4,N12,N3,N12,N3 ,本来应该是N12,N3,N12,N3,N1,N12,N3,N12,N3,N1
			long payAmount = Long.parseLong(data48.substring(0,12));
			int payNum = Integer.parseInt(data48.substring(12,16));
			long backAmount = Long.parseLong(data48.substring(16,28));
			int backNum = Integer.parseInt(data48.substring(28,32));
			logger.info(payAmount+":"+payNum+":"+backAmount+":"+backNum);
			
			CheckBatchInfoDao checkBatchInfoDao = new CheckBatchInfoBuss().getBaseDao();
			CheckBatchInfo checkBatchInfo = new CheckBatchInfo();
			
			if(batchInfo[0] !=payNum|| batchInfo[1]!=payAmount){
				checkBatchInfo.setCheckResult(CheckBatchInfo.CheckBatchResult.less.value());
				returnMap.put(44, "对账不平");
			}else{
				checkBatchInfo.setCheckResult(CheckBatchInfo.CheckBatchResult.normal.value());
				returnMap.put(44, "交易成功");
			}
			

			checkBatchInfo.setBatchNumber(batchNumber);
			checkBatchInfo.setPayAmount(payAmount);
			checkBatchInfo.setPayNum(payNum);
			checkBatchInfo.setBackAmount(backAmount);
			checkBatchInfo.setBackNum(backNum);
			checkBatchInfo.setCheckTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			checkBatchInfoDao.save(checkBatchInfo);
			
			//更新批次号
			long newBatchNumber = Long.parseLong(checkIn.getBatchNumber())+1;
			String headerStr = "000000";
			checkIn.setBatchNumber(headerStr.substring(String.valueOf(newBatchNumber).length())+newBatchNumber);
			data60.get(2).setValue(checkIn.getBatchNumber());
			
			returnMap.put(60, data60);
			returnMap.put(39, "00");
			

		}
		checkInDao.saveOrUpdate(checkIn);
	}
}