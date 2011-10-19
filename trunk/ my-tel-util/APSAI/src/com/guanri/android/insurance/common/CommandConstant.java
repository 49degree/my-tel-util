package com.guanri.android.insurance.common;

/**
 * 定义服务器交互命令常量
 */
public class CommandConstant {
	/**
	 * 命令功能 助记符 命令码 （ASC）
	 */
	//初始化命令	CMD_INIT
	public static final String CMD_INIT = "100001";
	// 用户登录命令
	public static final String CMD_LOGIN = "100002";
	// 终端故障告警
	public static final String CMD_POS_ALARM = "100003";
	// 通信测试命令
	public static final String CMD_TEST = "100004";
	// 错误信息更新
	public static final String CMD_ANSWER_MSG = "100005";
	// 增加操作员
	public static final String CMD_ADD_OPE = "100006";
	// 删除操作员
	public static final String CMD_DEL_OPE = "100007";
	// 密码修改
	public static final String CMD_CHANGE_PASSWORD = "100008";
	// 管理员修改操作员密码
	public static final String CMD_CHG_PSW2 = "100009";
	// 销售类（6） 保单销售(太保寿险专用)
	public static final String CMD_SALE = "110001";
	// 单证申请 CMD_ASKNOTE “110011”或“110014”
	public static final String CMD_ASKNOTE1 = "110011";
	public static final String CMD_ASKNOTE4 = "110014";
	// 实时保单销售
	public static final String CMD_SALE2 = "110012";
	// 非实时保单销售
	public static final String CMD_SALEREPORT = "110013";
	// 保单批量汇报
	public static final String CMD_SALEREPORT2 = "110015";
	// 退单命令
	public static final String CMD_REFUND = "110002";
	// 废单命令
	public static final String CMD_DESTROY = "110003";
	// 对账类（2） 总对账
	public static final String CMD_RECORD_TOTAL = "120001";
	// 明细对账
	public static final String CMD_RECORD_LIST = "120002";
	// 方案文件（4） 方案文件列表
	public static final String CMD_CONTROLFILE_LIST = "130001";
	// 方案文件下载
	public static final String CMD_CONTROLFILE_DL = "130002";
	// 编辑模板下载
	public static final String CMD_EDITFILE_DL = "130012";
	// 打印模板下载
	public static final String CMD_PRINTFILE_DL = "130022";
	// 程序下载（1） 程序下载
	public static final String CMD_PROGRAM_DL = "140001";
	// 查询销售类 查询销售统计信息
	public static final String CMD_SALECOUNT = "150001";

	//终端信息配置文件 
	public static String COMFIG_POS_ID = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.POS_ID);//终端ID
	public static String COMFIG_COM_PWD = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.COMPSW);//终端校验码
	public static String COMFIG_SIM_CODE = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.SIMCODE);//SIM卡号码
	public static String COMFIG_BRANCH_ID = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.BRANCHID);//分公司代码

	public final static byte ANSWER_CODE_RIGHT  =	 0x00;//：命令正确执行，没有错误；
	public final static byte ANSWER_CODE_CHECK_ERROR  =	 0x01;//： 本次命令正确执行，但对账不平（用于对账命令），需要上传明细；
	public final static byte ANSWER_CODE_KEY_ERROR  =	 0x10;//：错误，接收到的数据包数字签名或解密错，终端收到这个应答码后需要重新发登录命令取新的加密密钥；
	public final static byte ANSWER_CODE_SEQ_ERROR  =	 0x11;//：命令序列号失步，终端接收到这个应答码后需要将下一次的通信序列号归0，后台也同步归0
	public final static byte ANSWER_CODE_SEQ_STOP  =	 0x12;//：命令序列号暂停，终端接收到这个应答码后不累加命令序列号
	public final static byte ANSWER_CODE_RIGHT_SEQ_STOP = (byte)0xFF;//: 处理成功，和0x00结果码的唯一区别是不累加通信序列号，用于多包命令的中间应答使用
	
	/*
	平安保险公司 I01 \x7C\xA3\x16\x02\xB9\x64\x4F\xED
	国寿   I03 \x42\xA7\x06\xD8\xB9\xC5\x3F\xE1
	太平养老  I08 \xBA\x28\x1E\xD4\x73\x06\x59\x7F
	都邦   I09 \xAB\x08\x12\xD6\x59\x7B\x46\xF3
	人保人寿  I10 \x5D\xA3\x18\x09\x95\xBC\xF4\x26
	人保健康  I11 \xAA\x29\xD7\x6C\x5F\xEB\x4C\x07
	泰康人寿  I13 \x09\xAD\x7F\xB5\x6E\x13\xC4\x27
	华泰人寿  I06 \x9E\x08\x7D\x5F\x46\xC3\xAB\x12
	冠日公司   \x30\x32\x31\x31\x32\x33\x34\x35
	百年人寿  I14 \xB0\x18\x4C\x5F\xA6\x7E\x23\xD9
	*/
	
	//223.223.5.249","5552"对应泰康人寿
	//223.223.5.249","5555"对应华泰人寿
	//42A706D8B9053FE1
	public final static byte[]  GR_KEY_PINGANBAOXIAN  = {0x7C,(byte) 0xA3,(byte) 0x16,(byte) 0x02,(byte) 0xB9,(byte) 0x64,(byte) 0x4F,(byte) 0xED};
	public final static byte[]  GR_KEY_GUOSHOU   = {0x42,(byte) 0xA7,(byte) 0x06,(byte) 0xD8,(byte) 0xB9,(byte) 0xC5,(byte) 0x3F,(byte) 0xE1};
	public final static byte[]  GR_KEY_TAIPINGYANGLAO = {(byte) 0xBA,(byte) 0x28,(byte) 0x1E,(byte) 0xD4,(byte) 0x73,(byte) 0x06,(byte) 0x59,(byte) 0x7F};
	public final static byte[]  GR_KEY_DUBANG   = {(byte) 0xAB,(byte) 0x08,(byte) 0x12,(byte) 0xD6,(byte) 0x59,(byte) 0x7B,(byte) 0x46,(byte) 0xF3};
	public final static byte[]  GR_KEY_RENBAORENSHOU  = {0x5D,(byte) 0xA3,(byte) 0x18,(byte) 0x09,(byte) 0x95,(byte) 0xBC,(byte) 0xF4,(byte) 0x26};
	public final static byte[]  GR_KEY_RENBAOJIANKANG = {(byte) 0xAA,(byte) 0x29,(byte) 0xD7,(byte) 0x6C,(byte) 0x5F,(byte) 0xEB,(byte) 0x4C,(byte) 0x07};
	public final static byte[]  GR_KEY_TAIKANGRENSHOU = {0x09,(byte) 0xAD,(byte) 0x7F,(byte) 0xB5,(byte) 0x6E,(byte) 0x13,(byte) 0xC4,(byte) 0x27};
	public final static byte[]  GR_KEY_HUATAIRENSHOU  = {(byte) 0x9E,(byte) 0x08,(byte) 0x7D,(byte) 0x5F,(byte) 0x46,(byte) 0xC3,(byte) 0xAB,(byte) 0x12};
	public final static byte[]  GR_KEY_GUANRI   = {0x30,(byte) 0x32,(byte) 0x31,(byte) 0x31,(byte) 0x32,(byte) 0x33,(byte) 0x34,(byte) 0x35};
	public final static byte[]  GR_KEY_BAINIANRENSHOU = {(byte) 0xB0,(byte) 0x18,(byte) 0x4C,(byte) 0x5F,(byte) 0xA6,(byte) 0x7E,(byte) 0x23,(byte) 0xD9};
	

	//对账标志
	public final static int CHECK_STATE_YES = 1;
	public final static int CHECK_STATE_NO = 0;
}

