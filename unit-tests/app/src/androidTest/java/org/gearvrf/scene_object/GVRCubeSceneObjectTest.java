package org.gearvrf.scene_object;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.scene_objects.GVRCubeSceneObject;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Created by j.elidelson on 5/29/2015.
 */
public class GVRCubeSceneObjectTest extends ActivityInstrumentationGVRf {

    //************
    //*** Cube ***
    //************
    public void testCubeConstructor() {

        GVRCubeSceneObject cubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(cubeSceneObject);
    }

    public void ignoretestCubeNullConstructor() {

        try {
            GVRCubeSceneObject cubeSceneObject2 = new GVRCubeSceneObject(null);
            assertNull(cubeSceneObject2);
        } catch (NullPointerException e) {
            assertEquals(null, null);
        }
    }

    public void testCubeConstructorFacingout() {

        GVRCubeSceneObject cubeSceneObjectTrue = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, true);
        assertNotNull(cubeSceneObjectTrue);
        GVRCubeSceneObject cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false);
        assertNotNull(cubeSceneObjectFalse);
    }


    public void testCubeConstructorFacingoutMaterial() {

        GVRCubeSceneObject cubeSceneObject = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRCubeSceneObject cubeSceneObjectTrue = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, true, cubeSceneObject.getRenderData().getMaterial());
        assertNotNull(cubeSceneObjectTrue);
        GVRCubeSceneObject cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, cubeSceneObject.getRenderData().getMaterial());
        assertNotNull(cubeSceneObjectFalse);

    }

    public void testCubeConstructorFacingoutFutureTexture() {

        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.drawable.bottom));
        GVRCubeSceneObject cubeSceneObjectTrue = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, true, futureTexture);
        assertNotNull(cubeSceneObjectTrue);
        GVRCubeSceneObject cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTexture);
        assertNotNull(cubeSceneObjectFalse);
    }

    public void testCubeConstructorFacingoutArrayFutureTexture() {

        ArrayList<Future<GVRTexture>> futureTextureList = new ArrayList<Future<GVRTexture>>(
                6);
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.back)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.right)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.front)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.left)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.top)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));

        GVRCubeSceneObject cubeSceneObjectTrue = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, true, futureTextureList);
        assertNotNull(cubeSceneObjectTrue);
        GVRCubeSceneObject cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTextureList);
        assertNotNull(cubeSceneObjectFalse);
        //Testing futurelist length != 6 (should throws IllegalArgumentException)
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));
        try {
            cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTextureList);
            fail("should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCubeConstructorFacingoutArrayFutureTextureSegmentNumber() {

        ArrayList<Future<GVRTexture>> futureTextureList = new ArrayList<Future<GVRTexture>>(
                6);
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.back)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.right)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.front)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.left)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.top)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));

        GVRCubeSceneObject cubeSceneObjectTrue = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, true, futureTextureList, 1);
        assertNotNull(cubeSceneObjectTrue);
        GVRCubeSceneObject cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTextureList, 1);
        assertNotNull(cubeSceneObjectFalse);
        //Testing futurelist length != 6 (should throws IllegalArgumentException)
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));
        try {
            cubeSceneObjectFalse = new GVRCubeSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTextureList, 1);
            fail("should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }
}