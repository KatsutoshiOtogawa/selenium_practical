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

        ControllerDLSite controller = null;
        try
        {
            controller = new ControllerDLSite(String.join("/",System.getProperty("user.dir"),"resources",".env"));
        }catch(Exception ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
            return;
        }

        try
        {
            controller.setupController();
        }catch(Exception ex){
            logger.error("main message [%s]",ex.getMessage());
            ex.printStackTrace();
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
            controller.destructor();
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
                    controller.action(itemName);
                }catch(Exception ex){
                    logger.error("main message [%s]",ex.getMessage());
                    ex.printStackTrace();
                    controller.destructor();
                    return;
                }

            }
        }

        // final operation.
        controller.destructor();
        logger.info("main finish");

    }
}
