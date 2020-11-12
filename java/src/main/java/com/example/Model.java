
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

import java.util.Set;

/**
 * interfaceに変更。createAffiliateInfo createAffliateという風にする。
 * Model部分に何をダウンロードするかも決めるdowlonadFileという形で決める。
 */
public abstract class Model<T> {
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Map<String,String> Items;
    // 型名を入れるならcollectionItemsにすべき。
    protected Map<String,Set<String>> ListItems;
    protected Map<String,Boolean> BoolItems;
    /** DynamoDBのAttributesなどDB独自の実装 */
    protected Map<String,Map<String,T>> DBDependenItems;

    public Model()
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    abstract public void createShopItemInfoModel();
    abstract public void createShopItemAffiliateInfoModel();
    abstract public Map<String,String> createDowlonadFileModel();

    abstract public void clear();
    
}