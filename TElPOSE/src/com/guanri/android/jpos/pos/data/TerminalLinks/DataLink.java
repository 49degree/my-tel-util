package com.guanri.android.jpos.pos.data.TerminalLinks;

public abstract class DataLink {   //数据链路层
	

	public int ReadTimeout;

	public int ConnectTimeout;
	
	public abstract void Connect(); //连接
	public abstract void Disconnect(); //断开
	public abstract boolean GetConnected(); //判断是否连接
	

	
	public abstract boolean WriteBytes(byte[] ABytes);
	
	public abstract byte[] ReadBytes(int Count);
	

	
	
	
}



