LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= C:\Users\Spencer\AndroidStudioProjects\opencv
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}\sdk\native\jni\OpenCV.mk

#main
LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS += -llog
LOCAL_MODULE := faceRecognitionIpca

include $(BUILD_SHARED_LIBRARY)