DROP SCHEMA IF EXISTS kordi;

CREATE SCHEMA kordi;
USE kordi;

--
-- Struktura tabeli user
--
CREATE TABLE kordi.user (
	id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password CHAR(60) NOT NULL,
    email VARCHAR(319) NOT NULL,
	phone VARCHAR(319) NOT NULL,
    enabled BOOLEAN,
    PRIMARY KEY (id)
);

--
-- Struktura tabeli email_token
--
CREATE TABLE kordi.email_token (
	id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(200) NOT NULL,
	created_at DATETIME NOT NULL,
	expires_at DATETIME NOT NULL,
	confirmed_at DATE,
	user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id)
);
	
--
-- Struktura tabeli collection
--
CREATE TABLE kordi.collection (
	id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    start_time DATETIME,
    end_time DATETIME,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id)
);

--
-- Struktura tabeli address
--
CREATE TABLE kordi.address (
	id BIGINT NOT NULL AUTO_INCREMENT,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100),
	collection_id BIGINT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (collection_id) REFERENCES kordi.collection(id)
);
	
--
-- Struktura tabeli collection_item
--
CREATE TABLE kordi.collection_item (
	id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED'),
    current_amount INT,
    max_amount INT,
	collection_id BIGINT NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (collection_id) REFERENCES kordi.collection(id)
);

--
-- Struktura tabeli submitted_item
--
CREATE TABLE kordi.submitted_item (
	id BIGINT NOT NULL AUTO_INCREMENT,
    collection_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	item_id BIGINT NOT NULL,
	amount INT NOT NULL,
	submit_time DATETIME NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (collection_id) REFERENCES kordi.collection(id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id),
	FOREIGN KEY (item_id) REFERENCES kordi.collection_item(id)
);
	
--
-- Struktura tabeli collection_comment
--
CREATE TABLE kordi.collection_comment (
	id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(250) NOT NULL,
	created_time DATETIME NOT NULL,
	user_id BIGINT NOT NULL,
	collection_id BIGINT NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id),
	FOREIGN KEY (collection_id) REFERENCES kordi.collection(id)
);
	
--
-- Struktura tabeli collection_archive
--
CREATE TABLE kordi.collection_archive (
	id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    start_time DATETIME,
    end_time DATETIME,
    city VARCHAR(50) NOT NULL,
	street VARCHAR(50),
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id)
);

--
-- Struktura tabeli address_archive
--
CREATE TABLE kordi.address_archive (
	id BIGINT NOT NULL AUTO_INCREMENT,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100),
	collection_id BIGINT NOT NULL,
	PRIMARY KEY (id)
);
	
--
-- Struktura tabeli collection_item_archive
--
CREATE TABLE kordi.collection_item_archive (
	id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('amount', 'weight', 'unlimited'),
    current_amount INT,
    max_amount INT,
	collection_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

--
-- Struktura tabeli submitted_item_archive
--
CREATE TABLE kordi.submitted_item_archive (
	id INT NOT NULL AUTO_INCREMENT,
    collection_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	item_id BIGINT NOT NULL,
	amount INT NOT NULL,
	submit_time DATETIME NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES kordi.user(id)
);

--
-- Wyzwalacz do collection
--
DELIMITER $$
CREATE TRIGGER collection_delete
AFTER DELETE ON collection
FOR EACH ROW
BEGIN
	INSERT INTO collection_archive VALUES (old.id, old.title, old.description, old.start_time, old.end_time, old.user_id);
END$$

--
-- Wyzwalacz do collection_item
--
CREATE TRIGGER collection_item_delete
AFTER DELETE ON collection_item
FOR EACH ROW
BEGIN
	INSERT INTO collection_item_archive VALUES (old.id, old.name, old.type, old.current_amount, old.max_amount, old.collection_id);
END$$

--
-- Wyzwalacz do submitted_item
--
CREATE TRIGGER submitted_item_delete
AFTER DELETE ON submitted_item
FOR EACH ROW
BEGIN
	INSERT INTO submitted_item_archive  VALUES (old.id, old.collection_id, old.user_id, old.item_id, old.amount, old.submit_time);
END$$

--
-- Wyzwalacz do address
--
CREATE TRIGGER address_delete
AFTER DELETE ON address
FOR EACH ROW
BEGIN
	INSERT INTO address_archive VALUES (old.id, old.city, old.street, old.collection_id);
END$$
DELIMITER ;

--
-- Dane user
--
INSERT INTO user VALUES (1, "Grzegorz", "Kucharski", "gelo2424", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "gelo@gmail.com", "669649785", "1");
INSERT INTO user VALUES (2, "Adam", "Nowak", "adam", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "adam@gmail.com", "123123123", "1");
INSERT INTO user VALUES (3, "Jan", "Jerzy", "jan", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "jan@gmail.com", "321321321", "1");
INSERT INTO user VALUES (4, "Ewa", "Stopa", "ewa", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "ewa@gmail.com", "543543543", "1");
INSERT INTO user VALUES (5, "Daniel", "Spaniel", "daniel", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "daniel@gmail.com", "987123123", "1");
INSERT INTO user VALUES (6, "Michał", "Kichał", "michał", "$2a$12$exSprWI2lA.TgHrkIQyfR.QAHfsOVaCPKBLBhMUOPruXUDdj3JcK6", "michal@gmail.com", "555444333", "1");

--
-- Dane collection
--
INSERT INTO collection VALUES (1, "Zbiórka dla Bartka", "Zbieramy ubrania dla chłopca", null, null, 4);
INSERT INTO collection VALUES (2, "Zbiórka dla Oliwi", "Zbieramy ubrania dziewczęce", null, null,  4);

--
-- Dane address
--
INSERT INTO address VALUES (1, "Łódź", "Piotrkowska 54, m. 4", 1);
INSERT INTO address VALUES (2, "Łódź", "Piotrkowska 54, m. 4", 2);

--
-- Dane collection_item
--
INSERT INTO collection_item VALUES (1, "Buty sportowe", "amount", 0, 4, 1);
INSERT INTO collection_item VALUES (2, "Koszulki", "amount", 1, 10, 1);

INSERT INTO collection_item VALUES (3, "Spodnie", "amount", 0, 2, 2);

--
-- Dane submitted_item
--
INSERT INTO submitted_item VALUES (1, 1, 2, 2, 1, '2022-06-21 10:00:00');

