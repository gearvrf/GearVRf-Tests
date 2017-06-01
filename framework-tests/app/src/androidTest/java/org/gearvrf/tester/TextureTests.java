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
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;


@RunWith(AndroidJUnit4.class)
public class TextureTests
{
    private static final String TAG = TextureTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private GVRMaterial mBlueMtl;
    private GVRMaterial mCubeMapMtl;
    private boolean mDoCompare = true;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class)
    {
        /*
        protected void afterActivityFinished() {
            GVRScene scene = mTestUtils.getMainScene();
            if (scene != null) {
                scene.clear();
            }
        }
        */
    };
    @Before
    public void setUp() throws TimeoutException
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        Future<GVRTexture> tex = ctx.loadFutureCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));

        mWaiter.assertNotNull(scene);
        mBlueMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        mCubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);

        mBackground = new GVRCubeSceneObject(ctx, false);
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.getRenderData().setMaterial(mCubeMapMtl);
        mBackground.setName("background");
        mBlueMtl.setDiffuseColor(0, 0, 1, 1);
        mCubeMapMtl.setMainTexture(tex);
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

    @Test
    public void testAlphaToCoverage() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
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
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);

        rd = polygon.getRenderData();
        polygon.setName("polygon");
        polygon.getTransform().setPosition(0, 1, -3);
        mtl.setDiffuseColor(0, 1, 0, 0.5f);
        rd.setMaterial(mtl);
        rd.setShaderTemplate(GVRPhongShader.class);
        rd.setAlphaBlend(true);
        rd.setAlphaToCoverage(true);
        rd.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        rd.setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
        mRoot.addChildObject(t);
        mRoot.addChildObject(polygon);
        scene.bindShaders();
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testAlphaToCoverage", mWaiter, mDoCompare);
    }

    @Test
    public void testCompressedTextureASTC() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        final GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        final GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        final GVRDirectLight light = new GVRDirectLight(ctx);
        GVRSceneObject lightObj = new GVRSceneObject(ctx);

        light.setSpecularIntensity(0.5f, 0.5f, 0.5f, 1.0f);
        lightObj.attachComponent(light);
        scene.addSceneObject(lightObj);

        GVRAndroidResource.TextureCallback texLoadCallback = new GVRAndroidResource.TextureCallback()
        {
            public boolean stillWanted(GVRAndroidResource r) { return true; }
            public void loaded(GVRTexture tex, GVRAndroidResource r)
            {
                mtl.setTexture("diffuseTexture", tex);
                model.getRenderData().setShaderTemplate(GVRPhongShader.class);
                mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
                mtl.setSpecularColor(0.5f, 0.5f, 0.5f, 1);
                mtl.setSpecularExponent(10.0f);
                scene.getMainCameraRig().getOwnerObject().attachComponent(light);
                model.getTransform().setPositionZ(-2.0f);
                scene.addSceneObject(model);
                mWaiter.resume();
            }
            public void failed(Throwable t, GVRAndroidResource r) { mWaiter.fail(t); }
        };
        try
        {
            ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, "sunmap.astc"), texLoadCallback);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mWaiter.await();
        mTestUtils.screenShot(getClass().getSimpleName(), "testCompressedTextureASTC", mWaiter, mDoCompare);
    }

    @Test
    public void testLayeredDiffuseTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);
        GVRDirectLight light = new GVRDirectLight(ctx);

        layeredMtl.setTexture("diffuseTexture", tex1);
        layeredMtl.setTexture("diffuseTexture1", tex2);
        layeredMtl.setFloat("diffuseTexture1_blendop", 1);
        layeredMtl.setTexCoord("diffuseTexture", "a_texcoord", "diffuse_coord");
        layeredMtl.setTexCoord("diffuseTexture1", "a_texcoord", "diffuse_coord1");
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredDiffuseTexture", mWaiter, mDoCompare);
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
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        float[] texcoords = mesh.getTexCoords();
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setTexCoords(texcoords);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testRepeatTexture", mWaiter, mDoCompare);
    }


    @Test
    public void testSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));

        mtl.setDiffuseColor(0.7f, 0, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularTexture", mWaiter, mDoCompare);
    }


    @Test
    public void testLayeredSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial layeredMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.wavylines));
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, layeredMtl);
        GVRDirectLight light = new GVRDirectLight(ctx);

        layeredMtl.setDiffuseColor(0.7f, 0.2f, 0.2f, 1.0f);
        layeredMtl.setSpecularColor(1, 1, 1, 1);
        layeredMtl.setSpecularExponent(4.0f);
        layeredMtl.setTexture("specularTexture", tex1);
        layeredMtl.setTexture("specularTexture1", tex2);
        layeredMtl.setFloat("specularTexture1_blendop", 0);
        layeredMtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        layeredMtl.setTexCoord("specularTexture1", "a_texcoord", "specular_coord1");
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testLayeredSpecularTexture", mWaiter, mDoCompare);
    }

    @Test
    public void testDiffuseSpecularTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        float[] texcoords = mesh.getTexCoords();
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.specularring));

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setVec2Vector("a_texcoord1", texcoords);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("specularTexture", tex2);
        mtl.setTexCoord("diffuseTexture", "a_texcoord1", "diffuse_coord");
        mtl.setTexCoord("specularTexture", "a_texcoord", "specular_coord");
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseSpecularTexture", mWaiter, mDoCompare);
    }


    @Test
    public void testDiffuseNormalTexture() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRSceneObject model = new GVRCubeSceneObject(ctx, true, mtl);
        GVRDirectLight light = new GVRDirectLight(ctx);
        GVRMesh mesh = model.getRenderData().getMesh();
        GVRTextureParameters texparams = new GVRTextureParameters(ctx);
        texparams.setWrapSType(GVRTextureParameters.TextureWrapType.GL_REPEAT);
        texparams.setWrapTType(GVRTextureParameters.TextureWrapType.GL_REPEAT);

        float[] texcoords = mesh.getTexCoords();
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex), texparams);
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.rock_normal));

        for (int i = 0; i < texcoords.length; i++)
        {
            texcoords[i] *= 2.0f;
        }
        mesh.setVec2Vector("a_texcoord1", texcoords);
        mtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1);
        mtl.setSpecularColor(1, 1, 1, 1);
        mtl.setSpecularExponent(4.0f);
        mtl.setTexture("diffuseTexture", tex1);
        mtl.setTexture("normalTexture", tex2);
        mtl.setTexCoord("diffuseTexture", "a_texcoord1", "diffuse_coord");
        mtl.setTexCoord("normalTexture", "a_texcoord", "normal_coord");
        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        model.getRenderData().setShaderTemplate(GVRPhongShader.class);
        model.getTransform().setPositionZ(-2.0f);
        scene.addSceneObject(model);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testDiffuseNormalTexture", mWaiter, mDoCompare);
    }

    // TODO: wait for asset to load so the screen shot captures the render
    @Test
    public void testNormalDiffuseSpecularLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sceneObj;
        GVRDirectLight light = new GVRDirectLight(ctx);

        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        try
        {
            sceneObj = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_diffuse_specular_lightmap.fbx", scene);
            GVRTransform trans = sceneObj.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = sceneObj.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormalDiffuseSpecularLightmap", mWaiter, false);
    }

    @Test
    public void testNormaLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sceneObj;
        GVRDirectLight light = new GVRDirectLight(ctx);

        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        try
        {
            sceneObj = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/normal_lightmap.fbx", scene);
            GVRTransform trans = sceneObj.getTransform();
            trans.setScale(0.006f, 0.006f, 0.006f);
            trans.rotateByAxis(90.0f, 1, 0, 0);
            GVRSceneObject.BoundingVolume bv = sceneObj.getBoundingVolume();
            trans.setPosition(-bv.center.x + 0.05f, -bv.center.y - 0.05f, -bv.center.z - 4.5f);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testNormaLightmap", mWaiter, false);
    }

    @Test
    public void testSpecularLightmap() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sceneObj;
        GVRDirectLight light = new GVRDirectLight(ctx);

        scene.getMainCameraRig().getOwnerObject().attachComponent(light);
        try
        {
            sceneObj = ctx.getAssetLoader().loadModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/asset-tests/app/src/main/assets/jassimp/specular_lightmap.fbx", scene);
            GVRTransform trans = sceneObj.getTransform();
            trans.rotateByAxis(90.0f, 1, 0, 0);
            centerModel(sceneObj);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForXFrames(3);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSpecularLightmap", mWaiter, false);
    }

    @Test
    public void testLoadTextureFromResource() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTexture texture = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);

        material.setTexture("diffuseTexture", texture);
        groundObject.getRenderData().setShaderTemplate(GVRPhongShader.class);
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
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTexture jpg_geometry = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.jpg_geometry));
        GVRMaterial material = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        material.setTexture("diffuseTexture", jpg_geometry);
        GVRSceneObject groundObject = new GVRCubeSceneObject(ctx, true, material);

        // load jpg, RenderOrder == GEOMETRY
        GVRRenderData renderData = groundObject.getRenderData();
        renderData.setShaderTemplate(GVRPhongShader.class);
        int order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load png, 4 component, transparency, RenderOrder == TRANSPARENT
        GVRTexture png_4_transparent = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_transparent));
        material.setTexture("diffuseTexture", png_4_transparent);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load png, 3 component, RenderOrder == GEOMETRY
        GVRTexture png_3_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_3_opaque));
        material.setTexture("diffuseTexture", png_3_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load tga, 4 component, transparency, RenderOrder == TRANSPARENT 
        GVRTexture tga_4_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_transparency));
        material.setTexture("diffuseTexture", tga_4_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load png, 4 component, opaque, RenderOrder == GEOMETRY
        GVRTexture png_4_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.png_4_opaque));
        material.setTexture("diffuseTexture", png_4_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load astc, RenderOrder == TRANSPARENT
        GVRTexture astc_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.astc_transparency));
        material.setTexture("diffuseTexture", astc_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load tga, 3 component, RenderOrder == GEOMETRY
        GVRTexture tga_3_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_3_opaque));
        material.setTexture("diffuseTexture", tga_3_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load etc2, GL_COMPRESSED_RG11_EAC, RenderOrder == TRANSPARENT 
        GVRTexture etc2_rg11_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rg11_transparency));
        material.setTexture("diffuseTexture", etc2_rg11_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load tga, 4 component, opaque, RenderOrder == GEOMETRY
        GVRTexture tga_4_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.tga_4_opaque));
        material.setTexture("diffuseTexture", tga_4_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);


        // load etc2, GL_COMPRESSED_SIGNED_RG11_EAC, RenderOrder == TRANSPARENT
        GVRTexture etc2_signed_rg11_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_rg11_transparency));
        material.setTexture("diffuseTexture", etc2_signed_rg11_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load etc2, GL_COMPRESSED_R11_EAC, RenderOrder == GEOMETRY
        GVRTexture etc2_r11_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_r11_opaque));
        material.setTexture("diffuseTexture", etc2_r11_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load etc2, GL_COMPRESSED_RGBA8_ETC2_EAC, RenderOrder == TRANSPARENT
        GVRTexture etc2_rgba8_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgba8_transparency));
        material.setTexture("diffuseTexture", etc2_rgba8_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load etc2, GL_COMPRESSED_SIGNED_R11_EAC, RenderOrder == GEOMETRY
        GVRTexture etc2_signed_r11_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_signed_r11_opaque));
        material.setTexture("diffuseTexture", etc2_signed_r11_opaque);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

        // load etc2, GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2, RenderOrder == TRANSPARENT
        GVRTexture etc2_rgb8_punchthrough_alpha1_transparency = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_punchthrough_alpha1_transparency));
        material.setTexture("diffuseTexture", etc2_rgb8_punchthrough_alpha1_transparency);
        renderData.setMaterial(material);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        // load etc2, GL_COMPRESSED_RGB8_ETC2, RenderOrder == GEOMETRY
        // test setting texture after setting material
        GVRTexture etc2_rgb8_opaque = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.etc2_rgb8_opaque));
        renderData.setMaterial(material);
        material.setTexture("diffuseTexture", etc2_rgb8_opaque);
        order = renderData.getRenderingOrder();
        checkResults(order, GVRRenderData.GVRRenderingOrder.GEOMETRY);

    }

}

