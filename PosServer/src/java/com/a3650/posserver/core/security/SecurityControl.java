/***********************************************************************
 * Module:  SecurityControl.java
 * Author:  Administrator
 * Purpose: Defines the Interface SecurityControl
 ***********************************************************************/

package com.a3650.posserver.core.security;

import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.service.impl.PosTerminalBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;


/**
 * 安全控制对象
 * 对外提供以下功能：
 * 1、计算MAC
 * 2、验证MAC
 * 3、解密PIN码
 * 4、加密报文
 * 5、解密报文
 * @author Administrator
 *
 */
public abstract class SecurityControl extends SecurityUtils{
	static Logger logger = Logger.getLogger(SecurityControl.class);
	protected String posId;
	protected PosTerminalCheckIn posTerminalCheckIn;
	protected int keyLenth = 8;//密钥长度8(单倍长密钥 )、16（双倍长）、24（3倍长）
	protected byte[] rootKey;//主密钥
	protected byte[] pik;//加密pin码密钥
	protected byte[] mak;//加密mac密钥
	protected byte[] trk;//加密报文密钥
	
	public enum AcountInputType{
		handInput,brushCard
	}

	public SecurityControl(String posId) throws SecurityControlException{
		this.posId = posId;
		parseKey();
	}
	
	public abstract byte[] getMac(byte[] mab) throws SecurityControlException;
	public abstract byte[] encode(byte[] buffer) throws SecurityControlException;
	public abstract byte[] decode(byte[] buffer) throws SecurityControlException;
	public abstract String decryptPin(DataInPackage mDataPackage) throws SecurityControlException;
	public abstract boolean checkPin(DataInPackage mDataPackage) throws SecurityControlException;
	
	public abstract String getAcountNo(TreeMap<Integer,Object> mDataMap) throws SecurityControlException;
	protected abstract void parsePIK() throws SecurityControlException;
	protected abstract void parseMAK() throws SecurityControlException;
	protected abstract void parseTRK() throws SecurityControlException;
	

	public boolean checkMac(DataInPackage mDataPackage) throws SecurityControlException{
		// TODO Auto-generated method stub
		if(mDataPackage==null||!mDataPackage.getDataMap().containsKey(64)){
			throw new SecurityControlException("原始数据为空!");
		}
		byte[] mab = mDataPackage.getMabSource();
		
		logger.info("MAB:"+TypeConversion.byte2hex(mab));

		byte[] mac = getMac(mab);
		logger.info("mac:"+TypeConversion.byte2hex(mac));
		byte[] beCheckMac = (byte[])mDataPackage.getMapValue(64);
		logger.info("beCheckMac:"+TypeConversion.byte2hex(beCheckMac));
		if(mac==null||beCheckMac.length!=mac.length){
			return false;
		}
		//判断计算的MAC是否和返回的MAC相同
		if(Utils.calCheckSum(mac)==Utils.calCheckSum(beCheckMac))
			return true;
		return false;
	}
	

	/**
	 * 解密pin码
	 * 
	 */
	protected String decryptPin(byte[] pin,byte[] pan,byte[] mpik) throws SecurityControlException {
		// TODO Auto-generated method stub
		logger.info("pin:"+TypeConversion.byte2hex(pin));
		logger.info("pan:"+TypeConversion.byte2hex(pan));
		logger.info("mpik:"+TypeConversion.byte2hex(mpik));
		byte[] decodePin = null;
		if(this.keyLenth==8)
			decodePin = this.decryptECB(pin, mpik);
		else if(this.keyLenth==16)
			decodePin =  this.decryptECBKey2(pin, mpik);
		else if(this.keyLenth==32)
			decodePin =  this.decryptECBKey3(pin, mpik);
		
		byte[] result = new byte[8];
		for (int j = 0; j < 8; j++) {
			result[j] = (byte) (decodePin[j] ^ pan[j]);
		}
		logger.info(TypeConversion.byte2hex(result));
		//截取PIN
		int pinLength = Integer.parseInt(TypeConversion.bcd2string(result).substring(0,2));
		String pinStr = TypeConversion.bcd2string(result);
		pinStr = pinStr.substring(2,pinLength+2);
		return pinStr;
	}
	
	
	protected void parseKey() throws SecurityControlException {
		// TODO Auto-generated method stub
		PosTerminal posTerminal = new PosTerminalBuss().getBaseDao().get(posId);
		keyLenth = posTerminal.getRootKeyLength();
		String keys = posTerminal.getRootKey();
		posTerminalCheckIn = new PosTerminalCheckInBuss().getBaseDao().get(posId);
		
		
		String[] keyArgs = new String[keys.length()/(2*keyLenth)];
		for(int i=0;i<keyArgs.length;i++){
			keyArgs[i] = keys.substring(2*i*keyLenth, 2*(i+1)*keyLenth);
		}
		if(keyArgs!=null&&keyArgs.length>=posTerminalCheckIn.getRootKeyId()){
			rootKey = TypeConversion.hexStringToByte(keyArgs[posTerminalCheckIn.getRootKeyId()]);
		}else{
			throw new SecurityControlException("获取主密钥失败");
		}
		parsePIK();
		parseMAK();
		parseTRK();
	}
	
	
	/**
	 * 获取PAN
	 * 
	 * 手输卡号
	 * 如为手输卡号，从所输卡号（2域）右边数第二位开始，向左取12位，作为参与PIN加、解密的PAN。
	 * 刷卡方式
	 * 如为刷卡方式，从磁道2（35域）分隔符‘＝’左边第二位开始，向左取12个字符，作为参与PIN加密的PAN；如只有磁道3（36域），
	 * 则从磁道3分隔符‘＝’左边第二位开始，向左取12个字符，作为参与PIN加、解密的PAN。
	 */
	public byte[] getPanSource(String countNo,AcountInputType inputType) throws SecurityControlException{
		// TODO Auto-generated method stub
		if(inputType==AcountInputType.handInput){//手输卡号
			//存在主账号
			if(countNo.length()>12)
				countNo = countNo.substring(countNo.length()-13,countNo.length()-1);
			else
				countNo = countNo.substring(0,countNo.length()-1);
		}else if(inputType==AcountInputType.brushCard){//刷卡方式
			if(countNo.indexOf("=")>12)
				countNo = countNo.substring(countNo.indexOf("=")-13,countNo.indexOf("=")-1);
			else if(countNo.indexOf("=")>0)
				countNo = countNo.substring(0,countNo.indexOf("=")-1);
		}
		if(countNo==null)
			return null;
		int length = countNo.length();
		for(int i=0;i<16-length;i++){
			countNo ="0"+countNo;
		}
		return TypeConversion.str2bcd(countNo);
	}

	
	public static class SecurityControlException extends Exception{
		public SecurityControlException(String msg){
			super(msg);
		}
	}
	
}