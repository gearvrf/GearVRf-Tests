package org.gearvrf.da_gearvrf;

import android.os.Environment;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPostEffect;
import org.gearvrf.GVRPostEffectShaderId;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.utility.Log;

import org.gearvrf.misc.CustomPostEffectShaderManager;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.BoundsValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Future;

/**
 * Created by d.schettini on 3/4/2015.
 */
public class GVRPostEffectTest extends ActivityInstrumentationGVRf {
    private final String TAG = GVRPostEffectTest.class.getSimpleName();

    private static final float DEFAULT_R = 1f;
    private static final float DEFAULT_G = 1f;
    private static final float DEFAULT_B = 1f;
    private static final float ANIM_DURATION = 1.5f;

    private static GVRContext mGVRContext = null;

    private CustomPostEffectShaderManager shaderManager;

    public GVRPostEffectTest() {
        super(GVRTestActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mGVRContext = TestDefaultGVRViewManager.mGVRContext;
        shaderManager = new CustomPostEffectShaderManager(mGVRContext);
    }

    public void testPostEffectConstructor(){
        Log.d(TAG, "Starting testPostEffectConstructor!");

        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        assertNotNull(postEffect);

    }

    public void testSetFloat() {
        Log.d(TAG, "Starting testSetFloat");

        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float value = 1.5f;
        String key = "ratio_r";
        postEffect.setFloat(key, value);
        assertEquals(value, postEffect.getFloat(key));

    }

    public void testFloatBounds(){
        Log.d(TAG, "Starting testFloatBounds");

        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        String key = "ratio_r";

        int size = BoundsValues.getFloatList().size();
        for (int i = 0; i<size; i++){
            postEffect.setFloat(key, BoundsValues.getFloatList().get(i));
            assertEquals(BoundsValues.getFloatList().get(i), postEffect.getFloat(key));
        }

    }

    public void testSizeVec3(){
        Log.d(TAG, "Starting testSizeVec3");

        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f, 0.200f};
        String key = "ratio_r";
        postEffect.setVec3(key, value[0], value[1], value[2]);
        float[] ratioR = postEffect.getVec3(key);

        assertEquals(value.length, ratioR.length);

    }


    public void testSetVec3(){
        Log.d(TAG, "Starting testSetVec3");

        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f, 0.200f};
        String key = "ratio_r";
        postEffect.setVec3(key, value[0], value[1], value[2]);
        float[] ratioR = postEffect.getVec3(key);

        for(int i=0; i<value.length; i++)
            assertEquals(value[i], ratioR[i]);


    }

    public void testSizeVec2(){
        Log.d(TAG, "Starting testSizeVec2");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f};
        String key = "ratio_r";
        postEffect.setVec2(key, value[0], value[1]);
        float[] ratioR = postEffect.getVec2(key);

        assertEquals(value.length, ratioR.length);

    }


    public void testSetVec2(){
        Log.d(TAG, "Starting testSetVec2");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f};
        String key = "ratio_r";
        postEffect.setVec2(key, value[0], value[1]);
        float[] ratioR = postEffect.getVec2(key);

        for(int i=0; i<value.length; i++)
            assertEquals(value[i], ratioR[i]);


    }

    public void testSizeVec4(){
        Log.d(TAG, "Starting testSizeVec4");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f, 0.200f, 0.070f};
        String key = "ratio_r";
        postEffect.setVec4(key, value[0], value[1], value[2], value[3]);
        float[] ratioR = postEffect.getVec4(key);

        assertEquals(value.length, ratioR.length);

    }


    public void testSetVec4(){
        Log.d(TAG, "Starting testSetVec4");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());

        float[] value = {0.395f, 0.789f, 0.200f, 0.070f};
        String key = "ratio_r";
        postEffect.setVec4(key, value[0], value[1], value[2], value[3]);
        float[] ratioR = postEffect.getVec4(key);

        for(int i=0; i<value.length; i++)
            assertEquals(value[i], ratioR[i]);


    }


    public void testSetMat4(){
        Log.d(TAG, "Starting testSetVec4");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        String key = "ratio_r";
        postEffect.setMat4(key, 1.0f,1.0f,1.0f,1.0f,2.0f,2.0f,2.0f,2.0f,3.0f,3.0f,3.0f,3.0f,4.0f,4.0f,4.0f,4.0f);
    }


    public void testTextureNull(){
        Log.d(TAG, "Starting testTextureNull");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        String resourcePath = Environment.getExternalStorageDirectory()+ "sea_env.jpg";
        try {
            File file = new File(resourcePath);
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(file);
            GVRTexture texture = mGVRContext.loadTexture(gvrAndroidResource);
            String key = "texture";

            postEffect.setTexture(key, texture);

            assertNotNull(postEffect.getTexture(key));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void testGetTexture(){
        Log.d(TAG, "Starting testGetTexture");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        String resourcePath = Environment.getExternalStorageDirectory()+ "sea_env.jpg";
        try {
            File file = new File(resourcePath);
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(file);
            GVRTexture texture = mGVRContext.loadTexture(gvrAndroidResource);
            String key = "texture";

            postEffect.setTexture(key, texture);

        assertEquals(texture, postEffect.getTexture(key));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void testMainTextureNull(){
        Log.d(TAG, "Starting testMainTextureNull");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        String resourcePath = Environment.getExternalStorageDirectory()+ "sea_env.jpg";
        try {
            File file = new File(resourcePath);
            GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(file);
            GVRTexture texture = mGVRContext.loadTexture(gvrAndroidResource);

            postEffect.setMainTexture(texture);

            assertNotNull(postEffect.getMainTexture());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void testGetMainTexture(){
        Log.d(TAG, "Starting testGetTexture");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, R.drawable.gearvr_logo));
        //gvrMaterial.setMainTexture(futureTexture);

        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        //String resourcePath = Environment.getExternalStorageDirectory()+ "sea_env.jpg";
        //String resourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/sea_env.jpg";
        //try {
            //File file = new File(resourcePath);
            //GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(file);
            //GVRTexture texture = mGVRContext.loadTexture(gvrAndroidResource);
            postEffect.setMainTexture(futureTexture);
            postEffect.getMainTexture();
            postEffect.getTexture("0");
            postEffect.setTexture("MAIN_TEXTURE",futureTexture);
            //postEffect.setMainTexture(postEffect.getTexture("MAIN_TEXTURE"));
        //} catch(){};

    }


    public void testGetAetShaderType(){
        Log.d(TAG, "Starting testGetTexture");
        if(mGVRContext == null) {
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        GVRPostEffectShaderId gvrPostEffectShaderId = postEffect.getShaderType();
        assertNotNull(gvrPostEffectShaderId);
        postEffect.setShaderType(gvrPostEffectShaderId);
    }


    public void testGVRPostEffectShaderType(){

        assertEquals("r",GVRPostEffect.GVRPostEffectShaderType.ColorBlend.R);
        assertEquals("g",GVRPostEffect.GVRPostEffectShaderType.ColorBlend.G);
        assertEquals("b",GVRPostEffect.GVRPostEffectShaderType.ColorBlend.B);
        assertEquals("factor",GVRPostEffect.GVRPostEffectShaderType.ColorBlend.FACTOR);
        assertNotNull(GVRPostEffect.GVRPostEffectShaderType.ColorBlend.ID);
        assertNotNull(GVRPostEffect.GVRPostEffectShaderType.HorizontalFlip.ID);
    }



//    public void testShaderTypeNull(){
//        Log.d(TAG, "Starting testShaderTypeNull");
//
//        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
//        GVRTexture texture = mGVRContext.loadTexture("sea_env.jpg");
//
//        postEffect.setShaderType(new GVRPostEffectShaderId);
//
//        postEffect.setMainTexture(texture);
//
//        assertNotNull(postEffect.getMainTexture());
//
//    }

}
