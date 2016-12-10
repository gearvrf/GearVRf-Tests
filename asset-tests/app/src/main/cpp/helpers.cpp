#include "engine/renderer/renderer.h"

extern "C" {

class gvr::RenderData;

JNIEXPORT jlongArray JNICALL
Java_org_gearvrf_tester_StateSortTests_getRenderDataVector(JNIEnv *env, jclass type) {
    const std::vector<gvr::RenderData *> &renderDataVector = gvr::gRenderer->getRenderDataVector();
    jlongArray result = env->NewLongArray(renderDataVector.size());

    for (int i = 0; i < renderDataVector.size(); ++i) {
        const gvr::RenderData* const rd = renderDataVector[i];
        const jlong element = reinterpret_cast<jlong>(rd);
        env->SetLongArrayRegion(result, i, 1, &element);
    }
    return result;
}

}