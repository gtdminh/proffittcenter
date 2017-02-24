/*
 * Line.java
 * The details of a single line of a sale before payment
 *
 * Created on 03 October 2006, 16:27
 *
 * @author Dave Proffitt
 * @version 2006.10.03
 */
package proffittcenter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Proffitt
 */
public class Line {
    private static int encoded;

    /**
     * @return the encoded
     */
    public static int getEncoded() {
        return encoded;
    }

    /**
     * @param aEncoded the encoded to set
     */
    public static void setEncoded(int aEncoded) {
        encoded = aEncoded;
    }

    private int quantity = 0;
    private String description = "";
    private int retailPrice = 0;//measured in pence
    private Long barcode;//the bar code
    private int taxRate = 175;//tax rate in % times 10
    private int originalPrice;
    private String track;
    private int encode;
    private int charge;
    private boolean pricedOver;
    private boolean discounted;
    private int taxID;
    static ResourceBundle regimeBundle = ResourceBundle.getBundle("proffittcenter/resource/Regime");
    private int wholesalePrice;
    private int packSize;
    private int tax2ID;
    private int tax2Rate;
    private int ix;//from offers
    private int x;//from offers
    private int y;//from offers
    private int offerType;//from offers
    private int limit;//from offers
    private int z;
    private int discountedPrice;

    /** Creates a new line of Line */
    public Line() {
        quantity = 0;
        description = "";
        barcode = 0L;
        description = "";
        retailPrice = 0;
        originalPrice = 0;
        track = "";
        encode = 0;
        pricedOver = false;
        discounted = false;
        taxID = 1;
        taxRate = 0;
        wholesalePrice=0;
        packSize = 1;
        tax2ID = 1;
        tax2Rate = 0;
        ix = 0;
        x = 0;
        y = 0;
        z = 0;
        offerType = 0;
        limit = 0;
        discountedPrice = 0;
    }

    /** Creates a new line with values
     * @param quantity the quantity
     * @param description the description
     * @param price the price
     * @param barcode the bar code
     * @param taxRate the tax rate
     * @param originalPrice
     * @param track
     * @param encode  
     */
    public Line(int quantity, String description, int price, long barcode,
            int taxRate, int originalPrice, String track, int encode, boolean pricedOver, boolean discounted,
            int taxID, int wholesalePrice, int packSize,
            int tax2ID, int tax2Rate,int ix,int x,int y,int z, int offerType, int limit, int discountedPrice) {
        super();
        this.quantity = quantity;
        this.description = description;
        this.retailPrice = price;
        this.originalPrice = originalPrice;
        this.barcode = barcode;
        this.taxRate = taxRate;
        this.track = track;
        this.encode = encode;
        this.pricedOver= pricedOver;
        this.discounted = discounted;
        this.taxID = taxID;
        this.wholesalePrice = wholesalePrice;
        this.packSize = packSize;
        this.tax2Rate = tax2Rate;
        this.tax2ID = tax2ID;
        this.ix = ix;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offerType = offerType;
        this.limit = limit;
        this.discountedPrice = discountedPrice;
    }
    
    public Line(Line line){
        super();
        this.quantity = line.getQuantity();
        this.description = line.getDescription();
        this.retailPrice = line.getRetailPrice();
        this.originalPrice = line.getOriginalPrice();
        this.barcode = line.getBarcode();
        this.taxRate = line.getTaxRate();
        this.track = line.getTrack();
        this.encode = line.getEncode();
        this.pricedOver= line.isPricedOver();
        this.discounted = line.isDiscounted();
        this.taxID = line.getTaxID();
        this.wholesalePrice = line.getWholesalePrice();
        this.packSize = line.getPackSize();
        this.tax2Rate = line.getTax2Rate();
        this.tax2ID = line.getTax2ID();
        this.ix = line.getIx();
        this.x = line.getX();
        this.y = line.getY();
        this.z = line.getZ();
        this.offerType = line.getOfferType();
        this.limit = line.getLimit();
        this.discountedPrice = line.getDiscountedPrice();
    }

    /** Setters and getters
     * @param q the quantity
     */
    public void setQuantity(int q) {
        quantity = q;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProduct(long b) {
        setBarcode((Long) b);
    }

    public long getProduct() {
        return getBarcode();
    }

    public void setDescription(String d) {
        description = d;
    }

    public String getDescription() {
        return description;
    }

    public void setRetailPrice(int p) {
        retailPrice = p;
    }

    public void setOriginalPrice(int p) {
        originalPrice = p;
    }

    /**
     * @return the retail price charged
     */
    public int getRetailPrice() {
        return retailPrice;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxrate(int t) {
        taxRate = t;
    }

    /**
     * @return the price before any savings
     */
    public int getOriginalPrice() {
        return originalPrice;
    }

    /**
     * @return the track
     */
    public String getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * @return the encode
     */
    public int getEncode() {
        return encode;
    }

    /**
     * @param encode the encode to set
     */
    public void setEncode(int encode) {
        this.encode = encode;
    }

    /**
     * gets the charge for a line
     * allowing for different treatment of encoded items
     * @return the charge in pence
     */
    int getCharge() {
      charge = quantity * retailPrice;
       if (encode == NewProduct.ENCODEBYWEIGHTPARITY || encode == NewProduct.ENCODEBYWEIGHTNOPARITY) {
           if(charge>0){
                charge = (charge+500)/1000;
           } else {
               charge = (charge-500)/1000;
           }
        }
        return charge;
    }

    /**
     * 
     * @param rs must deliver Products.Encoded,
     * Quantity, Price {normally from SaleLines
     * and Taxes.Rate
     * @return the charge in pence
     */
    static int getCharge(ResultSet rs) {
        int charge = 0;
        Regime s=Regime.description(Main.shop.tax);
        String tax=Main.shop.tax;
        try {
           if ( tax.compareToIgnoreCase(regimeBundle.getString("Registered")) == 0){
                encoded=rs.getInt("Products.Encoded");
                int quantity = rs.getInt("Quantity");
                int price = rs.getInt("Price");
                if (encoded == NewProduct.ENCODEBYWEIGHTPARITY || encoded == NewProduct.ENCODEBYWEIGHTNOPARITY) {
                    charge = (quantity * price+500) / 1000;
                    charge = quantity * price;
                } else {
                    charge = quantity * price;                    
                }
            } else if (tax.compareToIgnoreCase(regimeBundle.getString("Unregistered")) == 0
                    ||tax.compareToIgnoreCase(regimeBundle.getString("Wholesale")) == 0
                    || tax.compareToIgnoreCase(regimeBundle.getString("SalesTax")) == 0
                    || tax.compareToIgnoreCase(regimeBundle.getString("None")) == 0) {
                if (rs.getInt("Products.Encoded") == NewProduct.ENCODEBYWEIGHTPARITY || encoded == NewProduct.ENCODEBYWEIGHTNOPARITY) {
                    charge = rs.getInt("Quantity") * rs.getInt("Price");
                    charge = (charge + 500)/1000;
                } else {
                    charge = rs.getInt("Quantity") * rs.getInt("Price");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Line.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return charge;
    }

    /**
     * @return the pricedOver
     */
    public boolean isPricedOver() {
        return pricedOver;
    }

    /**
     * @param pricedOver the pricedOver to set
     */
    public void setPricedOver(boolean pricedOver) {
        this.pricedOver = pricedOver;
    }

    /**
     * @return the discounted
     */
    public boolean isDiscounted() {
        return discounted;
    }

    /**
     * @param discounted the discounted to set
     */
    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    /**
     * @return the tax
     */
    public int getTaxID() {
        return taxID;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(int tax) {
        this.taxID = tax;
    }

    /**
     * @return the wholesalePrice
     */
    public int getWholesalePrice() {
        return wholesalePrice;
    }

    /**
     * @param wholesalePrice the wholesalePrice to set
     */
    public void setWholesalePrice(int wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    /**
     * @return the packSize
     */
    public int getPackSize() {
        return packSize;
    }

    /**
     * @param packSize the packSize to set
     */
    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    /**
     * @return the barcode
     */
    public Long getBarcode() {
        return barcode;
    }

    /**
     * @param barcode the barcode to set
     */
    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    /**
     * @return the tax2ID
     */
    public int getTax2ID() {
        return tax2ID;
    }

    /**
     * @return the tax2Rate
     */
    public int getTax2Rate() {
        return tax2Rate;
    }

    /**
     * @return the ix
     */
    public int getIx() {
        return ix;
    }

    /**
     * @param ix the ix to set
     */
    public void setIx(int ix) {
        this.ix = ix;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @param tax2ID the tax2ID to set
     */
    public void setTax2ID(int tax2ID) {
        this.tax2ID = tax2ID;
    }

    /**
     * @param tax2Rate the tax2Rate to set
     */
    public void setTax2Rate(int tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    /**
     * @return the offerType
     */
    public int getOfferType() {
        return offerType;
    }

    /**
     * @param offerType the offerType to set
     */
    public void setOfferType(int offerType) {
        this.offerType = offerType;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return the discountedPrice
     */
    public int getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * @param discountedPrice the discountedPrice to set
     */
    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    
}
