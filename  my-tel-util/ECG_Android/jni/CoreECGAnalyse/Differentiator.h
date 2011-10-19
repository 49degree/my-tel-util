#pragma once
#include "DataType.h"
class CDifferentiator
{
public:
	CDifferentiator(void);
	~CDifferentiator(void);

public:
	void Init();
	int Process(int nData);

protected:
	int m_buf[4];//x_derv[4]
};
