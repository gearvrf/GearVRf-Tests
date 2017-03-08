package org.gearvrf.asynchronous;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;

/**
 * Created by j.elidelson on 8/24/2015.
 */
public class GLESXTest extends ActivityInstrumentationGVRf {

    public GLESXTest() {
        super(GVRTestActivity.class);
    }

    public  void testGLESX(){

        assertEquals(0x93B0,GLESX.GL_COMPRESSED_RGBA_ASTC_4x4_KHR);
    }

}
