
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
public abstract class Storage<T> implements StorageInterface{
    protected String ShopItemId;
    protected String ShopName;
    protected String CreatedAt;
    protected Properties properties;
    protected String usePath;
    private static HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();;
    protected String bucketname;
    protected T storageClient;
    protected Logger logger;
    protected StorageType storageType;

    // public Storage(Properties properties) throws StorageTypeNotFoundException,S3Exception
    // {
    //     this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    //     this.factory = new NetHttpTransport().createRequestFactory();
    //     this.properties = properties;
    //     constructor();
    // }

    // protected void constructor() throws StorageTypeNotFoundException,S3Exception
    // {

    //     // 与えられた値によって返すインスタンスを変える?
    //     switch(System.getenv("USE_STORAGE") != null ? System.getenv("USE_STORAGE") : properties.getProperty("USE_STORAGE"))
    //     {
    //         case "S3":

    //             this.storageType = StorageType.S3;
    //             break;
    //         case "LOCAL_DISK":
    //             this.storageType = StorageType.LOCAL_DISK;
    //             break;
    //         case "GOOGLE_DRIVE":
    //             this.storageType = StorageType.GOOGLE_DRIVE;
    //             break;
    //         default:
    //             throw new StorageTypeNotFoundException();
    //     }


    //     this.usePath = System.getenv("USE_PATH") != null ? System.getenv("USE_PATH") : properties.getProperty("USE_PATH");

    //     this.bucketname = (System.getenv("BUCKET_NAME") != null ? System.getenv("BUCKET_NAME") : properties.getProperty("BUCKET_NAME")
    //                         ).toLowerCase();

    //     createRemoteStorage();
        
    // }

    protected abstract T createStorageConnection() throws Exception;

    protected void download(String uri) throws IOException,InterruptedException
    {
        String name = uri.substring(uri.lastIndexOf("/") + 1);

        HttpRequest request = factory.buildGetRequest(new GenericUrl(uri));

        HttpResponse response = request.execute();

        try(FileOutputStream out = new FileOutputStream(Paths.get(usePath,ShopName,ShopItemId,name).toString())) {
            
            response.download(out);
        }

        Thread.sleep(6);

    }

    public void createShopItemIdPath()
    {
        File file = new File(Paths.get(usePath,ShopName,ShopItemId).toString());
        file.mkdirs();
    }

    protected abstract void createRemoteStorage();

    public void transport(String uri) throws IOException,InterruptedException,Exception
    {
 // , GeneralSecurityException
        download(uri);
        upload(uri);
        // if(StorageType.LOCAL_DISK != storageType)
        // {
        //     upload(uri);
        // }
        
    }

    protected abstract void upload(String uri) throws IOException,Exception;
    
    // protected void upload(String uri) throws S3Exception,GeneralSecurityException,IOException
    // {
    //     logger.info("upload start");
        
    //     // S3
    //     if(StorageType.S3 == storageType)
    //     {
    //         logger.info("upload starting...");
    //         // createBucket code.

    //         String name = uri.substring(uri.lastIndexOf("/") + 1);
            
    //         File f = new File(Paths.get(usePath,ShopName,ShopItemId,name).toString());
                        
    //         try {

    //             logger.info("upload try create request");

    //             PutObjectRequest request = PutObjectRequest.builder()
    //                         .bucket(bucketname)
    //                         .key(Paths.get(ShopName,ShopItemId,name).toString())
    //                         .build();

    //             logger.info("upload try putobject");

    //             ((S3Client)storageClient).putObject(request,
    //                     RequestBody.fromFile(f)
    //                     );

    //             // System.out.println(xfer.getDescription());
    //             // print an empty progress bar...
    //             // printProgressBar(0.0);
    //             // // update the progress bar while the xfer is ongoing.
    //             // do {
    //             //     try {
    //             //         Thread.sleep(100);
    //             //     } catch (InterruptedException e) {
    //             //         return;
    //             //     }
    //             //     // Note: so_far and total aren't used, they're just for
    //             //     // documentation purposes.
    //             //     TransferProgress progress = xfer.getProgress();
    //             //     long so_far = progress.getBytesTransferred();
    //             //     long total = progress.getTotalBytesToTransfer();
    //             //     double pct = progress.getPercentTransferred();
    //             //     eraseProgressBar();
    //             //     printProgressBar(pct);
    //             // } while (xfer.isDone() == false);
    //             // // print the final state of the transfer.
    //             // TransferState xfer_state = xfer.getState();
    //             // System.out.println(": " + xfer_state);

    //             // //  or block with Transfer.waitForCompletion()
    //             // // XferMgrProgress.waitForCompletion(xfer);

    //             // try {
    //             //     xfer.waitForCompletion();
    //             // } catch (AmazonServiceException e) {
    //             //     System.err.println("Amazon service error: " + e.getMessage());
    //             //     System.exit(1);
    //             // } catch (AmazonClientException e) {
    //             //     System.err.println("Amazon client error: " + e.getMessage());
    //             //     System.exit(1);
    //             // } catch (InterruptedException e) {
    //             //     System.err.println("Transfer interrupted: " + e.getMessage());
    //             //     System.exit(1);
    //             // }

    //         } catch (S3Exception ex) {
    //             throw ex;
    //         }
    //         // xfer_mgr.shutdownNow();


    //     }

    //     // GOOGLE_DRIVE
    //     if(StorageType.GOOGLE_DRIVE == storageType)
    //     {

    //         logger.info("upload try create request");
    //         Drive service = null;
    //         try
    //         {
    //             // Build a new authorized API client service.
    //         // あとでconstructorに書く。S3Clientに相当するため。
    //         final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    //         service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    //                 .setApplicationName(APPLICATION_NAME)
    //                 .build();

    //         }catch(GeneralSecurityException ex){
    //             throw ex;
    //         }

    //         try{
    //                 // String folderId, String filePath, String fileName

    //         // String mimeType = Files.probeContentType(Paths.get(usePath,ShopName,ShopItemId,name));

            
    //         String mimeType = Files.probeContentType(Paths.get("/Volumes/EXTERNAL_HDD/source/selenium_practical/Pipfile"));

    //         com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
    //         // fileMetadata.setName(name);
    //         fileMetadata.setName(Paths.get(ShopName,ShopItemId,"Pipfile").toString());

    //         // fileMetadata.setName(Paths.get(ShopName,ShopItemId,name).toString());
    //         // if (Objects.nonNull(folderId)) {
    //         //     fileMetadata.setParents(Collections.singletonList(folderId));
    //         // }

    //         // java.io.File localFilePath = 
    //         // FileContent mediaContent = new FileContent(mimeType, new File(Paths.get(usePath,ShopName,ShopItemId,name)));

    //         FileContent mediaContent = new FileContent(mimeType, new File("/Volumes/EXTERNAL_HDD/source/selenium_practical/Pipfile"));

    //         logger.info("upload try GoogleDrive");
    //         com.google.api.services.drive.model.File file = service.files()
    //                 .create(fileMetadata, mediaContent)
    //                 .setFields("id, name, webContentLink, webViewLink")
    //                 .execute();

    //         // ret = new HashMap<String,String>();
    //         // ret.put("id",file.getId());
    //         // ret.put("name",file.getName());
    //         // ret.put("webContentLink",file.getWebContentLink());
    //         // ret.put("webViewLink",file.getWebViewLink());

    //         } catch (IOException ex) {
    //             throw ex;
    //         }
            
    //         // Print the names and IDs for up to 10 files.
    //         // FileList result = service.files().list()
    //         //         .setPageSize(10)
    //         //         .setFields("nextPageToken, files(id, name)")
    //         //         .execute();
    //         // List<File> files = result.getFiles();
    //         // if (files == null || files.isEmpty()) {
    //         //     System.out.println("No files found.");
    //         // } else {
    //         //     System.out.println("Files:");
    //         //     for (File file : files) {
    //         //         System.out.printf("%s (%s)\n", file.getName(), file.getId());
    //         //     }
    //         // }
    //     }

    //     logger.info("upload finish");
    // }

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