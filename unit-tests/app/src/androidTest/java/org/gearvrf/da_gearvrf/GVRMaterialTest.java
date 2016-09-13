package org.gearvrf.da_gearvrf;

import android.graphics.Color;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.misc.ColorShader;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.misc.ReflectionShader;
import org.gearvrf.ActivityInstrumentationGVRf;

import java.text.DecimalFormat;
import java.util.concurrent.Future;

/**
 * Created by santhyago on 2/27/15.
 */
public class GVRMaterialTest extends ActivityInstrumentationGVRf {
    private static final float UNPICKED_COLOR_R = 0.7f;
    private static final float UNPICKED_COLOR_G = 0.7f;
    private static final float UNPICKED_COLOR_B = 0.7f;
    private static final float UNPICKED_COLOR_A = 1.0f;

    private ColorShader mColorShader = null;
    private float mWidth = 1.0f;
    private float mHeight = 1.0f;


    public void ignoretestGetTexture() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);

        GVRTexture m360 = TestDefaultGVRViewManager.mGVRContext.loadTexture("env.jpg");
        //Testing GVRMaterial constructor using GVRContext and a Long parameter
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setTexture(ReflectionShader.TEXTURE_KEY, m360);
        material.setVec4(ColorShader.COLOR_KEY,
                UNPICKED_COLOR_R,
                UNPICKED_COLOR_G,
                UNPICKED_COLOR_B,
                UNPICKED_COLOR_A);
        GVRSceneObject board = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext, mWidth, mHeight);
        board.getRenderData().setMaterial(material);

        assertEquals(m360, material.getTexture(ReflectionShader.TEXTURE_KEY));
        assertNull(material.getTexture("0"));
    }

    public void ignoretestSetColorInt() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setVec4(ColorShader.COLOR_KEY,
                UNPICKED_COLOR_R,
                UNPICKED_COLOR_G,
                UNPICKED_COLOR_B,
                UNPICKED_COLOR_A);
        GVRSceneObject board = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext, mWidth, mHeight);
        board.getRenderData().setMaterial(material);

        DecimalFormat fourPlaces = new DecimalFormat("#.####");
        material.setColor(100000);

        float fourPlacesDecimal = Float.valueOf(fourPlaces.format(material.getColor()[0]));
        assertEquals(0.0039f, fourPlacesDecimal);
    }

    public void ignoreGetVec2WithAbsentKey() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        assertNull(material.getVec2("radio_r"));
    }

    public void ignoreGetVec3WithAbsentKey() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        assertNull(material.getVec3("radio_r"));
    }

    public void ignoreGetVec4WithAbsentKey() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        assertNull(material.getVec4("radio_r"));
    }

    public void testGetVec4() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setVec4(ColorShader.COLOR_KEY,
                UNPICKED_COLOR_R,
                UNPICKED_COLOR_G,
                UNPICKED_COLOR_B,
                UNPICKED_COLOR_A);
        material.getVec4(ColorShader.COLOR_KEY);
    }

    public void testGetVec3() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setVec3(ColorShader.COLOR_KEY,
                UNPICKED_COLOR_R,
                UNPICKED_COLOR_G,
                UNPICKED_COLOR_B);
        material.getVec3(ColorShader.COLOR_KEY);
    }

    public void testGetVec2() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setVec2(ColorShader.COLOR_KEY,
                UNPICKED_COLOR_R,
                UNPICKED_COLOR_G);
        assertNotNull(material.getVec2(ColorShader.COLOR_KEY));
    }

    public void testSetMat4Material() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setMat4("radio_r", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void testSetColorMaterial() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        float[] rgb = {0.0f, 0.0f, 0.0f};
        material.setColor(rgb[0], rgb[1], rgb[0]);
        assertNotNull(material.getColor());
    }

    public void testGetRGBColor() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        float[] rgb = {0.0f, 0.0f, 0.0f};
        material.setColor(rgb[0], rgb[1], rgb[0]);
        assertNotNull(material.getRgbColor());
    }

    public void testGetOpacity() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        material.setOpacity(1.0f);
        assertEquals(material.getOpacity(), 1.0f, 0);
    }

    public void ignoretestSetMainTexture() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());
        GVRTexture m360 = TestDefaultGVRViewManager.mGVRContext.loadTexture("sea_env.jpg");
        material.setMainTexture(m360);
        assertNotNull(material.getMainTexture());
    }

    public void testGetShaderType() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());

        material.setShaderType(GVRMaterial.GVRShaderType.OESHorizontalStereo.ID);
        assertEquals(GVRMaterial.GVRShaderType.OESHorizontalStereo.ID, material.getShaderType());
    }

    public void testSetColorShaderNull() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);

        try {
            new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, null);
            fail();

        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void ignoretestSetContextNull() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        try {
            new GVRMaterial(null, mColorShader.getShaderId());
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * Created by elidelson on 9/01/15.
     */

    public void testSetColor(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setColor(Color.BLUE);
    }


    public void testSetGetTexture(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.drawable.gearvr_logo));
        gvrMaterial.setTexture(ReflectionShader.TEXTURE_KEY, futureTexture);
        assertEquals(null, gvrMaterial.getTexture(ReflectionShader.TEXTURE_KEY));
    }

    public void testGetShaderType2() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());

        material.setShaderType(GVRMaterial.GVRShaderType.UnlitHorizontalStereo.ID);
        assertEquals(GVRMaterial.GVRShaderType.UnlitHorizontalStereo.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.UnlitVerticalStereo.ID);
        assertEquals(GVRMaterial.GVRShaderType.UnlitVerticalStereo.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.OES.ID);
        assertEquals(GVRMaterial.GVRShaderType.OES.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.OESHorizontalStereo.ID);
        assertEquals(GVRMaterial.GVRShaderType.OESHorizontalStereo.ID,material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.OESVerticalStereo.ID);
        assertEquals(GVRMaterial.GVRShaderType.OESVerticalStereo.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.Cubemap.ID);
        assertEquals(GVRMaterial.GVRShaderType.Cubemap.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.CubemapReflection.ID);
        assertEquals(GVRMaterial.GVRShaderType.CubemapReflection.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.Texture.ID);
        assertEquals(GVRMaterial.GVRShaderType.Texture.ID, material.getShaderType());
        material.setShaderType(GVRMaterial.GVRShaderType.ExternalRenderer.ID);
        assertEquals(GVRMaterial.GVRShaderType.ExternalRenderer.ID, material.getShaderType());
   }

    /*
    DEPRECATED - Assimp uses GVRPhongShader now

    public void testAssimp() {
        mColorShader = new ColorShader(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial material = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext, mColorShader.getShaderId());

        material.setShaderType(GVRMaterial.GVRShaderType.Assimp.ID);
        assertEquals(GVRMaterial.GVRShaderType.Assimp.ID, material.getShaderType());
        assertEquals(2, GVRMaterial.GVRShaderType.Assimp.setBit(GVRMaterial.GVRShaderType.Assimp.AS_DIFFUSE_TEXTURE, 1));
        assertEquals(3, GVRMaterial.GVRShaderType.Assimp.setBit(GVRMaterial.GVRShaderType.Assimp.AS_SPECULAR_TEXTURE,1));
        assertEquals(false, GVRMaterial.GVRShaderType.Assimp.isSet(GVRMaterial.GVRShaderType.Assimp.AS_DIFFUSE_TEXTURE, 1));
        assertEquals(0, GVRMaterial.GVRShaderType.Assimp.clearBit(GVRMaterial.GVRShaderType.Assimp.AS_DIFFUSE_TEXTURE, 1));
    }
    */

    public void testMainTexture(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        Future<GVRTexture> futureTexture = TestDefaultGVRViewManager.mGVRContext.loadFutureTexture(new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext,R.drawable.gearvr_logo));
        gvrMaterial.setMainTexture(futureTexture);
        //assertEquals(null,gvrMaterial.getMainTexture());
    }

    public void testAmbientColor(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setAmbientColor(1.0f, 1.0f, 1.0f, 1.0f);
        float ambientcolor[] = gvrMaterial.getAmbientColor();
        assertEquals(4,ambientcolor.length);
    }


    public void testSpecularColor(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setSpecularColor(1.0f, 1.0f, 1.0f, 1.0f);
        float specularcolor[] = gvrMaterial.getSpecularColor();
        assertEquals(4,specularcolor.length);
    }

    public void testDiffuseColorColor(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setDiffuseColor(1.0f, 1.0f, 1.0f, 1.0f);
        float specularcolor[] = gvrMaterial.getDiffuseColor();
        assertEquals(4,specularcolor.length);
    }

    public void testSpecularExponent(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setSpecularExponent(1.0f);
        float specularcolor = gvrMaterial.getSpecularExponent();
        assertEquals(1.0f,specularcolor);
    }

    /*
    DEPRECATED

    public void testGetSetShaderFeatureSet(){

        GVRMaterial gvrMaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        gvrMaterial.setShaderFeatureSet(GVRMaterial.GVRShaderType.Assimp.AS_DIFFUSE_TEXTURE);
        assertEquals(GVRMaterial.GVRShaderType.Assimp.AS_DIFFUSE_TEXTURE,gvrMaterial.getShaderFeatureSet());
    }
    */

}
