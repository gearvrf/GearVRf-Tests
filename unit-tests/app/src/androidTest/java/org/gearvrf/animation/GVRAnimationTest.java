package org.gearvrf.animation;


import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.CustomPostEffectShaderManager;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRHybridObject;
import org.gearvrf.GVRPostEffect;
import org.gearvrf.GVRSceneObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//import org.junit.BeforeClass;

/**
 * Created by diego on 2/14/15.
 */
public class GVRAnimationTest extends ActivityInstrumentationGVRf {

    private static final float ANIM_DURATION = 1.5f;
    private static final float ELAPSED_TIME = 0f;
    private static final int REPEAT_COUNT = 2;
    private static final int INVALID_REPEAT_MODE = -1;

    private GVRContext mGVRContext = null;

    public GVRAnimationTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
        assertNotNull(animation);

    }

    public void testPostEffectAnimation() throws InterruptedException {
        mGVRContext = TestDefaultGVRViewManager.mGVRContext;

        final CustomPostEffectShaderManager shaderManager = makeCustomPostEffectShaderManager(mGVRContext);
        GVRPostEffect postEffect = new GVRPostEffect(mGVRContext, shaderManager.getShaderId());
        GVRPostEffectAnimation animation = new GVRPostEffectAnimation(postEffect, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
        assertNotNull(animation);
    }

    // TODO create test which calls animate and uses the null object
    public void ignoretestConstructorNullObject() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        assertNotNull(animation);
    }

     public void testSetInterpolator() {
        GVRInterpolator interpolator = new GVRInterpolator() {
            @Override
            public float mapRatio(float v) {
                return 0;
            }
        };

        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setInterpolator(interpolator);
        assertNotNull(animation);
    }

    public void testSetInterpolatorNull() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setInterpolator(null);
        assertNotNull(animation);
    }

    public void testSetOnFinish() {
        GVROnFinish onFinishAnimation = new GVROnFinish(){
            @Override
            public void finished(GVRAnimation gvrAnimation) {
            }
        };

        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setOnFinish(new GVROnRepeat() {
            @Override
            public boolean iteration(GVRAnimation animation, int count) {
                return false;
            }

            @Override
            public void finished(GVRAnimation animation) {

            }
        });
        assertNotNull(animation);
    }

    public void testSetOnFinishNull() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setOnFinish(null);
        assertNotNull(animation);
    }

    public void testSetRepeatCount() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setRepeatCount(REPEAT_COUNT);
        assertNotNull(animation);
    }

    public void testGetRepeatCountBySet() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setRepeatCount(REPEAT_COUNT);
        int repeatCount = animation.getRepeatCount();
        assertEquals(REPEAT_COUNT, repeatCount);
    }

    public void testSetRepeatModeOnce() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setRepeatMode(GVRRepeatMode.ONCE);
        assertNotNull(animation);
    }

    public void testSetRepeatModePingPong() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setRepeatMode(GVRRepeatMode.PINGPONG);
        assertNotNull(animation);
    }

    public void testSetRepeatModeRepeated() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.setRepeatMode(GVRRepeatMode.REPEATED);
        assertNotNull(animation);
    }

    public void testSetRepeatModeInvalid() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        try {
            animation.setRepeatMode(INVALID_REPEAT_MODE);
            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
        }

        assertTrue(true);
    }

    public void testStart() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animation = animation.start(animationEngine);
        assertNotNull(animation);
    }

    // FIXME https://github.com/Samsung/Gear-VR-Hybrid/issues/11
    public void ignoretestStartNull() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        animation = animation.start(null);
        assertNotNull(animation);
    }

    public void testGetDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        float duration = animation.getDuration();
        assertEquals(ANIM_DURATION, duration, 0);
    }

    public void testGetElapsedTime() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        float elapsedTime = animation.getElapsedTime();
        assertEquals(ELAPSED_TIME, elapsedTime, 0);
    }

    public void testGetRepeatCount() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        int repeatCount = animation.getRepeatCount();
        assertEquals(GVRAnimation.DEFAULT_REPEAT_COUNT, repeatCount);
    }

    public void testIsFinishedFalse() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

        boolean isFinished = animation.isFinished();
        assertFalse(isFinished);
    }

    public void testIsFinishedTrue() throws InterruptedException {
        final GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);

        final CountDownLatch cdl = new CountDownLatch(1);
        final GVRAnimation animation = new GVRAnimation(sceneObject, 0f) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
                cdl.countDown();
            }
        };

        animation.start(TestDefaultGVRViewManager.mGVRContext.getAnimationEngine());
        assertTrue(cdl.await(1, TimeUnit.SECONDS));
        assertTrue(animation.isFinished());
    }

    // TODO create test which calls animate with a duration of 0f
    public void ignoretestConstructorZeroDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, 0f) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
    }

    // TODO create test which calls animate with a duration of -2f
    public void ignoretestConstructorNegativeDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, -2f) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
    }

    // TODO create test which calls animate with a duration of NaN
    public void ignoretestConstructorNaNDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, Float.NaN) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
    }

    // TODO create test which calls animate with a duration of positive infinity
    public void ignoretestConstructorPositiveInfinityDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, Float.POSITIVE_INFINITY) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
    }

    // TODO create test which calls animate with a duration of negative infinity
    public void ignoretestConstructorNegativeInfinityDuration() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, Float.NEGATIVE_INFINITY) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
    }

    public void testcheckTarget() { //Created by Elidelson on 8/12/15.

        final Class<?>[] SUPPORTED = { GVRSceneObject.class };

        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, 1.0f) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };
        //Test the object target (must be GVRSceneObject)
        Class<?> type = animation.checkTarget(sceneObject, SUPPORTED);
        //Test wrong object target (should throws IllegalArgumentException)
        GVRMaterial gvrmaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        try {
            Class<?> type2 = animation.checkTarget(gvrmaterial, SUPPORTED);
            fail("should throws IllegalArgumentException");
        }
        catch (IllegalArgumentException e){}
    }

    public void testSetPosition() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRPositionAnimation gvrPositionAnimation = new GVRPositionAnimation(sceneObject,1.0f,1.0f,1.0f,1.0f);
        assertNotNull(gvrPositionAnimation);
        GVRPositionAnimation gvrPositionAnimation2 = new GVRPositionAnimation(sceneObject.getTransform(),1.0f,1.0f,1.0f,1.0f);
        assertNotNull(gvrPositionAnimation2);

        GVRPositionAnimation gvrPositionAnimation3 = new  GVRPositionAnimation(sceneObject,1.0f,1.0f,1.0f,1.0f) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {
            }
        };

    }

    public void ignoretestMaterialAnimation() { //Created by Elidelson on 8/12/15.

        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterial gvrmaterial = new GVRMaterial(TestDefaultGVRViewManager.mGVRContext);
        GVRMaterialAnimation gvrMaterialAnimation = new GVRMaterialAnimation(sceneObject,1.0f) {
            @Override
            protected void animate(GVRHybridObject target, float ratio) {

            }
        };
    }

    public static CustomPostEffectShaderManager makeCustomPostEffectShaderManager(final GVRContext mGVRContext) throws InterruptedException {
        final CustomPostEffectShaderManager[] shaderManager = {null};
        final CountDownLatch cdl = new CountDownLatch(1);
        mGVRContext.runOnGlThread(new Runnable() {
            @Override
            public void run() {
                shaderManager[0] = new CustomPostEffectShaderManager(mGVRContext);
                cdl.countDown();
            }
        });
        assertTrue(cdl.await(1, TimeUnit.SECONDS));
        return shaderManager[0];
    }
}
