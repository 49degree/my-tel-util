package com.guanri.android.jpos.pos.data.Fields;

//import java.io.UnsupportedEncodingException;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.Stream;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;

public class TBINField extends TField {

	@Override
	public int GetInteger(int Index, int Len) {
		// TODO Auto-generated method stub
		int i, k, L, H;

		int Result = 0;
		if (Index < 0)
			return Result;

		if (Len <= 0)
			Len = Common.Length(FData);

		L = Index;
		H = Index + Len - 1;
		if (H > Common.High(FData))
			H = Common.High(FData);

		for (i = L; i <= H; i++) {
			/*
			 * if (Result > ((MaxInteger - 255) >> 8)) then raise
			 * FieldException.Create('GetInteger 溢出');
			 */
			Result <<= 8;
			k = FData[i] & 0xFF;
			Result += k;
		}

		return Result;
	}

	@Override
	public long GetInt64(int Index, int Len) {
		// TODO Auto-generated method stub
		int i, k, L, H;

		long Result = 0;
		if (Index < 0)
			return Result;

		if (Len <= 0)
			Len = Common.Length(FData);

		L = Index;
		H = Index + Len - 1;
		if (H > Common.High(FData))
			H = Common.High(FData);

		for (i = L; i <= H; i++) {
			/*
			 * if (Result > ((MaxInt64 - 255) >> 8)) then raise
			 * FieldException.Create('GetInt64 溢出');
			 */
			Result <<= 8;
			k = FData[i] & 0xFF;
			Result += k;
		}

		return Result;
	}

	@Override
	public String GetString(int Index, int Len) {
		int i, L, H;

		byte[] BResult = null;

		if (Index < 0)
			return null;

		if (Len <= 0)
			Len = Common.Length(FData);

		L = Index;
		H = Index + Len - 1;
		if (H > Common.High(FData))
			H = Common.High(FData);

		BResult = new byte[H - L + 1];
		for (i = L; i <= H; i++) {
			BResult[i - L] = FData[i];
		}
		
		return Common.BytesToString(BResult);
	}

	@Override
	public void SetInteger(int Index, int Len, int Value) {
		int i, NewLen, L, H;
		int d;

		if (Index < 0)
			return;

		if (Len <= 0) {
			if (FLengthType == TLengthType.lt_Fixed)
				Len = FMaxLength;
			else {
				// Len := Length(Value); // 有效数字位数;
				Len = 1;
				d = Value;
				while (d >= 256) {
					d >>= 8;
					Len++;
				}
			}
		}

		L = Index;
		H = Index + Len - 1;
		NewLen = H + 1;
		if (NewLen > FMaxLength)
			NewLen = FMaxLength;

		if (NewLen > Common.Length(FData)) {
			if (FLengthType == TLengthType.lt_Fixed)
				FData = Common.SetLength(FData, FMaxLength);
			else
				FData = Common.SetLength(FData, NewLen);
		}
		H = NewLen - 1;

		for (i = H; i >= L; i--) {
			FData[i] = (byte) (Value & 255);
			Value >>= 8;
		}
		return;
	}

	@Override
	public void SetInt64(int Index, int Len, long Value) {
		// TODO Auto-generated method stub
		int i, NewLen, L, H;
		long d;

		if (Index < 0)
			return;

		if (Len <= 0) {
			if (FLengthType == TLengthType.lt_Fixed)
				Len = FMaxLength;
			else {
				// Len := Length(Value); // 有效数字位数;
				Len = 1;
				d = Value;
				while (d >= 256) {
					d >>= 8;
					Len++;
				}
			}
		}

		L = Index;
		H = Index + Len - 1;
		NewLen = H + 1;
		if (NewLen > FMaxLength)
			NewLen = FMaxLength;

		if (NewLen > Common.Length(FData)) {
			if (FLengthType == TLengthType.lt_Fixed)
				FData = Common.SetLength(FData, FMaxLength);
			else
				FData = Common.SetLength(FData, NewLen);
		}
		H = NewLen - 1;

		for (i = H; i >= L; i--) {
			FData[i] = (byte) (Value & 255);
			Value >>= 8;
		}
		return;
	}

	@Override
	public void SetString(int Index, int Len, String Value) {
		// TODO Auto-generated method stub
		int i, NewLen, L, H, j;
		//byte B;

		byte[] BValue = Common.StringToBytes(Value);

		if (Index < 0)
			return;

		if (Len <= 0) {
			if (FLengthType == TLengthType.lt_Fixed)
				Len = FMaxLength;
			else {
				Len = Common.Length(BValue); // 有效字符串位数;
			}
		}

		L = Index;
		H = Index + Len - 1;
		NewLen = H + 1;
		if (NewLen > FMaxLength)
			NewLen = FMaxLength;

		if (NewLen > Common.Length(FData)) {
			if (FLengthType == TLengthType.lt_Fixed)
				FData = Common.SetLength(FData, FMaxLength);
			else
				FData = Common.SetLength(FData, NewLen);
		}
		H = NewLen - 1;

		j = 0; // 字符串左对齐
		for (i = L; i <= H; i++) {
			if (j < Common.Length(BValue))
				FData[i] = BValue[j++];
			else
				FData[i] = ' ';
		}
		return;
	}

	public  TBINField() {
		FDataType = TDataType.dt_BIN;
	}
	
	public boolean Test() {

		System.out.println("Test_TBINField");
		
		FMaxLength = 10;
		Clear();
		SetAsString("tan谭");
		System.out.println(GetAsString() + ".");

		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		
		FMaxLength = 10;
		Clear();
		SetAsString("谭自杰tanzijie");
		System.out.println(GetAsString() + ".");
		
		Stream.SetBytes(null);
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		
		FLengthType = TLengthType.lt_VarBCD2;
		FMaxLength = 10;
		Clear();
		SetAsString("tan谭");
		System.out.println(GetAsString() + ".");

		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		
		FLengthType = TLengthType.lt_VarBCD2;
		FMaxLength = 10;
		Clear();
		SetAsString("谭自杰tanzijie");
		System.out.println(GetAsString() + ".");

		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		
		FLengthType = TLengthType.lt_VarBCD3;
		FMaxLength = 10;
		Clear();
		SetAsString("谭自杰tanzijie");
		System.out.println(GetAsString() + ".");

		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		
		FLengthType = TLengthType.lt_VarBIN1;
		FMaxLength = 10;
		Clear();
		SetAsString("谭自杰tanzijie");
		System.out.println(GetAsString() + ".");


		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		

		SetAsInteger(123456);
		System.out.println(GetAsInteger() + ".");
		System.out.println(GetAsInt64() + ".");


		Stream.SetBytes(null);
		
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes));
		return true;
	}

}
