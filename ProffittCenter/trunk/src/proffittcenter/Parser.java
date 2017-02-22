 /*
 * Parser.java
 *
 * Created on 29-Jul-2007, 11:01:20
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.HeadlessException;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.text.*;
import com.Ostermiller.util.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author HP_Owner
 */
public class Parser {

    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Parser");
    private static long cus;
    private static Integer operator;
    private static Integer lastOperator = 0;
    private static Long lastCustomer = 0l;
    private static String lastProduct = "";
    private static String drop = "DROP DATABASE IF EXISTS " + Main.server.database;
    static String tableName = "";
    static String productDescription;
    private static int i;
    private static int j;
    private static int id;
    private static String t;
    private static String sx;
    private static long price;
    private static Double f;
    private static String[][] values;
    private static String s;
    private static int x;
    private static Long sr;

    public static String parseBuffer(BufferedReader buff, String line, boolean eof, String op, Long jj, String aTableName) throws NumberFormatException, IOException, ParseException, SQLException, HeadlessException {
        try {
            //find products
            do {
                line = buff.readLine();
            } while (line.compareTo(bundle.getString("[Vat]")) != 0);
            line = buff.readLine(); //drop headers
            //VatID,VatCode,VatRate,VatDescription
            eof = false;
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[Supplyer]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                }
            }
            //[Supplyer]
            //line=buff.readLine();//drop headers
            //SupplyerID,SupplyerName,SupplyerAddress,SupplyerPostcode,SupplyerTelephoneNumber,IncludesVAT,
            //WeeksCredit,DaysToNextDelivery,DeliveryDays,DaysSpareStock
            PreparedStatement iSupplier = Main.getConnection().prepareStatement("INSERT INTO Suppliers (ID,Description,Address,PostCode," + "PhoneNumber,WeeksCredit," + "DaysToNextDelivery,DeliveryDays,DaysSpare)" + "VALUES (?,?,?,?,?,?,?,?,?)");
            eof = false;
            tableName = "Suppliers";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[Department]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    try {
                        values = CSVParser.parse(new StringReader(line));
                        // Display the parsed data
                        int i = 0;
                        int j = 0;
                        String sx;
                        int id = Integer.parseInt(values[i][j++]) + 2; //add 2 to start at 2 0
                        iSupplier.setInt(1, id);
                        sx = StringOps.firstCaps(values[i][j++]);
                        iSupplier.setString(2, sx); //Description
                        sx = StringOps.firstCaps(values[i][j++]);
                        iSupplier.setString(3, sx); //Address
                        sx = values[i][j++].toUpperCase();
                        iSupplier.setString(4, sx); //PostCode               3
                        iSupplier.setString(5, values[i][j++]); //PhoneNumber            4
                        j++; //skip includesvat
                        int t = checkData(values[i][j++]);
                        t = (t > 0) ? t : 0;
                        iSupplier.setInt(6, t); //WeeksCredit
                        iSupplier.setInt(7, checkData(values[i][j++])); //DaysToNextdelivery
                        Integer itemp = checkData(values[i][j++]);
                        if ((itemp & (1)) != 0) {
                            itemp = itemp >> 1 + 64;
                        } else {
                            itemp = itemp >> 1;
                        }
                        iSupplier.setInt(8, itemp); //DeliveryDays
                        iSupplier.setInt(9, checkData(values[i][j++])); //DaysSpare
                        iSupplier.executeUpdate();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        continue;
                    }
                }
            }
            //DepartmentID,DepartmentDescription,DepartmentDefaultVatID,
            //DepartmentDefaultMargin,DepartmentDefaultMinimum
            //line=buff.readLine();//drop headers
            PreparedStatement iDepartment = Main.getConnection().prepareStatement(
                    "INSERT INTO Departments (ID,Description,Tax,Supplier,Margin,Minimum) VALUES (?,?,?,?,?,?)");
            eof = false;
            tableName = "Departments";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[StockType]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    String[][] values = CSVParser.parse(new StringReader(line));
                    // Display the parsed data
                    int i = 0;
                    int j = 0;
                    String sx;
                    int id = Integer.parseInt(values[i][j++]) + 2; //add 2 to start at ID=2
                    iDepartment.setInt(1, id); //ID
                    sx = values[i][j++];
                    sx = StringOps.firstCaps(sx);
                    iDepartment.setString(2, sx); //description
                    int tax = checkData(values[i][j++]);
                    tax = taxType(tax);
                    iDepartment.setInt(3, tax); //tax
                    iDepartment.setInt(4, 1);//supplier
                    iDepartment.setInt(5, checkData(values[i][j++])); //Margin
                    iDepartment.setInt(6, checkData(values[i][j++])); //Minimum
                    iDepartment.executeUpdate();
                }
            }
            /*
             //StockValueType,Description
             //line=buff.readLine();//drop headers
             PreparedStatement zKinds = Main.connection.prepareStatement(
             "DELETE FROM Kinds WHERE ID<>1"); //all but default
             zKinds.execute();
             */
            eof = false;
            tableName = "Kinds";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[Stock]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                }
            }
            //no need for Kinds in new database
            //StockID,StockDescription,ItsDate,DepartmentID,VatID,StockQuantity,DefaultCaseSize,
            //StockMinLevel,DefaultSupplyerID,StockValueType,Stopped,ShelfRent
            //line=buff.readLine();//drop headers


            PreparedStatement iSku = Main.getConnection().prepareStatement("INSERT INTO Skus (ID,Department,Tax,Quantity,Minimum,StockType,ShelfRent)VALUES (?,?,?,?,?,?,?)");
            eof = false;
            tableName = "Skus";
            while (!eof) {
                try {
                    line = buff.readLine();
                    if (line.isEmpty() || line.compareTo(bundle.getString("[Operator]")) == 0) {
                        line = buff.readLine(); //drop header
                        eof = true;
                    } else {
                        values = CSVParser.parse(new StringReader(line));
                        // Display the parsed data
                        i = 0;
                        String sx;
                        j = 0;
                        if (values[i][j].isEmpty()) {
                            values[i][j] = "0";
                        }
                        id = Integer.parseInt(values[i][j++]) + 3; //add 2 to start at ID=2
                        iSku.setInt(1, id); //ID
                        j++; //description not used
                        j++; //date not used
                        t = values[i][j];
                        iSku.setInt(2, checkData(values[i][j++]) + 2); //Department
                        int tax = checkData(values[i][j++]);
                        tax = taxType(tax);
                        iSku.setInt(3, tax); //tax
                        iSku.setInt(4, checkData(values[i][j++])); //Quantity
                        j++;
                        iSku.setInt(5, checkPosData(values[i][j++])); //Minimum
                        j++;
                        String ss = values[i][j];
                        short kind = (short) (checkData(values[i][j++]));
                        short k;
                        if (kind == 0) {
                            k = SkuType.EXCLUDED.value();
                        } else if (kind == 1) {
                            k = SkuType.INCLUDED.value();
                        } else if (kind == 2) {
                            k = SkuType.OTHERS.value();
                        } else if (kind == 3) {
                            k = SkuType.VIDEO.value();
                        } else {
                            k = SkuType.DEFAULT.value();
                        }
                        sx = values[i][j++];
                        boolean b = false;
                        if (sx.equalsIgnoreCase("True")) {
                            b = true;
                        } else if (sx.equalsIgnoreCase("False")) {
                            b = false;
                        } else {
                            b = false;
                        }
                        k = b ? SkuType.STOP.value() : k; //stopped OR kind
                        iSku.setShort(6, k); //StockType
                        sr = null;
                        try {
                            s = values[i][j];
                            if (s.isEmpty()) {
                                s = "0";
                            }
                            sr = Long.parseLong(s);
                        } catch (NumberFormatException numberFormatException) {
                            JOptionPane.showMessageDialog(null, "numberFormatException " + numberFormatException, "progress" + s + ":", JOptionPane.ERROR_MESSAGE);
                        }
                        iSku.setLong(7, sr); //Shelfrent
                        if (id == 9057) {
                            x = 0;
                        }
                        iSku.executeUpdate();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
                }
            }
            //OperatorID,OperatorName,OperatorAuthority
            //line=buff.readLine();//drop headers
            PreparedStatement iOperator = Main.getConnection().prepareStatement("INSERT INTO Operators (ID,Description,Authority)" + "VALUES (?,?,?)");
            eof = false;
            tableName = "Operators";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[Product]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    values = CSVParser.parse(new StringReader(line));
                    // Display the parsed data
                    int i = 0;
                    int j = 0;
                    int id = Integer.parseInt(values[i][j++]);
                    //need to precede by 1000036
                    operator = id;
                    if (id == 0) {
                        continue; //no need as done
                    }
                    op = operator.toString();
                    iOperator.setString(1, op); //ID
                    iOperator.setString(2, StringOps.firstCaps(values[i][j++])); //Description
                    iOperator.setInt(3, checkData(values[i][j++])); //Authority
                    iOperator.executeUpdate();
                }
            }
            //ProductID,ProductDescription,ItsDate,StockID,ProductPrice,ProductPriced
            //line=buff.readLine();//drop headers
            PreparedStatement iProduct = Main.getConnection().prepareStatement("INSERT INTO Products (ID,Description,Sku,Price)" + "VALUES (?,?,?,?)");
            PreparedStatement cProduct = Main.getConnection().prepareStatement("SELECT ID FROM Products WHERE ID=?");
            eof = false;
            tableName = "Products";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[VideoMember]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    values = CSVParser.parse(new StringReader(line));
                    // Display the parsed data
                    //for (int i=0; i<values.length; i++){
                    i = 0;
                    sx = "";
                    j = 0;
                    sx = values[i][j++];
                    sx = StringOps.numericOnly(sx);
                    if (sx.compareTo("100001") == 0) {
                        sx = SaleType.CUSTOMER.codeString();
                    } else if (sx.length() < 7 && sx.length() >= 4) {
                        if (sx.substring(0, 4).compareTo("1000") == 0) {
                            // a hot key
                            sx = "10000" + sx.substring(4, sx.length());
                        } else {
                            sx = SaleType.CUSTOMER.codeString();
                        }
                    }
                    cProduct.setString(1, sx);
                    ResultSet rs = cProduct.executeQuery();
                    if (rs.first()) {
                        rs.close();
                        continue; //duplicate product
                    }
                    rs.close();
                    iProduct.setString(1, sx); //description
                    productDescription = sx;
                    sx = values[i][j++];
                    sx = sx.toLowerCase();
                    if (sx.isEmpty()) {
                        sx = "xxx";
                    }
                    sx = StringOps.firstCaps(sx);
                    iProduct.setString(2, sx); //Description
                    j++;
                    sx = values[i][j++];
                    sx = StringOps.numericOnly(sx);
                    if (sx.isEmpty()) {
                        sx = "0";
                    }
                    Long id = Long.parseLong(sx);
                    if (id <= 1L) {
                        id = 1L; //set to default
                    } else {
                        id += 3; //add 3 to start at ID=2
                    }
                    iProduct.setLong(3, id); //Sku
                    String s = values[i][j++];
                    if (s.isEmpty()) {
                        s = "0";
                    }
                    f = Double.parseDouble(s);
                    f *= 100;
                    if (f <= 0.) {
                        f = 0.;
                    }
                    f = java.lang.Math.floor(f + 0.5);
                    price = f.longValue();
                    iProduct.setLong(4, price); //Price
                    iProduct.executeUpdate();
                }
            }
            //MemberNumber,Name1,PostCode,Address1,Address2,Address3,Town,County,
            //Phone1,Phone2,Name2,Name3,Stop
            //line=buff.readLine();//drop headers
            PreparedStatement iCustomer = Main.getConnection().prepareStatement("INSERT INTO Customers (ID,Name1,PostCode,Address1," + "Address2,Address3,Town,County," + "Phone1,Phone2,Name2,Name3,Stop)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"); //13 values
            eof = false;
            tableName = "Customers";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[Sale]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    try {
                        values = CSVParser.parse(new StringReader(line));
                        // Display the parsed data
                        //for (int i=0; i<values.length; i++){
                        int i = 0;
                        int j = 0;
                        String sx;
                        jj = 0l;
                        sx = values[i][j++]; //MemberNumber
                        sx = StringOps.numericOnly(sx);
                        cus = SaleType.CUSTOMER.code();
                        if (sx.compareTo("0") == 0) {
                            continue;
                        } else if (Integer.parseInt(sx) <= 10010000) {
                            continue;
                        } else {
                            jj = Long.parseLong(sx) - 10010000;
                        }
                        jj += SaleType.CUSTOMER.code() * 10000l; //long value
                        iCustomer.setLong(1, jj); //ID
                        sx = values[i][j]; //Name1
                        j++;
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(2, sx); //Name1
                        sx = values[i][j++]; //PostCode
                        sx = sx.toUpperCase();
                        iCustomer.setString(3, sx); //PostCode
                        sx = values[i][j++]; //Address1
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(4, sx); //Address1
                        sx = values[i][j++]; //Address2
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(5, sx); //Address2
                        sx = values[i][j++]; //Address3
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(6, sx); //Address3
                        sx = values[i][j++]; //Town
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(7, sx); //Town
                        sx = values[i][j++]; //County
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(8, sx); //County
                        sx = values[i][j++]; //Phone1
                        sx = sx.toUpperCase();
                        iCustomer.setString(9, sx); //Phone1
                        sx = values[i][j++]; //Phone2
                        sx = sx.toUpperCase();
                        iCustomer.setString(10, sx); //Phone2
                        sx = values[i][j++]; //Name2
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(11, sx); //Name2
                        sx = values[i][j++]; //Name3
                        sx = sx.toLowerCase();
                        sx = StringOps.firstCaps(sx);
                        iCustomer.setString(12, sx); //Name3
                        sx = values[i][j++]; //Stopped
                        if (sx.equalsIgnoreCase("True")) {
                            sx = "1";
                        } else {
                            sx = "0";
                        }
                        iCustomer.setString(13, sx); //Stop
                        iCustomer.executeUpdate();
                    } catch (IOException iOException) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, iOException + valuesString());
                    } catch (NumberFormatException numberFormatException) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, numberFormatException + valuesString());
                    } catch (SQLException ex) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
                    } catch (StringIndexOutOfBoundsException ex) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
                    }
                }
            }
            //SaleLineID,SaleID,ProductID,SaleLineQuantity,SaleLinePrice
            //line=buff.readLine();//drop headers
            //SaleID,ItsDate,ItsTime,OperatorID,MemberNumber,CashTendered,
            //DebitTendered,ChequeTendered,CouponTendered,Total,
            //VatTotal,OwnUse,WasteType,TillID
            //line=buff.readLine();//drop headers Credit,
            // ALTER TABLE tbl AUTO_INCREMENT = 100
            PreparedStatement zS = Main.getConnection().prepareStatement("ALTER TABLE Sales AUTO_INCREMENT = 2");
            zS.execute();
            PreparedStatement iSale = Main.getConnection().prepareStatement("INSERT INTO Sales (ID,WhenCreated,Operator,Customer," + "Cash,Debit,Cheque,Coupon,Total,Tax,Waste,TillID)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            eof = false;
            tableName = "Sales";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo("[SaleLine]") == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
//                    try {
//                        values = CSVParser.parse(new StringReader(line));
//                        int i = 0;
//                        String sx;
//                        int j = 0;
//                        sx = values[i][j++];
//                        int id = Integer.parseInt(sx) + 2;
//                        iSale.setInt(1, id); //ID
//                        sx = values[i][j++]; //ItsDate
//                        sx += " " + values[i][j++]; //+ItsTime
//                        iSale.setTimestamp(2, checkTimeStampData(sx)); //WhenCreated
//                        id = Integer.parseInt(values[i][j++]);
//                        operator = id;
//                        lastOperator = operator;
//                        if (operator == 9006 && file.getName().compareTo("paulButler.csv") == 0) {//for paul butler
//                            operator = 9005;
//                        }
//                        op = operator.toString();
//                        iSale.setString(3, op); //Operator
//                        sx = values[i][j++]; //MemberNumber
//                        sx = StringOps.numericOnly(sx);
//                        if (sx.isEmpty()) {
//                            sx = "0";
//                        }
//                        cus = SaleType.CUSTOMER.code();
//                        if (sx.compareTo("0") == 0) {
//                            jj = 0l;
//                        } else if (Integer.parseInt(sx) < 0){
//                           jj=cus;
//                        } else if (Integer.parseInt(sx) < cus) {
//                            sx = sx + "00000000";//append zeroes
//                            sx = sx.substring(0, 8);//chop to size
//                            jj = Long.parseLong(sx) - 10010000l;
//                        } else {
//                            sx = sx + "00000000";//append zeroes
//                            sx = sx.substring(0, 8);//chop to size
//                            jj = Long.parseLong(sx) - 10010000l;
//                        }
//                        jj += SaleType.CUSTOMER.code() * 10000;
//                        if (jj.compareTo(10000380002l) > 0) {
//                            jj = 10000380000l;
//                        }
//                        if(jj<10000000000l){
//                            int tag=0;
//                        }
//                        lastCustomer = jj;
//                        iSale.setLong(4, jj); //Customer
//                        iSale.setInt(5, checkFloatData(values[i][j++])); //CashTendered
//                        iSale.setInt(6, checkFloatData(values[i][j++])); //Debit
//                        iSale.setInt(7, checkFloatData(values[i][j++])); //Cheque
//                        iSale.setInt(8, checkFloatData(values[i][j++])); //Coupon
//                        j++;
//                        //iSale.setLong(9, checkFloatData(values[i][j++])); //Total
//                        iSale.setInt(9, checkData(values[i][j++]));
//                        int tax = checkData(values[i][j++]);
//                        tax = taxType(tax);
//                        iSale.setInt(10, tax); //tax
//                        short wastevalue = 0;
//                        sx = values[i][j++]; //OwnUse
//                        if (sx.equalsIgnoreCase("True")) {
//                            wastevalue = SaleType.OWNUSE.value();
//                        } else {
//                            wastevalue = 0;
//                        }
//                        j++; //completed
//                        wastevalue = (short) (checkData(values[i][j++]) | wastevalue);
//                        iSale.setInt(11, wastevalue); //Waste
//                        iSale.setInt(12, checkData(values[i][j++]) + 1); //TillID
//                        iSale.executeUpdate();
//                    } catch (IOException iOException) {
//                        JOptionPane.showMessageDialog(null, "iOException " + iOException, tableName, JOptionPane.ERROR_MESSAGE);
//                    } catch (NumberFormatException numberFormatException) {
//                        JOptionPane.showMessageDialog(null, "numberFormatException " + numberFormatException, tableName, JOptionPane.ERROR_MESSAGE);
//                    } catch (SQLException ex) {
//                        JOptionPane.showMessageDialog(null, "SQL exception " + ex + "\n " + lastOperator + "\n " + lastCustomer, tableName, JOptionPane.ERROR_MESSAGE);
//                    } catch (ParseException parseException) {
//                        JOptionPane.showMessageDialog(null, "parseException " + parseException, tableName, JOptionPane.ERROR_MESSAGE);
//                    }
                }
            }
            PreparedStatement iSaleLine = Main.getConnection().prepareStatement("INSERT INTO SaleLines (ID,Sale,Product,Quantity,Price)" + "VALUES (?,?,?,?,?)");
            eof = false;
            tableName = "SaleLines";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() | line.compareTo(bundle.getString("[Offers]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
//                    values = CSVParser.parse(new StringReader(line));
//                    int i = 0;
//                    String sx;
//                    int j = 0;
//                    int saleLineId = checkData(values[i][j++]) + 2;
//                    if (saleLineId == 80124) {
//                        int x = 0;
//                    }
//                    iSaleLine.setInt(1, saleLineId); //ID
//                    int saleId = checkData(values[i][j++]) + 2;
//                    iSaleLine.setInt(2, saleId); //Sale
//                    sx = values[i][j++]; //ProductID
//                    if (sx.compareTo("100001") == 0) {
//                        sx = "1000001";
//                    }
//                    if (sx.length() < 7 && sx.length() >= 4) {
//                        if (sx.substring(0, 4).compareTo("1000") == 0) {
//                            // a hot key
//                            sx = "10000" + sx.substring(4, sx.length());
//                        } else {
//                            continue;
//                        }
//                    }
//                    sx = sx.trim();
//                    sx = StringOps.numericOnly(sx);
//                    int kk = sx.length(); //test
//                    if (sx.isEmpty()) {
//                        continue;
//                    }
//                    try {
//                        Long il = Long.parseLong(sx);
//                        iSaleLine.setLong(3, il); //Product
//                    } catch (NumberFormatException ex) {
//                        int jk = 0;
//                    }
//                    iSaleLine.setInt(4, checkData(values[i][j++])); //Quantity
//                    iSaleLine.setInt(5, checkFloatData(values[i][j++])); //Price
//                    iSaleLine.executeUpdate();
                }
            }
            //ID,Product,X,Y,OfferType,EndDate,SaleCount,StartDate,L,C,Z,ZC,Limit
            PreparedStatement iOffer = Main.getConnection().prepareStatement("INSERT INTO Offers (IX,Product,X,Y,OfferType,EndDate," + "StartDate)VALUES (?,?,?,?,?,?,?)");
            eof = false;
            tableName = "Offers";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() | line.compareTo(bundle.getString("[Packs]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    try {
                        values = CSVParser.parse(new StringReader(line));
                        int i = 0;
                        String sx;
                        int j = 0;
                        iOffer.setInt(1, checkData(values[i][j++]) + 2); //ID
                        sx = values[i][j++]; //ProductID
                        sx = StringOps.numericOnly(sx);
                        if (sx.isEmpty()) {
                            continue;
                        }
                        Long il = Long.parseLong(sx);
                        iOffer.setLong(2, il); //Product
                        sx = values[i][j];
                        iOffer.setInt(3, checkData(values[i][j++])); //X
                        iOffer.setInt(4, checkFloatData(values[i][j++])); //Y
                        //sx=values[i][j++];
                        iOffer.setInt(5, checkData(values[i][j++]) + 1); //Kind
                        sx = values[i][j++]; //EndDate
                        sx += " 00:00:00";
                        Calendar d = dateString2Calendar(sx);
                        java.sql.Date ds = new java.sql.Date(d.getTimeInMillis()); //start
                        j++;
                        iOffer.setDate(6, ds); //EndDate
                        sx = values[i][j++]; //StartDate
                        sx += " 00:00:00";
                        d = dateString2Calendar(sx);
                        ds = new java.sql.Date(d.getTimeInMillis()); //start
                        iOffer.setDate(7, ds); //StartDate
                        iOffer.executeUpdate();
                    } catch (SQLException ex) {//skip any non existant products
                    }
                }
            }
            //PackID,ProductID,PackBarcode,PackDescription,ItsDate,PackSize
            PreparedStatement iPack = Main.getConnection().prepareStatement("INSERT INTO Packs (ID,Product,Code,WhenCreated,Size)" + "VALUES (?,?,?,?,?)");
            eof = false;
            tableName = "Packs";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[PackSuppliers]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    try {
                        values = CSVParser.parse(new StringReader(line));
                        i = 0;
                        j = 0;
                        iPack.setInt(1, checkData(values[i][j++]) + 2); //ID
                        sx = values[i][j++]; //ProductID
                        lastProduct = sx;
                        if (sx.isEmpty() || sx.length() < 7) {
                            continue;
                        }
                        sx = StringOps.numericOnly(sx);
                        if (sx.isEmpty()) {
                            continue;
                        }
                        Long il = Long.parseLong(sx);
                        iPack.setLong(2, il); //Product
                        sx = values[i][j++]; //PackBarcode
                        //check for form x+code and add two to x
                        int firstPlus = sx.indexOf('+');
                        if (firstPlus != -1) {
                            //add 2
                            int n = Integer.parseInt(sx.substring(0, firstPlus)) + 2;
                            sx = n + sx.substring(firstPlus);
                        }
                        iPack.setString(3, sx); //Code
                        j++; //PackDescription
                        sx = values[i][j++]; //ItsDate
                        sx += " 00:00:00"; //+ItsTime
                        iPack.setTimestamp(4, checkTimeStampData(sx)); //WhenCreated
                        iPack.setInt(5, checkData(values[i][j++]));
                        iPack.executeUpdate();
                    } catch (SQLException ex) {
                        //missing product - integrity problems
                        //Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    } catch (NumberFormatException numberFormatException) {
                        JOptionPane.showMessageDialog(null, "numberFormatException " + numberFormatException, "progress", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            //PackSupplyerID,PackID,ItsDate,SupplyerID,PackSupplyerCode,PackSupplyerPrice
            PreparedStatement iPackSupplier = Main.getConnection().prepareStatement("INSERT INTO PackSuppliers (ID,Pack,WhenCreated,Supplier," + "Price)VALUES (?,?,?,?,?)");
            eof = false;
            tableName = "PackSuppliers";
            while (!eof) {
                line = buff.readLine();
                if (line.isEmpty() || line.compareTo(bundle.getString("[End]")) == 0) {
                    line = buff.readLine(); //drop header
                    eof = true;
                } else {
                    values = CSVParser.parse(new StringReader(line));
                    int i = 0;
                    String sx;
                    int j = 0;
                    iPackSupplier.setInt(1, checkData(values[i][j++]) + 2); //ID
                    iPackSupplier.setInt(2, checkData(values[i][j++]) + 2); //Pack
                    sx = values[i][j++]; //ItsDate
                    sx += " 00:00:00"; //+ItsTime
                    try {
                        //+ItsTime
                        iPackSupplier.setTimestamp(3, checkTimeStampData(sx)); //WhenCreated
                    } catch (ParseException ex) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
                    }
                    iPackSupplier.setInt(4, checkData(values[i][j++]) + 2); //Supplier
                    try {
                        iPackSupplier.setInt(5, checkFloatData(values[i][j++])); //Price
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
                    }
                    try {
                        iPackSupplier.executeUpdate();
                    } catch (SQLException ex) {
//                        Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
                        continue; //missing pack
                    }
                }
            }
            return tableName;
        } catch (SQLException ex) {
            System.out.print(valuesString());
            String g = ex + valuesString();
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString() + " " + productDescription);
        }
        return tableName;
    }

    private static String valuesString() {
        String v = "";
        for (int cnt = 0; cnt < values.length; cnt++) {
            v += values[cnt][1];
        }
        return " " + v;
    }

    private Parser() {
    }

    public static boolean execute(File file) {
        String aTableName = "";
        try {
            //delete database
            PreparedStatement deleteDatabase = Main.getConnection().prepareStatement(drop);
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
            PreparedStatement selectDatabase = Main.getConnection().prepareStatement(
                    "USE " + Main.server.database);
            selectDatabase.execute();
            //get the file
            String line = null;
            boolean eof = false;
            String op = "";
            Long jj = 0l;
            FileReader csvText = new FileReader(file);
            BufferedReader buff = new BufferedReader(csvText);
            //VatID,VatCode,VatRate,VatDescription
            tableName = parseBuffer(buff, line, eof, op, jj, aTableName);
            buff.close();
        } catch (ParseException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex + valuesString());
            return false;
        }
        return true;
    }

    public static boolean execute(BufferedReader buff) {
        String tableName = "";
        try {
            //delete database
            PreparedStatement deleteDatabase = Main.getConnection().prepareStatement(drop);
            deleteDatabase.execute();
            // also set program version to 0.0
            Main.about.version = 0.0;
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
            //get the file
            String line = null;
            boolean eof = false;
            String op = "";
            Long jj = 0l;
            //VatID,VatCode,VatRate,VatDescription
            tableName = parseBuffer(buff, line, eof, op, jj, tableName);
            buff.close();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "ParseException " + ex, tableName, JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "FileNotFoundException " + ex, tableName, JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IOException " + ex, tableName, JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SQL exception " + ex, tableName + " " + lastProduct, JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }
    static SimpleDateFormat df = new SimpleDateFormat(bundle.getString("dd/mm/yyyy_hh:mm:ss"));

    public static Calendar dateString2Calendar(
            String s) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date d1 = df.parse(s);
        cal.setTime(d1);
        return cal;
    }

    private static int checkData(String data) {
        data = StringOps.numericOnly(data.trim());
        if (data.isEmpty()) {
            data = "0";
        }

        int dd = Integer.parseInt(data);
        return dd;
    }

    private static int checkPosData(String data) {
        data = StringOps.numericOnly(data.trim());
        if (data.isEmpty()) {
            data = "1";
        }

        int dd = Integer.parseInt(data);
        if (dd <= 0) {
            dd = 1;
        }

        return dd;
    }

    private static int checkFloatData(String data) {
        if (data.isEmpty()) {
            data = "0.0";
        }

        Float dd = Float.parseFloat(data);
        dd *=
                100;
        return dd.intValue();
    }

    private static java.sql.Timestamp checkTimeStampData(String data) throws ParseException {
        java.text.SimpleDateFormat formatter =
                new java.text.SimpleDateFormat(bundle.getString("dd/MM/yyyy_hh:mm:ss"));
        java.text.SimpleDateFormat newFormatter =
                new java.text.SimpleDateFormat(bundle.getString("yyyy-MM-dd_hh:mm:ss"));
        java.util.Date date1 = null;
        date1 =
                formatter.parse(data);
        data =
                newFormatter.format(date1);
        return java.sql.Timestamp.valueOf(data);
    }

    private static int taxType(int taxOld) {
        int tax;
        if (taxOld == 0) {
            tax = 1;//Tax Type.ZERO.code();
        } else if (taxOld == 1) {
            tax = 2;//Tax Type.STANDARD.code();
        } else if (taxOld == 2) {
            tax = 3;//Tax Type.LOW.code();
        } else if (taxOld == 3) {
            tax = 4;//Tax Type.EXEMPT.code();
        } else if (taxOld == 4) {
            tax = 5;//Tax Type.RENTAL.code();
        } else if (taxOld == 5) {
            tax = 6;//Tax Type.EXCLUDED.code();
        } else if (taxOld == 6) {
            tax = 7;//Tax Type.SERVICES.code();
        } else {
            tax = 8;//Tax ype.DEFAULT.code();
        }

        return tax;
    }
}
