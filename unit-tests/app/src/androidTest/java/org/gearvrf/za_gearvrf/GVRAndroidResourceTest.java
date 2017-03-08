package org.gearvrf.za_gearvrf;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import org.gearvrf.tests.R;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRAndroidResource.BitmapTextureCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by j.elidelson on 5/15/2015.
 */
public class GVRAndroidResourceTest extends ActivityInstrumentationGVRf {

    BitmapTextureCallback test2;

    /*
    CONSTRUCTORS Tests
    */
    public void ignoretestConstructorNullResource() {

        try {
            GVRAndroidResource gvrAndroidResource;
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, (String) null);
            assertNotNull("Resource was null: ", gvrAndroidResource);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void testConstructorNullContext() {
        try {
            Context ctx = null;
            GVRAndroidResource gvrAndroidResource;
            gvrAndroidResource = new GVRAndroidResource(ctx, R.raw.sample_20140509_r);
            assertNotNull("Context was null: ", gvrAndroidResource);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void testConstructorStringPath() {
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/home.png";
        try {
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(resourcePath);
            assertNotNull("Resource is null: " + resourcePath, gvrAndroidResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void testConstructorFile() {
        String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/home.png";
        try {
            File file = new File(resourcePath);
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(file);
            assertNotNull("Resource is null: " + resourcePath, gvrAndroidResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void testConstructor_GVRContext_res() {
        try {
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.sample_20140509_r);
            assertNotNull("R.raw.Bitmap was not loaded: ", gvrAndroidResource);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void testConstructor_AndroidContext_res() {
        GVRAndroidResource gvrAndroidResource;
        try {
            Context applicationContext = mActivity.getApplicationContext();
            if (applicationContext != null) {
                gvrAndroidResource = new GVRAndroidResource(applicationContext, R.raw.sample_20140509_r);
                assertNotNull("R.raw.Bitmap was not loaded: ", gvrAndroidResource);
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void testConstructor_GVRContext_asset() {
        AssetManager assetMgr = TestDefaultGVRViewManager.mGVRContext.getContext().getAssets();

        try {
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "coke.jpg");
            assertNotNull("R.raw.Bitmap was not loaded: ", gvrAndroidResource);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ignoretestConstructor_AndroidContext_asset() {
        GVRAndroidResource gvrAndroidResource;
        try {
            Context applicationContext = getActivity().getApplicationContext();
            if (applicationContext != null) {
                gvrAndroidResource = new GVRAndroidResource(applicationContext, R.raw.sample_20140509_r);
                assertNotNull("R.raw.Bitmap was not loaded: ", gvrAndroidResource);
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*
    METOHODS Tests
    */

    public void testGet_and_Close_Stream() {
        try {
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.test);
            InputStream inputstream = gvrAndroidResource.getStream();
            if (inputstream != null) {
                assertNotNull("GetStream method fail: ", inputstream);
                gvrAndroidResource.closeStream();
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }
/*
    GVRAndroidResource.mark & reset are private now

    public void testOthers() {
        try {
            AssetManager assets = getInstrumentation().getTargetContext().getAssets();
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.test);
            gvrAndroidResource.mark();
            gvrAndroidResource.reset();
            assertNotNull(gvrAndroidResource.toString());
            assertEquals(2131129462, gvrAndroidResource.hashCode());
            assertEquals("test.txt", gvrAndroidResource.getResourceFilename());
            GVRAndroidResource gvrAndroidResource2 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.raw.test);
            assertNotNull(gvrAndroidResource.equals(gvrAndroidResource));
            assertNotNull(gvrAndroidResource.equals(null));
            assertNotNull(gvrAndroidResource.equals(gvrAndroidResource2));
            String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/home.png";

            try {
                GVRAndroidResource gvrAndroidResource3 = new GVRAndroidResource(resourcePath);
                assertNotNull(gvrAndroidResource.equals(gvrAndroidResource3));
                assertNotNull(gvrAndroidResource3.getResourceFilename());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                GVRAndroidResource gvrAndroidResource4 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "coke.jpg");
                assertNotNull(gvrAndroidResource.equals(gvrAndroidResource4));
                assertNotNull(gvrAndroidResource4.getResourceFilename());
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            GVRAndroidResource gvrAndroidResource5=null;
            assertNotNull(gvrAndroidResource5.getResourceFilename());
            gvrAndroidResource5.closeStream();
            gvrAndroidResource5.reset();

            gvrAndroidResource.closeStream();
            gvrAndroidResource.reset();

        } catch (NullPointerException e) {e.printStackTrace();}
    }
    */
}