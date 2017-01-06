package org.gearvrf.sample.LodTest;

import android.os.Bundle;
import android.view.MotionEvent;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRScene;

public class LODTestActivity extends GVRActivity
{
    LODTestMain lodMain;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        lodMain = new LODTestMain();
        setMain(lodMain, "gvr.xml");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            final GVRScene mainScene = getGVRContext().getMainScene();
            mainScene.setStatsEnabled(!mainScene.getStatsEnabled());
        }
        return super.dispatchTouchEvent(event);
    }

}
