package org.gearvrf.utility;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;

/**
 * Created by j.elidelson on 5/14/2015.
 */
public class LogTest extends ActivityInstrumentationGVRf {

    public LogTest() {
        super(GVRTestActivity.class);
    }

    private static final String TAG= Log.tag(LogTest.class);

   public void testLogConstruct() {
       assertNotNull(TAG);
    }

    public void testDMethodparamNull() {
       try {
           Log.d(TAG, null, (Object) null);
       }catch (NullPointerException e) {}
    }

    public void testEMethodparamNull() {
        try {
            Log.e(TAG, null, (Object) null);
        }catch (NullPointerException e) {}
    }

    public void testIMethodparamNull() {
        try {
            Log.i(TAG, "%d", 1);
            Log.i(TAG, null, (Object) null);
        }catch (NullPointerException e) {}
    }

    public void testVMethodparamNull() {
        try {
            Log.v(TAG, null, (Object) null);
        }catch (NullPointerException e) {}
    }

    public void testWMethodparamNull() {
        try {
            Log.w(TAG, "%d", 1);
            Log.w(TAG, null, (Object) null);
        }catch (NullPointerException e) {}
    }

}