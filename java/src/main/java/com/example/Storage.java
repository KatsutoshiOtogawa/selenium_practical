
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
import java.io.File;

// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// import com.amazonaws.services.s3.model.AmazonS3Exception;
// import com.amazonaws.services.s3.model.Bucket;
// import com.amazonaws.AmazonServiceException;
// import com.amazonaws.services.s3.transfer.MultipleFileUpload;
// import com.amazonaws.services.s3.transfer.TransferProgress;
// import com.amazonaws.services.s3.transfer.TransferManager;
// import com.amazonaws.services.s3.transfer.Transfer.TransferState;
// import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
// import com.amazonaws.services.s3.transfer.Upload;
// import com.amazonaws.AmazonClientException;
// import com.amazonaws.regions.Regions;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;

/**
 * 
 */
public class Storage {
    public String ShopItemId;
    public String ShopName;
    protected String CreatedAt;
    protected Properties properties;
    protected String usePath;
    protected static HttpRequestFactory factory;
    protected String bucketname;
    protected Object storageClient;
    private static final Logger logger = LogManager.getFormatterLogger(Storage.class);
    protected StorageType storageType;

    public Storage(Properties properties) throws StorageTypeNotFoundException,S3Exception
    {
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.factory = new NetHttpTransport().createRequestFactory();
        this.properties = properties;
        constructor();
    }

    protected void constructor() throws StorageTypeNotFoundException,S3Exception
    {

        switch(System.getenv("USE_STORAGE") != null ? System.getenv("USE_STORAGE") : properties.getProperty("USE_STORAGE"))
        {
            case "S3":

                this.storageType = StorageType.S3;
                break;
            case "LOCAL_DISK":
                this.storageType = StorageType.LOCAL_DISK;
                break;
            default:
                throw new StorageTypeNotFoundException();
        }


        this.usePath = System.getenv("USE_PATH") != null ? System.getenv("USE_PATH") : properties.getProperty("USE_PATH");

        this.bucketname = (System.getenv("BUCKET_NAME") != null ? System.getenv("BUCKET_NAME") : properties.getProperty("BUCKET_NAME")
                            ).toLowerCase();

        createRemoteStorage();
        
    }

    protected void download(String uri) throws IOException,InterruptedException
    {
        logger.info("download start");
        String name = uri.substring(uri.lastIndexOf("/") + 1);

        HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));

        HttpResponse response = request.execute();

        try(FileOutputStream out = new FileOutputStream(Paths.get(usePath,ShopName,ShopItemId,name).toString())) {
            
            response.download(out);
        }

        Thread.sleep(6);

        logger.info("download finish");
    }

    public void createShopItemIdPath()
    {
        logger.info("createShopItemIdPath start");
        File file = new File(Paths.get(usePath,ShopName,ShopItemId).toString());
        file.mkdirs();

        logger.info("createShopItemIdPath finish");
    }

    protected void createRemoteStorage() throws S3Exception
    {
        logger.info("createRemoteStorage start");

        if(StorageType.LOCAL_DISK != storageType)
        {
            logger.info("createRemoteStorage creating...");

            storageClient = S3Client.builder()
                        .region(Region.AP_NORTHEAST_1)
                        .build();

            try 
            {
                CreateBucketRequest request = CreateBucketRequest.builder()
                                        .bucket(bucketname)
                                        .build();

                ((S3Client)storageClient).createBucket(request);
            }catch(BucketAlreadyExistsException ignore){
                logger.info("createRemoteStorage bucket already exists.");

            }catch(BucketAlreadyOwnedByYouException ignore){
                logger.info("createRemoteStorage bucket already owned.");

            } catch (S3Exception ex) {
                logger.error("createRemoteStorage creating failed.");
                throw ex;
            }
        }
        logger.info("createRemoteStorage finish");
    }

    public void transport(String uri) throws IOException,InterruptedException,S3Exception
    {

        download(uri);

        if(StorageType.LOCAL_DISK != storageType)
        {
            upload(uri);
        }
        
    }

    
    protected void upload(String uri) throws S3Exception
    {
        logger.info("upload start");
        
        // S3
        if(StorageType.S3 == storageType)
        {
            logger.info("upload starting...");
            // createBucket code.
            
            /** アップロード処理は別のプロセスとして起動。*/

            String name = uri.substring(uri.lastIndexOf("/") + 1);
            
            File f = new File(Paths.get(usePath,ShopName,ShopItemId,name).toString());
                        
            try {

                logger.info("upload try create request");

                PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucketname)
                            .key(Paths.get(ShopName,ShopItemId,name).toString())
                            .build();

                logger.info("upload try putobject");

                ((S3Client)storageClient).putObject(request,
                        RequestBody.fromFile(f)
                        );

                // System.out.println(xfer.getDescription());
                // print an empty progress bar...
                // printProgressBar(0.0);
                // // update the progress bar while the xfer is ongoing.
                // do {
                //     try {
                //         Thread.sleep(100);
                //     } catch (InterruptedException e) {
                //         return;
                //     }
                //     // Note: so_far and total aren't used, they're just for
                //     // documentation purposes.
                //     TransferProgress progress = xfer.getProgress();
                //     long so_far = progress.getBytesTransferred();
                //     long total = progress.getTotalBytesToTransfer();
                //     double pct = progress.getPercentTransferred();
                //     eraseProgressBar();
                //     printProgressBar(pct);
                // } while (xfer.isDone() == false);
                // // print the final state of the transfer.
                // TransferState xfer_state = xfer.getState();
                // System.out.println(": " + xfer_state);

                // //  or block with Transfer.waitForCompletion()
                // // XferMgrProgress.waitForCompletion(xfer);

                // try {
                //     xfer.waitForCompletion();
                // } catch (AmazonServiceException e) {
                //     System.err.println("Amazon service error: " + e.getMessage());
                //     System.exit(1);
                // } catch (AmazonClientException e) {
                //     System.err.println("Amazon client error: " + e.getMessage());
                //     System.exit(1);
                // } catch (InterruptedException e) {
                //     System.err.println("Transfer interrupted: " + e.getMessage());
                //     System.exit(1);
                // }

            } catch (S3Exception ex) {
                throw ex;
            }
            // xfer_mgr.shutdownNow();


        }
        logger.info("upload finish");
    }

    public static void printProgressBar(double pct) {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
                empty_bar.substring(0, bar_size - amt_full));
    }

    public static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }

    public void destructor()
    {

    }
}