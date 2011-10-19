#pragma once
#include "DataType.h"

class CSlidingWindowIntegrator
{
public:
	CSlidingWindowIntegrator(void);
	~CSlidingWindowIntegrator(void);

public:
	void Init();

	int Process(int nData);

protected:
	int m_buf[45/*64*/]; //x[64]
	int m_index;   //ptr
	int m_sum;     //sum

	int m_sum2;
	int m_avgBuf[45];
	int m_nWindowCount;

};
