/*
 * com_yujunkang_fangxinbao_utility_JniEncrypt.cpp
 *
 *  Created on: 2014-5-23
 *      Author: xieb
 */

#include <stdio.h>
#include <md5.h>
#include <com_yujunkang_fangxinbao_utility_JniEncrypt.h>
#include <jni.h>
#include <android/log.h>
#include <verifysignpkg.h>

#define buffsize 2048

JNIEXPORT jstring JNICALL Java_com_yujunkang_fangxinbao_utility_JniEncrypt_getEncryptString(
		JNIEnv *env, jobject thiz, jobjectArray params) {
	int packageVerify = verifyPackageName(env, thiz);
	if (packageVerify != 0) {
		//exit(0);
		//__android_log_write(ANDROID_LOG_DEBUG,"validate_packagename","error");
	} else {
		//__android_log_write(ANDROID_LOG_DEBUG,"validate_packagename","succ");
	}

	int signatureVerify = verifySignature(env, thiz);
	if (signatureVerify != 0) {
		//exit(0);
		//__android_log_write(ANDROID_LOG_DEBUG,"validate_signature","error");
	} else {
		//__android_log_write(ANDROID_LOG_DEBUG,"validate_signature","succ");
	}

	int len = 0;
	char* joined = malloc(buffsize);
	strcpy(joined, "");
	int size = (*env)->GetArrayLength(env, params);
	int i = 0;

	for (i = 0; i < size; i++) {
		jstring jparam = (jstring)(*env)->GetObjectArrayElement(env, params, i);
		char* param = (char*) (*env)->GetStringUTFChars(env, jparam, NULL);

		//__android_log_write(ANDROID_LOG_DEBUG, "tag", param);

		strcat(joined, param);
		len = len + strlen(param);
	}

	strcat(joined, "~1@)7q^48c96*");
	len = len + 13;
	joined[len] = '\0';
	__android_log_write(ANDROID_LOG_DEBUG, "jnitag", joined);

	char* md5 = MDString(joined);
	__android_log_write(ANDROID_LOG_DEBUG, "jnitag", md5);
	char* result = malloc(9);
	result[0] = md5[4];
	result[1] = md5[1];
	result[2] = md5[16];
	result[3] = md5[9];
	result[4] = md5[19];
	result[5] = md5[30];
	result[6] = md5[28];
	result[7] = md5[22];
	result[8] = '\0';

	jstring sid = (*env)->NewStringUTF(env, result);

	free(joined);
	free(result);

	return sid;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {

	JNIEnv* env = NULL;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return JNI_ERR;
	}

	//__android_log_write(ANDROID_LOG_DEBUG,"JNI","onload");

	return JNI_VERSION_1_4;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
	//__android_log_write(ANDROID_LOG_DEBUG,"JNI","onunload");
}
