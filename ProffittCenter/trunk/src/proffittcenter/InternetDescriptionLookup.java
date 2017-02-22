/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Dave
 */
public class InternetDescriptionLookup extends Thread{
    private String barcode;
    private InputStreamReader inStream;
    private String nextLine;
    public static String description="";
    private final JTextField jtf;

    public InternetDescriptionLookup(String barcode,JTextField jtf){
        this.barcode=barcode;
        this.jtf=jtf;
    }

   public void run(){
        try {
            description="";
            String lookupString = "http://www.digit-eyes.com/cgi-bin/digiteyes.fcgi?upcCode=5010024111076&action=lookupUpc&go=" + barcode;
//            String lookupString = "http://test.upcdata.info/lookup/" + barcode+"/";
            URL lookup = new URL(lookupString);
            URLConnection lookupConnection = lookup.openConnection();
            inStream = new InputStreamReader(lookupConnection.getInputStream());
            BufferedReader buff = null;
            buff = new BufferedReader(inStream);
            nextLine = buff.readLine();
            if (nextLine == null || nextLine.isEmpty()) {
                description = "";
            } else {
                description = nextLine;
            }
//            jtf.setText(description);
//            jtf.selectAll();
        } catch (IOException ex) {
//            Logger.getLogger(InternetDescriptionLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

}
