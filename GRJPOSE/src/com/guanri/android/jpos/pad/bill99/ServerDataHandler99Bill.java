package com.guanri.android.jpos.pad.bill99;

import java.util.TreeMap;

import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.pad.PosDataHandlerImp;
import com.guanri.android.jpos.pad.ServerDataHandlerImp;

public class ServerDataHandler99Bill implements ServerDataHandlerImp{
	public static ServerDataHandlerImp instance = null;
	public static ServerDataHandlerImp getInstance(){
		if(instance==null){
			instance = new ServerDataHandler99Bill(); 
		}
		return instance;
	}
	
	/**
	 * 收到服务器数据数据
	 * @param mReturnMap 从服务器获取的数据
	 */
	public synchronized void receiveData(TreeMap<Integer,Object> mReturnMap){
		
	}
}
