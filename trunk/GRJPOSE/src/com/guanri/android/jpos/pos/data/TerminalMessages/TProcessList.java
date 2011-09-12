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
	
	public TField TerminalID() {  //终端号
		return GetField(0xAE);
	}
	
	public TField UserID() {  //操作员ID
		return GetField(0xAF);
	}
	
	public TField MerchantID() {  //商户代码, 商户号 
		return GetField(0xB0);
	}
	
	public TProcessList() {
		  AddField(0x83, TDataType.dt_BCD, TLengthType.lt_VarBIN1, 160, "TrackData"); // 2磁道和3磁道的数据
		  AddField(0x84, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "PINData"); // 加密后的密码数据
		  AddField(0x85, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 20, "PAN"); // 手工输入的主帐号
		  AddField(0x8E, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 100, "Response"); // 应答信息
		  AddField(0xAE, TDataType.dt_ASC, TLengthType.lt_Fixed, 8, "TerminalID"); // 终端号
		  AddField(0xAF, TDataType.dt_ASC, TLengthType.lt_Fixed, 3, "UserID"); // 操作员ID
		  AddField(0xB0, TDataType.dt_ASC, TLengthType.lt_Fixed, 15, "MerchantID"); //商户代码, 商户号 
	}

}
