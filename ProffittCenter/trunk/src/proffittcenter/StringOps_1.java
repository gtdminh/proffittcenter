/*
 * StringOps.java
 *
 * Created on 05 December 2006, 06:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package proffittcenter;

import javax.swing.*;

/**
 *
 * @author David Proffitt
 */
public class StringOps_1 {

    /**
     * checks if numbers and spaces
     * @param s
     * @return true if only numbers and spaces
     */
    
    static boolean containsOnlyNumbersAndSpaces(String s) {
        int i = 0;
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(c >= '0' && c <= '9'||c==' ')) {//char not numeric, delete
                return false;
            }
        }
        return true;
    }

    /**
     * takes all of the extra spaces added to a displayed string (in dataEntry)
     * @param s
     * @return s without spaces
     */
    static String stripSpaces(String s) {
         if (s.isEmpty()) {
            return "";
        }
        //strip all space characters
        int i = 0;
        s=s.trim();
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ' ) {//char is space, delete
                s = s.substring(0, i) + s.substring(i + 1);
                i--;
            }
        }
        return s;
    }

    /**
     * returns a string that has no more than 16 character
     * @param data
     * @return the chopped data
     */
    static String maxLength(String data) {
        data = numericOnly(data);
        int len=data.length();
        if(len>16){
            return data.substring(0, 16);
        }
        return data;
    }

    char[] spaces = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    static String spaceString =
            "                                                                                                                                   ";

    /** Creates a new instance of StringOps */
    private StringOps_1() {
        //this constructor will never be invoked
    }

    /**
     * converts a int to a string money representation
     * @param value
     * @return money string
     */
    public static String asPounds(int value) {
        //converts a int to a string money representation
        String pound,penny;
        if(Main.shop==null){
            pound = "£";
            penny = "p";
        } else {
            pound = Main.shop.poundSymbol+" ";
            penny = Main.shop.getPennySymbol();
        }
        if (value > 0) {
            String stringValue = "" + value;//converts to string
            if (value < 100) {
                if(penny.isEmpty()){// a high value currency
                    {
                      stringValue="" + 100*value;
                     stringValue = pound + stringValue;
                    }
                } else {
                    stringValue = "0" + value;
                    stringValue = pound + "0." + stringValue.substring(stringValue.length() - 2);
                }
            } else {
                stringValue = pound + stringValue.substring(0, stringValue.length() - 2) + "." + stringValue.substring(stringValue.length() - 2);
                int len = stringValue.length()-pound.length();
                //insert extra space if large number, but not in poundSymbol
                if (len >= 7) {//
                    String s1 = stringValue.substring(0, len-6);
                    String s2 = stringValue.substring(len-6);
                    stringValue=s1 +" " +s2;
                }
            }
            return stringValue;
        } else if (value < 0) {
            return "-" + asPounds(-value);
        } else {
            return pound + "0.00";
        }
    }

    /**
     * takes an int value and outputs it as so many %
     * @param value
     * @return 
     */
    public static String asPerCent(int value) {
        String s;
        //value is percent times 10
        if(value>0){
            s = value + "%";
            return s.substring(0, s.length() - 2) + "." + s.substring(s.length() - 2);
        } else if(value==0){
            s = value + "%";
            return s;
        } else {
            s = -value + "%";
            return "-"+s.substring(0, s.length() - 2) + "." + s.substring(s.length() - 2);
        }
    }

    /**
     * Capitalizes first letter of each word
     * @param value
     * @return capitalized words
     */
    public static String firstCaps(String value) {
        String[] words=null;
        String s="";
        words = value.toLowerCase().split(" ");
        if(words.length<=0||value.isEmpty())return value;
        for(int i=0;i<words.length;i++){
            if(words[i].length()<=0)continue;
            Character c0 = words[i].charAt(0);
            if(Character.isLetter(c0)){
                c0=Character.toUpperCase(c0);
                words[i]=c0+words[i].substring(1);
            }
            if(words[i].length()>=2){
                char c1 = words[i].charAt(1);
                if(words[i].charAt(0)=='"' ){//capitalise 2nd character
                    c1 = Character.toUpperCase(c1);
                    words[i] = "\""+c1 + words[i].substring(2);
                }
            }
            if(words[i].length()>=3){
                String sss=words[i].substring(0, 2);
                if(sss.equalsIgnoreCase("o'")||sss.equalsIgnoreCase("mc")||words[i].substring(0, 3).equalsIgnoreCase("stj")){
                    words[i] = words[i].substring(0, 2)+Character.toUpperCase(words[i].charAt(2))+words[i].substring(3, words[i].length());
                }
            }
            s+=words[i]+' ';
        }
        words=null;
        return s.trim();
    }

//    public static void showValue(JLabel l, JLabel e, int value) {
//        if (value == 0) {
//            l.setVisible(false);
//            e.setText("none");
//            e.setVisible(false);
//        } else {
//            e.setText((new Money(value)).toString());
//            l.setVisible(true);
//            e.setVisible(true);
//        }
//    }
//
//    public static void showValue(JLabel l, JLabel e, String value) {
//        if (value.isEmpty()) {
//            l.setVisible(true);
//            e.setText("none");
//            e.setVisible(true);
//        } else {
//            e.setText(value);
//            l.setVisible(true);
//            e.setVisible(true);
//        }
//    }

    /**
     * strips out any non-digits from string
     * @param s
     * @return 
     */
    public static String numericOnly(String s) {
        if (s.isEmpty()) {
            return "";
        }
        //strip all non-numeric characters
        int i = 0;
        if (s.charAt(i) == '-') {
            i++;
        }
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(c >= '0' && c <= '9')) {//char not numeric, delete
                s = s.substring(0, i) + s.substring(i + 1);
                i--;
            }
        }
        if(s.length()==1 && s.startsWith("-")){
            s="";
        }
        return s;
    }

    public static String fixLength(String s, int length) {
        if (length <= s.length()) {
            return s.substring(0, length).trim();
        } else {
            return (s + spaceString).substring(0, length).trim();
        }
    }

    public static String fixLengthUntrimmed(String s, int length) {
        if (length <= s.length()) {
            return s.substring(0, length);
        } else {
            return (s + spaceString).substring(0, length);
        }
    }

//    public static boolean nextComponent(final KeyEvent evt,
//            JComponent current, JComponent next) {
//        //works for textfield to textfield or button or combo
//        int i=evt.getKeyCode() ;
//        if (evt.getKeyCode() == KeyEvent.VK_TAB  && evt.getModifiers() == 0) {
//            if (current.getClass() == JTextField.class) {
//                String s = ((JTextField) current).getText();
//                s = s.trim();
//                if (s.isEmpty()) {
//                    Audio.play("Ring");
//                    return false;
//                } else if (next.getClass() == JTextField.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    ((JTextField) next).selectAll();
//                    return true;
//                } else if (next.getClass() == JButton.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JComboBox.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JSpinner.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else {
//                    next.requestFocus();
//                    return true;
//                }
//            } else if (current.getClass() == JComboBox.class) {
//                if (next.getClass() == JTextField.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    ((JTextField) next).selectAll();
//                    return true;
//                } else if (next.getClass() == JButton.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JComboBox.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JSpinner.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                }
//            } else if (current.getClass() == JSpinner.class) {
//                if (next.getClass() == JTextField.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    ((JTextField) next).selectAll();
//                    return true;
//                } else if (next.getClass() == JButton.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JComboBox.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                } else if (next.getClass() == JSpinner.class) {
//                    Audio.play("Beep");
//                    next.requestFocus();
//                    return true;
//                }
//            } else if (current.getClass() == JButton.class) {
//                Audio.play("Beep");
//                ((JButton) current).doClick();
//                return true;
//            }
//        } else if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE) && evt.getModifiers() == 0) {
//            Container j = current;
//            do {
//                if (j.getParent() == null) {
//                    break;
//                }
//                j = j.getParent();
//            } while (j.getClass().getSuperclass() != Dialog.class && j.getClass().getSuperclass() != JFrame.class);
//            Audio.play("Beep");
//            j.setVisible(false);
//            return false;
//        }
//        return false;
//    }
//
//    public static boolean nextComponent(final KeyEvent evt,
//            JComponent current, JDateChooser next) {
//        //works for textfield to textfield or button or combo
//        if ((evt.getKeyCode() == KeyEvent.VK_TAB ) && evt.getModifiers() == 0) {
//            if (current.getClass() == JTextField.class) {
//                if (((JTextField) current).getText().isEmpty()) {
//                    Audio.play("Ring");
//                    return false;
//                } else {
//                    next.requestFocus();
//                    Audio.play("Beep");
//                    return true;
//                }
//            } else {
//                Audio.play("Beep");
//                next.requestFocus();
//                return true;
//            }
//        } else if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE) && evt.getModifiers() == 0) {
//            Container j = current;
//            do {
//                j = j.getParent();
//            } while (j.getClass().getSuperclass() != Dialog.class && j.getClass().getSuperclass() != JFrame.class);
//            Audio.play("Beep");
//            j.setVisible(false);
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean nextComponent(final KeyEvent evt,
//            JDateChooser current, JDateChooser next) {
//        //works for textfield to textfield or button or combo
//        if ((evt.getKeyCode() == KeyEvent.VK_TAB ) && evt.getModifiers() == 0) {
//            Audio.play("Beep");
//            next.requestFocus();
//            return true;
//        } else if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE) && evt.getModifiers() == 0) {
//            Container j = current;
//            do {
//                j = j.getParent();
//            } while (j.getClass().getSuperclass() != Dialog.class && j.getClass().getSuperclass() != JFrame.class);
//            Audio.play("Beep");
//            j.setVisible(false);
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean nextComponent(final KeyEvent evt,
//            JDateChooser current, JComponent next) {
//        //works for textfield to textfield or button or combo
//        if ((evt.getKeyCode() == KeyEvent.VK_TAB ) && evt.getModifiers() == 0) {
//            Audio.play("Beep");
//            next.requestFocus();
//            return true;
//        } else if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE) && evt.getModifiers() == 0) {
//            Container j = current;
//            do {
//                j = j.getParent();
//            } while (j.getClass().getSuperclass() != Dialog.class && j.getClass().getSuperclass() != JFrame.class);
//            Audio.play("Beep");
//            j.setVisible(false);
//            return true;
//        }
//        return false;
//    }
    public static boolean numericOnly(JTextField jtf){
        String s=numericOnly(jtf.getText());
        if(s.isEmpty()){
            jtf.requestFocus();
            jtf.setText("");
            Audio.play("Ring");
            return false;
        } else {
            jtf.setText(s);
            return true;
        }
    }

    public static boolean numericOnly(JTextField jtf,int minimum){
        String s=numericOnly(jtf.getText());
        if(s.isEmpty()){
            jtf.requestFocus();
            jtf.setText("");
            Audio.play("Ring");
            return false;
        } else {
            Integer v=Integer.parseInt(s);
            if(v<minimum){
                v=minimum;
                jtf.setText(v.toString());
            }
            jtf.setText(s);
            return true;
        }
    }

    public static boolean isNumericOnly(String s){
        int len=s.length();
        if (s.isEmpty()) {
            return false;
        }
        //strip all non-numeric characters
        int i = 0;
        if (s.charAt(i) == '-') {
            i++;
        }
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(c >= '0' && c <= '9')) {//char not numeric, delete
                s = s.substring(0, i) + s.substring(i + 1);
                i--;
            }
        }
        if(s.length()==1 && s.startsWith("-")){
            s="";
        }
        int len1 = s.length();
        if(len==s.length()){
            return true;
        }
        return false;
    }

}
