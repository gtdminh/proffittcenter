/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author HP_Owner
 */
//Used to add fields to an existing database
public class DatabaseUpdate {

    Preferences root = Preferences.userNodeForPackage(getClass());
    private long product;
    private int size;
    private int pack;
    private int price;
    private int supplier;
    private int margin;
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/DatabaseCreate");
    private PreparedStatement createCustomerMessages;
    private String usl = "ALTER TABLE `salelines` "
            + "ADD COLUMN `Encode`  TINYINT(1)  NOT NULL DEFAULT 0 AFTER `Track`,"
//            + "ADD COLUMN `pricedOver`  tinyint(1) NOT NULL DEFAULT 0 AFTER `Encode`,"
//            + "ADD COLUMN `discounted`  tinyint(1) NOT NULL DEFAULT 0 AFTER `pricedOver`,"
            + "ADD COLUMN `origPrice`  int(11) NOT NULL DEFAULT 0 AFTER `discounted`,"
            + "ADD COLUMN `taxID`  int(5) UNSIGNED DEFAULT 1 NOT NULL AFTER `origPrice`,"
            + "ADD COLUMN `taxRate`  int(5) UNSIGNED NOT NULL  DEFAULT 0 AFTER `taxID`,"
            + "ADD COLUMN `wholesalePrice`  int(11) NOT NULL DEFAULT 0 AFTER `taxRate`,"
            + "ADD COLUMN `packSize`  int(11) NOT NULL DEFAULT 1 AFTER `wholesalePrice`,"
            + "ADD INDEX `TaxID` USING BTREE (`taxID`) ";
//            + " ADD CONSTRAINT `salelines_ibfk_3` FOREIGN KEY (`taxID`) REFERENCES `taxes` (`ID`)";
    private final String sdsl = "UPDATE SaleLines SET wholesalePrice = "
            + " (SELECT PackSuppliers.Price FROM Packs,PackSuppliers "
            + "WHERE PackSuppliers.Pack=Packs.ID AND "
            + "Packs.Product=SaleLines.Product "
            + "ORDER BY PackSuppliers.WhenCreated DESC LIMIT 1) "
            + ""
            + " , PackSize= (SELECT Packs.Size FROM Packs,PackSuppliers WHERE Packs.Product=SaleLines.Product "
            + "AND PackSuppliers.Pack=Packs.ID "
            + "ORDER BY PackSuppliers.WhenCreated DESC LIMIT 1) "
            + "";
    private double version;
    private PreparedStatement updates;

    public void execute() {
        version = root.getDouble("Version", 0);
        if (version < Main.about.version) {
            try {                                                                                                                                                                                                                                                                            String queryPer = "SHOW COLUMNS FROM Products LIKE 'Per' ";
                PreparedStatement ps = Main.getConnection().prepareStatement(queryPer);
                ResultSet rs = ps.executeQuery();
                if (!rs.first()) {//field does not exist
                    rs.close();
                    String addPer = "ALTER TABLE Products ADD COLUMN "
                            + "Per varchar(5) NOT NULL default ' ' "
                            + "COMMENT 'unit of sale' AFTER Price";
                    ps = Main.getConnection().prepareStatement(addPer);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                String querySku = "UPDATE Skus SET StockType=6 WHERE StockType>6";
                ps = Main.getConnection().prepareStatement(querySku);
                ps.execute();
                //CustomerOrder
                String queryCustomerOrder = "SHOW COLUMNS FROM Sales LIKE 'CustomerOrder' ";
                ps = Main.getConnection().prepareStatement(queryCustomerOrder);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String addCustomerOrder = "ALTER TABLE Sales ADD COLUMN "
                            + " `CustomerOrder` varchar(50) "
                            + "collate utf8_unicode_ci NOT NULL DEFAULT ' ' COMMENT "
                            + "'Customers own code' "
                            + "AFTER TillId";
                    ps = Main.getConnection().prepareStatement(addCustomerOrder);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                //Customers this generates an error, sometimes
                String queryCustomers = "SHOW COLUMNS FROM Customers LIKE 'Creditlimit' ";
                ps = Main.getConnection().prepareStatement(queryCustomers);
                rs = ps.executeQuery();//error
                if (!rs.first()) {
                    rs.close();
                    String addCreditlimit = "ALTER TABLE Customers ADD COLUMN "
                            + " `Creditlimit` int(11)  NOT NULL DEFAULT '0' COMMENT "
                            + "'Maximum credit in pounds'  "
                            + "AFTER Stop";
                    ps = Main.getConnection().prepareStatement(addCreditlimit);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                //cashups
                String queryCashups = "SHOW COLUMNS FROM CashUps LIKE 'TomorrowsFloat' ";
                ps = Main.getConnection().prepareStatement(queryCashups);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String extendCashups = "ALTER  TABLE CashUps  "
                            + "ADD COLUMN `TomorrowsFloat` int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0' , "
                            + "ADD COLUMN Notes50 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes20 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes10 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes5 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Bags20 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Bags10 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Bags5 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Bags1 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose200 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose100 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose50 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose25 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose20 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose10 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose5 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose2 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose1 int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes20F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes10F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes5F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose200F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose100F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose50F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose25F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose20F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose10F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose5F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose2F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Loose1F int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Error int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN DebitsError int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN ChequeError int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN CouponsError int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN ReceivedError int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN PaidOutError int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Reconciled BOOL NOT NULL DEFAULT FALSE ";
                    ps = Main.getConnection().prepareStatement(extendCashups);
                    ps.execute();
                    ps.close();
                } else {
                    rs.close();
                }
                String queryCashups2 = "SHOW COLUMNS FROM CashUps LIKE 'Reconciled' ";
                ps = Main.getConnection().prepareStatement(queryCashups2);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String extendCashups2 = "ALTER  TABLE CashUps  "
                            + "ADD COLUMN Reconciled smallint(3) collate utf8_unicode_ci NOT NULL DEFAULT '0' ";
                    ps = Main.getConnection().prepareStatement(extendCashups2);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                //cashups
                String queryCashupsAgain = "SHOW COLUMNS FROM CashUps LIKE 'Notes200' ";
                ps = Main.getConnection().prepareStatement(queryCashupsAgain);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String extendCashupsAgain = "ALTER  TABLE CashUps  "
                            + "ADD COLUMN Notes200 int(11) NOT NULL DEFAULT '0', "
                            + "ADD COLUMN Notes100 int(11) NOT NULL DEFAULT '0' ";
                    ps = Main.getConnection().prepareStatement(extendCashupsAgain);
                    ps.executeUpdate();
                    ps.close();
                }
                String queryCashups3 = "SHOW COLUMNS FROM CashUps LIKE 'Received' ";
                ps = Main.getConnection().prepareStatement(queryCashups3);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String extendCashups2 = "ALTER  TABLE CashUps  "
                            + "ADD COLUMN Received int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0' ";
                    ps = Main.getConnection().prepareStatement(extendCashups2);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                String querySalesAgain = "SHOW COLUMNS FROM Sales LIKE 'DeliveryAddress' ";
                ps = Main.getConnection().prepareStatement(querySalesAgain);
                rs = ps.executeQuery();
                if (rs.first()) {
                    rs.close();
                    String addDeliveryAddress = "ALTER TABLE Sales DROP COLUMN "
                            + "   `DeliveryAddress` ";
                    ps = Main.getConnection().prepareStatement(addDeliveryAddress);
                    ps.execute();
                    ps.close();
                }
                rs.close();
                PreparedStatement deliveryAddresses = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `DeliveryAddresses` "
                        + "(`Sale` INT(10) unsigned NOT NULL,"
                        + " `CustomerOrder` varchar(50) "
                        + "collate utf8_unicode_ci NOT NULL DEFAULT ' ' COMMENT "
                        + "'Customers own code',"
                        + "`Name1` varchar(30) collate utf8_unicode_ci default "
                        + "NULL,  "
                        + "`PostCode` varchar(10) collate utf8_unicode_ci default "
                        + "NULL,  `Address1` varchar(30) collate "
                        + "utf8_unicode_ci default NULL,`Address2` varchar(30) "
                        + "collate utf8_unicode_ci default NULL,`Address3` "
                        + "varchar(30) collate utf8_unicode_ci default NULL,`Town` "
                        + "varchar(20) collate utf8_unicode_ci default NULL,"
                        + "`County` varchar(20) collate utf8_unicode_ci default "
                        + "NULL,"
                        + "`Name2` varchar(30) collate utf8_unicode_ci default NULL,"
                        + "PRIMARY KEY  (`Sale`),"
                        + "FOREIGN KEY (`Sale`) REFERENCES `Sales` (ID)"
                        + ") ENGINE=INNODB "
                        + "DEFAULT "
                        + "CHARSET=utf8 COLLATE=utf8_unicode_ci "); //all but default
                deliveryAddresses.execute();
                deliveryAddresses.close();
                PreparedStatement cleanDeliveryAddresses = Main.getConnection().prepareStatement(
                        "DELETE DeliveryAddresses  "
                        + "FROM DeliveryAddresses "
                        + "left Join Sales On DeliveryAddresses.Sale=Sales.ID "
                        + "WHERE Sales.ID is null");
                cleanDeliveryAddresses.execute();
                PreparedStatement dropCustomerOrder = Main.getConnection().prepareStatement(
                        "ALTER TABLE Sales DROP COLUMN CustomerOrder");
                dropCustomerOrder.execute();
                dropCustomerOrder.close();
                //exclude as seems to change every time
//            ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM Offers LIKE 'DiscountedPrice' ");
//            rs = ps.executeQuery();
//            if (!rs.first()) {
//                rs.close();
//                PreparedStatement offersUpdate = Main.getConnection().prepareStatement(
//                    "ALTER  TABLE Offers " +
//                    "ADD COLUMN `DiscountedPrice` int(11)  collate utf8_unicode_ci NOT NULL DEFAULT '0'  ");
//                offersUpdate.execute();
//                offersUpdate.close();
//            }else {
//                rs.close();
//            }
                //need to alter Offers table so that Discounted, Included and Normal, are unsigned
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM Offers LIKE 'L' ");
                rs = ps.executeQuery();
                if (rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE Offers DROP COLUMN L,DROP COLUMN C,DROP COLUMN ZC,DROP COLUMN Z");
                    ps.execute();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE Offers ADD (Discounted smallint(5) NOT NULL default '0',Included smallint(5) NOT NULL default '0',Normal smallint(5) NOT NULL default '0',`Z` int(10) "
                            + " NOT NULL default '0')");
                    ps.execute();
                } else {
                    rs.close();
                }
                //need to alter Offers table so that Discounted, Included and Normal, are unsigned
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM CashUps LIKE 'Notes20F' ");
                rs = ps.executeQuery();
                if (rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE CashUps DROP COLUMN Notes20F,DROP COLUMN Notes10F,DROP COLUMN Notes5F,DROP COLUMN Loose200F,DROP COLUMN Loose100F,DROP COLUMN Loose50F,DROP COLUMN Loose25F,DROP COLUMN Loose20F,DROP COLUMN Loose10F,DROP COLUMN Loose5F,DROP COLUMN Loose2F,DROP COLUMN Loose1F");
                    ps.execute();
                } else {
                    rs.close();
                }
                //See if Skus.Supplier exist
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM Skus LIKE 'Supplier'");
                rs = ps.executeQuery();
                if (rs.first()) {
                    rs.close();
                    PreparedStatement products = Main.getConnection().prepareStatement(
                            "SELECT Products.*,Skus.*,Departments.Margin "
                            + "FROM Products,Skus,Departments "
                            + "WHERE Products.Sku=Skus.ID "
                            + "AND Departments.ID = Skus.Department");
                    ResultSet eachProduct = products.executeQuery();
                    while (eachProduct.next()) {
                        //  Then scan through Skus and create new Pack and PackSupplier records
                        //  based on default  supplier, caseSize retail price and margin
                        product = eachProduct.getLong("Products.ID");
                        size = eachProduct.getInt("Skus.CaseSize");
                        ps = Main.getConnection().prepareStatement("INSERT INTO Packs "
                                + "(ID,WhenCreated,Product,Size,Code)"
                                + "VALUES(?,'2000-1-1',?,?,'Default')");
                        ps.setNull(1, Types.INTEGER);
                        ps.setLong(2, product);
                        ps.setInt(3, size);
                        ps.executeUpdate();
                        ps = Main.getConnection().prepareStatement(
                                "SELECT LAST_INSERT_ID() FROM Packs");
                        rs = ps.executeQuery();
                        rs.first();
                        pack = rs.getInt(1);
                        rs.close();
                        margin = eachProduct.getInt("Margin");
                        //need to modify for tax regime here
                        price = eachProduct.getInt("Price") * (100 - margin) / 100;
                        if (price == 0) {
                            price = 1;
                        }
                        supplier = eachProduct.getInt("Skus.Supplier");
                        ps = Main.getConnection().prepareStatement(
                                "INSERT INTO PackSuppliers "
                                + "(ID,Pack,WhenCreated,Price,Supplier)"
                                + "VALUES(?,?,'2000-1-1',?,?)");
                        ps.setNull(1, Types.INTEGER);
                        ps.setInt(2, pack);
                        ps.setInt(3, price);
                        ps.setInt(4, supplier);
                        ps.executeUpdate();
                    }
                    eachProduct.close();
                    //  Then delete Skus.Supplier, Skus.CaseSize
                    ps = Main.getConnection().prepareStatement(
                            "ALTER TABLE Skus DROP COLUMN CaseSize,DROP COLUMN Supplier");
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM Products LIKE 'Encoded'");
                rs = ps.executeQuery();
                if (!rs.first()) {//need to create this field
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE Products "
                            + "ADD (Encoded SMALLINT(1) NOT NULL DEFAULT '0')");
                    ps.executeUpdate();
                }
                rs.close();
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM Packs LIKE 'Encoded'");
                rs = ps.executeQuery();
                if (!rs.first()) {//need to create this field
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE Packs "
                            + "ADD (Encoded SMALLINT(1) NOT NULL DEFAULT '0')");
                    ps.executeUpdate();
                }
                rs.close();
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM CashUps LIKE 'FixedFloat'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE CashUps ADD (FixedFloat int(11) NOT NULL default '0')");
                    ps.executeUpdate();
                }
                rs.close();
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM CashUps LIKE 'DebitsError'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE CashUps ADD (DebitsError int(11) NOT NULL default '0',ChequeError int(11) NOT NULL DEFAULT '0',CouponsError int(11) NOT NULL DEFAULT '0',ReceivedError int(11) NOT NULL DEFAULT '0',PaidOutError int(11) NOT NULL DEFAULT '0')");
                    ps.executeUpdate();
                }
                rs.close();
                //create delivery table
                PreparedStatement deliveries = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Deliveries ( "
                        + "`ID` int(10) unsigned NOT NULL auto_increment,"
                        + "`Supplier` int(10) unsigned NOT NULL,"
                        + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP,  "
                        + "`Reference` varchar(50) collate utf8_unicode_ci NOT NULL, "
                        + "Total int (11) not null DEFAULT 0, "
                        + "Tax int (5) not null DEFAULT 0, "
                        + "`Completed` BOOL NOT NULL DEFAULT FALSE, "
                        + "PRIMARY KEY  (`ID`), "
                        + "FOREIGN KEY (`Supplier`) REFERENCES `Suppliers` (ID) "
                        + ") ENGINE=INNODB   DEFAULT CHARSET=utf8 "
                        + "COLLATE=utf8_unicode_ci ");
                deliveries.execute();
                PreparedStatement deliverylines = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `DeliveryLines` (  "
                        + "`ID` int(10) UNSIGNED NOT NULL auto_increment, "
                        + "`Delivery` int(10) UNSIGNED NOT NULL, "
                        + "Operator int(10) UNSIGNED NOT NULL,  "
                        + " `Quantity` int(11) NOT NULL DEFAULT '1', "
                        + "`Product` bigint(20) UNSIGNED NOT NULL, "
                        + "`Pack` int(10) UNSIGNED NOT NULL, "
                        + "`PackSupplier` int(10) UNSIGNED NOT NULL, "
                        + "`Price` int(11) NOT NULL DEFAULT '0', "
                        + "`PackSize` int(11) NOT NULL, "
                        + "`PackPrice` int(11)  NOT NULL, "
                        + "`Marked` BOOL NOT NULL DEFAULT FALSE, "
                        + "PRIMARY KEY  (`ID`), "
                        + "FOREIGN KEY (Delivery) REFERENCES Deliveries (ID), "
                        + "FOREIGN KEY (Product) REFERENCES Products (ID), "
                        + "FOREIGN KEY (Pack) REFERENCES Packs (ID), "
                        + "FOREIGN KEY (Operator) REFERENCES Operators (ID),"
                        + "FOREIGN KEY (`PackSupplier`) REFERENCES `PackSuppliers` (ID)"
                        + ")ENGINE=INNODB AUTO_INCREMENT=1 "
                        + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
                deliverylines.execute();
                PreparedStatement taxUpdate = Main.getConnection().prepareStatement(
                        "UPDATE Skus SET Tax=1 WHERE Tax<=0");
                taxUpdate.execute();
                PreparedStatement taxes = Main.getConnection().prepareStatement(
                        "SHOW COLUMNS FROM Taxes LIKE 'Description' ");
                rs = taxes.executeQuery();
                if (!rs.first()) {
                    //does not exist so create extra fields
                    PreparedStatement taxesUpdate = Main.getConnection().prepareStatement(
                            "ALTER TABLE Taxes ADD COLUMN Description varchar(30) collate utf8_unicode_ci NOT NULL, "
                            + "ADD COLUMN Symbol varchar(1)collate utf8_unicode_ci NOT NULL");
                    taxesUpdate.executeUpdate();
                    PreparedStatement taxesFill = Main.getConnection().prepareStatement(
                            "UPDATE Taxes SET Description=?,Symbol=? WHERE ID=?");
                    taxesFill.setString(1, "Default");
                    taxesFill.setString(2, "D");
                    taxesFill.setInt(3, 0);
                    if (taxesFill.executeUpdate() != 0) {
                        taxesFill.setString(1, "Zero rate");
                        taxesFill.setString(2, "Z");
                        taxesFill.setInt(3, 1);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Standard rate");
                        taxesFill.setString(2, "*");
                        taxesFill.setInt(3, 2);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Low rate");
                        taxesFill.setString(2, "L");
                        taxesFill.setInt(3, 3);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Exempt");
                        taxesFill.setString(2, "X");
                        taxesFill.setInt(3, 4);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Excluded");
                        taxesFill.setString(2, "E");
                        taxesFill.setInt(3, 5);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Services");
                        taxesFill.setString(2, "S");
                        taxesFill.setInt(3, 6);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Rental");
                        taxesFill.setString(2, "R");
                        taxesFill.setInt(3, 7);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Video");
                        taxesFill.setString(2, "V");
                        taxesFill.setInt(3, 8);
                        taxesFill.executeUpdate();

                    } else {
                        taxesFill.setString(1, "Default");
                        taxesFill.setString(2, "D");
                        taxesFill.setInt(3, 1);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Zero rate");
                        taxesFill.setString(2, "Z");
                        taxesFill.setInt(3, 2);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Standard rate");
                        taxesFill.setString(2, "*");
                        taxesFill.setInt(3, 3);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Low rate");
                        taxesFill.setString(2, "L");
                        taxesFill.setInt(3, 4);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Exempt");
                        taxesFill.setString(2, "X");
                        taxesFill.setInt(3, 5);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Excluded");
                        taxesFill.setString(2, "E");
                        taxesFill.setInt(3, 6);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Services");
                        taxesFill.setString(2, "S");
                        taxesFill.setInt(3, 7);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Rental");
                        taxesFill.setString(2, "R");
                        taxesFill.setInt(3, 8);
                        taxesFill.executeUpdate();
                        taxesFill.setString(1, "Video");
                        taxesFill.setString(2, "V");
                        taxesFill.setInt(3, 9);
                        taxesFill.executeUpdate();
                    }
                }
                PreparedStatement tracks = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Tracks ("
                        + "`ID` int(10) unsigned NOT NULL auto_increment, "
                        + "Track varchar(30) collate utf8_unicode_ci NOT NULL,"
                        + "Sale int(10) UNSIGNED NOT NULL, "
                        + " PRIMARY KEY  (`ID`), "
                        + " FOREIGN KEY (`Sale`) REFERENCES Sales (`ID`), "
                        + "INDEX (Track) "
                        + ")ENGINE=INNODB AUTO_INCREMENT=1  "
                        + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
                tracks.execute();
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM SaleLines LIKE 'Track'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE SaleLines ADD COLUMN (Track varchar(30) collate utf8_unicode_ci NOT NULL DEFAULT '') ");
                    ps.executeUpdate();
                    ps = Main.getConnection().prepareStatement("ALTER TABLE SaleLines ADD INDEX (Track) ");
                    ps.executeUpdate();
                }
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM SaleLines LIKE 'Encode'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement(usl);
                    ps.executeUpdate();
//                set defaults as well
                    //gave up on this
//                ps= Main.getConnection().prepareStatement(sdsl);
//                ps.executeUpdate();
                }
                PreparedStatement updateSaleTable = Main.getConnection().prepareStatement(
                        "ALTER TABLE Sales MODIFY COLUMN `Tax` int(11) SIGNED NOT NULL, "
                        + "MODIFY COLUMN `Customer` BIGINT  (12) UNSIGNED NOT NULL ");
                updateSaleTable.execute();
                PreparedStatement updateDeliveryAddressesTable = Main.getConnection().prepareStatement(
                        "ALTER TABLE DeliveryAddresses "
                        + "MODIFY COLUMN `Sale` int(10) UNSIGNED NOT NULL ");
                updateDeliveryAddressesTable.execute();
                updateDeliveryAddressesTable = Main.getConnection().prepareStatement(
                        "ALTER TABLE DeliveryAddresses "
                        + "ADD FOREIGN KEY (`Sale`) REFERENCES Sales (`ID`)");
                updateDeliveryAddressesTable.execute();
                PreparedStatement updateDeliveries = Main.getConnection().prepareStatement(
                        "ALTER TABLE Deliveries "
                        + "MODIFY COLUMN `Tax` int(11) SIGNED NOT NULL ");
                updateDeliveries.execute();
                updateDeliveries = Main.getConnection().prepareStatement(
                        "ALTER TABLE Deliveries "
                        + "MODIFY COLUMN `WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP ");
                updateDeliveries.execute();
//                updates = Main.getConnection().prepareStatement(
//                        "ALTER TABLE Offers "
//                        + "MODIFY COLUMN `Product` bigint(20) UNSIGNED NOT NULL, "
//                        + "ADD FOREIGN KEY (`Product`) REFERENCES Products (`ID`) ");
//                updates.execute();
                //would not work for Debbie
                PreparedStatement orderlines = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `Orderlines` (  "
                        + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                        + "`OrderNo` int(10) unsigned NOT NULL,  "
                        + "`Sku` int(10) unsigned NOT NULL,  "
                        + "`Product` bigint(20) UNSIGNED NOT NULL,  "
                        + "`Department` int(10) unsigned NOT NULL,  "
                        + "`Roci` double NOT NULL,  "
                        + "`PackSize` int(10) unsigned NOT NULL,  "
                        + "`PackPrice` int(10) unsigned NOT NULL,  "
                        + "`Quantity` int(10) unsigned NOT NULL,  "
                        + "`Tax` int(11) signed NOT NULL,  "
                        + "`Code` varchar(30) collate utf8_unicode_ci NOT NULL,  "
                        + "`WeeklySales` int(10) unsigned NOT NULL,  "
                        + "`SellBy` date NOT NULL,  "
                        + "PRIMARY KEY  (`ID`),  "
                        + "KEY `OrderIdx` (`OrderNo`),  "
                        + "KEY `SkuIdx` (`Sku`),  "
                        + "KEY `ProductIdx` (`Product`),  "
                        + "KEY `RociIdx` (`Roci`), "
                        + "FOREIGN KEY (`OrderNo`) REFERENCES `Orders`  (ID), "
                        + "FOREIGN KEY (`Sku`) REFERENCES `Skus`  (ID), "
                        + "FOREIGN KEY (`Product`) REFERENCES `Products`  (ID), "
                        + "FOREIGN KEY (`Department`) REFERENCES `Departments`  (ID) "
                        + " )ENGINE=INNODB "
                        + "AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                        + "COLLATE=utf8_unicode_ci ");
                orderlines.execute();
                updates = Main.getConnection().prepareStatement(
                        "ALTER TABLE OrderLines  "
                        + "MODIFY COLUMN `Tax` int(11) SIGNED NOT NULL ");
                updates.execute();
                PreparedStatement customerMessages = Main.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS CustomerMessages ("
                        + "Message varchar(60) collate utf8_unicode_ci NOT NULL,"
                        + "MessageShown varchar(60) collate utf8_unicode_ci NOT NULL, "
                        + "PRIMARY KEY  (`Message`)"
                        //+                     "INDEX (Message) " +
                        + ")ENGINE=INNODB   "
                        + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
                customerMessages.execute();
                createCustomerMessages = Main.getConnection().prepareStatement(
                        "INSERT IGNORE INTO CustomerMessages (Message,MessageShown) "
                        + "VALUES (?,?)");
                fillCustomerMesages("ReadyToServeYou");
                fillCustomerMesages("Sorry");
                fillCustomerMesages("OutOfService");
                fillCustomerMesages("ThankYou");
                fillCustomerMesages("ComeBackSoon");
                fillCustomerMesages("YourChange");
                fillCustomerMesages("Cheque");
                fillCustomerMesages("To_pay");
                fillCustomerMesages("YourCashBack");
                fillCustomerMesages("PartPayment");
                fillCustomerMesages("Wasted_items");
                fillCustomerMesages("Wastes");
                fillCustomerMesages("Returned_items");
                fillCustomerMesages("Returns");
                fillCustomerMesages("Total");
                fillCustomerMesages("Charged");
                fillCustomerMesages("Balance");
                fillCustomerMesages("NoSale!");
                fillCustomerMesages("AutoLock1");
                fillCustomerMesages("AutoLock2");
                fillCustomerMesages("RCTQtyProduct");
                fillCustomerMesages("RCPrice");
                fillCustomerMesages("RCCharge");
                fillCustomerMesages("RCPhone:");
                fillCustomerMesages("RCVATNO");
                fillCustomerMesages("RCCopy");
                fillCustomerMesages("RCThank_you");
                fillCustomerMesages("RCSale_no:_");
                fillCustomerMesages("RCServed_by:_");
                fillCustomerMesages("RCCustomer:_");
                fillCustomerMesages("RCBalance_remaining:");
                fillCustomerMesages("RCTotal:_");
                fillCustomerMesages("RCTax:_");
                fillCustomerMesages("RCTax2:_");
                fillCustomerMesages("RCCash:_");
                fillCustomerMesages("RCDebit:_");
                fillCustomerMesages("RCCheque:_");
                fillCustomerMesages("RCCharged");
                fillCustomerMesages("RCLoss");
                fillCustomerMesages("RCNo_change");
                fillCustomerMesages("RCChange:_");
                fillCustomerMesages("RCOwnUse");
                fillCustomerMesages("ReceiptMessage");
                //test to see if RCTQtyProduct exists
                ps = Main.getConnection().prepareStatement("SELECT * FROM CustomerMessages WHERE Message=?");
                ps.setString(1, "RCTQtyProduct");
                rs = ps.executeQuery();
                if(!rs.first()){
                    //need to ad fields
                    rs.close();                    
                    fillCustomerMesages("RCTQtyProduct");
                    fillCustomerMesages("RCPrice");
                    fillCustomerMesages("RCCharge");
                    fillCustomerMesages("RCPhone:");
                    fillCustomerMesages("RCVATNO");
                    fillCustomerMesages("RCCopy");
                    fillCustomerMesages("RCThank_you");
                    fillCustomerMesages("RCSale_no:_");
                    fillCustomerMesages("RCServed_by:_");
                    fillCustomerMesages("RCCustomer:_");
                    fillCustomerMesages("RCBalance_remaining:");
                    fillCustomerMesages("RCTotal:_");
                    fillCustomerMesages("RCTax:_");
                    fillCustomerMesages("RCTax2:_");
                    fillCustomerMesages("RCCash:_");
                    fillCustomerMesages("RCDebit:_");
                    fillCustomerMesages("RCCheque:_");
                    fillCustomerMesages("RCCharged");
                    fillCustomerMesages("RCLoss");
                    fillCustomerMesages("RCNo_change");
                    fillCustomerMesages("RCChange:_");
                    fillCustomerMesages("RCOwnUse");
                } else {
                    rs.close();
                }
                //Need t replace individual bags of coin by one communal bag
                //Test to see if Bags exists
                ps = Main.getConnection().prepareStatement("SHOW COLUMNS FROM CashUps LIKE 'Bags' ");
                rs = ps.executeQuery();
                if (!rs.first()) {//no it does not
                    rs.close();
                    //if so, create new field Bags
                    ps = Main.getConnection().prepareStatement("ALTER  TABLE CashUps  "
                            + "ADD COLUMN `Bags` int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0'");
                    ps.executeUpdate();
                    //then insert Bags= 1*Bags1+5*Bags5+10*Bags10+20*Bags20
                    ps = Main.getConnection().prepareStatement("UPDATE CashUps SET Bags= 1*Bags1+5*Bags5+10*Bags10+20*Bags20 ");
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps = Main.getConnection().prepareStatement("SHOW INDEX FROM Products WHERE KEY_NAME = 'IDIdx'");
                rs = ps.executeQuery();
                if (rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("alter table products drop index IDidx");
                    ps.executeUpdate();
                }
                rs.close();
                ps = Main.getConnection().prepareStatement("SHOW INDEX FROM Deliveries WHERE KEY_NAME = 'WhenCreated'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("alter table Deliveries ADD index (WhenCreated)");
                    ps.executeUpdate();
                }
                rs.close();
                ps = Main.getConnection().prepareStatement("SHOW INDEX FROM Packs WHERE KEY_NAME = 'Code'");
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    ps = Main.getConnection().prepareStatement("alter table Packs ADD index (Code)");
                    ps.executeUpdate();
                }
                rs.close();
                //Operators Properties
                String queryPemissions = "SHOW COLUMNS FROM Operators LIKE 'Pemissions' ";
                ps = Main.getConnection().prepareStatement(queryPemissions);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String addPemissions = "ALTER TABLE Operators ADD COLUMN "
                            + " `Pemissions` int(11)  NOT NULL DEFAULT '0' COMMENT "
                            + "'operator pemissions'  ";
                    ps = Main.getConnection().prepareStatement(addPemissions);
                    ps.execute();
                    ps.close();
                }
                ps.close();
                rs.close();
                String queryFixedProfit = "SHOW COLUMNS FROM `Sales` LIKE 'FixedProfit' ";
                ps = Main.getConnection().prepareStatement(queryFixedProfit);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String addFixedProfit = "ALTER TABLE  Sales ADD COLUMN "
                            + " `FixedProfit` TINYINT(1) default '0' COMMENT 'set to 1 after fault fixed date' ";
                    ps = Main.getConnection().prepareStatement(addFixedProfit);
                    ps.execute();
                    ps.close();
                }
                rs.close();
                String queryMultiPack = "SHOW COLUMNS FROM `Products` LIKE 'MultiPack' ";
                ps = Main.getConnection().prepareStatement(queryMultiPack);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String addMultiPack = "ALTER TABLE  Products ADD COLUMN "
                            + " `MultiPack` int(11) NOT NULL default '1' ";
                    ps = Main.getConnection().prepareStatement(addMultiPack);
                    ps.execute();
                    ps.close();
                }
                rs.close();
                String taxRates = "SELECT SUM(Rate)/COUNT(Rate) AS X FROM Taxes";
                ps = Main.getConnection().prepareStatement(taxRates);
                rs = ps.executeQuery();
                rs.first();
                double x = rs.getDouble("X");
                rs.close();
                if(x<250){
                    //increase tax rates by factor of ten
                    String taxByTen = "UPDATE Taxes SET Rate=10*Rate";
                    ps = Main.getConnection().prepareStatement(taxByTen);
                    int executeUpdate = ps.executeUpdate();
                    //increase department margin by factor of one hundred
                    String departmentByTen = "UPDATE Departments SET Margin=100*Margin";
                    ps = Main.getConnection().prepareStatement(departmentByTen);
                    executeUpdate = ps.executeUpdate();
                    rs.close();
                    String saleLinesByTen = "UPDATE SaleLines SET taxRate=10*taxRate";
                    ps = Main.getConnection().prepareStatement(saleLinesByTen);
                    executeUpdate = ps.executeUpdate();
                    rs.close();
                }
                //add tax2 field to skus
                String querySkuTax2 = "SHOW COLUMNS FROM `Skus` LIKE 'Tax2' ";
                ps = Main.getConnection().prepareStatement(querySkuTax2);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String tax2 = "ALTER TABLE Skus ADD COLUMN "
                            + "`Tax2` int(5) unsigned NOT NULL DEFAULT 1";
                    ps = Main.getConnection().prepareStatement(tax2);
                        ps.execute();
                        ps.close();
                }
                rs.close();
                    //add tax2 field to skus
                String querySalesTax2 = "SHOW COLUMNS FROM `Sales` LIKE 'Tax2' ";
                ps = Main.getConnection().prepareStatement(querySalesTax2);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String salesTax2 = "ALTER TABLE Sales ADD COLUMN "
                        + "`Tax2` int(11) SIGNED NOT NULL ";
                    ps = Main.getConnection().prepareStatement(salesTax2);
                    ps.execute();
                    ps.close();
                }
                rs.close();   
                //now modify Offers
                String queryOffers = "SHOW COLUMNS FROM `Offers` LIKE 'Till' ";
                ps = Main.getConnection().prepareStatement(queryOffers);
                rs = ps.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    String changeOffers = "ALTER TABLE Offers ADD COLUMN "
                        + "`Till` int(10) UNSIGNED NOT NULL DEFAULT 0, "
                            + "ADD INDEX `TillIdx` USING BTREE (`Till`)";
                    ps = Main.getConnection().prepareStatement(changeOffers);
                    ps.execute();
                    ps.close();
                }
                rs.close();                
                root.putDouble("Version", Main.about.version);
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void fillCustomerMesages(String aMessasge) throws SQLException {
        createCustomerMessages.setString(1, aMessasge);
        String messageShown = bundle.getString(aMessasge);
        createCustomerMessages.setString(2, messageShown);
        createCustomerMessages.executeUpdate();
    }
}
