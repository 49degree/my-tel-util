package com.xys.ecg.bean;

/**
 * contains information of diagnoses.
 * @author Administrator
 *
 */
public class CDiagnoseResult {
	//The types of Exceptional Event.
	public final static byte	ET_EventButtonDown = 1;			    // 紧急按钮
	public final static byte	ET_LowHeartRate    = 2;				// 心律过慢
	public final static byte	ET_HighHeartRate   = 3;				// 心律过快
	public final static byte	ET_STElevation     = 4;	            // ST上升
	public final static byte	ET_STDepression    = 5;             // ST下降
	
	public byte  m_code;          //异常的类型
	public int   m_dwPacketCount; //发生异常的包计数
	public int  m_nOffset;       //发生导常的点在包内的位置
	
	public int m_nCurHR;//心率
	
	
	

}
