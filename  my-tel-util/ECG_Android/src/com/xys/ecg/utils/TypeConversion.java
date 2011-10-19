package com.xys.ecg.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * ����ת����
 * 
 * @author
 * 
 */
public class TypeConversion {
	/**
	 * short����ת����byte[] ��λ��ǰ
	 * 
	 * @param num
	 *            short��
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
	 * short����ת����byte[] ��λ��ǰ
	 * 
	 * @param num
	 *            short��
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
	 * int����ת����byte[] ��λ��ǰ
	 * 
	 * @param num
	 *            int��
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
	 * int����ת����byte[] ��λ��ǰ
	 * 
	 * @param num
	 *            int��
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
	 * long����ת����byte[]
	 * 
	 * @param num
	 *            long��
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
	 * long����ת����byte[] ��λ��ǰ
	 * 
	 * @param num
	 *            long��
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
	 * byte[]ת����short��
	 * 
	 * @param data
	 *            ����short��byte[]
	 * @param offset
	 *            ƫ����
	 * @return short��
	 
	
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
	 * byte[]ת����short��
	 * 
	 * @param data
	 *            ����short��byte[]
	 * @param offset
	 *            ƫ����
	 * @return short��
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
	 * byte[]ת����int�� ��λ��ǰ
	 * 
	 * @param data
	 *            ����int��byte[]
	 * @param offset
	 *            ƫ����
	 * @return int��
	 
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
	 * byte[]ת����int�� ��λ��ǰ
	 * 
	 * @param data
	 *            ����int��byte[]
	 * @param offset
	 *            ƫ����
	 * @return int��
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
	 * byte[]ת����long��
	 * 
	 * @param data
	 *            ����long��byte[]
	 * @param offset
	 *            ƫ����
	 * @return long��
	 
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
	 * byte[]ת����long�� ��λ��ǰ
	 * 
	 * @param data
	 *            ����long��byte[]
	 * @param offset
	 *            ƫ����
	 * @return long��
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
	 * JAVA�ַ����ֽ�ת��
	 * @param ch
	 * @return
	 */
	public static byte[] charToByte(char ch) {
		int temp = (int) ch;
		byte[] b = new byte[2];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue(); // �����λ���������λ
			temp = temp >> 8; // ������8λ
		}
		return b; 
	}

	/**
	 * JAVA�ֽ����鵽�ַ�ת��
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
	 * JAVA�ַ�����ת����C++ ���ֽ��ַ�����
	 * @param b
	 * @return
	 */
	public static byte[] stringToBytes(String inStr) {
		char[] cArray = inStr.toCharArray();
		byte[] bArray = new byte[cArray.length];
		int i = 0;
		for(char c:cArray){
			int temp = (int) c;
			bArray[i++] = new Integer(temp & 0xff).byteValue(); // �����λ���������λ
		}
		return bArray;
	}
	
	/**
	 * C++ ���ֽ��ַ����鵽JAVA�ַ�����ת��
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
			String str1 = "��";
			System.out.println("s:" + s.length);// �����ֵΪ4
			System.out.println("a:" + str.getBytes().length);// �����ֵΪ1
			System.out.println("��:" + str1.getBytes().length);// �����ֵΪ2													// //�ɴ˿ɼ�,Java�е�:
			// һ��int=4��byte һ��String str1='a'; =1��byte һ��String str1='��'; =2��byte
	}*/
	
}
