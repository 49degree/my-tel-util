package com.xys.ecg.bean;

/**
 * contains information of diagnoses.
 * @author Administrator
 *
 */
public class CDiagnoseResult {
	//The types of Exceptional Event.
	public final static byte	ET_EventButtonDown = 1;			    // ������ť
	public final static byte	ET_LowHeartRate    = 2;				// ���ɹ���
	public final static byte	ET_HighHeartRate   = 3;				// ���ɹ���
	public final static byte	ET_STElevation     = 4;	            // ST����
	public final static byte	ET_STDepression    = 5;             // ST�½�
	
	public byte  m_code;          //�쳣������
	public int   m_dwPacketCount; //�����쳣�İ�����
	public int  m_nOffset;       //���������ĵ��ڰ��ڵ�λ��
	
	public int m_nCurHR;//����
	
	
	

}
