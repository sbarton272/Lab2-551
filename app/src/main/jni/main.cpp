/*** 18551 Homework 2 Starter Code ******/

#include <jni.h>
#include <string>
#include <fstream>
#include <opencv2/core/core.hpp>

#include "com_spencerbarton_lab2_551_FaceRecognitionActivity.h"

JNIEXPORT jint JNICALL Java_com_spencerbarton_lab2_1551_FaceRecognitionActivity_IPCAtest
  (JNIEnv * env, jobject obj, jstring testFile) {
    return 42;
}

JNIEXPORT jint JNICALL Java_com_spencerbarton_lab2_1551_FaceRecognitionActivity_IPCAtrain
  (JNIEnv * env, jobject obj, jstring trainFile, jint numPcaCmp) {
    return numPcaCmp;
}

