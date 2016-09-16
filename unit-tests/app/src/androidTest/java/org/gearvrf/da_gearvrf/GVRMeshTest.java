package org.gearvrf.da_gearvrf;

import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.tests.R;
import org.gearvrf.misc.ColorShader;
import org.gearvrf.utils.UtilResource;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.BoundsValues;

import java.security.InvalidParameterException;

/**
 * Created by d.alipio@samsunsg.com; on 1/21/15.
 */
public class GVRMeshTest extends ActivityInstrumentationGVRf {


    private static final float UNPICKED_COLOR_R = 0.7f;
    private static final float UNPICKED_COLOR_G = 0.7f;
    private static final float UNPICKED_COLOR_B = 0.7f;
    private static final float UNPICKED_COLOR_A = 1.0f;
    private GVRSceneObject mSceneObject;

    public GVRMeshTest() {
        super(GVRTestActivity.class);
    }

    /**
     * Valid create mesh factory.
     */
    public void testCreateMeshFactory() {
        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        assertNotNull(gvrContext.createQuad(11, 22));
    }

    public void testTheVerticesPositionOfVertices() {
        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        GVRMesh mesh = new GVRMesh(gvrContext);
        float vertices[] = {-0.5f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, -0.5f, 0.0f};
        mesh.setVertices(vertices);
        assertEquals(mesh.getVertices()[0], vertices[0]);
        assertEquals(mesh.getVertices()[1], vertices[1]);
        assertEquals(mesh.getVertices()[2], vertices[2]);
        assertEquals(mesh.getVertices()[3], vertices[3]);
        assertEquals(mesh.getVertices()[4], vertices[4]);
        assertEquals(mesh.getVertices()[5], vertices[5]);
        assertEquals(mesh.getVertices()[6], vertices[6]);
        assertEquals(mesh.getVertices()[7], vertices[7]);
        assertEquals(mesh.getVertices()[8], vertices[8]);
        assertEquals(mesh.getVertices()[9], vertices[9]);
        assertEquals(mesh.getVertices()[10], vertices[10]);
        assertEquals(mesh.getVertices()[11], vertices[11]);
    }


    private GVRSceneObject getColorBoard(float width, float height) {

        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        ColorShader mColorShader = new ColorShader(gvrContext);
        GVRMaterial material = new GVRMaterial(gvrContext, mColorShader.getShaderId());
        material.setVec4(ColorShader.COLOR_KEY, UNPICKED_COLOR_R, UNPICKED_COLOR_G, UNPICKED_COLOR_B, UNPICKED_COLOR_A);
        GVRSceneObject board = new GVRSceneObject(gvrContext, width, height);
        board.getRenderData().setMaterial(material);

        return board;
    }

    public void testAttachEyePointee() {
        mSceneObject = getColorBoard(1.0f, 1.0f);
        mSceneObject.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        GVRSceneObject object = getColorBoard(1.0f, 1.0f);
        object.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        GVREyePointeeHolder eyePointeeHolder = new GVREyePointeeHolder(gvrContext);
        GVRMeshEyePointee eyePointee = new GVRMeshEyePointee(gvrContext, mSceneObject.getRenderData().getMesh());
        eyePointeeHolder.addPointee(eyePointee);
        mSceneObject.attachEyePointeeHolder(eyePointeeHolder);
    }

    public void testGetGVRMeshOfEyePointee() {
        mSceneObject = getColorBoard(1.0f, 1.0f);
        mSceneObject.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        GVRSceneObject object = getColorBoard(1.0f, 1.0f);
        object.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        GVRMeshEyePointee eyePointee = new GVRMeshEyePointee(gvrContext, mSceneObject.getRenderData().getMesh());
        assertNotNull(eyePointee.getMesh());
    }


    public void testGetOwnerObjectScene() {
        mSceneObject = getColorBoard(1.0f, 1.0f);
        mSceneObject.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        GVRSceneObject object = getColorBoard(1.0f, 1.0f);
        object.getTransform().setPosition(0.0f, 3.0f, -5.0f);
        assertNotNull(mSceneObject);
    }


    public void testGetNormalGVRMesh() {
        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        assertNotNull(gvrMesh.getNormals());
    }


    /**
     * Try set object empty in @gvrMesh.setNormals
     */
    public void testRetrievePositionEmptyArrayInNormalsMesh() {
        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setNormals(BoundsValues.getArrayFloatEmpty());
            fail();

        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }

    }

    /**
     * Try set object empty in @gvrMesh.setFloatVector
     */
    public void testRetrievePositionEmptyArrayInVector() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setFloatVector("ratio_r", BoundsValues.getArrayFloatEmpty());
            fail();
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Try set object empty in @gvrMesh.setTexCoords
     */
    public void testRetrievePositionEmptyArrayInTextCoords() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setTexCoords(BoundsValues.getArrayFloatEmpty());
            fail();
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Try set object empty in @gvrMesh.setVec2Vector
     * TODO-Native Crash
     */

    public void ignoreRetrievePositionEmptyArrayInVec2() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setVec2Vector("ratio_", BoundsValues.getArrayFloatEmpty());
            assertNotNull(gvrMesh.getVec2Vector("ratio_r")[0]);
        } catch (Exception e) {
            fail();
        }

    }

    /**
     * Try set object empty in @gvrMesh.setVec3Vector
     * TODO-Native Crash
     */
    public void ignoreRetrievePositionEmptyArrayInVec3() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setVec3Vector("ratio_", BoundsValues.getArrayFloatEmpty());
            assertNull(gvrMesh.getVec3Vector("ratio_r")[0]);
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Try set object empty in @gvrMesh.setVec4Vector
     */
    public void ignoreRetrievePositionEmptyArrayInVec4() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setVec4Vector("ratio_", BoundsValues.getArrayFloatEmpty());
            assertNotNull(gvrMesh.getVec4Vector("ratio_r")[0]);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Try set object empty in @gvrMesh.setVertices
     * TODO-Native Crashd
     */
    public void ignoreRetrievePositionEmptyArrayInVertices() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));

        try {
            gvrMesh.setVertices(BoundsValues.getArrayFloatEmpty());
            assertNotNull(gvrMesh.getVec4Vector("ratio_r")[0]);
        } catch (Exception e) {
            fail();
        }

    }

    /**
     * Create a array with six position in vec4.
     * In documentation we have:
     * <p/>
     * Bind an array of three-component float vectors to the shader attribute key.
     */
    public void testCreateArrayForVec4With6Position() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));
        float[] vec4 = {-0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f};

        try {
            gvrMesh.setVec4Vector("ratio_r", vec4);
            assertNotNull(gvrMesh.getVec4Vector("ratio_r"));
            fail();
        } catch (Exception e) {
            //assertEquals(e.getMessage(), "setVec4Vector method support only three position array");
        }
    }


    /**
     * Create a array with six position in vec3.
     * In documentation we have:
     * <p/>
     * <p/>
     * Bind an array of three-component float vectors to the shader attribute key.
     */
    public void ignoreCreateArrayForVec3With6Position() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));
        float[] vec3 = {-0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f};

        try {
            gvrMesh.setVec3Vector("ratio_r", vec3);
            assertNotNull(gvrMesh.getVec4Vector("ratio_r"));
            fail();
        } catch (InvalidParameterException e) {
            assertEquals(e.getMessage(), "setVec3Vector method support only three position array");
        }
    }

    /**
     * Create a array with six position in vec2.
     * In documentation we have:
     * <p/>
     * Bind an array of two-component float vectors to the shader attribute key.
     */
    public void ignoreCreateArrayForVec2With6Position() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));
        float[] vec2 = {-0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f};

        try {
            gvrMesh.setVec3Vector("ratio_r", vec2);
            assertNotNull(gvrMesh.getVec4Vector("ratio_r"));
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "setVec2Vector method support only three position array");
        }
    }

    /**
     * Created by Elidelson on 9/02/15.
     */
    public void testGetSetTexCoords() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext,R.raw.cylinder3));
        float[] coords = {0.5f, 0.5f, 0.0f, 0.5f};
        gvrMesh.setTexCoords(coords);
        assertEquals(4,gvrMesh.getTexCoords().length);
        float[] coords2 = {0.5f, 0.5f, 0.0f, 0.5f,0.5f};
        try {
            gvrMesh.setTexCoords(coords2);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
     }

    public void testGetSetFloatVector() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        float[] coords = new float[80];
        for (int i = 0; i < 80; i++) coords[i] = 0.1f;
        gvrMesh.setFloatVector("test", coords);
        assertEquals(80, gvrMesh.getFloatVector("test").length);

        float[] coords2 = new float[100];
        for (int i = 0; i < 100; i++) coords2[i] = 0.1f;
        try{
           gvrMesh.setFloatVector("test", coords2);
           assertEquals(100, gvrMesh.getFloatVector("test").length);
           fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testGetSetVec2Vector() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        float[] coords = new float[160];
        for (int i = 0; i < 160; i++) coords[i] = 0.1f;
        gvrMesh.setVec2Vector("test", coords);
        assertEquals(160, gvrMesh.getVec2Vector("test").length);

        float[] coords2 = new float[100];
        for (int i = 0; i < 100; i++) coords2[i] = 0.1f;
        try{
            gvrMesh.setVec2Vector("test", coords2);
            assertEquals(100, gvrMesh.getVec2Vector("test").length);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }


    public void testGetSetVec3Vector() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        float[] coords = new float[240];
        for (int i = 0; i < 240; i++) coords[i] = 0.1f;
        gvrMesh.setVec3Vector("test", coords);
        assertEquals(240, gvrMesh.getVec3Vector("test").length);

        float[] coords2 = new float[100];
        for (int i = 0; i < 100; i++) coords2[i] = 0.1f;
        try{
            gvrMesh.setVec3Vector("test", coords2);
            assertEquals(100, gvrMesh.getVec3Vector("test").length);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testGetSetVec4Vector() {

        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        float[] coords = new float[320];
        for (int i = 0; i < 320; i++) coords[i] = 0.1f;
        gvrMesh.setVec4Vector("test", coords);
        assertEquals(320, gvrMesh.getVec4Vector("test").length);

        float[] coords2 = new float[100];
        for (int i = 0; i < 100; i++) coords2[i] = 0.1f;
        try{
            gvrMesh.setVec4Vector("test", coords2);
            assertEquals(100, gvrMesh.getVec4Vector("test").length);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testGetBoundingBox(){
        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        GVRMesh gvrMesh2 = gvrMesh.getBoundingBox();
        assertNotNull(gvrMesh2);
    }

    public void testEyePointeeSetMesh(){
        GVRMesh gvrMesh = TestDefaultGVRViewManager.mGVRContext.loadMesh(UtilResource.androidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.cylinder3));
        GVRMeshEyePointee gvrMeshEyePointee = new GVRMeshEyePointee(gvrMesh,true);
        GVRMeshEyePointee gvrMeshEyePointee2 = new GVRMeshEyePointee(gvrMesh,false);
        assertNotNull(gvrMeshEyePointee);
        assertNotNull(gvrMeshEyePointee2);
        gvrMeshEyePointee.setMesh(gvrMesh);
        gvrMeshEyePointee.getMesh();
        assertNotNull(gvrMeshEyePointee);
    }

}
