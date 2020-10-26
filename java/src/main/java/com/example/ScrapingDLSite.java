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
    //private DynamoDbClient DynamoDB;

    public ScrapingDLSite(Properties properties)
    {
        this.Driver = new ChromeDriver();
        
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.Wait = new WebDriverWait(Driver, 60);
        this.TransitionInterval = 5 * 1000;
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
        try
        {
            Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div > div.modal_close")))
            .click();
        }catch(TimeoutException ignored)
        {

        }
        Thread.sleep(TransitionInterval);
    }

    private void searchBox(String sentense) throws TimeoutException,InterruptedException,NotFoundException
    {
        // search for keyword using exact match. and go to page search result.

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
        }
        catch(TimeoutException ex)
        {
            // 例外キャストして例外できる?
            throw new NotFoundException();
        }

        Thread.sleep(TransitionInterval);
    }

    private HashMap<String,String> fetchSearchResult() throws TimeoutException,InterruptedException
    {
        HashMap<String,String> data = null;

        return data;
    }

    private HashMap<String,String> fetchSearchResultAffiriate() throws TimeoutException,InterruptedException
    {
        HashMap<String,String> data = null;

        // WebElement selectElement = driver.findElement(By.id("selectElementID"));
        // Select selectObject = new Select(selectElement);
        return data;
    }

    //public HashMap<String,String> fetchScraping(String artName) throws TimeoutException,InterruptedException,NotFoundException
    public void fetchScraping(String artName) throws TimeoutException,InterruptedException,NotFoundException
    {
        // check ArtName is exist DLSite.
        searchBox(artName);

        //HashMap<String,String> data 

        //return data;
    }


    // Thread.Sleep(TransitionInterval);
    // try 
    // {
    //     driver.get("https://google.com/ncr");
    //     driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
    //     WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3>div")));
    //     System.out.println(firstResult.getAttribute("textContent"));
    // } finally {
    //     driver.quit();
    // }
}