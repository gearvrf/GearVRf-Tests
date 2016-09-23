package org.gearvrf.za_gearvrf;

import android.graphics.Bitmap;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRCubemapTexture;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPostEffectShaderManager;
import org.gearvrf.GVRScene;

import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.R;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.periodic.GVRPeriodicEngine;
import org.gearvrf.ActivityInstrumentationGVRf;

import java.io.IOException;

/**
 * Created by santhyago on 2/25/15.
 */

public class GVRContextTest extends ActivityInstrumentationGVRf {
    /**
     * Test if the createQuad method creates a array of chars with 6 positions
     * (3 points = 1 triangle)
     * (6 points = 2 triangles = 1 quadrangle)
     */
    public void testCreateQuad() {
        float width = 1.0f;
        float height = 2.0f;

        /**
         * width  = X;
         * height = y;
         */

        GVRMesh quad = TestDefaultGVRViewManager.mGVRContext.createQuad(width, height);

        /**
         * Tests if the quad has 2 triangles with 2 vertices in common
         */
        assertEquals('\u0000', quad.getTriangles()[0]);
        assertEquals('\u0001', quad.getTriangles()[1]); // [1] is the same as [3]
        assertEquals('\u0002', quad.getTriangles()[2]); // [2] is the same as [5]
        assertEquals('\u0001', quad.getTriangles()[3]); // [3] is the same as [1]
        assertEquals('\u0003', quad.getTriangles()[4]);
        assertEquals('\u0002', quad.getTriangles()[5]); // [5] is the same as [2]

        /**
         * [   0] [   1] [  2]   [-width/2] [ height/2] [ 0.0]
         * [   3] [   4] [  5]   [-width/2] [-height/2] [ 0.0]
         * [   6] [   7] [  8] = [ width/2] [ height/2] [ 0.0]
         * [   9] [  10] [ 11]   [ width/2] [-height/2] [ 0.0]
         */

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        assertEquals(-halfWidth, quad.getVertices()[0]);
        assertEquals(halfHeight, quad.getVertices()[1]);
        assertEquals(0.0f, quad.getVertices()[2]);
        assertEquals(-halfWidth, quad.getVertices()[3]);
        assertEquals(-halfHeight, quad.getVertices()[4]);
        assertEquals(0.0f, quad.getVertices()[5]);
        assertEquals(halfWidth, quad.getVertices()[6]);
        assertEquals(halfHeight, quad.getVertices()[7]);
        assertEquals(0.0f, quad.getVertices()[8]);
        assertEquals(halfWidth, quad.getVertices()[9]);
        assertEquals(-halfHeight, quad.getVertices()[10]);
        assertEquals(0.0f, quad.getVertices()[11]);
    }

    public void testGetMainScene() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        assertNotNull(lGVRContext);
        assertNotNull(lGVRContext.getMainScene());
        assertEquals(new GVRScene(lGVRContext).getClass().getSimpleName(),
                lGVRContext.getMainScene().getClass().getSimpleName());
    }

    public void testGetPeriodicEngine() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRPeriodicEngine periodicEngine = lGVRContext.getPeriodicEngine();
        assertNotNull(periodicEngine);
        assertEquals(periodicEngine.getClass().getSimpleName(), "GVRPeriodicEngine");
    }

    public void testGetPostEffectShaderManager() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRPostEffectShaderManager shaderManager = lGVRContext.getPostEffectShaderManager();

        lGVRContext.getMaterialShaderManager();
        assertNotNull(shaderManager);
    }

    public void testGetAnimationEngine() {
        GVRContext gVRContext = TestDefaultGVRViewManager.mGVRContext;
        assertNotNull(gVRContext.getAnimationEngine());
    }

    public void testgetMaterialShaderManager() {
        GVRContext gVRContext = TestDefaultGVRViewManager.mGVRContext;
        assertNotNull(gVRContext.getMaterialShaderManager());
    }

//    public void testLoadMeshWhitRaw(){
//        GVRContext gVRContext = TestDefaultGVRViewManager.mGVRContext;
//        gVRContext.loadMesh(R.raw.cinema);
//    }

//    public void testLoadMeshRelativyFileName(){
//        GVRContext gVRContext = TestDefaultGVRViewManager.mGVRContext;
//        gVRContext.loadMesh("cinema.obj");
//    }

    public void testLoadPngFile(){
        //GVRContext gVRContext = TestDefaultGVRViewManager.mGVRContext;
        //gVRContext.loadBitmap("texture1.jpg");
        TestDefaultGVRViewManager.mGVRContext.loadBitmap("texture1.jpg");
    }

//    public void testLoadMeshFromAssets() {
//        GVRContext lGVRContext = getActivity().getGVRContext();
//        GVRMesh mesh = lGVRContext.loadMeshFromAssets("pokeball.obj");
//        assertNotNull(mesh);
//        assertTrue(mesh.getTriangles().length > 0);
//    }

//    public void testLoadTexture() {
//        GVRContext lGVRContext = getActivity().getGVRContext();
//        GVRBitmapTexture bitmapTexture = lGVRContext.loadTexture("texture1.jpg");
//        assertNotNull(bitmapTexture);
//    }

    //Added by Elidelson on 10/02/2015
    public void testLoadMesh01(){

        GVRAndroidResource gvrAndroidResource = null;
        GVRAndroidResource gvrAndroidResource2 = null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "cylinder.obj");
            gvrAndroidResource2 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "bunny.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GVRAndroidResource.MeshCallback meshCallback = new GVRAndroidResource.MeshCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRMesh resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };
        TestDefaultGVRViewManager.mGVRContext.loadMesh(meshCallback,gvrAndroidResource);
        TestDefaultGVRViewManager.mGVRContext.loadMesh(meshCallback,gvrAndroidResource2,0);
    }


    public void ignoretestLoadTexture() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRBitmapTexture bitmapTexture = lGVRContext.loadTexture("texture1.jpg");
        assertNotNull(bitmapTexture);
    }

    public void ignoretestLoadBitmap() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        Bitmap bitmap;
        bitmap = lGVRContext.loadBitmap("texture1.jpg");
        assertEquals(1024, bitmap.getWidth());

        bitmap = lGVRContext.loadBitmap("front.png");
        assertEquals(512, bitmap.getWidth());

        try {
            bitmap = lGVRContext.loadBitmap(null);
            assertEquals(3695, bitmap.getWidth());
        }catch (IllegalArgumentException e){}
    }

    public void testLoadCubeMApTesture1() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRAndroidResource gvrAndroidResource[] = new GVRAndroidResource[6];
        GVRTextureParameters gvrTextureParameters = new GVRTextureParameters(TestDefaultGVRViewManager.mGVRContext);

        try {
            gvrAndroidResource[0] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource[1] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource[2] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource[3] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource[4] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource[5] = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //GVRCubemapTexture gvrCubemapTexture = lGVRContext.loadCubemapTexture(gvrAndroidResource);
        //assertNotNull(gvrCubemapTexture);
        //GVRCubemapTexture gvrCubemapTexture1 = lGVRContext.loadCubemapTexture(gvrAndroidResource, gvrTextureParameters);
        //assertNotNull(gvrTextureParameters);

    }


    public void testLoadBitmapTexture1() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRAndroidResource gvrAndroidResource=null;
        GVRAndroidResource gvrAndroidResource2=null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource2 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posy.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GVRAndroidResource.BitmapTextureCallback bitmapTextureCallback = new GVRAndroidResource.BitmapTextureCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };
        lGVRContext.loadBitmapTexture(bitmapTextureCallback,gvrAndroidResource);
        lGVRContext.loadBitmapTexture(bitmapTextureCallback,gvrAndroidResource2,0);
    }


    public void testLoadCompressedTexture() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRAndroidResource gvrAndroidResource=null;
        GVRAndroidResource gvrAndroidResource2=null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource2 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posy.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GVRAndroidResource.CompressedTextureCallback compressedTextureCallback = new GVRAndroidResource.CompressedTextureCallback() {
            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

        lGVRContext.loadCompressedTexture(compressedTextureCallback, gvrAndroidResource);
        lGVRContext.loadCompressedTexture(compressedTextureCallback, gvrAndroidResource2, 0);
    }


    public void testLoadTexture() {
        GVRContext lGVRContext = TestDefaultGVRViewManager.mGVRContext;
        GVRAndroidResource gvrAndroidResource=null;
        GVRAndroidResource gvrAndroidResource2=null;
        try {
            gvrAndroidResource = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posx.png");
            gvrAndroidResource2 = new GVRAndroidResource(TestDefaultGVRViewManager.mGVRContext, "posy.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GVRAndroidResource.TextureCallback textureCallback = new GVRAndroidResource.TextureCallback() {
            @Override
            public boolean stillWanted(GVRAndroidResource androidResource) {
                return false;
            }

            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };
        lGVRContext.loadTexture(textureCallback, gvrAndroidResource);
        lGVRContext.loadTexture(textureCallback, gvrAndroidResource2, 0);
        lGVRContext.loadTexture(textureCallback,gvrAndroidResource2,0,0);
    }


}