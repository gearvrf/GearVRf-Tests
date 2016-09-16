package org.gearvrf.da_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRNotifications;
import org.gearvrf.GVRTestActivity;

/**
 * Created by j.elidelson on 9/3/2015.
 */
public class GVRNotificationsTest extends ActivityInstrumentationGVRf {
    public GVRNotificationsTest() {
        super(GVRTestActivity.class);
    }

    public void test1(){

        GVRNotifications.waitBeforeStep();
        GVRNotifications.waitAfterStep();
    }
}
