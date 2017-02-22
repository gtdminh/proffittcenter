 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 *
 * @author Dave
 */
public class SalesScreenFunctions {

    private static boolean wasEnter = false;

    /**
     * Lets you know if Enter or Tab was used for data entry
     *
     * @return the wasEnter
     */
    public static boolean isWasEnter() {
        return wasEnter;
    }

    /**
     * @param aWasEnter the wasEnter to set
     */
    public static void setWasEnter(boolean aWasEnter) {
        wasEnter = aWasEnter;
    }
  
    private boolean describeIt;
    private boolean weighIt;
    private boolean priceIt;
    private boolean priceEntry;
    private int selection;
    private LineList aSale;
    private ResourceBundle bundle1 = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen1");
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen");
    private javax.swing.JLabel dataLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField hidden;
    private javax.swing.JTable jSaleTable;
    private javax.swing.JTable resultsTable;
    private javax.swing.JTextField dataEntry;
    private javax.swing.JFrame salesScreen;
    private JTabbedPane jTabbedPane1;
    private boolean noIncrement;
    private boolean partPaid;
    public boolean isReceived;
    private String data;
    private int taxID;
    private int qty = 1;
    private int total;
    private int tax1;
    private int coupon;
    private int toPay;
    private int debit;
    private int cheque;
    private SaleTableModel model;
    private boolean productIdentification;
    private int savings;
    private boolean pharmacistApproval;
    private boolean ageCheck;
    private boolean newSaleStarted;
    private boolean receiptRequired;
    private long customer = SaleType.CUSTOMER.code() * 10000l;//Default customer
    private int discount;
    private boolean saleComplete;
    private String status = "";
    private int change;
    private String top;
    private String bottom;
    private String surname;
    private String firstName;
    private boolean agencyFirstTime;
    private String agencyString = "";
    private int cash;
    private SaleType st;
    private int newTotal;
    private int sale;
    private long product;
    private int sku;
    private int price;
    private int encode;
    private int rate;
    private int packSize;
    private int wholesalePrice;
    private boolean trackIt = false;
    private String track;
    private long barcode;
    private int discountTotal;
    private int fixedFloat;
    private boolean pharmacistApproved;
    private int verifiedAge;
    private int age;
    private boolean shift;
    private boolean noClear;
    private String rawData;
    private boolean stop;
    private String dateOut;
    private String description;
    private int creditLimit;
    private int balance;
    private boolean onlineProductFound;
    private long lastBarCode;
    private boolean agency;
    private int originalPrice;
    private int taxRate;
    private String restriction;
    private Line line;
    private String shortenedData7;
    private String shortenedData8;
    private int weight;
    private boolean alpha;
    private int charge;
    private String s;
    private String numericData;
    private String operatorData = "";
    private boolean isCharged;
    public static boolean invoiceRequired;
    private int multiPack;
    private int tax2ID;
    private int tax2Rate;
    private ResultSet rs2;
    private int tax2;
    private int newTax1;
    private int stockQuantity;
    private int ix = 0;
    private int x = 0;
    private int y = 0;
    private int offerType = 0;
    private int limit = 0;
    private int discountedPrice = 0;
    private int z = 0;
    private int n;
    private int sign;
    private short stockType;
    private int delay;
    private boolean discounted;
    private boolean isPricedOver;
    private String shortenedData6;

    public boolean blankEntry(String data) {
        if (!wasEnter || isPriceIt() || isPriceEntry() || isDescribeIt() || isWeighIt() || isTrackIt()) {
            return false;
        }
        //increase count by one
        setSelection(getaSale().getSelection());
        if (isNoIncrement()) {
            setNoIncrement(false);
            return false;
        }
        if (isNoIncrement() || isDescribeIt() || partPaid || isReceived) {//|| selection < 0
            setNoIncrement(false);
            if (getSelection() < 0 && !isReceived) {
                clearSale();
            }
            return false;
        }
        setSelection(getaSale().getSelection());
        if (data.length() == 0 && getSelection() >= 0) {//Main.sale.size() > 0
            if (getaSale().getValueAt(getSelection()).getEncode() != NewProduct.NOTENCODE) {
                return false;
            }
            Line localLine = getaSale().getSelectedLine();
            taxID = localLine.getTaxID();
            if (taxID == 6) {
                return false;
            }
            setQty((Integer) localLine.getQuantity());
            if (getQty() > 0) {
                setQty((Integer) (getQty() + 1));
            } else {
                setQty((Integer) (getQty() - 1));
            }
            localLine.setQuantity(getQty());
            setTotal(getaSale().getTotal());
            tax1 = getaSale().getTax1();
            tax2 = getaSale().getTax2();
            if (getCoupon() != 0) {
                toPay = getTotal() - getDebit() - getCoupon() - getCheque();
            }
            updateSale();
            updateResult();
            poleUpdate(getaSale().getSelectedLine());
            getaSale().setSelection(getaSale().size() - 1);
            setSelection(getaSale().getSelection());
            scrollToVisible(jSaleTable, getSelection(), 0);
            getModel().fireTableDataChanged();
            Audio.play("Beep");
            setQty((Integer) 1);
            Main.speech.say(Main.speech.asCash(getTotal()));
            return true;
        } else if (data.length() == 0) {
            getaSale().setSelection(-1);
            return true;//recognised but does nothing quietly
        }
        return false;// try other options
    }

    public void clearSale() {
        //clears all fields on screen
        newSaleStarted = false;
        setPriceEntry(false);
        qty = 1;
        if (Main.operator.getIntAuthority() < 5) {
            dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
        }
        isReceived = false;
        getModel().clear();
        getaSale().setSelection(-1);
        setSelection(-1);
        setReceiptRequired(false);
        updateSale();
        setCustomer(10000380000l);
        discount = 0;
        setPriceIt(false);
        setDescribeIt(false);
        setSaleComplete(true);
        setVerifiedAge(0);
        setAge(0);
        Main.salesScreen.nextShift = false;
        clearResult();

    }

    public void updateSale() {
        productIdentification = false;
        updatePole();
        getaSale().update();
        jSaleTable.repaint();
        setQty((Integer) 1);
        int size = getaSale().size() - 1;
        if (size >= 0) {
            jSaleTable.setRowSelectionInterval(size, size);
            scrollToVisible(jSaleTable, size, 0);
        } else {
            //do nothing
        }
        setQty((Integer) 1);
        Main.salesScreen.setTabCount();
    }
    
    public int getItemsSold(){
        //returns the number of items on the sales screen
        int items=model.getItemsSold();        
        return items;
    }

    public void updateResult() {
        if (productIdentification || isPriceIt() || isDescribeIt() || isPharmacistApproval() || isAgeCheck()) {
            Audio.play("Tada");
        }
        setSavings(getaSale().getSavings());
        tax1 = getTax1();
        tax2 = getaSale().getTax2();
        setTotal(getTotal());
        if (getCoupon() != 0) {
            toPay = getTotal() - getDebit() - getCoupon() - getCheque();
        }
        resultsTable.repaint();
    }

    public void poleUpdate(Line selectedLine) {
        if (selectedLine == null) {
            return;
        }
        if (isSaleComplete()) {
            return;
        }
        speechUpdate(selectedLine);
        top = selectedLine.getQuantity() + "x" + selectedLine.getDescription();
        charge = selectedLine.getCharge();
        setTotal(getaSale().getTotal());
        String tot = Main.customerMessages.getString("Total") + (new Money(getTotal())).toString();
        bottom = (new Money(charge)).toString();
        bottom = StringOps.fixLengthUntrimmed(bottom, 20 - tot.length());
        bottom += tot;
        Main.pole.execute(top, bottom);
    }

    public void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    public void updatePole() {
        if (getStatus().equalsIgnoreCase(bundle1.getString("SaleComplete"))) {
            if (getChange() == 0) {
                top = Main.customerMessages.getString("ThankYou");
                bottom = Main.customerMessages.getString("ComeBackSoon");
            } else {
                top = Main.customerMessages.getString("YourChange") + (new Money(getChange())).toString();
                bottom = Main.customerMessages.getString("ThankYou");
            }
            Main.pole.execute(top, bottom);
        } else if (getStatus().equalsIgnoreCase(Main.customerMessages.getString("Charged"))) {
            top = Main.customerMessages.getString("Charged") + ": " + Main.customerMessages.getString("ThankYou");
            bottom = getSurname() + "," + firstName;
            Main.pole.execute(top, bottom);
        } else if (getaSale().size() != 0) {
            Line aLine = getaSale().getSelectedLine();
            poleUpdate(getaSale().getSelectedLine());
        } else {
            if (Main.operator.operatorName == null) {
            } else {
                Main.pole.execute(Main.operator.operatorName,
                        Main.customerMessages.getString("ReadyToServeYou"));
            }
        }
    }

    public boolean coupon(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || getaSale().getSelection() == -1 || data.length() < 1 || data.length() > 12 || data.length() < 12 || !data.startsWith("99")) {
            return false;
        }
        int couponValue;
        try {
            couponValue = Integer.parseInt(data.substring(9, 12));
        } catch (NumberFormatException ex) {
            Main.logger.log(Level.SEVERE, "SalesScreenFunctions.Coupon", "NumberFormatException" + ex);
            return false;
        } catch (StringIndexOutOfBoundsException ex) {
            Main.logger.log(Level.SEVERE, "SalesScreenFunctions.Coupon", "StringIndexOutOfBounds" + ex);
            return false;
        }
        setTotal(getaSale().getTotal());
        int paid = 0;
        if (couponValue != 0 && getTotal() - getDebit() - getCoupon() >= couponValue) {
            //aSale.setTotal(total - couponValue);
            partPaid = true;//stops further sales
            setTotal(getTotal() - couponValue);
            tax1 = getaSale().getTax1();
            tax2 = getaSale().getTax2();
            coupon = couponValue;
            if (getTotal() <= couponValue) {
                if (!saleComplete(SaleType.NORMAL)) {
                    return false;
                }
                resultsTable.repaint();
                dataEntry.setText("");
                Audio.play("CashReg");
                setSaleComplete(true);
//                String top, bottom;
                if (getTotal() - getDebit() - getCheque() - getCoupon() == 0) {
                    top = Main.customerMessages.getString("ThankYou");
                    bottom = Main.customerMessages.getString("ComeBackSoon");
                } else {
                    top = Main.customerMessages.getString("YourCashBack") + (new Money(getTotal() - getDebit() - getCheque() - getCoupon())).toString();
                    bottom = Main.customerMessages.getString("ThankYou");
                }
                Main.pole.execute(top, bottom);
            }
            return true;
        }
        return false;
    }

    public boolean saleComplete(SaleType st) {
        if (Main.operator.getIntAuthority() == 4) {
            return true;//trainee
        }
        try {
            int fixedProfit = 1;
            if (st == SaleType.OWNUSE) {
                //need to know net prices of goods - now done elswhere
                fixedProfit = 0;
            }
            tax1 = getaSale().getTax1();
            tax2 = getaSale().getTax2();
            //create sale
            PreparedStatement np;
            np = Main.getConnection().prepareStatement(
                    "INSERT INTO Sales"
                    + "(ID,Total,Tax,Cash,Cheque,Debit,Coupon, Customer,Waste,Operator,TillId,FixedProfit,Tax2)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            np.setNull(1, Types.INTEGER); //ID
            int x = getTotal();
            setTotal(getTotal());
            np.setInt(2, getTotal());
            np.setInt(3, getTax1());//tax
            np.setInt(4, getCash());
            np.setInt(5, getCheque());
            np.setInt(6, getDebit());
            np.setInt(7, getCoupon());
            np.setLong(8, getCustomer());
            np.setShort(9, st.value());//sale type - waste
            np.setInt(10, Main.operator.getOperator());
            np.setLong(11, Main.shop.getTillId());
            np.setInt(12, fixedProfit);
            np.setInt(13, getTax2());//tax2
            np.executeUpdate();
            PreparedStatement np1 = Main.getConnection().prepareStatement(
                    SQL.lastSalesId);
            ResultSet rs = np1.executeQuery();
            rs.first();
            setSale(rs.getInt(1));
            rs.close();
            PreparedStatement np2 = Main.getConnection().prepareStatement(
                    SQL.insertIntoSaleLines);
            PreparedStatement np3 = Main.getConnection().prepareStatement(
                    SQL.updateSku);
            PreparedStatement np4 = Main.getConnection().prepareStatement(
                    "SELECT Sku,Encoded,Rate,Products.Price, Products.MultiPack FROM Products,Skus,Taxes WHERE Products.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND Products.ID=?");
            newTotal = getTotal();
            newTax1 = 0;
            int lineCount = 0;
            for (Iterator it = getaSale().iterator(); it.hasNext();) {
                Line localLine = (Line) it.next();
                np2.setNull(1, Types.INTEGER);
                np2.setInt(2, getSale());
                setQty((Integer) localLine.getQuantity());
                np2.setInt(3, getQty());
                product = localLine.getProduct();
                price = localLine.getRetailPrice();
                np2.setLong(4, product);
                np4.setLong(1, product);
                ResultSet rs4 = np4.executeQuery();
                if (!rs4.first()) {
                    np4.close();
                    return false;
                } else {
                    sku = rs4.getInt("Sku");
                    encode = rs4.getInt("Encoded");
                    rate = rs4.getInt("Rate");
                    originalPrice = rs4.getInt("Price");
                    multiPack = rs4.getInt("MultiPack");
                }
                rs4.close();
//                price = line.getRetailPrice();//
                if (st == SaleType.RETURN || st == SaleType.WASTE || st == SaleType.OWNUSE) {
                    change = 0;
                    PreparedStatement np5 = Main.getConnection().prepareStatement(
                            SQL.lastDelivered);
                    np5.setLong(1, product);
                    ResultSet rs5 = np5.executeQuery();
                    if (rs5.first()) {
                        int id = rs5.getInt("ID");
                        packSize = rs5.getInt("Size");
                        if (packSize == 0) {
                            packSize = 1;
                        }
                        wholesalePrice = rs5.getInt("Price");
                        np5.close();
                        if (encode == NewProduct.NOTENCODE) {
                            int q = getQty();
                            int calcPrice = originalPrice;
                            int newPrice = price;
                            Regime rr = Main.shop.regimeIs();
                            if (rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                                if (st == SaleType.OWNUSE) {
                                    calcPrice = (getQty() * wholesalePrice * (10000 + rate) / 10000) / packSize;
                                    newPrice = (wholesalePrice * (10000 + rate) / 10000) / packSize;
                                    newTax1 += (calcPrice * (rate) / (10000 + rate));
                                } else {
                                    calcPrice = ((getQty() * wholesalePrice) / packSize * (10000 + rate) + 500) / 10000;
                                    newPrice = ((wholesalePrice) / packSize * (10000 + rate) + 500) / 10000;
                                }
                            }
                            newTotal += calcPrice - q * price;//new calculted price less original
                            localLine.setRetailPrice(newPrice);
                        } else if (encode == NewProduct.ENCODEBYWEIGHTNOPARITY || 
                                encode == NewProduct.ENCODEBYWEIGHTPARITY ||
                                encode == NewProduct.ENCODEBYWEIGHT5) {
                            //handle weight encoded products
                            int q = getQty();
                            int calcPrice = originalPrice;
                            int newPrice = price;
                            Regime rr = Main.shop.regimeIs();
                            if (rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                                calcPrice = ((q * wholesalePrice * 10000 + rate) / (packSize) + 500) / 10000;
                                newPrice = ((wholesalePrice) * (10000 + rate) / (packSize) + 500) / 10000;
                            }
                            newTotal += calcPrice - q * price;
                            localLine.setRetailPrice(newPrice);
                        } else if (encode == NewProduct.ENCODEBYPRICENOPARITY 
                                || encode == NewProduct.ENCODEBYPRICEPARITY
                                || encode == NewProduct.ENCODEBYWEIGHT5) {
                            //handle price encoded products
                            int q = getQty();
                            int calcPrice = originalPrice;
                            int newPrice = price;
                            Regime rr = Main.shop.regimeIs();
                            if (rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                                calcPrice = ((q * wholesalePrice) / packSize * (10000 + rate) + 500) / 10000;
                                newPrice = ((wholesalePrice) / packSize * (10000 + rate) + 500) / 10000;
                            }
                            newTotal += calcPrice - q * price;
                            localLine.setRetailPrice(newPrice);
                        }
                    } else {
                        //never had a delivery; should apply default margin
                        rs5.close();//close existing results

                    }
                    rs5.close();
                }
                PreparedStatement np5 = Main.getConnection().prepareStatement(
                        SQL.lastDelivered);
                np5.setLong(1, product);
                ResultSet rs5 = np5.executeQuery();
                if (rs5.first()) {
                    packSize = rs5.getInt("Packs.Size");
                    wholesalePrice = rs5.getInt("Price");
                    np5.close();
                } else {
                    //no pack
                    packSize = getQty();
                    Regime rr = Main.shop.regimeIs();
                    if (rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                        wholesalePrice = ((price * getQty() * 800) / (1000 + rate));
                    } else {
                        wholesalePrice = (price * getQty() * 80) / 100;
                    }
                    np5.close();
                }
                if (st == SaleType.OWNUSE) {
                    np2.setInt(5, wholesalePrice / packSize);
                } else {
                    np2.setInt(5, localLine.getRetailPrice());
                }
                np2.setString(6, localLine.getTrack());
                encode = localLine.getEncode();
                np2.setInt(7, encode);
                isPricedOver=localLine.isPricedOver();
                if(isPricedOver){
                    np2.setInt(8,1 );
                } else {
                    np2.setInt(8,0 );
                }
                discounted=localLine.isDiscounted();
                if(discounted){
                    np2.setInt(9, 1);
                } else {
                    np2.setInt(9,0);
                }
                np2.setInt(10, localLine.getOriginalPrice());
                taxID = localLine.getTaxID();
                np2.setInt(11, localLine.getTaxID());
                np2.setInt(12, localLine.getTaxRate());
                np2.setInt(13, wholesalePrice);
                np2.setInt(14, packSize);
                np2.executeUpdate();
                int count = localLine.getQuantity();
                np3.setInt(1, count * multiPack);
                np3.setInt(2, sku);
                np3.executeUpdate();
                if (getTotal() != newTotal) {
                    //modify total in sale
                    PreparedStatement np6 = Main.getConnection().prepareStatement(
                            "UPDATE Sales SET Total=?,Tax=? WHERE ID=?");
                    setTotal(newTotal);
                    if (newTax1 != 0) {
                        tax1 = newTax1;
                    } else {
                        tax1 = 0;
                    }
                    tax2 = 0;
                    np6.setInt(3, getSale());
                    np6.setInt(1, newTotal);
                    np6.setInt(2, tax1);//no tax on wastes
                    np6.executeUpdate();
                    setSavings(0);
//                    aSale.setTotal(total);
//                    aSale.setTax(tax);
                    updateResult();
                }
                localLine.setRetailPrice(wholesalePrice);
            }
            getModel().clear();
            getaSale().setSelection(-1);
            setSelection(-1);
            if (!agencyString.isEmpty()) {
                //there is an agencyString to display
                //use the dateLabel
                Main.salesScreen.displayAgncyString(getAgencyString());
//                dateLabel.setText(agencyString);
            }
            dataEntry.requestFocus();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "SalesScreenFunctions.saleComplete ", "SQL_Error:_" + ex.getMessage());
            return false;
        }
        return true;
    }

    public long getCustomer() {
        return customer;
    }

    public boolean requestTrack() {
        if (isTrackIt()) {
            setTrack(dataEntry.getText());
            if (getTrack().isEmpty()) {
                return false;
            }
            setTrackIt(false);
            getaSale().getSelectedLine().setTrack(getTrack());
            dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
            return true;
        }
        return false;
    }

    public boolean isOnOffer() {
        try {
            //see if last poroduct is in offers table
            String onOfferSql = "SELECT ID FROM Offers WHERE product = ?";
            PreparedStatement ps = Main.getConnection().prepareStatement(onOfferSql);
            ps.setLong(1, barcode);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                ps.close();
                return true;
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(SalesScreenFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Request a customer name if a product is tracked sets trackingAllready
     * true
     *
     * @return true if a customer is selected, either now or previously
     */
    public boolean tracked() {
        if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {//no customer selected
            setCustomer(Main.customers.execute("ByName", false));//SaleType.CUSTOMER.code() * 10000l +
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                //no customer was selected
                dataEntry.setText("");
                setTrackIt(false);
                updateResult();
                Main.salesScreen.setS("");
                return false;
            } else {
                setDiscount(Main.customer.getDiscount(getCustomer()));
                setFirstName(Main.customers.getFirstName());
                setSurname(Main.customers.getSurname());
            }
            updateResult();
            setTrackIt(true);
            setNoIncrement(true);
            return true;
        } else {
            return false;
        }
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setCustomer(long aCustomer) {
        this.customer = aCustomer;
    }

    /**
     * @return the discountTotal
     */
    public int getDiscountTotal() {
        return discountTotal;
    }

    /**
     * @param discountTotal the discountTotal to set
     */
    public void setDiscountTotal(int discountTotal) {
        this.discountTotal = discountTotal;
    }

    /**
     * @return the fixedFloat value
     */
    public int getFixedFloat() {
        return fixedFloat;
    }

    /**
     *
     * @param restriction, the restriction set in department restriction may be
     * pharmacist, age or tracked
     * @return true if was restricted
     */
    public boolean isRestricted(String restriction) {//returns true if accepted
        if (restriction.trim().isEmpty()) {
            return false;
        }
        if (restriction.charAt(0) == 'P' && !pharmacistApproved) {//Pharmacist approval required
            dataLabel.setText(bundle1.getString("PharmacistApproval"));
            dataEntry.setBackground(Color.yellow);
            setPharmacistApproval(true);
            dataEntry.setText("");
            return true;
        } else if (restriction.charAt(0) == 'T') {
            Audio.play("Tada");
//            productIdentification = true;//done in tracked
            dataEntry.setText("");
            if(tracked()){
                //get product identification
                dataLabel.setText(bundle1.getString("productIdentification"));
                dataEntry.setBackground(Color.yellow);
                setNoIncrement(true);
                return true;                
            }else {
                dataEntry.setText("");
                return false;
            }
        } else if (restriction.length() >= 1 && restriction.length() <= 2) {
            String a = StringOps.numericOnly(restriction);
            if (a.isEmpty()) {
                return false;
            }
            int n = Integer.parseInt(a);
            if (n > getVerifiedAge()) {
                Object[] arguments = {new Integer(n)};
                String message = java.text.MessageFormat.format(bundle1.getString("AgeLimit"), arguments);
                dataLabel.setText(message);
                dataEntry.setBackground(Color.yellow);
                setAgeCheck(true);
                setAge(n);
//                verifiedAge=Math.max(verifiedAge, n);
                dataEntry.setText("");
                return true;
            }
        }
        return true;
    }

    public boolean minus(String data) {
        //change sign
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || partPaid || isWeighIt()) {
            return false;
        }
        setData(data);
        if (getData().length() == 1 && Main.sale.size() > 0 && data.startsWith("-")) {
            if (isShift()) {
                for (int i = 0; i < getaSale().size(); i++) {
                    setQty((Integer) getModel().getValueAt(i, 0));
                    setQty(-getQty());
                    getModel().setValueAt(getQty(), i, 0);
                }
                Audio.play("Beep");
                getModel().fireTableDataChanged();
                Audio.play("Beep");
                setTotal(getaSale().getTotal());
                tax1 = getaSale().getTax1();
                tax2 = getaSale().getTax2();
                setSavings(getaSale().getSavings());
                getaSale().update();
                updatePole();
                updateResult();
                setQty((Integer) 1);
                return true;
            } else {
                setQty((Integer) getaSale().getSelectedLine().getQuantity());
                setQty(-getQty());
                getaSale().getSelectedLine().setQuantity(getQty());
                getModel().fireTableDataChanged();
                Audio.play("Beep");
                setTotal(getaSale().getTotal());
                tax1 = getaSale().getTax1();
                tax2 = getaSale().getTax2();
                setSavings(getaSale().getSavings());
                getaSale().update();
                updatePole();
                updateResult();
                setQty((Integer) 1);
                return true;
            }
        }
        return false;// try other options
    }

    /**
     * Delete the selected line. Should be the last item scanned unless another
     * line is first selected
     */
    public void delete() {
        if (dataEntry.getText().isEmpty() && !isWeighIt() && !isDescribeIt() && !isPriceIt() && !isPriceEntry()) {
            if (!partPaid) {
                getModel().deleteSelection();
                updateSale();
                int currentSelection = getaSale().size() - 1;
                if (currentSelection >= 0) {
                    jSaleTable.setRowSelectionInterval(getaSale().size() - 1, getaSale().size() - 1);
                    getaSale().setSelection(getaSale().size() - 1);
                    setSelection(getaSale().getSelection());
                    scrollToVisible(jSaleTable, getSelection(), 0);
                    setTotal(getaSale().getTotal());
                    tax1 = getaSale().getTax1();
                    tax2 = getaSale().getTax2();
                    setSavings(getaSale().getSavings());
                    Audio.play("Beep");
                } else {
                    noClear = false;
                    clearSale();
                    clearResult();
                    getaSale().setSelection(-1);
                    Audio.play("Beep");
                    setSaleComplete(true);
                }
                updateSale();
                updateResult();
            } else if (partPaid && isCharged) {
                noClear = false;
                clearSale();
                clearResult();
                getaSale().setSelection(-1);
                Audio.play("Beep");
                setSaleComplete(true);
            }
        }
    }

    public boolean dataEntryProcess(String processData) {
        boolean b = isSaleComplete();
        if (b && getData().length() > 0) {//any other character
            Main.salesScreenFunctions.clearResult();
        }
        String s = getOperatorData();
        setData(StringOps.stripSpaces(getData()));
        rawData = StringOps.stripSpaces(processData);
        if (operator(rawData)) {
            //change to operator
            Audio.play("Beep");
            dataEntry.requestFocus();
            dataEntry.setText("");
            return true;
        }
        if (Main.shop.getTillId() == 0 && getData().length() != 0) {
            //no sales for zero till
            Audio.play("Ring");
            JOptionPane.showMessageDialog(salesScreen, bundle1.getString("NoSales"));
            dataEntry.requestFocus();
            dataEntry.setText("");
            return true;
        }
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0) {
            if (newDescription(rawData)) {
            } else if (blankEntry(rawData)) {
            } else if (requestTrack()) {
            } else if (zeroPriced(rawData)) {
            } else if (zeroQuantity(rawData)) {
            } else if (charge(rawData)) {
            } else if (receive(rawData)) {
            } else if (newPrice(rawData)) {
            } else if (waste(rawData)) {
            } else if (hotKey(rawData)) {
            } else if (tender(rawData)) {
            } else if (product(rawData)) { //check for product
            } else if (minus(rawData)) {//negate the quantity
            } else if (priceOver(rawData, true)) {//overwrite the price
            } else if (timesN(rawData)) {
            } else if (coupon(rawData)) {
            } else if (discount(rawData)) {
            } else if (ownUse(rawData)) {
            } else if (operator(rawData)) {
                Main.salesScreen.setDataLabel("");
            } else if (customer(rawData)) {
            } else if (layawayRetrieve(rawData)) {
            } else {
//                dataEntry.selectAll();
//                dataEntry.requestFocus();
                setNoIncrement(false);
                if (rawData.length() > 0) {
                    Audio.play("Ring");
                }
                return true;
            }
        } else {
            if (getData().isEmpty()) {
                return true;
            }
            if (!isDescribeIt() && !isPharmacistApproval() && !isAgeCheck()) {
//                dataEntry.setText("");
            }
            setNoIncrement(true);
            Audio.play("Ring");
            return false;
        }
        if (!isDescribeIt() && !isPharmacistApproval() && !isAgeCheck()) {
            dataEntry.setText("");
        }
        return true;
    }

    public void clearResult() {
        setCustomer(SaleType.CUSTOMER.code() * 10000);
        setSurname("");
        setFirstName("");
        setStatus("");
        isReceived = false;
        isCharged = false;
//        setSaleComplete(false);
        partPaid = false;
        setPriceEntry(false);
        setPharmacistApproval(false);
        setTrackIt(false);
        setAgeCheck(false);
        toPay = 0;
        setTotal(0);
        debit = 0;
        cheque = 0;
        coupon = 0;
        tax1 = 0;
        tax2 = 0;
        setSavings(0);
        cash = 0;
        toPay = 0;
        change = 0;
        setStatus("");
        setVerifiedAge(0);
        discountTotal = 0;
        pharmacistApproved = false;
        setNoIncrement(false);
        setDiscount(0);
        stop = false;
        newSaleStarted = true;
        agencyFirstTime = true;
        setAgencyString("");
        Main.salesScreen.displayDate();
        resultsTable.repaint();
    }

    public boolean newDescription(String rawData) {
        if (!isDescribeIt()) {
            return false;
        }
        String strippedData = StringOps.stripSpaces(rawData);
        if (StringOps.numericOnly(strippedData).length() == strippedData.length()
                || rawData.length() < 2) {//code scanned
            Audio.play("Ring");
            dataEntry.selectAll();
            dataEntry.requestFocus();
            return true;
        }
        setDescribeIt(false);
        setPriceIt(true);
        description = StringOps.firstCaps(dataEntry.getText());
        if (rawData.length() > 50) {
            description = description.substring(0, 49);//truncate to maximum length
        }
        dataLabel.setText(bundle1.getString("Enter_price_in") + " " + Main.shop.getPennySymbol() + " ...");
        dataEntry.setBackground(Color.yellow);
        dataEntry.setBackground(Color.yellow);
        Audio.play("Tada");
        dataEntry.setText("");
        dataEntry.requestFocus();
        return true;
    }

    public boolean operator(String rawData) {
        String s = getOperatorData();
        setData(StringOps.numericOnly(s));
        if (getData().length() > 7 && getData().startsWith(SaleType.OPERATOR.codeString())) {
            if (getaSale().size() > 0) {//no changing operator during sales
                dataEntry.setText("");
                return false;
            }
            //an escape
            Calendar today = Calendar.getInstance();
            java.sql.Date d1 = new java.sql.Date(today.getTimeInMillis());
            String s1 = "180341" + d1.toString().substring(8);
            if (getOperatorData().substring(7).endsWith(s1)) {
                Main.operator.setIntAuthority(0);
                Main.operator.setOperator(SaleType.OPERATOR.value());
                Main.operator.operatorName = "Default";
                jTabbedPane1.setTitleAt(0, Main.operator.operatorName);
                //FormRestore.create(this, root);
                if (Main.shop.fixedFloat == 0 && Main.shop.getShiftFloat() == 0
                        && Main.operator.getIntAuthority() <= Operator.SALESPERSON) {
                    Main.shop.setShiftFloat(Main.selectFloat.execute());
                }
                Main.pole.execute(Main.operator.operatorName,
                        Main.customerMessages.getString("ReadyToServeYou"));
                return true;
            }
            //log in new operator
            String operator = "SELECT * FROM Operators WHERE ID=? ";
            try {
                PreparedStatement operatorQuery =
                        Main.getConnection().prepareStatement(operator);
                ResultSet rs;
                long op = 0;
                try {
                    op = Long.valueOf(getOperatorData());
                    op -= SaleType.OPERATOR.code() * 10000;
                } catch (Exception ex) {//no need to inform
                    Audio.play("Ring");
                    setOperatorData("");
                    return false;
                }
                operatorQuery.setLong(1, op);
                rs = operatorQuery.executeQuery();
                if (rs.first()) {
                    Main.operator.setIntAuthority(rs.getInt("Authority"));
                    Main.operator.setOperator(rs.getInt("ID"));
                    Main.operator.operatorName = rs.getString("Description");
                    jTabbedPane1.setTitleAt(0, Main.operator.operatorName);
                    if (Main.operator.getIntAuthority() == Operator.LOCKEDOUT) {
                        dataLabel.setText(bundle1.getString("LogIn"));
                        dataEntry.setBackground(Color.yellow);
                        jTabbedPane1.setTitleAt(0, bundle1.getString("Locked"));
                        Audio.play("Ring");
                        return false;
                    } else {
                        Main.salesScreen.setDataEntry("");
                        Main.salesScreen.setDataLabel("");
                        Audio.play("Vibes");
                    }
                    if (Main.shop.fixedFloat == 0 && Main.shop.getShiftFloat() == 0
                            && Main.operator.getIntAuthority() <= Operator.SALESPERSON) {
                        Main.shop.setShiftFloat(Main.selectFloat.execute());
                    }
                    if (getaSale().getSelection() < 0) {
                        Main.pole.execute(Main.operator.operatorName,
                                Main.customerMessages.getString("ReadyToServeYou"));
                        setSaleComplete(true);
                    }
                    Main.operator.setPermissions();
                    setOperatorData("");
                } else {
                    rs.close();
                    Main.operator.setIntAuthority(5);
                    Main.operator.setOperator(00);//not known
                    Main.operator.operatorName = "";
                    jTabbedPane1.setTitleAt(0, bundle1.getString("Locked"));
                    Main.pole.execute(Main.customerMessages.getString("Sorry"),
                            Main.customerMessages.getString("OutOfService"));
                    Audio.play("Ring");
                    dataLabel.setText(bundle1.getString("LogIn"));
                    dataEntry.setBackground(Color.yellow);
                    setOperatorData("");
                    return false;
                }
                rs.close();
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreenFunctions.operator ", "SQLException: " + ex.getMessage());
                Audio.play("Ring");
            }
            return true;
        }
        return false;
    }

    public boolean zeroPriced(String data) {
        if (!isPriceEntry()) {
            return false;
        }
        try {
            price = Integer.parseInt(data);
            setPriceEntry(false);
            priceOver( price+"@", false);
        } catch (NumberFormatException ex) {
            Audio.play("Ring");
            return false;
        }
        Main.salesScreen.setDataLabel("");
        return true;
    }

    public boolean zeroQuantity(String data) {
        if (!isWeighIt()) {
            return false;
        }
        try {
            setQty((Integer) Integer.parseInt(StringOps.numericOnly(data)));
        } catch (NumberFormatException ex) {
            dataEntry.selectAll();
            Audio.play("Ring");
            return false;
        }
        setSelection(getaSale().getSelection());
        getModel().setValueAt(getQty(), getSelection(), 0);
        getModel().fireTableDataChanged();
        tax1 = getaSale().getTax1();
        tax2 = getaSale().getTax2();
        getaSale().update();
        updatePole();
        updateResult();
        setQty((Integer) 1);
        Audio.play("Beep");
        weighIt = false;
        setSaleComplete(getaSale().getSelection() < 0);
        dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
        return true;
    }

    public boolean charge(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || getaSale().getSelection() == -1 || data.length() == 1 || data.length() >= 9) {
            return false;
        }
        String data1 = StringOps.numericOnly(data);//to strip spaces
        if (data1.length() == 7 && data1.equalsIgnoreCase(SaleType.CHARGED.codeString()) && getTotal() != 0 && !isSaleComplete()) {
            dataEntry.setText("");
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {//no customer selected
                setCustomer(Main.customers.execute("ByName", false));//SaleType.CUSTOMER.code() * 10000l +
                setCustomer(getCustomer());
                if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                    //no customer was selected
                    dataEntry.setText("");
                    return false;
                } else {
                    discount = Main.customer.getDiscount(getCustomer());
                    setFirstName(Main.customers.getFirstName());
                    setSurname(Main.customers.getSurname());
                    applyDiscount(discount);
                }
            }
            setTotal(getaSale().getTotal());
            // save the data
            change = 0;
            creditLimit = Main.customer.getCreditLimit(getCustomer());
            stop = Main.customer.getStop(getCustomer());
            balance = Main.balance.getBalance(getCustomer());
            if (getTotal() > 0 && (stop || creditLimit < balance + getTotal() - getDebit() - getCheque() - getCoupon())) {
                //exceeds credit limit
                Audio.play("TaDa");
                Object[] options = {bundle1.getString("Override"), bundle1.getString("PayCash")};
                int i = JOptionPane.showOptionDialog(salesScreen,
                        bundle1.getString("Cash_only"),
                        bundle1.getString("WarningMessage"),
                        JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                if (i != JOptionPane.YES_OPTION) {//no or cancel
                    //could pay cash
                    if (stop && creditLimit > 0) {
                        setNoIncrement(true);
                        return true;
                    }
                    toPay = getTotal() - creditLimit + balance - getDebit() - getCheque() - getCoupon();
                    partPaid = true;
                    setNoIncrement(true);
                    Audio.play("TaDa");
                    isCharged = true;
                    updateResult();
                    return true;
                }
                if (!Main.operator.isChangeCreditLimits() && !Main.operator.isOwner()) {//authority to override
                    setNoIncrement(true);
                    return true;
                }
            }
            //customer order number?
            //This is where salesscreen gets cleared
            Audio.play("CashReg");
            setStatus(bundle1.getString("Charged"));
            dataEntry.setText("");
            noClear = true;
            updateSale();
            updateResult();
            if (!saleComplete(SaleType.CHARGED)) {
                return false;
            }
            if (Main.hardware.isInvoicePrinter()) {
                Audio.play("TaDa");
                Main.customerDelivery.execute(getSale());
            }
            noClear = true;
            setNoIncrement(true);
            if (!Main.hardware.isInvoicePrinter()) {
                Main.receiptPrinter.printReceipt(getSale());
                setReceiptRequired(false);
            } else if (Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                Main.invoice.print(getSale(), getDiscount(), false);
                setInvoiceRequired(false);
            }
            setSaleComplete(true);
            return true;
        }
        return false;
    }

    public boolean receive(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || isReceived) {
            return false;
        }
//        setSaleComplete(false);
        String data1 = StringOps.numericOnly(data);//to strip spaces
        int totalCharged = 0;
        int totalReceived = 0;
        setSelection(getaSale().getSelection());//1000037
        if (data1.length() == 7 && (data1.compareTo(SaleType.RECEIVEDONACCOUNT.codeString()) == 0 || data1.compareTo(SaleType.CREDITCUSTOMER.codeString()) == 0) && getSelection() == -1) {
            if (data1.compareTo(SaleType.CREDITCUSTOMER.codeString()) == 0) {//1000043
                Main.salesScreen.nextShift = true;
            }
            clearResult();
            //need to identify the customer
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                setCustomer(Main.customers.execute("ByName", false));//SaleType.CUSTOMER.code() * 10000l +
            }
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000L) {
                //no customer selected
                dataEntry.setText("");
                return false;
            } else {
                isReceived = true;
                setSaleComplete(false);
                setFirstName(Main.customers.getFirstName());
                setSurname(Main.customers.getSurname());
                try {
                    //do the work of calculating unpaid bills
                    PreparedStatement ps = Main.getConnection().prepareStatement(
                            SQL.sumOfCharged);
                    ps.setLong(1, getCustomer());// - SaleType.CUSTOMER.code() * 10000l
                    ResultSet rs = ps.executeQuery();
                    if (rs.first()) {
                        totalCharged = rs.getInt("T");
                    }
                    rs.close();
                    ps = Main.getConnection().prepareStatement(
                            SQL.sumOfReceived);
                    ps.setLong(1, getCustomer());//- SaleType.CUSTOMER.code() * 10000l
                    rs = ps.executeQuery();
                    if (rs.first()) {
                        totalReceived = rs.getInt("T");
                    }
                    rs.close();
                    setTotal(totalCharged - totalReceived);
                    tax1 = 0;
                    tax2 = 0;
                } catch (SQLException ex) {
                    Main.closeConnection();
                    Audio.play("Ring");
                    Main.logger.log(Level.SEVERE, "SalesScreenFunctions.received ", "SQLException: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "SQLException: " + ex, "SalesScreen.Received", JOptionPane.ERROR_MESSAGE);
                }
                top = Main.customerMessages.getString("Total") + " " + Money.asMoney(getTotal());
                Main.pole.execute(top, "");
                setStatus(Main.customerMessages.getString("Balance"));
                updateResult();
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                return true;
            }
        }
        return false;
    }

    public boolean newPrice(String data) {
        if (!isPriceIt()) {
            return false;
        }
        String localData = StringOps.stripSpaces(data);
        if (StringOps.numericOnly(localData).length() != localData.length()) {
            return false;
        }
        localData = StringOps.numericOnly(localData);
        if (localData.length() == 0) {
            return false;
        }
        try {
            price = Integer.parseInt(localData);
        } catch (NumberFormatException ex) {
            Audio.play("Ring");
            dataEntry.selectAll();
            dataEntry.requestFocus();
            return false;
        }
        setPriceIt(false);
        try {
            PreparedStatement np;
            //have price and description, need to store new product
            if (!onlineProductFound) {
                Connection connectionOnline = Main.getConnectionOnline();
                if (connectionOnline != null) {//save new product online
                    np = connectionOnline.prepareStatement(
                            "INSERT INTO " + " products(ID,Description,theLocale) " + " VALUES(?,?,?)");
                    np.setLong(1, barcode);//ID
                    np.setString(2, description);//description
                    np.setString(3, Main.hardware.myLanguage);//local
                    np.executeUpdate();
                    //Main.closeConnectionOnline();
                }
            }
            Main.newProduct.execute(barcode, description, price);
        } catch (SQLException ex) {
            Main.closeConnection();
            Main.logger.log(Level.SEVERE, "SalesScreenFunctions.newPrice ", "SQLException: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen.NewPrice", JOptionPane.ERROR_MESSAGE);
        }
        //then start afresh
        dataLabel.setText(bundle.getString("SalesScreen.dataLabel.text"));
        description = "";
        price = 0;
        Audio.play("Beep");
        product("" + barcode);
        dataEntry.setText("");
        dataEntry.requestFocus();
        return true;
    }

    public boolean waste(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || data.length() == 0) {
            dataEntry.setText("");
            return false;
        }
        if (getCustomer() != SaleType.CUSTOMER.code() * 10000l) {
            dataEntry.setText("");
            return false;
        }
        //waste the sale
        String data1 = StringOps.numericOnly(data);
        String s1 = SaleType.WASTE.codeString();
        String s2 = SaleType.RETURN.codeString();
        if (data1.compareTo(SaleType.WASTE.codeString()) == 0 && getaSale().size() != 0) {
            lastBarCode = 0l;
            if (isSaleComplete() || getaSale().size() == 0) {
                dataEntry.setText("");
                clearResult();
                return true;
            }
            if (!saleComplete(SaleType.WASTE)) {
                return false;
            }
            updateResult();
            dataEntry.setText("");
            Audio.play("CashReg");
            setSaleComplete(true);
            top = Main.customerMessages.getString("Wasted_items");
            bottom = (new Money(getTotal())).toString();
            Main.pole.execute(top, bottom);
            setStatus(Main.customerMessages.getString("Wastes"));
            noClear = true;
            return true;
        } else if (data1.compareTo(SaleType.RETURN.codeString()) == 0 && getaSale().size() != 0) {
            lastBarCode = 0l;
            if (isSaleComplete() || getaSale().size() == 0) {
                dataEntry.setText("");
                clearResult();
                return true;
            }
            updateResult();
            if (!saleComplete(SaleType.RETURN)) {
                return false;
            }
            dataEntry.setText("");
            Audio.play("CashReg");
            setSaleComplete(true);
            top = Main.customerMessages.getString("Returned_items");
            bottom = (new Money(getTotal())).toString();
            Main.pole.execute(top, bottom);
            setStatus(Main.customerMessages.getString("Returns"));
            noClear = true;
            return true;
        }
        return false;
    }

    public void addSpaces(String data) {
        String s = "";
        if (data.length() > 1) {
            s = data.substring(1);
        }
        if (StringOps.containsOnlyNumbersAndSpaces(data)) {
            String data1 = StringOps.numericOnly(data);
            int dataLength = data1.length();
            if (dataLength > 2) {//ensures just numeric
                //insert a space at position 2
                data1 = data1.substring(0, dataLength - 2) + " " + data1.substring(dataLength - 2);
                dataLength = data1.length();//will have extra space
                if (dataLength > 6) {
                    //insert a space at position 2
                    data1 = data1.substring(0, dataLength - 6) + " " + data1.substring(dataLength - 6);
                }
                dataEntry.setText(data1);
            } else {
                dataEntry.setText(data1);
            }
        } else if (StringOps.containsOnlyNumbersAndSpaces(s)) {
            String data1 = StringOps.numericOnly(s);
            int dataLength = data1.length();
            if (dataLength > 2) {//ensures just numeric
                //insert a space at position 2
                data1 = data1.substring(0, dataLength - 2) + " " + data1.substring(dataLength - 2);
                dataLength = data1.length();//will have extra space
                if (dataLength > 6) {
                    //insert a space at position 2
                    data1 = data1.substring(0, dataLength - 6) + " " + data1.substring(dataLength - 6);
                }
                dataEntry.setText(data.substring(0, 1) + data1);
            } else {
                dataEntry.setText(data.substring(0, 1) + data1);
            }
        }
    }

    public void showHashes(String data) {
        //The purpose of this is to display an operator id as hashes
        data = StringOps.stripSpaces(data);
        if (data.length() < 4) {
            setOperatorData("");
            return;
        }
        if (data.equalsIgnoreCase("10000370000")) {
            dataEntry.setText("Password");
        } else if (data.equalsIgnoreCase("1000037")) {
            char[] fill = new char[data.length()];
            Arrays.fill(fill, '#');
            dataEntry.setText(new String(fill));
            setOperatorData("1000037");
        } else if (getOperatorData().startsWith("1000037")) {
            char[] fill = new char[data.length()];
            Arrays.fill(fill, '#');
            if (getOperatorData().length() > data.length()) {
                String newSting = StringOps.numericOnly(data);
                setOperatorData(getOperatorData().substring(0, data.length() - newSting.length()) + newSting);
            } else {
                setOperatorData(getOperatorData() + data.substring(getOperatorData().length(), data.length()));
            }
            dataEntry.setText(new String(fill));
        } else {
            setOperatorData("");
        }
    }

    public boolean hotKey(String data) {
        if (data.length() == 0 || isDescribeIt() || isPriceIt() || isWeighIt() || isPriceEntry() || isWeighIt() || partPaid) {
            return false;
        }
        String localData = data;
        if (localData.length() > 1 && localData.charAt(0) == '-') {
            setQty(-1);
            localData = localData.substring(1);
        }
        if (localData.charAt(0) >= 'a' && localData.charAt(0) <= 'z' || localData.charAt(0) >= 'A' && localData.charAt(0) <= 'Z') {
            //localData is a hot key
            int n = 1; //n is the multiplyer, default to 1
            localData = localData.toUpperCase();
            char c = localData.charAt(0);
            barcode = c - 64 + 1000000l;
            if (localData.length() > 7) {
                return false;
            }
            if (localData.length() > 1) {
                if (!StringOps.isNumericOnly(localData.substring(1))) {
                    return false;
                }
                try {
                    n = Integer.parseInt(localData.substring(1));
                } catch (NumberFormatException ex) {
                    dataEntry.setText("");
                    dataEntry.requestFocus();
                    return false;
                }
            }
            setQty((Integer) getQty() * n);
            if (product("" + barcode)) {
                setQty((Integer) 1);
                return true;
            } else {
                setQty((Integer) 1);
                return false;
            }
        } else {
            setQty((Integer) 1);
            return false;
        }
    }

    public boolean product(String data) {
        String shortenedData;
        if (isPriceIt() || isDescribeIt() || isWeighIt() || isPriceEntry()) {
            return false;
        }
        String strippedData = StringOps.stripSpaces(data);
        //see if it is a product
        if (strippedData.length() < 6 || isDescribeIt() || isPriceIt() || isPriceEntry() || partPaid || isReceived || productIdentification || isAgeCheck()) {
            return false; //8 or more digits
        }
        if (strippedData.startsWith("1000038")) {
            return false;
        }
        shortenedData = StringOps.maxLength(strippedData);
        try {
            if (!StringOps.isNumericOnly(shortenedData) || shortenedData.length() > 19) {
                lastBarCode = 0L;
                return false;
            }
            barcode = Long.parseLong(StringOps.numericOnly(shortenedData));
        } catch (NumberFormatException ex) {
//            data = "";
            dataEntry.setText("");
            Audio.play("Ring");
            lastBarCode = 0L;
            return false;
        }
        if (barcode < 100000 && barcode >= -100000) {
            //will be a tender
            barcode = 0; //do not leave with a value
            lastBarCode = 0L;
            return false; //not big enough to be a bar code
        }
        long i = SaleType.NORMAL.code();
        i = SaleType.DELIVERY.code();
        if (barcode >= SaleType.NORMAL.code() && barcode <= SaleType.DELIVERY.code()) {
            lastBarCode = 0L; //do not leave with a value
            return false;
        }
        if (barcode < 0) {//a negative
            setQty(-1 * getQty());
            barcode = -barcode;
            shortenedData = shortenedData.substring(1);//drop the minus
        }
        String nextLine;
        try {
            String lur = "SELECT Rate FROM Taxes WHERE ID = ?";
            PreparedStatement pslur = Main.getConnection().prepareStatement(lur);
            //now need to check till/product table
            if (NewProduct.couldBeEncoded(shortenedData)) {
                //see if shortened data is a product
                shortenedData6 = NewProduct.shortenForEncoded6(shortenedData);
                shortenedData7 = NewProduct.shortenForEncoded7(shortenedData);
                shortenedData8 = NewProduct.shortenForEncoded8(shortenedData);
                PreparedStatement productLookup =
                        Main.getConnection().prepareStatement(
                        SQL.selectProduct);
                productLookup.setString(1, shortenedData);
                productLookup.setString(2, shortenedData7);
                productLookup.setString(3, shortenedData8);
                productLookup.setString(4, shortenedData6);
                ResultSet rs = productLookup.executeQuery();
                if (rs.first()) {
                    encode = rs.getInt("Encoded");
                    if (encode == NewProduct.ENCODEBYPRICEPARITY 
                            || encode == NewProduct.ENCODEBYPRICENOPARITY
                            || encode == NewProduct.ENCODEBYPRICE5) {
                        product = rs.getLong("Products.ID");
                        barcode = product;
                        if(encode == NewProduct.ENCODEBYPRICE5){
                            price = Integer.parseInt(shortenedData.substring(shortenedData.length() - 6, shortenedData.length() - 1));
                        } else {
                            price = Integer.parseInt(shortenedData.substring(shortenedData.length() - 5, shortenedData.length() - 1));
                        }
                        if (price == 0) {
                            dataLabel.setText(bundle1.getString("Enter_price_in") + " " + Main.shop.getPennySymbol() + " ...");
                            dataEntry.setBackground(Color.yellow);
                            setPriceEntry(true);
                            setSaleComplete(false);
                            Audio.play("TaDa");
                            qty = 1;
//                            return true;
                        }
                        description = rs.getString("Description") + " @";
                        sku = rs.getInt("Sku");
                        taxRate = rs.getInt("Taxes.Rate");
                        taxID = rs.getInt("Tax");
                        tax2ID = rs.getInt("Tax2");
                        if (tax2ID == 0) {
                            taxRate = 0;
                        } else {
                            //need to lookup in database
                            pslur.setInt(1, tax2ID);
                            rs2 = pslur.executeQuery();
                            if (rs2.first()) {
                                tax2Rate = rs2.getInt("Rate");
                            } else {
                                tax2Rate = 0;
                            }
                        }
                        //check restrictions
                        restriction = rs.getString("Departments.Restriction");
                        rs.close();
                        if (!isRestricted(restriction)) {
//                            return false;
                        }
                        originalPrice = price;
                        price = (price * 10 * (100 - discount) + 500) / 1000;
//                        qty = getQty();//test
                        line = new Line(getQty(), description, price, barcode,
                                taxRate, originalPrice,
                                "", encode, false, false, taxID, wholesalePrice,
                                packSize, tax2ID, tax2Rate, ix, x, y, z, offerType,
                                limit, discountedPrice);
                        getaSale().add(line);
                        getModel().fireTableDataChanged();
                        setQty((Integer) 1);
                        updateSale();
                        poleUpdate(getaSale().getSelectedLine());
                        if (!isPriceEntry()) {//in which case sound already
                            Audio.play("Beep");
                        }
                        rs.close();
                        setNoIncrement(false);//
                        tax1 = getaSale().getTax1();
                        tax2 = getaSale().getTax2();
                        setTotal(getaSale().getTotal());
                        setSavings(getaSale().getSavings());
                        updateResult();
                        Main.speech.say((description + Main.speech.asCash(getTotal())));
                        setSaleComplete(false);
                        return true;
                    } else if (encode == NewProduct.ENCODEBYWEIGHTPARITY 
                            || encode == NewProduct.ENCODEBYWEIGHTNOPARITY
                            || encode == NewProduct.ENCODEBYWEIGHT5) {
                        product = rs.getLong("Products.ID");
                        barcode = product;
                        weight = NewProduct.getEncodedData4(shortenedData);
                        price = rs.getInt("price");
                        setQty((Integer) weight * getQty());
                        if (getQty() == 0) {//zero priced
                            dataLabel.setText(bundle1.getString("Enter_weight.."));
                            dataEntry.setBackground(Color.yellow);
                            weighIt = true;
                            setSaleComplete(false);
                            Audio.play("TaDa");//done in updateResults()
                            setQty((Integer) 1);//stops line being deleted
//                            return true;
                        }
                        String g = "";
                        if (Main.shop.getKgSymbol().equalsIgnoreCase("kg")) {
                            g = "g ";
                        } else if (Main.shop.getKgSymbol().equalsIgnoreCase("lb")) {
                            g = "x 0.001 lb ";
                        }
                        description = g + rs.getString("Description").trim()
                                + " @ " + bundle.getString("Price") + " (" + Main.shop.poundSymbol + ") /" + Main.shop.getKgSymbol();
                        sku = rs.getInt("Sku");
                        taxRate = rs.getInt("Taxes.Rate");
                        taxID = rs.getInt("Tax");
                        tax2ID = rs.getInt("Tax2");
                        if (tax2ID == 0) {
                            taxRate = 0;
                        } else {
                            //need to lookup in database
                            pslur.setInt(1, tax2ID);
                            rs2 = pslur.executeQuery();
                            if (rs2.first()) {
                                tax2Rate = rs2.getInt("Rate");
                            } else {
                                tax2Rate = 0;
                            }
                        }
                        //check restrictions
                        restriction = rs.getString("Departments.Restriction");
                        rs.close();
                        if (!isRestricted(restriction)) {
//                            return false;
                        }
                        originalPrice = price;
                        price = (price * 10 * (100 - discount) + 500) / 1000;
                        line = new Line(getQty(), description, price, barcode,
                                taxRate, originalPrice, "", encode, false,
                                false, taxID, wholesalePrice, packSize, tax2ID,
                                tax2Rate, ix, x, y, z, offerType, limit, discountedPrice);
                        getaSale().add(line);
                        getModel().fireTableDataChanged();
                        setQty((Integer) 1);
                        updateSale();
                        updateResult();
                        poleUpdate(getaSale().getSelectedLine());
                        if (!isWeighIt()) {
                            Audio.play("Beep");
                        }
                        rs.close();
                        lastBarCode = 0l;
                        setNoIncrement(false);//
                        tax1 = getaSale().getTax1();
                        tax2 = getaSale().getTax2();
                        setTotal(getaSale().getTotal());
                        setSavings(getaSale().getSavings());
                        Main.speech.say((description + Main.speech.asCash(getTotal())));
                        return true;
                    }
                }
                rs.close();
            }
            PreparedStatement productLookup =
                    Main.getConnection().prepareStatement(
                    SQL.selectProduct);
            //now need to check till/product table
            productLookup.setLong(1, barcode);
            shortenedData6 = NewProduct.shortenForEncoded6(shortenedData);
            shortenedData7 = NewProduct.shortenForEncoded7(shortenedData);
            shortenedData8 = NewProduct.shortenForEncoded8(shortenedData);
            productLookup.setString(2, shortenedData7);
            productLookup.setString(3, shortenedData8);
            productLookup.setString(4, shortenedData6);

            ResultSet rs = productLookup.executeQuery();
            if (!rs.first()) {
                //no product
                int n = 1;
                rs.close(); //try nProduct
                if (Character.isDigit(shortenedData.charAt(0))) {
                    n = Integer.parseInt(shortenedData.substring(0, 1));
                }
                Long b = Long.parseLong(StringOps.numericOnly(shortenedData.substring(1, shortenedData.length())));
                productLookup.setLong(1, b);
                rs = productLookup.executeQuery();
                if (!rs.first() || n < 2 || shortenedData.length() == 7) {
                    rs.close();
                    if (barcode == lastBarCode && Main.shop.isNewProduct()) {//create a new product
                        if (Main.operator.createNewStock || Main.operator.isOwnerManager()) {
                            lastBarCode = 0l;
                            Main.newProduct.internetLookup(barcode, dataEntry);
                            dataLabel.setText(bundle1.getString("Enter_a_description"));
                            dataEntry.setBackground(Color.yellow);
                            Audio.play("Tada");
                            dataEntry.setText("");
                            dataEntry.requestFocus();
                            setDescribeIt(true);
                            setNoIncrement(false);//
                            tax1 = getaSale().getTax1();
                            tax2 = getaSale().getTax2();
                            setTotal(getaSale().getTotal());
                            setSavings(getaSale().getSavings());
                            setSaleComplete(getaSale().getSelection() < 0);
                            if (Main.server.internetEnabled) {//no delay if not enabled
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(SalesScreenFunctions.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (!InternetDescriptionLookup.description.isEmpty()) {
                                dataEntry.setText(InternetDescriptionLookup.description);
                                description = InternetDescriptionLookup.description;
                                dataEntry.selectAll();
                            }
                            return true;
                        } else {
                            lastBarCode = 0l;
                            dataLabel.setText(bundle1.getString("ScanAgain"));
                            dataEntry.setBackground(Color.yellow);
                            return false;
                        }
                    } else {
                        if (!isPharmacistApproval() && !isAgeCheck()) {
//                            dataEntry.setText("");
                        }
                        if (!NewProduct.couldBeEncoded(shortenedData)) {
                            lastBarCode = barcode;
                            dataLabel.setText(bundle1.getString("ScanAgain"));
                            dataEntry.setBackground(Color.yellow);
                            Audio.play("Ring");
                            return true;
                        }
                        setSaleComplete(getaSale().getSelection() < 0);
                        barcode = 0l;
                        dataLabel.setText(bundle1.getString("ScanAgain"));
                        dataEntry.setBackground(Color.yellow);
                        return false;
                    }
                }
                setQty((Integer) n * getQty());
                barcode = b;//without first digit
            }
            product = rs.getLong("Products.ID");
            barcode = product;
            encode = rs.getInt("Encoded");
            description = rs.getString("Description");
            price = rs.getInt("Price");
            sku = rs.getInt("Sku");
            taxID = rs.getInt("Tax");
            taxRate = rs.getInt("Taxes.Rate");
            tax2ID = rs.getInt("Tax2");
            stockQuantity = rs.getInt("Quantity");
            stockType = rs.getShort("Skus.StockType");
            if (tax2ID == 0) {
                taxRate = 0;
            } else {
                //need to lookup in database
                pslur.setInt(1, tax2ID);
                rs2 = pslur.executeQuery();
                if (rs2.first()) {
                    tax2Rate = rs2.getInt("Rate");
                } else {
                    tax2Rate = 0;
                }
            }
//            taxRate = rs.getInt("Taxes.Rate2");
            //check restrictions
            restriction = rs.getString("Departments.Restriction");
            rs.close();
            if (!isRestricted(restriction)) {
//                return true;
            }
            originalPrice = price;
            price = (price * 10 * (100 - discount) + 500) / 1000;
            showStockLevel();
            //lok up ix, x and y
            PreparedStatement offerLookup =
                    Main.getConnection().prepareStatement(
                    SQL.offerLookup);
            offerLookup.setLong(1, barcode);
            ResultSet olrs = offerLookup.executeQuery();
            if (olrs.first()) {
                ix = olrs.getInt("IX");
                x = olrs.getInt("X");
                y = olrs.getInt("Y");
                offerType = olrs.getInt("OfferType");
                limit = olrs.getInt("LimitValue");
            } else {
                ix = 0;
                x = 0;
                y = 0;
                limit = 0;
            }
            offerLookup.close();
            Line lineL = new Line(getQty(), description, price, barcode, taxRate,
                    originalPrice, "", encode, false, false, taxID,
                    wholesalePrice, packSize, tax2ID, tax2Rate, ix, x, y, z, offerType, limit, discountedPrice);
            getaSale().add(lineL);
            getModel().fireTableDataChanged();
            tax1 = getaSale().getTax1();
            tax2 = getaSale().getTax2();
            setTotal(getaSale().getTotal());
            agency = taxID == 6;//was agency
            if (!isPriceIt() && !isPharmacistApproval() && !isAgeCheck() && !isTrackIt()) {
                dataLabel.setText(bundle.getString("SalesScreen.dataLabel.text"));
            }
            if (agency) {
                if (agencyFirstTime) {
                    setAgencyString("");
                    agencyFirstTime = false;
                }
                setAgencyString(getAgencyString() + " " + Money.asMoney(getQty() * price) + " ");
                agency = false;
            }
            setQty((Integer) 1);
            if (price == 0) {
                dataLabel.setText(bundle1.getString("Enter_price_in") + " " + Main.shop.getPennySymbol() + " ...");
                dataEntry.setBackground(Color.yellow);
                setPriceEntry(true);
            }
            if (getCoupon() != 0 || getCheque() != 0) {
                toPay = getTotal() - getDebit() - getCoupon() - getCheque();
            }
            int x = getaSale().size();
            getaSale().setSelection(getaSale().size() - 1);
            setSelection(getaSale().getSelection());
            scrollToVisible(jSaleTable, getSelection(), 0);
            rs.close();
//            Line t = aSale.getSelectedLine();
            //Make last line visible
            int sr = jSaleTable.getSelectedRow();
            if (isPriceEntry() || isPharmacistApproval() || isAgeCheck() || isTrackIt()) {
                //needs a delay here
                Audio.play("TaDa");
            } else {
                Audio.play("Beep");
            }
            long g = SaleType.CUSTOMER.code();
//            if (customer != SaleType.CUSTOMER.code() * 10000) {
//            }
            setSaleComplete(false);
            updateSale();
            updateResult();
            poleUpdate(getaSale().getSelectedLine());
            lastBarCode = 0l;
            setNoIncrement(false);
            setSaleComplete(getaSale().getSelection() < 0);
//            tax=aSale.getTax();
            Main.speech.say(description + Main.speech.asCash(getTotal()));
            pslur.close();
            return true;
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "SalesScreenFunctions.product ", "SQLException: " + ex.getMessage());
            return false;
        }
    }

    public boolean priceOver(String data, boolean managerRequired) {
        if (!managerRequired) {
            return priceOver(data);
        } else {
            if (data.length() > 1 && (data.charAt(data.length()-1) == '@' || data.charAt(data.length()-1) == '=')) {
                if ( Main.operator.isPriceChange()||Main.operator.isOwnerManager()) {
                    return priceOver(data);
                } else {
                    Audio.play("Ring");
                    dataEntry.selectAll();
                    dataEntry.requestFocus();
                    setNoIncrement(true);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean priceOver(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || partPaid || isWeighIt()||getaSale().getSelection() == -1) {
            return false;
        }
        if (data.length() > 1 && (data.charAt(data.length()-1) == '@' || data.charAt(data.length()-1) == '=')) {
            if (data.length() > 2 && data.charAt(1) == '-') {
                try {
                    n = Integer.parseInt(data.substring(1,data.length()-1));
                    sign = -1;
                } catch (NumberFormatException ex) {
                    dataEntry.setText("");
                    dataEntry.requestFocus();
                    setNoIncrement(true);
                    hidden.requestFocus();
                    return true;
                }
            } else {
                try {
                    n = Integer.parseInt(data.substring(0,data.length()-1));
                    sign = 1;
                } catch (NumberFormatException ex) {
                    dataEntry.setText("");
                    dataEntry.requestFocus();
                    setNoIncrement(true);
                    hidden.requestFocus();
                    return true;
                }
            }
            setSelection(getaSale().getSelection());
            line = getaSale().getSelectedLine();
            taxID = line.getTaxID();
            if (line.isDiscounted()) {//why is this
                originalPrice = line.getOriginalPrice();
                price = line.getRetailPrice();
                discount = ((originalPrice - price) * 100) / originalPrice;
                n = n * (100 - discount) / 100;
                discount = 0;
            }
            if (originalPrice == 0) {//zerop priced item
                originalPrice = n;
                getaSale().getSelectedLine().setOriginalPrice(n);
            } else {
                getaSale().getSelectedLine().setPricedOver(true);
            }
            if (originalPrice > 0) {
                if (sign > 0) {
                    getaSale().setPriceAt(getSelection(), n);
                } else {
                    getaSale().setPriceAt(getSelection(), originalPrice - n);
                }
            } else {
                if (sign > 0) {
                    getaSale().setPriceAt(getSelection(), -n);
                } else {
                    getaSale().setPriceAt(getSelection(), originalPrice + n);
                }
            }
            setQty((Integer) 1);
            updateResult();
            updateSale();
            poleUpdate(getaSale().getSelectedLine());
            Audio.play("Beep");
//                noIncrement = true;
            hidden.requestFocus();
            setTotal(getaSale().getTotal());
            Main.speech.say(Main.speech.asCash(getTotal()));
            return true;
        }
        return false;
    }

    public boolean tender(String data) {
        if (!StringOps.isNumericOnly(data)) {
            return false;
        }
        String localData = StringOps.stripSpaces(data);
        setSelection(getaSale().getSelection());
        if (!wasEnter || isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || isTrackIt() || getSelection() == -1 && !isReceived || data.length() < 1 || data.length() >= 9 || !Character.isDigit(data.charAt(data.length() - 1))) {
            return false;
        }
        int nLocal;
        try {
            localData = StringOps.numericOnly(localData);
            if (localData.length() == 0) {
                return false;
            }
            nLocal = Integer.parseInt(localData);
        } catch (NumberFormatException ex) {
            return false;
        }
        if (nLocal <= 1000000 || isShift()) {//hotkey for a (£10,000)
            if ((nLocal < getTotal() - debit - coupon - cheque && !isReceived && !isCharged)) {
                Audio.play("Ring");
                return false;
            } else if (getTotal() == 0 && getaSale().getSelection() >= 0 && nLocal == 0) {
                if (!saleComplete(SaleType.NORMAL)) {
                    return false;
                }
                dataEntry.setText("");
                Audio.play("CashReg");
                top = Main.customerMessages.getString("ThankYou");
                bottom = Main.customerMessages.getString("ComeBackSoon");
                Main.speech.say(Main.customerMessages.getString("ThankYou") + " "
                        + Main.customerMessages.getString("ComeBackSoon"));
                Main.pole.execute(top, bottom);
                if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                        || isReceiptRequired()) {
                    Main.receiptPrinter.printReceipt(getSale());
                    setReceiptRequired(false);
                } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                    Main.invoice.print(getSale(), getDiscount(), false);
                    setInvoiceRequired(false);
                }
                status = bundle1.getString("SaleComplete");
                setSaleComplete(true);
                toPay = 0;
                resultsTable.repaint();
                setNoIncrement(true);
                getaSale().setSelection(-1);
                setSelection(-1);
                noClear = true;
                setSaleComplete(true);
                clearSale();
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                int cashInDrawer = Main.cashUp.cashInDrawer();
                if (cashInDrawer > Main.shop.getCashDrawerLimit() && Main.shop.getCashDrawerLimit()!=0) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(null, "Too much cash in drawer");
                    Audio.play("Ring");
                }
                return true;
            } else if (!isReceived && !isCharged) {//normal tender
                if (nLocal == 0 && getCustomer() != SaleType.CUSTOMER.code() * 10000) {
                    //should be ended with 'Charge'
                    return false;
                }
                if (getTotal() < 0 && !(Main.operator.refunds || Main.operator.isOwnerManager())) {
                    return false;
                }
                lastBarCode = 0;
                cash = nLocal;
                if (!saleComplete(SaleType.NORMAL)) {
                    return false;//changed
                }
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                toPay = 0;
                partPaid = false;
                if (nLocal - getTotal() + debit + coupon + cheque > 0) {//with change
                    change = nLocal - getTotal() + debit + coupon + cheque;
                } else {//without change
                    change = 0;
                }
                dataEntry.setText("");
                Audio.play("CashReg");
                if (nLocal - getTotal() + debit + coupon + cheque == 0) {
                    top = Main.customerMessages.getString("ThankYou");
                    bottom = Main.customerMessages.getString("ComeBackSoon");
                    Main.speech.say(Main.customerMessages.getString("ThankYou"));
                } else {
                    top = Main.customerMessages.getString("YourChange") + " " + (new Money(nLocal - getTotal() + debit + coupon + cheque)).toString();
                    bottom = Main.customerMessages.getString("ThankYou");
                    Main.speech.say(Main.customerMessages.getString("YourChange")
                            + Main.speech.asCash(nLocal - getTotal() + debit + coupon + cheque));
                }
                Main.pole.execute(top, bottom);
                Main.receiptPrinter.cashDrawerOpen();
                if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter() || isReceiptRequired()) {
                    Main.receiptPrinter.printReceipt(getSale());
                    setReceiptRequired(false);
                } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                    Main.invoice.print(getSale(), getDiscount(), false);
                    setInvoiceRequired(false);
                }
                status = bundle1.getString("SaleComplete");
                setSaleComplete(true);
                toPay = 0;
                resultsTable.repaint();
                setNoIncrement(true);
                getaSale().setSelection(-1);
                setSelection(-1);
                noClear = true;
                setSaleComplete(true);
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                int cashInDrawer = Main.cashUp.cashInDrawer();
                if (cashInDrawer > Main.shop.getCashDrawerLimit() && Main.shop.getCashDrawerLimit()!=0) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(null, "Too much cash in drawer");
                    Audio.play("Ring");
                }
                return true;
            } else if (nLocal == 0 && !isShift()) {//is received on account with no paymenr
                setSaleComplete(true);
                dataEntry.setText("");
                dataEntry.requestFocus();
                Audio.play("CashReg");
                status = bundle1.getString("SaleComplete");
//                resultsTable.repaint();
                isReceived = true;
                setSaleComplete(true);
                clearSale();
                updateResult();
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                int cashInDrawer = Main.cashUp.cashInDrawer();
                if (cashInDrawer > Main.shop.getCashDrawerLimit() && Main.shop.getCashDrawerLimit()!=0) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(null, "Too much cash in drawer");
                    Audio.play("Ring");
                }
                return true;
            } else if (isReceived) {//is received on account with payment
                cash = nLocal;
                if (getTotal() > cash && cash > 0) {//part payment
                    toPay = getTotal() - cash;
                    setTotal(cash);
                } else {//payment or overpayment
                    toPay = 0;
                    if (isShift() && cash == 0 && getTotal() < 0) {
                        //refund negative balance
                        change = -getTotal();
                    } else if (isShift() || Main.salesScreen.nextShift) {
                        Main.salesScreen.nextShift = false;
                        change = 0;
                        setTotal(cash);
                    } else {
                        change = nLocal - getTotal();
                    }
                }
                if (!saleComplete(SaleType.RECEIVEDONACCOUNT)) {
                    return false;
                }
                dataEntry.setText("");
                Audio.play("CashReg");
                isReceived = false;//set true in first stage
                if (nLocal - getTotal() == 0) {
                    top = Main.customerMessages.getString("ThankYou");
                    bottom = Main.customerMessages.getString("ComeBackSoon");
                    Main.speech.say(Main.customerMessages.getString("ThankYou"));
                } else {
                    top = Main.customerMessages.getString("YourChange") + (new Money(nLocal - getTotal())).toString();
                    bottom = Main.customerMessages.getString("ThankYou");
                    Main.speech.say(Main.customerMessages.getString("YourChange")
                            + Main.speech.asCash(nLocal - getTotal()));
                }
                Main.pole.execute(top, bottom);
                Main.receiptPrinter.cashDrawerOpen();
                setTotal(debit);
                if (!Main.hardware.isInvoicePrinter()) {
                    Main.receiptPrinter.printReceipt(getSale());
                    setReceiptRequired(false);
                } else if (Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                    Main.invoice.print(getSale(), getDiscount(), false);
                    setInvoiceRequired(false);
                }
                setReceiptRequired(false);
                status = bundle1.getString("SaleComplete");
                setSaleComplete(true);
                updateResult();
                setSaleComplete(true);
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                int cashInDrawer = Main.cashUp.cashInDrawer();
                if (cashInDrawer > Main.shop.getCashDrawerLimit() && Main.shop.getCashDrawerLimit()!=0) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(null, "Too much cash in drawer");
                    Audio.play("Ring");
                }
                return true;
            } else if (toPay != 0 && isCharged) {//charged with part payment
                cash = nLocal;
                if (nLocal > toPay) {
                    change = cash - toPay;
                    cash = toPay;
                    toPay = 0;
                } else if (nLocal == toPay) {
                    change = 0;
                    cash = toPay;
                    toPay = 0;
                } else {
                    Audio.play("Ring");
                    return false;
                }
                if (!saleComplete(SaleType.CHARGED)) {
                    return false;
                }
                dataEntry.setText("");
                Audio.play("CashReg");
                if (nLocal - getTotal() == 0) {
                    top = Main.customerMessages.getString("ThankYou");
                    bottom = Main.customerMessages.getString("ComeBackSoon");
                    Main.speech.say(Main.customerMessages.getString("ThankYou"));
                } else {
                    top = Main.customerMessages.getString("YourChange") + " " + (new Money(nLocal - getTotal())).toString();
                    bottom = Main.customerMessages.getString("ThankYou");
                    Main.speech.say(Main.customerMessages.getString("YourChange")
                            + Main.speech.asCash(nLocal - getTotal()));
                }
                Main.pole.execute(top, bottom);
                Main.receiptPrinter.cashDrawerOpen();
                setSaleComplete(true);
                updateResult();
                delay = Main.shop.getAutoclearMinutes();
                if (delay < 0) {
                    //lock whenever sale complete
                    Main.salesScreen.lock();
                }
                int cashInDrawer = Main.cashUp.cashInDrawer();
                if (cashInDrawer > Main.shop.getCashDrawerLimit() && Main.shop.getCashDrawerLimit()!=0 ) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(null, "Too much cash in drawer");
                    Audio.play("Ring");
                }
                return true;
            }
        }
        return false;
    }

    public boolean timesN(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || partPaid || isWeighIt()) {
            return false;
        }
        int minus = 1;
        if (data.length() > 1 && data.length() < 5 && (data.charAt(0) == '*' || data.charAt(0) == '.')) {
            numericData = StringOps.numericOnly(data);
            setSelection(getaSale().getSelection());
            if (numericData.isEmpty() || getSelection() < 0) {
                return false;
            }
            if (getaSale().getSelectedLine().getEncode() != NewProduct.NOTENCODE) {
                return false;
            }
            if (data.charAt(1) == '-') {
                minus = -1;
            }
            Line localLine = getaSale().getSelectedLine();
            taxID = localLine.getTaxID();
            if (taxID == 6) {
                return false;
            }
            int oldQty = (Integer) getModel().getValueAt(getSelection(), 0);
            if (oldQty < 0) {
                minus *= -1;
            }
            setQty((Integer) Integer.parseInt(numericData));
            setQty((Integer) (getQty() * minus));
            getModel().setValueAt(getQty(), getSelection(), 0);
            getModel().fireTableDataChanged();
            scrollToVisible(jSaleTable, getSelection(), 0);
            setTotal(getaSale().getTotal());
            tax1 = getaSale().getTax1();
            tax2 = getaSale().getTax2();
            updateSale();
            updatePole();
            updateResult();
            setQty((Integer) 1);
            Main.speech.say(Main.speech.asCash(getTotal()));
            Audio.play("Beep");
            return true;
        }
        return false;
    }

    boolean ownUse(String data) {
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || isWeighIt() || getaSale().getSelection() == -1 || data.length() == 1 || data.length() >= 9) {
            return false;
        }
        long n;
        try {
            n = Long.parseLong(data);
        } catch (NumberFormatException ex) {
            return false;
        }
        long g = SaleType.OWNUSE.code();
        if (n == SaleType.OWNUSE.code() && Main.operator.isOwnerManager()) {
            //an own use sale
            lastBarCode = 0;
            setTotal(getaSale().getTotal());
            dataEntry.setText("");
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {//no customer selected
                setCustomer(Main.customers.execute("OwnUse", true));//SaleType.CUSTOMER.code() * 10000l +
                setCustomer(getCustomer());
                if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                    //no customer was selected
                    dataEntry.setText("");
                    return false;
                } else {
                    setFirstName(Main.customers.getFirstName());
                    setSurname(Main.customers.getSurname());
                }
            }
            if (!saleComplete(SaleType.OWNUSE)) {
                return false;
            }
            dataEntry.setText("");
            Audio.play("CashReg");
            setSaleComplete(true);
            setStatus(bundle1.getString("OwnUse"));
            dataEntry.setText("");
            noClear = true;
            change = 0;
            if (!Main.hardware.isInvoicePrinter()) {
                Main.receiptPrinter.printReceipt(getSale());
                setReceiptRequired(false);
            } else if (Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                Main.invoice.print(getSale(), getDiscount(), false);
                setInvoiceRequired(false);
            }
            updateSale();
            return true;
        }
        return false;
    }

    public boolean customer(String data) {
        if (getCustomer() != SaleType.CUSTOMER.code() * 10000l) {
            return false;
        }
        String ss = StringOps.numericOnly(data);
        if (ss.isEmpty()) {
            return false;
        }
        if (ss.length() > 11) {
            return false;
        }
        long bc = Long.parseLong(ss);
        long k = SaleType.CUSTOMER.code() * 10000l;
        if (bc == SaleType.CUSTOMER.code() || bc >= SaleType.CUSTOMER.code() * 10000l && bc <= SaleType.CUSTOMER.code() * 10000l + 9999l) {
            lastBarCode = 0;
            if (bc != SaleType.CUSTOMER.code() * 10000L) {
                setCustomer(bc);
                if (Main.customers.isCustomer(bc)) {
                    setFirstName(Main.customers.getFirstName());
                    setSurname(Main.customers.getSurname());
                    setDiscount(Main.customer.getDiscount(getCustomer()));
                    applyDiscount(getDiscount());
                    Audio.play("Beep");
                    setSaleComplete(false);
//                    noIncrement = true;
                    updateSale();
                    updateResult();
                    return true;
                } else {
                    setCustomer(SaleType.CUSTOMER.code() * 10000L);
                    setFirstName(Main.customers.getFirstName());
                    setSurname(Main.customers.getSurname());
                    Audio.play("Ring");
                    setNoIncrement(true);
                    updateSale();
                    return false;
                }
            }
            setCustomer(Main.customers.execute("ByName", false));
            //customer+=SaleType.CUSTOMER.code() * 10000 ;
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                //no customer selected
                setCustomer(SaleType.CUSTOMER.code() * 10000);
                setFirstName(Main.customers.getFirstName());
                setSurname(Main.customers.getSurname());
                Audio.play("Ring");
                dataEntry.setText("");
                resultsTable.repaint();
//                noIncrement = true;
                return false;
            } else {
                setFirstName(Main.customers.getFirstName());
                setSurname(Main.customers.getSurname());
                resultsTable.repaint();
                setDiscount(Main.customer.getDiscount(getCustomer()));
                applyDiscount(getDiscount());
                setSaleComplete(false);
                setNoIncrement(true);
                updateSale();
                return true;
            }
        }
        setCustomer(SaleType.CUSTOMER.code() * 10000);
        setFirstName(Main.customers.getFirstName());
        setSurname(Main.customers.getSurname());
        resultsTable.repaint();
        return false;
    }

    public int getDiscount() {
        return discount;
    }

    public void applyDiscount(int discount) {
        for (int i = 0; i < getaSale().size(); i++) {
            getaSale().setSelection(i);
            taxID = getaSale().getSelectedLine().getTaxID();
            if (taxID != Tax.BILLPAYMENT) {//not bill payment
                price = (Integer) getaSale().getSelectedLine().getRetailPrice() * 10;
                setQty((Integer) getaSale().getSelectedLine().getQuantity());
                if (getQty() > 0) {
                    price = (price * (100 - discount) + 500) / 1000;
                } else {
                    //would be different if pricewere negative
                    price = (price * (100 - discount) + 500) / 1000;
                }
                getaSale().getSelectedLine().setRetailPrice(price);
                getaSale().getSelectedLine().setDiscounted(true);
            }
        }
        setTotal(getaSale().getTotal());
        Main.speech.say(Main.speech.asCash(getTotal()));
    }

    public boolean discount(String data) {
        int localDiscount;
        if (isDescribeIt() || isPriceIt() || isPriceEntry() || partPaid || isWeighIt()||getaSale().getSelection() == -1) {
            return false;
        }
        String data1 = StringOps.stripSpaces(data);
        if (data1.length() >= 2 && data1.length() <= 3 && (data1.charAt(data1.length() - 1) == '%' || data1.charAt(data1.length() - 1) == '/')) {
            if(Main.operator.isPriceChange()||Main.operator.isOwnerManager()){
                try {
                    localDiscount = Integer.parseInt(data1.substring(0, data1.length() - 1));
                } catch (NumberFormatException ex) {
                    Audio.play("Ring");
                    dataEntry.setText("");
                    dataEntry.requestFocus();
                    return false;
                }
                if (isShift()) {
                    int j = getaSale().size();
                    int i;
                    for (i = 0; i < j; i++) {
                        getaSale().setSelection(i);
                        taxID = getaSale().getSelectedLine().getTaxID();
                        if (taxID != Tax.BILLPAYMENT) {//not bill payment
                            price = (Integer) getaSale().getSelectedLine().getRetailPrice() * 10;
                            setQty((Integer) getaSale().getSelectedLine().getQuantity());
                            if (getQty() > 0) {
                                price = (price * (100 - localDiscount) + 500) / 1000;
                            } else {
                                price = (price * (100 - localDiscount) - 500) / 1000;
                            }
                            getaSale().getSelectedLine().setRetailPrice(price);
                            getaSale().getSelectedLine().setDiscounted(true);
                        }
                    }
                } else {
                    taxID = getaSale().getSelectedLine().getTaxID();
                    if (taxID != Tax.BILLPAYMENT) {//not bill payment
                        price = (Integer) getaSale().getSelectedLine().getRetailPrice() * 10;
                        setQty((Integer) getaSale().getSelectedLine().getQuantity());
                        if (getQty() > 0) {
                            price = (price * (100 - localDiscount) + 500) / 1000;
                        } else {
                            price = (price * (100 - localDiscount) - 500) / 1000;
                        }
                        getaSale().getSelectedLine().setRetailPrice(price);
                        getaSale().getSelectedLine().setDiscounted(true);
                    } else {
                        Audio.play("Ring");
                        return true;
                    }
                }
                tax1 = getaSale().getTax1();
                tax2 = getaSale().getTax2();
                setSavings(getaSale().getSavings());
                updateSale();
                updateResult();
                setSelection(getaSale().getSelection());
                scrollToVisible(jSaleTable, getSelection(), 0);
                poleUpdate(getaSale().getSelectedLine());
                Audio.play("Beep");
                hidden.requestFocus();
                setTotal(getaSale().getTotal());
                Main.speech.say(Main.speech.asCash(getTotal()));
                return true;
            } else {
                Audio.play("Ring");
                dataEntry.selectAll();
                dataEntry.requestFocus();
                setNoIncrement(true);
                return true;
            }
        } else if (data1.length() > 3 && (data1.charAt(data1.length() - 1) == '%' || data1.charAt(data1.length() - 1) == '/')) {
            dataEntry.selectAll();
            dataEntry.requestFocus();
            setNoIncrement(true);
        }
        return false;
    }

    /**
     * @param aSalesScreen the salesScreen to set
     */
    public void setSalesScreen(javax.swing.JFrame aSalesScreen) {
        salesScreen = aSalesScreen;
    }

    /**
     * @param aDataLabel the dataLabel to set
     */
    public void setDataLabel(javax.swing.JLabel aDataLabel) {
        dataLabel = aDataLabel;
    }

    /**
     * @param aDateLabel the dateLabel to set
     */
    public void setDateLabel(javax.swing.JLabel aDateLabel) {
        dateLabel = aDateLabel;
    }

    /**
     * @param aHidden the hidden to set
     */
    public void setHidden(javax.swing.JTextField aHidden) {
        hidden = aHidden;
    }

    /**
     * @param ajSaleTable the jSaleTable to set
     */
    public void setjSaleTable(javax.swing.JTable ajSaleTable) {
        jSaleTable = ajSaleTable;
    }

    /**
     * @param aResultsTable the resultsTable to set
     */
    public void setResultsTable(javax.swing.JTable aResultsTable) {
        resultsTable = aResultsTable;
    }

    /**
     * @param aDataEntry the dataEntry to set
     */
    public void setDataEntry(javax.swing.JTextField aDataEntry) {
        dataEntry = aDataEntry;
    }

    /**
     * @param ajTabbedPane1 the jTabbedPane1 to set
     */
    public void setjTabbedPane1(JTabbedPane ajTabbedPane1) {
        jTabbedPane1 = ajTabbedPane1;
    }

    /**
     * @param aaSale the aSale to set
     */
    public void setaSale(LineList aaSale) {
        aSale = aaSale;
    }

    /**
     * @param aModel the model to set
     */
    public void setModel(SaleTableModel aModel) {
        model = aModel;
    }

    /**
     * @param aStatus the status to set
     */
    public void setStatus(String aStatus) {
        status = aStatus;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return the tax
     */
    public int getTax1() {
        return tax1;
    }

    /**
     * @return the coupon
     */
    public int getCoupon() {
        return coupon;
    }

    /**
     * @return the toPay
     */
    public int getToPay() {
        return toPay;
    }

    /**
     * @return the debit
     */
    public int getDebit() {
        return debit;
    }

    /**
     * @return the cheque
     */
    public int getCheque() {
        return cheque;
    }

    /**
     * @param aSavings the savings to set
     */
    public void setSavings(int aSavings) {
        savings = aSavings;
    }

    /**
     * @return the describIt
     */
    public boolean getDescribeIt() {
        return describeIt;
    }

    /**
     * @return the priceIt
     */
    public boolean getPriceIt() {
        return priceIt;
    }

    /**
     * @return the cash
     */
    public int getCash() {
        return cash;
    }

    /**
     * @return the savings
     */
    public int getSavings() {
        return savings;
    }

    public boolean receiptRequired() {
        if (getTotal() >= Main.shop.getReceiptSpinnerValue() && Main.shop.getReceiptSpinnerValue() != 0) {
            setReceiptRequired(true);//should happen later
        }
        return isReceiptRequired();
    }

    /**
     * @return the change
     */
    public int getChange() {
        return change;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the saleComplete
     */
    public boolean isSaleComplete() {
        return saleComplete;
    }

    /**
     * @param aSaleComplete the saleComplete to set
     */
    public void setSaleComplete(boolean aSaleComplete) {
        saleComplete = aSaleComplete;
//        delay = Main.shop.getAutoclearMinutes();
//        if(delay<0){
//            //lock whenever sale complete
//            Main.operator.setOperator(-1);
//        }
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param aData the data to set
     */
    public void setData(String aData) {
        data = aData;
    }

    /**
     * @return the aSale
     */
    public LineList getaSale() {
        return aSale;
    }

    /**
     * @return the selection
     */
    public int getSelection() {
        return selection;
    }

    /**
     * @param aSelection the selection to set
     */
    public void setSelection(int aSelection) {
        selection = aSelection;
    }

    /**
     * @return the qty
     */
    public Integer getQty() {
        return qty;
    }

    /**
     * @param aQty the qty to set
     */
    public void setQty(Integer aQty) {
        qty = aQty;
    }

    /**
     * @return the model
     */
    public SaleTableModel getModel() {
        return model;
    }

    void updateSalesScreen() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean cheque() {
        if (Main.operator.isSalesPerson() && getTotal() != 0 && (agencyFirstTime)
                || Main.operator.isOwnerManager() ) {
            if (!isSaleComplete()) {
                cheque = Main.cheque.execute(getTotal() - coupon - debit);
                if (cheque == 0) {
                    Audio.play("Ring");
                    return true;
                }
                setNoIncrement(true);
                if (getTotal() - debit - coupon - cheque <= 0) {
                    //                    partPaid = true;//stops further sales
                    if (getTotal() - coupon - cheque - debit <= 0 && !isReceived) {
                        if (!saleComplete(SaleType.NORMAL)) {
                            Audio.play("Ring");
                            return true;
                        }
                        delay = Main.shop.getAutoclearMinutes();
                        if (delay < 0) {
                            //lock whenever sale complete
                            Main.salesScreen.lock();
                        }
                        if (getTotal() - coupon - cheque - debit < 0) {
                            change = debit + coupon + cheque - getTotal();
                        } else {
                            change = 0;
                        }
                        noClear = true;
                        dataEntry.setText("");
                        Audio.play("CashReg");
                        setSaleComplete(true);
                        delay = Main.shop.getAutoclearMinutes();
                        if (delay < 0) {
                            //lock whenever sale complete
                            Main.salesScreen.lock();
                        }

                        //                        updateResult();
                        resultsTable.repaint();
                        if (change == 0) {
                            top = Main.customerMessages.getString("ThankYou");
                            bottom = Main.customerMessages.getString("ComeBackSoon");
                            Main.speech.say(top + " " + bottom);
                        } else {
                            top = Main.customerMessages.getString("YourChange") + (new Money(change)).toString();
                            bottom = Main.customerMessages.getString("ThankYou");
                            Main.speech.say(Main.customerMessages.getString("YourChange")
                                    + Main.speech.asCash(change) + " " + bottom);
                        }
                        Main.pole.execute(top, bottom);
                        Main.receiptPrinter.cashDrawerOpen();
                        if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                                || isReceiptRequired()) {
                            Main.receiptPrinter.printReceipt(getSale());
                            setReceiptRequired(false);
                        } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                            Main.invoice.print(getSale(), getDiscount(), false);
                            setInvoiceRequired(false);
                        }
                        status = bundle1.getString("SaleComplete");
                    } else {
                        toPay = getTotal() - debit - coupon - cheque;
                        resultsTable.repaint();
                        Audio.play("Beep");
                    }
                }
                if (getTotal() - debit - coupon - cheque > 0) {
                    partPaid = true;
                    toPay = getTotal() - debit - coupon - cheque;
                    top = Main.customerMessages.getString("Cheque") + ": " + (new Money(cheque)).toString();
                    bottom = Main.customerMessages.getString("To_pay") + ": " + (new Money(toPay)).toString();
                    Main.pole.execute(top, bottom);
                    Main.speech.say(Main.customerMessages.getString("To_pay")
                            + Main.speech.asCash(toPay));
                }
                resultsTable.repaint();
                if (isReceived) {
                    if ((isShift() || Main.salesScreen.nextShift)) {
                        Main.salesScreen.nextShift = false;
                    }
                    setTotal(debit + cheque + coupon);
                    if (saleComplete(SaleType.RECEIVEDONACCOUNT)) {
                        Audio.play("CashReg");
                        if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                                || isReceiptRequired()) {
                            Main.receiptPrinter.printReceipt(getSale());
                            setReceiptRequired(false);
                        } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                            Main.invoice.print(getSale(), getDiscount(), false);
                            setInvoiceRequired(false);
                        }
                        isReceived = false;
                        setSaleComplete(true);
                        status = bundle1.getString("SaleComplete");
                        newSaleStarted = false;
                        delay = Main.shop.getAutoclearMinutes();
                        if (delay < 0) {
                            //lock whenever sale complete
                            Main.salesScreen.lock();
                        }
                        return true;
                    } else {
                        Audio.play("Ring");
                        return true;
                    }
                }
            }
        } else {
            Audio.play("Ring");
        }
        return false;
    }

    boolean coupon() {
        if (Main.operator.getIntAuthority() < 5 && (getTotal() != 0 || isReceived) && !isSaleComplete()) {
            if (isReceived) {
                int localCoupon = 0;
                if (!isSaleComplete()) {
                    localCoupon = Main.coupon.execute();
                    if (localCoupon <= 0) {
                        return true;
                    }
                    coupon += localCoupon;//in case more than one
                    setNoIncrement(true);
                    setTotal(coupon);
                    if (!saleComplete(SaleType.RECEIVEDONACCOUNT)) {
                        Audio.play("Ring");
                        return true;
                    }
                    status = bundle1.getString("SaleComplete");
                    noClear = true;
                    Audio.play("CashReg");
                    top = Main.customerMessages.getString("ThankYou");
                    bottom = Main.customerMessages.getString("ComeBackSoon");
                    Main.pole.execute(top, bottom);
                    Main.receiptPrinter.cashDrawerOpen();
                    if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                            || isReceiptRequired()) {
                        Main.receiptPrinter.printReceipt(getSale());
                        setReceiptRequired(false);
                    } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                        Main.invoice.print(getSale(), getDiscount(), false);
                        setInvoiceRequired(false);
                    }
                    status = bundle1.getString("SaleComplete");
                    setSaleComplete(true);
                    updateResult();
                    clearSale();
                    noClear = true;
                    setNoIncrement(true);
                    delay = Main.shop.getAutoclearMinutes();
                    if (delay < 0) {
                        //lock whenever sale complete
                        Main.salesScreen.lock();
                    }
                    return true;
                }
            } else {//not received
                int localCoupon = 0;
                if (!isSaleComplete()) {
                    localCoupon = Main.coupon.execute();
                    if (localCoupon > getTotal() - debit - coupon - cheque) {
                        //no coupon greater than total
                        setNoIncrement(true);
                        Audio.play("Ring");
                        return true;
                    }
                    if (localCoupon <= 0) {
                        return true;
                    }
                    coupon += localCoupon;//in case more than one
                    setNoIncrement(true);
                    toPay = getTotal() - debit - coupon - cheque;
                    if (toPay == 0) {
                        if (!saleComplete(SaleType.NORMAL)) {
                            Audio.play("Ring");
                            return true;
                        }
                        status = bundle1.getString("SaleComplete");
                        if (getTotal() - coupon - cheque - debit < 0) {
                            change = debit + coupon + cheque - getTotal();
                        } else {
                            change = 0;
                        }
                        delay = Main.shop.getAutoclearMinutes();
                        if (delay < 0) {
                            //lock whenever sale complete
                            Main.salesScreen.lock();
                        }
                        noClear = true;
                        Audio.play("CashReg");
                        if (change == 0) {
                            top = Main.customerMessages.getString("ThankYou");
                            bottom = Main.customerMessages.getString("ComeBackSoon");
                        }
                        Main.pole.execute(top, bottom);
                        Main.receiptPrinter.cashDrawerOpen();
                        if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                                || isReceiptRequired()) {
                            Main.receiptPrinter.printReceipt(getSale());
                            setReceiptRequired(false);
                        } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                            Main.invoice.print(getSale(), getDiscount(), false);
                            setInvoiceRequired(false);
                        }
                        status = bundle1.getString("SaleComplete");
                        setSaleComplete(true);
                        updateResult();
                        clearSale();
                        noClear = true;
                        setNoIncrement(true);
                        return true;
                    } else {
                        toPay = getTotal() - debit - coupon - cheque;
                        Audio.play("Beep");
                        updateResult();
                        return true;
                    }
                } else {
                    Audio.play("Ring");
                }
                return false;
            }
        }
        return false;
    }

    boolean debit() {
        if (getTotal() != 0 && (agencyFirstTime) && (Main.operator.isAcceptDebit() || Main.operator.isOwnerManager())
                || getTotal() > 0 && (Main.operator.isOverrideDebit() || Main.operator.isOwnerManager())) {
            if (!isSaleComplete()) {
                if (!isReceived) {
                    setTotal(aSale.getTotal());
                    if (Math.abs(getTotal()) >= Main.shop.minimumDebit || Main.operator.isOverrideDebit()) {
                        int newDebit = Main.debit.execute(getTotal() - coupon - cheque - debit, Main.shop.isCashBack());
                        if (newDebit == 0) {
                            return true;
                        }
                        debit += newDebit;
                        if (getTotal() - coupon - cheque <= debit) {
                            if (getTotal() - coupon - cheque < debit) {
                                change = debit + coupon + cheque - getTotal();
                            } else {
                                change = 0;
                            }
                            dataEntry.setText("");
                            Audio.play("CashReg");
                            setSaleComplete(true);
                            status = bundle1.getString("SaleComplete");
                            if (debit + coupon + cheque - getTotal() == 0) {
                                top = Main.customerMessages.getString("ThankYou");
                                bottom = Main.customerMessages.getString("ComeBackSoon");
                                Main.speech.say(Main.customerMessages.getString("ThankYou"));
                            } else {
                                top = Main.customerMessages.getString("YourCashBack") + (new Money(-getTotal() + debit + coupon + cheque)).toString();
                                bottom = Main.customerMessages.getString("ThankYou");
                                Main.speech.say(Main.customerMessages.getString("YourCashBack")
                                        + Main.speech.asCash(-getTotal() + debit + coupon + cheque));
                            }
                            Main.pole.execute(top, bottom);
                            Main.receiptPrinter.cashDrawerOpen();
                            if (!saleComplete(SaleType.NORMAL)) {
                                Audio.play("Ring");
                                return true;
                            }
                            if (Main.shop.receiptAlways && !Main.hardware.isInvoicePrinter()
                                    || isReceiptRequired()) {
                                Main.receiptPrinter.printReceipt(getSale());
                                setReceiptRequired(false);
                            } else if (Main.shop.receiptAlways && Main.hardware.isInvoicePrinter() || isInvoiceRequired()) {
                                Main.invoice.print(getSale(), getDiscount(), false);
                                setInvoiceRequired(false);
                            }
                            status = bundle1.getString("SaleComplete");
                            toPay = 0;
                            //                        updateResult();
                            resultsTable.repaint();
                            delay = Main.shop.getAutoclearMinutes();
                            if (delay < 0) {
                                //lock whenever sale complete
                                Main.salesScreen.lock();
                            }
                        } else {
                            partPaid = true; //stops further sales
                            toPay = getTotal() - debit - coupon - cheque;
                            resultsTable.repaint();
                            Audio.play("Beep");
                            if (debit == 0) {
                                //balanced out a part payment
                                partPaid = false;
                                if (toPay == getTotal()) {
                                    toPay = 0;
                                }
                            }
                        }
                        noClear = true;
                        delay = Main.shop.getAutoclearMinutes();
                        if (delay < 0) {
                            //lock whenever sale complete
                            Main.salesScreen.lock();
                        }
                    } else {
                        Audio.play("Ring");
                    }
                } else {
                    //received on account
                    if (getTotal() >= Main.shop.minimumDebit || Main.operator.isOverrideDebit()) {
                        Audio.play("Tada");
                        debit = Main.debit.execute(getTotal(), false);
                        if (getTotal() - coupon - cheque <= debit) {
                            //paid or paid with change
                            setReceiptRequired(true);
                            if (!saleComplete(SaleType.RECEIVEDONACCOUNT)) {
                                Audio.play("Ring");
                                return true;
                            }
                            setSaleComplete(true);
                            Main.receiptPrinter.printReceipt(getSale());
                            Audio.play("CashReg");
                            if (getTotal() - coupon - cheque < debit) {
                                //paid with change
                                top = Main.customerMessages.getString("YourCashBack") + (new Money(debit - getTotal())).toString();
                                bottom = Main.customerMessages.getString("ThankYou");
                                Main.speech.say(Main.customerMessages.getString("YourCashBack")
                                        + Main.speech.asCash(debit - getTotal()));
                                change = debit - getTotal() - coupon - cheque;
                            } else {
                                //paid no change
                                top = Main.customerMessages.getString("ThankYou");
                                bottom = Main.customerMessages.getString("ComeBackSoon");
                                Main.speech.say(Main.customerMessages.getString("ThankYou"));
                            }
                            resultsTable.repaint();
                            Main.pole.execute(top, bottom);
                            Main.receiptPrinter.cashDrawerOpen();
                            delay = Main.shop.getAutoclearMinutes();
                            if (delay < 0) {
                                //lock whenever sale complete
                                Main.salesScreen.lock();
                            }
                        } else if (debit != 0) {
                            //part paid
                            int oldTotal = total;
                            total = debit;
                            if (!saleComplete(SaleType.RECEIVEDONACCOUNT)) {
                                Audio.play("Ring");
                                return true;
                            }
                            setSaleComplete(true);
                            Main.receiptPrinter.printReceipt(getSale());
                            Audio.play("CashReg");
                            top = Main.customerMessages.getString("PartPayment") + " " + (new Money(debit)).toString();
                            bottom = Main.customerMessages.getString("ThankYou");
                            toPay = getTotal() - coupon - cheque - debit;
//                            setTotal(toPay);
                            //                        StringOps.showValue(changeLabel, changeT, 0);
                            //                        StringOps.showValue(toPayLabel, toPayT, toPay); 
                            total = 0;
                            resultsTable.repaint();
                            partPaid = true;
                            Main.pole.execute(top, bottom);
                            Main.receiptPrinter.cashDrawerOpen();
                        }
                    } else {
                        Audio.play("Ring");
                    }
                }
            }
        }
        return false;
    }

    private void speechUpdate(Line selectedLine) {
//        top = selectedLine.getQuantity() + "x" + selectedLine.getDescription();
//        charge = selectedLine.getCharge();
//        total = getaSale().getTotal();
//        String tot = Main.customerMessages.getString("Total") + (new Money(getTotal())).toString();
//        bottom = (new Money(charge)).toSpeech();
////        bottom = StringOps.fixLengthUntrimmed(bottom, 20 - tot.length());
//        bottom += " " + tot;
//        Main.speech.execute(top+" " + bottom);
    }

    /**
     * @param aOperatorData the operatorData to set
     */
    public void setOperatorData(String aOperatorData) {
        operatorData = aOperatorData;
    }

    /**
     * @return the sale
     */
    public int getSale() {
        return sale;
    }

    /**
     * @param aSale the sale to set
     */
    public void setSale(int aSale) {
        sale = aSale;
    }

    int getTax2() {
        return tax2;
    }

    /**
     * @return the invoiceRequired
     */
    public boolean isInvoiceRequired() {
        return invoiceRequired;
    }

    /**
     * @param aInvoiceRequired the invoiceRequired to set
     */
    public void setInvoiceRequired(boolean aInvoiceRequired) {
        invoiceRequired = aInvoiceRequired;
    }

    /**
     * @return the alpha
     */
    public boolean isAlpha() {
        return alpha;
    }

    /**
     * @param aAlpha the alpha to set
     */
    public void setAlpha(boolean aAlpha) {
        alpha = aAlpha;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param aSurname the surname to set
     */
    public void setSurname(String aSurname) {
        surname = aSurname;
    }

    /**
     * @param aFirstName the firstName to set
     */
    public void setFirstName(String aFirstName) {
        firstName = aFirstName;
    }

    /**
     * @return the agencyString
     */
    public String getAgencyString() {
        return agencyString;
    }

    /**
     * @param aAgencyString the agencyString to set
     */
    public void setAgencyString(String aAgencyString) {
        agencyString = aAgencyString;
    }

    /**
     * @return the trackIt
     */
    public boolean isTrackIt() {
        return trackIt;
    }

    /**
     * @param aTrackIt the trackIt to set
     */
    public void setTrackIt(boolean aTrackIt) {
        trackIt = aTrackIt;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param aAge the age to set
     */
    public void setAge(int aAge) {
        age = aAge;
    }

    /**
     * @return the shift
     */
    public boolean isShift() {
        return shift;
    }

    /**
     * @return the describeIt
     */
    public boolean isDescribeIt() {
        return describeIt;
    }

    /**
     * @param aDescribeIt the describeIt to set
     */
    public void setDescribeIt(boolean aDescribeIt) {
        describeIt = aDescribeIt;
    }

    /**
     * @return the weighIt
     */
    public boolean isWeighIt() {
        return weighIt;
    }

    /**
     * @return the priceIt
     */
    public boolean isPriceIt() {
        return priceIt;
    }

    /**
     * @return the priceEntry
     */
    public boolean isPriceEntry() {
        return priceEntry;
    }

    /**
     * @return the noIncrement
     */
    public boolean isNoIncrement() {
        return noIncrement;
    }

    /**
     * @param aNoIncrement the noIncrement to set
     */
    public void setNoIncrement(boolean aNoIncrement) {
        noIncrement = aNoIncrement;
    }

    /**
     * @param aTotal the total to set
     */
    public void setTotal(int aTotal) {
        total = aTotal;
    }

    /**
     * @return the pharmacistApproval
     */
    public boolean isPharmacistApproval() {
        return pharmacistApproval;
    }

    /**
     * @param aPharmacistApproval the pharmacistApproval to set
     */
    public void setPharmacistApproval(boolean aPharmacistApproval) {
        pharmacistApproval = aPharmacistApproval;
    }

    /**
     * @return the ageCheck
     */
    public boolean isAgeCheck() {
        return ageCheck;
    }

    /**
     * @return the receiptRequired
     */
    public boolean isReceiptRequired() {
        return receiptRequired;
    }

    /**
     * @return the track
     */
    public String getTrack() {
        return track;
    }

    /**
     * @param aTrack the track to set
     */
    public void setTrack(String aTrack) {
        track = aTrack;
    }

    /**
     * @return the verifiedAge
     */
    public int getVerifiedAge() {
        return verifiedAge;
    }

    /**
     * @param aShift the shift to set
     */
    public void setShift(boolean aShift) {
        shift = aShift;
    }

    /**
     * @param aReceiptRequired the receiptRequired to set
     */
    public void setReceiptRequired(boolean aReceiptRequired) {
        receiptRequired = aReceiptRequired;
    }

    /**
     * @param aVerifiedAge the verifiedAge to set
     */
    public void setVerifiedAge(int aVerifiedAge) {
        verifiedAge = aVerifiedAge;
    }

    /**
     * @param aAgeCheck the ageCheck to set
     */
    public void setAgeCheck(boolean aAgeCheck) {
        ageCheck = aAgeCheck;
    }

    /**
     *
     * @return weighIt
     */
    boolean getWeighIt() {
        return weighIt;
    }

    boolean getPriceEntry() {
        return priceEntry;
    }

    void setWeighIt(boolean b) {
        weighIt = b;
    }

    void setPriceEntry(boolean b) {
        if (!b) {
            int x = 0;
        }
        priceEntry = b;
    }

    void setPriceIt(boolean b) {
        priceIt = b;
    }

    String getFirstName() {
        return firstName;
    }

    /**
     * @return the operatorData
     */
    public String getOperatorData() {
        return operatorData;
    }

    void layaway() {
        if (Main.operator.isSalesPerson() && getTotal() != 0 && (agencyFirstTime)
                || Main.operator.isOwnerManager() ) {
            if (isSaleComplete()) {
                Audio.play("Ring");
                return;
            }
            //layaway the sale
            if (!saleComplete(SaleType.LAYAWAY)) {
                Audio.play("Ring");
                return;
            }
            dataEntry.setText("");
            Audio.play("CashReg");
            setSaleComplete(true);
            top = Main.customerMessages.getString("ThankYou");
            bottom = Main.customerMessages.getString("ReadyToServeYou");
            Main.speech.say(top + " " + bottom);
            Main.pole.execute(top, bottom);
            long saleId = getSale();
            long layawayCode = saleId;
            Main.receiptPrinter.printLayaway(saleId);
        }
    }
    
    public boolean layawayRetrieve(String data){
        if (data.length() == 0 || isDescribeIt() || isPriceIt() || isWeighIt() || isPriceEntry() || isWeighIt() || partPaid) {
            return false;
        }
        if (data.length()>8 && data.startsWith(SaleType.LAYAWAYRETRIEVE.codeString()) ){
            int x=0;
        }
        return false;
    }

    private void showStockLevel() {
        if (stockQuantity - getQty() < 3 && (stockType == SkuType.INCLUDED.value()
                || stockType == SkuType.OTHERS.value())) {
            description = description + " (" + (stockQuantity - getQty()) + ")";
        }
    }
}
