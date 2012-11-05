/***********************************************************************
 * Module:  OperatorCenterBill99.java
 * Author:  Administrator
 * Purpose: Defines the Class OperatorCenterBill99
 ***********************************************************************/

package com.a3650.posserver.bill99.init;

import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.bill99.bussiness.BussnessBill99;
import com.a3650.posserver.bill99.datapackage.DataInPackageBill99;
import com.a3650.posserver.bill99.datapackage.DataMessageTypeBill99;
import com.a3650.posserver.bill99.datapackage.DataUnPackageBill99;
import com.a3650.posserver.bill99.security.SecurityControlBill99;
import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.exception.MacErrorPacketException;
import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.init.OperatorCenter;
import com.a3650.posserver.core.service.impl.CompanyBuss;
import com.a3650.posserver.core.service.impl.PosTerminalBuss;


public class OperatorCenterBill99 extends OperatorCenter {
	static Logger logger = Logger.getLogger(OperatorCenterBill99.class);
	public OperatorCenterBill99(Client client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 验证终端合法性
	 */
	protected boolean checkPosTerminal() {
		
		// TODO Auto-generated method stub
		//验证商户是否正确
		Company company = new CompanyBuss().getBaseDao().get((String)dataUnPackage.getMapValue(42));// new CompanyDao().get((String)dataUnPackage.getMapValue(42));
		//验证终端是否正确
		PosTerminal posTerminal = new PosTerminalBuss().getBaseDao().get((String)dataUnPackage.getMapValue(41));
		if(company==null){
			DataMessageTypeBill99 returnMessageType = DataMessageTypeBill99.getInstance();
			returnMessageType.setMessageType(dataUnPackage.getMessageType().getMessageType());
			TreeMap<Integer, Object> returnMap = (TreeMap<Integer, Object>) dataUnPackage.getDataMap().clone();
			returnMap.put(39, "03");
			returnMap.put(44, JposConstantBill99.BILL99_RESULT_TYPE_CODE.get("03"));
			dataInPackage =  new DataInPackageBill99(returnMap,returnMessageType);//构造业务处理对象
			return false;
		}else if(posTerminal==null||!company.getCompanyId().equals(posTerminal.getCompanyId())){
			DataMessageTypeBill99 returnMessageType = DataMessageTypeBill99.getInstance();
			returnMessageType.setMessageType(dataUnPackage.getMessageType().getMessageType());
			TreeMap<Integer, Object> returnMap = (TreeMap<Integer, Object>) dataUnPackage.getDataMap().clone();
			returnMap.put(39, "04");
			returnMap.put(44, JposConstantBill99.BILL99_RESULT_TYPE_CODE.get("04"));
			dataInPackage =  new DataInPackageBill99(returnMap,returnMessageType);//构造业务处理对象
			
			return false;
		}else
			return true;
	}
	
	@Override
	public byte[] operator(byte[] buffer){
		try{
			dataUnPackage = new DataUnPackageBill99(buffer);//解析报文
			dataUnPackage.unPacketed();
			
			if(!checkPosTerminal()){//验证终端合法性
				logger.info("checkPosTerminal");
				return dataInPackage.packaged();
			}
			dataInPackage =  new DataInPackageBill99(dataUnPackage.getDataMap(),dataUnPackage.getMessageType());//构造业务处理对象
			//构造业务处理对象
			jposBussness = new BussnessBill99(dataInPackage);
			//校验MAC
			if(jposBussness.needCheckMac()){
				//构造安全控制对象
				securityControl = new SecurityControlBill99((String)dataInPackage.getMapValue(41));
				((SecurityControlBill99)securityControl).parseWorkKey(dataInPackage);
				
				if(!securityControl.checkMac(dataInPackage)){//maccuo
					logger.debug("MAC校验错误");
					throw new MacErrorPacketException("MAC校验错误");
				}
			}
			
			//处理业务
			DataInPackage returnPackage = jposBussness.handler();
			
			//构造数据签名
			if(jposBussness.needCheckMac()&&securityControl!=null){
				((SecurityControlBill99)securityControl).parseWorkKey(returnPackage);
				byte[] returnMac = securityControl.getMac(returnPackage.getMabSource());
				returnPackage.setMac(returnMac);
			}
			//返回数据
			byte[] reutrnBuffer = returnPackage.packaged();
			return reutrnBuffer;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}