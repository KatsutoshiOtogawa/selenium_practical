
package com.example;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openqa.selenium.TimeoutException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.ArrayList;
import java.util.List;
/**
 * interfaceに変更。createAffiliateInfo createAffliateという風にする。
 * Model部分に何をダウンロードするかも決めるdowlonadFileという形で決める。
 */
public abstract class Model {
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;

    public Model()
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    abstract public Map<String,Object> createShopItemInfoModel();
    abstract public Map<String,Object> createShopItemAffiliateInfoModel();
    abstract public List<String> createDowlonadFileModel();
    // abstract protected Object constructor() throws IllegalArgumentException;
    // abstract protected PutItemRequest buildPutRequest(Map<String,Object> data);
    // abstract protected void putItem(Map<String,Object> data);
    // abstract public void destructor();
}