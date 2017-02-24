
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Hotkeys.java
 *
 * Created on 22-Aug-2008, 19:58:51
 */
package proffittcenter;

import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Hotkeys extends EscapeDialog implements MouseListener {

    /** Creates new form Hotkeys */
    private Preferences root = Preferences.userNodeForPackage(getClass());
    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Hotkeys");
    private Vector tableHeader=new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0=tableHeader.add(bundle.getString("Hotkey"));
    @SuppressWarnings("unchecked")
    private boolean b1=tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2=tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b3=tableHeader.add(bundle.getString("Price")+" "+Main.shop.poundSymbol);
    @SuppressWarnings("unchecked")
    private boolean b4=tableHeader.add(bundle.getString("Tax"));
    @SuppressWarnings("unchecked")
    private boolean b5=tableHeader.add(bundle.getString("Department"));
    @SuppressWarnings("unchecked")
    private boolean b6=tableHeader.add(bundle.getString("Sku"));
    private Long barcode;
    private String description;
    private Date date;
    private int price;
    private String tax;
    private String department;
    private Integer sku;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.LEFT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT};
    private Object[] line={barcode,description,date,price,tax,department,sku};
    private Vector<Object[]> rowData=new Vector<Object[]>();
    private AbstractTableModel model=new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        @Override
        public int getColumnCount() {
            return tableHeader.size();
        }

        @Override
        public int getRowCount() {
            return rowData.size();
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            line=rowData.get(rowIndex);
            if(columnIndex==0){//barcode barCode = c - 64 + 1000000l;
                long k=(Long)line[0];
                k-=1000000l-64;
                char c=(char)k;
                return c;
            }
            else if(columnIndex==1){//description
                return line[1];
            }
            else if(columnIndex==2){//date
                return line[2];
            }else if(columnIndex==3){//price
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) line[3])) / 100);
            }else if(columnIndex==4){//tax
                return line[4];
            }else if(columnIndex==5){//department
                return line[5];
            }else if(columnIndex==6){//department
                return line[6];
            }else return null;
        }

        @Override
        public Class getColumnClass(int c) {
            if(c==3){
                return line[3].getClass();
            }
            return getValueAt(0, c).getClass();
        }

    };
    private int columnSelected=0;
    private String productsString;

    public Hotkeys(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(200);
        tc.getColumn(1).setPreferredWidth(600);
        tc.getColumn(2).setPreferredWidth(200);
        tc.getColumn(3).setPreferredWidth(200);
        tc.getColumn(4).setPreferredWidth(300);
        tc.getColumn(5).setPreferredWidth(600);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        header.addMouseListener(this);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Hotkeys", Main.mainHelpSet);
    }

public void execute(){
       columnSelected=0;
       jPackSuppliers.setEnabled(false);
       drawGrid();
       Audio.play("Tada");
       //FormRestore.createPosition(this, root);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
   }

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
       PreparedStatement products;
        try {
            productsString= "SELECT Products.ID," +
                    "Products.Description,UPPER(Products.Description) AS PD," +
                    "Products.WhenCreated,Products.Price,Products.Sku," +
                    "Departments.Description,Skus.Tax,Taxes.Description " +
                    "FROM Products,Skus,Departments,Taxes " +
                    "WHERE Products.Sku=Skus.ID " +
                    "AND Skus.Department=Departments.ID " +
                    "AND Products.ID>=1000001 AND Products.ID<=1000026 "
                    + "AND Skus.Tax=Taxes.ID " +
                    "ORDER BY ";
            if (columnSelected == 0) {
                products = Main.getConnection().prepareStatement(productsString + "Products.ID");
            } else if (columnSelected == 1) {
                products = Main.getConnection().prepareStatement(productsString + "Products.Description");
            } else if (columnSelected == 2) {
                products = Main.getConnection().prepareStatement(productsString + "Products.WhenCreated");
            } else if (columnSelected == 3) {
                products = Main.getConnection().prepareStatement(productsString + "Products.Price");
            } else if (columnSelected == 4) {
                products = Main.getConnection().prepareStatement(productsString + "Taxes.Description");
            } else if (columnSelected == 5) {
                products = Main.getConnection().prepareStatement(productsString + "Departments.Description");
            } else if (columnSelected == 6) {
                products = Main.getConnection().prepareStatement(productsString + "Products.Sku");
            }else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
             ResultSet rs=products.executeQuery();
             jTable1.clearSelection();
             rowData.clear();
             while(rs.next()){//departments to display
                 barcode=rs.getLong("Products.ID");
                 description=rs.getString("Description");
                 date=rs.getDate("WhenCreated");
                 price=rs.getInt("Price");
                 int t=rs.getInt("Skus.Tax");
                 if(t==0){
                     tax="";
                 }else{
                     tax=rs.getString("Taxes.Description");
                 }
                 department=rs.getString("Departments.Description");
                 sku=rs.getInt("Products.Sku");
                 Object[] line1={barcode,description,date,price,tax,department,sku};
                 rowData.add(line1);
                 model.fireTableDataChanged();
             }
             rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Hotkeys.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
       setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jDetailsBtn = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        jPackSuppliers = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Hotkeys"); // NOI18N
        setTitle(bundle.getString("Hotkeys.title")); // NOI18N
        setName("Hotkeys"); // NOI18N

        jPanel1.setName("Hotkeys"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Department ID", "Description", "Tax ID", "Tax rate", "Margin", "Minimum"
            }
        ));
        jTable1.addMouseListener(formListener);
        jTable1.addKeyListener(formListener);
        jScrollPane1.setViewportView(jTable1);

        jDetailsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        jDetailsBtn.setBorderPainted(false);
        jDetailsBtn.setContentAreaFilled(false);
        jDetailsBtn.addActionListener(formListener);

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setBorderPainted(false);
        closeButton2.setContentAreaFilled(false);
        closeButton2.addActionListener(formListener);

        jPackSuppliers.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPackSuppliers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        jPackSuppliers.setText(bundle.getString("Hotkeys.jPackSuppliers.text")); // NOI18N
        jPackSuppliers.setBorderPainted(false);
        jPackSuppliers.setContentAreaFilled(false);
        jPackSuppliers.addActionListener(formListener);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPackSuppliers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPackSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.KeyListener, java.awt.event.MouseListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == jDetailsBtn) {
                Hotkeys.this.jDetailsBtnActionPerformed(evt);
            }
            else if (evt.getSource() == closeButton2) {
                Hotkeys.this.closeButton2ActionPerformed(evt);
            }
            else if (evt.getSource() == jPackSuppliers) {
                Hotkeys.this.jPackSuppliersActionPerformed(evt);
            }
        }

        public void keyPressed(java.awt.event.KeyEvent evt) {
        }

        public void keyReleased(java.awt.event.KeyEvent evt) {
            if (evt.getSource() == jTable1) {
                Hotkeys.this.jTable1KeyReleased(evt);
            }
        }

        public void keyTyped(java.awt.event.KeyEvent evt) {
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == jTable1) {
                Hotkeys.this.jTable1MouseClicked(evt);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mousePressed(java.awt.event.MouseEvent evt) {
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
    if(KeyEvent.VK_ENTER==evt.getKeyCode()){
            int selection = jTable1.getSelectedRow();
            barcode = (Long) model.getValueAt(selection, 0);
            evt.setKeyCode(KeyEvent.VK_SPACE);
            setVisible(false);
        }
    }//GEN-LAST:event_jTable1KeyReleased

    private void jDetailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDetailsBtnActionPerformed
        int selection=jTable1.getSelectedRow();
        if(selection<0){//no selected row
            return;
        }
        char c=(Character)model.getValueAt(selection,0);
        barcode=c-65l+1000001l;
//        Main.product.setLocationRelativeTo(this);
        boolean b=Main.product.execute(barcode.toString());
        drawGrid();
    }//GEN-LAST:event_jDetailsBtnActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButton2ActionPerformed

    private void jPackSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPackSuppliersActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection == -1) {
            //no selected row
            return;
        }
        char c=(Character)model.getValueAt(selection,0);
        barcode=c-65l+1000001l;
        Main.packSuppliers.execute(""+barcode);
}//GEN-LAST:event_jPackSuppliersActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selection = jTable1.getSelectedRow();
        jPackSuppliers.setEnabled(selection!=-1);
    }//GEN-LAST:event_jTable1MouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Hotkeys dialog = new Hotkeys(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton2;
    private javax.swing.JButton jDetailsBtn;
    private javax.swing.JButton jPackSuppliers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        TableColumnModel colModel = jTable1.getColumnModel();
        // The index of the column whose header was clicked
        int vColIndex = colModel.getColumnIndexAtX(e.getX());
        //int mColIndex = jTable1.convertColumnIndexToModel(vColIndex);
        // Return if not clicked on any column header
        if (vColIndex == -1) {
            return;
        }
        // Determine if mouse was clicked between column heads
        Rectangle headerRect = jTable1.getTableHeader().getHeaderRect(vColIndex);
        if (vColIndex == 0) {
            headerRect.width -= 3;    // Hard-coded constant
        } else {
            headerRect.grow(-3, 0);   // Hard-coded constant
        }
        if (!headerRect.contains(e.getX(), e.getY())) {
            // Mouse was clicked between column heads
            // vColIndex is the column head closest to the click
            // vLeftColIndex is the column head to the left of the click
            int vLeftColIndex = vColIndex;
            if (e.getX() < headerRect.x) {
                vLeftColIndex--;
            }
        }
        columnSelected = vColIndex;
        drawGrid();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //do nothing
    }

}
