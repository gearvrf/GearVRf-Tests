package org.gearvrf.utility;

import android.content.Context;
import android.os.Environment;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.tests.R;
import org.gearvrf.ActivityInstrumentationGVRf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

/**
 * Created by j.elidelson on 6/9/2015.
 */
public class TextFileTest extends ActivityInstrumentationGVRf {

    public TextFileTest() {
        super(GVRTestActivity.class);
    }

    public void testreadTextFileEmptyString(){

        String text=null;
        text = TextFile.readTextFile("");
        assertNull(text);
    }

    public void ignoretestreadTextFileString(){

        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/encoding.txt";
        String text=null;
        text = TextFile.readTextFile(resourcePath);
        assertNotNull(text);
    }

    public void ignoretestreadTextFileFile(){

        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/encoding.txt";
        File file = new File(resourcePath);
        String text=null;
        text = TextFile.readTextFile(file);
        assertNotNull(text);
    }

    public void ignoretestreadTextFileResourceId(){

        String text;
        Context applicationContext = mActivity.getApplicationContext();
        text = TextFile.readTextFile(applicationContext, R.raw.test2);
        assertEquals("ABCDEF",text);
    }

    public void ignoretestreadTextinputStream(){

        String text=null;
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/encoding.txt";
        try {
            InputStream inputstream = new FileInputStream(resourcePath);
            text = TextFile.readTextFile(inputstream);
            assertNotNull(text);
        }catch (FileNotFoundException e){}
    }

    public void ignoretestreadTextBMPfile(){

        String text=null;
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/encoding.txt";
        text = TextFile.readTextFile(resourcePath);
        assertNotNull(text);
    }


    public void ignoretestreadBigTxt(){

        String text=null;
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/BigTextFileCorrupted.txt";
        try {
            InputStream inputstream = new FileInputStream(resourcePath);
            text = TextFile.readTextFile(inputstream);
            assertNotNull(text);
        }catch (FileNotFoundException e){}
    }


    public void ignoretestFileExtension(){

        String text=null;
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/encoding.txt";
        text = TextFile.readTextFile(resourcePath);
        assertEquals("/storage/emulated/0/Documents/encoding",FileNameUtils.getBaseName(resourcePath));
        assertEquals(null, FileNameUtils.getBaseName(null));
        assertEquals("txt", FileNameUtils.getExtension(resourcePath));
        assertEquals(null, FileNameUtils.getExtension(null));
        assertEquals(null, FileNameUtils.getExtension("test"));
        assertNotNull(text);
    }


}
