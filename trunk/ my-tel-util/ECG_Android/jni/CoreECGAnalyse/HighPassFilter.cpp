#include "HighPassFilter.h"

CHighPassFilter::CHighPassFilter(void)
{
	//::memset(m_buf, 0 , sizeof(m_buf) );
	m_y1 = 0;
	m_index = 32;
}

CHighPassFilter::~CHighPassFilter(void)
{
}

void CHighPassFilter::Init()
{
	//::memset(m_buf, 0 , sizeof(m_buf) );
	m_y1 = 0;
	m_index = 32;
}

int CHighPassFilter::Process(int nData)
{
	int ret = 0;

	m_buf[m_index] = m_buf[m_index+33] = nData;

	ret = m_y1 + m_buf[m_index] - m_buf[m_index+32];
	m_y1 = ret;

	if (--m_index < 0)
	{
		m_index = 32;
	}

	return (m_buf[m_index+16] - (ret>>5) );
}
