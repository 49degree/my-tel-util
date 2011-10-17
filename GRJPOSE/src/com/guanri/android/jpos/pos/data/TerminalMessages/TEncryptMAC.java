package com.guanri.android.jpos.pos.data.TerminalMessages;

public class TEncryptMAC extends TData {
	
	@Override
	protected void Build_DefaultData() {
		super.Build_DefaultData();
		CmdID().SetAsInteger(7);
	}
}
