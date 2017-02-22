/*
 * OrderGenerate.java
 *
 * Created on 01-Sep-2007, 17:56:35
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author HP_Owner
 */
public class OrderGenerate implements Runnable {

    static PreparedStatement np;
    static PreparedStatement addToQs;
    static ResultSet rs;
    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/OrderGenerate");
    private static boolean aSale;
    private static int rate;
    private static int tax;
    private static PreparedStatement np2;
    private static int P;
    private static int daysToSellOut;
    private int supplier;
    private Calendar deliveryDate;
    private Calendar nextDeliveryDate;
    public static boolean done=false;

    public OrderGenerate() {
    }
    
    public void setup(int supplier, Calendar deliveryDate, Calendar nextDeliveryDate){
        this.supplier=supplier;
        this.deliveryDate = deliveryDate;
        this.nextDeliveryDate = nextDeliveryDate;
    }

    public void execute(int supplier, Calendar deliveryDate, Calendar nextDeliveryDate) {
        int j = 0;
        int ordersLineCount = 0;
        int theOrder;
        boolean p1;
        boolean p2;
        boolean p3;
        boolean p4;
        boolean p5;
        boolean p6;
        boolean pRoci;
        int itsQuantity;
        long itsProduct;
        int pWaste;
        double yield;
        int sku;
        Long product;
        int price;
        String code;
        int size;
        int packSupplierPrice;
        int qt;
        int sq;
        int min;
        int v;
        int r;
        int quantityNeeded = 0;
        int w;
        int p;
        int c;
        int n;
        int weeksCredit = 0;
        int s = 0;
        int packPrice;
        int packSize;
        int orderQuantity;
        int weeklySales = 0;
        int department = 1;
        String newTable;
        int qroci;
        done=false;
        //append tillID and supplierID so that multiple orders may be done at same time
        try {
            PreparedStatement getInfo = Main.getConnection().prepareStatement(
                  "SELECT S.*,SUM(SL.Quantity)AS Quant,SL.*,P.ID,P.Sku " +
                  "FROM Sales AS S, "
                  + "SaleLines AS  SL,"
                    + "Products AS P "
                    + "WHERE S.ID=SL.Sale AND ((DATE(S.WhenCreated)>=? "
                    + "AND DATE(S.WhenCreated)<=?) OR (DATE(S.WhenCreated)>=? ) ) "
                    + "AND SL.Product=P.ID AND Sku<>1 AND Sku<>2  "
                    + "GROUP BY P.ID "
                    + "ORDER BY Sku");
            np = Main.getConnection().prepareStatement(
                    "INSERT INTO Orders (ID,Supplier,Operator)"
                    + "VALUES(?,?,?)");
            np.setNull(1, Types.INTEGER);
            np.setInt(2, supplier);
            np.setLong(3, Main.operator.getOperator());
            //Create the header
            np.executeUpdate();
            np = Main.getConnection().prepareStatement(
                    "SELECT LAST_INSERT_ID() FROM Orders");
            rs = np.executeQuery();
            rs.first();
            theOrder = rs.getInt(1);//get the order ID
            rs.close();
            newTable = "Skus_" + Main.shop.getTillId() + "_" + supplier;
            //define so not in loop
            addToQs = Main.getConnection().prepareStatement(
                    "UPDATE " + newTable
                    + " SET Q1=Q1+?,Q2=Q2+?,"
                    + "Q3=Q3+?,Q4=Q4+?,Q5=Q5+?,Q6=Q6+?,"
                    + "Qroci=Qroci+? "
                    + "WHERE ID=?");
            //DROP the table for next time
            np = Main.getConnection().prepareStatement(
                    "DROP TABLE IF EXISTS " + newTable);
                    //may have died part way through
            np.executeUpdate();
            //create a new table of Skus to work on
            np = Main.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + newTable
                    + "  LIKE Skus ");
            np.executeUpdate();
            np = Main.getConnection().prepareStatement(
                    "INSERT INTO " + newTable
                    + " SELECT * FROM Skus");
            np.executeUpdate();
            Calendar sdt = deliveryDate;
            Calendar edt = nextDeliveryDate;
            Calendar sdt1 = (Calendar) sdt.clone();
            Calendar edt1 = (Calendar) edt.clone();
            while (edt1.after(sdt)) {
                sdt1.add(Calendar.WEEK_OF_YEAR, -1);
                edt1.add(Calendar.WEEK_OF_YEAR, -1); //back one week
            }
            Calendar sdt2 = (Calendar) sdt1.clone();
            sdt2.add(Calendar.WEEK_OF_YEAR, -1);
            Calendar edt2 = (Calendar) edt1.clone();
            edt2.add(Calendar.WEEK_OF_YEAR, -1); //back two weeks
            Calendar sdt3 = (Calendar) sdt1.clone();
            sdt3.add(Calendar.WEEK_OF_YEAR, -2);
            Calendar edt3 = (Calendar) edt1.clone();
            edt3.add(Calendar.WEEK_OF_YEAR, -2); //back three weeks
            Calendar sdt4 = (Calendar) sdt1.clone();
            sdt4.add(Calendar.WEEK_OF_YEAR, -51);
            Calendar edt4 = (Calendar) edt1.clone();
            edt4.add(Calendar.WEEK_OF_YEAR, -51); //back 51 week
            Calendar sdt5 = (Calendar) sdt1.clone();
            sdt5.add(Calendar.WEEK_OF_YEAR, -52);
            Calendar edt5 = (Calendar) edt1.clone();
            edt5.add(Calendar.WEEK_OF_YEAR, -52); //back 52 week
            Calendar sdt6 = (Calendar) sdt1.clone();
            sdt6.add(Calendar.WEEK_OF_YEAR, -53);
            Calendar edt6 = (Calendar) edt1.clone();
            edt6.add(Calendar.WEEK_OF_YEAR, -53); //back 53 week
            java.sql.Date sd1 = new java.sql.Date(sdt1.getTimeInMillis());
            java.sql.Date ed1 = new java.sql.Date(edt1.getTimeInMillis());
            java.sql.Date sd2 = new java.sql.Date(sdt2.getTimeInMillis());
            java.sql.Date ed2 = new java.sql.Date(edt2.getTimeInMillis());
            java.sql.Date sd3 = new java.sql.Date(sdt3.getTimeInMillis());
            java.sql.Date ed3 = new java.sql.Date(edt3.getTimeInMillis());
            java.sql.Date sd4 = new java.sql.Date(sdt4.getTimeInMillis());
            java.sql.Date ed4 = new java.sql.Date(edt4.getTimeInMillis());
            java.sql.Date sd5 = new java.sql.Date(sdt5.getTimeInMillis());
            java.sql.Date ed5 = new java.sql.Date(edt5.getTimeInMillis());
            java.sql.Date sd6 = new java.sql.Date(sdt6.getTimeInMillis());
            java.sql.Date ed6 = new java.sql.Date(edt6.getTimeInMillis());
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = (Calendar) c1.clone();
            c2.add(Calendar.DAY_OF_YEAR, -6 * 7);
            java.sql.Date rsd = new java.sql.Date(c2.getTimeInMillis());
            Calendar c3 = (Calendar) c2.clone();
            c3.add(Calendar.DAY_OF_YEAR, -48 * 7);
            java.sql.Date rsd487 = new java.sql.Date(c3.getTimeInMillis());
            java.sql.Date red = new java.sql.Date(c1.getTimeInMillis());
            Calendar c4 = (Calendar) c1.clone();
            c4.add(Calendar.DAY_OF_YEAR, -48 * 7);
            java.sql.Date red487 = new java.sql.Date(c4.getTimeInMillis());
            getInfo.setDate(1, sd6);
            getInfo.setDate(2, ed4);
            getInfo.setDate(3, sd3);
            rs = getInfo.executeQuery();
            j = 0;//25sec
            while (rs.next()) {//compute Q values for each stock item
                //for each Sale record
                sku = rs.getInt("Sku");
                pWaste = rs.getInt("Waste");
                //TDateTime itsDate,itsTime,itsDateTime;
                int pWasteType = rs.getInt("Waste") & 7;
                aSale = (pWasteType == SaleType.NORMAL.value() || pWasteType == SaleType.CHARGED.value());
                java.sql.Date itsDate = rs.getDate("S.WhenCreated");
                p1 = !itsDate.before(sd1) && !itsDate.after(ed1) && aSale;
                p2 = !itsDate.before(sd2) && !itsDate.after(ed2) && aSale;
                p3 = !itsDate.before(sd3) && !itsDate.after(ed3) && aSale;
                p4 = !itsDate.before(sd4) && !itsDate.after(ed4) && aSale;
                p5 = !itsDate.before(sd5) && !itsDate.after(ed5) && aSale;
                p6 = !itsDate.before(sd6) && !itsDate.after(ed6) && aSale;
                pRoci = (itsDate.after(rsd) && itsDate.before(red)
                        || itsDate.after(rsd487) && itsDate.before(red487))
                        && aSale;
                if (p1 || p2 || p3 || p4 || p5 || p6 || pRoci) {
                    //if within any time slot
                    itsProduct = rs.getLong("P.ID");
                    if (itsProduct == 0L) {
                        break;
                    }
                    itsQuantity = rs.getInt("Quant");
                    if (rs.getInt("Sku") > 2) {
                        addToQs.setInt(8, sku);
                        addToQs.setInt(1, p1 ? itsQuantity : 0);
                        addToQs.setInt(2, p2 ? itsQuantity : 0);
                        addToQs.setInt(3, p3 ? itsQuantity : 0);
                        addToQs.setInt(4, p4 ? itsQuantity : 0);
                        addToQs.setInt(5, p5 ? itsQuantity : 0);
                        addToQs.setInt(6, p6 ? itsQuantity : 0);
                        addToQs.setInt(7, pRoci ? itsQuantity : 0);
                        addToQs.executeUpdate();
                        j++;
                    }
                }
            }//+7:35sec
            getInfo.close();//+7:35sec
            //for each stock item set QT values
            np = Main.getConnection().prepareStatement("UPDATE " + newTable
                    + " SET QT=GREATEST(Q1,Q2,Q3,Q4,Q5,Q6)");
            np.executeUpdate();
            String nps = " SELECT Products.ID,"
                    + "Products.Price," + newTable + ".Minimum,"
                    + "PackSuppliers.Price,Packs.Size," + newTable + ".ID,"
                    + "PackSuppliers.WhenCreated,Packs.Code,"
                    + newTable + ".Quantity,"
                    + newTable + ".Tax, "
                    + newTable + ".QT, "
                    + newTable + ".QRoci, "
                    + newTable + ".Department, "
                    + "Taxes.Rate "
                    + "FROM " + newTable + " JOIN Products ON Products.Sku="
                    + newTable + ".ID "
                    + "JOIN Packs ON Products.ID=Packs.Product "
                    + "JOIN PackSuppliers ON Packs.ID=PackSuppliers.Pack "
                    + "JOIN Taxes ON Taxes.ID="+ newTable +".Tax "
                    + "WHERE PackSuppliers.Supplier=? AND "
                    + newTable + ".StockType<>6 "
                    + "AND QT>(" + newTable + ".Quantity - CONVERT(" + newTable + ".Minimum,SIGNED)) "
                    + "ORDER BY " + newTable
                    + ".ID,PackSuppliers.WhenCreated DESC ";
            np = Main.getConnection().prepareStatement(nps);
            String S;
            try {
                np.setInt(1, supplier);
                rs = np.executeQuery();
                int lastStock = -1;//7:15sec
                //now scan through each stock item to see which to re-order
                while (rs.next()) {
                    sku = rs.getInt(newTable + ".ID");
                    if(sku==1278){
                        int jj=0;
                    }
                    if (sku == lastStock) {//so only one order for each product
                        rs.next();
                        continue;
                    }
                    lastStock = sku;
                    product = rs.getLong("Products.ID");
                    price = rs.getInt("Price");
                    code = rs.getString("Code");
                    size = rs.getInt("Size");
                    qt = rs.getInt("QT");
                    qroci=rs.getInt("QRoci");
                    department = rs.getInt("Department");
                    if (size == 0) {
                        size = 1;
                    }
                    packSupplierPrice = rs.getInt("PackSuppliers.Price");
                    np2 = Main.getConnection().prepareStatement(
                            "INSERT INTO OrderLines "
                            + "(ID,OrderNo,Sku,Product,Department,"
                            + "Roci,PackSize,PackPrice,Quantity,Tax,"
                            + "Code,WeeklySales,SellBy) "
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    np2.setNull(1, Types.INTEGER);
                    np2.setInt(2, theOrder);
                    np2.setInt(3, sku);
                    np2.setLong(4, product);
                    np2.setInt(5, department);
                    p = size;
                    n = qroci / 8;//four weeks this year, four weeks last year
                    w = weeksCredit;

                    //float X;//wastes
                    //X=OrdersForm3->StockTable->FieldByName("Q6")->AsInteger;
                    //N  number of items sold per week
                    //r  retail price less VAT
                    //c  cost price each less VAT
                    //W  weeks credit
                    //X  wastes per week
                    //P  pack size
                    //I  interest rate - try 20%
                    //S  Shelf Rent - try 23p per week
                    //K  Current stock
                    //Q  quantity needed, includin current stock
                    //G  order period
                    //Z shelf capitalization  -  try Â£20
                    //try 5200(RN-CN-S)/(Z+C/2-WNC/2P)
                    //float I=.2;
                    //float Q=quantityNeeded;
                    //float Z=20;
                    //if(SQ==0&&QT==0)Q=0.1;
                    //float G=Period;
                    //shelf rent is zero for now but less any payment
                    c = packSupplierPrice;
                    int denom = (c * (p - n * w));
                    if (denom == 0) {
                        denom = 1;
                    }
                    rate = rs.getInt("Rate");
                    if (Main.shop.regimeIs() == Regime.REGISTERED
                            || Main.shop.regimeIs() == Regime.SALESTAX) {
                        //need to reduce retail price by tax
                        price = 10000 * price / (10000 + rate);
                    }
                    r = price;
                    if (r == 0) {
                        //if retail price is 0, make it Â£100, so will be noticed
                        r = 10000;
                    }
                    if (p <= n * w) {
                        //stock sold before payment
                        yield = 10000.; //arbitrary high value
                    } else {
                        yield = 200 * 52 * (n * (r - c/p) - s) / denom;
                    }
                    np2.setDouble(6, yield);
                    np2.setInt(7, size);
                    np2.setInt(8, packSupplierPrice);
                    sq = rs.getInt("Quantity");
                    if (sq < 0) {
                        sq = 0; //if SQ<0 then assume it is 0
                    }
                    min = rs.getInt("Minimum");
//                    orderQuantity = ((qt - sq + min) / (size + 1)) + 1;
                    //floor(((max((qt - sq + min),1)-1)/size)+1,1);
//                    orderQuantity = Math.max((qt - sq + min-1)/size+1,1);
                    orderQuantity = (Math.max(qt - sq + min-1, 1)/size) + 1;
                    if (orderQuantity <= 0) {
                        continue;
                    }
                    np2.setInt(9, orderQuantity);
                    np2.setInt(10, tax);
                    np2.setString(11, code);
                    np2.setInt(12, weeklySales);
                    Calendar sellBy = Calendar.getInstance();
                    //packSize*30/QRoci days
                    if(qroci==0){
                        daysToSellOut=0;
                    }else{
                        daysToSellOut = p*60/qroci;//use 60 for two years figures
                    }
                    sellBy.add(Calendar.DAY_OF_YEAR, daysToSellOut);
                    np2.setDate(13, new java.sql.Date(sellBy.getTimeInMillis()));
                    np2.executeUpdate();
                }
                np.close();//+7sec
                //DROP the table for next time
                np = Main.getConnection().prepareStatement("DROP TABLE " + newTable);
                np.executeUpdate();
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, "OrderGenerate.inner ", "Exception: " + ex.getMessage()+ex.getStackTrace());
//                ex.printStackTrace();

            }
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "OrderGenerate.outer ", "Exception: " + ex.getMessage());
        }
    }

    public void run() {
        execute(supplier, deliveryDate, nextDeliveryDate);
        done=true;
//        Main.salesScreen.greyOrders(true);   
    }
}
