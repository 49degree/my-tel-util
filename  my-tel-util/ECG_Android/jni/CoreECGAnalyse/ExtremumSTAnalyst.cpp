#include "ExtremumSTAnalyst.h"

CExtremumSTAnalyst::CExtremumSTAnalyst(void)
{
}

CExtremumSTAnalyst::~CExtremumSTAnalyst(void)
{
}


void CExtremumSTAnalyst::Init()
{
	//ʲôҲ������ֻ��Ϊ�˺�����������ͳһ�ӿ�.
}

BOOL CExtremumSTAnalyst::Process(CRRWave &rrWave)
{
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

	//��ST�ε���㣺
	//��RR���ڿ�ʼ����RR���ڵ�1/16����ȡ��һ��YֵС�ڻ���ֵ�ĵ�N��
	//Ȼ�󽫵�N����5/2���ľ����õ��ĵ�M��ΪST�ε����
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

	//��ST���յ㣺
	//��iTHΪT���ĸ߶ȣ� iLTΪT��������ST�����ľ��룬M=iTH/iLT
	//��M<=2/5����ST�ε��յ�ΪST�����T����������7/10����
	//��2/5 < M < 3/4����ST�ε��յ�ΪSt�����T����������6/10����
	//��M >= 3/4����ST�ε��յ�ΪST�����T����������5/10����
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
