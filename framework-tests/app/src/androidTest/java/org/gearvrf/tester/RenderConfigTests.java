package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
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
}
