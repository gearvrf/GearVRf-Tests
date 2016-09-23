package org.gearvrf.animation;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import org.gearvrf.GVRSceneObject;


/**
 * Created by Douglas on 2/28/15.
 */
public class GVRRotationByAxisWithPivotAnimationTest extends ActivityInstrumentationGVRf {

    public void testSetInvalidRepeatModeAnimation() {

        try {
            GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
            new GVRRotationByAxisAnimation
                    (sceneObject, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f).setRepeatMode(2);

            GVRRotationByAxisWithPivotAnimation animation = new GVRRotationByAxisWithPivotAnimation(sceneObject,
                    1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            animation.setRepeatMode(4);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "4 is not a valid repetition type");
        }
    }

    public void testInterpolatorAnimation() {

        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        new GVRRotationByAxisAnimation
                (sceneObject, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f).setRepeatMode(2);

        GVRRotationByAxisWithPivotAnimation animation = new GVRRotationByAxisWithPivotAnimation(sceneObject,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        animation.setInterpolator(new GVRInterpolator() {
            @Override
            public float mapRatio(float ratio) {

                assertNotNull(ratio);

                return 0;
            }
        });
    }

    public void testSetFinishedObject() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        new GVRRotationByAxisAnimation
                (sceneObject, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f).setRepeatMode(2);

        GVRRotationByAxisWithPivotAnimation animation = new GVRRotationByAxisWithPivotAnimation(sceneObject,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        animation.setOnFinish(new GVROnFinish() {
            @Override
            public void finished(GVRAnimation animation) {
                assertNotNull(animation);
            }
        });
    }

    public void testSetRepeatCount() {
        GVRSceneObject sceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        new GVRRotationByAxisAnimation
                (sceneObject, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f).setRepeatMode(2);

        GVRRotationByAxisWithPivotAnimation animation = new GVRRotationByAxisWithPivotAnimation(sceneObject,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        animation.setRepeatCount(10);

    }
}
