package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.utility.Log;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class PickerTests
{
    private static final String TAG = PickerTests.class.getSimpleName();
    private GVRTestUtils gvrTestUtils;
    private GVRPicker mPicker;
    private GVRMaterial mBlue;
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
                    p.NumEnter = 1;
                    p.PickedObj = sceneObj;
                    p.EnterHits.add(new Vector3f(pickInfo.getHitX(), pickInfo.getHitY(), pickInfo.getHitZ()));
                    mPicked.put(name, p);
                }
                else
                {
                    p.NumEnter++;
                }
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
                        p.NumEnter = 1;
                        p.PickedObj = sceneObj;
                        mPicked.put(name, p);
                    }
                    else
                    {
                        mWaiter.assertNotNull(p);
                        p.NumPick++;
                    }
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
    }

    public PickerTests() {
        super();
    }

    @Rule
    public ActivityTestRule<TestableActivity> ActivityRule = new ActivityTestRule<TestableActivity>(TestableActivity.class)
    {
        protected void afterActivityFinished() {
            gvrTestUtils.getMainScene().clear();
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
        Log.d("Picker", "canPickSphereCollider");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mWaiter.resume();
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
        Log.d("Picker", "canPickBoundsCollider");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mWaiter.resume();
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
        Log.d("Picker", "canPickMeshColliderSphere");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("sphere", sphere, 1, 0, 1, 1);
        mPickHandler.checkHits("sphere", new Vector3f[] { new Vector3f(0, 0, 1) }, null);
        mWaiter.resume();
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
        Log.d("Picker", "canPickMeshColliderBox");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("cube", cube, 1, 0, 1, 1);
        mPickHandler.checkHits("cube", new Vector3f[] { new Vector3f(0, 0, 0.5f) }, null);
        mWaiter.resume();
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
        Log.d("Picker", "canPickQuad");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("quad", sceneObj, 1, 0, 1, 1);
        mPickHandler.checkHits("quad", new Vector3f[] { new Vector3f(0, 0, 0) }, null);
        mWaiter.resume();
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
        Log.d("Picker", "canPickTriangle");
        mPicker = new GVRPicker(context, scene);
        gvrTestUtils.waitForSceneRendering();
        mPickHandler.checkResults(1, 0);
        mPickHandler.checkObject("Triangle", sceneObjTriangle, 1, 0, 1, 1);
        mPickHandler.checkHits("Triangle", new Vector3f[] { new Vector3f(0.4f, 0.8f, 0.4f) }, null);
        mWaiter.resume();
    }
}
