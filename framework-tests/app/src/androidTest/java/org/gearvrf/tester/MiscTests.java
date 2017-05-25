package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRNotifications;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)

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
/*
    @Test
    public void testTextureGetFutureIdOnGlThread() throws TimeoutException, InterruptedException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final Bitmap b = BitmapFactory.decodeResource(ctx.getActivity().getResources(), R.drawable.gearvr_logo);

        final CountDownLatch cdl = new CountDownLatch(1);
        final int[] tid = { 0 };
        ctx.runOnGlThread(new Runnable() {
            @Override
            public void run() {
                final GVRBitmapTexture t = new GVRBitmapTexture(ctx, b);
                final Future<Integer> f = t.getFutureId();
                try {
                    tid[0] = f.get();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                cdl.countDown();
            }
        });

        cdl.await(5, TimeUnit.SECONDS);
        mWaiter.assertTrue(0 != tid[0]);
    }

    @Test
    public void testTextureGetFutureIdOnUserThread() throws TimeoutException, InterruptedException, ExecutionException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final Bitmap b = BitmapFactory.decodeResource(ctx.getActivity().getResources(), R.drawable.gearvr_logo);

        final GVRBitmapTexture t = new GVRBitmapTexture(ctx, b);
        final Future<Integer> f = t.getFutureId();
        final Integer id = f.get(5, TimeUnit.SECONDS);

        mWaiter.assertTrue(0 != id);
    }
*/

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
}
