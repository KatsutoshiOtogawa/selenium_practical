
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
/**
 * 
 */
public abstract class Storage {
    protected String CreatedAt;
    protected Properties properties;

    protected static HttpRequestFactory factory;

    public Storage(Properties properties)
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.properties = properties;
    }
    // abstract protected Object constructor() throws IllegalArgumentException;

    public void download(URI uri) throws IOException
    {
        String path = uri.getPath();
        String name = path.substring(path.lastIndexOf("/") + 1);

        HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));

        HttpResponse response = request.execute();

        try(FileOutputStream out = new FileOutputStream(name)) {

            response.download(out);
        }
    }

    // abstract public void destructor();
}