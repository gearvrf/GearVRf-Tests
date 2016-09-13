package org.gearvrf.scene_object;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import java.util.concurrent.Future;

/**
 * Created by j.elidelson on 5/29/2015.
 */
public class GVRSphereSceneObjectTest extends ActivityInstrumentationGVRf {

    //**************
    //*** Sphere ***
    //**************
    public void testConeConstructor() {

        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(sphereSceneObject);
    }

    public void testConeConstructorFacingout() {

        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,true);
        assertNotNull(sphereSceneObject);
        sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,false);
        assertNotNull(sphereSceneObject);
    }

    public void testConeConstructorFutureTexture() {
        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.drawable.bottom));
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,true,futureTexture);
        assertNotNull(sphereSceneObject);
        sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,false,futureTexture);
        assertNotNull(sphereSceneObject);
    }

    public void testConeConstructorMaterial() {
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,true,material);
        assertNotNull(sphereSceneObject);
        sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,false,material);
        assertNotNull(sphereSceneObject);
    }

    public void testConeConstructorMaterial2() {
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,3,4,true,material);
        assertNotNull(sphereSceneObject);
        sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,3,4,false,material);
        assertNotNull(sphereSceneObject);
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,2,4,false,material);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,3,3,false,material);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testConeConstructorMaterial3() {
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,5,8,true,material,3,4);
        assertNotNull(sphereSceneObject);
        sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,5,8,false,material,3,4);
        assertNotNull(sphereSceneObject);
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,2,4,false,material,3,4);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,3,3,false,material,3,4);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,4,4,false,material,3,4);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext,5,4,false,material,3,3);
            fail("should throw IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

}