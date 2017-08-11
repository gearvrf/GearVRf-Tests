package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRBone;
import org.gearvrf.GVRColorBlendShader;
import org.gearvrf.GVRIndexBuffer;
import org.gearvrf.GVRShaderData;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRVertexBuffer;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.joml.Matrix4f;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    boolean compareBuffers(FloatBuffer buf1, FloatBuffer buf2)
    {
        int n = buf1.capacity();
        if (n != buf2.capacity())
        {
            return false;
        }
        for (int i = 0; i < n; ++i)
        {
            float a = buf1.get(i);
            float b = buf2.get(i);
            if (Math.abs(a - b) > 0.00001f)
            {
                return false;
            }
        }
        return true;
    }

    boolean compareBuffers(IntBuffer buf1, IntBuffer buf2)
    {
        int n = buf1.capacity();
        if (n != buf2.capacity())
        {
            return false;
        }
        for (int i = 0; i < n; ++i)
        {
            int a = buf1.get(i);
            int b = buf2.get(i);
            if (a != b)
            {
                return false;
            }
        }
        return true;
    }

    boolean compareBuffers(CharBuffer buf1, CharBuffer buf2)
    {
        int n = buf1.capacity();
        if (n != buf2.capacity())
        {
            return false;
        }
        for (int i = 0; i < n; ++i)
        {
            char a = buf1.get(i);
            char b = buf2.get(i);
            if (a != b)
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
    public void testAccessMeshShortArray() throws TimeoutException
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
    public void testAccessMeshIntArray() throws TimeoutException
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

    @Test
    public void testAccessMeshIntBuffer() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final float[] vertices = { -1, 1, 0, -1,  -1, 0, 1, 1, 0, 1, -1, 0 };
        final float[] normals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
        final float[] texCoords = { 0, 0, 0, 1, 1, 0, 1, 1 };
        final int[] triangles = { 0, 1, 2, 1, 3, 2 };
        FloatBuffer posBuf = FloatBuffer.wrap(vertices);
        FloatBuffer normBuf = FloatBuffer.wrap(normals);
        FloatBuffer texBuf = FloatBuffer.wrap(texCoords);
        IntBuffer indBuf = ByteBuffer.allocateDirect(triangles.length * 4).asIntBuffer();
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord float3 a_normal");
        GVRIndexBuffer ibuf = new GVRIndexBuffer(ctx, 4, triangles.length);
        GVRVertexBuffer vbuf = mesh.getVertexBuffer();

        mesh.setFloatVec("a_position", posBuf);
        mesh.setFloatVec("a_normal", normBuf);
        mesh.setFloatVec("a_texcoord", texBuf);
        ibuf.setIntVec(indBuf);
        mesh.setIndexBuffer(ibuf);
        FloatBuffer fbtmp = vbuf.getFloatVec("a_position");
        mWaiter.assertTrue(compareBuffers(posBuf, fbtmp));
        fbtmp = vbuf.getFloatVec("a_texcoord");
        mWaiter.assertTrue(compareBuffers(texBuf, fbtmp));
        fbtmp = vbuf.getFloatVec("a_normal");
        mWaiter.assertTrue(compareBuffers(normBuf, fbtmp));
        IntBuffer ibtmp = ibuf.asIntBuffer();
        mWaiter.assertTrue(compareBuffers(indBuf, ibtmp));
    }

    @Test
    public void testAccessMeshShortBuffer() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final float[] vertices = { -1, 1, 0, -1,  -1, 0, 1, 1, 0, 1, -1, 0 };
        final float[] normals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
        final float[] texCoords = { 0, 0, 0, 1, 1, 0, 1, 1 };
        final char[] triangles = { 0, 1, 2, 1, 3, 2 };
        FloatBuffer posBuf = FloatBuffer.wrap(vertices);
        FloatBuffer normBuf = FloatBuffer.wrap(normals);
        FloatBuffer texBuf = FloatBuffer.wrap(texCoords);
        int n = triangles.length;
        CharBuffer indBuf = ByteBuffer.allocateDirect(n * 2).order(ByteOrder.nativeOrder()).asCharBuffer();
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord float3 a_normal");
        GVRIndexBuffer ibuf = new GVRIndexBuffer(ctx, 2, n);
        GVRVertexBuffer vbuf = mesh.getVertexBuffer();

        for (int i = 0; i < n; ++i)
        {
            indBuf.put(i, triangles[i]);
        }
        mesh.setFloatVec("a_position", posBuf);
        mesh.setFloatVec("a_normal", normBuf);
        mesh.setFloatVec("a_texcoord", texBuf);
        ibuf.setShortVec(indBuf);
        mesh.setIndexBuffer(ibuf);
        FloatBuffer fbtmp = vbuf.getFloatVec("a_position");
        mWaiter.assertTrue(compareBuffers(posBuf, fbtmp));
        fbtmp = vbuf.getFloatVec("a_texcoord");
        mWaiter.assertTrue(compareBuffers(texBuf, fbtmp));
        fbtmp = vbuf.getFloatVec("a_normal");
        mWaiter.assertTrue(compareBuffers(normBuf, fbtmp));
        CharBuffer ibtmp = ibuf.asCharBuffer();
        mWaiter.assertTrue(compareBuffers(indBuf, ibtmp));
    }

    @Test
    public void testAccessMeshBoneWeights() throws TimeoutException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final float[] vertices = {
                -1, 1, 0,
                -1, -1, 0,
                1, 1, 0,
                1, -1, 0 };
        final float[] weights = {
            0, 1, 0, 0,
            0.75f, 0.25f, 0, 0,
            0.5f, 0.5f, 0, 0,
            0.2f, 0.2f, 0.2f, 0.4f };
        final int[] indices = {
                0, 0, 0, 0,
                0, 1, 0, 0,
                1, 3, 0, 0,
                0, 1, 3, 2 };
        final int[] triangles = { 0, 1, 2, 1, 3, 2 };
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float4 a_bone_weights int4 a_bone_indices");
        float[] ftmp;
        int[] itmp;

        mesh.setVertices(vertices);
        mesh.setFloatArray("a_bone_weights", weights);
        mesh.setIntArray("a_bone_indices", indices);
        mesh.setIndices(triangles);
        ftmp = mesh.getFloatArray("a_position");
        mWaiter.assertTrue(compareArrays(vertices, ftmp));
        ftmp = mesh.getFloatArray("a_bone_weights");
        mWaiter.assertTrue(compareArrays(weights, ftmp));
        itmp = mesh.getIntArray("a_bone_indices");
        mWaiter.assertTrue(compareArrays(indices, itmp));
        itmp = mesh.getIndexBuffer().asIntArray();
        mWaiter.assertTrue(compareArrays(triangles, itmp));
    }

    @Test
    public void testOnePostEffect() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));;
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRShaderData flipHorzPostEffect = new GVRShaderData(ctx, GVRMaterial.GVRShaderType.VerticalFlip.ID);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        mTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testOnePostEffect", mWaiter, true);
    }

    @Test
    public void testTwoPostEffects() throws TimeoutException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex1 = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.gearvr_logo));;
        GVRMaterial mat1 = new GVRMaterial(ctx);
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mat1);
        GVRShaderData flipHorzPostEffect = new GVRShaderData(ctx, GVRMaterial.GVRShaderType.VerticalFlip.ID);
        GVRShaderId colorBlendID = new GVRShaderId(GVRColorBlendShader.class);
        GVRShaderData colorBlendPostEffect = new GVRShaderData(ctx, colorBlendID);

        colorBlendPostEffect.setVec3("u_color", 0.0f, 0.3f, 0.3f);
        colorBlendPostEffect.setFloat("u_factor", 0.5f);
        flipHorzPostEffect.setVec3("u_color", 0, 0, 0);
        flipHorzPostEffect.setFloat("u_factor", 0);

        mat1.setMainTexture(tex1);
        cube1.getTransform().setPositionZ(-2.0f);
        scene.getMainCameraRig().getRightCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getCenterCamera().addPostEffect(colorBlendPostEffect);
        scene.getMainCameraRig().getRightCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getLeftCamera().addPostEffect(flipHorzPostEffect);
        scene.getMainCameraRig().getCenterCamera().addPostEffect(flipHorzPostEffect);
        scene.addSceneObject(cube1);
        mTestUtils.waitForAssetLoad();
        ctx.getEventReceiver().removeListener(texHandler);
        mTestUtils.waitForSceneRendering();
        mTestUtils.screenShot(getClass().getSimpleName(), "testTwoPostEffects", mWaiter, true);
    }

    @Test
    public void testSkinningTwoBones() throws TimeoutException, InterruptedException
    {
        final int NUM_STACKS = 16;
        final int TOP_SIZE = 5;
        final int BOTTOM_SIZE = 5;
        final int MIDDLE_SIZE = (NUM_STACKS - (TOP_SIZE + BOTTOM_SIZE));
        final int NUM_SLICE = 16;

        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        GVRCylinderSceneObject.CylinderParams cylparams = new GVRCylinderSceneObject.CylinderParams();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject root = new GVRSceneObject(ctx);

        mtl.setDiffuseColor(1.0f, 0.5f, 0.8f, 0.5f);
        cylparams.Material = mtl;
        cylparams.HasTopCap = false;
        cylparams.HasBottomCap = false;
        cylparams.TopRadius = 0.5f;
        cylparams.BottomRadius = 0.5f;
        cylparams.Height = 2.0f;
        cylparams.FacingOut = true;
        cylparams.StackNumber = NUM_STACKS;
        cylparams.SliceNumber = NUM_SLICE;
        cylparams.VertexDescriptor = "float3 a_position float4 a_bone_weights int4 a_bone_indices ";
        GVRCylinderSceneObject cyl = new GVRCylinderSceneObject(ctx, cylparams);

        /*
         * Add bone indices and bone weights to the cylinder vertex buffer.
         */
        GVRVertexBuffer vbuf = cyl.getRenderData().getMesh().getVertexBuffer();

        int nverts = vbuf.getVertexCount();
        int vertsPerStack = nverts / cylparams.StackNumber;
        int[] boneIndices = new int[nverts * 4];
        float[] boneWeights = new float[nverts * 4];

        Arrays.fill(boneIndices, 0, nverts * 4, 0);
        Arrays.fill(boneWeights, 0, nverts * 4, 0.0f);
        for (int s = 0; s < NUM_STACKS; ++s)
        {
            float r = ((float) s - TOP_SIZE) / MIDDLE_SIZE;

            for (int i = 0; i < vertsPerStack; ++i)
            {
                int v = (s * vertsPerStack + i) * 4;
                //
                // bottom of cylinder controlled by bone 0
                //
                if (s <= TOP_SIZE)
                {
                    boneIndices[v] = 0;
                    boneWeights[v] = 1.0f;
                }
                //
                // top of cylinder controlled by bone 1
                //
                else if (s > (TOP_SIZE + MIDDLE_SIZE))
                {
                    boneIndices[v] = 1;
                    boneWeights[v] = 1.0f;
                }
                //
                // middle of cylinder controlled by both bones
                //
                else
                {
                    boneIndices[v + 1] = 1;
                    boneWeights[v] = 1.0f - r;
                    boneWeights[v + 1] = r;
                }
            }
        }

        /*
         * Define the two bones which control the mesh.
         * One bone is at the origin, the other is 1 unit below the first
         */
        GVRSceneObject bone0Obj = new GVRSceneObject(ctx);
        GVRSceneObject bone1Obj = new GVRSceneObject(ctx);
        GVRBone bone0 = new GVRBone(ctx);
        GVRBone bone1 = new GVRBone(ctx);
        Matrix4f bone0Mtx = new Matrix4f();
        Matrix4f bone1Mtx = new Matrix4f();
        Matrix4f outMtx0 = new Matrix4f();
        Matrix4f outMtx1 = new Matrix4f();
        float[] temp1 = new float[16];
        List<GVRBone> bones = new ArrayList<GVRBone>();

        bone1Mtx.translate(0, -1.0f, 0.0f);
        bones.add(bone0);
        bones.add(bone1);
        bone0.setName("top");
        bone0.setSceneObject(bone0Obj);
        bone0Mtx.get(temp1);
        bone0.setOffsetMatrix(temp1);
        bone1.setName("bottom");
        bone1.setSceneObject(bone1Obj);
        bone1Mtx.get(temp1);
        bone1.setOffsetMatrix(temp1);
        vbuf.setFloatArray("a_bone_weights", boneWeights);
        vbuf.setIntArray("a_bone_indices", boneIndices);
        cyl.getRenderData().getMesh().setBones(bones);
        bone0.setFinalTransformMatrix(outMtx0);
        outMtx1.translate(0, -1.0f, 0.0f);
        outMtx1.rotation((float) Math.PI / 4.0f, 0, 0, 1);
        bone1.setFinalTransformMatrix(outMtx1);
        root.getTransform().setPositionZ(-3.0f);
        root.addChildObject(cyl);
        scene.addSceneObject(root);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkinningTwoBones", mWaiter, false);
    }
}
