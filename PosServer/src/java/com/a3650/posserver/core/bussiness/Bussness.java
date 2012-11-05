/***********************************************************************
 * Module:  JposBussness.java
 * Author:  Administrator
 * Purpose: Defines the Interface JposBussness
 ***********************************************************************/

package com.a3650.posserver.core.bussiness;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.datapackage.DataMessageType;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.TypeConversion;

public abstract class Bussness {
	private static Logger logger = Logger.getLogger(Bussness.class);
	protected DataInPackage dataInPackage;
	protected Transactions transactions = null;
	protected int transactionsCode = 0;
	protected TreeMap<Integer, Object> sendMap = null;
	protected DataMessageType sendMessageType = null;// 消息头
	
	protected DataInPackage returnPackage = null;
	protected TreeMap<Integer, Object> returnMap = null;
	protected DataMessageType returnMessageType = null;// 消息头
	
	public Bussness(DataInPackage mDataInPackage){
		dataInPackage = mDataInPackage;
		sendMap = dataInPackage.getDataMap();
		sendMessageType = dataInPackage.getMessageType();
		
		transactions = new Transactions();
		transactions.setMsgTypeCode(dataInPackage.getMessageType().getMessageType());
		transactions.setTransCode((String)dataInPackage.getMapValue(3));
		transactionsCode = transactions.hashCode();
	}

	/**
	 * 判断是否需要校验MAC
	 * 
	 * @return
	 */
	public abstract boolean needCheckMac();


	protected abstract void handlerBussness();
	
	/**
	 * 计算交易参考号
	 * @param posId
	 * @return
	 */
	public PosTerminalCheckIn getReferenceNumber(String posId){
		PosTerminalCheckInDao checkInDao  = new PosTerminalCheckInBuss().getBaseDao();
		
		PosTerminalCheckIn checkIn = checkInDao.get(posId);
		if(checkIn==null){
			checkIn = new PosTerminalCheckIn();
			checkIn.setPosId(posId);
			checkIn.setReferenceNumber("000000000001");
			checkIn.setBatchNumber("000001");
			checkIn.setSysAuditNumber("000001");
		}else{
			long temp = 0;
			try{
				temp = Long.parseLong(checkIn.getReferenceNumber())+1;
				if(temp<1)
					temp = 1;
			}catch(Exception e){
				temp = 1;
			}

			String headerStr = "000000000000";
			checkIn.setReferenceNumber(headerStr.substring(String.valueOf(temp).length())+temp);
			
			long sysAuditNumber = 0;
			try{
				sysAuditNumber = Long.parseLong(checkIn.getSysAuditNumber())+1;
				if(sysAuditNumber<1)
					sysAuditNumber = 1;
			}catch(Exception e){
				sysAuditNumber = 1;
			}
			headerStr = "000000";
			checkIn.setSysAuditNumber(headerStr.substring(String.valueOf(sysAuditNumber).length())+sysAuditNumber);
		}

		return checkIn;
	}	
	/**
	 * 处理业务请求
	 */
	public DataInPackage handler() {
		// 进行业务处理
		handlerBussness();
		return returnPackage;
	}
	
	/**
	 * 输出数据
	 * @param msgHeader
	 * @param map
	 */
	protected void printDatas(String msgHeader,TreeMap<Integer,Object> map){
		// 遍历获取数据
		Iterator<Integer> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			Object o = map.get(key);
			if (o instanceof TreeMap) {
				TreeMap<Integer, DataSelfFieldLeaf> tlvData = (TreeMap<Integer, DataSelfFieldLeaf>) o;
				Iterator<Integer> tlvIt = tlvData.keySet().iterator();
				Integer key2 =  key;
				while (tlvIt.hasNext()) {
					key = tlvIt.next();
					logger.info(msgHeader+":"+key2+":"+ key + ":" + tlvData.get(key).getTag()+ ":" + tlvData.get(key).getValue());
				}
			}else if(o instanceof List){
				List<DataSelfFieldLeaf> tlvDatas = (List<DataSelfFieldLeaf>) o;
				int index = 1;
				for (DataSelfFieldLeaf tlvIt:tlvDatas) {
					logger.info(msgHeader+":"+key+":"+ (index++) + ":" + tlvIt.getTag()+ ":" + tlvIt.getValue());
				}
			}else if (o instanceof byte[]) {
				logger.info(msgHeader+":" + key + ":"+ TypeConversion.byte2hex((byte[])o));
			} else {
				logger.info(msgHeader+":" + key + ":"+o);
			}
		}
	}

	public TreeMap<Integer, Object> getSendMap() {
		return sendMap;
	}

	public DataMessageType getSendMessageType() {
		return sendMessageType;
	}

	public TreeMap<Integer, Object> getReturnMap() {
		return returnMap;
	}

	public DataMessageType getReturnMessageType() {
		return returnMessageType;
	}

	public DataInPackage getDataInPackage() {
		return dataInPackage;
	}

	
}