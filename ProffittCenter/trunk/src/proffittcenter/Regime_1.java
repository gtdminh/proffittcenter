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
public class Regime_1 {

    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Regime");
    private final String name;
    private static int nextOrdinal = 0;
    private int ordinal;
    static Hashtable hash = new Hashtable(20);
    static Hashtable hash2 = new Hashtable(20);

    /** Creates a new instance of Regime */
    private Regime_1(String name) {
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

    public static Regime_1 description(String aName) {
        return (Regime_1) hash.get(aName);        
    }

    public static int count(){
        return nextOrdinal;
    }
    /**
     * 
     * @param aCode
     * @return a Regime type
     */
    public static Regime_1 typeOf(int aCode){
        //returns a Tax Type for a code
        return (Regime_1)hash2.get(aCode);
    }
    public int value(){return getOrdinal();}
    
    /**
     * no tax applied
     */
    public static final Regime_1 NONE = new Regime_1("None");
    /**
     * Under a VAT scheme, a registered trader issues VAT receipts
     */
    public static final Regime_1 REGISTERED = new Regime_1("Registered");
    /**
     * Under a VAT scheme, a wholesaler deals with trade customers 
     * and separates VAT on receipts
     */
    public static final Regime_1 WHOLESALE = new Regime_1("Registered wholesale");

    /**
     * SalesTax means that tax is charged on the total sales, afterwards
     */
    public static final Regime_1 SALESTAX = new Regime_1("SalesTax");

    /**
     * Under a VAT scheme, an unregistered trader cannot issue a VAT receipt 
     * or charge VAT
     */
    public static final Regime_1 UNREGISTERED = new Regime_1("Unregistered");

    /**
     * @return the ordinal
     */
    public int getOrdinal() {
        return ordinal;
    }

}
