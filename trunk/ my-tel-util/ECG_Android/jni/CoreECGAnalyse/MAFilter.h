/****************************************************************************************************************
 * �� �� ����MAFilter.h                                                  
 * ժ    Ҫ�� 
 *           ʹ������������Ҫע���������㣺
 *			  1���˲����������ֱ�Ӵ洢��ԭ�������ģ�����ı�ԭ���ݻ������ڵ�ֵ��
 *			  2�����ڴ�����ʹ�ó�Ա����������ʵ�ֻ���������ÿһ�β�ͬ�����ݾ͵ö�Ӧ��ͬ���˲������
 ****************************************************************************************************************/
#pragma once
#include "DataType.h"


#define SIMPLING_FREQUENCY 300//�ĵ����Ƶ��300Hz��Ҫע����ǣ���������ಿ����Ƶ�˼���ǿ��Ը������ݰ�������������̬���ػ�ȡ����Ƶ��
							  //����ȴ�̶��ˣ���Ϊ�������Ƶ�ʷ����仯�����������ڵĳ��Ȳ���Ҫ�ı�

#define NOISE_FREQUENCY 50//�ҹ��Ĺ�Ƶ����Ƶ��Ϊ50Hz

#define FILTER_DEPH (SIMPLING_FREQUENCY/NOISE_FREQUENCY)

class CMAFilter
//:public CNoiseFilter
 {
public:
	CMAFilter(void);
	~CMAFilter(void);

	//��Ա����
public:
	virtual void Init();

	virtual BOOL RemoveNoise(BYTE *pData, UINT nLen);

	//��Ա����
	BYTE m_window [FILTER_DEPH];
	int m_sum;
};
