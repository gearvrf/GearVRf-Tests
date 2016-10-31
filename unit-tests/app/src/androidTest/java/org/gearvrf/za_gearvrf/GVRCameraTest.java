package org.gearvrf.za_gearvrf;

import android.graphics.Color;
import android.util.Log;

import org.gearvrf.CustomPostEffectShaderManager;
import org.gearvrf.GVRCamera;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRPostEffect;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTransform;
import org.gearvrf.animation.GVRAnimationTest;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

/**
 * @author Marcos Gorll
 */
public class GVRCameraTest extends ActivityInstrumentationGVRf {


    private final String TAG = GVRCameraTest.class.getSimpleName();

    private GVRCamera gvrCameraR;
    private GVRCamera gvrCameraL;

    private final float rl = 2.0f;
    private final float gl = 3.0f;
    private final float bl = 4.0f;
    private final float al = 5.0f;

    private final float rr = 2.1f;
    private final float gr = 3.1f;
    private final float br = 4.1f;
    private final float ar = 5.1f;


    /**
     * GVRCamera - Test if getOwnerObject is a GVRSceneObject object
     */
    public void testGetOwnerObject() {
        Log.d(TAG, "iniciando testgetOwnerObject");
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();

        GVRCameraRig gvrCamera2 = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig();
        //assertEquals(GVRSceneObject.class.getName(), gvrCamera.getOwnerObject().getClass().getName());
    }

    /**
     * GVRCamera - Test if getRenderMask is a number
     */
    public void testGetRenderMask() {
        Log.d(TAG, "iniciando testgetRenderMask");

        init();
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        assertFalse(Float.isNaN(gvrCamera.getRenderMask()));
    }

    /**
     * GVRCamera - Test if setRenderMask is a number
     */
    public void testSetRenderMask() {
        Log.d(TAG, "iniciando testsetRenderMask");

        init();
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        int renderMask = gvrCamera.getRenderMask();
        gvrCamera.setRenderMask(GVRRenderData.GVRRenderMaskBit.Left);
        try {
            assertEquals(GVRRenderData.GVRRenderMaskBit.Left, gvrCamera.getRenderMask());
        } finally {
            gvrCamera.setRenderMask(renderMask);
        }
    }

    /**
     * GVRCamera - Test addPostEffect
     */
    public void testAddPostEffect() throws InterruptedException {
        Log.d(TAG, "testaddPostEffect");

        final CustomPostEffectShaderManager shaderManager
                = GVRAnimationTest.makeCustomPostEffectShaderManager(TestDefaultGVRViewManager.mGVRContext);
        GVRPostEffect postEffect = new GVRPostEffect(TestDefaultGVRViewManager.mGVRContext, shaderManager.getShaderId());
        postEffect.setVec3("ratio_r", 0.393f, 0.769f, 0.189f);
        postEffect.setVec3("ratio_g", 0.349f, 0.686f, 0.168f);
        postEffect.setVec3("ratio_b", 0.272f, 0.534f, 0.131f);

        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera().addPostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera().addPostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera().removePostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera().removePostEffect(postEffect);
    }

    /**
     * GVRCamera - Test removePostEffect
     */
    public void testRemovePostEffectt() throws InterruptedException {
        Log.d(TAG, "testremovePostEffectt");

        final CustomPostEffectShaderManager shaderManager
                = GVRAnimationTest.makeCustomPostEffectShaderManager(TestDefaultGVRViewManager.mGVRContext);
        GVRPostEffect postEffect = new GVRPostEffect(TestDefaultGVRViewManager.mGVRContext, shaderManager.getShaderId());
        postEffect.setVec3("ratio_r", 0.393f, 0.769f, 0.189f);
        postEffect.setVec3("ratio_g", 0.349f, 0.686f, 0.168f);
        postEffect.setVec3("ratio_b", 0.272f, 0.534f, 0.131f);

        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera().addPostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera().addPostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera().removePostEffect(postEffect);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera().removePostEffect(postEffect);
    }

    /**
     * GVRCamera - Test setBackgroundColor
     */
    public void testSetBackgroundColor() {
        Log.d(TAG, "testsetBackgroundColor");

        init();

        GVRCamera gvrCameraL = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        GVRCamera gvrCameraR = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera();

        gvrCameraL.setBackgroundColor(1.0f, 1.0f, 1.0f, 1.0f);
        gvrCameraR.setBackgroundColor(1.0f, 1.0f, 1.0f, 1.0f);

        assertEquals(1.0f, gvrCameraL.getBackgroundColorR(), 0.01f);
        assertEquals(1.0f, gvrCameraL.getBackgroundColorG(), 0.01f);
        assertEquals(1.0f, gvrCameraL.getBackgroundColorB(), 0.01f);
        assertEquals(1.0f, gvrCameraL.getBackgroundColorA(), 0.01f);

        assertEquals(1.0f, gvrCameraR.getBackgroundColorR(), 0.01f);
        assertEquals(1.0f, gvrCameraR.getBackgroundColorG(), 0.01f);
        assertEquals(1.0f, gvrCameraR.getBackgroundColorB(), 0.01f);
        assertEquals(1.0f, gvrCameraR.getBackgroundColorA(), 0.01f);
    }

    /**
     * GVRCamera - Test getBackgroundColorR
     */
    public void testGetBackgroundColorR() {
        Log.d(TAG, "testgetBackgroundColorR");

        init();

        assertEquals(rl, gvrCameraL.getBackgroundColorR(), 0.01f);
        assertEquals(rr, gvrCameraR.getBackgroundColorR(), 0.01f);
    }

    /**
     * GVRCamera - Test getBackgroundColorG
     */
    public void testGetBackgroundColorG() {
        Log.d(TAG, "testgetBackgroundColorG");

        init();

        assertEquals(gl, gvrCameraL.getBackgroundColorG(), 0.01f);
        assertEquals(gr, gvrCameraR.getBackgroundColorG(), 0.01f);
    }

    /**
     * GVRCamera - Test getBackgroundColorB
     */
    public void testGetBackgroundColorB() {
        Log.d(TAG, "testgetBackgroundColorB");

        init();

        assertEquals(bl, gvrCameraL.getBackgroundColorB(), 0.01f);
        assertEquals(br, gvrCameraR.getBackgroundColorB(), 0.01f);
    }

    /**
     * GVRCamera - Test getBackgroundColorA
     */
    public void testGetBackgroundColorA() {

        Log.d(TAG, "testgetBackgroundColorA");
        init();

        assertEquals(al, gvrCameraL.getBackgroundColorA(), 0.01f);
        assertEquals(ar, gvrCameraR.getBackgroundColorA(), 0.01f);
    }


    /**
     * GVRCamera - Test setBackgroundColorR
     */
    public void testSetBackgroundColorR() {
        Log.d(TAG, "testsetBackgroundColorR");

        init();
        gvrCameraL.setBackgroundColorR(1.123f);
        gvrCameraR.setBackgroundColorR(3.211f);

        assertEquals(1.123f, gvrCameraL.getBackgroundColorR(), 0.01f);
        assertEquals(3.211f, gvrCameraR.getBackgroundColorR(), 0.01f);
    }

    /**
     * GVRCamera - Test setBackgroundColorG
     */
    public void testSetBackgroundColorG() {
        Log.d(TAG, "testsetBackgroundColorG");

        init();
        gvrCameraL.setBackgroundColorG(1.123f);
        gvrCameraR.setBackgroundColorG(3.211f);

        assertEquals(1.123f, gvrCameraL.getBackgroundColorG(), 0.01f);
        assertEquals(3.211f, gvrCameraR.getBackgroundColorG(), 0.01f);
    }

    /**
     * GVRCamera - Test setBackgroundColorB
     */
    public void testSetBackgroundColorB() {
        Log.d(TAG, "testsetBackgroundColorB");

        init();

        gvrCameraL.setBackgroundColorB(1.123f);
        gvrCameraR.setBackgroundColorB(3.211f);

        assertEquals(1.123f, gvrCameraL.getBackgroundColorB(), 0.01f);
        assertEquals(3.211f, gvrCameraR.getBackgroundColorB(), 0.01f);
    }


    /**
     * GVRCamera - Test setBackgroundColorA
     */
    public void testSetBackgroundColorA() {
        Log.d(TAG, "testsetBackgroundColorA");

        init();

        gvrCameraL.setBackgroundColorA(1.123f);
        gvrCameraR.setBackgroundColorA(3.211f);

        assertEquals(1.123f, gvrCameraL.getBackgroundColorA(), 0.01f);
        assertEquals(3.211f, gvrCameraR.getBackgroundColorA(), 0.01f);
    }

    public void testSetBackgroundCameraLeftInvalid() {

        gvrCameraL = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        try {
            gvrCameraL.setBackgroundColorA(new Float("s"));
            fail();
        } catch (NumberFormatException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testSetBackgroundCameraRightInvalid() {

        gvrCameraR = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        try {
            gvrCameraR.setBackgroundColorA(new Float("s"));
            fail();
        } catch (NumberFormatException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void init() {
        gvrCameraL = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        gvrCameraR = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getRightCamera();
        //setBackgroundColor(float r, float g, float b, float a)
        gvrCameraL.setBackgroundColor(rl, gl, bl, al);
        gvrCameraR.setBackgroundColor(rr, gr, br, ar);
    }

    /**
     * GVRCamera - Test if getOwnerObject is a GVRSceneObject object
     */
    public void testBackGroundColor() {
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        gvrCamera.setBackgroundColor(Color.YELLOW);
        assertEquals(Color.YELLOW, gvrCamera.getBackgroundColor());
        //assertEquals(GVRSceneObject.class.getName(), gvrCamera.getOwnerObject().getClass().getName());
    }

    public void testgetTransform() {
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        GVRTransform gvrTransform = gvrCamera.getTransform();
        assertNotNull(gvrTransform);
    }


    public void testAttachDetachTransfor() {
        GVRCamera gvrCamera = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig().getLeftCamera();
        GVRSceneObject gvrSceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        gvrCamera.addChildObject(gvrSceneObject);
        assertEquals(1,gvrCamera.getChildrenCount());
        gvrCamera.removeChildObject(gvrSceneObject);
    }

    public void testOthers() { //by Elidelson Carvalho on 10/01/2015
        GVRCameraRig gvrCameraRig = TestDefaultGVRViewManager.mGVRContext.getMainScene().getMainCameraRig();
        gvrCameraRig.setCameraRigType(GVRCameraRig.GVRCameraRigType.RollFreeze.ID);
        assertEquals(gvrCameraRig.getCameraRigType(), GVRCameraRig.GVRCameraRigType.RollFreeze.ID);
    }

}

