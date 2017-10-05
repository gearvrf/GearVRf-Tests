package org.gearvrf.scene_object;

import android.hardware.Camera;

import org.gearvrf.GVRMesh;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.tests.R;
import org.gearvrf.utils.UtilResource;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.scene_objects.GVRCameraSceneObject;
import org.gearvrf.ActivityInstrumentationGVRf;

/**
 * Created by m.gorll on 2/27/2015.
 */
public class GVRCameraSceneObjectTest extends ActivityInstrumentationGVRf {

    public GVRCameraSceneObjectTest() {
        super(GVRTestActivity.class);
    }

    public void testCreateCameraSceneObject() {
        Camera c = Camera.open();
        try {
            GVRMesh mesh = TestDefaultGVRViewManager.mGVRContext.getAssetLoader().loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
            GVRCameraSceneObject object = new GVRCameraSceneObject(TestDefaultGVRViewManager.mGVRContext, mesh, c);
            TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(object);
            assertNotNull(object);
            object.pause();
            object.resume();
        } finally {
            c.release();
        }
    }

    public void testCreateCameraSceneObject2() {
        Camera c = Camera.open();
        try {
            GVRCameraSceneObject object = new GVRCameraSceneObject(TestDefaultGVRViewManager.mGVRContext, 10f, 10f, c);
            TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(object);
            assertNotNull(object);

            object.pause();
            object.resume();
        } finally {
            c.release();
        }
    }

    //método onDrawFrame(float drawTime) deve ser chamado pelo framework, embora seja publico, por isso não foi testado.

}
