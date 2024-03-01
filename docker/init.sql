CREATE DATABASE IF NOT EXISTS demo;
USE demo;

CREATE TABLE `customer` (
	id int NOT NULL AUTO_INCREMENT,
	name varchar(100) not null,
	email varchar(100) not null,
	PRIMARY KEY (id),
    CONSTRAINT UC_customer UNIQUE (id,name,email)
);

insert into customer (name, email) values ('yodsarun', 'yodsarun.cpe30@gmail.com');