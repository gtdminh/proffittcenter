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
public class PerCent {
    private int value;
    
    public PerCent(int value){
        this.value=value;
    }
    
    public PerCent(String text) {
        text=StringOps.numericOnly(text);
        if(text.isEmpty()){
            text="0";
        }else{
            
        }
        value = Integer.parseInt(text);
    }
    
    public static String asPerCent(int value) {
        if(value==0){
            return "0.00%";
        }
        String s= ""+value;
        if(s.length()==1){
            s="00"+s;
        }else if(s.length()==2){
            s="0"+s;
        }
        if(s.length()>=3){
            String first=s.substring(0, s.length()-2);
            String end = s.substring(s.length()-2);
            s=first + '.' + end+'%';
        }
        return s;
    }
    
    public static void asPerCent(JTextField t){
        String s=StringOps.numericOnly(t.getText());
        if(s.isEmpty()){
            s="0";
        }
        int value=Integer.parseInt(s);
        value=Math.min(value, 9999);
        value=Math.max(value, 0);
        if(value>9999){
            value=9999;
        }
        s=asPerCent(value);
        t.setText(s);
        t.setCaretPosition(s.length()-1);
    }
    
    @Override
    public String toString(){
        NumberFormat nf = NumberFormat.getInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
        String aValue = nf.format((new Double((Integer) value)) / 100);
        return aValue;
         
    }

    
    public int getValue(){
        return value;
    }

}
