package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRImage;
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
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;


@RunWith(AndroidJUnit4.class)
public class TextureTests
{
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private boolean mDoCompare = true;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class)
    {
    };

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

    public void repeatTexcoords(GVRMesh mesh)
    {
        float[] texcoords = mesh.getTexCoords();

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setFloatArray("a_texcoord1", texcoords);
    }

    @Test
    public void testAlphaToCoverage() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRTextViewSceneObject t = new GVRTextViewSceneObject(ctx, 2, 2, "very very long test text");
        GVRRenderData rd = t.getRenderData();
        GVRMaterial m = rd.getMaterial();

        t.setTextSize(16);
        t.setGravity(Gravity.TOP | Gravity.LEFT);
        t.getTransform().setPosition(0, 1, -2);
        t.setName("textview");
        m.setColor(1, 0, 0);
        rd.setAlphaBlend(true);
        rd.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        rd.setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
        rd.setDepthTest(false);
        rd.setAlphaToCoverage(true);

        GVRSceneObject polygon = new GVRSceneObject(ctx, 4, 4);
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);

        rd = polygon.getRenderData();
        polygon.setName("polygon");
        polygon.getTransform().setPosition(0, 1, -3);
        mtl.setDiffuseColor(0, 1, 0, 0.5f);
        rd.setMaterial(mtl);
        rd.setAlphaBlend(true);
        rd.setAlphaToCoverage(true);
        rd.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        rd.setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
        mRoot.addChildObject(t);
        mRoot.addChildObject(polygon);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testAlphaToCoverage", mWaiter, mDoCompare);
    }


    @Test
    public void testCompressedTextureASTC() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRSceneObject lightObj = new GVRSceneObject(ctx);

        if (!GVRShader.isVulkanInstance())
        {
            light.setSpecularIntensity(0.5f, 0.5f, 0.5f, 1.0f);
            lightObj.attachComponent(light);
            scene.addSceneObject(lightObj);
        }
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        try
        {
            GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, "sunmap.astc"));
            mtl.setTexture("diffuseTexture", tex2);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(0.5f, 0.5f, 0.5f, 1.0f);
        mtl.setSpecularExponent(4.0f);
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testCompressedTextureASTC", mWaiter, mDoCompare);
    }

    @Test
    public void testCompressedTextureASTCUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);

        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        try
        {
            GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, "sunmap.astc"));
            mtl.setTexture("diffuseTexture", tex2);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(0.5f, 0.5f, 0.5f, 1.0f);
        mtl.setSpecularExponent(4.0f);
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testCompressedTextureASTCUnlit", mWaiter, mDoCompare);
    }

    @Test
    public void testLayeredDiffuseTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);
        GVRDirectLight light = new GVRDirectLight(ctx);

        layeredMtl.setTexture("diffuseTexture", tex1);
        layeredMtl.setTexture("diffuseTexture1", tex2);
        layeredMtl.setInt("diffuseTexture1_blendop", 0);
        layeredMtl.setTexCoord("diffuseTexture", "a_texcoord", "diffuse_coord");
        layeredMtl.setTexCoord("diffuseTexture1", "a_texcoord", "diffuse_coord1");
        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredDiffuseTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testLayeredDiffuseTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);

        layeredMtl.setTexture("diffuseTexture", tex1);
        layeredMtl.setTexture("diffuseTexture1", tex2);
        layeredMtl.setInt("diffuseTexture1_blendop", 0);
        layeredMtl.setTexCoord("diffuseTexture", "a_texcoord", "diffuse_coord");
        layeredMtl.setTexCoord("diffuseTexture1", "a_texcoord", "diffuse_coord1");
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredDiffuseTextureUnlit", mWaiter, mDoCompare);
    }

    @Test
    public void testMissingTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);

        mtl.setColor(0.7f, 0.4f, 0.6f);
        mtl.setMainTexture((GVRTexture) null);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        mTestUtils.waitForSceneRendering();
    }

    @Test
    public void testRepeatTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        float[] texcoords = mesh.getTexCoords();

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setFloatArray("a_texcoord", texcoords);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        model.getTransform().setPositionZ(-2.0f);

        if (!GVRShader.isVulkanInstance())
        {
            GVRSceneObject rig = scene.getMainCameraRig().getOwnerObject();
            rig.attachComponent(light);
        }
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testRepeatTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testRepeatTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        float[] texcoords = mesh.getTexCoords();

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setFloatArray("a_texcoord", texcoords);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        model.getTransform().setPositionZ(-2.0f);

        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testRepeatTextureUnlit", mWaiter, mDoCompare);
    }


    @Test
    public void testSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));

        mtl.setDiffuseColor(0.7f, 0, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testSpecularTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));

        mtl.setDiffuseColor(0.7f, 0, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularTextureUnlit", mWaiter, mDoCompare);
    }

    @Test
    public void testLayeredSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.wavylines));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);
        GVRDirectLight light = new GVRDirectLight(ctx);

        layeredMtl.setDiffuseColor(0.7f, 0.2f, 0.2f, 1.0f);
        layeredMtl.setSpecularColor(1, 1, 1, 1);
        layeredMtl.setSpecularExponent(4.0f);
        layeredMtl.setTexture("specularTexture", tex1);
        layeredMtl.setTexture("specularTexture1", tex2);
        layeredMtl.setInt("specularTexture1_blendop", 0);
        layeredMtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        layeredMtl.setTexCoord("specularTexture1", "a_texcoord", "specular_coord1");
        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredSpecularTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testLayeredSpecularTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.wavylines));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);

        layeredMtl.setDiffuseColor(0.7f, 0.2f, 0.2f, 1.0f);
        layeredMtl.setSpecularColor(1, 1, 1, 1);
        layeredMtl.setSpecularExponent(4.0f);
        layeredMtl.setTexture("specularTexture", tex1);
        layeredMtl.setTexture("specularTexture1", tex2);
        layeredMtl.setInt("specularTexture1_blendop", 0);
        layeredMtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        layeredMtl.setTexCoord("specularTexture1", "a_texcoord", "specular_coord1");
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredSpecularTextureUnlit", mWaiter, mDoCompare);
    }

    @Test
    public void testDiffuseSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        GVRMesh mesh = GVRCubeSceneObject.createCube(ctx, "float3 a_position, float2 a_texcoord, float3 a_normal, float2 a_texcoord1", true, new Vector3f(1, 1, 1));
        GVRSceneObject model = new GVRSceneObject(ctx, mesh, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);

        repeatTexcoords(mesh);
        model.getRenderData().setMesh(mesh);
        model.getTransform().setPositionZ(-2.0f);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("diffuseTexture", "a_texcoord1", "diffuse_coord");
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseSpecularTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testDiffuseSpecularTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        GVRMesh mesh = GVRCubeSceneObject.createCube(ctx, "float3 a_position, float2 a_texcoord, float3 a_normal, float2 a_texcoord1", true, new Vector3f(1, 1, 1));
        GVRSceneObject model = new GVRSceneObject(ctx, mesh, mtl);

        repeatTexcoords(mesh);
        model.getRenderData().setMesh(mesh);
        model.getTransform().setPositionZ(-2.0f);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("diffuseTexture", "a_texcoord1", "diffuse_coord");
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseSpecularTextureUnlit", mWaiter, mDoCompare);
    }


    @Test
    public void testDiffuseNormalTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.rock_normal));
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        GVRMesh mesh = GVRCubeSceneObject.createCube(ctx, "float3 a_position, float2 a_texcoord, float3 a_normal, float2 a_texcoord1", true, new Vector3f(1, 1, 1));
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRSceneObject model = new GVRSceneObject(ctx, mesh, mtl);

        repeatTexcoords(mesh);
        model.getRenderData().setMesh(mesh);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("normalTexture", tex2);
        tex1.setTexCoord("a_texcoord1", "diffuse_coord");
        tex2.setTexCoord("a_texcoord", "normal_coord");
        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseNormalTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testDiffuseNormalTextureUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);
        ctx.getEventReceiver().addListener(texHandler);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.rock_normal));
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.PhongLayered.ID);
        GVRMesh mesh = GVRCubeSceneObject.createCube(ctx, "float3 a_position, float2 a_texcoord, float3 a_normal, float2 a_texcoord1", true, new Vector3f(1, 1, 1));
        GVRSceneObject model = new GVRSceneObject(ctx, mesh, mtl);

        repeatTexcoords(mesh);
        model.getRenderData().setMesh(mesh);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("normalTexture", tex2);
        tex1.setTexCoord("a_texcoord1", "diffuse_coord");
        tex2.setTexCoord("a_texcoord", "normal_coord");
        model.getTransform().setPositionZ(-2.0f);
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseNormalTextureUnlit", mWaiter, mDoCompare);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testNormalDiffuseSpecularLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;
        GVRDirectLight light = new GVRDirectLight(ctx);

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_diffuse_specular_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormalDiffuseSpecularLightmap", mWaiter, false);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testNormalDiffuseSpecularLightmapUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_diffuse_specular_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormalDiffuseSpecularLightmapUnlit", mWaiter, false);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testNormaLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;
        GVRDirectLight light = new GVRDirectLight(ctx);

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormaLightmap", mWaiter, mDoCompare);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testNormaLightmapUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormaLightmapUnlit", mWaiter, mDoCompare);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testSpecularLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;
        GVRDirectLight light = new GVRDirectLight(ctx);

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/specular_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.rotateByAxis(90.0f, 1, 0, 0);
            centerModel(model);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        if (!GVRShader.isVulkanInstance())
        {
            scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularLightmap", mWaiter, mDoCompare);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testSpecularLightmapUnlit() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model;

        try
        {
            model = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/specular_lightmap.fbx", scene);
            GVRTransform trans = model.getTransform();
            trans.rotateByAxis(90.0f, 1, 0, 0);
            centerModel(model);
            scene.addSceneObject(model);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularLightmapUnlit", mWaiter, mDoCompare);
    }

    @Test
    public void testLoadTextureFromResource() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTexture texture = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);

        material.setTexture("diffuseTexture", texture);
        groundObject.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(groundObject);
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testLoadTextureFromResource", mWaiter, mDoCompare);
    }

    public void checkResults(int actual, int truth)
    {
        mWaiter.assertTrue(actual == truth);
    }

    /*
     * how to test futureTexture?
     */
    @Test
    public void testTextureTransparencyDetection() throws TimeoutException
    {
        android.util.Log.d("gvrf", "beginning texture transparency detection");
        final GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        final GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);

        // load jpg, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback jpgTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying JPG now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with JPG, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.jpg_opaque), jpgTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load png, 4 component, transparency, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback png4transparentTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying png 4 transparent now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with png 4 transparent, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_transparency), png4transparentTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load png, 3 component, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback png3opaqueTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying png 3 opaque now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with png 3 opaque, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_3_opaque), png3opaqueTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load tga, 4 component, transparency, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback tga4transparentTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying tga 4 transparent now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "tga transparent = " + image);
                android.util.Log.d("gvrf", "done with tga 4 transparent, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_transparency), tga4transparentTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();


        // load png, 4 component, opaque, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback png4opaqueTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying png 4 opaque now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with png 4 opaque, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_opaque), png4opaqueTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load astc, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback astcTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying ASTC now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with ASTC, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.astc_transparency), astcTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load tga, 3 component, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback tga3opaqueTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying tga 3 opaque now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with tga 3 opaque, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_3_opaque), tga3opaqueTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load etc2, GL_COMPRESSED_RG11_EAC, RenderOrder == TRANSPARENT 
        GVRAndroidResource.TextureCallback rg11TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying rg11 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with rg11, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rg11_transparency), rg11TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load tga, 4 component, opaque, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback tga4opaqueTexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying tga 4 opaque now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with tga 4 opaque, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_opaque), tga4opaqueTexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load etc2, GL_COMPRESSED_SIGNED_RG11_EAC, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback srg11TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying signed rg11 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with signed rg11, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_rg11_transparency), srg11TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load etc2, GL_COMPRESSED_R11_EAC, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback r11TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying r11 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with r11, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_r11_opaque), r11TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();


        // load etc2, GL_COMPRESSED_RGBA8_ETC2_EAC, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback rgba8TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying rgba8 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with rgba8, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgba8_transparency), rgba8TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load etc2, GL_COMPRESSED_SIGNED_R11_EAC, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback sr11TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying sr11 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with sr11, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_r11_opaque), sr11TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        // load etc2, GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2, RenderOrder == TRANSPARENT
        GVRAndroidResource.TextureCallback rgba1TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying rgba1 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with rgba1, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_punchthrough_alpha1_transparency), rgba1TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

 
        // load etc2, GL_COMPRESSED_RGB8_ETC2, RenderOrder == GEOMETRY
        GVRAndroidResource.TextureCallback rgb8TexLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRImage image, GVRAndroidResource r)
            {
                android.util.Log.d("gvrf", "trying rgb8 now");
                int order = groundObject.getRenderData().getRenderingOrder();
                android.util.Log.d("gvrf", "done with rgb8, order = " + order);
                checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        groundObject.getRenderData().setMaterial(material);
        tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_opaque), rgb8TexLoadCallback);
        material.setTexture("diffuseTexture", tex);
        mWaiter.await();

        android.util.Log.d("gvrf", "end texture transparency detection");
    }

}

