package com.guanri.android.insurance.command;

import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;


/**
 * 加解密命令,此类为单例类
 */
public class CryptionControl {
	public static Logger logger = Logger.getLogger(CryptionControl.class);//日志对象
	private byte[] rootKey = null;
	private byte[] dynamicKey = null;
	private static HashMap rootCmd = new HashMap(3);
	private static CryptionControl instance = new CryptionControl();
	
	static{
		rootCmd.put(CommandConstant.CMD_INIT, "1");//初始化
		rootCmd.put(CommandConstant.CMD_LOGIN, "1");//登陆
		rootCmd.put(CommandConstant.CMD_POS_ALARM, "1");//终端故障告警
	}
	
	/**
	 * @roseuid 4DF71C3B029F
	 */
	private CryptionControl() {
		rootKey = CommandConstant.GR_KEY_TAIKANGRENSHOU;// 泰康人寿初始化密码
	}

	/**
	 * 获取公开密钥
	 * @return
	 */
	public byte[] getRootKey() {
		return rootKey;
	}
	
	/**
	 * 获取动态密钥
	 * @return
	 */
	public byte[] getDynamicKey() {
		return dynamicKey;
	}

	/**
	 * 设置动态密钥
	 * @return
	 */
	public void setDynamicKey(byte[] dynamicKey) {
		this.dynamicKey = dynamicKey;
	}

	/**
	 * 获取实例
	 * 
	 * @return CryptionControl
	 */
	public static CryptionControl getInstance() {
		return instance;
	}

	/**
	 * 根据命令码加密命令体
	 * @param commandCode
	 * @param argname
	 * @return
	 */
	public byte[] getDES(String commandCode,byte[] argname) {
		if(rootCmd.containsKey(commandCode)){
			return this.getRootDES(argname);
		}else{
			return this.getDynamicDES(argname);
		}
	}
	
	/**
	 * 使用固定密钥 获取argname DES加密后的结果
	 * @param argname
	 * @return String
	 * @roseuid 4DF71C50005D
	 */
	public byte[] getRootDES(byte[] argname) {
		//logger.debug("rootKey:"+TypeConversion.byte2hex(CryptionControl.getInstance().getRootKey()));
		return this.encrypto(argname, rootKey);
	}

	/**
	 *使用动态密钥  获取argname DES加密后的结果
	 * @param argname
	 * @return String
	 * @roseuid 4DF71C50005D
	 */
	public byte[] getDynamicDES(byte[] argname) {
		return this.encrypto(argname, dynamicKey);
	}
	
	
	/**
	 * 根据命令码获取消息摘要
	 * @param commandCode
	 * @param argname
	 * @return
	 */
	public byte[] getMAC(String commandCode,byte[] text){
		if(rootCmd.containsKey(commandCode)){
			return this.getRootMAC(text);
		}else{
			return this.getDynamicMAC(text);
		}
	}
	
	/**
	 * 构造消息摘要
	 * 
	 * 对于第一步getRootDES()的结果进行标准的X9.9运算。 对第一步生成的报文体按八个字节，八个字节分开，
	 * 然后第一个八字节进行DES加密后与第二个八字节异或， 对异或的结果进行DES，然后用DES的结果与第三个八字节进行异或，
	 * 对异或的结果进行DES,依次递归，得到最后的MAC（数字签名，8字节长度，填充到命令头对应域）
	 */
	public byte[] getRootMAC(byte[] text){
		// 构造消息摘要
		int blockNum = text.length / 8;
		byte[] checkBlock = new byte[8];

		System.arraycopy(text, 0, checkBlock, 0, 8);
		byte[] result = getRootDES(checkBlock);

		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(text, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
			result = getRootDES(result);
		}

		return result;
	}

	/**
	 * 构造消息摘要
	 * @return String
	 * @roseuid 4DF71E7A02AF
	 */
	public byte[] getDynamicMAC(byte[] text) {
		// 构造消息摘要
		int blockNum = text.length / 8;
		byte[] checkBlock = new byte[8];

		System.arraycopy(text, 0, checkBlock, 0, 8);
		byte[] result = getDynamicDES(checkBlock);

		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(text, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
			result = getDynamicDES(result);
		}
		return result;
	}

	/**
	 * 根据命令码获取消息摘要
	 * @param commandCode
	 * @param argname
	 * @return
	 */
	public byte[] getFormerData(String commandCode,byte[] text){
		if(rootCmd.containsKey(commandCode)){
			return getRootText(text);
		}else{
			return getDynamicText(text);
		}
	}
	
	/**
	 * 解密登陆初始化命令
	 * @return String
	 * @roseuid 4DF71E890109
	 */
	public byte[] getRootText(byte[] text) {
		return decrypt(text,rootKey);
	}
	
	/**
	 *  解密通讯命令
	 * @return String
	 * @roseuid 4DF71E890109
	 */
	public byte[] getDynamicText(byte[] text) {
		return decrypt(text,dynamicKey);
	}

	
	public final static String DES_TYPE = "DES/ECB/NoPadding";//DES加密类型
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
}
