
package com.example;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openqa.selenium.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 */
public abstract class Controller {

    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Storage storage;
    protected DB db;
    protected Scraper scrapingDLSite;
    protected Logger logger;
    abstract protected Properties openProperties(String path) throws FileNotFoundException,IOException,UnsupportedEncodingException;
    abstract protected void constructor(String path) throws IllegalArgumentException,FileNotFoundException,IOException,UnsupportedEncodingException;
    abstract public void destructor();
    abstract public void setupController() throws TimeoutException,InterruptedException;
}