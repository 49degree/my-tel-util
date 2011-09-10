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


	
	public byte[] getMak(String msg){
//		终端号：20100601 
//		流水号：000022 
//		交易日期时间：0910220859 (不包括年份) 	
		//String msg = "201006010000220910220859";
		char[] msgs = msg.toCharArray();
		StringBuffer msg2 = new StringBuffer();
		for(char msgsarg:msgs)
			msg2.append("3").append(msgsarg);
		byte[] source = TypeConversion.hexStringToByte(msg2.toString());
		
		int blockNum = source.length % 8>0?(source.length/8+1):source.length/8;
		
		byte[] result = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		
//		byte[] source = TypeConversion.hexStringToByte("FFFFFFFFFFFFFFFF323031303036303130303030323230393130323230383539");
//		int blockNum = source.length % 8>0?(source.length/8+1):source.length/8;
//		byte[] result = new byte[8];
//		System.arraycopy(source, 0, result, 0, 8);
		
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
		
		logger.debug("MAK:"+TypeConversion.byte2hex(result));
		
		return result;
	}
	
	public byte[] getMac(byte[] mab,String makSource){
		byte[] mak = getMak(makSource);
		
		if(mab.length%8>0){
			byte[] temp = new byte[(mab.length/8+1)*8];
			System.arraycopy(mab, 0, temp, 0, mab.length);
			mab = temp;
		}
		logger.debug("getMac:"+TypeConversion.byte2hex(mab));
		
		byte[] result = this.getDynamicMAC(mab, mak);
		logger.debug(TypeConversion.byte2hex(result));
		
		
		return result;
	}

	
	/**
	 * 构造消息摘要
	 * @return String
	 * @roseuid 4DF71E7A02AF
	 */
	public byte[] getDynamicMAC(byte[] text,byte[] password) {
		// 构造消息摘要
		int blockNum = text.length / 8;
		byte[] checkBlock = new byte[8];

		System.arraycopy(text, 0, checkBlock, 0, 8);
		byte[] result = encrypto(checkBlock,password);

		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(text, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
			result = encrypto(result,password);
		}
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
		logger.debug(TypeConversion.byte2hex(
				CryptionControl.instance.getMac(
						TypeConversion.hexStringToByte("5264102500120211310000000022220859091001563230313030363031"),"201006010000220910220859")));
		
		//CryptionControl.instance.getMak();
	}
}
