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
	BYTE  m_data[RRWAVE_BUF_SIZE];  //���ݻ���
	int   m_nRRLen;                 //RR���ڵĳ���
	int   m_nDataLen;               //��������Ч���ݵĳ���
	int	  m_nTPos;                  //T���λ��
	int   m_nSTStartingPos;         //ST�ε����λ��
	int   m_nSTEndingPos;           //ST�ε��յ�λ��
	int   m_nBaseline;              //���ߵ�ֵ
	UINT  m_nAvarageHR;             //ƽ������
	int  m_nSamplFreq;             //AD������

	DWORD m_dwRPacketCout;          //��ǰRR�����У���2��R���������ڰ�����
	UINT  m_nROffsetInPacket;       //��ǰRR�����У���2��R�������ڰ��ڵ�λ��

	int   m_nRPos;					//R����������ݿ�ʼλ�õ�ƫ����(RR�����еĵ�һ��R��)
	bool  m_bIsInversion;			//R���Ƿ���
	bool  m_bIsTReverse;			//T���Ƿ���
	int	  m_nPPos;					//P�������R���λ��
	int   m_nQStartPos;				//Q�������R���λ��
	int	  m_nInflexion;				//��һ���յ��λ��
	int   m_nJPos;					//J�������R���λ��
	int   m_nLPos;					//L�������R���λ��
	int   m_nSPos;					//S�������R���λ��
	bool  m_bIsNormal;				//�Ƿ���������

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
	ET_EventButtonDown = 1,			    // ������ť
	ET_LowHeartRate    = 2,				// ���ɹ���
	ET_HighHeartRate   = 3,				// ���ɹ���
	ET_STElevation     = 4,	            // ST����
	ET_STDepression    = 5              // ST�½�
};

//contains information of diagnoses.
struct CDiagnoseResult
{
	TEventType  m_code;          //�쳣������
	DWORD       m_dwPacketCount; //�����쳣�İ�����
	UINT        m_nOffset;       //���������ĵ��ڰ��ڵ�λ��
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
