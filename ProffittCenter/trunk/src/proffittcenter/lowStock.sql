SELECT Skus.Quantity,Skus.id AS SKU,
(SELECT Products.Description FROM Products WHERE Products.Sku=Skus.ID
AND Products.Description IS NOT NULL
ORDER BY Products.WhenCreated DESC LIMIT 1)AS Description
FROM Skus
JOIN Products ON Products.Sku=Skus.ID
WHERE Skus.ID<>1 AND Skus.ID<>2