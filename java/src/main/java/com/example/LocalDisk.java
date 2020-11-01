
package com.example;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * 
 */
public class LocalDisk extends Storage {

    private static final Logger logger = LogManager.getFormatterLogger(LocalDisk.class);

    public LocalDisk(Properties properties)
    {
        super(properties);
        this.factory = new NetHttpTransport().createRequestFactory();
    }

    // abstract protected Object constructor() throws IllegalArgumentException;
    // abstract protected PutItemRequest buildPutRequest(Map<String,Object> data);
    // abstract protected void putItem(Map<String,Object> data);
    // abstract public void destructor();

    @Override
    public void download(URI uri) throws IOException
    {
        logger.info("download start");
        super.download(uri);
        logger.info("download finish");
    }
}