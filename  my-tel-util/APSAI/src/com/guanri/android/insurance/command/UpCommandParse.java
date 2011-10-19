package com.guanri.android.insurance.command;

import java.io.UnsupportedEncodingException;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 解析上传命令
 * 
 * @author Administrator
 * 
 */
public class UpCommandParse {
	public static Logger logger = Logger.getLogger(UpCommandParse.class);//日志对象
	
	private UpCommandBean upCommandBean;//命令内容对象
	

	/**
	 * 构造函数
	 * @roseuid 4DF8330C01E4
	 */
	public UpCommandParse(UpCommandBean upCommandBean){
		this.upCommandBean = upCommandBean;
	}

	/**
	 * 把上传命令对象解析成BYTE数组，其中包含命令头和命令体
	 * 命令头长度为56字节
	 * @return byte[]
	 * @roseuid 4DF7142D0261
	 */
	public byte[] getCommandBuffer()  throws CommandParseException{
		if(upCommandBean==null||upCommandBean.getBody()==null||upCommandBean.getBody().length==0){
			throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_upcommand_null)); 
		}
		byte[] sendByte = null;
		try{
			byte[] body = null;
			int oldBodyLength = upCommandBean.getBody().length;
			if(oldBodyLength%8>0){//如果命令体长度不是8的倍数
				body = new byte[oldBodyLength+8-oldBodyLength%8];
				System.arraycopy(upCommandBean.getBody(), 0, body, 0, oldBodyLength);
			}else{
				body = upCommandBean.getBody();
			}
			
			//对命令体进行加密并计算消息摘要
			CryptionControl cryptionControl = CryptionControl.getInstance();
			body = cryptionControl.getDES(upCommandBean.getCommandCode(),body);//加密
			byte[] mac = cryptionControl.getMAC(upCommandBean.getCommandCode(),body);//计算摘要
			
			/*
			 * 命令头格式如下 
			 * 1、命令长度 Len HEX 2 表示命令的整体长度，但是不包含本身这2个字节 
			 * 2、命令码 CommandCode ASC 6 
			 * 3、终端ID PosID ASC 8
			 * 4、命令序列 ComSeq HEX 4
			 * 5、数字签名 MAC HEX 16  经过加密计算得到8个字节数字签名，填充前8字节，后8字节预留暂不使用 保留 
			 * 6、Mark HEX 20 为了以后兼容扩充保留的20个字节，目前统一填写0x20，就是空格。
			 */
			sendByte = new byte[56+body.length];
			System.arraycopy(TypeConversion.shortToBytes((short)(sendByte.length-2)), 0, sendByte, 0, 2);
			System.arraycopy(TypeConversion.stringToAscii(upCommandBean.getCommandCode()), 0, sendByte, 2, 6);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, sendByte, 8, 8);
			System.arraycopy(TypeConversion.intToBytes(CommandControl.getInstance().getComSeq()), 0, sendByte, 16, 4);	
			System.arraycopy(mac, 0, sendByte, 20, 8);
			System.arraycopy(new byte[20], 0, sendByte, 36, 20);
			//加密后的命令体
			System.arraycopy(body, 0, sendByte, 56, body.length);
			return sendByte;
		}catch(UnsupportedEncodingException ue){
			throw new CommandParseException(StringUtils.getStringFromValue(R.string.apsai_common_upcommand_parse_error)); 
		}
	}
	

	public UpCommandBean getUpCommandBean() {
		return upCommandBean;
	}


}
