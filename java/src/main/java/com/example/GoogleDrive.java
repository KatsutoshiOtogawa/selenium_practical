
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

// GOOGLE Drive
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
// import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
/**
 * 
 */
public class GoogleDrive extends Storage<Drive> {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    // private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    // private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA);
    // DRIVE_METADATA
    // private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private static final String CREDENTIALS_FILE_PATH = String.join("/",System.getProperty("user.dir"),"resources","credentials.json");

    public GoogleDrive(Properties properties) throws StorageTypeNotFoundException,GeneralSecurityException,IOException
    {
        this.logger = LogManager.getFormatterLogger(GoogleDrive.class);
        this.CreatedAt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        this.properties = properties;
        constructor();
    }

    protected void constructor() throws StorageTypeNotFoundException,GeneralSecurityException,IOException
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

        this.storageType = StorageType.GOOGLE_DRIVE;

        this.usePath = System.getenv("USE_PATH") != null ? System.getenv("USE_PATH") : properties.getProperty("USE_PATH");

        this.bucketname = (System.getenv("BUCKET_NAME") != null ? System.getenv("BUCKET_NAME") : properties.getProperty("BUCKET_NAME")
                            ).toLowerCase();

        storageClient = createStorageConnection();
        createRemoteStorage();
        
    }

    public void destructor()
    {

    }

    @Override
    protected Drive createStorageConnection() throws GeneralSecurityException,IOException
    {
        logger.info("createStorageConnection start");

        Drive connection = null;
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        connection = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
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
    protected void createRemoteStorage()
    {
        logger.debug("createRemoteStorage noop!");
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
        
        logger.info("upload try create request");

        try
        {

            String mimeType = Files.probeContentType(Paths.get("/Volumes/EXTERNAL_HDD/source/selenium_practical/Pipfile"));

            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            // fileMetadata.setName(name);
            fileMetadata.setName(Paths.get(ShopName,ShopItemId,"Pipfile").toString());

            // fileMetadata.setName(Paths.get(ShopName,ShopItemId,name).toString());
            // if (Objects.nonNull(folderId)) {
            //     fileMetadata.setParents(Collections.singletonList(folderId));
            // }


            FileContent mediaContent = new FileContent(mimeType, new File("/Volumes/EXTERNAL_HDD/source/selenium_practical/Pipfile"));

            logger.info("upload try GoogleDrive");
            com.google.api.services.drive.model.File file = storageClient.files()
                                                .create(fileMetadata, mediaContent)
                                                .setFields("id, name, webContentLink, webViewLink")
                                                .execute();

            // ret = new HashMap<String,String>();
            // ret.put("id",file.getId());
            // ret.put("name",file.getName());
            // ret.put("webContentLink",file.getWebContentLink());
            // ret.put("webViewLink",file.getWebViewLink());

        } catch (IOException ex) {
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

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        // InputStream in = Storage.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}