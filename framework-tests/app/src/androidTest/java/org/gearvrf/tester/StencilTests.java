package org.gearvrf.tester;

import android.graphics.Color;
import android.opengl.GLES30;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class StencilTests
{
    private static final String TAG = StencilTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private boolean mDoCompare = true;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown()
    {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }
    @Before
    public void setUp() throws TimeoutException
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();
    }

    @Test
    public void testBasic() throws TimeoutException, IOException {
        GVRScene scene = mTestUtils.getMainScene();

        GVRCameraRig mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getLeftCamera().setBackgroundColor(Color.WHITE);
        mainCameraRig.getRightCamera().setBackgroundColor(Color.WHITE);

        GVRSceneObject testObject1 = makeTestObject();
        testObject1.getTransform().setPosition(1, 0, -3);
        scene.addSceneObject(testObject1);

        GVRSceneObject testObject2 = makeTestObject();
        testObject2.getTransform().setPosition(-1, 0, -3);
        scene.addSceneObject(testObject2);

        GVRSceneObject testObject3 = makeTestObject();
        testObject3.getTransform().setPosition(0, 1, -3);
        scene.addSceneObject(testObject3);

        GVRSceneObject testObject4 = makeTestObject();
        testObject4.getTransform().setPosition(0, -1, -3);
        scene.addSceneObject(testObject4);

        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testBasicReference", mWaiter, mDoCompare);
    }

    GVRSceneObject makeTestObject() throws IOException {
        final GVRContext gvrContext = mTestUtils.getGvrContext();
        GVRSceneObject parent = new GVRSceneObject(gvrContext);

        GVRTexture texture = gvrContext.getAssetLoader().loadTexture(new GVRAndroidResource(gvrContext, R.drawable.white_texture));
        GVRSceneObject sphere = new GVRSphereSceneObject(gvrContext, true);
        sphere.getRenderData().getMaterial().setMainTexture(texture);

        sphere.getRenderData()
                .setRenderingOrder(GVRRenderData.GVRRenderingOrder.STENCIL)
                .setStencilTest(true)
                .setStencilFunc(GLES30.GL_ALWAYS, 1, 0xFF)
                .setStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_REPLACE)
                .setStencilMask(0xFF);
        sphere.getTransform().setScale(0.5f, 0.5f, 0.5f);

        parent.addChildObject(sphere);

        GVRTexture quad = gvrContext.getAssetLoader().loadTexture(new GVRAndroidResource(gvrContext, "StencilTests/GearVR.jpg"));
        GVRSceneObject background = new GVRSceneObject(gvrContext, 1.2f, 0.7f, quad);
        background.getTransform().setScale(2,2,2);

        background.getRenderData()
                .setStencilTest(true)
                .setStencilFunc(GLES30.GL_EQUAL, 1, 0xFF)
                .setStencilMask(0x00);
        parent.addChildObject(background);

        return parent;
    }

}
