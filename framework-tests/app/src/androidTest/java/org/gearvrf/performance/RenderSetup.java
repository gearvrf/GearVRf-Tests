package org.gearvrf.performance;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRImage;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.IAssetEvents;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.tester.R;
import org.gearvrf.tester.TextureEventHandler;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)


public class RenderSetup
{
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRScene mScene;
    private int BITMAP_TEXTURE = R.drawable.checker;
    private int CUBEMAP_TEXTURE = R.raw.beach;
    private int COMPRESSED_TEXTURE = R.raw.sunmap;
    private int R11_BITMAP_TEXTURE =  R.raw.etc2_r11_opaque;
    private int RG11_BITMAP_TEXTURE = R.raw.etc2_rg11_transparency;
    private int RGB8_BITMAP_TEXTURE = R.raw.etc2_rgb8_opaque;
    private GVRImage mBitmapImage = null;
    private GVRImage mCompressedImage = null;
    private GVRImage mCubemapImage = null;

    public RenderSetup(Waiter waiter, GVRTestUtils testUtils)
    {
        mTestUtils = testUtils;
        mWaiter = waiter;
        mScene = testUtils.getMainScene();
    }

    private GVRSceneObject createQuad(GVRContext ctx, String meshDesc, GVRMaterial mtl, float scale)
    {
        GVRMesh quadMesh = new GVRMesh(ctx, meshDesc);
        GVRSceneObject quad = new GVRSceneObject(ctx, quadMesh, mtl);
        quadMesh.createQuad(scale, scale);
        return quad;
    }

    /*
     * They cylinder is 26080 vertices
     */
    private GVRSceneObject createCylinder(GVRContext ctx, String meshDesc, GVRMaterial mtl, float scale)
    {
        GVRCylinderSceneObject.CylinderParams params = new GVRCylinderSceneObject.CylinderParams();
        params.Material = mtl;
        params.VertexDescriptor = meshDesc;
        params.Height = scale;
        params.TopRadius = scale / 2.0f;
        params.BottomRadius = scale / 2.0f;
        params.StackNumber = 80;
        params.SliceNumber = 80;
        params.HasBottomCap = false;
        params.HasTopCap = false;
        params.FacingOut = true;
        GVRSceneObject cyl = new GVRCylinderSceneObject(ctx, params);
        return cyl;
    }

    private void setRenderState(GVRRenderData renderData, Map<String, Object> renderState)
    {
        for (Map.Entry<String, Object> entry : renderState.entrySet())
        {
            String state = entry.getKey();
            Object value = entry.getValue();

            if (state.equals("renderingorder"))
            {
                renderData.setRenderingOrder((Integer) value);
            }
            else if (state.equals("enablelight"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.disableLight();
                }
                else
                {
                    renderData.enableLight();
                }
            }
            else if (state.equals("cullface"))
            {
                GVRRenderPass.GVRCullFaceEnum cull = (GVRRenderPass.GVRCullFaceEnum) value;
                renderData.setCullFace(cull);
            }
            else if (state.equals("alphablend"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.setAlphaBlend(false);
                }
                else
                {
                    renderData.setAlphaBlend(true);
                }
            }
            else if (state.equals("drawmode"))
            {
                Integer drawmode = (Integer) value;
                renderData.setDrawMode(drawmode);
            }
            else if (state.equals("castshadows"))
            {
                Integer i = (Integer) value;
                if (i == 0)
                {
                    renderData.setCastShadows(false);
                }
                else
                {
                    renderData.setCastShadows(true);
                }
            }
        }
    }

    private GVRTexture createBitmap(GVRContext ctx, int resourceId, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceId);
        if (params != null)
        {
            return ctx.getAssetLoader().loadTexture(res, params);
        }
        return ctx.getAssetLoader().loadTexture(res);
    }

    private GVRTexture createCubemap(GVRContext ctx, int resourceID, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceID);
        GVRTexture tex = ctx.getAssetLoader().loadCubemapTexture(res);
        if (params != null)
        {
            tex.updateTextureParameters(params);
        }
        return tex;
    }

    private GVRTexture createCompressedCubemap(GVRContext ctx, int resourceID, GVRTextureParameters params)
    {
        GVRAndroidResource res = new GVRAndroidResource(ctx, resourceID);
        GVRTexture tex = ctx.getAssetLoader().loadCompressedCubemapTexture(res);
        if (params != null)
        {
            tex.updateTextureParameters(params);
        }
        return tex;
    }

    private String createMeshDesc(boolean doLight, boolean doSkin, boolean doTexture)
    {
        String meshDesc = "float3 a_position";
        if (doTexture)
        {
            meshDesc += " float2 a_texcoord";
        }
        if (doLight)
        {
            meshDesc += " float3 a_normal";
        }
        if (doSkin)
        {
            meshDesc += " float4 a_bone_weights int4 a_bone_indices";
        }
        return meshDesc;
    }

    private void createLights(GVRContext ctx, Map<String, Object> params)
    {
        if (params.containsKey("enablelight"))
        {
            boolean castShadows = false;
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                i = (Integer) params.get("castshadows");
                if (i != 0)
                {
                    castShadows = true;
                }
                if (params.containsKey("phong_spotlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRSpotLight spotLight = new GVRSpotLight(ctx);
                    lightObj.attachLight(spotLight);
                    spotLight.setCastShadow(castShadows);
                    mScene.addSceneObject(lightObj);
                }
                if (params.containsKey("phong_directlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRDirectLight directLight = new GVRDirectLight(ctx);
                    lightObj.attachLight(directLight);
                    lightObj.getTransform().rotateByAxis(90.0f, 1, 0, 0);
                    directLight.setCastShadow(castShadows);
                    mScene.addSceneObject(lightObj);
                }
                if (params.containsKey("phong_pointlight"))
                {
                    GVRSceneObject lightObj = new GVRSceneObject(ctx);
                    GVRPointLight pointLight = new GVRPointLight(ctx);
                    lightObj.attachLight(pointLight);
                    lightObj.getTransform().setPosition(-5.0f, 0, 0);
                    mScene.addSceneObject(lightObj);
                }
            }
        }
    }

    private GVRMaterial createMaterial(GVRContext ctx, Map<String, Object> params)
    {
        GVRTexture tex = null;
        GVRMaterial material;
        GVRShaderId shaderId = GVRMaterial.GVRShaderType.Texture.ID;
        TextureEventHandler waitForLoad = null;

        if (params.containsKey("enablelight"))
        {
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                shaderId = GVRMaterial.GVRShaderType.Phong.ID;
            }
        }
        if (params.containsKey("shaderid"))
        {
            shaderId = (GVRShaderId) params.get("shaderid");
        }
        if (params.containsKey("bitmap"))
        {
            if (mBitmapImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createBitmap(ctx, (Integer) params.get("bitmap"), null);
                mTestUtils.waitForAssetLoad();
                waitForLoad.checkTextureLoaded(mWaiter);
                mBitmapImage = tex.getImage();
                mWaiter.assertNotNull(mBitmapImage);
            }
            else
            {
                tex = new GVRTexture(ctx);
                mWaiter.assertNotNull(mBitmapImage);
                tex.setImage(mBitmapImage);
            }
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else if (params.containsKey("compressedbitmap"))
        {
            if (mCompressedImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createBitmap(ctx, (Integer) params.get("compressedbitmap"), null);
                mTestUtils.waitForAssetLoad();
                waitForLoad.checkTextureLoaded(mWaiter);
                mCompressedImage = tex.getImage();
                mWaiter.assertNotNull(mCompressedImage);
            }
            else
            {
                tex = new GVRTexture(ctx);
                mWaiter.assertNotNull(mCompressedImage);
                tex.setImage(mCompressedImage);
            }
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else if (params.containsKey("cubemap"))
        {
            if (mCubemapImage == null)
            {
                waitForLoad = new TextureEventHandler(mTestUtils, 1);
                ctx.getEventReceiver().addListener(waitForLoad);
                tex = createCubemap(ctx, (Integer) params.get("cubemap"), null);
                mTestUtils.waitForAssetLoad();
                mCubemapImage = tex.getImage();
            }
            else
            {
                tex = new GVRTexture(ctx);
                tex.setImage(mCubemapImage);
            }
            shaderId = GVRMaterial.GVRShaderType.Cubemap.ID;
            material = new GVRMaterial(ctx, shaderId);
            material.setMainTexture(tex);
        }
        else
        {
            float red = 0.3f + ((float) Math.random()) * 0.7f;
            float green = 0.3f + ((float) Math.random()) * 0.7f;
            float blue = 0.3f + ((float) Math.random()) * 0.7f;

            material = new GVRMaterial(ctx, shaderId);
            if (shaderId == GVRMaterial.GVRShaderType.Texture.ID)
            {
                material.setColor(red, green, blue);
            }
            else
            {
                material.setDiffuseColor(red, green, blue, 1.0f);
            }
        }
        if (waitForLoad != null)
        {
            ctx.getEventReceiver().removeListener(waitForLoad);
        }
        return material;
    }

    private GVRSceneObject createGeometry(GVRContext ctx, GVRMaterial material, Map<String, Object> params)
    {
        boolean doLight = false;
        boolean doSkin = false;
        boolean doTexture = false;
        String meshDesc;
        GVRSceneObject geometry = null;
        float scale = 1.0f;

        if (params.containsKey("enablelight"))
        {
            Integer i = (Integer) params.get("enablelight");
            if (i != 0)
            {
                doLight = true;
            }
        }
        if (params.containsKey("skinning"))
        {
            Integer i = (Integer) params.get("skinning");
            if (i != 0)
            {
                doSkin = true;
            }
        }
        if (params.containsKey("bitmap"))
        {
            doTexture = true;
        }
        else if (params.containsKey("cubemap"))
        {
            doTexture = true;
        }
        meshDesc = createMeshDesc(doLight, doSkin, doTexture);
        if (params.containsKey("quadgeometry"))
        {
            geometry = createQuad(ctx, meshDesc, material, scale);
        }
        else if (params.containsKey("cylindergeometry"))
        {
            geometry = createCylinder(ctx, meshDesc, material, scale);
        }
        return geometry;
    }

    public void createTestScene(GVRContext ctx, Map<String, Object> params)
    {
        int nrows = (Integer) params.get("rows");
        int ncols = (Integer) params.get("columns");
        float zpos = (nrows > ncols) ? (float) nrows : (float) ncols;
        GVRMaterial sourceMtl = createMaterial(ctx, params);
        GVRSceneObject sourceObj = createGeometry(ctx, sourceMtl, params);
        GVRMesh sourceMesh = sourceObj.getRenderData().getMesh();
        GVRSceneObject root = new GVRSceneObject(ctx);

        mScene.setBackgroundColor(0.8f, 1.0f, 0.8f, 1.0f);
        //createLights(ctx, params);
        for (int y = 0; y < nrows; ++y)
        {
            float ypos = (y - nrows / 2.0f);

            for (int x = 0; x < ncols; ++x)
            {
                float xpos = (x - ncols / 2.0f);
                GVRSceneObject testObj;
                GVRMaterial material = sourceMtl;

                if (!params.containsKey("share_material"))
                {
                    material = createMaterial(ctx, params);
                }
                if (params.containsKey("share_geometry"))
                {
                    testObj = new GVRSceneObject(ctx, sourceMesh, material);
                }
                else
                {
                    testObj = createGeometry(ctx, material, params);
                }
                setRenderState(testObj.getRenderData(), params);
                testObj.getTransform().setPosition(xpos, ypos, -zpos);
                root.addChildObject(testObj);
            }
        }
        mScene.addSceneObject(root);
    }

}

