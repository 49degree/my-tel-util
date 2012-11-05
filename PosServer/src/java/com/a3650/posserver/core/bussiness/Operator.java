package com.a3650.posserver.core.bussiness;

import java.util.BitSet;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.datapackage.DataMessageType;

public abstract class Operator {
	static Logger logger =  Logger.getLogger(Operator.class);
	protected Bussness bussness = null;
	
	protected TreeMap<Integer, Object> sendMap = null;
	protected DataMessageType sendMessageType = null;// 消息头
	
	protected TreeMap<Integer, Object> returnMap = null;
	protected DataMessageType returnMessageType = null;// 消息头
	
	protected BitSet needBitData = new BitSet(64);
	
	public Operator(Bussness bussness){
		this.bussness = bussness;
		sendMap = this.bussness.getSendMap();
		sendMessageType = this.bussness.getSendMessageType();
		
		returnMap = this.bussness.getReturnMap();
		returnMessageType = this.bussness.getReturnMessageType();
	}
	
	
	public void excute(){
		 if(checkBitMap()){
			 operate();
		 }
	}
	
	protected boolean checkBitMap(){
		for(int i=0;i<64;i++){
			if(needBitData.get(i)&&!sendMap.containsKey(i)){
				returnMap.put(39, "95");
				returnMap.put(44, "缺少"+i+"域");
				logger.error("缺少"+i+"域");
				return false;
			}
		}
		return true;
	}
	
	protected abstract void operate();
	
}
