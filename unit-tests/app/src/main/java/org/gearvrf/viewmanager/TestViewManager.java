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
    public void onInitTest() {


    }

    @Override
    public void onStep() {

/*

        float[] lookAt = mGVRContext.getMainScene().getMainCameraRig()
                .getLookAt();
        double degree = Math.atan2(lookAt[0], -lookAt[2]) * 180.0 / Math.PI;
        if (degree < 0.0) {
            degree += 360.0;
        }

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - mTimeStamp) / 1000.0f;
        mTimeStamp = currentTime;

        double deltaDegree = degree - mPreviousDegree;
        mPreviousDegree = degree;

        if (deltaDegree > 180.0f) {
            deltaDegree -= 360.0f;
        } else if (deltaDegree < -180.0f) {
            deltaDegree += 360.0f;
        }

        float angularVelocity = mGyroscope.getMagnitude();

        Log.v("", String.format("degree : %f", degree));
        Log.v("", String.format("angularVelocity : %f", angularVelocity));

        if (mState == State.Idle) {
            if (angularVelocity < mAValue && angularVelocity > -mAValue) {
                mState = State.Ready;
            }
        } else if (mState == State.Ready) {
            if (angularVelocity > mAValue || angularVelocity < -mAValue) {
                mState = State.Rotating;
            }
        } else if (mState == State.Rotating) {
            if (angularVelocity < mAValue || angularVelocity > -mAValue) {
                if ((degree >= 0.0f && degree < mBValue)
                        || (degree > 360.0f - mBValue && degree < 360.0f)) {
                    mState = State.Pass;
                } else {
                    mState = State.Fail;
                }
                mFinalDegree = degree;
            }
        }

        Bitmap degreeBitmap = GVRTextBitmapFactory.create(1024, 128,
                String.format("degree : %.2f", degree), 50, Align.LEFT,
                Color.YELLOW, Color.TRANSPARENT);
        GVRMaterial degreeMaterial = mDegreeBoard.getRenderData().getMaterial();
        degreeMaterial.setMainTexture(new GVRBitmapTexture(mGVRContext,
                degreeBitmap));
        degreeBitmap.recycle();

        Bitmap angularVelocityBitmap = GVRTextBitmapFactory.create(1024, 128,
                String.format("velocity : %.2f", angularVelocity), 50,
                Align.LEFT, Color.YELLOW, Color.TRANSPARENT);
        GVRMaterial angularVelocityMaterial = mAngularVelocityBoard
                .getRenderData().getMaterial();
        angularVelocityMaterial.setMainTexture(new GVRBitmapTexture(
                mGVRContext, angularVelocityBitmap));
        angularVelocityBitmap.recycle();

        Bitmap aValueBitmap = GVRTextBitmapFactory.create(1024, 128, String
                .format("ZRO : %.2f, Spec degree : %.2f", mAValue, mBValue),
                30, Align.LEFT, Color.YELLOW, Color.TRANSPARENT);
        GVRMaterial aValueMaterial = mValueBoard.getRenderData().getMaterial();
        aValueMaterial.setMainTexture(new GVRBitmapTexture(mGVRContext,
                aValueBitmap));
        aValueBitmap.recycle();

        Bitmap stateBitmap = null;

        switch (mState) {
        case Idle:
            stateBitmap = GVRTextBitmapFactory.create(1024, 128, "", 50,
                    Align.LEFT, Color.TRANSPARENT, Color.TRANSPARENT);
            break;
        case Ready:
            stateBitmap = GVRTextBitmapFactory.create(1024, 128, "Ready", 50,
                    Align.LEFT, Color.BLACK, Color.WHITE);
            break;
        case Rotating:
            stateBitmap = GVRTextBitmapFactory.create(1024, 128, "", 50,
                    Align.LEFT, Color.TRANSPARENT, Color.TRANSPARENT);
            break;
        case Pass:
            stateBitmap = GVRTextBitmapFactory.create(1024, 128,
                    String.format("PASS degree : %.2f", mFinalDegree), 50,
                    Align.LEFT, Color.BLACK, Color.GREEN);
            break;
        case Fail:
            stateBitmap = GVRTextBitmapFactory.create(1024, 128,
                    String.format("FAIL degree : %.2f", mFinalDegree), 50,
                    Align.LEFT, Color.BLACK, Color.RED);
            break;
        }

        GVRMaterial stateMaterial = mStateBoard.getRenderData().getMaterial();
        stateMaterial.setMainTexture(new GVRBitmapTexture(mGVRContext,
                stateBitmap));
        stateBitmap.recycle();
    }

    public void onDoubleTap() {
        mState = State.Idle;
    }

    public void addAValue(float a) {
        mAValue += a;
        if (mAValue < 0.0f) {
            mAValue = 0.0f;
        }
    }

    public void addBValue(float b) {
        mBValue += b;
        if (mBValue < 0.0f) {
            mBValue = 0.0f;
        }
    }

*/}
}
