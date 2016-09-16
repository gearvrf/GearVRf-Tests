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

import android.os.Bundle;
import android.util.Log;

import org.gearvrf.GVRActivity;

/**
 * This class defines the activity that can be used as the main activity for test projects. This
 * activity can be used as the main activity in the Manifest.xml file for the unit test project.
 */
public class GVRTestableActivity extends GVRActivity {
    private static final String TAG = GVRTestableActivity.class.getSimpleName();
    private GVRTestableMain testableMain;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        testableMain = new GVRTestableMain();
        setMain(testableMain, "gvr.xml");
        Log.d(TAG, "OnCreate called");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "OnDestroy called");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop called");
    }

    GVRTestableMain getGVRTestableMain() {
        return testableMain;
    }
}
