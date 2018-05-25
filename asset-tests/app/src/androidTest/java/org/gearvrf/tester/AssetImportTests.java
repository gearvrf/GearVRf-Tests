package org.gearvrf.tester;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRExternalScene;
import org.gearvrf.GVRLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRResourceVolume;
import org.gearvrf.GVRImportSettings;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRVertexBuffer;
import org.gearvrf.animation.GVRAnimator;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;

import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class AssetImportTests
{
    private static final String TAG = AssetImportTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private boolean mDoCompare = true;
    private AssetEventHandler mHandler;

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

        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();

        mWaiter.assertNotNull(scene);
        mBackground = new GVRCubeSceneObject(ctx, false, new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID));
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.setName("background");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mHandler = new AssetEventHandler(scene, mWaiter, mTestUtils, getClass().getSimpleName());
    }

    @Test
    public void canLoadModel() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        mHandler.dontAddToScene();
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 4);
        mWaiter.assertNull(scene.getSceneObjectByName("astro_boy.dae"));
        mHandler.checkAssetErrors(0, 0);
        scene.addSceneObject(model);
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot("AssetImportTests", "canLoadModel", mWaiter, mDoCompare);
    }

    @Test
    public void canStartAnimations() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.START_ANIMATIONS));
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", settings, false, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot("AssetImportTests", "canStartAnimations", mWaiter, mDoCompare);
    }

    @Test
    public void canLoadModelWithHandler() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", mHandler);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 4);
        mWaiter.assertTrue(model.getChildrenCount() > 0);
        mHandler.checkAssetErrors(0, 0);
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        scene.addSceneObject(model);
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot("AssetImportTests", "canLoadModelWithHandler", mWaiter, mDoCompare);
    }

    @Test
    public void canLoadModelWithCustomIO() throws TimeoutException
    {
        class ResourceLoader extends GVRResourceVolume
        {
            class Resource extends GVRAndroidResource
            {
                public Resource(GVRContext ctx, String path) throws IOException
                {
                    super(ctx, path);
                }

                public synchronized void openStream() throws IOException
                {
                    // do some special stuff here
                    super.openStream();
                }
            }
            public int ResourcesLoaded = 0;

            public ResourceLoader(GVRContext ctx, String fileName)
            {
                super(ctx, fileName);
            }

            public GVRAndroidResource openResource(String filePath) throws IOException
            {
                ++ResourcesLoaded;
                Resource resource = new Resource(gvrContext, getFullPath(defaultPath, filePath));
                return super.addResource(resource);
            }
        };
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRModelSceneObject model = new GVRModelSceneObject(ctx);
        ResourceLoader volume = new ResourceLoader(ctx, "jassimp/astro_boy.dae");
        EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_ANIMATION));

        mHandler.dontAddToScene();
        ctx.getAssetLoader().loadModel(volume, model, settings, false, mHandler);
        mTestUtils.waitForAssetLoad();
        mWaiter.assertEquals(8, volume.ResourcesLoaded);
        mHandler.checkAssetLoaded(null, 4);
        mWaiter.assertNull(scene.getSceneObjectByName("astro_boy.dae"));
        mWaiter.assertTrue(model.getChildrenCount() > 0);
        mHandler.checkAssetErrors(0, 0);
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        scene.addSceneObject(model);
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot("AssetImportTests", "canLoadModelWithCustomIO", mWaiter, false);
    }


    @Test
    public void canLoadModelInScene() throws TimeoutException
    {
        mHandler.loadTestModel("jassimp/astro_boy.dae", 4, 0, "canLoadModelInScene");
    }

    @Test
    public void canLoadExternalScene() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRExternalScene sceneLoader = new GVRExternalScene(ctx, "jassimp/astro_boy.dae", true);
        GVRSceneObject model = new GVRSceneObject(ctx);

        ctx.getEventReceiver().addListener(mHandler);
        model.attachComponent(sceneLoader);
        sceneLoader.load(scene);
        mWaiter.assertNotNull(model);
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded("astro_boy.dae", 4);
        mHandler.checkAssetErrors(0, 0);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot("AssetImportTests", "canLoadExternalScene", mWaiter, mDoCompare);
    }

    class MeshVisitorNoAnim implements GVRSceneObject.ComponentVisitor
    {
        public boolean visit(GVRComponent comp)
        {
            GVRRenderData rdata = (GVRRenderData) comp;
            GVRMesh mesh = rdata.getMesh();
            if (mesh != null)
            {
                GVRVertexBuffer vbuf = mesh.getVertexBuffer();
                mWaiter.assertNotNull(vbuf);
                mWaiter.assertTrue(vbuf.hasAttribute("a_position"));
                mWaiter.assertTrue(vbuf.hasAttribute("a_normal"));
                mWaiter.assertFalse(vbuf.hasAttribute("a_bone_weights"));
                mWaiter.assertFalse(vbuf.hasAttribute("a_bone_indices"));
            }
            return true;
        }
    }

    class MeshVisitorNoLights implements GVRSceneObject.ComponentVisitor
    {
        public boolean visit(GVRComponent comp)
        {
            if (comp.getClass().isAssignableFrom(GVRRenderData.class))
            {
                GVRRenderData rdata = (GVRRenderData) comp;
                GVRMesh mesh = rdata.getMesh();
                if (mesh != null)
                {
                    GVRVertexBuffer vbuf = mesh.getVertexBuffer();
                    mWaiter.assertNotNull(vbuf);
                    mWaiter.assertTrue(vbuf.hasAttribute("a_position"));
                    mWaiter.assertFalse(vbuf.hasAttribute("a_normal"));
                    mWaiter.assertTrue(vbuf.hasAttribute("a_texcoord"));
                }
            }
            else if (comp.getClass().isAssignableFrom(GVRLight.class))
            {
                mWaiter.fail("Light sources are present and should not be");
            }
            return true;
        }
    }

    class MeshVisitorNoTexture implements GVRSceneObject.ComponentVisitor
    {
        public boolean visit(GVRComponent comp)
        {
            GVRRenderData rdata = (GVRRenderData) comp;
            GVRMesh mesh = rdata.getMesh();
            if (mesh != null)
            {
                GVRVertexBuffer vbuf = mesh.getVertexBuffer();
                mWaiter.assertNotNull(vbuf);
                mWaiter.assertTrue(vbuf.hasAttribute("a_position"));
                mWaiter.assertTrue(vbuf.hasAttribute("a_normal"));
                mWaiter.assertFalse(vbuf.hasAttribute("a_texcoord"));
            }
            return true;
        }
    }

    @Test
    public void canLoadModelWithoutAnimation() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_ANIMATION));
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(5);
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNull(model.getComponent(GVRAnimator.getComponentType()));
        model.forAllComponents(new MeshVisitorNoAnim(), GVRRenderData.getComponentType());
    }

    @Test
    public void canLoadX3DModelWithoutAnimation() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_ANIMATION));
            model = ctx.getAssetLoader().loadModel(GVRTestUtils.GITHUB_URL + "x3d/animation/animation04.x3d", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(5);
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNull(model.getComponent(GVRAnimator.getComponentType()));
        model.forAllComponents(new MeshVisitorNoAnim(), GVRRenderData.getComponentType());
    }

    @Test
    public void canLoadModelWithoutTextures() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_TEXTURING));
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 0);
        mHandler.checkAssetErrors(0, 0);
        model.forAllComponents(new MeshVisitorNoTexture(), GVRRenderData.getComponentType());
    }

    @Test
    public void canX3DLoadModelWithoutTextures() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_TEXTURING));
            model = ctx.getAssetLoader().loadModel(GVRTestUtils.GITHUB_URL + "x3d/general/twoplanesobjects.x3d", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 0);
        mHandler.checkAssetErrors(0, 0);
        model.forAllComponents(new MeshVisitorNoTexture(), GVRRenderData.getComponentType());
    }

    @Test
    public void canLoadModelWithoutLights() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;
        GVRPointLight light = new GVRPointLight(ctx);
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRCameraRig rig = scene.getMainCameraRig();

        rig.getLeftCamera().setBackgroundColor(1, 1, 0.5f, 1.0f);
        rig.getRightCamera().setBackgroundColor(1, 1, 0.5f, 1.0f);
        rig.getCenterCamera().setBackgroundColor(1, 1, 0.5f, 1.0f);
        ctx.getEventReceiver().addListener(mHandler);
        lightObj.attachComponent(light);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_LIGHTING));
            model = ctx.getAssetLoader().loadModel( "gear_vr_controller.obj", settings, true, (GVRScene) scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 1);
        mHandler.checkAssetErrors(0, 0);
        model.getTransform().setPosition(0, 0, -0.2f);
        model.forAllComponents(new MeshVisitorNoLights());
    }

    @Test
    public void canLoadX3DModelWithoutLights() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_LIGHTING));
            model = ctx.getAssetLoader().loadModel(GVRTestUtils.GITHUB_URL + "x3d/lighting/pointlightmultilights.x3d", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        model.forAllComponents(new MeshVisitorNoLights());
    }

    @Test
    public void PLYVertexColors() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;
        String modelName = "man128.ply";

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/" + modelName, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(null, 0);
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        mHandler.checkAssetErrors(0, 0);
        List<GVRRenderData> rdatas = model.getAllComponents(GVRRenderData.getComponentType());
        GVRMaterial vertexColorMtl = new GVRMaterial(ctx, new GVRShaderId(VertexColorShader.class));
        for (GVRRenderData rdata : rdatas)
        {
            rdata.setMaterial(vertexColorMtl);
        }
        scene.addSceneObject(model);
        mWaiter.assertNotNull(scene.getSceneObjectByName(modelName));
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot("AssetImportTests", "PLYVertexColors", mWaiter, mDoCompare);
    }

    @Test
    public void jassimpTrees3DS() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/trees/trees9.3ds", 9, 0, "jassimpTrees3DS");
    }

    @Test
    public void jassimpBenchCollada() throws TimeoutException
    {
        mHandler.loadTestModel("jassimp/bench.dae", 0, 0, "jassimpBenchCollada");
    }

    @Test
    public void jassimpHippoOBJ() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/hippo/hippo.obj", 1, 0, "jassimpHippoOBJ");
    }

    @Test
    public void jassimpDeerOBJ() throws TimeoutException
    {
        GVRAndroidResource res = new GVRAndroidResource(mTestUtils.getGvrContext(), R.raw.deerobj);
        mHandler.loadTestModel(res, 1, 0, "jassimpDeerOBJ");
    }

    @Test
    public void jassimpBearOBJ() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/animals/bear-obj.obj", 5, 0, "jassimpBearOBJ");
    }

    @Test
    public void jassimpWolfOBJ() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/animals/wolf-obj.obj", 5, 0, "jassimpWolfOBJ");
    }

    @Test
    public void jassimpSkinningTREX() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/trex/TRex_NoGround.fbx", 1, 0, "jassimpSkinningTREX");
    }

    @Test
    public void jassimpRoughBoomboxGLTF() throws TimeoutException
    {
        GVRSceneObject light1 = createLight(mTestUtils.getGvrContext(), 1, 1, 1, 1.8f);
        GVRSceneObject light2 = createLight(mTestUtils.getGvrContext(), 1, 1, 1, -0.8f);
        mRoot.addChildObject(light1);
        mRoot.addChildObject(light2);
    }

    @Test
    public void jassimpGlossWaterBottleGLTF() throws TimeoutException
    {
        GVRSceneObject light1 = createLight(mTestUtils.getGvrContext(), 1, 1, 1, 1.8f);
        GVRSceneObject light2 = createLight(mTestUtils.getGvrContext(), 1, 1, 1, -0.8f);
        mRoot.addChildObject(light1);
        mRoot.addChildObject(light2);
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "/jassimp/gltf/WaterBottle/WaterBottle.gltf", 6, 1, "jassimpGlossWaterBottleGLTF");
    }

    @Test
    public void jassimpTelephonePBRGLTF() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRPointLight pointLight = new GVRPointLight(ctx);

        pointLight.setDiffuseIntensity(0.8f, 0.8f, 08f, 1.0f);
        pointLight.setSpecularIntensity(0.8f, 0.8f, 08f, 1.0f);
        lightObj.attachComponent(pointLight);
        lightObj.getTransform().setPosition(-1.0f, 1.0f, 0);
        scene.addSceneObject(lightObj);
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/gltf/Telephone/Telephone.gltf", 2, 0, "jassimpTelephonePBRGLTF");
    }

    @Test
    public void jassimpCowPBRGLTF() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRPointLight pointLight = new GVRPointLight(ctx);

        pointLight.setDiffuseIntensity(0.8f, 0.8f, 08f, 1.0f);
        pointLight.setSpecularIntensity(0.8f, 0.8f, 08f, 1.0f);
        lightObj.attachComponent(pointLight);
        lightObj.getTransform().setPosition(-1.0f, 1.0f, 0);
        scene.addSceneObject(lightObj);
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/gltf/cow/cow.gltf", 2, 0, "jassimpCowPBRGLTF");
    }

    @Test
    public void jassimpEngineBinaryGLTF() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/gltf/2CylinderEngine-glTF-Binary/2CylinderEngine.glb", 0, 0, "jassimpEngineBinaryGLTF");
    }

    @Test
    public void jassimpLoadError() throws TimeoutException
    {
        try
        {
            mTestUtils.getGvrContext().getAssetLoader().loadModel("missingmodel.obj");
        }
        catch (IOException ex)
        {
            mWaiter.assertTrue(ex.getMessage().contains("FileNotFoundException"));
            return;
        }
        catch (Exception ex)
        {
            mWaiter.fail(ex);
        }
    }

    @Test
    public void x3dTeapotTorus() throws TimeoutException
    {
        mHandler.loadTestModel("x3d/teapottorusdirlights.x3d", 2, 0, "x3dTeapotTorus");
    }

    @Test
    public void x3dGenerateNormals() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/generate_normals/teapotandtorusnonormals.x3d", 2, 0, "x3dGenerateNormals");
    }

    @Test
    public void x3dOpacity() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/general/opacitytest01.x3d", 2, 0, "x3dOpacity");
    }

    @Test
    public void x3dEmissive() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/general/emissivecolor.x3d", 0, 0, "x3dEmissive");
    }

    @Test
    public void x3dHierarchy() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/general/twoplaneswithchildren.x3d", 5, 0, "x3dHierarchy");
    }

    @Test
    public void x3dLoadError() throws TimeoutException
    {
        try
        {
            mTestUtils.getGvrContext().getAssetLoader().loadModel("missingmodel.x3d");
        }
        catch (IOException ex)
        {
            mWaiter.assertTrue(ex.getMessage().contains("FileNotFoundException"));
        }
        catch (Exception ex)
        {
            mWaiter.fail(ex);
        }

        try
        {
            mTestUtils.getGvrContext().getAssetLoader().loadModel("http://blah.blah/missingmodel.x3d");
        }
        catch (IOException ex)
        {
            mWaiter.assertTrue(ex.getMessage().contains("UnknownHostException"));
        }
        catch (Exception ex)
        {
            mWaiter.fail(ex);
        }
    }

    private GVRSceneObject createLight(GVRContext context, float r, float g, float b, float y)
    {
        GVRSceneObject lightNode = new GVRSceneObject(context);
        GVRSpotLight light = new GVRSpotLight(context);

        lightNode.attachLight(light);
        lightNode.getTransform().setPosition(0, y, 3);
        light.setAmbientIntensity(0.3f * r, 0.3f * g, 0.3f * b, 1);
        light.setDiffuseIntensity(r, g, b, 1);
        light.setSpecularIntensity(r, g, b, 1);
        light.setInnerConeAngle(8);
        light.setOuterConeAngle(12);
        return lightNode;
    }


}
