package org.gearvrf.performance;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRImage;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
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


public class QuadPerfTests
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
    public void quad15x15ShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15ShareAll", params);
    }

    @Test
    public void quad15x15ShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15ShareGeo", params);
    }

    @Test
    public void quad15x15ShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15ShareMtl", params);
    }


    @Test
    public void quad15x15() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15", params);
    }

    @Test
    public void quad15x15BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15BitmapShareAll", params);
    }

    @Test
    public void quad15x15BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15BitmapShareGeo", params);
    }

    @Test
    public void quad15x15BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 25.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15BitmapShareMtl", params);
    }


    @Test
    public void quad15x15Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15Bitmap", params);
    }


    @Test
    public void quad15x15CubemapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 28.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CubemapShareAll", params);
    }

    @Test
    public void quad15x15CubemapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CubemapShareGeo", params);
    }

    @Test
    public void quad15x15CubemapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 26.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CubemapShareMtl", params);
    }


    @Test
    public void quad15x15Cubemap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("cubemap", CUBEMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15Cubemap", params);
    }

    @Test
    public void quad15x15CompBmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CompBmapShareAll", params);
    }

    @Test
    public void quad15x15CompBmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 28.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CompBmapShareGeo", params);
    }

    @Test
    public void quad15x15CompBmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 26.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CompBmapShareMtl", params);
    }


    @Test
    public void quad15x15CompBmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("compressedbitmap", COMPRESSED_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15CompBmap", params);
    }

    @Test
    public void quad15x15R11BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11BitmapShareAll", params);
    }

    @Test
    public void quad15x15R11BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 28.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11BitmapShareGeo", params);
    }

    @Test
    public void quad15x15R11BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("frames", 600);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("fps", 25.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11BitmapShareMtl", params);
    }

    @Test
    public void quad15x15R11Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11Bitmap", params);
    }

    @Test
    public void quad15x15RG11BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 28.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11BitmapShareAll", params);
    }

    @Test
    public void quad15x15RG11BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11BitmapShareGeo", params);
    }

    @Test
    public void quad15x15RG11BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 26.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11BitmapShareMtl", params);
    }

    @Test
    public void quad15x15RG11Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 25.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11Bitmap", params);
    }

    @Test
    public void quad15x15RGB8BitmapShareAll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8BitmapShareAll", params);
    }

    @Test
    public void quad15x15RGB8BitmapShareGeo() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_geometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8BitmapShareGeo", params);
    }

    @Test
    public void quad15x15RGB8BitmapShareMtl() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 25.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8BitmapShareMtl", params);
    }

    @Test
    public void quad15x15RGB8Bitmap() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 27.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8Bitmap", params);
    }


    @Test
    public void testShareCompBmap() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = ctx.getMainScene();
        final GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord");
        GVRAndroidResource texFile = null;

        mesh.createQuad(1, 1);
        scene.setBackgroundColor(1, 1, 1, 1);
        try
        {
            texFile = new GVRAndroidResource(ctx, "sunmap.astc");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }

        TextureHandler loadHandler = new TextureHandler(ctx);
        ctx.getEventReceiver().addListener(loadHandler);
        GVRTexture tex = loadHandler.loadTexture(texFile);
        ctx.getEventReceiver().removeListener(loadHandler);

        createObjects(scene, tex.getImage(), mesh, 4);
        mTestUtils.waitForXFrames(10);
    }

    private void createObjects(GVRScene scene, GVRImage image, GVRMesh mesh, int n)
    {
        GVRContext ctx = mesh.getGVRContext();
        int nrows = n;
        int ncols = n;
        float zpos = n;

        for (int y = 0; y < nrows; ++y)
        {
            float ypos = (y - nrows / 2.0f);

            for (int x = 0; x < ncols; ++x)
            {
                float xpos = (x - ncols / 2.0f);
                GVRTexture tex = new GVRTexture(ctx);
                GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Texture.ID);
                GVRSceneObject testObj = new GVRSceneObject(ctx, mesh, mtl);

                testObj.getTransform().setPosition(xpos, ypos, -zpos);
                tex.setImage(image);
                mtl.setMainTexture(tex);
                scene.addSceneObject(testObj);
            }
        }
    }



}

