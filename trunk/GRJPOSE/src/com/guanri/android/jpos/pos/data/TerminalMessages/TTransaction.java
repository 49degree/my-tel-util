package com.guanri.android.jpos.pos.data.TerminalMessages;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.TStream;
import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

//import com.guanri.android.jpos.pos.data.TerminalMessages;

public class TTransaction extends TTerminalMessage {

	public TProcessList ProcessList = null;
	public TBufferList BufferList = null;
	
	public TField Year() {// YYYY
		return GetField(0);
	}

	public TField Date() {// MMDD
		return GetField(1);
	}

	public TField Time() {// hhmmss
		return GetField(2);
	}

	public TField SerialNumber() {// 交易流水号
		return GetField(3);
	}

	public TField Ident() {// 识别码
		return GetField(4);
	}

	public TField TransCode() {// 交易代码
		return GetField(5);
	}

	protected TField ProcessCode() {// 流程代码
		return GetField(6);
	}

	protected TField Data() {// 有效数据域
		return GetField(7);
	}

	public TField MAC() {// MAC值
		return GetField(8);
	}

	@Override
	protected void Build_DefaultData() {
		super.Build_DefaultData();
		MsgType().SetAsInteger(1);
	}

	protected byte[] EncryptMAC() {
		int iMAC = MAC().GetIndex();		
		TStream Stream = new TStream(null); 

		for (int i = 0; i < Common.Length(FFields); i++) {
			if ((FFields[i] != null) & (iMAC != (i + FFristFieldIndex)))
				FFields[i].SaveToBytes(Stream);
		}

		byte[] XorBytes = Common.ANSIX98_Xor(Stream.Bytes);

		byte[] Key = Common.HexToBytes("d52a092cf012dd0a");

		return Common.DES_Encrypt(Key, XorBytes);

	}

	public boolean CheckMAC() {
		return Common.IsSameBytes(EncryptMAC(), MAC().GetData());
	}

	public boolean LoadProcess() {
		byte[] sCode = ProcessCode().GetData();
		TStream Stream = new TStream(Data().GetData()); 
		TField Field;
		for (int i = 0; i < Common.Length(sCode); i++) {
			Field = ProcessList.GetField(sCode[i] & 0xFF);
			if (Field != null) {
				if (Field.LoadFromBytes(Stream) != TField.TResult_LoadFromBytes.lfr_NoError)
					return false;
			} else
				return false;
		}
		ProcessList.Resolve();
		return true;
	}

	public void SaveMAC() {
		Build_DefaultData();
		MAC().SetData(EncryptMAC());
	}

	public void SaveProcess() {
		TStream Stream = new TStream(null);

		byte[] sCode = null;
		TField Field;
		int Len;
		for (int i = 0; i < Common.Length(ProcessList.FFields); i++) {
			Field = ProcessList.FFields[i];
			if (Field != null) {
				if (!Field.GetIsEmpty()) {
					Len = Common.Length(sCode);
					sCode = Common.SetLength(sCode, Len + 1);
					sCode[Len] = (byte) Field.GetIndex();
					Field.SaveToBytes(Stream);
				}
			}
		}
		ProcessCode().SetData(sCode);
		Data().SetData(Stream.Bytes);

	}

	public void ClearProcess() {
		TField Field;
		for (int i = 0; i < Common.Length(ProcessList.FFields); i++) {
			Field = ProcessList.FFields[i];
			if (Field != null)
				Field.Clear();
		}
		ProcessCode().Clear();
		Data().Clear();
	}

	public TTransaction() {
		AddField(0, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "Year"); // YYYY
		AddField(1, TDataType.dt_BCD, TLengthType.lt_Fixed, 4, "Date"); // MMDD
		AddField(2, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "Time"); // hhmmss
		AddField(3, TDataType.dt_BCD, TLengthType.lt_Fixed, 6, "SerialNumber"); // 交易流水号
		AddField(4, TDataType.dt_BIN, TLengthType.lt_Fixed, 2, "Ident"); // 识别码
		AddField(5, TDataType.dt_ASC, TLengthType.lt_Fixed, 3, "TransCode"); // 交易代码
		AddField(6, TDataType.dt_BIN, TLengthType.lt_VarBIN1, 10, "ProcessCode"); // 流程代码
		AddField(7, TDataType.dt_BIN, TLengthType.lt_VarBIN2, 500, "Data"); // 有效数据域
		AddField(8, TDataType.dt_BIN, TLengthType.lt_Fixed, 8, "MAC"); // MAC值

		ProcessList = new TProcessList();
		BufferList = new TBufferList();
	}
}
