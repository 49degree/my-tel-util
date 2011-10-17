package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class THandshake_Response extends THandshake {
	
	public TField DateTime() {
		return GetField(1);
	}
	
	public TField SerialNumber() {
		return GetField(2);
	}
	
	public TField Ident() {
		return GetField(3);
	}
	
	
	public THandshake_Response() {
		  AddField(1, TDataType.dt_BCD, TLengthType.lt_Fixed, 14, "DateTime");
		  AddField(2, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "SerialNumber");
		  AddField(3, TDataType.dt_BIN, TLengthType.lt_Fixed, 2, "Ident");
	}
}
