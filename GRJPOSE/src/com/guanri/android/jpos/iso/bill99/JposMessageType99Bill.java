package com.guanri.android.jpos.iso.bill99;

import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 块钱消息类型
 * @author Administrator
 *
 */
public class JposMessageType99Bill extends JposMessageType{

	@Override
	public byte[] parseValue() {
		// TODO Auto-generated method stub
	
		return TypeConversion.str2bcd(String.valueOf(getMessageType()));
		
	}

}
