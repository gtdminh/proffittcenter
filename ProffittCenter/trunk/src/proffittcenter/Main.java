
/*
 * Main.java
 *
 * Created on 03 October 2006, 16:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
/*  Splashscreen
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentaion and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package proffittcenter;

import java.awt.Frame;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.speech.synthesis.Synthesizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author David Proffitt
 */
public class Main {

    public static LineList sale = new LineList();
    private static Connection connection = null;
    private static final String driverName = "com.mysql.jdbc.Driver";
    public static AlphaLookup alphaLookup;
    public static SalesScreen salesScreen;
    public static SaleTableModel saleModel;
    public static Sku sku;
    public static Departments departments;
    public static Department department;
    public static Sale sale1;
    public static Takings takings;
    public static Product product;
    public static Products products;
    public static StartingStockEntry startingStockEntry;
    public static NewProduct newProduct;
    public static NewSku newSku;
    public static StockTake stockTake;
    public static SelectDepartment selectDepartment;
    public static SelectSupplier selectSupplier;
    public static SelectProduct selectProduct;
    public static SalesBy salesBy;
    public static Suppliers suppliers;
    public static Supplier supplier;
    public static Operators operators;
    public static Operator operator;
    public static Packs packs;
    public static Debit debit;
    public static Offers offers;
    public static Offer offer;
    public static ReceiptPrinter receiptPrinter;
    public static Pole pole;
    public static DeliveryEntry deliveryEntry;
    public static OrderCreate orderCreate;
    public static int serviceslDepartment = -1;//default should not occur
    public static int agencyDepartment = -1;//default should not occur
    public static Order order;
    public static Audio audioPlayer;
    public static Sales sales;
    public static Customers customers;
    public static DatabaseCreate createDatabase;
    public static Hotkeys hotkeys;
    static URL imageURL;
    public static Frame splashFrame = null;
    public static SelectTill selectTill;
    public static Balances balances;
    public static Balance balance;
    public static Wastes wastes;
    public static Taxes taxes;
    public static Tax tax;
    public static PaidOuts paidOuts;
    public static NewPack newPack;
    public static NewPackSupplier newPackSupplier;
    public static Customer customer;
    public static Invoice invoice;
    public static DatabaseUpdate databaseUpdate;
    public static CustomerDelivery customerDelivery;
    private static Connection connectionOnline;
    public static About about;
    public static StockByValue stockByValue;
    public static StockByValueDepartmets stockByValueDepartments;
    public static PackSuppliers packSuppliers;
    public static Coupon coupon;
    public static Cheque cheque;
    public static LowStock lowStock;
    public static CashupReconciliation cashupReconciliation;
    public static SelectCashup selectCashup;
    public static SelectDate selectDate;
    public static EncodedProducts encodedProducts;
    public static HelpSet mainHelpSet = null;
    public static HelpBroker mainHelpBroker = null;
    public static CSH.DisplayHelpFromSource csh = null;
    public static final String mainHelpSetName = "InnoSetupFiles/Html/ProffittCenter.hs";
    private static final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Main");
    public static final Logger logger = Logger.getLogger("");
    static FileHandler fHandler;
    public static TempProducts tempProducts;
    public static SelectMonthYear selectMonthYear;
    public static SelectFloat selectFloat;
    private static int shiftFloat;
    public static ReconciledCashups reconciledCashups;
    public static SelectDelivery selectDelivery;
    public static Deliveries deliveries;
    public static Delivery delivery;
    public static DeliveryAddresses deliveryAddresses;
    public static SelectEncoded selectEncoded;
    public static Alphabetic alphabetic;
    public static Tracks tracks;
    public static SettingsTabbed settingsTab;
    public static Server server;
    public static Hardware hardware;
    public static Shop shop;
    public static SelectPrice selectPrice;
    public static CustomerMessages customerMessages;
    public static ProductPerformances productPerformances;
    public static Speech speech;
    private static Synthesizer synth;
    public static OrderGenerate orderGenerate;
    public static SelectCalendarDate selectCalendarDate;
    public static LinkProducts linkProducts;
    public static LinkSkus linkSkus;
    public static Pack pack;
    public static SalesScreenFunctions salesScreenFunctions;
    private static String publicProffittCenter;
    private static Preferences root;
    public static AddToStock addToStock;
    public static SelectYear selectYear;
    public static CashUp cashUp;

    public static void makeConnection() {
        // Make the driver class available.

        if (settingsTab != null) {
            String serverName,mydatabase,userName;
            serverName="";
            mydatabase="";
            userName = "";
            try {
                Class.forName(driverName);
                // Create a connection to pass into the report.
                serverName = server.getServerName(); //localhost
                mydatabase = server.getDatabase(); //till
                String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
                userName = server.getUserName(); //root
                String passWord = server.getPassword();
                connection = DriverManager.getConnection(url, userName, passWord);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Message " + ex
                        , "SQLException - " + serverName + mydatabase + userName
                        , JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: SQLException " + ex);
                
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message " + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: ClassNotFoundException " + ex);
                String shutdownCmd = "shutdown -s ";
                        try {
                            Process child = Runtime.getRuntime().exec(shutdownCmd);
                        } catch (IOException cause) {
                            Logger.getLogger(SalesScreen.class.getName()).log(Level.SEVERE, null, cause);
                        }
            }
            finally {
                return;
            }
        } else {
            try {
                Class.forName(driverName);
                // Create a connection to pass into the report.
                String serverName = "localhost";
                String mydatabase = server.getDatabase();
                String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
                String userName = "root";
                String passWord = "";
                connection = DriverManager.getConnection(url, userName, passWord);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Message " + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: SQLException " + ex);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Message " + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: ClassNotFoundException " + ex);
            }
        }
    }

    public static void makeConnectionOnline() {
        // Make the driver class available.
        if (!Main.server.internetEnabled) {
            return;
        }
        try {
            Class.forName(driverName);
            // Create a connection to pass into the report.
            String serverName = "www.proffittcenter.org"; //localhost
            String mydatabase = "proff2_ProductsOnline"; //till
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            String userName = "proff2_test"; //root
            String passWord = "1234"; //server.password  ;
            connectionOnline = DriverManager.getConnection(url, userName, passWord);
        } catch (SQLException ex) {
            //silently
             Main.logger.log(Level.SEVERE, null, "Main: SQLException " + ex);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Message " + ex, "ClassNotFoundException", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, null, "Main: ClassNotFoundException " + ex);
        }
    }

    public static Connection getConnection() {//
        if (connection == null) {
            makeConnection();
        }
        return connection;
    }

    public static void closeConnection() {
        connection = null;
    }

    public static Connection getConnectionOnline() {
        if (connectionOnline == null) {
            makeConnectionOnline();
        }
        return connectionOnline;
    }

    public static void closeConnectionOnline() {
        if (connectionOnline != null) {
            try {
                connectionOnline.close();
                connectionOnline = null;
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, null, "Main: SQLException " + ex);
            }
        }
    }
    private static URL hsURL = null;

    static class selectPrice {

        public selectPrice() {
        }
    }

    /** Creates a new instance of Main */
    public Main() {
    }

    /**
     * @param args the command line arguments 
     */
    public static void main(String[] args) {
        root = Preferences.userNodeForPackage(Main.class);
        String secondLine;
        String fileString="";
        long oldLastModified;
        try {
            shop = new Shop();
            
            FileReader f=SalesScreen.getAppData();
            if(f!=null){
                try {                
                    //we want second line of f
                    BufferedReader text = new BufferedReader(f);
                    String firstLine = text.readLine();
                    secondLine = text.readLine();
                    byte[] bb=Base64.decode(secondLine);            
                    fileString = new String(bb);
                    //look for Dropbox\Public\ProffittCenter.exe
                    publicProffittCenter= fileString+"\\ProffittCenter\\Pro.exe";
                    File publicProffittCenterInstallFile = new File(publicProffittCenter);
                    if(publicProffittCenterInstallFile.exists()){
                        Long lastModified= publicProffittCenterInstallFile.lastModified();
                        if(lastModified!=0L){
                            oldLastModified = root.getLong("lastModified", 0);
                            if(lastModified!=0L && lastModified>oldLastModified){
                                int returnValue = JOptionPane.showConfirmDialog(null, bundle.getString("InstallNewVersion"));
                                switch (returnValue) {
                                    case JOptionPane.CANCEL_OPTION:
                                        //just carry on
                                    case JOptionPane.CLOSED_OPTION:
                                        //just carry on
                                        break;
                                    case JOptionPane.NO_OPTION:
                                        //just carry on but never again so save lastModified
                                        root.putLong("lastModified", lastModified);
                                        break;
                                    case JOptionPane.OK_OPTION:
                                        //save lastModified and run install
                                        root.putLong("lastModified", lastModified);
                                        Runtime.getRuntime().exec(publicProffittCenter);
                                        System.exit(0);
                                }
                            }
                        }
                    }
                    String fileStringExtended = fileString+"\\"
                             + Main.shop.companyName + "Backups\\";
                    new File(fileStringExtended).mkdirs();//creates directory only if needed
                    try {
                        String computername=InetAddress.getLocalHost().getHostName();
                        File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
                        boolean success = (new File(defaultDirectory + "/ProffittCenterReports").mkdirs());
                        String s =  fileStringExtended+Main.shop.companyName+computername+".log";
//                        fHandler = new FileHandler(s);
                        if(fileString.isEmpty()){
                            logger.addHandler(new FileHandler(s));
                        }else{
                            //create a file with a unique name
                            String logFileString=fileStringExtended+"\\"+Main.shop.companyName+"-"+computername+".log";
                            FileHandler fh=new FileHandler(logFileString);
                            logger.addHandler(fh);
                        }
                    } catch (IOException ex) {
                        Main.logger.log(Level.SEVERE, null, "Main: IOException " + ex);
                    } catch (SecurityException ex) {
                        Main.logger.log(Level.SEVERE, null, "Main: SecurityException " + ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SalesScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
            try {
                String computername=InetAddress.getLocalHost().getHostName();
                File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
                boolean success = (new File(defaultDirectory + "/ProffittCenterReports").mkdirs());
                String s = defaultDirectory.getAbsolutePath();
                s+="\\ProffittCenterReports\\ProffittCenter.log";
                fHandler = new FileHandler(s);
                if(fileString.isEmpty()){
                    logger.addHandler(fHandler);
                }else{
                    //create a file with a unique name
                    FileHandler fh=new FileHandler(fileString+"\\ProffittCenter-"+computername+".log");
                    logger.addHandler(fh);
                }
            } catch (IOException ex) {
                 Main.logger.log(Level.SEVERE, null, "Main: IOException " + ex);
            } catch (SecurityException ex) {
                Main.logger.log(Level.SEVERE, null, "Main: SecurityException " + ex);
            }
            }
            try {
                hsURL = Main.class.getResource(mainHelpSetName);
                if (hsURL == null) {
                    System.out.println("HelpSet " + Main.mainHelpSetName + " not found.");
                } else {
                    Main.mainHelpSet = new HelpSet(null, hsURL);
                }
            } catch (HelpSetException ex) {
                JOptionPane.showMessageDialog(null, ex, "help" + hsURL, JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: HelpSetException " + ex);
                System.exit(0);
            }
            if (Main.mainHelpBroker == null) {
                if (Main.mainHelpSet != null) {
                    Main.mainHelpBroker = Main.mainHelpSet.createHelpBroker();
                }
                if (Main.mainHelpBroker != null) // CSH.DisplayHelpFromSource is a convenience class to display the helpset
                {
                    Main.csh = new CSH.DisplayHelpFromSource(Main.mainHelpBroker);
                }
            }
            imageURL = Main.class.getResource("resource/Logo.gif");
            if (imageURL != null) {
                splashFrame = SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(imageURL));
            } else {
                JOptionPane.showMessageDialog(null, "Splash image not found! " + imageURL, "error", JOptionPane.ERROR_MESSAGE);
                Main.logger.log(Level.SEVERE, null, "Main: Splash image not found");
            }
            server = new Server();
            hardware = new Hardware();
            settingsTab = new SettingsTabbed(null, true);
            createDatabase = new DatabaseCreate();
            boolean b = true;
            if (!DatabaseCreate.connectionQuery()) {
                //connection does not exist
                settingsTab.setLocation(20, 20);
                b = settingsTab.execute(true);
            } else if (!DatabaseCreate.databaseQuery()) {
                //database does not exist
                settingsTab.setLocation(20, 20);
                b = settingsTab.execute(true);
            } else if (!DatabaseCreate.tablesQuery()) {
                //Table does not exist
                settingsTab.setLocation(20, 20);
                b = settingsTab.execute(true);
            }
            if (b) {//b true was the default 
                customerMessages = new CustomerMessages(null, true);
                salesScreenFunctions= new SalesScreenFunctions();           
                speech = new Speech();
                databaseUpdate = new DatabaseUpdate();
                settingsTab = new SettingsTabbed(null, true);
                saleModel = new SaleTableModel(sale);
                alphaLookup = new AlphaLookup(null, true);
                sku = new Sku(null, true);
                departments = new Departments(null, true);
                department = new Department(null, true);
                hotkeys = new Hotkeys(null, true);
                sale1 = new Sale(null, true);
                takings = new Takings(null, true);
                product = new Product(null, true);
                products = new Products(null, true);
                startingStockEntry = new StartingStockEntry(null, true);
                newProduct = new NewProduct(null, true);
                newSku = new NewSku(null, true);
                stockTake = new StockTake(null, true);
                selectDepartment = new SelectDepartment(null, true);
                selectSupplier = new SelectSupplier(null, true);
                selectProduct = new SelectProduct(null, true);
                suppliers = new Suppliers(null, true);
                supplier = new Supplier(null, true);
                operators = new Operators(null, true);
                operator = new Operator(null, true);
                packs = new Packs(null, true);
                debit = new Debit(null, true);
                offers = new Offers(null, true);
                offer = new Offer(null, true);
                pole = new Pole();
                deliveryEntry = new DeliveryEntry(null, true);
                orderCreate = new OrderCreate(null, true);
                order = new Order(null, true);
                sales = new Sales(null, true);
                customers = new Customers(null, true);
                selectTill = new SelectTill(null, true);
                balances = new Balances(null, true);
                balance = new Balance(null, true);
                wastes = new Wastes(null, true);
                taxes = new Taxes(null, true);
                tax = new Tax(null, true);
                paidOuts = new PaidOuts(null, true);
                newPack = new NewPack(null, true);
                newPackSupplier = new NewPackSupplier(null, true);
                customer = new Customer(null, true);
                invoice = new Invoice();
                customerDelivery = new CustomerDelivery(null, true);
                about = new About(null, true);
                stockByValue = new StockByValue(null, true);
                stockByValueDepartments = new StockByValueDepartmets(null, true);
                packSuppliers = new PackSuppliers(null, true);
                coupon = new Coupon(null, true);
                cheque = new Cheque(null, true);
                lowStock = new LowStock(null, true);
                tempProducts = new TempProducts();
                cashupReconciliation = new CashupReconciliation(null, true);
                selectCashup = new SelectCashup(null, true);
                selectDate = new SelectDate(null, true);
                selectMonthYear = new SelectMonthYear(null, true);
                encodedProducts = new EncodedProducts(null, true);
                selectFloat = new SelectFloat(null, true);
                reconciledCashups = new ReconciledCashups(null, true);
                selectDelivery = new SelectDelivery(null, true);
                deliveries = new Deliveries(null, true);
                delivery = new Delivery(null, true);
                deliveryAddresses = new DeliveryAddresses(null, true);
                selectEncoded = new SelectEncoded(null, true);
                alphabetic = new Alphabetic(null, true);
                tracks = new Tracks(null, true);
                selectPrice = new SelectPrice(null, true);
                productPerformances = new ProductPerformances(null, true);
                orderGenerate = new OrderGenerate();
                selectCalendarDate = new SelectCalendarDate(null,true);
                linkProducts = new LinkProducts(null,true);
                linkSkus = new LinkSkus(null,true);
                pack = new Pack(null,true);
                salesBy = new SalesBy(null, true);
                addToStock = new AddToStock(null,true);
                selectYear = new SelectYear(null,true);
                cashUp = new CashUp();
                try {
                    makeConnection();
                    databaseUpdate.execute();
                    PreparedStatement departmentQuery = getConnection().prepareStatement(SQL.agency);
                    departmentQuery.setString(1, bundle.getString("Services"));
                    ResultSet rs = departmentQuery.executeQuery();
                    if (rs.first()) {
                        serviceslDepartment = rs.getInt("ID");
                    }
                    rs.close();
                    departmentQuery.setString(1, bundle.getString("Agency"));
                    rs = departmentQuery.executeQuery();
                    if (rs.first()) {
                        agencyDepartment = rs.getInt("ID");
                    }
                    rs.close();
                    String operatorSring = "SELECT * FROM Operators WHERE ID=?";
                    PreparedStatement operatorQuery = getConnection().prepareStatement(operatorSring);
                    operatorQuery.setString(1, "100500063514");
                    rs = operatorQuery.executeQuery();
                    if (rs.first()) {
                        settingsTab.authority = rs.getInt("Authority");
                        settingsTab.setOperator(rs.getInt("ID"));
                        settingsTab.operatorName = rs.getString("Description");
                    } else {
                        settingsTab.authority = 5;
                        settingsTab.setOperator(0); //not known
                        settingsTab.operatorName = "";
                    }
                    rs.close();
                    // Get the default toolkit
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    salesScreen = new SalesScreen(saleModel, sale);
                    salesScreen.setExtendedState(SalesScreen.MAXIMIZED_BOTH);
                    salesScreenFunctions.setSalesScreen(salesScreen);
                    Audio.play("CashReg");
                    settingsTab.execute(false);
                    salesScreen.execute();
                } // Exception handling for the Class.forName method. // Exception handling for the DriverManager.getConnection method.
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Message " + ex, "SQLException", JOptionPane.ERROR_MESSAGE);
                    Main.logger.log(Level.SEVERE, null, "Main: SQLException " + ex);
                    if (splashFrame != null) {
                        splashFrame.dispose();
                        splashFrame = null;
                    }
                }
                receiptPrinter = new ReceiptPrinter();
            } else { //get rid of the splash screen now
                if (splashFrame != null) {
                    splashFrame.dispose();
                    splashFrame = null;
                }
                System.exit(0);
            }        
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(null, "Message " + ex, "Exception", JOptionPane.ERROR_MESSAGE);
            Main.logger.log(Level.SEVERE, null, "Main: uncaught exception " + ex);
            System.err.println("Uncaught exception - " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(0);
        } 
    }
}
