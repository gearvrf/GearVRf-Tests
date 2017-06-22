package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRPostEffect;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;

@RunWith(AndroidJUnit4.class)

public class RenderTests {
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown() {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null) {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);
    }

    @Test
    public void testBlendFunc() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.checker));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.donut));
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRSceneObject quad2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex2);
        GVRRenderData rdata2 = quad2.getRenderData();
        GVRMaterial mat2 = rdata2.getMaterial();

        mat1.setMainTexture(tex1);
        mat2.setColor(1.0f, 1.0f, 0.0f);
        rdata2.setAlphaBlend(true);
        rdata2.setAlphaBlendFunc(GL_ONE, GL_SRC_ALPHA);
        rdata2.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        cube1.getTransform().setPositionZ(-2.0f);
        quad2.getTransform().setPositionZ(-0.8f);
        scene.addSceneObject(cube1);
        scene.addSceneObject(quad2);
        mWaiter.assertEquals(GL_ONE, rdata2.getSourceAlphaBlendFunc());
        mWaiter.assertEquals(GL_SRC_ALPHA, rdata2.getDestAlphaBlendFunc());
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testBlendFunc", mWaiter, true);
    }


    @Test
    public void testOnePostEffect() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRPostEffect flipHorzPostEffect = new GVRPostEffect(ctx, GVRPostEffect.GVRPostEffectShaderType.HorizontalFlip.ID);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testOnePostEffect", mWaiter, false);
    }

    @Test
    public void testTwoPostEffects() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));;
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRPostEffect flipHorzPostEffect = new GVRPostEffect(ctx, GVRPostEffect.GVRPostEffectShaderType.HorizontalFlip.ID);
        GVRPostEffect colorBlendPostEffect = new GVRPostEffect(ctx, GVRPostEffect.GVRPostEffectShaderType.ColorBlend.ID);

        colorBlendPostEffect.setVec3("u_color", 0.0f, 0.3f, 0.3f);
        colorBlendPostEffect.setFloat("u_factor", 0.5f);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testTwoPostEffects", mWaiter, false);
    }
}

