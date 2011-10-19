#pragma once
#include "DataType.h"
class CHighPassFilter
{
public:
	CHighPassFilter(void);
	~CHighPassFilter(void);

public:
	void Init();

	int Process(int nData);

protected:
	int m_y1;//y1

	int m_buf[66];//x[66]

	int m_index;//n
};
