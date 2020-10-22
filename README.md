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
in advance, you download DynamoDBLocal.jar file.
```
wget https://s3.ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/dynamodb_local_latest.tar.gz
wget https://s3.ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/dynamodb_local_latest.tar.gz.sha256
# amazon provide sha256 check file is not able to use as it is. you format file
gsed -i.org 's/ .*dynamodb_local_latest\.tar\.gz$/  dynamodb_local_latest\.tar\.gz/' dynamodb_local_latest.tar.gz.sha256

# if check sum is OK, expand file.
tar zxvf dynamodb_local_latest.tar.gz

# delete unnecessary files.
rm dynamodb_local_latest.tar.gz* 
```

```
docker run -p 8000:8000 amazon/dynamodb-local:latest -jar DynamoDBLocal.jar -inMemory -sharedDb
```
