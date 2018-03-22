package org.gearvrf.performance;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.tester.TextureEventHandler;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRImage;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.IAssetEvents;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.tester.R;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)


public class CylPerfTests
{
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRScene mScene;
    private int BITMAP_TEXTURE = R.drawable.checker;
    private int CUBEMAP_TEXTURE = R.raw.beach;
    private int COMPRESSED_TEXTURE = R.raw.sunmap;
    private int R11_BITMAP_TEXTURE =  R.raw.etc2_r11_opaque;
    private int RG11_BITMAP_TEXTURE = R.raw.etc2_rg11_transparency;
    private int RGB8_BITMAP_TEXTURE = R.raw.etc2_rgb8_opaque;
    private GVRImage mBitmapImage = null;
    private GVRImage mCompressedImage = null;
    private GVRImage mCubemapImage = null;

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

        mScene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(mScene);
    }

    private void runPerfTest(GVRContext ctx, String testName, Map<String, Object> params)
    {
        RenderSetup setup = new RenderSetup(mWaiter, mTestUtils);
        int nframes = (Integer) params.get("frames");
        float expectedFPS = (Float) params.get("fps");
        setup.createTestScene(ctx, params);
        mTestUtils.waitForXFrames(2);
        long startTime = System.currentTimeMillis();
        mTestUtils.waitForXFrames(nframes);
        long endTime = System.currentTimeMillis();
        float fps =  (1000.0f * nframes) / ((float) (endTime - startTime));
        Log.e("PERFORMANCE", testName + " FPS = %f, expected %f", fps, expectedFPS);
        mWaiter.assertTrue(fps >= expectedFPS);
    }

    @Test
    public void cyl10x10ShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10ShareAll", params);
    }

    @Test
    public void cyl10x10ShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10ShareGeo", params);
    }

    @Test
    public void cyl10x10ShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10ShareMtl", params);
    }

    @Test
    public void cyl10x10() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10", params);
    }

    @Test
    public void cyl10x10BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10BitmapShareMtl", params);
    }

    @Test
    public void cyl10x10Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10Bitmap", params);
    }

    @Test
    public void cyl10x10CubemapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CubemapShareAll", params);
    }

    @Test
    public void cyl10x10CubemapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CubemapShareGeo", params);
    }

    @Test
    public void cyl10x10CubemapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CubemapShareMtl", params);
    }


    @Test
    public void cyl10x10Cubemap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10Cubemap", params);
    }

    @Test
    public void cyl10x10CompBmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CompBmapShareAll", params);
    }

    @Test
    public void cyl10x10CompBmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CompBmapShareGeo", params);
    }

    @Test
    public void cyl10x10CompBmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CompBmapShareMtl", params);
    }


    @Test
    public void cyl10x10CompBmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CompBmap", params);
    }

    @Test
    public void cyl10x10R11BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11BitmapShareAll", params);
    }

    @Test
    public void cyl10x10R11BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 32.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10R11BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 26.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11BitmapShareMtl", params);
    }

    @Test
    public void cyl10x10R11Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11Bitmap", params);
    }

    @Test
    public void cyl10x10RG11BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 32.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11BitmapShareAll", params);
    }

    @Test
    public void cyl10x10RG11BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 32.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10RG11BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11BitmapShareMtl", params);
    }

    @Test
    public void cyl10x10RG11Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11Bitmap", params);
    }

    @Test
    public void cyl10x10RGB8BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("compressedbitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 32.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8BitmapShareAll", params);
    }

    @Test
    public void cyl10x10RGB8BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 32.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10RGB8BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 25.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8BitmapShareMtl", params);
    }

    @Test
    public void cyl10x10RGB8Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8Bitmap", params);
    }

}

