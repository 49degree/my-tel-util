#pragma once
#include "DataType.h"
class CLowPassFilter
{
public:
	CLowPassFilter(void);

	~CLowPassFilter(void);

public:
	void Init();

	int Process(int nData);

protected:
	int m_y1;//y1
		
	int m_y2;//y2

	int m_buf[26];//x[26]

	int m_index;//n

};
