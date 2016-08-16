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

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.utility.Log;

public class GVRTestUtils implements GVRMainMonitor {
    private static final String TAG = GVRTestUtils.class.getSimpleName();
    protected static final int TEST_TIMEOUT = 2000;

    private GVRContext gvrContext;
    private final Object onInitLock;
    private final Object onStepLock;
    private GVRTestableMain testableMain;
    private GVRScene mainScene;
    private OnInitCallback onInitCallback;
    private OnRenderCallback onRenderCallback;

    public GVRTestUtils(GVRTestableActivity testableGVRActivity) {
        gvrContext = null;
        onInitLock = new Object();
        onStepLock = new Object();
        if (testableGVRActivity == null) {
            throw new IllegalArgumentException();
        }
        testableMain = testableGVRActivity.getGVRTestableMain();
        testableMain.setMainMonitor(this);
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

    @Override
    public void onInitCalled(GVRContext context, GVRScene mainScene) {
        gvrContext = context;
        this.mainScene = mainScene;
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
}
