DROP SCHEMA IF EXISTS kordi;

CREATE SCHEMA kordi;

--
-- Struktura tabeli user
--
CREATE TABLE IF NOT EXISTS account (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(319) NOT NULL,
	phone VARCHAR(319) NOT NULL,
    enabled BOOLEAN
);

--
-- Struktura tabeli email_token
--
CREATE TABLE IF NOT EXISTS email_token (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
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
    name VARCHAR(100) NOT NULL,
    type ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED'),
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
    content VARCHAR(250) NOT NULL,
	created_time DATETIME NOT NULL,
	user_id BIGINT NOT NULL,
	collection_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id),
	FOREIGN KEY (collection_id) REFERENCES collection(id)
);
	
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
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Grzegorz', 'Kucharski', 'gelo2424', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'gelo@gmail.com', '669649785', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Adam', 'Nowak', 'adam', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'adam@gmail.com', '123123123', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Jan', 'Jerzy', 'jan', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'jan@gmail.com', '321321321', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Ewa', 'Stopa', 'ewa', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'ewa@gmail.com', '543543543', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Daniel', 'Spaniel', 'daniel', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'daniel@gmail.com', '987123123', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Michał', 'Kichał', 'michał', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'michal@gmail.com', '555444333', '1');
INSERT INTO account (first_name, last_name, username, password, email, phone, enabled)
VALUES ('Test', 'Test', 'test', '$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6', 'test@gmail.com', '111666342', '0');

--
-- Dane email_token
--
INSERT INTO email_token (token, created_at, expires_at, confirmed_at, user_id)
VALUES ('qwerty123456', '2022-06-28 15:00:00', '2022-06-28 15:15:00', null, 7);

--
-- Dane collection
--
INSERT INTO collection (title, description, start_time, end_time, user_id)
VALUES ('Zbiórka dla Bartka', 'Zbieramy ubrania dla chłopca', null, null, 4);
INSERT INTO collection (title, description, start_time, end_time, user_id)
VALUES ('Zbiórka dla Oliwii', 'Zbieramy ubrania dziewczęce', null, null,  4);
INSERT INTO collection (title, description, start_time, end_time, user_id)
VALUES ('Dary dary dla Oliwii', 'zbieram dary', null, null,  3);

--
-- Dane address
--
INSERT INTO address (city, street, collection_id)
VALUES ('Łódź', 'Piotrkowska 54, m. 4', 1);
INSERT INTO address (city, street, collection_id)
VALUES ('Łódź', 'Piotrkowska 54, m. 4', 2);
INSERT INTO address (city, street, collection_id)
VALUES ('Warszawa', 'Wolna 54, m. 4', 3);

--
-- Dane collection_item
--
INSERT INTO collection_item (name, type, current_amount, max_amount, collection_id)
VALUES ('Buty sportowe', 'amount', 0, 4, 1);
INSERT INTO collection_item (name, type, current_amount, max_amount, collection_id)
VALUES ('Koszulki', 'amount', 1, 10, 1);
INSERT INTO collection_item (name, type, current_amount, max_amount, collection_id)
VALUES ('Spodnie', 'amount', 0, 2, 2);

--
-- Dane submitted_item
--
INSERT INTO submitted_item (collection_id, user_id, item_id, amount, submit_time)
VALUES (1, 2, 2, 1, '2022-06-21 10:00:00');