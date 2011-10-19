#include "TompkinsAnalyst.h"
#include "LTSTAnalyst.h"

#define NORMAL_HEART_RATE_MIN			30
#define NORMAL_HEART_RATE_MAX			150
#define MIN_AVAILABLE_HEART_RATE		20
#define MAX_PACE_COUNT					20//TODO:这个宏所代表的时间会随着包内数据时长而改变

extern  void LOGD_INT(int intValue){
	char   csTemp[20];
	sprintf(csTemp,   "%d ",   intValue);
	LOGD(csTemp);
}

CTompkinsAnalyst::CTompkinsAnalyst(void) : m_STDDuration(0), 
										   m_STUDuration(0),
										   m_nCurHR(0),
										   m_dwPaceWaveCount(0),
										   m_nMaxHR(0),
										   m_nMinHR(1024)
{
	m_pSTAnalyst = new CLTSTAnalyst;
	this->Init();
}

CTompkinsAnalyst::~CTompkinsAnalyst(void)
{
	//释放动态生成的ST段分析器
	if (m_pSTAnalyst)
	{
		delete m_pSTAnalyst;
	}

	//释放列表中所有未释放的空间
	CDiagnoseResult *pTem;
	while (!m_resultList.empty() )
	{
		pTem = m_resultList.front();
		if (pTem)
		{
			delete pTem;
		}

		m_resultList.pop_front();
	}
}

//重新初始化
void CTompkinsAnalyst::Init()
{
	//若不是实时上传模式，才将对应的数据清零

	//m_nCurHR = 0;//重新初始化时不清空心率
	m_dwPaceWaveCount = 0;
	m_STDDuration = 0;
	m_STUDuration = 0;

	
	m_lowPassFilter.Init();
	m_highPassFilter.Init();
	m_differentiator.Init();
	m_slidingWindowIntegrator.Init();
	m_pSTAnalyst->Init();
	m_rWaveAnalyst.Init();
	m_rPosAnalyst.Init();

	m_nMinHR = 1024;
	m_nMaxHR = 0;
}

BOOL CTompkinsAnalyst::Analyse( BYTE *pData, UINT nLen, DWORD dwPacketCount, UINT nEcgFreq )
{
	LOGD("Analyse111");

	LOGD_INT(nEcgFreq);

	if (!pData || nLen == 0)
	{
		return FALSE;
	}

	LOGD("Analyse");

	bool bRWave = false;

	int nTemp = 0, nOriData = 0;
	for (UINT i = 0; i<nLen; ++i)
	{
		LOGD("i");
		LOGD_INT(i);

		nTemp = (*(pData+i) ) & 0xFF;
		nOriData = nTemp;

		//低通滤波
		nTemp = m_lowPassFilter.Process(nTemp);
		
		//高通滤波
		nTemp = m_highPassFilter.Process(nTemp);

		//微分
		nTemp = m_differentiator.Process(nTemp);

		//平方
		nTemp *= nTemp;

		//滑动窗口积分
		nTemp = m_slidingWindowIntegrator.Process(nTemp);

		//R波顶点检测
		m_rrWave.m_nSamplFreq = nEcgFreq;
		LOGD("nEcgFreq");
		LOGD_INT(nEcgFreq);

		if (/*m_rWaveAnalyst*/m_rPosAnalyst.Process(i, nTemp, nOriData, m_rrWave, dwPacketCount, nEcgFreq) )
		{
			LOGD("m_rPosAnalyst.Process");

			bRWave = true;//在这个包内检测到R波
			m_nCurHR = m_rrWave.m_nAvarageHR;

			//m_pSTAnalyst->Process(m_rrWave);//ST段分析

			//根据分析生成结果
			this->DoDiagnose(m_rrWave, nEcgFreq);

			//将已经分析过的数据移出RRWave的缓冲
			/*::memcpy(m_rrWave.m_data, (m_rrWave.m_data+m_rrWave.m_nRRLen),
				   m_rrWave.m_nDataLen-m_rrWave.m_nRRLen);
			m_rrWave.m_nDataLen -= m_rrWave.m_nRRLen;
			m_rrWave.m_nRRLen = m_rrWave.m_nDataLen;*/

		}
		
	}

	if (bRWave)
	{
		m_dwPaceWaveCount = 0;
	}
	else
	{
		++m_dwPaceWaveCount;

		if (MAX_PACE_COUNT <= m_dwPaceWaveCount)
		{
			m_nCurHR = 0;//连续好几个包没有检测到R波，则将心率设置为0
		}
	}
	

	return TRUE;
}

UINT CTompkinsAnalyst::GetResultSetCount()
{
//	return m_resultList.GetCount();
	return m_resultList.size();
}

CDiagnoseResult* CTompkinsAnalyst::GetResult()
{
	if (!m_resultList.size() )
	{
		return NULL;
	}

//	return m_resultList.RemoveHead();
	CDiagnoseResult* pTemp = m_resultList.front();
	m_resultList.pop_front();
	return pTemp;
}



BOOL CTompkinsAnalyst::DoDiagnose( CRRWave &rrWave, UINT nFreq )
{
	//心率检测--------------------------
	int hr = rrWave.m_nAvarageHR;

	if (hr < m_nMinHR && 0 != hr)
	{
		m_nMinHR = hr;
	}

	if (hr > m_nMaxHR)
	{
		m_nMaxHR = hr;
	}

	//TRACE(_T("当前心率：%d，最大心率：%d，最小心率：%d.\n"), hr, m_nMaxHR, m_nMinHR);

	if (hr >= MIN_AVAILABLE_HEART_RATE && hr < NORMAL_HEART_RATE_MIN)//心率过缓
	{
		CDiagnoseResult *pDiag = new CDiagnoseResult;
		pDiag->m_code = ET_LowHeartRate;
		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
		pDiag->m_nOffset =rrWave.m_nROffsetInPacket;
//		m_resultList.AddTail(pDiag);	
		m_resultList.push_back(pDiag);
	}

	if (hr > NORMAL_HEART_RATE_MAX )//心率过快
	{
		CDiagnoseResult *pDiag = new CDiagnoseResult;
		pDiag->m_code = ET_HighHeartRate;
		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
		pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
//		m_resultList.AddTail(pDiag);	
		m_resultList.push_back(pDiag);
	}

	////ST段检测----------------------------

	//int nSTHeight = (rrWave.m_data[rrWave.m_nSTStartingPos]) 
	//					- rrWave.m_nBaseline;//以ST的起点来判断上抬,下沉
	//float voltage = nSTHeight / 6.00f;//转换成真正的电压值

	//////Log分析结果------------------------------------------------------
	////static DWORD s_dwLastRPos = 0;
	////static bool  s_bIsFirstR = true;

	////ASSERT(rrWave.m_dwRPacketCout != 0);
	////DWORD dwRPos = rrWave.m_nROffsetInPacket + ( (rrWave.m_dwRPacketCout-1) * 184 );//hardcode!!!
	////DWORD dwPointInterval = dwRPos - 1;//点间隔 ＝ R点所在点 － 1
	////DWORD dwJPos = dwRPos - rrWave.m_nRRLen + rrWave.m_nSTStartingPos;
	////DWORD dwBaseLinePos = 777;
	////double dBLVal = (double)rrWave.m_nBaseline / 51.0 - 2.5;//将基线值换算成真实值

	////if (!s_bIsFirstR)
	////{
	////	CString csAnalysisInfo;
	////	csAnalysisInfo.Format(_T("%d %d %d %.3f"), s_dwLastRPos, dwJPos, dwBaseLinePos, dBLVal);
	////	BOOL bRet = m_loger.Log(IT_TRACE, csAnalysisInfo);
	////}
	////else
	////{
	////	s_bIsFirstR = false;
	////}

	////s_dwLastRPos = dwRPos;
	//

	//

	////检测ST段的偏移
	//if(voltage > 1.5) //ST上升，则无需重新计算ST上升的幅度，故直接对其进行判断是否超过1.5mv
	//{
	//	m_STDDuration = 0; //ST段下沉持续时间清零
	//	m_STUDuration += (rrWave.m_nRRLen *1000 / nFreq); //计算ST段上抬超过1.5mv的持续时间

	//	if (m_STUDuration >= ST_EVENT_DURATION)
	//	{
	//		m_STUDuration = 0;//重新开始计算上抬持续时间

	//		CDiagnoseResult *pDiag = new CDiagnoseResult;
	//		pDiag->m_code = ET_STElevation;
	//		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
	//		pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
	//		m_resultList.AddTail(pDiag);
	//	}
	//}
	//else if (voltage < 0) //ST下降，则需要重新计算偏移的幅度
	//{
	//	m_STUDuration = 0;//ST段上抬的持续时间清零

	//   //以ST段的中间点来测量点，计算ST段下沉的幅度
	//	int nSTMid = (rrWave.m_nSTStartingPos + rrWave.m_nSTEndingPos) / 2;

	//    nSTHeight = (rrWave.m_data[nSTMid]) - rrWave.m_nBaseline;
	//	voltage = nSTHeight / 6.00f;
	//	
	//	if (voltage < -1)//如果ST下沉幅度超过0.1mv，则认为有可能发生了心肌梗死，产生异常
	//	{
	//		m_STDDuration += (rrWave.m_nRRLen *1000 / nFreq); //计算ST段下沉超过0.1MV的持续时间

	//		if (m_STDDuration > ST_EVENT_DURATION)
	//		{
	//			m_STDDuration = 0;//重新开始计算下沉持续时间

	//			CDiagnoseResult *pDiag = new CDiagnoseResult;
	//			pDiag->m_code = ET_STDepression;
	//			pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
	//			pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
	//			m_resultList.AddTail(pDiag);

	//		}

	//	}
	//	else
	//	{
	//		m_STDDuration = 0; //ST段下沉的幅度不足1mv，持续时间清零
	//	}
	//}
	//else
	//{
	//	m_STUDuration = 0;//ST段上抬持续时间清零
	//	m_STDDuration = 0;//ST段下沉持续时间清零
	//}

		return TRUE;
}


UINT CTompkinsAnalyst::GetHeartRate()
{
	return m_nCurHR;
}
