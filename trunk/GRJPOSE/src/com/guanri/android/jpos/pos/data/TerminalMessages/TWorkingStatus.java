package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TWorkingStatus extends TData {
	public TField Status() {
		return GetField(1);
	}
	
	protected void Build_DefaultData() {
		super.Build_DefaultData();
		CmdID().SetAsInteger(4);
	}
	
	public TWorkingStatus() {
		AddField(1, TDataType.dt_BIN, TLengthType.lt_Fixed, 1, "Status");
	}
}
