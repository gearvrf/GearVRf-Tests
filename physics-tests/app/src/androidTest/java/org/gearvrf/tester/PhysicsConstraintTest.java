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
import org.gearvrf.physics.GVRConeTwistConstraint;
import org.gearvrf.physics.GVRFixedConstraint;
import org.gearvrf.physics.GVRGenericConstraint;
import org.gearvrf.physics.GVRHingeConstraint;
import org.gearvrf.physics.GVRPoint2PointConstraint;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRSliderConstraint;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
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
        GVRSceneObject ground = addGround(gvrTestUtils.getMainScene(), 0f, 0f, -15f);

        GVRSceneObject box1 = addCube(gvrTestUtils.getMainScene(), 0f, 0.5f, -30f, 1.0f);
        ((GVRRigidBody)box1.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRSceneObject box2 = addCube(gvrTestUtils.getMainScene(), 0f, 0.5f, -15f, 1.0f);
        ((GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRFixedConstraint constraint = new GVRFixedConstraint(gvrTestUtils.getGvrContext(), (GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType()));
        box1.attachComponent(constraint);

        gvrTestUtils.waitForXFrames(30);
        float distance = transformsDistance(box1.getTransform(), box2.getTransform());

        ((GVRRigidBody)box1.getComponent(GVRRigidBody.getComponentType())).applyTorque(0, 0, 200);
        gvrTestUtils.waitForXFrames(120);
        float rotation = Math.abs(box1.getTransform().getRotationX() - box2.getTransform().getRotationX())
                + Math.abs(box1.getTransform().getRotationY() - box2.getTransform().getRotationY())
                + Math.abs(box1.getTransform().getRotationZ() - box2.getTransform().getRotationZ());
        mWaiter.assertTrue(rotation < 0.2f);
        mWaiter.assertTrue(Math.abs(distance - transformsDistance(box1.getTransform(), box2.getTransform())) < 0.2);

        ((GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(300,0,300);
        gvrTestUtils.waitForXFrames(180);
        rotation = Math.abs(box1.getTransform().getRotationX() - box2.getTransform().getRotationX())
                + Math.abs(box1.getTransform().getRotationY() - box2.getTransform().getRotationY())
                + Math.abs(box1.getTransform().getRotationZ() - box2.getTransform().getRotationZ());
        mWaiter.assertTrue(rotation < 0.2f);
        mWaiter.assertTrue(Math.abs(distance - transformsDistance(box1.getTransform(), box2.getTransform())) < 0.2);

        gvrTestUtils.waitForXFrames(30);
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

    @Test
    public void hingeConstraintTest() throws Exception {
        float pivotInA[] = {0f, -3f, 0f};
        float pivotInB[] = {0f, 3f, 0f};
        float axisInA[] = {1f, 0f, 0f};
        float axisInB[] = {1f, 0f, 0f};

        GVRSceneObject ball = addSphere(gvrTestUtils.getMainScene(), 0.0f, 10.0f, -10.0f, 0.0f);
        GVRSceneObject box = addCube(gvrTestUtils.getMainScene(), 0.0f, 4.0f, -10.0f, 1.0f);

        GVRRigidBody boxBody = (GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType());
        boxBody.setSimulationType(GVRRigidBody.DYNAMIC);

        GVRHingeConstraint constraint = new GVRHingeConstraint(gvrTestUtils.getGvrContext(),
                boxBody, pivotInA, pivotInB, axisInA, axisInB);
        constraint.setLimits(-1f, 1f);
        ball.attachComponent(constraint);

        final float maxDistabceX = 0.000001f;
        final float minDistanceY = 3.f + 1.62f;
        final float maxDIstanceZ = 2.52f;

        gvrTestUtils.waitForXFrames(30);

        boxBody.applyCentralForce(0f, 0f, 1000f); // Must move towards camera
        gvrTestUtils.waitForXFrames(180);
        float dx = Math.abs(ball.getTransform().getPositionX() - box.getTransform().getPositionX());
        float dy = Math.abs(ball.getTransform().getPositionY() - box.getTransform().getPositionY());
        float dz = Math.abs(ball.getTransform().getPositionZ() - box.getTransform().getPositionZ());
        mWaiter.assertTrue(dx < maxDistabceX && dy > minDistanceY && dz < maxDIstanceZ);

        boxBody.applyCentralForce(0f, 1000f, 0f); // May have some effect
        gvrTestUtils.waitForXFrames(180);
        dx = Math.abs(ball.getTransform().getPositionX() - box.getTransform().getPositionX());
        dy = Math.abs(ball.getTransform().getPositionY() - box.getTransform().getPositionY());
        dz = Math.abs(ball.getTransform().getPositionZ() - box.getTransform().getPositionZ());
        mWaiter.assertTrue(dx < maxDistabceX && dy > minDistanceY && dz < maxDIstanceZ);

        boxBody.applyCentralForce(1000f, 0f, 0f); // Must have no effect
        gvrTestUtils.waitForXFrames(180);
        dx = Math.abs(ball.getTransform().getPositionX() - box.getTransform().getPositionX());
        dy = Math.abs(ball.getTransform().getPositionY() - box.getTransform().getPositionY());
        dz = Math.abs(ball.getTransform().getPositionZ() - box.getTransform().getPositionZ());
        mWaiter.assertTrue(dx < maxDistabceX && dy > minDistanceY && dz < maxDIstanceZ);
    }

    @Test
    public void sliderConstraintTest() throws Exception {
        GVRSceneObject ground = addGround(gvrTestUtils.getMainScene(), 0f, 0f, -15f);

        GVRSceneObject box1 = addCube(gvrTestUtils.getMainScene(), 3.0f, 0.5f, -15.0f, 1.0f);
        ((GVRRigidBody)box1.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRSceneObject box2 = addCube(gvrTestUtils.getMainScene(), -2.0f, 0.5f, -15.0f, 1.0f);
        ((GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRSliderConstraint constraint = new GVRSliderConstraint(gvrTestUtils.getGvrContext(), (GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType()));
        constraint.setAngularLowerLimit(-2f);
        constraint.setAngularUpperLimit(2f);
        constraint.setLinearLowerLimit(-5f);
        constraint.setLinearUpperLimit(-2f);
        box1.attachComponent(constraint);

        gvrTestUtils.waitForXFrames(30);

        ((GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(400f, 0f, 0f);
        gvrTestUtils.waitForXFrames(180);
        float d = transformsDistance(box1.getTransform(), box2.getTransform());
        mWaiter.assertTrue(d >= 2.0f && d <= 5.0f);

        ((GVRRigidBody)box2.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(-500f, 0f, 0f);
        gvrTestUtils.waitForXFrames(180);
        d = transformsDistance(box1.getTransform(), box2.getTransform());
        mWaiter.assertTrue(d >= 2.0f && d <= 5.0f);

        ((GVRRigidBody)box1.getComponent(GVRRigidBody.getComponentType())).applyTorque(100f, 0f, 0f);
        gvrTestUtils.waitForXFrames(180);
    }

    @Test
    public void ConeTwistConstraintTest() throws Exception {
        GVRSceneObject box = addCube(gvrTestUtils.getMainScene(), 0f, -5f, -15f, 0f);

        GVRSceneObject ball = addSphere(gvrTestUtils.getMainScene(), 0, 5f, -15f, 1f);

        float pivot[] = {0f, -5f, 0f};
        float rotation[] = {0f, -1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f};

        GVRConeTwistConstraint constraint = new GVRConeTwistConstraint(gvrTestUtils.getGvrContext(),
                (GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType()), pivot,
                rotation, rotation);

        ball.attachComponent(constraint);

        final float maxDistance = (float)(Math.sin(Math.PI * 0.375) * 10.0);

        ((GVRRigidBody)ball.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(100f, 0f, 100f);
        gvrTestUtils.waitForXFrames(180);
        mWaiter.assertTrue(maxDistance >= transformsDistance(ball.getTransform(), box.getTransform()));

        ((GVRRigidBody)ball.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(-500f, 0f, 0f);
        gvrTestUtils.waitForXFrames(180);
        mWaiter.assertTrue(maxDistance >= transformsDistance(ball.getTransform(), box.getTransform()));
    }

    @Test
    public void GenericConstraintTest() throws Exception {
        GVRSceneObject ground = addGround(gvrTestUtils.getMainScene(), 0f, -0.5f, -15f);

        GVRSceneObject box = addCube(gvrTestUtils.getMainScene(), -3f, 0f, -10f, 1f);
        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).setSimulationType(GVRRigidBody.DYNAMIC);

        GVRSceneObject ball = addSphere(gvrTestUtils.getMainScene(), 3f, 0f, -10f, 1f);

        final float joint[] = {-6f, 0f, 0f};
        final float rotation[] = {1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};

        GVRGenericConstraint constraint = new GVRGenericConstraint(
                gvrTestUtils.getGvrContext(), (GVRRigidBody)box.getComponent(
                        GVRRigidBody.getComponentType()), joint, rotation, rotation);
        constraint.setAngularLowerLimits((float)-Math.PI, (float)-Math.PI, (float)-Math.PI);
        constraint.setAngularUpperLimits((float)Math.PI, (float)Math.PI, (float)Math.PI);
        constraint.setLinearLowerLimits(-3f, -10f, -3f);
        constraint.setLinearUpperLimits(3f, 10f, 3f);

        ball.attachComponent(constraint);

        gvrTestUtils.waitForXFrames(30);

        float anchor[] = {ball.getTransform().getPositionX(), ball.getTransform().getPositionY(),
                ball.getTransform().getPositionZ()};
        float offsetLimit = 0.005f;

        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(100f, 200f, 100f);
        gvrTestUtils.waitForXFrames(90);
        mWaiter.assertTrue(checkTransformOffset(ball.getTransform(), anchor, offsetLimit));

        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(-100f, 200f, -100f);
        gvrTestUtils.waitForXFrames(90);
        mWaiter.assertTrue(checkTransformOffset(ball.getTransform(), anchor, offsetLimit));

        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).applyTorque(0f, 1000f, 0f);
        gvrTestUtils.waitForXFrames(180);
        mWaiter.assertTrue(checkTransformOffset(ball.getTransform(), anchor, offsetLimit));

        ((GVRRigidBody)box.getComponent(GVRRigidBody.getComponentType())).applyCentralForce(-1000f, 500f, 500f);
        gvrTestUtils.waitForXFrames(180);
        mWaiter.assertTrue(!checkTransformOffset(ball.getTransform(), anchor, offsetLimit));
    }

    /*
    * Function to add a sphere of dimension and position specified in the
    * Bullet physics world and scene graph
    */
    private GVRSceneObject addSphere(GVRScene scene, float x, float y, float z, float mass) {

        if (sphereMesh == null) {
            try {
                sphereMesh = gvrTestUtils.getGvrContext().getAssetLoader().loadMesh(
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
                cubeMesh = gvrTestUtils.getGvrContext().getAssetLoader().loadMesh(
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

    private GVRSceneObject addGround(GVRScene scene, float x, float y, float z) {

        GVRSceneObject groundObject = new GVRCubeSceneObject(gvrTestUtils.getGvrContext());

        groundObject.getTransform().setScale(100f, 0.5f, 100f);
        groundObject.getTransform().setPosition(x, y, z);

        GVRBoxCollider boxCollider = new GVRBoxCollider(gvrTestUtils.getGvrContext());
        boxCollider.setHalfExtents(0.5f, 0.5f, 0.5f);
        groundObject.attachCollider(boxCollider);

        GVRRigidBody body = new GVRRigidBody(gvrTestUtils.getGvrContext());
        body.setMass(0f);
        groundObject.attachComponent(body);

        scene.addSceneObject(groundObject);

        return groundObject;
    }

    static float transformsDistance(GVRTransform a, GVRTransform b) {
        float x = a.getPositionX() - b.getPositionX();
        float y = a.getPositionY() - b.getPositionY();
        float z = a.getPositionZ() - b.getPositionZ();

        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    static boolean checkTransformOffset(GVRTransform tr, float comp[], float limit) {
        return Math.abs(tr.getPositionX() - comp[0]) < limit
                && Math.abs(tr.getPositionY() - comp[1]) < limit
                && Math.abs(tr.getPositionZ() - comp[2]) < limit;
    }
}
