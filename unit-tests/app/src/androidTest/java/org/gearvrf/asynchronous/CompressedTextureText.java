package org.gearvrf.asynchronous;

import android.content.res.AssetManager;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by j.elidelson on 8/21/2015.
 */
public class CompressedTextureText extends ActivityInstrumentationGVRf {

    public void testCompressedTexture() {
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();

        InputStream is = null;
        try {
            is = assets.open("tiny_texture.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[0];
        try {
            data = streamToByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer data2 = null;
        data2 = ByteBuffer.wrap(data);
        CompressedTexture compressedTexture = new CompressedTexture(1, 1024, 1024, 1024, 1, data2);
        assertNotNull(compressedTexture);
        GVRCompressedTexture compressedTexture1;
        compressedTexture1=compressedTexture.toTexture(TestDefaultGVRViewManager.mGVRContext, GVRContext.DEFAULT_PRIORITY);
        assertNotNull(compressedTexture1);
        GVRCompressedTexture compressedTexture2;
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);
        compressedTexture2=compressedTexture.toTexture(TestDefaultGVRViewManager.mGVRContext,GVRContext.DEFAULT_PRIORITY,gvrTextureParameters);
        assertNotNull(compressedTexture2);
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
