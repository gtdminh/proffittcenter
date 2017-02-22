/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.util.HashMap;

/**
 *
 * @author Dave
 */
public class ApplyOffers_1 {

    private static int saleCount;
    private static int quantityNormal;
    private static int c;
    private static int quantityDiscounted;
    private static int z;
    private static int limit;
    private static int discountedPrice;
    private static long p;
    private static int price;
    private static boolean pricedOver;
    private static boolean discounted;
    private static int offerType;
    private static int q;
    private static int ix;
    private static int x;
    private static int y;
    private static int quantityIncluded;
//    private static final Integer[] offer = {ix,offerType, saleCount, quantityNormal,quantityDiscounted, z, x, y};
//    private static Hashtable<Integer,offer> offers = new Hashtable<Integer,offer>();
    private static HashMap<Integer, OfferLine> offerHash = new HashMap<Integer, OfferLine>();
    private static OfferLine offerLine;
    private static int includedPrice;
    private static boolean first;
    private static int changedPrice;
    private static int originalPrice;
    private static int taxRate;
    private static Long product;
    private static String productDescription;
    private static int encode;
    private static String track;
    private static int tax1;
    private static int wholesalePrice;
    private static int packSize;
    private static String description;

    public static void setup() {
        //Now scan through the sale lines every time they change
        //counting up the number of each product involved in an offer
        //and saving back into the Offers table
        saleCount = 0;
        offerHash.clear();
        for (int lineSelected = 0; lineSelected < LineList.getLines().size(); lineSelected++) {
            quantityNormal = 0;
            c = 0;
            quantityDiscounted = 0;
            z = 0;
            limit = 0;
            discountedPrice = 0;
            Main.sale.setSelection(lineSelected);
            Line line = Main.sale.getSelectedLine();
            offerType = line.getOfferType();
            if (offerType == 0) {//no offer, ignore
                continue;
            }
            ix = line.getIx();
            if (ix == 0) {//no offer
                continue;
            }
            offerLine = offerHash.get(ix);
            if (offerLine == null) {
//                offerType = line.getOfferType();
                saleCount = 0;
                quantityNormal = 0;
                quantityDiscounted = 0;
                z = 0;
                x = 0;
                y = 0;
            } else {
                offerType = line.getOfferType();
                saleCount = offerLine.getSaleCount();
                quantityNormal = offerLine.getQuantityNormal();
                quantityDiscounted = offerLine.getQuantityDiscounted();
                discountedPrice = offerLine.getDiscountedPrice();
            }
            x = line.getX();
            p = line.getProduct();//get the barcode of the product
            price = line.getOriginalPrice();
            pricedOver = line.isPricedOver();
            discounted = line.isDiscounted();
            q = line.getQuantity();
            saleCount += q;
            discountedPrice += q * price;
//            x = line.getX();
//            y = line.getY();
            limit = line.getLimit();
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
            z = (quantityIncluded + quantityDiscounted) / x;
            line.setZ(z);
            //{ saleCount, quantityNormal,quantityDiscounted, quantityIncluded, discountedPrice}
            offerLine = new OfferLine(saleCount, quantityNormal, quantityDiscounted, quantityIncluded, discountedPrice);
            offerHash.put(ix, offerLine);
            discountedPrice =0;
        }
    }

    public static void calculate() {
        //now apply the offers
            includedPrice = 0;
            int size = LineList.getLines().size();
           for (int lineSelected = 0; lineSelected < size; lineSelected++) {
                //once for each line in sale
                first = false;
                Main.sale.setSelection(lineSelected);
                Line line2 = Main.sale.getSelectedLine();
                p = line2.getProduct();
                description = line2.getDescription();
                pricedOver = line2.isPricedOver();
                discounted = line2.isDiscounted();
                changedPrice = line2.getRetailPrice();
                originalPrice = line2.getOriginalPrice();                
                offerType = line2.getOfferType();//the offer type 3 for pack  
                x = line2.getX();//2
                y = line2.getY();//150 the offer price
                z = line2.getZ();//0              
                ix = line2.getIx();
                q = line2.getQuantity();//5 number sold on current line
                //need to get price from line
                price = line2.getOriginalPrice();//100
                changedPrice = line2.getRetailPrice();
                taxRate = line2.getTaxRate();
                product = line2.getBarcode();
                productDescription = line2.getDescription();
                encode = line2.getEncode();
                track = line2.getTrack();
                tax1 = line2.getTaxID();
                wholesalePrice = 0;
                packSize = 0;
                if ( ix==0 || pricedOver || discounted) {//no offer for this product
                    continue;
                }//else offer found
                offerLine = offerHash.get(ix);
                saleCount = offerLine.getSaleCount();//5 the number sold
                quantityNormal = offerLine.getQuantityNormal();//1
                quantityDiscounted = offerLine.getQuantityDiscounted();//2
                discountedPrice = offerLine.getDiscountedPrice();
                quantityIncluded = offerLine.getQuantityIncluded();//2
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
                            Line newLine = new Line(line2);
                            newLine.setQuantity(q);
                            newLine.setDescription(productDescription);
                            int retail = price * quantityDiscounted + z * y - discountedPrice;//((price+y)*q -discountedPrice)/quantityDiscounted;
                            newLine.setRetailPrice(retail * quantityDiscounted);
                            newLine.setProduct(product);
                            newLine.setTaxrate(taxRate);
                            newLine.setOriginalPrice(originalPrice);
                            newLine.setTrack(track);
                            newLine.setEncode(encode);
                            newLine.setTax(tax1);
                            newLine.setWholesalePrice(wholesalePrice);
                            newLine.setPackSize(packSize);
                            newLine.setIx(ix);
                            newLine.setX(x);
                            newLine.setY(y);
                            newLine.setZ(z);
                            newLine.setBarcode(product);
                            newLine.setDescription(description);
                            Main.sale.add(newLine);
                            Main.salesScreenFunctions.getModel().fireTableDataChanged();
                            size++;
//                            Main.sale.setSelection(lineSelected);
//                            line2 = Main.sale.getSelectedLine();
                            quantityDiscounted -= q;
                            q = 0;
                            lineSelected++;
                        }
                    } else if (q != 0 && quantityDiscounted != 0) {
                        line2.setQuantity(q);
                        line2.setOriginalPrice(price);
                        int retail = price * quantityDiscounted + z * y - discountedPrice;//((price+y)*q -discountedPrice)/quantityDiscounted;
                        line2.setRetailPrice(retail * quantityDiscounted);
                        line2.setOriginalPrice(originalPrice);
                        discountedPrice = 0;
                        quantityDiscounted -= q;
                        q = 0;
                    }
                } else if (offerType == Offer.QUANTITY) {
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
                offerLine.setQuantityDiscounted(quantityDiscounted);
                offerLine.setQuantityIncluded(quantityIncluded);
                offerLine.setQuantityNormal(quantityNormal);
                offerLine.setSaleCount(saleCount); 
                offerLine.setDiscountedPrice(discountedPrice);
            }
    }

    public ApplyOffers_1() {
    }
}
