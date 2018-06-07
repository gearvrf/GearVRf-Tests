package org.gearvrf.tester;

import android.opengl.GLES30;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.shaders.GVRColorBlendShader;
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

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;

@RunWith(AndroidJUnit4.class)
public class RenderConfigTests {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private boolean mDoCompare = true;
    private GVRSceneMaker mSceneMaker;

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
        mSceneMaker = new GVRSceneMaker(gvrTestUtils);
    }

    private String createMaterialFormat(GVRShaderId shaderId, int textureResourceID) {
        String materialFormat = "";

        String type = shaderId == GVRMaterial.GVRShaderType.Phong.ID ?
                "shader: phong," : "shader: texture,";

        if (textureResourceID == -1) {
            final String color = "color: {r: 0.0, g: 1.0, b: 0.0, a: 1.0}";
            materialFormat = "{ " + type + color + "}";
        } else if (textureResourceID == -2) {
            final String color = "color: {r: 0.0, g: 0.0, b: 1.0, a: 1.0}";
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
        final GVRScene mainScene = gvrTestUtils.getMainScene();

        try
        {
            JSONArray sceneObjects = new JSONArray();
            JSONObject jsonScene = new JSONObject(("{id: scene}"));

            JSONObject object = new JSONObject("{name: quadObj}");
            object.put("geometry", new JSONObject("{type: quad}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, -1))); //green
            object.put("position", new JSONObject("{x: -0.3, z: -2.0}"));
            sceneObjects.put(object);

            JSONObject object2 = new JSONObject("{name: quadObj2}");
            object2.put("geometry", new JSONObject("{type: quad}"));
            object2.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, -2))); // blue
            object2.put("position", new JSONObject("{x: 0.3, z: -2.0}"));
            sceneObjects.put(object2);

            jsonScene.put("objects", sceneObjects);

            mSceneMaker.makeScene(gvrTestUtils, jsonScene,
                new Runnable()
                {
                    public void run()
                    {
                        mainScene.getSceneObjectByName("quadObj").getRenderData().
                                setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);
                        mainScene.getSceneObjectByName("quadObj2").getRenderData().
                                setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
                    }
                });
            gvrTestUtils.waitForXFrames(4);
            screenshotName = "testRenderingOrder1";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

            mSceneMaker.makeScene(gvrTestUtils, jsonScene,
                new Runnable()
                {
                    public void run()
                    {
                        mainScene.getSceneObjectByName("quadObj").getRenderData().
                                setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
                        mainScene.getSceneObjectByName("quadObj2").getRenderData().
                                setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);
                    }
                });
            gvrTestUtils.waitForXFrames(4);
            screenshotName = "testRenderingOrder2";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void depthTest() throws TimeoutException {
        String screenshotName = null;
        final GVRScene mainScene = gvrTestUtils.getMainScene();

        try
        {
            JSONArray sceneObjects = new JSONArray();
            JSONObject jsonScene = new JSONObject(("{id: scene}"));

            JSONObject object = new JSONObject("{name: quadObj}");
            object.put("geometry", new JSONObject("{type: quad}"));
            object.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, -1)));
            object.put("position", new JSONObject("{y: 0.3, z: -2.0}"));
            object.put("rotation", new JSONObject("{w: 0.924, x: 0.383, y: 0.0, z: 0.0}"));
            sceneObjects.put(object);

            JSONObject object2 = new JSONObject("{name: quadObj2}");
            object2.put("geometry", new JSONObject("{type: quad}"));
            object2.put("material", new JSONObject(createMaterialFormat(
                    GVRMaterial.GVRShaderType.Phong.ID, -2)));
            object2.put("position", new JSONObject("{z: -2.1}"));
            sceneObjects.put(object2);

            jsonScene.put("objects", sceneObjects);

            mSceneMaker.makeScene(gvrTestUtils, jsonScene,
                new Runnable()
                {
                    public void run()
                    {
                        mainScene.getSceneObjectByName("quadObj").getRenderData().setDepthTest(false);
                        mainScene.getSceneObjectByName("quadObj2").getRenderData().setDepthTest(false);
                    }
                });
            gvrTestUtils.waitForXFrames(4);
            screenshotName = "testDepthTest1";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
            gvrTestUtils.getGvrContext().runOnGlThread(new Runnable()
            {
                public void run()
                {
                    mainScene.getSceneObjectByName("quadObj").getRenderData().setDepthTest(true);
                    mainScene.getSceneObjectByName("quadObj2").getRenderData().setDepthTest(true);
                }
            });

            gvrTestUtils.waitForXFrames(8);
            screenshotName = "testDepthTest2";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);
        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
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
            mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            GVRSceneObject obj =  gvrTestUtils.getMainScene().getSceneObjectByName("quad_fg");
            obj.getRenderData().setOffset(true);
            obj.getRenderData().setOffsetFactor(-1.0f);
            obj.getRenderData().setOffsetUnits(-1.0f);

            gvrTestUtils.waitForSceneRendering();
            screenshotName = "testPolygonOffset";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
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

            mSceneMaker.makeScene(gvrTestUtils.getGvrContext(), gvrTestUtils.getMainScene(), jsonScene);

            gvrTestUtils.waitForXFrames(4);
            screenshotName = "testDrawMode";
            gvrTestUtils.screenShot(getClass().getSimpleName(), screenshotName, mWaiter, mDoCompare);

        }
        catch (JSONException e)
        {
            mWaiter.fail(e);
        }
    }

    @Test
    public void testOnePostEffect() throws TimeoutException {
        final GVRContext ctx = gvrTestUtils.getGvrContext();
        final GVRScene scene = gvrTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(gvrTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));;
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRMaterial flipHorzPostEffect = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.VerticalFlip.ID);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        gvrTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        gvrTestUtils.waitForSceneRendering();
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testOnePostEffect", mWaiter, true);
    }

    @Test
    public void testTwoPostEffects() throws TimeoutException {
        final GVRContext ctx = gvrTestUtils.getGvrContext();
        final GVRScene scene = gvrTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(gvrTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));;
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRMaterial flipHorzPostEffect = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.VerticalFlip.ID);
        GVRShaderId colorBlendID = new GVRShaderId(GVRColorBlendShader.class);
        GVRMaterial colorBlendPostEffect = new GVRMaterial(ctx, colorBlendID);

        colorBlendPostEffect.setVec3("u_color", 0.0f, 0.3f, 0.3f);
        colorBlendPostEffect.setFloat("u_factor", 0.5f);
        flipHorzPostEffect.setVec3("u_color", 0, 0, 0);
        flipHorzPostEffect.setFloat("u_factor", 0);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getCenterCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getCenterCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        gvrTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        gvrTestUtils.waitForSceneRendering();
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testTwoPostEffects", mWaiter, true);
    }


    @Test
    public void testBlendFunc() throws TimeoutException {
        final GVRContext ctx = gvrTestUtils.getGvrContext();
        final GVRScene scene = gvrTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(gvrTestUtils, 2);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.checker));;
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.donut));
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRSceneObject quad2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex2);
        GVRRenderData rdata2 = quad2.getRenderData();
        GVRMaterial mat2 = rdata2.getMaterial();

        mat1.setMainTexture(tex1);
        mat2.setColor(1.0f, 1.0f, 0.0f);
        rdata2.setAlphaBlend(true);
        rdata2.setAlphaBlendFunc(GL_ONE, GL_SRC_ALPHA);
        rdata2.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        cube1.getTransform().setPositionZ(-2.0f);
        quad2.getTransform().setPositionZ(-0.8f);
        scene.addSceneObject(cube1);
        scene.addSceneObject(quad2);
        mWaiter.assertEquals(GL_ONE, rdata2.getSourceAlphaBlendFunc());
        mWaiter.assertEquals(GL_SRC_ALPHA, rdata2.getDestAlphaBlendFunc());
        gvrTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        gvrTestUtils.waitForSceneRendering();
        gvrTestUtils.screenShot(getClass().getSimpleName(), "testBlendFunc", mWaiter, true);
    }
}
