package org.gearvrf.za_gearvrf;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRBitmapTexture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by santhyago on 2/24/15.
 */
public class GVRBitmapTextureTest extends ActivityInstrumentationGVRf {
    GVRBitmapTexture bitmapTexture;

    private DefaultGVRTestActivity defaultGVRTestActivity;
    public GVRBitmapTextureTest() {
        super(DefaultGVRTestActivity.class);
    }

    public void testConstructor() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("coke.jpg");
        } catch (FileNotFoundException e) {
            fail("File not found.");
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext, bitmap);
        } catch (NullPointerException e) {
        }
        assertNotNull(bitmapTexture);
    }

     /**
     * Tests constructor with a null GVRContext
     */
    public void testConstructorNullContext() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("coke.jpg");
        } catch (FileNotFoundException e) {
            fail("File not found.");
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        try {
            bitmapTexture = new GVRBitmapTexture(null, bitmap);
            fail("Constructor allows null GVRContext object.");
        } catch (NullPointerException e) {
        }
    }

    public void testConstructorBigBitmap() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("big_texture.jpg");
        } catch (FileNotFoundException e) {
            fail("File not found.");
        } catch (IOException e) {
            fail(e.getMessage());
        }
        try{
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext, bitmap);
            //fail("Should throws error.");
        } catch (OutOfMemoryError e) {
        }
    }

    public void testConstructorGVRTextureParameters() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("coke.jpg");
        } catch (FileNotFoundException e) {
            fail("File not found.");
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext, bitmap,gvrTextureParameters);
        } catch (NullPointerException e) {}
        assertNotNull(bitmapTexture);
    }

    public void ignoretestConstructorpngAssetFilename() {
        String pngAssetFilename = "frigate.png";
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,pngAssetFilename);
        } catch (NullPointerException e) {
        }
        assertNotNull(bitmapTexture);
    }


    public void testConstructorGVRTextureParametersAssetFilename() {
        String pngAssetFilename = "frigate.png";
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext, pngAssetFilename,gvrTextureParameters);
        } catch (NullPointerException e) { }
        assertNotNull(bitmapTexture);
    }


    public void testConstructorGrayScaleData() {
        byte grayScaleData[]={100,100,100,100};
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,0,4,grayScaleData);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,4,0,grayScaleData);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,2,2,null);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,4,4,grayScaleData);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,2,2,grayScaleData);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}

        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,2,2,grayScaleData);
            bitmapTexture.update(2,2,grayScaleData);
            ;
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
    }


    public void testConstructorGrayScaleDataGVRTextureParameters() {
        byte grayScaleData[]={100,100,100,100};
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,0,4,grayScaleData,gvrTextureParameters);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,4,0,grayScaleData,gvrTextureParameters);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,2,2,null,gvrTextureParameters);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,4,4,grayScaleData,gvrTextureParameters);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}
        try {
            bitmapTexture = new GVRBitmapTexture(TestDefaultGVRViewManager.mGVRContext,2,2,grayScaleData,gvrTextureParameters);
            //fail("Should throws IllegalArgumentException.");
        }catch (IllegalArgumentException e){}

    }


    public void ignoretestConstructorGVRTextureParameters2() { //by Elidelson Carvalho on 10/31/2015
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);

        gvrTextureParameters.setMinFilterType(GVRTextureParameters.TextureFilterType.GL_LINEAR);
        assertEquals(GVRTextureParameters.TextureFilterType.GL_LINEAR,gvrTextureParameters.getMinFilterType());

        gvrTextureParameters.setMagFilterType(GVRTextureParameters.TextureFilterType.GL_LINEAR);
        assertEquals(GVRTextureParameters.TextureFilterType.GL_LINEAR, gvrTextureParameters.getMagFilterType());

        gvrTextureParameters.setWrapSType(GVRTextureParameters.TextureWrapType.GL_CLAMP_TO_EDGE);
        assertEquals(GVRTextureParameters.TextureWrapType.GL_CLAMP_TO_EDGE, gvrTextureParameters.getWrapSType());

        gvrTextureParameters.setWrapTType(GVRTextureParameters.TextureWrapType.GL_CLAMP_TO_EDGE);
        assertEquals(GVRTextureParameters.TextureWrapType.GL_CLAMP_TO_EDGE,gvrTextureParameters.getWrapTType());

        assertEquals(false,gvrTextureParameters.isAnisotropicSupported());

        //gvrTextureParameters.setAnisotropicValue(16);
        //assertEquals(16,gvrTextureParameters.getAnisotropicValue());

        gvrTextureParameters.getMaxAnisotropicValue();

        assertNotNull(gvrTextureParameters.getDefalutValuesArray());
    }

}
