package com.a3650.posserver.bill99.security;

import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.security.SecurityControl;
import com.a3650.posserver.core.utils.TypeConversion;

public class SecurityControlBill99 extends SecurityControl{
	static Logger logger = Logger.getLogger(SecurityControlBill99.class);
	//public static byte[] ROOT_KEY = TypeConversion.hexStringToByte("DBED28F6415162BD");
	public SecurityControlBill99(String posId) throws SecurityControlException{
		super(posId);
	}
	/**
	 * 构造消息摘要
	 */
	@Override
	public byte[] getMac(byte[] mab) throws SecurityControlException {
		// TODO Auto-generated method stub
		if(mab.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(mab.length/8+1)*8];
			System.arraycopy(mab, 0, temp, 0, mab.length);
			mab = temp;
		}
		// 构造消息摘要
		int blockNum = mab.length / 8;
		byte[] checkBlock = new byte[8];

		System.arraycopy(mab, 0, checkBlock, 0, 8);
		byte[] result = encryptoECB(checkBlock,mak);

		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(mab, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
			result = encryptoECB(result,mak);
		}
		return result;
	}
	@Override
	public void parsePIK() throws SecurityControlException {
		
	}
	public void parseWorkKey(DataInPackage mDataPackage) throws SecurityControlException {
		// TODO Auto-generated method stub
//		终端号：20100601 
//		流水号：000022 
//		交易日期时间：0910220859 (不包括年份) 	
		//String msg = "000000010001280308170653";
		String posIdByte =    mDataPackage.getDataMap().containsKey(41)?(String)mDataPackage.getMapValue(41):"";//ascii
		String lineIdByte =    mDataPackage.getDataMap().containsKey(11)?(String)mDataPackage.getMapValue(11):"";//bcd
		String localDateByte =   mDataPackage.getDataMap().containsKey(13)?(String)mDataPackage.getMapValue(13):"";//bcd本地交易日期  n4  MMDD
		String localTimeByte =    mDataPackage.getDataMap().containsKey(12)?(String)mDataPackage.getMapValue(12):"";//bcd 本地交易时间  n6  hhmmss
		String keySource = posIdByte
		      +lineIdByte
		      +localDateByte
		      +localTimeByte;
		byte[] source = null;
		try{
			source = TypeConversion.stringToAscii(keySource);
		}catch(Exception e){
			return;
		}
		int blockNum = source.length % 8>0?(source.length/8+1):source.length/8;
		pik = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		
		
		for (int i = 0; i < blockNum; i++) {
			byte[] checkBlock = new byte[8];
			
			if(i==blockNum-1&&source.length%8>0){
				System.arraycopy(source, i * 8, checkBlock, 0, source.length%8);
			}else{
				System.arraycopy(source, i * 8, checkBlock, 0, 8);
			}
			
			for (int j = 0; j < 8; j++) {
				pik[j] = (byte) (pik[j] ^ checkBlock[j]);
			}
		}
		
		pik = encryptoECB(pik,rootKey);
		mak = pik;
		trk = pik;
	}

	@Override
	public void parseMAK() throws SecurityControlException {
		// TODO Auto-generated method stub
		mak = rootKey;
	}

	@Override
	public void parseTRK() throws SecurityControlException {
		// TODO Auto-generated method stub
		trk = rootKey;
	}

	@Override
	public byte[] encode(byte[] buffer) throws SecurityControlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] decode(byte[] buffer) throws SecurityControlException {
		// TODO Auto-generated method stub
		return null;
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
			countNo = (String)mDataMap.get(2);
		}else if(mDataMap.containsKey(35)){
			countNo = (String)mDataMap.get(35);
		}else{
			return null;
		}
		return countNo;
	}
	
	/**
	 * 解密PIN码
	 */
	@Override
	public String decryptPin(DataInPackage mDataPackage)
			throws SecurityControlException {
		// TODO Auto-generated method stub
		parseWorkKey(mDataPackage);
		
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
