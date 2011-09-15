package com.guanri.android.jpos.constant;

import java.util.HashMap;

public class JposConstant {
	/**
	 * 数据编码类型
	 * JPOS_BCD BCD编码
	 * JPOS_ASCII ASCII编码
	 * @author Administrator
	 *
	 */
	public enum DataEncodeType{
		JPOS_BCD,JPOS_ASCII
	}
	

	//POS终端交易码定义
	public final static int POS_TRANCE_CODE_QUERY_BALANCE = 100;//余额查询
	public final static int POS_TRANCE_CODE_PAY = 200;//消费
	public final static int POS_TRANCE_CODE_BACK_ORDER = 300;//退货
	public final static int POS_TRANCE_CODE_LOGIN = 1; // 签到
	public final static int POS_TRANCE_CODE_PAYCANEL = 400; // 撤销
	
	public final static int POS_TRANCE_CODE_QUERY_ORDER = 600; // 订单查询
	
	public final static int POS_TRANCE_CODE_QUERY_SALE = 601; // 查询后付费
	
	public final static int POS_TRANCE_CODE_AUTO_RECEIPT = 7; // 自动回执
	
	public final static int POS_TRANCE_CODE_MAN_RECEIPT = 8; // 手动回执
	
	public final static HashMap<Integer,String> POS_BILL99_TRANCE_CODE = new HashMap<Integer,String>();//POS终端，快钱交易码对照表
	public final static HashMap<String,String> BILL99_RESULT_TYPE_CODE = new HashMap<String,String>();//快钱交易结果对照表
	
	static{
		//POS终端交易码对照表
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_QUERY_BALANCE, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_PAY, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_BACK_ORDER, "");
		//快钱交易结果对照表
		BILL99_RESULT_TYPE_CODE.put("00", "交易成功");
		BILL99_RESULT_TYPE_CODE.put("01", "请联系发卡行,或核对卡信息后重复输入");
		BILL99_RESULT_TYPE_CODE.put("02", "请联系快钱公司");
		BILL99_RESULT_TYPE_CODE.put("03", "无效商户");
		BILL99_RESULT_TYPE_CODE.put("04", "无效终端");
		BILL99_RESULT_TYPE_CODE.put("05", "不予承兑,或核对卡信息后重新输入");
		BILL99_RESULT_TYPE_CODE.put("06", "出错");
		BILL99_RESULT_TYPE_CODE.put("07", "特定条件下没收卡 ");
		BILL99_RESULT_TYPE_CODE.put("09", "请求正在处理中");
		BILL99_RESULT_TYPE_CODE.put("12", "无效交易，或核对卡信息后重新输入 ");
		BILL99_RESULT_TYPE_CODE.put("13", "无效金额，交易金额不在许可的范围内，疑问请联系快钱公司 ");
		BILL99_RESULT_TYPE_CODE.put("14", "无效卡号（无此号），或核对卡信息后重新输入 ");
		BILL99_RESULT_TYPE_CODE.put("15", "无此发卡方");
		BILL99_RESULT_TYPE_CODE.put("17", "客户取消");
		BILL99_RESULT_TYPE_CODE.put("18", "商户保证金对应的可交易额度不足，请联系快钱公司 ");
		BILL99_RESULT_TYPE_CODE.put("19", "您的快钱账户余额不足以进行退货，请充值到快钱账户后，再进行退货操作 ");
		BILL99_RESULT_TYPE_CODE.put("20", "无效响应");
		BILL99_RESULT_TYPE_CODE.put("21", "不能采取行动 ");
		BILL99_RESULT_TYPE_CODE.put("22", "故障怀疑 ");
		BILL99_RESULT_TYPE_CODE.put("23", "不可接受的交易费 ");
		BILL99_RESULT_TYPE_CODE.put("25", "找不到原始交易 ");
		BILL99_RESULT_TYPE_CODE.put("30", "格式错误 ");
		BILL99_RESULT_TYPE_CODE.put("31", "不支持该发卡银行 ");
		BILL99_RESULT_TYPE_CODE.put("32", "商户不受理的卡 ");
		BILL99_RESULT_TYPE_CODE.put("33", "过期的卡 ");
		BILL99_RESULT_TYPE_CODE.put("34", "有作弊嫌疑 ");
		BILL99_RESULT_TYPE_CODE.put("35", "请联系快钱公司 ");
		BILL99_RESULT_TYPE_CODE.put("36", "受限制的卡 ");
		BILL99_RESULT_TYPE_CODE.put("37", "风险卡，请联系快钱公司 ");
		BILL99_RESULT_TYPE_CODE.put("38", "超过允许的试输入 ");
		BILL99_RESULT_TYPE_CODE.put("39", "无贷记账户 ");
		BILL99_RESULT_TYPE_CODE.put("40", "请求的功能尚不支持 ");
		BILL99_RESULT_TYPE_CODE.put("41", "挂失卡 ");
		BILL99_RESULT_TYPE_CODE.put("42", "无此账户 ");
		BILL99_RESULT_TYPE_CODE.put("43", "被窃卡"); 
		BILL99_RESULT_TYPE_CODE.put("44", "无此投资账户"); 
		BILL99_RESULT_TYPE_CODE.put("51", "资金不足"); 
		BILL99_RESULT_TYPE_CODE.put("52", "无此支票账户"); 
		BILL99_RESULT_TYPE_CODE.put("53", "无此储蓄卡账户"); 
		BILL99_RESULT_TYPE_CODE.put("54", "过期的卡"); 
		BILL99_RESULT_TYPE_CODE.put("55", "密码错误"); 
		BILL99_RESULT_TYPE_CODE.put("56", "无此卡记录"); 
		BILL99_RESULT_TYPE_CODE.put("57", "不允许持卡人进行的交易"); 
		BILL99_RESULT_TYPE_CODE.put("58", "不允许终端进行的交易"); 
		BILL99_RESULT_TYPE_CODE.put("59", "有作弊嫌疑");  
		BILL99_RESULT_TYPE_CODE.put("60", "请联系快钱公司"); 
		BILL99_RESULT_TYPE_CODE.put("61", "超出取款转账金额限制"); 
		BILL99_RESULT_TYPE_CODE.put("62", "受限制的卡"); 
		BILL99_RESULT_TYPE_CODE.put("63", "侵犯安全");
		BILL99_RESULT_TYPE_CODE.put("64", "原始金额错误"); 
		BILL99_RESULT_TYPE_CODE.put("65", "超出取款次数限制"); 
		BILL99_RESULT_TYPE_CODE.put("66", "请联系快钱公司"); 
		BILL99_RESULT_TYPE_CODE.put("67", "强行受理（要求在自动柜员机上没收此卡）"); 
		BILL99_RESULT_TYPE_CODE.put("68", "无法在正常时间内获得交易应答，请稍后重试"); 
		BILL99_RESULT_TYPE_CODE.put("75", "允许输入密码次数超限"); 
		BILL99_RESULT_TYPE_CODE.put("76", "无效账户"); 
		BILL99_RESULT_TYPE_CODE.put("80", "交易拒绝"); 
		BILL99_RESULT_TYPE_CODE.put("90C", "正在日终处理（系统终止一天的活动，开始第二天的活动，交易在几分钟后可再次发送）"); 
		BILL99_RESULT_TYPE_CODE.put("91", "发卡方或交换中心不能操作");
		BILL99_RESULT_TYPE_CODE.put("92", "网络暂时无法达到，请稍后重试"); 
		BILL99_RESULT_TYPE_CODE.put("93", "交易违法、不能完成");
		BILL99_RESULT_TYPE_CODE.put("94", "重复交易");
		BILL99_RESULT_TYPE_CODE.put("95", "核对差错");
		BILL99_RESULT_TYPE_CODE.put("96", "系统异常、失效"); 
		BILL99_RESULT_TYPE_CODE.put("97", "ATM/POS 终端号找不到"); 
		BILL99_RESULT_TYPE_CODE.put("98", "交换中心收不到发卡方应答"); 
		BILL99_RESULT_TYPE_CODE.put("99", "密码格式错"); 
		BILL99_RESULT_TYPE_CODE.put("C0", "正在处理中"); 
		BILL99_RESULT_TYPE_CODE.put("I0", "外部交易跟踪编号（如：商户订单号）发生重复"); 
		BILL99_RESULT_TYPE_CODE.put("I1", "请提供正确的持卡人姓名，必须与申请信用卡时的姓名一致"); 
		BILL99_RESULT_TYPE_CODE.put("I2", "I2"); 
		BILL99_RESULT_TYPE_CODE.put("22", "请提供正确的验证码（CVV2），验证码在卡背面签名栏后的三位数字串"); 
		BILL99_RESULT_TYPE_CODE.put("I3", "请提供正确的证件号码，必须与申请信用卡时的证件号码一致"); 
		BILL99_RESULT_TYPE_CODE.put("I4", "请提供正确的卡有效期，卡有效期是在卡号下面的4位数字"); 
		BILL99_RESULT_TYPE_CODE.put("I5", "超出持卡人设置的交易限额，请持卡人联系发卡银行调高限额"); 
		BILL99_RESULT_TYPE_CODE.put("I6", "无效证件类型");
		BILL99_RESULT_TYPE_CODE.put("I8", "不支持的报价币种"); 
		BILL99_RESULT_TYPE_CODE.put("I9", "银行结帐中，请重试交易"); 
		BILL99_RESULT_TYPE_CODE.put("AA", "中信银行内部系统错误"); 
		BILL99_RESULT_TYPE_CODE.put("N2", "流水号重复");
		
	}
	
	
	
	/**
	 * 块钱定义功能类型
	 * @author Administrator
	 *
	 */
	public interface MessageTypeDefine99Bill{
		public final static String REQUEST_POS_CHECK_IN = "0800";// 签到
		public final static String RESPONSE_POS_CHECK_IN = "0810";// 签到应答
		
		public final static String REQUEST_POS_CHECK_OUT = "0800";//签退？？？？？？？？？？？
		public final static String REQUEST_POS_CHECK_DATA = "0810";//批结算？？？？？？？？？
		
		public final static String REQUEST_POS_CHECK_DATA_DETAIL = "0200";//批上送？？？？？？？？？
		public final static String RESPONSE_POS_CHECK_DATA_DETAIL = "0210";//批上送？？？？？？？？？
		
		
		public final static String REQUEST_OP_PAY_MONEY = "0200";//消费请求
		public final static String RESPONSE_OP_PAY_MONEY = "0210";//消费应答
		
		public final static String REQUEST_OP_PAY_CANCEL = "0200";//消费撤销
		public final static String RESPONSE_OP_PAY_CANCEL = "0210";//消费撤销应答
		
		public final static String REQUEST_OP_OPERATE_CANCEL = "0400";//冲正
		public final static String RESPONSE_OP_OPERATE_CANCEL = "0410";//冲正应答
		
		public final static String REQUEST_OP_GOODS_BACKUP = "0220";//退货
		public final static String RESPONSE_OP_GOODS_BACKUP = "0230";//退货应答
		
		public final static String REQUEST_OP_QUERY_MONEY = "0200"; // 余额查询
		public final static String RESPONSE_OP_QUERY_MONEY = "0210"; // 余额查询应答
		
		public final static String REQUEST_OP_QUERY_INSURANCE = "0200"; // OQS查询
		public final static String RESPONSE_OP_QUERY_INSURANCE = "0210"; // OQS查询应答


		public final static String REQUEST_POS_RECEIPTSALE = "0800";// 交易回执
		public final static String RESPONSE_POS_RECEIPTSALE = "0810";// 回执应答
		
		// 域41 终端代码
		public final static String POSID = "20100601";
		// 域42 商户代码
		public final static String CONTACT =  "104110045110012";
		// 人民币代码
		public final static String RMBCODE = "156";
		
		// 网络信息代码
		public final static String NETMSGCODE = "001";
	}
	
	/**
	 * 银联定义功能类型
	 * @author Administrator
	 *
	 */
	public interface MessageTypeDefineUnionpay{
		public final static String REQUEST_POS_CHECK_IN = "800";// 签到
		public final static String RESPONSE_POS_CHECK_IN = "810";// 签到应答
		
		public final static String REQUEST_POS_CHECK_OUT = "800";//签退？？？？？？？？？？？
		public final static String REQUEST_POS_CHECK_DATA = "810";//批结算？？？？？？？？？
		
		public final static String REQUEST_POS_CHECK_DATA_DETAIL = "200";//批上送？？？？？？？？？
		public final static String RESPONSE_POS_CHECK_DATA_DETAIL = "210";//批上送？？？？？？？？？
		
		
		public final static String REQUEST_OP_PAY_MONEY = "200";//消费请求
		public final static String RESPONSE_OP_PAY_MONEY = "210";//消费应答
		
		public final static String REQUEST_OP_PAY_CANCEL = "200";//消费撤销
		public final static String RESPONSE_OP_PAY_CANCEL = "210";//消费撤销应答
		
		public final static String REQUEST_OP_OPERATE_CANCEL = "400";//冲正
		public final static String RESPONSE_OP_OPERATE_CANCEL = "410";//冲正应答
		
		public final static String REQUEST_OP_GOODS_BACKUP = "220";//退货
		public final static String RESPONSE_OP_GOODS_BACKUP = "230";//退货应答
	}
	
	/**
	 * POS终端报文类型
	 * @author Administrator
	 *
	 */
	public interface PosMessageType{
		public final static byte DATA_TRANSFER = 0X00;// 数据传输
		public final static byte TRANSFER_MESSAGE = 0X01;// 交易报文
		public final static byte CMD_CODE_MEMO = 0X02;// 指令码说明
	}
	
	/**
	 * POS终端命令码定义
	 * @author Administrator
	 *
	 */
	public interface PosCommandCode{
		// 数据传输命令ID
		public final static byte DATA_TRANSFER_QUERY_ORDER_ID = 0x01;// 查找原交易凭证号
		public final static byte DATA_TRANSFER_VIEW_COMM_PARAMS = 0x02;// 查看通讯参数
		public final static byte DATA_TRANSFER_SET_COMM_PARAMS = 0x03;// 设置通讯参数
		public final static byte DATA_TRANSFER_SET_COMM_STATE = 0x04;// 通讯状态设置
		public final static byte DATA_TRANSFER_VIEW_COMM_STATE = 0x05;// 查询通讯状态
		public final static byte DATA_TRANSFER_SYN_TIME = 0x06;// 时间同步指令
		public final static byte DATA_TRANSFER_CREATE_MAC = 0x07;//MAC计算
		
		//指令码说明命令ID
		public final static byte CMD_CODE_MEMO_READ_IC_INFO = 0X03;// 读取磁道信息
		public final static byte CMD_CODE_MEMO_READ_KEY_CRYPT = 0X04;// 读取密码密文
		public final static byte CMD_CODE_MEMO_INPUT_BY_HAND = 0X05;// 手工输入卡号
		
		
	}
	
	

	public static String result(String resultNo){
		
		String str = "失败";
		if(resultNo.equals("00")){
			str = "响应成功";
		}
		if(resultNo.equals("01")){
			str = "响应失败,请联系发卡行,或核对卡信息后重新输入" ;
		}
		if(resultNo.equals("02")){
			str = "响应失败,请联系快钱公司";
		}
		if(resultNo.equals("03")){
			str = "无效商户";
		}
		if(resultNo.equals("04")){
			str = "无效终端";
		}
		if(resultNo.equals("05")){
			str = "无效商户";
		}
		if(resultNo.equals("N2")){
			str = "流水号重复"; 
		}
		return BILL99_RESULT_TYPE_CODE.get(resultNo);
		
	}
}
