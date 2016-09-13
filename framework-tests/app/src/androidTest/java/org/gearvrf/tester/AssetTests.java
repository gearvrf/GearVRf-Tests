package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IErrorEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.IAssetEvents;

import org.gearvrf.utility.FileNameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class AssetTests
{
    private static final String TAG = AssetTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private boolean mDoCompare = false;
    private AssetEventHandler mHandler;

    class AssetEventHandler implements IAssetEvents
    {
        public int TexturesLoaded = 0;
        public int ModelsLoaded = 0;
        public int TextureErrors = 0;
        public int ModelErrors = 0;
        public String AssetErrors = null;
        public int AssetsLoaded = 0;
        protected GVRScene mScene;

        AssetEventHandler(GVRScene scene)
        {
            mScene = scene;
        }
        public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath, String errors)
        {
            AssetErrors = errors;
            if (model != null)
            {
                GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
                float sf = 1 / (4.0f * bv.radius);
                model.getTransform().setScale(sf, sf, sf);
                bv = model.getBoundingVolume();
                model.getTransform().setPosition(-bv.center.x, -bv.center.y, -bv.center.z - 1.5f * bv.radius);
            }
            mTestUtils.onAssetLoaded(model);
        }
        public void onModelLoaded(GVRContext context, GVRSceneObject model, String filePath)
        {
            ModelsLoaded++;
        }
        public void onTextureLoaded(GVRContext context, GVRTexture texture, String filePath)
        {
            TexturesLoaded++;
        }
        public void onModelError(GVRContext context, String error, String filePath)
        {
            ModelErrors++;
        }
        public void onTextureError(GVRContext context, String error, String filePath)
        {
            TextureErrors++;
        }

        public void checkAssetLoaded(Waiter waiter, String name, int numTex)
        {
            mWaiter.assertEquals(1, ModelsLoaded);
            mWaiter.assertEquals(0, ModelErrors);
            mWaiter.assertEquals(numTex, TexturesLoaded);
            if (name != null)
            {
                mWaiter.assertNotNull(mScene.getSceneObjectByName(name));
            }
            mWaiter.resume();
        }

        public void checkAssetErrors(Waiter waiter, int numModelErrors, int numTexErrors)
        {
            mWaiter.assertEquals(numModelErrors, ModelErrors);
            mWaiter.assertEquals(numTexErrors, TextureErrors);
            mWaiter.resume();
        }
    };

    @Rule
    public ActivityTestRule<TestableActivity> ActivityRule = new ActivityTestRule<TestableActivity>(TestableActivity.class)
    {
        protected void afterActivityFinished() {
            mTestUtils.getMainScene().clear();
        }
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
        mBackground = new GVRCubeSceneObject(ctx, false);
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.getRenderData().setShaderTemplate(GVRPhongShader.class);
        mBackground.setName("background");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mHandler = new AssetEventHandler(scene);
    }

    public void centerModel(GVRSceneObject model)
    {
        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        float sf = 1 / (4.0f * bv.radius);
        model.getTransform().setScale(sf, sf, sf);
        bv = model.getBoundingVolume();
        model.getTransform().setPosition(-bv.center.x, -bv.center.y, -bv.center.z - 1.5f * bv.radius);
    }

    public void loadTestModel(String modelfile, int numTex, String testname) throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel(modelfile, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(mWaiter, FileNameUtils.getFilename(modelfile), numTex);
        mHandler.checkAssetErrors(mWaiter, 0, 0);
        if (testname != null)
        {
            mTestUtils.screenShot("AssetTests", testname, mWaiter, mDoCompare);
        }
    }

    @Test
    public void canLoadModel() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(mWaiter, null, 4);
        mWaiter.assertNull(scene.getSceneObjectByName("astro_boy.dae"));
        mHandler.checkAssetErrors(mWaiter, 0, 0);
        scene.addSceneObject(model);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot("AssetTests", "canLoadModel", mWaiter, mDoCompare);
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
        mHandler.checkAssetLoaded(mWaiter, null, 4);
        mWaiter.assertNull(scene.getSceneObjectByName("astro_boy.dae"));
        mHandler.checkAssetErrors(mWaiter, 0, 0);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot("AssetTests", "canLoadModelWithHandler", mWaiter, mDoCompare);
    }

    @Test
    public void canLoadModelInScene() throws TimeoutException
    {
        loadTestModel("jassimp/astro_boy.dae", 4, "canLoadModelInScene");
    }
/*
    @Test
    public void canLoadModelSceneObject() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRModelSceneObject model = new GVRModelSceneObject(ctx, "jassimp/astro_boy.dae");

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel(model, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mWaiter.assertNotNull(model);
        mTestUtils.waitForAssetLoad();
        mHandler.checkAssetLoaded(mWaiter, "astro_boy.dae", 4);
        mHandler.checkAssetErrors(mWaiter, 0, 0);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot("AssetTests", "canLoadModelSceneObject", mWaiter, mDoCompare);
    }
*/

    @Test
    public void jassimpBench() throws TimeoutException
    {
        loadTestModel("jassimp/bench.dae", 0, "jassimpBench");
    }


    @Test
    public void x3dTeapotTorus() throws TimeoutException
    {
        loadTestModel("x3d/teapottorusdirlights.x3d", 2, "x3dTeapotTorus");
    }

}
