package org.gearvrf.viewer_func;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.viewmanager.ViewerActivity;


/**
 * Created by j.elidelson on 9/18/2015.
 */

public class viewerTest extends ActivityInstrumentationGVRf {

    public viewerTest() {
        //super(SceneObjectActivity.class);
    }

    public void ignoretestGetInstance() {
        Instrumentation mInstrumentation = getInstrumentation();
        // We register our interest in the activity
        Instrumentation.ActivityMonitor monitor = mInstrumentation.addMonitor(ViewerActivity.class.getName(), null, false);
        // We launch it
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(mInstrumentation.getTargetContext(), ViewerActivity.class.getName());
        mInstrumentation.startActivitySync(intent);
        try {
            Thread.sleep(TestDefaultGVRViewManager.DelayTest+9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Activity currentActivity = getInstrumentation().waitForMonitor(monitor);
        assertNotNull(currentActivity);

        // We register our interest in the next activity from the sequence in this use case
        //mInstrumentation.removeMonitor(monitor);
        //monitor = mInstrumentation.addMonitor(YourNextClass.class.getName(), null, false);
    }
}