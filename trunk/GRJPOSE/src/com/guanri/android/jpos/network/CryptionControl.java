package com.guanri.android.jpos.network;

import com.guanri.android.lib.log.Logger;



/**
 * 加解密命令,此类为单例类
 */
public class CryptionControl {
	public static Logger logger = Logger.getLogger(CryptionControl.class);//日志对象

	private static CryptionControl instance = new CryptionControl();
	
	/**
	 * @roseuid 4DF71C3B029F
	 */
	private CryptionControl() {
	}


	/**
	 * 构造消息摘要的原始数据域
	 * 进行标准的X9.9运算。
	 * @param source 要计算的原始数据
	 * 
	 *  
	 * @return
	 */
	public static byte[] getMacBlock(byte[] source) {
		// 构造消息摘要
		int blockNum = source.length / 8;
		
		byte[] result = new byte[8];
		System.arraycopy(source, 0, result, 0, 8);
		
		byte[] checkBlock = new byte[8];
		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(source, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
		}
		return result;
	}

}
