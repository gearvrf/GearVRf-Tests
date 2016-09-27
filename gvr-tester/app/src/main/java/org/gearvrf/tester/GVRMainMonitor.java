package org.gearvrf.tester;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;


public interface GVRMainMonitor {
    void onInitCalled(GVRContext context, GVRScene scene);
    void onSceneRendered();
    void onAssetLoaded(GVRSceneObject asset);
}
