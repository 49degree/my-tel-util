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
	int m_nMaxPos;          //最大点的位置,r_x
	int m_nMaxVal;          //最大点的值,r_y
	int m_nPackCount;       //当前正在处理的数据包计数
	int m_nRRDistance;      //最新的RR间期长度
	int m_aRRDistance [8];  //RR间期数组
	int m_index;            //RR间期数组迭代器

	int m_nPointCount;	    //计算当前的点是包内的第几个点
	int m_nROffsetInPacket; //最近检测到的R波所在包内的位置
	DWORD m_dwRPacketCount; //最近检测到的R波所在包的计数

	bool m_bIsChecked;      //是否是同一个波中的数据
	int m_nPos;             //m_freqpos
	int m_nThreshold;       //阈值MaxValue
	int m_sum;              //sum
	
};
