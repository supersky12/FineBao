/*
 * verifysignpkg.h
 *
 *  Created on: 2013-06-23
 *      Author: xieb
 */

#include <stdio.h>
#include <jni.h>

#ifndef VERIFYSIGNPKG_H_
#define VERIFYSIGNPKG_H_


const char* contextFieldName = "mContext";
const char* correctPackageNameMD5 = "16685dc58b23092da8a695083dfcf9a0";
const char* correctSignatureMD5 = "a3afdd4ed3e44ad7fbe84423f4f0b7a0";
	

int verifyPackageName(JNIEnv *env, jobject jniObj) {
	jclass jnicls = (*env)->GetObjectClass(env, jniObj);
	jfieldID fieldIDContext = (*env)->GetFieldID(env, jnicls, contextFieldName,"Landroid/content/Context;");
	if (fieldIDContext == NULL) {
		return 1; /* failed to find the field */
	}
	jobject objectContext = (*env)->GetObjectField(env, jniObj, fieldIDContext);
	jclass classContext = (*env)->FindClass(env, "android/content/Context");
	jmethodID methodIDgetPackageName =  (*env)->GetMethodID(env, classContext, "getPackageName", "()Ljava/lang/String;");
	jstring jPackageName = (jstring)(*env)->CallObjectMethod(env, objectContext, methodIDgetPackageName);

	char* toVerifyPackageName = (char*)(*env)->GetStringUTFChars(env, jPackageName, 0);
	//__android_log_write(ANDROID_LOG_DEBUG,"packagename",toVerifyPackageName);

	char* toVerifyPackageNameMD5 = MDString(toVerifyPackageName);
	(*env)->ReleaseStringUTFChars(env, jPackageName, toVerifyPackageName);

	//__android_log_write(ANDROID_LOG_DEBUG,"packagename_md5",toVerifyPackageNameMD5);

	return strcmp(toVerifyPackageNameMD5, correctPackageNameMD5);
}

int verifySignature(JNIEnv *env, jobject jniObj) {
	jclass jnicls = (*env)->GetObjectClass(env, jniObj);
	//获取mContext成员变量id
	jfieldID fieldIDContext = (*env)->GetFieldID(env, jnicls, contextFieldName,"Landroid/content/Context;");
	//获取mContext成员
	jobject objectContext = (*env)->GetObjectField(env, jniObj, fieldIDContext);

	//获取包名
	jclass classContext = (*env)->FindClass(env, "android/content/Context");
	jmethodID methodIDgetPackageName =  (*env)->GetMethodID(env, classContext, "getPackageName", "()Ljava/lang/String;");
	jstring jPackageName = (jstring)(*env)->CallObjectMethod(env, objectContext, methodIDgetPackageName);
	//获取PackageManager
	jmethodID methodIDgetPackageManager = (*env)->GetMethodID(env,classContext, "getPackageManager","()Landroid/content/pm/PackageManager;");
	jclass clazzPackageManager = (*env)->FindClass(env, "android/content/pm/PackageManager");
	//获取签名的packageinfo
	jfieldID fieldIDstaticSignatureFlag = (*env)->GetStaticFieldID(env, clazzPackageManager, "GET_SIGNATURES", "I");
	int signatureFlag = (*env)->GetStaticIntField(env, clazzPackageManager, fieldIDstaticSignatureFlag);
	jmethodID methodIDgetPackageInfo = (*env)->GetMethodID(env, clazzPackageManager, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
	jobject object_pm = (*env)->CallObjectMethod(env, objectContext, methodIDgetPackageManager);
	jobject object_pi = (*env)->CallObjectMethod(env, object_pm, methodIDgetPackageInfo, jPackageName, signatureFlag);
	//获取packageinfo的signature成员
	jclass clazzPackageInfo = (*env)->FindClass(env, "android/content/pm/PackageInfo");
	jfieldID fieldIDSignatures = (*env)->GetFieldID(env,clazzPackageInfo, "signatures","[Landroid/content/pm/Signature;");
	jobjectArray signatures = (jobjectArray)(*env)->GetObjectField(env, object_pi, fieldIDSignatures);
	jobject firstSignature = (*env)->GetObjectArrayElement(env, signatures, 0);
	//把签名对象转成字符串
	jclass clazzSignature = (*env)->FindClass(env, "android/content/pm/Signature");
	jmethodID methodIDSignatureToCharsString = (*env)->GetMethodID(env, clazzSignature, "toCharsString", "()Ljava/lang/String;");
	jstring jSignatureChars = (jstring)(*env)->CallObjectMethod(env, firstSignature, methodIDSignatureToCharsString);

	char* toVerifySignature = (char*)(*env)->GetStringUTFChars(env, jSignatureChars, NULL);
	if (toVerifySignature == NULL) {
		return 1; /* OutOfMemoryError already thrown */
	}
	//__android_log_write(ANDROID_LOG_DEBUG,"signature",toVerifySignature);
	char* toVerifySignatureMD5 = MDString(toVerifySignature);
	(*env)->ReleaseStringUTFChars(env, jSignatureChars, toVerifySignature);

	//__android_log_write(ANDROID_LOG_DEBUG,"signature_md5",toVerifySignatureMD5);

	return strcmp(toVerifySignatureMD5, correctSignatureMD5);
}


#endif /* VERIFYSIGNPKG_H_ */
