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
	//数据采样频率
	int Frequency;

	//上一个RR间期
	CRRWave m_lastRRWave;

	//上弦点列表
	vector<int> TopChordList;

	//上弦点索引
	int TopChordIndex;

	//vector<CRRWave> RRList;

	//心电数据
	vector<unsigned char> EcgData;

	//心电数据位置索引
	int EcgIndex;

	//R点的分析临时变量 判断是否为同一波内的数据
	bool IsSameWave;

	//R波分析采样范围长度
	int RLen;

	//R点分析范围内的合计值
	float RSum;
	deque<float> RSum2;

	//记录分析范围内当前最大幅值
	int CurrentMaxAmplitude;
	int CurrentMinAmplitude;

	int DetailId;

	//最大幅值所在的心电数据位置索引
	int CurrentMaxAmplitudeIndex;
	int CurrentMinAmplitudeIndex;

	int CurrentRRIndex;

	//R波是否倒置
	bool CurrentRRIsInversion;

	//用于基线检测的角度值
	//vector<int> AngleNumbers;

	int m_aRRDistance [8];  //RR间期数组
	int m_index;            //RR间期数组迭代器


};
