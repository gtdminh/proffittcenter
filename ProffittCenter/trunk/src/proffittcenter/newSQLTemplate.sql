SELECT Customers.ID as C,
            Customers.Name1 AS N1,
            Customers.Name2 AS N2,
            SUM(CASE WHEN Sales.Waste =
            '1000035'
             THEN Sales.Total-Sales.Debit-Sales.Coupon ELSE 0 END) AS Ch,
            SUM(CASE WHEN(Sales.Waste =
            '1000036'
            ) THEN Sales.Total  ELSE 0 END )AS Rc
            FROM Customers, Sales
            WHERE Sales.Customer=Customers.ID
             GROUP BY Customers.ID
             ORDER BY Customers.Name1;