package org.gearvrf.animation;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import org.gearvrf.GVRHybridObject;
import org.gearvrf.GVRSceneObject;

/**
 * Created by diego on 2/20/15.
 */
public class GVRAnimationEngineTest extends ActivityInstrumentationGVRf {

    private static final float ANIM_DURATION = 1.5f;

    public GVRAnimationEngineTest() {super(GVRTestActivity.class);}

    public void testGetInstance() {
        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        assertNotNull(animationEngine);
    }

    // TODO make a test that calls animate() to show start() working
    public void testStart() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {}
        };

        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animation = animationEngine.start(animation);
        animation.setRepeatCount(5);
        animation.setRepeatMode(GVRRepeatMode.PINGPONG);
        animation.setRepeatCount(0);
        animation.setRepeatMode(GVRRepeatMode.ONCE);
        animation.setRepeatCount(3);






        animation.setRepeatMode(GVRRepeatMode.REPEATED);

        assertNotNull(animationEngine);
    }

    // TODO make a test that calls animate() to show start() working with null
    public void ignoretestStartNull() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {}
        };

        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animation = animationEngine.start(null);
        assertNotNull(animationEngine);
    }

    // TODO make a test that shows start() and stop() working by checking animation state
    public void ignoretestStartStopAnimation() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {}
        };

        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animationEngine.stop(null);
        assertNotNull(animationEngine);
    }

    public void testStopAnimation() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        GVRAnimation animation = new GVRAnimation(sceneObject, ANIM_DURATION) {
            @Override
            protected void animate(GVRHybridObject gvrHybridObject, float v) {}
        };

        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animationEngine.stop(animation);
        assertNotNull(animationEngine);
    }

    public void testStopNull() {
        GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        animationEngine.stop(null);
        assertNotNull(animationEngine);
    }
}
