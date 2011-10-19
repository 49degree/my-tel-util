#include "ExtremumSTAnalyst.h"

CExtremumSTAnalyst::CExtremumSTAnalyst(void)
{
}

CExtremumSTAnalyst::~CExtremumSTAnalyst(void)
{
}


void CExtremumSTAnalyst::Init()
{
	//什么也不做，只是为了和其他分析器统一接口.
}

BOOL CExtremumSTAnalyst::Process(CRRWave &rrWave)
{
	int nLen = rrWave.m_nRRLen;
	rrWave.m_nTPos = 0;
	rrWave.m_nSTStartingPos = 0;
	rrWave.m_nSTEndingPos = 0;

	//计算基线：基线值 ＝ RR间期从1/2到3/4处的采样点Y值的平均值
	int nBaseline = 0;
	int chk1 = nLen / 2;
	int chk2 = 3 * nLen /4;
	for (int i = chk1; i <= chk2; ++i)
	{
		nBaseline += rrWave.m_data[i];
	}
	nBaseline = nBaseline / (chk2-chk1+1);
	rrWave.m_nBaseline = nBaseline;

	//找T点：即RR间期3/20处至2/5处最大的点
	chk1 = nLen * 3 / 20;
	chk2 = nLen * 2 / 5;
	int tempMax = 0;
	for (int i = chk1; i < chk2; ++i)
	{
		if (rrWave.m_data[i] > tempMax)
		{
			rrWave.m_nTPos = i;
			tempMax = rrWave.m_data[i];
		}
	}

	//求ST段的起点：
	//从RR间期开始处至RR间期的1/16处，取第一个Y值小于基线值的点N，
	//然后将点N后移5/2倍的距离后得到的点M作为ST段的起点
	chk1 = 0;
	chk2 = nLen / 16;
	for (int i = chk1; i < chk2; ++i)
	{
		if (rrWave.m_data[i] <= nBaseline)
		{
			rrWave.m_nSTStartingPos = (i+1) *5 / 2;
			break;
		}
		else
		{
			rrWave.m_nSTStartingPos = i;
		}
	}

	//求ST段终点：
	//设iTH为T波的高度， iLT为T波顶点与ST段起点的距离，M=iTH/iLT
	//若M<=2/5，则ST段的终点为ST起点与T波顶点距离的7/10处；
	//若2/5 < M < 3/4，则ST段的终点为St起点与T波顶点距离的6/10处；
	//若M >= 3/4，则ST段的终点为ST起点与T波顶点距离的5/10处；
	int iTH = rrWave.m_data[rrWave.m_nTPos] - nBaseline;
	int iLT = rrWave.m_nTPos - rrWave.m_nSTStartingPos + 1;

	rrWave.m_nSTEndingPos = rrWave.m_nSTStartingPos + iLT*6/10;

	if (iTH <= iLT*2/5)
	{
		rrWave.m_nSTEndingPos = rrWave.m_nSTStartingPos + iLT*7/10;
	}

	if (iTH >= iLT*3/4)
	{
		rrWave.m_nSTEndingPos =rrWave.m_nSTStartingPos + iLT*5/10;
	}

	return TRUE;
}
