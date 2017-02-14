package org.gearvrf.da_gearvrf;

import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRTestActivity;
import org.gearvrf.GVRVersion;

/**
 * Created by j.elidelson on 6/10/2015.
 */
public class GVRVersionTest extends ActivityInstrumentationGVRf {

    private GVRVersion version;
    private GVRVersion gvrVersion;

    public GVRVersionTest() {
        super(GVRTestActivity.class);
    }

    public void testConstructor(){

        this.version = new GVRVersion();
        assertNotNull(version);
    }

    public void testVersionsNumbers(){

        assertEquals("3.0.1",GVRVersion.CURRENT);
        assertEquals("1.5.0",GVRVersion.V_1_5_0);
        assertEquals("1.6.0",GVRVersion.V_1_6_0);
        assertEquals("1.6.1",GVRVersion.V_1_6_1);
        assertEquals("1.6.2",GVRVersion.V_1_6_2);
        assertEquals("1.6.3",GVRVersion.V_1_6_3);
        assertEquals("1.6.4",GVRVersion.V_1_6_4);
        assertEquals("1.6.5",GVRVersion.V_1_6_5);
        assertEquals("1.6.6",GVRVersion.V_1_6_6);
        assertEquals("1.6.7",GVRVersion.V_1_6_7);
        assertEquals("1.6.8",GVRVersion.V_1_6_8);
        assertEquals("1.6.9",GVRVersion.V_1_6_9);
        assertEquals("2.0.0",GVRVersion.V_2_0_0);
    }
}
