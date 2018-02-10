package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRAssetLoader;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRBillboard;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class SceneObjectTests
{
    private static final String TAG = SceneObjectTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRMaterial mBlueMtl;
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
    public void setUp() throws Exception
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);

        mBlueMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        mBlueMtl.setDiffuseColor(0, 0, 1, 1);
        mRoot = scene.getRoot();

        mWaiter.assertNotNull(mRoot);
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

        final GVRSceneObject background = new GVRCubeSceneObject(ctx, false);
        background.getTransform().setScale(10, 10, 10);
        background.setName("background");
        scene.addSceneObject(background);

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
    public void canDisplaySpheres() throws Exception
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(ctx, true, mBlueMtl);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);

        final GVRTexture tex = ctx.getAssetLoader().loadCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));
        final GVRMaterial cubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);
        cubeMapMtl.setMainTexture(tex);

        GVRSceneObject sphere2 = new GVRSphereSceneObject(ctx, false, cubeMapMtl);

        sphere1.getTransform().setPosition(0, 0, -4);
        sphere2.getTransform().setScale(20, 20, 20);
        sphere1.setName("sphere1");
        sphere2.setName("sphere2");
        mTestUtils.waitForAssetLoad();
        mRoot.addChildObject(sphere1);
        scene.addSceneObject(sphere2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplaySpheres", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayLitSpheres() throws Exception
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(ctx, true, mBlueMtl);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);

        final GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.checker));
        GVRSceneObject sphere2 = new GVRSphereSceneObject(ctx, false, tex);
        GVRSceneObject light = new GVRSceneObject(ctx);
        GVRPointLight lightSource = new GVRPointLight(ctx);

        light.setName("light");
        light.attachComponent(lightSource);
        sphere1.getTransform().setPosition(0, 0, -4);
        sphere2.getTransform().setScale(10, 10, 10);
        sphere1.setName("sphere1");
        sphere2.setName("sphere2");
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(light);
        scene.addSceneObject(sphere1);
        scene.addSceneObject(sphere2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayLitSpheres", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayCubes() throws Exception
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mBlueMtl);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);

        final GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.raw.beach));
        final GVRMaterial cubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);
        cubeMapMtl.setMainTexture(tex);
        GVRSceneObject cube2 = new GVRCubeSceneObject(ctx, false, cubeMapMtl);

        cube1.getTransform().setPosition(0, 0, -4);
        cube1.setName("cube1");
        cube2.getTransform().setScale(20, 20, 20);
        cube2.setName("cube2");
        mTestUtils.waitForAssetLoad();
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
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.color_sphere));
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, mBlueMtl);
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Texture.ID);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, mtl);

        mtl.setTexture("u_texture", tex);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(10, 10, 10);
        cylinder2.setName("cylinder2");
        mTestUtils.waitForAssetLoad();
        mRoot.addChildObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayCylinders", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayLitCylinders() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.color_sphere));
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, mBlueMtl);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, tex);
        GVRSceneObject light = new GVRSceneObject(ctx);
        GVRDirectLight lightSource = new GVRDirectLight(ctx);

        light.setName("light");
        light.attachComponent(lightSource);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(8, 8, 8);
        cylinder2.setName("cylinder2");
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(light);
        scene.addSceneObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayLitCylinders", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayComplexCylinders() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        ArrayList<GVRTexture> mBackTextures = new ArrayList<>(3);
        ArrayList<GVRTexture> mObjectTextures = new ArrayList<>(3);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 4);
        GVRAssetLoader loader = ctx.getAssetLoader();

        ctx.getEventReceiver().addListener(texHandler);
        mBackTextures.add(0, loader.loadTexture(new GVRAndroidResource(ctx, R.drawable.color_sphere)));
        mBackTextures.add(1, loader.loadTexture(new GVRAndroidResource(ctx, R.drawable.checker)));
        mBackTextures.add(2, loader.loadTexture(new GVRAndroidResource(ctx, R.drawable.wavylines)));
        mObjectTextures.add(0, loader.loadTexture(new GVRAndroidResource(ctx, R.drawable.rock_normal)));
        mObjectTextures.add(1, mBackTextures.get(1));
        mObjectTextures.add(2, mBackTextures.get(2));


        GVRSceneObject background = new GVRCylinderSceneObject(ctx, false, mBackTextures);
        GVRSceneObject cylinder = new GVRCylinderSceneObject(ctx, true, mObjectTextures);
        GVRSceneObject light = new GVRSceneObject(ctx);
        GVRDirectLight lightSource = new GVRDirectLight(ctx);

        light.setName("light");
        light.attachComponent(lightSource);
        cylinder.getTransform().setPosition(0, 0, -2);
        cylinder.getTransform().rotateByAxis(45, 1, 0, 0);
        cylinder.setName("cylinder");
        background.getTransform().setScale(8, 8, 8);
        background.getTransform().rotateByAxis(45, 1, 0, 0);
        background.setName("background");
        mTestUtils.waitForAssetLoad();
        scene.addSceneObject(light);
        scene.addSceneObject(cylinder);
        scene.addSceneObject(background);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("background"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayComplexCylinders", mWaiter, mDoCompare);
    }


    @Test
    public void canDisplayNonTextured() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial redMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial greenMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, redMtl);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, greenMtl);

        redMtl.setDiffuseColor(1, 0, 0, 1);
        greenMtl.setDiffuseColor(0, 1, 0, 1);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(10, 10, 10);
        cylinder2.setName("cylinder2");
        mRoot.addChildObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(20);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayNonTextured", mWaiter, mDoCompare);
    }

    @Test
    public void attachBillboard() throws TimeoutException {

        GVRContext ctx = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));

        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        quadObj1.getTransform().setPosition(0.8f, 0, -2);
        quadObj1.getTransform().setRotationByAxis(30, 1,1,1);
        quadObj1.attachComponent(new GVRBillboard(ctx));
        scene.getMainCameraRig().addChildObject(quadObj1);

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        quadObj2.getTransform().setPosition(-1.0f, 0.8f, -2);
        quadObj2.getTransform().setRotationByAxis(-45, 0,1,0);
        quadObj2.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, 0)));
        scene.getMainCameraRig().addChildObject(quadObj2);

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 1.1f, 1.1f, tex);
        quadObj3.getTransform().setPosition(-0.5f, -0.8f, -1.4f);
        quadObj3.getTransform().setRotationByAxis(-45, 1,0,0);
        quadObj3.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, -1)));
        scene.getMainCameraRig().addChildObject(quadObj3);
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(10);
        mTestUtils.screenShot(getClass().getSimpleName(), "testBillboards", mWaiter, mDoCompare);
    }


    @Test
    public void attachBillboardCameraOffset() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));

        scene.getMainCameraRig().getTransform().setPosition(0.5f, 1.0f, -0.4f);

        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj1.getTransform().setPosition(0.8f, 1.0f, -3);
        quadObj1.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj2.getTransform().setPosition(-0.8f, -1.0f, -3);
        quadObj2.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj3.getTransform().setPosition(0.8f, -1.0f, -3);
        quadObj3.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj4 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj4.getTransform().setPosition(-0.8f, 1.0f, -3);
        quadObj4.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj5 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj5.getTransform().setPosition(-1.5f, 0.0f, -3);
        quadObj5.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj6 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj6.getTransform().setPosition(1.5f, 0.0f, -3);
        quadObj6.attachComponent(new GVRBillboard(ctx));
        mTestUtils.waitForAssetLoad();

        mRoot.addChildObject(quadObj1);
        mRoot.addChildObject(quadObj2);
        mRoot.addChildObject(quadObj3);
        mRoot.addChildObject(quadObj4);
        mRoot.addChildObject(quadObj5);
        mRoot.addChildObject(quadObj6);

        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testBillboardsCamOffset", mWaiter, mDoCompare);
    }

    @Test
    public void testBillboardOwnersScale() throws TimeoutException {

        GVRContext ctx = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        final float epsilon = 0.00001f;

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        GVRTransform quadTrans1 = quadObj1.getTransform();

        quadObj1.setName("quadObj1");
        quadTrans1.setPosition(0.8f, 0, -2);
        quadTrans1.setRotationByAxis(30, 1,1,1);
        quadTrans1.setScale(5,6,7);
        quadObj1.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        GVRTransform quadTrans2 = quadObj2.getTransform();

        quadObj2.setName("quadObj2");
        quadTrans2.setPosition(-1.0f, 0.8f, -2);
        quadTrans2.setRotationByAxis(-45, 0,1,0);
        quadTrans2.setScale(0.5f,0.6f,0.7f);
        quadObj2.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, 0)));

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 1.1f, 1.1f, tex);
        GVRTransform quadTrans3 = quadObj3.getTransform();

        quadObj3.setName("quadObj3");
        quadTrans3.setPosition(-0.5f, -0.8f, -1.4f);
        quadTrans3.setRotationByAxis(-45, 1,0,0);
        quadTrans3.setScale(1,1,1);
        quadObj3.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, -1)));

        scene.addSceneObject(quadObj1);
        scene.addSceneObject(quadObj2);
        scene.addSceneObject(quadObj3);
        mTestUtils.waitForAssetLoad();

        mTestUtils.waitForXFrames(2);

        float xs = quadTrans1.getScaleX();
        float ys = quadTrans1.getScaleY();
        float zs = quadTrans1.getScaleZ();

        mWaiter.assertTrue(Math.abs(5 - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(6 - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(7 - zs) < epsilon);

        xs = quadTrans2.getScaleX();
        ys = quadTrans2.getScaleY();
        zs = quadTrans2.getScaleZ();
        mWaiter.assertTrue(Math.abs(0.5f - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(0.6f - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(0.7f - zs) < epsilon);

        xs = quadTrans3.getScaleX();
        ys = quadTrans3.getScaleY();
        zs = quadTrans3.getScaleZ();
        mWaiter.assertTrue(Math.abs(1 - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(1 - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(1 - zs) < epsilon);
   }

}
