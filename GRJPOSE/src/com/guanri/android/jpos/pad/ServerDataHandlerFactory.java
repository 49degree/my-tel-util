package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.pad.bill99.ServerDataHandler99Bill;

/**
 * 从POS接收数据
 * @author Administrator
 *
 */
public class ServerDataHandlerFactory {
	public static ServerDataHandlerImp geServerDataHandler(){
		return ServerDataHandler99Bill.getInstance();
	}
}
