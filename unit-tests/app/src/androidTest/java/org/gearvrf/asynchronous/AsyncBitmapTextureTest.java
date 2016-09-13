package org.gearvrf.asynchronous;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by j.elidelson on 8/21/2015.
 */
public class AsyncBitmapTextureTest extends ActivityInstrumentationGVRf {

    public void testloadTextureA() {
        GVRAndroidResource.CancelableCallback<GVRBitmapTexture> bitmapTextureCallback = new GVRAndroidResource.CancelableCallback<GVRBitmapTexture>() {
            @Override
            public void loaded(GVRBitmapTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }

            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }
        };
        GVRAndroidResource gvrAndroidResource = null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "texture1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AsyncBitmapTexture.loadTexture(TestDefaultGVRViewManager.mGVRContext, bitmapTextureCallback, gvrAndroidResource, GVRContext.LOWEST_PRIORITY);
    }

    public void testloadTextureB() {

        GVRAndroidResource.CancelableCallback<GVRBitmapTexture> cancelableCallback = new GVRAndroidResource.CancelableCallback<GVRBitmapTexture>() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRBitmapTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

        GVRAndroidResource gvrAndroidResource = null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "big_texture.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AsyncBitmapTexture.loadTexture(TestDefaultGVRViewManager.mGVRContext, cancelableCallback, gvrAndroidResource, GVRContext.HIGHEST_PRIORITY);
    }

    public void testDecodeStream() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("sample_20140509_l.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap=null;
        bitmap=AsyncBitmapTexture.decodeStream(is,1024,1024,true,bitmap,true);
        assertNotNull(bitmap);
    }
}