package org.gearvrf.asynchronous;

import android.content.res.AssetManager;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by j.elidelson on 8/24/2015.
 */
public class GVRCompressedTextureTest extends ActivityInstrumentationGVRf {

    public GVRCompressedTextureTest() {
        super(GVRTestActivity.class);
    }

    public void testGVRCompressedTextureConstructor(){

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

        GVRCompressedTexture gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,1,16,16,144,data,1,1,GVRCompressedTexture.DEFAULT_QUALITY);
        assertNotNull(gvrCompressedTexture);
    }

    public void testGVRCompressedTextureConstructor2(){

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

        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        GVRCompressedTexture gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,1,16,16,144,data,1,1,GVRCompressedTexture.DEFAULT_QUALITY,gvrTextureParameters);
        assertNotNull(gvrCompressedTexture);
    }


    public void testGVRCompressedTextureConstructor3(){

        GVRCompressedTexture gvrCompressedTexture = new GVRCompressedTexture(TestDefaultGVRViewManager.mGVRContext,GVRCompressedTexture.GL_TARGET,1,GVRCompressedTexture.DEFAULT_QUALITY);
        assertNotNull(gvrCompressedTexture);
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
