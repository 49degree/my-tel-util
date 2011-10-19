#include "TompkinsAnalyst.h"
#include "LTSTAnalyst.h"

#define NORMAL_HEART_RATE_MIN			30
#define NORMAL_HEART_RATE_MAX			150
#define MIN_AVAILABLE_HEART_RATE		20
#define MAX_PACE_COUNT					20//TODO:������������ʱ������Ű�������ʱ�����ı�

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
	//�ͷŶ�̬���ɵ�ST�η�����
	if (m_pSTAnalyst)
	{
		delete m_pSTAnalyst;
	}

	//�ͷ��б�������δ�ͷŵĿռ�
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

//���³�ʼ��
void CTompkinsAnalyst::Init()
{
	//������ʵʱ�ϴ�ģʽ���Ž���Ӧ����������

	//m_nCurHR = 0;//���³�ʼ��ʱ���������
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

		//��ͨ�˲�
		nTemp = m_lowPassFilter.Process(nTemp);
		
		//��ͨ�˲�
		nTemp = m_highPassFilter.Process(nTemp);

		//΢��
		nTemp = m_differentiator.Process(nTemp);

		//ƽ��
		nTemp *= nTemp;

		//�������ڻ���
		nTemp = m_slidingWindowIntegrator.Process(nTemp);

		//R��������
		m_rrWave.m_nSamplFreq = nEcgFreq;
		LOGD("nEcgFreq");
		LOGD_INT(nEcgFreq);

		if (/*m_rWaveAnalyst*/m_rPosAnalyst.Process(i, nTemp, nOriData, m_rrWave, dwPacketCount, nEcgFreq) )
		{
			LOGD("m_rPosAnalyst.Process");

			bRWave = true;//��������ڼ�⵽R��
			m_nCurHR = m_rrWave.m_nAvarageHR;

			//m_pSTAnalyst->Process(m_rrWave);//ST�η���

			//���ݷ������ɽ��
			this->DoDiagnose(m_rrWave, nEcgFreq);

			//���Ѿ��������������Ƴ�RRWave�Ļ���
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
			m_nCurHR = 0;//�����ü�����û�м�⵽R��������������Ϊ0
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
	//���ʼ��--------------------------
	int hr = rrWave.m_nAvarageHR;

	if (hr < m_nMinHR && 0 != hr)
	{
		m_nMinHR = hr;
	}

	if (hr > m_nMaxHR)
	{
		m_nMaxHR = hr;
	}

	//TRACE(_T("��ǰ���ʣ�%d��������ʣ�%d����С���ʣ�%d.\n"), hr, m_nMaxHR, m_nMinHR);

	if (hr >= MIN_AVAILABLE_HEART_RATE && hr < NORMAL_HEART_RATE_MIN)//���ʹ���
	{
		CDiagnoseResult *pDiag = new CDiagnoseResult;
		pDiag->m_code = ET_LowHeartRate;
		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
		pDiag->m_nOffset =rrWave.m_nROffsetInPacket;
//		m_resultList.AddTail(pDiag);	
		m_resultList.push_back(pDiag);
	}

	if (hr > NORMAL_HEART_RATE_MAX )//���ʹ���
	{
		CDiagnoseResult *pDiag = new CDiagnoseResult;
		pDiag->m_code = ET_HighHeartRate;
		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
		pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
//		m_resultList.AddTail(pDiag);	
		m_resultList.push_back(pDiag);
	}

	////ST�μ��----------------------------

	//int nSTHeight = (rrWave.m_data[rrWave.m_nSTStartingPos]) 
	//					- rrWave.m_nBaseline;//��ST��������ж���̧,�³�
	//float voltage = nSTHeight / 6.00f;//ת���������ĵ�ѹֵ

	//////Log�������------------------------------------------------------
	////static DWORD s_dwLastRPos = 0;
	////static bool  s_bIsFirstR = true;

	////ASSERT(rrWave.m_dwRPacketCout != 0);
	////DWORD dwRPos = rrWave.m_nROffsetInPacket + ( (rrWave.m_dwRPacketCout-1) * 184 );//hardcode!!!
	////DWORD dwPointInterval = dwRPos - 1;//���� �� R�����ڵ� �� 1
	////DWORD dwJPos = dwRPos - rrWave.m_nRRLen + rrWave.m_nSTStartingPos;
	////DWORD dwBaseLinePos = 777;
	////double dBLVal = (double)rrWave.m_nBaseline / 51.0 - 2.5;//������ֵ�������ʵֵ

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

	////���ST�ε�ƫ��
	//if(voltage > 1.5) //ST���������������¼���ST�����ķ��ȣ���ֱ�Ӷ�������ж��Ƿ񳬹�1.5mv
	//{
	//	m_STDDuration = 0; //ST���³�����ʱ������
	//	m_STUDuration += (rrWave.m_nRRLen *1000 / nFreq); //����ST����̧����1.5mv�ĳ���ʱ��

	//	if (m_STUDuration >= ST_EVENT_DURATION)
	//	{
	//		m_STUDuration = 0;//���¿�ʼ������̧����ʱ��

	//		CDiagnoseResult *pDiag = new CDiagnoseResult;
	//		pDiag->m_code = ET_STElevation;
	//		pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
	//		pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
	//		m_resultList.AddTail(pDiag);
	//	}
	//}
	//else if (voltage < 0) //ST�½�������Ҫ���¼���ƫ�Ƶķ���
	//{
	//	m_STUDuration = 0;//ST����̧�ĳ���ʱ������

	//   //��ST�ε��м���������㣬����ST���³��ķ���
	//	int nSTMid = (rrWave.m_nSTStartingPos + rrWave.m_nSTEndingPos) / 2;

	//    nSTHeight = (rrWave.m_data[nSTMid]) - rrWave.m_nBaseline;
	//	voltage = nSTHeight / 6.00f;
	//	
	//	if (voltage < -1)//���ST�³����ȳ���0.1mv������Ϊ�п��ܷ������ļ������������쳣
	//	{
	//		m_STDDuration += (rrWave.m_nRRLen *1000 / nFreq); //����ST���³�����0.1MV�ĳ���ʱ��

	//		if (m_STDDuration > ST_EVENT_DURATION)
	//		{
	//			m_STDDuration = 0;//���¿�ʼ�����³�����ʱ��

	//			CDiagnoseResult *pDiag = new CDiagnoseResult;
	//			pDiag->m_code = ET_STDepression;
	//			pDiag->m_dwPacketCount = rrWave.m_dwRPacketCout;
	//			pDiag->m_nOffset = rrWave.m_nROffsetInPacket;
	//			m_resultList.AddTail(pDiag);

	//		}

	//	}
	//	else
	//	{
	//		m_STDDuration = 0; //ST���³��ķ��Ȳ���1mv������ʱ������
	//	}
	//}
	//else
	//{
	//	m_STUDuration = 0;//ST����̧����ʱ������
	//	m_STDDuration = 0;//ST���³�����ʱ������
	//}

		return TRUE;
}


UINT CTompkinsAnalyst::GetHeartRate()
{
	return m_nCurHR;
}
