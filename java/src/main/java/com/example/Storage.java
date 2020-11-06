
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
import software.amazon.awssdk.services.s3.S3client;
import software.amazon.awssdk.services.s3.S3clientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.transfer.TransferClient;
import software.amazon.awssdk.services.transfer.TransferClientBuilder;
import software.amazon.awssdk.services.transfer.model.StartServerRequest;
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

    protected void createRemoteStorage() throws AmazonS3Exception
    {
        logger.info("createRemoteStorage start");

        if(StorageType.LOCAL_DISK != storageType)
        {
            logger.info("createRemoteStorage creating...");

            // Bucket name should not contain uppercase characters
            bucketname = ShopName.toLowerCase();
            // S3Client
            // storageClient = AmazonS3ClientBuilder.standard()
            //             .withRegion(Regions.AP_NORTHEAST_1)
            //             .build();
            storageClient = S3clientBuilder.builder()
                        .region(Region.AP_NORTHEAST_1)
                        .build();

                        
            // if (((AmazonS3)storageClient).doesBucketExistV2(bucketname)) {
            try 
            {
                // Bucket bucket = 
                // ((AmazonS3)storageClient).createBucket(bucketname);

                CreateBucketRequest request = CreateBucketRequest.builder()
                                        .bucket(bucketname)
                                        .build();

                ((S3Client)storageClient).createBucket(request);
            }catch(BucketAlreadyExistsException ignore){
                logger.info("createRemoteStorage bucket already exists.");
            } catch (S3Exception ex) {
                logger.error("createRemoteStorage creating failed.");
                throw ex;
            }
        }
        logger.info("createRemoteStorage finish");
    }

    // public static Bucket getBucket(String bucket_name) {
    //     final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    //     Bucket named_bucket = null;
    //     List<Bucket> buckets = s3.listBuckets();
    //     for (Bucket b : buckets) {
    //         if (b.getName().equals(bucket_name)) {
    //             named_bucket = b;
    //         }
    //     }
    //     return named_bucket;
    // }

    // public static Bucket createBucket(String bucket_name) {
    //     final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    //     Bucket b = null;
    //     if (s3.doesBucketExistV2(bucket_name)) {
    //         System.out.format("Bucket %s already exists.\n", bucket_name);
    //         b = getBucket(bucket_name);
    //     } else {
    //         try {
    //             b = s3.createBucket(bucket_name);
    //         } catch (AmazonS3Exception e) {
    //             System.err.println(e.getErrorMessage());
    //         }
    //     }
    //     return b;
    // }

    public void transport(String uri) throws IOException,InterruptedException
    {

        download(uri);

        if(StorageType.LOCAL_DISK != storageType)
        {
            upload(uri);
        }
        
    }

    
    protected void upload(String uri)
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
            // TransferManager xfer_mgr = TransferManagerBuilder.standard()
            //                         .withRegion(Regions.AP_NORTHEAST_1)
            //                         .build();
            // TransferClient xfer_mgr = TransferClient.builder()
            //             .region(Region.AP_NORTHEAST_1)
            //             .build();
                        // .startServer()
                                    // Put Object
                        
            try {
                // Upload xfer = xfer_mgr.upload(bucketname, Paths.get(ShopName,ShopItemId,name).toString(), f);

                // StartServerRequest request = StartServerRequest.builder()
                                        //    .
                
                // loop with Transfer.isDone()
                // XferMgrProgress.showTransferProgress(xfer);

                PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();
                storageClient.putObject(request,
                        RequestBody.fromByteBuffer(getRandomByteBuffer(10_000)));

                System.out.println(xfer.getDescription());
                // print an empty progress bar...
                printProgressBar(0.0);
                // update the progress bar while the xfer is ongoing.
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                    // Note: so_far and total aren't used, they're just for
                    // documentation purposes.
                    TransferProgress progress = xfer.getProgress();
                    long so_far = progress.getBytesTransferred();
                    long total = progress.getTotalBytesToTransfer();
                    double pct = progress.getPercentTransferred();
                    eraseProgressBar();
                    printProgressBar(pct);
                } while (xfer.isDone() == false);
                // print the final state of the transfer.
                TransferState xfer_state = xfer.getState();
                System.out.println(": " + xfer_state);

                //  or block with Transfer.waitForCompletion()
                // XferMgrProgress.waitForCompletion(xfer);

                try {
                    xfer.waitForCompletion();
                } catch (AmazonServiceException e) {
                    System.err.println("Amazon service error: " + e.getMessage());
                    System.exit(1);
                } catch (AmazonClientException e) {
                    System.err.println("Amazon client error: " + e.getMessage());
                    System.exit(1);
                } catch (InterruptedException e) {
                    System.err.println("Transfer interrupted: " + e.getMessage());
                    System.exit(1);
                }

            } catch (AmazonServiceException e) {
                
            }
            xfer_mgr.shutdownNow();


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