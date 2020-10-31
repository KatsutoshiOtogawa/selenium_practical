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
import java.util.HashMap;
import java.util.Collection;

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
import java.util.Map;
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
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

// AwsClientBuilder.EndpointConfiguration

// software.amazon.awssdk.services.apigateway.model;
/**
 * 
 */
public class DynamoDbDLSite extends Model
{
    private DynamoDbClient dynamodbClient;
    private static final Logger logger = LogManager.getFormatterLogger(DynamoDbDLSite.class);
    
    public DynamoDbDLSite(Properties properties) throws IllegalArgumentException
    {   
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";
        this.properties = properties;

        this.dynamodbClient = constructor();

    }

    protected DynamoDbClient constructor() throws IllegalArgumentException
    {
        // 手動で開放しないといけないリソースはここに書く。
        DynamoDbClient connection = null;

        if(StringUtils.isEmpty(
            System.getenv("HOST") != null ? System.getenv("HOST") : properties.getProperty("HOST")
        ))
        {
            connection = DynamoDbClient.create();
        }else{
            connection = DynamoDbClient.builder()
                .endpointOverride(URI.create(
                    String.format(
                        "http://%s:%s"
                        ,System.getenv("HOST") != null ? System.getenv("HOST") : properties.getProperty("HOST")
                        ,System.getenv("PORT") != null ? System.getenv("PORT") : properties.getProperty("PORT")
                    )
                ))
                .region(Region.AP_NORTHEAST_1)
                .build();

                // overrideConfiguration()
        }

        logger.info("resource open");

        return connection;
    }

    public void destructor()
    {
        dynamodbClient.close();
        logger.info("resource close");
    }

    protected PutItemRequest buildPutRequest(Map<String,Object> data)
    {
        logger.info("buildPutRequest start");
        Map<String,AttributeValue> items = new HashMap<String,AttributeValue>(){{
            put("ShopArtId"
                , StringUtils.isEmpty((String) data.get("ShopArtId"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("ShopArtId")).build()
            );
            put("ShopName", AttributeValue.builder()
                .s((String)data.get("ShopName"))
                .build()
            );
            put("ItemName", AttributeValue.builder()
                .s((String)data.get("ItemName"))
                .build()
            );
            put("ShopItemName", AttributeValue.builder()
                .s((String)data.get("ShopItemName"))
                .build()
            );
            put("MakerName"
                , StringUtils.isEmpty((String) data.get("MakerName"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("MakerName")).build()
            );
            put("ReleaseDate"
                , StringUtils.isEmpty((String) data.get("ReleaseDate"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("ReleaseDate")).build()
            );
            put("MakerFollowerNum"
                , StringUtils.isEmpty((String) data.get("MakerFollowerNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("MakerFollowerNum")).build()
            );
            put("UnitsSold"
                , StringUtils.isEmpty((String) data.get("UnitsSold"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("UnitsSold")).build()
            );
            put("SalePrice"
                , StringUtils.isEmpty((String) data.get("SalePrice"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("SalePrice")).build()
            );
            put("DiscountRate"
                , StringUtils.isEmpty((String) data.get("DiscountRate"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("DiscountRate")).build()
            );
            put("UntilHavingSale"
                , StringUtils.isEmpty((String) data.get("UntilHavingSale"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("UntilHavingSale")).build()
            );
            put("NormalPrice"
                , StringUtils.isEmpty((String) data.get("NormalPrice"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("NormalPrice")).build()
            );
            put("Assessment"
                , StringUtils.isEmpty((String) data.get("Assessment"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("Assessment")).build()
            );
            put("AssessmentNum"
                , StringUtils.isEmpty((String) data.get("AssessmentNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("AssessmentNum")).build()
            );
            put("IlustratorName"
                , ((Collection<String>) data.get("IlustratorName")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("IlustratorName")).build()
            );
            put("ScreenWriter"
                , ((Collection<String>) data.get("ScreenWriter")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("ScreenWriter")).build()
            );
            put("Musician"
                , ((Collection<String>) data.get("Musician")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("Musician")).build()
            );
            
            put("RerationMatome"
                , ((Collection<String>) data.get("RerationMatome")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("RerationMatome")).build()
            );
            put("ItemCategory"
                , ((Collection<String>) data.get("ItemCategory")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("ItemCategory")).build()
            );
            put("FileFormat"
                , ((Collection<String>) data.get("FileFormat")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("FileFormat")).build()
            );
            put("FileSize"
                , StringUtils.isEmpty((String) data.get("FileSize"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("FileSize")).build()
            );
            put("FileSizeUnit"
                , StringUtils.isEmpty((String) data.get("FileSizeUnit"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("FileSizeUnit")).build()
            );
            put("AgeVeridation"
                , StringUtils.isEmpty((String) data.get("AgeVeridation"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("AgeVeridation")).build()
            );

            put("VoiceActor"
                , ((Collection<String>) data.get("VoiceActor")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("VoiceActor")).build()
            );
            put("StarNum"
                , StringUtils.isEmpty((String) data.get("StarNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("StarNum")).build()
            );

            put("Genru"
                , ((Collection<String>) data.get("Genru")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("Genru")).build()
            );

            put("BuyingUserViewItems"
                , ((Collection<String>) data.get("BuyingUserViewItems")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("BuyingUserViewItems")).build()
            );
            put("LookingUserViewItems"
                , ((Collection<String>) data.get("LookingUserViewItems")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("LookingUserViewItems")).build()
            );

            put("reviews"
                , ((Collection<String>) data.get("reviews")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("reviews")).build()
            );
            put("ReviewNum"
                , StringUtils.isEmpty((String) data.get("ReviewNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) data.get("ReviewNum")).build()
            );
            put("Monopoly"
                , data.get("Monopoly") == null 
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().bool((Boolean) data.get("Monopoly")).build()
            );
            put("AffiliateUrl"
                , StringUtils.isEmpty((String) data.get("AffiliateUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("AffiliateUrl")).build()
            );

            put("AffiliateBigImageUrl"
                , StringUtils.isEmpty((String) data.get("AffiliateBigImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("AffiliateBigImageUrl")).build()
            );
            put("AffiliateMiddleImageUrl"
                , StringUtils.isEmpty((String) data.get("AffiliateMiddleImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("AffiliateMiddleImageUrl")).build()
            );
            put("AffiliateSmallImageUrl"
                , StringUtils.isEmpty((String) data.get("AffiliateSmallImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) data.get("AffiliateSmallImageUrl")).build()
            );
            put("PlayerEmbed"
                , ((Collection<String>) data.get("PlayerEmbed")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("PlayerEmbed")).build()
            );
            put("Gallery"
                , ((Collection<String>) data.get("Gallery")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) data.get("Gallery")).build()
            );
            put("CreatedAt", AttributeValue.builder()
                .s((String) data.get("CreatedAt"))
                .build()
            );
        }};
        logger.info("buildPutRequest finish");

        return PutItemRequest.builder()
                .tableName(TableName)
                .item(items)
                .build();
    }
    // public void putItem(PutItemRequest request)
    public void putItem(Map<String,Object> data)
    {
        logger.info("putItem start");

        PutItemRequest putItemRequest = buildPutRequest(data);

        PutItemResponse response = dynamodbClient.putItem(putItemRequest);

        logger.info("putItem finish");
    }

    public void setupDB() throws TimeoutException,InterruptedException
    {

    }

}