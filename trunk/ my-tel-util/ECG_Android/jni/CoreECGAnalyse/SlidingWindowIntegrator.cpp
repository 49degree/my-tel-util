#include "SlidingWindowIntegrator.h"

CSlidingWindowIntegrator::CSlidingWindowIntegrator(void)
{
	//memset(m_buf, 0, sizeof(m_buf) );
	m_index = m_sum = 0;
	
	//memset(m_avgBuf, 0, sizeof(m_avgBuf) );
	m_sum2 = m_nWindowCount = 0;
}

CSlidingWindowIntegrator::~CSlidingWindowIntegrator(void)
{
}

void CSlidingWindowIntegrator::Init()
{
	//memset(m_buf, 0, sizeof(m_buf) );
	m_index = m_sum = 0;

	//memset(m_avgBuf, 0, sizeof(m_avgBuf) );
	m_sum2 = m_nWindowCount = 0;
}

int CSlidingWindowIntegrator::Process(int nData)
{
	int nTemp = 0;//ly
	int ret = 0;  //y

	if (++m_index == 45/*64*/)
	{
		m_index = 0;
	}

	m_sum -= m_buf[m_index];
	m_sum += nData;
	m_buf[m_index] = nData;
	nTemp = m_sum / 45/*>>6*/;

	if (nTemp > 45600/*32400*/)
	{
		ret = 45600/*32400*/;
	}
	else
	{
		ret = nTemp;
	}

	//滑动窗口平均
	m_sum2 += ret;
	if (m_nWindowCount < 45)//未满则插入
	{
		m_avgBuf[m_nWindowCount] = ret;
		++m_nWindowCount;

		if (m_nWindowCount == 45 )//刚好满
		{
			ret = m_sum2 / 45;

			int nTemp = m_avgBuf[0];
			m_sum2 -= nTemp;
		}
	}
	else//满了则更新
	{
		int nOri = ret;
		ret = m_sum2 / 45;

		m_sum2 -= m_avgBuf[0];

		for (int i = 0; i < (45 - 1); ++i)
		{
			m_avgBuf[i] = m_avgBuf[i+1];
		}
		m_avgBuf[44] = nOri;
	}

	

	return ret;
}
