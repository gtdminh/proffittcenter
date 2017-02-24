/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author HP_Owner
 */
public class DatabaseCreate {

    static Connection connection = null;
    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/DatabaseCreate");
    static String driverName = "com.mysql.jdbc.Driver";
    private static String serverName = Main.server.getServerName();
    private static String url;
    private static String userName = Main.server.getUserName();
    private static String passWord = Main.server.getPassword();
    private static String mydatabase;
    private static String databaseName;
    private static PreparedStatement createCustomerMessages;

    public static boolean execute() {
        try {
            //delete database
            PreparedStatement deleteDatabase = Main.getConnection().prepareStatement(
                    "DROP DATABASE "+Main.server.database);
            deleteDatabase.execute();
            DatabaseCreate.createDatabase();
            DatabaseCreate.createTables();
            if (!DatabaseCreate.connectionQuery()) {
                return false;
            }
            if (!DatabaseCreate.databaseQuery()) {
                return false;
            }
            if (!DatabaseCreate.tablesQuery()) {
                //need to create tables
                return false;
            }
            PreparedStatement selectTill = Main.getConnection().prepareStatement(
                    "USE " + Main.server.database);
            selectTill.execute();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Message" + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, "DatabaseCreate.SQLException ", "SQLException: " + ex.getMessage());
        }
        return true;
    }

    public static void deleteData() {
        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException ex) {
                Main.logger.log(Level.SEVERE, "DatabaseCreate.jRefundsActionPerformed ", "SQLException: " + ex.getMessage());
                return;
            }
            //Empty database 
            PreparedStatement zOffer = Main.getConnection().prepareStatement(
                    "DELETE FROM Offers  ");
            zOffer.execute();
            PreparedStatement zPackSupplier = Main.getConnection().prepareStatement(
                    "DELETE FROM PackSuppliers WHERE ID<>1"); //all but default
            zPackSupplier.execute();
            PreparedStatement zPack = Main.getConnection().prepareStatement(
                    "DELETE FROM Packs ");
            zPack.execute();
            PreparedStatement zProduct = Main.getConnection().prepareStatement(
                    "DELETE FROM Products WHERE ID<>1000001"); //all but default
            zProduct.execute();
            PreparedStatement zSku = Main.getConnection().prepareStatement(
                    "DELETE FROM Skus WHERE ID<>1"); //all but default
            zSku.execute();
            PreparedStatement zDepartment = Main.getConnection().prepareStatement(
                    "DELETE FROM Departments WHERE ID<>1"); //all but default
            zDepartment.execute();
            PreparedStatement zOperator = Main.getConnection().prepareStatement(
                    "DELETE FROM Operators WHERE ID<>0"); //all but default
            zOperator.execute();
            PreparedStatement zCustomer = Main.getConnection().prepareStatement(
                    "DELETE FROM Customers "); //all but default
            zCustomer.execute();
            PreparedStatement zSaleLine = Main.getConnection().prepareStatement(
                    "DELETE FROM SaleLines  "); //all but default
            zSaleLine.execute();
            PreparedStatement zSale = Main.getConnection().prepareStatement(
                    "DELETE FROM Sales  "); //all
            zSale.execute();
            PreparedStatement zOrderLines = Main.getConnection().prepareStatement(
                    "DELETE FROM OrderLines "); //all but default
            zOrderLines.execute();
            PreparedStatement zOrders = Main.getConnection().prepareStatement(
                    "DELETE FROM Orders "); //all but default
            zOrders.execute();
            PreparedStatement zSupplier = Main.getConnection().prepareStatement(
                    "DELETE FROM Suppliers WHERE ID<>1"); //all but default
            zSupplier.execute();
            PreparedStatement zPaidOuts = Main.getConnection().prepareStatement(
                    "DELETE FROM PaidOuts "); //all but default
            zPaidOuts.execute();
            //connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Message" + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, "DatabaseCreate.jRefundsActionPerformed ", "SQLException: " + ex.getMessage());
        }
    }

    public static boolean connectionQuery() {
        try {
            try {
                Class.forName(driverName).newInstance();//"com.mysql.jdbc.Driver"
            } catch (InstantiationException ex) {
                Logger.getLogger(DatabaseCreate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DatabaseCreate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message " + ex, "ClassNotFoundException" + ex, JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, "DatabaseCreate.ClassNotFoundException ", "SQLException: " + ex.getMessage());
                return false;
            }
            // Create a connection to pass into the report.
            serverName = Main.server.getServerName();
            databaseName=Main.server.getDatabase();
            url = "jdbc:mysql://" + serverName;
            userName = Main.server.getUserName();
            passWord = Main.server.getPassword();
            connection = null;
            connection = DriverManager.getConnection(url, userName, passWord);
            connection.close();
            connection = null;
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There does not seem to be a MySQL database: \n"+
                    "Server: "+serverName+"\n"+"Database: "+databaseName+"\n"+"User: "+userName +"\n"+ ex, "SQLException" , JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, "DatabaseCreate.connectionQuery ", "SQLException: " 
            + ex.getMessage());
            return false;
        }
    }

    public static boolean databaseQuery() {
        mydatabase = Main.server.getDatabase();
        serverName = Main.server.getServerName();
        passWord = Main.server.getPassword();
        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message" + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, "DatabaseCreate.ClassNotFoundException ", "SQLException: " + ex.getMessage());
                return false;
            }
            url = "jdbc:mysql://" + serverName + "/";
            connection = null;
            connection = DriverManager.getConnection(url, userName, passWord);
            //so far so good
            //now we know database (aka till) exists, so rest should work
            String st = "SHOW DATABASES";
            PreparedStatement sts = connection.prepareStatement(st);
            ResultSet rs = sts.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (rs.getString(1).equalsIgnoreCase(mydatabase)) {
                    found = true;
                    break;
                }
            }
            connection.close();
            return found;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Message" + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, "DatabaseCreate.SQLException ", "SQLException: " + ex.getMessage());
            return false;
        }
    }

    public static boolean tablesQuery() {
        mydatabase = Main.server.getDatabase();
        serverName = Main.server.getServerName();
        passWord = Main.server.getPassword();
        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message" + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, "DatabaseCreate.ClassNotFoundException ", "SQLException: " + ex.getMessage());
                return false;
            }
            url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            connection = null;
            connection = DriverManager.getConnection(url, userName, passWord);
            //so far so good
            String sdb = "USE " + mydatabase;
            PreparedStatement udb = connection.prepareStatement(sdb);
            udb.execute();//use database
            String departmentQuery = "Select * FROM Departments";
            PreparedStatement dQ = connection.prepareStatement(departmentQuery);
            dQ.executeQuery();//department exists
            connection.close();
            return true;
        } catch (SQLException ex) {
//            Main.logger.log(Level.SEVERE, "DatabaseCreate.SQLException ", "SQLException: " + ex.getMessage());
            return false;
        }
    }

    public static boolean createDatabase() {
        mydatabase = Main.server.getDatabase();
        serverName = Main.server.getServerName();
        passWord = Main.server.getPassword();
        try {
            String driverName = "com.mysql.jdbc.Driver";
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message" + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, "DatabaseCreate.SQLException ", "SQLException: " + ex.getMessage());
                return false;
            }
            url = "jdbc:mysql://" + serverName;
            connection = DriverManager.getConnection(url, userName, passWord);
            //so far so good
            String cdb = "CREATE DATABASE " + mydatabase;
            PreparedStatement udb = connection.prepareStatement(cdb);
            udb.execute();//create database
            connection.close();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Message" + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, "DatabaseCreate.SQLException ", "SQLException: " + ex.getMessage());
            return false;
        }
    }

    static boolean createTables() {
        mydatabase = Main.server.getDatabase();
        serverName = Main.server.getServerName();
        passWord = Main.server.getPassword();
        try {
            // Create a connection to pass into the report.
            String cdb = "CREATE DATABASE IF NOT EXISTS "
                    + mydatabase;
            String sdb = "USE "
                    + mydatabase;
            Connection connection = null;
            url = "jdbc:mysql://" + serverName;
            connection = DriverManager.getConnection(url, userName, passWord);
            PreparedStatement udb = connection.prepareStatement(sdb);
            udb.execute();//use database
            PreparedStatement customerDrop = connection.prepareStatement(
                    "DROP TABLE IF EXISTS Customers");
            customerDrop.execute();
//            PreparedStatement customerSpace = connection.prepareStatement(
//                    "DROP TABLESPACE till");
//            customerSpace.execute();
            PreparedStatement customers = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Customers` ("
                    + "`ID` bigint(12) unsigned NOT NULL auto_increment, "
                    + " `WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP, "
                    + "`Name1` varchar(30) collate utf8_unicode_ci default NULL,  "
                    + "`PostCode` varchar(10) collate utf8_unicode_ci default NULL,  "
                    + "`Address1` varchar(30) collate utf8_unicode_ci default NULL, "
                    + "`Address2` varchar(30) collate utf8_unicode_ci default NULL, "
                    + "`Address3` varchar(30) collate utf8_unicode_ci default NULL, "
                    + "`Town` varchar(20) collate utf8_unicode_ci default NULL, "
                    + "`County` varchar(20) collate utf8_unicode_ci default NULL,"
                    + "`Phone1` varchar(20) collate utf8_unicode_ci default NULL,"
                    + "`Phone2` varchar(20) collate utf8_unicode_ci default NULL,"
                    + "`Name2` varchar(30) collate utf8_unicode_ci NOT NULL, "
                    + "`Name3` varchar(30) collate utf8_unicode_ci default NULL, "
                    + "`Account` varchar(30) collate utf8_unicode_ci, "
                    + "`Discount` smallint(5)unsigned collate utf8_unicode_ci NOT NULL DEFAULT '0', "
                    + "`Stop` smallint(1) NOT NULL default '0', "
                    + "`Creditlimit` int(11)  NOT NULL DEFAULT '0' COMMENT 'Maximum credit in pounds', "
                    + "  PRIMARY KEY  (`ID`)"
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 "
                    + "DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci "); //all but default

            customers.execute();
            PreparedStatement suppliersDrop = connection.prepareStatement(
                    "DROP TABLE IF EXISTS suppliers");
            suppliersDrop.execute();
//            PreparedStatement SuppliersSpace = connection.prepareStatement(
//                    "DROP TABLESPACE Suppliers");
//            SuppliersSpace.execute();
            PreparedStatement suppliers = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Suppliers` ("
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`Description` varchar(60) collate utf8_unicode_ci NOT NULL COMMENT 'Name',  "
                    + "`Address` varchar(60) collate utf8_unicode_ci NOT NULL,  "
                    + "`PostCode` varchar(10) collate utf8_unicode_ci NOT NULL,  "
                    + "`PhoneNumber` varchar(20) collate utf8_unicode_ci NOT NULL, "
                    + "`WeeksCredit` smallint(5) unsigned NOT NULL default '0',  "
                    + "`DaysToNextDelivery` smallint(5) unsigned NOT NULL default '0',  "
                    + "`DeliveryDays` set('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') "
                    + "collate utf8_unicode_ci NOT NULL,  "
                    + "`DaysSpare` smallint(5) unsigned NOT NULL default '1',  "
                    + "PRIMARY KEY  (`ID`),  "
                    + "KEY `DescriptionIndex` (`Description`)"
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 "
                    + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
            suppliers.execute();
            PreparedStatement taxes = connection.prepareStatement(//{id,description, rate, symbol, code}
                    "CREATE TABLE IF NOT EXISTS Taxes ("
                    + "`ID` int(5) unsigned NOT NULL auto_increment, "
                    + "Description varchar(30) collate utf8_unicode_ci NOT NULL,"
                    + "`Rate` int(5) unsigned NOT NULL, "
                    + "Symbol varchar(1)collate utf8_unicode_ci NOT NULL, "
                    + " PRIMARY KEY  (`ID`) "
                    + ")ENGINE=INNODB AUTO_INCREMENT=1  "
                    + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
            taxes.execute();
            PreparedStatement departments = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Departments` ("
                    + "`ID` int(5) unsigned NOT NULL auto_increment,  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Description` varchar(30) collate utf8_unicode_ci NOT NULL,  "
                    + "`Tax` int(5) unsigned NOT NULL,  "
                    + "`Supplier` int(10) unsigned NOT NULL,  "
                    + "`Margin` int(10) NOT NULL default '25' COMMENT 'In %',  "
                    + "`Minimum` smallint(5) unsigned NOT NULL default '1' COMMENT 'Used as a re-order level', "
                    + "Restriction varchar(2) collate utf8_unicode_ci default '' COMMENT 'Used for various sales restrictions', "
                    + "PRIMARY KEY  (`ID`), "
                    + "FOREIGN KEY (`Supplier`) REFERENCES Suppliers (`ID`), "
                    + "FOREIGN KEY (`Tax`) REFERENCES Taxes (`ID`) "
                    + ") ENGINE=INNODB "
                    + "AUTO_INCREMENT=1 "
                    + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci "); //all but default
            departments.execute();
            PreparedStatement operators = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Operators` ("
                    + "`ID` int(10) unsigned NOT NULL,  "
                    + "`Description` varchar(40) collate utf8_unicode_ci NOT NULL default 'The persons name',  "
                    + "`Authority` tinyint(3) unsigned NOT NULL default '4',  "
                    + " `Pemissions` int(11)  NOT NULL DEFAULT '0' COMMENT 'operator pemissions', "
                    + "PRIMARY KEY  (`ID`) "
                    + ") ENGINE=INNODB  "
                    + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci "); //all but default
            operators.execute();
            PreparedStatement skus = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Skus` ("
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Department` int(10) unsigned NOT NULL default '1',  "
                    + "`Tax` int(5) unsigned NOT NULL,  "
                    + "`Tax2` int(5) unsigned NOT NULL DEFAULT 1,  "
                    + "`Quantity` int(11) NOT NULL default '0',  `Minimum` "
                    + "smallint(5) NOT NULL default '1' COMMENT "
                    + "'Used for ordering',  `StockType` smallint(5) unsigned NOT NULL "
                    + "default '1' COMMENT 'Type for stock valuation',  "
                    + "`ShelfRent` int(11) NOT NULL default '0' COMMENT "
                    + "'In pence',  `Stopped` tinyint(1) NOT NULL default '0' "
                    + "COMMENT 'Boolean value',  `Q1` int(11) NOT NULL default "
                    + "'0',  `Q2` int(11) NOT NULL default '0',  `Q3` int(11) "
                    + "NOT NULL default '0',  `Q4` int(11) NOT NULL default "
                    + "'0',  `Q5` int(11) NOT NULL default '0',  `Q6` int(11) "
                    + "NOT NULL default '0',  `Qt` int(11) NOT NULL default "
                    + "'0',  `QRoci` int(11) NOT NULL default '0',  "
                    + "PRIMARY KEY  (`ID`),  KEY `WhenIndex` (`WhenCreated`), "
                    + "FOREIGN KEY (`Tax`) REFERENCES Taxes (`ID`), "
                    + "FOREIGN KEY (`Tax2`) REFERENCES Taxes (`ID`), "
                    + "FOREIGN KEY (`Department`) REFERENCES `Departments`  (ID) "
                    + ")ENGINE=INNODB AUTO_INCREMENT=1 "
                    + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
            skus.execute();
            PreparedStatement products = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Products` "
                    + "(  `ID` bigint(20) UNSIGNED NOT NULL COMMENT 'The bar code',  "
                    + "`Description` varchar(50) collate utf8_unicode_ci NOT NULL COMMENT 'Full description of product', "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT 'When created',  "
                    + "`Price` int(11) signed zerofill NOT NULL default '00000000000' COMMENT 'Price in pence', "
                    + "`Per` varchar(5) NOT NULL default ' ' COMMENT 'unit of sale',  "
                    + "`Sku` int(10) unsigned NOT NULL, "
                    + "Encoded SMALLINT(1) NOT NULL DEFAULT '0', "
                    + " `MultiPack` int(11) NOT NULL default '1',  "
                    + "PRIMARY KEY  (`ID`), "
                    + "  KEY `DescriptionIdx` (`Description`),"
                    + "FOREIGN KEY (`Sku`) REFERENCES `Skus`  (ID)) "
                    + " ENGINE=INNODB DEFAULT CHARSET=utf8  "
                    + "COLLATE=utf8_unicode_ci ");
            products.execute();
            PreparedStatement orders = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Orders` (  "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`Supplier` int(10) unsigned NOT NULL,  "
                    + "`WhenCreated` timestamp NOT NULL default "
                    + "CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Operator` INT(10) unsigned NOT NULL,  PRIMARY KEY  (`ID`), "
                    + "FOREIGN KEY (`Operator`) REFERENCES `Operators`  (ID), "
                    + "FOREIGN KEY (`Supplier`) REFERENCES `Suppliers` (ID)) "
                    + "ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            orders.execute();
            PreparedStatement orderlines = connection.prepareStatement(
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
            PreparedStatement packs = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Packs` ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Product` bigint(20) UNSIGNED NOT NULL,  "
                    + "`Size` int(11) NOT NULL default '1', "
                    + "`Code` varchar(30) collate utf8_unicode_ci NOT NULL, "
                    + "Encoded SMALLINT(1) NOT NULL DEFAULT '0',  "
                    + "PRIMARY KEY  (`ID`), "
                    + "KEY `Code` (`Code`),  "
                    + "FOREIGN KEY (`Product`) REFERENCES `Products`  (ID) "
                    + ")ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            packs.execute();
            PreparedStatement packsuppliers = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `PackSuppliers` (  "
                    + "`ID` int(10)unsigned NOT NULL auto_increment,  "
                    + "`Pack` int(10) unsigned  NOT NULL default '1',  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Price` int(11) NOT NULL default '1',  "
                    + "`Supplier` int(10)unsigned NOT NULL,  "
                    + "PRIMARY KEY  (`ID`), "
                    + "FOREIGN KEY (`Supplier`) REFERENCES `Suppliers`  (ID), "
                    + "FOREIGN KEY (`Pack`) REFERENCES `Packs`  (ID), "
                    + "FOREIGN KEY (`Supplier`) REFERENCES `Suppliers`  (ID) "
                    + ") ENGINE=INNODB "
                    + "AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            packsuppliers.execute();
            PreparedStatement sales = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Sales` (  "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`Operator` INT(10) unsigned NOT NULL,  "
                    + "`Customer` bigint  (12) unsigned NOT NULL,  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Total` int(11) NOT NULL default '0',  "
                    + "`Tax` int(11) SIGNED NOT NULL, "
                    + "`Cash` int(11) NOT NULL default '0',  "
                    + "`Cheque` int(11) NOT NULL default '0',  "
                    + "`Debit` int(11) NOT NULL default '0',  "
                    + "`Coupon` int(11) NOT NULL default '0',  "
                    + "`TillId` int(11) unsigned NOT NULL default '0',  "
                    + "`CustomerOrder` varchar(50) collate utf8_unicode_ci NOT NULL COMMENT 'Customers own code' NOT NULL DEFAULT ' ', "
                    + "`Waste` tinyint(2)  default '0' COMMENT 'OR together states',  "
                    + "`FixedProfit` TINYINT(1) default '0' COMMENT 'set to 1 after fault fixed date', "
                    + "`Tax2` int(11) SIGNED NOT NULL, "
                    + "PRIMARY KEY  (`ID`),  "
                    + "KEY `WhenCreatedIdx` (`WhenCreated`), "
                    + "FOREIGN KEY (`Operator`) REFERENCES `Operators`  (ID), "
                    + "FOREIGN KEY (`Customer`) REFERENCES `Customers`  (ID) "
                    + ")ENGINE=INNODB "
                    + "AUTO_INCREMENT=1 DEFAULT CHARSET=utf8  COLLATE="
                    + "utf8_unicode_ci ");
            sales.execute();
            PreparedStatement salelines = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `SaleLines` (  "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`Sale` int(10) unsigned NOT NULL, "
                    + "`Quantity` int(11) NOT NULL default '1' COMMENT 'number of items sold', "
                    + "`Product` bigint(20) UNSIGNED NOT NULL,  "
                    + "`Price` int(11) NOT NULL default '0' COMMENT 'price in pence charged',  "
                    + "Track varchar(30) collate utf8_unicode_ci NOT NULL DEFAULT '', "
                    + "`Encode`  TINYINT(1)  NOT NULL DEFAULT 0,"
                    + "`pricedOver`  tinyint(1) NOT NULL DEFAULT 0,"
                    + "`discounted`  tinyint(1) NOT NULL DEFAULT 0,"
                    + "`origPrice`  int(11) NOT NULL DEFAULT 0,"
                    + "`taxID`  int(5)UNSIGNED NOT NULL DEFAULT 1,"
                    + "`taxRate`  int(5) UNSIGNED NOT NULL,"
                    + "`wholesalePrice`  int(11) NOT NULL DEFAULT 0,"
                    + "`packSize`  int(11) NOT NULL DEFAULT 1,"
                    + "PRIMARY KEY  (`ID`), "
                    + "INDEX (Track), "
                    + "INDEX (Product), "
                    + "INDEX `TaxID` USING BTREE (`taxID`), "
                    + "CONSTRAINT `salelines_ibfk_3 ` FOREIGN KEY (`taxID`) REFERENCES `taxes` (`ID`),"
                    + "FOREIGN KEY (Sale) REFERENCES Sales (ID), "
                    + "FOREIGN KEY (Product) REFERENCES Products (ID) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            salelines.execute();
            PreparedStatement Orders = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Orders` ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment, "
                    + "`Supplier` int(10) unsigned NOT NULL,  "
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + "`Operator` int(10) unsigned NOT NULL,  "
                    + "PRIMARY KEY  (`ID`),"
                    + "FOREIGN KEY (`Operator`) REFERENCES `Operators`  (ID), "
                    + "FOREIGN KEY (`Supplier`) REFERENCES Suppliers (`ID`) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT "
                    + "CHARSET=utf8 COLLATE=utf8_unicode_ci ");
            Orders.execute();
            PreparedStatement OrderLines = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `OrderLines` (  "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`OrderNo` int(10) unsigned NOT NULL,  "
                    + "`Sku` int(10) unsigned NOT NULL,  "
                    + "`Product` bigint(20) UNSIGNED  NOT NULL,  "
                    + "`Department` int(10) UNSIGNED  NOT NULL,  "
                    + "`Roci` double NOT NULL,  "
                    + "`PackSize` int(10) unsigned NOT NULL, "
                    + "`PackPrice` int(10) unsigned NOT NULL,  "
                    + "`Quantity` int(10) unsigned NOT NULL,  "
                    + "`Tax` int(5) unsigned NOT NULL,  "
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
                    + "FOREIGN KEY (`Department`) REFERENCES `Departments`  (ID), "
                    + "FOREIGN KEY (`Tax`) REFERENCES `Taxes`  (ID) "
                    + ") ENGINE=INNODB "
                    + "AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            OrderLines.execute();
            PreparedStatement offers = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Offers` ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`Till` int(10)unsigned NOT NULL default 0,"
                    + "`IX` int(10) unsigned NOT NULL,  "
                    + "`Product` bigint(20) UNSIGNED NOT NULL,  "
                    + "`X` smallint(5) unsigned NOT NULL default '0',  "
                    + "`Y` int(10) NOT NULL default '0' COMMENT 'Price in pence',  "
                    + "`OfferType` smallint(5) unsigned NOT NULL default '0',  "
                    + "`StartDate` date NOT NULL,  `EndDate` date NOT NULL,  "
                    + "`SaleCount` int(11) NOT NULL default '0',  "
                    + "`Discounted` smallint(5) NOT NULL default '0',  "
                    + "`Included` smallint(5) NOT NULL default '0',  "
                    + "`Z` int(10) NOT NULL default '0',  "
                    + "`Normal` smallint(5) NOT NULL default '0',  "
                    + "`LimitValue` int(10) unsigned NOT NULL default '0', "
                    + "`DiscountedPrice` int(11) NOT NULL DEFAULT '0',  "
                    + "PRIMARY KEY  (`ID`), "
                    + "  KEY `StartDateIdx` (`StartDate`)," //TODO
                    + "  KEY `EndDateIdx` (`EndDate`),"     //TODO
                    + "KEY `ProductIdx` (`Product`),  "
                    + "KEY `TillIdx` (`Till`), "
                    + "FOREIGN KEY (`Product`) REFERENCES `Products`  (ID) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci "); //all but default
            offers.execute();
            PreparedStatement cashups = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `CashUps` ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,  "
                    + "`TillID` int(10) unsigned collate utf8_unicode_ci NOT NULL default 0,  "
                    + "`WhenCreated` timestamp NOT NULL default "
                    + "CURRENT_TIMESTAMP,  "
                    + "`WhenStarted` timestamp NOT NULL, "
                    + " `Operator` INT(10) unsigned collate utf8_unicode_ci NOT "
                    + "NULL default '1',"
                    + "Takings int(11) NOT NULL default '0',"
                    + "Charged int(11) NOT NULL default '0',"
                    + "Agency int(11) NOT NULL default '0',"
                    + "Received int(11) collate utf8_unicode_ci NOT NULL DEFAULT '0',"
                    + "`TomorrowsFloat` int(11)  NOT NULL DEFAULT '0', "
                    + "Notes200 int(11) NOT NULL DEFAULT '0', "
                    + "Notes100 int(11) NOT NULL DEFAULT '0', "
                    + "Notes50 int(11) NOT NULL DEFAULT '0', "
                    + "Notes20 int(11) NOT NULL DEFAULT '0', "
                    + "Notes10 int(11) NOT NULL DEFAULT '0', "
                    + "Notes5 int(11) NOT NULL DEFAULT '0', "
                    + "Bags int(11) NOT NULL DEFAULT '0', "
                    + "Loose200 int(11) NOT NULL DEFAULT '0', "
                    + "Loose100 int(11) NOT NULL DEFAULT '0', "
                    + "Loose50 int(11) NOT NULL DEFAULT '0', "
                    + "Loose25 int(11) NOT NULL DEFAULT '0', "
                    + "Loose20 int(11) NOT NULL DEFAULT '0', "
                    + "Loose10 int(11) NOT NULL DEFAULT '0', "
                    + "Loose5 int(11) NOT NULL DEFAULT '0', "
                    + "Loose2 int(11) NOT NULL DEFAULT '0', "
                    + "Loose1 int(11) NOT NULL DEFAULT '0', "
                    + "Error int(11) NOT NULL DEFAULT '0', "
                    + "DebitsError int(11) NOT NULL DEFAULT '0', "
                    + "ChequeError int(11) NOT NULL DEFAULT '0', "
                    + "CouponsError int(11) NOT NULL DEFAULT '0', "
                    + "ReceivedError int(11) NOT NULL DEFAULT '0', "
                    + "PaidOutError int(11) NOT NULL DEFAULT '0', "
                    + "Reconciled BOOL NOT NULL DEFAULT FALSE,"
                    + "FixedFloat int(11) NOT NULL default '0',"
                    + "PRIMARY KEY  (`ID`),"
                    + "KEY `WhenCreatedIdx` (`WhenCreated`), "
                    + "KEY `WhenStartedIdx` (`WhenStarted`), "
                    + "FOREIGN KEY (`Operator`) REFERENCES `Operators`  (ID) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci "); //all but default
            cashups.execute();
            PreparedStatement paidouts = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `PaidOuts` ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,"
                    + "`TillID` int(10) unsigned NOT NULL default 0,  "
                    + "`WhenCreated` timestamp NOT NULL default "
                    + "CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,  "
                    + " `Operator` INT(10) unsigned NOT "
                    + "NULL default '1',"
                    + "`Description` varchar(50) collate utf8_unicode_ci NOT NULL COMMENT 'Full description of reason',"
                    + "`Amount` int(11) default '0',"
                    + "PRIMARY KEY  (`ID`), "
                    + "KEY `WhenCreatedIdx` (`WhenCreated`), "
                    + "FOREIGN KEY (`Operator`) REFERENCES `Operators`  (ID) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci "); //all but default
            paidouts.execute();
            PreparedStatement deliveries = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Deliveries ( "
                    + "`ID` int(10) unsigned NOT NULL auto_increment,"
                    + "`Supplier` int(10) unsigned NOT NULL,"
                    + "`WhenCreated` timestamp NOT NULL default CURRENT_TIMESTAMP,  "
                    + "`Reference` varchar(50) collate utf8_unicode_ci NOT NULL, "
                    + "Total int (11) not null DEFAULT 0, "
                    + "Tax int (11) not null, "
                    + "`Completed` BOOL NOT NULL DEFAULT FALSE, "
                    + "PRIMARY KEY  (`ID`), "
                    + "  KEY `WhenCreated` (`WhenCreated`),"
                    + "FOREIGN KEY (`Supplier`) REFERENCES `Suppliers` (ID) "
                    + ") ENGINE=INNODB AUTO_INCREMENT=1   DEFAULT CHARSET=utf8 "
                    + "COLLATE=utf8_unicode_ci ");
            deliveries.execute();
            PreparedStatement deliveryAddresses = connection.prepareStatement(
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
            PreparedStatement deliverylines = connection.prepareStatement(
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
            PreparedStatement customerMessages = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `CustomerMessages` ( "
                        + "Message varchar(60) collate utf8_unicode_ci NOT NULL,"
                        + "MessageShown varchar(60) collate utf8_unicode_ci NOT NULL, "
                        + "PRIMARY KEY  (`Message`) "
                        + ")ENGINE=INNODB   "
                        + "DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ");
                customerMessages.execute();
            String delete1 = "DELETE FROM Customers";
            String delete2 = "DELETE FROM Departments";
            String delete3 = "DELETE FROM Offers";
            String delete4 = "DELETE FROM Operators";
            String delete5 = "DELETE FROM OrderLines";
            String delete6 = "DELETE FROM Orders";
            String delete7 = "DELETE FROM Packs";
            String delete8 = "DELETE FROM PackSuppliers";
            String delete9 = "DELETE FROM Products";
            String delete10 = "DELETE FROM SaleLines";
            String delete11 = "DELETE FROM Sales";
            String delete12 = "DELETE FROM Skus";
            String delete13 = "DELETE FROM Suppliers";
            String delete14 = "DELETE FROM OrderLines";
            String delete15 = "DELETE FROM Orders";
            PreparedStatement deletion;
            deletion = connection.prepareStatement(delete1);
            deletion.execute();
            deletion = connection.prepareStatement(delete2);
            deletion.execute();
            deletion = connection.prepareStatement(delete3);
            deletion.execute();
            deletion = connection.prepareStatement(delete4);
            deletion.execute();
            deletion = connection.prepareStatement(delete5);
            deletion.execute();
            deletion = connection.prepareStatement(delete6);
            deletion.execute();
            deletion = connection.prepareStatement(delete7);
            deletion.execute();
            deletion = connection.prepareStatement(delete8);
            deletion.execute();
            deletion = connection.prepareStatement(delete9);
            deletion.execute();
            deletion = connection.prepareStatement(delete10);
            deletion.execute();
            deletion = connection.prepareStatement(delete11);
            deletion.execute();
            deletion = connection.prepareStatement(delete12);
            deletion.execute();
            deletion = connection.prepareStatement(delete13);
            deletion.execute();
            deletion = connection.prepareStatement(delete14);
            deletion.execute();
            deletion = connection.prepareStatement(delete15);
            deletion.execute();
            //{id,description, rate, symbol, code}
            String insertIntoTaxes = "INSERT INTO `Taxes` VALUES"
                    + "(null,'Default',0,'D'),(null,'Zero rate',0,'Z'),(null,'Standard rate',2000,'*'),(null,'Low rate',500,'L'),(null,'Exempt',0,'X'),(null,'Excluded',0,'E'),(null,'Services',2000,'S'),(null,'Rental',2000,'R'),(null,'Video',2000,'V')";
            String insertIntoDepartments = "INSERT INTO `Departments` VALUES ("
                    + "null, "
                    + "'0000-00-00 00:00:00', "
                    + "'Default', "
                    + "'1',  '1', '2000', '1','')";
            String insertIntoProducts = "INSERT INTO `Products` VALUES"
                    + " ('1000001', 'Zero rate goods', '2007-06-22 07:00:09', '00000000001', ' ','1','0','1'),"
                    + "('1000002', 'Standard rate goods', '2007-06-22 07:00:09', '00000000001', ' ','2','0','1');";
            String insertIntoSkus = "INSERT INTO `Skus` VALUES "
                    + "('1', '2008-05-12 16:44:08','1','2','2'," + //ID,WhenCreated,Department,tax1,tax2
                    "'0', '0', '2', '0', " + //Quantity,Minimum,StockType,ShelfRent,
                    "'0', '0', '0', '0', '0', '0', '0', '0', '1'),"//Stopped,Q1-6,Qt,Qroci
                    + "('2', '2008-05-12 16:44:08','1','3','2'," 
                    + "'0', '0', '2', '0', "
                    + "'0', '0', '0', '0', '0', '0', '0', '0', '1');";
            String insertIntoSuppliers = "INSERT INTO `Suppliers` VALUES ('1', 'Default', "
                    + "'', '', '', '0', '0', '', '1');";
            String insertIntoOperators = "INSERT INTO `Operators` VALUES ('0', 'Default','0',0 );";
            String insertIntoCustomers = "INSERT INTO `Customers` VALUES ("
                    + SaleType.CUSTOMER.codeString() + "0000"
                    + ",'2007-06-22 07:00:09', 'Default','0','','','','','','',"
                    + "'','','','',0,false,0 );";
            PreparedStatement records = connection.prepareStatement(insertIntoTaxes);//Taxes
            records.execute();
            records = connection.prepareStatement(insertIntoCustomers);//Customers
            records.execute();
            records = connection.prepareStatement(insertIntoSuppliers);//Suppliers
            records.execute();
            records = connection.prepareStatement(insertIntoOperators);//Operators
            records.execute();
            records = connection.prepareStatement(insertIntoDepartments);//Departments
            records.execute();
            records = connection.prepareStatement(insertIntoSkus);//Skus
            records.execute();
            records = connection.prepareStatement(insertIntoProducts);//Products
            records.execute();            
                
                createCustomerMessages = connection.prepareStatement(
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
            connection.close();
        } catch (SQLException ex) {

            Main.logger.log(Level.SEVERE, "DatabaseCreate.CreateTables ", "SQLException: " + ex.getMessage());
            return false;
        }
        return true;
    }
    private static void fillCustomerMesages(String aMessasge) throws SQLException {
        createCustomerMessages.setString(1, aMessasge);
        String messageShown = bundle.getString(aMessasge);
        createCustomerMessages.setString(2, messageShown);
        createCustomerMessages.executeUpdate();
    }

    public DatabaseCreate() {
    }
}
