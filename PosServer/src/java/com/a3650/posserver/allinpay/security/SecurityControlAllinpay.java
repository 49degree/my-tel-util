package com.a3650.posserver.allinpay.security;

import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.allinpay.datapackage.DataMessageTypeAllinpay;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.security.SecurityControl;
import com.a3650.posserver.core.utils.TypeConversion;

public class SecurityControlAllinpay extends SecurityControl{
	static Logger logger = Logger.getLogger(SecurityControlAllinpay.class);
	public SecurityControlAllinpay(String posId) throws SecurityControlException{
		super(posId);
	}

	@Override
	public byte[] getMac(byte[] mab) throws SecurityControlException {
		// TODO Auto-generated method stub
		if(mab.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(mab.length/8+1)*8];
			System.arraycopy(mab, 0, temp, 0, mab.length);
			mab = temp;
		}
		//a对MAB，按每8个字节做异或（不管信息中的字符格式），如果最后不满8个字节，则添加“0X00”
		int blockNum = mab.length/8;
		byte[] result = new byte[8];
		System.arraycopy(mab, 0, result, 0,8);
		byte[] checkBlock = new byte[8];
		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(mab, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
		}
		//b将异或运算后的最后8个字节（RESULT BLOCK）转换成16 个HEXDECIMAL：
		String hexBlock = TypeConversion.byte2hex(result);
		logger.info("hexBlock:"+hexBlock);
		//c取前8 个字节用MAK加密：
		mab =   TypeConversion.stringToAscii(hexBlock.substring(0,8));
		result = encryptoECB(mab,mak);
		//d将加密后的结果与后8 个字节异或：
		mab =   TypeConversion.stringToAscii(hexBlock.substring(8,16));
		for (int j = 0; j < 8; j++) {
			result[j] = (byte) (result[j] ^ mab[j]);
		}
		//f)  用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算。
		result = encryptoECB(result,mak);
		//g)  将运算后的结果（ENC BLOCK2）转换成16 个HEXDECIMAL：
		hexBlock = TypeConversion.byte2hex(result);
		logger.info("hexBlock:"+hexBlock);
		//h)  取前8个字节作为MAC值。
		result = TypeConversion.stringToAscii(hexBlock.substring(0,8));
		return result;
	}




	@Override
	public void parsePIK() throws SecurityControlException {
		// TODO Auto-generated method stub
		pik = TypeConversion.hexStringToByte(posTerminalCheckIn.getPik());
		
	}

	@Override
	public void parseMAK() throws SecurityControlException {
		// TODO Auto-generated method stub
		mak = TypeConversion.hexStringToByte(posTerminalCheckIn.getMak().substring(0,16));
	}

	@Override
	public void parseTRK() throws SecurityControlException {
		// TODO Auto-generated method stub
		trk = TypeConversion.hexStringToByte(posTerminalCheckIn.getTrk());
	}


	/**
	 * 解密方法
	 * @param buffer
	 * @param key
	 * @return
	 */
	private byte[] decrypt(byte[] buffer,byte[] key){
		byte[] temp = null;
		if(this.keyLenth==8)
			temp = this.decryptECB(buffer, key);
		else if(this.keyLenth==16)
			temp = this.decryptECBKey2(buffer, key);
		else if(this.keyLenth==32)
			temp = this.decryptECBKey3(buffer, key);
		return temp;
	}
	
	
	@Override
	public byte[] encode(byte[] buffer) throws SecurityControlException {
		// TODO Auto-generated method stub
		int binginIndex = DataMessageTypeAllinpay.getInstance().getMessageTypeLength()-2;
		int becodeLength = buffer.length - binginIndex;
		byte[] temp = new byte[becodeLength%8>0?((becodeLength/8+1)*8):becodeLength];//补足8字节整数倍
		System.arraycopy(buffer, binginIndex, temp, 0, becodeLength);
		
		
		//加密数据过程也是使用解密算法，因为是对象加密，所以终端收到数据后加密即可以还原数据
		temp = decrypt(temp,trk);
		
		byte[] returnBuffer = new byte[binginIndex+temp.length];
		System.arraycopy(buffer, 0, returnBuffer,0 , binginIndex);       
		System.arraycopy(temp, 0, returnBuffer, binginIndex, temp.length);
		
		byte[] lengthbyte = TypeConversion.shortToBytesEx((short)(returnBuffer.length-2));
		System.arraycopy(lengthbyte, 0, returnBuffer, 0, 2);
		return returnBuffer;
	}

	@Override
	public byte[] decode(byte[] buffer) throws SecurityControlException {
		// TODO Auto-generated method stub
		
		int binginIndex = DataMessageTypeAllinpay.getInstance().getMessageTypeLength()-2;
		byte[] temp = new byte[buffer.length - binginIndex];
		System.arraycopy(buffer, binginIndex, temp, 0, temp.length);
		//解密
		temp = decrypt(temp,trk);
		
		System.arraycopy(temp, 0, buffer, binginIndex, temp.length);
		return buffer;
	}

	/**
	 * 获取账号信息
	 * 
	 * 手输卡号
	 * 如为手输卡号，从所输卡号（2域）右边数第二位开始，向左取12位，作为参与PIN加、解密的PAN。
	 * 刷卡方式
	 * 如为刷卡方式，从磁道2（35域）分隔符‘＝’左边第二位开始，向左取12个字符，作为参与PIN加密的PAN；如只有磁道3（36域），
	 * 则从磁道3分隔符‘＝’左边第二位开始，向左取12个字符，作为参与PIN加、解密的PAN。
	 */
	@Override
	public String getAcountNo(TreeMap<Integer,Object> mDataMap) throws SecurityControlException{
		String countNo = null; 
		if(mDataMap.containsKey(2)){
			//存在主账号
			countNo = TypeConversion.bcd2string(decrypt((byte[])mDataMap.get(2),trk));
			logger.info("countNo:"+countNo);

		}else if(mDataMap.containsKey(35)){
			countNo = TypeConversion.bcd2string(decrypt((byte[])mDataMap.get(35),trk));
			logger.info("countNo:"+countNo);
		}else{
			return null;
		}
		int length = Integer.parseInt(countNo.substring(0, 2));
		countNo = countNo.substring(2,2+length);
		return countNo;
	}
	
	/**
	 * 解密PIN码
	 */
	@Override
	public String decryptPin(DataInPackage mDataPackage)
			throws SecurityControlException {
		String countNo = getAcountNo(mDataPackage.getDataMap());
		AcountInputType inputType = AcountInputType.handInput;
		if(mDataPackage.getDataMap().containsKey(2))//手输卡号
			inputType = AcountInputType.handInput;
		else if(mDataPackage.getDataMap().containsKey(35))//刷卡方式
			inputType = AcountInputType.brushCard;
		
		return decryptPin((byte[])mDataPackage.getMapValue(52),getPanSource(countNo,inputType),pik);
	}

	/**
	 * 验证PIN码是否正确
	 */
	@Override
	public boolean checkPin(DataInPackage mDataPackage) throws SecurityControlException {
		String pin = decryptPin(mDataPackage);
		return "123456".equals(pin);
	}


}
