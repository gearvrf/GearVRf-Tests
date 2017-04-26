package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.physics.GVRCollisionMatrix;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PhysicsWorldTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    GVRWorld mWorld;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        mWaiter = new Waiter();

        GVRTestUtils.OnInitCallback initCallback = new GVRTestUtils.OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                mWorld = new GVRWorld(gvrContext);
                gvrContext.getMainScene().getRoot().attachComponent(mWorld);
            }
        };

        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity(), initCallback);
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void testEnableDisablePhysics() throws Exception {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        float posY;

        GVRSphereSceneObject sphere = new GVRSphereSceneObject(context);
        GVRSphereCollider collider = new GVRSphereCollider(context);
        GVRRigidBody body = new GVRRigidBody(context, 3.0f, 0);

        collider.setRadius(1.0f);

        sphere.attachCollider(collider);
        sphere.attachComponent(body);

        scene.addSceneObject(sphere);

        posY = sphere.getTransform().getPositionY();

        gvrTestUtils.waitForXFrames(60);

        mWaiter.assertTrue(posY != sphere.getTransform().getPositionY());

        mWorld.setEnable(false);

        posY = sphere.getTransform().getPositionY();

        gvrTestUtils.waitForXFrames(60);

        mWaiter.assertTrue(posY == sphere.getTransform().getPositionY());
    }
}
