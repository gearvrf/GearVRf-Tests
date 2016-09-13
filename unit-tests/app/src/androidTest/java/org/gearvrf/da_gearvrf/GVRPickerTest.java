package org.gearvrf.da_gearvrf;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.BoundsValues;

import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by rodolfo.ps on 5/19/2015.
 */
public class GVRPickerTest extends ActivityInstrumentationGVRf {

    public void testPickScene() {

        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        GVREyePointeeHolder[] gvrEPH = GVRPicker.pickScene(gvrScene);
        assertNotNull(gvrEPH);

        GVRSceneObject gvrSceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRCameraRig gvrCameraRigb = GVRCameraRig.makeInstance(TestDefaultGVRViewManager.mGVRContext);
        float rtn = GVRPicker.pickSceneObject(gvrSceneObject,gvrCameraRigb);
        assertNotNull(rtn);
    }

    public void testPickSceneMinFloat() {
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        GVREyePointeeHolder[] gvrEPH = GVRPicker.pickScene(gvrScene,
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0));
        assertNotNull(gvrEPH);
    }

    public void testPickSceneMaxFloat() {
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        BoundsValues.getFloatList().get(1);
        GVREyePointeeHolder[] gvrEPH = GVRPicker.pickScene(gvrScene,
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1));
        assertNotNull(gvrEPH);
    }

    public void testFindObjects() {
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        List<GVRPicker.GVRPickedObject> gvrPO1 = GVRPicker.findObjects(gvrScene);
        assertNotNull(gvrPO1);
        List<GVRPicker.GVRPickedObject> gvrPO2 = GVRPicker.findObjects(gvrScene,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f);
        assertNotNull(gvrPO2);
    }

    //Added by Elidelson Carvalho on 10/01/2015

    public void testOthers() {

        GVRContext mGVRContext=TestDefaultGVRViewManager.mGVRContext;
        GVRScene scene = mGVRContext.getNextMainScene();

        Future<GVRTexture> futureCubemapTexture = mGVRContext.loadFutureCubemapTexture(new GVRAndroidResource(mGVRContext, R.raw.beach));
        GVRMesh sphereMesh = mGVRContext.loadMesh(new GVRAndroidResource(mGVRContext, R.raw.sphere));
        GVRMaterial cubemapReflectionMaterial = new GVRMaterial(mGVRContext,GVRMaterial.GVRShaderType.CubemapReflection.ID);
        cubemapReflectionMaterial.setMainTexture(futureCubemapTexture);

        GVRSceneObject sphere = new GVRSceneObject(mGVRContext, sphereMesh);
        sphere.getRenderData().setMaterial(cubemapReflectionMaterial);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        sphere.getTransform().setScale(2.0f, 2.0f,2.0f);
        sphere.getTransform().setPosition(0.0f, 0.0f, -5.0f);
        attachDefaultEyePointee(sphere);
        List<GVRPicker.GVRPickedObject> gvrPO11 = GVRPicker.findObjects(scene,0.0f,0.0f,0.0f,0.0f,0.0f,-1f);
        List<GVRPicker.GVRPickedObject> gvrPO1 = GVRPicker.findObjects(scene);
        assertNotNull(gvrPO1.get(0).getHitLocation());
        assertNotNull(gvrPO1.get(0).getHitX());
        assertNotNull(gvrPO1.get(0).getHitY());
        assertNotNull(gvrPO1.get(0).getHitZ());
    }

    private void attachDefaultEyePointee(GVRSceneObject sceneObject) {
        GVREyePointeeHolder eyePointeeHolder = new GVREyePointeeHolder(
                TestDefaultGVRViewManager.mGVRContext);
        GVRMeshEyePointee eyePointee = new GVRMeshEyePointee(TestDefaultGVRViewManager.mGVRContext,
                sceneObject.getRenderData().getMesh());
        eyePointeeHolder.addPointee(eyePointee);
        sceneObject.attachEyePointeeHolder(eyePointeeHolder);
    }

}
