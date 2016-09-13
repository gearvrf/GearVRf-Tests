package org.gearvrf.debug;

import android.graphics.Color;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

/**
 * Created by j.elidelson on 5/19/2015.
 */
public class GVRConsoleTest extends ActivityInstrumentationGVRf {

    public void testConstructorNullEyeMode() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            assertNotNull("EyeMode was null: ", gvrconsole);
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
        /*catch (NoClassDefFoundError e) {
            //e.printStackTrace();
        }*/
    }

    public void testConstructorNullScene() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext, GVRConsole.EyeMode.BOTH_EYES,TestDefaultGVRViewManager.mGVRContext.getMainScene());
            assertNotNull("EyeMode was null: ", gvrconsole);
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
        /*catch (NoClassDefFoundError e) {
            //e.printStackTrace();
        }*/
    }


    public void ignoretestWriteLine() {

        int i = 461012;
        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.writeLine("The value of i issssssssssssssssssssssssssssssssssss: %d%n", i);
            //gvrconsole.writeLine("The value of i issssssssssssssssssssssssssssssssssss: %d%n", i);
            //gvrconsole.writeLine("The value of i issssssssssssssssssssssssssssssssssss: %d%n", i);
            //gvrconsole.writeLine("The value of i issssssssssssssssssssssssssssssssssss: %d%n", i);
            //gvrconsole.writeLine("The value of i issssssssssssssssssssssssssssssssssss: %d%n", i);
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }

    public void testTextColor() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.setTextColor(Color.BLUE);
            assertEquals(Color.BLUE,gvrconsole.getTextColor());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }

    public void testSize() {

        float size;
        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            size=gvrconsole.getTextSize();
            gvrconsole.setTextSize(size);
            assertEquals(size,gvrconsole.getTextSize());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }


    public void testEyeMode() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.setEyeMode(GVRConsole.EyeMode.NEITHER_EYE);
            assertEquals(GVRConsole.EyeMode.NEITHER_EYE,gvrconsole.getEyeMode());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }

    public void testClear() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.clear();
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }


    public void testOffSet() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.setXOffset(1.0f);
            assertEquals(1.0f,gvrconsole.getXOffset());
            gvrconsole.setYOffset(1.0f);
            assertEquals(1.0f,gvrconsole.getYOffset());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }


    public void testCanvas() {

        try {
            GVRConsole gvrconsole;
            gvrconsole = new GVRConsole(TestDefaultGVRViewManager.mGVRContext,null);
            gvrconsole.setCanvasWidthHeight(1, 2);
            assertEquals(1,gvrconsole.getCanvasWidth());
            assertEquals(2,gvrconsole.getCanvasHeight());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }
}
