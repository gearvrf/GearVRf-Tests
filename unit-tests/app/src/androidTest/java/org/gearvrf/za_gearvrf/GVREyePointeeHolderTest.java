package org.gearvrf.za_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVREyePointee;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by j.elidelson on 8/31/2015.
 */
public class GVREyePointeeHolderTest extends ActivityInstrumentationGVRf {

    public void testConstructor(){
        GVREyePointeeHolder gvrEyePointeeHolder = new GVREyePointeeHolder(TestDefaultGVRViewManager.mGVRContext);
        assertNotNull(gvrEyePointeeHolder);
    }

    public void testaddPointee(){
        GVREyePointeeHolder gvrEyePointeeHolder = new GVREyePointeeHolder(TestDefaultGVRViewManager.mGVRContext);
        Future<GVREyePointee> gvrEyePointeeFuture = new Future<GVREyePointee>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public GVREyePointee get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public GVREyePointee get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
        gvrEyePointeeHolder.addPointee(gvrEyePointeeFuture);
        GVRMesh gvrMesh = new GVRMesh(TestDefaultGVRViewManager.mGVRContext);
        GVRMeshEyePointee gvrEyePointee = new GVRMeshEyePointee(gvrMesh);
        gvrEyePointeeHolder.removePointee(gvrEyePointee);
    }

    public void testsetgetEnable(){
        GVREyePointeeHolder gvrEyePointeeHolder = new GVREyePointeeHolder(TestDefaultGVRViewManager.mGVRContext);

        gvrEyePointeeHolder.setEnable(true);
        assertEquals(true,gvrEyePointeeHolder.getEnable());
    }

    public void testgetHit(){
        GVREyePointeeHolder gvrEyePointeeHolder = new GVREyePointeeHolder(TestDefaultGVRViewManager.mGVRContext);

        float gh[] = gvrEyePointeeHolder.getHit();
        assertNotNull(gh);
    }

    public void ignoretestgetOwnerObject(){
        GVREyePointeeHolder gvrEyePointeeHolder = new GVREyePointeeHolder(TestDefaultGVRViewManager.mGVRContext);

        GVRSceneObject gvrSceneObject = gvrEyePointeeHolder.getOwnerObject();
        assertNotNull(gvrSceneObject);
    }

}
