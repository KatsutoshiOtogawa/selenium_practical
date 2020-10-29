package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import java.util.Properties;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.openqa.selenium.TimeoutException;
import java.io.File;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * Application Entory Point
 * 
 */
public class App 
{
    
    private static final Logger logger = LogManager.getFormatterLogger(App.class);

    public static void main( String[] args )
    {

        logger.info("main start");

        InputStreamReader fp = null;
        String resources = null;
        resources = String.join("/",System.getProperty("user.dir"),"resources",".env");
        try
        {
            fp = new InputStreamReader(new FileInputStream(new File(resources)),"UTF-8");
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            return;
        }

        Properties properties = new Properties();

        try
        {
            properties.load(fp);
        } catch (IOException ex) {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            properties = null;
        }finally{
            try
            {
                fp.close();
            }
            catch(IOException ex)
            {
                logger.error("main message [%s]",ex.getMessage());
                ex.printStackTrace();
            }

            if(properties == null)
            {
                return;
            }
        }
        
        DynamoDbDLSite dbconnection = null;

        try
        {
            dbconnection = new DynamoDbDLSite(properties);
        }catch(IllegalArgumentException ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            return;
        }

        ScrapingDLSite instance = new ScrapingDLSite(properties);

        try
        {
            instance.setupScraping();
        }catch(TimeoutException ex)    
        {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            dbconnection.destructor();
            instance.destructor();
            return;
        }catch(Exception ex)
        {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            dbconnection.destructor();
            instance.destructor();
            return;
        }

        List<String> lines = null;
        try 
        {
            lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"),"resources","ArtName.txt"), StandardCharsets.UTF_8);
        } catch (IOException ex) 
        {
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            dbconnection.destructor();
            instance.destructor();
            return;
        }

        for (String itemName: lines)
        {

            HashMap<String,Object> data = null;

            if(StringUtils.startsWith(itemName,"#") || StringUtils.isEmpty(itemName))
            {

            }else{
                try 
                {
                   data = instance.fetchScraping(itemName);

                } catch(TimeoutException ex){
                    logger.warn("main message [%s]",ex.getMessage());
                    ex.printStackTrace();

                } catch(NotFoundException ex){
                    logger.warn("main message [%s]",ex.getMessage());
                    ex.printStackTrace();

                }catch (InterruptedException ex){
                    logger.error("main message [%s]",ex.getMessage());
                    ex.printStackTrace();
                    dbconnection.destructor();
                    instance.destructor();
                    return;
                } catch (Exception ex){
                    logger.error("main message [%s]",ex.getMessage());
                    ex.printStackTrace();
                    dbconnection.destructor();
                    instance.destructor();
                    return;
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

                logger.info("main variable {data=%s}",data.toString());

                dbconnection.putItem(data);

            }
        }

        // final operation.
        dbconnection.destructor();
        instance.destructor();

        logger.info("main finish");
        // dynamodbのclose処理はこちらでやる。
    }
}
