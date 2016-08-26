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

package org.gearvrf.tester;

import android.os.Bundle;
import android.util.Log;

public class TestableActivity extends GVRTestableActivity {
    private static final String TAG = TestableActivity.class.getSimpleName();
    private TestableMain testableMain;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        testableMain = new TestableMain();
        setMain(testableMain, "gvr_note4.xml");
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

    @Override
    public GVRTestableMain getGVRTestableMain() {
        return testableMain;
    }
}
