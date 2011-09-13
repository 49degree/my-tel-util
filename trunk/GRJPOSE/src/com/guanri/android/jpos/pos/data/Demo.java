package com.guanri.android.jpos.pos.data;


import com.guanri.android.jpos.pos.data.Fields.*;
import com.guanri.android.jpos.pos.data.TerminalLinks.*;
//import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.jpos.pos.data.TerminalParsers.*;

public class Demo {


	public static void main(String[] Args) {
		// TestField();
		TCommTerminalLink TerminalLink = new TCommTerminalLink();
		TerminalLink.CommName = "COM1";
		TerminalLink.ReadTimeout = 5000;
		TerminalLink.Connect();
		if (!TerminalLink.GetConnected()) {
			System.out.println("端口打开失败!");
			return;
		}  
		TTerminalParser TerminalParser = new TTerminalParser();
		TerminalParser.SetTerminalLink(TerminalLink);

		System.out.println("终端解析器正在运行...");

		while (true) {
			//System.out.println("ParseRequest...");
			TerminalParser.ParseRequest();
		}

	}

}
