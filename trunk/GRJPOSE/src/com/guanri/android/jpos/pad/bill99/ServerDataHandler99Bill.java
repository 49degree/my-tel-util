package com.guanri.android.jpos.pad.bill99;

import java.util.Iterator;
import java.util.TreeMap;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
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
	 * @param mReturnData 从服务器获取的数据
	 */
	public synchronized void receiveData(byte[] mReturnData){
		try{
			JposUnPackage99Bill bill = new JposUnPackage99Bill(mReturnData);
			bill.unPacketed();
			TreeMap<Integer, Object>  tree = bill.getmReturnMap();
			Iterator<Integer> it = bill.getmReturnMap().keySet().iterator();
			while(it.hasNext()){
				int bitValue = it.next();
			}
		}catch(PacketException e){
			
		}
		
	}
}
