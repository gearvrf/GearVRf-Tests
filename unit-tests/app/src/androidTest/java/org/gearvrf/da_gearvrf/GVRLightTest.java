package org.gearvrf.da_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRLight;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

/**
 * Created by j.elidelson on 9/1/2015.
 */
public class GVRLightTest extends ActivityInstrumentationGVRf {

    public GVRLightTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor(){
        assertNotNull(TestDefaultGVRViewManager.mGVRContext);
        GVRDirectLight gvrLight = new GVRDirectLight(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(gvrLight);
    }

    public void testSetGetPosition(){
        GVRPointLight gvrLight = new GVRPointLight(TestDefaultGVRViewManager.mGVRContext);
        gvrLight.setPosition(1.0f,1.0f,1.0f);
        float pos[] = gvrLight.getPosition();
        assertEquals(3,pos.length);
        assertNotNull(gvrLight);
    }

    public void testSetDisableEnable(){
        GVRDirectLight gvrLight = new GVRDirectLight(TestDefaultGVRViewManager.mGVRContext);
        gvrLight.disable();
        assertEquals(false, gvrLight.isEnabled());
        gvrLight.enable();
        assertEquals(true, gvrLight.isEnabled());
    }

    /*
    //TODO native crash due to point light not having intensity
    public void testIntensity(){
        GVRPointLight gvrLight = new GVRPointLight(TestDefaultGVRViewManager.mGVRContext);
        float aux1[] = gvrLight.getAmbientIntensity();
        assertEquals(4, aux1.length);
        float aux2[] = gvrLight.getDiffuseIntensity();
        assertEquals(4,aux2.length);
        float aux3[] = gvrLight.getSpecularIntensity();
        assertEquals(4,aux3.length);
    }
    */

}

