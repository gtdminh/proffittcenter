/*
 * Regime.java
 *
 * Created on 26 May 2007, 10:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package proffittcenter;

import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * 
 * @author Dave Proffitt <br>proffittcenter@gmail.com</br>
 */
//Ordinal-based tysafe enum
public class Regime {

    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Regime");
    private final String name;
    private static int nextOrdinal = 0;
    private int ordinal;
    static Hashtable hash = new Hashtable(20);
    static Hashtable hash2 = new Hashtable(20);

    /** Creates a new instance of Regime */
    private Regime(String name) {
        this.name = name;
        ordinal = nextOrdinal++;
        hash.put(name, this);
        hash2.put(ordinal, this);
    }

//    private Regime(){
//
//    }

    @Override
    public String toString() {
        return bundle.getString(name);
    }

    public static Regime description(String aName) {
        return (Regime) hash.get(aName);        
    }

    public static int count(){
        return nextOrdinal;
    }
    /**
     * 
     * @param aCode
     * @return a Regime type
     */
    public static Regime typeOf(int aCode){
        //returns a Tax Type for a code
        return (Regime)hash2.get(aCode);
    }
    public int value(){return getOrdinal();}
    
    /**
     * no tax applied
     */
    public static final Regime NONE = new Regime("None");
    /**
     * Under a VAT scheme, a registered trader issues VAT receipts
     */
    public static final Regime REGISTERED = new Regime("Registered");
    /**
     * Under a VAT scheme, a wholesaler deals with trade customers 
     * and separates VAT on receipts
     */
    public static final Regime WHOLESALE = new Regime("Registered wholesale");

    /**
     * SalesTax means that tax is charged on the total sales, afterwards
     */
    public static final Regime SALESTAX = new Regime("SalesTax");

    /**
     * Under a VAT scheme, an unregistered trader cannot issue a VAT receipt 
     * or charge VAT
     */
    public static final Regime UNREGISTERED = new Regime("Unregistered");

    /**
     * @return the ordinal
     */
    public int getOrdinal() {
        return ordinal;
    }

}
