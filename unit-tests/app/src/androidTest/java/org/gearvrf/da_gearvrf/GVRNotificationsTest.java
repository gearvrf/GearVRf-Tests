package org.gearvrf.da_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRNotifications;

/**
 * Created by j.elidelson on 9/3/2015.
 */
public class GVRNotificationsTest extends ActivityInstrumentationGVRf {

    public void test1(){

        GVRNotifications.waitBeforeStep();
        GVRNotifications.waitAfterStep();
    }
}
