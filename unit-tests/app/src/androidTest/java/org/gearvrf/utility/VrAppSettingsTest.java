package org.gearvrf.utility;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRTexture;

/**
 * Created by j.elidelson on 8/26/2015.
 */
public class VrAppSettingsTest extends ActivityInstrumentationGVRf {

    public VrAppSettingsTest() {
        super(GVRTestActivity.class);
    }

    public void testToString(){

        VrAppSettings vrAppSettings = new VrAppSettings();

        assertNotNull(vrAppSettings.toString());
    }

    public void testShowLoadingIcon(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        vrAppSettings.setShowLoadingIcon(true);
        assertEquals(true,vrAppSettings.isShowLoadingIcon());
    }

    public void testUseSrgbFramebuffer(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        vrAppSettings.setUseSrgbFramebuffer(true);
        assertEquals(true,vrAppSettings.isUseSrgbFramebuffer());
    }


    public void testUseProtectedFramebuffer(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        vrAppSettings.setUseProtectedFramebuffer(true);
        assertEquals(true,vrAppSettings.isUseProtectedFramebuffer());
    }

    public void testFramebufferPixelsWide(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        vrAppSettings.setFramebufferPixelsWide(100);
        assertEquals(100,vrAppSettings.getFramebufferPixelsWide());
    }

    public void testFramebufferPixelsHigh(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        vrAppSettings.setFramebufferPixelsHigh(100);
        assertEquals(100,vrAppSettings.getFramebufferPixelsHigh());
    }

    public void testGetModeParms(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.ModeParams modeParms = vrAppSettings.getModeParams();
        assertNotNull(modeParms);
        assertEquals(true, modeParms.isAllowPowerSave());
    }

    public void testGetEyeBufferParms(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.EyeBufferParams eyeBufferParms = vrAppSettings.getEyeBufferParams();
        assertNotNull(eyeBufferParms);
    }

    public void testGetHeadModelParms(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.HeadModelParams headModelParms = vrAppSettings.getHeadModelParams();
        assertNotNull(headModelParms);
    }

    public void testMonoScopicModeParms(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.MonoscopicModeParams monoScopicModeParms = vrAppSettings.getMonoscopicModeParams();
        assertNotNull(monoScopicModeParms);
    }

    /*
     DEPRECATED
    public void testShowDebugLog(){
        VrAppSettings.setShowDebugLog(true);
        assertEquals(true,VrAppSettings.isShowDebugLog());
    }
    */

    public void testHeadModelsParams(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.HeadModelParams headModelParms = vrAppSettings.getHeadModelParams();

        headModelParms.setInterpupillaryDistance(1.0f);
        assertEquals(1.0f, vrAppSettings.getHeadModelParams().getInterpupillaryDistance());
        headModelParms.setEyeHeight(1.0f);
        assertEquals(1.0f, vrAppSettings.getHeadModelParams().getEyeHeight());
        headModelParms.setHeadModelDepth(1.0f);
        assertEquals(1.0f, vrAppSettings.getHeadModelParams().getHeadModelDepth());
        headModelParms.setHeadModelHeight(1.0f);
        assertEquals(1.0f,vrAppSettings.getHeadModelParams().getHeadModelHeight());
    }

    public void testEyeBufferParams(){

        VrAppSettings vrAppSettings = new VrAppSettings();
        VrAppSettings.EyeBufferParams eyeBufferParms = vrAppSettings.getEyeBufferParams();

        eyeBufferParms.setResolutionWidth(1);
        assertEquals(1, vrAppSettings.getEyeBufferParams().getResolutionWidth());
        eyeBufferParms.setResolutionHeight(1);
        assertEquals(1, vrAppSettings.getEyeBufferParams().getResolutionHeight());
        assertEquals(VrAppSettings.EyeBufferParams.DepthFormat.DEPTH_24, vrAppSettings.getEyeBufferParams().getDepthFormat());
        assertEquals(VrAppSettings.EyeBufferParams.ColorFormat.COLOR_8888,vrAppSettings.getEyeBufferParams().getColorFormat());
        assertEquals(false, vrAppSettings.getEyeBufferParams().isResolveDepth());

        assertEquals(true,vrAppSettings.getModeParams().allowPowerSave);
        assertEquals(true,vrAppSettings.getModeParams().isResetWindowFullScreen());
        assertEquals(false,vrAppSettings.getMonoscopicModeParams().isMonoFullScreenMode());
        assertEquals(2, vrAppSettings.getPerformanceParams().getCpuLevel());
        assertEquals(2, vrAppSettings.getPerformanceParams().getGpuLevel());

         ResourceCache <GVRTexture> cache = new ResourceCache <GVRTexture>();
         GVRAndroidResource.CompressedTextureCallback compressedTextureCallback = new GVRAndroidResource.CompressedTextureCallback() {
            @Override
            public void loaded(GVRTexture resource, GVRAndroidResource androidResource) {

            }

            @Override
            public void failed(Throwable t, GVRAndroidResource androidResource) {

            }
        };

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
        ResourceCache.wrapCallback(cache, compressedTextureCallback);
        ResourceCache.wrapCallback(cache,bitmapTextureCallback);
    }
}
