/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_spencerbarton_lab2_551_FaceRecognitionActivity */

#ifndef _Included_com_spencerbarton_lab2_551_FaceRecognitionActivity
#define _Included_com_spencerbarton_lab2_551_FaceRecognitionActivity
#ifdef __cplusplus
extern "C" {
#endif
#undef com_spencerbarton_lab2_551_FaceRecognitionActivity_FACE_IMG_SIZE
#define com_spencerbarton_lab2_551_FaceRecognitionActivity_FACE_IMG_SIZE 128L
#undef com_spencerbarton_lab2_551_FaceRecognitionActivity_REQUEST_IMAGE_CAPTURE
#define com_spencerbarton_lab2_551_FaceRecognitionActivity_REQUEST_IMAGE_CAPTURE 1L
#undef com_spencerbarton_lab2_551_FaceRecognitionActivity_NUM_PCA_COMP
#define com_spencerbarton_lab2_551_FaceRecognitionActivity_NUM_PCA_COMP 6L
/*
 * Class:     com_spencerbarton_lab2_551_FaceRecognitionActivity
 * Method:    IPCAtest
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_spencerbarton_lab2_1551_FaceRecognitionActivity_IPCAtest
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     com_spencerbarton_lab2_551_FaceRecognitionActivity
 * Method:    IPCAtrain
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_spencerbarton_lab2_1551_FaceRecognitionActivity_IPCAtrain
  (JNIEnv *, jobject, jstring, jint);

JNIEXPORT jint JNICALL Java_com_spencerbarton_lab2_1551_FaceRecognitionActivity_ClassifyGender
    (JNIEnv *, jobject, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
