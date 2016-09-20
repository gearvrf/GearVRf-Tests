/*
 * Copyright 2016. Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMesh;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestUtils.OnInitCallback;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class GVRContextTests {
    private static final String TAG = GVRContextTests.class.getSimpleName();
    private static final int NUM_NORMALS_IN_QUAD = 12;
    private GVRTestUtils gvrTestUtils;

    public GVRContextTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> mActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws Exception {
        gvrTestUtils = new GVRTestUtils(mActivityRule.getActivity());
    }

    @Test(timeout = GVRTestUtils.TEST_TIMEOUT)
    public void contextNonNull() throws TimeoutException {
        final Waiter waiter = new Waiter();
        // Execute tests in onInit
        gvrTestUtils.setOnInitCallback(new OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                waiter.assertNotNull(gvrContext.getEventManager());
                waiter.assertNotNull(gvrContext.getEventReceiver());
                waiter.assertNotNull(gvrContext.getInputManager());
                waiter.assertNotNull(gvrContext.getAssetLoader());
                waiter.assertNotNull(gvrContext.getAnimationEngine());
                waiter.assertNotNull(gvrContext.getActivity());
                waiter.resume();
            }
        });
        waiter.await(GVRTestUtils.TEST_TIMEOUT);
    }

    @Test(timeout = GVRTestUtils.TEST_TIMEOUT)
    public void createQuad() throws TimeoutException {
        GVRContext gvrContext = gvrTestUtils.waitForOnInit();
        // Execute tests after onInit is called.
        GVRMesh mesh = gvrContext.createQuad(2.0f, 2.0f);
        gvrTestUtils.waitForFrameCount(3);
        Assert.assertTrue(mesh.getNormals().length == NUM_NORMALS_IN_QUAD);
    }
}
