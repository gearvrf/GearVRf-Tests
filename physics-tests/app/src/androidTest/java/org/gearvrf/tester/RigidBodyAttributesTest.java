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
        //Assert.checkNotNull("GVRRigidBody", mSphereRigidBody);
        mWaiter.assertTrue(mSphereRigidBody.getMass() == 2.5f);
        mWaiter.assertTrue(mSphereRigidBody.getRestitution() == 1.5f);
        mWaiter.assertTrue(mSphereRigidBody.getFriction() == 0.5f);
        gvrTestUtils.getMainScene().removeSceneObject(mSphereRigidBody.getOwnerObject());
    }

    @Test
    public void updateRigidBody() throws Exception {
        GVRRigidBody mSphereRigidBody = new GVRRigidBody(gvrTestUtils.getGvrContext());
        addSphere(gvrTestUtils.getMainScene(), mSphereRigidBody, 1.0f, 1.5f, 40.0f, -10.0f, 2.5f);

        float[] lastPos = new float[4];
        float[] newPos = new float[4];
        float lastTransform = mSphereRigidBody.getOwnerObject().getTransform().getPositionY();

        lastPos[0] = mSphereRigidBody.getCenterX();
        lastPos[1] = mSphereRigidBody.getCenterY();
        lastPos[2] = mSphereRigidBody.getCenterZ();

        gvrTestUtils.waitForSceneRendering();
        mSphereRigidBody.applyCentralForce(-20.0f, 900.0f, 0.0f);
        gvrTestUtils.waitForXFrames(5 * 60);

        newPos[0] = mSphereRigidBody.getCenterX();
        newPos[1] = mSphereRigidBody.getCenterY();
        newPos[2] = mSphereRigidBody.getCenterZ();
        mWaiter.assertTrue(lastPos[0] == newPos[0] && lastPos[1] == newPos[1] && lastPos[2] == newPos[2]);

        lastPos[0] = mSphereRigidBody.getRotationW();
        lastPos[1] = mSphereRigidBody.getRotationX();
        lastPos[2] = mSphereRigidBody.getRotationY();
        lastPos[3] = mSphereRigidBody.getRotationZ();
        gvrTestUtils.waitForSceneRendering();

        mSphereRigidBody.applyTorque(5.0f, 0.5f, 0.0f);
        gvrTestUtils.waitForXFrames(5 * 60);

        newPos[0] = mSphereRigidBody.getRotationW();
        newPos[1] = mSphereRigidBody.getRotationX();
        newPos[2] = mSphereRigidBody.getRotationY();
        newPos[3] = mSphereRigidBody.getRotationZ();
        mWaiter.assertTrue(lastPos[0] == newPos[0] && lastPos[1] == newPos[1]);

        mWaiter.assertTrue(mSphereRigidBody.getOwnerObject().getTransform().getPositionY() != lastTransform);


    }

    private GVRSceneObject meshWithTexture(String mesh, String texture) {
        GVRSceneObject object = null;
        try {
            object = new GVRSceneObject(gvrTestUtils.getGvrContext(), new GVRAndroidResource(
                    gvrTestUtils.getGvrContext(), mesh), new GVRAndroidResource(gvrTestUtils.getGvrContext(),
                    texture));
        } catch (IOException e) {
            e.printStackTrace();
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
