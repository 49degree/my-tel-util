package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TEncryptMAC_Recv extends TEncryptMAC {
	public TField MAC() {	//MAC结果
		return GetField(1);
	} 
	public TEncryptMAC_Recv() {
		AddField(1, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "MAC"); // MAC
	} 
}
