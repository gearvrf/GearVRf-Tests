package org.gearvrf.utility;

import org.gearvrf.ActivityInstrumentationGVRf;
import org.gearvrf.GVRTestActivity;

import java.util.Arrays;

/**
 * Created by j.elidelson on 6/8/2015.
 */
public class AssertTest extends ActivityInstrumentationGVRf {

    public AssertTest() {
        super(GVRTestActivity.class);
    }

    public void testAssertcheckNotNull(){

        try {
            Assert.checkNotNull("test", null);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckStringNotNullOrEmpty(){

        try {
            Assert.checkStringNotNullOrEmpty("test", null);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
        try {
            Assert.checkStringNotNullOrEmpty("test", "");
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength1(){

        try {
            Assert.checkArrayLength("test", 1, 2);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength2(){

        boolean b[]={true,false};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength3(){

        char b[]={'a','b'};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength4(){

        byte b[]={'a','b'};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength5(){

        short b[]={'a','b'};
        try {
            Assert.checkArrayLength("test", b, 2);
            Assert.checkArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength6(){

        int b[]={1,2};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength7(){

        long b[]={1,2};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength8(){

        float b[]={1.0f,2.0f};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength9(){

        double b[]={1.0f,2.0f};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckArrayLength10(){

        Object b[]={1.0f,2.0f};
        try {
            Assert.checkArrayLength("test", b,2);
            Assert.checkArrayLength("test", b,3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckMinimumArrayLength(){
        try {
            Assert.checkMinimumArrayLength("test", 2, 2);
            Assert.checkMinimumArrayLength("test", 2, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckMinimumArrayLength1(){
        boolean b[] = {true,true};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckMinimumArrayLength2(){
        char b[] = {'a','b'};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckMinimumArrayLength3(){
        byte b []={'a','b'};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckMinimumArrayLength4(){
        short b []={'a','b'};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckMinimumArrayLength5(){
        int b []={1,2};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckMinimumArrayLength6(){
        long b []={1,2};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckMinimumArrayLength7(){
        float b []={1.0f,2.0f};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckMinimumArrayLength8(){
        double b []={1.0f,2.0f};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckMinimumArrayLength9(){
        Object b []={'a','b'};
        try {
            Assert.checkMinimumArrayLength("test", b, 2);
            Assert.checkMinimumArrayLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }



    public void testAssertcheckDivisibleDataLength(){
        try {
            Assert.checkDivisibleDataLength("test", 3, 3);
            Assert.checkDivisibleDataLength("test", 0, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
        try {
            Assert.checkDivisibleDataLength("test", 3, 3);
            Assert.checkDivisibleDataLength("test", 2, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckDivisibleDataLength1(){
        boolean[] b = new boolean[2];
        Arrays.fill(b, Boolean.FALSE);
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckDivisibleDataLength2(){
        char b[] = {'a','b'};
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckDivisibleDataLength3(){
        byte b []={(byte)1,(byte)2};
        try {
            Assert.checkDivisibleDataLength("test",b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckDivisibleDataLength4(){
        short b []={(short)1,(short)2};
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

    public void testAssertcheckDivisibleDataLength5(){
        int b []={(int)1,(int)2};
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckDivisibleDataLength6(){
        long b []={1l,2l};
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckDivisibleDataLength7(){
        float b []={1.0f,2.0f};
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckDivisibleDataLength8(){
        double b []= {1.0d,2.0d};
        b[0]=(double)1;b[1]=(double)2;
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("double", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }
    public void testAssertcheckDivisibleDataLength9(){
        Object b []= {null,null};
        int a=0;int c=1;
        b[0]=a;b[1]=c;
        try {
            Assert.checkDivisibleDataLength("test", b, 2);
            Assert.checkDivisibleDataLength("test", b, 3);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }


    public void testAssertcheckFloatNotNaNOrInfinity(){
        Object b []={'a','b'};
        try {
            Assert.checkFloatNotNaNOrInfinity("test",1.0f,2.0f,3.0f);
            Assert.checkFloatNotNaNOrInfinity("test",1.0f,2.0f, Float.NaN);
            fail("should throws Exceptions.IllegalArgument");
        }catch (IllegalArgumentException e){}
    }

}
