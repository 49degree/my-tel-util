#pragma once
#include "DataType.h"
#include "LowPassFilter.h"
#include "HighPassFilter.h"
#include "Differentiator.h"
#include "SlidingWindowIntegrator.h"
#include "RWaveAnalyst.h"
#include "RPosAnalyser.h"
#include "STAnalyst.h"

#include <list>
using std::list;

#define ST_EVENT_DURATION 1*60*1000

class CTompkinsAnalyst// :
//	public CAnalyst
{
public:
	//构造
	CTompkinsAnalyst(void);
	~CTompkinsAnalyst(void);

	//重写
public:
	virtual BOOL Analyse(BYTE *pData, UINT nLen, DWORD dwPacketCount, UINT nEcgFreq);

	virtual UINT GetResultSetCount();

	virtual CDiagnoseResult* GetResult();

	virtual UINT GetHeartRate();

	virtual void Init();

	//成员函数
	BOOL DoDiagnose(CRRWave &rrWave, UINT nFreq);


	//成员变量
protected:
	UINT                                         m_nCurHR;                  //当前心率
//	CList<CDiagnoseResult *, CDiagnoseResult *>  m_resultList;				//分析结果集
	list<CDiagnoseResult *>						m_resultList;
	CLowPassFilter                               m_lowPassFilter;			//低通滤波器
	CHighPassFilter								 m_highPassFilter;			//高通滤波器
	CDifferentiator								 m_differentiator;			//微分器
	CSlidingWindowIntegrator					 m_slidingWindowIntegrator; //滑窗积分器
	CSTAnalyst									*m_pSTAnalyst;				//ST段分析器
	CRWaveAnalyst		 						 m_rWaveAnalyst;			//R波顶点检测器
	CRPosAnalyser								 m_rPosAnalyst;				//WEB波的R波分析器
	CRRWave									     m_rrWave;					//RR间期数据实体

	__time64_t									 m_STUDuration;				//ST段上抬持续时间
	__time64_t									 m_STDDuration;				//ST段下沉持续时间

	DWORD                                        m_dwPaceWaveCount;         //连续没有检测到R波的包个数

	vector<unsigned char>			     		 m_vecOriData;				//原始的心电数据

public:
	UINT										 m_nMaxHR;					//最大心率
	UINT										 m_nMinHR;					//最小心率
};
