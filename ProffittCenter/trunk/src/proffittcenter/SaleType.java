/*
 * SaleType.java
 *
 * Created on 02 April 2007, 17:07
 *
 * a typesafe enumerator from 'Effective Java'
 */

package proffittcenter;

import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * specifies the type of sale - normal, waste, on account charged etc.
 * @author Dave Proffitt <br>proffittcenter@gmail.com</br>
 */
public class SaleType {
    // a typesafe enumerator from 'Effective Java'
    private static ResourceBundle bundle = ResourceBundle
            .getBundle("proffittcenter/resource/SaleType");
    private final String name;//such as "Charged"
    //1000030;//such as 1000033
    private static long code=Long.parseLong(bundle.getString("SaleTypeBase"));
    private long thisCode;
    private static short value=0;
    private Short thisValue;
    private static Hashtable shortHash=new Hashtable(20);
    private static Hashtable longHash=new Hashtable(20);
    /**
     * Creates a new instance of SaleType
     */
    private SaleType(String name){

        this.name=name;//the name of the type - such as "Charged"
        thisCode=code++;//the code such as 1000033
        thisValue=value++;//starts at 1 and increments
        shortHash.put(thisValue, this);
        longHash.put(thisCode, this);
    }
    
    @Override
    public String toString(){
        return name;
    }
    /**
     * finds the code value for the specified object
     * @return long code value
     */
    public long code(){return thisCode;}//returns a number
    
    /**
     * returns a description of the object
     * @return String description
     */
    public String codeString(){return thisCode+"";}//returns same number as a string
    
    /**
     * returns short value of object
     * @return short
     */
    public short value(){return thisValue;}//returns the Type - same as in Waste
    
    /**
     * given a short value as aValue, returns a description of SaleType
     * @param aValue (short)
     * @return SaleType
     */
    public static String description(short aValue){
        //returns a description of a value
        //i.e. 4 returns Charged
        SaleType st=(SaleType)shortHash.get(aValue);
        return st.toString();
    }
    /**
     * given a int aCode, returns the SaleType
     * @param aCode
     * @return a description of the aCode
     */
    public static String description(int aCode){
        //returns a description of a code
        //i.e. 1000034 returns Charged
        SaleType st=(SaleType)longHash.get(aCode);
        return st.toString();
    }
    public String valueString(){
        return thisValue.toString();
    }
    
    //max 256 constants; each one is a triple - a String, a number and a short
    //such as ("Charged",1000033,4)
    /**
     * NORMAL is a standard file
     */
    public static final SaleType NORMAL
            =new SaleType(bundle.getString("Normal"));//1000031,0
    /**
     * Waste is a waste sale
     */
    public static final SaleType WASTE
            =new SaleType(bundle.getString("Waste"));//1000032,1
    /**
     * RETURN is returnable waste
     */
    public static final SaleType RETURN
            =new SaleType(bundle.getString("Return"));//1000033,2
    /**
     * LOSS is stock lost found during stock take
     */
    public static final SaleType LOSS=
            new SaleType(bundle.getString("Loss"));//1000034,3
    /**
     * CHARGED is a sale that has been charged to a customer
     */
    public static final SaleType CHARGED=
            new SaleType(bundle.getString("Charged"));//1000035,4
    /**
     * RECEIVEDONACCOUNT is cash received off somebodies account
     */
    public static final SaleType RECEIVEDONACCOUNT
            =new SaleType(bundle.getString("Received_on_account"));//1000036,5
    /**
     * OWNUSE is a sale of goods to oneself
     */
    public static final SaleType OWNUSE
            =new SaleType(bundle.getString("Own_Use"));//1000037,6
    public static final SaleType OPERATOR
            =new SaleType(bundle.getString("Operator"));//1000038,7
    public static final SaleType CUSTOMER
            =new SaleType(bundle.getString("Customer"));//1000039,8
    public static final SaleType BOX
            =new SaleType(bundle.getString("Box"));//10000310,9
    public static final SaleType DISK
            =new SaleType(bundle.getString("Disk"));//10000311,10
    public static final SaleType DELIVERY
            =new SaleType(bundle.getString("Delivery"));//10000312,11
    public static final SaleType NOSALE
            =new SaleType(bundle.getString("NoSale"));//10000313,12
    public static final SaleType CREDITCUSTOMER
            =new SaleType(bundle.getString("CreditCustomer"));//10000314,13
    public static final SaleType LAYAWAY
            =new SaleType(bundle.getString("Layaway"));//10000315,14
    public static final SaleType LAYAWAYRETRIEVE
            =new SaleType(bundle.getString("LayawayRetrieve"));//10000316,15
}
