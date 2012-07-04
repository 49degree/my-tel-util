package com.custom.utils;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;




/**
 * 加解密命令,此类为单例类
 */
public class CryptionControl {
	public static Logger logger = Logger.getLogger(CryptionControl.class);//日志对象
	public final static String DES_TYPE = "DES/ECB/NoPadding";//DES加密类型
	public final  static byte[] rootKey = TypeConversion.hexStringToByte("DBED28F6415162BD");
	private static CryptionControl instance = new CryptionControl();
	public static CryptionControl getInstance(){
		return instance;
	}
	
	/**
	 * @roseuid 4DF71C3B029F
	 */
	private CryptionControl() {
	}

	/**
	 * 
	 * 3倍长密钥加密
	 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P))) 
	 */
	public byte[] encryptoCBCKey3(byte[] text,byte[] password) {
		if(password.length!=24)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		byte[] key3 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		System.arraycopy(password, 16, key3, 0, 8);
		byte[] result = encryptoCBCKey1(text,key1);
		result = decryptCBCKey1(result,key2);
		result = encryptoCBCKey1(result,key3);
		return result;
	}
	
	/**
	 * 
	 * 3倍长密钥解密
	 * 3DES解密过程为：P=Dk1((EK2(Dk3(C)))
	 */
	public byte[] decryptCBCKey3(byte[] text,byte[] password) {
		if(password.length!=24)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		byte[] key3 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		System.arraycopy(password, 16, key3, 0, 8);
		byte[] result = decryptCBCKey1(text,key3);
		result = encryptoCBCKey1(result,key2);
		result = decryptCBCKey1(result,key1);
		return result;
	}
	
	/**
	 * 
	 * 双倍长密钥加密
	 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P))) 
	 * 
	 * K1=K3，但不能K1=K2=K3（如果相等的话就成了DES算法了）
	 */
	public byte[] encryptoCBCKey2(byte[] text,byte[] password) {
		if(password.length!=16)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		byte[] result = encryptoCBCKey1(text,key1);
		result = decryptCBCKey1(result,key2);
		result = encryptoCBCKey1(result,key1);
		return result;
	}
	
	/**
	 * 
	 * 双倍长密钥解密
	 * 3DES解密过程为：P=Dk1((EK2(Dk3(C)))
	 * 
	 * K1=K3，但不能K1=K2=K3（如果相等的话就成了DES算法了）
	 */
	public byte[] decryptCBCKey2(byte[] text,byte[] password) {
		if(password.length!=16)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		byte[] result = decryptCBCKey1(text,key1);
		result = encryptoCBCKey1(result,key2);
		result = decryptCBCKey1(result,key1);
		return result;
	}
	
	/**
	 * DES CBC加密
	 * @return String
	 * @roseuid 4DF71E7A02AF
	 */
	public byte[] encryptoCBCKey1(byte[] text,byte[] password) {
		if(password.length!=8)
			return null;
		byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		
		// 构造消息摘要
		int blockNum = result.length / 8;
		byte[] checkBlock = new byte[8];

		System.arraycopy(text, 0, checkBlock, 0, 8);
		byte[] temp = encryptoECB(checkBlock,password);
		System.arraycopy(temp, 0, result, 0, 8);
		
		for (int i = 1; i < blockNum; i++) {
			if(text.length<i * 8+8){
				Arrays.fill(checkBlock, (byte)0x00);
				System.arraycopy(text, i * 8, checkBlock, 0, text.length%8);
			}else{
				System.arraycopy(text, i * 8, checkBlock, 0, 8);
			}
			
			for (int j = 0; j < 8; j++) {
				temp[j] = (byte) (temp[j] ^ checkBlock[j]);
			}
			temp = encryptoECB(temp,password);
			System.arraycopy(temp, 0, result, i*8, 8);
		}
		return result;
	}	
	
	/**
	 * DES CBC解密
	 * @return String
	 * @roseuid 4DF71E7A02AF
	 */
	public byte[] decryptCBCKey1(byte[] text,byte[] password) {
		if(password.length!=8)
			return null;
		byte[] result = new byte[text.length];
		// 构造消息摘要
		int blockNum = text.length / 8;
		
		byte[] checkBlock = new byte[8];
		System.arraycopy(text, 0, checkBlock, 0, 8);
		
		byte[] temp = decryptECB(checkBlock,password);
		System.arraycopy(temp, 0, result, 0, 8);

		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(text, i * 8, checkBlock, 0, 8);
			temp = decryptECB(checkBlock,password);
			
			for (int j = 0; j < 8; j++) {
				temp[j] = (byte) (temp[j] ^ text[(i-1)*8+j]);
			}
			System.arraycopy(temp, 0, result, i*8, 8);
		}
		return result;
	}	
	
	/**
	 * 
	 * 3倍长密钥加密
	 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P))) 
	 */
	public byte[] encryptoECBKey3(byte[] text,byte[] password) {
		if(password.length!=24)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		byte[] key3 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		System.arraycopy(password, 16, key3, 0, 8);
		byte[] result = encryptoECB(text,key1);
		result = decryptECB(result,key2);
		result = encryptoECB(result,key3);
		return result;
	}
	
	/**
	 * 
	 * 3倍长密钥解密
	 * 3DES解密过程为：P=Dk1((EK2(Dk3(C)))
	 */
	public byte[] decryptECBKey3(byte[] text,byte[] password) {
		if(password.length!=24)
			return null;
		if(text.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(text.length/8+1)*8];
			System.arraycopy(text, 0, temp, 0, text.length);
			text = temp;
		}
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		byte[] key3 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		System.arraycopy(password, 16, key3, 0, 8);
		byte[] result = decryptECB(text,key3);
		result = encryptoECB(result,key2);
		result = decryptECB(result,key1);
		return result;
	}
	
	/**
	 * 
	 * 双倍长密钥加密
	 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P))) 
	 * 
	 * K1=K3，但不能K1=K2=K3（如果相等的话就成了DES算法了）
	 */
	public byte[] encryptoECBKey2(byte[] text,byte[] password) {
		
		if(text.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(text.length/8+1)*8];
			System.arraycopy(text, 0, temp, 0, text.length);
			text = temp;
		}
		
		if(password.length!=16)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		byte[] result = encryptoECB(text,key1);
		result = decryptECB(result,key2);
		result = encryptoECB(result,key1);
		return result;
	}
	
	/**
	 * 
	 * 双倍长密钥解密
	 * 3DES解密过程为：P=Dk1((EK2(Dk3(C)))
	 * 
	 * K1=K3，但不能K1=K2=K3（如果相等的话就成了DES算法了）
	 */
	public byte[] decryptECBKey2(byte[] text,byte[] password) {
		if(password.length!=16)
			return null;
		//byte[] result = new byte[text.length%8==0?text.length:(text.length/8+1)*8];
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(password, 0, key1, 0, 8);
		System.arraycopy(password, 8, key2, 0, 8);
		byte[] result = decryptECB(text,key1);
		result = encryptoECB(result,key2);
		result = decryptECB(result,key1);
		return result;
	}	
	/**
	 * DES加密过程
	 * @param datasource
	 * @param password
	 * @return
	 */
	public byte[] encryptoECB(byte[] datasource, byte[] password) {
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
	public byte[] decryptECB(byte[] src, byte[] password){
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
		byte[] decryptDesTRK = TypeConversion.hexStringToByte("0B133462AD07C216E33E5D0BD34980C4");
		byte[] tmp = "000000".getBytes();
		logger.debug("原数据："+TypeConversion.byte2hex(tmp));			
		//加密数据
		byte[] encrypdata =  CryptionControl.getInstance().encryptoECBKey2(tmp, decryptDesTRK);
		logger.debug("加密后的数据："+TypeConversion.byte2hex(encrypdata));
		byte[] dencrypdata = CryptionControl.getInstance().decryptECBKey2(encrypdata, decryptDesTRK);
		logger.debug("解密后的数据："+TypeConversion.byte2hex(dencrypdata));
	}
}
