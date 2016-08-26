package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMesh;
import org.gearvrf.tester.GVRTestUtils.OnInitCallback;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class GVRContextTests {
    private static final String TAG = GVRContextTests.class.getSimpleName();
    private static final int NUM_NORMALS_IN_QUAD = 12;
    private GVRTestUtils gvrTestUtils;

    public GVRContextTests() {
        super();
    }

    @Rule
    public ActivityTestRule<TestableActivity> mActivityRule = new
            ActivityTestRule<TestableActivity>(TestableActivity.class);

    @Before
    public void setUp() throws Exception {
        gvrTestUtils = new GVRTestUtils(mActivityRule.getActivity());
    }

    @Test(timeout = GVRTestUtils.TEST_TIMEOUT)
    public void contextNonNull() throws TimeoutException {
        final Waiter waiter = new Waiter();
        // Execute tests in onInit
        gvrTestUtils.setOnInitCallback(new OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                waiter.assertNotNull(gvrContext.getEventManager());
                waiter.assertNotNull(gvrContext.getEventReceiver());
                waiter.assertNotNull(gvrContext.getInputManager());
                waiter.assertNotNull(gvrContext.getAssetLoader());
                waiter.assertNotNull(gvrContext.getAnimationEngine());
                waiter.assertNotNull(gvrContext.getActivity());
                waiter.resume();
            }
        });
        waiter.await(GVRTestUtils.TEST_TIMEOUT);
    }

    @Test(timeout = GVRTestUtils.TEST_TIMEOUT)
    public void createQuad() throws TimeoutException {
        GVRContext gvrContext = gvrTestUtils.waitForOnInit();
        // Execute tests after onInit is called.
        GVRMesh mesh = gvrContext.createQuad(2.0f, 2.0f);
        Assert.assertTrue(mesh.getNormals().length == NUM_NORMALS_IN_QUAD);
    }
}
