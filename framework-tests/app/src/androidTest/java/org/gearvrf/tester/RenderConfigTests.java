package org.gearvrf.tester;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.unittestutils.GVRSceneMaker;
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
public class RenderConfigTests {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private boolean mDoCompare = false;

    public RenderConfigTests() {
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

    @Test
    public void renderingOrderTest() throws TimeoutException {
        String screenshotName = null;

        try {
            JSONObject jsonScene = new JSONObject(("{id: scene}"));

            JSONObject object = new JSONObject();
            object.put("geometry", new JSONObject("{type: cube}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, R.drawable.checker)));
            object.put("position", new JSONObject("{z: -2.0}"));

            jsonScene.put("objects", new JSONArray().put(object));
            GVRSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForXFrames(10 * 60);
            screenshotName = "testRenderingOrder";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void polygonOffsetTest() throws TimeoutException {
        String screenshotName = null;

        try {
            String pos = "{z: -2.0}";
            JSONObject jsonScene = new JSONObject(("{id: scene}"));

            JSONObject quad_bg = new JSONObject("{name: quad_bg}");
            quad_bg.put("geometry", new JSONObject("{type: quad}"));
            quad_bg.put("material", new JSONObject("{shader: phong"
                    + ", color: {r: 0.0, b: 0.0}}"));
            quad_bg.put("position", new JSONObject(pos));
            quad_bg.put("scale", new JSONObject("{x: 2.0, y: 2.0}"));

            JSONObject quad_fg = new JSONObject("{name: quad_fg}");
            quad_fg.put("geometry", new JSONObject("{type: quad}"));
            quad_fg.put("material", new JSONObject("{shader: phong"
                    + ", color: {g: 0.0, b: 0.0}}"));
            quad_fg.put("position", new JSONObject(pos));

            jsonScene.put("objects", new JSONArray().put(quad_fg).put(quad_bg));
            GVRSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            GVRSceneObject obj =  gvrTestUtils.getMainScene().getSceneObjectByName("quad_fg");
            obj.getRenderData().setOffset(true);
            obj.getRenderData().setOffsetFactor(-1.0f);
            obj.getRenderData().setOffsetUnits(-1.0f);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testPolygonOffset";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> createDrawModeMeshes() {
        final String type = "type: polygon";

        final String vertices_triangles = "vertices: ["
                + "0.0000, 0.0000, 0.0000, 0.2242, 0.9745, 0.0000, -0.2208, 0.9753, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.2208, 0.9753, 0.0000, -0.6221, 0.7829, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.6221, 0.7829, 0.0000, -0.9002, 0.4355, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.9002, 0.4355, 0.0000, -1.0000, 0.0018, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -1.0000, 0.0018, 0.0000, -0.9017, -0.4323, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.9017, -0.4323, 0.0000, -0.6249, -0.7807, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.6249, -0.7807, 0.0000, -0.2242, -0.9745, 0.0000,"
                + "0.0000, 0.0000, 0.0000, -0.2242, -0.9745, 0.0000, 0.2208, -0.9753, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 0.2208, -0.9753, 0.0000, 0.6221, -0.7829, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 0.6221, -0.7829, 0.0000, 0.9002, -0.4355, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 0.9002, -0.4355, 0.0000, 1.0000, -0.0018, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 1.0000, -0.0018, 0.0000, 0.9017, 0.4323, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 0.9017, 0.4323, 0.0000, 0.6249, 0.7807, 0.0000,"
                + "0.0000, 0.0000, 0.0000, 0.6249, 0.7807, 0.0000, 0.2242, 0.9745, 0.0000"
                + "]";

        final String vertices_strip = "vertices: ["
                + "0.2242, 0.9745, 0.0000, -0.2208, 0.9753, 0.0000, 0.6249, 0.7807, 0.0000,"
                + "-0.6221, 0.7829, 0.0000, 0.9017, 0.4323, 0.0000, -0.9002, 0.4355, 0.0000,"
                + "1.0000, -0.0018, 0.0000, -1.0000, 0.0018, 0.0000, 0.9002, -0.4355, 0.0000,"
                + "-0.9017, -0.4323, 0.0000, 0.6221, -0.7829, 0.0000, -0.6249, -0.7807, 0.0000,"
                + "0.2208, -0.9753, 0.0000, -0.2242, -0.9745, 0.0000"
                + "]";

        final String vertices_loop = "vertices: ["
                + "0.0000, 0.0000, 0.0000, 0.2242, 0.9745, 0.0000, -0.2208, 0.9753, 0.0000,"
                + "-0.6221, 0.7829, 0.0000, -0.9002, 0.4355, 0.0000, -1.0000, 0.0018, 0.0000,"
                + "-0.9017, -0.4323, 0.0000, -0.6249, -0.7807, 0.0000, -0.2242, -0.9745, 0.0000,"
                + "0.2208, -0.9753, 0.0000, 0.6221, -0.7829, 0.0000, 0.9002, -0.4355, 0.0000,"
                + "1.0000, -0.0018, 0.0000, 0.9017, 0.4323, 0.0000, 0.6249, 0.7807, 0.0000,"
                + "0.2242, 0.9745, 0.0000"
                + "]";

        List<String> meshFormats = new ArrayList<String>();

        meshFormats.add("{" + type + "," + vertices_triangles + "}");
        meshFormats.add("{" + type + "," + vertices_strip + "}");
        meshFormats.add("{" + type + "," + vertices_loop + "}");

        return meshFormats;
    }

    @Test
    public void drawModeTest() throws TimeoutException {
        String screenshotName = null;
        List<String> meshFormats = createDrawModeMeshes();

        try {
            JSONObject jsonScene = new JSONObject(("{id: scene}"));

            JSONObject lineMode = new JSONObject();
            lineMode.put("geometry", new JSONObject(meshFormats.get(0)));
            lineMode.put("material",
                    new JSONObject("{shader: phong, color: {r: 1.0}}"));
            lineMode.put("position", new JSONObject("{x: -2.0, y: 1.0, z: -4.0}"));
            lineMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_LINES +"}"));

            JSONObject lineStripMode = new JSONObject();
            lineStripMode.put("geometry", new JSONObject(meshFormats.get(1)));
            lineStripMode.put("material",
                    new JSONObject("{shader: phong, color: {g: 1.0}}"));
            lineStripMode.put("position", new JSONObject("{y: 1.0, z: -4.0}"));
            lineStripMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_LINE_STRIP +"}"));

            JSONObject lineLoopMode = new JSONObject();
            lineLoopMode.put("geometry", new JSONObject(meshFormats.get(2)));
            lineLoopMode.put("material",
                    new JSONObject("{shader: phong, color: {b: 1.0}}"));
            lineLoopMode.put("position", new JSONObject("{x: 2.0, y: 1.0, z: -4.0}"));
            lineLoopMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_LINE_LOOP +"}"));

            JSONObject triangleMode = new JSONObject();
            triangleMode.put("geometry", new JSONObject(meshFormats.get(0)));
            triangleMode.put("material",
                    new JSONObject("{shader: phong, color: {r: 1.0}}"));
            triangleMode.put("position", new JSONObject("{x: -2.0, y: -1.0, z: -4.0}"));
            triangleMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_TRIANGLES +"}"));

            JSONObject triStripMode = new JSONObject();
            triStripMode.put("geometry", new JSONObject(meshFormats.get(1)));
            triStripMode.put("material",
                    new JSONObject("{shader: phong, color: {g: 1.0}}"));
            triStripMode.put("position", new JSONObject("{y: -1.0, z: -4.0}"));
            triStripMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_TRIANGLE_STRIP +"}"));

            JSONObject triLoopMode = new JSONObject();
            triLoopMode.put("geometry", new JSONObject(meshFormats.get(2)));
            triLoopMode.put("material",
                    new JSONObject("{shader: phong, color: {b: 1.0}}"));
            triLoopMode.put("position", new JSONObject("{x: 2.0, y: -1.0, z: -4.0}"));
            triLoopMode.put("renderconfig",
                    new JSONObject("{drawmode:"+ GLES30.GL_TRIANGLE_FAN +"}"));

            jsonScene.put("objects",
                    new JSONArray().put(lineMode).put(lineStripMode).put(lineLoopMode)
                                   .put(triangleMode).put(triStripMode).put(triLoopMode));

            GVRSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testDrawMode";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
