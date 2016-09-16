package org.gearvrf.utility;

import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRFutureOnGlThread;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTexture;
import org.gearvrf.R;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRScaleAnimation;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.utility.RuntimeAssertion;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.util.concurrent.Future;

/**
 * Created by j.elidelson on 6/9/2015.
 */
public class RuntimeAssertionTest extends ActivityInstrumentationGVRf {

    public RuntimeAssertionTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructorA(){
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion("test");
        assertNotNull(runtimeAssertionTest);
    }

    public void testConstructorAEmptyparam(){
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion("");
        assertNotNull(runtimeAssertionTest);
    }

    public void testConstructorANullparam(){
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion(null);
        assertNotNull(runtimeAssertionTest);
    }

    public void testConstructorB(){
        float a=1.0f;
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion("%f",a);
        assertNotNull(runtimeAssertionTest);
    }

    public void testConstructorBEmptyparam(){
        float a=1.0f;
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion("",a);
        assertNotNull(runtimeAssertionTest);
    }

    public void testConstructorBNullparam(){
        float a=1.0f;
        RuntimeAssertion runtimeAssertionTest = new RuntimeAssertion("%f", (Object) null);
        assertNotNull(runtimeAssertionTest);
    }
}