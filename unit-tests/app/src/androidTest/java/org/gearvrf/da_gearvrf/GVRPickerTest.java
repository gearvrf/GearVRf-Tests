package org.gearvrf.da_gearvrf;

import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.viewmanager.BaseViewManager;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.BoundsValues;

import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.viewmanager.ViewerScript;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by rodolfo.ps on 5/19/2015.
 */
public class GVRPickerTest extends ActivityInstrumentationGVRf {

    public GVRPickerTest() {
        super(GVRTestActivity.class);
    }

    public void testPickScene() {

        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        GVRPicker.GVRPickedObject[] hits = GVRPicker.pickObjects(gvrScene, 0, 0, 0, 0, 0, -1);
        assertNotNull(hits);
        assertTrue(hits.length == 0);
        GVRSceneObject gvrSceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRCameraRig gvrCameraRigb = GVRCameraRig.makeInstance(TestDefaultGVRViewManager.mGVRContext);
        float rtn = GVRPicker.pickSceneObject(gvrSceneObject, gvrCameraRigb);
        assertNotNull(rtn);
    }

    public void testPickSceneMinFloat() {
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        GVRPicker.GVRPickedObject[] hits = GVRPicker.pickObjects(gvrScene,
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0),
                BoundsValues.getFloatList().get(0));
        assertNotNull(hits);
        assertTrue(hits.length == 0);
    }

    public void testPickSceneMaxFloat() {
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        BoundsValues.getFloatList().get(1);
        GVRPicker.GVRPickedObject[] hits = GVRPicker.pickObjects(gvrScene,
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1),
                BoundsValues.getFloatList().get(1));
        assertNotNull(hits);
    }

    public void testFindObjects() {
        assertNotNull(TestDefaultGVRViewManager.mGVRContext);
        GVRScene gvrScene = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        List<GVRPicker.GVRPickedObject> gvrPO1 = GVRPicker.findObjects(gvrScene);
        assertNotNull(gvrPO1);
        GVRPicker.GVRPickedObject[] gvrPO2 = GVRPicker.pickObjects(gvrScene, 1.0f,1.0f,1.0f,1.0f,1.0f,1.0f);
        assertNotNull(gvrPO2);
        // for this to be true the test must wait until the scene is rendered
        // that is when visible objects are picked
        //assertTrue(gvrPO2.length > 0);
    }

    //Added by Elidelson Carvalho on 10/01/2015

    public void testOthers() {

        GVRContext mGVRContext = TestDefaultGVRViewManager.mGVRContext;
        assertNotNull(mGVRContext);
        GVRScene scene = mGVRContext.getNextMainScene();
        DefaultGVRTestActivity activity = DefaultGVRTestActivity.getInstance();
        GVRMain main = activity.getMain();

        Future<GVRTexture> futureCubemapTexture = mGVRContext.loadFutureCubemapTexture(new GVRAndroidResource(mGVRContext, R.raw.beach));
        GVRMesh sphereMesh = mGVRContext.getAssetLoader().loadMesh(new GVRAndroidResource(mGVRContext, R.raw.sphere));
        GVRMaterial cubemapReflectionMaterial = new GVRMaterial(mGVRContext,GVRMaterial.GVRShaderType.CubemapReflection.ID);
        cubemapReflectionMaterial.setMainTexture(futureCubemapTexture);

        GVRSceneObject sphere = new GVRSceneObject(mGVRContext, sphereMesh);
        sphere.getRenderData().setMaterial(cubemapReflectionMaterial);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        sphere.getTransform().setScale(2.0f, 2.0f, 2.0f);
        sphere.getTransform().setPosition(0.0f, 0.0f, -5.0f);
        sphere.attachComponent(new GVRMeshCollider(mGVRContext, false));
        List<GVRPicker.GVRPickedObject> gvrPO1 = GVRPicker.findObjects(scene);

        assertNotNull(gvrPO1);
        // for this to be true the test must wait until the scene is rendered
        // that is when visible objects are picked
        //assertTrue(gvrPO1.size() > 0);
        if (gvrPO1.size() > 0)
        {
            assertNotNull(gvrPO1.get(0).getHitLocation());
            assertNotNull(gvrPO1.get(0).getHitX());
            assertNotNull(gvrPO1.get(0).getHitY());
            assertNotNull(gvrPO1.get(0).getHitZ());
        }
    }

}
