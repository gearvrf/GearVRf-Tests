package org.gearvrf;

import org.gearvrf.viewmanager.TestViewManager;

public class GVRTestActivity extends DefaultGVRTestActivity {

    @Override
    protected void initGVRTestActivity() {
        setMain(new TestViewManager(), "gvr_note4.xml");
    }
}