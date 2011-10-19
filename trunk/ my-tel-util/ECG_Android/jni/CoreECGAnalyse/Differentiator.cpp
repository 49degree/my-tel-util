#include "Differentiator.h"

CDifferentiator::CDifferentiator(void)
{
	//memset(m_buf, 0, sizeof(m_buf) );
}

CDifferentiator::~CDifferentiator(void)
{
}

void CDifferentiator::Init()
{
	//memset(m_buf, 0, sizeof(m_buf) );
}

int CDifferentiator::Process(int nData)
{
	int ret = 0;//y

	ret = (nData<<1) + m_buf[3] - m_buf[1] - (m_buf[0]<<1);
	ret >>= 3;

	for (int i = 0; i < 3; ++i)
	{
		m_buf[i] = m_buf[i+1];
	}
	m_buf[3] = nData;

	return ret;
}
