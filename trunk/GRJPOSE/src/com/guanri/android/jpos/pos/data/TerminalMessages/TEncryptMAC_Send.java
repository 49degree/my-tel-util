package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;

public class TEncryptMAC_Send extends TData {
	
	public TField MAB() {	//MAB块
		return GetField(1);
	}  

}
