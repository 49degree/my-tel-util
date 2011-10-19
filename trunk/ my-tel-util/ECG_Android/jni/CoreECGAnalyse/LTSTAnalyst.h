#pragma once
#include "STAnalyst.h"
#include "DataType.h"


class CLTSTAnalyst :
	public CSTAnalyst
{
public:
	CLTSTAnalyst(void);
	~CLTSTAnalyst(void);

public:
	virtual void Init();
	virtual BOOL Process(CRRWave &rrWave);

};
