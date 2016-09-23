package org.gearvrf.viewmanager;

import org.gearvrf.GVRSceneObject;
import org.gearvrf.misc.Gyroscope;


public class TestViewManager extends TestDefaultGVRViewManager {
	
    private enum State {
        Idle, Ready, Rotating, Pass, Fail
    };

    private State mState = State.Idle;
    private GVRSceneObject mDegreeBoard = null;
    private GVRSceneObject mAngularVelocityBoard = null;
    private GVRSceneObject mValueBoard = null;
    private GVRSceneObject mStateBoard = null;

    private double mPreviousDegree = 0.0f;
    private long mTimeStamp = 0l;

    private float mAValue = 3.0f;
    private float mBValue = 10.0f;

    private double mFinalDegree = 0.0f;

    private Gyroscope mGyroscope = null;

    @Override
    public void onStep() {

}
}
