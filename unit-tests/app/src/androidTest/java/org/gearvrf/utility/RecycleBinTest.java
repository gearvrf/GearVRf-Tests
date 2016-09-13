package org.gearvrf.utility;

import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.utility.RecycleBin;

/**
 * Created by j.elidelson on 6/8/2015.
 */
public class RecycleBinTest extends ActivityInstrumentationGVRf {

    private Integer ax[] = {1, 2, 3, 4, 5};
    private Integer bx[] = null;
    private RecycleBin <Integer[]> recycleBinInteger;

    public void testAssertCreateHardBin(){
        recycleBinInteger = RecycleBin.hard();
        recycleBinInteger.put(ax);
        bx = recycleBinInteger.get();
        recycleBinInteger.synchronize();
        assertEquals(ax, bx);
    }

    public void testAssertSoftBinCreate(){
        recycleBinInteger = RecycleBin.soft();
        recycleBinInteger.put(ax);
        bx = recycleBinInteger.get();
        recycleBinInteger.synchronize();
        assertEquals(ax,bx);
    }

    public void testAssertWeakBinCreate(){
        recycleBinInteger = RecycleBin.weak();
        recycleBinInteger.put(ax);
        bx = recycleBinInteger.get();
        recycleBinInteger.synchronize();
        assertEquals(ax, bx);

        recycleBinInteger.put(bx);
        bx = recycleBinInteger.get();
    }


}
