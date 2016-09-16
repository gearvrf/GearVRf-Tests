package org.gearvrf.utility;


import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.utility.Exceptions;
import org.gearvrf.utility.RuntimeAssertion;

/**
 * Created by j.elidelson on 5/14/2015.
 */
public class ExceptionsTest extends ActivityInstrumentationGVRf {

    public ExceptionsTest() {
        super(GVRTestActivity.class);
    }

    public void testIllegalArgument() {
        Exceptions exceptions = new Exceptions() {
        };

        IllegalArgumentException IA = Exceptions.IllegalArgument("%f\n", 2.0f);
        assertNotNull(IA);
    }

    public void testIllegalArgumentThrowable() {
        Throwable aux = new Throwable("*",null);
        IllegalArgumentException IA = Exceptions.IllegalArgument(aux,"%f\n", 2.0f);
        assertNotNull(IA);
    }

    public void testRuntimeAssertion() {
        RuntimeAssertion RA = Exceptions.RuntimeAssertion("%f\n", 2.0f);
        assertNotNull(RA);
    }

    public void testRuntimeAssertionThrowable() {
        Throwable aux = new Throwable("*",null);
        RuntimeAssertion RA = Exceptions.RuntimeAssertion(aux,"%f\n", 2.0f);
        assertNotNull(RA);
    }

    public void testGVRJniException(){

        GVRJniException.printCallStack("text");
    }
}
