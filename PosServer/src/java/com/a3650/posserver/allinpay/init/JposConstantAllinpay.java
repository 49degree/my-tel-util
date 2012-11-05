package com.a3650.posserver.allinpay.init;

import java.util.HashMap;



public class JposConstantAllinpay {
	/**
	 * 数据编码类型 JPOS_BCD BCD编码 JPOS_ASCII ASCII编码
	 * 
	 * @author Administrator
	 * 
	 */
	public enum DataEncodeType {
		JPOS_BCD, JPOS_ASCII
	}

	// POS终端交易码定义
	public final static int POS_TRANCE_CODE_QUERY_BALANCE = 100;// 余额查询
	public final static int POS_TRANCE_CODE_PAY = 200;// 消费
	public final static int POS_TRANCE_CODE_BACK_ORDER = 300;// 退货
	public final static int POS_TRANCE_CODE_LOGIN = 1; // 签到
	public final static int POS_TRANCE_CODE_PAYCANEL = 400; // 撤销

	public final static int POS_TRANCE_CODE_QUERY_ORDER = 600; // 订单查询

	public final static int POS_TRANCE_CODE_QUERY_SALE = 601; // 查询后付费

	public final static int POS_TRANCE_CODE_AUTO_RECEIPT = 7; // 自动回执

	public final static int POS_TRANCE_CODE_MAN_RECEIPT = 8; // 手动回执

	public final static HashMap<Integer, String> POS_BILL99_TRANCE_CODE = new HashMap<Integer, String>();// POS终端，快钱交易码对照表
	public final static HashMap<String, String> BILL99_RESULT_TYPE_CODE = new HashMap<String, String>();// 快钱交易结果对照表
	// 通联交易结果对照表
	public final static HashMap<String, String> ALLINPAY_RESULT_TYPE_CODE = new HashMap<String, String>();
	// 银行编码表
	public final static HashMap<String, String> BANK_CODE = new HashMap<String, String>();



	public static String SERVER_IP = "116.236.252.102";// 服务器IP
	public static int SERVER_PORT = 8880;// 服务器PORT

	// 初始值
	public final static String SUPER_PWD = "201109";// 超级密码
	public final static String POS_PWD_INIT_VALUE = "000000"; // 初始密码
	public final static String SERVERIP_INIT_VALUE = "116.236.252.102";// 初始服务器IP
	public final static String SERVERPORT_INIT_VALUE = "8880";// 初始服务器端口

	static {
		// POS终端交易码对照表
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_QUERY_BALANCE, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_PAY, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_BACK_ORDER, "");
		// 快钱交易结果对照表
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
		BILL99_RESULT_TYPE_CODE.put("90C",
				"正在日终处理（系统终止一天的活动，开始第二天的活动，交易在几分钟后可再次发送）");
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

		// 通联交易结果对照表
		ALLINPAY_RESULT_TYPE_CODE.put("00", "交易成功");
		ALLINPAY_RESULT_TYPE_CODE.put("01", "查发卡方");
		ALLINPAY_RESULT_TYPE_CODE.put("02", "查发卡方的特殊条件");
		ALLINPAY_RESULT_TYPE_CODE.put("03", "无效商户");
		ALLINPAY_RESULT_TYPE_CODE.put("04", "没收卡");
		ALLINPAY_RESULT_TYPE_CODE.put("05", "不予承兑");
		ALLINPAY_RESULT_TYPE_CODE.put("06", "出错");
		ALLINPAY_RESULT_TYPE_CODE.put("07", "特定条件下没收卡 ");
		ALLINPAY_RESULT_TYPE_CODE.put("09", "请求正在处理中");
		ALLINPAY_RESULT_TYPE_CODE.put("10", "部分金额批准");
		ALLINPAY_RESULT_TYPE_CODE.put("11", "重要人物批准（VIP）");
		ALLINPAY_RESULT_TYPE_CODE.put("12", "无效交易 ");
		ALLINPAY_RESULT_TYPE_CODE.put("13", "无效金额 ");
		ALLINPAY_RESULT_TYPE_CODE.put("14", "无效卡号（无此号） ");
		ALLINPAY_RESULT_TYPE_CODE.put("15", "无此发卡方");
		ALLINPAY_RESULT_TYPE_CODE.put("16", "批准更新第三磁道");
		ALLINPAY_RESULT_TYPE_CODE.put("17", "拒绝但不没收卡");
		ALLINPAY_RESULT_TYPE_CODE.put("19", "重新送入交易 ");
		ALLINPAY_RESULT_TYPE_CODE.put("20", "无效响应");
		ALLINPAY_RESULT_TYPE_CODE.put("21", "不能采取行动 ");
		ALLINPAY_RESULT_TYPE_CODE.put("22", "故障怀疑 ");
		ALLINPAY_RESULT_TYPE_CODE.put("23", "不可接受的交易费 ");
		ALLINPAY_RESULT_TYPE_CODE.put("25", "找不到原始交易 ");
		ALLINPAY_RESULT_TYPE_CODE.put("30", "格式错误 ");
		ALLINPAY_RESULT_TYPE_CODE.put("31", "银联处理中心不支持的银行 ");
		ALLINPAY_RESULT_TYPE_CODE.put("33", "过期的卡 ");
		ALLINPAY_RESULT_TYPE_CODE.put("34", "有作弊嫌疑 ");
		ALLINPAY_RESULT_TYPE_CODE.put("35", "受卡方与代理方联系（没收卡） ");
		ALLINPAY_RESULT_TYPE_CODE.put("36", "受限制的卡 ");
		ALLINPAY_RESULT_TYPE_CODE.put("37", "受卡方电话通知代理方安全部门 ");
		ALLINPAY_RESULT_TYPE_CODE.put("38", "超过允许的试输入 ");
		ALLINPAY_RESULT_TYPE_CODE.put("39", "无贷记账户 ");
		ALLINPAY_RESULT_TYPE_CODE.put("40", "请求的功能尚不支持 ");
		ALLINPAY_RESULT_TYPE_CODE.put("41", "挂失卡 ");
		ALLINPAY_RESULT_TYPE_CODE.put("42", "无此账户 ");
		ALLINPAY_RESULT_TYPE_CODE.put("43", "被窃卡");
		ALLINPAY_RESULT_TYPE_CODE.put("44", "无此投资账户");
		ALLINPAY_RESULT_TYPE_CODE.put("51", "资金不足");
		ALLINPAY_RESULT_TYPE_CODE.put("52", "无此支票账户");
		ALLINPAY_RESULT_TYPE_CODE.put("53", "无此储蓄卡账户");
		ALLINPAY_RESULT_TYPE_CODE.put("54", "过期的卡");
		ALLINPAY_RESULT_TYPE_CODE.put("55", "不正确的PIN");
		ALLINPAY_RESULT_TYPE_CODE.put("56", "无此卡记录");
		ALLINPAY_RESULT_TYPE_CODE.put("57", "不允许持卡人进行的交易");
		ALLINPAY_RESULT_TYPE_CODE.put("58", "不允许终端进行的交易");
		ALLINPAY_RESULT_TYPE_CODE.put("59", "有作弊嫌疑");
		ALLINPAY_RESULT_TYPE_CODE.put("60", "受卡方与代理方联系（不没收卡）");
		ALLINPAY_RESULT_TYPE_CODE.put("61", "超出金额限制");
		ALLINPAY_RESULT_TYPE_CODE.put("62", "受限制的卡");
		ALLINPAY_RESULT_TYPE_CODE.put("63", "侵犯安全");
		ALLINPAY_RESULT_TYPE_CODE.put("64", "原始金额错误");
		ALLINPAY_RESULT_TYPE_CODE.put("65", "超出取款次数限制");
		ALLINPAY_RESULT_TYPE_CODE.put("66", "受卡方通知受理方安全部门");
		ALLINPAY_RESULT_TYPE_CODE.put("67", "强行受理（要求在自动柜员机上没收此卡）");
		ALLINPAY_RESULT_TYPE_CODE.put("68", "接收的响应超时");
		ALLINPAY_RESULT_TYPE_CODE.put("75", "允许输入密码次数超限");
		ALLINPAY_RESULT_TYPE_CODE.put("76", "无效账户");
		ALLINPAY_RESULT_TYPE_CODE.put("90",
				"正在日终处理（系统终止一天的活动，开始第二天的活动，交易在几分钟后可再次发送）");
		ALLINPAY_RESULT_TYPE_CODE.put("91", "发卡方或交换中心不能操作");
		ALLINPAY_RESULT_TYPE_CODE.put("92", "金融机构或中间网络设施找不到或无法达到");
		ALLINPAY_RESULT_TYPE_CODE.put("93", "交易违法、不能完成");
		ALLINPAY_RESULT_TYPE_CODE.put("94", "重复交易");
		ALLINPAY_RESULT_TYPE_CODE.put("95", "核对差错");
		ALLINPAY_RESULT_TYPE_CODE.put("96", "银联处理中心系统异常、失效");
		ALLINPAY_RESULT_TYPE_CODE.put("97", "ATM/POS 终端号找不到");
		ALLINPAY_RESULT_TYPE_CODE.put("98", "银联处理中心收不到发卡方应答");
		ALLINPAY_RESULT_TYPE_CODE.put("99", "PIN 格式错");
		ALLINPAY_RESULT_TYPE_CODE.put("A0", "MAC 鉴别失败");
		ALLINPAY_RESULT_TYPE_CODE.put("A1", "转账货币不一致");
		ALLINPAY_RESULT_TYPE_CODE.put("A2",
				"银联处理中心转发了原交易请求，但未收到发卡方应答时，银联处理中心直接向受理方应答为有缺陷的成功交易");
		ALLINPAY_RESULT_TYPE_CODE.put("A3", "转入行无此账户");
		ALLINPAY_RESULT_TYPE_CODE.put("A4", "未收到原交易请求时，对关联的确认交易的承兑为有缺陷的成功交易");
		ALLINPAY_RESULT_TYPE_CODE.put("A5", "原交易为拒绝时，对关联的确认交易的承兑为有缺陷的成功交易");
		ALLINPAY_RESULT_TYPE_CODE.put("A6",
				"银联处理中心转发了原交易请求，但未收到发卡方应答时，对受理方发来的关联的确认交易的承兑为有缺陷的成功交易");
		ALLINPAY_RESULT_TYPE_CODE.put("A7", "安全处理失败");
		ALLINPAY_RESULT_TYPE_CODE.put("B1", "费用未缴或无欠费（收据未打）（公共支付业务使用）");
		ALLINPAY_RESULT_TYPE_CODE.put("C1", "受理方状态非法");

		ALLINPAY_RESULT_TYPE_CODE.put("D1", "机构代码错误");
		ALLINPAY_RESULT_TYPE_CODE.put("D2", "日期错误");
		ALLINPAY_RESULT_TYPE_CODE.put("D3", "无效的文件类型");
		ALLINPAY_RESULT_TYPE_CODE.put("D4", "已经处理过的文件");
		ALLINPAY_RESULT_TYPE_CODE.put("D5", "无此文件");
		ALLINPAY_RESULT_TYPE_CODE.put("D6", "接收者不支持");
		ALLINPAY_RESULT_TYPE_CODE.put("D7", "文件锁定");
		ALLINPAY_RESULT_TYPE_CODE.put("D8", "未成功");
		ALLINPAY_RESULT_TYPE_CODE.put("D9", "文件长度不符");
		ALLINPAY_RESULT_TYPE_CODE.put("DA", "文件解压缩错");
		ALLINPAY_RESULT_TYPE_CODE.put("DB", "文件名称错");
		ALLINPAY_RESULT_TYPE_CODE.put("DC", "无法接收文件");

		ALLINPAY_RESULT_TYPE_CODE.put("N1", "未登折帐目已超限，交易不成功");
		ALLINPAY_RESULT_TYPE_CODE.put("Y1",
				"脱机交易成功（符合PBOC 借/贷记标准的IC卡专用，具体使用方法参见技术规范 第三部分 文件接口规范）");
		ALLINPAY_RESULT_TYPE_CODE.put("Y3",
				"不能联机，脱机交易成（符合PBOC 借/贷记标准的IC 卡专用，具体使用方法参见技术规范 第三部分 文件接口规范）");
		ALLINPAY_RESULT_TYPE_CODE.put("Z1",
				"脱机交易失败（符合PBOC 借/贷记标准的IC卡专用，具体使用方法参见技术规范 第三部分 文件接口规范）");
		ALLINPAY_RESULT_TYPE_CODE.put("Z3",
				"不能联机，脱机交易失败（符合PBOC 借/贷记标准的IC 卡专用，具体使用方法参见技术规范 第三部分 文件接口规范）");

		BANK_CODE.put("01020000", "工商银行");
		BANK_CODE.put("01030000", "农业银行");
		BANK_CODE.put("01040000", "中国银行");
		BANK_CODE.put("01050000", "建设银行");
		BANK_CODE.put("01000000", "邮储银行");
		BANK_CODE.put("03010000", "交通银行");
		BANK_CODE.put("03020000", "中信银行");
		BANK_CODE.put("03030000", "光大银行");
		BANK_CODE.put("03040000", "华夏银行");
		BANK_CODE.put("03050000", "民生银行");
		BANK_CODE.put("03060000", "广发银行");
		BANK_CODE.put("03070000", "深发银行");
		BANK_CODE.put("03080000", "招商银行");
		BANK_CODE.put("03090000", "兴业银行");
		BANK_CODE.put("03100000", "浦发银行");
		BANK_CODE.put("03110000", "恒丰银行");
		BANK_CODE.put("03160000", "浙商银行");
		BANK_CODE.put("03170000", "渤海银行");
		BANK_CODE.put("03240000", "星展银行");
		BANK_CODE.put("03260000", "恒生银行");
		BANK_CODE.put("04010000", "上海银行");
		BANK_CODE.put("04030000", "北京银行");
		BANK_CODE.put("04080000", "宁波银行");
		BANK_CODE.put("04100000", "平安银行");
		BANK_CODE.put("04120000", "温州银行");
		BANK_CODE.put("04140000", "汉口银行");
		BANK_CODE.put("04170000", "盛京银行");
		BANK_CODE.put("04180000", "洛阳银行");
		BANK_CODE.put("04200000", "大连银行");
		BANK_CODE.put("04240000", "南京银行");
		BANK_CODE.put("04360000", "宁夏银行");
		BANK_CODE.put("04400000", "徽商银行");
		BANK_CODE.put("04500000", "青岛商行");
		BANK_CODE.put("04580000", "青海银行");
		BANK_CODE.put("04600000", "盐城商行");
		BANK_CODE.put("04720000", "廊坊银行");
		BANK_CODE.put("04970000", "莱芜银行");
		BANK_CODE.put("05050000", "东莞商行");
		BANK_CODE.put("05060000", "温州银行");
		BANK_CODE.put("05070000", "汉口银行");
		BANK_CODE.put("05080000", "江苏银行");
		BANK_CODE.put("05210000", "江苏银行");
		BANK_CODE.put("05410000", "宝鸡商行");
		BANK_CODE.put("14010000", "上海农信");
		BANK_CODE.put("14480000", "农信");
		BANK_CODE.put("25060000", "永亨银行");
		BANK_CODE.put("25150000", "恒生银行");
		BANK_CODE.put("25210000", "集友银行");
		BANK_CODE.put("25240000", "工银亚洲");
		BANK_CODE.put("26530000", "可汗银行");
		BANK_CODE.put("48020000", "银商");
		BANK_CODE.put("63040000", "华夏银行");
		BANK_CODE.put("64030000", "北京银行");
		BANK_CODE.put("64080000", "宁波银行");
		BANK_CODE.put("64400000", "徽商银行");
		BANK_CODE.put("64480000", "南昌银行");
		BANK_CODE.put("64580000", "青海银行");
	}

	/**
	 * 块钱定义功能类型
	 * 
	 * @author Administrator
	 * 
	 */
	public interface MessageTypeDefine99Bill {
		public final static String REQUEST_POS_CHECK_IN = "0800";// 签到
		public final static String RESPONSE_POS_CHECK_IN = "0810";// 签到应答

		public final static String REQUEST_POS_CHECK_OUT = "0800";// 签退？？？？？？？？？？？
		public final static String REQUEST_POS_CHECK_DATA = "0810";// 批结算？？？？？？？？？

		public final static String REQUEST_POS_CHECK_DATA_DETAIL = "0200";// 批上送？？？？？？？？？
		public final static String RESPONSE_POS_CHECK_DATA_DETAIL = "0210";// 批上送？？？？？？？？？

		public final static String REQUEST_OP_PAY_MONEY = "0200";// 消费请求
		public final static String RESPONSE_OP_PAY_MONEY = "0210";// 消费应答

		public final static String REQUEST_OP_PAY_CANCEL = "0200";// 消费撤销
		public final static String RESPONSE_OP_PAY_CANCEL = "0210";// 消费撤销应答

		public final static String REQUEST_OP_OPERATE_CANCEL = "0400";// 冲正
		public final static String RESPONSE_OP_OPERATE_CANCEL = "0410";// 冲正应答

		public final static String REQUEST_OP_GOODS_BACKUP = "0220";// 退货
		public final static String RESPONSE_OP_GOODS_BACKUP = "0230";// 退货应答

		public final static String REQUEST_OP_QUERY_MONEY = "0200"; // 余额查询
		public final static String RESPONSE_OP_QUERY_MONEY = "0210"; // 余额查询应答

		public final static String REQUEST_OP_QUERY_INSURANCE = "0200"; // OQS查询
		public final static String RESPONSE_OP_QUERY_INSURANCE = "0210"; // OQS查询应答

		public final static String REQUEST_OP_QUERY_CHACKOUT = "0500"; // 批结算
		public final static String RESPONSE_OP_QUERY_CHACKOUT = "0500"; // 批结算应答

		public final static String REQUEST_POS_RECEIPTSALE = "0800";// 交易回执
		public final static String RESPONSE_POS_RECEIPTSALE = "0810";// 回执应答

		// 域41 终端代码
		public final static String POSID = "20100601";
		// 域42 商户代码
		public final static String CONTACT = "104110045110012";
		// 人民币代码
		public final static String RMBCODE = "156";

		// 网络信息代码
		public final static String NETMSGCODE = "001";

		/**
		 * POS 交易代码
		 */
		// 冲正代码
		public final static int POS_REVERSAL = 4;
		// POS结算
		public final static int POS_CHECK = 6;
		// 自动交易回执
		public final static int POS_SALE_ATRECEIPT = 7;
		// 手动交易回执
		public final static int POS_SALE_MTRECEIPT = 8;
		// 查询后消费
		public final static int POS_QUERYSALE = 601;
		// 查询
		public final static int POS_QUERY = 600;
		// POS 签到
		public final static int POS_LOGIN = 1;
		//
	}

	/**
	 * 块钱定义功能类型
	 * 
	 * @author Administrator
	 * 
	 */
	// public interface MessageTypeDefine99Bill{
	public interface MessageTypeDefineAllinpay {
		public final static String REQUEST_POS_CHECK_IN = "0800";// 签到
		public final static String RESPONSE_POS_CHECK_IN = "0810";// 签到应答

		public final static String REQUEST_POS_CHECK_OUT = "0820";// 签退
		public final static String REQUEST_POS_CHECK_DATA = "0810";// 批结算

		public final static String REQUEST_POS_CHECK_DATA_DETAIL = "0200";// 批上送？？？？？？？？？
		public final static String RESPONSE_POS_CHECK_DATA_DETAIL = "0210";// 批上送？？？？？？？？？

		public final static String REQUEST_OP_PAY_MONEY = "0200";// 消费请求
		public final static String RESPONSE_OP_PAY_MONEY = "0210";// 消费应答

		public final static String REQUEST_OP_PAY_CANCEL = "0200";// 消费撤销
		public final static String RESPONSE_OP_PAY_CANCEL = "0210";// 消费撤销应答

		public final static String REQUEST_OP_OPERATE_CANCEL = "0400";// 冲正
		public final static String RESPONSE_OP_OPERATE_CANCEL = "0410";// 冲正应答

		public final static String REQUEST_OP_GOODS_BACKUP = "0220";// 退货
		public final static String RESPONSE_OP_GOODS_BACKUP = "0230";// 退货应答

		public final static String REQUEST_OP_QUERY_MONEY = "0200"; // 余额查询
		public final static String RESPONSE_OP_QUERY_MONEY = "0210"; // 余额查询应答

		public final static String REQUEST_OP_QUERY_INSURANCE = "0220"; // OQS查询
		public final static String RESPONSE_OP_QUERY_INSURANCE = "0230"; // OQS查询应答

		public final static String REQUEST_OP_QUERY_CHACKOUT = "0500"; // 批结算
		public final static String RESPONSE_OP_QUERY_CHACKOUT = "0500"; // 批结算应答

		public final static String REQUEST_POS_RECEIPTSALE = "0800";// 交易回执
		public final static String RESPONSE_POS_RECEIPTSALE = "0810";// 回执应答

		// 域41 终端代码
		public final static String POSID = "20100601";
		// 域42 商户代码
		public final static String CONTACT = "104110045110012";
		// 人民币代码
		public final static String RMBCODE = "156";

		// 网络信息代码
		public final static String NETMSGCODE = "002";

		/**
		 * POS 交易代码
		 */
		// 冲正代码
		public final static int POS_REVERSAL = 4;
		// POS结算
		public final static int POS_CHECK = 6;
		// 自动交易回执
		public final static int POS_SALE_ATRECEIPT = 7;
		// 手动交易回执
		public final static int POS_SALE_MTRECEIPT = 8;
		// 查询后消费
		public final static int POS_QUERYSALE = 601;
		// 查询
		public final static int POS_QUERY = 600;
		// POS 签到
		public final static int POS_LOGIN = 1;
		//
		public final static int POS_OUT = 2;
		// 余额查询
		public final static int POS_QUERY_BALANCE = 100; 
		/**
		 * 表8　处理要求 0 无处理要求 1 POS中心下传的处理要求，让终端发起参数下载交易 2 上传终端状态信息 3 重新签到 4
		 * 通知终端发起更新公钥信息操作 5 下载终端IC卡参数 6 下载终端程序 7 POS中心通知POS将管理统计数据项清0 9
		 * 通知终端发起更新公钥和更新IC卡参数操作
		 */
		public final static int POS_REQUEST_NOT = 0;
		public final static int POS_REQUEST_DOWNLOAD = 1;
		public final static int POS_REQUEST_UPLOAD = 2;
		public final static int POS_REQUEST_LOGIN = 3;
		public final static int POS_REQUEST_UPDATA = 4;

	}

	/**
	 * 银联定义功能类型
	 * 
	 * @author Administrator
	 * 
	 */
	public interface MessageTypeDefineUnionpay {
		public final static String REQUEST_POS_CHECK_IN = "800";// 签到
		public final static String RESPONSE_POS_CHECK_IN = "810";// 签到应答

		public final static String REQUEST_POS_CHECK_OUT = "800";// 签退？？？？？？？？？？？
		public final static String REQUEST_POS_CHECK_DATA = "810";// 批结算？？？？？？？？？

		public final static String REQUEST_POS_CHECK_DATA_DETAIL = "200";// 批上送？？？？？？？？？
		public final static String RESPONSE_POS_CHECK_DATA_DETAIL = "210";// 批上送？？？？？？？？？

		public final static String REQUEST_OP_PAY_MONEY = "200";// 消费请求
		public final static String RESPONSE_OP_PAY_MONEY = "210";// 消费应答

		public final static String REQUEST_OP_PAY_CANCEL = "200";// 消费撤销
		public final static String RESPONSE_OP_PAY_CANCEL = "210";// 消费撤销应答

		public final static String REQUEST_OP_OPERATE_CANCEL = "400";// 冲正
		public final static String RESPONSE_OP_OPERATE_CANCEL = "410";// 冲正应答

		public final static String REQUEST_OP_GOODS_BACKUP = "220";// 退货
		public final static String RESPONSE_OP_GOODS_BACKUP = "230";// 退货应答
	}

	/**
	 * POS终端报文类型
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PosMessageType {
		public final static byte DATA_TRANSFER = 0X00;// 数据传输
		public final static byte TRANSFER_MESSAGE = 0X01;// 交易报文
		public final static byte CMD_CODE_MEMO = 0X02;// 指令码说明
	}

	/**
	 * POS终端命令码定义
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PosCommandCode {
		// 数据传输命令ID
		public final static byte DATA_TRANSFER_QUERY_ORDER_ID = 0x01;// 查找原交易凭证号
		public final static byte DATA_TRANSFER_VIEW_COMM_PARAMS = 0x02;// 查看通讯参数
		public final static byte DATA_TRANSFER_SET_COMM_PARAMS = 0x03;// 设置通讯参数
		public final static byte DATA_TRANSFER_SET_COMM_STATE = 0x04;// 通讯状态设置
		public final static byte DATA_TRANSFER_VIEW_COMM_STATE = 0x05;// 查询通讯状态
		public final static byte DATA_TRANSFER_SYN_TIME = 0x06;// 时间同步指令
		public final static byte DATA_TRANSFER_CREATE_MAC = 0x07;// MAC计算

		// 指令码说明命令ID
		public final static byte CMD_CODE_MEMO_READ_IC_INFO = 0X03;// 读取磁道信息
		public final static byte CMD_CODE_MEMO_READ_KEY_CRYPT = 0X04;// 读取密码密文
		public final static byte CMD_CODE_MEMO_INPUT_BY_HAND = 0X05;// 手工输入卡号

	}


	


	public static String bill99result(String resultNo) {
		if (BILL99_RESULT_TYPE_CODE.containsKey(resultNo))
			return BILL99_RESULT_TYPE_CODE.get(resultNo);
		else
			return "失败";
	}

	public static String allInpayresult(String resultNo) {
		if (ALLINPAY_RESULT_TYPE_CODE.containsKey(resultNo))
			return ALLINPAY_RESULT_TYPE_CODE.get(resultNo);
		else
			return "失败 ";
	}
	
	public static String allInpayGetBankInfo(String bankNo){
		if(BANK_CODE.containsKey(bankNo))
			return BANK_CODE.get(bankNo);
		else
			return "其他银行";
	}
}

