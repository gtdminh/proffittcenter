SELECT
 FORMAT((SaleLines.Price*SaleLines.Quantity)/100,2) AS NetPrice,
SaleLines.Quantity AS Qty,
                   Products.Description AS ProductDescription,
Products.Per AS Per,
                   FORMAT(Products.Price/100,2) AS ListPrice,
                   FORMAT(Taxes.Rate/10,1) AS Tax,
FORMAT(Taxes.Rate,2) AS Vat ,
Products.Price as t,
-SaleLines.Price as u ,
-SaleLines.Price+CAST(Products.Price AS SIGNED) as v
if (Products.Price=0, 0,FORMAT(FLOOR((CAST(Products.Price AS SIGNED)-SaleLines.Price)*100/Products.Price+.5),0)) AS Discount2
                   FROM SaleLines
                   INNER JOIN Products ON SaleLines.Product=Products.ID
                   INNER JOIN Skus ON Products.Sku=Skus.ID
                   INNER JOIN Taxes ON Skus.Tax=Taxes.ID
                   WHERE SaleLines.Sale='31631'