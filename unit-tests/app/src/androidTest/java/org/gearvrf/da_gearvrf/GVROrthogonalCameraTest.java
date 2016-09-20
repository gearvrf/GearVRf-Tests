package org.gearvrf.da_gearvrf;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVROrthogonalCamera;

/**
 * Created by j.elidelson on 6/10/2015.
 */
public class GVROrthogonalCameraTest extends ActivityInstrumentationGVRf {

    public GVROrthogonalCameraTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor(){
        assertNotNull(TestDefaultGVRViewManager.mGVRContext);
        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(gvrOrthogonalCamera);
    }

    public void testSetGetLeftClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setLeftClippingDistance(-1.0f);
        assertEquals(-1.0f,gvrOrthogonalCamera.getLeftClippingDistance());
    }

    public void testSetGetRighttClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setRightClippingDistance(1.0f);
        assertEquals(1.0f,gvrOrthogonalCamera.getRightClippingDistance());
    }

    public void testSetGetBottomClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setBottomClippingDistance(1.0f);
        assertEquals(1.0f,gvrOrthogonalCamera.getBottomClippingDistance());
    }

    public void testSetGetTopClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setTopClippingDistance(1.0f);
        assertEquals(1.0f,gvrOrthogonalCamera.getTopClippingDistance());
    }

    public void testSetGetNearClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setNearClippingDistance(1.0f);
        assertEquals(1.0f,gvrOrthogonalCamera.getNearClippingDistance());
    }

    public void testSetGetFarClippingDistance(){

        GVROrthogonalCamera gvrOrthogonalCamera = new GVROrthogonalCamera(TestDefaultGVRViewManager.mGVRContext);
        gvrOrthogonalCamera.setFarClippingDistance(1.0f);
        assertEquals(1.0f,gvrOrthogonalCamera.getFarClippingDistance());
    }
}
