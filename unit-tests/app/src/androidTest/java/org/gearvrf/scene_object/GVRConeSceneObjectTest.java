package org.gearvrf.scene_object;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.scene_objects.GVRConeSceneObject;

/**
 * Created by j.elidelson on 5/29/2015.
 */
public class GVRConeSceneObjectTest extends ActivityInstrumentationGVRf {

    public GVRConeSceneObjectTest() {
        super(GVRTestActivity.class);
    }

    //************
    //*** Cone ***
    //************
    public void testConeConstructor() {

        GVRConeSceneObject coneSceneObject = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(coneSceneObject);
    }

    public void ignoretestConeNullConstructor() {

        try {
            GVRConeSceneObject coneSceneObject2 = new GVRConeSceneObject(null);
            assertNull(coneSceneObject2);
        } catch (NullPointerException e) {
            assertEquals(null, null);
        }
    }

    public void testConeConstructorFacingout() {

        GVRConeSceneObject coneSceneObjectTrue = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext, true);
        assertNotNull(coneSceneObjectTrue);
        GVRConeSceneObject coneSceneObjectFalse = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext, false);
        assertNotNull(coneSceneObjectFalse);
    }

    public void testConeConstructorFacingoutMaterial() {

        GVRConeSceneObject coneSceneObject = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRConeSceneObject coneSceneObjectTrue = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext, true, coneSceneObject.getRenderData().getMaterial());
        assertNotNull(coneSceneObjectTrue);
        GVRConeSceneObject coneSceneObjectFalse = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, coneSceneObject.getRenderData().getMaterial());
        assertNotNull(coneSceneObjectFalse);
    }

    public void testIscollinding() {

        GVRConeSceneObject coneSceneObject1 = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        coneSceneObject1.getTransform().setPosition(0.0f,0.0f,-5.0f);
        GVRConeSceneObject coneSceneObject2 = new GVRConeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        coneSceneObject2.getTransform().setPosition(0.0f,0.0f,-5.0f);
        assertEquals(true,coneSceneObject1.isColliding(coneSceneObject2));
    }

}