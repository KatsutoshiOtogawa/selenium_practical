
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

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
public class AWSS3 extends Storage<S3Client> {

    private static HttpRequestFactory factory;

    public AWSS3(Properties properties) throws S3Exception
    {
        this.logger = LogManager.getFormatterLogger(AWSS3.class);
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        // this.factory = new NetHttpTransport().createRequestFactory();
        this.properties = properties;
        constructor();
    }

    protected void constructor() throws S3Exception
    {

        // switch(System.getenv("USE_STORAGE") != null ? System.getenv("USE_STORAGE") : properties.getProperty("USE_STORAGE"))
        // {
        //     case "S3":

        //         this.storageType = StorageType.S3;
        //         break;
        //     case "LOCAL_DISK":
        //         this.storageType = StorageType.LOCAL_DISK;
        //         break;
        //     case "GOOGLE_DRIVE":
        //         this.storageType = StorageType.GOOGLE_DRIVE;
        //         break;
        //     default:
        //         throw new StorageTypeNotFoundException();
        // }

        this.storageType = StorageType.S3;

        this.usePath = System.getenv("USE_PATH") != null ? System.getenv("USE_PATH") : properties.getProperty("USE_PATH");

        this.bucketname = (System.getenv("BUCKET_NAME") != null ? System.getenv("BUCKET_NAME") : properties.getProperty("BUCKET_NAME")
                            ).toLowerCase();

        storageClient = createStorageConnection();

        createRemoteStorage();
        
    }

    @Override
    protected S3Client createStorageConnection()
    {
        logger.info("createStorageConnection start");

        S3Client connection = S3Client.builder()
                    .region(Region.AP_NORTHEAST_1)
                    .build();

        logger.info("createStorageConnection finish");

        return connection;
    }

    @Override
    protected void download(String uri) throws IOException,InterruptedException
    {
        logger.info("download start");
        super.download(uri);
        logger.info("download finish");
    }

    @Override
    public void createShopItemIdPath()
    {
        logger.info("createShopItemIdPath start");
        super.createShopItemIdPath();
        logger.info("createShopItemIdPath finish");
    }

    @Override
    protected void createRemoteStorage() throws S3Exception
    {
        logger.info("createRemoteStorage start");

        logger.info("createRemoteStorage creating...");

        try 
        {
            CreateBucketRequest request = CreateBucketRequest.builder()
                                    .bucket(bucketname)
                                    .build();

            storageClient.createBucket(request);
        }catch(BucketAlreadyExistsException ignore){
            logger.info("createRemoteStorage bucket already exists.");

        }catch(BucketAlreadyOwnedByYouException ignore){
            logger.info("createRemoteStorage bucket already owned.");

        } catch (S3Exception ex) {
            logger.error("createRemoteStorage creating failed.");
            throw ex;
        }

        logger.info("createRemoteStorage finish");
    }

    @Override
    public void transport(String uri) throws IOException,InterruptedException,Exception
    {

        logger.info("transport start");
        super.transport(uri);
        logger.info("transport start");
    }

    @Override
    protected void upload(String uri) throws IOException,Exception
    {
        logger.info("upload start");
        
        logger.info("upload starting...");
        // createBucket code.

        String name = uri.substring(uri.lastIndexOf("/") + 1);
        
        File f = new File(Paths.get(usePath,ShopName,ShopItemId,name).toString());
                    
        try {

            logger.info("upload try create request");

            PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketname)
                        .key(Paths.get(ShopName,ShopItemId,name).toString())
                        .build();

            logger.info("upload try putobject");

            storageClient.putObject(request,
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
            ex.printStackTrace();
            throw ex;
        }
        // xfer_mgr.shutdownNow();
            // Print the names and IDs for up to 10 files.
            // FileList result = service.files().list()
            //         .setPageSize(10)
            //         .setFields("nextPageToken, files(id, name)")
            //         .execute();
            // List<File> files = result.getFiles();
            // if (files == null || files.isEmpty()) {
            //     System.out.println("No files found.");
            // } else {
            //     System.out.println("Files:");
            //     for (File file : files) {
            //         System.out.printf("%s (%s)\n", file.getName(), file.getId());
            //     }
            // }

        logger.info("upload finish");
    }

    public void destructor()
    {

    }
}