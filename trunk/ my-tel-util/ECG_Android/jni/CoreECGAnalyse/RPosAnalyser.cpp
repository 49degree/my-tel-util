#include "RPosAnalyser.h"
#include "LTSTAnalyst.h"

/*/// <summary>
/// RR��������ֵ��������ֵ������Ϊ���ݲ����� 
/// </summary>
private const int MaxRRLength = 6000;

/// <summary>
/// RR��������ֵ��������ֵ������Ϊ���ݲ����� 
/// </summary>
private const int MinRRLength = 30;*/

#define  MAX_RR_LENGTH	6000
#define  MIN_RR_LENGTH  30

extern double CalDistance(CPoint pt, CPoint ptLinePt1, CPoint ptLinePt2);

extern  void LOGD_INT1(int intValue){
	char   csTemp[20];
	sprintf(csTemp,   "%d ",   intValue);
	LOGD(csTemp);
}

CRPosAnalyser::CRPosAnalyser(void) : EcgIndex(0),
									 IsSameWave(false),
									 CurrentRRIsInversion(false),
									 RSum(0),
									 CurrentMaxAmplitude(0),
									 CurrentMinAmplitude(255),
									 CurrentMaxAmplitudeIndex(0),
									 CurrentMinAmplitudeIndex(0),
									 CurrentRRIndex(0),
									 TopChordIndex(0),
									 m_index(0)
{
	for (int i = 0; i < 8; ++i)
	{
		m_aRRDistance[i] = 220;
	}
}


CRPosAnalyser::~CRPosAnalyser(void)
{
}

int CRPosAnalyser::abs(int   x)
{
	if(x <0) x=-x;
	return x;
}

void CRPosAnalyser::Init()
{
	EcgIndex = 0;
	IsSameWave = false;
	CurrentRRIsInversion = false;
	RSum = 0;
	CurrentMaxAmplitude = 0;
	CurrentMinAmplitude = 255;
	CurrentMaxAmplitudeIndex = 0;
	CurrentMinAmplitudeIndex = 0;
	CurrentRRIndex = 0;
	TopChordIndex = 0;
	m_index = 0;

	EcgData.clear();
	TopChordList.clear();
	RSum2.clear();

	m_lastRRWave.m_nRRLen = 0;
	m_lastRRWave.m_nRPos = 0;

}


/*
* �������ܣ�
*           
* ���������
*           
* ���������
*           
* �� �� ֵ��
*           
* ��    ע��
*           
*/
bool CRPosAnalyser::Process(int nXPos, 
							int nTempData, 
							int nOriData, 
							CRRWave &rrWave, 
							DWORD dwPacketCount, 
							UINT nEcgFreq)
{
	//lazy-init
	Frequency = nEcgFreq;
	RLen = nEcgFreq * 8;
	rrWave.m_nSamplFreq = nEcgFreq;
	m_lastRRWave.m_nSamplFreq = nEcgFreq;

	//save original data
	EcgData.push_back( (unsigned char)nOriData);

	//�ж��Ƿ����R��
	bool bRet = false;
	float averge = 0;
	RSum += nTempData;
	RSum2.push_back(nTempData);
	LOGD("EcgIndex");
	LOGD_INT1(EcgIndex);
	LOGD_INT1(RLen);
	if (EcgIndex >= RLen)
	{
		averge = RSum / RLen / 2; //ȡƽ��ֵ�İٷ���ʮ
		float fOld = RSum2.front();/*data = RSum2.Dequeue();*/
		RSum2.pop_front();
		RSum -= fOld;

		LOGD("rrWave");

		bRet = FindRRWave(EcgIndex - RLen, fOld, averge, rrWave, dwPacketCount);

		//����RLen����û�б�����------------
		////���ڵ����
		//if (EcgIndex == EcgData.size() - 1)
		//{
		//	int count = RSum2.size();
		//	for (int i = 1; i <= count; i++)
		//	{
		//		/*data = RSum2.Dequeue();
		//		Caculate(EcgIndex - RLen + i, data, averge);*/
		//		float fOld = RSum2.front();
		//		RSum2.pop_front();

		//		bRet = FindRRWave(EcgIndex - RLen + i, fOld, averge);
		//	}
		//}
	}
	EcgIndex++;

	return bRet;
}

bool CRPosAnalyser::FindRRWave( int nXPos, float nTempData, float fThreshold, CRRWave& rrWave, DWORD dwPackCount )
{
	bool bRet = false;

	if (nTempData > fThreshold)//������
	{
		if (!IsSameWave)//�ֲ��ڵ�һ��������
		{
			TopChordList.push_back(nXPos);
			IsSameWave = true;
		}
	}
	else//�½���
	{
		IsSameWave = false;  //y6

		int nSize = TopChordList.size();
		if (TopChordIndex < TopChordList.size() && nXPos > TopChordList[TopChordIndex])
		{//�����ֵ

			for (int i = 2 * TopChordList[TopChordIndex] - nXPos >= 0 ? 2 * TopChordList[TopChordIndex] - nXPos : 0;
				i <= TopChordList[TopChordIndex]; i++)
			{
				int EcgValue = EcgData[i];
				if (EcgValue > CurrentMaxAmplitude)
				{//�������ֵ
					CurrentMaxAmplitude = EcgValue;
					CurrentMaxAmplitudeIndex = i;
				}
				if (EcgValue < CurrentMinAmplitude)
				{//������Сֵ
					CurrentMinAmplitude = EcgValue;
					CurrentMinAmplitudeIndex = i;
				}

				if (m_lastRRWave.m_nRRLen != 0)//���ǵ�һ��R��
				{
					//ȷ��R�������㻹����С��
					CurrentRRIsInversion = abs(CurrentMinAmplitude - m_lastRRWave.m_nBaseline) > 2 * abs(CurrentMaxAmplitude - m_lastRRWave.m_nBaseline);

					if (CurrentRRIsInversion)
					{
						CurrentRRIndex = CurrentMinAmplitudeIndex;
					}
					else
					{
						CurrentRRIndex = CurrentMaxAmplitudeIndex;
					}
				}
				else
				{
					CurrentRRIsInversion = false;
					CurrentRRIndex = CurrentMaxAmplitudeIndex;
				}
			}

			bRet = ConfirmRPos(CurrentRRIndex, CurrentRRIsInversion, rrWave, dwPackCount);
			CurrentMaxAmplitude = 0;
			CurrentMinAmplitude = 255;
			TopChordIndex++;
		}
	}

	return bRet;
}

/*
* �������ܣ�
*           �ҵ�R��ʱ����֮ǰ��R���������γ�һ��RR����
* ���������
*           
* ���������
*           
* �� �� ֵ��
*           
* ��    ע��
*           
*/
bool CRPosAnalyser::ConfirmRPos( int nRPos, bool bIsInversion, CRRWave &rr, DWORD dwPackCount )
{
	//ͷ�����ҵ���R��ͨ���ǲ����ģ�����֮
	if (m_lastRRWave.m_nRPos == 0)
	{//��һ���ҵ���R�㣬��ʼ��PrevRR����
		if (nRPos == 0) 
		{
			return false;
		}

		m_lastRRWave.m_nRPos = nRPos;
		m_lastRRWave.m_nBaseline = 127;
		return false;
	}
	else if (nRPos - m_lastRRWave.m_nRPos == 0)//R��λ���غ�
	{
		return false; 
	}
	else if (nRPos - m_lastRRWave.m_nRPos - m_lastRRWave.m_nRRLen <= 0)//�ҵ���R������һ��RR������ 
	{
		return false;
	}





	if (m_lastRRWave.m_nRRLen < MIN_RR_LENGTH)
	{//����RR���ڣ������Ӳ���ͬʱ�ϲ�����
		rr = m_lastRRWave;
		m_lastRRWave.m_bIsNormal = false;
	}
	else
	{
		rr.m_nRPos = m_lastRRWave.m_nRPos + m_lastRRWave.m_nRRLen;
	}
	rr.m_bIsInversion = bIsInversion;
	rr.m_nRRLen = nRPos - rr.m_nRPos;
	rr.m_nAvarageHR = rr.m_nSamplFreq * 60 / rr.m_nRRLen;


	LOGD("m_nRRLen");
	LOGD_INT1(rr.m_nRRLen);
	LOGD_INT1(MAX_RR_LENGTH);

	if (rr.m_nRRLen > MAX_RR_LENGTH) //�����쳣����
	{
		rr.m_bIsNormal = false;
		rr.m_nBaseline = 127;
	}
	else //���쳣���ݽ��� RR���ڷ��� 
	{
		//TRACE(_T("RRLen : %.2f ms.\n"), ( (float)rr.m_nRRLen * 1000 / (float)rr.m_nSamplFreq));
		rr.m_dwRPacketCout = dwPackCount;

		//����ƽ������
		int sum = 0;

		m_aRRDistance[m_index++] = rr.m_nRRLen;
		if (m_index >= 8)
		{
			m_index = 0;
		}

		for (int i = 0; i < 8; ++i)
		{
			sum += m_aRRDistance[i];
		}

		LOGD("sum");
		LOGD_INT1(sum);


		if (0 == sum)
		{
			rr.m_nAvarageHR = 0;
		}
		else
		{
			rr.m_nAvarageHR = 60 * 8 * rr.m_nSamplFreq / sum;
		}

		/*if (rr.m_nAvarageHR > 150 )
		{
			TRACE(_T("Find Heart Rate over speed.\n") );
		}
		TRACE(_T("RRLen:%d ms.\n"), rr.m_nRRLen*1000/300);
		if (rr.m_nRRLen < 100)
		{
			TRACE(_T("") );
		}*/


		//RR���ڷ���
		int nLen = min(rr.m_nRRLen, RRWAVE_BUF_SIZE);
		for (int i = 0; i < nLen; ++i)
		{
			(rr.m_data)[i] = EcgData[i];
		}
		AnalyseRR(rr.m_data, rr);//����RR���ڷ���
	}

	m_lastRRWave = rr;

	return true;
}

void CRPosAnalyser::AnalyseRR( unsigned char *pData, CRRWave &rr )
{
	float baseline = 0;//����
	int len = rr.m_nRRLen;//���ݳ���
	int p1, p2;

	//��Q�����㣺�� RR����β�� �� RR����2/3 ��Ѱ�Ҿ���������ȷ����ֱ�Ӿ�����Զ�Լ�Yֵ��С�ĵ�
	p1 = len * 2 / 3;
	p2 = len - 1;
	if (rr.m_bIsInversion)
	{
		//��Q�����㣺����R�����ã���Ѱ�Ҹ��������ߵ�
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 0, pData[p2], -1, false);
	}
	else
	{
		//��Q�����㣺Ѱ�Ҹ��������͵�
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 0, -1, pData[p2], false);
	}


	//��P�����㣺�� Q������ �� RR����68/100 ��Ѱ��Yֵ����

	p1 = len * 68 / 100;
	if (p1 < rr.m_nQStartPos)
	{
		rr.m_nPPos = p1;
		p2 = rr.m_nQStartPos;
		for (int i = p1; i < p2; i++)
		{
			if (pData[i] > pData[rr.m_nPPos])
			{
				rr.m_nPPos = i;
			}
		}
	}
	else
	{
		rr.m_nPPos = rr.m_nQStartPos;
	}


	//��Q����ʼ�㣺�� Q������ - 80ms �� Q������ Ѱ��������ȷ����ֱ�Ӿ�����Զ�Լ�Yֵ���ĵ�

	p1 =( (rr.m_nQStartPos - (rr.m_nSamplFreq * 80 / 1000) ) > 0 ) ? rr.m_nQStartPos - (rr.m_nSamplFreq * 80 / 1000) : rr.m_nPPos;
	p2 = rr.m_nQStartPos;
	if (p1 < 0)
	{
		TRACE(_T("Here.\n") );
	}
	if (rr.m_bIsInversion)
	{//R������
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 1, -1, 255, false);
	}
	else
	{//R������
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 1, pData[p2], -1, false);
	}

   //������ߣ���Q����ʼ�� - 50ms �� Q����ʼ�� ֮��Ĳ�����Yֵ��ƽ��ֵ

	p1 = rr.m_nQStartPos - (rr.m_nSamplFreq * 50 / 1000) > 0 ? rr.m_nQStartPos - (rr.m_nSamplFreq * 50 / 1000) : rr.m_nPPos;
	p2 = rr.m_nQStartPos;
	//���㱾���ڵĻ���
	for (int i = p1; i <= p2; i++)
	{
		baseline += pData[i];
	}

	baseline = baseline * 1.0 / (p2 - p1 + 1);
	rr.m_nBaseline = (unsigned char)baseline;

	////��T������: RR����3/20 �� RR����7/10 ֮������ֵ

	//p1 = len * 3 / 20;
	//p2 = len * 7 / 10;
	//rr.TPos = p1;
	//for (int i = p1; i < p2; i++)
	//{
	//	if (data[i] > data[rr.TPos])
	//	{
	//		rr.TPos = i;
	//	}
	//}


	////��S������: R������ �� RR����2/5��������С��--������ȷ����ֱ�Ӿ�����Զ�Լ�Yֵ��С�ĵ�

	//p1 = 0;
	//p2 = rr.HR < 80 ? this.Frequency / 5 : this.Frequency * 150 / 1000;
	//p2 = p2 >= len ? len - 1 : p2;
	//if (PrevRR.IsInversion)
	//{
	//	//R������
	//	rr.SPos = GetMaxDistanceIndex(data, p1, p2, 1, data[p1], -1, true);
	//}
	//else
	//{
	//	//R������
	//	rr.SPos = GetMaxDistanceIndex(data, p1, p2, 1, -1, byte.MaxValue, true);
	//}


	////��J�㣺�����S����S��140ms����������ȷ����ֱ�Ӿ�����Զ�ĵ�ΪJ��

	//if (PrevRR.IsInversion)
	//{
	//	//R������
	//	rr.JPos = rr.SPos;
	//}
	//else
	//{
	//	//R������
	//	p1 = rr.SPos;
	//	p2 = (rr.SPos + this.Frequency * 140 / 1000) >= len ? len - 1 : (rr.SPos + this.Frequency * 140 / 1000);
	//	rr.JPos = GetMaxDistanceIndex(data, p1, p2, 1, data[rr.SPos], -1, true);
	//}


	////��L��(��ST�ν�����)

	//int stOff = 0;
	//int iTH = data[rr.TPos] - rr.BaseY;
	//int iLT = rr.TPos - rr.JPos + 1;

	//stOff = rr.JPos + (iLT * 3) / 5;
	//if (iTH <= iLT * 2 / 5)
	//{
	//	stOff = rr.JPos + iLT * 7 / 10;
	//}
	//else if (iTH >= iLT * 3 / 4)
	//{
	//	stOff = rr.JPos + iLT / 2;
	//}
	//rr.LPos = stOff;


	////��I�㣺R��֮��ĵ�һ���յ�

	////��R��������RR����1/2��������С��
	//if (PrevRR.IsInversion)
	//{
	//	rr.IPos = rr.JPos;
	//}
	//else
	//{
	//	rr.IPos = p1 = 0;
	//	p2 = len * 1 / 2;
	//	for (int i = p1; i < p2; i++)
	//	{
	//		if (data[i] < data[rr.IPos])
	//			rr.IPos = i;
	//	}

	//	//��I�㣺���þ���任�ķ����������I����I��150ms����������ȷ����ֱ�Ӿ�����Զ�ĵ�ΪI��
	//	p1 = rr.IPos;
	//	p2 = (rr.IPos + this.Frequency * 150 / 1000) >= len ? len - 1 : (rr.IPos + this.Frequency * 150 / 1000);
	//	rr.IPos = GetMaxDistanceIndex(data, p1, p2, 1, data[p1], -1, true);
	//}

	////T���Ƿ���
	//rr.IsTReverse = (data[rr.JPos] - data[stOff]) > 0;

	////����ֵ
	//float stHeight;
	//if (data[rr.JPos] < rr.BaseY)
	//{
	//	int stPos = (rr.JPos + stOff) / 2;
	//	stHeight = data[stPos] - rr.BaseY;
	//}
	//else
	//{
	//	stHeight = data[rr.JPos] - rr.BaseY;
	//}

	//rr.TY = data[rr.TPos];
	//rr.PFlag = data[rr.PPos] > rr.BaseY;
	//rr.STSlope = (data[rr.LPos] - data[rr.JPos]) * 1f / (rr.LPos - rr.JPos);
	//rr.STValue = stHeight / 51F;
}

/// <summary>
/// Ѱ�� �� p1��p2���ɵ�ֱ�� ֱ�Ӿ������ĵ�
/// </summary>
/// <param name="data">��������</param>
/// <param name="p1">���</param>
/// <param name="p2">�յ�</param>
/// <param name="minDis">��С����</param>
/// <param name="top">�Ƿ�ֻ�ܳ��������ơ�ֵΪ-1ʱ����</param>
/// <param name="buttom">�Ƿ�ֻ�ܳ��½����ơ�ֵΪ-1ʱ����</param>
/// <param name="isAsc">��������</param>
/// <returns>�� �����������е�����</returns>
int CRPosAnalyser::GetMaxDistanceIndex(vector<unsigned char> &data, 
									   int p1, 
									   int p2, 
									   double minDis, 
									   int top, 
									   int buttom, 
									   bool isAsc)
{
	bool hasTop = (top != -1);
	bool hasButtom = (buttom != -1);

	if (hasTop && hasButtom)
	{
		TRACE(_T("Can't has two kinds of trends at the same time!") );
		return -1;
	}

	int index = -1;
	double maxDis = minDis;
	if (isAsc)
	{
		index = p1;
		for (int i = p1; i < p2; i++)
		{
			//��Index
			double tempDis = CalDistance(CPoint(i, data[i]), CPoint(p1, data[p1]),  CPoint(p2, data[p2]) );
			//double tempDis = 0;
			if (hasTop)
			{
				if (data[i] > top && tempDis > maxDis)
				{
					maxDis = tempDis;
					top = data[i];
					index = i;
				}
			}
			else if (hasButtom)
			{
				if (data[i] < buttom && tempDis > maxDis)
				{
					maxDis = tempDis;
					buttom = data[i];
					index = i;
				}
			}
			else if (tempDis > maxDis)
			{
				maxDis = tempDis;
				index = i;
			}
		}
	}
	else
	{
		index = p2;
		for (int i = p2; i > p1; i--)
		{
			//��Index
			double tempDis = CalDistance(CPoint(i, data[i]), CPoint(p1, data[p1]), CPoint(p2, data[p2]) );
			//double tempDis = 0;
			if (hasTop)
			{
				if (data[i] > top && tempDis > maxDis)
				{
					maxDis = tempDis;
					top = data[i];
					index = i;
				}
			}
			else if (hasButtom)
			{
				if (data[i] < buttom && tempDis > maxDis)
				{
					maxDis = tempDis;
					buttom = data[i];
					index = i;
				}
			}
			else if (tempDis > maxDis)
			{
				maxDis = tempDis;
				index = i;
			}
		}
	}
	return index;
}
