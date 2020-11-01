package com.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.TimeoutException;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Unit test for ControllerDLSite.
 */
public class ControllerDLSiteTest extends ControllerTest
{
    private static final Logger logger = LogManager.getFormatterLogger(ControllerDLSiteTest.class);

    // @Rule
    // public ExpectedException expectedException = ExpectedException.none();

    /**
     * Create the test case
     *
     * 
     */
    public ControllerDLSiteTest()
    {

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


    // @Test
    // void exceptionTesting() {
    //     Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
    //         throw new IllegalArgumentException("a message");
    //     });
    //     assertEquals("a message", exception.getMessage());
    // }

    // @Test
    // void dependentAssertions() {
    //     // Within a code block, if an assertion fails the
    //     // subsequent code in the same block will be skipped.
    //     assertAll("properties",
    //         () -> {
    //             String firstName = person.getFirstName();
    //             assertNotNull(firstName);

    //             // Executed only if the previous assertion is valid.
    //             assertAll("first name",
    //                 () -> assertTrue(firstName.startsWith("J")),
    //                 () -> assertTrue(firstName.endsWith("n"))
    //             );
    //         },
    //         () -> {
    //             // Grouped assertion, so processed independently
    //             // of results of first name assertions.
    //             String lastName = person.getLastName();
    //             assertNotNull(lastName);

    //             // Executed only if the previous assertion is valid.
    //             assertAll("last name",
    //                 () -> assertTrue(lastName.startsWith("D")),
    //                 () -> assertTrue(lastName.endsWith("e"))
    //             );
    //         }
    //     );
    // }

    // IllegalArgumentException,FileNotFoundException,IOException
    // @Test(expected = FileNotFoundException.class)

    @Test
    public void testControllerDLSiteHavingConstructor()
    {

        logger.debug("controllerDLSiteHavingConstructor is being checkd...");

        assertAll(
            () -> {
                assertThrows(FileNotFoundException.class, () -> {

                    logger.debug("checkking FileNotFoundException is occured");
                    ControllerDLSite controller = new ControllerDLSite("/path/to/not/exists");

                    controller.destructor();
                });
            }
            ,() -> {
                assertThrows(IllegalStateException.class, () -> {

                    logger.debug("checkking IllegalStateException is occured");
                    ControllerDLSite controller = new ControllerDLSite(String.join("/","target","resources",".env"));
                    controller.destructor();

                });
            }
        );
        
        logger.debug("controllerDLSiteHavingConstructor was checked.");
        
    }
    @Test
    public void testSetupController() throws Exception
    {
        logger.debug("testSetupController is being checkd...");

        Path ref = Paths.get(String.join("/","target","chromedriver"));
        Path destination = Paths.get(String.join("/",".","chromedriver"));

        try
        {
            Files.copy(ref, destination);
        }catch(Exception ex){
            logger.error("testSetupController message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        
        File fp = new File(destination.toString());

        ControllerDLSite controller = null;

        try
        {
            controller = new ControllerDLSite(String.join("/","target","resources",".env"));
        }catch(Exception ex){
            logger.error("testSetupController message [%s]",ex.getMessage());
            ex.printStackTrace();
            if (fp.exists()) {
                fp.delete();
            }
            throw ex;
        }

        // assertAll(
        //     () -> {
        //         assertThrows(TimeoutException.class, () -> {

        //             logger.debug("checkking TimeoutException is occured");
        //             controller.setupController();
        //             controller.destructor();
        //             throw new Throwable();
        //         });
        //     }
        //     ,() -> {
        //         assertThrows(InterruptedException.class, () -> {

        //             logger.debug("checkking InterruptedException is occured");
        //             controller.setupController();
        //             controller.destructor();
        //             throw new Throwable();
        //         });
        //     }
        // );

        controller.destructor();
        if (fp.exists()) {
            fp.delete();
        }

        logger.debug("testSetupController was checked.");
    }

    // logger.debug("checkking IllegalStateException is occured");
        // ControllerDLSite controller = new ControllerDLSite(String.join("/","target","resources",".env"));
        // controller.destructor();

    // public void setupController() throws TimeoutException,InterruptedException
    // {
    //     logger.info("setupController start");
    //     try
    //     {
    //         scrapingDLSite.setupScraping();
    //     }catch(TimeoutException ex){
    //         logger.error("main message [%s]",ex.getMessage());
    //         throw ex;
    //     }catch(InterruptedException ex){
    //         logger.error("main message [%s]",ex.getMessage());
    //         throw ex;
    //     }catch(Exception ex){
    //         logger.error("main message [%s]",ex.getMessage());
    //         throw ex;
    //     }
    //     logger.info("setupController finish");
    // }
}
