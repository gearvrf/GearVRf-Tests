#include "engine/renderer/renderer.h"

extern "C" {

JNIEXPORT jlongArray JNICALL
Java_org_gearvrf_tester_StateSortTests_getRenderDataVector(JNIEnv *env, jclass type) {
    jlongArray result = env->NewLongArray(1);

    return result;
}

}
