package org.gearvrf.animation;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;

import org.gearvrf.GVRPerspectiveCamera;

import java.security.InvalidParameterException;

//import org.junit.Test;

/**
 * Created by diego on 2/26/15.
 */
public class GVRAccelerateDecelerateInterpolatorTest extends ActivityInstrumentationGVRf {

    private GVRAccelerateDecelerateInterpolator interpolator;

    public GVRAccelerateDecelerateInterpolatorTest() {
        super(GVRTestActivity.class);
    }

    public void testGetInstance() {
        assertEquals(GVRPerspectiveCamera.getDefaultFovY(), 90.0f, 0.0f);
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        assertNotNull(interpolator);
    }

    public void testMapRatio() {
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        float result=interpolator.mapRatio(0.5f);
        assertEquals(result, 0.5f);
    }

    public void testMapRatioNegative() {
        try {
            GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
            assertEquals(interpolator.mapRatio(-0.5f), -0.5f);
            //fail();
        }catch (InvalidParameterException e) {
            assertNotNull(e.getMessage(), "ratio - the current time ratio, >= 0 and <= 1");
        }
    }

    public void testMapRatioEqualThanOne() {
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        assertEquals(interpolator.mapRatio(1.0f), 1.0f);
    }

    public void testMapRatioGreaterThanOne() {
        try {
            GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
            assertEquals(interpolator.mapRatio(2.0f), 2.0f);
            //fail();
        } catch (InvalidParameterException e) {
            assertNotNull(e.getMessage(), "ratio - the current time ratio, >= 0 and <= 1");
        }
    }

    public void testMapRatioNaN() {
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        assertEquals(interpolator.mapRatio(Float.NaN), Float.NaN);
    }

    public void ignoretestMapRatioPositiveInfinity() {
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        assertEquals(interpolator.mapRatio(Float.POSITIVE_INFINITY), Float.POSITIVE_INFINITY,0.0f);
        //assertEquals(interpolator.mapRatio(Float.POSITIVE_INFINITY), Float.NaN,0.0f);
    }

    public void testMapRatioNegativeInfinity() {
        GVRAccelerateDecelerateInterpolator interpolator = GVRAccelerateDecelerateInterpolator.getInstance();
        //assertEquals(interpolator.mapRatio(Float.NEGATIVE_INFINITY), Float.NEGATIVE_INFINITY,0.0f);
        assertEquals(interpolator.mapRatio(Float.NEGATIVE_INFINITY), Float.NaN,0.0f);
    }


}
