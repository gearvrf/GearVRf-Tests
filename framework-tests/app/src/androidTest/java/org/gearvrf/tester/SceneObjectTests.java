package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.IAssetEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;

import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

@RunWith(AndroidJUnit4.class)
public class SceneObjectTests
{
    private static final String TAG = SceneObjectTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mBackground;
    private GVRMaterial mBlueMtl;
    private GVRMaterial mCubeMapMtl;
    private boolean mDoCompare = true;

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
        Future<GVRTexture> tex = ctx.loadFutureCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));
        mBlueMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        mCubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);

        mBackground = new GVRCubeSceneObject(ctx, false, new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID));
        mBackground.getTransform().setScale(10, 10, 10);
        mBackground.setName("background");
        mBlueMtl.setDiffuseColor(0, 0, 1, 1);
        try
        {
            waitForTexture(tex);
        }
        catch (InterruptedException ex)
        {
            mWaiter.fail(ex);
        }
        mCubeMapMtl.setMainTexture(tex);
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
    }

    private void waitForTexture(Future<GVRTexture> futureTex) throws InterruptedException
    {
        while (!futureTex.isDone())
        {
            sleep(200);
        }
    }

    @Test
    public void canClearEmptyScene() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();

        GVRTextViewSceneObject text = new GVRTextViewSceneObject(ctx, "Hello");
        scene.removeAllSceneObjects();
    }

    @Test
    public void canClearSceneWithStuff() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSceneObject(ctx);
        GVRTextViewSceneObject text = new GVRTextViewSceneObject(ctx, "Hello");

        text.getTransform().setPosition(-1, 0, -2);
        sphere.getTransform().setPosition(1, 0, -2);
        scene.addSceneObject(mBackground);
        mRoot.addChildObject(sphere);
        mRoot.addChildObject(text);
        text.setName("text");
        sphere.setName("sphere");
        mTestUtils.waitForSceneRendering();
        scene.clear();
        mWaiter.assertNull(scene.getSceneObjectByName("background"));
        mWaiter.assertNull(scene.getSceneObjectByName("sphere"));
        mWaiter.assertNull(scene.getSceneObjectByName("text"));
    }

    @Test
    public void canDisplaySpheres() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(ctx, true, mBlueMtl);
        GVRSceneObject sphere2 = new GVRSphereSceneObject(ctx, false, mCubeMapMtl);

        sphere1.getTransform().setPosition(0, 0, -4);
        sphere2.getTransform().setScale(20, 20, 20);
        sphere1.setName("sphere1");
        sphere2.setName("sphere2");
        mRoot.addChildObject(sphere1);
        scene.addSceneObject(sphere2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplaySpheres", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayCubes() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mBlueMtl);
        GVRSceneObject cube2 = new GVRCubeSceneObject(ctx, false, mCubeMapMtl);

        cube1.getTransform().setPosition(0, 0, -4);
        cube1.setName("cube1");
        cube2.getTransform().setScale(20, 20, 20);
        cube2.setName("cube2");
        mRoot.addChildObject(cube1);
        scene.addSceneObject(cube2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cube2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cube1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayCubes", mWaiter, mDoCompare);
    }


    @Test
    public void canDisplayCylinders() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.color_sphere));
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, mBlueMtl);
        GVRMaterial mtl = new GVRMaterial(ctx);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, mtl);

        mtl.setMainTexture(tex);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(10, 10, 10);
        cylinder2.setName("cylinder2");
        mRoot.addChildObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayCylinders", mWaiter, mDoCompare);
    }
}
