/*
 * ReceiptPrinter.java
 * 
 * Created on 13-Aug-2007, 06:53:03
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP_Owner
 */
public class ReceiptPrinter_1 {

    private ResourceBundle bundle = ResourceBundle.getBundle(("proffittcenterworkingcopy/resource/ReceiptPrinter"));
    private OutputStream fileStream;
    private final byte[] RESET = {0x1b, 0x3d, 0x01, 0x1b, 0x40};//Initialize printer
    private final byte[] START = {0x1b, '@', 0x1b, '=', 0x01, 0x1b, 'S', 0x1b, '!', 0x01, 0x1b, 'R',
        0x03, '\n'
    };
    private final byte GS = 0x1d;
    private final byte ESC = 0x1b;
    private final byte NULL = 0x00;
    private final byte[] FONT1 = {0x1b, 'M', 0};
    private final byte[] DOUBLE = {0x1b, '!', 0x30, '\n'};//0x31
    private final byte[] RESETSIZE = {0x1b, '!', 0x00};//02
    private final byte[] CUT = {GS, 0x56, 0x30};
    private final byte[] RECEIPT = {0x1b, '=', 0x01};//select receipt printer
    private final byte[] NEWLINE = {'\n'};
    private final byte[] EMPHASIZE = {0x1b, 'E', 0x02, '\n'};
    private final byte[] EMPHASIZOFF = {0x1b, 'E', 0x02, '\n'};
    private final byte[] KICK = {0x1b, 0x3d, 01, 0x1b, 0x70, 00, 0x32, 0x79};//select printer (3),then kick
    private final byte[] BARCODE = {GS,'h',0x25,GS,'k',0x04};
    private final byte[] END = {NULL};
    private static int charsOnReceipt = Main.hardware.getCharsOnReceipt();
    private String heading;
    private int operator;
    private int total;
    private int tax;
    private int debit;
    private int cash;
    private int change;
    String port;
    byte b = Main.hardware.getCode();
//    private byte[] countrySelect = {0x1b, 'R', Main.hardware.getCode()};
    private byte code = Main.hardware.getCode();
    String country;
    private short waste;
    private long customer;
    String pad = "                                                              ";
    private String s = "";
    private int cheque;
    private int coupon;
    private boolean isCopy;
    private int tax2;
    private PreparedStatement sales;
    private PreparedStatement saleLines;
    private ResultSet rs2;
    private String symbol;
    private String quantity;
    private String product;
    private String charge;
    private String price;

    public ReceiptPrinter_1() {
        Serial rp = Serial.RECEIPTPORT;
        if (rp == null) {
            Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, "null port 1");
            return;
        }
        port = rp.getPort();
        fileStream = null;
        if (!port.isEmpty()) {
            fileStream = Serial.RECEIPTPORT.getFileStream();
            if (fileStream == null) {
                Serial.RECEIPTPORT.openPort("ReceiptPort");
                fileStream = Serial.RECEIPTPORT.getFileStream();
            }
        }
        charsOnReceipt = (charsOnReceipt >= 12) ? 12 : charsOnReceipt;
        heading = StringOps.fixLength(Main.customerMessages.getString("RCTQtyProduct"),
                Main.hardware.getCharsOnReceipt() - 16) + Main.customerMessages.getString("RCPrice")
                + Main.shop.poundSymbol
                + " " + Main.customerMessages.getString("RCCharge")
                + Main.shop.poundSymbol;
    }

    private void showLine(String s) {
        if (!port.isEmpty()) {
            if (s.length() == 0) {
                return;
            }
            String s1 = s;
            //need to convert from String to byte[]
            byte[] line = new byte[s1.length()];
            Charset cs = Charset.defaultCharset();
            SortedMap<String, Charset> sm = Charset.availableCharsets();
            try {
                line = s1.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    public void printLargeLine(String s) {
        if (!Main.hardware.getReceiptPort().isEmpty()) {
            //s = replaceEuroWithWord(s);
            s = replacePoundAndEuro(s);
            selectReceiptPrinter();
            if (s.length() == 0) {
                Serial.RECEIPTPORT.write(NEWLINE);
                return;
            }
            Serial.RECEIPTPORT.write(DOUBLE);
            //need to convert from String to byte[]
            byte[] line = new byte[s.length()];
            String charset = Main.hardware.charset;
            if (charset.isEmpty()) {
                try {
                    line = s.getBytes("ISO-8859-1");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    line = s.getBytes("ISO-8859-1");
                } catch (UnsupportedEncodingException ex) {
                    Main.logger.log(Level.SEVERE, "ReceiptPrinter.showLine ",
                            "UnsupportedEncodingException: " + ex.getMessage());
                }
            }
            Serial.RECEIPTPORT.write(line);
            Serial.RECEIPTPORT.write(RESETSIZE);
            Serial.RECEIPTPORT.write(NEWLINE);
            try {
                fileStream.close();
            } catch (IOException ex) {
            }
        }
    }

    public void printLine(String s) {
        if (s == null) {
            return;
        }
        s = replacePoundAndEuro(s);
        if (!Main.hardware.getReceiptPort().isEmpty()) {
            selectReceiptPrinter();
            if (s.length() == 0) {
                Serial.RECEIPTPORT.write(NEWLINE);
                return;
            }
            s += '\n';
            byte[] line = new byte[s.length()];
            try {
                line = s.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
            }
            Serial.RECEIPTPORT.write(line);
        }
    }
    
    public void printBarcode(String barcode){
        selectReceiptPrinter();
        Serial.RECEIPTPORT.write(BARCODE);
        byte[] line = new byte[barcode.length()];
        try {
            line = barcode.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        Serial.RECEIPTPORT.write(line);
        Serial.RECEIPTPORT.write(END);
        printLine(barcode);
    }

    public void printCentralLine(String s) {
        int numberOfCharsInFront = (Main.hardware.getCharsOnReceipt() - s.length()) / 2;
        numberOfCharsInFront = (numberOfCharsInFront < 0) ? 0 : numberOfCharsInFront;
        s = pad.substring(0, numberOfCharsInFront) + s;
        s = StringOps.fixLengthUntrimmed(s, Main.hardware.getCharsOnReceipt());
        Main.receiptPrinter.printLine(s);
    }

    public void printLargeCentralLine(String s) {
        s = replaceEuroWithWord(s);
        selectReceiptPrinter();
        Serial.RECEIPTPORT.write(Main.hardware.getCodeTable());
        Serial.RECEIPTPORT.write(Main.hardware.getFontSet());
        Serial.RECEIPTPORT.write(START);
        charsOnReceipt = Main.hardware.getCharsOnReceipt();
        int numberOfCharsInFront = (charsOnReceipt * 1 / 2 - s.length()) / 2;//large chars
        numberOfCharsInFront = (numberOfCharsInFront < 0) ? 0 : numberOfCharsInFront;
        if (numberOfCharsInFront != 0) {
            s = pad.substring(0, numberOfCharsInFront) + s;
            s = StringOps.fixLengthUntrimmed(s, charsOnReceipt * 1 / 2);
//            s = replacePoundAndEuro(s);
            printLargeLine(s);
        } else { //neeed to print them smaller
//            s = replacePoundAndEuro(s);
            printCentralLine(s);
        }
    }

    public void startReceipt() {
        if (!Main.hardware.getReceiptPort().isEmpty() && !Main.hardware.isInvoicePrinter()) {
            selectReceiptPrinter();
            Serial.RECEIPTPORT.write(FONT1);
            Serial.RECEIPTPORT.write(Main.hardware.getCodeTable());
            Serial.RECEIPTPORT.write(Main.hardware.getFontSet());
            Serial.RECEIPTPORT.write(START);//{0x1b,'S',0x1b,'!',0x01,0x1b,'R',0x03}
            if (!Main.shop.companyName.isEmpty()) {
                Serial.RECEIPTPORT.write(DOUBLE);//{0x1b,'!',0x31}
                printLine(Main.shop.companyName);
                resetPrinter();
            }
//            Serial.RECEIPTPORT.write(RESETSIZE);//{0x14,0x1b,0x14}
            printLine(Main.shop.companyAddress);
            printLine(Main.customerMessages.getString("RCPhone:") + Main.shop.companyPhone);
            if(! Main.shop.companyTaxID.isEmpty()){
                printLine(Main.customerMessages.getString("RCVATNO") + Main.shop.companyTaxID);
            }
        }
    }

    /**
     * reset the printer to required font set, character set
     */
    public void resetPrinter() {
        selectReceiptPrinter();
        Serial.RECEIPTPORT.write(RESET);
        Serial.RECEIPTPORT.write(Main.hardware.getCodeTable());
        Serial.RECEIPTPORT.write(Main.hardware.getFontSet());
    }

    public void selectPrinter() {
        selectReceiptPrinter();
        Serial.RECEIPTPORT.write(START);
        Serial.RECEIPTPORT.write(NEWLINE);
    }

    public void endReceipt() {
        if (!Main.hardware.getReceiptPort().isEmpty()) {
            selectReceiptPrinter();
            for (int i = 0; i < Main.hardware.getExtraLines() + 4; i++) {
                Serial.RECEIPTPORT.write(NEWLINE);
            }
            Serial.RECEIPTPORT.write(CUT);
//            resetPrinter();
        }
    }

    public void endPrinter() {
        if (!Main.hardware.getReceiptPort().isEmpty()) {
            selectReceiptPrinter();
            for (int i = 0; i < Main.hardware.getExtraLines() + 4; i++) {
                Serial.RECEIPTPORT.write(NEWLINE);
            }
            Serial.RECEIPTPORT.write(CUT);
        }
    }

    public void printLayaway(long saleId){
       if (!Main.hardware.getReceiptPort().isEmpty()) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            try {
                selectReceiptPrinter();
                startReceipt();
                long layawayCode = SaleType.LAYAWAY.code()*100000000+saleId;
                printLine(""+layawayCode);
                printBarcode(""+layawayCode);
                printReceipt(saleId);
                printLine("");
                         heading = Main.hardware.getHeading();
                         printLine(heading);
                         String[] result = heading.split("\\s+");
     //                    for (int x = 0; x < result.length; x++) {
     //                        System.out.println(result[x] + " " + result[x].length());
     //                    }
                         saleLines = Main.getConnection().prepareStatement(
                                 "SELECT SaleLines.*,Departments.*,Products.Description,"
                                 + "Products.Encoded,Skus.Tax,Taxes.* "
                                 + "FROM SaleLines,Departments,Products,Skus,Taxes "
                                 + "WHERE SaleLines.Sale=? AND SaleLines.Product=Products.ID "
                                 + "AND Products.Sku=Skus.ID AND Skus.Department=Departments.ID"
                                 + " AND Skus.Tax=Taxes.ID ");
                         saleLines.setLong(1, saleId);
                         //saleLines.setInt(2, operator);
                         rs2 = saleLines.executeQuery();
                         while (rs2.next()) {
                             String compose;
                             symbol = rs2.getString("Taxes.Symbol");
                             int j0 = result[0].length() + 1;
                             symbol = StringOps.fixLengthUntrimmed(symbol + " ", j0);
                             quantity = rs2.getString("SaleLines.Quantity");
                             int j1 = result[1].length() + 1;
                             quantity = StringOps.fixLengthUntrimmed(quantity, j1);
                             charge = " " + nf.format((double) (Line.getCharge(rs2)) / 100);
                             price = " " + nf.format(((double) rs2.getInt("SaleLines.Price")) / 100);
                             int j3 = result[3].length();
     //                        price =StringOps.fixLengthUntrimmed(price,j3+1);
                             product = rs2.getString("Products.Description");
                             int j2;
                             charsOnReceipt = Main.hardware.getCharsOnReceipt();
                             j2 = charsOnReceipt - symbol.length() - quantity.length() - charge.length() + 1;
                             product = StringOps.fixLengthUntrimmed(product, j2);
                             compose = symbol + quantity + product + charge;
                             compose = StringOps.fixLengthUntrimmed(compose, Main.hardware.getCharsOnReceipt() + 1);
                             printLine(compose);
                         }
                         rs2.close();
            } catch (SQLException ex) {
                Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
                printLine("");
                s = "";
                s = Main.customerMessages.getString("RCTotal:_") + ((new Money(total)).toString());//poundForPound
                s += " " + Main.customerMessages.getString("RCTax:_") + ((new Money(tax)).toString());//poundForPound
                printLine(s);
                if(tax2!=0){
                    s = Main.customerMessages.getString("RCTax2:_") + ((new Money(tax2)).toString());//poundForPound
                    printLine(s);
                }
    }
        
    
    
    public void printReceipt(long saleID, boolean isCopy) {
        this.isCopy = isCopy;
        printReceipt(saleID);
    }

    public void printReceipt(long saleID) {
        if (Main.hardware.getReceiptPort().isEmpty() || Main.hardware.isInvoicePrinter()) {
            return;
        }
        startReceipt();
        if (isCopy) {
            printLine(Main.customerMessages.getString("RCCopy"));
        }
        //printLine("!£$%^&*()~#");
        ResultSet rs1, rs2;
        String price;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        String charge;
        String quantity;
        String product;
        String symbol;
        try {
            sales = Main.getConnection().prepareStatement(
                    "SELECT Sales.*,DATE(Sales.WhenCreated) AS date,TIME(Sales.WhenCreated) AS time,Operators.ID,Operators.Description ,CONCAT(TRIM(Customers.Name2),' ',Customers.Name1) AS CustomerName FROM Sales,Operators,Customers " + "WHERE Sales.ID=? " + "AND Sales.Operator=Operators.ID " + "AND Sales.Customer=Customers.ID");
            sales.setLong(1, saleID);
            rs1 = sales.executeQuery();
            if (!rs1.first()) {
                rs1.close();
                printLine(Main.customerMessages.getString("RCThank_you"));
            } else {
                printLine(Main.customerMessages.getString("RCSale_no:_") + rs1.getString("Sales.ID"));
                printLine(rs1.getString("date")+"       "+ rs1.getString("time"));
                operator = rs1.getInt("Operators.ID");
                if (operator != 0) {
                    printLine(Main.customerMessages.getString("RCServed_by:_") + rs1.getString("Operators.Description"));
                }
                customer = rs1.getLong("Sales.Customer");
                if (customer != SaleType.CUSTOMER.code() * 10000) {
                    s = rs1.getString("CustomerName");
                } else {
                    s = "";
                }
                if (s.length() != 0) {
                    printLine(Main.customerMessages.getString("RCCustomer:_") + s);
                }
                total = rs1.getInt("Total");
                debit = rs1.getInt("Debit");
                tax = rs1.getInt("Tax");
                cash = rs1.getInt("Cash");
                waste = rs1.getShort("Waste");
                customer = rs1.getLong("Customer");
                cheque = rs1.getInt("Cheque");
                coupon = rs1.getInt("Coupon");
                tax2 = rs1.getInt("Tax2");
                rs1.close();
                if (waste == SaleType.RECEIVEDONACCOUNT.value()) {
                    //Print balance remaining
                    s = Main.customerMessages.getString("RCBalance_remaining:") + (new Money(Main.balance.getBalance(customer)));
                    printLine(s);
                } else {//normal sales
                    printLine("");
                    heading = Main.hardware.getHeading();
                    printLine(heading);
                    String[] result = heading.split("\\s+");
//                    for (int x = 0; x < result.length; x++) {
//                        System.out.println(result[x] + " " + result[x].length());
//                    }
                    saleLines = Main.getConnection().prepareStatement(
                            "SELECT SaleLines.*,Departments.*,Products.Description,"
                            + "Products.Encoded,Skus.Tax,Taxes.* "
                            + "FROM SaleLines,Departments,Products,Skus,Taxes "
                            + "WHERE SaleLines.Sale=? AND SaleLines.Product=Products.ID "
                            + "AND Products.Sku=Skus.ID AND Skus.Department=Departments.ID"
                            + " AND Skus.Tax=Taxes.ID ");
                    saleLines.setLong(1, saleID);
                    //saleLines.setInt(2, operator);
                    rs2 = saleLines.executeQuery();
                    while (rs2.next()) {
                        String compose;
                        symbol = rs2.getString("Taxes.Symbol");
                        int j0 = result[0].length();
                        symbol = StringOps.fixLengthUntrimmed(symbol , j0);
                        quantity = rs2.getString("SaleLines.Quantity");
                        int j1 = result[1].length() ;
                        quantity = StringOps.fixLengthUntrimmed(quantity, j1);
                        charge = " " + nf.format((double) (Line.getCharge(rs2)) / 100);
                        price = " " + nf.format(((double) rs2.getInt("SaleLines.Price")) / 100);
                        int moneyLength = (charge+price).length();
//                        int j3 = result[3].length();
//                        price =StringOps.fixLengthUntrimmed(price,j3+1);
                        product = rs2.getString("Products.Description");
                        int j2;
                        charsOnReceipt = Main.hardware.getCharsOnReceipt();
//                        j2 = charsOnReceipt - symbol.length() - quantity.length() - charge.length() + 1;
//                        product = StringOps.fixLengthUntrimmed(product, j2);
                        compose = symbol + quantity + product;
                        compose= StringOps.fixLengthUntrimmed(compose, charsOnReceipt-moneyLength);
                        compose = StringOps.fixLengthUntrimmed(compose+price+charge, charsOnReceipt);
                        printLine(compose);
                    }
                    rs2.close();
                }
                printLine("");
                s = "";
                s = Main.customerMessages.getString("RCTotal:_") + ((new Money(total)).toString());//poundForPound
                s += " " + Main.customerMessages.getString("RCTax:_") + ((new Money(tax)).toString());//poundForPound
                printLine(s);
                if(tax2!=0){
                    s = Main.customerMessages.getString("RCTax2:_") + ((new Money(tax2)).toString());//poundForPound
                    printLine(s);
                }
                s = "";
                if (cash != 0) {
                    s = Main.customerMessages.getString("RCCash:_") + ((new Money(cash)).toString());//poundForPound
                }
                if (debit != 0) {
                    s += "_" + Main.customerMessages.getString("RCDebit:_") + //poundForPound
                            ((new Money(debit)).toString());
                }
                if (cheque != 0) {
                    s += "_" + Main.customerMessages.getString("RCCheque:_") + //poundForPound
                            ((new Money(cheque)).toString());
                }
                if (coupon != 0) {
                    s += "_" + Main.customerMessages.getString("RCCoupon:_") + //poundForPound
                            ((new Money(coupon)).toString());
                }
                if (waste == SaleType.CHARGED.value()) {
                    s += Main.customerMessages.getString("RCCharged");
                }
                if (waste == SaleType.LOSS.value()) {
                    s += Main.customerMessages.getString("RCLoss");
                }
                printLine(s);
                if (waste != SaleType.CHARGED.value() && waste != SaleType.LOSS.value() && waste != SaleType.OWNUSE.value()) {
                    change = cash + debit + cheque + coupon - total;
                    if (change == 0) {
                        printLine(Main.customerMessages.getString("RCNo_change"));
                    } else {
                        printLine(Main.customerMessages.getString("RCChange:_") + //                            poundForPound
                                ((new Money(change)).toString()));
                    }
                }
                if (waste == SaleType.OWNUSE.value()) {
                    printLargeCentralLine(Main.customerMessages.getString("RCOwnUse"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReceiptPrinter_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!Main.customerMessages.getString("ReceiptMessage").isEmpty()){
            printLargeCentralLine(Main.customerMessages.getString("ReceiptMessage"));
        }
        endReceipt();
    }

    public void cashDrawerOpen() {
        if (!(Main.hardware.getReceiptPort().isEmpty())) {
            selectReceiptPrinter();
            Serial.RECEIPTPORT.write(KICK);
        }
    }

    private void selectReceiptPrinter() {
        if (Serial.isSame()) {
            Serial.RECEIPTPORT.write(RECEIPT);
        }
    }

    private String replacePoundAndEuro(String s) {
        char e = '€', ee = 0xd5, p = '£', pp = '#';
        s = s.replace(e, ee);
        s = s.replace(p, pp);
        return s;
    }

    private String replaceEuroWithWord(String s) {
        char e = '€', ec = '€';
        String euro = "Euro ";
        int end = s.length();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == e || s.charAt(i) == ec) {
                if (i - 1 > 0 && i + 1 <= s.length()) {
                    s = s.substring(0, i - 1) + euro + s.substring(i + 1);
                    end += euro.length();
                    i += euro.length();
                } else {
                    if (i == 0) {
                        s = euro + s.substring(1);
                        end += euro.length();
                        i += euro.length();
                    } else if (i == s.length()) {
                        s += euro;
                    }
                }
            }
        }
        return s;
    }

    void printDateTime() {
        Calendar cal = Calendar.getInstance();
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        printLine(sdf.format(cal.getTime()));
        
    }
}
