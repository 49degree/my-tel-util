package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TEncryptMAC_Send extends TEncryptMAC {
	
	public TField MAB() {	//MAB块
		return GetField(1);
	} 
	
	public TField Year() {// YYYY
		return GetField(2);
	}

	public TField Date() {// MMDD
		return GetField(3);
	}

	public TField Time() {// hhmmss
		return GetField(4);
	}
	
	public TEncryptMAC_Send(){
		AddField(1, TDataType.dt_BIN, TLengthType.lt_VarBIN1, 200, "MAB"); // MAB
		AddField(2, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "Year"); // YYYY
		AddField(3, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "Date"); // MMDD
		AddField(4, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "Time"); // hhmmss
	}
	
}
