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
	//����
	CTompkinsAnalyst(void);
	~CTompkinsAnalyst(void);

	//��д
public:
	virtual BOOL Analyse(BYTE *pData, UINT nLen, DWORD dwPacketCount, UINT nEcgFreq);

	virtual UINT GetResultSetCount();

	virtual CDiagnoseResult* GetResult();

	virtual UINT GetHeartRate();

	virtual void Init();

	//��Ա����
	BOOL DoDiagnose(CRRWave &rrWave, UINT nFreq);


	//��Ա����
protected:
	UINT                                         m_nCurHR;                  //��ǰ����
//	CList<CDiagnoseResult *, CDiagnoseResult *>  m_resultList;				//���������
	list<CDiagnoseResult *>						m_resultList;
	CLowPassFilter                               m_lowPassFilter;			//��ͨ�˲���
	CHighPassFilter								 m_highPassFilter;			//��ͨ�˲���
	CDifferentiator								 m_differentiator;			//΢����
	CSlidingWindowIntegrator					 m_slidingWindowIntegrator; //����������
	CSTAnalyst									*m_pSTAnalyst;				//ST�η�����
	CRWaveAnalyst		 						 m_rWaveAnalyst;			//R����������
	CRPosAnalyser								 m_rPosAnalyst;				//WEB����R��������
	CRRWave									     m_rrWave;					//RR��������ʵ��

	__time64_t									 m_STUDuration;				//ST����̧����ʱ��
	__time64_t									 m_STDDuration;				//ST���³�����ʱ��

	DWORD                                        m_dwPaceWaveCount;         //����û�м�⵽R���İ�����

	vector<unsigned char>			     		 m_vecOriData;				//ԭʼ���ĵ�����

public:
	UINT										 m_nMaxHR;					//�������
	UINT										 m_nMinHR;					//��С����
};
