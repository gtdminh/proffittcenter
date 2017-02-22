SELECT OL.*,P.*,D.Description,Taxes.Rate,Taxes.Description,S.Quantity 
FROM OrderLines AS OL,Products AS P,Departments AS D,Skus AS S, Taxes 
WHERE OrderNO=15 AND OL.Product=P.ID AND OL.Department=D.ID AND P.Sku=S.ID AND S.Tax=Taxes.ID 
ORDER BY OL.ID