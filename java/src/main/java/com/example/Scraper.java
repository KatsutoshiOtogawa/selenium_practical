
package com.example;
import java.util.HashMap;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openqa.selenium.TimeoutException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
/**
 * 
 */
public abstract class Scraper {

    protected WebDriver Driver;
    protected int TransitionInterval;
    protected WebDriverWait Wait;
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Properties properties;

    abstract protected ChromeDriver constructor();
    public void setupScraping() throws TimeoutException,InterruptedException
    {
        // // Driver.WindowHandles.
        // self.driver.set_window_size(1440, 797)
        goToTopPage();
        ageValidation();
        userLogin();
        shownCoupon();
    }
    abstract protected void goToTopPage() throws TimeoutException,InterruptedException;
    abstract protected void ageValidation() throws InterruptedException;
    abstract protected void userLogin() throws TimeoutException,InterruptedException;
    abstract protected void shownCoupon() throws InterruptedException;
    abstract protected void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException;
    abstract protected HashMap<String,Object> getShopItemInfo(String itemName) throws TimeoutException,InterruptedException;
    abstract protected HashMap<String,Object> getShopItemAffiriateInfo() throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException;
    abstract public HashMap<String,Object> fetchScraping(String itemName) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException,NotFoundException;
    public void destructor()
    {
        Driver.close();
        Driver.quit();
    }
}