package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for ControllerDLSite.
 */
public class ControllerDLSiteTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ControllerDLSiteTest( String testName )
    {
        super( testName );

    }

    /**
     * setUp 
     * 
     */
    protected void setUp()
    {
        
    }

    protected void tearDown()
    {

    }

    public void testControllerDLSiteHavingConstructor()
    {
        // Propertiesへのパスを入力。
        // 存在しないパス、Propertiesの形式が違うファイル、正しいパス。正しいパス以外はエラー。
        // new ControllerDLSite()
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ControllerDLSiteTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
