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

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestUtils.OnInitCallback;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class GVRComplexSceneTests {
    private static final String TAG = GVRComplexSceneTests.class.getSimpleName();
    private GVRTestUtils gvrTestUtils;

    public GVRComplexSceneTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> mActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws Exception {
        gvrTestUtils = new GVRTestUtils(mActivityRule.getActivity());
    }

    private GVRSceneObject getColorMesh(GVRContext gvrContext, ColorShader colorShader, float
            scale, GVRMesh mesh) {
        GVRMaterial material = new GVRMaterial(gvrContext, colorShader.getShaderId());
        material.setVec4(ColorShader.COLOR_KEY, 1.0f,
                0.0f, 1.0f, 1.0f);

        GVRSceneObject meshObject = null;
        meshObject = new GVRSceneObject(gvrContext, mesh);
        meshObject.getTransform().setScale(scale, scale, scale);
        meshObject.getRenderData().setMaterial(material);

        return meshObject;
    }

    @Test
    public void complexScene() throws TimeoutException {
        final Waiter waiter = new Waiter();
        // Execute tests in onInit
        gvrTestUtils.setOnInitCallback(new OnInitCallback() {
            @Override
            public void onInit(GVRContext gvrContext) {
                ColorShader mColorShader = new ColorShader(gvrContext);

                // set background color
                GVRScene scene = gvrContext.getNextMainScene();
                scene.getMainCameraRig().getLeftCamera()
                        .setBackgroundColor(Color.WHITE);
                scene.getMainCameraRig().getRightCamera()
                        .setBackgroundColor(Color.WHITE);

                float NORMAL_CURSOR_SIZE = 0.4f;
                float CURSOR_Z_POSITION = -9.0f;
                int CURSOR_RENDER_ORDER = 100000;
                GVRAndroidResource resource = null;
                try {
                    resource = new GVRAndroidResource(gvrContext, "cursor_idle.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GVRSceneObject cursor = new GVRSceneObject(gvrContext,
                        gvrContext.createQuad(NORMAL_CURSOR_SIZE, NORMAL_CURSOR_SIZE),
                        gvrContext.loadTexture(resource));
                cursor.getTransform().setPositionZ(CURSOR_Z_POSITION);
                cursor.getRenderData().setRenderingOrder(
                        GVRRenderData.GVRRenderingOrder.OVERLAY);
                cursor.getRenderData().setDepthTest(false);
                cursor.getRenderData().setRenderingOrder(CURSOR_RENDER_ORDER);
                gvrContext.getMainScene().getMainCameraRig().addChildObject(cursor);

                try {
                    GVRMesh mesh = gvrContext.loadMesh(new GVRAndroidResource(gvrContext,
                            "bunny.obj"));

                    final int OBJECTS_CNT = 5;
                    for (int x=-OBJECTS_CNT; x<=OBJECTS_CNT; ++x) {
                        for (int y=-OBJECTS_CNT; y<=OBJECTS_CNT; ++y) {
                            GVRSceneObject sceneObject = getColorMesh(gvrContext, mColorShader,
                                    1.0f, mesh);
                            sceneObject.getTransform().setPosition(1.0f*x, 1.0f*y, -5.0f);
                            sceneObject.getTransform().setScale(0.5f, 0.5f, 1.0f);
                            scene.addSceneObject(sceneObject);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        gvrTestUtils.waitForXFrames(10*60);
    }

//    @Test(timeout = GVRTestUtils.TEST_TIMEOUT)
//    public void createQuad() throws TimeoutException {
//        GVRContext gvrContext = gvrTestUtils.waitForOnInit();
//        // Execute tests after onInit is called.
//        GVRMesh mesh = gvrContext.createQuad(2.0f, 2.0f);
//        gvrTestUtils.waitForXFrames(3);
//        Assert.assertTrue(mesh.getNormals().length == NUM_NORMALS_IN_QUAD);
//    }
}
