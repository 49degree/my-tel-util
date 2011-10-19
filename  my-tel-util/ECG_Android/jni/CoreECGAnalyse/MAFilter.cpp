#include "MAFilter.h"

CMAFilter::CMAFilter(void)
{
	Init();
}

CMAFilter::~CMAFilter(void)
{
}

void CMAFilter::Init()
{
	//::memset(m_window, 127, FILTER_DEPH);

	m_sum = 127 * FILTER_DEPH;
}

BOOL CMAFilter::RemoveNoise(BYTE *pData, UINT nLen)
{
	if (!pData)
	{
		return FALSE;
	}

	for (UINT i = 0; i < nLen; ++i)
	{
		//TRACE(_T("%d,"), pData[i]);
		m_sum = m_sum - m_window[FILTER_DEPH-1] + *(pData+i);

		for (int j = FILTER_DEPH-1; j > 0; --j)
		{
			m_window[j] = m_window[j-1];
		}
		m_window[0] = *(pData+i);

		*(pData+i) = (BYTE)(m_sum/FILTER_DEPH);

		//TRACE(_T("%d\n"), pData[i]);
	}

	return TRUE;
}
