package com.guanri.android.jpos.pad;

import java.util.TreeMap;

import com.guanri.android.jpos.bean.PosMessageBean;

public interface ServerDataHandlerImp {
	/**
	 * 收到服务器数据数据
	 * @param mReturnMap 从服务器获取的数据
	 */
	public void receiveData(TreeMap<Integer,Object> mReturnMap);
}
