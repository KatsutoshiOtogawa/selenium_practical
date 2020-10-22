# selenium_practical

## set up environment

In advance, you install chrome.app,pipenv and selenium IDE chrome-extensions.

```
pipenv install
pipenv shell

# Execute your pyhton script.
python3 test.py
```

# Attension!
you check chrome.app version, and you use chromedriver-binary version the same one that was chrome.app version you use.

you check Pipfile. and you select appropriate vesion
```
# ex)
chromedriver-binary = "~=86.0"
```

you add below import to you want to use script. This import is path resolvor for chrome driver.
```
import chromedriver_binary
```
One time only you encounter stop 18 age limitter, and sensitive, you execute 
```
time.sleep(60)
```

# use dynamodb local
you use java virtual machine or docker environment.

reffrence from [aws Document](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
## use java virtual machine.
in advance, you download DynamoDBLocal.jar file.
```
wget https://s3.ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/dynamodb_local_latest.tar.gz
wget https://s3.ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/dynamodb_local_latest.tar.gz.sha256
# amazon provide sha256 check file is not able to use as it is. you format file
gsed -i.org 's/ .*dynamodb_local_latest\.tar\.gz$/  dynamodb_local_latest\.tar\.gz/' dynamodb_local_latest.tar.gz.sha256

shasum -a 256 -c dynamodb_local_latest.tar.gz.sha256
# if check sum is OK, expand file.
tar zxvf dynamodb_local_latest.tar.gz

# delete unnecessary files.
rm dynamodb_local_latest.tar.gz* 
```

```
wget https://ftp.jaist.ac.jp/pub/apache/logging/log4j/2.13.3/apache-log4j-2.13.3-bin.tar.gz
wget https://www.apache.org/dist/logging/log4j/2.13.3/apache-log4j-2.13.3-bin.tar.gz.sha512

shasum -a 512 -c apache-log4j-2.13.3-bin.tar.gz.sha512

wget https://www.apache.org/dist/logging/log4j/2.13.3/apache-log4j-2.13.3-bin.tar.gz.asc
wget https://downloads.apache.org/logging/KEYS
gpg --import KEYS

# generate your secretkey, if first time use gpg.
# if you gpgconf --kill gpg-agent

gpg --generate-key

# 秘密鍵で署名する。
gpg --sign-key 739A04DE604E6AD81CCE457F1E4B1D05B095DD52
gpg --verify apache-log4j-2.13.3-bin.tar.gz.asc 

```

you launch DynamoDBLocal
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb &
```

set aws credential.
```
aws configure set aws_access_key_id fakeMyKeyId
aws configure set aws_secret_access_key fakeSecretAccessKey

# this setting is optional. you dont have to use.
aws configure set region 'ap-northeast-1'
aws configure set format json
```

you check conncet dynamoDB
```
aws dynamodb list-tables --endpoint-url http://localhost:8000
```

## use 
```
docker run -p 8000:8000 amazon/dynamodb-local:latest -jar DynamoDBLocal.jar -inMemory -sharedDb
```


# create table DynamoDBLocal

```
aws dynamodb create-table --endpoint-url http://localhost:8000 \
    --table-name ArtCollection \
    --attribute-definitions AttributeName=Artist,AttributeType=S AttributeName=SongTitle,AttributeType=S \
    --key-schema AttributeName=Artist,KeyType=HASH AttributeName=SongTitle,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```