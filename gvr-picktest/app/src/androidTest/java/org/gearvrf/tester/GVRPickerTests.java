package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.tester.GVRTestUtils.OnInitCallback;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class GVRPickerTests
{


    private static final String TAG = GVRPickerTests.class.getSimpleName();
    private static final int NUM_NORMALS_IN_QUAD = 12;
    private GVRTestUtils gvrTestUtils;
    private GVRPicker mPicker;
    private GVRMaterial mBlue;
    private PickHandler mPickHandler;


    public class PickHandler implements IPickEvents
    {
        public int SphereHits = 0;
        public int CubeHits = 0;

        public void reset()
        {
            SphereHits = 0;
            CubeHits = 0;
        }
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            sceneObj.getRenderData().getMaterial().setVec4("diffuse_color", 1, 0, 0, 1);
            if (sceneObj.getName().equals("sphere"))
            {
                ++SphereHits;
            }
            else if (sceneObj.getName().equals("cube"))
            {
                ++CubeHits;
            }
        }
        public void onExit(GVRSceneObject sceneObj)
        {
            sceneObj.getRenderData().getMaterial().setVec4("diffuse_color", 0, 0, 1, 1);
        }
        public void onNoPick(GVRPicker picker) { }
        public void onPick(GVRPicker picker) { }
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo) { }
    }

    public GVRPickerTests() {
        super();
    }

    @Rule
    public ActivityTestRule<TestableActivity> mActivityRule = new
            ActivityTestRule<TestableActivity>(TestableActivity.class);

    @Before
    public void setUp() throws TimeoutException
    {
        gvrTestUtils = new GVRTestUtils(mActivityRule.getActivity());
        final Waiter waiter = new Waiter();
        gvrTestUtils.setOnInitCallback(new OnInitCallback() {
            public void onInit(GVRContext ctx)
            {
                GVRContext context = gvrTestUtils.getGvrContext();

                mBlue = new GVRMaterial(context, GVRMaterial.GVRShaderType.BeingGenerated.ID);
                mBlue.setDiffuseColor(0, 0, 1, 1);
                mPickHandler = new PickHandler();
                waiter.resume();
            }
        });
        waiter.await(4000);
    }

    class PickCallback implements GVRTestUtils.OnRenderCallback
    {
        public GVRPicker Picker;
        public GVRScene Scene;
        public GVRSceneObject PickMe;

        public PickCallback(GVRPicker picker, GVRScene scene, GVRSceneObject pickme)
        {
            Picker = picker;
            Scene = scene;
            PickMe = pickme;
        }

        public void onSceneRendered()
        {
            Picker.onDrawFrame(0);
            GVRPicker.GVRPickedObject[] picked = Picker.getPicked();

            Assert.assertNotNull(picked);
            Assert.assertEquals(picked.length, 1);
            Assert.assertEquals(picked[0].getHitObject(), PickMe);
            Scene.removeSceneObject(PickMe);
            Picker.setEnable(false);
        }
    }

    @Test
    public void canPickSphere()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider = new GVRSphereCollider(context);

        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        GVRPicker picker = new GVRPicker(context, scene);
        gvrTestUtils.setOnRenderCallback(new PickCallback(picker, scene, sphere));
    }

    @Test
    public void canPickCube()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject cube = new GVRSphereSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, true);

        cube.getRenderData().setShaderTemplate(GVRPhongShader.class);
        cube.getTransform().setPosition(0, 0, -2);
        cube.attachComponent(collider);
        scene.addSceneObject(cube);
        GVRPicker picker = new GVRPicker(context, scene);
        gvrTestUtils.setOnRenderCallback(new PickCallback(picker, scene, cube));
    }
}
