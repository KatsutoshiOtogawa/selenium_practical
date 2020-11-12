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
import java.util.Date;

import java.util.ArrayList;
import java.lang.Thread;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
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
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

/**
 * 
 */
public class DynamoDbDLSite extends DB<DynamoDbClient>
{

    public DynamoDbDLSite(Properties properties) throws IllegalArgumentException
    {   
        super(properties);
        logger = LogManager.getFormatterLogger(DynamoDbDLSite.class);
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";
        constructor();
    }

    @Override
    protected void constructor() throws IllegalArgumentException
    {
        logger.info("resource opening...");
        dbconnection = createDBConnection();
        model = new DLSiteModel<AttributeValue>();
        logger.info("resource opened");
    }

    @Override
    protected DynamoDbClient createDBConnection() throws IllegalArgumentException
    {
        logger.info("createDBConnection start");

        DynamoDbClient connection = null;

        try
        {
            if("AWS".equals(System.getenv("HOST")) || "AWS".equals(properties.getProperty("HOST")))
            {
                logger.info("createDBConnection is opening AWS...");
                connection = DynamoDbClient.create();
            }else if("LOCALHOST".equals(System.getenv("HOST")) || "LOCALHOST".equals(properties.getProperty("HOST"))){

                logger.info("createDBConnection is opening LOCALHOST...");
                connection = DynamoDbClient.builder()
                    .endpointOverride(URI.create(
                        String.format(
                            "http://%s:%s"
                            ,System.getenv("HOST") != null ? System.getenv("HOST") : properties.getProperty("HOST")
                            ,System.getenv("PORT") != null ? System.getenv("PORT") : properties.getProperty("PORT")
                        )
                    ))
                    //overrideConfiguration()
                    .region(Region.AP_NORTHEAST_1)
                    .build();
            }else{
                throw new IllegalArgumentException();
            }
        }catch(Exception ex){
            logger.info("createDBConnection is failed");
            throw ex;
        }

        logger.info("createDBConnection finish");

        return connection;
    }

    @Override
    public void destructor()
    {
        logger.info("resource closing...");
        dbconnection.close();
        logger.info("resource closed");
    }

    @SuppressWarnings("unchecked")
    protected PutItemRequest buildPutRequest()
    {
        logger.info("buildPutRequest start");
        Map<String,AttributeValue> items = new HashMap<String,AttributeValue>(){{
            put("ShopItemId"
                , StringUtils.isEmpty((String) model.Items.get("ShopItemId"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("ShopItemId")).build()
            );
            put("ShopName", AttributeValue.builder()
                .s((String)model.Items.get("ShopName"))
                .build()
            );
            put("ItemName", AttributeValue.builder()
                .s((String)model.Items.get("ItemName"))
                .build()
            );
            put("ShopItemName", AttributeValue.builder()
                .s((String)model.Items.get("ShopItemName"))
                .build()
            );
            put("MakerName"
                , StringUtils.isEmpty((String) model.Items.get("MakerName"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("MakerName")).build()
            );
            put("ReleaseDate"
                , StringUtils.isEmpty((String) model.Items.get("ReleaseDate"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("ReleaseDate")).build()
            );
            put("MakerFollowerNum"
                , StringUtils.isEmpty((String) model.Items.get("MakerFollowerNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("MakerFollowerNum")).build()
            );
            put("UnitsSold"
                , StringUtils.isEmpty((String) model.Items.get("UnitsSold"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("UnitsSold")).build()
            );
            put("SalePrice"
                , StringUtils.isEmpty((String) model.Items.get("SalePrice"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("SalePrice")).build()
            );
            put("DiscountRate"
                , StringUtils.isEmpty((String) model.Items.get("DiscountRate"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("DiscountRate")).build()
            );
            put("UntilHavingSale"
                , StringUtils.isEmpty((String) model.Items.get("UntilHavingSale"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("UntilHavingSale")).build()
            );
            put("NormalPrice"
                , StringUtils.isEmpty((String) model.Items.get("NormalPrice"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("NormalPrice")).build()
            );
            put("Assessment"
                , StringUtils.isEmpty((String) model.Items.get("Assessment"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("Assessment")).build()
            );
            put("AssessmentNum"
                , StringUtils.isEmpty((String) model.Items.get("AssessmentNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("AssessmentNum")).build()
            );
            put("IlustratorName"
                , ((Collection<String>) model.ListItems.get("IlustratorName")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("IlustratorName")).build()
            );
            put("ScreenWriter"
                , ((Collection<String>) model.ListItems.get("ScreenWriter")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("ScreenWriter")).build()
            );
            put("Musician"
                , ((Collection<String>) model.ListItems.get("Musician")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("Musician")).build()
            );
            put("MatomeNum"
                , StringUtils.isEmpty((String) model.Items.get("MatomeNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("MatomeNum")).build()
            );
            put("RerationMatome"
                , ((Map<String,AttributeValue>) model.DBDependenItems.get("RerationMatome")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().m((Map<String,AttributeValue>) model.DBDependenItems.get("RerationMatome")).build()
            );
            put("ItemCategory"
                , ((Collection<String>) model.ListItems.get("ItemCategory")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("ItemCategory")).build()
            );
            put("FileFormat"
                , ((Collection<String>) model.ListItems.get("FileFormat")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("FileFormat")).build()
            );
            put("FileSize"
                , StringUtils.isEmpty((String) model.Items.get("FileSize"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("FileSize")).build()
            );
            put("FileSizeUnit"
                , StringUtils.isEmpty((String) model.Items.get("FileSizeUnit"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("FileSizeUnit")).build()
            );
            put("AgeVeridation"
                , StringUtils.isEmpty((String) model.Items.get("AgeVeridation"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("AgeVeridation")).build()
            );

            put("VoiceActor"
                , ((Collection<String>) model.ListItems.get("VoiceActor")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("VoiceActor")).build()
            );
            put("StarNum"
                , StringUtils.isEmpty((String) model.Items.get("StarNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("StarNum")).build()
            );

            put("Genru"
                , ((Collection<String>) model.ListItems.get("Genru")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("Genru")).build()
            );

            put("MostProperyGenru"
                , ((Map<String,AttributeValue>) model.DBDependenItems.get("MostProperyGenru")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().m((Map<String,AttributeValue>) model.DBDependenItems.get("MostProperyGenru")).build()
            );
            put("BuyingUserViewItems"
                , ((Map<String,AttributeValue>) model.DBDependenItems.get("BuyingUserViewItems")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().m((Map<String,AttributeValue>) model.DBDependenItems.get("BuyingUserViewItems")).build()
            );

            put("LookingUserViewItems"
                , ((Map<String,AttributeValue>) model.DBDependenItems.get("LookingUserViewItems")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().m((Map<String,AttributeValue>) model.DBDependenItems.get("LookingUserViewItems")).build()
            );
            
            put("Reviews"
                , ((Collection<String>) model.ListItems.get("Reviews")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("Reviews")).build()
            );
            put("ReviewNum"
                , StringUtils.isEmpty((String) model.Items.get("ReviewNum"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().n((String) model.Items.get("ReviewNum")).build()
            );
            put("Monopoly"
                , model.BoolItems.get("Monopoly") == null 
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().bool((Boolean) model.BoolItems.get("Monopoly")).build()
            );
            put("AffiliateUrl"
                , StringUtils.isEmpty((String) model.Items.get("AffiliateUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("AffiliateUrl")).build()
            );

            put("AffiliateBigImageUrl"
                , StringUtils.isEmpty((String) model.Items.get("AffiliateBigImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("AffiliateBigImageUrl")).build()
            );
            put("AffiliateMiddleImageUrl"
                , StringUtils.isEmpty((String) model.Items.get("AffiliateMiddleImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("AffiliateMiddleImageUrl")).build()
            );
            put("AffiliateSmallImageUrl"
                , StringUtils.isEmpty((String) model.Items.get("AffiliateSmallImageUrl"))
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().s((String) model.Items.get("AffiliateSmallImageUrl")).build()
            );
            put("PlayerEmbed"
                , ((Collection<String>) model.ListItems.get("PlayerEmbed")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("PlayerEmbed")).build()
            );
            put("Gallery"
                , ((Collection<String>) model.ListItems.get("Gallery")).size() == 0
                    ? AttributeValue.builder().nul(true).build()
                    : AttributeValue.builder().ss((Collection<String>) model.ListItems.get("Gallery")).build()
            );
            put("CreatedAt", AttributeValue.builder()
                .s((String) model.Items.get("CreatedAt"))
                .build()
            );
        }};
        logger.info("buildPutRequest finish");

        return PutItemRequest.builder()
                .tableName(TableName)
                .item(items)
                .build();
    }

    public void putItem()
    {
        logger.info("putItem start");

        PutItemRequest putItemRequest = buildPutRequest();

        PutItemResponse response = dbconnection.putItem(putItemRequest);
        
        logger.info("putItem finish");
    }

    @Override
    public void upsertItem()
    {
        logger.info("upsertItem start");
        super.upsertItem();
        logger.info("upsertItem finish");
    }
    
    public void setupDB() throws TimeoutException,InterruptedException
    {

    }

}