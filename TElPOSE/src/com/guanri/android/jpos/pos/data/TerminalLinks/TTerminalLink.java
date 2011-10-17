/**
 * 
 */
package com.guanri.android.jpos.pos.data.TerminalLinks;

import com.guanri.android.jpos.pos.data.Common;

/**
 * @author Dino.TAN
 * 
 */
public abstract class TTerminalLink extends DataLink {
	public boolean SendPackage(byte[] ABytes) {// 发送一个包
		int len = Common.Length(ABytes);
		if (len <= 0) return true;
		byte[] Bytes = new byte[len + 4];
		Bytes[0] = 'M';
		Bytes[1] = (byte) (len >> 8);
		Bytes[2] = (byte) (len);
		System.arraycopy(ABytes, 0, Bytes, 3, len);
		byte B = 0;
		for (int i = 0; i < (Bytes.length - 1); i++)
			B ^= Bytes[i];

		Bytes[Bytes.length - 1] = B;
		return WriteBytes(Bytes);
	}

	public byte[] RecvPackage() {// 接收一个包
		byte[] Bytes;

		byte B = 'M';
		while (true) {
			Bytes = ReadBytes(1);
			if (Common.Length(Bytes) < 1) return null;
	
			if (Bytes[0] == B)
				break;
		}

		Bytes = ReadBytes(2);
		if (Common.Length(Bytes) < 2) return null;

		B ^= Bytes[0] ^ Bytes[1];
		int len = ((Bytes[0] & 0xff) << 8) + (Bytes[1] & 0xFF);

		Bytes = ReadBytes(len);

		if (Common.Length(Bytes) < len) return null;

		for (int i = 0; i < Bytes.length; i++)
			B ^= Bytes[i];
		
		byte[] ABytes = Bytes;
		
		Bytes = ReadBytes(1);
		if (Common.Length(Bytes) < 1) return null;
		
		if (Bytes[0] != B) return null;

		return ABytes;
	}

}
