DROP DATABASE IF EXISTS PIZZAS_R_US;
CREATE DATABASE PIZZAS_R_US; 
USE PIZZAS_R_US;
CREATE TABLE customer
(
	CustomerID int AUTO_INCREMENT,
    CustomerFirstName varchar(255) NOT NULL,
    CustomerLastName varchar(255) NOT NULL,
    CustomerPhone varchar(255) NOT NULL,
    PRIMARY KEY(CustomerID)    
);


CREATE TABLE ordert
(
	OrdertID int AUTO_INCREMENT,
    OrdertCustomerID int NOT NULL,
    OrdertTimeStamp varchar(255) NOT NULL,
    OrdertPriceToCustomer decimal(6,2) NOT NULL,
    OrdertPriceToBusiness decimal(6,2) NOT NULL,
    OrdertType varchar(255) NOT NULL,
    IsCompleted boolean NOT NULL,
    PRIMARY KEY(OrdertID),
    FOREIGN KEY(OrdertCustomerID) REFERENCES customer(CustomerID)
);


CREATE TABLE dinein
(
	DineinOrdertID int,
    DineinTableNumber int NOT NULL,
    PRIMARY KEY(DineinOrdertID),
    FOREIGN KEY(DineinOrdertID) REFERENCES ordert(OrdertID)
);

CREATE TABLE pickup
(
	PickupOrdertID int,
    PRIMARY KEY(PickupOrdertID),
    FOREIGN KEY(PickupOrdertID) REFERENCES ordert(OrdertID)
);

CREATE TABLE delivery
(
	DeliveryOrdertID int,
    DeliveryAddress varchar(255) NOT NULL,
    PRIMARY KEY(DeliveryOrdertID),
    FOREIGN KEY(DeliveryOrdertID) REFERENCES ordert(OrdertID)
);

CREATE TABLE discount
(
	DiscountID int AUTO_INCREMENT,
    DiscountName varchar(255) NOT NULL,
    IsPercent  boolean NOT NULL,
    DiscountValue decimal(6,2) NOT NULL,
    PRIMARY KEY(DiscountID)
);

CREATE TABLE discountorder
(
	DiscountorderOrdertID int,
    DiscountorderDiscountID int,
    PRIMARY KEY(DiscountorderOrdertID, DiscountorderDiscountID),
    FOREIGN KEY(DiscountorderOrdertID) REFERENCES ordert(OrdertID),
    FOREIGN KEY(DiscountorderDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE baseprice
(
	BasepriceCrustType varchar(255),
    BasepriceSize varchar(255),
    BasepriceToCustomer decimal(6,2) NOT NULL,
    BasepriceToBusiness decimal(6,2) NOT NULL,
    PRIMARY KEY(BasepriceCrustType, BasepriceSize)
);

CREATE TABLE pizza
(
	PizzaID int AUTO_INCREMENT,
    PizzaOrderID int NOT NULL,
    PizzaPriceToBusiness decimal(6,2) NOT NULL,
    PizzaPriceToCustomer decimal(6,2) NOT NULL,
    PizzaState varchar(255) NOT NULL, 
    PizzaCrustType varchar(255) NOT NULL,
    PizzaSize varchar(255) NOT NULL,
    PRIMARY KEY(PizzaID),
    FOREIGN KEY(PizzaOrderID) REFERENCES ordert(OrdertID),
    FOREIGN KEY(PizzaCrustType, PizzaSize) REFERENCES baseprice(BasepriceCrustType, BasepriceSize)
);

CREATE TABLE pizzadiscount
(
	PizzadiscountPizzaId int,
    PizzadiscountDiscountID int,
    PRIMARY KEY(PizzadiscountPizzaId, PizzadiscountDiscountID),
    FOREIGN KEY(PizzadiscountPizzaId) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzadiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE topping
(
	ToppingID int auto_increment,
    ToppingName varchar(255) NOT NULL,
    ToppingPriceToCustomer decimal(6,2) NOT NULL,
    ToppingPriceToBusiness decimal(6,2) NOT NULL,
    ToppingQuantityForPersonal decimal(6,2) NOT NULL,
    ToppingQuantityForMedium decimal(6,2) NOT NULL,
    ToppingQuantityForLarge decimal(6,2) NOT NULL,
    ToppingQuantityForXLarge decimal(6,2) NOT NULL,
    ToppingCurrentInvLvl decimal(6,2) NOT NULL,
    ToppingMinInvLvl decimal(6,2),
    PRIMARY KEY(ToppingID)
);

CREATE TABLE pizzatopping
(
	PizzatoppingPizzaID int,
    PizzatoppingToppingID int,
    PizzatoppingIsDouble boolean,
    PRIMARY KEY(PizzatoppingPizzaID, PizzatoppingToppingID),
    FOREIGN KEY(PizzatoppingPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzatoppingToppingID) REFERENCES topping(ToppingID) 
);