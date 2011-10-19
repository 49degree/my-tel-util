#include "RWaveAnalyst.h"

CRWaveAnalyst::CRWaveAnalyst(void)
{
	m_nMaxPos = m_nMaxVal = 0;
	m_nPackCount = 0;
	m_nRRDistance = 0;
	m_index = 0;

	//::ZeroMemory(m_aRRDistance, sizeof(m_aRRDistance) );

	//��ֵѡ����Ʊ���
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

	//��ֵѡ����Ʊ���
	m_bIsChecked = false;
	m_nPos = 0;
	m_nThreshold = 8;
	m_sum = 0;

	m_dwRPacketCount = 0;
}

/*
* �������ܣ�
*           
* ����˵����
*           
* ����ֵ  ��
*          	TRUE - ����R��
*           FALSE - δ����R��
* ��ע������
*           
*/
BOOL CRWaveAnalyst::Process( int nXPos, int nTempData, int nOriData, 
							CRRWave &rrWave, DWORD dwPacketCount, UINT nEcgFreq )
{
	int sum = 0;

	//�����������¼RR���ڵ���������
	rrWave.m_data[rrWave.m_nDataLen++] = nOriData;

	//��¼��ǰ�İ������Ͱ���ƫ����
	if (m_nPackCount != dwPacketCount)
	{
		m_nPointCount = 1;
		m_nPackCount = dwPacketCount;
	}
	else
	{
		++m_nPointCount;
	}
	
	//��¼�������Ϣ
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
		//��Ret>0�����ڵ�ǰ��֮ǰ���������ΪR������
		//(�Ѿ����ǵ�Tompkins�㷨�����ԭ�źŵ�X���ӳ�����)

	

		//��¼R��λ��
		rrWave.m_dwRPacketCout = m_dwRPacketCount;
		rrWave.m_nROffsetInPacket = m_nROffsetInPacket;

		//����ƽ������
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

		//����״̬
		m_nRRDistance = 0;
		m_nMaxVal = 0;

		return TRUE;
	}
	
	return FALSE;
}

/*
* �������ܣ�
*           ����Tompkins�㷨���ɵĲ��������R������
* ����˵����
*           nData - ����Tompkins����������
* ����ֵ  ��
*          	1 - ����R������
*			0 - ���ڻ��������ڣ����Ѿ����ֹ�R��
*		   -1 - �����������ڣ�δ����R��
* ��ע������
*           
*/
int CRWaveAnalyst::CheckRPoint( int nData, UINT nEcgFreq )
{
	m_sum += nData;
	++m_nPos;

	//���¼��㷧ֵ
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
			return 0;//�Ѿ�����
		}
		else
		{
			m_bIsChecked = true;//��⵽�����أ�R�����㣩
			return 1;
		}
	}
	else
	{
		m_bIsChecked = false; //��⵽�½��أ��������ڣ�
		return -1;
	}
}
