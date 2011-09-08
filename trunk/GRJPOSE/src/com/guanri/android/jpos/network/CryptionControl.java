package com.guanri.android.jpos.network;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;



/**
 * 加解密命令,此类为单例类
 */
public class CryptionControl {
	public static Logger logger = Logger.getLogger(CryptionControl.class);//日志对象
	public final static String DES_TYPE = "DES/ECB/NoPadding";//DES加密类型
	
	private static byte[] rootKey = TypeConversion.hexStringToByte("DBED28F6415162BD");
	
	private static CryptionControl instance = new CryptionControl();
	
	public static CryptionControl getInstance(){
		return instance;
	}
	
	/**
	 * @roseuid 4DF71C3B029F
	 */
	private CryptionControl() {
		
	}


	
	public byte[] getMak(){
//		终端号：00000001 
//		流水号：000128 
//		交易日期时间：0308170653 (不包括年份) 	
		String msg = "000000010001280308170653";
		char[] msgs = msg.toCharArray();
		StringBuffer msg2 = new StringBuffer();
		for(char msgsarg:msgs)
			msg2.append("3").append(msgsarg);
		byte[] source = TypeConversion.hexStringToByte(msg2.toString());
		
		int blockNum = source.length % 8>0?(source.length/8+1):source.length/8;
		
		byte[] result = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		
		
		
		for (int i = 0; i < blockNum; i++) {
			byte[] checkBlock = new byte[8];
			
			if(i==blockNum-1&&source.length%8>0){
				System.arraycopy(source, i * 8, checkBlock, 0, source.length%8);
			}else{
				System.arraycopy(source, i * 8, checkBlock, 0, 8);
			}
			
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
		}
		
		result = encrypto(result,rootKey);
		
		logger.debug(TypeConversion.byte2hex(result));
		
		return result;
	}
	
	public byte[] getMac(byte[] mab){
		byte[] mak = getMak();
		byte[] result = this.encrypto(mab, mak);
		logger.debug(TypeConversion.byte2hex(result));
		return result;
	}

	
	
	/**
	 * DES加密过程
	 * @param datasource
	 * @param password
	 * @return
	 */
	public byte[] encrypto(byte[] datasource, byte[] password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password);
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance(DES_TYPE);
			// 用密匙初始化Cipher对象 DES/ECB/NoPadding
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * DES解密过程
	 * @param src
	 * @param password
	 * @return
	 */
	private byte[] decrypt(byte[] src, byte[] password){
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom random = new SecureRandom();
			// 创建一个DESKeySpec对象
			DESKeySpec desKey = new DESKeySpec(password);
			// 创建一个密匙工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// 将DESKeySpec对象转换成SecretKey对象
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance(DES_TYPE);
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			// 真正开始解密操作
			return cipher.doFinal(src);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static void main(String[] args){
		CryptionControl.instance.getMak();
	}
}
