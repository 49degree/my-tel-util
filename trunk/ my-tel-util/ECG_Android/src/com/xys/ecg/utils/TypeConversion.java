package com.xys.ecg.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 类型转换器
 * 
 * @author
 * 
 */
public class TypeConversion {
	/**
	 * short类型转换成byte[] 高位在前
	 * 
	 * @param num
	 *            short数
	 * @return byte[]
	 
	public static byte[] shortToBytes(short num) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (num >>> (8-i*8));
		}  
		return b;
	}
    */
	/**
	 * short类型转换成byte[] 低位在前
	 * 
	 * @param num
	 *            short数
	 * @return byte[]
	 */
	public static byte[] shortToBytes(short num) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (num >>> (i*8));
		}  
		return b;
	}
	
	
	
	/**
	 * int类型转换成byte[] 高位在前
	 * 
	 * @param num
	 *            int数
	 * @return byte[]
	 
	public static byte[] intToBytes(int num) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
	*/ 
	/**
	 * int类型转换成byte[] 低位在前
	 * 
	 * @param num
	 *            int数
	 * @return byte[]
	 */ 
	public static byte[] intToBytes(int num) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (i * 8));
		}
		return b; 
	}

	
	/**
	 * long类型转换成byte[]
	 * 
	 * @param num
	 *            long数
	 * @return byte[]
	 
	public static byte[] longToBytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		
		
		return b;
	}
	*/
	/**
	 * long类型转换成byte[] 低位在前
	 * 
	 * @param num
	 *            long数
	 * @return byte[]
	 */
	public static byte[] longToBytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (i * 8));
		}
		return b;
	}
	
	/**
	 * byte[]转换成short数
	 * 
	 * @param data
	 *            包括short的byte[]
	 * @param offset
	 *            偏移量
	 * @return short数
	 
	
	public static short bytesToShort(byte[] data, int offset) {
		short num = 0;
		for (int i = offset; i < offset + 2; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
   */	
	/**
	 * byte[]转换成short数
	 * 
	 * @param data
	 *            包括short的byte[]
	 * @param offset
	 *            偏移量
	 * @return short数
	 */	
	
	public static short bytesToShort(byte[] data, int offset) {
		short num = 0;
		for (int i = offset + 1; i >offset-1 ; i--) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	
	
	/**
	 * byte[]转换成int数 高位在前
	 * 
	 * @param data
	 *            包括int的byte[]
	 * @param offset
	 *            偏移量
	 * @return int数
	 
	public static int bytesToInt(byte[] data, int offset) {
		int num = 0;
		for (int i = offset; i < offset + 4; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	*/
	/**
	 * byte[]转换成int数 低位在前
	 * 
	 * @param data
	 *            包括int的byte[]
	 * @param offset
	 *            偏移量
	 * @return int数
	 */
	
	public static int bytesToInt(byte[] data, int offset) {
		int num = 0;
		for (int i = offset + 3; i > offset-1; i--) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	


	/**
	 * byte[]转换成long数
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 
	public static long bytesToLong(byte[] data, int offset) {
		long num = 0;
		for (int i = offset; i < offset + 8; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	*/
	/**
	 * byte[]转换成long数 低位在前
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 */
	public static long bytesToLong(byte[] data, int offset) {
		long num = 0;
		for (int i = offset+7; i > offset - 1; i--) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}

	public static byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		byte[] bytes = bb.array();
		// System.arraycopy(bytes, 2, bytes, 0, bytes.length-2);

		return bytes;
	}

	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}

	/**
	 * JAVA字符到字节转换
	 * @param ch
	 * @return
	 */
	public static byte[] charToByte(char ch) {
		int temp = (int) ch;
		byte[] b = new byte[2];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b; 
	}

	/**
	 * JAVA字节数组到字符转换
	 * @param b
	 * @return
	 */
	public static char bytesToChar(byte[] b) {
		int s = 0;
		if (b[0] > 0)
			s += b[0];
		else
			s += 256 + b[0];
		
		s *= 256;
		if (b[1] > 0)
			s += b[1];
		else
			s += 256 + b[1];
		char ch = (char) s;
		return ch;
	}
	
	
	/**
	 * JAVA字符串的转换到C++ 单字节字符数组
	 * @param b
	 * @return
	 */
	public static byte[] stringToBytes(String inStr) {
		char[] cArray = inStr.toCharArray();
		byte[] bArray = new byte[cArray.length];
		int i = 0;
		for(char c:cArray){
			int temp = (int) c;
			bArray[i++] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
		}
		return bArray;
	}
	
	/**
	 * C++ 单字节字符数组到JAVA字符串的转换
	 * @param b
	 * @return
	 */
	public static char[] bytesToChars(byte[] bArray) {
		char[] cArray = new char[bArray.length];
		int s = 0;
		int i = 0;
		for(byte b:bArray){
			System.out.println(b);
			if(b!=0){
				s = b;
				char ch = (char) s;
				cArray[i++] = ch;
			}else{
				break;
			}

		}
		char[] cArray2 = new char[i];
		System.arraycopy(cArray, 0, cArray2, 0, i);
		return cArray2;
	}
	/**
	public static void main(String args[]) {
			byte[] s = TypeConversion.intToBytes(1);
			String str = "a";
			String str1 = "你";
			System.out.println("s:" + s.length);// 输出的值为4
			System.out.println("a:" + str.getBytes().length);// 输出的值为1
			System.out.println("你:" + str1.getBytes().length);// 输出的值为2													// //由此可见,Java中的:
			// 一个int=4个byte 一个String str1='a'; =1个byte 一个String str1='中'; =2个byte
	}*/
	
}
