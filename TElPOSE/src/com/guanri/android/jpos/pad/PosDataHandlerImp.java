package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.bean.PosMessageBean;

/**
 * 从POS接收数据
 * @author Administrator
 *
 */
public interface PosDataHandlerImp {
	/**
	 * 收到POS数据 
	 * 进行相应的协议解析
	 * @param posMessageBean 从POS机获取的数据
	 */
	public void receiveData(PosMessageBean posMessageBean);
}
