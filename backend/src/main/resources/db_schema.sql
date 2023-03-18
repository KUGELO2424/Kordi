DROP SCHEMA IF EXISTS kordi;

CREATE SCHEMA kordi;

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

CREATE TABLE IF NOT EXISTS collection (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    start_time DATETIME,
    end_time DATETIME,
    completed_time DATETIME,
    image LONGBLOB,
    donates BIGINT,
    status ENUM('IN_PROGRESS', 'COMPLETED', 'ARCHIVED') NOT NULL,
    user_id BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS address (
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100),
	collection_id BIGINT NOT NULL,
	FOREIGN KEY (collection_id) REFERENCES collection(id)
);

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