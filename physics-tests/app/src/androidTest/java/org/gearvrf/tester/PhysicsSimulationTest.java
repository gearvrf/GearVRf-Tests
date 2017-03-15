package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRBoxCollider;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.physics.ICollisionEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

// kinematic equation
// d = 0.5*g(m/s^2)*t(s)^2 
// (d/(0.5*g))^0.5 = t
// Xframes = t * 60

@RunWith(AndroidJUnit4.class)
public class PhysicsSimulationTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private boolean passedCollider;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        mWaiter = new Waiter();

        GVRTestUtils.OnInitCallback initCallback = new GVRTestUtils.OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                GVRWorld world = new GVRWorld(gvrContext);
                gvrContext.getMainScene().getRoot().attachComponent(world);
            }
        };

        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity(), initCallback);
        gvrTestUtils.waitForOnInit();
        passedCollider = false;
    }

    @Test
    public void freeFallTest() throws Exception {
        GVRSceneObject cube = addCube(gvrTestUtils.getMainScene(), 0.0f, 1.0f, -10.0f, 0.0f);
        GVRSceneObject sphere = addSphere(gvrTestUtils.getMainScene(), 0.0f, 40.0f, -10.0f, 1.0f);
        gvrTestUtils.waitForXFrames(168);
        float d = (sphere.getTransform().getPositionY() - cube.getTransform().getPositionY()); //sphere is on top of the cube
        mWaiter.assertTrue( d <= 1.6f);
        //asserts on collision event
        mWaiter.assertTrue(passedCollider);
    }

    public class CollisionHandler implements ICollisionEvents {
        public long startTime;
        public long extimatedTime;

        public void onEnter(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
            long t = System.currentTimeMillis() - startTime;

            mWaiter.assertFalse(distance > 0.0f);
            mWaiter.assertTrue(t <= extimatedTime);
            passedCollider = true;
        }

        public void onExit(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
            mWaiter.assertFalse(distance < 0.0f);
        }

    }

    private GVRSceneObject addCube(GVRScene scene, float x, float y, float z, float mass) {

        GVRSceneObject cubeObject = new GVRCubeSceneObject(gvrTestUtils.getGvrContext());
        cubeObject.getTransform().setPosition(x, y, z);

        // Collider
        GVRBoxCollider boxCollider = new GVRBoxCollider(gvrTestUtils.getGvrContext());
        boxCollider.setHalfExtents(0.5f, 0.5f, 0.5f);
        cubeObject.attachCollider(boxCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrTestUtils.getGvrContext());

        body.setMass(mass);

        cubeObject.attachComponent(body);

        scene.addSceneObject(cubeObject);
        return cubeObject;
    }

    /*
     * Function to add a sphere of dimension and position specified in the
     * Bullet physics world and scene graph
     */
    private GVRSceneObject addSphere(GVRScene scene, float x, float y, float z, float mass) {
        CollisionHandler mCollisionHandler = new CollisionHandler();
        mCollisionHandler.startTime = System.currentTimeMillis();
        mCollisionHandler.extimatedTime = 2800; //... + round up

        GVRSceneObject sphereObject = new GVRSphereSceneObject(gvrTestUtils.getGvrContext());
        sphereObject.getTransform().setPosition(x, y, z);

        // Collider
        GVRSphereCollider sphereCollider = new GVRSphereCollider(gvrTestUtils.getGvrContext());
        sphereCollider.setRadius(1.0f);
        sphereObject.attachCollider(sphereCollider);

        // Physics body
        GVRRigidBody mSphereRigidBody = new GVRRigidBody(gvrTestUtils.getGvrContext());

        mSphereRigidBody.setMass(mass);
        sphereObject.getEventReceiver().addListener(mCollisionHandler);

        sphereObject.attachComponent(mSphereRigidBody);

        scene.addSceneObject(sphereObject);
        return sphereObject;
    }
}
