/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.text.NumberFormat;
import javax.swing.JTextField;

/**
 *
 * @author HP_Owner
 */
public class Money implements Comparable<Money> {

    String  money;
    int value;

    public Money(int value) {
        this.value=value;
    }

    public static void asMoney(JTextField t){
        String s=StringOps.numericOnly(t.getText());
        if(s.isEmpty()){
            s="0";
        }
        int value=Integer.parseInt(s);
        s=asMoney(value);
        t.setText(s);
        String ss=Main.shop.getPennySymbol();
        if(s.endsWith(ss)){
            t.setCaretPosition(s.length()-ss.length());
        }
    }

    @Override
    public String toString(){
        NumberFormat nf = NumberFormat.getInstance();
                    nf.setMinimumFractionDigits(2);
        String aValue = nf.format((new Double((Integer) value)) / 100);
        return aValue;
    }

    public String toSpeech(){
        //convert a money value toa spoken value
        return asMoney(value);
    }

    public static String asMoney(int value){
        //converts a int to a string money represenation
        if(value>0){//
            if(value<100){//show in pence
                return " "+value+Main.shop.getPennySymbol();
            }else{//show in pounds
                String newValue = "" + value;
                newValue = newValue.substring(0, newValue.length() - 2)
                        + "." + newValue.substring(newValue.length() - 2);
                newValue = Main.shop.poundSymbol + newValue;
                return newValue;
            }
        }else if(value<0){
            return "-"+asMoney(-value);
        }else{
            return "0"+Main.shop.getPennySymbol();
        }
    }

    public Integer toInt(){
        return value;
    }

    public int compareTo(Money o) {
        return this.value-o.value;
    }
}
