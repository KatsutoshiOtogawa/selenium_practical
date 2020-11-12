
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * T: DBclientconnection
 * U: Model
 */
public abstract class DB<T> {
    protected String CreatedAt;
    protected String TableName;
    protected String ShopName;
    protected Properties properties;
    protected T dbconnection;
    protected Model model;
    protected DBType dbtype;
    protected Logger logger;

    public DB(Properties properties)
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.properties = properties;
    }

    /** Map<String,Objectをモデルでかけるようにすべき。 */
    abstract protected void constructor() throws IllegalArgumentException;
    abstract protected T createDBConnection() throws IllegalArgumentException;
    abstract protected PutItemRequest buildPutRequest();

    abstract protected void putItem();
    abstract public void destructor();

    public Model getModel()
    {
        return model;
    }

    // public void upsertItem(Map<String,Object> data)
    // {
    //     // dbtype == DBType.DYNAMODB;
    //     putItem(data);

    //     // dbtype == DBType.RDBMS;
    //     // 
    // }

    public void upsertItem()
    {
        // dbtype == DBType.DYNAMODB;
        // putItem(data);
        putItem();
        // dbtype == DBType.RDBMS;
        // 
    }
}