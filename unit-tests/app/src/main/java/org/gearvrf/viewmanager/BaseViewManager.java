package org.gearvrf.viewmanager;

import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.misc.ColorShader;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by santhyago on 1/15/15.
 */
public abstract class BaseViewManager extends GVRMain {
    protected static final float UNPICKED_COLOR_R = 0.7f;
    protected static final float UNPICKED_COLOR_G = 0.7f;
    protected static final float UNPICKED_COLOR_B = 0.7f;
    protected static final float UNPICKED_COLOR_A = 1.0f;
    protected static final float PICKED_COLOR_R = 1.0f;
    protected static final float PICKED_COLOR_G = 0.0f;
    protected static final float PICKED_COLOR_B = 0.0f;
    protected static final float PICKED_COLOR_A = 1.0f;

    protected GVRContext mGVRContext = null;
    protected ColorShader mColorShader = null;
    protected Vector<GVRSceneObject> mObjects = new Vector<GVRSceneObject>();
    protected LinkedBlockingQueue<Runnable> runnableTests = new LinkedBlockingQueue<>();

    protected float OBJECT_ROT = 0.0f;

    public void addRunnableTests(Runnable test) {
        runnableTests.add(test);
    }

    @Override
    public void onStep() {
        Runnable runTest;
        while ((runTest = runnableTests.poll()) != null)
            runTest.run();

        GVRSceneObject objectExt = null;

        for (GVRSceneObject object : mObjects) {
            object.getRenderData().getMaterial().setVec4(ColorShader.COLOR_KEY, UNPICKED_COLOR_R, UNPICKED_COLOR_G, UNPICKED_COLOR_B, UNPICKED_COLOR_A);
        }
        for (GVRPicker.GVRPickedObject hit : GVRPicker.pickObjects(mGVRContext.getMainScene(), 0, 0, 0, 0, 0, -1)) {
            for (GVRSceneObject object : mObjects) {
                if (hit.getHitObject().equals(object)) {
                    object.getRenderData().getMaterial().setVec4(ColorShader.COLOR_KEY, PICKED_COLOR_R, PICKED_COLOR_G, PICKED_COLOR_B, PICKED_COLOR_A);
                    objectExt = object;
                    break;
                }
            }
        }
        OBJECT_ROT += -1.0f;
        for (int i = 0; i < OBJECT_ROT; i++)
            objectExt.getTransform().rotateByAxis(OBJECT_ROT, 0f, 1.0f, 0f);
    }
}


