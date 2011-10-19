#include "RWaveAnalyst.h"

CRWaveAnalyst::CRWaveAnalyst(void)
{
	m_nMaxPos = m_nMaxVal = 0;
	m_nPackCount = 0;
	m_nRRDistance = 0;
	m_index = 0;

	//::ZeroMemory(m_aRRDistance, sizeof(m_aRRDistance) );

	//阈值选择控制变量
	m_bIsChecked = false;
	m_nPos = 0;
	m_nThreshold = 8;
	m_sum = 0;

	m_dwRPacketCount = 0;
}

CRWaveAnalyst::~CRWaveAnalyst(void)
{
}

void CRWaveAnalyst::Init()
{
	m_nMaxPos = m_nMaxVal = 0;
	m_nPackCount = 0;
	m_nRRDistance = 0;
	m_index = 0;

	for (int i = 0; i < 8; ++i)
	{
		m_aRRDistance[i] = 220;
	}

	//阈值选择控制变量
	m_bIsChecked = false;
	m_nPos = 0;
	m_nThreshold = 8;
	m_sum = 0;

	m_dwRPacketCount = 0;
}

/*
* 函数介绍：
*           
* 参数说明：
*           
* 返回值  ：
*          	TRUE - 发现R波
*           FALSE - 未发现R波
* 备注　　：
*           
*/
BOOL CRWaveAnalyst::Process( int nXPos, int nTempData, int nOriData, 
							CRRWave &rrWave, DWORD dwPacketCount, UINT nEcgFreq )
{
	int sum = 0;

	//将数据置入记录RR间期的数据容器
	rrWave.m_data[rrWave.m_nDataLen++] = nOriData;

	//记录当前的包计数和包内偏移量
	if (m_nPackCount != dwPacketCount)
	{
		m_nPointCount = 1;
		m_nPackCount = dwPacketCount;
	}
	else
	{
		++m_nPointCount;
	}
	
	//记录最大点的信息
	if (nOriData > m_nMaxVal)
	{
		m_nMaxPos = nXPos;
		m_nMaxVal = nOriData;

		rrWave.m_nRRLen = rrWave.m_nDataLen;
		m_nRRDistance = rrWave.m_nDataLen;
		m_dwRPacketCount = dwPacketCount;
		m_nROffsetInPacket = m_nPointCount;
	}

	int ret = CheckRPoint(nTempData, nEcgFreq);
	if (rrWave.m_nDataLen >= RRWAVE_BUF_SIZE-1)
	{
		rrWave.m_nAvarageHR = 0;
		m_nRRDistance = 0;
		m_nMaxVal = 0;
		rrWave.m_nDataLen = 0;
	}
	else if (ret > 0)
	{
		//若Ret>0，则在当前点之前最近的最大点为R波顶点
		//(已经考虑到Tompkins算法输出与原信号的X轴延迟问题)

	

		//记录R波位置
		rrWave.m_dwRPacketCout = m_dwRPacketCount;
		rrWave.m_nROffsetInPacket = m_nROffsetInPacket;

		//计算平均心率
		double dCurRR = 1.0 * m_nRRDistance / nEcgFreq;
		m_aRRDistance[m_index++] = m_nRRDistance;
		if (m_index >= 8)
		{
			m_index = 0;
		}

		for (int i = 0; i < 8; ++i)
		{
			sum += m_aRRDistance[i];
		}

		if (0 == sum)
		{
			rrWave.m_nAvarageHR = 0;
		}
		else
		{
			rrWave.m_nAvarageHR = 60 * 8 * nEcgFreq / sum;
		}

		//更新状态
		m_nRRDistance = 0;
		m_nMaxVal = 0;

		return TRUE;
	}
	
	return FALSE;
}

/*
* 函数介绍：
*           根据Tompkins算法生成的波形来检测R波顶点
* 参数说明：
*           nData - 经过Tompkins换算后的数据
* 返回值  ：
*          	1 - 发现R波顶点
*			0 - 仍在滑动窗口内，并已经发现过R波
*		   -1 - 超出滑动窗口，未发现R波
* 备注　　：
*           
*/
int CRWaveAnalyst::CheckRPoint( int nData, UINT nEcgFreq )
{
	m_sum += nData;
	++m_nPos;

	//重新计算阀值
	if ( (m_nPos % nEcgFreq)==0 && m_nPos>0)
	{
		m_nThreshold = (m_nThreshold + (m_sum/nEcgFreq) ) / 2;
		m_nPos = 0;
		m_sum = 0;
	}

	if (nData > m_nThreshold)
	{
		if (m_bIsChecked)
		{
			return 0;//已经检测过
		}
		else
		{
			m_bIsChecked = true;//检测到上升沿（R波顶点）
			return 1;
		}
	}
	else
	{
		m_bIsChecked = false; //检测到下降沿（超过窗口）
		return -1;
	}
}
