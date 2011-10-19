#include "RPosAnalyser.h"
#include "LTSTAnalyst.h"

/*/// <summary>
/// RR间期上限值，超过此值可以认为数据不正常 
/// </summary>
private const int MaxRRLength = 6000;

/// <summary>
/// RR间期下限值，超过此值可以认为数据不正常 
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
* 函数介绍：
*           
* 输入参数：
*           
* 输出参数：
*           
* 返 回 值：
*           
* 备    注：
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

	//判定是否存在R点
	bool bRet = false;
	float averge = 0;
	RSum += nTempData;
	RSum2.push_back(nTempData);
	LOGD("EcgIndex");
	LOGD_INT1(EcgIndex);
	LOGD_INT1(RLen);
	if (EcgIndex >= RLen)
	{
		averge = RSum / RLen / 2; //取平均值的百分五十
		float fOld = RSum2.front();/*data = RSum2.Dequeue();*/
		RSum2.pop_front();
		RSum -= fOld;

		LOGD("rrWave");

		bRet = FindRRWave(EcgIndex - RLen, fOld, averge, rrWave, dwPacketCount);

		//会有RLen个点没有被分析------------
		////窗口到最后
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

	if (nTempData > fThreshold)//上升点
	{
		if (!IsSameWave)//局部内第一个上升点
		{
			TopChordList.push_back(nXPos);
			IsSameWave = true;
		}
	}
	else//下降点
	{
		IsSameWave = false;  //y6

		int nSize = TopChordList.size();
		if (TopChordIndex < TopChordList.size() && nXPos > TopChordList[TopChordIndex])
		{//计算峰值

			for (int i = 2 * TopChordList[TopChordIndex] - nXPos >= 0 ? 2 * TopChordList[TopChordIndex] - nXPos : 0;
				i <= TopChordList[TopChordIndex]; i++)
			{
				int EcgValue = EcgData[i];
				if (EcgValue > CurrentMaxAmplitude)
				{//保留最大值
					CurrentMaxAmplitude = EcgValue;
					CurrentMaxAmplitudeIndex = i;
				}
				if (EcgValue < CurrentMinAmplitude)
				{//保留最小值
					CurrentMinAmplitude = EcgValue;
					CurrentMinAmplitudeIndex = i;
				}

				if (m_lastRRWave.m_nRRLen != 0)//不是第一个R波
				{
					//确定R点是最大点还是最小点
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
* 函数介绍：
*           找到R点时，与之前的R波结束点形成一个RR间期
* 输入参数：
*           
* 输出参数：
*           
* 返 回 值：
*           
* 备    注：
*           
*/
bool CRPosAnalyser::ConfirmRPos( int nRPos, bool bIsInversion, CRRWave &rr, DWORD dwPackCount )
{
	//头两个找到的R点通常是不符的，忽略之
	if (m_lastRRWave.m_nRPos == 0)
	{//第一个找到的R点，初始化PrevRR返回
		if (nRPos == 0) 
		{
			return false;
		}

		m_lastRRWave.m_nRPos = nRPos;
		m_lastRRWave.m_nBaseline = 127;
		return false;
	}
	else if (nRPos - m_lastRRWave.m_nRPos == 0)//R点位置重合
	{
		return false; 
	}
	else if (nRPos - m_lastRRWave.m_nRPos - m_lastRRWave.m_nRRLen <= 0)//找到的R波在上一个RR间期内 
	{
		return false;
	}





	if (m_lastRRWave.m_nRRLen < MIN_RR_LENGTH)
	{//超短RR间期，当作杂波，同时合并处理
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

	if (rr.m_nRRLen > MAX_RR_LENGTH) //超长异常波形
	{
		rr.m_bIsNormal = false;
		rr.m_nBaseline = 127;
	}
	else //非异常数据进行 RR间期分析 
	{
		//TRACE(_T("RRLen : %.2f ms.\n"), ( (float)rr.m_nRRLen * 1000 / (float)rr.m_nSamplFreq));
		rr.m_dwRPacketCout = dwPackCount;

		//计算平均心率
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


		//RR间期分析
		int nLen = min(rr.m_nRRLen, RRWAVE_BUF_SIZE);
		for (int i = 0; i < nLen; ++i)
		{
			(rr.m_data)[i] = EcgData[i];
		}
		AnalyseRR(rr.m_data, rr);//进行RR间期分析
	}

	m_lastRRWave = rr;

	return true;
}

void CRPosAnalyser::AnalyseRR( unsigned char *pData, CRRWave &rr )
{
	float baseline = 0;//基线
	int len = rr.m_nRRLen;//数据长度
	int p1, p2;

	//求Q波顶点：从 RR间期尾端 到 RR间期2/3 处寻找距离这两点确定的直接距离最远以及Y值最小的点
	p1 = len * 2 / 3;
	p2 = len - 1;
	if (rr.m_bIsInversion)
	{
		//求Q波顶点：由于R波倒置，则寻找该区间的最高点
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 0, pData[p2], -1, false);
	}
	else
	{
		//求Q波顶点：寻找该区间的最低点
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 0, -1, pData[p2], false);
	}


	//求P波顶点：从 Q波顶点 到 RR间期68/100 处寻找Y值最大点

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


	//求Q波起始点：从 Q波顶点 - 80ms 到 Q波顶点 寻找这两点确定的直接距离最远以及Y值最大的点

	p1 =( (rr.m_nQStartPos - (rr.m_nSamplFreq * 80 / 1000) ) > 0 ) ? rr.m_nQStartPos - (rr.m_nSamplFreq * 80 / 1000) : rr.m_nPPos;
	p2 = rr.m_nQStartPos;
	if (p1 < 0)
	{
		TRACE(_T("Here.\n") );
	}
	if (rr.m_bIsInversion)
	{//R波倒置
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 1, -1, 255, false);
	}
	else
	{//R波正向
		rr.m_nQStartPos = GetMaxDistanceIndex(EcgData, p1, p2, 1, pData[p2], -1, false);
	}

   //计算基线：从Q波起始点 - 50ms 到 Q波起始点 之间的采样点Y值的平均值

	p1 = rr.m_nQStartPos - (rr.m_nSamplFreq * 50 / 1000) > 0 ? rr.m_nQStartPos - (rr.m_nSamplFreq * 50 / 1000) : rr.m_nPPos;
	p2 = rr.m_nQStartPos;
	//计算本周期的基线
	for (int i = p1; i <= p2; i++)
	{
		baseline += pData[i];
	}

	baseline = baseline * 1.0 / (p2 - p1 + 1);
	rr.m_nBaseline = (unsigned char)baseline;

	////求T波顶点: RR间期3/20 到 RR间期7/10 之间的最大值

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


	////求S波顶点: R波顶点 至 RR间期2/5到处的最小点--这两点确定的直接距离最远以及Y值最小的点

	//p1 = 0;
	//p2 = rr.HR < 80 ? this.Frequency / 5 : this.Frequency * 150 / 1000;
	//p2 = p2 >= len ? len - 1 : p2;
	//if (PrevRR.IsInversion)
	//{
	//	//R波倒置
	//	rr.SPos = GetMaxDistanceIndex(data, p1, p2, 1, data[p1], -1, true);
	//}
	//else
	//{
	//	//R波正向
	//	rr.SPos = GetMaxDistanceIndex(data, p1, p2, 1, -1, byte.MaxValue, true);
	//}


	////求J点：计算从S点至S＋140ms，与这两点确定的直接距离最远的点为J点

	//if (PrevRR.IsInversion)
	//{
	//	//R波倒置
	//	rr.JPos = rr.SPos;
	//}
	//else
	//{
	//	//R波正向
	//	p1 = rr.SPos;
	//	p2 = (rr.SPos + this.Frequency * 140 / 1000) >= len ? len - 1 : (rr.SPos + this.Frequency * 140 / 1000);
	//	rr.JPos = GetMaxDistanceIndex(data, p1, p2, 1, data[rr.SPos], -1, true);
	//}


	////求L点(即ST段结束点)

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


	////求I点：R波之后的第一个拐点

	////求R波顶点至RR间期1/2到处的最小点
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

	//	//求I点：得用局域变换的方法，计算从I点至I＋150ms，与这两点确定的直接距离最远的点为I点
	//	p1 = rr.IPos;
	//	p2 = (rr.IPos + this.Frequency * 150 / 1000) >= len ? len - 1 : (rr.IPos + this.Frequency * 150 / 1000);
	//	rr.IPos = GetMaxDistanceIndex(data, p1, p2, 1, data[p1], -1, true);
	//}

	////T波是否反向
	//rr.IsTReverse = (data[rr.JPos] - data[stOff]) > 0;

	////特征值
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
/// 寻找 到 p1与p2构成的直线 直接距离最大的点
/// </summary>
/// <param name="data">数据数组</param>
/// <param name="p1">起点</param>
/// <param name="p2">终点</param>
/// <param name="minDis">最小距离</param>
/// <param name="top">是否只能呈上升趋势。值为-1时忽略</param>
/// <param name="buttom">是否只能呈下降趋势。值为-1时忽略</param>
/// <param name="isAsc">遍历方向</param>
/// <returns>点 在数据数组中的索引</returns>
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
			//找Index
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
			//找Index
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
