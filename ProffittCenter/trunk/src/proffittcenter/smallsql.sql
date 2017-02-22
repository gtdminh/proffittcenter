SELECT Skus.Quantity,Skus.ID AS SKU,
(SELECT Products.Description FROM PRODUCTS
WHERE Products.Sku=Skus.ID
ORDER BY Products.WhenCreated DESC LIMIT 1)AS Description,
Suppliers.Description AS Supplier
FROM Skus,Suppliers
WHERE Skus.ID<>1 AND Skus.ID<>2
AND Skus.Quantity < 2
AND Skus.Supplier=3
AND Skus.Supplier=Suppliers.ID
AND CHAR_LENGTH(Description)<>0