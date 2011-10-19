#ifndef _DATA_TYPE_H
#define _DATA_TYPE_H

#define RRWAVE_BUF_SIZE 2048


#define BOOL	bool
#define DWORD	unsigned int
#define UINT	unsigned int
#define BYTE	unsigned char
#define TRUE	true
#define FALSE   false
#define __time64_t	long long
#define MAKEWORD(a, b)      ((WORD)(((BYTE)(a)) | ((WORD)((BYTE)(b))) << 8))
#define MAKELONG(a, b)      ((LONG)(((WORD)(a)) | ((DWORD)((WORD)(b))) << 16))	
#define TRACE
#define ASSERT
#define _T
#define memcpy



struct CRRWave
{
	BYTE  m_data[RRWAVE_BUF_SIZE];  //数据缓冲
	int   m_nRRLen;                 //RR间期的长度
	int   m_nDataLen;               //缓冲中有效数据的长度
	int	  m_nTPos;                  //T点的位置
	int   m_nSTStartingPos;         //ST段的起点位置
	int   m_nSTEndingPos;           //ST段的终点位置
	int   m_nBaseline;              //基线的值
	UINT  m_nAvarageHR;             //平均心率
	int  m_nSamplFreq;             //AD采样率

	DWORD m_dwRPacketCout;          //当前RR间期中，第2个R波顶点所在包计数
	UINT  m_nROffsetInPacket;       //当前RR间期中，第2个R波顶点在包内的位置

	int   m_nRPos;					//R点相对于数据开始位置的偏移量(RR间期中的第一个R点)
	bool  m_bIsInversion;			//R波是否倒置
	bool  m_bIsTReverse;			//T波是否反向
	int	  m_nPPos;					//P点相对于R点的位置
	int   m_nQStartPos;				//Q点相对于R点的位置
	int	  m_nInflexion;				//第一个拐点的位置
	int   m_nJPos;					//J点相对于R点的位置
	int   m_nLPos;					//L点相对于R点的位置
	int   m_nSPos;					//S点相对于R点的位置
	bool  m_bIsNormal;				//是否是正常波

public:
	CRRWave():m_nRRLen(0), m_nDataLen(0),
			  m_nTPos(0), m_nSTStartingPos(0),
			  m_nSTEndingPos(0), m_nBaseline(0),
			  m_nAvarageHR(0), m_nSamplFreq(0),
			  m_dwRPacketCout(0), m_nROffsetInPacket(0)
	{
//		::memset(m_data, 0, sizeof(m_data) );
	}
};


//The types of Exceptional Event.
enum TEventType
{
	ET_EventButtonDown = 1,			    // 紧急按钮
	ET_LowHeartRate    = 2,				// 心律过慢
	ET_HighHeartRate   = 3,				// 心律过快
	ET_STElevation     = 4,	            // ST上升
	ET_STDepression    = 5              // ST下降
};

//contains information of diagnoses.
struct CDiagnoseResult
{
	TEventType  m_code;          //异常的类型
	DWORD       m_dwPacketCount; //发生异常的包计数
	UINT        m_nOffset;       //发生导常的点在包内的位置
};

class CPoint
{
public:
	CPoint()
	{
		x = 0;
		y = 0;
	};

	CPoint(const CPoint& pt)
	{
		this->x = pt.x;
		this->y = pt.y;
	};

	CPoint(int& x, BYTE& y){
		this->x = x;
		this->y = y;
	};
	CPoint(int x, int y){
		this->x = x;
		this->y = y;
	};
public:
	int x;
	int y;
};


#pragma once
#include "CUtils.h"


#endif // _DATA_TYPE_H
