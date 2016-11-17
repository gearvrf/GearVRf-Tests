package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRRenderData;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class StateSortTests {
    static {
        System.loadLibrary("framework-tests");
    }

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

    @After
    public void tearDown() {
        mRenderDataVector = null;
    }

    @Test
    public void testSameShaderThusSortOnCameraDistance() throws InterruptedException {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        final GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(context, R.drawable.gearvr_logo));
        final GVRMaterial material = new GVRMaterial(context);
        material.setMainTexture(texture);

        final GVRSceneObject geometryFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryFar.setName("geometryFar");
        geometryFar.getRenderData().setMaterial(material);
        geometryFar.getTransform().setPosition(1.0f, 0.0f, -15.0f);
        scene.addSceneObject(geometryFar);

        final GVRSceneObject geometryNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryNear.setName("geometryNear");
        geometryNear.getRenderData().setMaterial(material);
        geometryNear.getTransform().setPosition(-1.0f, 0.0f, -5.0f);
        scene.addSceneObject(geometryNear);

        final GVRSceneObject geometryMiddle = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryMiddle.setName("geometryMiddle");
        geometryMiddle.getRenderData().setMaterial(material);
        geometryMiddle.getTransform().setPosition(-1.5f, 0.0f, -10.0f);
        scene.addSceneObject(geometryMiddle);

        gvrTestUtils.waitForSceneRendering();

        final CountDownLatch cdl = new CountDownLatch(1);
        context.runOnGlThreadPostRender(10, new Runnable() {
            @Override
            public void run() {
                mRenderDataVector = getRenderDataVector();
                cdl.countDown();
            }
        });
        cdl.await();

        //same shader type, sort on camera distance, opaque thus front-to-back
        mWaiter.assertTrue(geometryNear.getRenderData().getNative() == mRenderDataVector[0]);
        mWaiter.assertTrue(geometryMiddle.getRenderData().getNative() == mRenderDataVector[1]);
        mWaiter.assertTrue(geometryFar.getRenderData().getNative() == mRenderDataVector[2]);
    }

    @Test
    public void testSortOnShaderType() throws InterruptedException {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(context, R.drawable.gearvr_logo));
        final GVRSceneObject largestShaderId = new GVRSceneObject(context, 5.0f, 5.0f, texture, GVRMaterial.GVRShaderType.Phong.ID);
        largestShaderId.setName("largestShaderId");
        largestShaderId.getTransform().setPosition(1.0f, 0.0f, -15.0f);
        scene.addSceneObject(largestShaderId);

        final GVRSceneObject middleShaderId = new GVRSceneObject(context, 5.0f, 5.0f, texture, GVRMaterial.GVRShaderType.Texture.ID);
        middleShaderId.setName("middleShaderId");
        middleShaderId.getTransform().setPosition(-1.0f, 0.0f, -5.0f);
        scene.addSceneObject(middleShaderId);

        final GVRSceneObject smallestShaderId = new GVRSceneObject(context, 5.0f, 5.0f, texture, GVRMaterial.GVRShaderType.OES.ID);
        smallestShaderId.setName("smallestShaderId");
        smallestShaderId.getTransform().setPosition(-1.5f, 0.0f, -10.0f);
        scene.addSceneObject(smallestShaderId);

        gvrTestUtils.waitForSceneRendering();

        final CountDownLatch cdl = new CountDownLatch(1);
        context.runOnGlThreadPostRender(10, new Runnable() {
            @Override
            public void run() {
                mRenderDataVector = getRenderDataVector();
                cdl.countDown();
            }
        });
        cdl.await();

        //different shaders, sort on shader id ascending
        mWaiter.assertTrue(smallestShaderId.getRenderData().getNative() == mRenderDataVector[0]);
        mWaiter.assertTrue(middleShaderId.getRenderData().getNative() == mRenderDataVector[1]);
        mWaiter.assertTrue(largestShaderId.getRenderData().getNative() == mRenderDataVector[2]);
    }

    @Test
    public void testTransparencySorting() throws InterruptedException {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        final GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(context, R.drawable.gearvr_logo));
        final GVRMaterial material = new GVRMaterial(context);
        material.setMainTexture(texture);

        final GVRSceneObject transparentNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentNear.setName("transparentNear");
        transparentNear.getTransform().setPosition(1.0f, 0.0f, -15.0f);
        transparentNear.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        scene.addSceneObject(transparentNear);

        final GVRSceneObject geometryNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryNear.setName("geometryNear");
        geometryNear.getRenderData().setMaterial(material);
        geometryNear.getTransform().setPosition(-1.0f, 0.0f, -5.0f);
        scene.addSceneObject(geometryNear);

        final GVRSceneObject geometryFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryFar.setName("geometryFar");
        geometryFar.getRenderData().setMaterial(material);
        geometryFar.getTransform().setPosition(-1.5f, 0.0f, -10.0f);
        scene.addSceneObject(geometryFar);

        final GVRSceneObject transparentFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentFar.setName("transparentFar");
        transparentFar.getTransform().setPosition(1.0f, 0.0f, -20.0f);
        transparentFar.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        scene.addSceneObject(transparentFar);

        gvrTestUtils.waitForSceneRendering();

        final CountDownLatch cdl = new CountDownLatch(1);
        context.runOnGlThreadPostRender(10, new Runnable() {
            @Override
            public void run() {
                mRenderDataVector = getRenderDataVector();
                cdl.countDown();
            }
        });
        cdl.await();

        //same shader type, opaque thus front-to-back
        mWaiter.assertTrue(geometryNear.getRenderData().getNative() == mRenderDataVector[0]);
        mWaiter.assertTrue(geometryFar.getRenderData().getNative() == mRenderDataVector[1]);
        //transparent, hence back-to-front, after GEOMETRY nodes
        mWaiter.assertTrue(transparentFar.getRenderData().getNative() == mRenderDataVector[2]);
        mWaiter.assertTrue(transparentNear.getRenderData().getNative() == mRenderDataVector[3]);
    }

    @Test
    public void testByRenderingOrder() throws InterruptedException {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        final GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(context, R.drawable.gearvr_logo));
        final GVRMaterial material = new GVRMaterial(context);
        material.setMainTexture(texture);

        final GVRSceneObject backgroundNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        backgroundNear.setName("backgroundNear");
        backgroundNear.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
        backgroundNear.getRenderData().setMaterial(material);
        backgroundNear.getTransform().setPosition(1.0f, 0.0f, -2.0f);
        scene.addSceneObject(backgroundNear);
        final GVRSceneObject backgroundFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        backgroundFar.setName("backgroundFar");
        backgroundFar.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
        backgroundFar.getRenderData().setMaterial(material);
        backgroundFar.getTransform().setPosition(1.0f, 0.0f, -6.0f);
        scene.addSceneObject(backgroundFar);

        final GVRSceneObject geometryNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryNear.setName("geometryNear");
        geometryNear.getTransform().setPosition(-1.0f, 0.0f, -5.0f);
        geometryNear.getRenderData().setMaterial(material);
        scene.addSceneObject(geometryNear);
        final GVRSceneObject geometryFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        geometryFar.setName("geometryFar");
        geometryFar.getTransform().setPosition(-1.0f, 0.0f, -15.0f);
        geometryFar.getRenderData().setMaterial(material);
        scene.addSceneObject(geometryFar);

        final GVRSceneObject transparentNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentNear.setName("transparentNear");
        transparentNear.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        transparentNear.getTransform().setPosition(-1.0f, 0.0f, -5.5f);
        scene.addSceneObject(transparentNear);
        final GVRSceneObject transparentFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentFar.setName("transparentFar");
        transparentFar.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        transparentFar.getTransform().setPosition(-1.0f, 0.0f, -7.5f);
        scene.addSceneObject(transparentFar);

        final GVRSceneObject overlayNear = new GVRSceneObject(context, 1.0f, 1.0f, texture);
        overlayNear.setName("overlayNear");
        overlayNear.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        overlayNear.getRenderData().setMaterial(material);
        overlayNear.getTransform().setPosition(-1.5f, 0.0f, -1.0f);
        scene.addSceneObject(overlayNear);
        final GVRSceneObject overlayFar = new GVRSceneObject(context, 1.0f, 1.0f, texture);
        overlayFar.setName("overlayFar");
        overlayFar.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        overlayFar.getRenderData().setMaterial(material);
        overlayFar.getTransform().setPosition(-1.5f, 0.0f, -3.0f);
        scene.addSceneObject(overlayFar);

        gvrTestUtils.waitForSceneRendering();

        final CountDownLatch cdl = new CountDownLatch(1);
        context.runOnGlThreadPostRender(10, new Runnable() {
            @Override
            public void run() {
                mRenderDataVector = getRenderDataVector();
                cdl.countDown();
            }
        });
        cdl.await();

        mWaiter.assertTrue(backgroundNear.getRenderData().getNative() == mRenderDataVector[0]);
        mWaiter.assertTrue(backgroundFar.getRenderData().getNative() == mRenderDataVector[1]);
        mWaiter.assertTrue(geometryNear.getRenderData().getNative() == mRenderDataVector[2]);
        mWaiter.assertTrue(geometryFar.getRenderData().getNative() == mRenderDataVector[3]);
        mWaiter.assertTrue(transparentFar.getRenderData().getNative() == mRenderDataVector[4]);
        mWaiter.assertTrue(transparentNear.getRenderData().getNative() == mRenderDataVector[5]);
        mWaiter.assertTrue(overlayNear.getRenderData().getNative() == mRenderDataVector[6]);
        mWaiter.assertTrue(overlayFar.getRenderData().getNative() == mRenderDataVector[7]);
    }

    @Test
    public void testSameMaterial() throws InterruptedException {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        final GVRTexture texture = context.getAssetLoader().loadTexture(new GVRAndroidResource(context, R.drawable.gearvr_logo));
        final GVRMaterial material = new GVRMaterial(context);
        material.setMainTexture(texture);

        final GVRSceneObject sameMaterialFar = new GVRSceneObject(context, 5.0f, 5.0f);
        sameMaterialFar.setName("sameMaterialFar");
        sameMaterialFar.getRenderData().setMaterial(material);
        sameMaterialFar.getTransform().setPosition(1.0f, 0.0f, -15.0f);
        scene.addSceneObject(sameMaterialFar);

        final GVRSceneObject sameMaterialNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        sameMaterialNear.setName("sameMaterialNear");
        sameMaterialNear.getRenderData().setMaterial(material);
        sameMaterialNear.getTransform().setPosition(-1.0f, 0.0f, -5.0f);
        scene.addSceneObject(sameMaterialNear);

        final GVRSceneObject sameMaterialMiddle = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        sameMaterialMiddle.setName("sameMaterialMiddle");
        sameMaterialMiddle.getRenderData().setMaterial(material);
        sameMaterialMiddle.getTransform().setPosition(-1.5f, 0.0f, -10.0f);
        scene.addSceneObject(sameMaterialMiddle);

        final GVRSceneObject transparentNear = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentNear.setName("transparentNear");
        transparentNear.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        transparentNear.getTransform().setPosition(-1.0f, 0.0f, -1.5f);
        scene.addSceneObject(transparentNear);

        final GVRSceneObject transparentFar = new GVRSceneObject(context, 5.0f, 5.0f, texture);
        transparentFar.setName("transparentFar");
        transparentFar.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        transparentFar.getTransform().setPosition(-1.0f, 0.0f, -7.5f);
        scene.addSceneObject(transparentFar);

        gvrTestUtils.waitForSceneRendering();

        final CountDownLatch cdl = new CountDownLatch(1);
        context.runOnGlThreadPostRender(10, new Runnable() {
            @Override
            public void run() {
                mRenderDataVector = getRenderDataVector();
                cdl.countDown();
            }
        });
        cdl.await();

        mWaiter.assertTrue(sameMaterialNear.getRenderData().getNative() == mRenderDataVector[0]);
        mWaiter.assertTrue(sameMaterialMiddle.getRenderData().getNative() == mRenderDataVector[1]);
        mWaiter.assertTrue(sameMaterialFar.getRenderData().getNative() == mRenderDataVector[2]);
        mWaiter.assertTrue(transparentFar.getRenderData().getNative() == mRenderDataVector[3]);
        mWaiter.assertTrue(transparentNear.getRenderData().getNative() == mRenderDataVector[4]);
    }

    long[] mRenderDataVector;
    private static native long[] getRenderDataVector();
}
