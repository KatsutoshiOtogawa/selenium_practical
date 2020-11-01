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

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
public class ControllerDLSite extends Controller
{
    private DynamoDbDLSite dynamodbClient;
    private ScrapingDLSite scrapingDLSite;
    private static final Logger logger = LogManager.getFormatterLogger(ControllerDLSite.class);
    
    public ControllerDLSite(String path) throws IllegalArgumentException,IllegalStateException,FileNotFoundException,IOException
    {
        
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());

        Map<String,Object> data = constructor(path);
        this.dynamodbClient = (DynamoDbDLSite) data.get("DynamoDbDLSite");
        this.scrapingDLSite = (ScrapingDLSite) data.get("ScrapingDLSite");

        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";

        // aws credential propertiesよりも環境変数を優先しているので
        // それと同じような作りにするとよいはず。

    }

    protected Properties openProperties(String path) throws FileNotFoundException,IOException,UnsupportedEncodingException
    {

        logger.info("openProperties start");
        InputStreamReader fp = null;
        
        try
        {
            fp = new InputStreamReader(new FileInputStream(new File(path)),"UTF-8");
        }catch(FileNotFoundException ex)
        {
            logger.error("openProperties message [%s]",ex.getMessage());
            throw ex;
        }catch(UnsupportedEncodingException ex)
        {
            logger.error("openProperties message [%s]",ex.getMessage());
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
                throw ex2;
            }
            logger.error("openProperties message [%s]",ex.getMessage());
            throw ex;
        }
        
        try
        {
            fp.close();
        }catch(IOException ex){
            logger.error("openProperties message [%s]",ex.getMessage());
            throw ex;
        }

        logger.info("openProperties finish");
        return properties;
    }

    protected Map<String,Object> constructor(String path) throws IllegalArgumentException,FileNotFoundException,IOException,UnsupportedEncodingException,IllegalStateException
    {
        logger.info("resource opening...");
        Properties properties = openProperties(path);

        Map<String,Object> resources = new HashMap<String,Object>();

        resources.put("DynamoDbDLSite",new DynamoDbDLSite(properties));

        try
        {
            resources.put("ScrapingDLSite",new ScrapingDLSite(properties));
        }catch(Exception ex){
            ((DynamoDbDLSite)resources.get("DynamoDbDLSite")).destructor();
            throw ex;
        }

        logger.info("resource opend");

        return resources;

        // return new HashMap<String,Object>(){{
        //     put("DynamoDbDLSite",new DynamoDbDLSite(properties));
        //     put("ScrapingDLSite",new ScrapingDLSite(properties));
        // }};
    }

    public void destructor()
    {
        logger.info("resource closing...");
        dynamodbClient.destructor();
        scrapingDLSite.destructor();
        logger.info("resource closed");
    }

    public void setupController() throws TimeoutException,InterruptedException
    {
        logger.info("setupController start");
        try
        {
            scrapingDLSite.setupScraping();
        }catch(TimeoutException ex){
            logger.error("main message [%s]",ex.getMessage());
            throw ex;
        }catch(InterruptedException ex){
            logger.error("main message [%s]",ex.getMessage());
            throw ex;
        }catch(Exception ex){
            logger.error("main message [%s]",ex.getMessage());
            throw ex;
        }
        logger.info("setupController finish");
    }

    public void action(String itemName) throws InterruptedException,IOException,UnsupportedFlavorException
    {

        logger.info("action start");
        Map<String,Object> data = null;
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
            ,CreatedAt
        );

        data.put("ItemName"
            ,itemName
        );

        data.put("ShopName"
            ,ShopName
        );
        data.put("ShopItemName"
            ,ShopName + itemName
        );

        logger.info("action variable {data=%s}",data.toString());

        dynamodbClient.putItem(data);
        
        // try {
        //     URL url = new URL("http://localhost/image1.jpg");
        //     HttpURLConnection conn =
        //         (HttpURLConnection) url.openConnection();
        //     conn.setAllowUserInteraction(false);
        //     conn.setInstanceFollowRedirects(true);
        //     conn.setRequestMethod("GET");
        //     conn.connect();
      
        //     int httpStatusCode = conn.getResponseCode();
        //     if (httpStatusCode != HttpURLConnection.HTTP_OK) {
        //       throw new Exception("HTTP Status " + httpStatusCode);
        //     }
      
        //     String contentType = conn.getContentType();
        //     System.out.println("Content-Type: " + contentType);
      
        //     // Input Stream
        //     DataInputStream dataInStream
        //         = new DataInputStream(
        //         conn.getInputStream());
      
        //     // Output Stream
        //     DataOutputStream dataOutStream
        //         = new DataOutputStream(
        //         new BufferedOutputStream(
        //             new FileOutputStream("/Users/keisukeo/image1.jpg")));
      
        //     // Read Data
        //     byte[] b = new byte[4096];
        //     int readByte = 0;
      
        //     while (-1 != (readByte = dataInStream.read(b))) {
        //       dataOutStream.write(b, 0, readByte);
        //     }
      
        //     // Close Stream
        //     dataInStream.close();
        //     dataOutStream.close();
      
        //   } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        //   } catch (ProtocolException e) {
        //     e.printStackTrace();
        //   } catch (MalformedURLException e) {
        //     e.printStackTrace();
        //   } catch (IOException e) {
        //     e.printStackTrace();
        //   } catch (Exception e) {
        //     System.out.println(e.getMessage());
        //     e.printStackTrace();
        //   }
    }
}