package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.physics.GVRCollisionMatrix;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.gearvrf.utility.Assert;
import org.gearvrf.utility.Exceptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PhysicsCollisionTest {
    private GVRTestUtils gvrTestUtils;
    private Waiter mWaiter;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new
            ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @Before
    public void setUp() throws TimeoutException {
        gvrTestUtils = new GVRTestUtils(ActivityRule.getActivity());
        mWaiter = new Waiter();
        gvrTestUtils.waitForOnInit();
    }

    @Test
    public void testCollisionMask() throws Exception {

        for (int groupA = 0; groupA < 16; groupA++) {
            for (int groupB = 0; groupB < 16; groupB++) {
                if (groupA == groupB) {
                    continue;
                }

                GVRCollisionMatrix gvrCollisionMatrix = new GVRCollisionMatrix();

                gvrCollisionMatrix.enableCollision(groupA, groupB);

                if ((GVRCollisionMatrix.getCollisionFilterGroup(groupA)
                        & gvrCollisionMatrix.getCollisionFilterMask(groupB)) == 0
                        || (GVRCollisionMatrix.getCollisionFilterGroup(groupB)
                        & gvrCollisionMatrix.getCollisionFilterMask(groupA)) == 0) {
                    throw Exceptions.IllegalArgument("Failed to enable collision between groups "
                                                        + groupA + " and " + groupB);
                }

                gvrCollisionMatrix.disableCollision(groupA, groupB);

                if ((GVRCollisionMatrix.getCollisionFilterGroup(groupA)
                        & gvrCollisionMatrix.getCollisionFilterMask(groupB)) != 0
                        || (GVRCollisionMatrix.getCollisionFilterGroup(groupB)
                        & gvrCollisionMatrix.getCollisionFilterMask(groupA)) != 0) {
                    throw Exceptions.IllegalArgument("Failed to disable collision between groups "
                                                        + groupA + " and " + groupB);
                }
            }
        }
    }
}
