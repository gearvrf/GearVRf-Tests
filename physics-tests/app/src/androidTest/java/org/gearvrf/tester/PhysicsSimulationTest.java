package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBoxCollider;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.physics.ICollisionEvents;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// kinematic equation
// d = 0.5*g(m/s^2)*t(s)^2
// (d/(0.5*g))^0.5 = t
// Xframes = t * 60
// tested on note4, no other apps running
// time to fall 2800ms or 168 frames, rounded up

@RunWith(AndroidJUnit4.class)
public class PhysicsSimulationTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    GVRWorld world;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        mWaiter = new Waiter();

        GVRTestUtils.OnInitCallback initCallback = new GVRTestUtils.OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                gvrContext.getMainScene().getMainCameraRig().getTransform().setPosition(0.0f, 6.0f, 0.0f);
                world = new GVRWorld(gvrContext);
                gvrContext.getMainScene().getRoot().attachComponent(world);
            }
        };

        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity(), initCallback);
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void freeFallTest() throws Exception {
        CollisionHandler mCollisionHandler = new CollisionHandler();
        mCollisionHandler.extimatedTime = 2800; //... + round up
        mCollisionHandler.collisionCounter = 0;

        GVRSceneObject cube = addCube(gvrTestUtils.getMainScene(), 0.0f, 1.0f, -10.0f, 0.0f);
        GVRSceneObject sphere = addSphere(gvrTestUtils.getMainScene(), mCollisionHandler, 0.0f, 40.0f, -10.0f, 1.0f);

        mCollisionHandler.startTime = System.currentTimeMillis();
        gvrTestUtils.waitForXFrames(168 + 60);

        //Log.d("PHYSICS", "    Delta time of the last collisions:" + mCollisionHandler.lastCollisionTime);
        //mWaiter.assertTrue(mCollisionHandler.lastCollisionTime <= mCollisionHandler.extimatedTime);

        float d = (sphere.getTransform().getPositionY() - cube.getTransform().getPositionY()); //sphere is on top of the cube
        mWaiter.assertTrue( d <= 1.6f);
        //Log.d("PHYSICS", "    Number of collisions:" + mCollisionHandler.collisionCounter);
        mWaiter.assertTrue(mCollisionHandler.collisionCounter >= 1);

        gvrTestUtils.waitForXFrames(120);
    }

    @Test
    public void spacedFreeFallTest() throws Exception {
        OnTestStartRenderCallback beginCallback = new OnTestStartRenderCallback();
        gvrTestUtils.setOnRenderCallback(beginCallback);

        for(int i = 0; i < beginCallback.lenght; i++) {
            gvrTestUtils.waitForSceneRendering();
            gvrTestUtils.waitForXFrames(1);
        }
        gvrTestUtils.waitForSceneRendering();
        gvrTestUtils.setOnRenderCallback(null);

        beginCallback.mCollisionHandler.startTime = System.currentTimeMillis();
        gvrTestUtils.waitForXFrames(168);

        runTest(beginCallback.sphere, beginCallback.cube, beginCallback.lenght);

        //It is complicated to try to predict this timing depends on the device state, should be almost the same as the above
        //Log.d("PHYSICS", "    Delta time of the last collisions:" + beginCallback.mCollisionHandler.lastCollisionTime);
        //mWaiter.assertTrue(beginCallback.mCollisionHandler.lastCollisionTime <= beginCallback.mCollisionHandler.extimatedTime);

        //asserts can not happen on collision event, but we get an idea here
        //Log.d("PHYSICS", "    Number of collisions:" + beginCallback.mCollisionHandler.collisionCounter);
        mWaiter.assertTrue(beginCallback.mCollisionHandler.collisionCounter >= beginCallback.lenght);

        gvrTestUtils.waitForXFrames(120);
    }

    @Test
    public void simultaneousFreeFallTest() throws Exception {
        OnTestStartRenderAllCallback beginCallback = new OnTestStartRenderAllCallback();
        gvrTestUtils.setOnRenderCallback(beginCallback);
        gvrTestUtils.waitForSceneRendering();
        gvrTestUtils.setOnRenderCallback(null);
        beginCallback.mCollisionHandler.startTime = System.currentTimeMillis();
        gvrTestUtils.waitForXFrames(168 + 60 ); // Too many simultaneous events triggered need more time to process all???

        runTest(beginCallback.sphere, beginCallback.cube, beginCallback.lenght);

        //It is complicated to try to predict this timing
        //Log.d("PHYSICS", "    Delta time of the last collisions:" + mCollisionHandler.lastCollisionTime);
        //mWaiter.assertTrue(mCollisionHandler.lastCollisionTime <= mCollisionHandler.extimatedTime);

        //asserts can not happen on collision event, but we get an idea here
        //Log.d("PHYSICS", "    Number of collisions:" + beginCallback.mCollisionHandler.collisionCounter);
        mWaiter.assertTrue(beginCallback.mCollisionHandler.collisionCounter >= beginCallback.lenght);

        gvrTestUtils.waitForXFrames(120);
    }

    class OnTestStartRenderCallback implements GVRTestUtils.OnRenderCallback {
        public int lenght;
        public GVRSceneObject cube[];
        public GVRSceneObject sphere[];
        public CollisionHandler mCollisionHandler;
        int currentIndex;
        int k;

        OnTestStartRenderCallback () {
            cube = new GVRSceneObject[100];
            sphere  = new GVRSceneObject[100];
            mCollisionHandler = new CollisionHandler();
            mCollisionHandler.extimatedTime = 2800; //... + round up
            mCollisionHandler.collisionCounter = 0;
            currentIndex = 0;
            k = -100;
            lenght = 100;
        }

        @Override
        public void onSceneRendered() {
            k += 2;
            cube[currentIndex] = addCube(gvrTestUtils.getMainScene(), (float)k, 1.0f, -10.0f, 0.0f);
            sphere[currentIndex] = addSphere(gvrTestUtils.getMainScene(), mCollisionHandler, (float)k, 40.0f, -10.0f, 1.0f);
            currentIndex++;
        }
    }

    class OnTestStartRenderAllCallback implements GVRTestUtils.OnRenderCallback {
        public int lenght;
        public GVRSceneObject cube[];
        public GVRSceneObject sphere[];
        public CollisionHandler mCollisionHandler;

        OnTestStartRenderAllCallback () {
            cube = new GVRSceneObject[100];
            sphere  = new GVRSceneObject[100];
            mCollisionHandler = new CollisionHandler();
            mCollisionHandler.extimatedTime = 2800; //... + round up
            mCollisionHandler.collisionCounter = 0;
            lenght = 100;
        }

        @Override
        public void onSceneRendered() {
            world.setEnable(false);
            int k = -250;
            for(int i = 0; i < lenght; i++) {
                k += 5;
                cube[i] = addCube(gvrTestUtils.getMainScene(), (float)k, 1.0f, -10.0f, 0.0f);
                sphere[i] = addSphere(gvrTestUtils.getMainScene(), mCollisionHandler, (float)k, 40.0f, -10.0f, 1.0f);
            }
            world.setEnable(true);
        }
    }

    public void runTest(GVRSceneObject sphere[], GVRSceneObject cube[], int lenght) throws  Exception{
        world.setEnable(false);
        for(int i = 0; i < lenght; i++) {
            float d = (sphere[i].getTransform().getPositionY() - cube[i].getTransform().getPositionY());
            //sphere is on top of the cube
            mWaiter.assertTrue( d <= 1.6f);
            mWaiter.assertTrue(sphere[i].getTransform().getPositionX() == cube[i].getTransform().getPositionX());
            mWaiter.assertTrue(sphere[i].getTransform().getPositionZ() == cube[i].getTransform().getPositionZ());
            //Log.d("PHYSICS", "    Index:" + i + "    Collision distance:" + d);
        }
    }

    public class CollisionHandler implements ICollisionEvents {
        public long startTime;
        public long extimatedTime;
        public long lastCollisionTime;
        public int collisionCounter;

        public void onEnter(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
            lastCollisionTime = System.currentTimeMillis() - startTime;
            collisionCounter++;
        }

        public void onExit(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
        }

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

    /*
     * Function to add a sphere of dimension and position specified in the
     * Bullet physics world and scene graph
     */
    private GVRSceneObject addSphere(GVRScene scene, ICollisionEvents mCollisionHandler, float x, float y, float z, float mass) {


        GVRSceneObject sphereObject = meshWithTexture("sphere.obj",
                "sphere.jpg");
        sphereObject.getTransform().setScaleX(0.5f);
        sphereObject.getTransform().setScaleY(0.5f);
        sphereObject.getTransform().setScaleZ(0.5f);
        sphereObject.getTransform().setPosition(x, y, z);

        // Collider
        GVRSphereCollider sphereCollider = new GVRSphereCollider(gvrTestUtils.getGvrContext());
        sphereCollider.setRadius(0.5f);
        sphereObject.attachCollider(sphereCollider);

        // Physics body
        GVRRigidBody mSphereRigidBody = new GVRRigidBody(gvrTestUtils.getGvrContext());

        mSphereRigidBody.setMass(mass);
        sphereObject.getEventReceiver().addListener(mCollisionHandler);

        sphereObject.attachComponent(mSphereRigidBody);

        scene.addSceneObject(sphereObject);
        return sphereObject;
    }

    private GVRSceneObject addCube(GVRScene scene, float x, float y, float z, float mass) {

        GVRSceneObject cubeObject = meshWithTexture("cube.obj", "cube.jpg");

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
}
