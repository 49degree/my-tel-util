#include "LTSTAnalyst.h"
#include <math.h>

extern double CalDistance(CPoint pt, CPoint ptLinePt1, CPoint ptLinePt2);

CLTSTAnalyst::CLTSTAnalyst(void)
{
}

CLTSTAnalyst::~CLTSTAnalyst(void)
{
}

void CLTSTAnalyst::Init()
{
	//什么也不做，只是为了和其他分析器统一接口.
}

BOOL CLTSTAnalyst::Process(CRRWave &rrWave)
{
	if (0 == rrWave.m_nSamplFreq)
	{
		TRACE(_T("Invalid Sampling Frequence for CLTSTAnalyst::Process.\n") );
		return FALSE;
	}
	
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

	//求S点:R波顶点至R+50ms处的最小点
	chk1 = 0;
	chk2 = 50 / (1000/rrWave.m_nSamplFreq);
	int nSPos = chk2;
	int nTempMin = rrWave.m_data[chk2];
	for (int i = chk1; i <= chk2; ++i)
	{
		if (rrWave.m_data[i] < nTempMin)
		{
			nSPos = i;
		    nTempMin = rrWave.m_data[i];
		}
	}


	//求J点
	double dMaxDis = 0.0;
	CPoint ptMaxDis (0, 0);

	if (nSPos == chk2)//没有找到S波
	{
		//用局域变换的方法，计算从R点至R＋5０ms，与这两点确定的直接距离最远的点
		chk1 = 0;
		chk2 = 50 / (1000/rrWave.m_nSamplFreq);

		for (int i = chk1; i <= chk2; ++i)
		{
			double dTempDis = CalDistance(CPoint(i, rrWave.m_data[i]), 
				CPoint(chk1, rrWave.m_data[chk1]), CPoint(chk2, rrWave.m_data[chk2]) );

			if (dTempDis > dMaxDis)
			{
				dMaxDis = dTempDis;
				ptMaxDis = CPoint(i, rrWave.m_data[i]);
			}
		}

		rrWave.m_nSTStartingPos = ptMaxDis.x + (10 / (1000/rrWave.m_nSamplFreq) );//J点为最远的点后加30ms
		
	}
	else//找到S波
	{
		//求J点：得用局域变换的方法，计算从S点至R＋８０ms，与这两点确定的直接距离最远的点为J点
		chk1 = nSPos;
		CPoint ptS (chk1, rrWave.m_data[chk1] );	
		chk2 = 80 / (1000/rrWave.m_nSamplFreq);
		CPoint ptR80 (chk2, rrWave.m_data[chk2] );

		if (chk1 >= chk2 )
		{
			TRACE(_T("Invalid Point of Line for CLTSTAnalyst::CalDistance.\n") );
			return FALSE;
		}

		for (int i = chk1; i <= chk2; ++i)
		{
			double dTempDis = CalDistance(CPoint(i, rrWave.m_data[i]), ptS, ptR80);
			if (dTempDis > dMaxDis)
			{
				dMaxDis = dTempDis;
				ptMaxDis = CPoint(i, rrWave.m_data[i]);
			}
		}
		rrWave.m_nSTStartingPos = ptMaxDis.x;

	}
	
	//求ST段结束点
	rrWave.m_nSTEndingPos = ptMaxDis.x + (120 / (1000/rrWave.m_nSamplFreq) );//取J点后１２０ms处作为ST段结束点，为的是在计算ST段中间点时取到J+60ms


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

	////求ST段的起点——J点：
	////从RR间期开始处至RR间期的1/16处，取第一个Y值小于基线值的点N，
	////然后将点N后移5/2倍的距离后得到的点M作为ST段的起点
	//chk1 = 0;
	//chk2 = nLen / 16;
	//for (int i = chk1; i < chk2; ++i)
	//{
	//	if (rrWave.m_data[i] <= nBaseline)
	//	{
	//		rrWave.m_nSTStartingPos = (i+1) *5 / 2;
	//		break;
	//	}
	//	else
	//	{
	//		rrWave.m_nSTStartingPos = i;
	//	}
	//}

	////求ST段终点：
	////设iTH为T波的高度， iLT为T波顶点与ST段起点的距离，M=iTH/iLT
	////若M<=2/5，则ST段的终点为ST起点与T波顶点距离的7/10处；
	////若2/5 < M < 3/4，则ST段的终点为St起点与T波顶点距离的6/10处；
	////若M >= 3/4，则ST段的终点为ST起点与T波顶点距离的5/10处；
	//int iTH = rrWave.m_data[rrWave.m_nTPos] - nBaseline;
	//int iLT = rrWave.m_nTPos - rrWave.m_nSTStartingPos + 1;

	//rrWave.m_nSTEndingPos = rrWave.m_nSTStartingPos + iLT*6/10;

	//if (iTH <= iLT*2/5)
	//{
	//	rrWave.m_nSTEndingPos = rrWave.m_nSTStartingPos + iLT*7/10;
	//}

	//if (iTH >= iLT*3/4)
	//{
	//	rrWave.m_nSTEndingPos =rrWave.m_nSTStartingPos + iLT*5/10;
	//}

	return TRUE;
}

double CalDistance(CPoint pt, CPoint ptLinePt1, CPoint ptLinePt2)
{
	if (ptLinePt1.x==ptLinePt2.x && ptLinePt1.y==ptLinePt2.y)
	{
		TRACE(_T("Invalid Point of Line for CLTSTAnalyst::CalDistance.\n") );
		return 0;
	}
	//function:ax+by+c=0,calculate a,b,c 
	double a = ptLinePt2.y - ptLinePt1.y;//a = y2 - y1
	double b = ptLinePt1.x - ptLinePt2.x;//b = x1 - x2
	double c = (ptLinePt2.x*ptLinePt1.y) - (ptLinePt1.x*ptLinePt2.y);//c = x2*y1 - x1*y2

	//calculate distance
	double dDis = fabs(a*pt.x + b*pt.y + c) / sqrt(a*a+b*b);
	return dDis;
}
