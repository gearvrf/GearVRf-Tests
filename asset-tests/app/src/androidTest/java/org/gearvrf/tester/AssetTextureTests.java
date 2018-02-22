package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRAssetLoader;
import org.gearvrf.GVRCompressedImage;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRImage;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;

import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;

import org.joml.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
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
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown()
    {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }

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
        mBackground = new GVRCubeSceneObject(ctx, false, new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID));
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.setName("background");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mHandler = new AssetEventHandler(scene, mWaiter, mTestUtils, getClass().getSimpleName());
    }

    @Test
    public void jassimpNormalDiffuseSpecularLightmap() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/lightmap/normal_diffuse_specular_lightmap.fbx", 25, 0, "jassimpNormalDiffuseSpecularLightmap");
    }

    @Test
    public void jassimpNormaLightmap() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/lightmap/normal_lightmap.fbx", 25, 0, "jassimpNormalLightmap");
    }

    @Test
    public void jassimpSpecularLightmap() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/lightmap/specular_lightmap.fbx", 25, 0, "jassimpSpecularLightmap");
    }

    @Test
    public void jassimpEmbeddedTextures() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRMaterial mtl =  new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRCubeSceneObject backgnd = new GVRCubeSceneObject(ctx, false, mtl);

        mtl.setDiffuseColor(1.0f, 1.0f, 0.7f, 1.0f);
        backgnd.getTransform().setScale(10, 10, 10);
        mTestUtils.getMainScene().addSceneObject(backgnd);
        mHandler.loadTestModel("jassimp/bmw.FBX", 20, 1, "jassimpEmbeddedTextures");
    }

    @Test
    public void jassimpLightmap() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/lightmap/lightmap_test.fbx", 13, 0, "jassimpLightmap");
    }

    @Test
    public void jassimpCubeWrongTex() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/cube/cube_wrongtex.fbx", 0, 1, "jassimpCubeWrongTex");
    }


    @Test
    public void x3dTexcoordTest1() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/texture_coordinates/texturecoordinatetest.x3d", 5, 0, "x3dTexcoordTest1");
    }

    @Test
    public void x3dTexcoordTest2() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/texture_coordinates/texturecoordinatetestsubset.x3d", 3, 0, "x3dTexcoordTest2");
    }

    @Test
    public void x3dTexcoordTest3() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/texture_coordinates/texturecoordinatetestsubset2.x3d", 5, 0, "x3dTexcoordTest3");
    }

    @Test
    public void x3dTexcoordTest4() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "x3d/texture_coordinates/texturecoordinatetestsubset3.x3d", 5, 0, "x3dTexcoordTest4");
    }

    @Test
    public void NormalDiffuseSpecularLightmap() throws TimeoutException
    {
        mHandler.loadTestModel(GVRTestUtils.GITHUB_URL + "jassimp/lightmap/normal_diffuse_specular_lightmap.fbx", "testNormalDiffuseSpecularLightmap",
                0.006f, true, new Vector3f( 0.05f, -0.05f, -4.5f));
    }


    @Test
    public void testDownloadTextureCache() throws MalformedURLException {
        final GVRContext gvr = mTestUtils.getGvrContext();
        final String urlString = GVRTestUtils.GITHUB_URL + "asset-tests/app/src/main/res/drawable-xxxhdpi/gearvr_logo.jpg";

        final String directoryPath = gvr.getContext().getCacheDir().getAbsolutePath();
        final String outputFilename = directoryPath + File.separator + UUID.nameUUIDFromBytes(urlString.getBytes()).toString() + "gearvr_logo.jpg";
        final File file = new File(outputFilename);
        Assert.assertFalse(file.exists());

        final URL url = new URL(urlString);
        GVRAndroidResource resource = new GVRAndroidResource(gvr, url, true);
        GVRAndroidResource.TextureCallback callback = new GVRAndroidResource.TextureCallback()
        {
            public void failed(Throwable t, GVRAndroidResource androidResource)
            {
                mWaiter.fail(t);
            }
            public boolean stillWanted(GVRAndroidResource r)
            {
                return true;
            }
            public void loaded(GVRImage image, GVRAndroidResource resource)
            {
                Assert.assertTrue(file.exists());
                file.delete();
                Assert.assertFalse(file.exists());
                mTestUtils.onAssetLoaded(null);
            }
        };
        gvr.getAssetLoader().loadTexture(resource, callback, gvr.getAssetLoader().getDefaultTextureParameters(),
                                         GVRAssetLoader.DEFAULT_PRIORITY, GVRCompressedImage.BALANCED);
        mTestUtils.waitForAssetLoad();
    }
}
