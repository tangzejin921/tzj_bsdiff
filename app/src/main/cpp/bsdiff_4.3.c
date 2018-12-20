//
// Created by tangzejin921 on 2018/12/20.
//


#include "jni.h"
#include "bzip2_1.0.6/bzip2.h"
#include "bsdiff.h"
#include "bspatch.h"
#include <string.h>
#include <malloc.h>

int indexof(const char *str, const char c) {
    int ret = 0;
    int leng = strlen(str);
    for (int i = 0; i < leng; ++i) {
        if (str[i] == c) {
            ret = i;
        }
    }
    return ret + 1;
}

JNIEXPORT jint JNICALL
Java_com_tzj_bsdiff_UtilBsDiff_zip(JNIEnv *env, jclass type, jstring fileName_) {
    const char *fileName = (*env)->GetStringUTFChars(env, fileName_, 0);

    char *argv[] = {"./", fileName, "-zk"};

    int ret = bzip2_main(3, argv);

    (*env)->ReleaseStringUTFChars(env, fileName_, fileName);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_tzj_bsdiff_UtilBsDiff_unzip(JNIEnv *env, jclass type, jstring fileName_) {
    const char *fileName = (*env)->GetStringUTFChars(env, fileName_, 0);

    char *argv[] = {"./", "-dk", fileName};

    int ret = bzip2_main(3, argv);

    (*env)->ReleaseStringUTFChars(env, fileName_, fileName);
    return ret;
}

JNIEXPORT jstring JNICALL
Java_com_tzj_bsdiff_UtilBsDiff_bsDiff(JNIEnv *env, jclass type, jstring oldFielName_,
                                      jstring newFileName_) {
    const char *oldFielName = (*env)->GetStringUTFChars(env, oldFielName_, 0);
    const char *newFileName = (*env)->GetStringUTFChars(env, newFileName_, 0);

    char newFileNameTemp[strlen(newFileName)];
    strcpy(newFileNameTemp, newFileName);
    char *p = strtok(newFileNameTemp, "/");
    char *newName = p;
    while (p != NULL) {
        newName = p;
        p = strtok(NULL, "/");
    }
    int leng = (strlen(oldFielName) + strlen(newName) + 6);
    char *diff = malloc(sizeof(char) * leng);
    diff[0] = '\0';
    strcat(diff, oldFielName);//放在了old 的目录
    strcat(diff, "_");
    strcat(diff, newName);
    strcat(diff, ".diff");

    char *argv[] = {"./", oldFielName, newFileName, diff};

    bsdiff_main(4, argv);

    (*env)->ReleaseStringUTFChars(env, oldFielName_, oldFielName);
    (*env)->ReleaseStringUTFChars(env, newFileName_, newFileName);
    jstring jstr = (*env)->NewStringUTF(env, diff);
    if (diff != NULL) {
        free(diff);
        diff = NULL;
    }
    return jstr;
}

JNIEXPORT jstring JNICALL
Java_com_tzj_bsdiff_UtilBsDiff_bsPatch(JNIEnv *env, jclass type, jstring oldFielName_,
                                       jstring diffFileName_) {
    const char *oldFielName = (*env)->GetStringUTFChars(env, oldFielName_, 0);
    const char *diffFileName = (*env)->GetStringUTFChars(env, diffFileName_, 0);
    int index = indexof(oldFielName, '/');

    int oldL = strlen(oldFielName);
    int diffL = strlen(diffFileName);
    int tempL = diffL - oldL - 5;
    char *newFileName = malloc(sizeof(char) * (index + tempL));
    for (int i = 0; i < index; ++i) {
        newFileName[i] = oldFielName[i];
    }
    for (int i = 0; i < tempL; ++i) {
        newFileName[index + i] = diffFileName[oldL + 1 + i];
    }
    newFileName[index + tempL - 1] = '\0';

    char *argv[] = {"./", oldFielName, newFileName, diffFileName};
    bspatch_main(4, argv);

    (*env)->ReleaseStringUTFChars(env, oldFielName_, oldFielName);
    (*env)->ReleaseStringUTFChars(env, diffFileName_, diffFileName);

    jstring jstr = (*env)->NewStringUTF(env, newFileName);
    if (newFileName != NULL) {
        free(newFileName);
        newFileName = NULL;
    }
    return jstr;
}


