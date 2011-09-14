package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;
import com.guanri.android.jpos.pos.data.Fields.TFieldList;


public class TTerminalMessage extends TFieldList {

	public TField MsgType() {
		return GetField(-2);
	}

	public TField Eof() {
		return GetField(-1);
	}

	protected void Build_DefaultData() {
		MsgType().SetAsInteger(0);
		Eof().SetAsInteger(0);
	}

	@Override
	public TResult_SaveToBytes SaveToBytes() {
		Build_DefaultData();
		return super.SaveToBytes();
	}
	public TTerminalMessage() {
		AddField(-2, TDataType.dt_BIN, TLengthType.lt_Fixed, 1, "MsgType"); // 报文类型
		AddField(-1, TDataType.dt_BIN, TLengthType.lt_Fixed, 1, "Eof"); // 结束标志
	}
}
