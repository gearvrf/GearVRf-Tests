package org.gearvrf.scene_object;

import android.graphics.Color;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
/*import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.*;
*/

/**
 * Created by j.elidelson on 6/1/2015.
 */
public class GVRTextViewSceneObjectTest extends ActivityInstrumentationGVRf {
    public GVRTextViewSceneObjectTest() {
        super(GVRTestActivity.class);
    }

    //@BeforeClass
    //public static void onceExecutedBeforeAll() {
    //    System.out.println("@BeforeClass: onceExecutedBeforeAll");
    //}

    //*** CONSTRUCTOR TYPE A ***
    public void testConstructorTypeA(){
        GVRTextViewSceneObject textViewSceneObjectA;

        textViewSceneObjectA = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(textViewSceneObjectA);
    }

    public void testConstructorTypeAWithNullContext() {
        GVRTextViewSceneObject textViewSceneObjectA;

        try {
            textViewSceneObjectA = new GVRTextViewSceneObject(null);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }

    /*
    DEPRECATED
    public void testConstructorTypeAWithNullActivity() {
        GVRTextViewSceneObject textViewSceneObjectA;
        try {
            textViewSceneObjectA = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext, null);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }
    */

    //*** CONSTRUCTOR TYPE B ***
    public void testConstructorTypeB(){
        GVRTextViewSceneObject textViewSceneObjectB;
        CharSequence msg = "ABC";

        textViewSceneObjectB = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,msg);
        assertNotNull(textViewSceneObjectB);
    }

    public void testConstructorTypeBWithNullContext(){
        GVRTextViewSceneObject textViewSceneObjectB;
        CharSequence msg = "ABC";

        try {
            textViewSceneObjectB = new GVRTextViewSceneObject(null, msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }


    //*** CONSTRUCTOR TYPE C ***
    public void testConstructorTypeC(){
        GVRTextViewSceneObject textViewSceneObjectC;
        CharSequence msg = "ABC";

        textViewSceneObjectC = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext, 8.0f,4.0f,msg);
        assertNotNull(textViewSceneObjectC);
    }

    public void testConstructorTypeCWithNullContext(){
        GVRTextViewSceneObject textViewSceneObjectC;
        CharSequence msg = "ABC";

        try {
            textViewSceneObjectC = new GVRTextViewSceneObject(null,8.0f,4.0f,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }

    /*
    DEPRECATED
    public void testConstructorTypeCWithNullActivity(){
        GVRTextViewSceneObject textViewSceneObjectC;
        CharSequence msg = "ABC";

        try {
            textViewSceneObjectC = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                    null,8.0f,4.0f,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }
    */


    //*** CONSTRUCTOR TYPE D ***
    public void testConstructorTypeD(){
        GVRTextViewSceneObject textViewSceneObjectD;
        CharSequence msg = "ABC";

        textViewSceneObjectD = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,8.0f,4.0f,msg);
        assertNotNull(textViewSceneObjectD);
    }

    public void testConstructorTypeDWithNullContext(){
        GVRTextViewSceneObject textViewSceneObjectD;
        CharSequence msg = "ABC";

        try {
            textViewSceneObjectD = new GVRTextViewSceneObject(null,8.0f,4.0f,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }

    /*
    DEPRECATED
    public void testConstructorTypeDWithNullActivity(){
        GVRTextViewSceneObject textViewSceneObjectD;
        CharSequence msg = "ABC";

        try {
            textViewSceneObjectD = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                    null,8.0f,4.0f,10,10,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }



    //*** CONSTRUCTOR TYPE E ***
    public void testConstructorTypeE(){
        GVRTextViewSceneObject textViewSceneObjectE;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        textViewSceneObjectE = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                sphereSceneObject.getRenderData().getMesh(),msg);
        assertNotNull(textViewSceneObjectE);
    }

    public void testConstructorTypeEWithNullContext(){
        GVRTextViewSceneObject textViewSceneObjectE;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        try {
            textViewSceneObjectE = new GVRTextViewSceneObject(null,
                    sphereSceneObject.getRenderData().getMesh(), msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }


    public void testConstructorTypeEWithNullActivity(){
        GVRTextViewSceneObject textViewSceneObjectE;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        try {
            textViewSceneObjectE = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                    sphereSceneObject.getRenderData().getMesh(),
                    null,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }


    //*** CONSTRUCTOR TYPE F ***
    public void testConstructorTypeF(){
        GVRTextViewSceneObject textViewSceneObjectF;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        textViewSceneObjectF = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                sphereSceneObject.getRenderData().getMesh(),
                TestDefaultGVRViewManager.mGVRContext.getActivity(),10,10,msg);
        assertNotNull(textViewSceneObjectF);
    }

    public void testConstructorTypeFWithNullContext(){
        GVRTextViewSceneObject textViewSceneObjectF;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        try {
            textViewSceneObjectF = new GVRTextViewSceneObject(null,
                    sphereSceneObject.getRenderData().getMesh(),
                    TestDefaultGVRViewManager.mGVRContext.getActivity(),10,10,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }

    public void testConstructorTypeFWithNullActivity(){
        GVRTextViewSceneObject textViewSceneObjectF;
        CharSequence msg = "ABC";
        GVRSphereSceneObject sphereSceneObject = new GVRSphereSceneObject(TestDefaultGVRViewManager.mGVRContext);

        try {
            textViewSceneObjectF = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext,
                    sphereSceneObject.getRenderData().getMesh(),
                    null,10,10,msg);
            fail("Should have raised an NullPointerException or IllegalArgumentException");
        }
        catch (NullPointerException e){
        }
        catch (IllegalArgumentException e){
        }
    }
   */

    public void testSetGet(){
        GVRTextViewSceneObject textViewSceneObjectA;

        textViewSceneObjectA = new GVRTextViewSceneObject(TestDefaultGVRViewManager.mGVRContext);
        textViewSceneObjectA.setText("testabc");
        assertEquals("testabc", textViewSceneObjectA.getTextString());
        textViewSceneObjectA.setBackgroundColor(Color.WHITE);
        textViewSceneObjectA.setBackGround(null);
        assertNull(textViewSceneObjectA.getBackGround());
        textViewSceneObjectA.setGravity(textViewSceneObjectA.getGravity() * 1);
        assertEquals(8388659, textViewSceneObjectA.getGravity());
        textViewSceneObjectA.setTextSize(textViewSceneObjectA.getTextSize() * (1.0f));
        assertEquals(224.0f, textViewSceneObjectA.getTextSize());
        textViewSceneObjectA.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.LOW);
        assertEquals(GVRTextViewSceneObject.IntervalFrequency.LOW,textViewSceneObjectA.getRefreshFrequency());
        textViewSceneObjectA.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.MEDIUM);
        assertEquals(GVRTextViewSceneObject.IntervalFrequency.MEDIUM, textViewSceneObjectA.getRefreshFrequency());
        textViewSceneObjectA.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.HIGH);
        assertEquals(GVRTextViewSceneObject.IntervalFrequency.HIGH,textViewSceneObjectA.getRefreshFrequency());

    }

    //NOTE: parameters widht and height accepts negatives o zero values


    }
