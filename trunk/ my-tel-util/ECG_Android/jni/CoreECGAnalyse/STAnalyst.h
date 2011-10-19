#pragma once
#include "DataType.h"

class CSTAnalyst
{
public:
	virtual ~CSTAnalyst(void) = 0 ;

public:
	virtual void Init() = 0;
	virtual BOOL Process(CRRWave &rrWave) = 0;
};
