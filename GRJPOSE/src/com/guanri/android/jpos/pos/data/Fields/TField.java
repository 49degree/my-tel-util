package com.guanri.android.jpos.pos.data.Fields;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.TStream;

public abstract class TField {
	public enum TDataType {
		dt_BIN /* 二进制, 8bit */, dt_ASC /* ASCII, 8bit */, dt_BCD
		/* BCD码, 4bit */
	}

	public enum TLengthType {
		lt_Fixed, lt_VarBCD2, lt_VarBCD3, lt_VarBIN1, lt_VarBIN2
	}

	public enum TAlignment {
		a_Left, a_Right
	}

	public enum TResult_LoadFromBytes {
		lfr_NoError, lfr_LessBytes, lfr_InvalidLength
	}

	public enum TResult_SaveToBytes {
		sfr_NoError, sfr_EmptyField
	}

	protected byte[] FData = null;
	protected int FIndex = 0;
	protected String FName = null;
	protected int FMaxLength = 0;
	protected TDataType FDataType = TDataType.dt_BIN;
	protected TLengthType FLengthType = TLengthType.lt_Fixed;

	public abstract int GetInteger(int Index, int Len);

	public abstract long GetInt64(int Index, int Len);

	public abstract String GetString(int Index, int Len);

	public abstract void SetInteger(int Index, int Len, int Value);

	public abstract void SetInt64(int Index, int Len, long Value);

	public abstract void SetString(int Index, int Len, String Value);
	
	public int GetAsInteger(){
		return GetInteger(0, -1);
	}
	
	public long GetAsInt64(){
		return GetInt64(0, -1);
	}
	
	public String GetAsString(){
		return GetString(0, -1);
	}
	
	public void SetAsInteger(int Value) {
		Clear();
		SetInteger(0, -1, Value);
	}
	
	public void SetAsInt64(long Value) {
		Clear();
		SetInt64(0, -1, Value);
	}
	
	public void SetAsString(String Value) {
		Clear();
		SetString(0, -1, Value);
	}
	

	// Load, Save 相关的变量


	
	protected int FData_Len; //数据的长度

	public int GetIndex() {
		return FIndex;
	}
	
	public void Clear() {
		FData = null;
	}

	public boolean GetIsEmpty() {
		return FData == null;
	}

	public byte[] GetData() {
		return FData;
	}

	public void SetData(byte[] Value) {
		FData = Value;
	}

	public TResult_LoadFromBytes LoadFromBytes(TStream Stream) {
		TResult_LoadFromBytes Result;
		Clear();

		Result = LoadFromBytes_Length(Stream);
		if (Result != TResult_LoadFromBytes.lfr_NoError)
			return Result;
		
		if (FData_Len <= 0) return Result;
		
		Result = LoadFromBytes_Data(Stream);
		return Result;
	}

	public TResult_LoadFromBytes LoadFromBytes_Length(TStream Stream) {
		TResult_LoadFromBytes Result = TResult_LoadFromBytes.lfr_NoError;
		int i, k, m, d, Len;
		int B;
		boolean bBCD, bBIN;

		k = Stream.Index;
		if (k <= 0)
			k = 0;

		m = 0;
		bBCD = false;
		bBIN = false;
		switch (FLengthType) {
		case lt_VarBCD2:
			m = 2;
			bBCD = true;
			break;
		case lt_VarBCD3:
			m = 3;
			bBCD = true;
			break;
		case lt_VarBIN1:
			m = 1;
			bBIN = true;
			break;
		case lt_VarBIN2:
			m = 2;
			bBIN = true;
			break;
		}
		if (Stream.Compress & bBCD)
			d = (m + 1) >> 1;
		else
			d = m;

		if (d > 0) {
			if ((k + d) > Common.Length(Stream.Bytes)) {
				return TResult_LoadFromBytes.lfr_LessBytes;
			}
			Len = 0;

			if (Stream.Compress & bBCD) {
				for (i = 0; i < d; i++) { // 压缩
					Len *= 10;
					B = ((Stream.Bytes[k] >> 4) & 0x0F);
					if (B < 10)
						Len += B;

					Len *= 10;
					B = ((Stream.Bytes[k]) & 0x0F);
					if (B < 10)
						Len += B;

					k++;
				}
			} else {
				if (bBCD) {
					for (i = 0; i < d; i++) { // 压缩
						Len *= 10;
						B = Stream.Bytes[k] & 0xFF;
						if (B < 10)
							Len += B;
						k++;
					}
				} else {
					for (i = 0; i < d; i++) { // 非压缩
						Len *= 256;
						B = Stream.Bytes[k] & 0xFF;
						if (B < 256)
							Len += B;
						k++;
					}
					if (bBIN & Stream.Compress
							& (FDataType == TDataType.dt_BCD))
						Len *= 2;
				}
			}
			if (Len > FMaxLength) {
				return TResult_LoadFromBytes.lfr_InvalidLength;
			}
		} else
			Len = FMaxLength;
		Stream.Index = k;
		FData_Len = Len;
		return Result;
	}

	public TResult_LoadFromBytes LoadFromBytes_Data(TStream Stream) {
		TResult_LoadFromBytes Result = TResult_LoadFromBytes.lfr_NoError;
		int i, k, d;

		k = Stream.Index;
		d = FData_Len;
		if ((k + d) > Common.Length(Stream.Bytes)) {
			return TResult_LoadFromBytes.lfr_LessBytes;
		}

		FData = new byte[d];

		for (i = 0; i < d; i++) {
			FData[i] = Stream.Bytes[k];
			k++;
		}
		Stream.Index = k;

		return Result;

	}

	public TResult_SaveToBytes SaveToBytes(TStream Stream) {
		TResult_SaveToBytes Result = TResult_SaveToBytes.sfr_NoError;
		Result = SaveToBytes_Length(Stream);
		if (Result != TResult_SaveToBytes.sfr_NoError)
			return Result;
		if (Common.Length(FData) <= 0) return Result;
		Result = SaveToBytes_Data(Stream);
		return Result;
	}

	public TResult_SaveToBytes SaveToBytes_Length(TStream Stream) {
		TResult_SaveToBytes Result = TResult_SaveToBytes.sfr_NoError;
		int i, k, m, d, Len;
		byte B;
		boolean bBCD, bBIN;

		if (GetIsEmpty())
			return TResult_SaveToBytes.sfr_EmptyField;

		k = Stream.Index;
		if (k <= 0)
			k = 0;

		m = 0;
		bBCD = false;
		bBIN = false;
		switch (FLengthType) {
		case lt_VarBCD2:
			m = 2;
			bBCD = true;
			break;
		case lt_VarBCD3:
			m = 3;
			bBCD = true;
			break;
		case lt_VarBIN1:
			m = 1;
			bBIN = true;
			break;
		case lt_VarBIN2:
			m = 2;
			bBIN = true;
			break;
		}
		if (Stream.Compress & bBCD)
			d = (m + 1) >> 1;
		else
			d = m;

		if (d > 0) {
			if ((k + d) > Common.Length(Stream.Bytes)) {
				Stream.Bytes = Common.SetLength(Stream.Bytes, k + d);
			}

			Len = Common.Length(FData);
			k += d;
			if (Stream.Compress & bBCD) {
				for (i = 0; i < d; i++) {
					k--;
					B = (byte) (Len % 10);
					Len /= 10;
					B |= (byte) ((Len % 10) << 4);
					Len /= 10;
					Stream.Bytes[k] = B;
				}
			} else {
				if (bBCD) {
					for (i = 0; i < d; i++) {
						k--;
						B = (byte) (Len % 10);
						Len /= 10;
						Stream.Bytes[k] = B;
					}
				} else {
					if (bBIN & Stream.Compress
							& (FDataType == TDataType.dt_BCD))
						Len /= 2;

					for (i = 0; i < d; i++) {
						k--;
						B = (byte) (Len % 256);
						Len /= 256;
						Stream.Bytes[k] = B;
					}
				}
			}
			k += d;
		}
		Stream.Index = k;
		return Result;

	}

	public TResult_SaveToBytes SaveToBytes_Data(TStream Stream) {
		TResult_SaveToBytes Result = TResult_SaveToBytes.sfr_NoError;
	
		  int Len, d, k, i;
	
		  k = Stream.Index;

		  Len = Common.Length(FData);

		  d = Len;

		  if ((k + d) > Common.Length(Stream.Bytes)) {
			  Stream.Bytes = Common.SetLength(Stream.Bytes, k + d);
		  }
		  for (i = 0 ; i < d; i++ ){
			  Stream.Bytes[k] = FData[i];
			  k ++;
		  }
		  Stream.Index = k;
		  return Result;
	}
}



