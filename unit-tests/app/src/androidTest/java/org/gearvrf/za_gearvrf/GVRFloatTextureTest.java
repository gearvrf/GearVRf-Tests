package org.gearvrf.za_gearvrf;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import java.lang.IllegalArgumentException;
import org.gearvrf.GVRFloatTexture;

/**
 * Created by j.elidelson on 5/20/2015.
 */
public class GVRFloatTextureTest extends ActivityInstrumentationGVRf {

    private float pixels[] = {
            0.0f, 0.0f, 0.0f,   1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,   0.0f, 0.0f, 0.0f
    };

    private float pixels2[] = {
            1.0f, 1.0f, 1.0f,   0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,   1.0f, 1.0f, 1.0f
    };

    //*************************
    //*** Constructor tests ***
    //*************************
    public void testFloatTextureConstructorWidthZero() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,0,1,pixels);
            assertNull("Width was 0: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureConstructorWidthNegative() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,-1,1,pixels);
            assertNull("Width was negative: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureConstructorHeightZero() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,0,pixels);
            assertNull("Height was negative: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureConstructorHeightNegative() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,0,pixels);
            assertNull("Height was 0: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureConstructorDataNull() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,2,2,null);
            assertNull("Data was null: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureConstructorDataLength() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,10,10,pixels);
            if(gvrFloatTexture==null) {
                assertNull("data.length < height * width * 2: ", gvrFloatTexture);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    //*************************
    //*** Method Update     ***
    //*************************

    public void testFloatTextureUpdateWidthZero() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(0,1,pixels2);
            assertNull("update method with Widht zero: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureUpdateWidthNegative() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(-1,1,pixels2);
            assertNull("update method with Widht negative: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureUpdateHeightZero() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(1,0,pixels2);
            assertNull("update method with Height zero: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureUpdateHeightNegative() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(1,-1,pixels2);
            assertNull("update method with Height negative: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureUpdateDataNull() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(1,1,null);
            assertNull("update method with Widht zero: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void testFloatTextureUpdateDataLength() {
        try {
            GVRFloatTexture gvrFloatTexture;
            gvrFloatTexture = new GVRFloatTexture(TestDefaultGVRViewManager.mGVRContext,1,1,pixels);
            gvrFloatTexture.update(10,10,pixels2);
            assertNull("update method with Widht zero: ", gvrFloatTexture);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}
