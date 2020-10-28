package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import org.openqa.selenium.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.lang.Thread;
import java.util.Date;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
/**
 * 
 */
public class ScrapingDLSite
{
    private WebDriver Driver;
    private int TransitionInterval;
    private WebDriverWait Wait;
    private String CreatedAt;
    private String TableName;
    private String ShopName;
    private Properties properties;
    private static final Logger logger = LogManager.getFormatterLogger(ScrapingDLSite.class);
    //private DynamoDbClient DynamoDB;

    public ScrapingDLSite(Properties properties)
    {
        this.Driver = new ChromeDriver();
        
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.Wait = new WebDriverWait(Driver, 60);
        this.TransitionInterval = 10 * 1000;
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";
        // this.DynamoDbClient = connection;

        this.properties = properties;
        // aws credential propertiesよりも環境変数を優先しているので
        // それと同じような作りにするとよいはず。

    }

    public void destructor()
    {
        Driver.close();
        Driver.quit();
    }

    public void setupScraping() throws TimeoutException,InterruptedException
    {
        // // Driver.WindowHandles.
        // self.driver.set_window_size(1440, 797)
        goToTopPage();
        ageValidation();
        userLogin();
        shownCoupon();
    }

    private void goToTopPage() throws TimeoutException,InterruptedException
    {
        Driver.get("https://www.dlsite.com/index.html");
        Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[contains(text(),\'同人\')])[2]")))
            .click();
        Thread.sleep(TransitionInterval);
    }

    private void ageValidation() throws InterruptedException
    {
        // this popup is shown when you visit first time.
        // Because of ignore Exception operation.
        try
        {
            Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li.btn_yes.btn-approval > a")))
            .click();
        }catch(TimeoutException ignored)
        {
            logger.info("age validation isnt shown.");
        }
        Thread.sleep(TransitionInterval);
    }

    private void userLogin() throws TimeoutException,InterruptedException
    {
        Wait.until(ExpectedConditions.elementToBeClickable(By.linkText("ログイン")))
            .click();

        Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_id")))
            .click();
        
        Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_id")))
            .sendKeys(System.getenv("DLSITE_ID") != null ? System.getenv("DLSITE_ID") : properties.getProperty("DLSITE_ID"));

        Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_password")))
            .click();
        
        Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_password")))
            .sendKeys(System.getenv("DLSITE_PASSWORD") != null ? System.getenv("DLSITE_PASSWORD") : properties.getProperty("DLSITE_PASSWORD"));
        
        Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".type-clrDefault")))
            .click();
        // WebDriverWait(self.driver, 60).until(expected_conditions.element_to_be_clickable((By.CSS_SELECTOR, ".type-clrDefault")))
        //     self.driver.find_element(By.CSS_SELECTOR, ".type-clrDefault").click()
        Thread.sleep(TransitionInterval);
    }

    private void shownCoupon() throws InterruptedException
    {
        // close modal window for qupon. qupon is shown when user is just login.
        // this popup dont know to show
        // you dont know this popup show or not.
        // このポップアップが表示されるかどうか分からない。
        logger.info("shownCoupon start");

        try
        {
            Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div > div.modal_close")))
            .click();
        }catch(TimeoutException ignored)
        {
            logger.info("shownCoupon comment Coupon isnt shown");
            // [ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
        }
        Thread.sleep(TransitionInterval);

        logger.info("shownCoupon finish");
    }

    private void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException
    {
        logger.info("searchBox start variable [sentense=%s]",sentense);
        // search for keyword using exact match. and go to page search result.

        // global search use goto type-doujin        
        Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".headerCore-main .floorTab-item.type-doujin")))
            .click();

        Thread.sleep(TransitionInterval);

        // clear the input box for 
        Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
            .clear();
        
        Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
            .click();

        Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
            .sendKeys(sentense);

        Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
            .sendKeys(Keys.ENTER);
            
        Thread.sleep(TransitionInterval);

        try
        {
            Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(String.format(".search_result_img_box_inner a[title='%s']",sentense))))
                .click();
        }catch(TimeoutException ex)
        {
            logger.info("searchBox comment %s is NotFound. variable [sentense=%s]",sentense,sentense);

            throw new NotFoundException(ex);
        }

        Thread.sleep(TransitionInterval);

        logger.info("searchBox finish variable [sentense=%s]",sentense);
    }

    private HashMap<String,Object> fetchSearchResult(String artName) throws TimeoutException,InterruptedException
    {
        logger.info("fetchSearchResult start variable [artName=%s]",artName);

        HashMap<String,Object> data = new HashMap<String,Object>(){{
            put("ShopArtId", "");
            put("ShopName", ShopName);
            put("ArtName", artName);
            put("Monopoly", false);
        }};

        // url からArtIdを取得。
        // ex) https://www.dlsite.com/pro/work/=/product_id/VJ009935.html -> VJ000935
        data.put("ShopArtId", Driver.getCurrentUrl().replaceAll("^.*/","").replaceAll("\\..*$",""));

        logger.info("fetchSearchResult return data=%s", data.toString());
        return data;
    }

    private HashMap<String,Object> fetchSearchResultAffiriate() throws TimeoutException,InterruptedException
    {
        logger.info("fetchSearchResultAffiriate start");
        
        HashMap<String,Object> data = new HashMap<String,Object>(){{
            put("AffiliateUrl", "");
            put("AffiliateBigImageUrl", "");
            put("AffiliateMiddleImageUrl", "");
            put("AffiliateSmallImageUrl", "");
            put("PlayerEmbed", new ArrayList<String>());
            put("Gallery", new ArrayList<String>());
        }};

        Wait.until(ExpectedConditions.elementToBeClickable(By.linkText("アフィリエイトリンク作成")))
            .click();

        Thread.sleep(TransitionInterval);

        WebElement selectElement = Wait.until(ExpectedConditions.elementToBeClickable(By.id("afid")));

        Select selectObject = new Select(selectElement);

        selectObject.selectByVisibleText(
            String.format("%s (%s)"
                ,System.getenv("DLSITE_AFFILIATE_ID") != null ? System.getenv("DLSITE_AFFILIATE_ID") : properties.getProperty("DLSITE_AFFILIATE_ID")
                ,System.getenv("DLSITE_AFFILIATE_SITE") != null ? System.getenv("DLSITE_AFFILIATE_SITE") : properties.getProperty("DLSITE_AFFILIATE_SITE")
            )
        );
        

        // IWebElement element = Driver.FindElement(By.Id("afid"));
                
        // var selectObject = new SelectElement(element);

        // WebElement selectElement = driver.findElement(By.id("selectElementID"));
        // Select selectObject = new Select(selectElement);
        // WebElement element = Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".globalSearchSelect select.globalSearchSelect-list")));

        // Select select = new Select(element);

        // select.selectByVisibleText("すべて");
        
        // select.select_by_visible_text("{} ({})".format(os.environ.get("DLSITE_AFFILIATE_ID"),os.environ.get("DLSITE_AFFILIATE_SITE")))
        // WebElement selectElement = driver.findElement(By.id("selectElementID"));
        // Select selectObject = new Select(selectElement);

        logger.info("fetchSearchResult return data=%s", data.toString());

        return data;
    }

    //public HashMap<String,Object> fetchScraping(String artName) throws TimeoutException,InterruptedException,NotFoundException
    public void fetchScraping(String artName) throws TimeoutException,InterruptedException,NotFoundException
    {
        // check ArtName is exist DLSite.
        searchBox(artName);

        fetchSearchResult(artName);

        fetchSearchResultAffiriate();

        //HashMap<String,String> data 

        //return data;
    }
}