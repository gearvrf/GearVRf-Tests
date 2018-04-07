package org.gearvrf.performance;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

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
import org.gearvrf.tester.TextureEventHandler;
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


public class TextureHandler implements IAssetEvents
{
    private GVRContext mContext;
    private boolean mTextureLoaded = false;
    private final Object onAssetLock = new Object();
    private final String TAG = "TEXTURE";

    public TextureHandler(GVRContext ctx)
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


