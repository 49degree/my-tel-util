/***********************************************************************
 * Module:  DataPackageBill99.java
 * Author:  Administrator
 * Purpose: Defines the Class DataPackageBill99
 ***********************************************************************/

package com.a3650.posserver.allinpay.datapackage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.datapackage.DataInPackage;
import com.a3650.posserver.core.datapackage.DataMessageType;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.exception.FieldIsNullException;
import com.a3650.posserver.core.exception.FieldTooLongException;
import com.a3650.posserver.core.utils.TypeConversion;
import com.a3650.posserver.core.utils.Utils;


public class DataInPackageAllinpay extends DataInPackage {
	Logger logger = Logger.getLogger(DataInPackageAllinpay.class);
	
	public final static String PARSE_METHOD = "parseFeild";
	
	
	public DataInPackageAllinpay(TreeMap<Integer,Object> sendMap,DataMessageType messageType){
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
		
		byte[] temp = (byte[])o;
		
		byte[] lengthtemp = null;
		//补足长度位
		String length = String.valueOf(temp.length);
		if(length.length()<2){
			for(int i=2-length.length();i>0;i--){
				length= "0" + length;
			}
		}
		//获取长度位 BCD码
		lengthtemp = TypeConversion.str2bcd(length);
		
		byte[] result = new byte[temp.length+1];
		result[0] = lengthtemp[0];
		System.arraycopy(temp, 0, result, 1, temp.length);
		return result;
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
		//logger.debug("处理码："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild3 to long");
		}
		
		byte[] temp = fixLengthStr2cbcd(acount,6, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		
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
		//logger.debug("交易金额："+acount);
		if(acount!=null&&acount.length()>12){
			throw new FieldTooLongException("Feild4 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,12, true);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("POS流水号："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild11 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,6, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("本地交易时间："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild12 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,6, false); 
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("本地交易日期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild13 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false); 
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("卡有限期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild14 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 1.28.9　域15 清算日期(Date Of Settlement) 
	 * 1.28.9.1　变量属性
	 * N4，4个字节的定长数字字符域，压缩时用BCD码表示的2个字节的定长域。
	 *  格式：MMDD。 1.28.9.2　域描述
	 * POS中心和发卡方之间的交易结算日期。 
	 * 格式为MMDD，其中MM为月份，DD为日。
	 */
	public byte[] parseFeild15(Object o) throws FieldTooLongException{
		String acount = (String)o;
		//logger.debug("清算日期："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild14 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("分期期数："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild19 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,2);
		

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
		//logger.debug("分期返回金额："+acount);
		if(acount!=null&&acount.length()>20){
			throw new FieldTooLongException("Feild20 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,20, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("POS输入方式："+acount);
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild22 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,4, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("卡序列号："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild23 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,3, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("NII："+acount);
		if(acount!=null&&acount.length()>4){
			throw new FieldTooLongException("Feild24 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,3, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("服务点条件码："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild25 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,2, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 1.28.13　域26
		服务点PIN获取码(Point Of Service PIN Capture Code)
		1.28.13.1　变量属性
		N2，2个字节的定长数字字符域，压缩时用BCD码表示的1个字节的定长域。
		1.28.13.2　域描述
		务点设备所允许输入的个人密码明文的最大长度。
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild26(Object o) throws FieldTooLongException{
		String acount = (String)o;
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild25 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,2, false);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp; 
	}
	
	/**
	 * 受理方标识码(Acquiring Institution Identification Code)
		1.28.14.1　变量属性
		N..11(LLVAR)，2个字节的长度值＋最大11个字节的受理方标识码，
		压缩时用BCD码表示的1个字节的长度值＋用左靠BCD码表示的最大6个字节的受理方标识码。
		1.28.14.2　域描述
		受理方标识码。POS交易中一般指POS中心。
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild32(Object o) throws FieldTooLongException{
		String acount = (String)o;
		//logger.debug("扩展主账号："+acount);
		if(acount!=null&&acount.length()>11){
			throw new FieldTooLongException("Feild34 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,2);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		//logger.debug("扩展主账号："+acount);
		if(acount!=null&&acount.length()>28){
			throw new FieldTooLongException("Feild34 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,2);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
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
		
		byte[] temp = (byte[])o;
		
		byte[] lengthtemp = null;
		//补足长度位
		String length = String.valueOf(temp.length);
		if(length.length()<2){
			for(int i=2-length.length();i>0;i--){
				length= "0" + length;
			}
		}
		//获取长度位 BCD码
		lengthtemp = TypeConversion.str2bcd(length);
		
		byte[] result = new byte[temp.length+1];
		result[0] = lengthtemp[0];
		System.arraycopy(temp, 0, result, 1, temp.length);
		return result;
	}
	
	/**
	 * 三磁道数据
	 * 位图位置:36
	 * 格式: 变长,LLLVAR
	 * 类型: B..56 用右靠BCD码表示的最大52个字节的第三磁道数据
	 * 描述: 写在卡三磁道的数据,数据应组成遵循ISO4909标准,数据中包含域分隔符,但
	 * 不包含启始,结束符,LRC等
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild36(Object o) throws FieldTooLongException{
		
		byte[] temp = (byte[])o;
		
		byte[] lengthtemp = null;
		//补足长度位
		String length = String.valueOf(temp.length);
		logger.debug("三磁道数据长度"+length);
		if(length.length()<3){
			for(int i=3-length.length();i>0;i--){
				length= "0" + length;
			}
		}
		//获取长度位 BCD码
		lengthtemp = TypeConversion.str2bcd(length);
		
		byte[] result = new byte[temp.length+2];
		System.arraycopy(lengthtemp, 0, result, 0, 2);
		System.arraycopy(temp, 0, result, 2, temp.length);
		return result;
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
		//logger.debug("参考号："+acount);
		if(acount!=null&&acount.length()>12){
			throw new FieldTooLongException("Feild37 to long");
		}
		byte[] temp = str2ASCII(acount,12);

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
		//logger.debug("授权码："+acount);
		if(acount!=null&&acount.length()>6){
			throw new FieldTooLongException("Feild38 to long");
		}
		byte[] temp = str2ASCII(acount,6);

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
		//logger.debug("响应码："+acount);
		if(acount!=null&&acount.length()>2){
			throw new FieldTooLongException("Feild39 to long");
		}
		byte[] temp = str2ASCII(acount,2);

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
		//logger.debug("终端号："+acount);
		if(acount!=null&&acount.length()>8){
			throw new FieldTooLongException("Feild41 to long");
		}
		byte[] temp = str2ASCII(acount,8);

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
		//logger.debug("商户号："+acount);
		if(acount!=null&&acount.length()>15){
			throw new FieldTooLongException("Feild42 to long");
		}
		byte[] temp = str2ASCII(acount,15);

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
		//logger.debug("附加响应数据："+acount);
		if(acount!=null&&acount.length()>25){
			throw new FieldTooLongException("Feild44 to long");
		}
		byte[] temp =  floatLengthstr2ASCII(acount,2);

		return temp;
	}
	
	/**
	 * TLV处理域
	 * 位图位置 46
	 * 格式: 变长, LLLVAR
	 * 类型: ANS..255  
	 * -----------------------------------------------------------------
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild46(Object o) throws FieldTooLongException{
		if(o!=null&&o instanceof TreeMap){
			TreeMap<Integer,DataSelfFieldLeaf> data = (TreeMap<Integer,DataSelfFieldLeaf>)o;
			byte[] feild = new byte[182];
			int index = 0;
			Iterator<Integer> it = data.keySet().iterator();
			byte[] temp = null;
			while(it.hasNext()){
				try {
					int key = it.next();
					DataSelfFieldLeaf leaf = data.get(key);
					//	定长
					if(leaf.getLengthType() ==0){
						
						temp = TypeConversion.stringToAscii(leaf.getValue());
						logger.debug("定长" + TypeConversion.byte2hex(temp) +" : "+ leaf.getValue());
						System.arraycopy(temp, 0, feild, index, temp.length);
						index += temp.length;
					}
					//	变长
					else if(leaf.getLengthType() > 0){
						logger.debug("变长" + leaf.getValue());
						byte[] dytedata = TypeConversion.stringToAscii(leaf.getValue()); 
						// 获取数据的长度
						int datalength = dytedata.length;
						if((datalength+"").length() <= leaf.getLengthType()){
							byte[] dytelength = fixLengthStr2cbcd(String.valueOf(datalength), leaf.getLengthType(), true);
							System.arraycopy(dytelength, 0, feild, index, dytelength.length);
							index += dytelength.length;
							System.arraycopy(dytedata, 0, feild, index, dytedata.length);
							index += dytedata.length;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	


			}
			//返回数据，计算总长度，长度用3位BCD码表示
			logger.debug(index+"");
			byte[] returnData = new byte[index+2];
			System.arraycopy(fixLengthStr2cbcd(String.valueOf(index), 3, true), 0, returnData, 0, 2);//长度用3位BCD码表示
			System.arraycopy(feild, 0, returnData, 2, index);
			return returnData;
			
		}else if(o!=null&&o instanceof byte[]){
			byte[] returnData = new byte[((byte[])o).length+2];
			System.arraycopy(fixLengthStr2cbcd(String.valueOf(((byte[])o).length), 3, true), 0, returnData, 0, 2);//长度用3位BCD码表示
			System.arraycopy(o, 0, returnData, 2, returnData.length-2);
			return returnData;
		}else
			return null;
	}
	
	
	/**
	 * 1.28.24　域47
		附加数据 - 私有(Additional Data - Private)
		1.28.24.1　变量属性
		ans...999(LLVAR)，3个字节长度+ 最大999个字节的数据。
		压缩时用右靠BCD码表示的2个字节的长度值＋实际数据。
		实际数据采用TLV格式，所有的TLV总长度不能超过999字节。
		1.28.24.2　域描述
		私有，用于增值类业务特殊使用，具体使用见用法。
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild47(Object o) throws FieldTooLongException{
		String acount = (String)o;
		if(acount!=null&&acount.length()>999){
			throw new FieldTooLongException("Feild48 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);

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
		//logger.debug("附加数据："+acount);
		if(acount!=null&&acount.length()>322){
			throw new FieldTooLongException("Feild48 to long");
		}
		byte[] temp = floatLengthStr2cbcd(acount,String.valueOf(acount.length()), true,3);

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
		if(acount!=null&&acount.length()>3){
			throw new FieldTooLongException("Feild37 to long");
		}
		byte[] temp = str2ASCII(acount,3);

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
		
		return (byte[])o;
	}
	
	/**
	 * 安全控制信息(Security Related Control Information )
	 * 位图位置: 53
	 * 格式: 定长
	 * 类型: n16，16个字节的定长数字字符域。
	 * 压缩时用BCD码表示的8个字节的定长域。
	 * 描述: 安全控制信息
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild53(Object o) throws FieldTooLongException{
		String acount = (String)o;
		//logger.debug("结算商户号："+acount);
		if(acount!=null&&acount.length()>16){
			throw new FieldTooLongException("Feild53 to long");
		}
		byte[] temp = fixLengthStr2cbcd(acount,16, false);

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
		//logger.debug("Additional Amounts："+acount);
		if(acount!=null&&acount.length()>20){
			throw new FieldTooLongException("Feild54 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
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
		//logger.debug("IC CARD TRANSACTION DATA："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild55 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
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
		//logger.debug(" TC-结果："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild56 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
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
		//logger.debug("Reserved："+acount);
		if(acount!=null&&acount.length()>255){
			throw new FieldTooLongException("Feild57 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		return temp;
	}
	
	/**
	 * 1.28.31　域58
		PBOC电子钱包标准的交易信息（PBOC_ELECTRONIC_DATA）
		1.28.31.1　变量属性
		ans...100(LLLVAR)，3个字节的长度值＋最大100个字节的字母、数字字符、特殊符号，压缩时采用右靠2个字节表示长度值。
		1.28.31.2　域描述
		本标准中只支持电子钱包的应用。
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild58(Object o) throws FieldTooLongException{
		String acount = (String)o;
		if(acount!=null&&acount.length()>100){
			throw new FieldTooLongException("Feild59 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 汇率信息
	 * 位图位置: 59
	 * 格式：变长，LLLVAR 
	 * 类型：ANS..28，最大28个字节的ASCII 
	 * 描述: 汇率信息域
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild59(Object o) throws FieldTooLongException{
		String acount = (String)o;
		//logger.debug("汇率信息："+acount);
		if(acount!=null&&acount.length()>28){
			throw new FieldTooLongException("Feild59 to long");
		}
		byte[] temp = floatLengthstr2ASCII(acount,3);
		//logger.debug("结果："+temp.length+":"+TypeConversion.byte2hex(temp));
		return temp;
	}
	
	/**
	 * 自定义域(Reserved Private)
	 * 位图位置:60
	 * 格式: N...019(LLLVAR)，3个字节的长度值＋最大19个字节的数字字符域。
	 * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大10个字节的数据
	 * 描述: 本规范中此域用于存放要求POS上送的数据
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild60(Object o) throws FieldTooLongException{
		//自定义域由TreeMap对象传递
		if(o!=null&&o instanceof TreeMap){
			TreeMap<Integer,DataSelfFieldLeaf> data = (TreeMap<Integer,DataSelfFieldLeaf>)o;
			Iterator<Integer> it = data.keySet().iterator();
			StringBuffer temp = new StringBuffer();
			while(it.hasNext()){
				try {
					int key = it.next();
					DataSelfFieldLeaf leaf = data.get(key);
					temp.append(leaf.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			//返回数据，计算总长度，长度用3位BCD码表示
			//logger.debug("订单号："+acount);
			if(temp!=null&&temp.length()>19){
				throw new FieldTooLongException("Feild60 to long");
			}
			byte[] returnData = floatLengthStr2cbcd(temp.toString(),String.valueOf(temp.length()), false,3);
			return returnData;
			
		}else{
			return null;
		}
	}
	
	/**
	 * 原始信息域(Original Message)
	 * N...029(LLLVAR)，3个字节的长度值＋最大29个字节的数字字符域，
	 * 压缩时用右靠BCD码表示的2个字节的长度值＋用左靠BCD码表示的最大15个字节的数据。
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild61(Object o){
		//自定义域由TreeMap对象传递
		if(o!=null&&o instanceof TreeMap){
			TreeMap<Integer,DataSelfFieldLeaf> data = (TreeMap<Integer,DataSelfFieldLeaf>)o;
			byte[] feild = new byte[30];
			int index = 0;
			Iterator<Integer> it = data.keySet().iterator();
			byte[] temp = null;
			while(it.hasNext()){
				
				try {
					int key = it.next();
					DataSelfFieldLeaf leaf = data.get(key);
					temp = TypeConversion.stringToAscii(leaf.getValue());
					if(key==1){
						insertFeild(feild,temp,0,6);
						index += 6;
					}else if(key==2){
						insertFeild(feild,temp,6,3);
						index += 3;
					}else if(key==3){
						insertFeild(feild,temp,9,6);
						index += 6;
					}else if(key==4){
						insertFeild(feild,temp,15,2);
						index += 2;
					}else if(key==5){
						insertFeild(feild,temp,17,10);
						index += 10;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			//返回数据，计算总长度，长度用3位BCD码表示
			byte[] returnData = new byte[index+2];
			System.arraycopy(fixLengthStr2cbcd(String.valueOf(index), 3, true), 0, returnData, 0, 2);//长度用3位BCD码表示
			System.arraycopy(feild, 0, returnData, 2, index);
			return returnData;
			
		}else{
			return null;
		}
	}
	
	/**
	 * 把数组params的指定长度length复制到数组source，不足的在前面补0
	 * @param source
	 * @param params
	 * @param length
	 */
	public void insertFeild(byte[] source,byte[] params,int startIndex,int length){
		try{
			if(params.length<length){//补足6位，不足的在前面补“0”
				byte[] blanck = new byte[length-params.length];
				for(int i=0;i<length-params.length;i++){
					System.arraycopy(TypeConversion.stringToAscii("0"), 0, source, startIndex+i, 1);
				}
				System.arraycopy(params, 0, source, startIndex+length-params.length, params.length);
				
			}else{
				System.arraycopy(params, 0, source, startIndex, params.length>length?length:params.length);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 自定义域
	 * 位图位置: 62
	 * 格式: ANS...512(LLLVAR)，3个字节的长度值＋最大512个字节的数据域。
	 * 压缩时用右靠BCD码表示的2个字节的长度值＋用ASCII码表示的最大512个字节的数据。
	 * 描述: 原始信息域
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */		
	public byte[] parseFeild62(Object o) throws FieldTooLongException{
		//返回数据，计算总长度，长度用3位BCD码表示
		byte[] temp = (byte[])o;
		byte[] returnData = new byte[temp.length+2];
		System.arraycopy(fixLengthStr2cbcd(String.valueOf(temp.length), 3, true), 0, returnData, 0, 2);//长度用3位BCD码表示
		System.arraycopy(temp, 0, returnData, 2, temp.length);
		return returnData;
	}
	
	/**
	 * 自定义域
	 * 格式: 3位变长
	 * 类型: ANS30 30个字节的ASCII
	 * 描述: 自定义域
	 * 此自定域在本规范中用于在 POS 结帐报文，用于定义 POS 本批交易的总计数据。
	 * 正向交易笔数(3位) + 正向交易总金额(12位) + 逆向总笔数(3位) + 逆向交易总金额(12位) 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild63(Object o) throws FieldTooLongException{
		//自定义域由TreeMap对象传递
		if(o!=null&&o instanceof TreeMap){
			TreeMap<Integer,DataSelfFieldLeaf> data = (TreeMap<Integer,DataSelfFieldLeaf>)o;
			byte[] feild = new byte[30];
			//System.arraycopy(fixLengthStr2cbcd("30", 2, true), 0, feild, 0, 1);//长度用3位BCD码表示
			int index = 0;
			Iterator<Integer> it = data.keySet().iterator();
			byte[] temp = null;
			while(it.hasNext()){
				try {
					int key = it.next();
					DataSelfFieldLeaf leaf = data.get(key);
					
					temp = TypeConversion.stringToAscii(leaf.getValue());
					if(key==1){
						//insertFeild(feild,temp,0,3);
						feild=Utils.insertEnoughLengthBuffer(feild, index, temp, 0, 3, 10);
						
						index += 3;
					}else if(key==2){
						//insertFeild(feild,temp,3,12);
						feild = Utils.insertEnoughLengthBuffer(feild, index, temp, 0, 12, 10);
						index += 12;
					}else if(key==3){
						//insertFeild(feild,temp,15,3);
						feild = Utils.insertEnoughLengthBuffer(feild, index, temp, 0, 3, 10);
						index += 3;
					}else if(key==4){
						//insertFeild(feild,temp,18,12);
						feild = Utils.insertEnoughLengthBuffer(feild, index, temp, 0, 12, 10);
						index += 12;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			byte[] result = new byte[2+index];
			System.arraycopy(fixLengthStr2cbcd(String.valueOf(index), 3, true), 0, result, 0,2);
			System.arraycopy(feild, 0, result, 2, index);
			return result;
			
		}else{
			return null;
		}
	}
	
	/**
	 * 由外部传入
	 * 消息鉴定码
	 * 位图位置: 64
	 * 格式 定长
	 * 类型: 
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public byte[] parseFeild64(Object o) throws FieldTooLongException{
		//logger.debug("parseFeild64:"+TypeConversion.byte2hex((byte[])o));
		if(o==null){
			return null;
		}
		return (byte[])o;
	}
	
	

	
	@Override
	public byte[] getMabSource() {
		// TODO Auto-generated method stub
		if(!mDataMap.containsKey(64)){//计算mac时保证位图第64位为1
			mDataMap.put(64, null);
		}
		String temp = TypeConversion.byte2hex(this.packaged());
		//logger.info("packaged：" + temp);
		temp = temp.substring((this.mMessageType.getMessageTypeLength()-2)*2);
		if(mDataMap.get(64)!=null)
			temp = temp.substring(0,temp.length()-16);
		return TypeConversion.hexStringToByte(temp);
	}



	
	
	public byte[] result(byte[] source){
		if(source.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(source.length/8+1)*8];
			System.arraycopy(source, 0, temp, 0, source.length);
			source = temp;
		}
		int blockNum = source.length % 8>0?(source.length/8+1):source.length/8;
		
		byte[] result = new byte[8];
		System.arraycopy(source, 0, result, 0,8);
		
		byte[] checkBlock = new byte[8];
		
		
		for (int i = 1; i < blockNum; i++) {
			System.arraycopy(source, i * 8, checkBlock, 0, 8);
			for (int j = 0; j < 8; j++) {
				result[j] = (byte) (result[j] ^ checkBlock[j]);
			}
		}

		logger.debug("MAC:"+TypeConversion.byte2hex(result));
		return result;
	}
	
	public static void main(String[] args){
		try{
			System.out.println(TypeConversion.stringToAscii("0A")[1]);
		}catch(Exception e){
			
		}
		
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		
		ArrayList<DataSelfFieldLeaf> tlvData = new ArrayList<DataSelfFieldLeaf>();
		DataSelfFieldLeaf leaf = new DataSelfFieldLeaf();
		leaf.setTag("0001 ");
		leaf.setMaxLength(12);
		leaf.setValue("hello world+");
		tlvData.add(leaf);
		leaf = new DataSelfFieldLeaf();
		leaf.setTag("0002");
		leaf.setMaxLength(6);
		leaf.setValue("hello+");
		tlvData.add(leaf);
		
		sendMap.put(46,tlvData);
		
		
		sendMap.put(2, new String("123456"));
		//sendMap.put(3, new String("1234"));
		sendMap.put(13,new String("0905"));
		
		
		TreeMap<Integer,DataSelfFieldLeaf> data1 = new TreeMap<Integer,DataSelfFieldLeaf>();
		leaf = new DataSelfFieldLeaf();
		leaf.setTag("1");
		leaf.setValue("12345111");
		data1.put(1,leaf);
		
		leaf = new DataSelfFieldLeaf();
		leaf.setTag("2");
		leaf.setValue("12");
		data1.put(2,leaf);
		
		sendMap.put(63,data1);
		
		DataMessageTypeAllinpay messageType = DataMessageTypeAllinpay.getInstance();
		DataInPackageAllinpay jposPackage99Bill = new DataInPackageAllinpay(sendMap,messageType);
		byte[] data = jposPackage99Bill.packaged();
		try{
			DataUnPackageAllinpay jposUnPackage99Bill = new DataUnPackageAllinpay(data);
			jposUnPackage99Bill.unPacketed();
			List<DataSelfFieldLeaf> data46 = (List<DataSelfFieldLeaf>)jposUnPackage99Bill.getDataMap().get(46);
			System.out.println(data46.get(0).getTag()+":"+data46.get(0).getMaxLength()+":"+data46.get(0).getValue());
			System.out.println(data46.get(1).getTag()+":"+data46.get(1).getMaxLength()+":"+data46.get(1).getValue());
			
			TreeMap<Integer ,DataSelfFieldLeaf> data61 = (TreeMap<Integer ,DataSelfFieldLeaf>)jposUnPackage99Bill.getDataMap().get(63);
//			System.out.println(data61.get(5).getTag()+":"+data61.get(5).getMaxLength()+":"+data61.get(5).getValue());
			System.out.println(data61.get(4).getTag()+":"+data61.get(4).getMaxLength()+":"+data61.get(4).getValue());

			System.out.println(data61.get(3).getTag()+":"+data61.get(3).getMaxLength()+":"+data61.get(3).getValue());
			System.out.println(data61.get(2).getTag()+":"+data61.get(2).getMaxLength()+":"+data61.get(2).getValue());
			System.out.println(data61.get(1).getTag()+":"+data61.get(1).getMaxLength()+":"+data61.get(1).getValue());
				
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
	}


}