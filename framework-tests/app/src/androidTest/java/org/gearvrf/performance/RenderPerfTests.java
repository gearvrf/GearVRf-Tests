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


public class RenderPerfTests
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

    private GVRSceneObject createQuad(GVRContext ctx, String meshDesc, GVRMaterial mtl, float scale)
    {
        GVRMesh quadMesh = new GVRMesh(ctx, meshDesc);
        GVRSceneObject quad = new GVRSceneObject(ctx, quadMesh, mtl);
        quadMesh.createQuad(scale, scale);
        return quad;
    }

    /*
     * They cylinder is 26080 vertices
     */
    private GVRSceneObject createCylinder(GVRContext ctx, String meshDesc, GVRMaterial mtl, float scale)
    {
        GVRCylinderSceneObject.CylinderParams params = new GVRCylinderSceneObject.CylinderParams();
        params.Material = mtl;
        params.VertexDescriptor = meshDesc;
        params.Height = scale;
        params.TopRadius = scale / 2.0f;
        params.BottomRadius = scale / 2.0f;
        params.StackNumber = 80;
        params.SliceNumber = 80;
        params.HasBottomCap = false;
        params.HasTopCap = false;
        params.FacingOut = true;
        GVRSceneObject cyl = new GVRCylinderSceneObject(ctx, params);
        return cyl;
    }

    private void setRenderState(GVRRenderData renderData, Map<String, Object> renderState)
    {
        for (Map.Entry<String, Object> entry : renderState.entrySet())
        {
            String state = entry.getKey();
            Object value = entry.getValue();

            if (state.equals("renderingorder"))
            {
                renderData.setRenderingOrder((Integer) value);
            }
            else if (state.equals("enablelight"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.disableLight();
                }
                else
                {
                    renderData.enableLight();
                }
            }
            else if (state.equals("cullface"))
            {
                GVRRenderPass.GVRCullFaceEnum cull = (GVRRenderPass.GVRCullFaceEnum) value;
                renderData.setCullFace(cull);
            }
            else if (state.equals("alphablend"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.setAlphaBlend(false);
                }
                else
                {
                    renderData.setAlphaBlend(true);
                }
            }
            else if (state.equals("drawmode"))
            {
                Integer drawmode = (Integer) value;
                renderData.setDrawMode(drawmode);
            }
            else if (state.equals("castshadows"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.setCastShadows(false);
                }
                else
                {
                    renderData.setCastShadows(true);
                }
            }
        }
    }

    private GVRTexture createBitmap(GVRContext ctx, int resourceId, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceId);
        if (params != null)
        {
            return ctx.getAssetLoader().loadTexture(res, params);
        }
        return ctx.getAssetLoader().loadTexture(res);
    }

    private GVRTexture createCubemap(GVRContext ctx, int resourceID, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceID);
        GVRTexture tex = ctx.getAssetLoader().loadCubemapTexture(res);
        if (params != null)
        {
            tex.updateTextureParameters(params);
        }
        return tex;
    }

    private GVRTexture createCompressedCubemap(GVRContext ctx, int resourceID, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceID);
        GVRTexture tex = ctx.getAssetLoader().loadCompressedCubemapTexture(res);
        if (params != null)
        {
            tex.updateTextureParameters(params);
        }
        return tex;
    }

    private String createMeshDesc(boolean doLight, boolean doSkin, boolean doTexture)
    {
        String meshDesc = "float3 a_position";
        if (doTexture)
        {
            meshDesc += " float2 a_texcoord";
        }
        if (doLight)
        {
            meshDesc += " float3 a_normal";
        }
        if (doSkin)
        {
            meshDesc += " float4 a_bone_weights int4 a_bone_indices";
        }
        return meshDesc;
    }

    private void createLights(GVRContext ctx, Map<String, Object> params)
    {
        if (params.containsKey("enablelight"))
        {
            boolean castShadows = false;
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                i = (Integer) params.get("castshadows");
                if (i != 0)
                {
                    castShadows = true;
                }
                if (params.containsKey("spotlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRSpotLight spotLight = new GVRSpotLight(ctx);
                    lightObj.attachLight(spotLight);
                    spotLight.setCastShadow(castShadows);
                    mScene.addSceneObject(lightObj);
                }
                if (params.containsKey("directlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRDirectLight directLight = new GVRDirectLight(ctx);
                    lightObj.attachLight(directLight);
                    lightObj.getTransform().rotateByAxis(90.0f, 1, 0, 0);
                    directLight.setCastShadow(castShadows);
                    mScene.addSceneObject(lightObj);
                }
                if (params.containsKey("pointlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRPointLight pointLight = new GVRPointLight(ctx);
                    lightObj.attachLight(pointLight);
                    lightObj.getTransform().setPosition(-5.0f, 0, 0);
                    mScene.addSceneObject(lightObj);
                }
            }
        }
    }

    private GVRMaterial createMaterial(GVRContext ctx, Map<String, Object> params)
    {
        GVRTexture tex = null;
        GVRMaterial material;
        GVRShaderId shaderId = GVRMaterial.GVRShaderType.Texture.ID;
        TextureEventHandler waitForLoad = null;

        if (params.containsKey("enablelight"))
        {
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                shaderId = GVRMaterial.GVRShaderType.Phong.ID;
            }
        }
        if (params.containsKey("shaderid"))
        {
            shaderId = (GVRShaderId) params.get("shaderid");
        }
        if (params.containsKey("bitmap"))
        {
            if (mBitmapImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createBitmap(ctx, (Integer) params.get("bitmap"), null);
                mTestUtils.waitForAssetLoad();
                waitForLoad.checkTextureLoaded(mWaiter);
                mBitmapImage = tex.getImage();
                mWaiter.assertNotNull(mBitmapImage);
            }
            else
            {
                tex = new GVRTexture(ctx);
                mWaiter.assertNotNull(mBitmapImage);
                tex.setImage(mBitmapImage);
            }
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else if (params.containsKey("compressedbitmap"))
        {
            if (mCompressedImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createBitmap(ctx, (Integer) params.get("compressedbitmap"), null);
                mTestUtils.waitForAssetLoad();
                waitForLoad.checkTextureLoaded(mWaiter);
                mCompressedImage = tex.getImage();
                mWaiter.assertNotNull(mCompressedImage);
            }
            else
            {
                tex = new GVRTexture(ctx);
                mWaiter.assertNotNull(mCompressedImage);
                tex.setImage(mCompressedImage);
            }
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else if (params.containsKey("cubemap"))
        {
            if (mCubemapImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createCubemap(ctx, (Integer) params.get("cubemap"), null);
                mTestUtils.waitForAssetLoad();
                mCubemapImage = tex.getImage();
            }
            else
            {
                tex = new GVRTexture(ctx);
                tex.setImage(mCubemapImage);
            }
            shaderId = GVRMaterial.GVRShaderType.Cubemap.ID;
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else
        {
            float red = 0.3f + ((float) Math.random()) * 0.7f;
            float green = 0.3f + ((float) Math.random()) * 0.7f;
            float blue = 0.3f + ((float) Math.random()) * 0.7f;

            material = new GVRMaterial(ctx, shaderId);
            if (shaderId == GVRMaterial.GVRShaderType.Texture.ID)
            {
                material.setColor(red, green, blue);
            }
            else
            {
                material.setDiffuseColor(red, green, blue, 1.0f);
            }
        }
        if (waitForLoad != null)
        {
            ctx.getEventReceiver().removeListener(waitForLoad);
        }
        return material;
    }

    private GVRSceneObject createGeometry(GVRContext ctx, GVRMaterial material, Map<String, Object> params)
    {
        boolean doLight = false;
        boolean doSkin = false;
        boolean doTexture = false;
        String meshDesc;
        GVRSceneObject geometry = null;
        float scale = 1.0f;

        if (params.containsKey("enablelight"))
        {
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                doLight = true;
            }
        }
        if (params.containsKey("skinning"))
        {
            Integer i = (Integer) params.get("skinning");
            if (i != 0)
            {
                doSkin = true;
            }
        }
        if (params.containsKey("bitmap"))
        {
            doTexture = true;
        }
        else if (params.containsKey("cubemap"))
        {
            doTexture = true;
        }
        meshDesc = createMeshDesc(doLight, doSkin, doTexture);
        if (params.containsKey("quadgeometry"))
        {
            geometry = createQuad(ctx, meshDesc, material, scale);
        }
        else if (params.containsKey("cylindergeometry"))
        {
            geometry = createCylinder(ctx, meshDesc, material, scale);
        }
        return geometry;
    }

    private void createTestScene(GVRContext ctx, Map<String, Object> params)
    {
        int nrows = (Integer) params.get("rows");
        int ncols = (Integer) params.get("columns");
        float zpos = (nrows > ncols) ? (float) nrows : (float) ncols;
        GVRMaterial sourceMtl = createMaterial(ctx, params);
        GVRSceneObject sourceObj = createGeometry(ctx, sourceMtl, params);
        GVRMesh sourceMesh = sourceObj.getRenderData().getMesh();
        GVRSceneObject root = new GVRSceneObject(ctx);

        mScene.setBackgroundColor(0.8f, 1.0f, 0.8f, 1.0f);
        //createLights(ctx, params);
        for (int y = 0; y < nrows; ++y)
        {
            float ypos = (y - nrows / 2.0f);

            for (int x = 0; x < ncols; ++x)
            {
                float xpos = (x - ncols / 2.0f);
                GVRSceneObject testObj;
                GVRMaterial material = sourceMtl;

                if (!params.containsKey("share_material"))
                {
                    material = createMaterial(ctx, params);
                }
                if (params.containsKey("share_geometry"))
                {
                    testObj = new GVRSceneObject(ctx, sourceMesh, material);
                }
                else
                {
                    testObj = createGeometry(ctx, material, params);
                }
                setRenderState(testObj.getRenderData(), params);
                testObj.getTransform().setPosition(xpos, ypos, -zpos);
                root.addChildObject(testObj);
            }
        }
        mScene.addSceneObject(root);
    }

    private void runPerfTest(GVRContext ctx, String testName, Map<String, Object> params)
    {
        int nframes = (Integer) params.get("frames");
        float expectedFPS = (Float) params.get("fps");
        createTestScene(ctx, params);
        mTestUtils.waitForXFrames(2);
        long startTime = System.currentTimeMillis();
        mTestUtils.waitForXFrames(nframes);
        long endTime = System.currentTimeMillis();
        float fps =  (1000.0f * nframes) / ((float) (endTime - startTime));
        Log.e("PERFORMANCE", testName + " FPS = %f", fps);
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
        params.put("fps", 29.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10CompBmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11BitmapShareGeo", params);
    }

    @Test
    public void quad15x15R11BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15R11Bitmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10R11BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", R11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10R11Bitmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11BitmapShareGeo", params);
    }

    @Test
    public void quad15x15RG11BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RG11Bitmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10RG11BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RG11_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RG11Bitmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8BitmapShareGeo", params);
    }

    @Test
    public void quad15x15RGB8BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("quadgeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 15);
        params.put("columns", 15);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "quad15x15RGB8Bitmap", params);
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
        params.put("fps", 59.0f);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8BitmapShareGeo", params);
    }

    @Test
    public void cyl10x10RGB8BitmapShareMtll() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cylindergeometry", 1);
        params.put("share_material", 1);
        params.put("rows", 10);
        params.put("columns", 10);
        params.put("bitmap", RGB8_BITMAP_TEXTURE);
        params.put("frames", 600);
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8BitmapShareMtll", params);
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
        params.put("fps", 59.0f);
        params.put("renderingorder", (int) GVRRenderData.GVRRenderingOrder.GEOMETRY);
        runPerfTest(ctx, "cyl10x10RGB8Bitmap", params);
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

        TextureLoadHandler loadHandler = new TextureLoadHandler(ctx);
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

    private static class TextureLoadHandler implements IAssetEvents
    {
        private GVRContext mContext;
        private boolean mTextureLoaded = false;
        private final Object onAssetLock = new Object();
        private final String TAG = "TEXTURE";

        public TextureLoadHandler(GVRContext ctx)
        {
            mContext = ctx;
        }

        public void onAssetLoaded(GVRContext ctx, GVRSceneObject model, String fileName, String errors) { }
        public void onModelLoaded(GVRContext ctx, GVRSceneObject model, String fileName) { }
        public void onModelError(GVRContext ctx, String fileName, String errors) { }

        @Override
        public void onTextureLoaded(GVRContext context, GVRTexture texture, String filePath)
        {
            if (mTextureLoaded)
            {
                Log.e(TAG, "Unanticipated texture load " + filePath);
            }
            else
            {
                textureLoaded();
            }
        }

        @Override
        public void onTextureError(GVRContext context, String error, String filePath)
        {
            Log.e(TAG, "texture load failed for " + filePath);
            if (!mTextureLoaded)
            {
                textureLoaded();
            }
        }

        public GVRTexture loadTexture(GVRAndroidResource texResource)
        {
            mTextureLoaded = false;
            synchronized (onAssetLock)
            {
                GVRTexture tex = null;
                try
                {
                    tex = mContext.getAssetLoader().loadTexture(texResource);
                    Log.d(TAG, "Waiting for texture load");
                    onAssetLock.wait();
                }
                catch (InterruptedException e)
                {
                    Log.e(TAG, "", e);
                    Thread.currentThread().interrupt();
                    return null;
                }
                return tex;
            }
        }

        private void textureLoaded()
        {
            synchronized (onAssetLock)
            {
                onAssetLock.notifyAll();
            }
            Log.d(TAG, "allTexturesLoaded Called");
        }
    };

}

