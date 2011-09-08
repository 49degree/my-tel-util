package com.guanri.android.jpos.iso.bill99;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import com.guanri.android.exception.FieldIsNullException;
import com.guanri.android.exception.FieldTooLongException;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.unionpay.JposMessageTypeUnionPay;
import com.guanri.android.jpos.network.CryptionControl;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

/**
 * 银联封包类
 * @author Administrator
 *
 */

public class JposPackage99Bill extends JposPackageFather{
	Logger logger = Logger.getLogger(JposPackage99Bill.class);
	
	public final static String PARSE_METHOD = "parseFeild";
	
	
	public JposPackage99Bill(TreeMap<Integer,Object> sendMap,JposMessageType99Bill messageType){
		super(sendMap,messageType);
	}
	
	/**
	 * 子类根据定义的解析位数据的方法名构造方法名称，调用父类中的解析方法
	 * 方法名最好与位标志有规律
	 * 例如方法名为parseFeild1，parseFeild2，parseFeild3，1、2、3分别表示位代码
	 * 
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
	public byte[] parseFeild2(Object o) throws FieldTooLongException, FieldIsNullException{ 
		String acount = (String)o;
		logger.debug("账号："+acount);
		if(acount!=null&&acount.length()>19){
			throw new FieldTooLongException("Feild2 to long");
		}else if(acount == null) {
			throw new FieldIsNullException("Feild2 is Null");
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
		
		byte[] temp = fixLengthStr2cbcd(acount,6, true);
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
		byte[] temp = fixLengthStr2cbcd(acount,12, true);
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
		byte[] temp = fixLengthStr2cbcd(acount,6, true);
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
		byte[] temp = fixLengthStr2cbcd(acount,6, true); 
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
		byte[] temp = fixLengthStr2cbcd(acount,4, true); 
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
		byte[] temp = fixLengthStr2cbcd(acount,4, true);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 分期期数
	 * 位图位置: 19
	 * 格式: 变长 LLVAR
	 * 类型: ANS..2 最大两个字节的ASCII
	 * 描述: 分期消费的期数
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild19(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("分期期数："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild19 to long");
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
	 * 分期返回金额
	 * 位图位置: 20
	 * 格式: 定长,压缩时使用右靠BCD表示最大10个字节的定长域
	 * 类型: N20
	 * 描述: 分期消费的首付款金额和每期应付金额
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild20(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("分期返回金额："+acount);
		if(acount!=null&&acount.length()>20){
			throw new FieldTooLongException("Feild20 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,20, true);
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
		byte[] temp = fixLengthStr2cbcd(acount,3, true);
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
		byte[] temp = fixLengthStr2cbcd(acount,3, true);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * NII
	 * 位图位置:24
	 * 格式:定长
	 * 类型:N3 压缩时右靠BCD码表示的2个字节的定长域
	 * 描述:用于在IEN网络中表示网络地址.
	 * 其作用相当于拨号POS的TPDU信息,在快钱系统默认使用009
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild24(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("NII："+acount);
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild24 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,3, true);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	/**
	 * 服务点条件码
	 * 位图位置:25
	 * 格式:定长
	 * 类型:N2 压缩时用右靠BCD码表示的1个字节的定长域
	 * 描述:定义交易发生的服务点类型
	 * 用法说明:下面是CYBERBANK 支持的服务点条件代码
	 * 02 自动柜员机 (ATM)
	 * 10  银行终端(10)
	 * 20 电话银行
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
		byte[] temp = fixLengthStr2cbcd(acount,2, true);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 未确定
	 * 扩展主账号
	 * 位图位置: 34
	 * 格式: 变长,LLVAR
	 * 类型: N..28 最大28个字节的变长ASCII
	 * 描述: 扩展主账号信息域
	 * 目前主要用于通过消费方式为储值卡充值时上送待充值的储值卡全卡号
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild34(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("扩展主账号："+acount);
		if(acount!=null&&acount.length()>28){
			throw new FieldTooLongException("Feild34 to long");
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
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,2); 
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
	 * 参考号
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
	 * 位图位置 38
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
		byte[] temp =  floatLengthstr2ASCII(acount,2);
		try {
			logger.debug("结果："+temp.length+":"+TypeConversion.asciiToString(temp));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * TLV处理域
	 * 位图位置 46
	 * 格式: 变长, LLLVAR
	 * 类型: ANS..255  最大128个字节的ASCII
	 * 描述: 该域用于处理TLV数据
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild46(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("TLV处理域："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild46 to long");
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
	 * **********************************************************************************
	 * 有异议
	 * **********************************************************************************
	 * 附加数据
	 * 位图位置: 48
	 * 格式: 变长, LLLVAR
	 * 类型: ANS...322,压缩时用右靠BCD标识                                                                                        
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
	 * **********************************************************************************
	 * 有异议
	 * **********************************************************************************
	 * 货币代码
	 * 格式: 定长
	 * 类型: AN3,压缩时用右靠BCD吗表示的2个字节的定长域.
	 * 描述: 按ISO4217 定义的交易货币代码,用来表示"交易金额" 所用的货币种类
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
	 * 结算商户号
	 * 位图位置: 53
	 * 格式: 定长
	 * 类型: ANS15 15个字节的定长ASCII
	 * 描述: 结算商户编号
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild53(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("结算商户号："+acount);
		if(acount!=null&&acount.length()>15){
			throw new FieldTooLongException("Feild53 to long");
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
	 * Additional Amounts
	 * 位图位置: 54
	 * 格式: 变长 LLLVAR
	 * 类型: ANS..20  最大20个字节的ASCII
	 * 描述:此域由授权行主机讲客户的余额返回给收单终端,以显示或打印在客户回单上
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild54(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("Additional Amounts："+acount);
		if(acount!=null&&acount.length()>20){
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
	 * IC CARD TRANSACTION DATA
	 * 位图位置: 55
	 * 格式: 变长, LLLVAR
	 * 类型: ANS..255
	 * 描述: 本域为IC卡交易使用
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild55(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("IC CARD TRANSACTION DATA："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild55 to long");
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
	 * TC-结果,发卡行脚本结果,下装EMV参数相关数据及其他
	 * 位图位置: 56
	 * 格式: 变长, LLLVAR
	 * 类型: ANS..255 压缩时采用右靠BCD码表示最大的128字节的数据
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild56(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug(" TC-结果："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild56 to long");
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
	 * Reserved
	 * 位图位置: 57
	 * 格式: 变长, LLLVAR
	 * 类型: ANS..255, 压缩时采用右靠BCD
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild57(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("Reserved："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild57 to long");
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
	 * 汇率信息
	 * 位图位置: 59
	 * 格式: 定长
	 * 类型: N24,压缩时使用右靠BCD吗表示最大12个字节的定长域.
	 * 描述: 汇率信息域
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild59(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("汇率信息："+acount);
		if(acount!=null&&acount.length()>24){
			throw new FieldTooLongException("Feild59 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,24,true);
		logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 订单号
	 * 位图位置:60
	 * 格式: 变长, LLLVAR
	 * 类型,ANS..30 
	 * 描述: 本规范中此域用于存放要求POS上送的数据
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild60(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("订单号："+acount);
		if(acount!=null&&acount.length()>30){
			throw new FieldTooLongException("Feild60 to long");
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
	 * 位图位置 61
	 * 格式 变长 LLLVAR
	 * 类型: ANS..30 最大30个字节的ASCII
	 * 描述: 原交易信息域 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild61(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>30){
			throw new FieldTooLongException("Feild61 to long");
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
	 * 位图位置: 62
	 * 格式: 变长, LLVAR
	 * 类型: N..20 最大20个字节的ASCII
	 * 描述: 原始信息域
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */		
	public byte[] parseFeild62(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>20){
			throw new FieldTooLongException("Feild62 to long");
		}
		byte[] temp =  floatLengthstr2ASCII(acount,2);
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
	 * 格式: 定长 
	 * 类型: ANS30 30个字节的ASCII
	 * 描述: 自定义域
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild63(Object o) throws FieldTooLongException{
		String acount = (String)o;
		logger.debug("自定义域："+acount);
		if(acount!=null&&acount.length()>30){
			throw new FieldTooLongException("Feild63 to long");
		}
		byte[] temp = str2ASCII(acount,30);
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
	 * 消息鉴定码
	 * 位图位置: 64
	 * 格式 定长
	 * 类型: 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild64(Object o) throws FieldTooLongException{
		byte[] macSource = this.getMacSource();
		macSource = CryptionControl.getInstance().getMac(macSource);//构造消息摘要的原始数据域，
		//使用macSource传送给POS终端得到MAC值
		//后续增加。。。。。
		
		
		return null;
	}
	
	
	/**
	 * 计算消息摘要需要的源数据
	 * 需要用到的位数据如下（必须安装下面的顺序进行组织）：
	 * 	2  Primary Account Number 
	 *	3  Processing Code 
	 *	4  Amount, Transaction 
	 *	11  System Trace Audit Number 
	 *	12  Time, Local Transaction 
	 *	13  Date, Local Transaction 
	 *	49  Currency Code, Transaction 
	 *	38  Authorization  Identification Response 
	 *	39  Response Code 
	 *	41  Card Acceptor Terminal Identification 
	 *
	 *  请注意：MAC计算数据需要按此数据域顺序产生。
	 *  特别的，49 域值是在38/39/41域前。  
	 *  同时，第 2 域，第 49 域如果是奇数位数字，
	 *  那么在其内容前面补入的‘0’字符也需要带入MAC计算数据。 
	 */
	protected byte[] getMacSource(){
		byte[] macSource = new byte[256];
		int index = 0;
		try{
			//2  Primary Account Number 
			Object value = mSendMap.get(2);
			byte[] temp;
			try {
				temp = this.parseFeild2(value);
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 1, temp.length-1, 128);
				index += temp.length-1;
				
				//3  Processing Code 
				value =  mSendMap.get(3);
				temp = this.parseFeild3(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(4);
				temp = this.parseFeild4(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(11);
				temp = this.parseFeild11(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(12);
				temp = this.parseFeild12(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(13);
				temp = this.parseFeild13(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(49);
				temp = this.parseFeild49(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(38);
				temp = this.parseFeild38(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(39);
				temp = this.parseFeild39(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
				
				value =  mSendMap.get(41);
				temp = this.parseFeild41(value);//定长
				macSource = Utils.insertEnoughLengthBuffer(macSource, index, temp, 0, temp.length, 128);
				index += temp.length;
			} catch (FieldIsNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//头2为BCD码为长度，去掉
			
			
		}catch(FieldTooLongException fe){
			return null;
		}
		if(index<=0){
			return null;
		}
		//判断是否存满数据
		if(index<macSource.length){
			byte[] temp = new byte[index];
			System.arraycopy(macSource, 0, temp, 0, index);
			macSource = temp;
		}
		return macSource;
	}
	
	
	public static void main(String[] args){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(2, new String("123456"));
		//sendMap.put(3, new String("1234"));
		sendMap.put(13,new String("0905"));
		JposPackageFather jposPackage99Bill = new JposPackage99Bill(sendMap,new JposMessageType99Bill());
		
		jposPackage99Bill.packaged();
		
	}
}
