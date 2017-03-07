package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.physics.GVRCollisionMatrix;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.physics.ICollisionEvents;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Assert;
import org.gearvrf.utility.Exceptions;
import org.gearvrf.utility.Log;
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
public class PhysicsCollisionTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private GVRCollisionMatrix mCollisionMatrix;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        mWaiter = new Waiter();
        mCollisionMatrix = new GVRCollisionMatrix();

        GVRTestUtils.OnInitCallback initCallback = new GVRTestUtils.OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                GVRWorld world = new GVRWorld(gvrContext, mCollisionMatrix);
                gvrContext.getMainScene().getRoot().attachComponent(world);
            }
        };

        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity(), initCallback);
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void testCollisionMask() throws Exception {

        for (int groupA = 0; groupA < 16; groupA++) {
            for (int groupB = 0; groupB < 16; groupB++) {
                if (groupA == groupB) {
                    continue;
                }

                mCollisionMatrix.enableCollision(groupA, groupB);

                if ((GVRCollisionMatrix.getCollisionFilterGroup(groupA)
                        & mCollisionMatrix.getCollisionFilterMask(groupB)) == 0
                        || (GVRCollisionMatrix.getCollisionFilterGroup(groupB)
                        & mCollisionMatrix.getCollisionFilterMask(groupA)) == 0) {
                    throw Exceptions.IllegalArgument("Failed to enable collision between groups "
                                                        + groupA + " and " + groupB);
                }

                mCollisionMatrix.disableCollision(groupA, groupB);

                if ((GVRCollisionMatrix.getCollisionFilterGroup(groupA)
                        & mCollisionMatrix.getCollisionFilterMask(groupB)) != 0
                        || (GVRCollisionMatrix.getCollisionFilterGroup(groupB)
                        & mCollisionMatrix.getCollisionFilterMask(groupA)) != 0) {
                    throw Exceptions.IllegalArgument("Failed to disable collision between groups "
                                                        + groupA + " and " + groupB);
                }
            }
        }
    }

    @Test
    public void testCollisionEvent() throws Exception  {

        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        GVRSphereSceneObject sphereA = new GVRSphereSceneObject(context);
        GVRSphereSceneObject sphereB = new GVRSphereSceneObject(context);
        GVRSphereCollider colliderA = new GVRSphereCollider(context);
        GVRSphereCollider colliderB = new GVRSphereCollider(context);
        GVRRigidBody bodyA = new GVRRigidBody(context, 3.0f, 0);
        GVRRigidBody bodyB = new GVRRigidBody(context, 0.0f, 1);

        mCollisionMatrix.enableCollision(0, 1);

        CollisionHandler collisionHandler = new CollisionHandler();

        sphereA.getTransform().setPosition(0.5f, 3.0f, 0.0f);
        sphereB.getTransform().setPosition(0.0f, 0.0f, 0.0f);

        sphereA.getEventReceiver().addListener(collisionHandler);

        colliderA.setRadius(1.0f);
        colliderB.setRadius(1.0f);

        bodyA.setRestitution(1.5f);
        bodyA.setFriction(0.5f);

        bodyB.setRestitution(0.5f);
        bodyB.setFriction(0.5f);

        sphereA.attachCollider(colliderA);
        sphereB.attachCollider(colliderB);

        sphereA.attachComponent(bodyA);
        sphereB.attachComponent(bodyB);

        scene.addSceneObject(sphereA);
        scene.addSceneObject(sphereB);

        mWaiter.assertTrue(collisionHandler.waitForCollision(5 * 60 * 1000));
    }

    public class CollisionHandler implements ICollisionEvents {
        private final Object mCollisionLock;
        private boolean mCollisionEnter;

        CollisionHandler() {
            mCollisionLock = new Object();
            mCollisionEnter = false;
        }

        public void onEnter(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
            synchronized (mCollisionLock) {
                mCollisionEnter = true;
                mCollisionLock.notifyAll();
            }
        }

        public void onExit(GVRSceneObject sceneObj0, GVRSceneObject sceneObj1, float normal[], float distance) {
        }

        boolean waitForCollision(long timeout) {
            synchronized (mCollisionLock) {
                try {
                    mCollisionLock.wait(timeout);
                } catch (InterruptedException e) {}
            }
            return mCollisionEnter;
        }

    }
}
