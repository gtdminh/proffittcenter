/*
 * TempProducts.java
 *
 * Created on 27 June 2007, 14:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP_Owner
 */
public class TempProducts {

    /** Creates a new instance of TempProducts */
    public TempProducts() {
    }

    public boolean execute() {
        Long product;
        try {
            PreparedStatement productsBySku = Main.getConnection().prepareStatement(
                    "SELECT ID,WhenCreated FROM Products WHERE Sku=1 " + "AND ID<>'1000001' AND ID<>'1000002'" + //A or B
                    "ORDER BY WhenCreated DESC  ");
            ResultSet rs = productsBySku.executeQuery();
            if (rs.first()) {//not if empty
                do {//Products to display
                    product = rs.getLong("ID");
                    product = Main.newProduct.execute(product,true);
                    if (product == 0L) {
                        Audio.play("Ring");
                        break;
                    } else if (product > 0L) {
                        Audio.play("Beep");
                    } else {//closed as =-1
                        break;
                    }
                } while (rs.next());
            } else {
                //no products left
            }
            rs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(TempProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
