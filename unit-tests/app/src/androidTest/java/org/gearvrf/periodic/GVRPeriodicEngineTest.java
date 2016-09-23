package org.gearvrf.periodic;

import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDrawFrameListener;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRScaleAnimation;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.periodic.GVRPeriodicEngine;
//import org.junit.Test;
//import org.junit.runner.RunWith;

/**
 * Created by nathan.a on 24/02/2015.
 */

public class GVRPeriodicEngineTest extends ActivityInstrumentationGVRf {

    private String DELAY_ERROR_MESSAGE = "delay must be >= 0";
    private String PERIOD_ERROR_MESSAGE = "period must be > 0";

     //@Test
     public void ignoretestCreateNewGVRPeriodicEngineTestWithNullContext() {
        try {
            GVRPeriodicEngine check;
            check=GVRPeriodicEngine.getInstance(null);
            fail("Cannot create GVRPeriodicEngine instance with null context");
        } catch (NullPointerException e) {
        }
    }


    //@Test
    public void testCreateNewGVRPeriodicEngineTestWithNullContext() {
        try {
            GVRPeriodicEngine check;
            check = GVRPeriodicEngine.getInstance(null);
            fail("Cannot create GVRPeriodicEngine instance with null context");
        }catch (NullPointerException e) {

        }
    }

    //@Test
    public void testRunAfterNegativeDelay() {
        float delay = -200f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        try {
            periodicEngine.runAfter(new Runnable() {
                @Override
                public void run() {

                }
            }, delay);
            fail("Delay cannot be negative");
        } catch (IllegalArgumentException e) {
            assertEquals(DELAY_ERROR_MESSAGE, e.getMessage());
        }
    }

    //@Test
    public void testRunAfterZeroDelay() {
        float delay = 0;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        periodicEngine.runAfter(new Runnable() {
            @Override
            public void run() {

            }
        }, delay);
    }

    /**
     * valid values for decimal points are:
     * .0
     * .0625
     * .125
     * .1875
     * .25
     * .3125
     * .375
     * .4375
     * .5
     * .5625
     * .625
     * .6875
     * .75
     * .8125
     * .875
     * .9375
     */
    //@Test
    public void testRunAfterPositiveDelay() {
        //float delay = 28.125f;
        float delay = 28.122f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        assertEquals(delay, periodicEngine.runAfter(new Runnable() {
            @Override
            public void run() {

            }
        }, delay).getCurrentWait(), 0.1);
    }

    //@Test
    public void testRunEveryNegativeDelay() {
        float delay = -200f;
        float period = 3.234f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        try {
            periodicEngine.runEvery(new Runnable() {
                @Override
                public void run() {

                }
            }, delay, period);
            fail("Delay cannot be negative");
        } catch (IllegalArgumentException e) {
            assertEquals(DELAY_ERROR_MESSAGE, e.getMessage());
        }
    }

    //@Test
    public void testRunEveryZeroDelay() {
        float delay = 0;
        float period = 3.234f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period);
    }

    //@Test
    public void testRunEveryPositiveDelay() {
        float delay = 28.124985f;
        float period = 3.234f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        assertEquals(delay, periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period).getCurrentWait(), 0.005);
    }

    //@Test
    public void testRunEveryNegativePeriod() {
        float delay = 0f;
        float period = -8.2564f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        try {
            periodicEngine.runEvery(new Runnable() {
                @Override
                public void run() {

                }
            }, delay, period);
            fail("Period cannot be negative");
        } catch (IllegalArgumentException e) {
            assertEquals(PERIOD_ERROR_MESSAGE, e.getMessage());
        }
    }

    //@Test
    public void testRunEveryZeroPeriod() {
        float delay = 0f;
        float period = 0f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        try {
            periodicEngine.runEvery(new Runnable() {
                @Override
                public void run() {

                }
            }, delay, period);
            fail("Delay cannot be zero");
        } catch (IllegalArgumentException e) {
            assertEquals(PERIOD_ERROR_MESSAGE, e.getMessage());
        }
    }

    //@Test
    //TODO: Remover assertEquals
    public void testRunEveryPositivePeriod() {
        float delay = 0f;
        float period = 12.25f;
        GVRPeriodicEngine periodicEngine = GVRPeriodicEngine.getInstance(TestDefaultGVRViewManager.mGVRContext);
        periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period);


        periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period);

        periodicEngine.runAfter((new Runnable() {
            @Override
            public void run() {}
        }),2.0f);

        GVRPeriodicEngine.PeriodicEvent periodicEvent = periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period, 3);

        GVRPeriodicEngine.PeriodicEvent periodicEvent1 = periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period, 1);

        GVRPeriodicEngine.PeriodicEvent periodicEvent2 = periodicEngine.runEvery(new Runnable() {
            @Override
            public void run() {

            }
        }, delay, period, 0);


        periodicEvent.getCurrentWait();
        periodicEvent.getRunCount();
        periodicEvent.runAfter(1.0f);
        periodicEvent.runEvery(1.0f, 1.0f);
        periodicEvent.runEvery(1.0f, 1.0f, 0);
        periodicEvent.runEvery(1.0f, 1.0f, 1);
        periodicEvent.runEvery(1.0f, 1.0f, new GVRPeriodicEngine.KeepRunning() {
            @Override
            public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                return true;
            }
        });
        periodicEvent.runEvery(1.0f, 1.0f, 2);
        periodicEvent.runEvery(1.0f, 1.0f, 2);
        periodicEvent.cancel();
    }


    public void testQueued() {

        final GVRSceneObject gvrSceneObject = new GVRSceneObject(TestDefaultGVRViewManager.mGVRContext);
        final GVRAnimationEngine animationEngine = TestDefaultGVRViewManager.mGVRContext.getAnimationEngine();
        Runnable pulse = new Runnable() {
            public void run() {
                new GVRScaleAnimation(gvrSceneObject.getTransform(), 0.5f, 2f) //
                        .setRepeatMode(GVRRepeatMode.PINGPONG) //
                        .start(animationEngine);
            }
        };
        TestDefaultGVRViewManager.mGVRContext.getPeriodicEngine().runEvery(pulse, 1f, 2f, 10);
        TestDefaultGVRViewManager.mGVRContext.getPeriodicEngine().runEvery(pulse, 1f, 2f, 10);

        GVRContext gvrContext = TestDefaultGVRViewManager.mGVRContext;
        gvrContext.registerDrawFrameListener(new GVRDrawFrameListener() {
            @Override
            public void onDrawFrame(float frameTime) {

            }
        });
    }

}
