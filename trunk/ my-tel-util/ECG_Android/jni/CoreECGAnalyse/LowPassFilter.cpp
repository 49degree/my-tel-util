#include "LowPassFilter.h"

CLowPassFilter::CLowPassFilter(void)
{
	//::memset(m_buf, 0, sizeof(m_buf) );
	m_y1 = m_y2 = 0;
	m_index = 12;

}

CLowPassFilter::~CLowPassFilter(void)
{
}


void CLowPassFilter::Init()
{
	//::memset(m_buf, 0, sizeof(m_buf) );
	m_y1 = m_y2 = 0;
	m_index = 12;
}

int CLowPassFilter::Process(int nData)
{
	int ret = 0;//y0

	m_buf[m_index] = m_buf[m_index+13] = nData;

	ret = (m_y1<<1) - m_y2 + m_buf[m_index] - (m_buf[m_index+6]<<1) + m_buf[m_index+12];

	m_y2 = m_y1;

	m_y1 = ret;

	ret >>= 5;

	if (--m_index < 0)
	{
		m_index = 12;
	}

	return ret;
}
