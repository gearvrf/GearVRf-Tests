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


package org.gearvrf.viewmanager;

import android.graphics.Color;

import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRScript;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;


public class TestDefaultGVRViewManager extends GVRMain {

    public static GVRContext mGVRContext = null;
    public static int DelayTest = 3500;

    @Override
    public void onInit(GVRContext gvrContext) {
        synchronized (DefaultGVRTestActivity.class) {
            DefaultGVRTestActivity.sContextLoaded = true;
            DefaultGVRTestActivity.class.notify();
        }
        mGVRContext = gvrContext;
    }

    @Override
    public SplashMode getSplashMode() {
        return SplashMode.NONE;
    }

    @Override
    public void onStep() {

    }

}
