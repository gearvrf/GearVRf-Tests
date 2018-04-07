package org.gearvrf.io;

import android.app.Activity;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.gearvrf.GVRContext;
import org.gearvrf.io.GVRCursorController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class TestSendEvents {

    volatile boolean doneProducing = false;

    /**
     * Tests thread-safety of producer and consumer side. Verifies the
     * result by checking all events were consumed in the order they
     * were produced.
     */
    public boolean test1(final GVRContext context) {
        // total number of events produced controlled by these two
        final int outerLoops = 100;
        final int innerLoops = 10;

        // store the consumed events here; need for results verification
        Looper.prepare();
        final long[] recorder = new long[outerLoops*innerLoops];
        Arrays.fill(recorder, 0);

        // set up the test environment
        final Activity dummyActivity = new Activity() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return true;
            }

            int index;
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                recorder[index++] = ev.getDownTime();
                return true;
            }
        };
        Looper.myLooper().quit();

        final GVRGearCursorController.SendEvents sendEvents = new GVRGearCursorController.SendEvents(context);

        final Runnable producer = new Runnable() {
            @Override
            public void run() {
                int i = 0;

                final ArrayList<KeyEvent> keyEvents = new ArrayList<>();
                final ArrayList<MotionEvent> motionEvents = new ArrayList<>();

                for (int j = 0; j < outerLoops; ++j) {
                    motionEvents.clear();

                    for (int k = 0; k < innerLoops; ++k) {
                        final MotionEvent event = MotionEvent.obtain(++i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                        motionEvents.add(event);
                    }
                    // produce
                    sendEvents.init(keyEvents, motionEvents);
                }

                doneProducing = true;
            }
        };

        final CountDownLatch done = new CountDownLatch(1);
        final Runnable consumer = new Runnable() {
            @Override
            public void run() {
                while (!doneProducing) {
                    // consume
                    sendEvents.run();
                }
                done.countDown();
            }
        };

        new Thread(consumer).start();
        new Thread(producer).start();

        try {
            done.await();
        } catch (InterruptedException e) {
            return false;
        }

        // verification
        int i = 0;
        for (long el : recorder) {
            if (el != ++i) {
                return false;
            }
        }

        return true;
    }
}
