/***********************************************************************
 * Module:  OperatorCenter.java
 * Author:  Administrator
 * Purpose: Defines the Class OperatorCenter
 ***********************************************************************/

package com.a3650.posserver.core.init;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.ReceiveMsg;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.dao.impl.ReceiveMsgDao;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.datapackage.DataUnPackage;
import com.a3650.posserver.core.security.SecurityControl;
import com.a3650.posserver.core.service.impl.ReceiveMsgBuss;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;

/** @pdOid 137721cf-ea92-4e04-a329-446fe4b62b8b */
public abstract class OperatorCenter implements Runnable {
	static Logger logger = Logger.getLogger(OperatorCenter.class);
	public Client client;
	public SecurityControl securityControl;
	public DataUnPackage dataUnPackage;
	public DataInPackage dataInPackage;
	public Bussness jposBussness;

	public OperatorCenter(Client client) {
		
		this.client = client;
	}

	protected abstract byte[] operator(byte[] buffer);
	protected abstract boolean checkPosTerminal() ;

	public void run() {
		ReceiveMsgDao receiveMsgDao = null;
		try {
			byte[] buffer = client.receiveData();
			if(buffer==null)
				return ;
			logger.info("收到报文：" + TypeConversion.byte2hex(buffer));
			//保存到数据库
			receiveMsgDao = new ReceiveMsgBuss().getBaseDao();
			
			ReceiveMsg receiveMsg = new ReceiveMsg();
			receiveMsg.setReceiveMsg(TypeConversion.byte2hex(buffer));
			receiveMsg.setReceiveTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			receiveMsgDao.save(receiveMsg);

			//进入业务流程
			buffer = operator(buffer);
			// 返回数据
			if(buffer!=null)
				client.returnData(buffer);
			logger.info("返回报文：" + TypeConversion.byte2hex(buffer));

			
			//保存到数据库
			receiveMsg.setSendMsg(TypeConversion.byte2hex(buffer));
			receiveMsgDao.saveOrUpdate(receiveMsg);
			
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			try{
				client.close();
			}catch(Exception e){
				
			}
		}
	}

}