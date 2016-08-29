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
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    private boolean mDoCompare = false;

    @Rule
    public ActivityTestRule<TestableActivity> ActivityRule = new ActivityTestRule<TestableActivity>(TestableActivity.class);

    @Before
    public void setUp() throws TimeoutException
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject background = new GVRCubeSceneObject(ctx, false);
        GVRMaterial blue = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRMaterial red = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.BeingGenerated.ID);

        mWaiter.assertNotNull(scene);
        background.getTransform().setScale(10, 10, 10);
        background.getRenderData().setShaderTemplate(GVRPhongShader.class);
        blue.setDiffuseColor(0, 0, 1, 1);
        red.setDiffuseColor(0.8f, 0, 0, 1);
        red.setSpecularColor(0.6f, 0.3f, 0.6f, 1);
        red.setSpecularExponent(10.0f);
        mSphere = new GVRSphereSceneObject(ctx, true, blue);
        mSphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        mSphere.getTransform().setPosition(0, 0, -4);
        mSphere.setName("sphere");
        mCube = new GVRCubeSceneObject(ctx, true, blue);
        mCube.getRenderData().setShaderTemplate(GVRPhongShader.class);
        mCube.getTransform().setPosition(0, 0, -3);
        mCube.setName("cube");
        mRoot = scene.getRoot();
        mWaiter.assertNotNull(mRoot);
        mRoot.addChildObject(background);
    }


    @Test
    public void spotLightAtCornerCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        light.setCastShadow(true);
        lightObj.attachComponent(light);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().rotateByAxis(35, 0, 1, 0);
        lightObj.getTransform().setPosition(3, 3, 3);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtCornerCastsShadow", mWaiter, mDoCompare);
    }

    @Test
    public void spotLightAtFrontCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRSpotLight light = new GVRSpotLight(ctx);

        light.setCastShadow(true);
        lightObj.attachComponent(light);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().setPosition(0, 3, 0);
        light.setInnerConeAngle(30.0f);
        light.setOuterConeAngle(45.0f);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "spotLightAtFrontCastsShadow", mWaiter, mDoCompare);
    }


    @Test
    public void directLightAtCornerCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        light.setCastShadow(true);
        lightObj.attachComponent(light);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        lightObj.getTransform().rotateByAxis(35, 0, 1, 0);
        lightObj.getTransform().setPosition(3, 3, 3);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mCube);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtCornerCastsShadow", mWaiter, mDoCompare);
    }


    @Test
    public void directLightAtFrontCastsShadow() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRDirectLight light = new GVRDirectLight(ctx);

        light.setCastShadow(true);
        lightObj.attachComponent(light);
        lightObj.getTransform().rotateByAxis(-45, 1, 0, 0);
        mRoot.addChildObject(lightObj);
        mRoot.addChildObject(mSphere);
        scene.bindShaders();
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "directLightAtFrontCastsShadow", mWaiter, mDoCompare);
    }
}
