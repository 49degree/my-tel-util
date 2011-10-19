#include "BaseLineProcessor.h"

CBaseLineProcessor::CBaseLineProcessor(void) : m_nSum(127 * BASELINE_BUF_SIZE)
{
	//::memset(m_buf, 127, BASELINE_BUF_SIZE * sizeof(m_buf[0]) );
}

CBaseLineProcessor::~CBaseLineProcessor(void)
{
}

void CBaseLineProcessor::Init()
{
	//m_nSum = 127 * BASELINE_BUF_SIZE;
	//::memset(m_buf, 127, BASELINE_BUF_SIZE * sizeof(m_buf[0]) );
}

BYTE CBaseLineProcessor::Process(BYTE nData)
{
	int nModify = nData;

	//calculate average value
	m_nSum =m_nSum - m_buf[0] + nData;
	for (int i = 0; i < (BASELINE_BUF_SIZE-1); ++i)
	{
		m_buf[i] = m_buf[i+1];
	}
	m_buf[BASELINE_BUF_SIZE - 1] = nData;
	int nAvg = m_nSum / BASELINE_BUF_SIZE;
	

	//offset value
	int nOffset = nAvg - (int)STANDARD_BASELINE_VALUE;

	//modified value
	nModify =nData - nOffset;


	if (nModify > 255)
	{
		nModify = 255;
	}
	else if (nModify < 0)
	{
		nModify = 0;
	}

	//TRACE(_T("Ori:%d, Avg:%d, Offset:%d, Modify:%d.\n"), nData, nAvg, nOffset, nModify);

	return (BYTE)nModify;
}

bool CBaseLineProcessor::ProcessDrift(BYTE *pData, DWORD dwLen)
{
	if (!pData || 0==dwLen)
	{
		TRACE(_T("Invalid Parameters for CBaseLineProcessor::ProcessDrift().\n") );
	}

	for (int i = 0; i < dwLen; ++i)
	{
		pData[i] = this->Process(pData[i]);
	}

	return true;
}
