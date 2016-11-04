package org.gearvrf.tester;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IAssetEvents;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.utility.FileNameUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class AssetEventHandler implements IAssetEvents
{
    public int TexturesLoaded = 0;
    public int ModelsLoaded = 0;
    public int TextureErrors = 0;
    public int ModelErrors = 0;
    public String AssetErrors = null;
    public int AssetsLoaded = 0;
    protected GVRScene mScene;
    protected Waiter mWaiter;
    protected GVRTestUtils mTester;
    protected String mCategory;
    protected boolean mDoCompare = true;

    AssetEventHandler(GVRScene scene, Waiter waiter, GVRTestUtils tester, String category)
    {
        mScene = scene;
        mWaiter = waiter;
        mTester = tester;
        mCategory = category;
    }
    public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath, String errors)
    {
        AssetErrors = errors;
        mTester.onAssetLoaded(model);
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

    public void DisableImageCompare()
    {
        mDoCompare = false;
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

    public void centerModel(GVRSceneObject model)
    {
        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        float sf = 1 / bv.radius;
        model.getTransform().setScale(sf, sf, sf);
        bv = model.getBoundingVolume();
        model.getTransform().setPosition(-bv.center.x, -bv.center.y, -bv.center.z - 1.5f * bv.radius);
    }

    public GVRSceneObject loadTestModel(String modelfile, int numTex, int texError, String testname) throws TimeoutException
    {
        GVRContext ctx  = mTester.getGvrContext();
        GVRScene scene = mTester.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(this);
        try
        {
            model = ctx.getAssetLoader().loadModel(modelfile, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTester.waitForAssetLoad();
        centerModel(model);
        checkAssetLoaded(mWaiter, FileNameUtils.getFilename(modelfile), numTex);
        checkAssetErrors(mWaiter, 0, texError);
        if (testname != null)
        {
            mTester.waitForXFrames(2);
            mTester.screenShot(mCategory, testname, mWaiter, mDoCompare);
        }
        return model;
    }

    public void loadTestScene(String modelfile, int numTex, String testname) throws TimeoutException
    {
        GVRContext ctx  = mTester.getGvrContext();
        GVRScene scene = mTester.getMainScene();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(this);
        try
        {
            model = ctx.getAssetLoader().loadScene(modelfile, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTester.waitForAssetLoad();
        checkAssetLoaded(mWaiter, FileNameUtils.getFilename(modelfile), numTex);
        checkAssetErrors(mWaiter, 0, 0);
        if (testname != null)
        {
            mTester.waitForXFrames(2);
            mTester.screenShot(mCategory, testname, mWaiter, mDoCompare);
        }
    }

};

