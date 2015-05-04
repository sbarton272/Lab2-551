LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= C:\Users\Spencer\AndroidStudioProjects\OpenCV-2.4.10-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

#main
LOCAL_SRC_FILES := ipca.cpp dirUtils.cpp
LOCAL_LDLIBS += -llog
LOCAL_MODULE := faceRecognitionIpca

include $(BUILD_SHARED_LIBRARY)