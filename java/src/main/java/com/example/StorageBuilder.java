package com.example;

import java.io.IOException;
import java.util.Properties;
import java.security.GeneralSecurityException;

/**
 * ファイルが多くなってきたらAWSS3などをprivateのinnerclassにして、AWSS3などのコンストラクタを外部から実行できないようにする。
 * その場合はDBTYPEもstaticないの列挙隊になる。
 */
public class StorageBuilder {

    public static Storage build(Properties properties) throws StorageTypeNotFoundException,GeneralSecurityException,IOException
    {
        Storage  storage = null;
        switch(System.getenv("USE_STORAGE") != null ? System.getenv("USE_STORAGE") : properties.getProperty("USE_STORAGE"))
        {
            case "S3":

                storage = new AWSS3(properties);
                break;
            // case "LOCAL_DISK":
            //     this.storageType = StorageType.LOCAL_DISK;
            //     break;
            case "GOOGLE_DRIVE":
                storage = new GoogleDrive(properties);
                break;
            default:
                throw new StorageTypeNotFoundException();
        }

        return storage;
    }
}