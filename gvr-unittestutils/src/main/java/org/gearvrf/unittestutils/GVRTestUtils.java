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

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.utility.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

/**
 * This class defines utility function to be used for writing unit tests for GearVR framework
 */
public class GVRTestUtils implements GVRMainMonitor {
    private static final String TAG = GVRTestUtils.class.getSimpleName();
    public static final int TEST_TIMEOUT = 2000;
    protected static final int SCREENSHOT_TEST_TIMEOUT = 10000;

    private GVRContext gvrContext;
    private final Object onInitLock;
    private final Object onStepLock;
    private final Object onScreenshotLock;
    private final Object xFramesLock;
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
        gvrContext = null;
        onInitLock = new Object();
        onStepLock = new Object();
        xFramesLock = new Object();
        onScreenshotLock = new Object();
        onAssetLock = new Object();

        if (testableGVRActivity == null) {
            throw new IllegalArgumentException();
        }
        testableMain = testableGVRActivity.getGVRTestableMain();
        if (testableMain != null) {
            testableMain.setMainMonitor(this);
        }
        onInitCallback = null;
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
            synchronized (onInitLock) {
                try {
                    Log.d(TAG, "Waiting for OnInit");
                    onInitLock.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "", e);
                    return null;
                }
                return gvrContext;
            }
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

        synchronized (onStepLock) {
            try {
                Log.d(TAG, "Waiting for OnStep");
                onStepLock.wait();
            } catch (InterruptedException e) {
                Log.e(TAG, "", e);
                return;
            }
        }
    }

    public void waitForAssetLoad() {
        if (mAssetIsLoaded)
            return;
        synchronized (onAssetLock) {
            try {
                Log.d(TAG, "Waiting for OnAssetLoaded");
                onAssetLock.wait();
            } catch (InterruptedException e) {
                Log.e(TAG, "", e);
                return;
            }
        }
    }

    /**
     * Waits for "frames" number of frames to be rendered before returning. This is a blocking call.
     * @param frames number of frames to wait for
     */
    public void waitForXFrames(int frames) {
        testableMain.notifyAfterXFrames(frames);
        synchronized (xFramesLock) {
            try {
                xFramesLock.wait();
            } catch (InterruptedException e) {
                Log.e(TAG,"",e);
                return;

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
        synchronized (onInitLock) {
            onInitLock.notifyAll();
        }
        Log.d(TAG, "On Init called");
    }

    @Override
    public void onSceneRendered() {
        if (onRenderCallback != null) {
            onRenderCallback.onSceneRendered();
        }
        synchronized (onStepLock) {
            onStepLock.notifyAll();
        }
    }

    public void xFramesRendered() {
        synchronized (xFramesLock) {
            xFramesLock.notifyAll();
        }
    }

    public void onAssetLoaded(GVRSceneObject asset) {
        mAssetIsLoaded = true;
        synchronized (onAssetLock) {
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
            private void compareWithGolden(Bitmap bitmap, String testname, Waiter waiter)
            {
                Bitmap golden = null;
                try
                {
                    InputStream stream = gvrContext.getContext().getAssets().open(category + "/" + testname);
                    golden = BitmapFactory.decodeStream(stream);
                }
                catch (Throwable ex)
                {
                    waiter.fail(ex);
                }
                if (golden != null)
                {
                    waiter.assertEquals(golden.getWidth(), bitmap.getWidth());
                    waiter.assertEquals(golden.getHeight(), bitmap.getHeight());

                    Bitmap diffmap = golden.copy(golden.getConfig(), true);
                    float diff = 0;
                    try {
                        for (int y = 0; y < golden.getHeight(); y++) {
                            for (int x = 0; x < golden.getWidth(); x++) {
                                int p1 = golden.getPixel(x, y);
                                int p2 = bitmap.getPixel(x, y);
                                int r = Math.abs(Color.red(p1) - Color.red(p2));
                                int g = Math.abs(Color.green(p1) - Color.green(p2));
                                int b = Math.abs(Color.blue(p1) - Color.blue(p2));
                                diffmap.setPixel(x, y, Color.argb(255, r, g, b));
                                diff += (float) r / 255.0f + g / 255.0f + b / 255.0f;
                            }
                        }
                    }
                    catch (Throwable t)
                    {
                        waiter.fail(t);
                    }

                    Log.e(category, category + ": %s %f", testname, diff);
                    if (diff > 1000.0f)
                    {
                        writeBitmap(category, "diff_" + testname, diffmap);
                    }
                    waiter.assertTrue(diff <= 30000.0f);
                }
            }

            protected void writeBitmap(String dir, String filename, Bitmap bitmap)
            {
                try
                {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    File sdcard = Environment.getExternalStorageDirectory();
                    dir = sdcard.getAbsolutePath() + "/GearVRFTests/" + dir + "/";
                    File d = new File(dir);
                    d.mkdirs();
                    File f = new File(d, filename);
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }
                catch (Throwable ex)
                {ex.printStackTrace();
                    waiter.fail(ex);
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
}
