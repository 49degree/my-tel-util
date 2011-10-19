#include <EcgAnalyse.h>
#include "CoreECGAnalyse/TompkinsAnalyst.h"


//�����������
CTompkinsAnalyst tompkinsAnalyst;

/*
 * ��ʼ����������
 * Class:     com_xys_ecg_dataproc_EcgDataAnalysis
 * Method:    analysisInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_analysisInit(JNIEnv *env, jclass thizClass){
	LOGD("it is the first data and to init tompkinsAnalyst");
	tompkinsAnalyst.Init();
}

/*
 * �����ĵ�����
 * Class:     com_xys_ecg_dataproc_EcgDataAnalysis
 * Method:    analysis
 * Signature: ([B)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_analysis(
		JNIEnv *env, jobject thiz, jbyteArray byteArray,jint dwPacketCount,jint nEcgFreq) {
	LOGE("it is the Analyst:");

	//���÷�������
	char *tmpdata = (char*) env->GetByteArrayElements(byteArray, 0);
	env->ReleaseByteArrayElements(byteArray, (jbyte*) tmpdata, 0);
	UINT nLen = sizeof(tmpdata);//�����ĵ����ݰ�����
	tompkinsAnalyst.Analyse((BYTE*) tmpdata, nLen, dwPacketCount, nEcgFreq);//���÷�������
	CDiagnoseResult *cDiagnoseResult = tompkinsAnalyst.GetResult();//���ط������

	/*���췵�ض���*/
	jclass resultClass = env->FindClass("com/xys/ecg/bean/CDiagnoseResult");
	jmethodID resultMethod = env->GetMethodID(resultClass, "<init>", "()V");
	jfieldID m_code = env->GetFieldID(resultClass, "m_code", "B"); //�쳣������
	jfieldID m_dwPacketCount = env->GetFieldID(resultClass, "m_dwPacketCount","I");//�����쳣�İ�����
	jfieldID m_nOffset = env->GetFieldID(resultClass, "m_nOffset", "I");//���������ĵ��ڰ��ڵ�λ��
	jfieldID m_nCurHR = env->GetFieldID(resultClass, "m_nCurHR", "I");//���������ĵ��ڰ��ڵ�λ��

	jobject resultObject = env->NewObject(resultClass, resultMethod);

	if(cDiagnoseResult!=0){
		LOGD("cDiagnoseResult:");
		env->SetByteField(resultObject, m_code, cDiagnoseResult->m_code);
		env->SetIntField(resultObject, m_dwPacketCount,
				cDiagnoseResult->m_dwPacketCount);
		env->SetIntField(resultObject, m_nOffset, cDiagnoseResult->m_nOffset);
	}

	//��ȡ����ֵ
	int m_nCurHRValue = tompkinsAnalyst.GetHeartRate();
	env->SetIntField(resultObject, m_nCurHR, m_nCurHRValue);
	return resultObject;

}

/**
 * ���Է���
 */
JNIEXPORT jint JNICALL Java_com_xys_ecg_dataproc_EcgDataAnalysis_getIntCountFromJNI(JNIEnv *env, jobject thiz){
    int x,y;
    x = 700;
    y = 600;
    x+=y;
    return x;
}
