package org.gearvrf.asynchronous;

import android.content.Intent;
import android.content.res.AssetManager;
import android.provider.MediaStore;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.utility.RuntimeAssertion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by j.elidelson on 8/21/2015.
 */
public class AdaptiveScalableTextureCompressionTest extends ActivityInstrumentationGVRf {

    public void testeheaderLength() {

        AdaptiveScalableTextureCompression adaptiveScalableTextureCompression = new AdaptiveScalableTextureCompression();
        assertEquals(16,adaptiveScalableTextureCompression.headerLength());
    }

    public void testSniff() {
        AdaptiveScalableTextureCompression adaptiveScalableTextureCompression = new AdaptiveScalableTextureCompression();

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
        GVRCompressedTextureLoader.Reader reader = new GVRCompressedTextureLoader.Reader(data);
        assertEquals(true,adaptiveScalableTextureCompression.sniff(data,reader));
    }

    public void testSniff2() {
        AdaptiveScalableTextureCompression adaptiveScalableTextureCompression = new AdaptiveScalableTextureCompression();

        AssetManager assets = getInstrumentation().getTargetContext().getAssets();

        InputStream is = null;
        try {
            is = assets.open("tiny_texture.pkm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[0];
        try {
            data = streamToByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GVRCompressedTextureLoader.Reader reader = new GVRCompressedTextureLoader.Reader(data);
        assertEquals(false,adaptiveScalableTextureCompression.sniff(data,reader));

        reader.getPosition();
        reader.setPosition(0);
        reader.readLong(2);
    }


    public void testParser() {
        AdaptiveScalableTextureCompression adaptiveScalableTextureCompression = new AdaptiveScalableTextureCompression();
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
        GVRCompressedTextureLoader.Reader reader = new GVRCompressedTextureLoader.Reader(data);
        CompressedTexture compressedTexture =adaptiveScalableTextureCompression.parse(data, reader);
        assertNotNull(compressedTexture);
    }


    public void testParser2() {
        AdaptiveScalableTextureCompression adaptiveScalableTextureCompression = new AdaptiveScalableTextureCompression();
        AssetManager assets = getInstrumentation().getTargetContext().getAssets();
        InputStream is = null;
        try {
            is = assets.open("tiny_texture.pkm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[0];
        try {
            data = streamToByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GVRCompressedTextureLoader.Reader reader = new GVRCompressedTextureLoader.Reader(data);
        try {
            CompressedTexture compressedTexture = adaptiveScalableTextureCompression.parse(data, reader);
            fail("should throws RuntimeAssertion: wrong file format");
        }catch (ArrayIndexOutOfBoundsException e){}
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
