package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBoundsPicker;
import org.gearvrf.GVRBoxCollider;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRFrustumPicker;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumSet;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class PickerTests
{
    private static final int NUM_WAIT_FRAMES = 4;
    private GVRTestUtils gvrTestUtils;
    private GVRPicker mPicker;
    private GVRMaterial mBlue;
    private GVRMaterial mRed;
    private PickHandler mPickHandler;
    private Waiter mWaiter;

    public PickerTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class)
    {
        protected void afterActivityFinished() {
            GVRScene scene = gvrTestUtils.getMainScene();
            if (scene != null) {
                scene.clear();
            }
        }
    };

    @Before
    public void setUp() throws TimeoutException
    {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
        GVRContext context = gvrTestUtils.getGvrContext();

        mBlue = new GVRMaterial(context, GVRMaterial.GVRShaderType.Phong.ID);
        mBlue.setDiffuseColor(0, 0, 1, 1);
        mRed = new GVRMaterial(context, GVRMaterial.GVRShaderType.Phong.ID);
        mRed.setDiffuseColor(1, 0, 0, 1);
        mPickHandler = new PickHandler(mWaiter);
    }

    @Test
    public void canPickBoxCollider()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject box = new GVRCubeSceneObject(context, true, mBlue);
        GVRBoxCollider collider = new GVRBoxCollider(context);

        box.setName("box");
        box.getTransform().setPosition(0, 0, -2);
        collider.setHalfExtents(0.5f, 0.5f, 0.5f);
        box.attachComponent(collider);
        scene.addSceneObject(box);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("box", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
    }

    @Test
    public void canPickSphereCollider()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider = new GVRSphereCollider(context);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }

    @Test
    public void canSendEventsToHitObjects()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider = new GVRSphereCollider(context);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        gvrTestUtils.waitForXFrames(1);

        sphere.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(scene, false);
        mPicker.setEventOptions(EnumSet.of(GVRPicker.EventOptions.SEND_TO_HIT_OBJECT, GVRPicker.EventOptions.SEND_PICK_EVENTS));
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }


    @Test
    public void canPickMultiple()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);

        GVRSceneObject box = new GVRCubeSceneObject(context, true, mRed);
        GVRBoxCollider collider2 = new GVRBoxCollider(context);

        box.setName("box");
        box.getTransform().setPosition(0, 0, -2);
        collider2.setHalfExtents(0.5f, 0.5f, 0.5f);
        box.attachComponent(collider2);
        scene.addSceneObject(box);


        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(scene, false);
        mPicker.setPickClosest(false);
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkHits("box", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
    }

    @Test
    public void canPickBoundsCollider()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, true);

        sphere.getTransform().setPosition(0, 0, -2);
        sphere.attachComponent(collider);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        gvrTestUtils.waitForXFrames(1);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }

    @Test
    public void canPickMeshColliderSphere()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, sphere.getRenderData().getMesh(), true);

        sphere.getTransform().setPosition(0, 0, -2);
        sphere.attachComponent(collider);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        gvrTestUtils.waitForXFrames(1);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkObject("sphere", sphere, 1, 0, NUM_WAIT_FRAMES - 1);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkTexCoords("sphere", new Vector2f[] { new Vector2f(0.75f, 0.5f) }, null);
    }

    @Test
    public void canPickMeshColliderBox()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject cube = new GVRCubeSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, cube.getRenderData().getMesh(), true);

        cube.getTransform().setPosition(0, 0, -2);
        cube.attachComponent(collider);
        cube.setName("cube");
        scene.addSceneObject(cube);
        gvrTestUtils.waitForXFrames(1);
        mPicker = new GVRPicker(context, scene);
        mPicker.getEventReceiver().addListener(mPickHandler);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkObject("cube", cube, 1, 0, NUM_WAIT_FRAMES - 1);
        mPickHandler.checkHits("cube", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
        mPickHandler.checkTexCoords("cube", new Vector2f[] { new Vector2f(0.5f, 0.5f) }, null);
    }

    @Test
    public void canPickQuad()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sceneObj = new GVRSceneObject(context, 2.0f, 2.0f);
        GVRMeshCollider collider = new GVRMeshCollider(context, sceneObj.getRenderData().getMesh(), true);
        GVRRenderData rdata = sceneObj.getRenderData();

        sceneObj.attachCollider(collider);
        sceneObj.setName("quad");
        sceneObj.getTransform().setPositionZ(-5.0f);
        rdata.setMaterial(mBlue);
        scene.addSceneObject(sceneObj);
        gvrTestUtils.waitForXFrames(1);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkObject("quad", sceneObj, 1, 0, NUM_WAIT_FRAMES - 1);
        mPickHandler.checkHits("quad", new Vector3f[] { new Vector3f(0, 0, 0) }, null);
        mPickHandler.checkTexCoords("quad", new Vector2f[] { new Vector2f(0.5f, 0.5f) }, null);
    }

    @Test
    public void canPickTriangle()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRMesh triangleMesh = new GVRMesh(context);
        float[] a = {0f, 0f, 0f, 5f, 5f, 5f, 1f, 4f, 1f};
        char indices[] = { 0, 1, 2 };
        triangleMesh.setVertices(a);
        triangleMesh.setTriangles(indices);
        GVRSceneObject sceneObjTriangle = new GVRSceneObject(context, triangleMesh);
        GVRMeshCollider collider = new GVRMeshCollider(context, sceneObjTriangle.getRenderData().getMesh(), true);
        GVRRenderData rdata = sceneObjTriangle.getRenderData();

        sceneObjTriangle.attachCollider(collider);
        sceneObjTriangle.setName("Triangle");
        rdata.setMaterial(mBlue);
        scene.addSceneObject(sceneObjTriangle);
        sceneObjTriangle.getTransform().setPosition(-2.0f, -4.0f, -15.0f);
        sceneObjTriangle.getTransform().setScale(5, 5, 5);
        gvrTestUtils.waitForXFrames(1);

        mPicker = new GVRPicker(scene, true);
        mPicker.getEventReceiver().addListener(mPickHandler);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkObject("Triangle", sceneObjTriangle, 1, 0, NUM_WAIT_FRAMES - 1);
        mPickHandler.checkHits("Triangle", new Vector3f[] { new Vector3f(0.4f, 0.8f, 0.4f) }, null);
    }

    @Test
    public void testPickObjects()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);
        GVRSceneObject box = new GVRCubeSceneObject(context, true, mRed);
        GVRMeshCollider collider2 = new GVRMeshCollider(context, false);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getTransform().setPosition(0, 0.25f, -1);
        box.attachComponent(collider2);
        scene.addSceneObject(sphere);
        scene.addSceneObject(box);

        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);

        //test picking after the scene is rendered
        GVRPicker.GVRPickedObject picked[] = GVRPicker.pickObjects(scene, null, 0, 0, 0, 0, 0, -1.0f);
        Log.d("Picker", "testPickObjects");

        mWaiter.assertNotNull(picked);
        mWaiter.assertTrue(picked.length == 2);
        GVRPicker.GVRPickedObject hit1 = picked[0];
        GVRPicker.GVRPickedObject hit2 = picked[1];
        mWaiter.assertNotNull(hit1);
        mWaiter.assertEquals("box", hit1.hitObject.getName());
        mWaiter.assertEquals(0.0f, hit1.hitLocation[0]);
        mWaiter.assertEquals(-0.25f, hit1.hitLocation[1]);
        mWaiter.assertEquals(0.5f, hit1.hitLocation[2]);
        mWaiter.assertNotNull(hit2);
        mWaiter.assertEquals("sphere", hit2.hitObject.getName());
        mWaiter.assertEquals(0.0f, hit2.hitLocation[0]);
        mWaiter.assertEquals(0.0f, hit2.hitLocation[1]);
        mWaiter.assertEquals(1.0f, hit2.hitLocation[2]);
    }

    @Test
    public void canPickObjectWithRay()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);

        sphere1.setName("sphere1");
        sphere1.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        scene.addSceneObject(sphere1);

        final GVRPicker.GVRPickedObject pickedObject = GVRPicker.pickSceneObject(sphere1);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mWaiter.assertEquals(1.0f, pickedObject.getHitDistance());
    }


    @Test
    public void canPickWithFrustum()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);
        GVRSceneObject box = new GVRCubeSceneObject(context, true, mRed);
        GVRMeshCollider collider2 = new GVRMeshCollider(context, false);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getTransform().setPosition(-2, 0, -1);
        box.attachComponent(collider2);
        scene.addSceneObject(sphere);
        scene.addSceneObject(box);
        gvrTestUtils.waitForXFrames(1);

        GVRFrustumPicker picker = new GVRFrustumPicker(context, scene);
        picker.setFrustum(45.0f, 1.0f, 0.1f, 100.0f);
        picker.getEventReceiver().addListener(mPickHandler);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, -2) }, null);
        mPickHandler.checkNoHits("box");
    }


    @Test
    public void canPickFromObject()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);
        GVRSceneObject box = new GVRCubeSceneObject(context, true, mRed);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getTransform().setPosition(-2, 0, -2);
        scene.addSceneObject(sphere);
        scene.addSceneObject(box);
        gvrTestUtils.waitForXFrames(1);

        mPicker = new GVRPicker(context, scene);
        mPicker.setPickRay(0, 0, 0, 1, 0, 0);
        mPicker.getEventReceiver().addListener(mPickHandler);
        box.attachComponent(mPicker);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(-1, 0, 0) }, null);
    }

    @Test
    public void canPickWithObject()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(context, true, mBlue);
        GVRSceneObject sphere2 = new GVRSphereSceneObject(context, true, mRed);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);
        GVRSphereCollider collider2 = new GVRSphereCollider(context);
        GVRSceneObject box = new GVRCubeSceneObject(context, true, mRed);

        sphere1.setName("sphere1");
        sphere1.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        sphere2.setName("sphere2");
        sphere2.getTransform().setPosition(2, 0, 0);
        collider2.setRadius(1.0f);
        sphere2.attachComponent(collider2);
        box.setName("box");
        box.getTransform().setScale(0.25f, 0.25f, 0.25f);
        box.getTransform().setPosition(0.5f, 0, -1.75f);
        scene.addSceneObject(sphere1);
        scene.addSceneObject(sphere2);
        scene.addSceneObject(box);

        GVRSceneObject.BoundingVolume bv = box.getBoundingVolume();
        Vector3f boxCtr = new Vector3f(0.5f, 0, -1.75f);
        Vector3f sphereCtr = new Vector3f(0, 0, -2.0f);
        Vector3f sphereToBox = new Vector3f();
        sphereCtr.sub(boxCtr, sphereToBox);
        float dist = sphereToBox.length();
        sphereToBox.normalize();
        gvrTestUtils.waitForXFrames(1);

        GVRBoundsPicker picker = new GVRBoundsPicker(scene, true);
        picker.addCollidable(box);
        picker.getEventReceiver().addListener(mPickHandler);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere1", new Vector3f[] { new Vector3f(-0.894427f, 0, -0.447214f) }, null);
        mPickHandler.checkNoHits("sphere2");
    }

    @Test

    public void pickSphereFromLeftAndRight()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider1 = new GVRSphereCollider(context);
        GVRSceneObject sphere2 = new GVRSphereSceneObject(context, true, mRed);
        GVRSphereCollider collider2 = new GVRSphereCollider(context);
        GVRSceneObject origin = new GVRSceneObject(context);

        scene.addSceneObject(origin);
        mPicker = new GVRPicker(scene, false);
        mPicker.getEventReceiver().addListener(mPickHandler);
        origin.attachComponent(mPicker);

        sphere1.setName("sphere1");
        sphere1.getTransform().setPosition(-2, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        scene.addSceneObject(sphere1);
        sphere2.setName("sphere2");
        sphere2.getTransform().setPosition(2, 0, -2);
        collider2.setRadius(1.0f);
        sphere2.attachComponent(collider2);
        scene.addSceneObject(sphere2);
        gvrTestUtils.waitForXFrames(1);

        Vector3f v = new Vector3f(-4.5f, 0.0f, -2.0f);  // no hits
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkNoHits("sphere2");
        mPickHandler.checkNoHits("sphere1");
        v.set(-4.0f, 0.0f, -2.0f);      // hit sphere1 on the left
        v.normalize();
        mPickHandler.clearResults();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere1", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkNoHits("sphere2");
        mPickHandler.clearResults();
        v.set(4.0f, 0.0f, -2.0f);      // hit sphere2 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere2", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkNoHits("sphere1");
        mPickHandler.clearResults();
        v.set(4.5f, 0.0f, -2.0f);      // ho hits
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.checkNoHits("sphere1");
        mPickHandler.checkNoHits("sphere2");
    }


    @Test
    public void pickQuadFromLeftAndRight()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject quad1 = new GVRSceneObject(context, 2.0f, 2.0f, null, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMeshCollider collider1 = new GVRMeshCollider(context, quad1.getRenderData().getMesh(), true);
        GVRSceneObject quad2 = new GVRSceneObject(context, 2.0f, 2.0f, null, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMeshCollider collider2 = new GVRMeshCollider(context, quad2.getRenderData().getMesh(), true);
        GVRSceneObject origin = new GVRSceneObject(context);

        scene.addSceneObject(origin);
        mPicker = new GVRPicker(context, scene);
        mPicker.setEnable(false);
        scene.getEventReceiver().addListener(mPickHandler);
        origin.attachComponent(mPicker);

        quad1.setName("quad1");
        quad1.getRenderData().setMaterial(mBlue);
        quad1.getTransform().setPosition(-2, 0, -2);
        quad1.attachComponent(collider1);
        scene.addSceneObject(quad1);
        quad2.setName("quad2");
        quad2.getRenderData().setMaterial(mRed);
        quad2.getTransform().setPosition(2, 0, -2);
        quad2.attachComponent(collider2);
        scene.addSceneObject(quad2);

        Vector3f v = new Vector3f(-3.05f, 0.0f, -2.0f);  // no hits
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkNoHits("quad2");
        mPickHandler.checkNoHits("quad1");
        v.set(-2.999f, 0.0f, -2.0f);      // hit quad1 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(2 * NUM_WAIT_FRAMES);
        mPickHandler.checkHits("quad1", new Vector3f[]{new Vector3f(-0.999f, 0, 0)}, null);
        mPickHandler.checkTexCoords("quad1", new Vector2f[]{new Vector2f(0.0005f, 0.5f)}, null);
        mPickHandler.checkNoHits("quad2");
        mPickHandler.clearResults();
        v.set(2.999f, 0.0f, -2.0f);      // hit quad2 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("quad2", new Vector3f[]{new Vector3f(0.999f, 0, 0)}, null);
        mPickHandler.checkTexCoords("quad2", new Vector2f[]{new Vector2f(0.9995f, 0.5f)}, null);
        mPickHandler.checkNoHits("quad1");
        mPickHandler.clearResults();
        v.set(3.05f, 0.0f, -2.0f);      // ho hits
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.checkNoHits("quad1");
        mPickHandler.checkNoHits("quad2");
    }

    @Test
    public void canPickMeshWithObject()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        try
        {
            GVRMesh mesh = context.getAssetLoader().loadMesh(new GVRAndroidResource(context,
                    "PickerTests/bunny.obj"));
            GVRSceneObject bunny = new GVRSceneObject(context, mesh);
            bunny.getRenderData().setMaterial(mBlue);
            bunny.attachComponent(new GVRMeshCollider(context, false));

            bunny.getTransform().setPositionZ(-10.0f);

            //add the bunny to the scene
            scene.addSceneObject(bunny);


            GVRMesh sphereMesh = context.getAssetLoader().loadMesh(new GVRAndroidResource(context,
                    "PickerTests/sphere.obj"));
            GVRSceneObject sceneObject = new GVRSceneObject(context, sphereMesh);
            sceneObject.getRenderData().setMaterial(mRed);
            GVRSceneObject parent = new GVRSceneObject(context);
            parent.getTransform().setPosition(0.2f, -0.4f, -0.4f);
            parent.getTransform().setRotation(1.0f, 0.04f, 0.01f, 0.01f);

            sceneObject.getTransform().setPositionZ(-10.0f);

            parent.addChildObject(sceneObject);
            gvrTestUtils.waitForXFrames(1);

            GVRBoundsPicker picker = new GVRBoundsPicker(scene, true);
            picker.addCollidable(sceneObject);
            picker.getEventReceiver().addListener(mPickHandler);

            //place the object behind the bunny
            scene.addSceneObject(parent);
            gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
            mPickHandler.countPicks(NUM_WAIT_FRAMES);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test


    public void canPickAfterCameraMove()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSceneObject cube = new GVRCubeSceneObject(context,true, mRed);
        GVRSphereCollider collider = new GVRSphereCollider(context);

        sphere.setName("sphere");
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        cube.setName("cube");
        cube.getTransform().setPosition(-1, 0, -2);
        cube.attachComponent(new GVRMeshCollider(context, cube.getRenderData().getMesh(), true));
        scene.addSceneObject(cube);

        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.clearResults();
        scene.getMainCameraRig().getOwnerObject().getTransform().setPosition(-1, 0, 0);
        gvrTestUtils.waitForXFrames(NUM_WAIT_FRAMES);
        mPickHandler.countPicks(NUM_WAIT_FRAMES);
        mPickHandler.checkHits("cube", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
        mPickHandler.checkTexCoords("cube", new Vector2f[] { new Vector2f(0.5f, 0.5f) }, null);
    }
}
