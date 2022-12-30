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

**Step 4:**
Project use mailing for verifing user by email address. To use email verification add enviromental variables to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for mail server
```
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USER=test
MAIL_PASSWORD=test
```

## Technologies

* Java 11
* Spring 5.3.20 (Security, Data, Boot)
* Maven 3.8.1
* Junit 5
* Lombok 1.18.24
* SPringdoc 1.6.11
* MapStruct 1.5.2
* Twilio 8.31.1
* MySQL
* TypeScript 4.6.2
* Angular 14
* Bootstrap 5.2.2
* PrimeNG 14

## Demo
### Home
![11](https://user-images.githubusercontent.com/55559640/210115968-a012e748-8315-4b32-8117-41bec39f5afb.PNG)
### Login
![12](https://user-images.githubusercontent.com/55559640/210115972-6e6a4c2d-1f80-401a-b7ea-8eb7d4e0ac5e.PNG)
### Signup
![13](https://user-images.githubusercontent.com/55559640/210115988-1db67021-2c6d-4c40-8b70-b28fa48709aa.PNG)
### Add new collection
![14](https://user-images.githubusercontent.com/55559640/210116026-c452a5ce-ec87-4433-b900-6b16ba80acd6.PNG)
### Collection list
![15](https://user-images.githubusercontent.com/55559640/210116022-d3600ad2-7636-45b9-afa1-66265465736a.PNG)
### Collection
![16](https://user-images.githubusercontent.com/55559640/210116020-fc7fd605-fba5-4b77-8342-5bdd3732693e.PNG)
### Collection locations & items
![17](https://user-images.githubusercontent.com/55559640/210116015-b35607bb-d308-435c-8679-c5e6e01d2b56.PNG)
