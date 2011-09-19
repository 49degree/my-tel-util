package com.guanri.android.jpos.pos.data;

public class TStream {
	
	//公共静态变量
	public  byte[] Bytes;  //静态的数据流
	public  int Index;  //数据流的索引值
	
	public  final boolean Compress = true; //数据是否压缩
	
	public TStream(byte[] ABytes){
		Bytes = ABytes;
		Index = 0;
	}

}
