package org.gearvrf.jassimp;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRImportSettings;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by j.elidelson on 9/15/2015.
 */
public class AiMaterialTest extends ActivityInstrumentationGVRf {

    public AiMaterialTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor() {
        AiMaterial aiMaterial = new AiMaterial();
    }

    public void testLoadingObjects() {

        try {
            GVRSceneObject astroBoyModel = TestDefaultGVRViewManager.mGVRContext.getAssimpModel("astro_boy.dae");
            GVRSceneObject benchModel = TestDefaultGVRViewManager.mGVRContext.getAssimpModel("bench.dae", GVRImportSettings.getRecommendedSettings());
            GVRSceneObject obj3 = TestDefaultGVRViewManager.mGVRContext.getAssimpModel("donovan_kick_fail.fbx");
            GVRSceneObject obj4 = TestDefaultGVRViewManager.mGVRContext.getAssimpModel("bunny.obj");
        } catch (IOException e) {
        }
    }

    public void testTextture() {
        AiMaterial aiMaterial = new AiMaterial();
        try {
            aiMaterial.setDefault(AiMaterial.PropertyKey.COLOR_DIFFUSE, AiMaterial.PropertyKey.COLOR_EMISSIVE);
            fail("should throws exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            aiMaterial.setDefault(AiMaterial.PropertyKey.COLOR_DIFFUSE, null);
            fail("should throws exception");fail("should throws exception");
        } catch (IllegalArgumentException e) {
        }

        aiMaterial.setDefault(AiMaterial.PropertyKey.TEX_FILE, "tiny_texture");
        List<AiMaterial.Property> propertyList = aiMaterial.getProperties();
        assertNotNull(propertyList);

        AiTextureInfo aiTextureInfo = new AiTextureInfo(AiTextureType.DIFFUSE,1,"",0,1.0f,AiTextureOp.ADD,AiTextureMapMode.CLAMP,AiTextureMapMode.CLAMP,AiTextureMapMode.CLAMP);
        assertEquals(AiTextureType.DIFFUSE,aiTextureInfo.getType());
        assertEquals("", aiTextureInfo.getFile());
        assertEquals(1.0f,aiTextureInfo.getBlend());
        assertEquals(1,aiTextureInfo.getIndex());
        assertEquals(AiTextureOp.ADD,aiTextureInfo.getTextureOp());
        assertEquals(0,aiTextureInfo.getUVIndex());
        assertEquals(AiTextureOp.ADD,aiTextureInfo.getTextureOp());
        assertEquals(AiTextureMapMode.CLAMP,aiTextureInfo.getTextureMapModeU());
        assertEquals(AiTextureMapMode.CLAMP,aiTextureInfo.getTextureMapModeV());
        assertEquals(AiTextureMapMode.CLAMP, aiTextureInfo.getTextureMapModeW());

        //aiMaterial.getTextureInfo(AiTextureType.DIFFUSE,1);
    }

    public void testColor() {
        AiMaterial aiMaterial = new AiMaterial();
        AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        assertNotNull(aiMaterial.getReflectiveColor(aiBuiltInWrapperProvider));
        assertNotNull(aiMaterial.getTransparentColor(aiBuiltInWrapperProvider));
        assertNotNull(aiMaterial.getDiffuseColor(aiBuiltInWrapperProvider));
    }

    public void testGeneral() {
        AiMaterial aiMaterial = new AiMaterial();
        assertNotNull(aiMaterial.getTwoSided());
        assertNotNull(aiMaterial.getWireframe());
        assertNotNull(aiMaterial.getBumpScaling());
        assertNotNull(aiMaterial.getShininess());
        assertNotNull(aiMaterial.getReflectivity());
        assertNotNull(aiMaterial.getShininessStrength());
        assertNotNull(aiMaterial.getRefractIndex());
        assertNotNull(aiMaterial.getName());
        assertNotNull(aiMaterial.getGlobalBackgroundImage());
        assertNotNull(aiMaterial.getShadingMode());
        assertNotNull(aiMaterial.getBlendMode());

        assertEquals(AiPrimitiveType.LINE, AiPrimitiveType.LINE);
        assertEquals(AiPrimitiveType.POINT, AiPrimitiveType.POINT);
        assertEquals(AiPrimitiveType.POLYGON, AiPrimitiveType.POLYGON);
        assertEquals(AiPrimitiveType.TRIANGLE, AiPrimitiveType.TRIANGLE);

        assertEquals(AiSceneFlag.INCOMPLETE, AiSceneFlag.INCOMPLETE);
        assertEquals(AiSceneFlag.NON_VERBOSE_FORMAT, AiSceneFlag.NON_VERBOSE_FORMAT);
        assertEquals(AiSceneFlag.TERRAIN, AiSceneFlag.TERRAIN);
        assertEquals(AiSceneFlag.VALIDATED, AiSceneFlag.VALIDATED);
        assertEquals(AiSceneFlag.VALIDATION_WARNING, AiSceneFlag.VALIDATION_WARNING);

    }
}