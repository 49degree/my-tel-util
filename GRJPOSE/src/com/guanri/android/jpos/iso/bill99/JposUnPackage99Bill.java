package com.guanri.android.jpos.iso.bill99;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.guanri.android.exception.FieldIsNullException;
import com.guanri.android.exception.FieldTooLongException;
import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.network.test.ReadFile;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 银联解包类
 * @author Administrator
 *
 */

public class JposUnPackage99Bill extends JposUnPackageFather{

	static Logger logger = Logger.getLogger(JposUnPackage99Bill.class);
	
	public static void main(String[] args){
		
		byte[] sendData = ReadFile.read("E:\\yang_workgroup\\workgroup\\TestJavaProject\\src\\Req.bin");
		logger.debug("请求数据:"+TypeConversion.byte2hex(sendData));
		try{
			JposUnPackage99Bill jposUnPackage99Bill = new JposUnPackage99Bill(sendData);
			jposUnPackage99Bill.unPacketed();
		}catch(PacketException e){
			e.printStackTrace();
		}
		
		

	}
	
	JposMessageType99Bill jposMessageType99Bill = new JposMessageType99Bill();
	public final static String PARSE_METHOD = "parseFeild";
	
	public JposUnPackage99Bill(byte[] data) throws PacketException{
		super(data);
	}
	
	
	/**
	 * 解析协议头部信息
	 */
	@Override
	protected void creatMessageType(){
		mMessageType = new JposMessageType99Bill();
	}
	/**
	 * 解析协议头部信息
	 */
	@Override
	protected void parseMessageType(){
		mMessageType.setPageLength(TypeConversion.bytesToShortEx(data,0));
		index += 2;
		((JposMessageType99Bill)mMessageType).setId(data[2]);
		index ++;
		((JposMessageType99Bill)mMessageType).setServerAddress(TypeConversion.bcd2string(data, index, 2));
		index += 2;
		((JposMessageType99Bill)mMessageType).setAddress(TypeConversion.bcd2string(data, index, 2));
		index += 2;
		((JposMessageType99Bill)mMessageType).setPagever(TypeConversion.bcd2string(data, index, 2));
		index += 2;
		((JposMessageType99Bill)mMessageType).setMessageType(TypeConversion.bcd2string(data, index, 2));
		index += 2;
	}
	
	
	/**
	 * 子类根据定义的解析位数据的方法名构造方法名称，调用父类中的解析方法
	 * 方法名最好与位标志有规律
	 * 例如方法名为parseFeild1，parseFeild2，parseFeild3，1、2、3分别表示位代码
	 * 
	 */
	@Override
	protected Object parseBitValue(int position){ 
		String methodName = PARSE_METHOD+position;//解析相应位数据的方法名称
		return parseBitValue(position,methodName);
	}
	
	/**
	 * 	 * 第2位数据
	 * 12.3.3  主账号     
	 * 位图位置：02  
	 * 格式：变长,LLVAR  
	 * 类型：N..19，压缩时用右靠BCD码表示最大10个字节的变长域。 
	 * 描述：主账号信息域 
	 * 唯一的确认一个用户交易的基本帐号。
	 * @param index
	 * @return
	 * @throws FieldTooLongException
	 * @throws FieldIsNullException
	 */
	public Object parseFeild2() throws FieldTooLongException, FieldIsNullException{
		String result = floatbytetoint(2);
		return (Object)result;
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
	public Object parseFeild3() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(6);
			System.out.println("解析第三域 :"+result);
			if((result!=null) && (result.length()>2)){
				mMessageType.setTransactionCode(result.substring(0, 2));
				System.out.println("解析第三域 :"+mMessageType.getTransactionCode());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild4() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(12);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild11() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(6);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild12() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(6);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild13() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(4);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild14() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(4);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild19() throws FieldTooLongException, FieldIsNullException{
		
		String result = floatASCIItoStr(2);
		return (Object)result;
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
	public Object parseFeild20() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(20);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild22() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(3);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild23() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(3);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild24() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(3);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild25() throws FieldTooLongException, FieldIsNullException{
		String result = null;
		try {
			result = fixBcdToInt(2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Object)result;
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
	public Object parseFeild34() throws FieldTooLongException, FieldIsNullException{
		
		String result = floatbytetoint(2);
		return (Object)result;
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
	public Object parseFeild35() throws FieldTooLongException, FieldIsNullException{
		
		String result = floatbytetoint(2);
		return (Object)result;
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
	public Object parseFeild36() throws FieldTooLongException, FieldIsNullException{
		
		String result = floatbytetoint(3);
		return (Object)result;
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
	public Object parseFeild37() throws FieldTooLongException, FieldIsNullException{
		
		String result;
		try {
			result = asciiToString(12);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild38() throws FieldTooLongException, FieldIsNullException{

		String result;
		try {
			result = asciiToString(6);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild39() throws FieldTooLongException, FieldIsNullException{

		String result;
		try {
			result = asciiToString(2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild41() throws FieldTooLongException, FieldIsNullException{

		String result;
		try {
			result = asciiToString(8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
	}
	
	/**
	 * 商户号
	 * 位图位置:42
	 * 格式:定长
	 * 类型: ANS15,15个字段的定长ASII
	 * 描述: 在本地和网络中定义交易单位(商户)的编码
	 */
	public Object parseFeild42() throws FieldTooLongException, FieldIsNullException{

		String result;
		try {
			result = asciiToString(15);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild44() throws FieldTooLongException, FieldIsNullException{

		String result = floatASCIItoStr(2);
		return (Object)result;
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
	public Object parseFeild46() throws FieldTooLongException, FieldIsNullException{
		//TLV数据格式为  ID(2字节)+ LAN长度(2字节)+DATA数据
		ArrayList<JposSelfFieldLeaf> tlvData = new ArrayList<JposSelfFieldLeaf>();
		int tlvLength = Integer.parseInt(TypeConversion.bcd2string(data,index,2));
		index +=2;
		
		byte[] tlvBuffer = new byte[tlvLength];
		System.arraycopy(data, index, tlvBuffer, 0, tlvLength);
		index +=tlvLength;
		int tvfIndex = 0;
		while(tvfIndex<tlvLength-4){
			try{
				JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
				leaf.setTag(TypeConversion.byte2hex(tlvBuffer, tvfIndex, 2));
				tvfIndex +=2;
				leaf.setMaxLength(TypeConversion.bytesToShortEx(tlvBuffer, tvfIndex));
				tvfIndex +=2;
				leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,leaf.getMaxLength()));
				tvfIndex +=leaf.getMaxLength();
				tlvData.add(leaf);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return tlvData;
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
	public Object parseFeild48() throws FieldTooLongException, FieldIsNullException{

		String result = floatASCIItoStr(3);
		return (Object)result;
	}
	
	/**
	 * **********************************************************************************
	 * 有异议
	 * **********************************************************************************
	 * 货币代码
	 * 格式: 定长
	 * 位图位置 49
	 * 类型: AN3,压缩时用右靠BCD吗表示的2个字节的定长域.
	 * 描述: 按ISO4217 定义的交易货币代码,用来表示"交易金额" 所用的货币种类
	 * @param o
	 * @return
	 * @throws FieldTooLongException
	 */
	public Object parseFeild49() throws FieldTooLongException, FieldIsNullException{

		String result;
		try {
			result = fixBcdToInt(3);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild52() throws FieldTooLongException, FieldIsNullException{
		
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
	public Object parseFeild53() throws FieldTooLongException, FieldIsNullException{
		String result;
		try {
			result = asciiToString(15);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (Object)result;
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
	public Object parseFeild54() throws FieldTooLongException, FieldIsNullException{
		//String result = floatASCIItoStr(3);
		TreeMap<String,AdditionalAmounts> amountData = new TreeMap<String,AdditionalAmounts>();
		int amountLength = Integer.parseInt(TypeConversion.bcd2string(data,index,2));//计算数据长度
		index +=2;
		if(amountLength>0){
			byte[] amountBuffer = new byte[amountLength];
			System.arraycopy(data, index, amountBuffer, 0, amountLength);//获取数据
			index +=amountLength;
			int amountNum = amountLength/20;
			AdditionalAmounts amount = null;
			for(int i=0;i<amountNum;i++){
				amount = new AdditionalAmounts();
				try{
					amount.setCode(TypeConversion.asciiToString(amountBuffer,i*20,2));
					amount.setAmountType(TypeConversion.asciiToString(amountBuffer,i*20+2,2));
					amount.setHuobiCode(TypeConversion.asciiToString(amountBuffer,i*20+4,3));
					amount.setBanlanceType(TypeConversion.asciiToString(amountBuffer,i*20+7,1));
					amount.setAmount(TypeConversion.asciiToString(amountBuffer,i*20+8,12));
					amountData.put(amount.getAmountType(), amount);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			
			
		}
		return amountData;
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
	public Object parseFeild55() throws FieldTooLongException, FieldIsNullException{
		String result = floatASCIItoStr(3);
		return (Object)result;
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
	public Object parseFeild56() throws FieldTooLongException, FieldIsNullException{
		String result = floatASCIItoStr(3);
		return (Object)result;
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
	public Object parseFeild57() throws FieldTooLongException, FieldIsNullException{
		String result = floatASCIItoStr(3);
		return (Object)result;
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
	public Object parseFeild59() throws FieldTooLongException, FieldIsNullException{
		String result = floatbytetoint(3);
		return (Object)result;
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
	public Object parseFeild60() throws FieldTooLongException, FieldIsNullException{
		String result = floatbytetoint(3);
		return (Object)result;
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
	public Object parseFeild61() throws FieldTooLongException, FieldIsNullException{
		//获取该域的全部数据
		TreeMap<Integer,JposSelfFieldLeaf> tlvData = new TreeMap<Integer,JposSelfFieldLeaf>();
		int tlvLength = Integer.parseInt(TypeConversion.bcd2string(data,index,2));//计算数据长度
		index +=2;
		byte[] tlvBuffer = new byte[tlvLength];
		System.arraycopy(data, index, tlvBuffer, 0, tlvLength);//获取数据
		index +=tlvLength;
		//解析数据
		int tvfIndex = 0;
		int dataIndex = 0;
		while(tvfIndex<tlvLength-2){
			try{
				dataIndex++;
				JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
				if(dataIndex==1){
					leaf.setTag("1");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,6));
					tvfIndex += 6;
				}else if(dataIndex==2){
					leaf.setTag("2");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,3));
					tvfIndex += 3;
				}else if(dataIndex==3){
					leaf.setTag("3");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,6));
					tvfIndex += 6;
				}else if(dataIndex==4){
					leaf.setTag("4");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,2));
					tvfIndex += 2;
				}else if(dataIndex==5){
					leaf.setTag("5");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,10));
					tvfIndex += 10;
				}
				tlvData.put(dataIndex, leaf);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return tlvData;
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
	public Object parseFeild62() throws FieldTooLongException, FieldIsNullException{
		//获取该域的全部数据
		TreeMap<Integer,JposSelfFieldLeaf> tlvData = new TreeMap<Integer,JposSelfFieldLeaf>();
		int tlvLength = Integer.parseInt(TypeConversion.bcd2string(data,index,1));//计算数据长度
		index +=1;
		byte[] tlvBuffer = new byte[tlvLength];
		System.arraycopy(data, index, tlvBuffer, 0, tlvLength);//获取数据
		index +=tlvLength;
		//解析数据
		int tvfIndex = 0;
		int dataIndex = 0;
		while(tvfIndex<tlvLength-2){
			try{
				dataIndex++;
				JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
				if(dataIndex==1){
					leaf.setTag("1");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,4));
					tvfIndex += 4;
				}else if(dataIndex==2){
					leaf.setTag("2");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,6));
					tvfIndex += 6;
				}else if(dataIndex==3){
					leaf.setTag("3");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,10));
					tvfIndex += 10;
				}
				tlvData.put(dataIndex, leaf);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return tlvData;
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
	public Object parseFeild63() throws FieldTooLongException, FieldIsNullException{
		//获取该域的全部数据
		TreeMap<Integer,JposSelfFieldLeaf> tlvData = new TreeMap<Integer,JposSelfFieldLeaf>();
		int tlvLength = Integer.parseInt(TypeConversion.bcd2string(data,index,1));//计算数据长度
		index +=1;
		byte[] tlvBuffer = new byte[tlvLength];
		System.arraycopy(data, index, tlvBuffer, 0, tlvLength);//获取数据
		index +=tlvLength;
		//解析数据
		int tvfIndex = 0;
		int dataIndex = 0;
		while(tvfIndex<tlvLength-2){
			try{
				dataIndex++;
				JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
				if(dataIndex==1){
					leaf.setTag("1");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,3));
					tvfIndex += 3;
				}else if(dataIndex==2){
					leaf.setTag("2");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,12));
					tvfIndex += 12;
				}else if(dataIndex==3){
					leaf.setTag("3");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,3));
					tvfIndex += 3;
				}else if(dataIndex==4){
					leaf.setTag("4");
					leaf.setValue(TypeConversion.asciiToString(tlvBuffer,tvfIndex,12));
					tvfIndex += 12;
				}
				tlvData.put(dataIndex, leaf);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return tlvData;
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
	public Object parseFeild64() throws FieldTooLongException, FieldIsNullException{
		String mac = TypeConversion.byte2hex(data, index, 8);
		return mac;
	}
	

	/**
	 * 解TLV数据
	 * 目前只处理 24 25 26 字段
	 * TLV数据格式为  ID(2字节)+ LAN长度(2字节)+DATA数据
	 * @param data
	 * @return
	 */
	public TLVType parseTLV(byte[] data){
		TLVType tlvType = new TLVType();
		int i = 0;
		while (i<data.length){
			byte[] idbyte = new byte[2];
			System.arraycopy(idbyte, 0,data , i, 2);
			short id = TypeConversion.bytesToShort(idbyte,0);
			i = i + 2;
			byte[] lenbyte = new byte[2];
			System.arraycopy(idbyte, 0,data , i, 2);
			short len = TypeConversion.bytesToShort(lenbyte,0);
			i = i + 2;
			byte[] databyte = new byte[len];
			System.arraycopy(databyte, 0,data , i, len);
			i = i + len;
			switch (id) {
			case 24:
				String merchant_name = TypeConversion.bcd2string(databyte);
				tlvType.setMerchant_name(merchant_name);
				break;
			case 25:
				String tel1 = TypeConversion.bcd2string(databyte);
				tlvType.setTel1(tel1);
				break;
			case 26:
				String tel2 = TypeConversion.bcd2string(databyte);
				tlvType.setTel2(tel2);
				break;
			default:
				break;
			}
		}
		return null;
		
	}	
}
