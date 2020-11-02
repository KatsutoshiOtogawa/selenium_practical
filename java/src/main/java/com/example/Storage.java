
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
/**
 * s3,localdisk,googledriveなどのenumを持たせて切り替えの方がよいか。
 */
// public abstract class Storage {
public class Storage {
    protected String CreatedAt;
    protected Properties properties;
    protected String downloadDestination;
    protected String usePath;
    protected static HttpRequestFactory factory;
    
    protected StorageType storageType;
    // enum StorageType {
    //     S3
    //     ,LOCAL_DISK
    // }
    public Storage(Properties properties) throws StorageTypeNotFoundException
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.factory = new NetHttpTransport().createRequestFactory();
        this.properties = properties;
        Map<String,Object> data = constructor();
        this.storageType = (StorageType) data.get("USE_STORAGE");
        this.usePath = (String) data.get("USE_PATH");
    }
    // abstract protected Object constructor() throws IllegalArgumentException;

    // for (StorageType m : StorageType.values()) {
    //     System.out.println(m.toString());
    //  StorageType.S3.toString();
    //  StorageType.LOCAL_DISK.toString();
    // }

    protected Map<String,Object> constructor() throws StorageTypeNotFoundException
    {

        Map<String,Object> data = null;

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
    
    public void download(URI uri) throws IOException
    {
        String path = uri.getPath();
        String name = path.substring(path.lastIndexOf("/") + 1);

        HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));

        HttpResponse response = request.execute();

        // fileOutputStreamにダウンロード先のパスを追加。
        // try(FileOutputStream out = new FileOutputStream(name)) {
        try(FileOutputStream out = new FileOutputStream(Paths.get(downloadDestination,name).toString())) {
            response.download(out);
        }
    }

    public void destructor()
    {

    }
}