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
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
    
    public ScrapingDLSite(Properties properties) throws IllegalStateException
    {
        super(properties);
        this.Driver = constructor();
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";

    }

    protected ChromeDriver constructor() throws IllegalStateException
    {
        logger.info("resource opening...");

        ChromeDriver driver = null;
        // 手動で開放しないといけないリソースはここに書く。
        try
        {
            driver = new ChromeDriver();
        }catch(Exception ex)
        {
            throw ex;
        }
        
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
    protected void setupWebDriver() throws IllegalArgumentException
    {
        logger.info("setupWebDriver start");
        super.setupWebDriver();
        logger.info("setupWebDriver finish");
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
            logger.debug("age validation isnt shown.");
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
            logger.debug("shownCoupon comment Coupon isnt shown");
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

    @SuppressWarnings("unchecked")
    protected Map<String,Object> getShopItemInfo(String itemName,Storage storage) throws TimeoutException,InterruptedException
    {
        logger.info("getShopItemInfo start variable [shopItemName=%s]",itemName);

        Map<String,Object> data = (new DLSiteModel()).createShopItemInfoModel();
        // Map<String,Object> data = DLSiteModel.createShopItemAffiliateInfoModel();

        // this frase is after functoned.
        {
            // url からArtIdを取得。
            // ex) https://www.dlsite.com/pro/work/=/product_id/VJ009935.html -> VJ000935
            String ShopItemId = Driver.getCurrentUrl().replaceAll("^.*/","").replaceAll("\\..*$","");
            data.put("ShopItemId", ShopItemId);
            storage.ShopItemId = ShopItemId;
            storage.ShopName = ShopName;
            // 画像ダウンロード先を作成。
            storage.createShopItemIdPath();
        }
        
        try
        {
            data.put("UnitsSold"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_right']/div[1]/div[2]/dl/dt[text() = '販売数：']/following-sibling::dd[1]"))).getAttribute("textContent")
                    .replace(",","")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("NormalPrice"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_buy_box_wrapper']//div[@class='work_buy_label' and text()='価格']/following-sibling::div[1]/*[@class='price']")))
                    .getAttribute("textContent").replace(",","").replace("円","")  
            );
        }catch(TimeoutException ex){

            try
            {
                data.put("NormalPrice"
                    ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_price']//div[@class='work_buy_label' and text()='通常価格']/following-sibling::div[1]/*[@class='price strike']")))                                                      
                        .getAttribute("textContent").replace(",","").replace("円","")  
                );

                data.put("SalePrice"
                    ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_price']//div[@class='work_buy_label' and text()='セール特価']/following-sibling::div[1]/*[@class='price']")))
                        .getAttribute("textContent").replace(",","").replace("円","")  
                );
                
                data.put("DiscountRate"
                    ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_price']//*[@class='type_sale transition']/a/span")))
                        .getAttribute("textContent").replaceAll("%.*?$","")
                );

                data.put("UntilHavingSale"
                    ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_price']//*[@class='type_sale transition']/a/span/span[@class='period']")))
                        .getAttribute("textContent")
                );

            }catch(TimeoutException ex2){

                
            }
            
        }

        try
        {
            data.put("MatomeNum"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='top_wrapper']//a/span[text()='まとめ']/following-sibling::span[1]"))).getAttribute("textContent")
                    .replace(",","").replace("件","")
            );
        }catch(TimeoutException ex){
            
        }

        

        try
        {
            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='top_wrapper']//span[@title='DLsite専売']"));

            if(elements.size() > 0)
            {
                data.put("Monopoly",true);
            }
                
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("StarNum"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_right']//dl/dt[text() = 'お気に入り数：']/following-sibling::dd[1]"))).getAttribute("textContent")
                    .replace(",","")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("ReviewNum"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_right']//dl/dt[text() = 'レビュー数：']/following-sibling::dd[1]/span[1]"))).getAttribute("textContent")
                    .replace(",","")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("Assessment"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#work_right > div.work_right_info > div:nth-child(2) > dl > dd > span.point.average_count"))).getAttribute("textContent")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("AssessmentNum"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_right']//dl/dt[text() = '評価：']/following-sibling::dd[1]/span[@class='count']"))).getAttribute("textContent")
                    .replace(",","").replaceAll("(\\(|\\))","")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("MakerName"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#work_maker span.maker_name > a"))).getAttribute("textContent")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("MakerFollowerNum"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#work_maker span.follow_count"))).getAttribute("textContent")
                    .replace(",","")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '声優']/following-sibling::td[1]/a"));

            Set<String> VoiceActor = (Set<String>)data.get("VoiceActor");
            logger.debug("getShopItemInfo show variable [VoiceActor=%s]",elements.toString());
            for(WebElement element:elements)
            {
                VoiceActor.add(element.getAttribute("textContent"));
            }
            
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'イラスト']/following-sibling::td[1]/a"));
            Set<String> IlustratorName = (Set<String>) data.get("IlustratorName");

            logger.debug("getShopItemInfo show variable [IlustratorName=%s]",elements.toString());
            for(WebElement element:elements)
            {
                IlustratorName.add(element.getAttribute("textContent"));
            }
            
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'シナリオ']/following-sibling::td[1]/a"));
            Set<String> ScreenWriter = (Set<String>) data.get("ScreenWriter");
            logger.debug("getShopItemInfo show variable [ScreenWriter=%s]",elements.toString());
            for(WebElement element:elements)
            {
                ScreenWriter.add(element.getAttribute("textContent"));
            }
            
        }catch(TimeoutException ex){
            
        }

        try
        {
        
            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'ジャンル']/following-sibling::td[1]/div/a"));
            logger.debug("getShopItemInfo show variable [Genru=%s]",elements.toString());
            Set<String> Genru = (Set<String>) data.get("Genru");
            for(WebElement element:elements)
            {
                Genru.add(element.getAttribute("textContent"));
            }
            
        }catch(TimeoutException ex){
            
        }

        try
        {

            String text = Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'ファイル容量']/following-sibling::td[1]/div")))
                    .getAttribute("textContent").replaceAll(",","");

            if(text.contains("GB"))
            {
                data.put("FileSizeUnit","GB");
            }else if(text.contains("MB")){
                data.put("FileSizeUnit","MB");
            }else if(text.contains("KB")){
                data.put("FileSizeUnit","KB");
            }
            data.put("FileSize",text.replaceAll("[^0-9\\.]",""));

        }catch(TimeoutException ex){
            
        }

        try
        {
            data.put("AgeVeridation"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '年齢指定']/following-sibling::td[1]/div/a")))
                    .getAttribute("textContent")
            );
        }catch(TimeoutException ex){
            
        }
        try
        {
            data.put("ReleaseDate"
                ,Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '販売日']/following-sibling::td[1]/a")))
                    .getAttribute("textContent")
            );
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '音楽']/following-sibling::td[1]/a"));

            Set<String> Musician = (Set<String>) data.get("Musician");
            logger.debug("getShopItemInfo show variable [Musician=%s]",elements.toString());
            for(WebElement element:elements)
            {
                Musician.add(element.getAttribute("textContent"));
            }
            
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '作品形式']/following-sibling::td[1]/div/a/span"));
            Set<String> ItemCategory = (Set<String>) data.get("ItemCategory");
            ItemCategory.add(
                Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= '作品形式']/following-sibling::td[1]/div")))
                    .getAttribute("textContent").replaceAll("^.*/ ","")
            );
            
            logger.debug("getShopItemInfo show variable [ItemCategory=%s]",elements.toString());
            for(WebElement element:elements)
            {
                ItemCategory.add(element.getAttribute("textContent"));
            }
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'ファイル形式']/following-sibling::td[1]/div/a/span"));

            Set<String> FileFormat = (Set<String>) data.get("FileFormat");
            FileFormat.add(
                Wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='work_outline']/tbody/tr/th[text()= 'ファイル形式']/following-sibling::td[1]/div")))
                    .getAttribute("textContent").replaceAll("^.*/ ","")
            );
            
            logger.debug("getShopItemInfo show variable [FileFormat=%s]",elements.toString());
            for(WebElement element:elements)
            {
                FileFormat.add(element.getAttribute("textContent"));
            }
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_left']//div[@class='controller_body']//img"));
            Set<String> Gallery = (Set<String>) data.get("Gallery");
            logger.debug("getShopItemInfo show variable [Gallery=%s]",elements.toString());
            for(WebElement element:elements)
            {
                String uri = element.getAttribute("src");
                Gallery.add(uri);

                try
                {
                    storage.transport(uri);
                }catch(Exception ignore)
                {
                    ignore.printStackTrace();
                }
                
            }
        }catch(TimeoutException ex){
            
        }

        try
        {
            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_review_list']//*[@class='reviewer_descrip']"));
            Set<String> Reviews = (Set<String>) data.get("Reviews");
            logger.debug("getShopItemInfo show variable [Reviews=%s]",elements.toString());
            for(WebElement element:elements)
            {
                Reviews.add(element.getAttribute("textContent"));
            }
        }catch(TimeoutException ex){
            
        }

        
        try
        {
         
        
            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='main_inner']//div[@data-type='viewsales2']//div[@class='recommend_work_item']/a[@title]"));
            Map<String,AttributeValue> BuyingUserViewItems = (Map<String,AttributeValue>) data.get("BuyingUserViewItems");
            logger.debug("getShopItemInfo show variable [BuyingUserViewItems=%s]",elements.toString());
            for(WebElement element:elements)
            {
                BuyingUserViewItems.put(element.getAttribute("href")
                    ,AttributeValue.builder().s(element.getAttribute("title")).build()
                );
            }
        }catch(TimeoutException ex){
            
        }

        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='main_inner']//div[@data-type='viewsales']//div[@class='recommend_work_item']/a[@title]"));
            Map<String,AttributeValue> LookingUserViewItems = (Map<String,AttributeValue>) data.get("LookingUserViewItems");
            logger.debug("getShopItemInfo show variable [LookingUserViewItems=%s]",elements.toString());
            for(WebElement element:elements)
            {   
                LookingUserViewItems.put(element.getAttribute("href")
                    ,AttributeValue.builder().s(element.getAttribute("title")).build()
                );
            }
        }catch(TimeoutException ex){
            
        }

        try
        {
            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='work_review']/table[@class='reviewer_most_genre']//td//a"));
            Map<String,AttributeValue> MostProperyGenru = (Map<String,AttributeValue>) data.get("MostProperyGenru");
            logger.debug("getShopItemInfo show variable [MostProperyGenru=%s]",elements.toString());
            for(WebElement element:elements)
            {   

                String text = element.getAttribute("textContent");

                MostProperyGenru.put(text.replaceAll("\\（.*$","")
                    ,AttributeValue.builder().n(text.replaceAll("[^0-9]","")).build()
                );
                
                
            }
        }catch(TimeoutException ex){
            
        }
        
        //　レビューおすすめ
        try
        {

            List<WebElement> elements = Driver.findElements(By.xpath("//*[@id='main_inner']//div[@class='matome_container']//*[@class='matome_content_title']/a"));
            Map<String,AttributeValue> RerationMatome = (Map<String,AttributeValue>) data.get("RerationMatome");
            logger.debug("getShopItemInfo show variable [RerationMatome=%s]",elements.toString());
            for(WebElement element:elements)
            {   
                RerationMatome.put(element.getAttribute("href")
                    ,AttributeValue.builder().s(element.getAttribute("textContent")).build()
                );
            }
        }catch(TimeoutException ex){
            
        }
        
        logger.info("getShopItemInfo return data=%s", data.toString());

        return data;
    }

    @SuppressWarnings("unchecked")
    protected Map<String,Object> getShopItemAffiriateInfo(Storage storage) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException
    {
        logger.info("getShopItemAffiriateInfo start");

        Map<String,Object> data = (new DLSiteModel()).createShopItemAffiliateInfoModel();
        // Map<String,Object> data = DLSiteModel.createShopItemAffiliateInfoModel();

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
        

        {
            String uri = Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_mini img"))).getAttribute("src");

            data.put("AffiliateSmallImageUrl"
                , uri
            );

            try
            {
                storage.transport(uri);
            }catch(Exception ignore)
            {
                ignore.printStackTrace();
            }
        }

        {
            String uri = Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_thum img"))).getAttribute("src");

            data.put("AffiliateMiddleImageUrl"
                , uri
            );

            try
            {
                storage.transport(uri);
            }catch(Exception ignore)
            {
                ignore.printStackTrace();
            }
        }

        {
            String uri = Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#preview_main img"))).getAttribute("src");

            data.put("AffiliateBigImageUrl"
                , uri
            );

            try
            {
                storage.transport(uri);
            }catch(Exception ignore)
            {
                ignore.printStackTrace();
            }
        }

        Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".main_modify_box button.copy_btn")))
            .click();

        Set<String> PlayerEmbed = (Set<String>) data.get("PlayerEmbed");
        PlayerEmbed.add(
            (String)clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor)
        );

        logger.info("getShopItemAffiriateInfo return data=%s", data.toString());

        return data;
    }

    public Map<String,Object> fetchScraping(String itemName,Storage storage) throws TimeoutException,InterruptedException,IOException,UnsupportedFlavorException,NotFoundException
    {
        logger.info("fetchScraping start");
        // check shopItemName is exist DLSite.
        searchBox(itemName);

        Map<String,Object> shopItemInfo = getShopItemInfo(itemName,storage);

        Map<String,Object> shopItemAffiriateInfo = getShopItemAffiriateInfo(storage);

        Map<String,Object> data = new HashMap<String,Object>(){{
            put("ShopItemId", shopItemInfo.get("ShopItemId"));
            put("Monopoly", shopItemInfo.get("Monopoly"));
            put("MakerName", shopItemInfo.get("MakerName"));
            put("MakerFollowerNum", shopItemInfo.get("MakerFollowerNum"));
            put("UnitsSold", shopItemInfo.get("UnitsSold"));
            put("SalePrice", shopItemInfo.get("SalePrice"));
            put("DiscountRate", shopItemInfo.get("DiscountRate"));
            put("ReleaseDate", shopItemInfo.get("ReleaseDate"));
            put("UntilHavingSale", shopItemInfo.get("UntilHavingSale"));
            put("NormalPrice", shopItemInfo.get("NormalPrice"));
            put("Assessment", shopItemInfo.get("Assessment"));
            put("AssessmentNum", shopItemInfo.get("AssessmentNum"));
            put("IlustratorName", shopItemInfo.get("IlustratorName"));
            put("ScreenWriter", shopItemInfo.get("ScreenWriter"));
            put("Musician", shopItemInfo.get("Musician"));
            put("MatomeNum", shopItemInfo.get("MatomeNum"));
            put("RerationMatome", shopItemInfo.get("RerationMatome"));
            put("ItemCategory", shopItemInfo.get("ItemCategory"));
            put("FileFormat", shopItemInfo.get("FileFormat"));
            put("FileSize", shopItemInfo.get("FileSize"));
            put("FileSizeUnit", shopItemInfo.get("FileSizeUnit"));
            put("AgeVeridation", shopItemInfo.get("AgeVeridation"));
            put("VoiceActor", shopItemInfo.get("VoiceActor"));
            put("StarNum", shopItemInfo.get("StarNum"));
            put("Genru", shopItemInfo.get("Genru"));
            put("MostProperyGenru", shopItemInfo.get("MostProperyGenru"));
            put("Gallery", shopItemInfo.get("Gallery"));
            put("BuyingUserViewItems", shopItemInfo.get("BuyingUserViewItems"));
            put("LookingUserViewItems", shopItemInfo.get("LookingUserViewItems"));
            put("Reviews", shopItemInfo.get("Reviews"));
            put("ReviewNum", shopItemInfo.get("ReviewNum"));
            put("AffiliateUrl", shopItemAffiriateInfo.get("AffiliateUrl"));
            put("AffiliateBigImageUrl", shopItemAffiriateInfo.get("AffiliateBigImageUrl"));
            put("AffiliateMiddleImageUrl", shopItemAffiriateInfo.get("AffiliateMiddleImageUrl"));
            put("AffiliateSmallImageUrl", shopItemAffiriateInfo.get("AffiliateSmallImageUrl"));
            put("PlayerEmbed", shopItemAffiriateInfo.get("PlayerEmbed"));
        }};

        logger.info("fetchScraping return data=%s", data.toString());

        return data;
    }
}