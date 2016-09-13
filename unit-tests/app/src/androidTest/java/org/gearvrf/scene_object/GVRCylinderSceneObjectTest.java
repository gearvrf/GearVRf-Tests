package org.gearvrf.scene_object;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.scene_objects.GVRCylinderSceneObject;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Created by j.elidelson on 5/29/2015.
 */
public class GVRCylinderSceneObjectTest extends ActivityInstrumentationGVRf {

    //****************
    //*** Cylinder ***
    //****************
    public void testCylinderConstructor() {

        GVRCylinderSceneObject cylinderSceneObject = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(cylinderSceneObject);
    }

    public void ignoretestCylinderNullConstructor() {

        try {
            GVRCylinderSceneObject cylinderSceneObject = new GVRCylinderSceneObject(null);
            assertNull(cylinderSceneObject);
        } catch (NullPointerException e) {
            assertEquals(null, null);
        }
    }

    public void testCylinderConstructorWithParameters() {

        GVRCylinderSceneObject cylinderSceneObject =
                new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f,0.5f,1.0f,2,36,true);
        assertNotNull(cylinderSceneObject);
    }


    public void testCylinderConstructorNegativeRadius() {

        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, -1.0f, -1.0f, 1.0f, 2, 36,false);
            assertNull(cylinderSceneObject);
        }catch (IllegalArgumentException e){}
    }

    public void testCylinderConstructorZeroRadius() {

        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.0f, -1.0f, 1.0f, 2, 36,false);
            assertNull(cylinderSceneObject);
        }catch (IllegalArgumentException e){}
    }


    public void testCylinderConstructorZeroHeight() {

        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 0, 2, 36,true);
            assertNull(cylinderSceneObject);
        }catch (IllegalArgumentException e){}
    }

    public void testCylinderConstructorZeroStack() {

        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 0, 36,false);
            assertNull(cylinderSceneObject);
        }catch (IllegalArgumentException e){}
    }


    public void testCylinderConstructorZeroSlice() {

        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 0,true);
            assertNull(cylinderSceneObject);
        }catch (IllegalArgumentException e){}
    }

    public void testCylinderConstructorFacingout() { //Created by j.elidelson on 8/14/2015.

        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,true);
        assertNotNull(cylinderSceneObjectTrue);
        GVRCylinderSceneObject cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,false);
        assertNotNull(cylinderSceneObjectFalse);
    }

    public void testCylinderConstructorMaterial() { //Created by j.elidelson on 8/14/2015.

        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,true,material);
        assertNotNull(cylinderSceneObjectTrue);
    }

    public void testCylinderConstructorMaterial2() { //Created by j.elidelson on 8/14/2015.

        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.0f, 0.5f, 1.0f, 2, 36,true,material);
        assertNotNull(cylinderSceneObjectTrue);
        cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 4, 8,true,material,2,8);
        assertNotNull(cylinderSceneObjectTrue);
    }


    public void testCylinderConstructorFacingoutFutureTexture() {

        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.drawable.bottom));
        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,true,futureTexture);
        assertNotNull(cylinderSceneObjectTrue);
        GVRCylinderSceneObject cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,false,futureTexture);
        assertNotNull(cylinderSceneObjectFalse);
    }

    public void testCylinderConstructorFacingoutArrayFutureTexture() {

        ArrayList<Future<GVRTexture>> futureTextureList = new ArrayList<Future<GVRTexture>>(
                3);
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.back)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.right)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.front)));

        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,true,futureTextureList);
        assertNotNull(cylinderSceneObjectTrue);
        GVRCylinderSceneObject cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,false,futureTextureList);
        try{
            cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.0f, 0.0f, 1.0f, 2, 36,true,futureTextureList);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 0,true,futureTextureList);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        //Testing futurelist length != 3 (should throws IllegalArgumentException)
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));
        try {
            cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, false, futureTextureList);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }


    public void testCylinderConstructorFacingoutArrayFutureTexture2() {

        ArrayList<Future<GVRTexture>> futureTextureList = new ArrayList<Future<GVRTexture>>(
                3);
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.back)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.right)));
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.front)));

        GVRCylinderSceneObject cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 8,true,futureTextureList,1,2);
        assertNotNull(cylinderSceneObjectTrue);
        cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 36,true,futureTextureList,1,1);
        try{
            cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 36,true,futureTextureList,3,1);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.0f, 0.0f, 1.0f, 2, 36,true,futureTextureList,1,1);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try{
            cylinderSceneObjectTrue = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 0,true,futureTextureList,1,1);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        GVRCylinderSceneObject cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 36,false,futureTextureList);
        assertNotNull(cylinderSceneObjectFalse);
        try{
            cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext,0.5f, 0.5f, 1.0f, 2, 36,true,futureTextureList,1,5);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        //Testing futurelist length != 3 (should throws IllegalArgumentException)
        futureTextureList.add(TestDefaultGVRViewManager.mGVRContext
                .loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,
                        R.drawable.bottom)));
        try {
            cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 36,false, futureTextureList);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try {
            cylinderSceneObjectFalse = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 36,false, futureTextureList,1,1);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }


    public void testCylinderConstructorWithNegativeParametersMaterial() {

        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.0f, 0.0f, 1.0f, 2, 36, true, material);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try {
            GVRCylinderSceneObject cylinderSceneObject =
                    new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 0, true, material);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testCylinderConstructorMaterial_StackNum_SliceNum() {

        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRCylinderSceneObject cylinderSceneObject =
                new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 36, true, material,2,4);
        assertNotNull(cylinderSceneObject);
        cylinderSceneObject =
                new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 2, 36, false, material,2,6);
        assertNotNull(cylinderSceneObject);
        try {
            cylinderSceneObject = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.0f, 0.0f, 1.0f, 5, 36, true, material, 2, 6);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try {
            cylinderSceneObject = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.5f, 1.0f, 5, 0, true, material, 2, 6);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try {
            cylinderSceneObject = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.0f, 0.5f, 1.0f, 5, 36, true, material, 2, 6);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
        try {
            cylinderSceneObject = new GVRCylinderSceneObject(TestDefaultGVRViewManager.mGVRContext, 0.5f, 0.0f, 1.0f, 2, 36, true, material, 2, 5);
            fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}

    }
}
