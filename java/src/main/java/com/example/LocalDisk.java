
// package com.example;
// import java.util.Map;
// import java.util.Properties;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.net.URI;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
// import java.io.IOException;
// import java.io.UnsupportedEncodingException;
// import java.io.FileNotFoundException;
// import com.google.api.client.http.HttpRequestFactory;
// import com.google.api.client.http.javanet.NetHttpTransport;
// import java.util.Map;
// import java.util.HashMap;

// /**
//  * 
//  */
// public class LocalDisk extends Storage {

//     private static final Logger logger = LogManager.getFormatterLogger(LocalDisk.class);

//     public LocalDisk(Properties properties)
//     {
//         super(properties);
//         this.factory = new NetHttpTransport().createRequestFactory();
//         // Map<String,Object> data = constructor();
//         // this.dbconnection = constructor();
//     }

//     protected Map<String,Object> constructor() throws IllegalStateException
//     {
//         logger.info("resource opening...");

//         Map<String,Object> resources = new HashMap<String,Object>();

//         // USE_STORAGE
//         // USE_PATH

//         // resources.put("USE_STORAGE",);
//         // resources.put("USE_PATH",new DynamoDbDLSite(properties));

//         // ChromeDriver driver = null;
//         // 手動で開放しないといけないリソースはここに書く。
//         // try
//         // {
//         //     driver = new ChromeDriver();
//         // }catch(Exception ex)
//         // {
//         //     throw ex;
//         // }
//         // try
//         // {
//         //     resources.put("ScrapingDLSite",new ScrapingDLSite(properties));
//         // }catch(Exception ex){
//         //     ((DynamoDbDLSite)resources.get("DynamoDbDLSite")).destructor();
//         //     throw ex;
//         // }

//         logger.info("resource opened");

//         return driver;
//     }

//     // abstract protected Object constructor() throws IllegalArgumentException;
//     // abstract protected PutItemRequest buildPutRequest(Map<String,Object> data);
//     // abstract protected void putItem(Map<String,Object> data);
//     // abstract public void destructor();

//     @Override
//     public void download(URI uri) throws IOException
//     {
//         logger.info("download start");
//         super.download(uri);
//         logger.info("download finish");
//     }
// }