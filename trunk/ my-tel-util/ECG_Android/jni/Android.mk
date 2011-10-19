LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := EcgAnalyse
LOCAL_SRC_FILES := CoreECGAnalyse/TompkinsAnalyst.cpp\
      CoreECGAnalyse/BaseLineProcessor.cpp\
      CoreECGAnalyse/Differentiator.cpp\
      CoreECGAnalyse/ExtremumSTAnalyst.cpp\
      CoreECGAnalyse/HighPassFilter.cpp\
      CoreECGAnalyse/LowPassFilter.cpp\
      CoreECGAnalyse/LTSTAnalyst.cpp\
      CoreECGAnalyse/MAFilter.cpp\
      CoreECGAnalyse/RPosAnalyser.cpp\
      CoreECGAnalyse/RWaveAnalyst.cpp\
      CoreECGAnalyse/SlidingWindowIntegrator.cpp\
      CoreECGAnalyse/STAnalyst.cpp\
      EcgAnalyse.cpp
LOCAL_LDLIBS    := -llog      
include $(BUILD_SHARED_LIBRARY)