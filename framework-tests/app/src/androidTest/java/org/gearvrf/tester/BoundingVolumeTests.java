package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;

import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRConeSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;

import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class BoundingVolumeTests {
    private static final float SQRT_2 = (float) Math.sqrt(2.0f);
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void testSimpleQuadBV() {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(
                context, R.drawable.gearvr_logo));
        GVRSceneObject sceneObject = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        sceneObject.getTransform().setPosition(0.0f, 0.0f, -5.0f);
        scene.addSceneObject(sceneObject);
        gvrTestUtils.waitForSceneRendering();
        GVRSceneObject.BoundingVolume boundingVolume = sceneObject.getBoundingVolume();
        Vector3f bvMin = boundingVolume.minCorner;
        Vector3f bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-2.5f, -2.5f, -5.0f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(2.5f, 2.5f, -5.0f), bvMax));
    }

    @Test
    public void testRotatedQuadBV() {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(
                context, R.drawable.gearvr_logo));
        GVRSceneObject sceneObject = new GVRSceneObject(context, 5.0f, 5.0f, texture);

        sceneObject.getTransform().setPosition(-5.0f, 0.0f, 0.0f);
        sceneObject.getTransform().rotateByAxis(+90.0f, 0.0f, 1.0f, 0.0f);
        scene.addSceneObject(sceneObject);
        gvrTestUtils.waitForSceneRendering();
        GVRSceneObject.BoundingVolume boundingVolume = sceneObject.getBoundingVolume();
        Vector3f bvMin = boundingVolume.minCorner;
        Vector3f bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-5.0f, -2.5f, -2.5f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(-5.0f, 2.5f, 2.5f), bvMax));
    }

    @Test
    public void testBoxBV() {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        Future<GVRTexture> texture = context.getAssetLoader().loadFutureTexture(new GVRAndroidResource(
                context, R.drawable.gearvr_logo));

        GVRCubeSceneObject cubeSceneObject = new GVRCubeSceneObject(context, true, texture);
        scene.addSceneObject(cubeSceneObject);
        gvrTestUtils.waitForSceneRendering();
        GVRSceneObject.BoundingVolume boundingVolume = cubeSceneObject.getBoundingVolume();
        Vector3f bvMin = boundingVolume.minCorner;
        Vector3f bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-0.5f, -0.5f, -0.5f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(0.5f, 0.5f, 0.5f), bvMax));
        // rotate by 45 degrees
        cubeSceneObject.getTransform().rotateByAxis(45.0f, 0.0f, 1.0f, 0.0f);

        boundingVolume = cubeSceneObject.getBoundingVolume();
        bvMin = boundingVolume.minCorner;
        bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-0.5f * SQRT_2, -0.5f, -0.5f * SQRT_2), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(0.5f * SQRT_2, 0.5f, 0.5f * SQRT_2), bvMax));
        // rotate by 45 degrees
        cubeSceneObject.getTransform().rotateByAxis(45.0f, 0.0f, 1.0f, 0.0f);
        boundingVolume = cubeSceneObject.getBoundingVolume();
        bvMin = boundingVolume.minCorner;
        bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-0.5f, -0.5f, -0.5f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(0.5f, 0.5f, 0.5f), bvMax));
    }

    @Test
    public void testScaledBV() {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        GVRConeSceneObject coneSceneObject = new GVRConeSceneObject(context, true);
        scene.addSceneObject(coneSceneObject);
        gvrTestUtils.waitForSceneRendering();

        GVRSceneObject.BoundingVolume boundingVolume = coneSceneObject.getBoundingVolume();

        Vector3f bvMin = boundingVolume.minCorner;
        Vector3f bvMax = boundingVolume.maxCorner;
        mWaiter.assertTrue(checkResult(new Vector3f(-0.5f, -0.5f, -0.5f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(0.5f, 0.5f, 0.5f), bvMax));

        coneSceneObject.getTransform().setScale(2.0f, 2.0f, 2.0f);

        boundingVolume = coneSceneObject.getBoundingVolume();
        bvMin = boundingVolume.minCorner;
        bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-1.0f, -1.0f, -1.0f), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(1.0f, 1.0f, 1.0f), bvMax));

        coneSceneObject.getTransform().rotateByAxis(45.0f, 1.0f, 0.0f, 0.0f);
        boundingVolume = coneSceneObject.getBoundingVolume();
        bvMin = boundingVolume.minCorner;
        bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-1.0f, -1.0f * SQRT_2, -1.0f * SQRT_2), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(1.0f, 1.0f * SQRT_2, 1.0f * SQRT_2), bvMax));
    }

    @Test
    public void testHierarchicalBV() {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        GVRSceneObject parent = new GVRSceneObject(context);
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(context, true);
        parent.addChildObject(sphereSceneObject);

        scene.addSceneObject(parent);
        gvrTestUtils.waitForSceneRendering();

        // rotate by 45 degrees
        sphereSceneObject.getTransform().rotateByAxis(45.0f, 0.0f, 1.0f, 0.0f);
        GVRSceneObject.BoundingVolume boundingVolume = parent.getBoundingVolume();

        Vector3f bvMin = boundingVolume.minCorner;
        Vector3f bvMax = boundingVolume.maxCorner;

        mWaiter.assertTrue(checkResult(new Vector3f(-1.0f * SQRT_2, -1.0f, -1.0f * SQRT_2), bvMin));
        mWaiter.assertTrue(checkResult(new Vector3f(1.0f * SQRT_2, 1.0f, 1.0f * SQRT_2), bvMax));
    }

    private boolean checkResult(Vector3f expected, Vector3f actual) {
        if (Math.abs(expected.x - actual.x) < 0.0001f && Math.abs(expected.y - actual.y) <
                0.0001f && Math.abs(expected.z - actual.z) < 0.0001f) {
            return true;
        }
        return false;
    }
}
