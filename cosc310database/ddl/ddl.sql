CREATE DATABASE IF NOT EXISTS cosc310database;

USE cosc310database;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Staff;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Purchase;
DROP TABLE IF EXISTS Supplier;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS PurchasedProduct;
DROP TABLE IF EXISTS ProductOrder;
DROP TABLE IF EXISTS OrderedProduct;
DROP TABLE IF EXISTS RentalItem;
DROP TABLE IF EXISTS RentalItemType;
DROP TABLE IF EXISTS PurchasedRental;
DROP TABLE IF EXISTS ServiceType;
DROP TABLE IF EXISTS WorkOrder;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Staff (
	first_name VARCHAR(30),
	last_name VARCHAR(30),
	username VARCHAR(30),
	password VARCHAR(30),
	manager BOOLEAN,
	PRIMARY KEY (username)
);

CREATE TABLE Customer (
	id INT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(30),
	last_name VARCHAR(30),
	email VARCHAR(50),
	phone VARCHAR(15),
	PRIMARY KEY (id)
);

CREATE TABLE Purchase (
	id INT NOT NULL AUTO_INCREMENT,
	purchase_date DATE,
	customer_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY (customer_id) REFERENCES Customer (id)
);

CREATE TABLE Supplier (
	id INT NOT NULL AUTO_INCREMENT,
	supplier_name VARCHAR(50),
	PRIMARY KEY (id)
);

CREATE TABLE Product (
	id INT NOT NULL AUTO_INCREMENT,
	product_name VARCHAR(50),
	price DECIMAL(7,2),
	supplier_id INT,
	stock INT,
	PRIMARY KEY (id),
	FOREIGN KEY (supplier_id) REFERENCES Supplier (id)
);

CREATE TABLE PurchasedProduct (
	purchase_id INT,
	product_id INT,
	amount INT,
	PRIMARY KEY (purchase_id, product_id),
	FOREIGN KEY (purchase_id) REFERENCES Purchase (id),
	FOREIGN KEY (product_id) REFERENCES Product (id)
);

CREATE TABLE ProductOrder (
	id INT NOT NULL AUTO_INCREMENT,
	order_date DATE,
	supplier_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY (supplier_id) REFERENCES Supplier (id)
);


CREATE TABLE OrderedProduct (
	order_id INT,
	product_id INT,
	amount INT,
	PRIMARY KEY (order_id, product_id),
	FOREIGN KEY (order_id) REFERENCES ProductOrder (id),
	FOREIGN KEY (product_id) REFERENCES Product (id)
);

CREATE TABLE RentalItemType (
	id INT NOT NULL AUTO_INCREMENT,
	type_name VARCHAR(50),
	price_per_day DECIMAL(7,2),
	PRIMARY KEY (id)
);

CREATE TABLE RentalItem (
	id INT NOT NULL AUTO_INCREMENT,
	type_id INT,
	FOREIGN KEY (type_id) REFERENCES RentalItemType (id),
	PRIMARY KEY (id)
);

CREATE TABLE PurchasedRental(
	purchase_id INT,
	rental_item_id INT,
	pickup_date DATE,
	return_date DATE,
	PRIMARY KEY (purchase_id, rental_item_id),
	FOREIGN KEY (purchase_id) REFERENCES Purchase (id),
	FOREIGN KEY (rental_item_id) REFERENCES RentalItem (id)
);

CREATE TABLE ServiceType (
	id INT NOT NULL AUTO_INCREMENT,
	service_name VARCHAR(50),
	price DECIMAL(7,2),
	PRIMARY KEY (id)
);

CREATE TABLE WorkOrder (
	purchase_id INT,
	service_id INT,
	due_date DATE,
	service_notes VARCHAR(300),
	PRIMARY KEY (purchase_id, service_id),
	FOREIGN KEY (purchase_id) REFERENCES Purchase (id),
	FOREIGN KEY (service_id) REFERENCES ServiceType (id)
);

INSERT INTO Staff VALUES ("Stu", "McGorman", "stumcg", "password", TRUE);
INSERT INTO Staff VALUES ("Bob", "Brown", "bbrown", "password", FALSE);

INSERT INTO Supplier VALUES (Null, "CoolGear");

INSERT INTO Product VALUES (Null, "Skis", "669.99", 1, 10);
INSERT INTO Product VALUES (Null, "Special Skis", "999.99", 1, 1);
INSERT INTO Product VALUES (Null, "Hat", "19.95", 1, 50);

INSERT INTO Purchase VALUES (1, STR_TO_DATE("11 5 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (1, 1, 4);
INSERT INTO PurchasedProduct VALUES (1, 2, 1);

INSERT INTO Purchase VALUES (2, STR_TO_DATE("11 7 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (2, 1, 1);
INSERT INTO PurchasedProduct VALUES (2, 3, 3);

INSERT INTO Purchase VALUES (3, STR_TO_DATE("11 15 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (3, 3, 1);

INSERT INTO Purchase VALUES (4, STR_TO_DATE("11 15 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (4, 2, 1);

INSERT INTO Purchase VALUES (5, STR_TO_DATE("11 28 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (5, 3, 20);

INSERT INTO Purchase VALUES (6, STR_TO_DATE("11 28 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (6, 2, 2);

INSERT INTO Purchase VALUES (7, STR_TO_DATE("10 10 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (7, 1, 2);
INSERT INTO PurchasedProduct VALUES (7, 3, 3);

INSERT INTO Purchase VALUES (8, STR_TO_DATE("10 15 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (8, 1, 1);
INSERT INTO PurchasedProduct VALUES (8, 3, 2);

INSERT INTO Purchase VALUES (9, STR_TO_DATE("10 17 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (9, 1, 1);
INSERT INTO PurchasedProduct VALUES (9, 3, 7);

INSERT INTO Purchase VALUES (10, STR_TO_DATE("10 3 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (10, 1, 1);
INSERT INTO PurchasedProduct VALUES (10, 3, 2);

INSERT INTO Purchase VALUES (11, STR_TO_DATE("11 27 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (11, 1, 1);
INSERT INTO PurchasedProduct VALUES (11, 2, 1);
INSERT INTO PurchasedProduct VALUES (11, 3, 1);

INSERT INTO Purchase VALUES (12, STR_TO_DATE("11 28 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (12, 2, 2);

INSERT INTO Purchase VALUES (13, STR_TO_DATE("11 29 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (13, 3, 12);

INSERT INTO Purchase VALUES (14, STR_TO_DATE("11 30 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (14, 1, 3);

INSERT INTO Purchase VALUES (15, STR_TO_DATE("11 26 2022", "%m %e %Y"), null);
INSERT INTO PurchasedProduct VALUES (15, 2, 2);