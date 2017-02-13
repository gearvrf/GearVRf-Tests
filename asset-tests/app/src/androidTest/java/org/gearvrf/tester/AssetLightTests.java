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
public class AssetLightTests
{
    private static final String TAG = AssetLightTests.class.getSimpleName();
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
    public void jassimpCubeDiffuseDirectional() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_diffuse_directionallight.fbx", 1, 0, "jassimpCubeDiffuseDirectional");
    }

    @Test
    public void jassimpCubeDiffusePoint() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_diffuse_pointlight.fbx", 1, 0, "jassimpCubeDiffusePoint");
    }

    @Test
    public void jassimpCubeDiffuseSpot() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_diffuse_spotlight.fbx", 1, 0, "jassimpCubeDiffuseSpot");
     }
    
    @Test
    public void jassimpCubeDiffuseSpotLinearDecay() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_diffuse_spotlight_linear.fbx", 1, 0, "jassimpCubeDiffuseSpotLinearDecay");
    }

    @Test
    public void jassimpCubeDiffuseSpotLinearDecay9() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_diffuse_spotlight_linear9.fbx", 1, 0, "jassimpCubeDiffuseSpotLinearDecay9");
    }

    @Test
    public void jassimpCubeNormalDiffuseAmbient() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_ambientlight.fbx", 2, 0, "jassimpCubeNormalDiffuseAmbient");
    }

    @Test
    public void jassimpCubeNormalDiffuseDirect() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_directionallight.fbx", 2, 0, "jassimpCubeNormalDiffuseDirect");
    }

    @Test
    public void jassimpCubeNormalDiffusePoint() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_pointlights.fbx", 2, 0, "jassimpCubeNormalDiffusePoint");
    }

    @Test
    public void jassimpCubeNormalDiffuseSpot() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_spotlight.fbx", 2, 0, "jassimpCubeNormalDiffuseSpot");
    }

    @Test
    public void jassimpCubeNormalDiffuseSpotLinearDecay() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_spotlight_linear_decay.fbx", 2, 0, "jassimpCubeNormalDiffuseSpot");
    }

    @Test
    public void jassimpCubeNormalDiffuseSpotQuadraticDecay() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_normal_diffuse_spotlight_quadratic_decay.fbx", 1, 0, "jassimpCubeNormalDiffuseSpotQuadraticDecay");
    }

    @Test
    public void jassimpCubeAmbientTexture() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_ambient_texture.fbx", 1, 0, "jassimpCubeAmbientTexture");
    }

    @Test
    public void jassimpCubeAmbientSpecularTexture() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/jassimp/cube/cube_ambient_specular_texture.fbx", 2, 0, "jassimpCubeAmbientSpecularTexture");
    }

    @Test
    public void x3dPointLight1() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/pointlightsimple.x3d", 1, 0, "x3dPoint1");
    }

    @Test
    public void x3dPointLight2() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/pointlighttest.x3d", 2, 0, "x3dPoint2");
    }

    @Test
    public void x3dPointLightAttenuation() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/pointlightattenuationtest.x3d", 3, 0, "x3dPointLightAttenuation");
    }

    @Test
    public void x3dMultiplePoints() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/pointlightmultilights.x3d", 4, 0, "x3dMultiplePoints");
    }

    @Test
    public void x3dDirectLight() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/directionallight1.x3d", 4, 0, "x3dDirectLight");
    }

    @Test
    public void x3dSpotLight1() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/spotlighttest1.x3d", 4, 0, "x3dSpotLight1");
    }


    @Test
    public void x3dSpotLight2() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/spotlighttest2.x3d", 4, 0, "x3dSpotLight2");
    }

    @Test
    public void x3dSpotLight3() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/spotlighttest3.x3d", 4, 0, "x3dSpotLight3");
    }

    @Test
    public void x3dSpotLight4() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/lighting/spotlighttest4.x3d", 4, 0, "x3dSpotLight4");
    }


    @Test
    public void x3dGenerateNormalsPoint() throws TimeoutException
    {
        mHandler.loadTestModel("https://raw.githubusercontent.com/gearvrf/GearVRf-Tests/master/x3d/generate_normals/nonormalswithptlights.x3d", 2, 0, "x3dGenerateNormalsPoint");
    }
 /*
    @Test
    public void VRBenchmark() throws TimeoutException
    {
        GVRSceneObject model = loadTestModel("sd:GearVRF/benchmark_assets/TestShadowNew.FBX", 2, 0, null);
        model.getTransform().rotateByAxis(90, 0, 1, 0);
        model.getTransform().setPosition(0, -0.5f, 0);
        mTestUtils.waitForXFrames(2000);
    }
    */
}
