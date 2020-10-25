package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import java.util.Properties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;
//import java.io.FileInputStream;
import java.io.Reader;
import java.io.InputStream;
import java.text.SimpleDateFormat;


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
    //private TimeSpan TransitionInterval; Duration
    private WebDriverWait Wait;
    private String CreatedAt;
    private String TableName;
    private String ShopName;
    private Properties properties;
    //private DynamoDbClient DynamoDB;

    public ScrapingDLSite(Properties properties)
    {
        this.Driver = new ChromeDriver();
        
        this.CreatedAt = (new SimpleDateFormat("yyyy/MM/dd E HH:mm:ss")).format(new Date());
        this.Wait = new WebDriverWait(Driver, 60);
        //WebDriverWait wait = new WebDriverWait(driver, 60);
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";
        // this.DynamoDbClient = connection;

        this.properties = properties;
        // aws credential propertiesよりも環境変数を優先しているので
        // それと同じような作りにするとよいはず。
        // this.properties = new Properties();

        // this.properties.load(reader);

    }

    public void destructor()
    {
        Driver.close();
        Driver.quit();
    }

    public void setupScraping() throws Exception
    {
        userLogin();
    }

    private void userLogin() throws Exception
    {

    }

    public void fetchScraping(String line)
    {
        // throws 
    }

    // // String apppath;
    // // try
    // // {
    // //     apppath = (String)System.AppDomain.CurrentDomain.BaseDirectory;
    // // }
    // // catch(Exception ex)
    // // {
    // //     Console.WriteLine(ex.Message);
    // //     return;
    // // }

    // // Driver.Navigate().GoToUrl("https://www.dlsite.com/index.html");
    // // // Driver.WindowHandles.
    // // // self.driver.set_window_size(1440, 797)

    // // Wait.Until(driver =>driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Enabled);
    // // Driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Click();

    // // age validation
    // // this popup is shown when you visit first time.
    // try
    // {
    //     Wait.Until(driver =>driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Enabled);
    //     Driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Click();
    // }
    // catch(Exception ex)
    // {
    //     Console.WriteLine(ex.Message);
    // }

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