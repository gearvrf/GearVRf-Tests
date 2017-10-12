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

class TextureEventHandler implements IAssetEvents
{
    public int TexturesLoaded = 0;
    public int TextureErrors = 0;
    protected GVRTestUtils mTester;
    protected int mNumTextures = 0;

    TextureEventHandler(GVRTestUtils tester, int numTex)
    {
        mTester = tester;
        mNumTextures = numTex;
    }

    public void reset() { TexturesLoaded = 0; TextureErrors = 0; }
    public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath, String errors) { }
    public void onModelLoaded(GVRContext context, GVRSceneObject model, String filePath) { }
    public void onModelError(GVRContext context, String error, String filePath) { }

    public void onTextureLoaded(GVRContext context, GVRTexture texture, String filePath)
    {
        TexturesLoaded++;
        if ((TexturesLoaded + TextureErrors) == mNumTextures)
        {
            mTester.onAssetLoaded(null);
        }
    }

    public void onTextureError(GVRContext context, String error, String filePath)
    {
        TextureErrors++;
        if ((TexturesLoaded + TextureErrors) == mNumTextures)
        {
            mTester.onAssetLoaded(null);
        }
    }

    public void checkTextureLoaded(Waiter waiter)
    {
        waiter.assertEquals(1, TexturesLoaded);
    }

};

