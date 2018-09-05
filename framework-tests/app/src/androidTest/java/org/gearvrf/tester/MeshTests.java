package org.gearvrf.tester;

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRMeshMorph;
import org.gearvrf.GVRNotifications;
import org.gearvrf.GVRPointLight;
import org.gearvrf.animation.GVRPose;
import org.gearvrf.animation.GVRSkeleton;
import org.gearvrf.animation.GVRSkin;
import org.gearvrf.animation.keyframe.GVRAnimationChannel;
import org.gearvrf.animation.keyframe.GVRSkeletonAnimation;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.GVRIndexBuffer;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRVertexBuffer;

import org.gearvrf.scene_objects.GVRCylinderSceneObject;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;


@RunWith(AndroidJUnit4.class)


public class MeshTests
{
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
    public void testMeshSimpleApi1() {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        final GVRCylinderSceneObject so = new GVRCylinderSceneObject(ctx);
        final GVRMesh mesh = so.getRenderData().getMesh();

        mWaiter.assertTrue(0 < mesh.getIndexBuffer().getIndexCount());

        float[] asArray = mesh.getVertices();
        mWaiter.assertTrue(0 < asArray.length);
        FloatBuffer asBuffer = mesh.getVerticesAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());

        asArray = mesh.getNormals();
        mWaiter.assertTrue(0 < asArray.length);
        asBuffer = mesh.getNormalsAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());

        asArray = mesh.getTexCoords();
        mWaiter.assertTrue(0 < asArray.length);
        asBuffer = mesh.getTexCoordsAsFloatBuffer();
        mWaiter.assertTrue(0 < asBuffer.remaining());
    }

    @Test
    public void testVertexBufferSimpleApi1() {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        mTestUtils.waitForOnInit();
        final GVRCylinderSceneObject so = new GVRCylinderSceneObject(ctx);
        so.getTransform().setPosition(0,0,-2);
        scene.addSceneObject(so);
        mTestUtils.waitForSceneRendering();
        GVRNotifications.waitAfterStep();

        {
            final float[] bound = new float[6];
            final boolean result = so.getRenderData().getMesh().getVertexBuffer().getBoxBound(bound);
            mWaiter.assertTrue(result);
            mWaiter.assertTrue(0 != bound[0] && 0 != bound[1] && 0 != bound[2] && 0 != bound[3]
                    && 0 != bound[4] && 0 != bound[5]);
        }

        {
            final float[] bound = new float[4];
            final float radius = so.getRenderData().getMesh().getVertexBuffer().getSphereBound(bound);
            mWaiter.assertTrue(0 != radius);
        }
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
        mesh.setIndices(triangles);
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

    private GVRSceneObject makeSkinnedMesh(int firstBone, int secondBone)
    {
        final int NUM_STACKS = 16;
        final int TOP_SIZE = 5;
        final int BOTTOM_SIZE = 5;
        final int MIDDLE_SIZE = (NUM_STACKS - (TOP_SIZE + BOTTOM_SIZE));
        final int NUM_SLICE = 16;

        final GVRContext ctx = mTestUtils.getGvrContext();
        GVRCylinderSceneObject.CylinderParams cylparams = new GVRCylinderSceneObject.CylinderParams();
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);

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
        //
        // bottom of cylinder controlled by second bone
        //
        for (int s = 0; s <= BOTTOM_SIZE; ++s)
        {
            for (int i = 0; i < vertsPerStack; ++i)
            {
                int v = (s * vertsPerStack + i) * 4;

                boneIndices[v] = secondBone;
                boneWeights[v] = 1;
            }
        }
        //
        // top of cylinder controlled by first bone
        //
        for (int s = NUM_STACKS - TOP_SIZE; s < NUM_STACKS; ++s)
        {
            for (int i = 0; i < vertsPerStack; ++i)
            {
                int v = (s * vertsPerStack + i) * 4;


                boneIndices[v] = firstBone;
                boneWeights[v] = 1;
            }
        }
        //
        // middle of cylinder controlled by both bones
        //
        for (int s = 0; s <= MIDDLE_SIZE; ++s)
        {
            float r = (float) s / MIDDLE_SIZE;

            for (int i = 0; i < vertsPerStack; ++i)
            {
                int v = ((s + TOP_SIZE) * vertsPerStack + i) * 4;

                boneIndices[v] = secondBone;
                boneWeights[v] = 1 - r;
                boneIndices[v + 1] = firstBone;
                boneWeights[v + 1] = r;
            }
        }
        vbuf.setFloatArray("a_bone_weights", boneWeights);
        vbuf.setIntArray("a_bone_indices", boneIndices);
        return cyl;
    }

    /*
     * Define the two bones which control the mesh.
     * "bone0" is at the origin, "bone1" is 1 unit below the first
     * (which is its parent)
     */
    public GVRSceneObject makeSkeleton(GVRContext ctx, int[] parentIds, float[] positions)
    {
        GVRSkeleton skel = new GVRSkeleton(ctx, parentIds);
        int numbones = parentIds.length;
        GVRSceneObject[] bones = new GVRSceneObject[numbones];

        for (int i = 0; i < numbones; ++i)
        {
            GVRSceneObject boneObj = new GVRSceneObject(ctx);
            int parid = parentIds[i];
            int t = i * 3;

            bones[i] = boneObj;
            boneObj.setName("bone" + Integer.toString(i));
            skel.setBoneName(i, boneObj.getName());

            boneObj.getTransform().setPosition(positions[t], positions[t + 1], positions[t + 2]);
            if (parid >= 0)
            {
                bones[parid].addChildObject(boneObj);
            }
        }
        bones[0].attachComponent(skel);
        return bones[0];
    }

    @Test
    public void testSkinningTwoMeshes() throws TimeoutException, InterruptedException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject root = new GVRSceneObject(ctx);
        GVRSceneObject cyl1 = makeSkinnedMesh(0, 1);
        GVRSceneObject cyl2 = makeSkinnedMesh(0, 1);

        GVRSceneObject rootBone = makeSkeleton(ctx, new int[] { -1, 0, 0 },
                new float[] { 0, -1, 0,  0, 1, 0,  0, 1, 0 });
        GVRSkeleton skel = (GVRSkeleton) rootBone.getComponent(GVRSkeleton.getComponentType());
        GVRSkin skin1 = new GVRSkin(skel);
        GVRSkin skin2 = new GVRSkin(skel);

        skin1.setBoneMap(new int[] { 0, 1 });
        skin2.setBoneMap(new int[] { 0, 2 });
        cyl1.getTransform().setPositionX(-2);
        cyl1.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 1);
        cyl2.getTransform().setPositionX(2);
        cyl2.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
        root.addChildObject(cyl1);
        root.addChildObject(cyl2);
        root.getTransform().setPositionZ(-4.0f);
        cyl1.attachComponent(skin1);
        cyl2.attachComponent(skin2);
        scene.addSceneObject(root);

        GVRPose curPose = skel.getPose();
        Quaternionf q = new Quaternionf();
        Quaternionf correctRot1 = new Quaternionf();
        Quaternionf correctRot2 = new Quaternionf();
        Quaternionf resultRot = new Quaternionf();

        q.rotationXYZ((float) -Math.PI / 4, 0, 0);
        q.normalize();
        curPose.setLocalRotation(0, q.x, q.y, q.z, q.w);
        correctRot1.set(q);
        correctRot2.set(q);

        q.rotationXYZ(0, 0, (float) Math.PI / 4);
        q.normalize();
        curPose.setLocalRotation(1, q.x, q.y, q.z, q.w);
        correctRot1.mul(q);

        q.rotationXYZ(0, 0, (float) -Math.PI / 4);
        q.normalize();
        curPose.setLocalRotation(2, q.x, q.y, q.z, q.w);
        correctRot2.mul(q);

        curPose.sync();
        curPose.getWorldRotation(1, resultRot);
        mWaiter.assertEquals(correctRot1, resultRot);
        curPose.getWorldRotation(2, resultRot);
        mWaiter.assertEquals(correctRot2, resultRot);

        skel.computeSkinPose();
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkinningTwoMeshes", mWaiter, false);
    }

    @Test
    public void testSkinningTwoBones() throws TimeoutException, InterruptedException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject root = new GVRSceneObject(ctx);
        GVRSceneObject cyl = makeSkinnedMesh(0, 1);
        GVRSceneObject rootBone = makeSkeleton(ctx, new int[] { -1, 0 },
                new float[] { 0, -1, 0, 0, 1, 0 });
        GVRSkeleton skel = (GVRSkeleton) rootBone.getComponent(GVRSkeleton.getComponentType());
        GVRSkin skin = new GVRSkin(skel);

        skin.setBoneMap(new int[] { 0, 1 });
        cyl.attachComponent(skin);
        root.addChildObject(cyl);
        root.getTransform().setPosition(0.71f, 0.29f, -3);
        scene.addSceneObject(root);

        GVRPose curPose = skel.getPose();
        Quaternionf q = new Quaternionf();
        Matrix4f mtx = new Matrix4f();
        Quaternionf correctRot = new Quaternionf();
        Quaternionf resultRot = new Quaternionf();
        Matrix4f resultMtx = new Matrix4f();
        Matrix4f correctMtx = new Matrix4f();
        Vector3f resultPos = new Vector3f();
        Vector3f correctPos = new Vector3f();

        curPose.sync();
        /*
         * Rotate bone0 45 degrees around the Z axis
         */
        q.rotation(0,0, (float) Math.PI / 4.0f);
        curPose.setLocalRotation(0, q.x, q.y, q.z, q.w);
        correctRot.set(q);
        mtx.rotation(q);
        mtx.setTranslation(0, -1, 0);
        correctMtx.set(mtx);
        /*
         * Rotate bone1 -45 degrees around the Z axis
         */
        q.rotation(0,0, (float) -Math.PI / 4.0f);
        curPose.setLocalRotation(1, q.x, q.y, q.z, q.w);
        mtx.rotation(q);
        mtx.setTranslation(0, 1, 0);
        correctMtx.mul(mtx);
        correctMtx.getTranslation(resultPos);
        q.mul(correctRot, correctRot);

        curPose.sync();
        curPose.getWorldMatrix(1, resultMtx);
        skel.computeSkinPose();

        resultMtx.getTranslation(resultPos);
        correctMtx.getTranslation(correctPos);
        resultMtx.getUnnormalizedRotation(resultRot);
        mWaiter.assertEquals(correctPos, resultPos);
        mWaiter.assertEquals(correctRot, resultRot);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkinningTwoBones", mWaiter, true);
    }

    public void moveVertices(GVRVertexBuffer vbuf, float x, float y, float z)
    {
        float[] positions = vbuf.getFloatArray("a_position");

        for (int i = 0; i < vbuf.getVertexCount(); ++i)
        {
            int t = i * 3;

            positions[t++] += x;
            positions[t++] += y;
            positions[t] += z;
        }
        vbuf.setFloatArray("a_position", positions);
    }

    @Test
    public void testSkinningBindPose() throws TimeoutException, InterruptedException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject root = new GVRSceneObject(ctx);
        GVRSceneObject cyl1 = makeSkinnedMesh(0, 1);
        GVRSceneObject cyl2 = makeSkinnedMesh(0, 1);

        GVRSceneObject rootBone = makeSkeleton(ctx, new int[] { -1, 0, 0 },
                new float[] { 0, -1, 0,  -2, 1, 0,  2, 1, 0 });
        GVRSkeleton skel = (GVRSkeleton) rootBone.getComponent(GVRSkeleton.getComponentType());
        GVRPose pose = new GVRPose(skel);
        GVRSkin skin1 = new GVRSkin(skel);
        GVRSkin skin2 = new GVRSkin(skel);

        skin1.setBoneMap(new int[] { 0, 1 });
        skin2.setBoneMap(new int[] { 0, 2 });

        moveVertices(cyl1.getRenderData().getMesh().getVertexBuffer(), -2, 0, 0);
        cyl1.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 1);
        moveVertices(cyl2.getRenderData().getMesh().getVertexBuffer(), 2, 0, 0);
        cyl2.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
        pose.setWorldPositions(new float[] { 0, -1, 0, -2, 0, 0, 2, 0, 0 });
        skel.setBindPose(pose);
        cyl1.attachComponent(skin1);
        cyl2.attachComponent(skin2);
        root.addChildObject(cyl1);
        root.addChildObject(cyl2);
        root.getTransform().setPositionZ(-4.0f);
        scene.addSceneObject(root);

        /*
         * Rotate bone1 around the Z axis
         */
        GVRPose curPose = skel.getPose();
        Quaternionf q = new Quaternionf();
        Quaternionf correctRot1 = new Quaternionf();
        Quaternionf correctRot2 = new Quaternionf();
        Quaternionf resultRot = new Quaternionf();

        q.rotationXYZ((float) -Math.PI / 4, 0, 0);
        q.normalize();
        curPose.setLocalRotation(0, q.x, q.y, q.z, q.w);
        correctRot1.set(q);
        correctRot2.set(q);

        q.rotationXYZ(0, 0, (float) Math.PI / 4);
        q.normalize();
        curPose.setLocalRotation(1, q.x, q.y, q.z, q.w);
        correctRot1.mul(q);

        q.rotationXYZ(0, 0, (float) -Math.PI / 4);
        q.normalize();
        curPose.setLocalRotation(2, q.x, q.y, q.z, q.w);
        correctRot2.mul(q);

        curPose.sync();
        curPose.getWorldRotation(1, resultRot);
        mWaiter.assertEquals(correctRot1, resultRot);
        curPose.getWorldRotation(2, resultRot);
        mWaiter.assertEquals(correctRot2, resultRot);

        skel.computeSkinPose();
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkinningBindPose", mWaiter, true);
    }

    @Test
    public void testSkeletonAnimation() throws TimeoutException, InterruptedException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject root = new GVRSceneObject(ctx);

        /*
         * Make bone structure out of scene objects
         * and create skeleton animation
         */
        GVRMaterial boneMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject bone0 = new GVRSphereSceneObject(ctx, true, boneMtl, 0.1f);
        GVRSceneObject bone1 = new GVRSphereSceneObject(ctx, true, boneMtl, 0.1f);
        GVRSceneObject bone2 = new GVRSphereSceneObject(ctx, true, boneMtl, 0.1f);
        List<String> boneNames = new ArrayList<>();

        boneMtl.setDiffuseColor(0, 1, 0, 1);
        boneNames.add("bone0");
        boneNames.add("bone1");
        boneNames.add("bone2");
        bone0.setName("bone0");
        bone1.setName("bone1");
        bone2.setName("bone2");
        bone0.getTransform().setPositionY(-1);
        bone1.getTransform().setPosition(-2,1, 0);
        bone2.getTransform().setPosition(2, 1, 0);
        bone0.addChildObject(bone1);
        bone0.addChildObject(bone2);
        root.addChildObject(bone0);
        root.getTransform().setPositionZ(-4.0f);
        scene.addSceneObject(root);

        GVRSkeletonAnimation skelanim = new GVRSkeletonAnimation("skelanim", bone0, 3);
        GVRSkeleton skel = skelanim.createSkeleton(boneNames);
        GVRSkin skin1 = new GVRSkin(skel);
        GVRSkin skin2 = new GVRSkin(skel);

        skin1.setBoneMap(new int[] { 0, 1 });
        skin2.setBoneMap(new int[] { 0, 2 });

        /*
         * Make meshes
         */
        GVRSceneObject cyl1 = makeSkinnedMesh(0, 1);
        GVRSceneObject cyl2 = makeSkinnedMesh(0, 1);
        cyl1.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 0.7f);
        cyl2.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 0.7f);
        moveVertices(cyl1.getRenderData().getMesh().getVertexBuffer(), -2, 0, 0);
        moveVertices(cyl2.getRenderData().getMesh().getVertexBuffer(), 2, 0, 0);
        cyl1.attachComponent(skin1);
        cyl2.attachComponent(skin2);
        root.addChildObject(cyl1);
        root.addChildObject(cyl2);

        /*
         * Animation to rotate bone1
         */
        GVRAnimationChannel channel1 = new GVRAnimationChannel("bone1", 1, 4, 0, null, null);
        Quaternionf q = new Quaternionf();

        channel1.setPosKeyVector(0, 0, -2, 1, 0);
        q.rotationXYZ(0, 0, (float) -Math.PI / 4);
        q.normalize();
        channel1.setRotKeyQuaternion(0, 0, q);
        q.rotationXYZ(0, 0, 0);
        q.normalize();
        channel1.setRotKeyQuaternion(1, 1, q);
        q.rotationXYZ(0, 0, (float) Math.PI / 4);
        q.normalize();
        channel1.setRotKeyQuaternion(2, 2, q);
        q.rotationXYZ(0, 0, 0);
        q.normalize();
        channel1.setRotKeyQuaternion(3, 3, q);
        /*
         * Animation to rotate bone2
         */
        GVRAnimationChannel channel2 = new GVRAnimationChannel("bone2", 1, 4, 0, null, null);

        channel2.setPosKeyVector(0, 0, 2, 1, 0);
        q.rotationXYZ(0, 0, (float) Math.PI / 4);
        q.normalize();
        channel2.setRotKeyQuaternion(0, 0, q);
        q.rotationXYZ(0, 0, 0);
        q.normalize();
        channel2.setRotKeyQuaternion(1, 1, q);
        q.rotationXYZ(0, 0, (float) -Math.PI / 4);
        q.normalize();
        channel2.setRotKeyQuaternion(2, 2, q);
        q.rotationXYZ(0, 0, 0);
        q.normalize();
        channel2.setRotKeyQuaternion(3, 3, q);

        /*
         * Add animations to skeleton and set bind pose
         */
        GVRPose pose = new GVRPose(skel);

        skelanim.addChannel("bone1", channel1);
        skelanim.addChannel("bone2", channel2);
        pose.setWorldPositions(new float[] { 0, -1, 0, -2, 0, 0, 2, 0, 0 });
        skel.setBindPose(pose);
        skel.computeSkinPose();

        skelanim.animate(0.5f);
        skelanim.animate(1);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation-1", mWaiter, true);
        skelanim.animate(1.5f);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation-2", mWaiter, true);
        skelanim.animate(2.5f);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testSkeletonAnimation-3", mWaiter, true);
    }

    @Test
    public void testMorphTwoShapes() throws TimeoutException, InterruptedException
    {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();
        final GVRCameraRig rig = scene.getMainCameraRig();
        GVRPointLight light = new GVRPointLight(ctx);
        GVRSceneObject lightObj = new GVRSceneObject(ctx);
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        final GVRSphereSceneObject baseShape = new GVRSphereSceneObject(ctx, true, mtl);
        GVRMesh baseMesh = baseShape.getRenderData().getMesh();
        GVRVertexBuffer baseVerts = baseMesh.getVertexBuffer();
        float[] positions = baseMesh.getVertices();
        float[] normals = baseMesh.getNormals();
        float[] weights = new float[] { 1, 0.5f };

        GVRVertexBuffer blendShape1 = new GVRVertexBuffer(ctx, baseVerts.getDescriptor(), baseVerts.getVertexCount());
        GVRVertexBuffer blendShape2 = new GVRVertexBuffer(ctx, baseVerts.getDescriptor(), baseVerts.getVertexCount());

        baseShape.getTransform().setPositionZ(-2);
        rig.getLeftCamera().setBackgroundColor(Color.LTGRAY);
        rig.getRightCamera().setBackgroundColor(Color.LTGRAY);
        rig.getCenterCamera().setBackgroundColor(Color.LTGRAY);
        mtl.setDiffuseColor(1, 0.4f, 0.8f, 1);
        for (int i = 0; i < positions.length; i += 3)
        {
            positions[i] *= 1 + positions[i + 1] * 0.3f;
        }
        blendShape1.setFloatArray("a_position", positions);
        blendShape1.setFloatArray("a_normal", normals);
        positions = baseMesh.getVertices();
        for (int i = 0; i < positions.length; i += 3)
        {
            positions[i + 1] *= 1 + positions[i] * 0.3f;
        }
        blendShape2.setFloatArray("a_position", positions);
        blendShape2.setFloatArray("a_normal", normals);
        GVRMeshMorph morph = new GVRMeshMorph(ctx, 2);
        baseShape.attachComponent(morph);
        morph.setBlendShape(0, blendShape1);
        morph.setBlendShape(1, blendShape2);
        morph.update();
        morph.setWeights(weights);
        lightObj.attachComponent(light);
        scene.addSceneObject(lightObj);
        scene.addSceneObject(baseShape);
        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testMorphTwoShapes", mWaiter, true);
    }


}
