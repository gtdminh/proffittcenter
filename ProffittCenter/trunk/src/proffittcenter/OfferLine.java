/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

/**
 *
 * @author Dave
 */
public class OfferLine {
    private Integer saleCount;
    private Integer quantityNormal;
    private Integer quantityDiscounted;
    private Integer quantityIncluded;
    private Integer discountedPrice;
    
//    private Integer[] offer = { saleCount, quantityNormal,quantityDiscounted, quantityIncluded, discountedPrice};
    public OfferLine(int saleCount, int quantityNormal, 
            int quantityDiscounted, int quantityIncluded, int discountedPrice){
        this.saleCount = saleCount;
        this.quantityNormal = quantityNormal;
        this.quantityDiscounted = quantityDiscounted;
        this.quantityIncluded = quantityIncluded;
        this.discountedPrice = discountedPrice;
    }


    /**
     * @return the saleCount
     */
    public Integer getSaleCount() {
        return saleCount;
    }

    /**
     * @param saleCount the saleCount to set
     */
    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    /**
     * @return the quantityNormal
     */
    public Integer getQuantityNormal() {
        return quantityNormal;
    }

    /**
     * @param quantityNormal the quantityNormal to set
     */
    public void setQuantityNormal(Integer quantityNormal) {
        this.quantityNormal = quantityNormal;
    }

    /**
     * @return the quantityDiscounted
     */
    public Integer getQuantityDiscounted() {
        return quantityDiscounted;
    }

    /**
     * @param quantityDiscounted the quantityDiscounted to set
     */
    public void setQuantityDiscounted(Integer quantityDiscounted) {
        this.quantityDiscounted = quantityDiscounted;
    }


//    /**
//     * @return the offer
//     */
//    public Integer[] getOffer() {
//        return offer;
//    }

//    /**
//     * @param offer the offer to set
//     */
//    public void setOffer(Integer[] offer) {
//        this.offer = offer;
//    }

    /**
     * @return the quantityIncluded
     */
    public Integer getQuantityIncluded() {
        return quantityIncluded;
    }

    /**
     * @param quantityIncluded the quantityIncluded to set
     */
    public void setQuantityIncluded(Integer quantityIncluded) {
        this.quantityIncluded = quantityIncluded;
    }

    /**
     * @return the discountedPrice
     */
    public Integer getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * @param discountedPrice the discountedPrice to set
     */
    public void setDiscountedPrice(Integer discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
