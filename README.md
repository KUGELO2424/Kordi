<p align="center">
<img src="https://user-images.githubusercontent.com/55559640/193420600-3238e881-105c-4c09-b094-a85ff9ca6760.png" width="320" height=160">

</p>

## General info

This project was created as part of an engineering thesis.
The goal of this project was to create a functional online app to coordinate in-kind assistance in crisis situations.
The application allows verified users to create collection and manage them. User can specify collection items and their limits.
Other logged users can donate things to collections.<br />
Web app has a responsive user interface built with Angular, Bootstrap, PrimeNG and use REST API implemented in Spring project<br />
The application is intended to improve the efficiency and effectiveness of assistance efforts in crisis situations, and to better match the needs of those affected with available resources.

## How to Use

**Step 1:**
To run Kordi you need to have an instance of mySql server. You can use the `mysql.yml` file for this and fire up mysql as a docker container using the following command:

```
docker-compose -f mysql.yml up -d
```

**Step 2:**
The `ddl-auto` property is set to `update` by default. After launching the kordi application, the database schema will be prepared. To import the initial data, change the `sql.init.mode` property to `always` to import data from the `data.sql` file. After importing the data, return to the previous value to prevent the data from being loaded twice. If you want to create the database schema yourself, you can use the db_schema.sql file for this purpose.

**Step 3:**
Before running project, export `DB_HOSTNAME`, `DB_PORT`, `DB_NAME`, `KORDI_USER` and `KORDI_PASS` as your enviromental variables or add them to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for connecting with local MySQL instance.

```
DB_HOSTNAME=localhost
DB_PORT=3307
DB_NAME=kordi
KORDI_USER=kordi
KORDI_PASS=kordi
```

**Step 4:**
Project use external platform Twilio for verifing user by phone number. To use phone verification, plese create account <a href="https://www.twilio.com/docs">here</a>.
Then export `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_SERVICE_ID` as your enviromental variables or add them to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for connecting with TWILIO platform.

```
TWILIO_ACCOUNT_SID=test
TWILIO_AUTH_TOKEN=test
TWILIO_SERVICE_ID=test
```

**Step 5:**
Project use mailing for verifing user by email address. To use email verification add enviromental variables to configuration.properties file locally with proper values.
Example values in configuration.properties that could be used for mail server

```
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USER=test
MAIL_PASSWORD=test
```

**Step 6:**
After setting all the properties, run the following command to launch the application:

```
./mvnw spring-boot:run
```

## Technologies

- Java 11
- Spring 5.3.20 (Security, Data, Boot)
- Maven 3.8.1
- Junit 5
- Lombok 1.18.24
- SPringdoc 1.6.11
- MapStruct 1.5.2
- Twilio 8.31.1
- MySQL
- TypeScript 4.6.2
- Angular 14
- Bootstrap 5.2.2
- PrimeNG 14

## Demo

### Home

![11](https://user-images.githubusercontent.com/55559640/210115968-a012e748-8315-4b32-8117-41bec39f5afb.PNG)

### Login

![12](https://user-images.githubusercontent.com/55559640/210115972-6e6a4c2d-1f80-401a-b7ea-8eb7d4e0ac5e.PNG)

### Signup

![13](https://user-images.githubusercontent.com/55559640/210115988-1db67021-2c6d-4c40-8b70-b28fa48709aa.PNG)

### Phone verification

![verify](https://user-images.githubusercontent.com/55559640/226198698-b4f6bfe1-e106-4f4c-b4b7-a6934519368f.PNG)

### Add new collection (info)

![add01](https://user-images.githubusercontent.com/55559640/213863107-6509ba30-a18c-4285-be6f-8c1463ad5029.PNG)

### Add new collection (locations)

![add02](https://user-images.githubusercontent.com/55559640/213863117-5b071ae3-8c3a-4048-877d-9dca5b76c078.PNG)

### Add new collection (add item)

![add03](https://user-images.githubusercontent.com/55559640/213863122-76804920-987e-4784-8bac-02286a5b5d2c.PNG)

### Add new collection (items)

![add04](https://user-images.githubusercontent.com/55559640/213863127-9f304c5e-ee44-473e-b5bd-ecbd6ba9bb29.PNG)

### Collection list (filter & sort)

![collections01](https://user-images.githubusercontent.com/55559640/213863138-d45aac58-fe49-47c9-8aa5-7bb54c5d4a87.PNG)

### Collection list

![collections02](https://user-images.githubusercontent.com/55559640/213863139-a13fa8c4-806d-4d27-9af9-2d93d4e9ed9e.PNG)

### Collection

![collection01](https://user-images.githubusercontent.com/55559640/213863143-f68c8208-0a2a-4a21-9d84-657abf16234f.PNG)

### Collection share buttons

![collection04](https://user-images.githubusercontent.com/55559640/213864109-abde9eef-57af-488e-b0e1-a0997a0f13dd.PNG)

### Collection (locations & comments)

![collection02](https://user-images.githubusercontent.com/55559640/213863157-03b5ea3c-ec4e-4665-9e25-cf8c44ec98b5.PNG)

### Collection (items)

![collection03](https://user-images.githubusercontent.com/55559640/213863160-fb745d97-6712-473e-b0a1-3a75596fdc3b.PNG)

### Collection donate overview

![overview](https://user-images.githubusercontent.com/55559640/213863162-160bf717-fb57-4ce7-90e5-ecb9726a5b7f.PNG)

### Profile (user collections)
![profile1](https://user-images.githubusercontent.com/55559640/226198729-5627af8a-f2eb-4211-9071-18deeef0a404.PNG)

### Profile (user donates)
![profile2](https://user-images.githubusercontent.com/55559640/226198732-006bcabc-ad4c-4a3b-ae84-2f5bffbce70b.PNG)

### Collection (donated items)
![donates](https://user-images.githubusercontent.com/55559640/226198734-9c245b17-8fbb-479a-addf-2b146b9281e0.PNG)
