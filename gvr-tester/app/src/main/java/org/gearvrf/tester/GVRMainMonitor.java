package org.gearvrf.tester;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;

public interface GVRMainMonitor {
    void onInitCalled(GVRContext context, GVRScene scene);
    void onSceneRendered();
}
