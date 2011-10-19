#pragma once
#include "DataType.h"

class CRWaveAnalyst
{
public:
	CRWaveAnalyst(void);
	~CRWaveAnalyst(void);

public:
	void Init();

	BOOL Process(int nXPos, int nTempData, int nOriData, CRRWave &rrWave, DWORD dwPacketCount, UINT nEcgFreq);

protected:
	int CheckRPoint(int nData, UINT nEcgFreq);

protected:
	int m_nMaxPos;          //�����λ��,r_x
	int m_nMaxVal;          //�����ֵ,r_y
	int m_nPackCount;       //��ǰ���ڴ�������ݰ�����
	int m_nRRDistance;      //���µ�RR���ڳ���
	int m_aRRDistance [8];  //RR��������
	int m_index;            //RR�������������

	int m_nPointCount;	    //���㵱ǰ�ĵ��ǰ��ڵĵڼ�����
	int m_nROffsetInPacket; //�����⵽��R�����ڰ��ڵ�λ��
	DWORD m_dwRPacketCount; //�����⵽��R�����ڰ��ļ���

	bool m_bIsChecked;      //�Ƿ���ͬһ�����е�����
	int m_nPos;             //m_freqpos
	int m_nThreshold;       //��ֵMaxValue
	int m_sum;              //sum
	
};
