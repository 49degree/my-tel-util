package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.bean.PosMessageBean;

/**
 * 从POS接收数据
 * @author Administrator
 *
 */
public interface ServerDataHandlerImp {
	/**
	 * 收到POS数据 
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	public byte[] receivePosData(PosMessageBean posMessageBean);
}
