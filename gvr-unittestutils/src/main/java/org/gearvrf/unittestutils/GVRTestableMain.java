/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRTexture;
import org.gearvrf.utility.Log;

class GVRTestableMain extends GVRMain{

    private static final String TAG = GVRTestableMain.class.getSimpleName();
    private static final int WAIT_DISABLED = -1;
    private GVRContext gvrContext;
    private GVRScene mainScene;
    private GVRMainMonitor mainMonitor;
    private boolean sceneRendered = false;
    private int framesRendered = 0;
    private final Object waitXFramesLock = new Object();
    private int waitForXFrames = WAIT_DISABLED;

    @Override
    public void onInit(GVRContext gvrContext) {
        this.gvrContext = gvrContext;
        mainScene = gvrContext.getMainScene();

        //Freeze the camera rig for the tests
        mainScene.getMainCameraRig().setCameraRigType(GVRCameraRig.GVRCameraRigType.Freeze.ID);
        if (mainMonitor != null) {
            mainMonitor.onInitCalled(gvrContext, mainScene);
        } else {
            Log.d(TAG, "On Init callback is null when on init is called");
        }
    }

    @Override
    public void onStep() {
        if (mainMonitor != null) {
            sceneRendered = true;
        }
        synchronized (waitXFramesLock) {
            if (waitForXFrames != WAIT_DISABLED) {
                framesRendered++;
                if (framesRendered == waitForXFrames) {
                    mainMonitor.xFramesRendered();
                    waitForXFrames = WAIT_DISABLED;
                    framesRendered = 0;
                }
            }
        }
        mainMonitor.onSceneRendered();
    }

    public void setMainMonitor(GVRMainMonitor mainMonitor) {
        this.mainMonitor = mainMonitor;
    }

    public boolean isOnInitCalled() {
        return (gvrContext != null);
    }

    public boolean isSceneRendered() {
        return sceneRendered;
    }

    public void notifyAfterXFrames(int frames) {
        synchronized (waitXFramesLock) {
            waitForXFrames = frames;
            framesRendered = 0;
        }

    }

    public GVRTexture getSplashTexture(GVRContext gvrContext) {
        Bitmap bitmap = BitmapFactory.decodeResource(
                gvrContext.getContext().getResources(),
                R.mipmap.ic_launcher);
        // return the correct splash screen bitmap
        return new GVRBitmapTexture(gvrContext, bitmap);
    }
}
