#pragma once
#include "DataType.h"

#define BASELINE_BUF_SIZE				179
#define STANDARD_BASELINE_VALUE			127

class CBaseLineProcessor
{
public:
	CBaseLineProcessor();
	~CBaseLineProcessor(void);

public:
	bool ProcessDrift(BYTE *pData, DWORD dwLen);
	BYTE Process(BYTE nData);
	void Init();

private:
	BYTE			m_buf[BASELINE_BUF_SIZE];
	int				m_nSum;
};
