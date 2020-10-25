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
import java.io.FileInputStream;
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
    //private AmazonDynamoDBClient DynamoDB;

    public ScrapingDLSite(DynamoDbClient connection)
    {
        this.Driver = new ChromeDriver();
        
        this.CreatedAt = (new SimpleDateFormat("yyyy/MM/dd E HH:mm:ss")).format(new Date());
        this.Wait = new WebDriverWait(Driver, 60);
        //WebDriverWait wait = new WebDriverWait(driver, 60);
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";
        //this.DynamoDB = connection;
        
    }
}