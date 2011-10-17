package com.guanri.android.jpos.iso.unionpay;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TreeMap;

import com.guanri.android.exception.FieldTooLongException;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 块钱封包类
 * @author Administrator
 *
 */

public class JposPackageUnionPay extends JposPackageFather{
	Logger logger = Logger.getLogger(JposPackage99Bill.class);
	
	public final static String PARSE_METHOD = "parseFeild";
	
	public JposPackageUnionPay(TreeMap<Integer,Object> sendMap,JposMessageType messageType){
		super(sendMap,messageType);
	}
	
	
	/**
	 * 子类根据定义的解析位数据的方法名构造方法名称，调用父类中的解析方法
	 * 方法名最好与位标志有规律
	 */
	@Override
	protected byte[] parseBitValue(int position){
		String methodName = PARSE_METHOD+position;//解析相应位数据的方法名称
		return parseBitValue(position,methodName);
	}
	
	

	/**
	 * 第2位数据
	 * 12.3.3  主账号     
	 * 位图位置：02  
	 * 格式：变长,LLVAR  
	 * 类型：N..19，压缩时用右靠BCD码表示最大10个字节的变长域。 
	 * 描述：主账号信息域 
	 * 唯一的确认一个用户交易的基本帐号。 
	 * @return
	 */
	public byte[] parseFeild2(Object o) throws FieldTooLongException{ 
		String acount = (String)o;
		logger.debug("账号："+acount);
		if(acount!=null&&acount.length()>19){
			throw new FieldTooLongException("Feild2 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,2);
		
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;//最长19位数字，要用20位BCD码
	}
	
	/**
	 * 第3位数据
	 * 处理码
	 * 格式：定长  
		类型：N6，压缩时用BCD码表示的3个字节的定长域。 
		描述：用于描述交易对客户帐户造成何种影响的代码。  
		处理代码和信息码一起可唯一定义一种交易的类型。  
		处理代码由以下三部分组成：  
		位置描述  
		1－2交易动作码  
		3－4付出帐户类型，用于借记类，如查询、代收费、转场交易。 
		5－6收入帐户类型，用于代收费、转帐等。  
	 * @return
	 */
	public byte[] parseFeild3(Object o) throws FieldTooLongException{ 
		String acount = (String)o;
		logger.debug("处理码："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild3 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,6, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		
		return temp;
	}
	
	/**
	 * 交易金额  
	 * 位图位置：04 
	 * 格式：定长 
	 * 类型：N12，压缩时用BCD码表示的6个字节的定长域。 
	 * 描述：帐户人要求交易的交易金额，不含任何处理和交易费用。
	 *  金额的表示和货币代码有关，应能表示相应货币的最小单位。
	 *  参ISO4217有关货币代码定义。 
	 *  如“000000000100”用于表示人民币，表示1.00元；如用于表示意大利货币，则表示100
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild4(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("交易金额："+acount);
		if(acount!=null&&acount.length()>12){
			throw new FieldTooLongException("Feild4 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,12, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * POS流水号
	 * 位图位置 11
	 * 格式: 定长
	 * 类型: N6 压缩使用BCD码表示的3个字节的定长域
	 * 描述: 终端交易的跟踪号码
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild11(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("POS流水号："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild11 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,6, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 本地交易时间
	 * 位图位置:12
	 * 格式: 定长, HHMMSS
	 * 类型: N6 压缩时用BCD码表示3个字节的长度
	 * 描述: 交易在终端上发生的时间
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild12(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("本地交易时间："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild12 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,6, false); 
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 本地交易日期
	 * 位图位置 13
	 * 格式:定长 MMDD
	 * 类型: N4 压缩时用BCD码表示的2个字节的定长域
	 * 描述: 交易在终端上发生的时间
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild13(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("本地交易日期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild13 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false); 
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 卡有限期
	 * 格式: 定长,YYMM
	 * 类型: N4 压缩时用BCD码表示的2个字节的定长域
	 * 描述: 卡的有限期 年年月月
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild14(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("卡有限期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild14 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	
	/**
	 * 清算日期
	 * 格式: 定长,MMDD
	 * 类型: N4 压缩时用BCD码表示的2个字节的定长域
	 * 描述: 卡的有限期 年年月月
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild15(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("清算日期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild15 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * POS输入方式
	 * 位图位置 22
	 * 格式 定长
	 * 类型 N3 压缩时右靠BCD码表示的2个字节的定长域
	 * 描述 在服务端上定义PIN和PAN的输入方式
	 * 1-2 表示
	 * @param o
	 * @return
	 * @throws FieldTooLongExcetion
	 */
	public byte[] parseFeild22(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("POS输入方式："+acount);
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild22 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,3, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * Card Sequence Number 卡序列号
	 * 位图位置: 23
	 * 格式: 定长
	 * 类型: N3 压缩时用右靠BCD码表示的2个字节的定长域
	 * 描述: IC卡的序列号,用于区分
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild23(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("卡序列号："+acount);
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild23 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,3, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 服务点条件码
	 * 位图位置:25
	 * 格式:定长
	 * 类型:N2 压缩时用右靠BCD码表示的1个字节的定长域
	 * 描述:定义交易发生的服务点类型
	 * 用法说明:  
	 * 00  正常交易
	 * 06 预授权类交易
	 * 60 追加预授权
	 * 64 分期付款消费
	 * 91 基于PDOC电子钱包的IC卡圈存类交易 	
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild25(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("服务点条件码："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild25 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,2, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 服务点PIN获取码
	 * 位图位置:26
	 * 格式:定长
	 * 类型:N2 压缩时用右靠BCD码表示的1个字节的定长域
	 * 描述:服务点设备所允许输入的个人密码明文的最大长度
	 * 用法说明:下面是CYBERBANK 支持的服务点条件代码
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild26(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("服务点条件码："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild26 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,2, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 受理方标识码
	 * 位图位置:32
	 * 格式:变长 LLVAR
	 * 类型:N..11 
	 * 描述:受理方标识码，POS交易中一般指POS中心
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild32(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("服务点条件码："+acount);
		if(acount!=null&&acount.length()>11){
			throw new FieldTooLongException("Feild32 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,2);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 二磁道数据
	 * 位图位置:35
	 * 格式: 变长,LLVAR
	 * 类型: Z..37,用右靠BCD码表示的最大19个字节的第二磁道数据.  
	 * 描述: 写在卡二磁道的数据.数据组成遵循ISO7811-1985标准,数据中包含域分隔符,
	 * 但不包含卡的启始.结束符,LRC等
	 * 暂时全当做BCD处理
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild35(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("二磁道数据："+acount);
		if(acount!=null&&acount.length()>37){
			throw new FieldTooLongException("Feild35 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), false,2); 
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 三磁道数据
	 * 位图位置:36
	 * 格式: 变长,LLLVAR
	 * 类型: Z..104 用右靠BCD码表示的最大52个字节的第三磁道数据
	 * 描述: 写在卡三磁道的数据,数据应组成遵循ISO4909标准,数据中包含域分隔符,但
	 * 不包含启始,结束符,LRC等
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild36(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("三磁道数据："+acount);
		if(acount!=null&&acount.length()>104){
			throw new FieldTooLongException("Feild36 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,3);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 检索参考号
	 * 位图位置: 37
	 * 格式: 定长
	 * 类型: AN12, 12个字节的定长ASCII
	 * 描述: 检索索引号用来在任何时间标识一个金融,授权,自动冲正交易
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild37(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("参考号："+acount);
		if(acount!=null&&acount.length()>12){
			throw new FieldTooLongException("Feild37 to long");
		}
		byte[] temp = str2ASCII(acount,12);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp; 
	}
	
	/**
	 * 授权码
	 * 格式: 定长
	 * 类型: AN6 6个字节的定长ASCII
	 * 描述: 交易授权机构返回的返回代码.
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild38(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("授权码："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild38 to long");
		}
		byte[] temp = str2ASCII(acount,6);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 响应码
	 * 位图位置: 39
	 * 格式: 定长
	 * 类型: AN2 2个字节的定长ASCII
	 * 描述: 对一交易地你故意其处理结果的编码
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild39(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("响应码："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild39 to long");
		}
		byte[] temp = str2ASCII(acount,2);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp; 
	}
	/**
	 * 终端号
	 * 位图位置: 41
	 * 格式: 定长
	 * 类型 : ANS8 8个字节的定长ASCII
	 * 描述: 定义在收单单位中定义一个服务终端的标识码,在同一商户服务终端标识码应
	 * 唯一
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild41(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("终端号："+acount);
		if(acount!=null&&acount.length()>8){
			throw new FieldTooLongException("Feild41 to long");
		}
		byte[] temp = str2ASCII(acount,8);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 商户号
	 * 位图位置:42
	 * 格式:定长
	 * 类型: ANS15,15个字段的定长ASII
	 * 描述: 在本地和网络中定义交易单位(商户)的编码
	 */
	public byte[] parseFeild42(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("商户号："+acount);
		if(acount!=null&&acount.length()>15){
			throw new FieldTooLongException("Feild42 to long");
		}
		byte[] temp = str2ASCII(acount,15);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 附加响应数据
	 * 位图位置: 44
	 * 格式: LLVAR
	 * 类型: ANS..25  最大25个字节的ASCII
	 * 描述: 在金融()交易中授权机构返回的其他信息,也可在响应授权或者其他交易
	 * 需要时,提供额外的一些补充数据(例如持卡人身份证号)
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild44(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("附加响应数据："+acount);
		if(acount!=null&&acount.length()>25){
			throw new FieldTooLongException("Feild44 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,2);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	
	
	
	/**
	 * 附加数据
	 * 位图位置: 48
	 * 格式: 变长, LLLVAR
	 * 类型: ANS...322,3个字节长度 + 最大322个字节的数据
	 * 描述: 私有,用于存放POS批结算时的结算总额,批上送时的交易明细和交易明细总笔数                                                                                        
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild48(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("附加数据："+acount);
		if(acount!=null&&acount.length()>322){
			throw new FieldTooLongException("Feild48 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 货币代码
	 * 格式: 定长
	 * 位图位置 : 49
	 * 类型: AN3
	 * 描述: 按ISO4217 定义的交易货币代码,用来表示"交易金额" 所用的货币种类
	 * 用法: 人民币的货币代码为 156
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild49(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("货币代码："+acount);
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild49 to long");
		}
		byte[] temp = str2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 未实现
	 * 个人识别码
	 * 位图位置 52
	 * 格式: 定长
	 * 类型: B64 8个字节的定长二进制数
	 * 描述: 用户在服务终端上交易用于识别用户合法性的一些数字
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild52(Object o) throws FieldTooLongException{
		
		return null;
	}
	
	/**
	 * 安全控制信息
	 * 位图位置 53
	 * 格式 定长
	 * 类型 N16
	 * 描述 与安全相关的信息
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild53(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("处理码："+acount);
		if(acount!=null&&acount.length()>16){
			throw new FieldTooLongException("Feild53 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,16, false);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 附加金额
	 * 位图位置 54
	 * 格式  变长 LLLVAR
	 * 类型 AN..020
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild54(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("附加金额："+acount);
		if(acount!=null&&acount.length()>322){
			throw new FieldTooLongException("Feild54 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * IC卡数据域
	 * 位图位置 55
	 * 格式  变长 LLLVAR
	 * 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild55(Object o) throws FieldTooLongException{
		return null;
	}
	
	
	/**
	 * PBOC店子钱包标准的交易信息
	 * 位图位置 58
	 * 格式  变长 LLLVAR
	 * 类型 ANS..100
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild58(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("PBOC店子钱包标准的交易信息："+acount);
		if(acount!=null&&acount.length()>322){
			throw new FieldTooLongException("Feild58 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 自定义域
	 * 位图位置 60
	 * 格式  LLLVAR
	 * 类型 N..013
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild60(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>13){
			throw new FieldTooLongException("Feild60 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), false,3);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 原始信息域
	 * 位图位置 61
	 * 格式 LLLVAR
	 * 类型 N..029
	 * 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild61(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>29){
			throw new FieldTooLongException("Feild61 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), false,3);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}

	/**
	 * 自定义域
	 * 位图位置  62
	 * 格式  LLLVAR
	 * 类型 ANS..512
	 * @param o
	 * @return
	 */
	public byte[] parseFeild62(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>512){
			throw new FieldTooLongException("Feild62 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 自定义域
	 * 位图位置 63
	 * 格式 LLLVAR
	 * 属性  ANS..163
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild63(Object o) throws FieldTooLongException {
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>163){
			throw new FieldTooLongException("Feild63 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 报文鉴别码
	 * 位图位置  64
	 * 格式   定长 
	 * 属性 B64  8个字节定长域
	 * 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild64(Object o) throws FieldTooLongException{
		return null;
	}
	
	
	/**
	 * 计算消息摘要需要的源数据
	 */
	protected byte[] getMacSource(){
		return null;
	}
	
	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(date.getHours()+""+date.getMinutes()+date.getSeconds());
	}
}
