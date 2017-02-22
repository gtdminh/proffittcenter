/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author HP_Owner
 */
public class MyTableCellRenderer extends DefaultTableCellRenderer {

    int[] align;
    Font plain= new Font("SansSerif", Font.PLAIN, 16);
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);        
        JLabel jl = (JLabel) cell;
        if(table.getColumnClass(column)==Money.class){
            jl.setHorizontalAlignment(SwingConstants.RIGHT);
        }else if(align!=null){
            jl.setHorizontalAlignment(align[column]);
        }
        jl.setFont(plain);//font appears bold without this
        Color colour = getBackground();
        Border border = BorderFactory.createMatteBorder(1, 3, 1, 3, colour);
        Border margin = BorderFactory.createMatteBorder(-1,-1, -1, -1, Color.WHITE);
        //now cobine the two
        jl.setBorder(new CompoundBorder(margin, border));
        return cell;
    }

    public MyTableCellRenderer(int[] align) {
        super();
        this.align = align;
    }
    public  MyTableCellRenderer(){
        super();
        this.align=null;
    }
}
