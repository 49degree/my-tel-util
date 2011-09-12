package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TData extends TTerminalMessage {
	public TField CmdID() {
		return GetField(0);
	}
	public TData() {
		AddField(0, TDataType.dt_BIN, TLengthType.lt_Fixed, 1, "CmdID"); //命令ID
	}

}
