package com.szxys.mhub.base.communication.webservice;

import java.nio.ByteBuffer;

/**
 * 工具类，用来提供字节序转换等操作工具
 * 
 * @author sujinyi
 **/

public class WebUtils {

	/**
	 * 将short转为低字节在前，高字节在后的byte数组,向web端上传输数据时使用
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toLH(short n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * 将short转为高字节在前，低字节在后的byte数组,接收web端数据时使用
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toHH(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * 将int转为低字节在前，高字节在后的byte数组,向web端上传输数据时使用
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * 将int转为高字节在前，低字节在后的byte数组,接收web端数据时使用
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toHH(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * 将低字节数组转换为int,接收web端数据时使用
	 * 
	 * @param byte[]
	 * @return int
	 */
	public static int lBytesToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[3 - i] >= 0) {
				s = s + b[3 - i];
			} else {
				s = s + 256 + b[3 - i];
			}
			s = s * 256;
		}
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		return s;
	}

	/**
	 * 将byte数组中的元素倒序排列
	 * 
	 * @param b
	 *            :需要转序的字节数组
	 * @return byte[] :转换后的字节数组
	 */
	public static byte[] bytesReverseOrder(byte[] b) {
		int length = b.length;
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[length - i - 1] = b[i];
		}
		return result;
	}

	/**
	 * 将byte数组中的元素倒序排列
	 * 
	 * @param l
	 *            :需要转序的long
	 * @return byte[] :转换后的字节数组
	 */
	public static byte[] longReverseOrder(long l) {
		byte[] bytes = new byte[8];

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.putLong(l);

		bytes = bytesReverseOrder(byteBuffer.array());
		return bytes;

	}

	/**
	  * 高字节数组到short的转换
	  * @param b byte[]
	  * @return short
	  */
	public static short hBytesToShort(byte[] b) {
	  int s = 0;
	  if (b[0] >= 0) {
	    s = s + b[0];
	    } else {
	    s = s + 256 + b[0];
	    }
	    s = s * 256;
	  if (b[1] >= 0) {
	    s = s + b[1];
	  } else {
	    s = s + 256 + b[1];
	  }
	  short result = (short)s;
	  return result;
	} 

	/**
	  * 低字节数组到short的转换
	  * @param b byte[]
	  * @return short
	  */
	public static short lBytesToShort(byte[] b) {
	  int s = 0;
	  if (b[1] >= 0) {
	    s = s + b[1];
	    } else {
	    s = s + 256 + b[1];
	    }
	    s = s * 256;
	  if (b[0] >= 0) {
	    s = s + b[0];
	  } else {
	    s = s + 256 + b[0];
	  }
	  short result = (short)s;
	  return result;
	} 
	
	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static int getReverseBytesInt(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	public static long getLong(byte[] bb, int index) {
		return ((((long) bb[index + 0] & 0xff) << 56)
				| (((long) bb[index + 1] & 0xff) << 48)
				| (((long) bb[index + 2] & 0xff) << 40)
				| (((long) bb[index + 3] & 0xff) << 32)
				| (((long) bb[index + 4] & 0xff) << 24)
				| (((long) bb[index + 5] & 0xff) << 16)
				| (((long) bb[index + 6] & 0xff) << 8) | (((long) bb[index + 7] & 0xff) << 0));
	}

	public static long getReverseBytesLong(byte[] bb, int index) {
		return ((((long) bb[index + 7] & 0xff) << 56)
				| (((long) bb[index + 6] & 0xff) << 48)
				| (((long) bb[index + 5] & 0xff) << 40)
				| (((long) bb[index + 4] & 0xff) << 32)
				| (((long) bb[index + 3] & 0xff) << 24)
				| (((long) bb[index + 2] & 0xff) << 16)
				| (((long) bb[index + 1] & 0xff) << 8) | (((long) bb[index + 0] & 0xff) << 0));
	}
}
