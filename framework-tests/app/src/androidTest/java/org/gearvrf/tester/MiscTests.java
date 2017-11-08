package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRNotifications;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MiscTests {
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown() {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null) {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);
    }

    /**
     * Used to crash; verifies it doesn't anymore.
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void stressRenderDataDirty() throws TimeoutException, InterruptedException, ExecutionException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        final Bitmap gearvr_logo = BitmapFactory.decodeResource(ctx.getActivity().getResources(), R.drawable.gearvr_logo);
        final GVRMaterial material = new GVRMaterial(ctx);
        final GVRMesh mesh = ctx.createQuad(4, 2);
        final GVRRenderPass pass = new GVRRenderPass(ctx);
        pass.setMaterial(material);

        try {
            for (int testRun = 0; testRun < 2000; ++testRun) {
                GVRTexture t = new GVRTexture(ctx);
                GVRBitmapTexture bmap = new GVRBitmapTexture(ctx, gearvr_logo);
                t.setImage(bmap);
                final GVRSceneObject so1 = new GVRSceneObject(ctx, 3, 2, t);
                so1.getTransform().setPosition(0, 0, -3);
                so1.getRenderData().setMaterial(material);
                so1.getRenderData().setMesh(mesh);
                so1.getRenderData().addPass(pass);
                scene.addSceneObject(so1);

                final GVRSceneObject so2 = new GVRSceneObject(ctx, 2, 1, t);
                so2.getTransform().setPosition(-1, -1, -3);
                so2.getRenderData().setMaterial(material);
                so2.getRenderData().setMesh(mesh);
                so2.getRenderData().addPass(pass);
                scene.addSceneObject(so2);

                //dirty the updateGPU data; allocate a big buffer to create some memory pressure
                //and have the gc run sooner
                scene.clear();
                byte[] b = new byte[1*1024*1024];
                pass.setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
                final float[] texCoords = mesh.getTexCoords();
                mesh.setFloatArray("a_texcoord", texCoords);
                material.setDiffuseColor(0, 0, 0, 0);
                GVRNotifications.waitAfterStep();
          }
        } catch (final Throwable t) {
            t.printStackTrace();
            mWaiter.assertTrue(false);
        }
    }

    @Test
    @UiThreadTest
    public void gcOomTest1() throws Exception {
        oomTest(false);
    }

    @Test
    @UiThreadTest
    public void gcOomTest2() throws Exception {
        oomTest(true);
    }

    //  Change this between true & false to either trigger an OutOfMemoryError exception or a
    // "global reference table overflow" crash.
    private void oomTest(boolean createBitmap) throws IOException {
        try {
            final int MaxInstances = 100000;
            GVRContext gvrContext = ActivityRule.getActivity() .getGVRContext();
            for (int count = 0; count < MaxInstances; count++) {
                Log.d(TAG, "Count: " + count);
                GVRSceneObject sceneObject = new GVRSceneObject(gvrContext, gvrContext.createQuad(10f, 10f));
                GVRRenderData renderData = sceneObject.getRenderData();
                GVRTexture texture;
                if (createBitmap) {
                    Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                    texture = new GVRTexture(gvrContext);
                    texture.setImage(new GVRBitmapTexture(gvrContext, bitmap));
                } else {
                    texture = gvrContext.getAssetLoader().loadTexture(new GVRAndroidResource(gvrContext, "StencilTests/GearVR.jpg"));
                }
                renderData.getMaterial().setMainTexture(texture);
                renderData.setAlphaBlend(true);
                if ((count % 100) == 99) {
                    System.gc();
                    System.runFinalization();
                }
            }
        } catch (OutOfMemoryError oom) {
            HeapDump();
            fail(oom.getMessage());
        }
    }

    private void HeapDump() {
        Log.d(TAG, "Dumping heap");
        try {
            File external = Environment.getExternalStorageDirectory();
            File folder = new File(external, "Documents");
            File heapDumpFile1 = new File(folder, "oom.hprof");
            Debug.dumpHprofData(heapDumpFile1.getPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Log.d(TAG, "Finished heap dump");
    }

    @Test
    public void testMeshSimpleApi1() {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        final GVRCylinderSceneObject so = new GVRCylinderSceneObject(ctx);
        final GVRMesh mesh = so.getRenderData().getMesh();

        mWaiter.assertTrue(0 < mesh.getIndices().length);

        float[] asArray = mesh.getVertices();
        mWaiter.assertTrue(0 < asArray.length);
        FloatBuffer asBuffer = mesh.getVerticesAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());

        asArray = mesh.getNormals();
        mWaiter.assertTrue(0 < asArray.length);
        asBuffer = mesh.getNormalsAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());

        asArray = mesh.getTexCoords();
        mWaiter.assertTrue(0 < asArray.length);
        asBuffer = mesh.getTexCoordsAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());
    }

    @Test
    public void testVertexBufferSimpleApi1() {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        mTestUtils.waitForOnInit();
        final GVRCylinderSceneObject so = new GVRCylinderSceneObject(ctx);
        so.getTransform().setPosition(0,0,-2);
        scene.addSceneObject(so);
        mTestUtils.waitForSceneRendering();
        GVRNotifications.waitAfterStep();

        {
            final float[] bound = new float[6];
            final boolean result = so.getRenderData().getMesh().getVertexBuffer().getBoxBound(bound);
            mWaiter.assertTrue(result);
            mWaiter.assertTrue(0 != bound[0] && 0 != bound[1] && 0 != bound[2] && 0 != bound[3]
                    && 0 != bound[4] && 0 != bound[5]);
        }

        {
            final float[] bound = new float[4];
            final float radius = so.getRenderData().getMesh().getVertexBuffer().getSphereBound(bound);
            mWaiter.assertTrue(0 != radius);
        }
    }

    private final static String TAG = "MiscTests";
}
