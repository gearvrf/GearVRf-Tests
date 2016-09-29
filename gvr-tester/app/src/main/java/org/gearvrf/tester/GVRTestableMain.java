package org.gearvrf.tester;

import org.gearvrf.GVRMain;

public abstract class  GVRTestableMain extends GVRMain{
    abstract void setMainMonitor(GVRMainMonitor mainMonitor);
    abstract boolean isOnInitCalled();
    abstract boolean isSceneRendered();
}
