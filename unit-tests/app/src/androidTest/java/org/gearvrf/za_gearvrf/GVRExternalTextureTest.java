package org.gearvrf.za_gearvrf;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRExternalTexture;

/**
 * Created by j.elidelson on 5/20/2015.
 */
public class GVRExternalTextureTest extends ActivityInstrumentationGVRf {

    public void testExternalTextureConstructor () {
        try {
            GVRExternalTexture gvrExternalTexture;
            gvrExternalTexture = new GVRExternalTexture(TestDefaultGVRViewManager.mGVRContext);
            assertNotNull("Resource was null: ", gvrExternalTexture);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void testExternalTextureNullConstructor() {
        try {
            GVRExternalTexture gvrExternalTexture;
            gvrExternalTexture = new GVRExternalTexture(null);
            //assertNull("Constructor parameter was null: ", gvrExternalTexture);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //Methods are tested in GVRTexture and GVRHybridObject
}
