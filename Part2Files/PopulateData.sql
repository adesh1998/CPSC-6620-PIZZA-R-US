USE PIZZAS_R_US;
INSERT INTO topping
(
ToppingName, 
ToppingPriceToCustomer, 
ToppingPriceToBusiness, 
ToppingMinInvLvl,
ToppingCurrentInvLvl, 
ToppingQuantityForPersonal,
ToppingQuantityForMedium,
ToppingQuantityForLarge,
ToppingQuantityForXLarge
)
VALUES('Pepperoni', 1.25, 0.2, 100, 100, 2, 2.75, 3.5, 4.5),
 ('Sausage', 1.25, 0.15, 100, 100, 2.5, 3, 3.5, 4.25),
 ('Ham', 1.5, 0.15, 78, 78, 2, 2.5, 3.25, 4),
 ('Chicken', 1.75, 0.25, 56, 56, 1.5, 2, 2.25, 3),
 ('Green Pepper', 0.5, 0.02, 79, 79, 1, 1.5, 2, 2.5),
 ('Onion', 0.5, 0.02, 85, 85, 1, 1.5, 2, 2.75),
 ('Roma Tomato', 0.75, 0.03, 86, 86, 2, 3, 3.5, 4.5),
 ('Mushrooms', 0.75, 0.1, 52, 52, 1.5, 2, 2.5, 3),
 ('Black Olives', 0.6, 0.1, 39, 39, 0.75, 1, 1.5, 2),
 ('Pineapple', 1, 0.25, 15, 15, 1, 1.25, 1.75, 2),
 ('Jalapenos', 0.5, 0.05, 64, 64, 0.5, 0.75, 1.25, 1.75),
 ('Banana Peppers', 0.5, 0.05, 36, 36, 0.6, 1, 1.3, 1.75),
 ('Regular Cheese', 1.5, 0.12, 250, 250, 2, 3.5, 5, 7),
 ('Four Cheese Blend', 2, 0.15, 150, 150, 2, 3.5, 5, 7),
 ('Feta Cheese', 2, 0.18, 75, 75, 1.75, 3, 4, 5.5),
 ('Goat Cheese', 2, 0.2, 54, 54, 1.6, 2.75, 4, 5.5),
 ('Bacon', 1.5, 0.25, 89, 89, 1, 1.5, 2, 3);
 
 INSERT INTO discount(DiscountName, IsPercent, DiscountValue) 
VALUES("Employee", 1, 15), 
 ("Lunch Special Medium", 0, 1),
 ("Lunch Special Large", 0, 2),
 ("Specialty Pizza", 0, 1.50),
 ("Gameday Special", 1, 20);

INSERT INTO baseprice(BasepriceSize,BasepriceCrustType,BasepriceToCustomer,BasepriceToBusiness)
VALUES('small', 'Thin', 3, 0.5),
('small', 'Original', 3, 0.75),
('small', 'Pan', 3.5, 1),
('small', 'Gluten-Free', 4, 2),
('medium', 'Thin', 5, 1),
('medium', 'Original', 5, 1.5),
('medium', 'Pan', 6, 2.25),
('medium', 'Gluten-Free', 6.25, 3),
('large', 'Thin', 8, 1.25),
('large', 'Original', 8, 2),
('large', 'Pan', 9, 3),
('large', 'Gluten-Free', 9.5, 4),
('x-large', 'Thin', 10, 2),
('x-large', 'Original', 10, 3),
('x-large', 'Pan', 11.5, 4.5),
('x-large', 'Gluten-Free', 12.5, 6);

insert into customer(CustomerFirstName, CustomerLastName, CustomerPhone) 
values('a','b','1234567890'),
('a','b','1234567890'),
('Ellis', 'Beck', '8642545861'),
('Kurt', 'McKinney', '8644749953'),
('Calvin', 'Sanders', '8642328944'), 
('Lance', 'Benton', '8648785679');

insert into ordert(OrdertCustomerID, OrdertTimeStamp, OrdertPriceToCustomer, OrdertPriceToBusiness, OrdertType,IsCompleted) 
values(1, '2023-03-05 12:03:00', 13.50, 3.68, 'dinein',1),
(2, '2023-03-03 12:05:00', 17.35, 4.63, 'dinein',1),
(3, '2023-03-03 21:30:00', 64.50, 19.8, 'pickup',1),
(3, '2023-03-05 19:11:00', 45.5, 16.86, 'delivery',1),
(4, '2023-03-02 17:30:00', 16.85, 7.85, 'pickup',1),
(5, '2023-03-02 18:17:00', 13.25, 3.20, 'delivery',1),
(6, '2023-03-06 20:32:00', 24, 6.3, 'delivery',1);

insert into discountorder values(1, 3), (4, 5), (7, 1);

insert into pizza(PizzaOrderID, PizzaPriceToCustomer, PizzaPriceToBusiness, PizzaState, PizzaSize,PizzaCrustType) 
values(1, 13.50, 3.68, "Completed", 'large', 'Thin'), 
(2, 10.60, 3.23, "Completed", 'medium', 'Pan'),
(2, 6.75, 1.40, "Completed", 'small', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(3, 10.75, 3.30, "Completed", 'large', 'Original'),
(4, 14.50, 5.59, "Completed", 'x-large', 'Original'),
(4, 17, 5.59, "Completed", 'x-large', 'Original'),
(4, 14.00, 5.68, "Completed", 'x-large', 'Original'),
(5, 16.85, 7.85, "Completed", 'x-large', 'Gluten-Free'),
(6, 13.25, 3.20, "Completed", 'large', 'Thin'),
(7, 12, 3.75, "Completed", 'large', 'Thin'),
(7, 12, 2.55, "Completed", 'large', 'Thin');

insert into pizzadiscount values(2, 2), (3, 4), (11, 4), (13, 4); 

insert into dinein values(1, 14), (2, 4);

insert into pickup values(3),(5);

insert into delivery 
values(4, '115 Party Blvd, Anderson SC 29621'),
(6, '6745 Wessex St Anderson SC 29621'),
(7, '8879 Suburban Home, Anderson, SC 29621');

insert into pizzatopping values(1, 13, 1), (1, 1, 0), (1, 2, 0), 
(2, 15, 0), (2, 9, 0), (2, 7, 0), (2, 8, 0), (2, 12, 0), 
(3, 13, 0), (3, 4, 0), (3, 12, 0), 
(4, 13, 0), (4, 1, 0), 
(5, 13, 0), (5, 1, 0),
(6, 13, 0), (6, 1, 0),
(7, 13, 0), (7, 1, 0),
(8, 13, 0), (8, 1, 0),
(9, 13, 0), (9, 1, 0),
(10, 1, 0), (10, 2, 0),(10,14,0),
(11, 3, 1), (11, 10, 1),(11,14,0),
(12, 11, 0), (12, 17, 0),(12,14,0),
(13, 5, 0), (13, 6, 0), (13, 7, 0), (13, 8, 0), (13, 9, 0), (13, 16, 0),
(14, 4, 0), (14, 5, 0), (14, 6, 0), (14, 8, 0), (14, 14, 1),
(15, 14, 1),
(16, 13, 0), (16, 1, 1);

















