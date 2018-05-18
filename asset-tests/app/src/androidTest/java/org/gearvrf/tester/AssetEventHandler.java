package org.gearvrf.tester;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShader;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.IAssetEvents;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRImportSettings;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.utility.FileNameUtils;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeoutException;

class AssetEventHandler implements IAssetEvents
{
    public int TexturesLoaded = 0;
    public int ModelsLoaded = 0;
    public int TextureErrors = 0;
    public int ModelErrors = 0;
    public String AssetErrors = null;
    protected GVRScene mScene;
    protected Waiter mWaiter;
    protected GVRTestUtils mTester;
    protected String mCategory;
    protected boolean mDoCompare = true;
    protected boolean mAddToScene = true;

    AssetEventHandler(GVRScene scene, Waiter waiter, GVRTestUtils tester, String category)
    {
        mScene = scene;
        mWaiter = waiter;
        mTester = tester;
        mCategory = category;
        mAddToScene = true;
    }

    public void dontAddToScene()
    {
        mAddToScene = false;
    }

    public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath, String errors)
    {
        AssetErrors = errors;
        if (model != null)
        {
            if (mAddToScene)
            {
                mScene.addSceneObject(model);
                mTester.waitForXFrames(2);
            }
            mTester.onAssetLoaded(model);
        }
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

    public void checkModelLoaded(String name)
    {
        mWaiter.assertEquals(1, ModelsLoaded);
        mWaiter.assertEquals(0, ModelErrors);
        if (name != null)
        {
            mWaiter.assertNotNull(mScene.getSceneObjectByName(name));
        }
    }

    public void checkAssetLoaded(String name, int numTex)
    {
        mWaiter.assertEquals(1, ModelsLoaded);
        mWaiter.assertEquals(0, ModelErrors);
        mWaiter.assertEquals(numTex, TexturesLoaded);
        if (name != null)
        {
            mWaiter.assertNotNull(mScene.getSceneObjectByName(name));
        }
    }

    public void checkAssetErrors(int numModelErrors, int numTexErrors)
    {
        mWaiter.assertEquals(numModelErrors, ModelErrors);
        mWaiter.assertEquals(numTexErrors, TextureErrors);
    }

    public void centerModel(GVRSceneObject model, GVRTransform camTrans)
    {
        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        float x = camTrans.getPositionX();
        float y = camTrans.getPositionY();
        float z = camTrans.getPositionZ();
        float sf = 1 / bv.radius;
        model.getTransform().setScale(sf, sf, sf);
        bv = model.getBoundingVolume();
        model.getTransform().setPosition(x - bv.center.x, y - bv.center.y, z - bv.center.z - 1.5f * bv.radius);
    }

    public GVRSceneObject loadTestModel(String modelfile, int numtex)
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
        centerModel(model, scene.getMainCameraRig().getTransform());
        return model;
    }

    public GVRSceneObject loadTestModel(String modelfile, int numTex, int texError, String testname) throws TimeoutException
    {
        GVRContext ctx  = mTester.getGvrContext();
        GVRScene scene = mTester.getMainScene();
        GVRSceneObject model = null;

        try
        {
            model = ctx.getAssetLoader().loadModel(modelfile, this);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTester.waitForAssetLoad();
        mTester.waitForXFrames(10);

        centerModel(model, scene.getMainCameraRig().getTransform());
        checkAssetLoaded(FileNameUtils.getFilename(modelfile), numTex);
        checkAssetErrors(0, texError);
        if (testname != null)
        {
            mTester.screenShot(mCategory, testname, mWaiter, mDoCompare);
        }
        return model;
    }

    public GVRSceneObject loadTestModel(GVRAndroidResource res, int numTex, int texError, String testname) throws TimeoutException
    {
        GVRContext ctx  = mTester.getGvrContext();
        GVRScene scene = mTester.getMainScene();
        GVRSceneObject model = null;
        GVRTransform t = scene.getMainCameraRig().getTransform();

        ctx.getEventReceiver().addListener(this);
        try
        {
            model = ctx.getAssetLoader().loadModel(res,
                    GVRImportSettings.getRecommendedSettings(), true, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTester.waitForAssetLoad();
        centerModel(model, t);
        checkAssetLoaded(res.getResourceFilename(), numTex);
        checkAssetErrors(0, texError);
        if (testname != null)
        {
            mTester.waitForXFrames(2);
            mTester.screenShot(mCategory, testname, mWaiter, mDoCompare);
        }
        return model;
    }

    public GVRSceneObject loadTestModel(String modelfile, String testname,
                                        float scale, boolean rotX90, Vector3f pos) throws TimeoutException
    {
        GVRContext ctx  = mTester.getGvrContext();
        GVRScene scene = mTester.getMainScene();
        GVRSceneObject model = null;

        try
        {
            model = ctx.getAssetLoader().loadModel(modelfile, this);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTester.waitForAssetLoad();
        GVRTransform modelTrans = model.getTransform();
        modelTrans.setScale(scale, scale, scale);
        if (rotX90)
        {
            modelTrans.rotateByAxis(90.0f, 1, 0, 0);
        }
        if (pos != null)
        {
            GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
            modelTrans.setPosition(pos.x - bv.center.x, pos.y - bv.center.y, pos.z - bv.center.z);
        }
        else
        {
            centerModel(model, scene.getMainCameraRig().getTransform());
        }
        checkModelLoaded(FileNameUtils.getFilename(modelfile));

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
        checkAssetLoaded(FileNameUtils.getFilename(modelfile), numTex);
        checkAssetErrors(0, 0);
        if (testname != null)
        {
            mTester.waitForXFrames(2);
            mTester.screenShot(mCategory, testname, mWaiter, mDoCompare);
        }
    }

};

