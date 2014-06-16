LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := TestFfmpegLib

LOCAL_SRC_FILES := \
	onLoad.cpp\
	DecodeH264.cpp

LOCAL_CFLAGS := -D__STDC_CONSTANT_MACROS
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid  
LOCAL_SHARED_LIBRARIES:= libavcodec libavformat libswscale libavutil libavfilter libavdevice libwsresample
include $(BUILD_SHARED_LIBRARY)  
$(call import-module,ffmpeg-2.2.3/android/arm) 