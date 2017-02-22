SELECT
Sales.ID AS Sale,DATE(Sales.WhenCreated) AS Date,
FORMAT(if(MONTH(Sales.WhenCreated)='1' AND
 (YEAR(Sales.WhenCreated)=YEAR(CURDATE()) AND '1'<=MONTH(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),Sales.Total/100,0),2) AS Total,
FORMAT(if(MONTH(Sales.WhenCreated)='1' AND
 (YEAR(Sales.WhenCreated)=YEAR(CURDATE()) AND '1'<=MONTH(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),Sales.Tax/100,0),2) AS Tax,
(CURDATE()) AS now,
 Sales.Waste AS Waste,
 Customers.Name1 AS N,
if(Sales.Waste=0 AND (MONTH(Sales.WhenCreated)='1'  AND
 YEAR(Sales.WhenCreated)=YEAR(CURDATE()) AND '1'<=MONTH(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),Sales.Total,
 if(Sales.Waste=4 AND (MONTH(Sales.WhenCreated)='1'  AND
 YEAR(Sales.WhenCreated)=YEAR(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),
 Sales.Total,0)) AS purchase,

 if(Sales.Waste=4 AND MONTH(Sales.WhenCreated)='1'  AND
 (YEAR(Sales.WhenCreated)=YEAR(CURDATE()) AND '1'<=MONTH(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),Sales.Total-Sales.Debit-Sales.Coupon,0) AS charge,

 if(Sales.Waste=5 AND MONTH(Sales.WhenCreated)='1'  AND
 (YEAR(Sales.WhenCreated)=YEAR(CURDATE()) AND '1'<=MONTH(CURDATE())
 OR (YEAR(Sales.WhenCreated)=YEAR(CURDATE())-1 AND
 '1'>MONTH(CURDATE()))),Sales.Total-Sales.Debit-Sales.Coupon,0) AS received,

(if(Sales.Waste=4 AND (MONTH(Sales.WhenCreated) <MONTH(CURDATE())+1 OR
YEAR(Sales.WhenCreated)=YEAR(CURDATE())),
Sales.Total-Sales.Debit-Sales.Coupon,0)
-if(Sales.waste=5 AND (MONTH(Sales.WhenCreated) <MONTH(CURDATE())+1 AND
YEAR(Sales.WhenCreated)=YEAR(CURDATE()) OR
YEAR(Sales.WhenCreated)=YEAR(CURDATE())),Sales.Total,0)) AS Balance,

 @fValue:=if(Sales.Waste=0,'Cash',if(Sales.Waste=4,'Charged',
 if(Sales.Waste=5,'Received on account','*'))) AS Type,
Sales.Customer AS Customer
 FROM Sales
LEFT JOIN Customers ON Customers.ID=Sales.Customer
 WHERE (Sales.Waste = 4 OR Sales.Waste = 5 OR Sales.Waste = 0)
 AND Customer <> 10000380000
ORDER BY Customers.Name1,Customers.Name2,Sales.WhenCreated,Sales.Waste