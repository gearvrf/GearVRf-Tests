package org.gearvrf.scene_object;

import android.media.MediaPlayer;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.scene_objects.GVRVideoSceneObject;

public class GVRVideoSceneObjectTest extends ActivityInstrumentationGVRf {


    public void testCreateGVRVideoSceneObject() {

        MediaPlayer mediaPlayer = new MediaPlayer();
        assertNotNull(new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO));
    }

    public void testCreateNameVideoSceneObject() {

        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        videoSceneObject.setName("Test");
        assertEquals(videoSceneObject.getName(), "Test");
    }

    public void testStartVideo() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        videoSceneObject.getMediaPlayer().start();
        videoSceneObject.getMediaPlayer().stop();
    }

    public void testStopVideo() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        videoSceneObject.getMediaPlayer().start();
        videoSceneObject.getMediaPlayer().stop();
    }

    public void testPauseVideo() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        videoSceneObject.getMediaPlayer().start();
        videoSceneObject.getMediaPlayer().pause();
        videoSceneObject.getMediaPlayer().stop();
    }

    public void testGetGVRVideoSceneObjectTransform() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        assertNotNull(videoSceneObject.getTransform());
    }

    public void testGetGVRVideoSceneObjectTimeStamp() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        assertNotNull(videoSceneObject.getTimeStamp());
    }

    public void testGetGVRVideoSceneIsActive() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        assertTrue(videoSceneObject.isActive());
    }

    public void testGetGVRVideoSceneCameraRig() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.VERTICAL_STEREO);
        GVRCameraRig cameraRig = GVRCameraRig.makeInstance(TestDefaultGVRViewManager.mGVRContext);
        videoSceneObject.attachCameraRig(cameraRig);
        videoSceneObject.getMediaPlayer().start();
        assertNotNull(videoSceneObject.getCameraRig());
    }

    public void testDeactivateVideo() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.MONO);
        videoSceneObject.deactivate();
        assertFalse(videoSceneObject.isActive());
    }

    public void testReleaseVideo() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        videoSceneObject.getMediaPlayer().start();
        videoSceneObject.release();
    }

    public void testSetVideoTypeInvalid() {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, 320);

        } catch (IllegalArgumentException e) {
            /*assertEquals(e.getMessage(), "IllegalArgumentException for videoType. Types supported: " +
                    "GVRVideoSceneObject.GVRVideoType.MONO, " +
                    "GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO, " +
                    "GVRVideoSceneObject.GVRVideoType.VERTICAL_STEREO");
                    */
        }
    }

    public void testSetMediaPlayer() { //Created by j.elidelson on 08/17/2015
        MediaPlayer mediaPlayer = new MediaPlayer();
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        MediaPlayer mediaPlayer2 = new MediaPlayer();
        videoSceneObject.setMediaPlayer(mediaPlayer2);
        videoSceneObject.activate();
        videoSceneObject.deactivate();
    }

    public void testCreateGVRVideoSceneObjectWrongVideoType() {

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            assertNotNull(new GVRVideoSceneObject(TestDefaultGVRViewManager.mGVRContext, 100f, 100f, mediaPlayer, 3));
        fail("should throws IllegalArgumentException");
        }catch (IllegalArgumentException e){}
    }

    public void testGVRVideotype() { //Created by j.elidelson on 08/17/2015
        //GVRVideoSceneObject.GVRVideoType gvrVideoType=null;
        assertEquals(0,GVRVideoSceneObject.GVRVideoType.MONO);
        assertEquals(1,GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
        assertEquals(2,GVRVideoSceneObject.GVRVideoType.VERTICAL_STEREO);
    }


}
