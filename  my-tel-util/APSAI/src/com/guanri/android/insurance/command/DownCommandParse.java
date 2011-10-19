package com.guanri.android.insurance.command;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 下载命令解析
 * 
 * @author Administrator
 * 
 */
public class DownCommandParse {
	public static Logger logger = Logger.getLogger(DownCommandParse.class);//日志对象
	private byte[] retutnByte;
	private DownCommandBean downCommandBean;

	/**
	 * 构造函数
	 * @roseuid 4DF8330C030D
	 */
	public DownCommandParse(byte[] retutnByte) throws CommandParseException{
		this.retutnByte = retutnByte;
		downCommandBean = new DownCommandBean();
		parseCommand();
	}

	/**
	 * 解析返回数据，其中包含命令头和命令体
	 * 
	 * 命令头长度为69字节
	 * 命令头格式如下：
	 * 1 命令长度 Len HEX 2 表示命令的整体长度，但是不包含本身这2个字节；
	 * 2 命令码 CommandCode ASC 6 具体的命令，应与上行命令相同
	 * 3 命令序列 ComSeq HEX 4 表示该命令的应答序列号，应与上行命令相同
	 * 4 数字签名 MAC HEX 16 经过加密计算得到8个字节数字签名，填充前8字节，后8字节预留暂不使用
	 * 5 应答码 AnswerCode HEX 1 具体定义如下：
	 *	 0x00：命令正确执行，没有错误；
	 *	 0x01： 本次命令正确执行，但对账不平（用于对账命令），需要上传明细；
	 *	 0x02~0x0F值预留；
	 *	
	 *	 0x10：错误，接收到的数据包数字签名或解密错，终端收到这个应答码后需要重新发登录命令取新的加密密钥；
	 *	 0x11：命令序列号失步，终端接收到这个应答码后需要将下一次的通信序列号归0，后台也同步归0
	 *	 0x12：命令序列号暂停，终端接收到这个应答码后不累加命令序列号
	 *	 0xff: 处理成功，和0x00结果码的唯一区别是不累加通信序列号，用于多包命令的中间应答使用
	 * 	  其它值不做具体定义，统一根据应答信息来确定。
	 * 6 应答信息 AnswerMsg ASC 20   表示后台给服务器的应答信息。如果不足字节后面的用0x00补充。（应答码为0x00时本域无效）
	 * 7 应答信息 Mark ASC 20 和上一字段一起构成应答信息
		
	 * 
	 * @return DownCommandBean
	 * @roseuid 4DF72FB00242
	 */
	private void parseCommand() throws CommandParseException{
		try{
			//解析命令头
			int cmdLength = retutnByte.length;
			downCommandBean.setLen(TypeConversion.bytesToShort(retutnByte, 0));//命令长度 Len HEX 2
			downCommandBean.setCommandCode(TypeConversion.asciiToString(retutnByte, 2, 6));//命令码 CommandCode ASC 6 
			downCommandBean.setComSeq(String.valueOf(TypeConversion.bytesToInt(retutnByte, 8)));//命令序列 ComSeq HEX 4 
			byte[] mac = new byte[16];
			System.arraycopy(retutnByte, 12, mac, 0, mac.length);
			downCommandBean.setMac(mac);//数字签名 MAC
			downCommandBean.setAnswerCode(String.valueOf(retutnByte[28]));//应答码 
			downCommandBean.setAnswerMsg(TypeConversion.asciiToString(retutnByte, 29, 40));//应答信息
			downCommandBean.setMark("");//应答信息 Mark
			if(cmdLength-69>0){
				//验证数字签名是否正确
				byte[] body = new byte[cmdLength-69];//命令体长度cmdLength-69
				System.arraycopy(retutnByte, 69, body, 0, body.length);
				byte[] tempMac = CryptionControl.getInstance().getMAC(downCommandBean.getCommandCode(), body);
				
				//验证数字签名是否正确,并解密命令体
				if(TypeConversion.bytesToLong(tempMac, 0)==
					TypeConversion.bytesToLong(downCommandBean.getMac(),0)){//摘要正确，解析解码
					//解码数据
					body =  CryptionControl.getInstance().getFormerData(downCommandBean.getCommandCode(), body);
					downCommandBean.setBody(body);
				}else{
					throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_downcommand_mac_error));
				}
			}
		}catch(UnsupportedEncodingException ue){
			throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_downcommand_parse_error)); 
		}
	}

	public byte[] getRetutnByte() {
		return retutnByte;
	}

	public DownCommandBean getDownCommandBean() {
		return downCommandBean;
	}
}
