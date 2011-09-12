package com.guanri.android.jpos.pos.data;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.guanri.android.jpos.network.CryptionControl;

import com.guanri.android.jpos.pos.data.*;
import com.guanri.android.jpos.pos.data.Fields.*;
import com.guanri.android.jpos.pos.data.TerminalLinks.*;
//import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.jpos.pos.data.TerminalParsers.*;

public class Demo {
	public static void TestField() {
		TBINField BINField = new TBINField();
		BINField.Test();

		TASCField ASCField = new TASCField();
		ASCField.Test();

		TBCDField BCDField = new TBCDField();
		BCDField.Test();
	}

	public static void TestCommTerminalLink() {
		TCommTerminalLink comLink = new TCommTerminalLink();
		comLink.CommName = "COM5";
		comLink.ReadTimeout = 5000;
		comLink.Connect();
		// comLink.SendPackage("tanzijie".getBytes());
		byte[] b = new byte[2];
		b[0] = -128;
		b[1] = 100;
		System.out.println(Common.ToHex(b));
		System.out.println("Reading...");
		// System.out.println("tan".substring(1, 2));
		byte[] bytes = comLink.RecvPackage();
		if (bytes != null)
			System.out.println(Common.ToHex(bytes));
		comLink.Disconnect();
		System.out.println("Disconnect.");
	}

	public static void main(String[] Args) {
		// TestField();
		TCommTerminalLink TerminalLink = new TCommTerminalLink();
		TerminalLink.CommName = "COM5";
		TerminalLink.ReadTimeout = 5000;
		TerminalLink.Connect();

		TTerminalParser TerminalParser = new TTerminalParser();
		TerminalParser.SetTerminalLink(TerminalLink);

		System.out.println("终端解析器正在运行...");

		while (true) {
			TerminalParser.ParseRequest();
		}

	}

}
