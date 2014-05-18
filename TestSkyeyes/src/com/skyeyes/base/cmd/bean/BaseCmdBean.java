package com.skyeyes.base.cmd.bean;

import com.skyeyes.base.util.TypeConversion;

public abstract class BaseCmdBean{
	protected CmdHeaderBean commandHeader;
	protected byte[] ending = {0x3e,0x68,0x52,0x69};

	public CmdHeaderBean getCommandHeader() {
		return commandHeader;
	}
	public void setCommandHeader(CmdHeaderBean commandHeader) {
		this.commandHeader = commandHeader;
	}
	public byte[] getEnding() {
		return ending;
	}
	public void setEnding(byte[] ending) {
		this.ending = ending;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getSimpleName()+":commandHeader=(").append(commandHeader==null?"null":commandHeader.toString()).append(")").append(";");
		buffer.append("ending=").append(TypeConversion.byte2hex(ending)).append(";");
		return buffer.toString();
	}
}
