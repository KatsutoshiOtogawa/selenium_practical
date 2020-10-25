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
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
/**
 * Application Entory Point
 * 
 */
public class App 
{
    // public static DynamoDbClient CreateDynamodbConnection() throws IOException,IllegalArgumentException
    // {
    //     DynamoDbClient connection = null;

    //     Properties properties = new Properties();
    //     try 
    //     {
    //         // .env file is the same format with propertiy file.
    //         properties.load(new FileInputStream(String.join("/",System.getProperty("user.dir"),"resources",".env")));
    //     } catch (IOException e) {
    //         System.out.println(e.getMessage());
    //         e.printStackTrace();
    //         return;
    //     }

    //     if(String.IsNullOrEmpty(Environment.GetEnvironmentVariable("HOST"))){
    //         // connection = new DynamoDbClient();

    //         // connection = DynamoDbClient.builder()
    //         connection = DynamoDbClient.create();
    //     }else{
    //         AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
    //         ddbConfig.ServiceURL = String.Format(
    //             "http://{0}:{1}"
    //             ,Environment.GetEnvironmentVariable("HOST")
    //             ,Environment.GetEnvironmentVariable("PORT")
    //             );
    //         connection = new AmazonDynamoDBClient(ddbConfig);

    //         properties.getProperty("HOST")
    //         properties.getProperty("PORT")
    //         connection = DynamoDbClient.builder()
    //                     .region(Region.US_WEST_2)
    //                     .credentialsProvider(ProfileCredentialsProvider.builder()
    //                                  .profileName("myProfile")
    //                                  .build())
    //                     .build();
    //     }
    //     return connection;
    // }

    public static void main( String[] args )
    {

        // .env file is the same format with propertiy file.
        // properties.load(new FileInputStream(String.join("/",System.getProperty("user.dir"),"resources",".env")));
        // Properties properties = new Properties();
        // try 
        // {
        //     // .env file is the same format with propertiy file.
        //     properties.load(new FileInputStream(String.join("/",System.getProperty("user.dir"),"resources",".env")));
        // } catch (IOException e) {
        //     System.out.println(e.getMessage());
        //     e.printStackTrace();
        //     return;
        // }
        // System.out.println(properties.getProperty("DLSITE_AFFILIATE_SITE"));
        //System.getenv("PATH");

        FileInputStream fp = null;
        String resources = null;
        resources = String.join("/",System.getProperty("user.dir"),"resources",".env");
        try
        {
            fp = new FileInputStream(resources);
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return;
        }

        Properties properties = new Properties();

        try
        {
            properties.load(fp);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            properties = null;
        }finally{
            try
            {
                fp.close();
            }
            catch(IOException ex)
            {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

            if(properties == null)
            {
                return;
            }
        }

        ScrapingDLSite instance = new ScrapingDLSite(properties);

        try
        {
            instance.setupScraping();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            instance.destructor();
            return;
        }
        
        // return instance;

        List<String> lines = null;
        try 
        {
            lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"),"resources","ArtName.txt"), StandardCharsets.UTF_8);
        } catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            instance.destructor();
            return;
        }

        for (String line: lines)
        {
            if(StringUtils.startsWith(line,"#") || StringUtils.isEmpty(line))
            {

            }else{
                System.out.println(line);
                try 
                {
                    instance.fetchScraping(line);
                } catch (Exception ex) 
                {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    instance.destructor();
                    return;
                }
            }
        }

        // final operation.
        instance.destructor();

        // dynamodbのclose処理はこちらでやる。

    }
}
