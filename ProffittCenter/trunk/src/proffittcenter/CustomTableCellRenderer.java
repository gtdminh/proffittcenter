package proffittcenter;


import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HP_Owner
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel jl = (JLabel) cell;
            if (column == 0 || column == 2 || column == 3) {
                jl.setHorizontalAlignment(RIGHT);
            } else {
                jl.setHorizontalAlignment(CENTER);
            }
            return cell;
        }
    }
