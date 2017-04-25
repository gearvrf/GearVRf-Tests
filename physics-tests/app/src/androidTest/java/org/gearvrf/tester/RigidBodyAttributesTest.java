package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class RigidBodyAttributesTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        GVRTestUtils.OnInitCallback onInitCallback = new GVRTestUtils.OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                Log.d("GVRPHYSICS", "HAPPENING");
                GVRWorld world = new GVRWorld(gvrTestUtils.getGvrContext());
                gvrTestUtils.getMainScene().getRoot().attachComponent(world);
            }
        };
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity(), onInitCallback);
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void createRigidBody() throws Exception {
        GVRRigidBody mSphereRigidBody = new GVRRigidBody(gvrTestUtils.getGvrContext());
        addSphere(gvrTestUtils.getMainScene(), mSphereRigidBody, 1.0f, 1.5f, 40.0f, -10.0f, 2.5f);
        mWaiter.assertTrue(mSphereRigidBody.getMass() == 2.5f);
        mWaiter.assertTrue(mSphereRigidBody.getRestitution() == 1.5f);
        mWaiter.assertTrue(mSphereRigidBody.getFriction() == 0.5f);
    }

    @Test
    public void enableRigidBody() throws Exception {
        GVRRigidBody mSphereRigidBody = new GVRRigidBody(gvrTestUtils.getGvrContext());
        addSphere(gvrTestUtils.getMainScene(), mSphereRigidBody, 1.0f, 1.0f, 10.0f, -10.0f, 2.5f);

        GVRRigidBody mSphereRigidBody2 = new GVRRigidBody(gvrTestUtils.getGvrContext());
        addSphere(gvrTestUtils.getMainScene(), mSphereRigidBody2, 1.0f, 2.0f, 10.0f, -10.0f, 2.5f);

        gvrTestUtils.waitForXFrames(10);

        float lastY = mSphereRigidBody.getTransform().getPositionY();
        float lastY2 =  mSphereRigidBody2.getTransform().getPositionY();
        gvrTestUtils.waitForXFrames(10);
        mWaiter.assertTrue( lastY > mSphereRigidBody.getTransform().getPositionY());//balls are falling
        mWaiter.assertTrue( lastY2 > mSphereRigidBody2.getTransform().getPositionY());

        lastY = mSphereRigidBody.getTransform().getPositionY();
        lastY2 =  mSphereRigidBody2.getTransform().getPositionY();
        mSphereRigidBody.setEnable(false);
        gvrTestUtils.waitForXFrames(60);
        mWaiter.assertTrue( lastY == mSphereRigidBody.getTransform().getPositionY()); //ball1 stoped falling
        mWaiter.assertTrue( lastY2 > mSphereRigidBody2.getTransform().getPositionY()); //ball2 is falling

        lastY = mSphereRigidBody.getTransform().getPositionY();
        lastY2 =  mSphereRigidBody2.getTransform().getPositionY();
        mSphereRigidBody.setEnable(true);
        gvrTestUtils.waitForXFrames(10);
        mWaiter.assertTrue( lastY > mSphereRigidBody.getTransform().getPositionY()); //ball1 is falling again
        mWaiter.assertTrue( lastY2 > mSphereRigidBody2.getTransform().getPositionY()); //ball2 kept falling

    }

    private GVRSceneObject meshWithTexture(String mesh, String texture) {
        GVRSceneObject object = null;
        try {
            object = new GVRSceneObject(gvrTestUtils.getGvrContext(), new GVRAndroidResource(
                    gvrTestUtils.getGvrContext(), mesh), new GVRAndroidResource(gvrTestUtils.getGvrContext(),
                    texture));
        } catch (IOException e) {
            mWaiter.fail(e);
        }
        return object;
    }

    private void addSphere(GVRScene scene, GVRRigidBody sphereRigidBody, float radius, float x, float y,
                           float z, float mass) {

        GVRSceneObject sphereObject = meshWithTexture("sphere.obj",
                "sphere.jpg");
        sphereObject.getTransform().setPosition(x, y, z);

        // Collider
        GVRSphereCollider sphereCollider = new GVRSphereCollider(gvrTestUtils.getGvrContext());
        sphereCollider.setRadius(1.0f);
        sphereObject.attachCollider(sphereCollider);

        // Physics body
        sphereRigidBody.setMass(mass);
        sphereRigidBody.setRestitution(1.5f);
        sphereRigidBody.setFriction(0.5f);

        sphereObject.attachComponent(sphereRigidBody);

        scene.addSceneObject(sphereObject);
    }
}
