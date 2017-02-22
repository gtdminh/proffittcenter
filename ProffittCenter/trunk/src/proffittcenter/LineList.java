/*
 * LineList.java
 *
 * Created on 03 October 2006, 17:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author David Proffitt
 */
public class LineList implements Iterator {

    private static ArrayList<Line> lines;

    /**
     * @return the lines
     */
    public static ArrayList<Line> getLines() {
        return lines;
    }
    private int lineSelected;
    private int total;
    private int toPay;
    private int tax1;
    Iterator<Line> it;
    private boolean dirty = true;
    int q;
    int x;
    int ix;
    int oldIx;
    int offerType;
    int saleCount = 0;
    int c = 0;
    //int l = 0;
    int quantityNormal = 0;
    int limit = 0;
    //int ts;
    int y;
    int z = 0;
    int quantityDiscounted;
    int price;
    Long p = 0L;
    String productDescription = "";
    boolean first = false;
    Calendar todayCal = Calendar.getInstance();
    java.sql.Date today = new java.sql.Date(todayCal.getTimeInMillis());
    private int quantity;
    Line l;
    private int taxRate;
    private int oldLineSelected = -1;
    private static String matchOffers = "SELECT Offers.*,Products.*,Skus.Tax FROM Offers,Products,Skus"
            + " WHERE Offers.Product=Products.ID And Offers.Product=? "
            + "AND Products.Sku=Skus.ID "
            + "AND Offers.StartDate<=? AND (Offers.EndDate>=? OR Offers.StartDate=Offers.EndDate)";
    private static String updateOffer = "UPDATE Offers SET Discounted=?,Included=?,Normal=?,SaleCount=?,Z=?,LimitValue=? WHERE IX=?";

    private static String resetOffer = "UPDATE Offers SET SaleCount=0,Discounted=0,Included=0,Z=0,DiscountedPrice=0";
    private static String updateOffer2 = "UPDATE Offers SET Discounted=?,Included=?,Normal=?,SaleCount=?,Z=?,LimitValue=?,DiscountedPrice=? WHERE IX=?";
//    private static String findOffer = "SELECT Offers.*,Products.Price,Products.Description FROM Offers,Products WHERE Offers.Product=Products.ID " 
//            + "And Offers.Product=? AND Offers.StartDate<=? AND (Offers.EndDate>=? OR Offers.StartDate=Offers.EndDate)";
    private static String findOffer = "SELECT DISTINCT * FROM Offers WHERE product=?  AND StartDate<=? AND (EndDate>=? )";

    private static String onOffer = "SELECT DISTINCT IX, X,Y,OfferType,SaleCount,LimitValue,DiscountedPrice FROM Offers WHERE SaleCount<>0 AND StartDate<=? AND (EndDate>=? OR StartDate=EndDate)";
    private int offerPrice;
    private long product;
    private int quantityIncluded;
    private int includedPrice;
    private int discountedPrice;
    private int savings;
    private int discountTotal;
    private int ownUseSavings;
    private int changedPrice;
    private int originalPrice;
    private boolean pricedOver;
    private boolean discounted;
    private long charge;
    private long numerator;
    private long denominator;
    private String description;
    private long barcode;
    private String track;
    private int encode;
    private int wholesalePrice;
    private int packSize;
    private long rate1;
    private int tax2;
    private long rate2;
    private Integer[] offerData = {x, quantityDiscounted, quantityIncluded, quantityNormal, z, discountedPrice, saleCount};
    private Map<Integer, Integer[]> offers = new HashMap<Integer, Integer[]>();
    private int disPrice;
    private int qty;
    private ApplyOffers applyOffers;

    /**
     * Creates a new instance of LineList
     */
    private void resetIterator() {
        it = lines.iterator();
    }

    public LineList() {
        lines = new ArrayList<proffittcenterworkingcopy.Line>();
        toPay = 0;
        tax1 = 0;
        lineSelected = -1;
        //it= lines.iterator();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean() {
        dirty = false;
    }

    public void add(Line line) {
        line = pennyItems(line);
        lineSelected = lines.size() - 1;
        lines.add(line);
        lineSelected = lines.size() - 1;
        dirty = true;
    }

    public void add(int row, Line line) {
        setSelection(row);
        lineSelected++;
        lines.add(row, line);
        lineSelected = lines.size() - 1;
        dirty = true;
    }

    public int size() {
        return lines.size();
    }

    public Line getSelectedLine() {
        if (lineSelected < 0) {
            return null;
        }
        if (lineSelected >= lines.size()) {
            lineSelected = lines.size() - 1;
        }
        return lines.get(lineSelected);
    }

    public void setSelection(int row) {
        if (row >= 0 && row < lines.size()) {
            oldLineSelected = lineSelected;
            lineSelected = row;
        } else {
            lineSelected = -1;
        }
    }

    public int getSelection() {
        return lineSelected;
    }

    public int getOldSelection() {
        return oldLineSelected;
    }

    public void deleteSelectedLine() {
        if (lineSelected > -1) {
            l = lines.get(lineSelected);
            lines.remove(lineSelected);
            lineSelected = lines.size() - 1;
        }
    }

    public Line getValueAt(int i) {
        if (i < 0 || i >= lines.size()) {
            return null;
        }
        return lines.get(i);
    }

    public void setValueAt(int row, Line line) {
        if (row < 0 || row >= lines.size()) {
            return;
        }
        lines.add(row, line);
    }

    public void replaceLineAt(int row, Line line) {
        if (row < 0 || row >= lines.size()) {
            return;
        }
        setSelection(row);
        deleteSelectedLine();
        add(row, line);
    }

    public void setQuantityAt(int row, int qty) {
        if (row < 0 || row >= lines.size()) {
            return;
        }
        Line line = getValueAt(row);
        line.setQuantity(qty);
        setSelection(row);
        deleteSelectedLine();
        setSelection(row);
        add(line);
    }

    public void setPriceAt(int row, int price) {
        if (row < 0 || row >= lines.size()) {
            return;
        }
        Line line = getValueAt(row);
        line.setRetailPrice(price);
        setSelection(row);
    }

    public void setOriginalPriceAt(int row, int price) {
        if (row < 0 || row >= lines.size()) {
            return;
        }
        Line line = getValueAt(row);
        line.setOriginalPrice(price);
        setSelection(row);
    }

    public int getTotal() {
        savings = 0;
        discountTotal = 0;
        total = 0;
        for (int i = 0; i < size(); i++) {
            Line localLine = getValueAt(i);
            disPrice = localLine.getRetailPrice();

            qty = localLine.getQuantity();
            savings += (localLine.getRetailPrice() - disPrice) * qty;
            discountTotal += (localLine.getRetailPrice() - disPrice) * qty;
            if (localLine.getEncode() == NewProduct.NOTENCODE) {
                total += (qty * disPrice);
            } else if (localLine.getEncode() == NewProduct.ENCODEBYPRICEPARITY 
                    || localLine.getEncode() == NewProduct.ENCODEBYPRICENOPARITY
                    || localLine.getEncode() == NewProduct.ENCODEBYPRICE5) {
                total += (qty * disPrice);
            } else {
                charge = qty * disPrice;
                if (charge > 0) {
                    total += (charge + 5000) / 10000;
                } else {
                    total += (charge - 5000) / 10000;
                }
            }
        }
        Regime rr = Main.shop.regimeIs();
        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
            total += getTax2();
        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            total += getTax1() + getTax2();//need getTax not just tax as otherwise 0
        }
        int r = Main.shop.getRounding();
        discountTotal = discountTotal * 10 + r * 5;
        Main.salesScreenFunctions.setDiscountTotal((discountTotal / (10 * r)) * r);
        total = total * 10 + r * 5;
        if (total < 0) {
            return (total / (10 * r)) * r - 1;
        }
        return (total / (10 * r)) * r;
    }

    public int getTax1() {
        long rp;
        long qt;
        long en;
        long ch;
//            float fTax = 0;
        float[] fTaxes1 = new float[10];
        long[] longTaxes1 = new long[10];
        for (int i = 0; i < fTaxes1.length; i++) {//zero the array
            fTaxes1[i] = 0;
        }
        for (int i = 0; i < longTaxes1.length; i++) {//zero the array
            longTaxes1[i] = 0;
        }
        Regime rr = Main.shop.regimeIs();
        Line localLine = null;
        for (int i = 0; i < size(); i++) {
            localLine = getValueAt(i);
            product = localLine.getProduct();
            tax1 = localLine.getTaxID();
            tax1 = Math.min(tax1, fTaxes1.length - 1);//so tax in range
            rate1 = localLine.getTaxRate();
            qt = localLine.getQuantity();
            rp = localLine.getRetailPrice();
            en = localLine.getEncode();
            if (rr == Regime.REGISTERED) {
                if (en == NewProduct.NOTENCODE) {
                    numerator = qt * rp * 10000 * rate1;
                    denominator = 10000 + rate1;
//                        charge = (qt * rp * 10000 * rate1 / (10000 + rate1));
                    longTaxes1[tax1] += numerator / denominator;
                } else if (en == NewProduct.ENCODEBYPRICEPARITY 
                        || en == NewProduct.ENCODEBYPRICENOPARITY
                        || en == NewProduct.ENCODEBYPRICE5) {
//                        charge = (qt * rp * 10000 * rate1) / (10000 + rate1);

                    longTaxes1[tax1] += (qt * rp * 10000 * rate1) / (10000 + rate1);
                } else {
                    //encoded by weight
//                        charge = qt * rp * rate1 / (10000 + rate1);
                    longTaxes1[tax1] += (qt * rp * rate1) / (10000 + rate1);
                }
            } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                ch = qt * rp;
                if (en == NewProduct.NOTENCODE) {
                    if (qt >= 0) {
//                            charge = ch * rate1;
                        longTaxes1[tax1] += ch * rate1;
                    } else {
//                            charge = ch * rate1;
                        longTaxes1[tax1] += ch * rate1;
                    }
                } else if (en == NewProduct.ENCODEBYPRICEPARITY 
                        || en == NewProduct.ENCODEBYPRICENOPARITY
                        || en == NewProduct.ENCODEBYPRICE5) {
                    if (qt >= 0) {
//                            charge = (ch * rate1);
                        longTaxes1[tax1] += (ch * rate1);
                    } else {
//                            charge = (ch * rate1);
                        longTaxes1[tax1] += (ch * rate1);
                    }
                } else if (en == NewProduct.ENCODEBYWEIGHTPARITY 
                        || en == NewProduct.ENCODEBYWEIGHTNOPARITY
                        || en == NewProduct.ENCODEBYWEIGHT5) {
                    if (ch >= 0) {
//                            charge = (ch * rate1 + 50000) / 10000;
                        longTaxes1[tax1] += (ch * rate1 + 5000) / 1000;
                    } else {
//                            charge = (ch * rate1 - 50000) / 10000;
                        longTaxes1[tax1] += (ch * rate1 - 50000) / 10000;
                    }
                }
            }
        }
        if (localLine == null) {
            return 0;
        }
        int r = Main.shop.getRounding();
        tax1 = 0;
        for (int i = 0; i < longTaxes1.length; i++) {//sum the taxes
            if (rr == Regime.REGISTERED) {
                if (longTaxes1[i] >= 0) {
                    tax1 += (longTaxes1[i] + 5000) / 10000;
                } else {
                    tax1 += (longTaxes1[i] - 5000) / 10000;
                }
            } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                tax1 += (longTaxes1[i] + 5000) / 10000;
            } else {
                tax1 += (longTaxes1[i] + 5000) / 10000;
            }
        }
        return tax1;
    }

    public int getTax2() {
        long rp;
        long qt;
        long en;
        long ch;
//            float fTax = 0;
        float[] fTaxes2 = new float[10];
        long[] longTaxes2 = new long[10];
        for (int i = 0; i < fTaxes2.length; i++) {//zero the array
            fTaxes2[i] = 0;
        }
        for (int i = 0; i < longTaxes2.length; i++) {//zero the array
            longTaxes2[i] = 0;
        }
        Regime rr = Main.shop.regimeIs();
        Line localLine = null;
        for (int i = 0; i < size(); i++) {
            localLine = getValueAt(i);
            product = localLine.getProduct();
            tax2 = localLine.getTax2ID();
            tax2 = Math.min(tax2, fTaxes2.length - 1);//so tax in range
            rate2 = localLine.getTax2Rate();
            qt = localLine.getQuantity();
            rp = localLine.getRetailPrice();
            en = localLine.getEncode();
//                if (rr == Regime.REGISTERED) {
//                    if (en == NewProduct.NOTENCODE) {
//                        numerator = qt * rp * 10000 * rate2;
//                        denominator = 10000 + rate2;
////                        charge = (qt * rp * 10000 * rate2 / (10000 + rate2));
//                        longTaxes2[tax2] += numerator/ denominator;
//                    } else if (en == NewProduct.ENCODEBYPRICEPARITY || en == NewProduct.ENCODEBYPRICENOPARITY) {
////                        charge = (qt * rp * 10000 * rate2) / (10000 + rate2);
//
//                        longTaxes2[tax2] += (qt * rp * 10000 * rate2) / (10000 + rate2);
//                    } else {
//                        //encoded by weight
////                        charge = qt * rp * rate2 / (10000 + rate2);
//                        longTaxes2[tax2] += (qt * rp * rate2 )/ (10000 + rate2);
//                    }
//                } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            ch = qt * rp;
            if (en == NewProduct.NOTENCODE) {
                if (qt >= 0) {
//                            charge = ch * rate2;
                    longTaxes2[tax2] += ch * rate2;
                } else {
//                            charge = ch * rate2;
                    longTaxes2[tax2] += ch * rate2;
                }
            } else if (en == NewProduct.ENCODEBYPRICEPARITY 
                    || en == NewProduct.ENCODEBYPRICENOPARITY
                    || en == NewProduct.ENCODEBYPRICE5) {
                if (qt >= 0) {
//                            charge = (ch * rate2);
                    longTaxes2[tax2] += (ch * rate2);
                } else {
//                            charge = (ch * rate2);
                    longTaxes2[tax2] += (ch * rate2);
                }
            } else if (en == NewProduct.ENCODEBYWEIGHTPARITY 
                    || en == NewProduct.ENCODEBYWEIGHTNOPARITY
                    || en == NewProduct.ENCODEBYWEIGHT5) {
                if (ch >= 0) {
//                            charge = (ch * rate2 + 50000) / 10000;
                    longTaxes2[tax2] += (ch * rate2 + 5000) / 1000;
                } else {
//                            charge = (ch * rate2 - 50000) / 10000;
                    longTaxes2[tax2] += (ch * rate2 - 50000) / 10000;
                }
            }
//                } 
        }
        if (localLine == null) {
            return 0;
        }
        int r = Main.shop.getRounding();
        tax2 = 0;
        for (int i = 0; i < longTaxes2.length; i++) {//sum the taxes
            tax2 += (longTaxes2[i] + 5000) / 10000;
//                if (rr == Regime.REGISTERED) {
//                    if(longTaxes2[i]>=0){
//                        tax2 += (longTaxes2[i] + 5000) / 10000;
//                    } else {
//                        tax2 += (longTaxes2[i] - 5000) / 10000;
//                    }
//                } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
//                    tax2 += (longTaxes2[i] + 5000) / 10000;
//                } else {
//                    tax2 += (longTaxes2[i] + 5000) / 10000;
//                }
        }
        return tax2;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Line next() {
        return it.next();
    }

    @Override
    public void remove() {
        it.remove();
    }

    public Iterator iterator() {
        it = lines.iterator();
        return it;
    }

    public void clear() {
        lines.clear();
    }

    private Line pennyItems(Line line) {
        //for penny items swap price and quantity
        //also change packsize, wholesale
        if (line.getRetailPrice() == 1) {
            //need to change n items at a penny to one item at n pence
            int n = line.getQuantity();
            if (n >= 0) {
                line.setQuantity(1);
                line.setRetailPrice(n);
                line.setOriginalPrice(n);//no longer a penny item
//                int w=line.getWholesalePrice();
//                w=n*line.getWholesalePrice();
//                line.setWholesalePrice(n*line.getWholesalePrice());
            } else {
                line.setQuantity(-1);
                line.setRetailPrice(-n);
                line.setOriginalPrice(-n);//no longer a penny item
            }
        }
        return line;
    }

    /**
     * replace two identical lines by one, times two
     */
    private void collapse() {
        for (int cnt1 = 0; cnt1 < lines.size() - 1; cnt1++) {//for each line
            Line line1 = lines.get(cnt1);
            long product1 = line1.getProduct();
            int price1 = line1.getRetailPrice();
            int q1 = line1.getQuantity();
            int orrigPrice1 = line1.getOriginalPrice();
            int encode1 = line1.getEncode();
            for (int cnt2 = cnt1 + 1; cnt2 < lines.size(); cnt2++) {//to each other line
                Line line2 = lines.get(cnt2);
                long product2 = line2.getProduct();
                int price2 = line2.getRetailPrice();
                int q2 = line2.getQuantity();
                int orrigPrice2 = line2.getOriginalPrice();
                int encode2 = line2.getEncode();
                if (product1 == product2 && price1 == price2 && orrigPrice1 == orrigPrice2 && encode1 == NewProduct.NOTENCODE) {
                    //update line2 to sum and set line1 to 0
                    line2.setQuantity(q1 + q2);
                    line1.setQuantity(0);
                    q1 = 0;
                } else if (product1 == product2 && price1 > price2 && q1 != 0 && orrigPrice1 == orrigPrice2) {
                    //swap so cheapest last
                    line2.setRetailPrice(price1);
                    line2.setQuantity(q1);
                    line1.setRetailPrice(price2);
                    line1.setQuantity(q2);
                    q1 = q2;
                    price1 = price2;
                }
            }
        }
    }

    private int newLine(int cnt) {
        //insert a line at cnt+1
        Line line = new Line(0, "", 0, 0L, 0, 0, "", 0, false, false, 1, 0, 1, 0, 0,0,0,0, 0, 0, 0, 0);
        setSelection(cnt);
        add(line);
        return cnt++;
    }

        private void offersSetup() {
        //insertion
        //scan the sale lines to see what will be affected by an offer
//clear the Offers table
        try {
            //query1: clear all salecount,Discounted,Included,Z
            PreparedStatement query1 = Main.getConnection().prepareStatement(resetOffer);
            query1.executeUpdate();
//Now scan through the sale lines every time they change
//counting up the number of each product involved in an offer
//and saving back into the Offers table
            saleCount = 0;
            for (int cnt = 0; cnt < lines.size(); cnt++) {
                quantityNormal = 0;
                c = 0;
                quantityDiscounted = 0;
                z = 0;
                limit = 0;
                discountedPrice = 0;
                lineSelected = cnt;
                Line line = getSelectedLine();
                p = line.getProduct();//get the barcode of the product
                price = line.getOriginalPrice();
                pricedOver = line.isPricedOver();
                discounted = line.isDiscounted();
//query2: match offers with product
                PreparedStatement query2 = Main.getConnection().prepareStatement(findOffer);
                query2.setLong(1, p);
                query2.setDate(2, today);
                query2.setDate(3, today);
//query2: match offers with product
                ResultSet r2 = query2.executeQuery();
                if (!r2.first() || pricedOver || discounted) {//next line if not an offer
                    r2.close();
                    continue;
                }
//                productDescription = r2.getString("Products.Description");
                q = line.getQuantity();
                x = r2.getInt("X");
                y = r2.getInt("Y");
                ix = r2.getInt("IX");
                saleCount = r2.getInt("SaleCount");
                limit = r2.getInt("LimitValue");
                discountedPrice += q * price;
                r2.close();
//query3: increase salecount by ?
                PreparedStatement query3 = Main.getConnection().prepareStatement(
                        "UPDATE Offers SET SaleCount=SaleCount+?,DiscountedPrice=DiscountedPrice+? WHERE IX=?");
                query3.setInt(1, q); //Increases SaleCount=sc+q for all offers with same ix
                query3.setInt(2, discountedPrice);
                query3.setInt(3, ix);
                query3.executeUpdate();
                discountedPrice = 0;
            }
            PreparedStatement query4 = Main.getConnection().prepareStatement(onOffer);
            query4.setDate(1, today);
            query4.setDate(2, today);
//query4: select applicable offer
            ResultSet r4 = query4.executeQuery();
            ix = 0;
            oldIx = 0;
            if (!r4.next()) {
                r4.close();
                return; //no offers to explore
            }
            do {
//
                ix = r4.getInt("IX");
                oldIx = ix;
                offerType = r4.getInt("OfferType");//5 is ?
                saleCount = r4.getInt("SaleCount");
                x = r4.getInt("X");
                y = r4.getInt("Y");
                limit = r4.getInt("LimitValue");
//                discountedPrice = r4.getInt("DiscountedPrice");
                quantityNormal = 0;
                c = 0;
                quantityDiscounted = 0;
//                z = 0;
                if (saleCount > limit && limit > 0) {
//no limit if limit==0
                    quantityNormal = saleCount - limit;
                    saleCount -= quantityNormal;
                }
                if (offerType == Offer.QUANTITY) {//ie 5 or more Heinz beans at 50p each
                    if (saleCount > 0) {//positive
                        if (saleCount < x) {
                            quantityNormal += saleCount;
                            quantityDiscounted = 0;
                        } else {
                            quantityDiscounted = saleCount;
//normal = 0;
                        }
                    } else {
                        if (-saleCount < x) {//negative
                            quantityNormal += saleCount;
                            quantityDiscounted = 0;
                        } else {
                            quantityDiscounted = saleCount;
//normal = 0;
                        }
                    }
                } else if (offerType == Offer.PACK) {//ie 6pack Stella for £5
                    if (saleCount > 0) {//positive
                        if (saleCount < x) {//none at discount
                            quantityNormal += saleCount;
                            quantityDiscounted = 0;
                            quantityIncluded = 0;
                        } else {
                            quantityDiscounted = 1;//saleCount / x;
                            quantityNormal = saleCount % x;
                            quantityIncluded = saleCount - quantityNormal - quantityDiscounted; //quantityDiscounted * (x-1);
                        }
                    } else {//negative
                        if (-saleCount < x) {//none at discount, saleCount negative
                            quantityNormal += saleCount;
                            quantityDiscounted = 0;
                            quantityIncluded = 0;
                        } else {
                            quantityDiscounted = -1;
                            quantityNormal += saleCount % x;
                            quantityIncluded = saleCount - quantityNormal - quantityDiscounted;
                        }
                    }
                }
//query5: update offer values
                PreparedStatement query5 = Main.getConnection().prepareStatement(updateOffer);
//need to save these values
                query5.setInt(1, quantityDiscounted); //Discounted
                query5.setInt(2, quantityIncluded); //Included
                query5.setInt(3, quantityNormal); //Normal - number at normal price
                query5.setInt(4, saleCount); //SaleCount
                z = (quantityIncluded + quantityDiscounted) / x;
                query5.setInt(5, (quantityIncluded + quantityDiscounted) / x); //Z
                query5.setInt(6, limit);//limit
                query5.setInt(7, ix); //IX
                query5.executeUpdate();
            } while (r4.next());
            r4.close();
        } catch (SQLException ex) {
            Logger.getLogger(LineList.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        } catch (java.lang.IndexOutOfBoundsException ex) {
            Logger.getLogger(LineList.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
    }

//    originalPrice-(discountedPrice-(quantityNormal*originalPrice))/quantityDiscounted)
    private void offersCalculate() {
        //now apply the offers
        try {
            includedPrice = 0;
            int size = lines.size();
            for (int cnt = 0; cnt < size; cnt++) {
                //once for each line in sale
                first = false;
                Line line2;
                line2 = lines.get(cnt);
                p = line2.getProduct();
                pricedOver = line2.isPricedOver();
                discounted = line2.isDiscounted();
                changedPrice = line2.getRetailPrice();
                originalPrice = line2.getOriginalPrice();
                //query2: match offers with product
                PreparedStatement query2 = Main.getConnection().prepareStatement(matchOffers);
                query2.setLong(1, p);
                query2.setDate(2, today);
                query2.setDate(3, today);
                ResultSet rs2 = query2.executeQuery();
                if (!rs2.next() || pricedOver || discounted) {//no offer for this product
                    rs2.close();
                    continue;
                }//else offer found
                offerType = rs2.getInt("OfferType");//the offer type 3 for pack
                saleCount = rs2.getInt("SaleCount");//5 the number sold
                x = rs2.getInt("X");//2
                y = rs2.getInt("Y");//150 the offer price
                z = rs2.getInt("Z");//0
                quantityNormal = rs2.getInt("Normal");//1
                quantityDiscounted = rs2.getInt("Discounted");//2
                discountedPrice = rs2.getInt(("DiscountedPrice"));
                q = line2.getQuantity();//5 number sold on current line
                ix = rs2.getInt("IX");//1
                //need to get price from line
                price = line2.getOriginalPrice();//100
                changedPrice = line2.getRetailPrice();
                taxRate = line2.getTaxRate();
                product = rs2.getLong("Product");
                productDescription = rs2.getString("Products.Description");
                quantityIncluded = rs2.getInt("Included");//2
                encode = rs2.getInt("Encoded");
                track = "";
                tax1 = rs2.getInt("tax");
                wholesalePrice = 0;
                packSize = 0;
                int factor = z;
                rs2.close();
                if (offerType == Offer.PACK) {
                    if (q <= quantityNormal && quantityNormal > 0
                            || q >= quantityNormal && quantityNormal < 0) {
                        line2.setQuantity(q);
                        line2.setRetailPrice(price);
                        discountedPrice -= q * originalPrice;//take off normal sales
                        quantityNormal -= q;
                        q = 0;
                    } else if (q <= quantityIncluded && quantityIncluded > 0
                            || q >= quantityIncluded && quantityIncluded < 0) {
                        line2.setQuantity(q);
                        line2.setRetailPrice(price);
                        quantityIncluded -= q;
                        q = 0;
                    }
                    if (q != 0 && (quantityNormal != 0 || quantityIncluded != 0)) {
                        line2.setQuantity(quantityNormal + quantityIncluded);
                        line2.setRetailPrice(price);
                        discountedPrice -= quantityNormal * originalPrice;
                        q -= quantityNormal + quantityIncluded;
                        quantityNormal = 0;
                        quantityIncluded = 0;
                        if ((q <= quantityDiscounted && quantityDiscounted > 0 || q >= quantityDiscounted && quantityDiscounted < 0) && q != 0) {
                            Line line3 = new Line();
                            line3.setQuantity(q);
                            line3.setDescription(productDescription);
                            int retail = price * quantityDiscounted + factor * y - discountedPrice;//((price+y)*q -discountedPrice)/quantityDiscounted;
                            line3.setRetailPrice(retail * quantityDiscounted);
                            line3.setProduct(product);
                            line3.setTaxrate(taxRate);
                            line3.setOriginalPrice(originalPrice);
                            line3.setTrack(track);
                            line3.setEncode(encode);
                            line3.setTax(tax1);
                            line3.setWholesalePrice(wholesalePrice);
                            line3.setPackSize(packSize);
                            lines.add(cnt, line3);
//                            Main.sale.add(cnt, line3);
                            size++;
                            quantityDiscounted -= q;
                            q = 0;
                            cnt++;
                        }
                    } else if (q != 0 && quantityDiscounted != 0) {
                        line2.setQuantity(q);
                        line2.setOriginalPrice(price);
                        int retail = price * quantityDiscounted + factor * y - discountedPrice;//((price+y)*q -discountedPrice)/quantityDiscounted;
                        line2.setRetailPrice(retail * quantityDiscounted);
                        line2.setOriginalPrice(originalPrice);
                        discountedPrice = 0;
                        quantityDiscounted -= q;
                        q = 0;
                    }
                } 
         else if (offerType == Offer.QUANTITY) {
                    if (quantityDiscounted != 0) {
                        int origPrice = line2.getOriginalPrice();
                        int newPrice = (origPrice * (100 - y) + 50) / 100;
                        if (newPrice > price) {
                            newPrice = price;
                        }
                        line2.setRetailPrice(newPrice);
                        line2.setQuantity(q);
                        q = 0;
                    } else {
                        line2.setQuantity(q);
                        line2.setRetailPrice(price);
                        q = 0;
                    }
                }
                //query5: update offer values
                PreparedStatement query5 = Main.getConnection().prepareStatement(updateOffer2);
                //need to save these values
                query5.setInt(1, quantityDiscounted); //Discounted
                query5.setInt(2, quantityIncluded); //Included
                query5.setInt(3, quantityNormal); //Normal
                query5.setInt(4, saleCount); //SaleCount  - does not change?
                query5.setInt(5, z); //Normal - does not change
                query5.setInt(6, limit);
                query5.setInt(7, discountedPrice);
                query5.setInt(8, ix); //IX  -
                query5.executeUpdate();

            }
        } catch (SQLException ex) {
            Logger.getLogger(LineList.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        } catch (java.lang.IndexOutOfBoundsException ex) {
            Logger.getLogger(LineList.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
    }


    public void update() {
        //recalculate totals etc.
        applyOffers = new ApplyOffers();
        ApplyOffers.setup();
        ApplyOffers.calculate();
//        offersSetup();
//        offersCalculate();
        collapse();
        eliminateZeroes();
    }

    public int getSavings() {
        savings = 0;
        for (int i = 0; i < size(); i++) {
            Line localLine = getValueAt(i);
            int originalPrice = localLine.getOriginalPrice();
            int currentPrice = localLine.getRetailPrice();
            int encoded = localLine.getEncode();
            if (encoded == NewProduct.NOTENCODE) {
                if (originalPrice > currentPrice && originalPrice != 0) {
                    //may have increased price in price over or be a zero priced item
                    savings += localLine.getQuantity() * (localLine.getOriginalPrice() - localLine.getRetailPrice());
                }
            } else {
                if (originalPrice > currentPrice && originalPrice != 0) {
                    //may have increased price in price over or be a zero priced item
                    savings += (localLine.getQuantity() * (localLine.getOriginalPrice() - localLine.getRetailPrice())) / 10000;
                }
            }
        }
        return savings;
    }

    private void eliminateZeroes() {
        for (int cnt1 = lines.size() - 1; cnt1 >= 0; cnt1--) {//count down for each line
            Line line1 = lines.get(cnt1);
            if (line1.getQuantity() == 0) {
                lines.remove(cnt1);
            }
            lineSelected = lines.size() - 1;
        }
    }

    long getLastProduct() {
        int last = lines.size() - 1;
        if (last >= 0) {
            Line line = lines.get(last);
            return line.getProduct();
        }
        return -1;
    }

    void setTotal(int total) {
        this.total = total;
    }

    void setTax(int tax) {
        this.tax1 = tax;
    }
}
