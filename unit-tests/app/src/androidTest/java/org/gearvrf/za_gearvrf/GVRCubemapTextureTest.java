package org.gearvrf.za_gearvrf;

import android.graphics.Bitmap;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRCubemapTexture;

/**
 * Created by j.elidelson on 6/9/2015.
 */
public class GVRCubemapTextureTest extends ActivityInstrumentationGVRf {

    public void testConstructor(){
        Bitmap bitmap1 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posx.png");
        Bitmap bitmap2 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negx.png");
        Bitmap bitmap3 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posy.png");
        Bitmap bitmap4 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negy.png");
        Bitmap bitmap5 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posz.png");
        Bitmap bitmap6 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negz.png");

        Bitmap[] bitmapArray = {bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6};
        String names [] ={"posx.png", "negx.png", "posy.png", "negx.png", "posz.png","negz.png"};
        GVRCubemapTexture cubemapTexture=null;
        GVRCubemapTexture.setFaceNames(names);
        cubemapTexture = new  GVRCubemapTexture(TestDefaultGVRViewManager.mGVRContext,bitmapArray);
        assertNotNull(cubemapTexture);
    }


    public void testConstructor2(){
        Bitmap bitmap1 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posx.png");
        Bitmap bitmap2 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negx.png");
        Bitmap bitmap3 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posy.png");
        Bitmap bitmap4 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negy.png");
        Bitmap bitmap5 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("posz.png");
        Bitmap bitmap6 = TestDefaultGVRViewManager.mGVRContext.loadBitmap("negz.png");

        Bitmap[] bitmapArray = {bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6};
        String names [] ={"posx.png", "negx.png", "posy.png", "negx.png", "posz.png","negz.png"};
        GVRCubemapTexture cubemapTexture=null;
        GVRCubemapTexture.setFaceNames(names);
        cubemapTexture = new  GVRCubemapTexture(TestDefaultGVRViewManager.mGVRContext,bitmapArray);
        assertNotNull(cubemapTexture);
    }


    public void testsetFaceNames(){

        String names [] ={"posx.png", "negx.png", "posy.png", "negx.png", "posz.png","negz.png","last"};
        try {
            GVRCubemapTexture.setFaceNames(names);
            fail("Should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }
}
