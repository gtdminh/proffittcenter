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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Dave
 */
public class ResultsRenderer_1 extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Integer) {
                Integer amount = (Integer) value;
                if (amount.intValue() < 0) {
                    cell.setForeground(Color.red);
                } else {
                    cell.setForeground(Color.black);
                }
            }
            if (value instanceof String) {
                String s = (String) value;
                if (s.startsWith("-", 0)) {
                    cell.setForeground(Color.red);
                } else {
                    cell.setForeground(Color.black);
                }
            }
            JLabel jl = (JLabel) cell;
            Font plain = new Font("SansSerif", Font.BOLD, 18);
            jl.setFont(plain);//font appears bold without this
            Color colour = getBackground();
            Border border = BorderFactory.createMatteBorder(3, 3, 3, 3, colour);
            Border margin = BorderFactory.createMatteBorder(3, 3, 3, 3, colour);
            //now cobine the two
            jl.setBorder(new CompoundBorder(margin, border));
            jl.setBackground(colour);
            if (column == 0) {
                jl.setHorizontalAlignment(RIGHT);
            } else {
                jl.setHorizontalAlignment(LEFT);
            }
            return cell;
        }
    
}
