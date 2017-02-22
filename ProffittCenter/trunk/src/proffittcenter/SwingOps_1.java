/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;

import javax.swing.JTable;
import javax.swing.JViewport;

/**
 *
 * @author Dave
 */
public class SwingOps_1 {
    private static int height;
    private static int rowHeight;

    public static int getVisbleRowCount(JTable table){
        int returnValue=40;
        JViewport viewport = (JViewport) table.getParent();
        if(viewport!=null){
            height = viewport.getHeight();
            rowHeight = table.getRowHeight();
            returnValue = height/rowHeight;
        }
        return returnValue;
    }

    private SwingOps_1() {
    }
}
