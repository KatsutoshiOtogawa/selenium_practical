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

import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

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
import java.util.Date;

import java.util.ArrayList;
import java.lang.Thread;

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
public class ScrapingDLSite extends Scraper
{

    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private static final Logger logger = LogManager.getFormatterLogger(ScrapingDLSite.class);
    
    public ScrapingDLSite(Properties properties)
    {
        this.Driver = constructor();
        
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

    protected ChromeDriver constructor()
    {
        logger.info("resource opening...");
        // 手動で開放しないといけないリソースはここに書く。
        ChromeDriver driver = new ChromeDriver();
        logger.info("resource opened");

        return driver;
    }

    @Override
    public void destructor()
    {
        logger.info("resource closesing...");
        super.destructor();
        logger.info("resource closed");
    }

    @Override
    public void setupScraping() throws TimeoutException,InterruptedException
    {
        logger.info("setupScraping start");
        super.setupScraping();
        logger.info("setupScraping finish");
    }

    protected void goToTopPage() throws TimeoutException,InterruptedException
    {
        logger.info("goToTopPage start");
        Driver.get("https://www.dlsite.com/index.html");
        Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[contains(text(),\'同人\')])[2]")))
            .click();
        Thread.sleep(TransitionInterval);

        logger.info("goToTopPage finish");
    }

    protected void ageValidation() throws InterruptedException
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

    protected void userLogin() throws TimeoutException,InterruptedException
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
        Thread.sleep(TransitionInterval);
    }

    protected void shownCoupon() throws InterruptedException
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

    protected void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException
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

    protected HashMap<String,Object> getShopItemInfo(String itemName) throws TimeoutException,InterruptedException
    {
        logger.info("getShopItemInfo start variable [shopItemName=%s]",itemName);

        HashMap<String,Object> data = new HashMap<String,Object>(){{
            put("ShopArtId", "");
            put("CircleName", "");
            put("CircleFollowerNum", "");
            put("UnitsSold", "");
            put("SalePrice", "");
            put("DiscountRate", "");
            put("UntilHavingSale", "");
            put("NormalPrice", "");
            put("Assessment", "");
            put("AssessmentNum", "");
            put("IlustratorName", new ArrayList<String>());
            put("RerationMatome", new ArrayList<String>());
            put("ItemCategory", "");
            put("FileFormat", "");
            put("FileSize", "");
            put("StarNum", "");
            put("AgeVeridation", "");
            put("VoiceActor", new ArrayList<String>());
            put("Genru", new ArrayList<String>());
            put("BuyingUserViewItems", new ArrayList<String>());
            put("LookingUserViewItems", new ArrayList<String>());
            put("reviews", new ArrayList<String>());
            put("Monopoly", false);
        }};
        // url からArtIdを取得。
        // ex) https://www.dlsite.com/pro/work/=/product_id/VJ009935.html -> VJ000935
        data.put("ShopArtId", Driver.getCurrentUrl().replaceAll("^.*/","").replaceAll("\\..*$",""));

        data.put("UnitsSold"
            ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_right']/div[1]/div[2]/dl/dd[1]"))).getAttribute("href")
        );

        
        //*[@id="work_right"]/div[1]/div[2]/dl/dd[1]
        //*[@id="work_right"]/div[1]/div[2]/dl/dd[1]
        // koganName = driver.findElement(By.xpath(("//div[@class='row mb-1']//dd[1]")).getText();

        logger.info("getShopItemInfo return data=%s", data.toString());

        return data;
    }

    protected HashMap<String,Object> getShopItemAffiriateInfo() throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException
    {
        logger.info("getShopItemAffiriateInfo start");
        
        ArrayList<String> PlayerEmbed = new ArrayList<String>();

        HashMap<String,Object> data = new HashMap<String,Object>(){{
            put("AffiliateUrl", "");
            put("AffiliateBigImageUrl", "");
            put("AffiliateMiddleImageUrl", "");
            put("AffiliateSmallImageUrl", "");
            put("PlayerEmbed", PlayerEmbed);
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

        
        data.put("AffiliateUrl"
            , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_wname_sns a"))).getAttribute("href")
        );
        
        data.put("AffiliateSmallImageUrl"
            , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_mini img"))).getAttribute("src")
        );

        data.put("AffiliateMiddleImageUrl"
            , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_thum img"))).getAttribute("src")
        );

        data.put("AffiliateBigImageUrl"
            , Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_main img"))).getAttribute("src")
        );

        Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".main_modify_box button.copy_btn")))
            .click();


        PlayerEmbed.add(
            (String)clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor)
        );

        logger.info("getShopItemAffiriateInfo return data=%s", data.toString());

        return data;
    }

    public HashMap<String,Object> fetchScraping(String itemName) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException,NotFoundException
    {
        logger.info("fetchScraping start");
        // check shopItemName is exist DLSite.
        searchBox(itemName);

        HashMap<String,Object> shopItemInfo = getShopItemInfo(itemName);

        HashMap<String,Object> shopItemAffiriateInfo = getShopItemAffiriateInfo();

        HashMap<String,Object> data = new HashMap<String,Object>(){{
            put("ShopArtId", shopItemInfo.get("ShopArtId"));
            put("Monopoly", shopItemInfo.get("Monopoly"));
            put("CircleName", shopItemInfo.get("CircleName"));
            put("CircleFollowerNum", shopItemInfo.get("CircleFollowerNum"));
            put("UnitsSold", shopItemInfo.get("UnitsSold"));
            put("SalePrice", shopItemInfo.get("SalePrice"));
            put("DiscountRate", shopItemInfo.get("DiscountRate"));
            put("UntilHavingSale", shopItemInfo.get("UntilHavingSale"));
            put("NormalPrice", shopItemInfo.get("NormalPrice"));
            put("Assessment", shopItemInfo.get("Assessment"));
            put("AssessmentNum", shopItemInfo.get("AssessmentNum"));
            put("IlustratorName", shopItemInfo.get("IlustratorName"));
            put("RerationMatome", shopItemInfo.get("RerationMatome"));
            put("ItemCategory", shopItemInfo.get("ItemCategory"));
            put("FileFormat", shopItemInfo.get("FileFormat"));
            put("FileSize", shopItemInfo.get("FileSize"));
            put("AgeVeridation", shopItemInfo.get("AgeVeridation"));
            put("VoiceActor", shopItemInfo.get("VoiceActor"));
            put("StarNum", shopItemInfo.get("StarNum"));
            put("Genru", shopItemInfo.get("Genru"));
            put("BuyingUserViewItems", shopItemInfo.get("BuyingUserViewItems"));
            put("LookingUserViewItems", shopItemInfo.get("LookingUserViewItems"));
            put("reviews", shopItemInfo.get("reviews"));
            put("AffiliateUrl", shopItemAffiriateInfo.get("AffiliateUrl"));
            put("AffiliateBigImageUrl", shopItemAffiriateInfo.get("AffiliateBigImageUrl"));
            put("AffiliateMiddleImageUrl", shopItemAffiriateInfo.get("AffiliateMiddleImageUrl"));
            put("AffiliateSmallImageUrl", shopItemAffiriateInfo.get("AffiliateSmallImageUrl"));
            put("PlayerEmbed", shopItemAffiriateInfo.get("PlayerEmbed"));
            put("Gallery", shopItemAffiriateInfo.get("Gallery"));
        }};

        logger.info("fetchScraping return data=%s", data.toString());

        return data;
    }
}