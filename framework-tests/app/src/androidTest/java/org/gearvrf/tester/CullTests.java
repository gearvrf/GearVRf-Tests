package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class CullTests
{
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private boolean mDoCompare = true;

    public CullTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);


    @After
    public void tearDown()
    {
        GVRScene scene = gvrTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
        GVRContext ctx  = gvrTestUtils.getGvrContext();
        GVRScene mainScene = gvrTestUtils.getMainScene();
        GVRSceneObject cube = new GVRSceneObject(ctx);

        GVRTexture tempTex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.redtex));
        GVRSceneObject quad1 = new GVRSceneObject(ctx, 4, 4, tempTex1);
        quad1.getTransform().setPosition(0.0f, 0.0f, 2.0f);
        cube.addChildObject(quad1);

        GVRTexture tempTex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.bluetex));
        GVRSceneObject quad2 = new GVRSceneObject(ctx, 4, 4, tempTex2);
        quad2.getTransform().setPosition(0.0f, 0.0f, -2.0f);
        cube.addChildObject(quad2);

        GVRTexture tempTex3 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.yellowtex));
        GVRSceneObject quad3 = new GVRSceneObject(ctx, 4, 4, tempTex3);
        quad3.getTransform().setPosition(2.0f, 0.0f, 0.0f);
        quad3.getTransform().setRotationByAxis(90, 0, 1.0f, 0.0f);
        cube.addChildObject(quad3);

        GVRTexture tempTex4 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.greentex));
        GVRSceneObject quad4 = new GVRSceneObject(ctx, 4, 4, tempTex4);
        quad4.getTransform().setPosition(-2.0f, 0.0f, 0.0f);
        quad4.getTransform().setRotationByAxis(-90, 0, 1.0f, 0.0f);
        cube.addChildObject(quad4);

        cube.getTransform().setRotationByAxis(45, 1, 0, 0);
        cube.getTransform().setPosition(0, -0.8f, -8.0f);

        mainScene.getMainCameraRig().addChildObject(cube);
        mRoot = cube;
    }

    private void testCull(int id)
    {
        //quad1
        mRoot.getChildByIndex(0).getRenderData().setCullFace(GVRRenderPass.GVRCullFaceEnum.fromInt(id));
        //quad2
        mRoot.getChildByIndex(1).getRenderData().setCullFace(GVRRenderPass.GVRCullFaceEnum.fromInt(id));
        //quad3
        mRoot.getChildByIndex(2).getRenderData().setCullFace(GVRRenderPass.GVRCullFaceEnum.fromInt(id));
        //quad4
        mRoot.getChildByIndex(3).getRenderData().setCullFace(GVRRenderPass.GVRCullFaceEnum.fromInt(id));
    }


    @Test
    public void frontFaceCullTest() throws TimeoutException {
        testCull(0);
        gvrTestUtils.waitForXFrames(3);
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testFrontFaceCull", mWaiter, mDoCompare);
    }

    @Test
    public void backFaceCullTest() throws TimeoutException {
        testCull(1);
        gvrTestUtils.waitForXFrames(3);
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testBackFaceCull", mWaiter, mDoCompare);

    }

    @Test
    public void noneFaceCullTest() throws TimeoutException {
        testCull(2);
        gvrTestUtils.waitForXFrames(3);
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testNoneFaceCull", mWaiter, mDoCompare);
    }
}
