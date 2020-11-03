
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
import java.io.FileOutputStream;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.GenericUrl;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.lang.Thread;
/**
 * 
 */
public class Storage {
    protected String CreatedAt;
    protected Properties properties;
    // protected String downloadDestination;
    protected String usePath;
    protected static HttpRequestFactory factory;
    private static final Logger logger = LogManager.getFormatterLogger(Storage.class);
    protected StorageType storageType;

    public Storage(Properties properties) throws StorageTypeNotFoundException
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.factory = new NetHttpTransport().createRequestFactory();
        this.properties = properties;
        Map<String,Object> data = constructor();
        this.storageType = (StorageType) data.get("USE_STORAGE");
        this.usePath = (String) data.get("USE_PATH");
    }

    protected Map<String,Object> constructor() throws StorageTypeNotFoundException
    {

        Map<String,Object> data = new HashMap<String,Object>();

        switch(System.getenv("USE_STORAGE") != null ? System.getenv("USE_STORAGE") : properties.getProperty("USE_STORAGE"))
        {
            case "S3":
                data.put("USE_STORAGE",StorageType.S3);
                break;
            case "LOCAL_DISK":
                data.put("USE_STORAGE",StorageType.LOCAL_DISK);
                break;
            default:
                throw new StorageTypeNotFoundException();
        }

        data.put(
            "USE_PATH"
            ,System.getenv("USE_PATH") != null ? System.getenv("USE_PATH") : properties.getProperty("USE_PATH")
        );

        return data;
    }

    public void download(String uri) throws IOException,InterruptedException
    {
        logger.info("download start");
        String name = uri.substring(uri.lastIndexOf("/") + 1);

        HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));

        HttpResponse response = request.execute();

        // fileOutputStreamにダウンロード先のパスを追加。
        // try(FileOutputStream out = new FileOutputStream(name)) {
        // try(FileOutputStream out = new FileOutputStream(Paths.get(downloadDestination,name).toString())) {
        try(FileOutputStream out = new FileOutputStream(Paths.get(usePath,name).toString())) {
            
            response.download(out);
        }

        Thread.sleep(6);

        logger.info("download finish");
    }

    public void transport(String uri) throws IOException,InterruptedException
    {
        // data.get(model.);
        download(uri);

        if(StorageType.LOCAL_DISK != storageType)
        {
            upload(uri);
        }
        
    }

    public void upload(String uri)
    {
        logger.info("upload start");
    }

    public void destructor()
    {

    }
}