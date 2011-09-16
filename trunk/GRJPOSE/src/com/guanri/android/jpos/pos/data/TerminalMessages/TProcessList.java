package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;
import com.guanri.android.jpos.pos.data.Fields.TFieldList;

public class TProcessList extends TFieldList {

	protected TField TrackData() { // 2磁道和3磁道的数据
		return GetField(0x83);
	}

	public TField PINData() { // 加密后的密码数据
		return GetField(0x84);
	}

	protected TField PANorTrackData() { // 手输卡号或磁道数据
		return GetField(0x92);
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
	
	public TField DateOfExpired() { //卡有效期
		return GetField(0x93);
	}
	
	public TField BillNumber() { //票据号
		return GetField(0xB2);
	}
		
	
	public TField Response() { // 应答信息
		return GetField(0x8E);
	}
	
	public TField MerchantName() {  //商户名称
		return GetField(0xAC);
	}
	
	
	public TField ReturnSaleAmount() {  //回送消费金额
		return GetField(0xA9);
	}
	
	public TField ReturnOrderNumber() {  //回送订单编号
		return GetField(0xB0);
	}
	
	public TField ReturnDisplayMessage() {  //回送显示信息
		return GetField(0xB1);
	}
	
	
	
	public TProcessList() {
		  //下列字段是终端传过来的
		  AddField(0x83, TDataType.dt_BCD, TLengthType.lt_VarBIN1, 160, "TrackData"); // 2磁道和3磁道的数据
		  AddField(0x84, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "PINData"); // 加密后的密码数据
		  AddField(0x92, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 20, "PANorTrackData"); // 手输卡号或磁道数据
		  AddField(0xAE, TDataType.dt_ASC, TLengthType.lt_Fixed, 8, "TerminalID"); // 终端号
		  AddField(0xAF, TDataType.dt_ASC, TLengthType.lt_Fixed, 3, "UserID"); // 操作员ID
		  AddField(0xA6, TDataType.dt_ASC, TLengthType.lt_Fixed, 15, "MerchantID"); //商户代码, 商户号 
		  AddField(0x87, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 40, "OrderNumber"); //订单编号
		  AddField(0x8D, TDataType.dt_BCD, TLengthType.lt_Fixed, 12, "SaleAmount"); //消费金额
		  AddField(0x93, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "DateOfExpired"); //卡有效期
		  AddField(0xB2, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "BillNumber"); //票据号
		  
		  
		//下列字段是需要传给终端的
		  AddField(0x8E, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 100, "Response"); // 应答信息
		  AddField(0xAC, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 50, "MerchantName"); //商户名称
		  
		 
		//下列字段是互相传送的  
		  AddField(0xA9, TDataType.dt_BIN, TLengthType.lt_VarBIN1, 12, "ReturnSaleAmount"); //回送消费金额
		  AddField(0xB0, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 40, "ReturnOrderNumber"); //回送订单编号
		  AddField(0xB1, TDataType.dt_ASC, TLengthType.lt_VarBIN1, 200, "ReturnDisplayMessage"); //回送显示信息

	}	
	public boolean GetIsExistTrackData() {    //是否存在磁道数据
		return GetTrack2Data() != null;
	}
	
	public boolean GetIsExistPINData() {    //是否存在密码数据
		return false;
		//if (PINData().GetIsEmpty()) return false;
		//return true;
	}

	public String GetTrack2Data() {  //获取2磁道数据
		byte[] s = Common.StringToBytes(TrackData().GetAsString());
		int i, len, k;
		len = Common.Length(s);
		if (len <= 0) {
			s = Common.StringToBytes(PANorTrackData().GetAsString());
			len = Common.Length(s);
			if (len <= 1) return null;
			if (s[0] != 0) return null; 
			i = 1;
		} else 
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
		if (len <= 0) {
			s = Common.StringToBytes(PANorTrackData().GetAsString());
			len = Common.Length(s);
			if (len <= 1) return null;
			if (s[0] != 0) return null; 
			i = 1;
		} else 
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
		byte [] r = new byte[i - k];
		for (int j = 0; j < Common.Length(r); j++ ) r[j] = s[j + k];
		return Common.BytesToString(r);
	}
	
	public String GetPAN() {  //获取主帐号
		byte[] s = Common.StringToBytes(TrackData().GetAsString());
		int i, len, k;
		len = Common.Length(s);
		if (len <= 0) {
			s = Common.StringToBytes(PANorTrackData().GetAsString());
			len = Common.Length(s);
			if (len <= 1) return null;
			//if (s[0] != 0) return null; 
			i = 1;
		} else 
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
	
	


}
