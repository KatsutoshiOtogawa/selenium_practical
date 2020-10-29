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

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

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
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

// AwsClientBuilder.EndpointConfiguration

// software.amazon.awssdk.services.apigateway.model;
/**
 * 
 */
public class ControllerDLSite
{
    // private int TransitionInterval;
    private String CreatedAt;
    private String TableName;
    private String ShopName;
    private DynamoDbDLSite dynamodbClient;
    private ScrapingDLSite scrapingDLSite;
    private static final Logger logger = LogManager.getFormatterLogger(ControllerDLSite.class);
    
    public ControllerDLSite(String path) throws IllegalArgumentException,FileNotFoundException,IOException
    {
        // this.Driver = constructor();
        
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());

        Map<String,Object> data = constructor(path);
        this.dynamodbClient = (DynamoDbDLSite) data.get("DynamoDbDLSite");
        this.scrapingDLSite = (ScrapingDLSite) data.get("ScrapingDLSite");

        // this.TableName = "ArtCollection";
        // this.ShopName = "DLSite";

        // this.dynamodbClient = constructor();
        // aws credential propertiesよりも環境変数を優先しているので
        // それと同じような作りにするとよいはず。

    }

    private Properties openProperties(String path) throws FileNotFoundException,IOException,UnsupportedEncodingException
    {

        InputStreamReader fp = null;
        
        try
        {
            fp = new InputStreamReader(new FileInputStream(new File(path)),"UTF-8");
        }catch(FileNotFoundException ex)
        {
            throw ex;
        }catch(UnsupportedEncodingException ex)
        {
            throw ex;
        }

        Properties properties = new Properties();

        try
        {
            properties.load(fp);
        }catch(IOException ex){
            try
            {
                fp.close();
            }catch(IOException ex2){
                throw ex2;
            }
            throw ex;
        }
        
        try
        {
            fp.close();
        }catch(IOException ex){
            throw ex;
        }
        return properties;
    }

    private Map<String,Object> constructor(String path) throws IllegalArgumentException,FileNotFoundException,IOException,UnsupportedEncodingException
    {
        
        Properties properties = openProperties(path);

        return new HashMap<String,Object>(){{
            put("DynamoDbDLSite",new DynamoDbDLSite(properties));
            put("ScrapingDLSite",new ScrapingDLSite(properties));
        }};
    }

    public void destructor()
    {
        dynamodbClient.destructor();
        scrapingDLSite.destructor();
        logger.info("resource close");
    }

    public void setupDB() throws TimeoutException,InterruptedException
    {

        try
        {
            scrapingDLSite.setupScraping();
        }catch(TimeoutException ex)    
        {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            destructor();
            return;
        }catch(Exception ex)
        {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            destructor();
            return;
        }

        // // Driver.WindowHandles.
        // self.driver.set_window_size(1440, 797)
        // goToTopPage();
        // ageValidation();
        // userLogin();
        // shownCoupon();
    }

    // private void goToTopPage() throws TimeoutException,InterruptedException
    // {
    //     Driver.get("https://www.dlsite.com/index.html");
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[contains(text(),\'同人\')])[2]")))
    //         .click();
    //     Thread.sleep(TransitionInterval);
    // }

    // private void ageValidation() throws InterruptedException
    // {
    //     // this popup is shown when you visit first time.
    //     // Because of ignore Exception operation.
    //     try
    //     {
    //         Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li.btn_yes.btn-approval > a")))
    //         .click();
    //     }catch(TimeoutException ignored)
    //     {
    //         logger.info("age validation isnt shown.");
    //     }
    //     Thread.sleep(TransitionInterval);
    // }

    // private void userLogin() throws TimeoutException,InterruptedException
    // {
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.linkText("ログイン")))
    //         .click();

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_id")))
    //         .click();
        
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_id")))
    //         .sendKeys(System.getenv("DLSITE_ID") != null ? System.getenv("DLSITE_ID") : properties.getProperty("DLSITE_ID"));

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_password")))
    //         .click();
        
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("form_password")))
    //         .sendKeys(System.getenv("DLSITE_PASSWORD") != null ? System.getenv("DLSITE_PASSWORD") : properties.getProperty("DLSITE_PASSWORD"));
        
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".type-clrDefault")))
    //         .click();
    //     // WebDriverWait(self.driver, 60).until(expected_conditions.element_to_be_clickable((By.CSS_SELECTOR, ".type-clrDefault")))
    //     //     self.driver.find_element(By.CSS_SELECTOR, ".type-clrDefault").click()
    //     Thread.sleep(TransitionInterval);
    // }

    // private void shownCoupon() throws InterruptedException
    // {
    //     // close modal window for qupon. qupon is shown when user is just login.
    //     // this popup dont know to show
    //     // you dont know this popup show or not.
    //     // このポップアップが表示されるかどうか分からない。
    //     logger.info("shownCoupon start");

    //     try
    //     {
    //         Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div > div.modal_close")))
    //         .click();
    //     }catch(TimeoutException ignored)
    //     {
    //         logger.info("shownCoupon comment Coupon isnt shown");
    //         // [ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
    //     }
    //     Thread.sleep(TransitionInterval);

    //     logger.info("shownCoupon finish");
    // }

    // private void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException
    // {
    //     logger.info("searchBox start variable [sentense=%s]",sentense);
    //     // search for keyword using exact match. and go to page search result.

    //     // global search use goto type-doujin        
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".headerCore-main .floorTab-item.type-doujin")))
    //         .click();

    //     Thread.sleep(TransitionInterval);

    //     // clear the input box for 
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
    //         .clear();
        
    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
    //         .click();

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
    //         .sendKeys(sentense);

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.id("search_text")))
    //         .sendKeys(Keys.ENTER);
            
    //     Thread.sleep(TransitionInterval);

    //     try
    //     {
    //         Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(String.format(".search_result_img_box_inner a[title='%s']",sentense))))
    //             .click();
    //     }catch(TimeoutException ex)
    //     {
    //         logger.info("searchBox comment %s is NotFound. variable [sentense=%s]",sentense,sentense);

    //         throw new NotFoundException(ex);
    //     }

    //     Thread.sleep(TransitionInterval);

    //     logger.info("searchBox finish variable [sentense=%s]",sentense);
    // }

    // private HashMap<String,Object> getShopItemInfo(String shopItemName) throws TimeoutException,InterruptedException
    // {
    //     logger.info("getShopItemInfo start variable [shopItemName=%s]",shopItemName);

    //     HashMap<String,Object> data = new HashMap<String,Object>(){{
    //         put("ShopArtId", "");
    //         put("ShopName", ShopName);
    //         put("ShopItemName", shopItemName);
    //         put("Monopoly", false);
    //     }};

    //     // url からArtIdを取得。
    //     // ex) https://www.dlsite.com/pro/work/=/product_id/VJ009935.html -> VJ000935
    //     data.put("ShopArtId", Driver.getCurrentUrl().replaceAll("^.*/","").replaceAll("\\..*$",""));

    //     logger.info("getShopItemInfo return data=%s", data.toString());

    //     return data;
    // }

    // private HashMap<String,Object> getShopItemAffiriateInfo() throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException
    // {
    //     logger.info("getShopItemAffiriateInfo start");
        
    //     ArrayList<String> PlayerEmbed = new ArrayList<String>();

    //     HashMap<String,Object> data = new HashMap<String,Object>(){{
    //         put("AffiliateUrl", "");
    //         put("AffiliateBigImageUrl", "");
    //         put("AffiliateMiddleImageUrl", "");
    //         put("AffiliateSmallImageUrl", "");
    //         put("PlayerEmbed", PlayerEmbed);
    //         put("Gallery", new ArrayList<String>());
    //     }};

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.linkText("アフィリエイトリンク作成")))
    //         .click();

    //     Thread.sleep(TransitionInterval);

    //     WebElement selectElement = Wait.until(ExpectedConditions.elementToBeClickable(By.id("afid")));

    //     Select selectObject = new Select(selectElement);

    //     selectObject.selectByVisibleText(
    //         String.format("%s (%s)"
    //             ,System.getenv("DLSITE_AFFILIATE_ID") != null ? System.getenv("DLSITE_AFFILIATE_ID") : properties.getProperty("DLSITE_AFFILIATE_ID")
    //             ,System.getenv("DLSITE_AFFILIATE_SITE") != null ? System.getenv("DLSITE_AFFILIATE_SITE") : properties.getProperty("DLSITE_AFFILIATE_SITE")
    //         )
    //     );

        
    //     data.put("AffiliateUrl"
    //         , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_wname_sns a"))).getAttribute("href")
    //     );
        
    //     data.put("AffiliateSmallImageUrl"
    //         , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_mini img"))).getAttribute("src")
    //     );

    //     data.put("AffiliateMiddleImageUrl"
    //         , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_thum img"))).getAttribute("src")
    //     );

    //     data.put("AffiliateBigImageUrl"
    //         , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_main img"))).getAttribute("src")
    //     );

    //     Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".main_modify_box button.copy_btn")))
    //         .click();


    //     PlayerEmbed.add(
    //         (String)clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor)
    //     );

    //     logger.info("getShopItemAffiriateInfo return data=%s", data.toString());

    //     return data;
    // }

    // public HashMap<String,Object> fetchScraping(String shopItemName) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException,NotFoundException
    // {
    //     // HashMap<String,Object> data = new HashMap<String,Object>(){{
    //     //     put("ShopArtId", "");
    //     //     put("ShopName", ShopName);
    //     //     put("ShopItemName", shopItemName);
    //     //     put("Monopoly", false);
    //     //     put("AffiliateUrl", "");
    //     //     put("AffiliateBigImageUrl", "");
    //     //     put("AffiliateMiddleImageUrl", "");
    //     //     put("AffiliateSmallImageUrl", "");
    //     //     put("PlayerEmbed", new ArrayList<String>());
    //     //     put("Gallery", new ArrayList<String>());
    //     // }};

    //     // check shopItemName is exist DLSite.
    //     logger.info("fetchScraping start");
    //     searchBox(shopItemName);

    //     HashMap<String,Object> shopItemInfo = getShopItemInfo(shopItemName);

    //     HashMap<String,Object> shopItemAffiriateInfo = getShopItemAffiriateInfo();

    //     HashMap<String,Object> data = new HashMap<String,Object>(){{
    //         put("ShopArtId", shopItemInfo.get("ShopArtId"));
    //         put("ShopName", ShopName);
    //         put("ShopItemName", shopItemName);
    //         put("Monopoly", shopItemInfo.get("Monopoly"));
    //         put("AffiliateUrl", shopItemAffiriateInfo.get("AffiliateUrl"));
    //         put("AffiliateBigImageUrl", shopItemAffiriateInfo.get("AffiliateBigImageUrl"));
    //         put("AffiliateMiddleImageUrl", shopItemAffiriateInfo.get("AffiliateMiddleImageUrl"));
    //         put("AffiliateSmallImageUrl", shopItemAffiriateInfo.get("AffiliateSmallImageUrl"));
    //         put("PlayerEmbed", shopItemAffiriateInfo.get("PlayerEmbed"));
    //         put("Gallery", shopItemAffiriateInfo.get("Gallery"));
    //     }};

    //     logger.info("fetchScraping return data=%s", data.toString());

    //     return data;
    // }
}