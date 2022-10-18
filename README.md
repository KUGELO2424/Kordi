<p align="center">
<img src="https://user-images.githubusercontent.com/55559640/193420600-3238e881-105c-4c09-b094-a85ff9ca6760.png" width="320" height=160">

</p>

## General info

This project was created as part of an engineering thesis.
The goal of this project was to create a functional online app to coordinate in-kind assistance in crisis situations.
The application allows verified users to create collection and manage them. User can specify collection items and their limits.
Other logged or verified users can donate things to collections.

## How to Use

**Step 1:**
If you have mySql server in a cloud, you can run Kordi/backend/src/main/resources/data.sql
file to create database, tables and import same basic data.
If you have local mySql server, than you can use same script as above or set below property to update
```
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

**Step 2:**
Before running project, export `DB_HOSTNAME`, `DB_PORT`, `DB_NAME`, `KORDI_USER` and `KORDI_PASS` as your enviromental variables or add  them to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for connecting with local MySQL instance.
```
DB_HOSTNAME=localhost
DB_PORT=3307
DB_NAME=kordi
KORDI_USER=kordi
KORDI_PASS=kordi
```

**Step 3:**
Project use external platform Twilio for verifing user by phone number. To use phone verification, plese create account <a href="https://www.twilio.com/docs">here</a>.
Then export `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_SERVICE_ID` as your enviromental variables or add them to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for connecting with TWILIO platform.
```
TWILIO_ACCOUNT_SID=test
TWILIO_AUTH_TOKEN=test
TWILIO_SERVICE_ID=test
```

## Technologies

* Java 11
* Spring (Security, Data)
* Maven
* Junit 5
* Lombok
* MapStruct
* MySQL
* TypeScript
* Angular
