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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author HP_Owner
 */
public class MyHeaderRenderer extends JLabel implements TableCellRenderer {

    JLabel jl;
    int[] align;
    JTableHeader header=null;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        header = table.getTableHeader();
        jl = (JLabel) this;
        jl.setText((String) value);
        jl.setHorizontalAlignment(align[column]);
        Font plain = new Font("SansSerif", Font.PLAIN, 12);
        jl.setFont(plain);//font appears bold without this
        //create one pixel space left and right
        Color colour = getBackground();
        Border border = BorderFactory.createMatteBorder(1, 3, 1, 2, colour);
        //create bottom and right border
        Border margin = BorderFactory.createMatteBorder(-1,-1, 1, 1, Color.BLACK);
        //now cobine the two
        jl.setBorder(new CompoundBorder(margin, border));
        return jl;
    } 
public void setHeader(JTable t){
   // mchl=new MyColumnHeaderListener(t);
}
    public MyHeaderRenderer(int[] align) {
        super();
        this.align = align;
    }

    public void mouseClicked(java.awt.event.KeyEvent evt) {
        jlMouseClicked(evt);
    }

    private void jlMouseClicked(java.awt.event.KeyEvent evt) {
        int x = 0;
    }
    public int getColumnClicked(){
//        if(mchl==null){
//            return -1;
//        }else{
//        return mchl.getColumn();
    return 0;
        }
    
}
 
