
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author liyan
 * 
 * 
 */

public class DESCryption {

	/***************************** 压缩替换S-Box�? **************************************************/

	private static final int[][] s1 = {
			{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },

			{ 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },

			{ 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },

			{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } };

	private static final int[][] s2 = {
			{ 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },

			{ 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },

			{ 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },

			{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } };

	private static final int[][] s3 = {
			{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },

			{ 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },

			{ 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },

			{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } };

	private static final int[][] s4 = {
			{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },

			{ 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },// erorr

			{ 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },

			{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } };

	private static final int[][] s5 = {
			{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },

			{ 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },

			{ 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },

			{ 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } };

	private static final int[][] s6 = {
			{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },

			{ 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },

			{ 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },

			{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } };

	private static final int[][] s7 = {
			{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },

			{ 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },

			{ 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },

			{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } };

	private static final int[][] s8 = {
			{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },

			{ 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },

			{ 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },

			{ 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };

	private static final int[] ip = { 58, 50, 42, 34, 26, 18, 10, 2,
		
			60, 52, 44, 36, 28, 20, 12, 4,
		
			62, 54, 46, 38, 30, 22, 14, 6,
		
			64, 56, 48, 40, 32, 24, 16, 8,
		
			57, 49, 41, 33, 25, 17, 9, 1,
		
			59, 51, 43, 35, 27, 19, 11, 3,
		
			61, 53, 45, 37, 29, 21, 13, 5,
		
			63, 55, 47, 39, 31, 23, 15, 7 };

	private static final int[] _ip = { 40, 8, 48, 16, 56, 24, 64, 32,

			39, 7, 47, 15, 55, 23, 63, 31,
		
			38, 6, 46, 14, 54, 22, 62, 30,
		
			37, 5, 45, 13, 53, 21, 61, 29,
		
			36, 4, 44, 12, 52, 20, 60, 28,
		
			35, 3, 43, 11, 51, 19, 59, 27,
		
			34, 2, 42, 10, 50, 18, 58, 26,
		
			33, 1, 41, 9, 49, 17, 57, 25 };
	

	private static final int[] key_ip = { 57, 49, 41, 33, 25, 17, 9,

			1, 58, 50, 42, 34, 26, 18,
		
			10, 2, 59, 51, 43, 35, 27,
		
			19, 11, 3, 60, 52, 44, 36,
		
			63, 55, 47, 39, 31, 23, 15,
		
			7, 62, 54, 46, 38, 30, 22,
		
			14, 6, 61, 53, 45, 37, 29,
		
			21, 13, 5, 28, 20, 12, 4 };

	// 每次密钥循环左移位数
	private static final int[] LS = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2,2, 1 };

	/**
	 * 对64bit的明文输入进行换位变换
	 * IP初始置换
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] changeIP(int[] source) {

		int[] dest = new int[64];

		for (int i = 0; i < 64; i++) {

			dest[i] = source[ip[i] - 1];

		}

		return dest;

	}

	/**
	 * 将最后的L,R合并成64位，然后进行如下转化得到最后的结果。这是对第4步的一个逆变化
	 * IP-1逆置
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] changeInverseIP(int[] source) {

		int[] dest = new int[64];

		for (int i = 0; i < 64; i++) {

			dest[i] = source[_ip[i] - 1];

		}

		return dest;

	}

	/**
	 * 
	 *加密过程，对R[i][32]进行扩展变换成48位数，方法如下， 记为E(R[i][32])
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] expend(int[] source) {

		int[] ret = new int[48];

		int[] temp = { 32, 1, 2, 3, 4, 5,

		4, 5, 6, 7, 8, 9,

		8, 9, 10, 11, 12, 13,

		12, 13, 14, 15, 16, 17,

		16, 17, 18, 19, 20, 21,

		20, 21, 22, 23, 24, 25,

		24, 25, 26, 27, 28, 29,

		28, 29, 30, 31, 32, 1 };

		for (int i = 0; i < 48; i++) {

			ret[i] = source[temp[i] - 1];

		}

		return ret;

	}

	/**
	 * 
	 * 48bit压缩32bit
	 * 
	 * @param source
	 *            (48bit)
	 * 
	 * @return R(32bit)
	 * 
	 *         B=E(R)⊕K，将48 位的B 分成8 个分组，B=B1B2B3B4B5B6B7B8
	 *         
	 * 7. 使用S[i]替换B[i][6]。过程如下: 取出B[i][6]的第1位和第6位连成一个2位数m，
	 *  m就是S[i]中对应的行数(0-3)，取出B[i][6]的第2到第5位连成一个4位数n(0-15)，
	 *  n就是S[i]中对应的列数，用S[i][m][n]代替B[i][6]。S是4行16列的对应表，
	 *  里面是4位的数，一共有8个S
	 */

	public static int[] press(int[] source) {

		int[] ret = new int[32];

		int[][] temp = new int[8][6];

		int[][][] s = { s1, s2, s3, s4, s5, s6, s7, s8 };
		for (int i = 0; i < 8; i++) {

			for (int j = 0; j < 6; j++) {

				temp[i][j] = source[i * 6 + j];

			}
		}
		for (int i = 0; i < 8; i++) {

			// (16)
			int x = temp[i][0] * 2 + temp[i][5];
			// (2345)
			int y = temp[i][1] * 8 + temp[i][2] * 4 + temp[i][3] * 2
					+ temp[i][4];
			int val = s[i][x][y];
			ret[i*4+3] = val&0X01;
		    ret[i*4+2] = val>>1&0X01;
		    ret[i*4+1] = val>>2&0X01;
		    ret[i*4] = val>>3&0X01;
		}
		// 置换P
		ret = dataP(ret);
		return ret;

	}

	/**
	 * 
	 * 置换P(32bit)
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] dataP(int[] source) {
		int[] dest = new int[32];
		int[] temp = { 16, 7, 20, 21,
		
				29, 12, 28, 17,
		
				1, 15, 23, 26,
		
				5, 18, 31, 10,
		
				2, 8, 24, 14,
		
				32, 27, 3, 9,
		
				19, 13, 30, 6,
		
				22, 11, 4, 25 };
		int len = source.length;
		for (int i = 0; i < len; i++) {
			dest[i] = source[temp[i] - 1];
		}
		return dest;
	}

	/**
	 * 
	 * @param R
	 *            (32bit)
	 * 
	 * @param K
	 *            (48bit的轮子密
	 * 
	 * @return 32bit
	 */

	public static int[] f(int[] R, int[] K) {
		// 先将输入32bit扩展48bit
		int[] expendR = expend(R);// 48bit
		// 与轮子密钥进行异或
		int[] temp  = diffOr(expendR, K);
		// 压缩32bit
		int[] dest = press(temp);
		return dest;

	}

	/**
	 * 
	 * 两个等长的数组做异或
	 * 
	 * @param source1
	 * 
	 * @param source2
	 * 
	 * @return
	 */

	public static int[] diffOr(int[] source1, int[] source2) {
		int len = source1.length;
		int[] dest = new int[len];
		for (int i = 0; i < len; i++) {
			dest[i] = source1[i] ^ source2[i];
		}
		return dest;
	}
	
	
	/**
	 * 
	 * 将密钥循环左移
	 * 
	 * @param source
	 *            二进制密钥数
	 * 
	 * @param i
	 *            循环左移位数
	 * 
	 * @return
	 */

	public static int[] keyLeftMove(int[] source, int i) {

		int temp = 0;

		int len = source.length;

		int ls = LS[i];
		for (int k = 0; k < ls; k++) {
			temp = source[0];
			for (int j = 0; j < len - 1; j++) {
				source[j] = source[j + 1];
			}
			source[len - 1] = temp;
		}
		return source;

	}

	/**
	 * 
	 * 56bit的密钥转换成48bit
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] keyPC_2(int[] source) {

		int[] dest = new int[48];

		int[] temp = { 14, 17, 11, 24, 1, 5,

		3, 28, 15, 6, 21, 10,

		23, 19, 12, 4, 26, 8,

		16, 7, 27, 20, 13, 2,

		41, 52, 31, 37, 47, 55,

		30, 40, 51, 45, 33, 48,

		44, 49, 39, 56, 34, 53,

		46, 42, 50, 36, 29, 32 };

		for (int i = 0; i < 48; i++) {

			dest[i] = source[temp[i] - 1];

		}
		return dest;

	}

	/**
	 * 
	 * 获取轮子密钥(48bit)
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[][] setKey(int[] temp) {
		int[][] subKey = new int[16][48];

		// 56bit均分成两部分
		int[] left = new int[28];

		int[] right = new int[28];
		
		
		//64bit的密钥转换成56bit
		int[] temp1 = new int[56];
		for (int i = 0; i < 56; i++) {
			temp1[i] = temp[key_ip[i] - 1];
		}

		// printArr(temp1);

		// 将经过转换的temp1均分成两部分
		System.arraycopy(temp1, 0, left, 0, 28);
		System.arraycopy(temp1, 28, right, 0, 28);

		// 经过16次循环左移，然后PC-2置换
		for (int i = 0; i < 16; i++) {

			left = keyLeftMove(left, i);

			right = keyLeftMove(right, i);

			for (int j = 0; j < 28; j++) {

				temp1[j] = left[j];

				temp1[j + 28] = right[j];

			}

			// printArr(temp1);

			subKey[i] = keyPC_2(temp1);

		}
		return subKey;
	}








	/**
	 * 
	 * DES加密--->对称密钥
	 * 
	 * D = Ln(32bit)+Rn(32bit)
	 * 
	 * 经过16轮置�?
	 * 
	 * @param D
	 *            (16byte)明文
	 * 
	 * @param K
	 *            (16byte)轮子密钥
	 * 
	 * @return (16byte)密文
	 */
	public static byte[] encryptionECB(byte[] source, byte[] keyByte) throws DESCryptionException{
		if(source.length%8!=0)
			throw new DESCryptionException("source length is not multiple of 8 bytes");
		if(keyByte.length!=8)
			throw new DESCryptionException("key length is not 8 bytes");
		byte[] result = new byte[source.length];
		byte[] temp = new byte[8];
		for(int i=0;i<source.length/8;i++){
			System.arraycopy(source,0, temp, 0,8); 
			System.arraycopy(encryptionDES(temp,keyByte),0, result, i*8,8); 
		}
		return result;
	}
		
	public static byte[] encryptionDES(byte[] source, byte[] keyByte) throws DESCryptionException{
		if(source.length%8!=0)
			throw new DESCryptionException("source length is not multiple of 8 bytes");
		if(keyByte.length!=8)
			throw new DESCryptionException("key length is not 8 bytes");

		int[] temp = new int[64];
		// 第一步初始置
		int[] data = TypeConversion.byte2hexInt(source);

		data = changeIP(data);
		// printArr(data);
		int[][] left = new int[17][32];
		int[][] right = new int[17][32];
		for (int j = 0; j < 32; j++) {
			left[0][j] = data[j];
			right[0][j] = data[j + 32];
		}


		int[] key = TypeConversion.byte2hexInt(keyByte);
		int[][] subKey = setKey(key);// sub key ok

		for (int i = 1; i < 17; i++) {
			// 获取(48bit)的轮子密
			key = subKey[i - 1];
			left[i] = right[i - 1];
			int[] fTemp = f(right[i - 1], key);// 32bit
			right[i] = diffOr(left[i - 1], fTemp);

		}

		//组合的时候，左右调换**************************************************
		for (int i = 0; i < 32; i++) {
			temp[i] = right[16][i];
			temp[32 + i] = left[16][i];
		}

		
		temp = changeInverseIP(temp);

		return TypeConversion.hexInt2byte(temp);

	}

	/**
	 * 
	 * DES解密--->对称密钥
	 * 
	 * 解密算法与加密算法基本相同，不同之处仅在于轮子密钥的使用顺序逆序，即解密的第1
	 * 
	 * 轮子密钥为加密的�?6 轮子密钥，解密的�? 轮子密钥为加密的�?5 轮子密钥，�?…，
	 * 
	 * 解密的第16 轮子密钥为加密的�? 轮子密钥�?
	 * 
	 * @param source密文
	 * 
	 * @param key密钥
	 * 
	 * @return
	 */

	public static byte[] discryptionECB(byte[] source, byte[] keyByte) throws DESCryptionException{
		if(source.length%8!=0)
			throw new DESCryptionException("source length is not multiple of 8 bytes");
		if(keyByte.length!=8)
			throw new DESCryptionException("key length is not 8 bytes");
		byte[] result = new byte[source.length];
		byte[] temp = new byte[8];
		for(int i=0;i<source.length/8;i++){
			System.arraycopy(source,0, temp, 0,8); 
			System.arraycopy(discryptionDES(temp,keyByte),0, result, i*8,8); 
		}
		return result;
	}
	public static byte[] discryptionDES(byte[] source, byte[] keyByte)  throws DESCryptionException{
		if(source.length%8!=0)
			throw new DESCryptionException("source length is not multiple of 8 bytes");
		if(keyByte.length!=8)
			throw new DESCryptionException("key length is not 8 bytes");


		// 第一步初始置
		int[] data = TypeConversion.byte2hexInt(source);
		data = changeIP(data);

		int[] left = new int[32];

		int[] right = new int[32];

		int[] tmp = new int[32];

		for (int j = 0; j < 32; j++) {

			left[j] = data[j];

			right[j] = data[j + 32];

		}

		int[] key = TypeConversion.byte2hexInt(keyByte);
		int[][] subKey = setKey(key);// sub key ok

		for (int i = 16; i > 0; i--) {

			// 获取(48bit)的轮子密�?

			/********* 不同之处 **********/

			int[] sKey = subKey[i - 1];

			tmp = left;

			// R1 = L0

			left = right;

			// L1 = R0 ^ f(L0,K1)

			int[] fTemp = f(right, sKey);// 32bit

			right = diffOr(tmp, fTemp);

		}

		//组合的时候，左右调换**************************************************

		for (int i = 0; i < 32; i++) {

			data[i] = right[i];

			data[32 + i] = left[i];

		}

		data = changeInverseIP(data);


		return TypeConversion.hexInt2byte(data);

	}


	public static class DESCryptionException extends Exception{
		public DESCryptionException(String msg){
			super(msg);
		}
	}
	
	public static void main(String[] args) throws Exception {

		System.out.println("/*************Tripe-DES*************/");

		// 主密�?

		String masterKey = "1111111111111111";

		// 数据

		String data = "11111111111111111111111111111111";

		//System.out.println(DES_3(data, masterKey, 0));
		
		byte[] key = TypeConversion.hexStringToByte(masterKey);
		byte[] source = TypeConversion.hexStringToByte(data);
		
		if(source.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(source.length/8+1)*8];
			System.arraycopy(source, 0, temp, 0, source.length);
			source = temp;
		}
		System.out.println("source:"+source.length+TypeConversion.byte2hex(source,0,source.length));
		byte[] encodeResult = DESCryption.encryptionDES(source,key);
		System.out.println("encodeResult:"+TypeConversion.byte2hex(encodeResult));
		byte[] decodeResult =  DESCryption.discryptionDES(encodeResult, key);
		System.out.println("decodeResult:"+TypeConversion.byte2hex(decodeResult));

	}

}