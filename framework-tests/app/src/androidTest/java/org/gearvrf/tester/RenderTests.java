package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;

@RunWith(AndroidJUnit4.class)

public class RenderTests {
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown() {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null) {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);
    }

    boolean compareArrays(float[] arr1, float[] arr2)
    {
        if (arr1.length != arr2.length)
        {
            return false;
        }
        for (int i =- 0; i < arr1.length; ++i)
        {
            if (Math.abs(arr1[i] - arr2[i]) > 0.00001f)
            {
                return false;
            }
        }
        return true;
    }

    boolean compareArrays(char[] arr1, char[] arr2)
    {
        if (arr1.length != arr2.length)
        {
            return false;
        }
        for (int i =- 0; i < arr1.length; ++i)
        {
            if (arr1[i] != arr2[i])
            {
                return false;
            }
        }
        return true;
    }

    boolean compareArrays(int[] arr1, int[] arr2)
    {
        if (arr1.length != arr2.length)
        {
            return false;
        }
        for (int i =- 0; i < arr1.length; ++i)
        {
            if (arr1[i] != arr2[i])
            {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testBlendFunc() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 2);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.checker));;
        GVRTexture tex2 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.donut));
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRSceneObject quad2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex2);
        GVRRenderData rdata2 = quad2.getRenderData();
        GVRMaterial mat2 = rdata2.getMaterial();

        mat1.setMainTexture(tex1);
        mat2.setColor(1.0f, 1.0f, 0.0f);
        rdata2.setAlphaBlend(true);
        rdata2.setAlphaBlendFunc(GL_ONE, GL_SRC_ALPHA);
        rdata2.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        cube1.getTransform().setPositionZ(-2.0f);
        quad2.getTransform().setPositionZ(-0.8f);
        scene.addSceneObject(cube1);
        scene.addSceneObject(quad2);
        mWaiter.assertEquals(GL_ONE, rdata2.getSourceAlphaBlendFunc());
        mWaiter.assertEquals(GL_SRC_ALPHA, rdata2.getDestAlphaBlendFunc());
        mTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testBlendFunc", mWaiter, true);
    }

    @Test
    public void testAccessMeshShort() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final float[] vertices = { -1, 1, 0, -1, -1, 0, 1, 1, 0, 1, -1, 0 };
        final float[] normals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
        final float[] texCoords = { 0, 0, 0, 1, 1, 0, 1, 1 };
        final char[] triangles = { 0, 1, 2, 1, 3, 2 };
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord float3 a_normal");
        float[] ftmp;
        char[] itmp;

        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setTriangles(triangles);
        ftmp = mesh.getFloatArray("a_position");
        mWaiter.assertTrue(compareArrays(vertices, ftmp));
        ftmp = mesh.getFloatArray("a_texcoord");
        mWaiter.assertTrue(compareArrays(texCoords, ftmp));
        ftmp = mesh.getFloatArray("a_normal");
        mWaiter.assertTrue(compareArrays(normals, ftmp));
        itmp = mesh.getIndexBuffer().asCharArray();
        mWaiter.assertTrue(compareArrays(triangles, itmp));
    }

    @Test
    public void testAccessMeshInt() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final float[] vertices = { -1, 1, 0, -1,  -1, 0, 1, 1, 0, 1, -1, 0 };
        final float[] normals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
        final float[] texCoords = { 0, 0, 0, 1, 1, 0, 1, 1 };
        final int[] triangles = { 0, 1, 2, 1, 3, 2 };
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord float3 a_normal");
        float[] ftmp;
        int[] itmp;

        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(triangles);
        ftmp = mesh.getFloatArray("a_position");
        mWaiter.assertTrue(compareArrays(vertices, ftmp));
        ftmp = mesh.getFloatArray("a_texcoord");
        mWaiter.assertTrue(compareArrays(texCoords, ftmp));
        ftmp = mesh.getFloatArray("a_normal");
        mWaiter.assertTrue(compareArrays(normals, ftmp));
        itmp = mesh.getIndexBuffer().asIntArray();
        mWaiter.assertTrue(compareArrays(triangles, itmp));
    }
}

