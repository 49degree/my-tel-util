#ifndef _JNI_UTILS_H_
#define _JNI_UTILS_H_

#include <stdlib.h>
#include <jni.h>

#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"H264JNI",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"H264JNI",__VA_ARGS__)

#ifdef __cplusplus
extern "C"
{
#endif

int jniThrowException(JNIEnv* env, const char* className, const char* msg);

JNIEnv* getJNIEnv();

int jniRegisterNativeMethods(JNIEnv* env, const char* className, const JNINativeMethod* gMethods, int numMethods);

#ifdef __cplusplus
}
#endif

#endif
