#pragma once
#include <vector>
#include <deque>

#include "DataType.h"



using namespace std;

class CRPosAnalyser
{
public:
	CRPosAnalyser(void);
	~CRPosAnalyser(void);

public:
	bool Process(int nXPos, int nTempData, int nOriData, CRRWave &rrWave, DWORD dwPacketCount, UINT nEcgFreq);

	bool FindRRWave(int nXPos, float nTempData, float fThreshold, CRRWave& rrWave, DWORD dwPackCount);

	bool ConfirmRPos(int nRPos, bool bIsInversion, CRRWave &rr, DWORD dwPackCount);

	void AnalyseRR(unsigned char *pData, CRRWave &rr);

	int GetMaxDistanceIndex(vector<unsigned char> &data, int p1, int p2, double minDis, int top, int buttom, bool isAsc);

	void Init();
private:
	int abs(int x);


private:
	//���ݲ���Ƶ��
	int Frequency;

	//��һ��RR����
	CRRWave m_lastRRWave;

	//���ҵ��б�
	vector<int> TopChordList;

	//���ҵ�����
	int TopChordIndex;

	//vector<CRRWave> RRList;

	//�ĵ�����
	vector<unsigned char> EcgData;

	//�ĵ�����λ������
	int EcgIndex;

	//R��ķ�����ʱ���� �ж��Ƿ�Ϊͬһ���ڵ�����
	bool IsSameWave;

	//R������������Χ����
	int RLen;

	//R�������Χ�ڵĺϼ�ֵ
	float RSum;
	deque<float> RSum2;

	//��¼������Χ�ڵ�ǰ����ֵ
	int CurrentMaxAmplitude;
	int CurrentMinAmplitude;

	int DetailId;

	//����ֵ���ڵ��ĵ�����λ������
	int CurrentMaxAmplitudeIndex;
	int CurrentMinAmplitudeIndex;

	int CurrentRRIndex;

	//R���Ƿ���
	bool CurrentRRIsInversion;

	//���ڻ��߼��ĽǶ�ֵ
	//vector<int> AngleNumbers;

	int m_aRRDistance [8];  //RR��������
	int m_index;            //RR�������������


};
