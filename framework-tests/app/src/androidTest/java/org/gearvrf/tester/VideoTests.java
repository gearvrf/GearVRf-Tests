package org.gearvrf.tester;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class VideoTests
{
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private boolean mDoCompare = true;

    public VideoTests() {
        super();
    }

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);


    @After
    public void tearDown()
    {
        GVRScene scene = gvrTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws TimeoutException {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
    }

    private GVRVideoSceneObject createVideoObject(GVRContext gvrContext, String file, int videotype) throws IOException
    {
        MediaPlayer.OnInfoListener listener = new MediaPlayer.OnInfoListener()
        {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra)
            {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    gvrTestUtils.onAssetLoaded(null);
                    return true;
                }
                return false;
            }
        };
        final AssetFileDescriptor afd = gvrContext.getActivity().getAssets().openFd(file);
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mediaPlayer.setOnInfoListener(listener);
        mediaPlayer.prepare();
        GVRVideoSceneObject video = new GVRVideoSceneObject(gvrContext, 8.0f, 4.0f, mediaPlayer, videotype);
        video.setName("video");
        video.getTransform().setPosition(0.0f, 0.0f, -4.0f);
        while (video.getMediaPlayer() == null)
        {
            ;
        }
        video.getMediaPlayer().start();
        return video;
    }

    private GVRVideoSceneObject createVideoMeshObject(GVRContext gvrContext, String file, int videotype, GVRMesh mesh) throws IOException
    {
        MediaPlayer.OnInfoListener listener = new MediaPlayer.OnInfoListener()
        {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra)
            {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    gvrTestUtils.onAssetLoaded(null);
                    return true;
                }
                return false;
            }
        };
        final AssetFileDescriptor afd = gvrContext.getActivity().getAssets().openFd(file);
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mediaPlayer.setOnInfoListener(listener);
        mediaPlayer.prepare();
        GVRVideoSceneObject video = new GVRVideoSceneObject(gvrContext, mesh, mediaPlayer, videotype);
        video.setName("video");
        video.getTransform().setPosition(0.0f, 0.0f, -2.0f);
        while (video.getMediaPlayer() == null)
        {
            ;
        }
        video.getMediaPlayer().start();
        return video;
    }

    @Test
    public void testMonoVideo() throws TimeoutException {
        GVRContext ctx  = gvrTestUtils.getGvrContext();
        GVRScene mainScene = gvrTestUtils.getMainScene();

        try
        {
            GVRVideoSceneObject video =
                    createVideoObject(ctx, "tnb.mp4", GVRVideoSceneObject.GVRVideoType.MONO);
            mainScene.addSceneObject(video);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        gvrTestUtils.waitForAssetLoad();
        gvrTestUtils.screenShotRight(getClass().getSimpleName(), "testMonoVideo", mWaiter, mDoCompare);
    }

    @Test
    public void testMonoVideoMesh() throws TimeoutException {
        GVRContext ctx  = gvrTestUtils.getGvrContext();
        GVRScene mainScene = gvrTestUtils.getMainScene();
        GVRMesh mesh = new GVRMesh(ctx, "float3 a_position float2 a_texcoord");
        mesh.createQuad(3, 2);

        try
        {
            GVRVideoSceneObject video =
                    createVideoMeshObject(ctx, "tnb.mp4", GVRVideoSceneObject.GVRVideoType.MONO, mesh);
            mainScene.addSceneObject(video);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        gvrTestUtils.waitForAssetLoad();
        gvrTestUtils.screenShotRight(getClass().getSimpleName(), "testMonoVideoMesh", mWaiter, mDoCompare);
    }

    @Test
    public void testHorizontalStereo() throws TimeoutException {
        GVRContext ctx  = gvrTestUtils.getGvrContext();
        GVRScene mainScene = gvrTestUtils.getMainScene();

        try
        {
            GVRVideoSceneObject video =
                    createVideoObject(ctx, "sbs.mp4", GVRVideoSceneObject.GVRVideoType.HORIZONTAL_STEREO);
            mainScene.addSceneObject(video);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        gvrTestUtils.waitForAssetLoad();
        gvrTestUtils.screenShotRight(getClass().getSimpleName(), "testHorizontalStereo", mWaiter, mDoCompare);
    }

    @Test
    public void testVerticalStereo() throws TimeoutException {
        GVRContext ctx  = gvrTestUtils.getGvrContext();
        GVRScene mainScene = gvrTestUtils.getMainScene();

        try
        {
            GVRVideoSceneObject video =
                    createVideoObject(ctx, "tnb.mp4", GVRVideoSceneObject.GVRVideoType.VERTICAL_STEREO);
            mainScene.addSceneObject(video);
        }
        catch (IOException ex)
        {
            mWaiter.fail(ex);
        }
        gvrTestUtils.waitForAssetLoad();
        gvrTestUtils.screenShotRight(getClass().getSimpleName(), "testVerticalStereo", mWaiter, mDoCompare);
    }

}
