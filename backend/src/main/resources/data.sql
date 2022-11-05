DROP SCHEMA IF EXISTS kordi;

CREATE SCHEMA kordi;

--
-- Struktura tabeli user
--
CREATE TABLE IF NOT EXISTS account (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(319) NOT NULL,
	phone VARCHAR(319) NOT NULL,
    enabled BOOLEAN,
    verification_type ENUM('EMAIL', 'PHONE')
);

--
-- Struktura tabeli email_token
--
CREATE TABLE IF NOT EXISTS email_token (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    token VARCHAR(200) NOT NULL,
	created_at DATETIME NOT NULL,
	expires_at DATETIME NOT NULL,
	confirmed_at DATETIME,
	user_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id)
);
	
--
-- Struktura tabeli collection
--
CREATE TABLE IF NOT EXISTS collection (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    start_time DATETIME,
    end_time DATETIME,
    user_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id)
);

--
-- Struktura tabeli address
--
CREATE TABLE IF NOT EXISTS address (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100),
	collection_id BIGINT NOT NULL,
	FOREIGN KEY (collection_id) REFERENCES collection(id)
);
	
--
-- Struktura tabeli collection_item
--
CREATE TABLE IF NOT EXISTS collection_item (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED') NOT NULL,
    category ENUM('CLOTHES', 'FOOD', 'ANIMALS', 'CHILDREN', 'MEDICINE', 'ELECTRONIC', 'OTHER') NOT NULL,
    current_amount INT,
    max_amount INT,
	collection_id BIGINT NOT NULL,
	FOREIGN KEY (collection_id) REFERENCES collection(id)
);

--
-- Struktura tabeli submitted_item
--
CREATE TABLE IF NOT EXISTS submitted_item (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    collection_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	item_id BIGINT NOT NULL,
	amount INT NOT NULL,
	submit_time DATETIME NOT NULL,
	FOREIGN KEY (collection_id) REFERENCES collection(id),
	FOREIGN KEY (user_id) REFERENCES account(id),
	FOREIGN KEY (item_id) REFERENCES collection_item(id)
);
	
--
-- Struktura tabeli collection_comment
--
CREATE TABLE IF NOT EXISTS collection_comment (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    content VARCHAR(250) NOT NULL,
	created_time DATETIME NOT NULL,
	user_id BIGINT NOT NULL,
	collection_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id),
	FOREIGN KEY (collection_id) REFERENCES collection(id)
);


--
-- ARCHIWA
--
--
-- Struktura tabeli collection_archive
--
CREATE TABLE IF NOT EXISTS collection_archive (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    start_time DATETIME,
    end_time DATETIME,
    city VARCHAR(50) NOT NULL,
	street VARCHAR(50),
    user_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id)
);

--
-- Struktura tabeli address_archive
--
CREATE TABLE IF NOT EXISTS address_archive (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100),
	collection_id BIGINT NOT NULL
);
	
--
-- Struktura tabeli collection_item_archive
--
CREATE TABLE IF NOT EXISTS collection_item_archive (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('amount', 'weight', 'unlimited'),
    current_amount INT,
    max_amount INT,
	collection_id BIGINT NOT NULL
);

--
-- Struktura tabeli submitted_item_archive
--
CREATE TABLE IF NOT EXISTS submitted_item_archive (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    collection_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	item_id BIGINT NOT NULL,
	amount INT NOT NULL,
	submit_time DATETIME NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id)
);

-- --
-- -- Wyzwalacz do collection
-- --
-- CREATE TRIGGER collection_delete
-- AFTER DELETE ON collection
-- FOR EACH ROW
-- INSERT INTO collection_archive VALUES (old.id, old.title, old.description, old.start_time, old.end_time, old.user_id);
--
--
-- --
-- -- Wyzwalacz do collection_item
-- --
-- CREATE TRIGGER collection_item_delete
-- AFTER DELETE ON collection_item
-- FOR EACH ROW
-- INSERT INTO collection_item_archive VALUES (old.id, old.name, old.type, old.current_amount, old.max_amount, old.collection_id);
--
-- --
-- -- Wyzwalacz do submitted_item
-- --
-- CREATE TRIGGER submitted_item_delete
-- AFTER DELETE ON submitted_item
-- FOR EACH ROW
-- INSERT INTO submitted_item_archive  VALUES (old.id, old.collection_id, old.user_id, old.item_id, old.amount, old.submit_time);
--
--
-- --
-- -- Wyzwalacz do address
-- --
-- CREATE TRIGGER address_delete
-- AFTER DELETE ON address
-- FOR EACH ROW
-- INSERT INTO address_archive VALUES (old.id, old.city, old.street, old.collection_id);

--
-- Dane user
--
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('9db1664b-246d-4763-b75a-a95e11655636', 'Grzegorz', 'Kucharski', 'gelo2424', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'gelo@gmail.com', '669649785', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('10356aa8-3c10-478e-a095-c3aac109f3a4', 'Adam', 'Nowak', 'adam', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'adam@gmail.com', '123123123', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('4781e71b-2016-438f-9065-bcfc37558da4', 'Jan', 'Jerzy', 'jan', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'jan@gmail.com', '321321321', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('5ba1164c-ad29-49d9-bd78-2f54800b4d96', 'Ewa', 'Stopa', 'ewa', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'ewa@gmail.com', '543543543', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('407855b1-f901-4efa-9634-5fbbc57e61b3', 'Daniel', 'Spaniel', 'daniel', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'daniel@gmail.com', '987123123', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled)
VALUES ('bf6cc765-1baa-4a39-9e9d-74cd570e4eb1', 'Michał', 'Kichał', 'michał', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'michal@gmail.com', '555444333', '1');
INSERT INTO account (uuid, first_name, last_name, username, password, email, phone, enabled, verification_type)
VALUES ('af1c34e0-9843-494b-975d-e1214ed1d49e', 'Test', 'Test', 'test', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'test@gmail.com', '111666342', '0', 'EMAIL');

--
-- Dane email_token
--
INSERT INTO email_token (uuid, token, created_at, expires_at, confirmed_at, user_id)
VALUES ('87251290-7859-4dcf-b3f0-8b4c2aa2e55b', 'qwerty123456', '2022-06-28 15:00:00', '2022-06-28 15:15:00', null, 7);

--
-- Dane collection
--
INSERT INTO collection (uuid, title, description, start_time, end_time, user_id)
VALUES ('87251290-7859-4dcf-b3f0-8b4c2aa2e55b', 'Zbiórka dla Bartka', 'Zbieramy ubrania dla chłopca',
        '2022-06-28 15:15:00', null, 4);
INSERT INTO collection (uuid, title, description, start_time, end_time, user_id)
VALUES ('dc3d8821-a9d9-43d8-a5d8-695824bd0880', 'Zbiórka dla Oliwii', 'Zbieramy ubrania dziewczęce',
        '2022-06-28 15:25:00', null,  4);
INSERT INTO collection (uuid, title, description, start_time, end_time, user_id)
VALUES ('4c1e22e9-85c2-4cae-add3-0e82e206e9ac', 'Dary dary dla Oliwii', 'zbieram dary', '2022-06-28 15:45:00',
        null,  3);
INSERT INTO collection (uuid, title, description, start_time, end_time, user_id)
VALUES ('1603f612-fd7f-495d-9829-4b637d48c374', 'Pomoc dla Plamy', 'pomoc dla królika', '2022-06-28 15:55:00',
        null,  3);

--
-- Dane comments
--
INSERT INTO collection_comment (uuid, content, created_time, user_id, collection_id)
VALUES ('de8ed0c2-8a32-42b4-a5b3-a1ed93473f2f', 'New content for comment 01', '2022-06-28 16:00:00', 1, 1);
INSERT INTO collection_comment (uuid, content, created_time, user_id, collection_id)
VALUES ('523bb084-abed-4580-a5ea-f059a8fb91ae', 'New content for comment 02', '2022-06-28 16:05:00', 2, 1);
INSERT INTO collection_comment (uuid, content, created_time, user_id, collection_id)
VALUES ('c7a0fbdd-ba0f-4f0d-957d-4ac46dd335bd', 'New content for comment 03', '2022-06-28 16:10:00', 3, 2);

--
-- Dane address
--
INSERT INTO address (uuid, city, street, collection_id)
VALUES ('c861c901-e9c7-42ab-8eba-a2b7b956ca8e', 'Łódź', 'Piotrkowska 54, m. 4', 1);
INSERT INTO address (uuid, city, street, collection_id)
VALUES ('b6d80c9c-a532-459e-9807-e8a0d9c7ed02', 'Łódź', 'Piotrkowska 54, m. 4', 2);
INSERT INTO address (uuid, city, street, collection_id)
VALUES ('b1c4f59d-558e-4ad9-9e5e-7d5a60b50fd9', 'Warszawa', 'Wolna 54, m. 4', 3);
INSERT INTO address (uuid, city, street, collection_id)
VALUES ('306f5343-9fa9-4506-ad24-51ea97df06a8', 'Warszawa', 'Noc 20', 3);

--
-- Dane collection_item
--
INSERT INTO collection_item (uuid, name, type, category, current_amount, max_amount, collection_id)
VALUES ('ce2ac4b5-f508-48f0-9326-09a01616ac9a', 'Buty sportowe', 'amount', 'CLOTHES', 0, 4, 1);
INSERT INTO collection_item (uuid, name, type, category, current_amount, max_amount, collection_id)
VALUES ('be89507a-db89-4b7d-bac1-d76696b392e8', 'Buty zimowe', 'amount', 'CLOTHES', 0, 2, 1);
INSERT INTO collection_item (uuid, name, type, category, current_amount, max_amount, collection_id)
VALUES ('97187d76-d5ea-4340-936b-9c7584020cf5', 'Koszulki', 'amount', 'CLOTHES', 1, 10, 1);
INSERT INTO collection_item (uuid, name, type, category, current_amount, max_amount, collection_id)
VALUES ('51352e5f-8b86-472f-8364-ab4f0f65db24', 'Spodnie', 'amount', 'CLOTHES', 0, 2, 2);

--
-- Dane submitted_item
--
INSERT INTO submitted_item (uuid, collection_id, user_id, item_id, amount, submit_time)
VALUES ('8ec4a64a-3208-4e0c-a93a-2de593df4fdc', 1, 2, 2, 1, '2022-06-21 10:00:00');