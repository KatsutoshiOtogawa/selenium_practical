
package com.example;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openqa.selenium.TimeoutException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 */
public abstract class Scraper {
    // public abstract class Scraper<T extends Model> {

    protected WebDriver Driver;
    protected int TransitionInterval;
    protected WebDriverWait Wait;
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Properties properties;
    protected Model model; 
    protected final static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    protected Logger logger;
    public Scraper(Properties properties, Model model)
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.model = model;
        this.properties = properties;
    }

    abstract protected void constructor();
    public void setupScraping() throws TimeoutException,InterruptedException,IllegalArgumentException
    {
        setupWebDriver();
        goToTopPage();
        ageValidation();
        userLogin();
        shownCoupon();
    }

    protected void setupWebDriver() throws IllegalArgumentException
    {
        // Driver.WindowHandles.
        // self.driver.set_window_size(1440, 797)
        int PAGE_DESCREBIED_WAIT = Integer.parseInt(System.getenv("PAGE_DESCREBIED_WAIT") != null ? System.getenv("PAGE_DESCREBIED_WAIT") : properties.getProperty("PAGE_DESCREBIED_WAIT"));
        if(PAGE_DESCREBIED_WAIT < 20){
            throw new IllegalArgumentException("you set 'PAGE_DESCREBIED_WAIT' large than 20.");
        }
        Wait = new WebDriverWait(Driver, PAGE_DESCREBIED_WAIT);

        int PAGE_INTERVAL = Integer.parseInt(System.getenv("PAGE_INTERVAL") != null ? System.getenv("PAGE_INTERVAL") : properties.getProperty("PAGE_INTERVAL"));
        if(PAGE_INTERVAL < 5){
            throw new IllegalArgumentException("you set 'PAGE_INTERVAL' large than 5.");
        }

        TransitionInterval = PAGE_INTERVAL * 1000;
    }

    abstract protected void goToTopPage() throws TimeoutException,InterruptedException;
    abstract protected void ageValidation() throws InterruptedException;
    abstract protected void userLogin() throws TimeoutException,InterruptedException;
    abstract protected void shownCoupon() throws InterruptedException;
    abstract protected void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException;
    abstract protected void getShopItemInfo(String itemName,Storage storage) throws TimeoutException,InterruptedException;
    abstract protected void getShopItemAffiriateInfo(Storage storage) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException;
    public void fetchScraping(String itemName,Storage storage) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException,NotFoundException
    {
        // check shopItemName is exist DLSite.
        searchBox(itemName);
        getShopItemInfo(itemName,storage);
        getShopItemAffiriateInfo(storage);
    }

    public void destructor()
    {
        Driver.close();
        Driver.quit();
    }
}