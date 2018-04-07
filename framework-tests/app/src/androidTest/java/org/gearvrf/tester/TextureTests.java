package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
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
import org.gearvrf.utility.Log;
import org.joml.Vector3f;
import org.junit.After;
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

    public void repeatTexcoords(GVRMesh mesh)
    {
        float[] texcoords = mesh.getTexCoords();

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setFloatArray("a_texcoord1", texcoords);
    }

    private GVRSceneObject makeObject(GVRContext ctx, float w, float h)
    {
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject sceneObj = new GVRSceneObject(ctx, w, h, "float3 a_position float2 a_texcoord", mtl);
        GVRRenderData rd = sceneObj.getRenderData();

        mtl.setDiffuseColor(0, 1, 0, 0.5f);
        rd.setAlphaBlend(true);
        rd.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        rd.setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
        rd.setDepthTest(false);
        rd.setAlphaToCoverage(true);
        return sceneObj;
    }

    @Test
    public void testAlphaToCoverage() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRCameraRig rig = ctx.getMainScene().getMainCameraRig();
        GVRSceneObject middle = makeObject(ctx, 3, 3);
        GVRSceneObject bottom = makeObject(ctx, 2, 2);
        GVRSceneObject top = makeObject(ctx, 2, 2);

        rig.getLeftCamera().setBackgroundColor(1, 1, 1, 1);
        rig.getRightCamera().setBackgroundColor(1, 1, 1, 1);
        top.getTransform().setPosition(0.5f, 0, -2);
        top.getRenderData().getMaterial().setDiffuseColor(0, 1, 0, 0.5f);
        middle.getTransform().setPosition(0, 0, -2.2f);
        middle.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 0.5f);
        bottom.getTransform().setPosition(-1, 0, -3);
        bottom.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 0.5f);
        mRoot.addChildObject(top);
        mRoot.addChildObject(middle);
        mRoot.addChildObject(bottom);
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

        light.setSpecularIntensity(0.5f, 0.5f, 0.5f, 1.0f);
        lightObj.attachComponent(light);
        scene.addSceneObject(lightObj);
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
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
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

        GVRSceneObject rig = scene.getMainCameraRig().getOwnerObject();
        rig.attachComponent(light);
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
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getTransform().setPositionZ(-2.0f);
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
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
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
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
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
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
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


    @Test
    public void testSwitchTextures() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        String[] texFiles = new String[] { "NumberOne.png", "NumberTwo.png" };
        GVRTexture[] textures = new GVRTexture[texFiles.length];
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, texFiles.length);
        int i = 0;
        ctx.getEventReceiver().addListener(texHandler);
        try
        {
            for (String texFile : texFiles)
            {
                GVRAndroidResource r = new GVRAndroidResource(ctx, texFile);
                textures[i++] = ctx.getAssetLoader().loadTexture(r);
            }
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        GVRTexture tex = new GVRTexture(ctx);
        GVRSceneObject quad = new GVRSceneObject(ctx, 2, 2, tex);
        quad.getTransform().setPositionZ(-4.0f);
        scene.addSceneObject(quad);
        for (i = 0; i < 1000; ++i)
        {
            GVRTexture t = textures[i % texFiles.length];
            GVRImage image = t.getImage();
            tex.setImage(image);
            mTestUtils.waitForXFrames(1);
        }
    }

    public void checkResults(int actual, int truth)
    {
        mWaiter.assertEquals(truth, actual);
    }
}

