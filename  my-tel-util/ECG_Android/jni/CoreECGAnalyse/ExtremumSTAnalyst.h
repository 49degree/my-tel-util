#pragma once
#include "STAnalyst.h"
#include "DataType.h"
class CExtremumSTAnalyst :
	public CSTAnalyst
{
public:
	CExtremumSTAnalyst();
	~CExtremumSTAnalyst();

public:
	virtual void Init();
	virtual BOOL Process(CRRWave &rrWave);
};
