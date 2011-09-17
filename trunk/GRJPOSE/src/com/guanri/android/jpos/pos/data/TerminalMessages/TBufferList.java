package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TFieldList;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TBufferList extends TFieldList {
	
	public TField MsgTypeID() { // 消息类型
		return GetField(-1);
	}
	
	public TField ProcessCode() { // 处理码
		return GetField(3);
	}
	
	public TField PAN() { // 主帐号
		return GetField(2);
	}
	
	public TField DateOfExpired() { // 卡有效期
		return GetField(14);
	}
	
	public TField SaleAmount() { // 消费金额
		return GetField(4);
	}
	
	public TField Track2Data() { // 2磁道的数据
		return GetField(35);
	}
	
	public TField Track3Data() { // 3磁道的数据
		return GetField(36);
	}
	
	public TField AuthCode() { // 授权码
		return GetField(38);
	}
	
	public TField TraceAuditNumber() { // 系统跟踪号， POS终端流水号
		return GetField(11);
	}
	
	public TField ReferenceNumber() { // 检索参考号, POS中心流水号
		return GetField(37);
	}
	
	
	public TBufferList(){
		AddField(-1, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "MsgTypeID"); // 消息类型
		AddField(2, TDataType.dt_BCD, TLengthType.lt_VarBCD2, 19, "PAN"); //主帐号
		AddField(3, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "ProcessCode"); //主帐号
		AddField(4, TDataType.dt_BCD, TLengthType.lt_Fixed, 12, "SaleAmount"); //消费金额
		AddField(11, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "TraceAuditNumber"); // 系统跟踪号， POS终端流水号
		AddField(14, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "DateOfExpired"); //卡有效期
		
		AddField(35, TDataType.dt_BCD, TLengthType.lt_VarBCD2, 37, "Track2Data"); //2磁道的数据
		AddField(36, TDataType.dt_BCD, TLengthType.lt_VarBCD3, 104, "Track3Data"); //主帐号
		
		AddField(37, TDataType.dt_ASC, TLengthType.lt_Fixed, 12, "ReferenceNumber"); // 检索参考号, POS中心流水号
		AddField(38, TDataType.dt_ASC, TLengthType.lt_Fixed, 6, "AuthCode"); //授权码
		
		
	}
	
	

}
