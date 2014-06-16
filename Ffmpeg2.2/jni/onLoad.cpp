
#include <stdlib.h>
#include <android/log.h>
#include "jniUtils.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"onLoad",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"onLoad",__VA_ARGS__)

extern int register_JNI_function(JNIEnv *env);

static JavaVM *sVm;

// Throw an exception with the specified class and an optional message.
int jniThrowException(JNIEnv* env, const char* className, const char* msg) 
{
    jclass exceptionClass = env->FindClass(className);
    if (exceptionClass == NULL) 
        return -1;

    if (env->ThrowNew(exceptionClass, msg) != JNI_OK) {
    }

    return 0;
}

JNIEnv* getJNIEnv() 
{
    JNIEnv* env = NULL;
    if (sVm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) 
        return NULL;

    return env;
}

/*
 * Register native JNI-callable methods.
 * "className" looks like "java/lang/String".
 */
int jniRegisterNativeMethods(JNIEnv* env, const char* className, const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL)
        return -1;
    LOGE("%s",className);
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0)
        return -1;

    return 0;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) 
{
	LOGE("JNI_OnLoad");
    JNIEnv* env = NULL;
    jint result = JNI_ERR;
	sVm = vm;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) 
        return result;
        
    if(register_JNI_function(env) != JNI_OK)
    	goto end;

    result = JNI_VERSION_1_4;

end:
    return result;
}
