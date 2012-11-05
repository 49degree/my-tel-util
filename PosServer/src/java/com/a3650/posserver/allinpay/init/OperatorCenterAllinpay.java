/***********************************************************************
 * Module:  OperatorCenterBill99.java
 * Author:  Administrator
 * Purpose: Defines the Class OperatorCenterBill99
 ***********************************************************************/

package com.a3650.posserver.allinpay.init;

import org.apache.log4j.Logger;

import com.a3650.posserver.allinpay.bussiness.BussnessAllinpay;
import com.a3650.posserver.allinpay.datapackage.DataInPackageAllinpay;
import com.a3650.posserver.allinpay.datapackage.DataMessageTypeAllinpay;
import com.a3650.posserver.allinpay.datapackage.DataUnPackageAllinpay;
import com.a3650.posserver.allinpay.security.SecurityControlAllinpay;
import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.exception.MacErrorPacketException;
import com.a3650.posserver.core.init.Client;
import com.a3650.posserver.core.init.OperatorCenter;
import com.a3650.posserver.core.service.impl.CompanyBuss;
import com.a3650.posserver.core.service.impl.PosTerminalBuss;
import com.a3650.posserver.core.utils.TypeConversion;


public class OperatorCenterAllinpay extends OperatorCenter {
	static Logger logger = Logger.getLogger(OperatorCenterAllinpay.class);
	public OperatorCenterAllinpay(Client client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 验证终端合法性
	 */
	protected boolean checkPosTerminal() {
		// TODO Auto-generated method stub
		DataMessageTypeAllinpay returnMessageType = (DataMessageTypeAllinpay)dataUnPackage.getMessageType();

		//验证商户是否正确
		Company company = new CompanyBuss().getBaseDao().get(returnMessageType.getCompanyId());// new CompanyDao().get((String)dataUnPackage.getMapValue(42));
		//验证终端是否正确
		PosTerminal posTerminal = new PosTerminalBuss().getBaseDao().get(returnMessageType.getPosId());

		if(company==null){
			return false;
		}else if(posTerminal==null||!company.getCompanyId().equals(posTerminal.getCompanyId())){
			return false;
		}else
			return true;
	}
	
	@Override
	public byte[] operator(byte[] buffer){
		try{
			dataUnPackage = new DataUnPackageAllinpay(buffer);//解析报文头
			if(!checkPosTerminal())//验证终端合法性
				return null;
			
			//判断是否需要解密报文
			if(!"90".equals(((DataMessageTypeAllinpay)dataUnPackage.getMessageType()).getAppType())){
				securityControl = new SecurityControlAllinpay(((DataMessageTypeAllinpay)dataUnPackage.getMessageType()).getPosId());//构造安全控制对象
				buffer = securityControl.decode(buffer);//解密报文
				logger.info("解密后数据：" + TypeConversion.byte2hex(buffer));
				dataUnPackage = new DataUnPackageAllinpay(buffer);//解析报文
			}
			dataUnPackage.unPacketed();
			dataInPackage =  new DataInPackageAllinpay(dataUnPackage.getDataMap(),dataUnPackage.getMessageType());//构造业务处理对象
			
			//构造业务处理对象
			jposBussness = new BussnessAllinpay(dataInPackage);
			//校验MAC
			if(jposBussness.needCheckMac()&&securityControl!=null){
				if(!securityControl.checkMac(dataInPackage)){//maccuo
					logger.debug("MAC校验错误");
					throw new MacErrorPacketException("MAC校验错误");
				}
			}
			
			//处理业务
			DataInPackage returnPackage = jposBussness.handler();
			//构造数据签名
			if(jposBussness.needCheckMac()&&securityControl!=null){
				byte[] returnMac = securityControl.getMac(returnPackage.getMabSource());//计算mac时保证位图第64位为1
				returnPackage.setMac(returnMac);
			}
			
			byte[] reutrnBuffer = returnPackage.packaged();
			logger.info("reutrnBuffer:"+TypeConversion.byte2hex(reutrnBuffer));
			//加密数据
			if(!"90".equals(((DataMessageTypeAllinpay)returnPackage.getMessageType()).getAppType())
					&&securityControl!=null){
				reutrnBuffer = securityControl.encode(reutrnBuffer);
			}
			//返回数据
			return reutrnBuffer;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}