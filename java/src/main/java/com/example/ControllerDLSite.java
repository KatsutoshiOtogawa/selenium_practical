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
            logger.error("openProperties message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }catch(UnsupportedEncodingException ex)
        {
            logger.error("openProperties message [%s]",ex.getMessage());
            ex.printStackTrace();
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
                logger.error("openProperties message [%s]",ex.getMessage());
                ex.printStackTrace();
                throw ex2;
            }
            logger.error("openProperties message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        
        try
        {
            fp.close();
        }catch(IOException ex){
            logger.error("openProperties message [%s]",ex.getMessage());
            ex.printStackTrace();
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

    public void setupController() throws TimeoutException,InterruptedException
    {
        logger.info("setupController start");
        try
        {
            scrapingDLSite.setupScraping();
        }catch(TimeoutException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }catch(InterruptedException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }catch(Exception ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        logger.info("setupController finish");
    }

    public void action(String itemName) throws InterruptedException,IOException,UnsupportedFlavorException
    {

        logger.info("action start");
        HashMap<String,Object> data = null;
        try 
        {
            data = scrapingDLSite.fetchScraping(itemName);

        } catch(TimeoutException ex){
            logger.warn("main message [%s]",ex.getMessage());
            ex.printStackTrace();

        } catch(NotFoundException ex){
            logger.warn("main message [%s]",ex.getMessage());
            ex.printStackTrace();

        }catch (InterruptedException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }catch(UnsupportedFlavorException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }catch(IOException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        
        data.put("CreatedAt"
            ,(new SimpleDateFormat("yyyy-MM-dd")).format(new Date())
        );

        data.put("ItemName"
            ,itemName
        );

        data.put("ShopName"
            ,"DLSite"
        );
        data.put("ShopItemName"
            ,"DLSite" + itemName
        );

        logger.info("action variable {data=%s}",data.toString());

        dynamodbClient.putItem(data);
    }
}