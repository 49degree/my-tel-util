package com.guanri.android.jpos.pos.data.Fields;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.Stream;

public class TBCDField extends TField {
	protected TAlignment FAlignment = TAlignment.a_Right;

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
			 * if (Result > ((MaxInteger - 9) div 10)) then raise
			 * FieldException.Create('GetInteger 溢出');
			 */
			Result *= 10;
			k = FData[i]/* - '0' */;
			if ((0 <= k) & (k <= 9))
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
			 * if (Result > ((MaxInt64 - 9) div 10)) then raise
			 * FieldException.Create('GetInt64 溢出');
			 */
			Result *= 10;
			k = FData[i]/* - '0' */;
			if ((0 <= k) & (k <= 9))
				Result += k;
		}

		return Result;
	}

	@Override
	public String GetString(int Index, int Len) {
		// TODO Auto-generated method stub
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
			BResult[i - L] = (byte) (FData[i] + '0');
		}

		return Common.BytesToString(BResult);
	}

	@Override
	public void SetInteger(int Index, int Len, int Value) {
		// TODO Auto-generated method stub
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
				while (d >= 10) {
					d /= 10;
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
			FData[i] = (byte) ((Value % 10)/* + '0' */);
			Value /= 10;
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
				while (d >= 10) {
					d /= 10;
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
			FData[i] = (byte) ((Value % 10)/* + '0' */);
			Value /= 10;
		}
		return;
	}

	@Override
	public void SetString(int Index, int Len, String Value) {
		// TODO Auto-generated method stub
		int i, NewLen, L, H, j, k;
		byte B;

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
			if (j < Common.Length(BValue)) {
				k = BValue[j++] - '0';
				if ((0 <= k) & (k <= 15))
					B = (byte) k;
				else
					B = 0;
			} else
				B = 0;

			FData[i] = B;
		}
		return;
	}

	@Override
	public TResult_LoadFromBytes LoadFromBytes_Data() {
		TResult_LoadFromBytes Result = TResult_LoadFromBytes.lfr_NoError;
		int Len, i, k, d, j;
		byte[] Temp;
		byte B;

		if (!Stream.Compress)
			return super.LoadFromBytes_Data();

		k = Stream.Index;
		Len = FData_Len;

		d = (Len + 1) >> 1;
		if ((k + d) > Common.Length(Stream.Bytes)) {
			return TResult_LoadFromBytes.lfr_LessBytes;
		}

		FData = new byte[Len];
		Temp = new byte[Len + 1];

		for (i = 0; i < d; i++) {
			B = Stream.Bytes[k];
			Temp[i * 2 + 1] = (byte) (B & 0x0F);

			B >>= 4;
			Temp[i * 2] = (byte) (B & 0x0F);

			k++;
		}

		if (((Len & 1) != 0) & (FAlignment == TAlignment.a_Right))
			// 长度不是2的倍数, 且右对齐时, 左边第一个数为空
			j = 1;
		else
			j = 0;

		for (i = 0; i < Len; i++)
			FData[i] = Temp[i + j];

		Stream.Index = k;

		return Result;

	}

	@Override
	public TResult_SaveToBytes SaveToBytes_Data() {
		TResult_SaveToBytes Result = TResult_SaveToBytes.sfr_NoError;

		int Len, d, k, i, j;
		byte[] Temp;
		byte B;

		if (!Stream.Compress)
			return super.SaveToBytes_Data();

		k = Stream.Index;

		Len = Common.Length(FData);

		d = (Len + 1) >> 1;

		if ((k + d) > Common.Length(Stream.Bytes)) {
			Stream.Bytes = Common.SetLength(Stream.Bytes, k + d);
		}

		Temp = new byte[Len + 1];

		if (((Len & 1) != 0) & (FAlignment == TAlignment.a_Right))
			// 长度不是2的倍数, 且右对齐时, 左边第一个数为空
			j = 1;
		else
			j = 0;

		Temp[0] = 0;

		for (i = 0; i < Len; i++)
			Temp[i + j] = FData[i];

		for (i = 0; i < d; i++) {
			B = (byte) (Temp[i * 2] & 0x0F);
			B <<= 4;
			B = (byte) (B | (Temp[i * 2 + 1] & 0x0F));
			Stream.Bytes[k] = B;
			k++;
		}
		Stream.Index = k;
		return Result;
	}
	public  TBCDField() {
		FDataType = TDataType.dt_BCD;
	}
	
	public void Test() {
		System.out.println("Test_TBCDField");

		FMaxLength = 10;
		SetAsString("譚自杰");
		System.out.println(GetAsString() + ".");

		FMaxLength = 10;
		SetAsInteger(54321);
		System.out.println(GetAsInteger() + ".");
		System.out.println(GetAsInt64() + ".");
		System.out.println(GetAsString() + ".");
		
		Stream.SetBytes(null);
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes) + ".");
		

		FMaxLength = 10;
		SetAsString("888666");
		System.out.println(GetAsInteger() + ".");
		System.out.println(GetAsInt64() + ".");
		System.out.println(GetAsString() + ".");
		Stream.SetBytes(null);
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes) + ".");
		
		
		FMaxLength = 6;
		//FLengthType = TLengthType.lt_VarBIN1; 
		SetAsInteger(54321);
		System.out.println(GetAsInteger() + ".");
		FAlignment = TAlignment.a_Left;
		Stream.SetBytes(null);
		SaveToBytes();
		System.out.println(Common.ToHex(Stream.Bytes) + ".");
		
	}
}
