package com.guanri.android.jpos.pos.data.TerminalMessages;

public class THandshake extends TData {
	
	@Override
	protected void Build_DefaultData() {
		super.Build_DefaultData();
		CmdID().SetAsInteger(6);
	}
}
