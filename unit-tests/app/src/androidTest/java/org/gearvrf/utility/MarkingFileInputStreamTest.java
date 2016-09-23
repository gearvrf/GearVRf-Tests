package org.gearvrf.utility;

import android.os.Environment;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.utility.MarkingFileInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by j.elidelson on 6/8/2015.
 */
public class MarkingFileInputStreamTest extends ActivityInstrumentationGVRf {

    public MarkingFileInputStreamTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructorA(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(resourcePath);
            assertNotNull("Resource was null: ", markingFileInputStreamTest);
        }catch (FileNotFoundException e){}
    }

    public void testConstructorAWithEmptyPath(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream("");
            assertNotNull("Resource path was empty: ", markingFileInputStreamTest);
        }catch (FileNotFoundException e){}
    }

    public void testConstructorAWithNullPath(){
        String resourcePath = null;
        try {
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(resourcePath);
            assertNotNull("Resource path was empty: ", markingFileInputStreamTest);
        }
        catch (FileNotFoundException e){}
        catch (NullPointerException e){}
    }


    public void testConstructorB(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            File file = new File(resourcePath);
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(file);
            assertNotNull("Resource was null: ", markingFileInputStreamTest);
        }catch (FileNotFoundException e){}
    }

    public void testConstructorBNullPath(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            File file = null;
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(file);
            assertNotNull("Resource path was empty: ", markingFileInputStreamTest);
        }
        catch (FileNotFoundException e){}
        catch (NullPointerException e){}
    }

    public void testResetMethod(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            File file = new File(resourcePath);
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(file);
            markingFileInputStreamTest.markSupported();
            markingFileInputStreamTest.mark(-100);
            markingFileInputStreamTest.reset();
            assertNotNull("Resource path was empty: ", markingFileInputStreamTest);
        }
        catch (IOException e){}
    }

    public void testResetMethod2(){
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/home.png";
        try {
            File file = new File(resourcePath);
            MarkingFileInputStream markingFileInputStreamTest = new MarkingFileInputStream(file);
            markingFileInputStreamTest.markSupported();
            markingFileInputStreamTest.mark(10000);
            markingFileInputStreamTest.reset();
            assertNotNull("Resource path was empty: ", markingFileInputStreamTest);
        }
        catch (IOException e){}
    }
}
