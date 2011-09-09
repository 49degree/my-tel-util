package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.pad.bill99.PosDataHandler99Bill;

/**
 * 从POS接收数据
 * @author Administrator
 *
 */
public class PosDataHandlerFactory {
	public static PosDataHandlerImp getPosDataHandler(){
		return PosDataHandler99Bill.getInstance();
	}
}
