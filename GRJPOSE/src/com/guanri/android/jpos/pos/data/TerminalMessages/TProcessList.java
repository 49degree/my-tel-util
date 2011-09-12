package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Common;
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
	
	public TField TerminalID() {  //终端号
		return GetField(0xAE);
	}
	
	public TField UserID() {  //操作员ID
		return GetField(0xAF);
	}
	
	public TField MerchantID() {  //商户代码, 商户号 
		return GetField(0xA6);
	}
	
	public TField OrderNumber() { //订单编号
		return GetField(0x87);
	}
	
	public TField SaleAmount() { //消费金额
		return GetField(0x8D);
	}
	
	
	public TField Response() { // 应答信息
		return GetField(0x8E);
	}
	
	public TField MerchantName() {  //商户名称
		return GetField(0xAC);
	}
	
	public String GetTrack2Data() {  //获取2磁道数据
		byte[] s = Common.StringToBytes(TrackData().GetAsString());
		int i, len, k;
		len = Common.Length(s);
		if (len <= 0) return null;
		i = 0;
		k = i;
		while (i < len) {
			if (s[i] == '?') break;
			i++;
		}
		byte [] r = new byte[i - k];
		for (int j = 0; j < Common.Length(r); j++ ) r[j] = s[j + k];
		return Common.BytesToString(r);
		
	}
	public String GetTrack3Data() {  //获取3磁道数据
		byte[] s = Common.StringToBytes(TrackData().GetAsString());
		int i, len, k;
		len = Common.Length(s);
		if (len <= 0) return null;
		i = 0;
		k = i;
		while (i < len) {
			if (s[i] == '?') break;
			i++;
		}
		while (i < len) {
			if (s[i] != '?') break;
			i++;
		}
		k = i;
		while (i < len) {
			if (s[i] == '?') break;
			i++;
		}
		while (i < len) {
			if (s[i] == '?') break;
			i++;
		}
		byte [] r = new byte[i - k];
		for (int j = 0; j < Common.Length(r); j++ ) r[j] = s[j + k];
		return Common.BytesToString(r);
	}
	
	public String GetPAN() {  //获取主帐号
		byte[] s = Common.StringToBytes(GetTrack2Data());
		int i, len, k;
		len = Common.Length(s);
		if (len <= 0) return PAN().GetAsString();
		i = 0;
		k = i;
		while (i < len) {
			if (s[i] == '=') break;
			i++;
		}
		byte [] r = new byte[i - k];
		for (int j = 0; j < Common.Length(r); j++ ) r[j] = s[j + k];
		return Common.BytesToString(r);
	}
	
	
	public TProcessList() {
		  AddField(0x83, TDataType.dt_BCD, TLengthType.lt_VarBIN1, 160, "TrackData"); // 2磁道和3磁道的数据
		  AddField(0x84, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "PINData"); // 加密后的密码数据
		  AddField(0x85, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 20, "PAN"); // 手工输入的主帐号  
		  AddField(0xAE, TDataType.dt_ASC, TLengthType.lt_Fixed, 8, "TerminalID"); // 终端号
		  AddField(0xAF, TDataType.dt_ASC, TLengthType.lt_Fixed, 3, "UserID"); // 操作员ID
		  AddField(0xA6, TDataType.dt_ASC, TLengthType.lt_Fixed, 15, "MerchantID"); //商户代码, 商户号 
		  AddField(0x87, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 40, "OrderNumber"); //订单编号
		  AddField(0x8D, TDataType.dt_BCD, TLengthType.lt_Fixed, 12, "SaleAmount"); //消费金额
		  
		  AddField(0x8E, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 100, "Response"); // 应答信息
		  AddField(0xAC, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 50, "MerchantName"); //商户名称
	}

}
