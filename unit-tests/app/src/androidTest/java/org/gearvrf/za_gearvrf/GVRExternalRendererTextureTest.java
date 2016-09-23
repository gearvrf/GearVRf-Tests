package org.gearvrf.za_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRExternalRendererTexture;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

/**
 * Created by j.elidelson on 8/31/2015.
 */
public class GVRExternalRendererTextureTest extends ActivityInstrumentationGVRf {

    public void testConstructor(){
        GVRExternalRendererTexture gvrExternalRendererTexture = new GVRExternalRendererTexture(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(gvrExternalRendererTexture);
    }

    public void testsetgetData(){
        GVRExternalRendererTexture gvrExternalRendererTexture = new GVRExternalRendererTexture(TestDefaultGVRViewManager.mGVRContext);
        gvrExternalRendererTexture.setData(1000l);
        assertEquals(1000l,gvrExternalRendererTexture.getData());
    }
}
