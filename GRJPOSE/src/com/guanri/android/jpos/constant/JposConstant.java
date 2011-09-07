package com.guanri.android.jpos.constant;

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
	
	
	/**
	 * 块钱定义功能类型
	 * @author Administrator
	 *
	 */
	public interface MessageTypeDefine99Bill{
		public final static int REQUEST_POS_CHECK_IN = 800;// 签到
		public final static int RESPONSE_POS_CHECK_IN = 810;// 签到应答
		
		public final static int REQUEST_POS_CHECK_OUT = 800;//签退？？？？？？？？？？？
		public final static int REQUEST_POS_CHECK_DATA = 810;//批结算？？？？？？？？？
		
		public final static int REQUEST_POS_CHECK_DATA_DETAIL = 200;//批上送？？？？？？？？？
		public final static int RESPONSE_POS_CHECK_DATA_DETAIL = 210;//批上送？？？？？？？？？
		
		
		public final static int REQUEST_OP_PAY_MONEY = 200;//消费请求
		public final static int RESPONSE_OP_PAY_MONEY = 210;//消费应答
		
		public final static int REQUEST_OP_PAY_CANCEL = 200;//消费撤销
		public final static int RESPONSE_OP_PAY_CANCEL = 210;//消费撤销应答
		
		public final static int REQUEST_OP_OPERATE_CANCEL = 400;//冲正
		public final static int RESPONSE_OP_OPERATE_CANCEL = 410;//冲正应答
		
		public final static int REQUEST_OP_GOODS_BACKUP = 220;//退货
		public final static int RESPONSE_OP_GOODS_BACKUP = 230;//退货应答
	}
	
	/**
	 * 银联定义功能类型
	 * @author Administrator
	 *
	 */
	public interface MessageTypeDefineUnionpay{
		public final static int REQUEST_POS_CHECK_IN = 800;// 签到
		public final static int RESPONSE_POS_CHECK_IN = 810;// 签到应答
		
		public final static int REQUEST_POS_CHECK_OUT = 800;//签退？？？？？？？？？？？
		public final static int REQUEST_POS_CHECK_DATA = 810;//批结算？？？？？？？？？
		
		public final static int REQUEST_POS_CHECK_DATA_DETAIL = 200;//批上送？？？？？？？？？
		public final static int RESPONSE_POS_CHECK_DATA_DETAIL = 210;//批上送？？？？？？？？？
		
		
		public final static int REQUEST_OP_PAY_MONEY = 200;//消费请求
		public final static int RESPONSE_OP_PAY_MONEY = 210;//消费应答
		
		public final static int REQUEST_OP_PAY_CANCEL = 200;//消费撤销
		public final static int RESPONSE_OP_PAY_CANCEL = 210;//消费撤销应答
		
		public final static int REQUEST_OP_OPERATE_CANCEL = 400;//冲正
		public final static int RESPONSE_OP_OPERATE_CANCEL = 410;//冲正应答
		
		public final static int REQUEST_OP_GOODS_BACKUP = 220;//退货
		public final static int RESPONSE_OP_GOODS_BACKUP = 230;//退货应答
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
