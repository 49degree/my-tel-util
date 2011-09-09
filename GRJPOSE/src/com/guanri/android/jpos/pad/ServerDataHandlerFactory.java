package com.guanri.android.jpos.pad;

import java.util.TreeMap;

import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.pad.bill99.ServerDataHandler99Bill;

public class ServerDataHandlerFactory {
	public static ServerDataHandlerImp getServerDataHandler(){
		return ServerDataHandler99Bill.getInstance();
	}
}