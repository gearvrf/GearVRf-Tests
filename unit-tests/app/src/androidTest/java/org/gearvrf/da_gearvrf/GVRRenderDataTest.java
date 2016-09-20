package org.gearvrf.da_gearvrf;

import org.gearvrf.GVRLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRRenderData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by santhyago on 2/26/15.
 * Modified by Elidelson Cravlho on 09/09/2015
 */
public class GVRRenderDataTest extends ActivityInstrumentationGVRf {

    public GVRRenderDataTest() {
        super(GVRTestActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testGetAlphaBlend() {
        //TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        //getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        //GVRSceneObject object = TestDefaultGVRViewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        //assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getAlphaBlend());
    }

    public void testGetDepthTest() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getDepthTest());*/

        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getDepthTest());

    }

    public void testGetOffsetUnits() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertEquals(0.0f, renderDataLeftScreen.getOffsetUnits());*/

        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertEquals(0.0f, renderDataLeftScreen.getOffsetUnits());
    }

    public void testGetOffsetFactor() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertEquals(0.0f, renderDataLeftScreen.getOffsetFactor());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertEquals(0.0f, renderDataLeftScreen.getOffsetFactor());
    }

    public void testGetOffset() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertTrue(!renderDataLeftScreen.getOffset());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertTrue(!renderDataLeftScreen.getOffset());
    }

    public void testGetCullTest() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getCullTest());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertEquals(true, renderDataLeftScreen.getCullTest());
        assertNotNull(renderDataLeftScreen.getCullFace());
    }

    public void testGetRenderingOrder() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getRenderingOrder() > 0);*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getRenderingOrder() > 0);
    }

    public void testGetRenderMask() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setRenderMask(2);
        assertTrue(renderDataLeftScreen.getRenderMask() > 0);*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertTrue(renderDataLeftScreen.getRenderMask() > 0);
    }

    public void testSetAlphaBlend() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setAlphaBlend(false);
        assertTrue(!renderDataLeftScreen.getAlphaBlend());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setAlphaBlend(false);
        assertTrue(!renderDataLeftScreen.getAlphaBlend());
    }

    public void testSetDepthTest() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setDepthTest(false);
        assertTrue(!renderDataLeftScreen.getDepthTest());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setDepthTest(false);
        assertTrue(!renderDataLeftScreen.getDepthTest());
    }

    public void testSetOffsetUnits() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffsetUnits(2.0f);
        assertEquals(2.0f, renderDataLeftScreen.getOffsetUnits());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffsetUnits(2.0f);
        assertEquals(2.0f, renderDataLeftScreen.getOffsetUnits());
    }

    public void testSetOffsetFactor() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffsetFactor(2.0f);
        assertEquals(2.0f, renderDataLeftScreen.getOffsetFactor());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffsetFactor(2.0f);
        assertEquals(2.0f, renderDataLeftScreen.getOffsetFactor());
    }

    public void testSetOffset() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffset(true);
        assertTrue(renderDataLeftScreen.getOffset());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setOffset(true);
        assertTrue(renderDataLeftScreen.getOffset());
    }

    public void testSetCullTest() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setCullTest(false);
        assertTrue(!renderDataLeftScreen.getCullTest());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setCullTest(false);
        assertTrue(!renderDataLeftScreen.getCullTest());
        renderDataLeftScreen.setCullTest(true);
        assertTrue(renderDataLeftScreen.getCullTest());
        renderDataLeftScreen.setCullFace(GVRRenderPass.GVRCullFaceEnum.Back);
        renderDataLeftScreen.setCullFace(GVRRenderPass.GVRCullFaceEnum.Back,0);
    }

    public void testSetRenderingOrder() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setRenderingOrder(2);
        assertEquals(2, renderDataLeftScreen.getRenderingOrder());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setRenderingOrder(2);
        assertEquals(2, renderDataLeftScreen.getRenderingOrder());
    }

    public void testSetRenderMask() {
        /*TestDefaultGVRViewManager viewManager = new TestDefaultGVRViewManager();
        getActivity().setScript(viewManager, "gvr_note4.xml");
        GVRSceneObject object = viewManager.mGVRContext.getMainScene().getWholeSceneObjects()[0];
        GVRRenderData renderDataLeftScreen = object.getRenderData();
        assertNotNull(viewManager);
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setRenderMask(2);
        assertEquals(2, renderDataLeftScreen.getRenderMask());*/
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setRenderMask(2);
        assertEquals(2, renderDataLeftScreen.getRenderMask());
    }

    public void testLight() {
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        GVRLight gvrLight = new GVRLight(TestDefaultGVRViewManager.mGVRContext);
        try {
            renderDataLeftScreen.enableLight();
        }catch (UnsupportedOperationException e){}
        try {
            renderDataLeftScreen.disableLight();
        }catch (UnsupportedOperationException e){}
        renderDataLeftScreen.setLight(gvrLight);
        assertNotNull(renderDataLeftScreen.getLight());
        renderDataLeftScreen.disableLight();
        assertTrue(!renderDataLeftScreen.isLightEnabled());
        renderDataLeftScreen.enableLight();
        assertTrue(renderDataLeftScreen.isLightEnabled());
    }

    public void testDrawMode() {
        assertNotNull(TestDefaultGVRViewManager.mGVRContext);
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.setDrawMode(1);
        assertEquals(1, renderDataLeftScreen.getDrawMode());
        try {
            renderDataLeftScreen.setDrawMode(20);
        }catch (IllegalArgumentException e){}
    }

    public void testMaterial() {
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        renderDataLeftScreen.setMaterial(gvrMaterial);
        assertNotNull(renderDataLeftScreen.getMaterial());
        renderDataLeftScreen.setMaterial(gvrMaterial, 1);
        assertEquals(null, renderDataLeftScreen.getMaterial(1));
    }

    public void testMesh() {
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        GVRMesh gvrMesh = new GVRMesh(TestDefaultGVRViewManager.mGVRContext);
        renderDataLeftScreen.setMesh(gvrMesh);
        assertNotNull(renderDataLeftScreen.getMesh());
        Future<GVRMesh> gvrMeshFuture = new Future<GVRMesh>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public GVRMesh get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public GVRMesh get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
        renderDataLeftScreen.setMesh(gvrMeshFuture);

    }

    public void testGetPass() {
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        renderDataLeftScreen.getPass(0);
        renderDataLeftScreen.getPass(20);
    }

    public void testEyePointee() {
        GVRCubeSceneObject gvrCubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        TestDefaultGVRViewManager.mGVRContext.getMainScene().addSceneObject(gvrCubeSceneObject);
        GVRRenderData renderDataLeftScreen = gvrCubeSceneObject.getRenderData();
        assertNotNull(renderDataLeftScreen);
        assertNotNull(renderDataLeftScreen.getMeshEyePointee());
    }


}
