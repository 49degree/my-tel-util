package test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.security.SecurityUtils;
import com.a3650.posserver.core.utils.TypeConversion;

public class SecurityControlAllinpayTest extends TestCase{
	static Logger logger = Logger.getLogger(SecurityControlAllinpayTest.class);
	public void testEncode(){
		byte[] mab = TypeConversion.hexStringToByte("74EEB5AF5CBD104BF4411BC2188B1DD9B6CB18C882E8A7CA1ABF678AE3811D70CD151012C3A262E7E5761E377054CC32D110B35EFDC7B65688A2D849A91BA3C942212A651E0E864C500A5A4B7C78EB92F36FF4561424733A0D8A2A5397AD1A8C2E5A9B36EB31ACF03481054DB2F650169269A9750FC24860107A1C2774AB37862E5A9B36EB31ACF02E5A9B36EB31ACF02E5A9B36EB31ACF09959A9CBCF6BB2E2165D1E914C815D9B934A55E4C5D9EC1F0ABEB9148E9C6DB5491CDF90241F1F9FA2FEAAB4CF01BAC4");
		byte[] mak = TypeConversion.hexStringToByte("A0B68CE67F24E33907B20BF1283C8DFC");
		SecurityUtils securityUtils = new SecurityUtils();
		
		mab = securityUtils.encryptoECBKey2(mab, mak);
		String hexBlock = TypeConversion.byte2hex(mab);
		logger.info("result:"+hexBlock);
	}
	
	public void testMac(){
		SecurityUtils securityUtils = new SecurityUtils();
		byte[] mab = TypeConversion.hexStringToByte("0230603804810AC4801116EAAE150C713816646390353ACA454BBB999999000173100615051001200008630979003030303030303030303034333030303030313030303033303936313031353639313030303100873041303136202020202020202020202020202020202030313032333038313031383030363531323334353620202020202020202020202020202020202020202020202020207CB2E2CAD4B1A3B5A57C313030302E323323313536001901000001000200000000");
		byte[] mak = TypeConversion.hexStringToByte("5A522C558E91A2AE");
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
		result = securityUtils.encryptoECB(mab,mak);
		//d将加密后的结果与后8 个字节异或：
		mab =   TypeConversion.stringToAscii(hexBlock.substring(8,16));
		for (int j = 0; j < 8; j++) {
			result[j] = (byte) (result[j] ^ mab[j]);
		}
		//f)  用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算。
		result = securityUtils.encryptoECB(result,mak);
		//g)  将运算后的结果（ENC BLOCK2）转换成16 个HEXDECIMAL：
		hexBlock = TypeConversion.byte2hex(result);
		logger.info("hexBlock:"+hexBlock);
		//h)  取前8个字节作为MAC值。
		result = TypeConversion.stringToAscii(hexBlock.substring(0,8));
		logger.info("hexBlock:"+TypeConversion.byte2hex(result));
	}
}
