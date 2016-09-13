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

package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.utility.FileNameUtils;
import org.gearvrf.utility.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

public class GVRTestUtils implements GVRMainMonitor {
    private static final String TAG = GVRTestUtils.class.getSimpleName();
    protected static final int TEST_TIMEOUT = 2000;
    protected static final int SCREENSHOT_TEST_TIMEOUT = 10000;

    private GVRContext gvrContext;
    private final Object onInitLock;
    private final Object onStepLock;
    private final Object onAssetLock;
    private GVRTestableMain testableMain;
    private GVRScene mainScene;
    private OnInitCallback onInitCallback;
    private OnRenderCallback onRenderCallback;
    private boolean mAssetIsLoaded = false;

    public GVRTestUtils(GVRTestableActivity testableGVRActivity) {
        gvrContext = null;
        onInitLock = new Object();
        onStepLock = new Object();
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

    public GVRContext waitForOnInit() {
        if (gvrContext == null) {
            if (testableMain.isOnInitCalled()) {
                gvrContext = testableMain.getGVRContext();
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
        Log.d(TAG, "OnSceneRenderedCalled");
    }

    @Override
    public void onAssetLoaded(GVRSceneObject asset) {
        mAssetIsLoaded = true;
        synchronized (onAssetLock) {
            onAssetLock.notifyAll();
        }
        Log.d(TAG, "OnAssetLoaded Called");
    }

    public GVRContext getGvrContext() {
        return gvrContext;
    }

    public GVRScene getMainScene() {
        return mainScene;
    }

    public void setOnInitCallback(OnInitCallback callback) {
        this.onInitCallback = callback;
    }

    public void setOnRenderCallback(OnRenderCallback callback) { this.onRenderCallback = callback; }

    public interface OnInitCallback {
        void onInit(GVRContext gvrContext);
    }

    public interface OnRenderCallback {
        void onSceneRendered();
    }

    public interface OnAssetCallback {
        void onAssetLoaded(GVRSceneObject asset);
    }

    void screenShot(final String category, final String testname, final Waiter waiter, final boolean doCompare) throws TimeoutException
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
                catch (IOException ex)
                {
                    waiter.fail(ex);
                }
                if (golden != null)
                {
                    waiter.assertEquals(golden.getWidth(), bitmap.getWidth());
                    waiter.assertEquals(golden.getHeight(), bitmap.getHeight());

                    Bitmap diffmap = golden.copy(golden.getConfig(), true);
                    float diff = 0;
                    for (int y = 0; y < golden.getHeight(); y++)
                    {
                        for (int x = 0; x < golden.getWidth(); x++)
                        {
                            int p1 = golden.getPixel(x, y);
                            int p2 = bitmap.getPixel(x, y);
                            int r = Math.abs(Color.red(p1) - Color.red(p2));
                            int g = Math.abs(Color.green(p1) - Color.green(p2));
                            int b = Math.abs(Color.blue(p1) - Color.blue(p2));
                            diffmap.setPixel(x, y, Color.argb(255, r, g, b));
                            diff += (float) r / 255.0f + g / 255.0f + b / 255.0f;
                        }
                    }

                    Log.e(category, category + ": %s %f", testname, diff);
                    if (diff > 1000.0f)
                    {
                        writeBitmap(category, "diff_" + testname, diffmap);
                    }
                    waiter.assertTrue(diff <= 1000.0f);
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
                catch (IOException ex)
                {
                    waiter.fail(ex);
                }
            }

            @Override
            public void onScreenCaptured(Bitmap bitmap)
            {
                try
                {
                    String basename = testname + ".png";
                    writeBitmap(category, basename, bitmap);
                    Log.d(category, "Saved screenshot of %s", testname);
                    if (doCompare)
                    {
                        compareWithGolden(bitmap, basename, waiter);
                    }
                    waiter.resume();
                }
                catch (Exception e)
                {
                    Log.d(category, "Could not save screenshot of %s", testname);
                    waiter.fail(e);
                }
            }
        };
        waitForSceneRendering();
        gvrContext.captureScreenCenter(callback);
        waiter.await();
    }
}
