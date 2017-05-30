package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBoxCollider;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.physics.GVRFixedConstraint;
import org.gearvrf.physics.GVRPoint2PointConstraint;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PhysicsConstraintTest {

    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    GVRWorld world;

    private GVRMesh cubeMesh = null;
    private GVRTexture cubeTexture = null;

    private GVRMesh sphereMesh = null;
    private GVRTexture sphereTexture = null;

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
    public void fixedConstraintTest() throws Exception {

        GVRSceneObject cube = addCube(gvrTestUtils.getMainScene(), 1.0f, 10.0f, -10.0f, 1.0f);
        GVRSceneObject sphere = addSphere(gvrTestUtils.getMainScene(), 1.0f, 14.0f, -10.0f, 1.0f);
        GVRFixedConstraint constraint = new GVRFixedConstraint(gvrTestUtils.getGvrContext(), (GVRRigidBody)sphere.getComponent(GVRRigidBody.getComponentType()));
        cube.attachComponent(constraint);
        //gvrTestUtils.waitForXFrames(60);
        float d = (sphere.getTransform().getPositionY() - cube.getTransform().getPositionY()); //sphere is on top of the cube
        ((GVRRigidBody)cube.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(1000,0,0);
        gvrTestUtils.waitForXFrames(60);
        mWaiter.assertTrue((sphere.getTransform().getPositionX() == cube.getTransform().getPositionX()));
        mWaiter.assertTrue( d - (sphere.getTransform().getPositionY() - cube.getTransform().getPositionY()) < 1.0f);

        ((GVRRigidBody)sphere.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(-1000,0,0);
        gvrTestUtils.waitForXFrames(60);
        mWaiter.assertTrue((sphere.getTransform().getPositionX() == cube.getTransform().getPositionX()));
        mWaiter.assertTrue( d - (sphere.getTransform().getPositionY() - cube.getTransform().getPositionY()) < 1.0f);

        gvrTestUtils.waitForXFrames(60);
    }

    @Test
    public void point2pointConstraintTest() throws Exception {
        float pivotInA[] = {0f, -1.5f, 0f};
        float pivotInB[] = {-8f, -1.5f, 0f};

        GVRSceneObject ball = addSphere(gvrTestUtils.getMainScene(), 0.0f, 10.0f, -10.0f, 0.0f);
        GVRSceneObject box = addCube(gvrTestUtils.getMainScene(), 8.0f, 10.0f, -10.0f, 1.0f);
        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRPoint2PointConstraint constraint = new GVRPoint2PointConstraint(gvrTestUtils.getGvrContext(), (GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType()), pivotInA, pivotInB);
        ball.attachComponent(constraint);

        gvrTestUtils.waitForXFrames(30);

        gvrTestUtils.waitForXFrames(60);
        float distance = transformsDistance(ball.getTransform(), box.getTransform());
        mWaiter.assertTrue(distance < 9.8);

        gvrTestUtils.waitForXFrames(60);
        distance = transformsDistance(ball.getTransform(), box.getTransform());
        mWaiter.assertTrue(distance < 9.5);

        gvrTestUtils.waitForXFrames(60);
    }

    /*
    * Function to add a sphere of dimension and position specified in the
    * Bullet physics world and scene graph
    */
    private GVRSceneObject addSphere(GVRScene scene, float x, float y, float z, float mass) {

        if (sphereMesh == null) {
            try {
                sphereMesh = gvrTestUtils.getGvrContext().loadMesh(
                        new GVRAndroidResource(gvrTestUtils.getGvrContext(), "sphere.obj"));
                sphereTexture = gvrTestUtils.getGvrContext().getAssetLoader().loadTexture(
                        new GVRAndroidResource(gvrTestUtils.getGvrContext(), "sphere.jpg"));
            } catch (IOException e) {

            }
        }

        GVRSceneObject sphereObject = new GVRSceneObject(gvrTestUtils.getGvrContext(), sphereMesh, sphereTexture);

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

        sphereObject.attachComponent(mSphereRigidBody);

        scene.addSceneObject(sphereObject);
        return sphereObject;
    }

    private GVRSceneObject addCube(GVRScene scene, float x, float y, float z, float mass) {

        if (cubeMesh == null) {
            try {
                cubeMesh = gvrTestUtils.getGvrContext().loadMesh(
                        new GVRAndroidResource(gvrTestUtils.getGvrContext(), "cube.obj"));
                cubeTexture = gvrTestUtils.getGvrContext().getAssetLoader().loadTexture(
                        new GVRAndroidResource(gvrTestUtils.getGvrContext(), "cube.jpg"));
            } catch (IOException e) {

            }
        }

        GVRSceneObject cubeObject = new GVRSceneObject(gvrTestUtils.getGvrContext(), cubeMesh, cubeTexture);

        cubeObject.getTransform().setPosition(x, y, z);

        // Collider
        GVRBoxCollider boxCollider = new GVRBoxCollider(gvrTestUtils.getGvrContext());
        boxCollider.setHalfExtents(0.5f, 0.5f, 0.5f);
        cubeObject.attachCollider(boxCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrTestUtils.getGvrContext());
        body.setMass(mass);
        body.setSimulationType(GVRRigidBody.KINEMATIC);

        cubeObject.attachComponent(body);

        scene.addSceneObject(cubeObject);
        return cubeObject;
    }

    public static float transformsDistance(GVRTransform a, GVRTransform b) {
        float x = a.getPositionX() - b.getPositionX();
        float y = a.getPositionY() - b.getPositionY();
        float z = a.getPositionZ() - b.getPositionZ();

        return (float)Math.sqrt(x * x + y * y + z * z);
    }
}
