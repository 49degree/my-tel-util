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
	
	public final static HashMap<Integer,String> POS_BILL99_TRANCE_CODE = new HashMap<Integer,String>();//POS终端，快钱交易码对照表
	public final static HashMap<String,String> BILL99_RESULT_TYPE_CODE = new HashMap<String,String>();//快钱交易结果对照表
	
	static{
		//POS终端交易码对照表
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_QUERY_BALANCE, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_PAY, "");
		POS_BILL99_TRANCE_CODE.put(POS_TRANCE_CODE_BACK_ORDER, "");
		//快钱交易结果对照表
		BILL99_RESULT_TYPE_CODE.put("00", "交易成功");
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
}
