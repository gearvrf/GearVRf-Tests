package org.gearvrf.da_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRLight;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

/**
 * Created by j.elidelson on 9/1/2015.
 */
public class GVRLightTest extends ActivityInstrumentationGVRf {

    public void testConstructor(){
        GVRLight gvrLight = new GVRLight(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(gvrLight);
    }

    public void testSetGetPosition(){
        GVRLight gvrLight = new GVRLight(TestDefaultGVRViewManager.mGVRContext);
        gvrLight.setPosition(1.0f,1.0f,1.0f);
        float pos[] = gvrLight.getPosition();
        assertEquals(3,pos.length);
        assertNotNull(gvrLight);
    }

    public void testSetDisableEnable(){
        GVRLight gvrLight = new GVRLight(TestDefaultGVRViewManager.mGVRContext);
        gvrLight.disable();
        assertEquals(false, gvrLight.isEnabled());
        gvrLight.enable();
        assertEquals(true, gvrLight.isEnabled());
    }

    public void testIntensity(){
        GVRLight gvrLight = new GVRLight(TestDefaultGVRViewManager.mGVRContext);
        float aux1[] = gvrLight.getAmbientIntensity();
        assertEquals(4, aux1.length);
        float aux2[] = gvrLight.getDiffuseIntensity();
        assertEquals(4,aux2.length);
        float aux3[] = gvrLight.getSpecularIntensity();
        assertEquals(4,aux3.length);
    }

}

