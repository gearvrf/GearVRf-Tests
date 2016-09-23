package org.gearvrf.asynchronous;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRHybridObject;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.tests.R;
import org.gearvrf.utility.MarkingFileInputStream;
import org.gearvrf.utility.ResourceCache;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by j.elidelson on 8/19/2015.
 */
public class GVRAsynchronousResourceLoaderTest extends ActivityInstrumentationGVRf {

    public GVRAsynchronousResourceLoaderTest() {
        super(GVRTestActivity.class);
    }

   public void testGVRAsynchronousResourceLoader(){
        GVRAsynchronousResourceLoader gvrAsynchronousResourceLoader = new GVRAsynchronousResourceLoader();
        assertNotNull(gvrAsynchronousResourceLoader);
   }



    public void testGVRCompressedTexture() {

        GVRCompressedTexture gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,1,1,1);
        assertNotNull(gvrCompressedTexture);


        AssetManager assets = getInstrumentation().getTargetContext().getAssets();

        InputStream is = null;
        try {
            is = assets.open("tiny_texture.astc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[0];
        try {
            data = streamToByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,1,2,3,6,data,1,1,1);
        assertNotNull(gvrCompressedTexture);

        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,1,2,3,6,data,1,1,1,gvrTextureParameters);
        assertNotNull(gvrCompressedTexture);
    }

    public void testGVRAsynchronousResourceLoaderloadCompressedTexture(){

        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.coke);
        ResourceCache<GVRTexture> textureCache = new ResourceCache<GVRTexture>();
        GVRAndroidResource.CompressedTextureCallback compressedTextureCallback = new GVRAndroidResource.CompressedTextureCallback() {
            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

        GVRAsynchronousResourceLoader.loadCompressedTexture(TestDefaultGVRViewManager.mGVRContext,textureCache,compressedTextureCallback,gvrAndroidResource);
        GVRAsynchronousResourceLoader.loadCompressedTexture(TestDefaultGVRViewManager.mGVRContext,textureCache,compressedTextureCallback,gvrAndroidResource,GVRCompressedTexture.BALANCED);
    }

    public void testGVRAsynchronousResourceLoaderloadBitmapTexture(){

        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        ResourceCache<GVRTexture> textureCache = new ResourceCache<GVRTexture>();
        GVRAndroidResource.BitmapTextureCallback bitmapTextureCallback = new GVRAndroidResource.BitmapTextureCallback() {
            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }

            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }
        };
        GVRAsynchronousResourceLoader.loadBitmapTexture(TestDefaultGVRViewManager.mGVRContext,textureCache,bitmapTextureCallback,gvrAndroidResource,GVRCompressedTexture.BALANCED);
        try {//cannot load the same GVRAndroidResource more than once
            GVRAsynchronousResourceLoader.loadBitmapTexture(TestDefaultGVRViewManager.mGVRContext, textureCache, bitmapTextureCallback, gvrAndroidResource, -1);
        }catch (IllegalArgumentException e){}
        GVRAndroidResource gvrAndroidResource1 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        try {//GVRAndroidResource cannot be loaded twice
            GVRAsynchronousResourceLoader.loadBitmapTexture(TestDefaultGVRViewManager.mGVRContext, textureCache, bitmapTextureCallback, gvrAndroidResource1, GVRContext.LOWEST_PRIORITY - 1);
        }catch (IllegalArgumentException e){}
        try {//priority must be >= GVRContext.LOWEST_PRIORITY and <= GVRContext.HIGHEST_PRIORITY
            GVRAsynchronousResourceLoader.loadBitmapTexture(TestDefaultGVRViewManager.mGVRContext, textureCache, bitmapTextureCallback, gvrAndroidResource1, GVRContext.HIGHEST_PRIORITY+1);
        }catch (IllegalArgumentException e){}

    }

    public void testGVRAsynchronousResourceLoaderloadFutureCubemapTexture(){

        Map<String, Integer> m = new HashMap<String, Integer>();
        Integer key1   = new Integer(123);
        String  value1 = "value 1";
        m.put(value1,key1);
        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        ResourceCache<GVRTexture> textureCache = new ResourceCache<GVRTexture>();
        GVRAsynchronousResourceLoader.loadFutureCubemapTexture(TestDefaultGVRViewManager.mGVRContext,textureCache,gvrAndroidResource,GVRCompressedTexture.BALANCED,m);
    }

    public void testGVRAsynchronousResourceLoaderloadFutureMesh(){

        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        GVRAsynchronousResourceLoader.loadFutureMesh(TestDefaultGVRViewManager.mGVRContext, gvrAndroidResource, GVRCompressedTexture.BALANCED);
    }

    public void ignoretestGVRAsynchronousResourceLoaderloadMesh(){

        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.drawable.cylinder2);
        GVRAndroidResource.MeshCallback meshCallback = new GVRAndroidResource.MeshCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRMesh resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };
        GVRAsynchronousResourceLoader.loadMesh(TestDefaultGVRViewManager.mGVRContext, meshCallback, gvrAndroidResource, GVRCompressedTexture.BALANCED);
    }

    public void ignoretestGVRAsynchronousResourceLoader2(){

        GVRAsynchronousResourceLoader gvrAsynchronousResourceLoader = new GVRAsynchronousResourceLoader();

        AssetManager assets = getInstrumentation().getTargetContext().getAssets();

        InputStream is = null;
        try {
            is = assets.open("sample_20140509_l.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MarkingFileInputStream markingFileInputStreamTest=null;
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            markingFileInputStreamTest = new MarkingFileInputStream(resourcePath);
        }catch (FileNotFoundException e){}

        Bitmap bitmap = GVRAsynchronousResourceLoader.decodeStream(is, true);
        assertNotNull(bitmap);
        Bitmap bitmap1 = GVRAsynchronousResourceLoader.decodeStream(markingFileInputStreamTest, true);
        assertNotNull(bitmap1);
    }


    public void testLoadTexture(){
        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        ResourceCache<GVRTexture> textureCache = new ResourceCache<GVRTexture>();
        GVRAndroidResource.CancelableCallback cancelableCallback = new GVRAndroidResource.CancelableCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRHybridObject resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

        GVRAsynchronousResourceLoader.loadTexture(TestDefaultGVRViewManager.mGVRContext, textureCache, cancelableCallback, gvrAndroidResource, GVRContext.HIGHEST_PRIORITY, 1);

    }

    public void testLoadFutureTexture(){
        GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.sample_20140509_r);
        ResourceCache<GVRTexture> textureCache = new ResourceCache<GVRTexture>();
        GVRAndroidResource.CancelableCallback cancelableCallback = new GVRAndroidResource.CancelableCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRHybridObject resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

        GVRAsynchronousResourceLoader.loadFutureTexture(TestDefaultGVRViewManager.mGVRContext, textureCache, gvrAndroidResource, GVRContext.HIGHEST_PRIORITY, 1);

    }


    public static byte[] streamToByteArray(InputStream stream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }
}
