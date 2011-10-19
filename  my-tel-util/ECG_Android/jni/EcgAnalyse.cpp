#include <EcgAnalyse.h>
#include "CoreECGAnalyse/TompkinsAnalyst.h"


//定义分析对象
CTompkinsAnalyst tompkinsAnalyst;

/*
 * 初始化分析对象
 * Class:     com_xys_ecg_dataproc_EcgDataAnalysis
 * Method:    analysisInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_analysisInit(JNIEnv *env, jclass thizClass){
	LOGD("it is the first data and to init tompkinsAnalyst");
	tompkinsAnalyst.Init();
}

/*
 * 分析心电数据
 * Class:     com_xys_ecg_dataproc_EcgDataAnalysis
 * Method:    analysis
 * Signature: ([B)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_analysis(
		JNIEnv *env, jobject thiz, jbyteArray byteArray,jint dwPacketCount,jint nEcgFreq) {
	LOGE("it is the Analyst:");

	//调用分析方法
	char *tmpdata = (char*) env->GetByteArrayElements(byteArray, 0);
	env->ReleaseByteArrayElements(byteArray, (jbyte*) tmpdata, 0);
	UINT nLen = sizeof(tmpdata);//计算心电数据包长度
	tompkinsAnalyst.Analyse((BYTE*) tmpdata, nLen, dwPacketCount, nEcgFreq);//调用分析方法
	CDiagnoseResult *cDiagnoseResult = tompkinsAnalyst.GetResult();//返回分析结果

	/*构造返回对象*/
	jclass resultClass = env->FindClass("com/xys/ecg/bean/CDiagnoseResult");
	jmethodID resultMethod = env->GetMethodID(resultClass, "<init>", "()V");
	jfieldID m_code = env->GetFieldID(resultClass, "m_code", "B"); //异常的类型
	jfieldID m_dwPacketCount = env->GetFieldID(resultClass, "m_dwPacketCount","I");//发生异常的包计数
	jfieldID m_nOffset = env->GetFieldID(resultClass, "m_nOffset", "I");//发生导常的点在包内的位置
	jfieldID m_nCurHR = env->GetFieldID(resultClass, "m_nCurHR", "I");//发生导常的点在包内的位置

	jobject resultObject = env->NewObject(resultClass, resultMethod);

	if(cDiagnoseResult!=0){
		LOGD("cDiagnoseResult:");
		env->SetByteField(resultObject, m_code, cDiagnoseResult->m_code);
		env->SetIntField(resultObject, m_dwPacketCount,
				cDiagnoseResult->m_dwPacketCount);
		env->SetIntField(resultObject, m_nOffset, cDiagnoseResult->m_nOffset);
	}

	//获取心率值
	int m_nCurHRValue = tompkinsAnalyst.GetHeartRate();
	env->SetIntField(resultObject, m_nCurHR, m_nCurHRValue);
	return resultObject;

}

/**
 * 测试方法
 */
JNIEXPORT jint JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_getIntCountFromJNI(JNIEnv *env, jobject thiz){
    int x,y;
    x = 700;
    y = 600;
    x+=y;
    return x;
}
