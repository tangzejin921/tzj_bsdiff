
#ifndef BZIP2_1_0_6_BZIP2_H
#define BZIP2_1_0_6_BZIP2_H

#include "jni.h"

typedef int IntNative;
typedef char            Char;
typedef unsigned char   Bool;
typedef unsigned char   UChar;
typedef int             Int32;
typedef unsigned int    UInt32;
typedef short           Int16;
typedef unsigned short  UInt16;

#ifdef __cplusplus
extern "C" {
#endif

IntNative bzip2_main(IntNative argc, Char *argv[]);

#ifdef __cplusplus
}
#endif


#endif //BZIP2_1_0_6_BZIP2_H
