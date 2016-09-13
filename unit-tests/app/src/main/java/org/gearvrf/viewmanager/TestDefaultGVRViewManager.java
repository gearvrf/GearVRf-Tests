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
import org.gearvrf.GVRScene;
import org.gearvrf.GVRScript;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;


public class TestDefaultGVRViewManager extends GVRScript {

    public static GVRContext mGVRContext = null;
    public static int DelayTest = 3500;

    @Override
    public void onInit(GVRContext gvrContext) {

        /*GVRScene gvrSceneObject = new GVRScene(gvrContext);
        GVRTextViewSceneObject textViewSceneObjectC;
        textViewSceneObjectC = new GVRTextViewSceneObject(gvrContext,gvrContext.getActivity(),"Test");
        textViewSceneObjectC.getTransform().setPosition(0.0f, 0.0f, -3.0f);
        textViewSceneObjectC.setText("ABCDEFG");
        textViewSceneObjectC.setTextSize(textViewSceneObjectC.getTextSize() * (2.0f));
        textViewSceneObjectC.setTextColor(Color.rgb(200, 0, 0));*/
        // add the scene object to the scene graph
        //scene.addSceneObject(sceneObject);
        //scene.addSceneObject(sceneObject2);
        //gvrSceneObject.addSceneObject(textViewSceneObjectC);

        synchronized (DefaultGVRTestActivity.class) {
            DefaultGVRTestActivity.sContextLoaded = true;
            DefaultGVRTestActivity.class.notify();
        }
        mGVRContext = gvrContext;
        onInitTest();
    }

    @Override
    public SplashMode getSplashMode() {
        return SplashMode.NONE;
    }

    @Override
    public void onStep() {

    }

    public void onInitTest() {

        /*GVRScene gvrSceneObject = new GVRScene(TestDefaultGVRViewManager.mGVRContext);
        GVRTextViewSceneObject textViewSceneObjectC;
        textViewSceneObjectC = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext, TestDefaultGVRViewManager.mGVRContext.getActivity(),"Test");
        textViewSceneObjectC.getTransform().setPosition(0.0f, 0.0f, -3.0f);
        textViewSceneObjectC.setText("ABCDEFG");
        textViewSceneObjectC.setTextSize(textViewSceneObjectC.getTextSize() * (2.0f));
        textViewSceneObjectC.setTextColor(Color.rgb(200, 0, 0));
        gvrSceneObject.addSceneObject(textViewSceneObjectC);*/

        //for(int i=0;i<10000000;i++);
       /* GVRAndroidResource androidResource = new GVRAndroidResource(mGVRContext, R.mipmap.cylinder);
        GVRMesh mesh = mGVRContext.loadMesh(androidResource);

        GVRSceneObject leftScreen = new GVRSceneObject(mGVRContext, mesh,
                mGVRContext.loadTexture("sample_20140509_l.bmp"));
        GVRSceneObject rightScreen = new GVRSceneObject(mGVRContext, mesh,
                mGVRContext.loadTexture("sample_20140509_r.bmp"));

        mGVRContext.getMainScene().addSceneObject(leftScreen);
        mGVRContext.getMainScene().addSceneObject(rightScreen);
*/
        // set background color

        /*GVRScene scene = mGVRContext.getNextMainScene();

        GVRCameraRig mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.WHITE);
        mainCameraRig.getRightCamera()
                .setBackgroundColor(Color.WHITE);

        // load texture
        GVRTexture texture = mGVRContext.loadTexture(new GVRAndroidResource(
                mGVRContext, R.drawable.gearvr_logo));

        // create a scene object (this constructor creates a rectangular scene
        // object that uses the standard 'unlit' shader)
        GVRSceneObject sceneObject = new GVRSceneObject(mGVRContext, 4.0f, 2.0f,
                texture);

        // set the scene object position
        sceneObject.getTransform().setPosition(0.0f, 0.0f, -3.0f);

        // add the scene object to the scene graph
        scene.addSceneObject(sceneObject);
*/
    }

}
