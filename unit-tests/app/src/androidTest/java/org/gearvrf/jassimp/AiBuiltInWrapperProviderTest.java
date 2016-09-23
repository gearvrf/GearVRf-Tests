package org.gearvrf.jassimp;

import android.support.annotation.NonNull;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * Created by j.elidelson on 9/11/2015.
 */
public class AiBuiltInWrapperProviderTest extends ActivityInstrumentationGVRf {

    public AiBuiltInWrapperProviderTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        assertNotNull(aiBuiltInWrapperProvider);
    }

    public void testwrapVector3f() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        AiVector aiVector = aiBuiltInWrapperProvider.wrapVector3f(byteBuffer, 0, 1000);
        assertEquals(1000, aiVector.getNumComponents());
        aiVector.setX(1.0f);
        assertEquals(1.0f, aiVector.getX());
        aiVector.setY(2.0f);
        assertEquals(2.0f, aiVector.getY());
        aiVector.setZ(3.0f);
        assertEquals(3.0f, aiVector.getZ());
        String ts=aiVector.toString();
        assertNotNull(ts);
        try {
            AiVector aiVector2 = aiBuiltInWrapperProvider.wrapVector3f(null, 0, 0);
            fail("should throws exception...");
        }catch (IllegalArgumentException e){}
        AiVector aiVector3 = aiBuiltInWrapperProvider.wrapVector3f(byteBuffer, 0, 0);
        try {
            aiVector3.getY();
            fail("should throws exception...");
        }catch (UnsupportedOperationException e){}
        try {
            aiVector3.getZ();
            fail("should throws exception...");
        }catch (UnsupportedOperationException e){}
        try {
            aiVector3.setY(1.0f);
            fail("should throws exception...");
        }catch (UnsupportedOperationException e){}
        try {
            aiVector3.setZ(1.0f);
            fail("should throws exception...");
        }catch (UnsupportedOperationException e){}
    }

    public void testwrapMatrix4f() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        float floatv1[] = new float[1000];
        try {
            AiMatrix4f aiMatrix4f = aiBuiltInWrapperProvider.wrapMatrix4f(floatv1);
            fail("should throws exception...");
        } catch (IllegalArgumentException e) {}
        try {
            AiMatrix4f aiMatrix4f = aiBuiltInWrapperProvider.wrapMatrix4f(null);
            fail("should throws exception...");
        } catch (IllegalArgumentException e) {}
        float floatv2[] = new float[16];
        for (int i = 0; i < 16; i++) floatv2[1] = (float) i;
        AiMatrix4f aiMatrix4f = aiBuiltInWrapperProvider.wrapMatrix4f(floatv2);
        assertEquals(0.0f, aiMatrix4f.get(1, 1));
        try {
            aiMatrix4f.get(4,1);
            fail("should throws exception...");
        } catch (IndexOutOfBoundsException e) {}
        try {
            aiMatrix4f.get(1,4);
            fail("should throws exception...");
        } catch (IndexOutOfBoundsException e) {}
        FloatBuffer floatBuffer = aiMatrix4f.toByteBuffer();
        assertNotNull(floatBuffer);
        String ts=aiMatrix4f.toString();
        assertNotNull(ts);
    }

    public void testwrapColor() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        AiColor aiColor = aiBuiltInWrapperProvider.wrapColor(byteBuffer, 0);
        assertNotNull(aiColor);
        aiColor.setAlpha(1.0f);
        assertEquals(1.0f, aiColor.getAlpha());
        aiColor.setRed(2.0f);
        assertEquals(2.0f, aiColor.getRed());
        aiColor.setGreen(3.0f);
        assertEquals(3.0f, aiColor.getGreen());
        aiColor.setBlue(4.0f);
        assertEquals(4.0f, aiColor.getBlue());
        String ts=aiColor.toString();
        assertNotNull(ts);
    }

    public void testwrapSceneNode() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        AiNode aiNode = null;
        AiNode aiNode1 = null;
        int meshreferences[] = new int[100];
        AiNode aiNode3 = aiBuiltInWrapperProvider.wrapSceneNode(aiNode, aiNode1, meshreferences, "test");
        assertNull(aiNode3.getParent());
        AiMatrix4f aiMatrix4f = aiNode3.getTransform(aiBuiltInWrapperProvider);
        assertNull(aiMatrix4f);
        AiNode aiNode4 = aiBuiltInWrapperProvider.wrapSceneNode(aiNode, aiNode1, meshreferences, "test2");;
        aiNode3.addChild(aiNode4);
        assertEquals(1, aiNode3.getNumChildren());
        AiNode aiNode5 = aiNode3.findNode("test");
        assertNotNull(aiNode5);
        try {
            AiNode aiNode6 = aiNode3.findNode("test2");
        }catch (NullPointerException e){}
        aiNode3.addChild(aiNode5);
        List<AiNode> aiNodeList = aiNode3.getChildren();
        assertEquals(2, aiNodeList.size());
        assertEquals("test", aiNode3.getName());
        assertEquals(100, aiNode3.getNumMeshes());
        assertEquals(meshreferences, aiNode3.getMeshes());
    }

    public void testwrapQuaternion() {

        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        AiQuaternion aiQuaternion = aiBuiltInWrapperProvider.wrapQuaternion(byteBuffer, 0);
        aiQuaternion.setW(1.0f);
        assertEquals(1.0f, aiQuaternion.getW());
        aiQuaternion.setX(2.0f);
        assertEquals(2.0f, aiQuaternion.getX());
        aiQuaternion.setY(3.0f);
        assertEquals(3.0f, aiQuaternion.getY());
        aiQuaternion.setZ(4.0f);
        assertEquals(4.0f,aiQuaternion.getZ());
        try {
            AiQuaternion aiQuaternion2 = aiBuiltInWrapperProvider.wrapQuaternion(null, 0);
        }catch (IllegalArgumentException e){}
        String ts = aiQuaternion.toString();
        assertNotNull(ts);
    }
}