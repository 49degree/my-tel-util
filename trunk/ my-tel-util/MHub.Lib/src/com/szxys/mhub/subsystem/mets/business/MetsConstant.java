package com.szxys.mhub.subsystem.mets.business;

public class MetsConstant {
	//control中用到的 控制指令ID，具体值由各个子系统（业务）自行定义
	public final static int CTRL_TEST = 21999;//测试业务
	//public final static int CTRL_GET_URAL = 21001;//获取尿量
	
	public final static int CTRL_SEND_WATER = 21001;//发送饮水信息
	public final static int CTRL_SEND_URINE = 125;//发送尿量信息
	public final static int CTRL_SEND_URINE_EMERGENCY = 21003;//发送尿急信息
	public final static int CTRL_SEND_URINE_INCONTIUNUE = 21004;//发送尿失禁信息
	public final static int CTRL_SEND_URINE_UFR = 21005;//发送尿流率信息
	public final static int CTRL_SEND_URINE_GETUPGOTOBED = 21006;//发送起床睡觉时间信息
	public final static int CTRL_SEND_URINE_QUESTIONAIRE = 21007;//发送问卷调查结果信息
	
	
	//数据协议主码 lMainCmd
	public final static int MAIN_CMD_TEST = 22001;//测试
	
	//lMainCmd	数据协议主码
	public final static int SUB_CMD_TEST = 23001;//测试

}
