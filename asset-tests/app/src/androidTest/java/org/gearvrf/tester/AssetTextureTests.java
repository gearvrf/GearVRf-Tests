package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.GVRPhongShader;

import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class AssetTextureTests
{
    private static final String TAG = AssetTextureTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private boolean mDoCompare = true;
    private AssetEventHandler mHandler;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class)
    {
        protected void afterActivityFinished() {
            GVRScene scene = mTestUtils.getMainScene();
            if (scene != null) {
                scene.clear();
            }
        }
    };

    @Before
    public void setUp() throws TimeoutException
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();

        mWaiter.assertNotNull(scene);
        mBackground = new GVRCubeSceneObject(ctx, false);
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.getRenderData().setShaderTemplate(GVRPhongShader.class);
        mBackground.setName("background");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mHandler = new AssetEventHandler(scene, mWaiter, mTestUtils, getClass().getSimpleName());
    }


    @Test
    public void jassimpEmbeddedTextures() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRCubeSceneObject backgnd = new GVRCubeSceneObject(ctx, false);

        backgnd.getRenderData().setShaderTemplate(GVRPhongShader.class);
        backgnd.getRenderData().getMaterial().setDiffuseColor(1.0f, 1.0f, 0.7f, 1.0f);
        backgnd.getTransform().setScale(10, 10, 10);
        mTestUtils.getMainScene().addSceneObject(backgnd);
        mHandler.loadTestModel("jassimp/bmw.FBX", 4, 1, "jassimpEmbeddedTextures");
    }

    @Test
    public void jassimpLightmap() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/lightmap/lightmap_test.fbx", 13, 0, "jassimpLightmap");
    }

    @Test
    public void jassimpCubeWrongTex() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_wrongtex.fbx", 0, 1, "jassimpCubeWrongTex");
    }


    @Test
    public void x3dTexcoordTest1() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/texture_coordinates/texturecoordinatetest.x3d", 4, 0, "x3dTexcoordTest1");
    }

    @Test
    public void x3dTexcoordTest2() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/texture_coordinates/texturecoordinatetestsubset.x3d", 3, 0, "x3dTexcoordTest2");
    }

    @Test
    public void x3dTexcoordTest3() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/texture_coordinates/texturecoordinatetestsubset2.x3d", 5, 0, "x3dTexcoordTest3");
    }

    @Test
    public void x3dTexcoordTest4() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/texture_coordinates/texturecoordinatetestsubset3.x3d", 2, 0, "x3dTexcoordTest4");
    }
}
