package com.a3650.posserver.core.datapackage;

import java.util.TreeMap;

public abstract class DataPackage {
	protected TreeMap<Integer,Object> mDataMap = null;//表示需要分包的数据
	protected DataBitMap mBitMap= null;
	protected DataMessageType mMessageType = null;
	

	/**
	 * 数据编码类型
	 * JPOS_BCD BCD编码
	 * JPOS_ASCII ASCII编码
	 * @author Administrator
	 *
	 */
	public enum DataEncodeType{
		JPOS_BCD,JPOS_ASCII
	}

	/**
	 * 设置MAC值
	 * @param mac
	 * @return
	 */
	public boolean setMac(byte[] mac){
		if(mDataMap==null){
			return false;
		}
		mDataMap.put(64, mac);
		return true;
	}
	/**
	 * 获取位数据对象
	 * @param key
	 * @return
	 */
	public Object getMapValue(Integer key){
		if(mDataMap==null){
			return null;
		}
		return mDataMap.get(key);
	}
	
	
	public TreeMap<Integer, Object> getDataMap() {
		return mDataMap;
	}
	public DataBitMap getBitMap() {
		return mBitMap;
	}
	public DataMessageType getMessageType() {
		return mMessageType;
	}

	
}
