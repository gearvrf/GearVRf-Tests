package org.gearvrf.tester;


import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRImportSettings;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshMorph;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTransform;
import org.gearvrf.GVRVertexBuffer;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimator;
import org.gearvrf.animation.GVRPose;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRSkeleton;
import org.gearvrf.animation.keyframe.GVRAnimationBehavior;
import org.gearvrf.animation.keyframe.GVRAnimationChannel;
import org.gearvrf.animation.keyframe.GVRSkeletonAnimation;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class AssetAnimationTests
{
    private static final String TAG = AssetAnimationTests.class.getSimpleName();
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
    public void canStartAnimations() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.START_ANIMATIONS));
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", settings, false, scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        GVRAnimator animator = (GVRAnimator) model.getComponent(GVRAnimator.getComponentType());
        animator.setRepeatMode(GVRRepeatMode.REPEATED);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "canStartAnimations", mWaiter, mDoCompare);
    }

    class MeshVisitorNoAnim implements GVRSceneObject.ComponentVisitor
    {
        public boolean visit(GVRComponent comp)
        {
            GVRRenderData rdata = (GVRRenderData) comp;
            GVRMesh mesh = rdata.getMesh();
            if (mesh != null)
            {
                GVRVertexBuffer vbuf = mesh.getVertexBuffer();
                mWaiter.assertNotNull(vbuf);
                mWaiter.assertTrue(vbuf.hasAttribute("a_position"));
                mWaiter.assertTrue(vbuf.hasAttribute("a_normal"));
                mWaiter.assertFalse(vbuf.hasAttribute("a_bone_weights"));
                mWaiter.assertFalse(vbuf.hasAttribute("a_bone_indices"));
            }
            return true;
        }
    }

    @Test
    public void canLoadModelWithoutAnimation() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_ANIMATION));
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(5);
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNull(model.getComponent(GVRAnimator.getComponentType()));
        model.forAllComponents(new MeshVisitorNoAnim(), GVRRenderData.getComponentType());
    }

    @Test
    public void canLoadX3DModelWithoutAnimation() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRSceneObject model = null;

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedSettingsWith(EnumSet.of(GVRImportSettings.NO_ANIMATION));
            model = ctx.getAssetLoader().loadModel(GVRTestUtils.GITHUB_URL + "x3d/animation/animation04.x3d", settings, true, (GVRScene) null);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(5);
        mHandler.checkAssetLoaded(null, 4);
        mHandler.checkAssetErrors(0, 0);
        mWaiter.assertNull(model.getComponent(GVRAnimator.getComponentType()));
        model.forAllComponents(new MeshVisitorNoAnim(), GVRRenderData.getComponentType());
    }

    @Test
    public void testSkeleton() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;
        GVRCameraRig rig = scene.getMainCameraRig();

        rig.getLeftCamera().setBackgroundColor(Color.LTGRAY);
        rig.getRightCamera().setBackgroundColor(Color.LTGRAY);
        rig.getTransform().rotateByAxis(0, 1, 0, 90);

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel("jassimp/astro_boy.dae", scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        mWaiter.assertNotNull(scene.getSceneObjectByName("astro_boy.dae"));
        GVRAnimator animator = (GVRAnimator) model.getComponent(GVRAnimator.getComponentType());
        GVRSkeleton skel = null;
        Quaternionf q = new Quaternionf();

        for (int i = 0; i < animator.getAnimationCount(); ++i)
        {
            GVRAnimation anim = animator.getAnimation(i);

            if (anim instanceof GVRSkeletonAnimation)
            {
                GVRSkeletonAnimation skelAnim = (GVRSkeletonAnimation) anim;
                skel = skelAnim.getSkeleton();
                break;
            }
        }
        mWaiter.assertNotNull(skel);
        int rightShoulder = skel.getBoneIndex("astroBoy_newSkeleton_R_shoulder");
        int leftShoulder = skel.getBoneIndex("astroBoy_newSkeleton_L_shoulder");
        GVRPose pose = new GVRPose(skel);

        mWaiter.assertTrue(rightShoulder >= 0);
        mWaiter.assertTrue(leftShoulder >= 0);
        q.fromAxisAngleDeg(1, 0, 0, -45);
        pose.setLocalRotation(leftShoulder, q.x, q.y, q.z, q.w);
        pose.setLocalRotation(rightShoulder, q.x, q.y, q.z, q.w);
        skel.applyPose(pose, GVRSkeleton.BIND_POSE_RELATIVE);
        skel.poseToBones();
        skel.computeSkinPose();

        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeleton", mWaiter, mDoCompare);
    }

    public void centerModel(GVRSceneObject model, GVRTransform camTrans)
    {
        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        float x = camTrans.getPositionX();
        float y = camTrans.getPositionY();
        float z = camTrans.getPositionZ();
        float sf = 1 / bv.radius;
        model.getTransform().setScale(sf, sf, sf);
        bv = model.getBoundingVolume();
        model.getTransform().setPosition(x - bv.center.x, y - bv.center.y, z - bv.center.z - 1.5f * bv.radius);
    }

    @Test
    public void jassimpMorphTest() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRPointLight pointLight = new GVRPointLight(ctx);
        GVRCameraRig rig = scene.getMainCameraRig();
        GVRSceneObject model = null;

        rig.getCenterCamera().setBackgroundColor(Color.LTGRAY);
        rig.getLeftCamera().setBackgroundColor(Color.LTGRAY);
        rig.getRightCamera().setBackgroundColor(Color.LTGRAY);
        pointLight.setDiffuseIntensity(0.8f, 0.8f, 08f, 1.0f);
        pointLight.setSpecularIntensity(0.8f, 0.8f, 08f, 1.0f);
        lightObj.attachComponent(pointLight);
        lightObj.getTransform().setPosition(-1.0f, 1.0f, 0);
        scene.addSceneObject(lightObj);

        try
        {
            EnumSet<GVRImportSettings> settings = GVRImportSettings.getRecommendedMorphSettings();
            model = ctx.getAssetLoader().loadModel("jassimp/faceBlendShapes_center.fbx", settings, true, null);
            centerModel(model, rig.getTransform());
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }

        String[] shapeNames = { "Jason_Shapes_Ref:JasnNeutral:Default", "Jaw_Open", "Smile" };
        float[] weights = new float[] { 1, 0 };
        GVRMeshMorph morph = addMorph(model, shapeNames);
        morph.setWeights(weights);

        scene.addSceneObject(model);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "jassimpMorphTest", mWaiter, mDoCompare);
    }

    private GVRMeshMorph addMorph(GVRSceneObject model, String shapeNames[])
    {
        GVRSceneObject baseShape = model.getSceneObjectByName(shapeNames[0]);
        GVRMeshMorph morph = new GVRMeshMorph(model.getGVRContext(), 2, false);

        baseShape.attachComponent(morph);
        for (int i = 1; i < shapeNames.length; ++i)
        {
            GVRSceneObject blendShape = model.getSceneObjectByName(shapeNames[i]);
            blendShape.getParent().removeChildObject(blendShape);
            morph.setBlendShape(i - 1, blendShape);
        }
        morph.update();
        return morph;
    }

    @Test
    public void testSkeletonAnimation() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject model = null;
        GVRCameraRig rig = scene.getMainCameraRig();

        rig.getLeftCamera().setBackgroundColor(Color.LTGRAY);
        rig.getRightCamera().setBackgroundColor(Color.LTGRAY);
        rig.getTransform().rotateByAxis(0, 1, 0, 90);

        ctx.getEventReceiver().addListener(mHandler);
        try
        {
            model = ctx.getAssetLoader().loadModel(GVRTestUtils.GITHUB_URL + "jassimp/Andromeda/Andromeda.dae", scene);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        mTestUtils.waitForAssetLoad();
        mHandler.centerModel(model, scene.getMainCameraRig().getTransform());
        mWaiter.assertNotNull(scene.getSceneObjectByName("Andromeda.dae"));
        List<GVRComponent> components = model.getAllComponents(GVRSkeleton.getComponentType());

        mWaiter.assertTrue(components.size() > 0);
        GVRSkeleton skel = (GVRSkeleton) components.get(0);
        mWaiter.assertNotNull(skel);

        GVRSkeletonAnimation skelAnim = makeSkeletonAnimation(skel);

        skelAnim.animate(0);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation0", mWaiter, mDoCompare);
        skelAnim.animate(1);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation1", mWaiter, mDoCompare);
        skelAnim.animate(2);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation2", mWaiter, mDoCompare);
    }

    public GVRSkeletonAnimation makeSkeletonAnimation(GVRSkeleton skel)
    {
        Quaternionf q = new Quaternionf();
        Matrix4f mtx = new Matrix4f();
        GVRPose pose = new GVRPose(skel);
        int leftShoulderBone = skel.getBoneIndex("mixamorig_LeftShoulder");
        int rightShoulderBone = skel.getBoneIndex("mixamorig_RightShoulder");
        Vector3f v = new Vector3f();
        float[] rotKeys = new float[] { 0, 0, 0, 0, 1,   2, 0, 0, 0, 1 };
        float[] posKeys = new float[] { 0, 0, 0, 0,      2, 0, 0, 0 };

        pose.copy(skel.getBindPose());
        pose.getLocalMatrix(leftShoulderBone, mtx);
        mtx.getTranslation(v);
        mtx.getUnnormalizedRotation(q);
        posKeys[1] = v.x;
        posKeys[2] = v.y;
        posKeys[3] = v.z;
        rotKeys[1] = q.x;
        rotKeys[2] = q.y;
        rotKeys[3] = q.z;
        rotKeys[4] = q.w;

        q.fromAxisAngleDeg(1, 0, 0, -45);
        mtx.rotate(q);
        pose.setLocalMatrix(leftShoulderBone, mtx);
        mtx.getTranslation(v);
        mtx.getUnnormalizedRotation(q);
        posKeys[5] = v.x;
        posKeys[6] = v.y;
        posKeys[7] = v.z;
        rotKeys[6] = q.x;
        rotKeys[7] = q.y;
        rotKeys[8] = q.z;
        rotKeys[9] = q.w;
        GVRAnimationChannel leftShoulder = new GVRAnimationChannel("mixamorig_LeftShoulder", posKeys, rotKeys, null,
                                                                   GVRAnimationBehavior.DEFAULT, GVRAnimationBehavior.DEFAULT);
        rotKeys = new float[] { 0, 0, 0, 0, 1,   2, 0, 0, 0, 1 };
        posKeys = new float[] { 0, 0, 0, 0,      2, 0, 0, 0 };
        pose.getLocalMatrix(rightShoulderBone, mtx);
        mtx.getTranslation(v);
        mtx.getUnnormalizedRotation(q);
        posKeys[1] = v.x;
        posKeys[2] = v.y;
        posKeys[3] = v.z;
        rotKeys[1] = q.x;
        rotKeys[2] = q.y;
        rotKeys[3] = q.z;
        rotKeys[4] = q.w;

        q.fromAxisAngleDeg(1, 0, 0, -45);
        mtx.rotate(q);
        pose.setLocalMatrix(rightShoulderBone, mtx);
        mtx.getTranslation(v);
        mtx.getUnnormalizedRotation(q);
        posKeys[5] = v.x;
        posKeys[6] = v.y;
        posKeys[7] = v.z;
        rotKeys[6] = q.x;
        rotKeys[7] = q.y;
        rotKeys[8] = q.z;
        rotKeys[9] = q.w;
        GVRAnimationChannel rightShoulder = new GVRAnimationChannel("mixamorig_RightShoulder", posKeys, rotKeys, null,
                                                                   GVRAnimationBehavior.DEFAULT, GVRAnimationBehavior.DEFAULT);
        GVRSkeletonAnimation skelAnim = new GVRSkeletonAnimation("LiftArms", skel, 2);
        skelAnim.setTarget(skel.getOwnerObject());
        skelAnim.addChannel("mixamorig_LeftShoulder", leftShoulder);
        skelAnim.addChannel("mixamorig_RightShoulder", rightShoulder);
        return skelAnim;
    }

}
