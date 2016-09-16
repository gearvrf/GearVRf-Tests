package org.gearvrf.da_gearvrf;

import org.gearvrf.GVRRenderTexture;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

/**
 * Created by santhyago on 2/27/15.
 */
public class GVRRenderTextureTest extends ActivityInstrumentationGVRf {

    public GVRRenderTextureTest() {
        super(GVRTestActivity.class);
    }

    public void testGVRRenderTextureCtx3Int() {
        GVRRenderTexture renderTex = new GVRRenderTexture(TestDefaultGVRViewManager.mGVRContext, 1, 2, 3);
        //assertEquals(0,renderTex.getUseCount()); // DEPRECATED
        assertEquals(2, renderTex.getHeight());
        assertEquals(1,renderTex.getWidth());
        GVRRenderTexture renderTex2 = new GVRRenderTexture(TestDefaultGVRViewManager.mGVRContext, 1, 2);
        assertNotNull(renderTex2);
    }
}
