package com.guanri.android.jpos.pos.data;

public class Stream {
	
	//公共静态变量
	public static byte[] Bytes;  //静态的数据流
	public static int Index;  //数据流的索引值
	public static final boolean Compress = true; //数据是否压缩
	
	public static void SetBytes(byte[] ABytes){
		Bytes = ABytes;
		Index = 0;
	}

}
