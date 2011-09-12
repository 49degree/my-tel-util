package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TFieldList;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TProcessList extends TFieldList {

	protected TField TrackData() { // 2磁道和3磁道的数据
		return GetField(0x83);
	}

	public TField PINData() { // 加密后的密码数据
		return GetField(0x84);
	}

	public TField PAN() { // 手工输入的主帐号
		return GetField(0x85);
	}

	public TField Response() { // 应答信息
		return GetField(0x8E);
	}
	
	public TField TerminalIdent() {  //终端ID
		return GetField(0xAE);
	}
	
	public TField MerchantCode() {  //商户代码, 商户号 
		return GetField(0xB0);
	}
	
	public TProcessList() {
		  AddField(0x83, TDataType.dt_BCD, TLengthType.lt_VarBIN1, 160, "TrackData"); // 2磁道和3磁道的数据
		  AddField(0x84, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "PINData"); // 加密后的密码数据
		  AddField(0x85, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 20, "PAN"); // 手工输入的主帐号
		  AddField(0x8E, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 100, "Response"); // 应答信息
	}

}
