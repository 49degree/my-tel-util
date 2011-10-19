/****************************************************************************************************************
 * 文 件 名：MAFilter.h                                                  
 * 摘    要： 
 *           使用这个类是务必要注意以下两点：
 *			  1、滤波后的数据是直接存储到原缓冲区的，即会改变原数据缓冲区内的值；
 *			  2、由于此类是使用成员变量数组来实现滑窗，所以每一段不同的数据就得对应不同的滤波类对象；
 ****************************************************************************************************************/
#pragma once
#include "DataType.h"


#define SIMPLING_FREQUENCY 300//心电采样频率300Hz，要注意的是：程序的其余部分设计的思想是可以根据数据包的内容来“动态”地获取采样频率
							  //这里却固定了！因为就算采样频率发生变化，但滑动窗口的长度不需要改变

#define NOISE_FREQUENCY 50//我国的工频干扰频率为50Hz

#define FILTER_DEPH (SIMPLING_FREQUENCY/NOISE_FREQUENCY)

class CMAFilter
//:public CNoiseFilter
 {
public:
	CMAFilter(void);
	~CMAFilter(void);

	//成员函数
public:
	virtual void Init();

	virtual BOOL RemoveNoise(BYTE *pData, UINT nLen);

	//成员变量
	BYTE m_window [FILTER_DEPH];
	int m_sum;
};
