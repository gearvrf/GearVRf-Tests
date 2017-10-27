/*
 * Copyright (c) 2016. Samsung Electronics Co., LTD
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.unittestutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Build;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.utility.Log;
import org.gearvrf.utility.Threads;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This class defines utility function to be used for writing unit tests for GearVR framework
 */
public class GVRTestUtils implements GVRMainMonitor {
    private static final String TAG = GVRTestUtils.class.getSimpleName();
    private static final String DEFAULT_DEVICE_TYPE = "S7Edge";
    public static final String DEVICE_TYPE = GetDeviceType();
    public static final String GITHUB_URL = "https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/";
    private static final String GOLDEN_MASTERS_BASE_URL = GITHUB_URL + "golden_masters/";
    public static final String GOLDEN_MASTERS_URL = GOLDEN_MASTERS_BASE_URL + DEVICE_TYPE;

    protected static final int SCREENSHOT_TEST_TIMEOUT = 80000;

    private GVRContext gvrContext;
    private final CountDownLatch onInitLatch = new CountDownLatch(1);
    private final CountDownLatch onStepLatch = new CountDownLatch(1);
    private final Object onScreenshotLock;
    private final Object xFramesLock;
    private boolean mFramesLockDone = true;
    private final Object onAssetLock;
    private GVRTestableMain testableMain;
    private GVRScene mainScene;
    private OnInitCallback onInitCallback;
    private OnRenderCallback onRenderCallback;
    private boolean mAssetIsLoaded = false;

    /**
     * Constructor, needs an instance of {@link GVRTestableActivity}.
     * @param testableGVRActivity The instance of the activity to be tested.
     */
    public GVRTestUtils(GVRTestableActivity testableGVRActivity) {
        this(testableGVRActivity, null);
    }

    /**
     * Constructor, needs an instance of {@link GVRTestableActivity} && GVRTestUtils.OnInitCallback.
     * @param testableGVRActivity The instance of the activity to be tested.
     */
    public GVRTestUtils(GVRTestableActivity testableGVRActivity, OnInitCallback onInitCallback) {
        gvrContext = null;
        xFramesLock = new Object();
        onScreenshotLock = new Object();
        onAssetLock = new Object();
        this.onInitCallback = onInitCallback;

        if (testableGVRActivity == null) {
            throw new IllegalArgumentException();
        }

        testableMain = testableGVRActivity.getGVRTestableMain();
        if (testableMain != null) {
            testableMain.setMainMonitor(this);
        }

    }

    private static String GetDeviceType() {
        String TryUrl = GOLDEN_MASTERS_BASE_URL + Build.MODEL;
        try {
            return new BufferedReader(new InputStreamReader(new URL(TryUrl).openStream())).readLine().trim();
        } catch (Exception ex) {
            Log.e(TAG,"Golden master redirect not found: " + TryUrl);
            return DEFAULT_DEVICE_TYPE;
        }
    }

    /**
     * Waits for the {@link GVRMain#onInit(GVRContext)} to be called on the corresponding
     * {@link GVRMain}. This function is useful to obtain an instance to the {@link GVRContext}
     * in the unit tests.
     * @return Returns the {@link GVRContext} instance associated with the application.
     */
    public GVRContext waitForOnInit() {
        if (gvrContext == null) {
            if (testableMain.isOnInitCalled()) {
                gvrContext = testableMain.getGVRContext();
                mainScene = gvrContext.getMainScene();
                return gvrContext;
            }
            try {
                Log.d(TAG, "Waiting for OnInit");
                onInitLatch.await();
            } catch (InterruptedException e) {
                Log.e(TAG, "", e);
                Thread.currentThread().interrupt();
                return null;
            }
            return gvrContext;
        } else {
            return gvrContext;
        }
    }

    /**
     * Waits for the first frame to be rendered. It returns after the first time
     * {@link GVRMain#onStep()} is called. If {@link GVRMain#onStep()} is already called it
     * returns immediately. This is a blocking call.
     */
    public void waitForSceneRendering() {
        if (testableMain.isSceneRendered()) {
            return;
        }

        try {
            Log.d(TAG, "Waiting for OnStep");
            onStepLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
            Thread.currentThread().interrupt();
            return;
        }
    }

    public void waitForAssetLoad() {
        synchronized (onAssetLock) {
            while(!mAssetIsLoaded) {
                try {
                    Log.d(TAG, "Waiting for OnAssetLoaded");
                    onAssetLock.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "", e);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            mAssetIsLoaded = false;
        }
    }

    /**
     * Waits for "frames" number of frames to be rendered before returning. This is a blocking call.
     * @param frames number of frames to wait for
     */
    public void waitForXFrames(int frames) {
        synchronized (xFramesLock) {
            mFramesLockDone = false;
            testableMain.notifyAfterXFrames(frames);
            while(!mFramesLockDone) {
                try {
                    xFramesLock.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG,"",e);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    @Override
    public void onInitCalled(GVRContext context, GVRScene mainScene) {
        this.mainScene = mainScene;
        gvrContext = context;
        if (onInitCallback != null) {
            onInitCallback.onInit(gvrContext);
        }
        onInitLatch.countDown();
        Log.d(TAG, "On Init called");
    }

    @Override
    public void onSceneRendered() {
        if (onRenderCallback != null) {
            onRenderCallback.onSceneRendered();
        }
        onStepLatch.countDown();
    }

    public void xFramesRendered() {
        synchronized (xFramesLock) {
            mFramesLockDone = true;
            xFramesLock.notifyAll();
        }
    }

    public void onAssetLoaded(GVRSceneObject asset) {
        synchronized (onAssetLock) {
            mAssetIsLoaded = true;
            onAssetLock.notifyAll();
        }
        Log.d(TAG, "OnAssetLoaded Called");
    }

    /**
     *  Returns the {@link GVRContext} associated with the application
     * @return the {@link GVRContext} instance
     */
    public GVRContext getGvrContext() {
        return gvrContext;
    }

    /**
     *  Returns the main scene to be rendered.
     * @return the {@link GVRScene} instance of the main scene.
     */
    public GVRScene getMainScene() {
        return mainScene;
    }

    /**
     * Set a callback to be executed in the {@link GVRMain#onInit(GVRContext)} method. This can
     * be used for executing code in the {@link GVRMain#onInit(GVRContext)} method. To use
     * assertions inside the {@link OnInitCallback} use the {@link Waiter} class.
     * @param callback
     */
    public void setOnInitCallback(OnInitCallback callback) {
        this.onInitCallback = callback;
    }

    /**
     * Set a callback to be executed when {@link GVRMain#onStep()} is called for the first time.
     * This callback executed on the GL thread. Use this to execute code in the
     * {@link GVRMain#onStep()} method. To use assertions inside the {@link OnRenderCallback} use
     * the {@link Waiter} class.
     * @param callback
     */
    public void setOnRenderCallback(OnRenderCallback callback) { this.onRenderCallback = callback; }

    /**
     * Defines the interface for setting a callback in the {@link GVRMain#onInit(GVRContext)}
     * method. Use the {@link GVRTestUtils#setOnInitCallback(OnInitCallback)} to set this callback.
     */
    public interface OnInitCallback {
        void onInit(GVRContext gvrContext);
    }

    /**
     * Defines the interface for setting a callback which is invoked when the
     * {@link GVRMain#onStep()} is executed for the first time. Use the
     * {@link GVRTestUtils#setOnRenderCallback(OnRenderCallback)} to set this callback.
     */
    public interface OnRenderCallback {
        void onSceneRendered();
    }

    public interface OnAssetCallback {
        void onAssetLoaded(GVRSceneObject asset);
    }

    /**
     * Captures a screenshot and compares it with a golden screenshot from the
     * assets directory. This method looks for a file named "diff_$testname$.png" in the assets
     * folder for the reference screenshot of the expected result. The captured screenshots are
     * stored in /sdcard/GearVRfTests/$category$/$testname$.png
     * @param category directory to store screenshots in.
     * @param testname the name of the test method.
     * @param waiter instance of the {@link Waiter} class.
     * @param doCompare flag used to turnon/off comparison of screenshots.
     * @throws TimeoutException
     */
    public void screenShot(final String category, final String testname, final Waiter waiter,
                           final boolean doCompare) throws TimeoutException
    {
        GVRScreenshotCallback callback = new GVRScreenshotCallback()
        {
            private void compareWithGolden(final Bitmap bitmap, String testname, Waiter waiter)
            {
                try {
                    Bitmap golden = null;

                    try {
                        URL url = new URL(ASSETS_BASE_URL + "golden_masters/" + DEVICE_TYPE + "/" + category + "/" + testname);
                        final InputStream inputStream = url.openStream();
                        try {
                            final BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            golden = BitmapFactory.decodeStream(inputStream, null, options);
                        } finally {
                            inputStream.close();
                        }
                    } catch (Throwable ex) {
                        waiter.fail(ex);
                    }
                    if (golden != null) {
                        try {
                            final float[] diff = {0.0f};
                            waiter.assertEquals(golden.getWidth(), bitmap.getWidth());
                            waiter.assertEquals(golden.getHeight(), bitmap.getHeight());

                            final ReentrantLock lockDiff = new ReentrantLock();

                            try {
                                final CountDownLatch cdl = new CountDownLatch(golden.getHeight());

                                for (int y = 0; y < golden.getHeight(); y++) {
                                    Threads.spawn(new CompareRunnable(y, golden, lockDiff, diff, bitmap, cdl));
                                }

                                cdl.await();
                            } catch (Throwable t) {
                                waiter.fail(t);
                            }

                            Log.e(category, category + ": %s %f", testname, diff[0]);
                            if (diff[0] > 1000.0f) {
                                writeBitmap(category, "diff_" + testname, golden);
                            }

                            waiter.assertTrue(diff[0] <= 30000.0f);
                        } finally {
                            golden.recycle();
                        }
                    }
                } finally {
                    bitmap.recycle();
                }
            }

            protected void writeBitmap(String dir, String filename, Bitmap bitmap)
            {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                File sdcard = Environment.getExternalStorageDirectory();
                dir = sdcard.getAbsolutePath() + "/GearVRFTests/" + dir + "/";
                File d = new File(dir);
                d.mkdirs();

                try
                {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    FileOutputStream fo = new FileOutputStream(new File(d, filename));
                    try {
                        bytes.writeTo(fo);
                    } finally {
                        fo.close();
                    }
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                    waiter.fail(ex);
                } finally {
                    try {
                        bytes.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onScreenCaptured(Bitmap bitmap)
            {
                synchronized (onScreenshotLock)
                {
                    String basename = testname + ".png";

                    try
                    {
                        writeBitmap(category, basename, bitmap);
                    }
                    catch (Exception e)
                    {
                        Log.e(category, "Could not save screenshot of %s", testname);
                        waiter.fail(e);
                    }
                    try
                    {
                        Log.d(category, "Saved screenshot of %s", testname);
                        if (doCompare)
                        {
                            compareWithGolden(bitmap, basename, waiter);
                        }
                    }
                    catch (Throwable t)
                    {
                        Log.d(category, "Exception while comparing screenshot for %s", testname);
                        waiter.fail(t);
                    }
                    waiter.resume();
                }
            }
        };
        waitForSceneRendering();
        gvrContext.captureScreenCenter(callback);
        waiter.await();
    }

    final static class CompareRunnable implements Runnable {
        private final int y;
        private Bitmap golden;
        private ReentrantLock lockDiff;
        private float[] diff;
        private Bitmap bitmap;
        private CountDownLatch cdl;

        CompareRunnable(final int y, final Bitmap golden, final ReentrantLock lockDiff, final float[] diff,
                        final Bitmap bitmap, final CountDownLatch cdl) {
            this.y = y;
            this.golden = golden;
            this.lockDiff = lockDiff;
            this.diff = diff;
            this.bitmap = bitmap;
            this.cdl = cdl;
        }

        @Override
        public void run() {
            try {
                for (int x = 0; x < golden.getWidth(); x++) {
                    int p1 = golden.getPixel(x, y);
                    int p2 = bitmap.getPixel(x, y);
                    int r = Math.abs(Color.red(p1) - Color.red(p2));
                    int g = Math.abs(Color.green(p1) - Color.green(p2));
                    int b = Math.abs(Color.blue(p1) - Color.blue(p2));
                    golden.setPixel(x, y, Color.argb(255, r, g, b));

                    lockDiff.lock();
                    try {
                        diff[0] += (float) r / 255.0f + g / 255.0f + b / 255.0f;
                    } finally {
                        lockDiff.unlock();
                    }
                }
            } finally {
                cdl.countDown();

                golden = null;
                bitmap = null;
                lockDiff = null;
                cdl = null;
                diff = null;
            }
        }
    }
}
