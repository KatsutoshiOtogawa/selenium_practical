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

    private static final Logger logger = LogManager.getFormatterLogger(App.class);

    public static void main( String[] args )
    {

        InputStreamReader fp = null;
        String resources = null;
        resources = String.join("/",System.getProperty("user.dir"),"resources",".env");
        try
        {
            fp = new InputStreamReader(new FileInputStream(new File(resources)),"UTF-8");
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
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
        catch(TimeoutException ex)
        {

        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            instance.destructor();
            return;
        }

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

        for (String ArtName: lines)
        {
            if(StringUtils.startsWith(ArtName,"#") || StringUtils.isEmpty(ArtName))
            {

            }else{
                try 
                {
                    instance.fetchScraping(ArtName);

                } catch(TimeoutException ex){
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    continue;
                } catch(NotFoundException ex){
                    // 作成
                    continue;

                }catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    instance.destructor();
                    return;
                } catch (Exception ex){
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    instance.destructor();
                    return;
                }
                // スクレイピングのclassと分けた方がいい。dynamoDBの値を入れるクラスと。
            }
        }

        // final operation.
        instance.destructor();

        // dynamodbのclose処理はこちらでやる。
    }
}
