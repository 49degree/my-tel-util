#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This makefile supplies the rules for building a library of JNI code for
# use by our example of how to bundle a shared library with an APK.

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= libandroidterm

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  termExec.cpp

LOCAL_LDLIBS := -ldl -llog

include $(BUILD_SHARED_LIBRARY)

# rageagainstthecage
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= rageagainstthecage

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  rageagainstthecage.c


include $(BUILD_EXECUTABLE)

# GingerBreak
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= gingerbreak

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  GingerBreak.c


include $(BUILD_EXECUTABLE)

# exploid
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= exploid

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  exploid.c


include $(BUILD_EXECUTABLE)

# zergRush
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= zergrush

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
  zergRush.c
  
LOCAL_LDLIBS := -ldl -lcutils

include $(BUILD_EXECUTABLE)

