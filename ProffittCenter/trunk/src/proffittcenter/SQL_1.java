/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

/**
 *
 * @author HP_Owner
 */
public class SQL_1 {

    public static final String agency =
            "SELECT ID FROM Departments WHERE Description=?";
    public static final String totalSQL =
            "INSERT INTO "
            + " Sales(ID,Total,Tax,Cash,Cheque,Debit,Coupon,"
            + "Customer,Waste,Operator,TillId,CustomerOrder,DeliveryAddress)"
            + " VALUES(?,?,?,?,0,0,?,?,?,?,?,?,?)";
    public static final String lastSalesId =
            "SELECT LAST_INSERT_ID() FROM Sales";
    public static final String insertIntoSaleLines = "INSERT INTO SaleLines "
            + "(ID,Sale,Quantity,Product,Price,Track,Encode,pricedOver,discounted,origPrice,taxID,taxRate,wholesalePrice,packSize )"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String updateSku = "UPDATE Skus SET "
            + " Quantity=Quantity-? "
            + " WHERE ID=?";
    public static final String selectProduct =
            "SELECT Products.*,Skus.Tax,Skus.Tax2,Taxes.Rate,Departments.Restriction,"
            + "Skus.Quantity,Skus.StockType "
            + "FROM Products,Skus,Departments,Taxes "
            + "WHERE ((Products.ID=? AND Products.Encoded = 0) "
            + "OR (Products.ID=? AND (Products.Encoded=1 OR Products.Encoded=2)) "
            + "OR (Products.ID=? AND (Products.Encoded=3 OR Products.Encoded=4)) "
            + "OR (Products.ID=? AND (Products.Encoded>4))) "
            + "AND Products.Sku=Skus.ID "
            + "AND Skus.Department=Departments.ID "
            + "AND Skus.Tax=Taxes.ID "
            + "ORDER BY Products.WhenCreated DESC";
    public static final String sumOfCharged = "SELECT SUM(Total-Sales.Debit-Sales.Cheque-Sales.Coupon-Sales.Cash) AS T FROM Sales "
            + "WHERE Customer=? AND Waste=" + SaleType.CHARGED.value();
    public static final String sumOfReceived = "SELECT SUM(Total) AS T FROM Sales "
            + "WHERE Customer=? AND Waste=" + SaleType.RECEIVEDONACCOUNT.value();
    public static final String insertIntoSales = "INSERT INTO "
            + " Sales(ID,Total,Tax,Cash,Cheque,Debit,Coupon,Waste)"
            + " VALUES(?,?,?,0,0,0,0,?)";
    public static final String insertIntoProducts = "INSERT INTO "
            + "Products(ID,Description,Price,Sku)"
            + "VALUES(?,?,?,1)";//1 is the temp value
    public static final String salesByOperator =
            "SELECT Operators.Description AS Name,"
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals By Operator in " + Main.shop.poundSymbol+"' "
            + "FROM Operators,Sales,SaleLines,Products "
            + "WHERE Sales.ID=SaleLines.Sale "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND Sales.Operator=Operators.ID "
            + "AND SaleLines.Product=Products.ID "
            + "AND SaleLines.TaxID <>6 "
            + "GROUP BY Operators.Description ";
    public static final String salesByDepartment =
            "SELECT Departments.Description AS Department,"
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price/100)),2) "
            + "AS Total "
            
//            + "AS 'Totals By Department in " + Main.shop.poundSymbol+"' "
            + "FROM Departments,SaleLines,Sales,Skus,Products "
            + "WHERE "
            + "(Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND SaleLines.Product=Products.ID "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND Skus.ID=Products.Sku "
            + "AND Departments.ID=Skus.Department "
            + "AND SaleLines.TaxID <>6 "
//            + "GROUP BY Departments.Description  "
            + "ORDER BY Departments.Description ";
    public static final String salesByValue =
            "SELECT Products.Description AS Product, "
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Value in " + Main.shop.poundSymbol+"' "
            + "FROM SaleLines,Products,Sales,Skus "
            + "WHERE SaleLines.Product=Products.ID "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND Skus.ID=Products.Sku "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.TaxID <>6 "
            + "GROUP BY Sku "
            + "ORDER BY ROUND(SUM((SaleLines.Quantity*SaleLines.Price)/100),2) DESC";
    public static final String salesByQuantity =
            "SELECT SUM(SaleLines.Quantity) AS Totals,"
            + "Products.Description,"
            + "Departments.Description As Department "
            + "FROM SaleLines,Products,Sales,Skus,Departments "
            + "WHERE SaleLines.Product=Products.ID "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) " +
            "AND Skus.ID=Products.Sku "
            + "AND Departments.ID=Skus.Department "
            + "AND SaleLines.Product=Products.ID "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.TaxID <>6 "
            + "GROUP BY Products.ID,Products.Description,"
            + "Departments.Description ORDER BY "
            + "Departments.Description DESC,totals";
    public static final String salesByAgency =
            "SELECT ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"', "
            + "Products.Description AS Name "
            + "FROM Departments,SaleLines,Products,Sales,Skus "
            + "WHERE SaleLines.Product=Products.ID "
            + "AND Sales.ID=SaleLines.Sale " +
            "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND Skus.ID=Products.Sku "
            + "AND Skus.Department=Departments.ID "
            + "AND Departments.Description='Agency' "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.TaxID =6 "
            + "GROUP BY Products.Description  "
            + "ORDER BY 'Totals in " + Main.shop.poundSymbol+"' ";
    public static final String salesByWastes =
            "SELECT Operators.Description as Operator, "
//            + "ROUND(SUM(Sales.Total/100),2) AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "ROUND(SUM(IF(Products.Encoded=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Operators,Sales,SaleLines,Products "
            + "WHERE   Sales.Waste<>0 "
            + "AND Sales.Waste='" + SaleType.WASTE.valueString() + "' "
            + "AND Operators.ID=Sales.Operator "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND SaleLines.Product=Products.ID "
            + "GROUP BY Sales.ID  "
            + "ORDER BY 'Totals in " + Main.shop.poundSymbol+"' ";
    public static final String salesByLosses =
            "SELECT Operators.Description as Operator, "
            + "-ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Operators,Sales,Products,SaleLines "
            + "WHERE   Sales.Waste<>0 "
            + "AND Sales.Waste='" + SaleType.LOSS.valueString() + "' "
            + "AND Operators.ID=Sales.Operator "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.Product=Products.ID "
            + "AND Sales.ID=SaleLines.Sale "
            + "GROUP BY Sales.ID  "
            + "ORDER BY 'Totals in " + Main.shop.poundSymbol+"' ";
    public static final String salesByReturns =
            "SELECT Operators.Description as Operator, "
//            + "ROUND(SUM(Sales.Total/100),2) AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
           // + "Products.Description AS ProductName "
            + "FROM Operators,Sales,SaleLines,Products "
            + "WHERE   Sales.Waste<>0 "
            + "AND Sales.Waste='" + SaleType.RETURN.valueString() + "' "
            + "AND Operators.ID=Sales.Operator "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.Product=Products.ID "
            + "AND Sales.ID=SaleLines.Sale "
            + "GROUP BY Sales.ID  "
            + "ORDER BY 'Totals in " + Main.shop.poundSymbol+"' ";
    public static final String salesByCharges =
            "SELECT Sales.ID AS Sale, " +
            "CONCAT(Customers.Name2,' ',Customers.Name1) as Name, "
//            + "IF(Encoded=0,Total,Total/1000)/100 AS 'Totals in '"
//            + Main.shop.poundSymbol+"' "
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Customers,Sales,SaleLines,Products "
            + "WHERE  Sales.Waste='" + SaleType.CHARGED.valueString() + "' "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND Customers.ID=Sales.Customer "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.Product=Products.ID "
            + "GROUP BY Sales.ID  "
            + "ORDER BY Sales.ID DESC";
    public static final String salesByReceived =
            "SELECT Sales.ID AS Sale," +
            "CONCAT(Customers.Name2,' ',Customers.Name1) as Name, "
//            + "ROUND(Total/100,2) AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "ROUND(SUM(IF(Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Customers,Sales,SaleLines "
            + "WHERE  Sales.Waste='" + SaleType.RECEIVEDONACCOUNT.valueString() + "' "
            + "AND Customers.ID=Sales.Customer "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "GROUP BY Sales.ID  "
            + "ORDER BY Sales.ID DESC";
    public static final String salesByOwnUse =
            "SELECT Operators.Description as Name, "
           + "ROUND(SUM(IF(Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Operators,SaleLines,Sales,Products "
            + "WHERE Sales.ID=SaleLines.Sale AND Sales.Waste<>0 "
            + "AND Sales.Waste='" + SaleType.OWNUSE.valueString() + "' "
            + "AND Operators.ID=Sales.Operator "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? "
            + "AND SaleLines.Product=Products.ID "
            + "GROUP BY Name,Sales.WhenCreated  "
            + "ORDER BY 'Totals in " + Main.shop.poundSymbol+"' ";
    public static String salesByTax =
            "SELECT Taxes.ID, Taxes.Description AS Tax,"
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals in " + Main.shop.poundSymbol+"' "
            + "FROM Sales "
            + "LEFT JOIN SaleLines ON Sales.ID=SaleLines.Sale "
            + "LEFT JOIN Products ON SaleLines.Product=Products.ID "
            + "LEFT JOIN Skus ON Skus.ID=Products.Sku "
            + "LEFT JOIN Taxes ON Skus.Tax=Taxes.ID "
            + "WHERE   ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=? " + //that is between dates
            "GROUP BY Taxes.ID "
            + "ORDER BY Taxes.ID";
    public static final String alpha =
            "SELECT  Products.ID,Products.Description,"
            + "Products.Price AS Price,"
            + "Skus.Id, "
            + "Skus.Quantity,Products.WhenCreated,Products.Sku "
            + "FROM  Products,Skus "
            + "WHERE  Products.Sku=Skus.ID AND "
            + "(((Products.Description) LIKE ?)  "
            + "OR ((Products.Description) LIKE ?)) "
            + " AND Skus.StockType<>? "
            + "AND Skus.Stopped = FALSE "
            + "ORDER BY Products.Description";
    public static final String balance =
            "SELECT Sales.WhenCreated as D,"
            + "Sales.ID AS  Sale,"
            + "(CASE WHEN(Sales.Waste = "
            + (SaleType.CHARGED.valueString())
            + " ) THEN(Sales.Total-Sales.Debit-Sales.Cheque-Sales.Coupon-Sales.Cash)END)"
            + " AS C,"
            + "(CASE WHEN(Sales.Waste = "
            + (SaleType.RECEIVEDONACCOUNT.valueString())
            + " ) THEN Sales.Total  END) AS R "
            + "FROM Sales "
            + "WHERE Sales.Customer=?  "
            + "AND (Sales.Waste="
            + (SaleType.CHARGED.valueString())
            + " OR Sales.Waste="
            + (SaleType.RECEIVEDONACCOUNT.valueString())
            + ")  ORDER BY Sales.WhenCreated ";
    public static final String balances =
            "SELECT Customers.ID as C,"
            + "Customers.Name1 AS N1,"
            + "Customers.Name2 AS N2,"
            + "SUM(CASE WHEN Sales.Waste = "
            + (SaleType.CHARGED.valueString())
            + " THEN Sales.Total-Sales.Debit-Sales.Coupon-Sales.Cash ELSE 0 END) AS Ch, "
            + "SUM(CASE WHEN(Sales.Waste = "
            + (SaleType.RECEIVEDONACCOUNT.valueString())
            + ") THEN Sales.Total  ELSE 0 END )AS Rc," 
            + "SUM(CASE WHEN Sales.Waste = "
            + (SaleType.CHARGED.valueString())
            + " THEN Sales.Total-Sales.Debit-Sales.Coupon-Sales.Cash ELSE 0 END)- " +
            "SUM(CASE WHEN(Sales.Waste = "
            + (SaleType.RECEIVEDONACCOUNT.valueString())
            + ") THEN Sales.Total  ELSE 0 END ) AS Bal "
            + "FROM Customers, Sales "
            + "WHERE Sales.Customer=Customers.ID "
            + "AND Customers.ID<>10000380000 "
            + "AND (false = ? AND Customers.Account!='Own use' "
            + "OR Customers.Account='Own use') "
            + " GROUP BY Customers.ID "
            + "ORDER BY Name1,Name2";
    public static final String ownUseBalances =
            "SELECT Customers.ID as C,"
            + "Customers.Name1 AS N1,"
            + "Customers.Name2 AS N2,"
            + "SUM(CASE WHEN Sales.Waste = "
            + (SaleType.OWNUSE.valueString())
            + " THEN Sales.Total-Sales.Debit-Sales.Coupon-Sales.Cash ELSE 0 END) AS Ch "
            + "FROM Customers, Sales "
            + "WHERE Sales.Customer=Customers.ID "
            + "AND Customers.ID<>10000380000 "
            + "AND (false = ? AND Customers.Account!='Own use' "
            + "OR Customers.Account='Own use') "
            + " GROUP BY Customers.ID "
            + "ORDER BY Name1,Name2";
    public static final String cashupSales =
            "SELECT Sales.* "
            + "FROM Sales "
            + "WHERE (Sales.TillId=? OR 0=?)  "
            + "AND Sales.WhenCreated>=? "
            + "AND Sales.WhenCreated<=? "
            + "ORDER BY Sales.ID";
    public static final String saveCashupSQL =
            "INSERT INTO "
            + " CashUps(ID,TillID,Operator,Takings,Charged,Agency,"
            + "WhenStarted,WhenCreated,Reconciled,FixedFloat,Received )"
            + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    public static final String lastCashupSQL =
            "SELECT ID,WhenCreated FROM CashUps WHERE TillID=? "
            + "ORDER BY WhenCreated DESC LIMIT 1";
    public static final String lastCashup =
            "SELECT LAST_INSERT_ID() FROM CashUps";
    public static final String byCustomer =
            "SELECT * FROM Customers "
            + "WHERE (Customers.Name1) LIKE(?) "
            + "AND ID<>'10000380000' "
            + "AND (Stop<>? OR Stop=false) "
            + "AND Customers.Account<>'Own use' "
            + "ORDER BY ";
    public static final String isCustomer =
            "SELECT * FROM Customers WHERE ID=?";
    static String departments = "SELECT D.description AS "
            + "departmentDescription,"
            + "D.id AS department,D.tax,D.Margin,D.Minimum,D.Restriction,"
            + "UPPER(D.description) "
            + "AS UD, T.* "
            + " FROM Departments AS D ,Taxes AS T "
            + "WHERE D.Tax=T.ID";
    static String productsSQL = "SELECT Products.ID,"
            + "Products.Description,UPPER(Products.Description) AS PD,"
            + "Products.WhenCreated,Products.Price,Products.Sku,"
            + "Departments.Description,Skus.Tax,Skus.Quantity,"
            + "Taxes.* "
            + "FROM Products,Skus,Departments,Taxes "
            + "WHERE Products.Sku=Skus.ID AND Taxes.ID=Skus.Tax "
            + "AND Skus.Department=Departments.ID ";
    static String dp = "ORDER BY Departments.Description,PD ";
    static String bc = " ORDER BY Products.ID";
    static String pd = " ORDER BY PD";
    static String dt = " ORDER BY WhenCreated,PD  ";
    static String pr = " ORDER BY Products.Price ";
    static String aSale = "SELECT Sales.*,MAX(Sales.ID) AS "
            + "Last,Operators.Description,C.Name1,C.Name2, "
            + "c.Discount AS Discount "
            + "FROM Sales,Operators,Customers AS C "
            + "WHERE Sales.ID<? "
            + "AND (Sales.TillId=? OR 0=?) "
            + "AND (Sales.Customer=? OR 0=?) "
            + "AND (Sales.ID=? OR 0=?) "
            + "AND (Sales.Operator=Operators.ID "
            + "OR Sales.Operator=0"
            + " ) AND Sales.Customer=C.ID "
            + "GROUP BY Sales.ID "
            + "ORDER BY Sales.ID DESC LIMIT 1";
    static String dSale = "SELECT Sales.*,MAX(Sales.ID) AS Last,"
            + "Operators.Description,C.Name1,C.Name2  "
            + "FROM Sales,Operators,Customers AS C "
            + "WHERE Sales.ID<? "
            + "AND (Sales.TillId=? OR 0=?) "
            + "AND (Sales.Customer=? OR 0=?) "
            + "AND (Sales.Operator=Operators.ID OR Sales.Operator=0"
            + ") AND Sales.Customer=C.ID "
            + "GROUP BY Sales.ID "
            + "ORDER BY Sales.ID DESC LIMIT 1";
    static String uSale = "SELECT Sales.*,MIN(Sales.ID) AS First,"
            + "Operators.Description,C.Name1,C.Name2 "
            + "FROM Sales,Operators,Customers AS C "
            + "WHERE Sales.ID>? "
            + "AND (Sales.TillId=? OR 0=?) "
            + "AND (Sales.Customer=? OR 0=?) "
            + "AND (Sales.Operator=Operators.ID OR Sales.Operator=0"
            + ") AND Sales.Customer=C.ID "
            + "GROUP BY Sales.ID "
            + "ORDER BY Sales.Id LIMIT 1";
    static String saleLines = "SELECT SaleLines.*,"
            + "Products.Description,Products.Encoded, "
            + "Taxes.Rate "
            + "FROM SaleLines,Products,Skus,Taxes "
            + "WHERE SaleLines.Sale=? "
            + "AND SaleLines.Product=Products.ID "
            + "AND Products.Sku=Skus.ID "
            + "AND Skus.Tax=Taxes.ID "
            + "ORDER BY SaleLines.ID  ";
    static String sales = "SELECT S.ID,S.TillId AS Till,"
            + " S.WhenCreated,"
            + "O.Description AS OperatorName,"
            + "S.Total,S.Tax,S.Cash,S.Debit,S.Cheque,S.Coupon,"
            + "IF (S.Waste=0,'Normal', "
            + "IF (S.Waste=1,'Waste', "
            + "IF (S.Waste=2,'Return',"
            + "IF (S.Waste=3,'Loss', "
            + "IF (S.Waste=4,'Charged',"
            + "IF (S.Waste=5,'Received',"
            + "IF (S.Waste=6,'Own use', "
            + "IF (S.Waste=11,'Delivery', "
            + "IF (S.Waste=12,'No sale!','')))))))))AS Type,"
            + "C.Name1,C.Name2 "
            + "FROM Customers AS C,Sales AS S, "
            + "Operators AS O  "
            + "WHERE "
            + "S.Operator=O.ID AND"
            + "  S.Customer=C.ID "
            + "AND (S.TillID=? OR 0=? ) "
            + "AND S.WhenCreated >= ? "
            + "AND DATE(S.WhenCreated) <= ? " +
            "ORDER BY S.ID DESC LIMIT ? ";
    static String byDepartment = " ORDER BY UD ";
    static String byDepartmentID = " ORDER BY Department ";
    static String byTax = " ORDER BY D.Tax ";
    static String byMargin = "ORDER BY D.Margin ";
    static String ByMinimum = " ORDER BY D.Minimum ";
    static String ByRestriction = " ORDER BY D.Restriction ";
    static String paidOutTransactions = "SELECT PaidOuts.* "
            + "FROM PaidOuts "
            + "WHERE (PaidOuts.TillId=? OR 0=?)  "
            + "AND PaidOuts.WhenCreated>=? "
            + "AND PaidOuts.WhenCreated<=? "
            + "ORDER BY PaidOuts.ID";
    static String taxes = "SELECT * FROM TAXES ";
    static String tax = "SELECT * FROM Taxes WHERE ID=?";
    static String lastDelivered = "SELECT Packs.ID AS ID,Price,Size FROM Packs,PackSuppliers "
                            + "WHERE Packs.ID = PackSuppliers.Pack "
                            + "AND Packs.Product=? "
                            + "ORDER BY PackSuppliers.WhenCreated DESC ";
    static String proffittSQL= "SELECT Quantity,Price,wholesalePrice,packSize,"
            + "taxRate,Encode "
            + "FROM SaleLines "
            + "WHERE Sale=? ";
    static String byOwnUseCustomer =
             "SELECT * FROM Customers WHERE (Customers.Name1) "
            + "LIKE(?)AND ID<>'10000380000' AND (Stop<>? OR Stop=false) "
            + "AND Account = 'Own use' "
            + "ORDER BY ";
    static String ownUseBalance  =
            "SELECT Sales.WhenCreated as D,"
            + "Sales.ID AS  Sale,"
            + "(CASE WHEN(Sales.Waste = "
            + (SaleType.OWNUSE.valueString())
            + " ) THEN(Sales.Total)END)"
            + " AS C "
            + "FROM Sales "
            + "WHERE Sales.Customer=?  "
            + "AND (Sales.Waste="
            + (SaleType.OWNUSE.valueString())
            + ")  ORDER BY Sales.WhenCreated ";
    static String offerLookup = " SELECT IX,X,Y,OfferType,LimitValue FROM Offers WHERE Product=? ";
    static String salesByDiscounted=
            "SELECT DATE(Sales.WhenCreated) AS 'Date',"
            + "CONCAT(Customers.Name2,' ',Customers.Name1) AS Name,"
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals by discounted in " + Main.shop.poundSymbol+"' "
            + "FROM Sales,SaleLines,Customers "
            + "WHERE Sales.ID=SaleLines.Sale "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=DATE(?) "
            + "AND SaleLines.TaxID <>6 "
            + "AND SaleLines.discounted >0 "
            + "AND Sales.Customer=Customers.ID "
            + "GROUP BY Customers.Name1,DATE(Sales.WhenCreated) "
            + "ORDER BY Sales.ID ";
    static String salesByPricedOver=
            "SELECT DATE(Sales.WhenCreated) AS 'Date',"
            + "CONCAT(Customers.Name2,' ',Customers.Name1) AS Name,"
            + "ROUND(SUM(IF(SaleLines.Encode=2, "//encoded by weight
            + "SaleLines.Quantity*SaleLines.Price/1000, "
            + "SaleLines.Quantity*SaleLines.Price)/100),2) "
            + "AS 'Totals by discounted in " + Main.shop.poundSymbol+"' "
            + "FROM Sales,SaleLines,Customers "
            + "WHERE Sales.ID=SaleLines.Sale "
            + "AND ((Sales.Waste= "
            + SaleType.NORMAL.value() + ")OR(Sales.Waste=   "
            + SaleType.CHARGED.value() + ")) "
            + "AND (Sales.WhenCreated)>=? "
            + "AND (DATE(Sales.WhenCreated))<=DATE(?) "
            + "AND SaleLines.TaxID <>6 "
            + "AND SaleLines.pricedOver >0 "
            + "AND Sales.Customer=Customers.ID "
            + "GROUP BY Customers.Name1,DATE(Sales.WhenCreated) "
            + "ORDER BY Sales.ID ";

    private SQL_1() {
    }
   
}
