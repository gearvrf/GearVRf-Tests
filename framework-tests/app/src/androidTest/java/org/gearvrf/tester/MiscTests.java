package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
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
}
