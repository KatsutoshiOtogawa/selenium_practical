
package com.example;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openqa.selenium.TimeoutException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
/**
 * 
 */
public abstract class Storage {
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Properties properties;

    abstract protected Object constructor() throws IllegalArgumentException;
    abstract protected PutItemRequest buildPutRequest(Map<String,Object> data);
    abstract protected void putItem(Map<String,Object> data);
    abstract public void destructor();
}