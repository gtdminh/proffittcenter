/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 *
 * @author HP_Owner
 */
public class SkuType {
    // a typesafe enumerator from 'Effective Java'
    static ResourceBundle bundle = ResourceBundle
            .getBundle("proffittcenterworkingcopy/resource/SkuType");
    private final String name;//such as "Other people's stock"
    private static short value=0;
    private short thisValue;
    static Hashtable shortHash=new Hashtable(20);
    private SkuType(String name){
        this.name=name;//the name of the type - such as "Charged"
        thisValue=value++;//starts at 1 and increments
        shortHash.put(thisValue, this);
    }
    @Override
    public String toString(){
        return name;
    }

    public static String description(short aValue){
        //returns a description of a value
        //i.e. 4 returns Charged
        SkuType k=(SkuType)shortHash.get(aValue);
        return k.toString();
    }

    public static short size(){
        return value;
    }

    public static short typeOf(String description){
        Enumeration e=shortHash.keys();
        while(e.hasMoreElements()){
            Object s=e.nextElement();
            String t= shortHash.get(s).toString();
            if(t.equalsIgnoreCase(description)){
                return (Short)s;
            }
        }
            return (Short)null;
    }
    public short value(){return thisValue;}//returns the Type //max 256 constants; each one is a pair - a String and a number 
    
    public static final SkuType DEFAULT
            =new SkuType(bundle.getString("Default"));//0
    public static final SkuType INCLUDED
            =new SkuType(bundle.getString("Included"));//1
    public static final SkuType EXCLUDED
            =new SkuType(bundle.getString("Excluded"));//2
    public static final SkuType OTHERS=
            new SkuType(bundle.getString("Others"));//3
    public static final SkuType RENTAL=
            new SkuType(bundle.getString("Rental"));//4
    public static final SkuType VIDEO=
            new SkuType(bundle.getString("Video"));//5
    public static final SkuType STOP
            =new SkuType(bundle.getString("Stop"));//6
}
