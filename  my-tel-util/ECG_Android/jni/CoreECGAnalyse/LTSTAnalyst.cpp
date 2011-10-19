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
	//ʲôҲ������ֻ��Ϊ�˺�����������ͳһ�ӿ�.
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

	//������ߣ�����ֵ �� RR���ڴ�1/2��3/4���Ĳ�����Yֵ��ƽ��ֵ
	int nBaseline = 0;
	int chk1 = nLen / 2;
	int chk2 = 3 * nLen /4;
	for (int i = chk1; i <= chk2; ++i)
	{
		nBaseline += rrWave.m_data[i];
	}
	nBaseline = nBaseline / (chk2-chk1+1);
	rrWave.m_nBaseline = nBaseline;

	//��S��:R��������R+50ms������С��
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


	//��J��
	double dMaxDis = 0.0;
	CPoint ptMaxDis (0, 0);

	if (nSPos == chk2)//û���ҵ�S��
	{
		//�þ���任�ķ����������R����R��5��ms����������ȷ����ֱ�Ӿ�����Զ�ĵ�
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

		rrWave.m_nSTStartingPos = ptMaxDis.x + (10 / (1000/rrWave.m_nSamplFreq) );//J��Ϊ��Զ�ĵ���30ms
		
	}
	else//�ҵ�S��
	{
		//��J�㣺���þ���任�ķ����������S����R������ms����������ȷ����ֱ�Ӿ�����Զ�ĵ�ΪJ��
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
	
	//��ST�ν�����
	rrWave.m_nSTEndingPos = ptMaxDis.x + (120 / (1000/rrWave.m_nSamplFreq) );//ȡJ��󣱣���ms����ΪST�ν����㣬Ϊ�����ڼ���ST���м��ʱȡ��J+60ms


	//��T�㣺��RR����3/20����2/5�����ĵ�
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

	////��ST�ε���㡪��J�㣺
	////��RR���ڿ�ʼ����RR���ڵ�1/16����ȡ��һ��YֵС�ڻ���ֵ�ĵ�N��
	////Ȼ�󽫵�N����5/2���ľ����õ��ĵ�M��ΪST�ε����
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

	////��ST���յ㣺
	////��iTHΪT���ĸ߶ȣ� iLTΪT��������ST�����ľ��룬M=iTH/iLT
	////��M<=2/5����ST�ε��յ�ΪST�����T����������7/10����
	////��2/5 < M < 3/4����ST�ε��յ�ΪSt�����T����������6/10����
	////��M >= 3/4����ST�ε��յ�ΪST�����T����������5/10����
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
