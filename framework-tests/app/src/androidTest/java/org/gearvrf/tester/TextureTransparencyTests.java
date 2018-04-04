package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShader;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.joml.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@RunWith(AndroidJUnit4.class)
public class TextureTransparencyTests
{
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;

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

        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
    }

    public void centerModel(GVRSceneObject model)
    {
        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        float sf = 1 / bv.radius;
        model.getTransform().setScale(sf, sf, sf);
        bv = model.getBoundingVolume();
        model.getTransform().setPosition(-bv.center.x, -bv.center.y, -bv.center.z - 1.5f * bv.radius);
    }

    public void checkResults(int actual, int truth)
    {
        mWaiter.assertEquals(truth, actual);
    }

    @Test
    public void testTransparencyJPG() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.jpg_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "JPG order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyPNG4_Transp() throws TimeoutException
    {
        android.util.Log.d("Texture:", "beginning texture transparency detection");
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load png, 4 component, transparency, RenderOrder == TRANSPARENT
        texHandler.reset();
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "PNG 4 transparent order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyPNG3() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load png, 3 component, RenderOrder == GEOMETRY
        texHandler.reset();
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_3_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "PNG 3 opaque order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyTGA4_Transp() throws TimeoutException
    {
        android.util.Log.d("Texture:", "beginning texture transparency detection");
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load tga, 4 component, transparency, RenderOrder == TRANSPARENT
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "TGA 4 transparent order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyPNG4_Opaque() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load png, 4 component, opaque, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "PNG 4 opaque order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyASTC() throws TimeoutException
    {
        android.util.Log.d("Texture:", "beginning texture transparency detection");
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load astc, RenderOrder == TRANSPARENT
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.astc_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "ASTC order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyTGA3() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load tga, 3 component, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_3_opaque));
        material.setTexture("diffuseTexture", tex);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "after setTexture TGA 3 opaque order = " + order);

        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "TGA 3 opaque order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyRG11() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_RG11_EAC, RenderOrder == TRANSPARENT
        texHandler.reset();
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rg11_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "rg11 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyTGA4_Opaque() throws TimeoutException
    {
        android.util.Log.d("Texture:", "beginning texture transparency detection");
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load tga, 4 component, opaque, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "TGA 4 opaque order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencySR11_Transp() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_SIGNED_RG11_EAC, RenderOrder == TRANSPARENT
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_rg11_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "signed rg11 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyR11() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_R11_EAC, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_r11_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "r11 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencySR11() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_R11_EAC, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_r11_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "r11 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyRGBA8() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_RGBA8_ETC2_EAC, RenderOrder == TRANSPARENT
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgba8_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "rgba8 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        groundObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencySR11_Opaque() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_SIGNED_R11_EAC, RenderOrder == GEOMETRY
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_r11_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "sr11 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testTransparencyRGBA1() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2, RenderOrder == TRANSPARENT
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_punchthrough_alpha1_transparency));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("gvrf", "rgba1 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testTransparencyRGB8_Opaque() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load etc2, GL_COMPRESSED_RGB8_ETC2, RenderOrder == GEOMETRY
        texHandler.reset();
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_opaque));
        material.setTexture("diffuseTexture", tex);
        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "rgb8 order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }

    @Test
    public void testCompressedTextureASTC_Transparent() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load astc, RenderOrder == TRANSPARENT
        try {
            GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, "3dgreen_transparent.astc"));
            material.setTexture("diffuseTexture", tex);
        } catch (IOException ex) {
            mWaiter.fail(ex);
        }

        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("gvrf", "astc transparent order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
    }

    @Test
    public void testCompressedTextureASTC_Opaque() throws TimeoutException
    {
        final GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        groundObject.getTransform().setPositionZ(-3.0f);
        mTestUtils.getMainScene().addSceneObject(groundObject);

        // load astc, RenderOrder == GEOMETRY
        texHandler.reset();
        try {
            GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, "3dgreen_opaque.astc"));
            material.setTexture("diffuseTexture", tex);
        } catch (IOException ex) {
            mWaiter.fail(ex);
        }

        mTestUtils.waitForAssetLoad();
        texHandler.checkTextureLoaded(mWaiter);
        mTestUtils.waitForXFrames(2);
        int order = groundObject.getRenderData().getRenderingOrder();
        android.util.Log.d("Texture:", "astc opaque order = " + order);
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
    }
}

