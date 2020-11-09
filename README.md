# selenium_practical

## set up environment

In advance, you install chrome.app.

# python
you need to pipenv install.
```
pipenv install
pipenv shell

# Execute your pyhton script.
python3 main.py
```
# java
you need to install mvn.
```
# you download chrome driver binary.
wget https://chromedriver.storage.googleapis.com/86.0.4240.22/chromedriver_mac64.zip #(execute in /path/to/you/Selenium_practical/)
unzip chromedriver_mac64.zip
rm chromedriver_mac64.zip

# build code.
mvn package 
mvn compile
# execute class file.
java -cp classes/:dependency-jars/* com.example.App #(execute in /path/to/java/target/)
# create jar file.
mvn package
# execute jar file.
java -jar java-1.0-SNAPSHOT.jar #(execute in /path/to/java/target/)
# you need file is, chromedriver,java-1.0-SNAPSHOT.jar,dependency-jars/* and resources/ in production Environment.
```

# C#
you need to install dotnet-sdk.
```
# build code.
dotnet build
# start code.
dotnet run 
# execute class file.
java -cp classes/:dependency-jars/* com.example.App #(execute in /path/to/java/target/)
# create jar file.
mvn package
# execute jar file.
java -jar java-1.0-SNAPSHOT.jar #(execute in /path/to/java/target/)
# you need file is, chromedriver,java-1.0-SNAPSHOT.jar,dependency-jars/* and resources/ in production Environment.
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

you launch DynamoDBLocal
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb &
```

set aws credential.
```
aws configure set aws_access_key_id fakeMyKeyId
aws configure set aws_secret_access_key fakeSecretAccessKey

set -o allexport;
source .env
set +o allexport


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
docker run -p 8000:8000 amazon/dynamodb-local:latest -jar DynamoDBLocal.jar -inMemory -sharedDb &
```


# create table DynamoDBLocal

if you want not to create DynamoDB-Local, omit endpoint-url parameter.
```
aws dynamodb create-table --endpoint-url http://localhost:8000 \
    --table-name ArtCollection \
    --attribute-definitions AttributeName=ShopItemName,AttributeType=S \
                            AttributeName=CreatedAt,AttributeType=S \
    --key-schema AttributeName=ShopItemName,KeyType=HASH \
                 AttributeName=CreatedAt,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```


# Refference

- [Selenium Document](https://www.selenium.dev/documentation/en/)

## aws cli Credential

[aws cli Credential](https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-precedence)

## DynamoDB
[NoSQL Workbench (GUI tool)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.settingup.html)
## Java

- [apache log4j](https://logging.apache.org/log4j/2.x/index.html)
- [log4j json](https://github.com/prayagupd/log4j2-JSONLayout)
- [Selenium java document](https://www.selenium.dev/selenium/docs/api/java/overview-summary.html)
- [apache StringUtils](http://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)
- [aws dynamoDB java document](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/dynamodb/DynamoDbClient.html)

- [aws dynamoDB java SDK](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/getting-started.html)

## Csharp

## Python

- [boto3]()