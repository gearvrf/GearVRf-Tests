package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRFrustumPicker;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRObjectPicker;
import org.gearvrf.GVRPicker;
//import org.gearvrf.GVRObjectPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class PickerTests
{
    private static final String TAG = PickerTests.class.getSimpleName();
    private GVRTestUtils gvrTestUtils;
    private GVRPicker mPicker;
    private GVRMaterial mBlue;
    private GVRMaterial mRed;
    private PickHandler mPickHandler;
    private Waiter mWaiter;

    /*
     * PickHandler records the number of times onEnter, onExit and onInside
     * are called for each scene object picked. Records number of onNoPick
     * and onPick events.
     */
    class PickHandler implements IPickEvents
    {
        public final class PickInfo
        {
            public GVRSceneObject PickedObj;
            public int  NumEnter;
            public int  NumExit;
            public int  NumInside;
            public int  NumPick;
            public ArrayList<Vector3f> EnterHits;
            public ArrayList<Vector3f> InsideHits;
            public PickInfo()
            {
                PickedObj = null;
                NumEnter = 0;
                NumExit = 0;
                NumInside = 0;
                NumPick = 0;
                EnterHits = new ArrayList<Vector3f>();
                InsideHits = new ArrayList<Vector3f>();
            }
        }

        private Map<String, PickInfo> mPicked = new HashMap<String, PickInfo>();
        private int mNumNoPick = 0;
        private int mNumPick = 0;

        public void reset()
        {
            mPicked.clear();
            mNumPick = 0;
            mNumNoPick = 0;
        }

        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            String name = sceneObj.getName();
            if (name != null)
            {
                PickInfo p = mPicked.get(name);

                if (p == null)
                {
                    p = new PickInfo();
                    p.PickedObj = sceneObj;
                    p.EnterHits.add(new Vector3f(pickInfo.getHitX(), pickInfo.getHitY(), pickInfo.getHitZ()));
                }
                p.NumEnter++;
                mPicked.put(name, p);
                Log.d("Picker", "onEnter %s %f, %f, %f", name, pickInfo.getHitX(), pickInfo.getHitY(), pickInfo.getHitZ());
            }
        }

        public void onExit(GVRSceneObject sceneObj)
        {
            String name = sceneObj.getName();
            if (name != null)
            {
                PickInfo p = mPicked.get(name);

                // onEnter or onPick should be called first
                // It puts the PickInfo in the map
                mWaiter.assertNotNull(p);
                p.NumExit++;
                Log.d("Picker", "onExit %s", name);
            }
        }

        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            String name = sceneObj.getName();
            if (name != null)
            {
                PickInfo p = mPicked.get(name);

                // onEnter or onPick should be called first
                // It puts the PickInfo in the map
                mWaiter.assertNotNull(p);
                p.NumInside++;
                p.InsideHits.add(new Vector3f(pickInfo.getHitX(), pickInfo.getHitY(), pickInfo.getHitZ()));
                Log.d("Picker", "onInside %s %f, %f, %f", name, pickInfo.getHitX(), pickInfo.getHitY(), pickInfo.getHitZ());
            }
        }

        public void onNoPick(GVRPicker picker) { ++mNumNoPick; }
        public void onPick(GVRPicker picker)
        {
            ++mNumPick;
            for (GVRPicker.GVRPickedObject pick : picker.getPicked())
            {
                GVRSceneObject sceneObj = pick.hitObject;
                String name = sceneObj.getName();
                if (name != null)
                {
                    PickInfo p = mPicked.get(name);

                    if (p == null)
                    {
                        p = new PickInfo();
                        p.PickedObj = sceneObj;
                    }
                    mWaiter.assertNotNull(p);
                    p.NumPick++;
                    mPicked.put(name, p);
                    Log.d("Picker", "onPick %s", name);
                }
            }
        }

        public void checkObject(String name, GVRSceneObject pickedObj, int numEnter, int numExit, int numInside, int numPick)
        {
            PickInfo p = mPicked.get(name);

            if (numEnter == 0)
            {
                mWaiter.assertNull(p);
                return;
            }
            mWaiter.assertNotNull(p);
            mWaiter.assertEquals(numEnter, p.NumEnter);
            mWaiter.assertEquals(numExit, p.NumExit);
            mWaiter.assertEquals(numPick, p.NumPick);
            if (numInside > 0)
            {
                mWaiter.assertTrue(p.NumInside >= 0);
            }
            else
            {
                mWaiter.assertEquals(numInside, p.NumInside);
            }
            if (pickedObj != null)
            {
                mWaiter.assertEquals(pickedObj, p.PickedObj);
            }
        }

        public void checkResults(int numPick, int numNoPick)
        {
            mWaiter.assertEquals(numPick, mNumPick);
            mWaiter.assertEquals(numNoPick, mNumNoPick);
        }

        public void checkHits(String name, Vector3f[] enterHits, Vector3f[] insideHits)
        {
            PickInfo p = mPicked.get(name);
            mWaiter.assertNotNull(p);
            if (enterHits != null)
            {
                int j = 0;
                mWaiter.assertFalse(p.EnterHits.size() == 0);
                for (Vector3f enterHit : enterHits)
                {
                    Vector3f pickHit = p.EnterHits.get(j);
                    mWaiter.assertTrue(pickHit.distance(enterHit) < 0.0001f);
                }
            }
            if (insideHits != null)
            {
                int j = 0;
                mWaiter.assertFalse(p.InsideHits.size() == 0);
                for (Vector3f insideHit : insideHits)
                {
                    Vector3f pickHit = p.InsideHits.get(j);
                    mWaiter.assertTrue(pickHit.distance(insideHit) < 0.0001f);
                }
            }
        }

        public void checkNoHits(String name)
        {
            mWaiter.assertNull(mPicked.get(name));
        }

        public void clearResults()
        {
            mPicked.clear();
            mNumPick = 0;
            mNumNoPick = 0;
        }
    }

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

        mBlue = new GVRMaterial(context, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        mBlue.setDiffuseColor(0, 0, 1, 1);
        mRed = new GVRMaterial(context, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        mRed.setDiffuseColor(1, 0, 0, 1);
        mPickHandler = new PickHandler();
    }

    @Test
    public void canPickSphereCollider()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRSphereCollider collider = new GVRSphereCollider(context);

        sphere.setName("sphere");
        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }

    @Test
    public void canPickBoundsCollider()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, true);

        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        sphere.attachComponent(collider);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }

    @Test
    public void canPickMeshColliderSphere()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, false);

        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        sphere.attachComponent(collider);
        sphere.setName("sphere");
        scene.addSceneObject(sphere);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("sphere", sphere, 1, 0, 1, 1);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
    }

    @Test
    public void canPickMeshColliderBox()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject cube = new GVRCubeSceneObject(context, true, mBlue);
        GVRMeshCollider collider = new GVRMeshCollider(context, false);

        cube.getRenderData().setShaderTemplate(GVRPhongShader.class);
        cube.getTransform().setPosition(0, 0, -2);
        cube.attachComponent(collider);
        cube.setName("cube");
        scene.addSceneObject(cube);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("cube", cube, 1, 0, 1, 1);
        mPickHandler.checkHits("cube", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
    }

    @Test
    public void canPickQuad()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject sceneObj = new GVRSceneObject(context, 2.0f, 2.0f);
        GVRMeshCollider collider = new GVRMeshCollider(context, false);
        GVRRenderData rdata = sceneObj.getRenderData();

        sceneObj.attachCollider(collider);
        sceneObj.setName("quad");
        sceneObj.getTransform().setPositionZ(-5.0f);
        rdata.setMaterial(mBlue);
        rdata.setShaderTemplate(GVRPhongShader.class);
        scene.addSceneObject(sceneObj);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("quad", sceneObj, 1, 0, 1, 1);
        mPickHandler.checkHits("quad", new Vector3f[] { new Vector3f(0, 0, 0) }, null);
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
        triangleMesh.setIndices(indices);
        GVRSceneObject sceneObjTriangle = new GVRSceneObject(context, triangleMesh);
        GVRMeshCollider collider = new GVRMeshCollider(context, false);
        GVRRenderData rdata = sceneObjTriangle.getRenderData();

        sceneObjTriangle.attachCollider(collider);
        sceneObjTriangle.setName("Triangle");
        rdata.setMaterial(mBlue);
        rdata.setShaderTemplate(GVRPhongShader.class);
        scene.addSceneObject(sceneObjTriangle);
        sceneObjTriangle.getTransform().setPosition(-2.0f, -4.0f, -15.0f);
        sceneObjTriangle.getTransform().setScale(5, 5, 5);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("Triangle", sceneObjTriangle, 1, 0, 1, 1);
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
        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getRenderData().setShaderTemplate(GVRPhongShader.class);
        box.getTransform().setPosition(0, 0.25f, -1);
        box.attachComponent(collider2);
        scene.addSceneObject(sphere);
        scene.addSceneObject(box);

        gvrTestUtils.waitForXFrames(2);

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
        sphere1.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere1.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        scene.addSceneObject(sphere1);

        final GVRPicker.GVRPickedObject pickedObject = GVRPicker.pickSceneObject(sphere1);
        gvrTestUtils.waitForXFrames(2);
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
        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getRenderData().setShaderTemplate(GVRPhongShader.class);
        box.getTransform().setPosition(-2, 0, -1);
        box.attachComponent(collider2);
        scene.addSceneObject(sphere);
        scene.addSceneObject(box);
        scene.getEventReceiver().addListener(mPickHandler);

        mPicker = new GVRFrustumPicker(context, scene);
        ((GVRFrustumPicker) mPicker).setFrustum(45.0f, 1.0f, 0.1f, 100.0f);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
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
        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere.attachComponent(collider1);
        scene.addSceneObject(sphere);
        box.setName("box");
        box.getRenderData().setShaderTemplate(GVRPhongShader.class);
        box.getTransform().setPosition(-2, 0, -2);

        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        mPicker.setPickRay(0, 0, 0, 1, 0, 0);
        box.attachComponent(mPicker);

        scene.addSceneObject(sphere);
        scene.addSceneObject(box);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
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
        sphere1.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere1.getTransform().setPosition(0, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        sphere2.setName("sphere2");
        sphere2.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere2.getTransform().setPosition(2, 0, 0);
        collider2.setRadius(1.0f);
        sphere2.attachComponent(collider2);
        box.setName("box");
        box.getRenderData().setShaderTemplate(GVRPhongShader.class);
        box.getTransform().setScale(0.25f, 0.25f, 0.25f);
        box.getTransform().setPosition(0.5f, 0, -1.75f);

        scene.getEventReceiver().addListener(mPickHandler);
        Log.d("Picker", "canPickWithObject");
        mPicker = new GVRObjectPicker(context, scene);
        mPicker.setPickRay(0, 0, 0, 1, 0, 0);
        box.attachComponent(mPicker);
        scene.addSceneObject(sphere1);
        scene.addSceneObject(sphere2);
        scene.addSceneObject(box);

        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere1", new Vector3f[] { new Vector3f(0, 0, -2) }, null);
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
        mPicker = new GVRPicker(context, scene);
        mPicker.setEnable(false);
        scene.getEventReceiver().addListener(mPickHandler);
        origin.attachComponent(mPicker);

        sphere1.setName("sphere1");
        sphere1.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere1.getTransform().setPosition(-2, 0, -2);
        collider1.setRadius(1.0f);
        sphere1.attachComponent(collider1);
        scene.addSceneObject(sphere1);
        sphere2.setName("sphere2");
        sphere2.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere2.getTransform().setPosition(2, 0, -2);
        collider2.setRadius(1.0f);
        sphere2.attachComponent(collider2);
        scene.addSceneObject(sphere2);

        Vector3f v = new Vector3f(-4.5f, 0.0f, -2.0f);  // no hits
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(0, 0);
        mPickHandler.checkNoHits("sphere2");
        mPickHandler.checkNoHits("sphere1");
        v.set(-4.0f, 0.0f, -2.0f);      // hit sphere1 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere1", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkNoHits("sphere2");
        mPickHandler.clearResults();
        v.set(4.0f, 0.0f, -2.0f);      // hit sphere2 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere2", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.checkNoHits("sphere1");
        mPickHandler.clearResults();
        v.set(4.5f, 0.0f, -2.0f);      // ho hits
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkNoHits("sphere1");
        mPickHandler.checkNoHits("sphere2");
    }


    @Test
    public void pickQuadFromLeftAndRight()
    {
        GVRContext context = gvrTestUtils.getGvrContext();
        GVRScene scene = gvrTestUtils.getMainScene();
        GVRSceneObject quad1 = new GVRSceneObject(context, 2.0f, 2.0f, null, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRMeshCollider collider1 = new GVRMeshCollider(context, false);
        GVRSceneObject quad2 = new GVRSceneObject(context, 2.0f, 2.0f, null, GVRMaterial.GVRShaderType.BeingGenerated.ID);
        GVRMeshCollider collider2 = new GVRMeshCollider(context, false);
        GVRSceneObject origin = new GVRSceneObject(context);

        scene.addSceneObject(origin);
        mPicker = new GVRPicker(context, scene);
        mPicker.setEnable(false);
        scene.getEventReceiver().addListener(mPickHandler);
        origin.attachComponent(mPicker);

        quad1.setName("quad1");
        quad1.getRenderData().setMaterial(mBlue);
        quad1.getRenderData().setShaderTemplate(GVRPhongShader.class);
        quad1.getTransform().setPosition(-2, 0, -2);
        quad1.attachComponent(collider1);
        scene.addSceneObject(quad1);
        quad2.setName("quad2");
        quad2.getRenderData().setMaterial(mRed);
        quad2.getRenderData().setShaderTemplate(GVRPhongShader.class);
        quad2.getTransform().setPosition(2, 0, -2);
        quad2.attachComponent(collider2);
        scene.addSceneObject(quad2);

        Vector3f v = new Vector3f(-3.05f, 0.0f, -2.0f);  // no hits
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        mPicker.setEnable(true);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(0, 0);
        mPickHandler.checkNoHits("quad2");
        mPickHandler.checkNoHits("quad1");
        v.set(-2.999f, 0.0f, -2.0f);      // hit quad1 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("quad1", new Vector3f[]{new Vector3f(-0.999f, 0, 0)}, null);
        mPickHandler.checkNoHits("quad2");
        mPickHandler.clearResults();
        v.set(2.999f, 0.0f, -2.0f);      // hit quad2 on the left
        v.normalize();
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("quad2", new Vector3f[]{new Vector3f(0.999f, 0, 0)}, null);
        mPickHandler.checkNoHits("quad1");
        mPickHandler.clearResults();
        v.set(3.05f, 0.0f, -2.0f);      // ho hits
        mPicker.setPickRay(0, 0, 0, v.x, v.y, v.z);
        gvrTestUtils.waitForXFrames(2);
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
            bunny.getRenderData().setShaderTemplate(GVRPhongShader.class);
            bunny.getRenderData().setMaterial(mBlue);
            bunny.attachComponent(new GVRMeshCollider(context, false));

            bunny.getTransform().setPositionZ(-10.0f);

            //add the bunny to the scene
            scene.addSceneObject(bunny);

            scene.getEventReceiver().addListener(mPickHandler);

            GVRMesh sphereMesh = context.getAssetLoader().loadMesh(new GVRAndroidResource(context,
                    "PickerTests/sphere.obj"));
            GVRSceneObject sceneObject = new GVRSceneObject(context, sphereMesh);
            sceneObject.getRenderData().setShaderTemplate(GVRPhongShader.class);
            sceneObject.getRenderData().setMaterial(mRed);
            GVRSceneObject parent = new GVRSceneObject(context);
            parent.getTransform().setPosition(0.2f, -0.4f, -0.4f);
            parent.getTransform().setRotation(1.0f, 0.04f, 0.01f, 0.01f);

            sceneObject.getTransform().setPositionZ(-10.0f);

            parent.addChildObject(sceneObject);

            mPicker = new GVRPicker(sceneObject, scene);

            //place the object behind the bunny
            scene.addSceneObject(parent);
            gvrTestUtils.waitForXFrames(2);

            mPickHandler.checkResults(1, 0);
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
        sphere.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphere.getTransform().setPosition(0, 0, -2);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);
        scene.addSceneObject(sphere);
        cube.setName("cube");
        cube.getRenderData().setShaderTemplate(GVRPhongShader.class);
        cube.getTransform().setPosition(-1, 0, -2);
        cube.attachComponent(new GVRMeshCollider(context, false));
        scene.addSceneObject(cube);

        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mPickHandler.clearResults();
        scene.getMainCameraRig().getOwnerObject().getTransform().setPosition(-1, 0, 0);
        gvrTestUtils.waitForXFrames(2);
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("cube", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
    }
}
