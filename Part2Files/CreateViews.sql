use PIZZAS_R_US;
CREATE OR REPLACE VIEW ToppingPopularity AS SELECT topping.ToppingName as Topping, 
count(pizzatopping.PizzatoppingToppingID) + sum(pizzatopping.PizzatoppingIsDouble) as ToppingCount
from pizzatopping 
right join topping on pizzatopping.PizzatoppingToppingID = topping.ToppingID
group by topping.ToppingName
order by ToppingCount desc;

create or replace view ProfitByPizza AS select baseprice.BasepriceSize as 'Pizza Size',
baseprice.BasePriceCrustType as 'Pizza Crust',
round(sum(pizza.PizzaPriceToCustomer-pizza.PizzaPriceToBusiness), 2) as 'Profit',
date_format(max(ordert.OrdertTimeStamp), '%M-%e-%Y') as 'LastOrderDate' from baseprice 
join pizza on baseprice.BasepriceSize = pizza.PizzaSize and baseprice.BasepriceCrustType = pizza.PizzaCrustType
join ordert on pizza.PizzaOrderID = ordert.OrdertID
group by baseprice.BasePriceSize, baseprice.BasepriceCrustType
order by Profit desc;


CREATE OR REPLACE VIEW ProfitByOrderType AS
SELECT OrdertType AS CustomerType, DATE_FORMAT (OrdertTimeStamp, '%Y-%M') AS OrderMonth ,
SUM(OrdertPriceToCustomer) AS TotalOrderPrice, SUM(OrdertPriceToBusiness) AS TotalOrderCost, SUM(OrdertPriceToCustomer)-SUM(OrdertPriceToBusiness) AS Profit FROM ordert GROUP BY CustomerType,OrderMonth
UNION
SELECT '', 'Grand Total',SUM(OrdertPriceToCustomer), SUM(OrdertPriceToBusiness), SUM(OrdertPriceToCustomer)-SUM(OrdertPriceToBusiness) FROM ordert;

