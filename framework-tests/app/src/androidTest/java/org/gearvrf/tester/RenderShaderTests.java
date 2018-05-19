package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRTexture;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class RenderShaderTests
{
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private GVRSceneMaker mSceneMaker;
    private boolean mDoCompare = true;
    private final int NUM_FRAMES = 8;

    public RenderShaderTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);


    @After
    public void tearDown()
    {
        GVRScene scene = gvrTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
        mSceneMaker = new GVRSceneMaker(gvrTestUtils);
    }

    private List<String> createNonTexturedMeshFormats() {
        final String type = "type: polygon";
        final String vertices = "vertices: [-0.5, 0.5, 0.0, -0.5, -0.5, 0.0, 0.5, 0.5, 0.0, 0.5, -0.5, 0.0]";
        final String normals = "normals: [0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0]";
        final String texcoords = "texcoords: [[0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0]]";
        final String triangles = "triangles: [ 0, 1, 2, 1, 3, 2 ]";

        final String weights = "bone_weights: [0.0, 1.0, 0.0, 0.0, 0.75, 0.25, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.2, 0.2, 0.2, 0.4]";
        final String indices = "bone_indices: [ 0, 0, 0, 0, 0, 1, 0, 0, 1, 3, 0, 0, 0, 1, 3, 2 ]";

        List<String> meshFormats = new ArrayList<String>();

        meshFormats.add(null);  // positions, normals, texcoords
        // Mesh with positions, normals
        meshFormats.add("{" + type + ", " + vertices + ", " + triangles + ", " + normals + "}");
        meshFormats.add(null);  // positions, normals, texcoords, bone indices, bone weights
        // Mesh with positions, normals, bone indices, bone weights
//        meshFormats.add("{" + type + ", " + vertices + ", " + triangles + ", " + normals + ", " + indices + ", " + weights + "}");
        return meshFormats;
    }

    private List<String> createTexturedMeshFormats() {
        final String type = "type: polygon";
        final String vertices = "vertices: [-0.5, 0.5, 0.0, -0.5, -0.5, 0.0, 0.5, 0.5, 0.0, 0.5, -0.5, 0.0]";
        final String normals = "normals: [0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0]";
        final String texcoords = "texcoords: [[0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0]]";
        final String triangles = "triangles: [ 0, 1, 2, 1, 3, 2 ]";

        final String weights = "bone_weights: [0.0, 1.0, 0.0, 0.0, 0.75, 0.25, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.2, 0.2, 0.2, 0.4]";
        final String indices = "bone_indices: [ 0, 0, 0, 0, 0, 1, 0, 0, 1, 3, 0, 0, 0, 1, 3, 2 ]";

        List<String> meshFormats = new ArrayList<String>();

        // Mesh with positions, normals, texcoords
        meshFormats.add("{" + type + ", " + vertices + ", " + triangles + ", " + normals + ", " + texcoords + "}");
        meshFormats.add(null);  // positions, normals
        // Mesh with positions, normals, texcoords, bone indices, bone weights
        meshFormats.add("{" + type + ", " + vertices + ", " + triangles + ", " + normals + ", "
                + texcoords + ", " + indices + ", " + weights + "}");
//        meshFormats.add(null); // positions, normals, bone indices, bone weights

        return meshFormats;
    }

    private String createLightType(String type, float r, float g, float b, float x) {
        String ambientIntensity = "ambientintensity: {r:" + 0.3f * r + ", g:" + 0.3f * g
                + ", b:" + 0.3f * b + ", a: 1.0}";
        String diffuseIntensity = "diffuseintensity: {r:" + r + ", g:" + g + ", b:" + b
                + ", a: 1.0}";
        String specularIntensity = "specularintensity: {r:" + r + ", g:" + g + ", b:" + b
                + ", a: 1.0}";
        String position = "position: {x:" + x + ",y: 0.0, z: 0.0}";
        String rotation = "rotation: {w: 1.0, x: 0.0, y: 0.0, z: 0.0}";
        String spotCone = "innerconeangle: 20.0f, outerconeangle: 30.0f";

        return "{type:" + type + "," + rotation + ", " + position + ", " + spotCone + ","
                + ambientIntensity + ", " + diffuseIntensity + ", " + specularIntensity + "}";
    }

    private List<String> createLightingTypes() throws JSONException {
        List<String> lightingTypes = new ArrayList<String >();

        // No lighting
        lightingTypes.add("");

        // one directional light
        lightingTypes.add("["
                + createLightType("directional", 1.0f, 0.3f, 0.3f, 0.0f) + "]");
        // one spot light
        lightingTypes.add("["
                + createLightType("spot", 1.0f, 0.3f, 0.3f, 0.0f) + "]");
        // one point light
        lightingTypes.add("["
                + createLightType("point", 1.0f, 0.3f, 0.3f, 0.0f) + "]");
        // two directional lights
        lightingTypes.add("["
                + createLightType("directional", 1.0f, 0.3f, 0.3f, -1.0f) + ", "
                + createLightType("directional", 1.0f, 0.3f, 0.3f, 1.0f) + "]");
        // two spot lights
        lightingTypes.add("["
                + createLightType("spot", 1.0f, 0.3f, 0.3f, -1.0f) + ","
                + createLightType("spot", 1.0f, 0.3f, 0.3f, 1.0f) + "]");
        // two point lights
        lightingTypes.add("["
                + createLightType("point", 1.0f, 0.3f, 0.3f, -1.0f) + ","
                + createLightType("point", 1.0f, 0.3f, 0.3f, 1.0f) + "]");
        // one directional, one spot
        lightingTypes.add("["
                + createLightType("directional", 1.0f, 0.3f, 0.3f, -1.0f) + ", "
                + createLightType("spot", 1.0f, 0.3f, 0.3f, 1.0f) + "]");
        // one directional, one point
        lightingTypes.add("["
                + createLightType("directional", 1.0f, 0.3f, 0.3f, -1.0f) + ", "
                + createLightType("point", 1.0f, 0.3f, 0.3f, 1.0f) + "]");
        // one directional, one point, one spot
        lightingTypes.add("["
                + createLightType("directional", 1.0f, 0.3f, 0.3f, -1.0f) + ", "
                + createLightType("spot", 1.0f, 0.3f, 0.3f, 0.0f) + ", "
                + createLightType("point", 1.0f, 0.3f, 0.3f, 1.0f) + "]");

        return lightingTypes;
    }

    private String createMaterialFormat(GVRShaderId shaderId, int textureResourceID) {
        String materialFormat = "";

        String type = shaderId == GVRMaterial.GVRShaderType.Phong.ID ?
                "shader: phong," : "shader: texture,";

        if (textureResourceID == -1) {
            final String color = "color: {r: 0.0, g: 1.0, b: 0.0, a: 1.0}";
            materialFormat = "{ " + type + color + "}";
        } else {
            final String textureFormat = "textures: [{" +
                    "id: default, " +
                    "type: bitmap," +
                    "resource_id:" + textureResourceID + "}]";

            materialFormat = "{" + type + textureFormat + "}";
        }

        return materialFormat;
    }

    private String createMaterialFormat(GVRShaderId shaderId) {
        return createMaterialFormat(shaderId, -1);
    }


    private List<String> createMaterialWithTextureFormats() {
        List<String> materials = new ArrayList<String>();
        int[] textures = new int[] {R.drawable.checker, R.drawable.rock_normal, R.raw.jpg_opaque};
        for (int j = 0; j < textures.length; j++) { // Textures
            materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Phong.ID, textures[j]));
            materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Texture.ID, textures[j]));
        }
        return materials;
    }

    /**
     * Test mesh formats and shader combinations.
     *
     * @throws TimeoutException
     */
    @Test
    public void meshFormatsShaderCombinations() throws TimeoutException {
        // Mesh with positions, normals, texcoords, bone indices, bone weights
        JSONObject jsonScene = null;
        List<String> meshFormats = createNonTexturedMeshFormats();
        List<String> lightingTypes = null;
        String screenshotName = null;
        List<String> materials = new ArrayList<String>();

        materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Phong.ID));
        materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Texture.ID));

        try
        {
            lightingTypes = createLightingTypes();

            for (int i = 0; i < meshFormats.size(); i++)
            { // Mesh formats
                String meshFormat = meshFormats.get(i);
                if (meshFormat == null)
                {
                    continue;
                }
                for (int j = 0; j < lightingTypes.size(); j++)
                { // Lighting
                    jsonScene = new JSONObject("{\"id\": \"scene" + i + "\"}");
                    JSONArray objects = new JSONArray();
                    JSONObject objectPhong = new JSONObject();
                    objectPhong.put("geometry", new JSONObject(meshFormat));
                    objectPhong.put("material", new JSONObject(materials.get(0)));
                    objectPhong.put("position", new JSONObject("{x: -1.0f, z: -2.0}"));
                    objectPhong.put("scale", new JSONObject("{x: 2.0, y: 2.0, z: 2.0}"));

                    JSONObject objectTexture = new JSONObject();
                    objectTexture.put("geometry", new JSONObject(meshFormat));
                    objectTexture.put("material", new JSONObject(materials.get(1)));
                    objectTexture.put("position", new JSONObject("{x: 1.0f, z: -2.0}"));
                    objectTexture.put("scale", new JSONObject("{x: 2.0, y: 2.0, z: 2.0}"));

                    objects.put(objectPhong);
                    objects.put(objectTexture);

                    jsonScene.put("objects", objects);
                    if (j > 0)
                    { // No lighting when j == 0
                        jsonScene.put("lights", new JSONArray(lightingTypes.get(j)));
                    }

                    mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                    screenshotName = "testMesh" + i + "Lighting" + j;
                    gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
                    gvrTestUtils.getMainScene().clear();
                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                }
            }
        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshFormatsTexturedShaderCombinations() throws TimeoutException
    {
        // Mesh with positions, normals, texcoords, bone indices, bone weights
        JSONObject jsonScene = null;
        List<String> meshFormats = createTexturedMeshFormats();;
        List<String> lightingTypes = null;
        String screenshotName = null;
        List<String> materials = new ArrayList<String>();

        materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Phong.ID, R.drawable.checker));
        materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Texture.ID, R.drawable.checker));

        try
        {
            lightingTypes = createLightingTypes();

            for (int i = 0; i < meshFormats.size(); i++)
            { // Mesh formats
                String meshFormat = meshFormats.get(i);
                if (meshFormat == null)
                {
                    continue;
                }
                for (int j = 0; j < lightingTypes.size(); j++)
                { // Lighting

                    jsonScene = new JSONObject("{\"id\": \"scene" + i + "\"}");
                    JSONArray objects = new JSONArray();
                    JSONObject objectPhong = new JSONObject();
                    objectPhong.put("geometry", new JSONObject(meshFormat));
                    objectPhong.put("material", new JSONObject(materials.get(0)));
                    objectPhong.put("position", new JSONObject("{x: -1.0f, z: -2.0}"));
                    objectPhong.put("scale", new JSONObject("{x: 2.0, y: 2.0, z: 2.0}"));

                    JSONObject objectTexture = new JSONObject();
                    objectTexture.put("geometry", new JSONObject(meshFormat));
                    objectTexture.put("material", new JSONObject(materials.get(1)));
                    objectTexture.put("position", new JSONObject("{x: 1.0f, z: -2.0}"));
                    objectTexture.put("scale", new JSONObject("{x: 2.0, y: 2.0, z: 2.0}"));

                    objects.put(objectPhong);
                    objects.put(objectTexture);

                    jsonScene.put("objects", objects);
                    if (j > 0)
                    { // No lighting when j == 0
                        jsonScene.put("lights", new JSONArray(lightingTypes.get(j)));
                    }

                    mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                    screenshotName = "testMesh" + i + "Lighting" + j + "2";
                    gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
                    gvrTestUtils.getMainScene().clear();
                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                }
            }
        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshStartWithoutTextureTest() throws TimeoutException {
        String screenshotName = null;
        JSONObject jsonScene = null;
        GVRContext ctx = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        int[] textures = new int[] {R.drawable.checker, R.drawable.rock_normal, R.raw.jpg_opaque};

        try {
            List<String> materials = new ArrayList<String>();
            materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Phong.ID));
            materials.add(createMaterialFormat(GVRMaterial.GVRShaderType.Texture.ID));

            for (int i = 0; i < materials.size(); i++) { // Materials without textures
                for (int j = 0; j < textures.length; j++) { // Textures
                    jsonScene = new JSONObject("{\"id\": \"scene" + j + "\"}");
                    String objName = "cubeObj" + j;
                    JSONObject object = new JSONObject(String.format("{name: %s}", objName));
                    object.put("geometry", new JSONObject("{type: cube}"));
                    object.put("material", new JSONObject(materials.get(i)));
                    object.put("position", new JSONObject("{z: -2.0}"));
                    object.put("rotation", new JSONObject("{w: 0.5f, x: 0.2f, y: 1.0f, z:0.0f}"));

                    jsonScene.put("objects", new JSONArray().put(object));
                    mSceneMaker.makeScene(ctx, scene, jsonScene);

                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                    screenshotName = "testMeshWithoutTexture" + i + "." + j;
                    gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

                    GVRSceneObject obj = scene.getSceneObjectByName(objName);
                    GVRTexture text = mSceneMaker.loadTexture(ctx, textures[j]);
                    obj.getRenderData().getMaterial().setMainTexture(text);

                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                    screenshotName = "testMeshAddTexture" + i + "." + j;
                    gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
                    scene.clear();
                    gvrTestUtils.waitForXFrames(NUM_FRAMES);
                }
            }
        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshStartWithTextureTest() throws TimeoutException {
        String screenshotName = null;
        JSONObject jsonScene = null;
        GVRContext ctx = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        List<String> materials = null;

        try {
            materials = createMaterialWithTextureFormats();

            for (int i = 0; i < materials.size(); i++) { // Materials with textures
                jsonScene = new JSONObject("{\"id\": \"scene" + i + "\"}");
                String objName = "cubeObj" + i;
                JSONObject object = new JSONObject(String.format("{name: %s}", objName));
                object.put("geometry", new JSONObject("{type: cube}"));
                object.put("material", new JSONObject(materials.get(i)));
                object.put("position", new JSONObject("{z: -2.0}"));
                object.put("rotation", new JSONObject("{w: 0.5f, x: 0.2f, y: 1.0f, z:0.0f}"));

                jsonScene.put("objects", new JSONArray().put(object));
                mSceneMaker.makeScene(ctx, scene, jsonScene);
                gvrTestUtils.waitForXFrames(NUM_FRAMES);

                screenshotName = "testMeshWithTexture" + i;
                gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

                GVRSceneObject obj = scene.getSceneObjectByName(objName);
                obj.getRenderData().getMaterial().setMainTexture(null);
                gvrTestUtils.waitForXFrames(NUM_FRAMES);

                screenshotName = "testMeshRemoveTexture" + i;
                gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

                scene.clear();
            }
        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshSwitchTexcoordSet() throws TimeoutException {
        String screenshotName = null;
        JSONObject jsonScene = null;
        GVRContext ctx = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();

        try {

            String material = createMaterialFormat(GVRMaterial.GVRShaderType.Phong.ID,
                    R.drawable.colortex);

            final String type = "type: quad";
            final String descriptor = "descriptor: \"float3 a_position float2 a_texcoord float3 " +
                    "a_normal float2 a_texcoord1\"";
            String geometry = "{" + type + ", " + descriptor + "}";

            jsonScene = new JSONObject("{id: scene}");
            String objName = "cubeObj";
            JSONObject object = new JSONObject(String.format("{name: %s}", objName));
            object.put("geometry", new JSONObject(geometry));
            object.put("material", new JSONObject(material));
            object.put("position", new JSONObject("{z: -2.0}"));

            jsonScene.put("objects", new JSONArray().put(object));
            mSceneMaker.makeScene(ctx, scene, jsonScene);
            GVRSceneObject obj = scene.getSceneObjectByName(objName);
            obj.getRenderData().getMesh().setTexCoords(
                    new float[]{0.5F, 0.5F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F}, 1);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshWithFirstSetTextCoord";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

            // change text coordinates
            obj.getRenderData().getMaterial().setTexCoord("diffuseTexture",
                    "a_texcoord1",
                    "diffuse_coord");

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshWithSecondSetTextCoord";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
            // test failed, textcoord is not changing

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshWithoutLightingTest() throws TimeoutException {
        String screenshotName = null;

        try {
            JSONObject jsonScene = new JSONObject("{id: scene}");

            JSONObject object = new JSONObject();
            object.put("geometry", new JSONObject("{type: cube}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, R.drawable.checker)));
            object.put("position", new JSONObject("{x: -1.0, z: -2.0}"));

            jsonScene.put("objects", new JSONArray().put(object));
            mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshWithoutLighting";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);


            // add lighting on the scene
            GVRSceneObject lightNode = new GVRSceneObject(gvrTestUtils.getGvrContext());
            GVRDirectLight light = new GVRDirectLight(gvrTestUtils.getGvrContext());

            lightNode.attachComponent(light);
            lightNode.getTransform().setPosition(0, 0, 0);
            lightNode.getTransform().setRotation(1, 0, 0, 0);
            light.setAmbientIntensity(0.3f * 1, 0.3f * 0.3f, 0.3f * 0.3f, 1);
            light.setDiffuseIntensity(1, 0.3f, 0.3f, 1);
            light.setSpecularIntensity(1, 0.3f, 0.3f, 1);

            gvrTestUtils.getMainScene().addSceneObject(lightNode);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshAddLighting";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshWithLightingTest() throws TimeoutException {
        String screenshotName = null;

        try {
            JSONObject jsonScene = new JSONObject("{id: scene}");

            JSONObject object = new JSONObject();
            object.put("geometry", new JSONObject("{type: cube}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, R.drawable.checker)));
            object.put("position", new JSONObject("{x: -1.0, z: -2.0}"));

            jsonScene.put("objects", new JSONArray().put(object));
            jsonScene.put("lights", new JSONArray("["
                    + createLightType("directional", 1.0f, 0.3f, 0.3f, 0.0f) + "]"));
            mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshWithLighting";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

            // remove lighting from the scene
            GVRSceneObject lightNode = gvrTestUtils.getMainScene().getSceneObjectByName("lightNode");
            gvrTestUtils.getMainScene().removeSceneObject(lightNode);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testMeshRemoveLighting";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void meshWithLightingDisableRenderDataLightTest() throws TimeoutException {
        String screenshotName = null;

        try {
            JSONObject jsonScene = new JSONObject("{id: scene}");

            JSONObject object = new JSONObject();
            object.put("geometry", new JSONObject("{type: cube}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, R.drawable.checker)));
            object.put("position", new JSONObject("{x: -1.0, z: -2.0}"));

            jsonScene.put("objects", new JSONArray().put(object));
            jsonScene.put("lights", new JSONArray("["
                    + createLightType("directional", 1.0f, 0.3f, 0.3f, 0.0f) + "]"));
            mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForXFrames(NUM_FRAMES);
            screenshotName = "testMeshWithLightAndRenderDataEnabled";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

            // disable light in object render data
            GVRSceneObject cubeSceneObj = gvrTestUtils.getMainScene().getSceneObjectByName("cubeSceneObj");
            cubeSceneObj.getRenderData().disableLight();

            gvrTestUtils.waitForXFrames(NUM_FRAMES);
            screenshotName = "testMeshDisableRenderDataLight";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }
}
