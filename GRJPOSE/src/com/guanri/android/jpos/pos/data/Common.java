package com.guanri.android.jpos.pos.data;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.guanri.android.jpos.pos.data.Fields.TField;

public class Common {

	public static String ToHex(byte[] Bytes) {
		String hex = "0123456789ABCDEF";
		String s = "";
		byte B;
		int k;

		if (Bytes != null) {
			for (int i = 0; i < Bytes.length; i++) {
				B = Bytes[i];
				k = (B >> 4) & 0x0F;
				s += hex.substring(k, k + 1);
				k = (B) & 0x0F;
				s += hex.substring(k, k + 1);
			}
		}
		return s;
	}

	public static byte[] HexToBytes(String Str) {
		byte[] BStr = StringToBytes(Str);
		if (Length(BStr) <= 1)
			return null;
		byte[] Bytes = new byte[Length(BStr) >> 1];
		byte C;
		byte B;
		for (int i = 0; i < Length(Bytes); i++) {
			B = 0;
			C = BStr[i * 2];
			if (('0' <= C) & (C <= '9'))
				B += C - '0';
			else if (('A' <= C) & (C <= 'F'))
				B += C - 'A' + 10;
			else if (('a' <= C) & (C <= 'f'))
				B += C - 'a' + 10;

			B <<= 4;

			C = BStr[i * 2 + 1];
			if (('0' <= C) & (C <= '9'))
				B += C - '0';
			else if (('A' <= C) & (C <= 'F'))
				B += C - 'A' + 10;
			else if (('a' <= C) & (C <= 'f'))
				B += C - 'a' + 10;

			Bytes[i] = B;
		}
		return Bytes;

	}

	public static boolean IsSameBytes(byte[] ABytes, byte[] BBytes) {
		if (Length(ABytes) != Length(BBytes))
			return false;
		if (Length(ABytes) <= 0) return true;
		for (int i = 0; i < Length(ABytes); i ++ ){
			if (ABytes[i] != BBytes[i]) return false;
		}
		return true;
	}

	public static byte[] SetLength(byte[] ABytes, int NewSize) {
		if (NewSize <= 0)
			return null;
		byte[] Bytes = new byte[NewSize];
		int OldSize = Length(ABytes);
		for (int i = 0; i < NewSize; i++) {
			if (i < OldSize)
				Bytes[i] = ABytes[i];
			else
				Bytes[i] = 0;

		}
		return Bytes;
	}

	public static TField[] SetLength(TField[] AFields, int NewSize) {
		if (NewSize <= 0)
			return null;
		TField[] Fields = new TField[NewSize];
		int OldSize = Length(AFields);
		for (int i = 0; i < NewSize; i++) {
			if (i < OldSize)
				Fields[i] = AFields[i];
			else
				Fields[i] = null;

		}
		return Fields;
	}

	public static int High(TField[] Fields) {
		return Length(Fields) - 1;
	}

	public static int High(byte[] ABytes) {
		return Length(ABytes) - 1;
	}

	public static int Length(byte[] ABytes) {
		if (ABytes == null)
			return 0;
		return ABytes.length;
	}

	public static int Length(String S) {
		if (S == null)
			return 0;
		return S.length();
	}

	public static int Length(TField[] Fields) {
		if (Fields == null)
			return 0;
		return Fields.length;
	}

	public static byte[] StringToBytes(String S) {
		byte[] Bytes = null;
		if (S == null)
			return Bytes;
		try {
			Bytes = S.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Bytes;

	}

	public static String BytesToString(byte[] Bytes) {
		String S = null;
		if (Bytes == null)
			return S;
		try {
			S = new String(Bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return S;
	}

	public static byte[] ANSIX98_Xor(byte[] ABytes) {
		int Size = 8;
		byte[] XorBytes = new byte[Size];
		int k, i;
		byte B;
		int Len = Length(ABytes);

		for (i = 0; i < Length(XorBytes); i++) {
			B = 0;
			k = i;
			while (k < Len) {
				B ^= ABytes[k];
				k += Size;
			}
			XorBytes[i] = B;
		}
		return XorBytes;
	}
	
	public static byte[] DES_Encrypt(byte[] Key, byte[] Data){
		
		Key = SetLength(Key, 8);
		
		int Len = Length(Data);
		Len = (Len + 7) & (~7);
		
		Data = SetLength(Data, Len);
		byte[] Result = new byte[Len];
		
		try {
			Cipher cipher = Cipher.getInstance("DES");
			DESKeySpec desKeySpec = new DESKeySpec(Key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			
			int i = 0, k = 8, j;
			byte[] Bytes = new byte[8], Temp = new byte[k];
			
			for (i = 0; i < k; i ++) Temp[i] = 0;
			
			for (i = 0; i < Len; i += k ) {
				for (j = 0; j < k; j++) 
					Bytes[j] = (byte) (Temp[j] ^ Data[j + i]);
				
				Bytes = cipher.doFinal(Bytes);
				for (j = 0; j < k; j++) 
					Result[j + i] = Bytes[j];
				
				Temp = Bytes;
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return Result;
		
		
	} 

}
