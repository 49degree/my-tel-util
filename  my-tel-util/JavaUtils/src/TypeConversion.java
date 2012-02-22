

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
	 * byte[]转换成short数 高位在前
	 * 
	 * @param data
	 *            包括short的byte[]
	 * @param offset
	 *            偏移量
	 * @return short数
	 */
	
	public static short bytesToShortEx(byte[] data, int offset) {
		short num = 0;
		for (int i = offset; i < offset + 2; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
   	
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
	 */
	public static int bytesToIntEx(byte[] data, int offset) {
		int num = 0;
		for (int i = offset; i < offset + 4; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	
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
	 * byte[]转换成long数 高位在前
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 */
	public static long bytesToLongEX(byte[] data, int offset) {
		long num = 0;
		for (int i = offset; i < offset + 8; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}
	
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
	
	/**
	 * short类型转换成byte[] 高位在前
	 * 
	 * @param num
	 *            short数
	 * @return byte[]
	 */
	public static byte[] shortToBytesEx(short num) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (num >>> (8-i*8));
		}  
		return b;
	}
    
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
	 */ 
	public static byte[] intToBytesEx(int num) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
	
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
	 * long类型转换成byte[] 高位在前
	 * 
	 * @param num
	 *            long数
	 * @return byte[]
	 */
	public static byte[] longToBytesEx(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		
		
		return b;
	}
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
	 * byte 转换成无符号整数
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 */
	public static long bytesToSingleNum(byte[] data, int offset,int length) {

		byte[] temp = new byte[8];
		System.arraycopy(data, offset, temp, 0, length);
		temp[length] = 0x00;
		return TypeConversion.bytesToLong(temp, 0);
	}	
	
	/**
	 * 从原数据指定位置开始取指定长度的数组
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 */
	public static byte[] getSomeBytesFromSource(byte[] data, int offset,int length) {

		byte[] temp = new byte[length];
		System.arraycopy(data, offset, temp, 0, length);
		return temp;
	}	

	/**
	 * 字符串数组转BYTE	数组
	 * @param chars
	 * @return
	 */
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
	
	/**
	 * BYTE	数组转字符串数组
	 * @param chars
	 * @return
	 */
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
     * 以GBK编码ascii转换成String
     * @param value
     * @return
     * @throws Exception
     */
	public final static String DEFAULT_ENCODE = "GBK";
    public static String asciiToString(byte[] value) throws UnsupportedEncodingException 
    {  
        return new String(value,DEFAULT_ENCODE);  
    }  
    
    /**
     * 以GBK编码ascii转换成String
     * @param value 数组
     * @param begin 开始位置
     * @param length 长度
     * @return
     * @throws Exception
     */
    public static String asciiToString(byte[] value,int begin,int length) throws UnsupportedEncodingException {  
    	byte[] temp = new byte[length];
    	System.arraycopy(value, begin, temp, 0, length);
        return new String(temp,DEFAULT_ENCODE);  
    }  
    
    /**
     * 以GBK编码String转换成ascii
     * @param input
     * @return
     * @throws Exception
     */
    public static byte[] stringToAscii(String input) throws UnsupportedEncodingException{
    	if(input==null){
    		input="";
    	}
      return input.getBytes(DEFAULT_ENCODE);
    }
    
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byte2hex(byte[] b,int startIndex,int length) //二行制转字符串
    {
		if(b==null||startIndex+length>b.length){
			return null;
		}
		String hs="";
		String stmp="";
		for (int n=startIndex;n<startIndex+length;n++)
		{
			stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1) hs=hs+"0"+stmp;
			else hs=hs+stmp;
		}
		return hs.toUpperCase();
    }
	
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byte2hexBlack(byte[] b,int startIndex,int length) //二行制转字符串
    {
		if(b==null||startIndex+length>b.length){
			return null;
		}
		String hs="";
		String stmp="";
		for (int n=startIndex;n<startIndex+length;n++)
		{
			stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1) hs=hs+"0"+stmp;
			else hs=hs+stmp;
			
			hs+=" ";
		}
		return hs.toUpperCase();
    }	
	
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byte2hex(byte[] b,int startIndex) {//二行制转字符串
		return byte2hex(b,startIndex,b.length-startIndex);
    }
	
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byte2hex(byte[] b) {//二行制转字符串
		return byte2hex(b,0,b.length);
    }
	
	
    /**
     * 二进制转2进制字符串
     * @param b
     * @return
     */
	public static String byteTo0XString(byte[] b,int startIndex,int length) //二行制转字符串
    {
		StringBuffer temp = new StringBuffer();
		if(b==null||startIndex+length>b.length){
			return null;
		}
		for (int n=startIndex;n<startIndex+length;n++)
		{
			for(int i=0;i<8;i++){
				temp.append((b[n]>>7-i)&0x01);
			}
		}
		return temp.toString();
    }
	
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byteTo0XString(byte[] b,int startIndex) {//二行制转字符串
		return byteTo0XString(b,startIndex,b.length-startIndex);
    }
	
    /**
     * 二进制转16进制字符串
     * @param b
     * @return
     */
	public static String byteTo0XString(byte[] b) {//二行制转字符串
		return byteTo0XString(b,0,b.length);
    }
	
	/**
	 * 
	 * byte转0\1二进制int数组
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static int[] byte2hexInt(byte[] source) {
		int[] dest = new int[source.length*8];
		for (int i = 0; i < source.length; i++) {
			for(int j=0;j<8;j++){
				dest[i*8+j] = (source[i]>>7-j)&0x01;
			}
		}
		return dest;
	}
	/**
	 * 
	 * 0\1二进制int数组转byte
	 * 
	 * @param source
	 * 
	 * @return
	 */

	public static byte[] hexInt2byte(int[] source) {
		byte[] dest = new byte[source.length/8];
		for (int i = 0; i < dest.length; i++) {
			for(int j=0;j<8;j++){
				dest[i] = (byte)(dest[i]|source[i*8+j]<<7-j);
			}
		}
		return dest;
	}

	
	/**
	 * 获取字符串
	 * @param data
	 * @param dataIndex 表示要转换的长度
	 * @return
	 */
	public static String getASCString(byte[] data,int dataIndex) {
		byte[] temp = new byte[dataIndex];
		System.arraycopy(data, 0, temp, 0, temp.length);
		int k = 0;
		for (int i = 0; i < temp.length; i ++) {
			if ((temp[i] >= 32)) temp[k ++] = temp[i];
		}
		return new String(temp, 0, k);	
	}
	
	/**
	 * 数字字符串转BDC压缩码
	 * @param s
	 * rightAlign 是否右对齐
	 * @return
	 */
	public static byte[] str2bcd(String s,boolean rightAlign) {
		if (s.length() % 2 != 0) {
			s = rightAlign?"0" + s:s+"0";
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length/2; i++) {
			int high = cs[2*i] - 48;
			int low = cs[2*i + 1] - 48;
			baos.write(high << 4 | low);
		}
		return baos.toByteArray();
	}

	/**
	 * 数字字符串转BDC压缩码
	 * @param s
	 * rightAlign 是否右对齐
	 * @return
	 */
	public static byte[] str2bcd(String s) {
		return str2bcd(s,true);
	}
	
	
	/**
	 * BCD码转字符串
	 * @param b
	 * @return
	 */
	public static String bcd2string(byte[] b) {
		return bcd2string(b,0,b.length);
	}
	/**
	 * BCD码转字符串
	 * @param b
	 * @return
	 */
	public static String bcd2string(byte[] b,int begin,int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = begin; i < begin+length; i++) {
			int h = ((b[i] & 0xff) >> 4) + 48;
			sb.append((char) h);
			int l = (b[i] & 0x0f) + 48;
			sb.append((char) l);
		}
		return sb.toString();
	}
	
	/**
	 * java二进制,字节数组,字符,十六进制,BCD编码转换 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) ("0123456789ABCDEF".indexOf(achar[pos])<< 4|"0123456789ABCDEF".indexOf(achar[pos+1]));
		}
		return result;
	}
	
	public static void main(String args[]) {
		try{
			
			byte[] temp = stringToAscii(null);
			byte[] s = TypeConversion.intToBytes(1);
			String str = "123456我";
			System.out.println(str.getBytes("GBK").length);
			byte[] a = new byte[2];
			a[0] = str.getBytes("GBK")[6];
			a[1] = str.getBytes("GBK")[7];
			System.out.println(new String(a,"GBK"));
			  // //由此可见,Java中的:
			// 一个int=4个byte 一个String str1='a'; =1个byte 一个String str1='中'; =2个byte
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
}