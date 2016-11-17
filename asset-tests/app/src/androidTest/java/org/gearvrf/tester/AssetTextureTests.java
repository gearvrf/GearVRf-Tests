package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRExternalScene;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IErrorEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.IAssetEvents;

import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.FileNameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class AssetTextureTests
{
    private static final String TAG = AssetTextureTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private boolean mDoCompare = false;
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
        Future<GVRTexture> tex = ctx.loadFutureCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));

        mWaiter.assertNotNull(scene);
        mBackground = new GVRCubeSceneObject(ctx, false, new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID));
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.setName("background");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mHandler = new AssetEventHandler(scene, mWaiter, mTestUtils, getClass().getSimpleName());
    }


    @Test
    public void jassimpEmbeddedTextures() throws TimeoutException
    {
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
