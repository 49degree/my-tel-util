#define JNI_DEBUG
#ifdef JNI_DEBUG
#ifndef LOG_TAG
#define LOG_TAG "JNI_DEBUG"
#endif
#include <android/log.h>
#include <stdio.h>
#define LOGE(msg) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, msg)
#define LOGI(msg) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, msg)
#define LOGD(msg)
#endif
