package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCamera;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRCubemapTexture;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRLightBase;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVROrthogonalCamera;
import org.gearvrf.GVRPerspectiveCamera;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShadowMap;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.joml.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class ShadowTests
{
    private static final String TAG = ShadowTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRSceneObject mSphere;
    private GVRSceneObject mCube;
    private boolean mDoCompare = true;

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
        GVRMaterial blue = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial red = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject background;

        mWaiter.assertNotNull(scene);
        scene.getMainCameraRig().setFarClippingDistance(20.0f);
        background = makeBackground(ctx);
        blue.setDiffuseColor(0, 0, 1, 1);
        red.setDiffuseColor(0.8f, 0, 0, 1);
        red.setSpecularColor(0.6f, 0.3f, 0.6f, 1);
        red.setSpecularExponent(10.0f);
        mSphere = new GVRSphereSceneObject(ctx, true, red);
        mSphere.getTransform().setPosition(0, 0.5f, -3);
        mSphere.setName("sphere");
        mCube = new GVRCubeSceneObject(ctx, true, blue);
        mCube.getTransform().setPosition(0, -0.5f, -3);
        mCube.setName("cube");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        scene.addSceneObject(background);
    }

    void setupShadow(GVRDirectLight light, GVRSceneObject owner)
    {
        owner.attachComponent(light);
        light.setShadowRange(0.1f, 25.0f);
    }

    void setupShadow(GVRSpotLight light, GVRSceneObject owner)
    {
        owner.attachComponent(light);
        light.setShadowRange(0.1f, 25.0f);
    }

    @Test
    public void spotLightAtCornerCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().rotateByAxis(35, 0, 1, 0);
        lightObj.getTransform().setPosition(3, 3, 0);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtCornerCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void spotLightAtFrontCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().setPosition(0, 3, 0);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtFrontCastsShadow", mWaiter, mDoCompare);
    }


    @Test
    public void spotLightAtSideCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-90, 0, 1, 0);
        lightObj.getTransform().setPosition(-3, 0, -3);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtSideCastsShadow", mWaiter, mDoCompare);
    }


    @Test
    public void spotLightAtTopCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-90, 1, 0, 0);
        lightObj.getTransform().setPosition(0, 3, -3);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtTopCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void directLightAtCornerCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().rotateByAxis(35, 0, 1, 0);
        lightObj.getTransform().setPosition(3, 3, 3);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtCornerCastsShadow", mWaiter, mDoCompare);
    }


    @Test
    public void directLightAtFrontCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(10);
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtFrontCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void directLightAtSideCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-90, 0, 1, 0);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtSideCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void directLightAtTopCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        setupShadow(light, lightObj);
        lightObj.getTransform().rotateByAxis(-90, 1, 0, 0);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtTopCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void twoLightsCastShadows() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj1 = new GVRSceneObject(ctx);
        GVRSceneObject lightObj2 = new GVRSceneObject(ctx);
        GVRDirectLight light1 = new GVRDirectLight(ctx);
        GVRSpotLight light2 = new GVRSpotLight(ctx);

        setupShadow(light1, lightObj1);
        lightObj1.getTransform().rotateByAxis(-90, 1, 0, 0);
        setupShadow(light2, lightObj2);
        lightObj2.getTransform().rotateByAxis(-90, 0, 1, 0);
        lightObj2.getTransform().setPosition(-3, 0, -3);
        light2.setInnerConeAngle(30.0f);
        light2.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj1);
        mRoot.addChildObject(lightObj2);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "twoLightsCastShadows", mWaiter, mDoCompare);
    }


    @Test
    public void threeLightsCastShadows() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject lightObj1 = new GVRSceneObject(ctx);
        GVRSceneObject lightObj2 = new GVRSceneObject(ctx);
        GVRSceneObject lightObj3 = new GVRSceneObject(ctx);
        GVRDirectLight light1 = new GVRDirectLight(ctx);
        GVRSpotLight light2 = new GVRSpotLight(ctx);
        GVRSpotLight light3 = new GVRSpotLight(ctx);

        setupShadow(light1, lightObj1);
        lightObj1.getTransform().rotateByAxis(-90, 1, 0, 0);
        setupShadow(light2, lightObj2);
        lightObj2.getTransform().rotateByAxis(-90, 0, 1, 0);
        lightObj2.getTransform().setPosition(-3, 0, -3);
        light2.setInnerConeAngle(30.0f);
        light2.setOuterConeAngle(45.0f);
        setupShadow(light3, lightObj3);
        lightObj3.getTransform().rotateByAxis(-45, 0, 1, 0);
        lightObj3.getTransform().setPosition(0, 3, 0);
        light3.setInnerConeAngle(30.0f);
        light3.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj1);
        mRoot.addChildObject(lightObj2);
        mRoot.addChildObject(lightObj3);
        mRoot.addChildObject(mCube);
        mRoot.addChildObject(mSphere);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "threeLightsCastShadows", mWaiter, mDoCompare);
    }

    GVRSceneObject makeBackground(GVRContext ctx)
    {
        GVRMaterial leftmtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial rightmtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial floormtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial backmtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject rightside =  new GVRSceneObject(ctx, 4.0f, 4.0f);
        GVRSceneObject leftside =  new GVRSceneObject(ctx, 4.0f, 4.0f);
        GVRSceneObject floor =  new GVRSceneObject(ctx, 4.0f, 4.0f);
        GVRSceneObject back = new GVRSceneObject(ctx, 4.0f, 4.0f);
        GVRSceneObject background = new GVRSceneObject(ctx);

        backmtl.setAmbientColor(0.3f, 0.3f, 0.3f, 1.0f);
        backmtl.setDiffuseColor(0.7f, 0.7f, 0.7f, 1.0f);
        rightmtl.setAmbientColor(0.4f, 0.4f, 0.2f, 1.0f);
        rightmtl.setDiffuseColor(0.7f, 0.7f, 0.3f, 1.0f);
        leftmtl.setAmbientColor(0.2f, 0.4f, 0.4f, 1.0f);
        leftmtl.setDiffuseColor(0.4f, 0.7f, 0.7f, 1.0f);
        floormtl.setAmbientColor(0.4f, 0.2f, 0.4f, 1.0f);
        floormtl.setDiffuseColor(0.7f, 0.4f, 0.7f, 1.0f);

        floor.getRenderData().setMaterial(floormtl);
        floor.getRenderData().setCastShadows(false);
        floor.getTransform().rotateByAxis(-90, 1, 0, 0);
        floor.getTransform().setPosition(0, -2.0f, -2.0f);
        floor.getRenderData().setCastShadows(false);

        rightside.getRenderData().setMaterial(rightmtl);
        rightside.getRenderData().setCastShadows(false);
        rightside.getTransform().rotateByAxis(90, 0, 1, 0);
        rightside.getTransform().setPosition(-2.0f, 0.0f, -2.0f);

        leftside.getRenderData().setMaterial(leftmtl);
        leftside.getRenderData().setCastShadows(false);
        leftside.getTransform().rotateByAxis(-90, 0, 1, 0);
        leftside.getTransform().setPosition(2.0f, 0.0f, -2.0f);

        back.getRenderData().setMaterial(backmtl);
        back.getRenderData().setCastShadows(false);
        back.getTransform().setPosition(0.0f, 0.0f, -4.0f);

        background.addChildObject(floor);
        background.addChildObject(rightside);
        background.addChildObject(leftside);
        background.addChildObject(back);
        return background;
    }
}
